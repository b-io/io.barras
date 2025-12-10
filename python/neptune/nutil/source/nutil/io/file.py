#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide common utilities.
########################################################################################################################

import csv
import fnmatch
import json
import shutil
from pathlib import Path
from tempfile import NamedTemporaryFile
from typing import IO
from urllib.request import urlopen

import validators

from nutil.common import *
from nutil.struct.table.util import get_row_keys, get_row_values, Row
from nutil.struct.util import set_index_name

## WRITERS ###############################################################################


def flush(path: Union[str, Path]) -> None:
    """Flushes and `fsync`s the file at the given `path` (best-effort)."""
    with open(path, "rb+") as fh:
        flush_handler(fh)


def flush_handler(file_handler: IO[Any]) -> None:
    """Flushes and `fsync`s the already opened file handle (best-effort, ignores errors)."""
    try:
        file_handler.flush()
        os.fsync(file_handler.fileno())
    except Exception:
        # Treat the `fsync` failures as non-fatal
        pass


### CSV ####################################################


def write_csv(
    path: Union[str, Path],
    rows: Iterable[Row],
    *,
    # Backup options
    backup: bool = False,
    backup_dir: Optional[Path] = None,
    # File options
    encoding: str = DEFAULT_ENCODING,
    mode: Optional[int] = None,
    overwrite: bool = True,
    # Writing options
    delimiter=",",
    doublequote=True,
    escapechar=None,
    header: Optional[Row] = None,
    lineterminator="\n",
    quotechar='"',
    quoting=csv.QUOTE_MINIMAL,
    skipinitialspace=False,
) -> None:
    """
    Writes rows to a CSV file at `path` atomically using a temporary file and `os.replace`.

    The file is first written to a temporary file in the same directory and then swapped into
    place. This avoids partially written files and makes the operation crash-safe.

    Additionally, when `backup` is `True`, a **dated backup** of the previous file is created
    *before* it is replaced. By default, the backup is created next to `path` as
    `"<name>.<YYYYMMDD-HHMMSS>[.<n>].bak"`, or inside `backup_dir` when provided.

    Each element of `rows` can be:
        * a dataclass instance → fields are written in definition order,
        * a `Mapping`         → `row.values()` are written in insertion order,
        * any other iterable  → written as-is.

    The optional `header` accepts the same shapes:
        * dataclass instance → field names in definition order,
        * `Mapping`          → keys of the mapping,
        * other iterable     → used directly as the header row.

    Args:
        path: Destination file path.
        rows: The rows to write; each row is either an iterable of fields or a `Mapping`.

        backup: If `True`, creates a timestamped backup of the current file (if it exists)
            before replacing it.
        backup_dir: Directory in which to store backups. Defaults to `path.parent`.

        encoding: Text encoding for the output file (default: `DEFAULT_ENCODING`, e.g. `"utf-8"`).
        mode: Optional file-permission bits (e.g., `0o644`) applied to the temporary file
            before it is swapped into place.
        overwrite: If `False` and `path` already exists, raises `FileExistsError` instead of
            overwriting the file.

        delimiter: Field delimiter passed to `csv.writer`.
        doublequote: Whether to escape quotes by doubling them (see `csv.writer`).
        escapechar: Escape character for `csv.writer`, or `None` to disable.
        header: Optional header row to write before `rows`. Interpreted as described
            above via `get_row_keys`.
        lineterminator: Line terminator passed to `csv.writer` (default: `"\n"`).
        quotechar: Quote character for `csv.writer`.
        quoting: Quoting strategy (e.g., `csv.QUOTE_MINIMAL`).
        skipinitialspace: Whether to skip whitespace immediately following the delimiter.

    Raises:
        FileExistsError: If `overwrite` is `False` and `path` already exists.
        OSError: If directory creation, writing, permission changes, backup move/copy, or the
            atomic replacement fails.
        ValueError: If `csv.writer` fails to serialize a row.
        TypeError: If a row or field has an unsupported type for `csv.writer`.
    """
    path: Path = Path(path)
    if path.exists() and not overwrite:
        raise FileExistsError(f"File already exists: '{path}'")

    path.parent.mkdir(parents=True, exist_ok=True)
    temp_file = NamedTemporaryFile(
        "w", delete=False, encoding=encoding, newline="", dir=path.parent
    )
    temp_filename = temp_file.name
    backup_path: Optional[Path] = None
    is_backup_moved: bool = False

    try:
        # Write to the temp file
        with temp_file as tfh:
            writer = csv.writer(
                tfh,
                delimiter=delimiter,
                doublequote=doublequote,
                escapechar=escapechar,
                lineterminator=lineterminator,
                quotechar=quotechar,
                quoting=quoting,
                skipinitialspace=skipinitialspace,
            )
            if header:
                writer.writerow(get_row_keys(header))
            for row in rows:
                writer.writerow(get_row_values(row))
            flush_handler(tfh)

        # Prepare an optional backup of the existing file
        if backup and path.exists():
            bd = backup_dir if backup_dir is not None else path.parent
            bd.mkdir(parents=True, exist_ok=True)

            ts = datetime.now().strftime("%Y%m%d-%H%M%S")
            candidate = (
                bd / f"{path.name}.{ts}.bak"
            )  # e.g., `"cache.json.20201230-153045.bak"` (or `"… .2.bak"` if needed)
            i = 1
            while candidate.exists():
                i += 1
                candidate = bd / f"{path.name}.{ts}.{i}.bak"
            backup_path = candidate

            # Try to atomically move the current file into the backup; if cross-FS, fall back to a copy
            try:
                os.replace(path, backup_path)
                is_backup_moved = True
            except OSError:
                shutil.copy2(path, backup_path)
                # Keep the original in place; it will be replaced by the new file below

        # Atomically replace the target with the new temp file
        try:
            if mode is not None:
                os.chmod(temp_filename, mode)
            os.replace(temp_filename, path)
        except Exception as e:
            # Best-effort rollback if we moved the original away
            if is_backup_moved and backup_path and backup_path.exists():
                try:
                    os.replace(backup_path, path)
                except Exception:
                    pass
            raise e

    finally:
        # Best-effort cleanup of the temp file (if any)
        try:
            os.unlink(temp_filename)
        except Exception:
            pass


