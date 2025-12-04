#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide utility enums.
########################################################################################################################

from __future__ import annotations

import enum
from enum import Enum, EnumMeta
from typing import cast, Dict, Iterator, Tuple, Type, TypeVar, Union

## ENUMS #################################################################################

__ENUMS_____________________________________________________ = ""

I = TypeVar("I", bound="IntEnum")


class IntEnumMeta(EnumMeta):
    """
    The metaclass for enums whose members are integers, providing convenience methods for the `name`,
    the `value`, and the validation of the enum members.

    This metaclass adds:
        • Utilities for accessing enum members by name or value.
        • Collections of all names or values for quick access.
        • Validation of name or value membership.
    """

    def __new__(metacls, name, bases, namespace, **kwargs):
        cls = super().__new__(metacls, name, bases, namespace, **kwargs)
        if name == "IntEnum":
            return cls
        if not issubclass(cls, IntEnum):
            raise TypeError(f"'{name}' must inherit from 'IntEnum'")
        for member in cls:
            if not isinstance(member.value, int):
                raise TypeError(f"'{name}.{member.name}' has non-int value '{member.value}'")
        return cls

    ### ACCESSORS ##########################################

    def from_name(cls: Type[I], name: str) -> I:
        """
        Retrieves the enum member with the specified `name`.

        Args:
            name: The name of the enum member.

        Returns:
            The corresponding enum member.

        Raises:
            ValueError: If the `name` is not valid for the enum.
        """
        member = cls.__members__.get(name)
        if member is None:
            raise ValueError(f"'{name}' is not a valid name for '{cls.__name__}'")
        return cast(I, member)

    def from_value(cls: Type[I], value: int) -> I:
        """
        Retrieves the enum member with the specified `value`.

        Args:
            value: The integer value of the enum member.

        Returns:
            The corresponding enum member.

        Raises:
            ValueError: If the `value` is not valid for the enum.
        """
        try:
            return cast(I, cls._value2member_map_[value])  # o(1)
        except KeyError:
            raise ValueError(f"'{value}' is not a valid value for '{cls.__name__}'")

    ##########################

    def names(cls: Type[I]) -> Tuple[str, ...]:
        """
        Lists all the names of the enum members as a `tuple`.

        Returns:
            A `tuple` of the member names.
        """
        return tuple(member.name for member in cls)

    def values(cls: Type[I]) -> Tuple[int, ...]:
        """
        Lists all the values of the enum members as a `tuple`.

        Returns:
            A `tuple` of the member values.
        """
        return tuple(member.value for member in cls)

    def items(cls: Type[I]) -> Iterator[Tuple[str, int]]:
        """
        Iterates over all the `(name, value)` pairs of the enum.

        Yields:
            The pairs of the member names and their values.
        """
        yield from ((member.name, member.value) for member in cls)

    ##########################

    def by_name(cls: Type[I]) -> Dict[str, I]:
        """
        Maps the names of the enum members to the corresponding enum members.

        Returns:
            The dictionary-like mapping from names to members.
        """
        return dict(cls.__members__)

    def by_value(cls: Type[I]) -> Dict[int, I]:
        """
        Maps the values of the enum members to the corresponding enum members.

        Returns:
            The dictionary mapping the member values to the members.
        """
        return {member.value: cast(I, member) for member in cls}

    ### OPERATORS ##########################################

    def __contains__(cls: Type[I], item: Union[int, str, I]) -> bool:
        """
        Returns whether the specified `name` or `value` corresponds to an enum member.

        Args:
            item: The `name`, the `value`, or the enum member to check.
                  Numeric strings are also treated as values.

        Returns:
            `True` if the item corresponds to an enum member, `False` otherwise.
        """
        if isinstance(item, enum.IntEnum):
            if item.__class__ is cls:
                return True
            item = int(item)

        if isinstance(item, int):
            return item in cls._value2member_map_  # o(1)
        elif isinstance(item, str):
            if item in cls.__members__:  # o(1)
                return True
            try:
                value = int(item.strip())
            except ValueError:
                return False
            return value in cls._value2member_map_  # o(1)
        return False

    def __str__(cls: Type[I]) -> str:
        """
        Returns the `name` of the enum class.
        """
        return cls.__name__

    def __repr__(cls: Type[I]) -> str:
        """Returns the canonical representation of the enum class as `(name=value)` pairs."""
        members = ", ".join(f"{member.name}={repr(member.value)}" for member in cls)
        return f"{cls.__name__}({members})"

    ### VALIDATORS #########################################

    def is_valid_name(cls: Type[I], name: str) -> bool:
        """
        Validates whether the specified string is the `name` of an enum member.

        Args:
            name: The string to validate.

        Returns:
            `True` if the string is the `name` of an enum member, `False` otherwise.
        """
        return name in cls.__members__  # o(1)

    def is_valid_value(cls: Type[I], value: int) -> bool:
        """
        Validates whether the specified integer is the `value` of an enum member.

        Args:
            value: The integer to validate.

        Returns:
            `True` if the integer is the `value` of an enum member, `False` otherwise.
        """
        return value in cls._value2member_map_  # o(1)


