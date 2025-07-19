####################################################################################################
# ENUMS MODULE
# Provides a comprehensive collection of enumeration classes for consistent data representation and
# validation across the project. Includes base string enum functionality and domain-specific enums.
####################################################################################################

from __future__ import annotations

from enum import EnumMeta
from typing import Dict, Iterator, List, Tuple, Type, TypeVar, Union


####################################################################################################
# COMMON ENUMS
####################################################################################################

__COMMON_ENUMS____________________________________ = ""

T = TypeVar("T", bound="StringEnum")


class StringEnumMeta(EnumMeta):
    """
    A metaclass for string-based Enums offering convenience methods for name, value, and validation.

    This metaclass adds:
        - Utilities for accessing enum members by name or value.
        - Collections of all names or values for quick access.
        - Validation of name or value membership.
    """

    def from_name(cls: Type[T], name: str) -> T:
        """
        Retrieves an enum member based on its name.

        Args:
            name (str): Name of the enum member.

        Returns:
            T: Corresponding enum member.

        Raises:
            ValueError: If the name is invalid.
        """
        try:
            return cls[name]
        except KeyError:
            raise ValueError(f"{name!r} is not a valid name for {cls.__name__}")

    def from_value(cls: Type[T], value: str) -> T:
        """
        Retrieves an enum member based on its value.

        Args:
            value (str): Value of the enum member.

        Returns:
            T: Corresponding enum member.

        Raises:
            ValueError: If the value is invalid.
        """
        for member in cls:
            if member.value == value:
                return member
        raise ValueError(f"{value!r} is not a valid value for {cls.__name__}")

    ##############################################
    # ACCESSORS
    ##############################################

    def names(cls: Type[T]) -> List[str]:
        """
        Lists all member names of the enum.

        Returns:
            List[str]: List of member names.
        """
        return list(cls.__members__.keys())

    def values(cls: Type[T]) -> List[str]:
        """
        Lists all member values of the enum.

        Returns:
            List[str]: List of member values.
        """
        return [str(member.value) for member in cls]

    def items(cls: Type[T]) -> Iterator[Tuple[str, str]]:
        """
        Iterates over all name-value pairs of the enum.

        Yields:
            Tuple[str, str]: Pairs of member names and their values.
        """
        yield from ((member.name, str(member.value)) for member in cls)

    def by_value(cls: Type[T]) -> Dict[str, T]:
        """
        Maps enum member values to their corresponding enum members.

        Returns:
            Dict[str, T]: A dictionary mapping values to members.
        """
        return {str(member.value): member for member in cls}

    ##############################################
    # OPERATORS
    ##############################################

    def __contains__(cls: Type[T], item: Union[str, T]) -> bool:
        """
        Checks if a given name or value is a member of the enum.

        Args:
            item (Union[str, T]): Name or value to check.

        Returns:
            bool: True if valid, False otherwise.
        """
        if isinstance(item, str):
            return item in cls.__members__ or any(str(member.value) == item for member in cls)
        return super().__contains__(item)

    def __str__(cls: Type[T]) -> str:
        return cls.__name__

    def __repr__(cls: Type[T]) -> str:
        members = ", ".join(
            f"{name}={repr(member.value)}" for name, member in cls.__members__.items()
        )
        return f"{cls.__name__}({members})"

    ##############################################
    # VALIDATORS
    ##############################################

    def is_valid_name(cls: Type[T], name: str) -> bool:
        """
        Validates if a string is a valid enum member name.

        Args:
            name (str): Name to validate.

        Returns:
            bool: True if valid, False otherwise.
        """
        return name in cls.__members__

    def is_valid_value(cls: Type[T], value: str) -> bool:
        """
        Validates if a string is a valid enum member value.

        Args:
            value (str): Value to validate.

        Returns:
            bool: True if valid, False otherwise.
        """
        return any(member.value == value for member in cls)


class StringEnum(metaclass=StringEnumMeta):
    """
    Base class for enums with string values. Provides enhanced functionality for common operations.
    """

    def __str__(self) -> str:
        """
        Converts an enum member to its string value.

        Returns:
            str: Enum member value as a string.
        """
        return str(self.value)


# • DISPLAY ########################################################################################

__DISPLAY_ENUMS___________________________________ = ""


class ColorCode(StringEnum):
    """Enumeration for common color hex codes."""

    BLACK = "#000000"
    BLUE = "#0000FF"
    GREEN = "#00FF00"
    RED = "#FF0000"
    WHITE = "#FFFFFF"
    YELLOW = "#FFFF00"


