#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

# POETRY PY310 PIN ######################################################################################################
# Goal
#   Pin Poetry dependencies to the latest PyPI releases compatible with the specified Python version (default: 3.10.0).
#
# Behavior
#   • Scans Poetry dependency sections and ignores `"python"` plus non-PyPI dependencies (tables such as `{ path=… }`)
#   • Queries the PyPI JSON API for each dependency via the HTTP utilities (requests + retries)
#   • Selects the newest non-yanked, non-prerelease release compatible with the target Python version
#   • Patches `"pyproject.toml"` in-place atomically (optionally creating a timestamped backup) while preserving spacing
#     and comments
#   • When `"--root"` is used, scans recursively and prunes excluded folders using `DEFAULT_EXCLUDES`
#
# CLI
#   • `"--root"` scan this root directory recursively for `"pyproject.toml"` and update all of them
#   • `"--pyproject"` path to `"pyproject.toml"` (default: `"pyproject.toml"`)
#
#   • `"--include-prereleases"` include prereleases
#   • `"--python"` target version (defaults to the lowest version from the `"python"` dependency, fallback: `"3.10.0"`)
#   • `"--verbose"` enable debug logging
#
#   • `"--dry-run"` do not write changes; only log results
#   • `"--no-inplace"` do not patch `"pyproject.toml"` (still resolves versions)
#
#   • `"--backup"` create a timestamped backup of the previous file on save
#   • `"--backup-dir"` directory to store backups (defaults to the `"pyproject.toml"` directory)
########################################################################################################################

from __future__ import annotations

import argparse
import logging
import re
from dataclasses import dataclass

import requests
from packaging.specifiers import SpecifierSet
from packaging.version import Version

from nconnect.network import http
from ntool.common import DEFAULT_EXCLUDES
from nutil.io.file import *
from nutil.io.logging import configure_logging
from nutil.scalar.string import ALPHANUMERIC_CHARS, split_line, strip_line

__POETRY_UPDATER_CONSTANTS________________________________________________________________ = ""


### DEFAULTS ###############################################

DEFAULT_PYPROJECT = "pyproject.toml"
DEFAULT_TARGET_PYTHON = "3.10.0"


### GLOBALS ################################################

PYPROJECT = "pyproject.toml"


### GLOBALS ################################################

PYPI_JSON_URL_TEMPLATE = "https://pypi.org/pypi/%s/json"

PYPI_API_NAME = "PyPI"
PINNER_USER_AGENT = "poetry-py310-pin/1.0"

DEPENDENCY_SECTION_PATTERN: re.Pattern[str] = re.compile(r"^\[tool\.poetry\.dependencies\]\s*$")
GROUP_DEPENDENCY_SECTION_PATTERN: re.Pattern[str] = re.compile(
    rf"^\[tool\.poetry\.group\.[{ALPHANUMERIC_CHARS}_.\-+]+\.dependencies\]\s*$"
)
SECTION_HEADER_PATTERN: re.Pattern[str] = re.compile(r"^\[.*\]\s*$")

DEPENDENCY_LINE_PATTERN: re.Pattern[str] = re.compile(
    rf"""
    ^
    (?P<name>[{ALPHANUMERIC_CHARS}_.\-+]+)
    \s*=\s*
    (?P<value>.+?)
    (?P<comment>\s+\#.*)?
    $
    """,
    re.VERBOSE,
)

QUOTED_STRING_PATTERN: re.Pattern[str] = re.compile(r'^\s*(?P<q>["\'])(?P<value>.*?)(?P=q)\s*$')

SIMPLE_VERSION_SPEC_PATTERN: re.Pattern[str] = re.compile(
    rf"""
    ^
    (?P<op>\^|~|==|!=|~=|>=|<=|>|<)?
    (?P<ws>\s*)
    (?P<version>[{ALPHANUMERIC_CHARS}][{ALPHANUMERIC_CHARS}_.\-+]*)
    (?P<trail>\s*)
    $
    """,
    re.VERBOSE,
)


__POETRY_UPDATER_CLASSES__________________________________________________________________ = ""


@dataclass(frozen=True)
class Pin:
    """A pin for a dependency to a specific version."""

    name: str
    version: str


__POETRY_UPDATER_ACCESSORS________________________________________________________________ = ""