### JSON ###################################################


def parse_json(s: str) -> Optional[Dict[str, Any]]:
    """Attempts to parse the response text as JSON and returns the object on success, otherwise `None`."""
    try:
        obj = json.loads(s)
        return obj if isinstance(obj, dict) else None
    except Exception:
        return None


def write_json(
    path: Union[str, Path],
    data: Any,
    *,
    # Backup options
    backup: bool = False,
    backup_dir: Optional[Path] = None,
    # File options
    encoding: str = DEFAULT_ENCODING,
    mode: Optional[int] = None,
    overwrite: bool = True,
    # Writing options
    compact: bool = False,
) -> None:
    """
    Writes JSON to `path` atomically using a temporary file and `os.replace`.

    The file is first written to a temporary file in the same directory and then swapped into
    place. This avoids partially written files and makes the operation crash-safe.

    Additionally, when `backup` is `True`, a **dated backup** of the previous file is created
    *before* it is replaced. By default, the backup is created next to `path` as
    `"<name>.<YYYYMMDD-HHMMSS>[.<n>].bak"`, or inside `backup_dir` when provided.

    JSON specifics:
        • Normalizes containers via `to_json(data)` so that `tuple` → `list`, `set` → sorted `list`,
          and nested containers are JSON-friendly.
        • Uses `ensure_ascii=False` to preserve non-ASCII characters.
        • Uses `default=str` to stringify non-JSON-native objects (e.g., `Path`, `datetime`).
        • With `compact=True`, uses minimal separators `(",", ":")`; otherwise pretty-prints with
          `indent=2`.

    Args:
        path: Destination file path.
        data: Payload to serialize. Containers are normalized via `to_json`.

        backup: If `True`, creates a timestamped backup of the current file (if it exists)
            before replacing it.
        backup_dir: Directory in which to store backups. Defaults to `path.parent`.

        encoding: Text encoding for the output file (default: `DEFAULT_ENCODING`, e.g. `"utf-8"`).
        mode: Optional file-permission bits (e.g., `0o644`) applied to the temporary file
            before it is swapped into place.
        overwrite: If `False` and `path` already exists, raises `FileExistsError` instead of
            overwriting the file.

        compact: If `True`, writes compact JSON; otherwise writes pretty-printed JSON.

    Raises:
        FileExistsError: If `overwrite` is `False` and `path` already exists.
        OSError: If directory creation, writing, permission changes, backup move/copy, or the
            atomic replacement fails.
        ValueError: If `json.dump` fails to serialize `data`.
        TypeError: If `json.dump` encounters unsupported types even after `to_json`.
    """
    path: Path = Path(path)
    if path.exists() and not overwrite:
        raise FileExistsError(f"File already exists: '{path}'")

    path.parent.mkdir(parents=True, exist_ok=True)
    temp_file = NamedTemporaryFile(
        "w", delete=False, encoding=encoding, newline="", dir=path.parent
    )
    temp_filename = temp_file.name
    backup_path: Optional[Path] = None
    is_backup_moved: bool = False

    try:
        # Write to the temp file
        with temp_file as tfh:
            payload = to_json(data)
            if compact:
                json.dump(payload, tfh, ensure_ascii=False, separators=(",", ":"), default=str)
            else:
                json.dump(payload, tfh, ensure_ascii=False, indent=2, default=str)
            flush_handler(tfh)

        # Prepare an optional backup of the existing file
        if backup and path.exists():
            bd = backup_dir if backup_dir is not None else path.parent
            bd.mkdir(parents=True, exist_ok=True)

            ts = datetime.now().strftime("%Y%m%d-%H%M%S")
            candidate = (
                bd / f"{path.name}.{ts}.bak"
            )  # e.g., `"cache.json.20201230-153045.bak"` (or `"… .2.bak"` if needed)
            i = 1
            while candidate.exists():
                i += 1
                candidate = bd / f"{path.name}.{ts}.{i}.bak"
            backup_path = candidate

            # Try to atomically move the current file into the backup; if cross-FS, fall back to a copy
            try:
                os.replace(path, backup_path)
                is_backup_moved = True
            except OSError:
                shutil.copy2(path, backup_path)
                # Keep the original in place; it will be replaced by the new file below

        # Atomically replace the target with the new temp file
        try:
            if mode is not None:
                os.chmod(temp_filename, mode)
            os.replace(temp_filename, path)
        except Exception as e:
            # Best-effort rollback if we moved the original away
            if is_backup_moved and backup_path and backup_path.exists():
                try:
                    os.replace(backup_path, path)
                except Exception:
                    pass
            raise e

    finally:
        # Best-effort cleanup of the temp file (if any)
        try:
            os.unlink(temp_filename)
        except Exception:
            pass