# • FINANCE ########################################################################################

__FINANCE_ENUMS___________________________________ = ""


class CurrencyCode(StringEnum):
    """ISO 4217 currency codes."""

    EUR = "EUR"
    GBP = "GBP"
    JPY = "JPY"
    USD = "USD"


# • ENVIRONMENT ####################################################################################

__ENVIRONMENT_ENUMS_______________________________ = ""


class Environment(StringEnum):
    """Application deployment environments."""

    DEV = "dev"
    LOCAL = "local"
    PROD = "prod"
    STAGING = "staging"
    TEST = "test"


# • FILE ###########################################################################################

__FILE_ENUMS______________________________________ = ""


class Charset(StringEnum):
    """Character encodings."""

    ASCII = "us-ascii"
    LATIN1 = "iso-8859-1"
    UTF8 = "utf-8"


class CompressionFormat(StringEnum):
    """File compression formats."""

    BZ2 = "bz2"
    GZ = "gz"
    TAR = "tar"
    XZ = "xz"
    ZIP = "zip"


class FileType(StringEnum):
    """
    Enumeration of common file types.

    File format categories:
    - Documents: DOC, DOCX, MD, ODT, PDF, RTF, TXT
    - Spreadsheets: CSV, ODS, XLS, XLSX
    - Data Exchange: INI, JSON, TOML, XML, YAML, YML
    - Presentations: ODP, PPT, PPTX
    - Data Storage: DB, H5, PKL, SQLITE
    - Web: CSS, HTML, HTM, JS
    - Images: BMP, GIF, JPEG, JPG, PNG, SVG, WEBP
    - Audio: FLAC, M4A, MP3, OGG, WAV
    - Video: AVI, MKV, MOV, MP4, WMV
    - Code: CPP, H, JAVA, PY, SQL, TS
    - Config: CFG, CONF, ENV, INI
    - Archives: GZ, RAR, SEVEN_ZIP, TAR, ZIP
    - Logs: LOG
    - Other: BIN, DAT, TMP

    Members:
        # Documents
        DOC: Microsoft Word Document (.doc) - Legacy Word format
        DOCX: Microsoft Word Open XML Document (.docx) - Modern Word format
        MD: Markdown Document (.md) - Lightweight markup language
        ODT: OpenDocument Text (.odt) - Open-source document format
        PDF: Portable Document Format (.pdf) - Fixed-layout document
        RTF: Rich Text Format (.rtf) - Formatted text document
        TXT: Plain Text (.txt) - Unformatted text file

        # Spreadsheets
        CSV: Comma-Separated Values (.csv) - Tabular data in plain text
        ODS: OpenDocument Spreadsheet (.ods) - Open-source spreadsheet
        XLS: Microsoft Excel Spreadsheet (.xls) - Legacy Excel format
        XLSX: Microsoft Excel Open XML (.xlsx) - Modern Excel format

        # Data Exchange
        INI: Configuration File (.ini) - Simple config format
        JSON: JavaScript Object Notation (.json) - Lightweight data interchange
        TOML: Tom's Obvious Minimal Language (.toml) - Config file format
        XML: Extensible Markup Language (.xml) - Structured data format
        YAML: YAML Ain't Markup Language (.yaml) - Human-readable data format
        YML: Alternative extension for YAML (.yml)

        # Presentations
        ODP: OpenDocument Presentation (.odp) - Open-source presentation
        PPT: PowerPoint Presentation (.ppt) - Legacy PowerPoint format
        PPTX: PowerPoint Open XML (.pptx) - Modern PowerPoint format

        # Data Storage
        DB: Generic Database (.db) - Generic database file
        H5: Hierarchical Data Format (.h5) - Large dataset storage
        PKL: Python Pickle (.pkl) - Python object serialization
        SQLITE: SQLite Database (.sqlite) - Self-contained database

        # Web
        CSS: Cascading Style Sheets (.css) - Web styling
        HTML: HyperText Markup Language (.html) - Web page format
        HTM: Alternative extension for HTML (.htm)
        JS: JavaScript (.js) - Web scripting

        # Images
        BMP: Bitmap Image (.bmp) - Uncompressed image format
        GIF: Graphics Interchange Format (.gif) - Animated image format
        JPEG: JPEG Image (.jpeg) - Compressed image format
        JPG: JPEG Image (.jpg) - Compressed image format
        PNG: Portable Network Graphics (.png) - Lossless image format
        SVG: Scalable Vector Graphics (.svg) - Vector image format
        WEBP: WebP Image (.webp) - Modern web image format

        # Audio
        FLAC: Free Lossless Audio Codec (.flac) - Lossless audio
        M4A: MPEG-4 Audio (.m4a) - AAC audio format
        MP3: MPEG Audio Layer III (.mp3) - Compressed audio
        OGG: Ogg Vorbis Audio (.ogg) - Free audio format
        WAV: Waveform Audio (.wav) - Uncompressed audio

        # Video
        AVI: Audio Video Interleave (.avi) - Microsoft video format
        MKV: Matroska Video (.mkv) - Open video container
        MOV: QuickTime Movie (.mov) - Apple video format
        MP4: MPEG-4 Video (.mp4) - Common video format
        WMV: Windows Media Video (.wmv) - Microsoft video format

        # Code
        CPP: C++ Source (.cpp) - C++ code file
        H: C/C++ Header (.h) - C/C++ header file
        JAVA: Java Source (.java) - Java code file
        PY: Python Source (.py) - Python code file
        SQL: SQL Query (.sql) - Database query file
        TS: TypeScript Source (.ts) - TypeScript code file

        # Config
        CFG: Configuration (.cfg) - Generic config file
        CONF: Configuration (.conf) - Unix config file
        ENV: Environment (.env) - Environment variables
        INI: Configuration (.ini) - Simple config format

        # Archives
        GZ: Gzip Compressed (.gz) - Gzip compression
        RAR: RAR Archive (.rar) - Proprietary compression
        SEVEN_ZIP: 7-Zip Archive (.7z) - Open source compression
        TAR: Tape Archive (.tar) - Unix archive format
        ZIP: ZIP Archive (.zip) - Compressed file container

        # Logs
        LOG: Log File (.log) - Text-based logging output

        # Other
        BIN: Binary File (.bin) - Raw binary data
        DAT: Data File (.dat) - Generic data file
        TMP: Temporary File (.tmp) - Temporary data
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
    INI = "ini"
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


# GEOGRAPHY ########################################################################################


class LanguageCode(StringEnum):
    """ISO 639-1 language codes (https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes)."""

    AF = "af"  # Afrikaans
    SQ = "sq"  # Albanian
    AR = "ar"  # Arabic
    HY = "hy"  # Armenian
    BN = "bn"  # Bengali
    BS = "bs"  # Bosnian
    BG = "bg"  # Bulgarian
    CA = "ca"  # Catalan
    HR = "hr"  # Croatian
    CS = "cs"  # Czech
    DA = "da"  # Danish
    NL = "nl"  # Dutch
    EN = "en"  # English
    EO = "eo"  # Esperanto
    ET = "et"  # Estonian
    TL = "tl"  # Filipino
    FI = "fi"  # Finnish
    FR = "fr"  # French
    DE = "de"  # German
    EL = "el"  # Greek
    HI = "hi"  # Hindi
    HU = "hu"  # Hungarian
    IS = "is"  # Icelandic
    ID = "id"  # Indonesian
    IT = "it"  # Italian
    JA = "ja"  # Japanese
    KO = "ko"  # Korean
    LA = "la"  # Latin
    LV = "lv"  # Latvian
    LT = "lt"  # Lithuanian
    MK = "mk"  # Macedonian
    NO = "no"  # Norwegian
    PL = "pl"  # Polish
    PT = "pt"  # Portuguese
    RO = "ro"  # Romanian
    RU = "ru"  # Russian
    SR = "sr"  # Serbian
    SK = "sk"  # Slovak
    SL = "sl"  # Slovenian
    ES = "es"  # Spanish
    SV = "sv"  # Swedish
    TR = "tr"  # Turkish
    UK = "uk"  # Ukrainian
    ZH = "zh"  # Chinese


class RegionCode(StringEnum):
    """ISO 3166-1 country/region codes (https://en.wikipedia.org/wiki/List_of_ISO_3166_country_codes)."""

    AF = "AF"  # Afghanistan
    AL = "AL"  # Albania
    DZ = "DZ"  # Algeria
    AS = "AS"  # American Samoa
    AD = "AD"  # Andorra
    AO = "AO"  # Angola
    AI = "AI"  # Anguilla
    AQ = "AQ"  # Antarctica
    AR = "AR"  # Argentina
    AM = "AM"  # Armenia
    AW = "AW"  # Aruba
    AU = "AU"  # Australia
    AT = "AT"  # Austria
    AZ = "AZ"  # Azerbaijan
    BS = "BS"  # Bahamas
    BH = "BH"  # Bahrain
    BD = "BD"  # Bangladesh
    BB = "BB"  # Barbados
    BY = "BY"  # Belarus
    BE = "BE"  # Belgium
    BZ = "BZ"  # Belize
    BJ = "BJ"  # Benin
    BM = "BM"  # Bermuda
    BT = "BT"  # Bhutan
    BO = "BO"  # Bolivia
    BA = "BA"  # Bosnia and Herzegovina
    BW = "BW"  # Botswana
    BR = "BR"  # Brazil
    IO = "IO"  # British Indian Ocean Territory
    BN = "BN"  # Brunei
    BG = "BG"  # Bulgaria
    BF = "BF"  # Burkina Faso
    BI = "BI"  # Burundi
    KH = "KH"  # Cambodia
    CM = "CM"  # Cameroon
    CA = "CA"  # Canada
    CV = "CV"  # Cape Verde
    KY = "KY"  # Cayman Islands
    CF = "CF"  # Central African Republic
    TD = "TD"  # Chad
    CL = "CL"  # Chile
    CN = "CN"  # China
    CO = "CO"  # Colombia
    KM = "KM"  # Comoros
    CG = "CG"  # Congo
    CD = "CD"  # Democratic Republic of the Congo
    CK = "CK"  # Cook Islands
    CR = "CR"  # Costa Rica
    HR = "HR"  # Croatia
    CU = "CU"  # Cuba
    CW = "CW"  # Curaçao
    CY = "CY"  # Cyprus
    CZ = "CZ"  # Czech Republic
    DK = "DK"  # Denmark
    DJ = "DJ"  # Djibouti
    DM = "DM"  # Dominica
    DO = "DO"  # Dominican Republic
    EC = "EC"  # Ecuador
    EG = "EG"  # Egypt
    SV = "SV"  # El Salvador
    GQ = "GQ"  # Equatorial Guinea
    ER = "ER"  # Eritrea
    EE = "EE"  # Estonia
    ET = "ET"  # Ethiopia
    FK = "FK"  # Falkland Islands
    FO = "FO"  # Faroe Islands
    FJ = "FJ"  # Fiji
    FI = "FI"  # Finland
    FR = "FR"  # France
    GF = "GF"  # French Guiana
    PF = "PF"  # French Polynesia
    TF = "TF"  # French Southern and Antarctic Lands
    GA = "GA"  # Gabon
    GM = "GM"  # Gambia
    GE = "GE"  # Georgia
    DE = "DE"  # Germany
    GH = "GH"  # Ghana
    GI = "GI"  # Gibraltar
    GR = "GR"  # Greece
    GL = "GL"  # Greenland
    GD = "GD"  # Grenada
    GP = "GP"  # Guadeloupe
    GU = "GU"  # Guam
    GT = "GT"  # Guatemala
    GG = "GG"  # Guernsey
    GN = "GN"  # Guinea
    GW = "GW"  # Guinea-Bissau
    GY = "GY"  # Guyana
    HT = "HT"  # Haiti
    HM = "HM"  # Heard Island and McDonald Islands
    HN = "HN"  # Honduras
    HK = "HK"  # Hong Kong
    HU = "HU"  # Hungary
    IS = "IS"  # Iceland
    IN = "IN"  # India
    ID = "ID"  # Indonesia
    IR = "IR"  # Iran
    IQ = "IQ"  # Iraq
    IE = "IE"  # Ireland
    IL = "IL"  # Israel
    IT = "IT"  # Italy
    JM = "JM"  # Jamaica
    JP = "JP"  # Japan
    JE = "JE"  # Jersey
    JO = "JO"  # Jordan
    KZ = "KZ"  # Kazakhstan
    KE = "KE"  # Kenya
    KI = "KI"  # Kiribati
    KP = "KP"  # North Korea
    KR = "KR"  # South Korea
    KW = "KW"  # Kuwait
    KG = "KG"  # Kyrgyzstan
    LA = "LA"  # Laos
    LV = "LV"  # Latvia
    LB = "LB"  # Lebanon
    LS = "LS"  # Lesotho
    LR = "LR"  # Liberia
    LY = "LY"  # Libya
    LI = "LI"  # Liechtenstein
    LT = "LT"  # Lithuania
    LU = "LU"  # Luxembourg
    MO = "MO"  # Macao
    MK = "MK"  # North Macedonia
    MG = "MG"  # Madagascar
    MW = "MW"  # Malawi
    MY = "MY"  # Malaysia
    MV = "MV"  # Maldives
    ML = "ML"  # Mali
    MT = "MT"  # Malta
    MH = "MH"  # Marshall Islands
    MQ = "MQ"  # Martinique
    MR = "MR"  # Mauritania
    MU = "MU"  # Mauritius
    YT = "YT"  # Mayotte
    MX = "MX"  # Mexico
    FM = "FM"  # Micronesia
    MD = "MD"  # Moldova
    MC = "MC"  # Monaco
    MN = "MN"  # Mongolia
    ME = "ME"  # Montenegro
    MS = "MS"  # Montserrat
    MA = "MA"  # Morocco
    MZ = "MZ"  # Mozambique
    MM = "MM"  # Myanmar
    NA = "NA"  # Namibia
    NR = "NR"  # Nauru
    NP = "NP"  # Nepal
    NL = "NL"  # Netherlands
    NC = "NC"  # New Caledonia
    NZ = "NZ"  # New Zealand
    NI = "NI"  # Nicaragua
    NE = "NE"  # Niger
    NG = "NG"  # Nigeria
    NU = "NU"  # Niue
    NF = "NF"  # Norfolk Island
    MP = "MP"  # Northern Mariana Islands
    NO = "NO"  # Norway
    OM = "OM"  # Oman
    PK = "PK"  # Pakistan
    PW = "PW"  # Palau
    PA = "PA"  # Panama
    PG = "PG"  # Papua New Guinea
    PY = "PY"  # Paraguay
    PE = "PE"  # Peru
    PH = "PH"  # Philippines
    PN = "PN"  # Pitcairn Islands
    PL = "PL"  # Poland
    PT = "PT"  # Portugal
    PR = "PR"  # Puerto Rico
    QA = "QA"  # Qatar
    RO = "RO"  # Romania
    RU = "RU"  # Russia
    RW = "RW"  # Rwanda
    RE = "RE"  # Réunion
    BL = "BL"  # Saint Barthélemy
    SH = "SH"  # Saint Helena
    KN = "KN"  # Saint Kitts and Nevis
    LC = "LC"  # Saint Lucia
    MF = "MF"  # Saint Martin
    PM = "PM"  # Saint Pierre and Miquelon
    VC = "VC"  # Saint Vincent and the Grenadines
    WS = "WS"  # Samoa
    SM = "SM"  # San Marino
    ST = "ST"  # São Tomé and Príncipe
    SA = "SA"  # Saudi Arabia
    SN = "SN"  # Senegal
    RS = "RS"  # Serbia
    SC = "SC"  # Seychelles
    SL = "SL"  # Sierra Leone
    SG = "SG"  # Singapore
    SX = "SX"  # Sint Maarten
    SK = "SK"  # Slovakia
    SI = "SI"  # Slovenia
    SB = "SB"  # Solomon Islands
    SO = "SO"  # Somalia
    ZA = "ZA"  # South Africa
    GS = "GS"  # South Georgia and the South Sandwich Islands
    ES = "ES"  # Spain
    LK = "LK"  # Sri Lanka
    SD = "SD"  # Sudan
    SR = "SR"  # Suriname
    SJ = "SJ"  # Svalbard and Jan Mayen
    SE = "SE"  # Sweden
    CH = "CH"  # Switzerland
    SY = "SY"  # Syria
    TW = "TW"  # Taiwan
    TJ = "TJ"  # Tajikistan
    TZ = "TZ"  # Tanzania
    TH = "TH"  # Thailand
    TL = "TL"  # Timor-Leste
    TG = "TG"  # Togo
    TK = "TK"  # Tokelau
    TO = "TO"  # Tonga
    TT = "TT"  # Trinidad and Tobago
    TN = "TN"  # Tunisia
    TR = "TR"  # Turkey
    TM = "TM"  # Turkmenistan
    TC = "TC"  # Turks and Caicos Islands
    TV = "TV"  # Tuvalu
    UG = "UG"  # Uganda
    UA = "UA"  # Ukraine
    AE = "AE"  # United Arab Emirates
    GB = "GB"  # United Kingdom
    US = "US"  # United States
    UY = "UY"  # Uruguay
    UZ = "UZ"  # Uzbekistan
    VU = "VU"  # Vanuatu
    VE = "VE"  # Venezuela
    VN = "VN"  # Vietnam
    VG = "VG"  # British Virgin Islands
    VI = "VI"  # U.S. Virgin Islands
    WF = "WF"  # Wallis and Futuna
    EH = "EH"  # Western Sahara
    YE = "YE"  # Yemen
    ZM = "ZM"  # Zambia
    ZW = "ZW"  # Zimbabwe


# • LOGGING ########################################################################################


class LogLevel(StringEnum):
    CRITICAL = "CRITICAL"
    DEBUG = "DEBUG"
    ERROR = "ERROR"
    INFO = "INFO"
    WARNING = "WARNING"


class SeverityLevel(StringEnum):
    FAIL = 0
    ERROR = 1
    WARN = 2
    RESULT = 3
    INFO = 4
    TEST = 5
    DEBUG = 6
    TRACE = 7


# • MIME #############################################################################################


class MimeCategory(StringEnum):
    APPLICATION = "application"
    AUDIO = "audio"
    FONT = "font"
    IMAGE = "image"
    MESSAGE = "message"
    MODEL = "model"
    MULTIPART = "multipart"
    TEXT = "text"
    VIDEO = "video"


##################################################


class AudioType(StringEnum):
    AAC = "audio/aac"
    FLAC = "audio/flac"
    MP3 = "audio/mpeg"
    OGG = "audio/ogg"
    OPUS = "audio/opus"
    WAV = "audio/wav"
    WEBM = "audio/webm"
    X_MS_WMA = "audio/x-ms-wma"
    X_PN_REALAUDIO = "audio/x-pn-realaudio"


class ImageType(StringEnum):
    AVIF = "image/avif"
    BMP = "image/bmp"
    GIF = "image/gif"
    HEIC = "image/heic"
    JPEG = "image/jpeg"
    PNG = "image/png"
    SVG_XML = "image/svg+xml"
    TIFF = "image/tiff"
    WEBP = "image/webp"


class MediaType(StringEnum):
    APPLICATION_JSON = "application/json"
    APPLICATION_PDF = "application/pdf"
    APPLICATION_ZIP = "application/zip"
    TEXT_CSV = "text/csv"
    TEXT_HTML = "text/html"
    TEXT_PLAIN = "text/plain"
    TEXT_XML = "text/xml"


class VideoType(StringEnum):
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


# • TIME SERIES ####################################################################################

__TIME_SERIES_ENUMS_______________________________ = ""


class Aggregation(StringEnum):
    COUNT = "count"
    MIN = "min"
    MAX = "max"
    MEAN = "mean"
    MEDIAN = "median"
    STD = "std"
    VAR = "var"
    SUM = "sum"


class Frequency(StringEnum):
    DAYS = "D"
    WEEKS = "W"
    MONTHS = "M"
    QUARTERS = "Q"
    SEMESTERS = "S"
    YEARS = "Y"


class Position(StringEnum):
    AUTO = "auto"
    START = "start"
    MIDDLE = "middle"
    END = "end"


# • WEB ############################################################################################


class CloudProvider(StringEnum):
    AWS = "aws"
    AZURE = "azure"
    GCP = "gcp"


##################################################


class HttpContentEncoding(StringEnum):
    """
    Standard HTTP content encoding mechanisms for compression.

    As defined in RFC 7231 section 3.1.2.2 and IANA HTTP Content Coding Registry.
    See: https://tools.ietf.org/html/rfc7231#section-3.1.2.2
    """

    BR = "br"
    COMPRESS = "compress"
    DEFLATE = "deflate"
    GZIP = "gzip"
    IDENTITY = "identity"


class HttpMethod(StringEnum):
    """HTTP request methods."""

    DELETE = "DELETE"
    GET = "GET"
    PATCH = "PATCH"
    POST = "POST"
    PUT = "PUT"


class HttpStatusCode(StringEnum):
    """Enumeration of common HTTP status codes."""

    OK = 200
    CREATED = 201
    NO_CONTENT = 204
    BAD_REQUEST = 400
    UNAUTHORIZED = 401
    FORBIDDEN = 403
    NOT_FOUND = 404
    METHOD_NOT_ALLOWED = 405
    CONFLICT = 409
    UNSUPPORTED_MEDIA_TYPE = 415
    TOO_MANY_REQUESTS = 429
    INTERNAL_SERVER_ERROR = 500
    BAD_GATEWAY = 502
    SERVICE_UNAVAILABLE = 503
    GATEWAY_TIMEOUT = 504
