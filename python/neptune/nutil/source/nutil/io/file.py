#!/usr/bin/env python
##########################################################################################
# NAME
#   <NAME> - contains common utility functions
#
# AUTHOR
#   Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#   Copyright © 2013-2025 Florian Barras <https://barras.io>.
#   The MIT License (MIT) <https://opensource.org/licenses/MIT>.
##########################################################################################

import csv
import fnmatch
import json
from pathlib import Path
from urllib.request import urlopen

import validators

from nutil.common import *
from nutil.struct.util import set_index_name

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
    if hasattr(x, "to_json"):
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
    index_name="index",
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
    if not index_cols:
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


def globs_to_dirs(globs: List[str], *, suffix: str = "/**") -> Set[str]:
    """
    Derives the directories from the glob patterns that end with the `suffix`.

    Strategy:
        - Select the last non-wildcard path segment from patterns ending with the `suffix`.

    Args:
        globs: The list of repository-level glob patterns.
        suffix: The marker suffix that denotes directory patterns (defaults to `"/**"`).

    Returns:
        The set of directories discovered.
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
    Matches a POSIX relative path against any glob pattern.

    Args:
        rel_path: The relative path (POSIX separators).
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


def merge_globs(primary: List[str], extra: List[str]) -> List[str]:
    """
    Merges two glob lists, preserving order and removing duplicates.

    Args:
        primary: The primary list of patterns.
        extra: The additional list of patterns.

    Returns:
        The merged list of unique patterns in first-seen order.
    """
    seen: Set[str] = set()
    merged: List[str] = []
    for g in [*primary, *extra]:
        if g not in seen:
            merged.append(g)
            seen.add(g)
    return merged


def should_exclude_dir(rel_dir: str, exclude: List[str]) -> bool:
    """
    Decides whether a directory should be pruned based on the exclude globs.

    Notes:
        A trailing slash is appended to make `"**/dir/**"` style patterns work reliably.
    """
    rel_path = rel_dir.rstrip("/") + "/"
    return match_any_globs(rel_path, exclude)


def should_exclude_file(rel_path: str, exclude: List[str], include: List[str]) -> bool:
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
        - When `must_exist` is `True` (default), searches the CWD and all parent directories for an existing match;
          raises `FileNotFoundError` if not found.
        - When `must_exist` is `False`, does not search and does not require existence; returns an absolute path
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


def to_relative_posix_path(path: Path, root: Path) -> str:
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
