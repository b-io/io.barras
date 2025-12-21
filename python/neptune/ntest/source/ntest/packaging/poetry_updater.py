#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

# POETRY PY310 PIN ######################################################################################################
# Goal
#   Pin Poetry dependencies to the latest PyPI releases compatible with the specified Python version (default: 3.10.0).
#
# Behavior
#   • Scans `[tool.poetry.dependencies]` and ignores `"python"` plus non-PyPI dependencies (tables such as `{ path=… }`)
#   • Queries the PyPI JSON API for each dependency via the HTTP utilities (requests + retries)
#   • Selects the newest non-yanked, non-prerelease release compatible with the target Python version
#   • Prints suggested pins and optionally patches the `pyproject.toml` file atomically
#
# CLI
#   • "--pyproject" path to `"pyproject.toml"` (default: `"pyproject.toml"`)
#   • "--python" target version (default: `"3.10.0"`)
#   • "--inplace" patch `"pyproject.toml"` (best-effort; preserves trailing comments)
#   • "--include-prereleases" include prereleases
#   • "--verbose" enable debug logging
########################################################################################################################

from __future__ import annotations

import argparse
import logging
import re
from dataclasses import dataclass
from pathlib import Path

import requests
from packaging.specifiers import SpecifierSet
from packaging.version import Version

from nconnect.internet import http
from nutil.common import *
from nutil.io.file import read, resolve_path, write_text
from nutil.io.logging import configure_logging
from nutil.scalar.string import ALPHANUMERIC_CHARS

__POETRY_UPDATER_CONSTANTS________________________________________________________________ = ""


### DEFAULTS ###############################################

DEFAULT_PYPROJECT = "pyproject.toml"
DEFAULT_TARGET_PYTHON = "3.10.0"


### GLOBALS ################################################

PYPI_JSON_URL_TEMPLATE = "https://pypi.org/pypi/%s/json"

PYPI_API_NAME = "PyPI"
PINNER_USER_AGENT = "poetry-py310-pin/1.0"

DEPENDENCY_SECTION_PATTERN: re.Pattern[str] = re.compile(r"^\[tool\.poetry\.dependencies\]\s*$")
SECTION_HEADER_PATTERN: re.Pattern[str] = re.compile(r"^\[.*\]\s*$")

DEPENDENCY_LINE_PATTERN: re.Pattern[str] = re.compile(
    rf"""
    ^
    (?P<name>[{ALPHANUMERIC_CHARS}_.-]+)
    \s*=\s*
    (?P<value>.+?)
    (?P<comment>\s+\#.*)?
    $
    """,
    re.VERBOSE,
)

QUOTED_STRING_PATTERN: re.Pattern[str] = re.compile(r'^\s*(?P<q>["\'])(?P<value>.*?)(?P=q)\s*$')


__POETRY_UPDATER_CLASSES__________________________________________________________________ = ""


@dataclass(frozen=True)
class Pin:
    """A pin for a dependency to a specific version."""

    name: str
    version: str


__POETRY_UPDATER_ACCESSORS________________________________________________________________ = ""


def get_latest_compatible_version(
    pypi_json: Dict[str, Any],
    target_version: Version,
    include_prereleases: bool,
) -> Optional[str]:
    """
    Selects the latest compatible version from the PyPI JSON payload.

    Args:
        pypi_json: The PyPI JSON payload.
        target_version: The target Python version.
        include_prereleases: Whether prereleases are eligible.

    Returns:
        The selected version, or `None` if no compatible release exists.
    """
    releases = pypi_json.get("releases")
    if not isinstance(releases, dict):
        return None

    best: Optional[Version] = None

    for ver_str, files_obj in releases.items():
        try:
            ver = Version(str(ver_str))
        except Exception:
            continue

        if ver.is_prerelease and not include_prereleases:
            continue

        files = files_obj if isinstance(files_obj, list) else []
        if not supports_python_version(files, target_version):
            continue

        if is_null(best) or ver > best:
            best = ver

    return str(best) if not is_null(best) else None


__POETRY_UPDATER_PARSERS__________________________________________________________________ = ""


def parse_pyproject_lines(pyproject_path: Path) -> List[str]:
    """
    Parses the specified `pyproject.toml` into a list of lines.

    Args:
        pyproject_path: The `pyproject.toml` path.

    Returns:
        The list of lines without line terminators.
    """
    text = read(str(pyproject_path))
    return text.splitlines(keepends=False)