class IntEnum(enum.IntEnum, metaclass=IntEnumMeta):
    """
    The base class for enums whose members are integers.
    """

    def __str__(self) -> str:
        """Returns the string value of the enum member."""
        return str(self.value)


############################################################

S = TypeVar("S", bound="StrEnum")


class StrEnumMeta(EnumMeta):
    """
    The metaclass for enums whose members are strings, providing convenience methods for the `name`,
    the `value`, and the validation of the enum members.

    This metaclass adds:
        • Utilities for accessing enum members by name or value.
        • Collections of all names or values for quick access.
        • Validation of name or value membership.
    """

    def __new__(metacls, name, bases, namespace, **kwargs):
        cls = super().__new__(metacls, name, bases, namespace, **kwargs)
        if name == "StrEnum":
            return cls
        if not issubclass(cls, StrEnum):
            raise TypeError(f"'{name}' must inherit from 'StrEnum'")
        for member in cls:
            if not isinstance(member.value, str):
                raise TypeError(f"'{name}.{member.name}' has non-str value '{member.value}'")
        return cls

    ### ACCESSORS ##########################################

    def from_name(cls: Type[S], name: str) -> S:
        """
        Retrieves the enum member with the specified `name`.

        Args:
            name: The name of the enum member.

        Returns:
            The corresponding enum member.

        Raises:
            ValueError: If the `name` is not valid for the enum.
        """
        member = cls.__members__.get(name)
        if member is None:
            raise ValueError(f"'{name}' is not a valid name for '{cls.__name__}'")
        return cast(S, member)

    def from_value(cls: Type[S], value: str) -> S:
        """
        Retrieves the enum member with the specified `value`.

        Args:
            value: The value of the enum member.

        Returns:
            The corresponding enum member.

        Raises:
            ValueError: If the `value` is not valid for the enum.
        """
        try:
            return cast(S, cls._value2member_map_[value])  # o(1)
        except KeyError:
            raise ValueError(f"'{value}' is not a valid value for '{cls.__name__}'")

    ##########################

    def names(cls: Type[S]) -> Tuple[str, ...]:
        """
        Lists all the names of the enum members as a `tuple`.

        Returns:
            A `tuple` of the member names.
        """
        return tuple(member.name for member in cls)

    def values(cls: Type[S]) -> Tuple[str, ...]:
        """
        Lists all the values of the enum members as a `tuple`.

        Returns:
            A `tuple` of the member values.
        """
        return tuple(member.value for member in cls)

    def items(cls: Type[S]) -> Iterator[Tuple[str, str]]:
        """
        Iterates over all the `(name, value)` pairs of the enum.

        Yields:
            The pairs of the member names and their values.
        """
        yield from ((member.name, member.value) for member in cls)

    ##########################

    def by_name(cls: Type[S]) -> Dict[str, S]:
        """
        Maps the names of the enum members to the corresponding enum members.

        Returns:
            The dictionary-like mapping from names to members.
        """
        return dict(cls.__members__)

    def by_value(cls: Type[S]) -> Dict[str, S]:
        """
        Maps the values of the enum members to the corresponding enum members.

        Returns:
            The dictionary mapping the member values to the members.
        """
        return {member.value: cast(S, member) for member in cls}

    ### OPERATORS ##########################################

    def __contains__(cls: Type[S], item: Union[str, S]) -> bool:
        """
        Returns whether the specified `name` or `value` corresponds to an enum member.

        Args:
            item: The `name`, the `value`, or the enum member to check.

        Returns:
            `True` if the item corresponds to an enum member, `False` otherwise.
        """
        if isinstance(item, StrEnum):
            if item.__class__ is cls:
                return True
            item = str(item)

        if isinstance(item, str):
            return (item in cls.__members__) or (item in cls._value2member_map_)  # o(1)
        return False

    def __str__(cls: Type[S]) -> str:
        """
        Returns the `name` of the enum class.
        """
        return cls.__name__

    def __repr__(cls: Type[S]) -> str:
        """Returns the canonical representation of the enum class as `(name=value)` pairs."""
        members = ", ".join(f"{member.name}={repr(member.value)}" for member in cls)
        return f"{cls.__name__}({members})"

    ### VALIDATORS #########################################

    def is_valid_name(cls: Type[S], name: str) -> bool:
        """
        Validates whether the specified string is the `name` of an enum member.

        Args:
            name: The string to validate.

        Returns:
            `True` if the string is the `name` of an enum member, `False` otherwise.
        """
        return name in cls.__members__  # o(1)

    def is_valid_value(cls: Type[S], value: str) -> bool:
        """
        Validates whether the specified string is the `value` of an enum member.

        Args:
            value: The string to validate.

        Returns:
            `True` if the string is the `value` of an enum member, `False` otherwise.
        """
        return value in cls._value2member_map_  # o(1)