def to_json(x: Any) -> Any:
    """
    Converts arbitrary Python containers into JSON-friendly structures.

    Transforms the nested containers so they can be serialized by `json.dump` / `json.dumps`
    without a custom encoder. Specifically:
      * `dict` → the same structure with values converted recursively (the keys are left as is).
      * `list` / `tuple` → a `list` with the elements converted recursively.
      * `set` → a *sorted* `list` of the converted elements; if the natural ordering fails,
        the elements are sorted by `repr` for deterministic output.

    Notes:
        • The dictionary keys are *not* coerced to strings. JSON requires string keys; use
          `json.dump(…, skipkeys=True)` or normalize the keys beforehand if needed.

    Args:
        x: An arbitrary Python object or structure.

    Returns:
        A structure composed of `dict`, `list`, and JSON-native scalars suitable for
        the standard JSON serialization.
    """
    if isinstance(x, dict):
        return {k: to_json(v) for k, v in x.items()}
    elif isinstance(x, (list, tuple)):
        return [to_json(item) for item in x]
    elif isinstance(x, set):
        items = [to_json(item) for item in x]
        try:
            items.sort()  # try the natural ordering
        except TypeError:
            items.sort(key=repr)  # the deterministic fallback
        return items
    return x


### TEXT ###################################################


