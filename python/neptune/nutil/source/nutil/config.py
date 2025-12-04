#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

# CONFIGURATION UTILITIES ##############################################################################################
# Goal
#   Provide a robust configuration loader around `configparser.ConfigParser` with environment-variable interpolation,
#   string-only option coercion, and typed accessors (including enums) that return `Optional[...]` when unspecified.
#
# Terminology
#   • a `section` is a top-level group in the properties file (e.g., 'common', 'console').
#   • an `option` is the key inside a section (e.g., 'env', 'severityLevel').
#   • a `value` is the string stored by `ConfigParser` (we coerce Python inputs to strings at the boundary).

from __future__ import annotations

import json
from configparser import BasicInterpolation, ConfigParser
from io import StringIO

from nutil.common import *
from nutil.enums import *

# CONFIG CLASSES #######################################################################################################

__CONFIG_CLASSES____________________________________________ = ""


class EnvInterpolation(BasicInterpolation):
    """Extends the basic property parser to handle environment variables."""

    def before_get(self, parser, section, option, value, defaults):
        value = super().before_get(parser, section, option, value, defaults)
        return os.path.expandvars(value)


# CONFIG CONSTANTS #####################################################################################################

__CONFIG_CONSTANTS__________________________________________ = ""

CONFIG: ConfigParser = ConfigParser(interpolation=EnvInterpolation())

# The default configuration (values are coerced to strings for `ConfigParser`)
DEFAULT_CONFIG = {
    "common": {
        # Assert
        "assert": True,
        # Environment (local, dev, test, model, prod)
        "env": "local",
    },
    "console": {
        # Severity level (0: FAIL, 1: ERROR, 2: WARN, 3: RESULT, 4: INFO, 5: TEST, 6: DEBUG, 7: TRACE)
        "severityLevel": 5,
        # Verbose
        "verbose": True,
    },
    "date": {
        # Date format
        "dateFormat": "%%Y-%%m-%%d",
        # Time format
        "timeFormat": "%%H:%%M:%%S.%%f",
    },
    "series": {
        # Aggregation (`count`, `identity`, `min`, `max`, `mean`, `median`, `std`, `var`, `sum`)
        "aggregation": Aggregation.IDENTITY,
        # Frequency (`D`, `W`, `M`, `Q`, `S`, `Y`)
        "frequency": Frequency.DAYS,
        # Period (e.g., `"1D"`, `"2W"`, `"3M"`, `"4Q"`, `"5S"`, `"6Y"`)
        "period": "1" + Frequency.YEARS.value,
        # Position (`auto`, `start`, `middle`, `end`)
        "position": Position.AUTO,
    },
}


## CONFIG ACCESSORS ######################################################################

__CONFIG_ACCESSORS__________________________________________ = ""

TEnum = TypeVar("TEnum")


def get_config_path(filename: str, dir: str = DEFAULT_ROOT, subdir: str = DEFAULT_RES_DIR) -> str:
    """Returns the path to the properties with the specified `filename` in the specified directory.

    Args:
        filename: The base file name without extension.
        dir: The root directory to search from.
        subdir: The subdirectory where resources are stored.

    Returns:
        The resolved path to `filename + ".properties"`.
    """
    return find_path(filename + ".properties", dir=dir, subdir=subdir)


##############################


def get_bool(section: str, option: str) -> Optional[bool]:
    """Selects an option as an optional boolean (empty or missing → `None`)."""
    raw = CONFIG.get(section, option, fallback="")
    if raw.strip() == "":
        return None
    return CONFIG.getboolean(section, option)


def get_enum(section: str, option: str, enum_cls: Type[TEnum]) -> Optional[TEnum]:
    """Selects an option as an optional enum instance.

    Accepts either:
      • the enum **name** (case-insensitive): e.g., `"INFO"`, `"info"`
      • the enum **value** coerced to the enum"s value type: e.g., `"4"` → 4 for `IntEnum`

    Args:
        section: The config section name.
        option: The option key inside `section`.
        enum_cls: The enum class to instantiate.

    Returns:
        An instance of `enum_cls` if set; otherwise `None`.

    Raises:
        ValueError: If the value is set but invalid for the specified enum.
    """
    raw = CONFIG.get(section, option, fallback="").strip()
    if raw == "":
        return None

    # 1) Try by name (case-insensitive)
    try:
        return enum_cls[raw.upper()]
    except Exception:
        pass

    # 2) Try by value with type-aware coercion
    coerced = _coerce_to_value_type(raw, enum_cls)
    try:
        return enum_cls(coerced)
    except Exception:
        raise ValueError(f"'{raw}' is not a valid value for '{enum_cls.__name__}'")


def get_int(section: str, option: str) -> Optional[int]:
    """Selects an option as an optional integer (empty or missing → `None`)."""
    raw = CONFIG.get(section, option, fallback="")
    return int(raw) if raw.strip() != "" else None