class StrEnum(str, Enum, metaclass=StrEnumMeta):
    """
    The base class for enums whose members are strings.
    """

    def __str__(self) -> str:
        """Returns the string value of the enum member."""
        return self.value


### DISPLAY ################################################

__DISPLAY_ENUMS_____________________________________________ = ""


class ColorCode(StrEnum):
    """An enumeration of the common color hex codes."""

    BLACK = "#000000"
    BLUE = "#0000FF"
    GREEN = "#00FF00"
    RED = "#FF0000"
    WHITE = "#FFFFFF"
    YELLOW = "#FFFF00"


### FINANCE ################################################

__FINANCE_ENUMS_____________________________________________ = ""


class CurrencyCode(StrEnum):
    """The ISO 4217 currency codes."""

    EUR = "EUR"
    GBP = "GBP"
    JPY = "JPY"
    USD = "USD"


### ENVIRONMENT ############################################

__ENVIRONMENT_ENUMS_________________________________________ = ""


class Environment(StrEnum):
    """The application deployment environments."""

    DEV = "dev"
    LOCAL = "local"
    PROD = "prod"
    STAGING = "staging"
    TEST = "test"


### LOG ####################################################

__LOG_ENUMS_________________________________________________ = ""


class LogLevel(StrEnum):
    CRITICAL = "CRITICAL"
    DEBUG = "DEBUG"
    ERROR = "ERROR"
    INFO = "INFO"
    WARNING = "WARNING"


class SeverityLevel(StrEnum):
    FAIL = 0
    ERROR = 1
    WARN = 2
    RESULT = 3
    INFO = 4
    TEST = 5
    DEBUG = 6
    TRACE = 7


### FILE ###################################################

__FILE_ENUMS________________________________________________ = ""


class Charset(StrEnum):
    """The character encodings."""

    ASCII = "us-ascii"
    LATIN1 = "iso-8859-1"
    UTF8 = "utf-8"


class CompressionFormat(StrEnum):
    """The file compression formats."""

    BZ2 = "bz2"
    GZ = "gz"
    TAR = "tar"
    XZ = "xz"
    ZIP = "zip"