def write_text(
    path: Union[str, Path],
    text: str,
    *,
    # Backup options
    backup: bool = False,
    backup_dir: Optional[Path] = None,
    # File options
    encoding: str = DEFAULT_ENCODING,
    mode: Optional[int] = None,
    overwrite: bool = True,
) -> None:
    """
    Writes plain text to `path` atomically using a temporary file and `os.replace`.

    The file is first written to a temporary file in the same directory and then swapped into
    place. This avoids partially written files and makes the operation crash-safe.

    Additionally, when `backup` is `True`, a **dated backup** of the previous file is created
    *before* it is replaced. By default, the backup is created next to `path` as
    `"<name>.<YYYYMMDD-HHMMSS>[.<n>].bak"`, or inside `backup_dir` when provided.

    Args:
        path: Destination file path.
        text: Full file contents to write.

        backup: If `True`, creates a timestamped backup of the current file (if it exists)
            before replacing it.
        backup_dir: Directory in which to store backups. Defaults to `path.parent`.

        encoding: Text encoding for the output file (default: `DEFAULT_ENCODING`, e.g. `"utf-8"`).
        mode: Optional file-permission bits (e.g., `0o644`) applied to the temporary file
            before it is swapped into place.
        overwrite: If `False` and `path` already exists, raises `FileExistsError` instead of
            overwriting the file.

    Raises:
        FileExistsError: If `overwrite` is `False` and `path` already exists.
        OSError: If directory creation, writing, permission changes, backup move/copy, or the
            atomic replacement fails.
    """
    path: Path = Path(path)
    if path.exists() and not overwrite:
        raise FileExistsError(f"File already exists: '{path}'")

    path.parent.mkdir(parents=True, exist_ok=True)
    temp_file = NamedTemporaryFile(
        "w", delete=False, encoding=encoding, newline="", dir=path.parent
    )
    temp_filename = temp_file.name
    backup_path: Optional[Path] = None
    is_backup_moved: bool = False

    try:
        # Write to the temp file
        with temp_file as tfh:
            tfh.write(text)
            flush_handler(tfh)

        # Optional backup of the existing file
        if backup and path.exists():
            bd = backup_dir if backup_dir is not None else path.parent
            bd.mkdir(parents=True, exist_ok=True)

            ts = datetime.now().strftime("%Y%m%d-%H%M%S")
            candidate = bd / f"{path.name}.{ts}.bak"
            i = 1
            while candidate.exists():
                i += 1
                candidate = bd / f"{path.name}.{ts}.{i}.bak"
            backup_path = candidate

            # Try an atomic move; fall back to a copy across filesystems
            try:
                os.replace(path, backup_path)
                is_backup_moved = True
            except OSError:
                shutil.copy2(path, backup_path)

        # Atomically replace the target with the new temp file
        try:
            if mode is not None:
                os.chmod(temp_filename, mode)
            os.replace(temp_filename, path)
        except Exception as e:
            # Best-effort rollback if we moved the original away
            if is_backup_moved and backup_path and backup_path.exists():
                try:
                    os.replace(backup_path, path)
                except Exception:
                    pass
            raise e

    finally:
        # Best-effort cleanup of the temp file
        try:
            os.unlink(temp_filename)
        except Exception:
            pass


## FILE CONVERTERS #######################################################################

__FILE_CONVERTERS___________________________________________ = ""


def parse_json(s: str) -> Optional[Dict[str, Any]]:
    """Attempts to parse the response text as JSON and returns the object on success, otherwise `None`."""
    try:
        obj = json.loads(s)
        return obj if isinstance(obj, dict) else None
    except Exception:
        return None


############################################################


def to_json(x: Any) -> Any:
    """Serializes arbitrary containers into JSON-friendly structures (e.g., a `set` → a sorted `list`)."""
    if has_callable(x, "to_json"):
        return x.to_json()
    elif isinstance(x, dict):
        return {k: to_json(v) for k, v in x.items()}
    elif isinstance(x, (list, tuple)):
        return [to_json(x) for x in x]
    elif isinstance(x, set):
        return sorted(to_json(x) for x in x)
    return x


## FILE GENERATORS #######################################################################

__FILE_GENERATORS___________________________________________ = ""


def create_dir(path):
    if not os.path.exists(path):
        os.makedirs(path)


## FILE PROCESSORS #######################################################################

__FILE_PROCESSORS___________________________________________ = ""


def format_dir(dir):
    if not dir:
        return ""
    if dir[-1] == "/" or dir[-1] == "\\":
        dir = dir[:-1]
    return dir + "/"


### READING ################################################


def read(path, encoding=DEFAULT_ENCODING, ignore=False, newline=None):
    if validators.url(path):
        with urlopen(path) as f:
            encoding = encoding if not is_null(encoding) else f.headers.get_content_charset()
            return f.read().decode(encoding=encoding)
    with open(
        path, mode="r", encoding=encoding, errors="ignore" if ignore else None, newline=newline
    ) as f:
        return f.read()


