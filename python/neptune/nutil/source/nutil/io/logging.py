import logging

## LOGGING CONFIG ########################################################################


DEFAULT_LOG_LEVEL = logging.INFO
DEFAULT_LOG_FORMAT = "%(asctime)s [%(module)s] [%(levelname)s] %(message)s"
DEFAULT_LOG_DATE_FORMAT = "%H:%M:%S"


def configure_logging(
    level: int = DEFAULT_LOG_LEVEL,
    format: str = DEFAULT_LOG_FORMAT,
    date_format: str = DEFAULT_LOG_DATE_FORMAT,
):
    logging.basicConfig(
        level=level,
        format=format,
        datefmt=date_format,
    )