class FileType(StrEnum):
    """
    An enumeration of the common file types.

    File format categories:
        • Documents: `DOC`, `DOCX`, `MD`, `ODT`, `PDF`, `RTF`, `TXT`
        • Spreadsheets: `CSV`, `ODS`, `XLS`, `XLSX`
        • Data Exchange: `JSON`, `TOML`, `XML`, `YAML`, `YML`
        • Presentations: `ODP`, `PPT`, `PPTX`
        • Data Storage: `DB`, `H5`, `PKL`, `SQLITE`
        • Web: `CSS`, `HTML`, `HTM`, `JS`
        • Images: `BMP`, `GIF`, `JPEG`, `JPG`, `PNG`, `SVG`, `TIFF`, `TIF`, `WEBP`
        • Audio: `FLAC`, `M4A`, `MP3`, `OGG`, `WAV`
        • Video: `AVI`, `MKV`, `MOV`, `MP4`, `WMV`
        • Code: `CPP`, `H`, `JAVA`, `PY`, `SQL`, `TS`
        • Config: `CFG`, `CONF`, `ENV`, `INI`
        • Archives: `GZ`, `RAR`, `SEVEN_ZIP`, `TAR`, `ZIP`
        • Logs: `LOG`
        • Other: `BIN`, `DAT`, `TMP`

    Members:
        # Documents
        `DOC`: Microsoft Word Document (.doc) - Legacy Word format
        `DOCX`: Microsoft Word Open XML Document (.docx) - Modern Word format
        `MD`: Markdown Document (.md) - Lightweight markup language
        `ODT`: OpenDocument Text (.odt) - Open-source document format
        `PDF`: Portable Document Format (.pdf) - Fixed-layout document
        `RTF`: Rich Text Format (.rtf) - Formatted text document
        `TXT`: Plain Text (.txt) - Unformatted text file

        # Spreadsheets
        `CSV`: Comma-Separated Values (.csv) - Tabular data in plain text
        `ODS`: OpenDocument Spreadsheet (.ods) - Open-source spreadsheet
        `XLS`: Microsoft Excel Spreadsheet (.xls) - Legacy Excel format
        `XLSX`: Microsoft Excel Open XML (.xlsx) - Modern Excel format

        # Data Exchange
        `JSON`: JavaScript Object Notation (.json) - Lightweight data interchange
        `TOML`: Tom's Obvious Minimal Language (.toml) - Config file format
        `XML`: Extensible Markup Language (.xml) - Structured data format
        `YAML`: YAML Ain't Markup Language (.yaml) - Human-readable data format
        `YML`: Alternative extension for YAML (.yml)

        # Presentations
        `ODP`: OpenDocument Presentation (.odp) - Open-source presentation
        `PPT`: PowerPoint Presentation (.ppt) - Legacy PowerPoint format
        `PPTX`: PowerPoint Open XML (.pptx) - Modern PowerPoint format

        # Data Storage
        `DB`: Generic Database (.db) - Generic database file
        `H5`: Hierarchical Data Format (.h5) - Large dataset storage
        `PKL`: Python Pickle (.pkl) - Python object serialization
        `SQLITE`: SQLite Database (.sqlite) - Self-contained database

        # Web
        `CSS`: Cascading Style Sheets (.css) - Web styling
        `HTML`: HyperText Markup Language (.html) - Web page format
        `HTM`: Alternative extension for HTML (.htm)
        `JS`: JavaScript (.js) - Web scripting

        # Images
        `BMP`: Bitmap Image (.bmp) - Uncompressed image format
        `GIF`: Graphics Interchange Format (.gif) - Animated image format
        `JPEG`: JPEG Image (.jpeg) - Compressed image format
        `JPG`: JPEG Image (.jpg) - Short extension of `JPEG`
        `PNG`: Portable Network Graphics (.png) - Lossless image format
        `SVG`: Scalable Vector Graphics (.svg) - Vector image format
        `TIFF`: Tagged Image File Format (.tiff) - Lossless, multi-page image format
        `TIF`: Tagged Image File Format (.tif) - Short extension of `TIFF`
        `WEBP`: WebP Image (.webp) - Modern web image format

        # Audio
        `FLAC`: Free Lossless Audio Codec (.flac) - Lossless audio
        `M4A`: MPEG-4 Audio (.m4a) - AAC audio format
        `MP3`: MPEG Audio Layer III (.mp3) - Compressed audio
        `OGG`: Ogg Vorbis Audio (.ogg) - Free audio format
        `WAV`: Waveform Audio (.wav) - Uncompressed audio

        # Video
        `AVI`: Audio Video Interleave (.avi) - Microsoft video format
        `MKV`: Matroska Video (.mkv) - Open video container
        `MOV`: QuickTime Movie (.mov) - Apple video format
        `MP4`: MPEG-4 Video (.mp4) - Common video format
        `WMV`: Windows Media Video (.wmv) - Microsoft video format

        # Code
        `CPP`: C++ Source (.cpp) - C++ code file
        `H`: C/C++ Header (.h) - C/C++ header file
        `JAVA`: Java Source (.java) - Java code file
        `PY`: Python Source (.py) - Python code file
        `SQL`: SQL Query (.sql) - Database query file
        `TS`: TypeScript Source (.ts) - TypeScript code file

        # Config
        `CFG`: Configuration (.cfg) - Generic config file
        `CONF`: Configuration (.conf) - Unix config file
        `ENV`: Environment (.env) - Environment variables
        `INI`: Configuration (.ini) - Simple config format

        # Archives
        `GZ`: Gzip Compressed (.gz) - Gzip compression
        `RAR`: RAR Archive (.rar) - Proprietary compression
        `SEVEN_ZIP`: 7-Zip Archive (.7z) - Open source compression
        `TAR`: Tape Archive (.tar) - Unix archive format
        `ZIP`: ZIP Archive (.zip) - Compressed file container

        # Logs
        `LOG`: Log File (.log) - Text-based logging output

        # Other
        `BIN`: Binary File (.bin) - Raw binary data
        `DAT`: Data File (.dat) - Generic data file
        `TMP`: Temporary File (.tmp) - Temporary data
    """

    # Documents
    DOC = "doc"
    DOCX = "docx"
    MD = "md"
    ODT = "odt"
    PDF = "pdf"
    RTF = "rtf"
    TXT = "txt"

    # Spreadsheets
    CSV = "csv"
    ODS = "ods"
    XLS = "xls"
    XLSX = "xlsx"

    # Data Exchange
    JSON = "json"
    TOML = "toml"
    XML = "xml"
    YAML = "yaml"
    YML = "yml"

    # Presentations
    ODP = "odp"
    PPT = "ppt"
    PPTX = "pptx"

    # Data Storage
    DB = "db"
    H5 = "h5"
    PKL = "pkl"
    SQLITE = "sqlite"

    # Web
    CSS = "css"
    HTML = "html"
    HTM = "htm"
    JS = "js"

    # Images
    BMP = "bmp"
    GIF = "gif"
    JPEG = "jpeg"
    JPG = "jpg"
    PNG = "png"
    SVG = "svg"
    TIFF = "tiff"
    TIF = "tif"
    WEBP = "webp"

    # Audio
    FLAC = "flac"
    M4A = "m4a"
    MP3 = "mp3"
    OGG = "ogg"
    WAV = "wav"

    # Video
    AVI = "avi"
    MKV = "mkv"
    MOV = "mov"
    MP4 = "mp4"
    WMV = "wmv"

    # Code
    CPP = "cpp"
    H = "h"
    JAVA = "java"
    PY = "py"
    SQL = "sql"
    TS = "ts"

    # Config
    CFG = "cfg"
    CONF = "conf"
    ENV = "env"
    INI = "ini"

    # Archives
    GZ = "gz"
    RAR = "rar"
    SEVEN_ZIP = "7z"
    TAR = "tar"
    ZIP = "zip"

    # Logs
    LOG = "log"

    # Other
    BIN = "bin"
    DAT = "dat"
    TMP = "tmp"