def extract_pypi_dependencies(pyproject_lines: List[str]) -> List[str]:
    """
    Extracts the dependency names from `[tool.poetry.dependencies]` that are expected to be on PyPI.

    Args:
        pyproject_lines: The raw `pyproject.toml` lines.

    Returns:
        The list of dependency names.
    """
    dependencies: List[str] = []
    is_in_dependencies = False

    for line in pyproject_lines:
        stripped = line.strip()

        if DEPENDENCY_SECTION_PATTERN.match(stripped):
            is_in_dependencies = True
            continue

        if is_in_dependencies and SECTION_HEADER_PATTERN.match(stripped):
            break

        if not is_in_dependencies:
            continue

        if not stripped or stripped.startswith("#"):
            continue

        m = DEPENDENCY_LINE_PATTERN.match(line)
        if is_null(m):
            continue

        name = m.group("name")
        raw_value = m.group("value").strip()

        # Skip the python requirement and table-style dependencies (path/git/url/markers/extras)
        if name == "python":
            continue
        if raw_value.startswith("{"):
            continue

        # Keep only string dependencies (e.g., `"^1.2.3"`, `">=1,<2"`, `"*"`)
        if not QUOTED_STRING_PATTERN.match(raw_value):
            continue

        dependencies.append(name)

    return dependencies


__POETRY_UPDATER_PROCESSORS_______________________________________________________________ = ""


def apply_pins_inplace(pyproject_lines: List[str], pins: List[Pin]) -> List[str]:
    """
    Applies the specified pins to `[tool.poetry.dependencies]`.

    Args:
        pyproject_lines: The raw `pyproject.toml` lines.
        pins: The pins to apply.

    Returns:
        The patched `pyproject.toml` lines.
    """
    pin_map: Dict[str, str] = {p.name: p.version for p in pins}

    out: List[str] = []
    in_dependencies = False

    for line in pyproject_lines:
        stripped = line.strip()

        if DEPENDENCY_SECTION_PATTERN.match(stripped):
            in_dependencies = True
            out.append(line)
            continue

        if in_dependencies and SECTION_HEADER_PATTERN.match(stripped):
            in_dependencies = False
            out.append(line)
            continue

        if in_dependencies:
            m = DEPENDENCY_LINE_PATTERN.match(line)
            if m:
                name = m.group("name")
                raw_value = m.group("value").strip()
                comment = m.group("comment") or ""

                if name in pin_map and QUOTED_STRING_PATTERN.match(raw_value):
                    out.append(f'{name} = "^{pin_map[name]}"{comment}')
                    continue

        out.append(line)

    return out


def compute_pins(
    session: requests.Session,
    dep_names: Iterable[str],
    target_version: Version,
    include_prereleases: bool,
) -> List[Pin]:
    """
    Computes the version pins for the specified dependency names.

    Args:
        session: The shared HTTP session configured with retries.
        dep_names: The dependency names.
        target_version: The target Python version.
        include_prereleases: Whether prereleases are eligible.

    Returns:
        The list of resolved pins.
    """
    pins: List[Pin] = []

    for name in dep_names:
        logging.info("Resolving '%s' …", name)
        try:
            data = fetch_pypi_json(session, name)
            if is_null(data):
                continue
            ver = get_latest_compatible_version(data, target_version, include_prereleases)
        except Exception as e:
            logging.warning("Skip '%s' due to an error: %s", name, e)
            continue

        if not ver:
            logging.warning("No compatible release found for '%s'", name)
            continue

        pins.append(Pin(name=name, version=ver))

    return pins


__POETRY_UPDATER_READERS__________________________________________________________________ = ""


def fetch_pypi_json(session: requests.Session, name: str) -> Optional[Dict[str, Any]]:
    """
    Fetches the PyPI JSON metadata for the specified project.

    Args:
        session: The shared HTTP session configured with retries.
        name: The PyPI project name.

    Returns:
        The decoded JSON object, or `None` when the package is not found (404) or when the payload is invalid.
    """
    url = PYPI_JSON_URL_TEMPLATE % name
    status, payload = http.request_json(
        url,
        session=session,
        api_name=PYPI_API_NAME,
        raise_on_http_error=False,
    )

    if status == 0:
        logging.warning("Skip '%s' due to a transport failure", name)
        return None

    if is_null(payload):
        logging.warning("Skip '%s' due to an empty response (HTTP %s)", name, status)
        return None

    if not isinstance(payload, dict):
        logging.warning("Skip '%s' due to an unexpected JSON shape", name)
        return None

    return payload


__POETRY_UPDATER_VALIDATORS_______________________________________________________________ = ""


