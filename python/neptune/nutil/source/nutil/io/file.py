#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide I/O utilities for files.
########################################################################################################################

from __future__ import annotations

import csv
import fnmatch
import json
import shutil
from dataclasses import is_dataclass
from pathlib import Path
from tempfile import NamedTemporaryFile
from typing import IO
from urllib.request import urlopen

import validators

from nutil.common import *
from nutil.scalar.string import NEWLINE
from nutil.struct.table.util import get_row_keys, get_row_values, Row
from nutil.struct.util import set_index_name

__FILE_ACCESSORS__________________________________________________________________________ = ""


#### GLOBS #################################################


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


### PATHS ##################################################


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
    path = Path(path).expanduser()

    if must_exist:
        if path.exists():
            return path.resolve()
        if not path.is_absolute() and path.parts:
            cwd = Path.cwd().resolve()
            for base in (cwd, *cwd.parents):
                candidate = base / path
                if candidate.exists():
                    return candidate.resolve()
        raise FileNotFoundError(
            f"Could not find '{path}'. Tried the CWD and all the CWD parents with the specified subpath"
        )

    path = path if path.is_absolute() else (Path.cwd() / path)
    return path.resolve() if path.exists() else path


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


__FILE_BUILDERS___________________________________________________________________________ = ""


def build_backup_path(path: Path, backup_dir: Optional[Path]) -> Path:
    """
    Builds a unique timestamped backup path for `path`,
    e.g., `"cache.json.20201230-153045.bak"` (or `"….2.bak"` if needed).
    """
    bd = backup_dir if backup_dir is not None else path.parent
    bd.mkdir(parents=True, exist_ok=True)

    ts = datetime.now().strftime("%Y%m%d-%H%M%S")
    candidate = bd / f"{path.name}.{ts}.bak"
    i = 1
    while candidate.exists():
        i += 1
        candidate = bd / f"{path.name}.{ts}.{i}.bak"
    return candidate


__FILE_CONVERTERS_________________________________________________________________________ = ""


def to_json(x: Any) -> Any:
    """
    Converts arbitrary Python containers into JSON-friendly structures.

    Transforms nested containers so they can be serialized by `json.dump` / `json.dumps` without a custom encoder.

    Behavior:
        • If `x` has a callable `to_json`, returns `x.to_json()`.
        • `dict` → same mapping with values converted recursively (keys preserved).
        • `list` / `tuple` → `list` with elements converted recursively.
        • `set` → sorted `list` of converted elements; if elements are not mutually orderable, sorts by `repr` for
          deterministic output.

    Notes:
        • Dictionary keys are not coerced to `str`. JSON requires string keys; use `json.dump(…, skipkeys=True)`
          or normalize the keys beforehand if needed.

    Args:
        x: Arbitrary Python object or structure.

    Returns:
        A JSON-serializable structure composed of `dict`, `list`, and JSON-native scalars.
    """
    if has_callable(x, "to_json"):
        return x.to_json()
    elif isinstance(x, dict):
        return {k: to_json(v) for k, v in x.items()}
    elif isinstance(x, (list, tuple)):
        return [to_json(item) for item in x]
    elif isinstance(x, set):
        items = [to_json(item) for item in x]
        try:
            items.sort()
        except TypeError:
            items.sort(key=repr)
        return items
    return x


__FILE_PARSERS____________________________________________________________________________ = ""


def parse_json(s: str) -> Optional[Dict[str, Any]]:
    """Attempts to parse `s` as JSON and returns the object on success, otherwise `None`."""
    try:
        data = json.loads(s)
        return data if isinstance(data, dict) else None
    except Exception:
        return None


__FILE_READERS____________________________________________________________________________ = ""


def read(
    path: Union[str, Path], encoding: str = DEFAULT_ENCODING, ignore: bool = False, newline: Optional[str] = None
) -> str:
    """
    Reads and returns the full text content of a local file or URL.

    Args:
        path: The local path or URL.
        encoding: The text encoding for local files or as a fallback for URLs.
        ignore: Ignores decoding errors when `True`.
        newline: The newline policy forwarded to `open(…, newline=…)`.

    Returns:
        The file contents as text.
    """
    if is_url(path):
        with urlopen(str(path)) as fh:
            encoding = encoding if not is_null(encoding) else fh.headers.get_content_charset()
            return fh.read().decode(encoding=encoding, errors="ignore" if ignore else "strict")
    with open(
        str(path),
        mode="r",
        encoding=encoding,
        errors="ignore" if ignore else None,
        newline=newline,
    ) as fh:
        return fh.read()