def get_int_strict(section: str, option: str, *, min_value: Optional[int] = None,
                   max_value: Optional[int] = None) -> int:
    """Selects an option as an integer and validates optional bounds.

    Args:
        section: The config section name.
        option: The option key inside `section`.
        min_value: The minimum accepted value (inclusive), or `None`.
        max_value: The maximum accepted value (inclusive), or `None`.

    Returns:
        A validated integer.

    Raises:
        ValueError: If missing or out of bounds.
    """
    raw = CONFIG.get(section, option, fallback=None)
    if raw is None or raw.strip() == "":
        raise ValueError(f"missing required property '{section}.{option}'")
    value = int(raw)
    if min_value is not None and value < min_value:
        raise ValueError(f"'{section}.{option}' must be ≥ {min_value} (got {value})")
    if max_value is not None and value > max_value:
        raise ValueError(f"'{section}.{option}' must be ≤ {max_value} (got {value})")
    return value


def get_str(section: str, option: str) -> Optional[str]:
    """Selects an option as an optional string (empty or missing → `None`)."""
    val = CONFIG.get(section, option, fallback="")
    return val if val.strip() != "" else None


def _coerce_to_value_type(raw: str, enum_cls: Type[TEnum]) -> Any:
    """Coerces `raw` to the enum's underlying value type.

    Notes:
        • Detects the value type from the first member.
        • Supports `int`, `float`, `bool`, and `str`.
        • Falls back to the original `raw` if type is unknown.

    Args:
        raw: The string read from `ConfigParser`.
        enum_cls: The enum class.

    Returns:
        A best-effort conversion of `raw` to the enum's value type.
    """
    s = raw.strip()
    if s == "":
        return raw

    # Infer the value type from the first member
    try:
        first_member = next(create_iterator(enum_cls))
    except StopIteration:
        return s  # empty enum; nothing sensible to do
    val_type = type(first_member.value)

    if val_type is bool:
        # Accept common truthy/falsey strings
        lowered = s.lower()
        if lowered in {"1", "true", "on", "yes"}:
            return True
        if lowered in {"0", "false", "off", "no"}:
            return False
        return s
    elif val_type is float:
        try:
            return float(s)
        except ValueError:
            return s
    elif val_type is int:
        try:
            return int(s, 10)
        except ValueError:
            return s
    return s


# CONFIG PROCESSORS ####################################################################################################

__CONFIG_PROCESSORS_________________________________________ = ""


def escape_property(prop: Optional[str]) -> Optional[str]:
    """Escapes percent signs in a property value for `ConfigParser` interpolation.

    Args:
        prop: The string to escape, or `None`.

    Returns:
        The escaped string with `"%"` doubled, or `None` if unspecified.
    """
    return prop.replace("%", "%%") if not is_null(prop) else None


def load_config(filename: str, dir: str = DEFAULT_ROOT, subdir: str = DEFAULT_RES_DIR) -> List[str]:
    """Loads the properties with the specified `filename` in the specified directory.

    Args:
        filename: The base file name without extension.
        dir: The root directory to search from.
        subdir: The subdirectory where resources are stored.

    Returns:
        The number of successfully read files.
    """
    return CONFIG.read(get_config_path(filename, dir=dir, subdir=subdir))


##############################

def merge_config(config: ConfigParser, data: Mapping[str, Mapping[str, Any]]) -> None:
    """Reads a nested mapping after coercing all option values to strings.

    Args:
        config: The target `ConfigParser`.
        data: The nested defaults (sections → options).
    """
    sanitized: Dict[str, Dict[str, str]] = {section: {opt: format_value(val) for opt, val in options.items()} for
                                            section, options in data.items()}
    config.read_dict(sanitized)


def format_value(value: Any) -> str:
    """Converts a Python value to a `ConfigParser`-compatible string.

    Args:
        value: The Python value to convert (e.g., `None`, `bool`, `int`, `float`, `list`).

    Returns:
        A string that `ConfigParser` accepts for an option.
    """
    if value is None:
        return ""  # unspecified
    elif isinstance(value, bool):
        return "true" if value else "false"
    elif isinstance(value, (int, float)):
        return str(value)
    elif isinstance(value, (list, tuple)):
        return ",".join(format_value(v) for v in value)
    return str(value)


# CONFIG PROPERTIES ####################################################################################################

__CONFIG_PROPERTIES_________________________________________ = ""

merge_config(CONFIG, DEFAULT_CONFIG)

# Common
ASSERT: Optional[bool] = get_bool("common", "assert")
ENV: Optional[Environment] = get_enum("common", "env", Environment)

# Console (defaults are safe and conservative)
SEVERITY_LEVEL: SeverityLevel = get_enum("console", "severityLevel", SeverityLevel)
VERBOSE: bool = get_bool("console", "verbose") or False