def read_iterator(path, encoding=DEFAULT_ENCODING, ignore=False, newline=None):
    if validators.url(path):
        with urlopen(path) as f:
            encoding = encoding if not is_null(encoding) else f.headers.get_content_charset()
            for line in f:
                yield line.decode(encoding=encoding)
    else:
        with open(
            path, mode="r", encoding=encoding, errors="ignore" if ignore else None, newline=newline
        ) as f:
            for line in f:
                yield line


def read_enumerator(path, encoding=DEFAULT_ENCODING, ignore=False, newline=None):
    if validators.url(path):
        with urlopen(path) as f:
            encoding = encoding if not is_null(encoding) else f.headers.get_content_charset()
            for i, line in enumerate(f):
                yield i, line.decode(encoding=encoding)
    else:
        with open(
            path, mode="r", encoding=encoding, errors="ignore" if ignore else None, newline=newline
        ) as f:
            for i, line in enumerate(f):
                yield i, line


def read_bytes(path):
    if validators.url(path):
        with urlopen(path) as f:
            return f.read()
    with open(path, mode="rb") as f:
        return f.read()


def read_csv(
    path,
    encoding=DEFAULT_ENCODING,
    delimiter=",",
    ignore=False,
    index_cols=None,
    index_name: Optional[str] = None,
    na_values=[""],
    newline=None,
    element_type: Optional[Union[np.dtype[Any], Type[Any]]] = None,
    **kwargs,
):
    df = pd.read_csv(
        path,
        encoding=encoding,
        delimiter=delimiter,
        dtype=element_type,
        error_bad_lines=not ignore,
        index_col=index_cols,
        lineterminator=newline,
        na_values=na_values,
        **kwargs,
    )
    if not is_null(index_name) and not index_cols:
        set_index_name(df, index_name)
    return df


def read_json(path, encoding=DEFAULT_ENCODING, ignore=None, newline=None, **kwargs):
    if validators.url(path):
        with urlopen(path) as f:
            return json.load(f, **kwargs)
    with open(
        path, mode="r", encoding=encoding, errors="ignore" if ignore else None, newline=newline
    ) as f:
        return json.load(f, **kwargs)


### SEARCHING ##############################################

#### GLOBS ###################


def get_dirnames_from_globs(globs: List[str], *, suffix: str = "/**") -> Set[str]:
    """
    Derives the directory basenames from the glob patterns that end with the `suffix`.

    Strategy:
        • Selects the last non-wildcard path segment from the patterns ending with the `suffix`.

    Args:
        globs: The list of repository-level glob patterns.
        suffix: The marker suffix that denotes the directory patterns (defaults to `"/**"`).

    Returns:
        The set of directory basenames discovered.
    """
    names: Set[str] = set()
    for glob in globs:
        if glob.endswith(suffix):
            core = glob[: -len(suffix)]
            segment = core.split("/")[-1]
            if segment and not any(ch in segment for ch in "*?[]"):  # a plain segment
                names.add(segment)
    return names


def match_any_globs(rel_path: str, globs: Iterable[str]) -> bool:
    """
    Matches a POSIX relative path against any of the glob patterns.

    Args:
        rel_path: The relative path (uses the POSIX separators).
        globs: The iterable of glob patterns.

    Returns:
        `True` if at least one pattern matches; otherwise `False`.
    """
    for glob in globs:
        if fnmatch.fnmatch(rel_path, glob):
            return True
        # Treat `"**/"` as optional so the root-level files match `"**/*.ext"`
        if glob.startswith("**/") and fnmatch.fnmatch(rel_path, glob[3:]):
            return True
    return False


def exclude_dir(rel_dir: str, exclude: List[str]) -> bool:
    """
    Decides whether a directory should be pruned based on the exclude globs.

    Notes:
        The trailing slash is appended to make `"**/dir/**"`-style patterns work reliably.
    """
    rel_path = rel_dir.rstrip("/") + "/"
    return match_any_globs(rel_path, exclude)


def exclude_file(rel_path: str, exclude: List[str], include: List[str]) -> bool:
    """
    Decides whether a file should be excluded based on the include/exclude globs.

    Args:
        rel_path: The POSIX-style relative path (e.g., `"src/pkg/mod.py"`).
        exclude: The list of exclude patterns.
        include: The list of include patterns.

    Returns:
        `True` if the file should be skipped; otherwise `False`.
    """
    if include and not match_any_globs(rel_path, include):
        return True
    if exclude and match_any_globs(rel_path, exclude):
        return True
    return False