##############################


def read_iterator(
    path: Union[str, Path],
    encoding: str = DEFAULT_ENCODING,
    ignore: bool = False,
    newline: Optional[str] = None,
) -> Iterator[str]:
    """
    Yields the lines of a local file or URL response.

    Args:
        path: The local path or URL.
        encoding: The text encoding for local files or as a fallback for URLs.
        ignore: Ignores decoding errors when `True`.
        newline: The newline policy forwarded to `open(…, newline=…)`.

    Yields:
        The lines (including their terminators when present).
    """
    if is_url(path):
        with urlopen(str(path)) as fh:
            encoding = encoding if not is_null(encoding) else fh.headers.get_content_charset()
            for line in fh:
                yield line.decode(encoding=encoding, errors="ignore" if ignore else "strict")
    else:
        with open(
            str(path),
            mode="r",
            encoding=encoding,
            errors="ignore" if ignore else None,
            newline=newline,
        ) as fh:
            for line in fh:
                yield line


def read_enumerator(
    path: Union[str, Path],
    encoding: str = DEFAULT_ENCODING,
    ignore: bool = False,
    newline: Optional[str] = None,
) -> Iterator[Tuple[int, str]]:
    """
    Yields `(line_number, line)` for a local file or URL response.

    Args:
        path: The local path or URL.
        encoding: The text encoding for local files or as a fallback for URLs.
        ignore: Ignores decoding errors when `True`.
        newline: The newline policy forwarded to `open(…, newline=…)`.

    Yields:
        The 0-based line index and the corresponding line string.
    """
    if is_url(path):
        with urlopen(str(path)) as fh:
            encoding = encoding if not is_null(encoding) else fh.headers.get_content_charset()
            for i, line in enumerate(fh):
                yield i, line.decode(encoding=encoding, errors="ignore" if ignore else "strict")
    else:
        with open(
            str(path),
            mode="r",
            encoding=encoding,
            errors="ignore" if ignore else None,
            newline=newline,
        ) as fh:
            for i, line in enumerate(fh):
                yield i, line


##############################


def read_bytes(path: Union[str, Path]) -> bytes:
    """Reads and returns the full bytes content of a local file or URL."""
    if is_url(path):
        with urlopen(str(path)) as fh:
            return fh.read()
    with open(str(path), mode="rb") as fh:
        return fh.read()


def read_csv(
    path: Union[str, Path],
    encoding: str = DEFAULT_ENCODING,
    delimiter: str = ",",
    ignore: bool = False,
    index_cols: Optional[Union[int, List[int], str, List[str]]] = None,
    index_name: Optional[str] = None,
    na_values: Optional[Iterable[str]] = None,
    newline: Optional[str] = None,
    element_type: Optional[ElementType] = None,
    **kwargs: Any,
):
    """
    Reads a CSV file into a pandas DataFrame.

    Args:
        path: The local path or URL.
        encoding: The text encoding.
        delimiter: The field delimiter.
        ignore: Skips invalid lines when `True` (maps to `on_bad_lines="skip"`).
        index_cols: The `index_col` argument forwarded to pandas.
        index_name: Sets the index name when `index_cols` is not provided.
        na_values: The NA tokens.
        newline: The line terminator (forwarded as `lineterminator` when provided).
        element_type: The dtype (forwarded as `dtype`).
        **kwargs: Extra arguments forwarded to `pd.read_csv`.

    Returns:
        The parsed DataFrame.
    """
    na_values = [""] if na_values is None else list(na_values)

    read_kwargs: Dict[str, Any] = dict(kwargs)
    read_kwargs.setdefault("on_bad_lines", "skip" if ignore else "error")
    if newline is not None:
        read_kwargs.setdefault("lineterminator", newline)

    df = pd.read_csv(
        path,
        encoding=encoding,
        delimiter=delimiter,
        dtype=element_type,
        index_col=index_cols,
        na_values=na_values,
        **read_kwargs,
    )
    if not is_null(index_name) and not index_cols:
        set_index_name(df, index_name)
    return df