### GETTERS ################################################


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

    latest_version: Optional[Version] = None

    for release_version, files_obj in releases.items():
        try:
            version = Version(str(release_version))
        except Exception:
            continue

        if version.is_prerelease and not include_prereleases:
            continue

        if version.post == 0:
            continue

        files = files_obj if isinstance(files_obj, list) else []
        if not supports_python_version(files, target_version):
            continue

        if is_null(latest_version) or version > latest_version:
            latest_version = version

    return str(latest_version) if not is_null(latest_version) else None


__POETRY_UPDATER_VALIDATORS_______________________________________________________________ = ""


def is_dependency_section_header(stripped: str) -> bool:
    """
    Tests whether `stripped` is a Poetry dependency section header.

    Behavior:
        • Matches `[tool.poetry.dependencies]`.
        • Matches any group dependency header of the form `[tool.poetry.group.<name>.dependencies]`.

    Args:
        stripped: The line content with surrounding whitespace already removed.

    Returns:
        `True` if `stripped` is a dependency section header, otherwise `False`.
    """
    return bool(DEPENDENCY_SECTION_PATTERN.match(stripped) or GROUP_DEPENDENCY_SECTION_PATTERN.match(stripped))


##############################


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


############################################################


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

    any_non_yanked = False
    any_requires_python = False

    for file in files:
        if not isinstance(file, dict):
            continue

        if file.get("yanked") is True:
            continue
        any_non_yanked = True

        requires_python = file.get("requires_python")
        if isinstance(requires_python, str):
            any_requires_python = True
            if is_python_compatible(requires_python, target_version):
                return True

    # If everything is yanked, the release is unusable
    if not any_non_yanked:
        return False

    # If there is `"Requires-Python"` but none matched, the release is incompatible
    if any_requires_python:
        return False

    # Otherwise, assume compatible
    return True


__POETRY_UPDATER_FINDERS__________________________________________________________________ = ""


def find_pyproject_files(root: Path, *, exclude: List[str] = DEFAULT_EXCLUDES) -> List[Path]:
    """
    Finds all `PYPROJECT` files under `root`, recursively, pruning excluded directories.

    Args:
        root: The root directory to scan.
        exclude: The list of exclude glob patterns (defaults to `DEFAULT_EXCLUDES`).

    Returns:
        The list of discovered `pyproject.toml` paths (sorted).
    """
    prune_names = get_dirnames_from_globs(exclude)

    found: List[Path] = []
    for dirpath, dirnames, filenames in os.walk(root):
        dir_path = Path(dirpath)
        rel_dir = to_relative_posix_path(dir_path, root)

        # Prune excluded directories in place (avoid entering ".venv", ".git", etc.)
        kept: List[str] = []
        for d in dirnames:
            if d in prune_names:
                continue
            rel_child = join_posix_paths(rel_dir, d)
            if exclude_dir(rel_child, exclude):
                continue
            kept.append(d)
        dirnames[:] = kept

        for name in filenames:
            if name != PYPROJECT:
                continue
            rel_file = join_posix_paths(rel_dir, name)
            if exclude_file(rel_file, exclude, include=[]):
                continue
            found.append(dir_path / name)

    found.sort(key=lambda p: str(p).casefold())
    return found


__POETRY_UPDATER_PARSERS__________________________________________________________________ = ""


def parse_pyproject_lines(pyproject_path: Path) -> List[str]:
    """
    Parses the specified `pyproject.toml` into a list of raw lines.

    Notes:
        • Reads with `newline=""` so the original line terminators (`"\n"` vs `"\r\n"`) are preserved.
        • Returns each line with its original line terminator when present.

    Args:
        pyproject_path: The `pyproject.toml` path.

    Returns:
        The list of raw lines (each line may include its terminator).
    """
    text = read(pyproject_path, newline="")
    return text.splitlines(keepends=True)