### GEOGRAPHY ##############################################


class LanguageCode(StrEnum):
    """The ISO 639-1 language codes (https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes)."""

    AF = "af"  # afrikaans
    SQ = "sq"  # albanian
    AR = "ar"  # arabic
    HY = "hy"  # armenian
    BN = "bn"  # bengali
    BS = "bs"  # bosnian
    BG = "bg"  # bulgarian
    CA = "ca"  # catalan
    HR = "hr"  # croatian
    CS = "cs"  # czech
    DA = "da"  # danish
    NL = "nl"  # dutch
    EN = "en"  # english
    EO = "eo"  # esperanto
    ET = "et"  # estonian
    TL = "tl"  # filipino
    FI = "fi"  # finnish
    FR = "fr"  # french
    DE = "de"  # german
    EL = "el"  # greek
    HI = "hi"  # hindi
    HU = "hu"  # hungarian
    IS = "is"  # icelandic
    ID = "id"  # indonesian
    IT = "it"  # italian
    JA = "ja"  # japanese
    KO = "ko"  # korean
    LA = "la"  # latin
    LV = "lv"  # latvian
    LT = "lt"  # lithuanian
    MK = "mk"  # macedonian
    NO = "no"  # norwegian
    PL = "pl"  # polish
    PT = "pt"  # portuguese
    RO = "ro"  # romanian
    RU = "ru"  # russian
    SR = "sr"  # serbian
    SK = "sk"  # slovak
    SL = "sl"  # slovenian
    ES = "es"  # spanish
    SV = "sv"  # swedish
    TR = "tr"  # turkish
    UK = "uk"  # ukrainian
    ZH = "zh"  # chinese