def is_python_compatible(requires_python: Optional[str], target_version: Version) -> bool:
    """
    Tests whether `Requires-Python` matches the specified target version.

    Args:
        requires_python: The `Requires-Python` specifier string.
        target_version: The target Python version.

    Returns:
        `True` if compatible, otherwise `False`.
    """
    if not requires_python:
        return True
    try:
        return target_version in SpecifierSet(requires_python)
    except Exception:
        return False


def supports_python_version(files: List[object], target_version: Version) -> bool:
    """
    Determines whether at least one non-yanked distribution file supports the target Python version.

    Args:
        files: The release file list from the PyPI JSON payload.
        target_version: The target Python version.

    Returns:
        `True` if the release is compatible, otherwise `False`.
    """
    if not files:
        return False

    any_requires_python = False

    for f in files:
        if not isinstance(f, dict):
            continue
        if f.get("yanked") is True:
            continue

        rp = f.get("requires_python")
        if isinstance(rp, str):
            any_requires_python = True
            if is_python_compatible(rp, target_version):
                return True

    if any_requires_python:
        return False

    return True


__POETRY_UPDATER_RUNNERS__________________________________________________________________ = ""


def run(pyproject_path: Path, target_version: Version, inplace: bool, include_prereleases: bool) -> int:
    """
    Runs the pin computation and optional patching.

    Args:
        pyproject_path: The `pyproject.toml` path.
        target_version: The target Python version.
        inplace: Whether to patch in-place.
        include_prereleases: Whether prereleases are eligible.

    Returns:
        Exit code `0` on success, otherwise `1`.
    """
    lines = parse_pyproject_lines(pyproject_path)
    dependencies = extract_pypi_dependencies(lines)

    if not dependencies:
        logging.error("No dependencies found under '[tool.poetry.dependencies]' in '%s'", pyproject_path)
        return 1

    session = http.create_session_with_retries(
        total_retries=4,
        backoff_factor=0.5,
        allowed_methods={"GET"},
        headers={"User-Agent": PINNER_USER_AGENT},
        accept="application/json",
    )
    try:
        pins = compute_pins(session, dependencies, target_version, include_prereleases)
    finally:
        try:
            session.close()
        except Exception:
            pass

    pins_sorted = sorted(pins, key=lambda p: p.name.lower())

    print("# Suggested pins (paste into [tool.poetry.dependencies])")
    for p in pins_sorted:
        print(f'{p.name} = "^{p.version}"')

    if inplace:
        patched_lines = apply_pins_inplace(lines, pins_sorted)
        patched_text = "\n".join(patched_lines) + "\n"
        write_text(pyproject_path, patched_text, overwrite=True)
        logging.info("Patched '%s'; run 'poetry lock' and 'poetry install'", pyproject_path)

    return 0


### ARGUMENTS ##############################################


def parse_args() -> argparse.Namespace:
    """
    Parses the CLI arguments.

    Returns:
        An `argparse.Namespace` containing the parsed arguments.

    Raises:
        FileNotFoundError: When the `pyproject.toml` path does not exist.
        ValueError: When the specified python version is invalid.
    """
    ap = _build_arg_parser()
    args = ap.parse_args()

    args.pyproject = resolve_path(args.pyproject, must_exist=True)

    try:
        args.python = Version(args.python)
    except Exception as e:
        raise ValueError(f"Invalid python version '{args.python}'") from e

    return args


def _build_arg_parser() -> argparse.ArgumentParser:
    """Builds the CLI argument parser."""
    ap = argparse.ArgumentParser(
        description="Pins Poetry dependencies to the latest PyPI releases compatible with the target Python version."
    )
    ap.add_argument("--pyproject", default=DEFAULT_PYPROJECT, help="Path to 'pyproject.toml'.")
    ap.add_argument("--python", default=DEFAULT_TARGET_PYTHON, help="Target python version (e.g., '3.10.0').")
    ap.add_argument("--inplace", action="store_true", help="Patch 'pyproject.toml' in-place.")
    ap.add_argument("--include-prereleases", action="store_true", help="Allow prerelease versions.")
    ap.add_argument("--verbose", action="store_true", help="Enable debug logging.")
    return ap


### MAIN ###################################################


def run_with_args(args: argparse.Namespace) -> None:
    """
    Runs the CLI with the specified arguments.

    Args:
        args: The CLI arguments.
    """
    configure_logging()
    if args.verbose:
        logging.getLogger().setLevel(logging.DEBUG)

    logging.info("Run '%s' with args: %s", Path(__file__).name, args)
    sys.exit(run(args.pyproject, args.python, args.inplace, args.include_prereleases))


def main() -> None:
    """Runs the CLI entry point."""
    run_with_args(parse_args())


if __name__ == "__main__":
    main()