def extract_pypi_dependencies(pyproject_lines: List[str]) -> List[str]:
    """
    Extracts the dependency names from the Poetry dependency sections that are expected to be on PyPI.

    Args:
        pyproject_lines: The raw `pyproject.toml` lines.

    Returns:
        The list of dependency names (order preserved, de-duplicated case-insensitively).
    """
    dependencies: List[str] = []
    seen: Set[str] = set()
    is_in_dependencies = False

    for raw_line in pyproject_lines:
        line = strip_line(raw_line)
        stripped = line.strip()

        if is_dependency_section_header(stripped):
            is_in_dependencies = True
            continue

        if is_in_dependencies and SECTION_HEADER_PATTERN.match(stripped):
            is_in_dependencies = False
            continue

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
        if name.lower() == "python":
            continue
        if raw_value.startswith("{"):
            continue

        # Keep only string dependencies (e.g., `"^1.2.3"`, `">=1"`, `"*"`)
        if not QUOTED_STRING_PATTERN.match(raw_value):
            continue

        key = name.lower()
        if key in seen:
            continue
        seen.add(key)
        dependencies.append(name)

    return dependencies


def extract_current_pins(pyproject_lines: List[str]) -> Dict[str, str]:
    """
    Extracts the currently pinned versions from the Poetry dependency sections for simple quoted specs.

    Notes:
        • Only extracts specs that `_override_version_spec` would accept (single version token).
        • Returns a mapping keyed case-insensitively (lowercased dependency name).

    Args:
        pyproject_lines: The raw `pyproject.toml` lines.

    Returns:
        A mapping `{dependency_name: version}` for dependencies with a simple pinned spec.
    """
    current: Dict[str, str] = {}
    is_in_dependencies = False

    for raw_line in pyproject_lines:
        line = strip_line(raw_line)
        stripped = line.strip()

        if is_dependency_section_header(stripped):
            is_in_dependencies = True
            continue

        if is_in_dependencies and SECTION_HEADER_PATTERN.match(stripped):
            is_in_dependencies = False
            continue

        if not is_in_dependencies:
            continue

        if not stripped or stripped.startswith("#"):
            continue

        m = DEPENDENCY_LINE_PATTERN.match(line)
        if is_null(m):
            continue

        name = m.group("name")
        raw_value = m.group("value")

        qm = QUOTED_STRING_PATTERN.match(raw_value)
        if is_null(qm):
            continue

        spec = qm.group("value")
        sm = SIMPLE_VERSION_SPEC_PATTERN.match(spec)
        if is_null(sm):
            continue

        version = sm.group("version")
        if not is_null(version):
            current[name.lower()] = version

    return current


def extract_python_requirement(pyproject_lines: List[str]) -> Optional[str]:
    """
    Extracts the `"python"` dependency requirement from `[tool.poetry.dependencies]` when present.

    Args:
        pyproject_lines: The raw `pyproject.toml` lines.

    Returns:
        The inner quoted requirement string (without quotes), or `None` if not found / not a quoted string.
    """
    is_in_dependencies = False

    for raw_line in pyproject_lines:
        line = strip_line(raw_line)
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
        if name.lower() != "python":
            continue

        raw_value = m.group("value")
        qm = QUOTED_STRING_PATTERN.match(raw_value)
        if is_null(qm):
            return None
        return qm.group("value")

    return None


def detect_lowest_python_version(pyproject_lines: List[str]) -> Optional[Version]:
    """
    Detects the lowest eligible target Python version from the `"python"` dependency requirement.

    Notes:
        • Selects the maximum of the lower bounds (`">="`, `">"`, `"~="`, `"=="`) as the effective minimum.
        • Ignores upper bounds (e.g., `"<4.0"`).
        • Returns `None` when no requirement is found or when no lower bound can be derived.

    Args:
        pyproject_lines: The raw `pyproject.toml` lines.

    Returns:
        The detected lowest target version, or `None`.
    """
    requirement = extract_python_requirement(pyproject_lines)
    if not requirement:
        return None

    try:
        spec = SpecifierSet(requirement)
    except Exception:
        return None

    best_lower: Optional[Version] = None

    for s in spec:
        op = str(s.operator or "").strip()
        raw = str(s.version or "").strip()
        if not raw:
            continue

        if op in {">=", "~=", "==", "==="}:
            candidate = _coerce_python_version(raw)
        elif op == ">":
            candidate = _bump_python_micro(_coerce_python_version(raw))
        else:
            continue

        if is_null(best_lower) or candidate > best_lower:
            best_lower = candidate

    return best_lower


__POETRY_UPDATER_PROCESSORS_______________________________________________________________ = ""