class RegionCode(StrEnum):
    """The ISO 3166-1 country/region codes (https://en.wikipedia.org/wiki/List_of_ISO_3166_country_codes)."""

    AF = "AF"  # afghanistan
    AL = "AL"  # albania
    DZ = "DZ"  # algeria
    AS = "AS"  # american Samoa
    AD = "AD"  # andorra
    AO = "AO"  # angola
    AI = "AI"  # anguilla
    AQ = "AQ"  # antarctica
    AR = "AR"  # argentina
    AM = "AM"  # armenia
    AW = "AW"  # aruba
    AU = "AU"  # australia
    AT = "AT"  # austria
    AZ = "AZ"  # azerbaijan
    BS = "BS"  # bahamas
    BH = "BH"  # bahrain
    BD = "BD"  # bangladesh
    BB = "BB"  # barbados
    BY = "BY"  # belarus
    BE = "BE"  # belgium
    BZ = "BZ"  # belize
    BJ = "BJ"  # benin
    BM = "BM"  # bermuda
    BT = "BT"  # bhutan
    BO = "BO"  # bolivia
    BA = "BA"  # bosnia and Herzegovina
    BW = "BW"  # botswana
    BR = "BR"  # brazil
    IO = "IO"  # british Indian Ocean Territory
    BN = "BN"  # brunei
    BG = "BG"  # bulgaria
    BF = "BF"  # burkina Faso
    BI = "BI"  # burundi
    KH = "KH"  # cambodia
    CM = "CM"  # cameroon
    CA = "CA"  # canada
    CV = "CV"  # cape Verde
    KY = "KY"  # cayman Islands
    CF = "CF"  # central African Republic
    TD = "TD"  # chad
    CL = "CL"  # chile
    CN = "CN"  # china
    CO = "CO"  # colombia
    KM = "KM"  # comoros
    CG = "CG"  # congo
    CD = "CD"  # democratic Republic of the Congo
    CK = "CK"  # cook Islands
    CR = "CR"  # costa Rica
    HR = "HR"  # croatia
    CU = "CU"  # cuba
    CW = "CW"  # curaçao
    CY = "CY"  # cyprus
    CZ = "CZ"  # czech Republic
    DK = "DK"  # denmark
    DJ = "DJ"  # djibouti
    DM = "DM"  # dominica
    DO = "DO"  # dominican Republic
    EC = "EC"  # ecuador
    EG = "EG"  # egypt
    SV = "SV"  # el Salvador
    GQ = "GQ"  # equatorial Guinea
    ER = "ER"  # eritrea
    EE = "EE"  # estonia
    ET = "ET"  # ethiopia
    FK = "FK"  # falkland Islands
    FO = "FO"  # faroe Islands
    FJ = "FJ"  # fiji
    FI = "FI"  # finland
    FR = "FR"  # france
    GF = "GF"  # french Guiana
    PF = "PF"  # french Polynesia
    TF = "TF"  # french Southern and Antarctic Lands
    GA = "GA"  # gabon
    GM = "GM"  # gambia
    GE = "GE"  # georgia
    DE = "DE"  # germany
    GH = "GH"  # ghana
    GI = "GI"  # gibraltar
    GR = "GR"  # greece
    GL = "GL"  # greenland
    GD = "GD"  # grenada
    GP = "GP"  # guadeloupe
    GU = "GU"  # guam
    GT = "GT"  # guatemala
    GG = "GG"  # guernsey
    GN = "GN"  # guinea
    GW = "GW"  # guinea-Bissau
    GY = "GY"  # guyana
    HT = "HT"  # haiti
    HM = "HM"  # heard Island and McDonald Islands
    HN = "HN"  # honduras
    HK = "HK"  # hong Kong
    HU = "HU"  # hungary
    IS = "IS"  # iceland
    IN = "IN"  # india
    ID = "ID"  # indonesia
    IR = "IR"  # iran
    IQ = "IQ"  # iraq
    IE = "IE"  # ireland
    IL = "IL"  # israel
    IT = "IT"  # italy
    JM = "JM"  # jamaica
    JP = "JP"  # japan
    JE = "JE"  # jersey
    JO = "JO"  # jordan
    KZ = "KZ"  # kazakhstan
    KE = "KE"  # kenya
    KI = "KI"  # kiribati
    KP = "KP"  # north Korea
    KR = "KR"  # south Korea
    KW = "KW"  # kuwait
    KG = "KG"  # kyrgyzstan
    LA = "LA"  # laos
    LV = "LV"  # latvia
    LB = "LB"  # lebanon
    LS = "LS"  # lesotho
    LR = "LR"  # liberia
    LY = "LY"  # libya
    LI = "LI"  # liechtenstein
    LT = "LT"  # lithuania
    LU = "LU"  # luxembourg
    MO = "MO"  # macao
    MK = "MK"  # north Macedonia
    MG = "MG"  # madagascar
    MW = "MW"  # malawi
    MY = "MY"  # malaysia
    MV = "MV"  # maldives
    ML = "ML"  # mali
    MT = "MT"  # malta
    MH = "MH"  # marshall Islands
    MQ = "MQ"  # martinique
    MR = "MR"  # mauritania
    MU = "MU"  # mauritius
    YT = "YT"  # mayotte
    MX = "MX"  # mexico
    FM = "FM"  # micronesia
    MD = "MD"  # moldova
    MC = "MC"  # monaco
    MN = "MN"  # mongolia
    ME = "ME"  # montenegro
    MS = "MS"  # montserrat
    MA = "MA"  # morocco
    MZ = "MZ"  # mozambique
    MM = "MM"  # myanmar
    NA = "NA"  # namibia
    NR = "NR"  # nauru
    NP = "NP"  # nepal
    NL = "NL"  # netherlands
    NC = "NC"  # new Caledonia
    NZ = "NZ"  # new Zealand
    NI = "NI"  # nicaragua
    NE = "NE"  # niger
    NG = "NG"  # nigeria
    NU = "NU"  # niue
    NF = "NF"  # norfolk Island
    MP = "MP"  # northern Mariana Islands
    NO = "NO"  # norway
    OM = "OM"  # oman
    PK = "PK"  # pakistan
    PW = "PW"  # palau
    PA = "PA"  # panama
    PG = "PG"  # papua New Guinea
    PY = "PY"  # paraguay
    PE = "PE"  # peru
    PH = "PH"  # philippines
    PN = "PN"  # pitcairn Islands
    PL = "PL"  # poland
    PT = "PT"  # portugal
    PR = "PR"  # puerto Rico
    QA = "QA"  # qatar
    RO = "RO"  # romania
    RU = "RU"  # russia
    RW = "RW"  # rwanda
    RE = "RE"  # réunion
    BL = "BL"  # saint Barthélemy
    SH = "SH"  # saint Helena
    KN = "KN"  # saint Kitts and Nevis
    LC = "LC"  # saint Lucia
    MF = "MF"  # saint Martin
    PM = "PM"  # saint Pierre and Miquelon
    VC = "VC"  # saint Vincent and the Grenadines
    WS = "WS"  # samoa
    SM = "SM"  # san Marino
    ST = "ST"  # são Tomé and Príncipe
    SA = "SA"  # saudi Arabia
    SN = "SN"  # senegal
    RS = "RS"  # serbia
    SC = "SC"  # seychelles
    SL = "SL"  # sierra Leone
    SG = "SG"  # singapore
    SX = "SX"  # sint Maarten
    SK = "SK"  # slovakia
    SI = "SI"  # slovenia
    SB = "SB"  # solomon Islands
    SO = "SO"  # somalia
    ZA = "ZA"  # south Africa
    GS = "GS"  # south Georgia and the South Sandwich Islands
    ES = "ES"  # spain
    LK = "LK"  # sri Lanka
    SD = "SD"  # sudan
    SR = "SR"  # suriname
    SJ = "SJ"  # svalbard and Jan Mayen
    SE = "SE"  # sweden
    CH = "CH"  # switzerland
    SY = "SY"  # syria
    TW = "TW"  # taiwan
    TJ = "TJ"  # tajikistan
    TZ = "TZ"  # tanzania
    TH = "TH"  # thailand
    TL = "TL"  # timor-Leste
    TG = "TG"  # togo
    TK = "TK"  # tokelau
    TO = "TO"  # tonga
    TT = "TT"  # trinidad and Tobago
    TN = "TN"  # tunisia
    TR = "TR"  # turkey
    TM = "TM"  # turkmenistan
    TC = "TC"  # turks and Caicos Islands
    TV = "TV"  # tuvalu
    UG = "UG"  # uganda
    UA = "UA"  # ukraine
    AE = "AE"  # united Arab Emirates
    GB = "GB"  # united Kingdom
    US = "US"  # united States
    UY = "UY"  # uruguay
    UZ = "UZ"  # uzbekistan
    VU = "VU"  # vanuatu
    VE = "VE"  # venezuela
    VN = "VN"  # vietnam
    VG = "VG"  # british Virgin Islands
    VI = "VI"  # u.S. Virgin Islands
    WF = "WF"  # wallis and Futuna
    EH = "EH"  # western Sahara
    YE = "YE"  # yemen
    ZM = "ZM"  # zambia
    ZW = "ZW"  # zimbabwe