def read_json(
    path: Union[str, Path],
    encoding: str = DEFAULT_ENCODING,
    ignore: bool = False,
    newline: Optional[str] = None,
    **kwargs: Any,
) -> Any:
    """
    Reads and parses a JSON document from a local file or URL.

    Args:
        path: The local path or URL.
        encoding: The text encoding for local files.
        ignore: Ignores decoding errors when `True` (local file only).
        newline: The newline policy forwarded to `open(…, newline=…)` (local file only).
        **kwargs: Extra arguments forwarded to `json.load`.

    Returns:
        The decoded JSON payload.
    """
    if is_url(path):
        with urlopen(str(path)) as fh:
            return json.load(fh, **kwargs)
    with open(
        str(path),
        mode="r",
        encoding=encoding,
        errors="ignore" if ignore else None,
        newline=newline,
    ) as fh:
        return json.load(fh, **kwargs)


__FILE_WRITERS____________________________________________________________________________ = ""


def create_dir(path: Union[str, Path]) -> None:
    """Creates the directory at `path` (including parents) when missing."""
    Path(path).mkdir(parents=True, exist_ok=True)


##############################


def flush(path: Union[str, Path]) -> None:
    """Flushes and `fsync`s the file at the given `path` (best-effort)."""
    with open(str(path), "rb+") as fh:
        flush_handler(fh)


def flush_handler(file_handler: IO[Any]) -> None:
    """Flushes and `fsync`s the already opened file handle (best-effort, ignores errors)."""
    try:
        file_handler.flush()
        os.fsync(file_handler.fileno())
    except Exception:
        # Treat the `fsync` failures as non-fatal
        pass


############################################################


def atomic_write(
    path: Union[str, Path],
    *,
    writer: Callable[[IO[Any]], None],
    is_binary: bool = False,
    encoding: str = DEFAULT_ENCODING,
    newline: str = NEWLINE,
    ignore: bool = False,
    mode: Optional[int] = None,
    overwrite: bool = True,
    backup: bool = False,
    backup_dir: Optional[Path] = None,
) -> None:
    """
    Writes a file atomically (same-directory temp file + `os.replace`) and optionally makes a timestamped backup.

    Args:
        path: Destination path.
        writer: Function that writes the payload to an already opened file handle.
        is_binary: Indicates whether the file handle is opened in binary mode.
        encoding: The text encoding for text-mode.
        newline: The newline policy for text-mode temp files.
        ignore: Ignores encoding errors in text-mode when `True`.
        mode: Optional file-permission bits applied to the temp file before replacement.
        overwrite: Raises `FileExistsError` when `False` and the file exists.
        backup: Creates a dated backup of the previous file before replacing it when `True`.
        backup_dir: Directory in which to store backups; defaults to `path.parent`.

    Raises:
        FileExistsError: If `overwrite` is `False` and `path` exists.
        OSError: On write, backup, permission, or replace failures.
    """
    path = Path(path)
    if path.exists() and not overwrite:
        raise FileExistsError(f"File already exists: '{path}'")

    path.parent.mkdir(parents=True, exist_ok=True)

    temp_file: Any
    if is_binary:
        temp_file = NamedTemporaryFile("wb", delete=False, dir=path.parent)
    else:
        temp_file = NamedTemporaryFile(
            "w",
            delete=False,
            encoding=encoding,
            errors="ignore" if ignore else "strict",
            newline=newline,
            dir=path.parent,
        )

    temp_filename = temp_file.name
    backup_path: Optional[Path] = None
    is_backup_moved = False

    try:
        # Write to the temp file
        with temp_file as tfh:
            writer(tfh)
            flush_handler(tfh)

        # Prepare an optional backup of the existing file
        if backup and path.exists():
            backup_path = build_backup_path(path, backup_dir)

            # Try to atomically move the current file into the backup; if cross-filesystem, fall back to a copy
            try:
                os.replace(path, backup_path)
                is_backup_moved = True
            except OSError:
                shutil.copy2(path, backup_path)
                # Keep the original in place; it will be replaced by the new file below

        # Atomically replace the target with the new temp file
        try:
            if not is_null(mode):
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