def apply_pins_inplace(pyproject_lines: List[str], pins: List[Pin]) -> Tuple[List[str], int]:
    """
    Applies the specified pins to the Poetry dependency sections.

    Notes:
        • Preserves original whitespace, alignment, quoting style, line terminators, and trailing comments.
        • Only overwrites the version token inside a simple quoted spec; skips composite constraints.

    Args:
        pyproject_lines: The raw `pyproject.toml` lines.
        pins: The pins to apply.

    Returns:
        A tuple `(patched_lines, updated_count)`.
    """
    pin_map: Dict[str, str] = {p.name.lower(): p.version for p in pins}

    out: List[str] = []
    updated = 0
    is_in_dependencies = False

    for raw_line in pyproject_lines:
        line, eol = split_line(raw_line)
        stripped = line.strip()

        if is_dependency_section_header(stripped):
            is_in_dependencies = True
            out.append(raw_line)
            continue

        if is_in_dependencies and SECTION_HEADER_PATTERN.match(stripped):
            is_in_dependencies = False
            out.append(raw_line)
            continue

        if not is_in_dependencies:
            out.append(raw_line)
            continue

        m = DEPENDENCY_LINE_PATTERN.match(line)
        if is_null(m):
            out.append(raw_line)
            continue

        name = m.group("name")
        raw_value = m.group("value")

        pin_key = name.lower()
        if pin_key not in pin_map:
            out.append(raw_line)
            continue

        qm = QUOTED_STRING_PATTERN.match(raw_value)
        if is_null(qm):
            out.append(raw_line)
            continue

        q = qm.group("q")
        spec = qm.group("value")
        new_spec = _override_version_spec(spec, pin_map[pin_key])
        if is_null(new_spec):
            logging.warning(
                "⚠️ Skip '%s' because the version constraint is not a simple spec: %s", name, raw_value.strip()
            )
            out.append(raw_line)
            continue

        new_value = f"{q}{new_spec}{q}"
        if new_value == raw_value:
            out.append(raw_line)
            continue

        start, end = m.span("value")
        patched_line = f"{line[:start]}{new_value}{line[end:]}{eol}"
        out.append(patched_line)
        updated += 1

    return out, updated


def compute_pins(
    session: requests.Session,
    dependency_names: Iterable[str],
    target_version: Version,
    include_prereleases: bool,
    *,
    current_versions: Optional[Dict[str, str]] = None,
) -> List[Pin]:
    """
    Computes the version pins for the specified dependency names.

    Args:
        session: The shared HTTP session configured with retries.
        dependency_names: The dependency names.
        target_version: The target Python version.
        include_prereleases: Whether prereleases are eligible.
        current_versions: The mapping of current pinned versions (optional).

    Returns:
        The list of resolved pins.
    """
    pins: List[Pin] = []
    current_versions = {k.lower(): v for k, v in current_versions.items()} if not is_null(current_versions) else {}

    for name in dependency_names:
        try:
            data = fetch_pypi_json(session, name)
            if is_null(data):
                continue
            version = get_latest_compatible_version(data, target_version, include_prereleases)
        except Exception as e:
            logging.warning("⚠️ Skip '%s' due to an error: %s", name, e)
            continue

        if not version:
            logging.warning("⚠️ No compatible release found for '%s'", name)
            continue

        current_version = current_versions.get(name.lower())
        if current_version == version:
            logging.debug("Pin '%s' unchanged at version '%s'", name, version)
        else:
            logging.info("Pin '%s' to '%s'", name, version)

        pins.append(Pin(name=name, version=version))

    return pins


### HELPERS ################################################


def _coerce_python_version(version: str) -> Version:
    """
    Coerces `version` to a normalized `Version` for python target selection.

    Notes:
        • Drops wildcard suffixes such as `"3.10.*"`.
        • Pads missing components to `MAJOR.MINOR.MICRO` (e.g., `"3.10"` → `"3.10.0"`).

    Args:
        version: The version string.

    Returns:
        The normalized `Version`.
    """
    s = str(version).strip()
    if not s:
        return Version(DEFAULT_TARGET_PYTHON)

    # Drop wildcards (best-effort)
    s = s.replace(".*", "").replace("*", "")

    parts: List[str] = []
    for p in s.split("."):
        if p.isdigit():
            parts.append(p)
            continue
        m = re.match(r"^(\d+)", p)
        if m:
            parts.append(m.group(1))
            continue
        break

    while len(parts) < 3:
        parts.append("0")

    return Version(".".join(parts[:3]))