### MIME ###################################################


class MimeCategory(StrEnum):
    """The top-level MIME categories."""

    APPLICATION = "application"
    AUDIO = "audio"
    FONT = "font"
    IMAGE = "image"
    MESSAGE = "message"
    MODEL = "model"
    MULTIPART = "multipart"
    TEXT = "text"
    VIDEO = "video"


############################################################


class AudioType(StrEnum):
    """The audio MIME types."""

    AAC = "audio/aac"
    FLAC = "audio/flac"
    MP3 = "audio/mpeg"
    OGG = "audio/ogg"
    OPUS = "audio/opus"
    WAV = "audio/wav"
    WEBM = "audio/webm"
    X_MS_WMA = "audio/x-ms-wma"
    X_PN_REALAUDIO = "audio/x-pn-realaudio"


class ImageType(StrEnum):
    """The image MIME types."""

    AVIF = "image/avif"
    BMP = "image/bmp"
    GIF = "image/gif"
    HEIC = "image/heic"
    JPEG = "image/jpeg"
    PNG = "image/png"
    SVG_XML = "image/svg+xml"
    TIFF = "image/tiff"
    WEBP = "image/webp"


class MediaType(StrEnum):
    """The common media (MIME) types."""

    # Generic / structured
    APPLICATION_JSON = "application/json"
    APPLICATION_PDF = "application/pdf"
    APPLICATION_XML = "application/xml"
    APPLICATION_ZIP = "application/zip"
    APPLICATION_OCTET_STREAM = "application/octet-stream"
    APPLICATION_JAVASCRIPT = "application/javascript"
    APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded"
    MULTIPART_FORM_DATA = "multipart/form-data"

    # Text
    TEXT_CSV = "text/csv"
    TEXT_HTML = "text/html"
    TEXT_PLAIN = "text/plain"
    TEXT_XML = "text/xml"