############################################################


def write(
    path: Union[str, Path],
    content: str,
    append: bool = False,
    encoding: str = DEFAULT_ENCODING,
    ignore: bool = False,
    newline: Optional[str] = None,
) -> int:
    """
    Writes text content to a local file (non-atomic).

    Args:
        path: Destination path.
        content: The text content to write.
        append: Appends when `True`, otherwise overwrites.
        encoding: The text encoding.
        ignore: Ignores encoding errors when `True`.
        newline: The newline policy forwarded to `open(…, newline=…)`.

    Returns:
        The number of characters written.
    """
    with open(
        str(path),
        mode="a" if append else "w",
        encoding=encoding,
        errors="ignore" if ignore else None,
        newline=newline,
    ) as fh:
        return fh.write(content)


def write_bytes(
    path: Union[str, Path],
    content: Union[bytes, bytearray, memoryview],
    append: bool = False,
    *,
    # Atomic/backup options
    atomic: bool = True,
    backup: bool = False,
    backup_dir: Optional[Path] = None,
    mode: Optional[int] = None,
    overwrite: bool = True,
) -> Path:
    """
    Writes bytes to `path`.

    Behavior:
        • When `append=True`, writes in append mode (non-atomic).
        • Otherwise, writes atomically by default (`atomic=True`), with optional backups.

    Args:
        path: Destination path.
        content: Bytes payload.
        append: Appends when `True` (non-atomic).
        atomic: Enables atomic replace for non-append writes.
        backup: Creates a timestamped backup of the previous file (non-append writes only).
        backup_dir: Directory in which to store backups.
        mode: Optional file-permission bits applied to the written file (atomic mode only).
        overwrite: Raises `FileExistsError` when `False` and the file exists (non-append writes only).

    Returns:
        The written path as a `Path`.
    """
    path = Path(path)

    if append:
        path.parent.mkdir(parents=True, exist_ok=True)
        with open(str(path), mode="ab") as fh:
            fh.write(bytes(content))
            flush_handler(fh)
        return path

    if not atomic:
        if path.exists() and not overwrite:
            raise FileExistsError(f"File already exists: '{path}'")
        path.parent.mkdir(parents=True, exist_ok=True)
        with open(str(path), mode="wb") as fh:
            fh.write(bytes(content))
            flush_handler(fh)
        return path

    def _writer(fh: IO[Any]) -> None:
        fh.write(bytes(content))

    # Atomically replace the target with a temp file
    atomic_write(
        path,
        writer=_writer,
        is_binary=True,
        mode=mode,
        overwrite=overwrite,
        backup=backup,
        backup_dir=backup_dir,
    )
    return path