def _bump_python_micro(version: Version) -> Version:
    """
    Bumps the micro version, turning an exclusive lower bound (e.g., `">3.10"`) into a usable minimum.

    Args:
        version: The base `Version`.

    Returns:
        The bumped `Version`.
    """
    release = list(version.release)
    while len(release) < 3:
        release.append(0)
    release[2] += 1
    return Version(".".join(str(x) for x in release[:3]))


def _override_version_spec(spec: str, new_version: str) -> Optional[str]:
    """
    Overrides the version token inside a simple Poetry/Pep440 spec string.

    Supported:
        • `"^1.2.3"` → `"^<new>"`
        • `"~1.2.3"` → `"~<new>"`
        • `">=1.2.3"` → `">=<new>"`
        • `"1.2.3"` → `"<new>"`

    Notes:
        • Does not modify composite constraints such as `">=1,<2"` or marker expressions such as `"; python_version<…"`.
        • Returns `None` when the spec is not a simple single-version spec.

    Args:
        spec: The inner quoted spec value (without quotes).
        new_version: The version string to inject.

    Returns:
        The updated spec, or `None` if the spec should not be modified.
    """
    stripped = spec.strip()
    if not stripped or stripped == "*":
        return None
    if "," in spec or ";" in spec:
        return None

    m = SIMPLE_VERSION_SPEC_PATTERN.match(spec)
    if is_null(m):
        return None

    op = m.group("op") or ""
    ws = m.group("ws") or ""
    trail = m.group("trail") or ""
    return f"{op}{ws}{new_version}{trail}"


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
        logging.warning("⚠️ Skip '%s' due to a transport failure", name)
        return None

    if is_null(payload):
        logging.warning("⚠️ Skip '%s' due to an empty response (HTTP %s)", name, status)
        return None

    if not isinstance(payload, dict):
        logging.warning("⚠️ Skip '%s' due to an unexpected JSON shape", name)
        return None

    return payload


__POETRY_UPDATER_RUNNERS__________________________________________________________________ = ""


def run(
    pyproject_path: Path,
    *,
    include_prereleases: bool = False,
    target_version: Optional[Version] = None,
    # Save
    dry_run: bool = False,
    inplace: bool = True,
    # Backup
    backup: bool = False,
    backup_dir: Optional[str] = None,
) -> int:
    """
    Runs the pin computation and optional patching.

    Args:
        pyproject_path: The `pyproject.toml` path.

        include_prereleases: Whether prereleases are eligible.
        target_version: The target Python version (or `None` to auto-detect from the `"python"` dependency).

        dry_run: Tells to not write changes; only logs the results.
        inplace: Whether to patch in-place.

        backup: Tells to create a timestamped backup on save.
        backup_dir: The directory where backups are stored.

    Returns:
        Exit code `0` on success, otherwise `1`.
    """
    lines = parse_pyproject_lines(pyproject_path)

    if is_null(target_version):
        detected = detect_lowest_python_version(lines)
        target_version = detected if not is_null(detected) else Version(DEFAULT_TARGET_PYTHON)

    dependencies = extract_pypi_dependencies(lines)

    if not dependencies:
        logging.error("❌ No dependencies found under Poetry dependency sections in '%s'", pyproject_path)
        return 1

    current_versions = extract_current_pins(lines)

    session = http.create_session_with_retries(
        total_retries=4,
        backoff_factor=0.5,
        allowed_methods={"GET"},
        headers={"User-Agent": PINNER_USER_AGENT},
        accept="application/json",
    )
    try:
        pins = compute_pins(
            session,
            dependencies,
            target_version,
            include_prereleases,
            current_versions=current_versions,
        )
    finally:
        try:
            session.close()
        except Exception:
            pass

    if not inplace:
        logging.info("✅ Resolved %d dependencies without patching '%s'", len(pins), pyproject_path)
        return 0

    pins_sorted = sorted(pins, key=lambda p: p.name.lower())
    patched_lines, updated_count = apply_pins_inplace(lines, pins_sorted)

    if updated_count <= 0:
        logging.info("✅ No dependency lines updated in '%s'", pyproject_path)
        return 0

    if dry_run:
        logging.info("[dry-run] Would patch '%s' (%d dependency lines updated)", pyproject_path, updated_count)
        return 0

    patched_text = "".join(patched_lines)
    write_text(
        pyproject_path,
        patched_text,
        # Save
        overwrite=True,
        # Backup
        backup=backup,
        backup_dir=Path(backup_dir) if backup_dir else None,
    )
    logging.info(
        "✅ Patched '%s' (%d dependency lines updated); run 'poetry lock' and 'poetry install'",
        pyproject_path,
        updated_count,
    )
    return 0