# Date
DATE_FORMAT: Optional[str] = get_str("date", "dateFormat")
TIME_FORMAT: Optional[str] = get_str("date", "timeFormat")
DATE_TIME_FORMAT: Optional[str] = (f"{DATE_FORMAT} {TIME_FORMAT}" if DATE_FORMAT and TIME_FORMAT else None)

# Series
AGGREGATION: Optional[Aggregation] = get_enum("series", "aggregation", Aggregation)
FREQUENCY: Optional[Frequency] = get_enum("series", "frequency", Frequency)
PERIOD: Optional[str] = get_str("series", "period")
POSITION: Optional[Position] = get_enum("series", "position", Position)


## CONFIG VIEW ###########################################################################

def render_config(config: ConfigParser, *, resolve_env: bool = True, redact: bool = True, align: bool = True) -> str:
    """
    Renders a readable, aligned multi-line string of the configuration.

    Args:
        config: The source `ConfigParser`.
        resolve_env: Whether to resolve environment variables via interpolation.
        redact: Whether to redact common secret-like options.
        align: Whether to align the `"="` delimiters by option width per section.

    Returns:
        A pretty-printed string.
    """
    data = config_to_dict(config, resolve_env=resolve_env, redact=redact)
    lines: list[str] = []
    for section in sorted(data.keys()):
        lines.append(f"[{section}]")
        options = data[section]
        if not options:
            lines.append("")  # appends a blank line after an empty section
            continue
        width = max(map(len, options.keys())) if align else 0
        for key in sorted(options.keys()):
            pad = " " * (width - len(key)) if align else ""
            lines.append(f"{key}{pad} = {options[key]}")
        lines.append("")  # blank line between sections
    return "\n".join(lines).rstrip()  # no trailing spaces, no extra trailing newline


def config_to_dict(config: ConfigParser, *, resolve_env: bool = True, redact: bool = True) -> Dict[str, Dict[str, str]]:
    """
    Converts a `ConfigParser` to a nested dict for inspection.

    Args:
        config: The source `ConfigParser`.
        resolve_env: Whether to resolve environment variables via interpolation.
        redact: Whether to redact common secret-like options.

    Returns:
        A nested dict of sections → options → string values.
    """
    secret_markers = ("password", "passwd", "secret", "token", "apikey", "api_key", "access_key", "private_key")
    snapshot: Dict[str, Dict[str, str]] = {}
    for section in config.sections():
        opts: Dict[str, str] = {}
        # `raw=False` triggers interpolation; `raw=True` shows literal stored strings
        for k, v in config.items(section, raw=not resolve_env):
            opts[k] = _redact(k, v, patterns=secret_markers) if redact else v
        snapshot[section] = opts
    return snapshot


def _redact(option: str, value: str, *, patterns: Iterable[str]) -> str:
    """
    Redacts a value when its `option` name matches one of the specified patterns.

    Args:
        option: The option key (e.g., `"password"`, `"token"`).
        value: The resolved string value.
        patterns: The iterable of case-insensitive substrings to match in `option`.

    Returns:
        The redacted value if matched; otherwise the original `value`.
    """
    opt = option.lower()
    for p in patterns:
        if p in opt:
            # Keep the length hint for debugging without leaking the value
            return f"<redacted:{len(value)}>"
    return value


def config_to_ini(config: ConfigParser) -> str:
    """Serializes a `ConfigParser` to an INI string (no comments)."""
    buffer = StringIO()
    config.write(buffer, space_around_delimiters=False)
    return buffer.getvalue().rstrip()


def config_to_json(config: ConfigParser, *, resolve_env: bool = True, redact: bool = True, indent: int = 2) -> str:
    """
    Serializes a `ConfigParser` snapshot to a JSON string.

    Args:
        config: The source `ConfigParser`.
        resolve_env: Whether to resolve environment variables via interpolation.
        redact: Whether to redact common secret-like options.
        indent: The JSON indentation.

    Returns:
        A JSON string.
    """
    return json.dumps(config_to_dict(config, resolve_env=resolve_env, redact=redact), indent=indent, ensure_ascii=False)


# CONFIG MAIN ##########################################################################################################

__CONFIG_MAIN_______________________________________________ = ""

if __name__ == "__main__":
    # Load the defaults only (or call `load_config("app")` first)
    #   load_config("app")

    # 1) Pretty, aligned human view (env resolved, secrets redacted)
    print("# CONFIG (pretty)")
    print(render_config(CONFIG, resolve_env=True, redact=True, align=True))

    # 2) Programmatic JSON snapshot
    print("\n# CONFIG (json)")
    print(config_to_json(CONFIG, resolve_env=True, redact=True, indent=2))

    # 3) Raw INI emission (as `ConfigParser` would write it)
    print("\n# CONFIG (ini)")
    print(config_to_ini(CONFIG))