def write_csv(
    path: Union[str, Path],
    content: Union[Row, Iterable[Row]],
    append: bool = False,
    dialect: str = "excel",
    encoding: str = DEFAULT_ENCODING,
    ignore: bool = False,
    *,
    # Atomic/backup options
    atomic: bool = True,
    backup: bool = False,
    backup_dir: Optional[Path] = None,
    mode: Optional[int] = None,
    overwrite: bool = True,
    # Writing options
    header: Optional[Row] = None,
    lineterminator: str = NEWLINE,
    **kwargs: Any,
) -> None:
    """
    Writes CSV rows to `path`.

    Behavior:
        • Accepts a single `Row` or an iterable of `Row`.
        • If `append=True`, writes in append mode (non-atomic).
        • Otherwise, writes atomically by default (`atomic=True`), with optional backups.
        • When `header` is provided, writes a header row using `get_row_keys(header)`.
        • Always opens CSV files with `newline=""` and uses `lineterminator` to control row endings.

    Args:
        path: Destination file path.
        content: A single row or an iterable of rows.
        append: Appends when `True` (non-atomic).
        dialect: The CSV dialect (passed to `csv.writer`).
        encoding: The output encoding.
        ignore: Ignores encoding errors when `True`.

        atomic: Enables atomic replace for non-append writes.
        backup: Creates a timestamped backup of the previous file (non-append writes only).
        backup_dir: Directory in which to store backups.
        mode: Optional file-permission bits applied to the written file (atomic mode only).
        overwrite: Raises `FileExistsError` when `False` and the file exists (non-append writes only).

        header: Optional header row.
        lineterminator: Line terminator passed to `csv.writer` (default: `NEWLINE`).
        **kwargs: Extra keyword args forwarded to `csv.writer`.

    Raises:
        FileExistsError: If `overwrite` is `False` and `path` exists (non-append writes).
        OSError: On write, backup, permission, or replace failures.
    """
    path = Path(path)

    def _iter_rows(x: Union[Row, Iterable[Row]]) -> Iterable[Row]:
        if is_dict(x) or is_dataclass(x):
            return [x]
        return x

    rows = list(_iter_rows(content))

    if append:
        path.parent.mkdir(parents=True, exist_ok=True)
        with open(
            str(path),
            mode="a",
            encoding=encoding,
            errors="ignore" if ignore else None,
            newline="",
        ) as fh:
            w = csv.writer(fh, dialect=dialect, lineterminator=lineterminator, **kwargs)
            if header is not None:
                w.writerow(get_row_keys(header))
            for row in rows:
                w.writerow(get_row_values(row))
            flush_handler(fh)
        return

    if not atomic:
        if path.exists() and not overwrite:
            raise FileExistsError(f"File already exists: '{path}'")
        path.parent.mkdir(parents=True, exist_ok=True)
        with open(
            str(path),
            mode="w",
            encoding=encoding,
            errors="ignore" if ignore else None,
            newline="",
        ) as fh:
            w = csv.writer(fh, dialect=dialect, lineterminator=lineterminator, **kwargs)
            if header is not None:
                w.writerow(get_row_keys(header))
            for row in rows:
                w.writerow(get_row_values(row))
            flush_handler(fh)
        return

    def _writer(fh: IO[Any]) -> None:
        w = csv.writer(fh, dialect=dialect, lineterminator=lineterminator, **kwargs)
        if header is not None:
            w.writerow(get_row_keys(header))
        for row in rows:
            w.writerow(get_row_values(row))

    # Atomically replace the target with a temp file
    atomic_write(
        path,
        writer=_writer,
        is_binary=False,
        encoding=encoding,
        newline="",
        ignore=ignore,
        mode=mode,
        overwrite=overwrite,
        backup=backup,
        backup_dir=backup_dir,
    )


def write_json(
    path: Union[str, Path],
    content: Any,
    append: bool = False,
    encoding: str = DEFAULT_ENCODING,
    ignore: bool = False,
    indent: Optional[int] = None,
    newline: Optional[str] = None,
    *,
    # Atomic/backup options
    atomic: bool = True,
    backup: bool = False,
    backup_dir: Optional[Path] = None,
    mode: Optional[int] = None,
    overwrite: bool = True,
    # JSON options
    compact: bool = False,
    **kwargs: Any,
) -> None:
    """
    Writes JSON to `path`.

    Behavior:
        • Normalizes containers via `to_json(content)` for JSON-friendliness.
        • When `append=True`, writes in append mode (non-atomic).
        • Otherwise, writes atomically by default (`atomic=True`), with optional backups.
        • When `compact=True`, uses minimal separators and ignores `indent`.

    Args:
        path: Destination file path.
        content: The JSON payload.
        append: Appends when `True` (non-atomic).
        encoding: The output encoding.
        ignore: Ignores encoding errors when `True`.
        indent: The pretty-print indentation; ignored when `compact=True`.
        newline: The newline policy forwarded to `open(…, newline=…)` in append mode.
        atomic: Enables atomic replace for non-append writes.
        backup: Creates a timestamped backup of the previous file (non-append writes only).
        backup_dir: Directory in which to store backups.
        mode: Optional file-permission bits applied to the written file (atomic mode only).
        overwrite: Raises `FileExistsError` when `False` and the file exists (non-append writes only).
        compact: Writes compact JSON when `True`.
        **kwargs: Extra keyword args forwarded to `json.dump`.

    Raises:
        FileExistsError: If `overwrite` is `False` and `path` exists (non-append writes).
        OSError: On write, backup, permission, or replace failures.
        ValueError/TypeError: When JSON serialization fails.
    """
    path = Path(path)
    payload = to_json(content)

    def _dump(fh: IO[Any]) -> None:
        if compact:
            json.dump(payload, fh, ensure_ascii=False, separators=(",", ":"), default=str, **kwargs)
        else:
            json.dump(
                payload, fh, ensure_ascii=False, indent=indent if indent is not None else 2, default=str, **kwargs
            )

    if append:
        path.parent.mkdir(parents=True, exist_ok=True)
        with open(
            str(path),
            mode="a",
            encoding=encoding,
            errors="ignore" if ignore else None,
            newline="" if newline is None else newline,
        ) as fh:
            _dump(fh)
            flush_handler(fh)
        return

    if not atomic:
        if path.exists() and not overwrite:
            raise FileExistsError(f"File already exists: '{path}'")
        path.parent.mkdir(parents=True, exist_ok=True)
        with open(
            str(path),
            mode="w",
            encoding=encoding,
            errors="ignore" if ignore else None,
            newline="",
        ) as fh:
            _dump(fh)
            flush_handler(fh)
        return

    # Atomically replace the target with a temp file
    atomic_write(
        path,
        writer=_dump,
        is_binary=False,
        encoding=encoding,
        newline="",
        ignore=ignore,
        mode=mode,
        overwrite=overwrite,
        backup=backup,
        backup_dir=backup_dir,
    )