#### PATHS ###################


def join_posix_paths(a: str, b: str) -> str:
    """Joins two POSIX path fragments into a clean relative path."""
    return "/".join(p for p in (a, b) if p)


def resolve_path(path: Union[str, Path], *, must_exist: bool = True) -> Path:
    """
    Resolves a `path` relative to the current working directory (and its parents), then returns an absolute `Path`.

    Behavior:
        • When `must_exist` is `True` (default), searches the CWD and all the parent directories for an existing match;
          raises `FileNotFoundError` if not found.
        • When `must_exist` is `False`, does not search and does not require existence; returns an absolute path
          composed against the CWD if the input was relative.

    Args:
        path: The input path-like to resolve.
        must_exist: Whether the path must exist to be considered valid.

    Returns:
        The absolute `Path`. If `must_exist` is `False` and the target does not exist, returns an absolute path without
        verifying existence.

    Raises:
        FileNotFoundError: When `must_exist` is `True` and the path cannot be located.
    """
    p = Path(path).expanduser()

    if must_exist:
        if p.exists():
            return p.resolve()
        if not p.is_absolute() and p.parts:
            cwd = Path.cwd().resolve()
            for base in (cwd, *cwd.parents):
                candidate = base / p
                if candidate.exists():
                    return candidate.resolve()
        raise FileNotFoundError(
            f"Could not find '{p}'. Tried the CWD and all the CWD parents with the specified subpath"
        )

    p = p if p.is_absolute() else (Path.cwd() / p)
    return p.resolve() if p.exists() else p


def to_relative_posix_path(path: Union[str, Path], root: Path) -> str:
    """
    Converts a path to a POSIX-style relative string.

    Args:
        path: The absolute or relative path to convert.
        root: The root directory used as the base.

    Returns:
        The POSIX-style relative path (`""` for the root itself).
    """
    rel_path = os.path.relpath(str(path), str(root))
    return "" if rel_path == "." else rel_path.replace("\\", "/")


### WRITING ################################################


def write(path, content, append=False, encoding=DEFAULT_ENCODING, ignore=False, newline=None):
    with open(
        path,
        mode="a" if append else "w",
        encoding=encoding,
        errors="ignore" if ignore else None,
        newline=newline,
    ) as f:
        return f.write(content)


def write_bytes(path, content, append=False, ignore=False):
    with open(path, mode="ab" if append else "wb", errors="ignore" if ignore else None) as f:
        return f.write(content)


def write_csv(
    path,
    content,
    append=False,
    dialect="excel",
    encoding=DEFAULT_ENCODING,
    ignore=False,
    newline=None,
    **kwargs,
):
    with open(
        path,
        mode="a" if append else "w",
        encoding=encoding,
        errors="ignore" if ignore else None,
        newline=newline,
    ) as f:
        if is_dict(content):
            return csv.writer(f, dialect=dialect, **kwargs).writerow(content)
        else:
            return csv.writer(f, dialect=dialect, **kwargs).writerows(content)


def write_json(
    path,
    content,
    append=False,
    encoding=DEFAULT_ENCODING,
    ignore=False,
    indent=None,
    newline=None,
    **kwargs,
):
    with open(
        path,
        mode="a" if append else "w",
        encoding=encoding,
        errors="ignore" if ignore else None,
        newline=newline,
    ) as f:
        return json.dump(content, f, indent=indent, **kwargs)


def write_text(path: Path, text: str) -> None:
    """
    Writes the `text` to the `path` atomically.

    Args:
        path: The destination file path.
        text: The full file text to write.

    Raises:
        OSError: Propagated only from `Path.replace` if it fails.
    """
    temp_path = path.with_suffix(path.suffix + ".tmp")
    temp_path.parent.mkdir(parents=True, exist_ok=True)
    with temp_path.open("w", encoding="utf-8", newline="") as fh:
        fh.write(text)
        try:
            fh.flush()
            os.fsync(fh.fileno())
        except Exception:
            # Treat the `fsync` failures as non-fatal
            pass
    temp_path.replace(path)