def run_root(
    root: Path,
    *,
    exclude: List[str] = DEFAULT_EXCLUDES,
    include_prereleases: bool = False,
    target_version: Optional[Version] = None,
    # Save
    dry_run: bool = False,
    inplace: bool = True,
    # Backup
    backup: bool = False,
    backup_dir: Optional[str] = None,
) -> int:
    """
    Scans `root` recursively for `PYPROJECT` and applies the same updater.

    Args:
        root: The root directory to scan.

        include_prereleases: Whether prereleases are eligible.
        target_version: The target Python version (or `None` to auto-detect per file from the `"python"` dependency).

        dry_run: Tells to not write changes; only logs the results.
        inplace: Whether to patch in-place.

        backup: Tells to create a timestamped backup on save.
        backup_dir: The directory where backups are stored.

    Returns:
        Exit code `0` on success, otherwise `1`.
    """
    pyprojects = find_pyproject_files(root, exclude=exclude)
    if not pyprojects:
        logging.error("❌ No 'pyproject.toml' files found under '%s'", root)
        return 1

    any_error = False
    for p in pyprojects:
        logging.info("Process '%s'", p)
        code = run(
            p,
            include_prereleases=include_prereleases,
            target_version=target_version,
            # Save
            dry_run=dry_run,
            inplace=inplace,
            # Backup
            backup=backup,
            backup_dir=backup_dir,
        )
        any_error |= code != 0

    if any_error:
        logging.error("❌ One or more updates failed under '%s'", root)
        return 1

    logging.info("✅ Updated %d file(s) under '%s'", len(pyprojects), root)
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

    if not is_null(args.root):
        args.root = resolve_path(args.root, must_exist=True)
    else:
        args.pyproject = resolve_path(args.pyproject, must_exist=True)

    if not is_null(args.python):
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
    # Add the path(s)
    ap.add_argument("--root", help="Root directory to scan recursively for 'pyproject.toml'.")
    ap.add_argument("--pyproject", default=DEFAULT_PYPROJECT, help="Path to 'pyproject.toml'.")
    # Add the parameter(s)
    ap.add_argument("--include-prereleases", action="store_true", help="Allow prerelease versions.")
    ap.add_argument(
        "--python",
        default=None,
        help="Target python version (e.g., '3.10.0'). When omitted, uses the lowest version from the 'python' spec.",
    )
    ap.add_argument("--verbose", action="store_true", help="Enable debug logging.")
    # Add the save parameter(s)
    ap.add_argument("--dry-run", help="Do not write changes; only log results.", action="store_true")
    ap.add_argument("--inplace", dest="inplace", action="store_true", help="Patch 'pyproject.toml' in-place.")
    ap.add_argument("--no-inplace", dest="inplace", action="store_false", help="Do not patch 'pyproject.toml'.")
    ap.set_defaults(inplace=True)
    # Add the backup parameter(s)
    ap.add_argument(
        "--backup",
        help="Create a timestamped backup of the previous file on save.",
        action="store_true",
    )
    ap.add_argument("--backup-dir", help="Directory to store backups (defaults to the 'pyproject.toml' directory).")
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

    if not is_null(args.root):
        sys.exit(
            run_root(
                args.root,
                include_prereleases=args.include_prereleases,
                target_version=args.python,
                # Save
                dry_run=args.dry_run,
                inplace=args.inplace,
                # Backup
                backup=args.backup,
                backup_dir=args.backup_dir,
            )
        )

    sys.exit(
        run(
            args.pyproject,
            include_prereleases=args.include_prereleases,
            target_version=args.python,
            # Save
            dry_run=args.dry_run,
            inplace=args.inplace,
            # Backup
            backup=args.backup,
            backup_dir=args.backup_dir,
        )
    )


def main() -> None:
    """Runs the CLI entry point."""
    run_with_args(parse_args())


if __name__ == "__main__":
    main()