class VideoType(StrEnum):
    """The common video MIME types."""

    GPP = "video/3gpp"
    MP4 = "video/mp4"
    MPEG = "video/mpeg"
    OGG = "video/ogg"
    QUICKTIME = "video/quicktime"
    WEBM = "video/webm"
    X_FLV = "video/x-flv"
    X_MATROSKA = "video/x-matroska"
    X_MS_WMV = "video/x-ms-wmv"
    X_MSVIDEO = "video/x-msvideo"


### TIME SERIES ############################################

__TIME_SERIES_ENUMS_________________________________________ = ""


class Aggregation(StrEnum):
    """The common aggregation functions for time series."""

    COUNT = "count"
    IDENTITY = "identity"  # skips the aggregation
    MIN = "min"
    MAX = "max"
    MEAN = "mean"
    MEDIAN = "median"
    STD = "std"
    VAR = "var"
    SUM = "sum"


class Frequency(StrEnum):
    """The common time series frequencies."""

    DAYS = "D"
    WEEKS = "W"
    MONTHS = "M"
    QUARTERS = "Q"
    SEMESTERS = "S"
    YEARS = "Y"


class Position(StrEnum):
    """The common positional options."""

    AUTO = "auto"
    START = "start"
    MIDDLE = "middle"
    END = "end"


### WEB ####################################################


class CloudProvider(StrEnum):
    """The major cloud providers."""

    AWS = "aws"
    AZURE = "azure"
    GCP = "gcp"


############################################################


class HttpContentEncoding(StrEnum):
    """
    The common HTTP content coding mechanisms for compression.

    As defined in `RFC 7231` section `3.1.2.2` and in the IANA HTTP Content Coding Registry.
    See: https://tools.ietf.org/html/rfc7231#section-3.1.2.2
    """

    BR = "br"
    COMPRESS = "compress"
    DEFLATE = "deflate"
    GZIP = "gzip"
    IDENTITY = "identity"


class HttpMethod(StrEnum):
    """The HTTP request methods."""

    CONNECT = "CONNECT"
    DELETE = "DELETE"
    GET = "GET"
    HEAD = "HEAD"
    OPTIONS = "OPTIONS"
    PATCH = "PATCH"
    POST = "POST"
    PUT = "PUT"
    TRACE = "TRACE"


class HttpStatusCode(IntEnum):
    """The common HTTP status codes."""

    # 1xx — Informational
    CONTINUE = 100
    SWITCHING_PROTOCOLS = 101
    PROCESSING = 102
    EARLY_HINTS = 103

    # 2xx — Success
    OK = 200
    CREATED = 201
    ACCEPTED = 202
    NO_CONTENT = 204
    PARTIAL_CONTENT = 206

    # 3xx — Redirection
    MULTIPLE_CHOICES = 300
    MOVED_PERMANENTLY = 301
    FOUND = 302
    SEE_OTHER = 303
    NOT_MODIFIED = 304
    TEMPORARY_REDIRECT = 307
    PERMANENT_REDIRECT = 308

    # 4xx — Client Error
    BAD_REQUEST = 400
    UNAUTHORIZED = 401
    PAYMENT_REQUIRED = 402
    FORBIDDEN = 403
    NOT_FOUND = 404
    METHOD_NOT_ALLOWED = 405
    NOT_ACCEPTABLE = 406
    PROXY_AUTHENTICATION_REQUIRED = 407
    REQUEST_TIMEOUT = 408
    CONFLICT = 409
    GONE = 410
    LENGTH_REQUIRED = 411
    PRECONDITION_FAILED = 412
    PAYLOAD_TOO_LARGE = 413
    URI_TOO_LONG = 414
    UNSUPPORTED_MEDIA_TYPE = 415
    RANGE_NOT_SATISFIABLE = 416
    EXPECTATION_FAILED = 417
    I_AM_A_TEAPOT = 418  # fun but widely recognized
    UNPROCESSABLE_ENTITY = 422
    TOO_EARLY = 425
    UPGRADE_REQUIRED = 426
    PRECONDITION_REQUIRED = 428
    TOO_MANY_REQUESTS = 429
    REQUEST_HEADER_FIELDS_TOO_LARGE = 431
    UNAVAILABLE_FOR_LEGAL_REASONS = 451

    # 5xx — Server Error
    INTERNAL_SERVER_ERROR = 500
    NOT_IMPLEMENTED = 501
    BAD_GATEWAY = 502
    SERVICE_UNAVAILABLE = 503
    GATEWAY_TIMEOUT = 504
    HTTP_VERSION_NOT_SUPPORTED = 505
    NETWORK_AUTHENTICATION_REQUIRED = 511