def write_text(
    path: Union[str, Path],
    text: str,
    append: bool = False,
    encoding: str = DEFAULT_ENCODING,
    ignore: bool = False,
    newline: Optional[str] = None,
    *,
    # Atomic/backup options
    atomic: bool = True,
    backup: bool = False,
    backup_dir: Optional[Path] = None,
    mode: Optional[int] = None,
    overwrite: bool = True,
) -> None:
    """
    Writes plain text to `path`.

    Behavior:
        • When `append=True`, writes in append mode (non-atomic).
        • Otherwise, writes atomically by default (`atomic=True`), with optional backups.

    Args:
        path: Destination file path.
        text: Full file contents to write.
        append: Appends when `True` (non-atomic).
        encoding: The output encoding.
        ignore: Ignores encoding errors when `True`.
        newline: The newline policy forwarded to `open(…, newline=…)` in append mode.
        atomic: Enables atomic replace for non-append writes.
        backup: Creates a timestamped backup of the previous file (non-append writes only).
        backup_dir: Directory in which to store backups.
        mode: Optional file-permission bits applied to the written file (atomic mode only).
        overwrite: Raises `FileExistsError` when `False` and the file exists (non-append writes only).

    Raises:
        FileExistsError: If `overwrite` is `False` and `path` exists (non-append writes).
        OSError: On write, backup, permission, or replace failures.
    """
    path = Path(path)

    if append:
        path.parent.mkdir(parents=True, exist_ok=True)
        with open(
            str(path),
            mode="a",
            encoding=encoding,
            errors="ignore" if ignore else None,
            newline=newline,
        ) as fh:
            fh.write(text)
            flush_handler(fh)
        return

    if not atomic:
        if path.exists() and not overwrite:
            raise FileExistsError(f"File already exists: '{path}'")
        path.parent.mkdir(parents=True, exist_ok=True)
        with open(
            str(path),
            mode="w",
            encoding=encoding,
            errors="ignore" if ignore else None,
            newline=newline,
        ) as fh:
            fh.write(text)
            flush_handler(fh)
        return

    def _writer(fh: IO[Any]) -> None:
        fh.write(text)

    # Atomically replace the target with a temp file
    atomic_write(
        path,
        writer=_writer,
        is_binary=False,
        encoding=encoding,
        newline="" if newline is None else newline,
        ignore=ignore,
        mode=mode,
        overwrite=overwrite,
        backup=backup,
        backup_dir=backup_dir,
    )


__FILE_VALIDATORS_________________________________________________________________________ = ""


def is_url(path: Union[str, Path]) -> bool:
    """Indicates whether `path` is a URL string."""
    if not isinstance(path, str):
        return False
    try:
        return bool(validators.url(path))
    except Exception:
        return False
