#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

########################################################################################################################
# Goal
#   Provide connectivity utilities for databases.
########################################################################################################################

from __future__ import annotations

import logging

import sqlalchemy as db
from sqlalchemy.dialects import mssql

from nutil.scalar.date import DEFAULT_DATE_TIME_FORMAT
from nutil.scalar.number import ceil
from nutil.scalar.string import dquote, par, quote, to_lowercase
from nutil.struct.util import *

__DB_CONSTANTS____________________________________________________________________________ = ""


### DEFAULTS ###############################################

# The default flag specifying whether the DB is Microsoft SQL Server
DEFAULT_IS_MSSQL: bool = True

# The default schema
DEFAULT_SCHEMA: Optional[str] = None

##############################

# The default chunk size
DEFAULT_CHUNK_SIZE: int = 100

##############################

# The default debug interval
DEFAULT_DEBUG_INTERVAL: int = 1000


__DB_TYPES________________________________________________________________________________ = ""


ColumnLike = Union[str, Iterable[str]]
RowLike = Union[Mapping[str, Any], pd.Series]


__DB_ACCESSORS____________________________________________________________________________ = ""


def get_full_table_name(
    table: str,
    *,
    schema: Optional[str] = DEFAULT_SCHEMA,
) -> str:
    """
    Returns the fully-qualified table name for SQL.

    Behavior:
        • Applies `format_name()` to the `schema` and `table`.
        • If the `schema` is null, returns only the formatted `table`.

    Args:
        table: The table name.

        schema: The schema name (defaults to `None`).

    Returns:
        The formatted full table name string (e.g., `"myschema.MyTable"` or `"MyTable"` when `schema=None`).
    """
    return collapse(collapse(format_name(schema), ".") if not is_null(schema) else "", format_name(table))


def get_table_metadata(
    engine: db.Engine,
    table: str,
    *,
    metadata: Optional[db.MetaData] = None,
    schema: Optional[str] = DEFAULT_SCHEMA,
) -> db.Table:
    """
    Returns the SQLAlchemy table metadata for the specified table.

    Behavior:
        • Creates the `metadata` via `create_metadata(schema=schema)` when not provided.
        • Reflects only the requested `table` (and views) from the specified `schema`.

    Args:
        engine: The SQLAlchemy engine bound to the source database.
        table: The table name.

        metadata: Optional SQLAlchemy metadata to reuse.
        schema: The schema name (defaults to `None`).

    Returns:
        The SQLAlchemy `db.Table` instance for the specified table.

    Raises:
        SQLAlchemyError: If reflection fails (connection, permissions, or missing objects).
        KeyError: If the reflected table is not present in the metadata tables mapping.
    """
    if is_null(metadata):
        metadata = create_metadata(schema=schema)
    metadata.reflect(bind=engine, schema=schema, views=True, only=[table], extend_existing=True)
    key = collapse(schema, ".", table) if not is_null(schema) else table
    return metadata.tables[key]


##############################


def get_cols(
    engine: db.Engine,
    table: str,
    *,
    metadata: Optional[db.MetaData] = None,
    schema: Optional[str] = DEFAULT_SCHEMA,
) -> List[str]:
    """
    Returns the column names of the specified table.

    Behavior:
        • Resolves the table metadata via `get_table_metadata()`.
        • Extracts `col.name` for each table column.

    Args:
        engine: The SQLAlchemy engine bound to the database.
        table: The table name.

        metadata: Optional SQLAlchemy metadata to reuse.
        schema: The schema name (defaults to `None`).

    Returns:
        The list of column names in the table.
    """
    table_metadata = get_table_metadata(engine, table, metadata=metadata, schema=schema)
    return [col.name for col in table_metadata.columns]


def get_common_cols(
    df: pd.DataFrame,
    table: str,
    table_cols: ColumnLike,
    *,
    filtering_cols: Optional[ColumnLike] = None,
    # Test
    test: bool = ASSERT,
) -> List[str]:
    """
    Returns the dataframe columns that exist in the table, excluding filtering columns.

    Behavior:
        • Optionally logs warnings for dataframe columns missing in the table (`test=True`).
        • Returns `df` columns filtered by `table_cols` and excluding `filtering_cols`.

    Args:
        df: The source dataframe.
        table: The table name (used only for log messages).
        table_cols: The list of existing columns in the table.

        filtering_cols: Optional list of filtering columns to exclude.

        test: When `True`, enables validation warnings.

    Returns:
        The list of common dataframe column names suitable for insert/update operations.
    """
    if test:
        # Check the existence of the columns in the table
        for col in df:
            if col not in table_cols:
                logging.warning("The column '%s' does not exist in the table '%s'", col, table)
    return filter_list(df, inclusion=table_cols, exclusion=filtering_cols)


def get_filtering_cols(
    engine: db.Engine,
    df: pd.DataFrame,
    table: str,
    *,
    filtering_cols: Optional[ColumnLike] = None,
    metadata: Optional[db.MetaData] = None,
    schema: Optional[str] = DEFAULT_SCHEMA,
    use_only_primary: bool = True,
    # Test
    test: bool = ASSERT,
) -> List[str]:
    """
    Resolves the effective filtering columns for row-matching operations.

    Behavior:
        • If `filtering_cols` is null:
          - Uses `get_primary_cols()` when `use_only_primary=True`.
          - Otherwise uses all table columns via `get_cols()`.
        • Intersects the resolved columns with the dataframe columns via `include_list()`.
        • Optionally warns when filtering columns are missing (`test=True`) or empty.

    Args:
        engine: The SQLAlchemy engine bound to the database.
        df: The dataframe providing candidate columns (and/or rows).
        table: The table name.

        filtering_cols: Optional user-specified filtering columns.
        metadata: Optional SQLAlchemy metadata to reuse.
        schema: The schema name (defaults to `None`).
        use_only_primary: When `True`, prefers the table primary key as the filtering columns.

        test: When `True`, enables validation warnings.

    Returns:
        The list of filtering column names to be used for WHERE clause construction.
    """
    if is_null(filtering_cols):
        if use_only_primary:
            filtering_cols = get_primary_cols(engine, table, metadata=metadata, schema=schema)
            if test:
                # Check the existence of the filtering columns in the dataframe
                for col in filtering_cols:
                    if col not in df:
                        logging.warning("The filtering column '%s' does not exist in the dataframe", col)
        else:
            filtering_cols = get_cols(engine, table, metadata=metadata, schema=schema)
    filtering_cols = include_list(df, filtering_cols)
    if test and is_empty(filtering_cols):
        # Check the existence of any filtering column in the dataframe
        logging.warning("There is no filtering column")
    return filtering_cols


def get_identity_cols(
    engine: db.Engine,
    table: str,
    *,
    is_mssql: bool = DEFAULT_IS_MSSQL,
    # Log
    verbose: bool = VERBOSE,
) -> List[str]:
    """
    Returns the identity (auto-increment) column names for the specified table.

    Behavior:
        • For MSSQL (`is_mssql=True`), queries `"sys"."identity_columns"` for the table.
        • For non-MSSQL, returns an empty list.

    Args:
        engine: The SQLAlchemy engine bound to the database.
        table: The table name.

        is_mssql: Whether the source database is MSSQL.

        verbose: When `True`, enables logging.

    Returns:
        The list of identity column names (empty when not MSSQL).

    Raises:
        SQLAlchemyError: If the identity-columns query fails.
    """
    if is_mssql:
        return select_table_where(
            engine,
            "identity_columns",
            chunk_size=None,
            cols=["name"],
            filtering_cols="OBJECT_NAME(object_id)",
            filtering_row={"OBJECT_NAME(object_id)": table},
            schema="sys",
            # Log
            verbose=verbose,
        )["name"].tolist()
    return []


def get_primary_cols(
    engine: db.Engine,
    table: str,
    *,
    cols: Optional[ColumnLike] = None,
    metadata: Optional[db.MetaData] = None,
    schema: Optional[str] = DEFAULT_SCHEMA,
) -> List[str]:
    """
    Returns the primary key column names for the specified table.

    Behavior:
        • Reflects the table via `get_table_metadata()`.
        • Extracts the primary key columns and optionally filters them via `include_list()`.

    Args:
        engine: The SQLAlchemy engine bound to the database.
        table: The table name.

        cols: Optional subset of columns to keep (filters the primary key list).
        metadata: Optional SQLAlchemy metadata to reuse.
        schema: The schema name (defaults to `None`).

    Returns:
        The list of primary key column names (optionally filtered by `cols`).
    """
    table_metadata = get_table_metadata(engine, table, metadata=metadata, schema=schema)
    primary_cols = [col.name for col in table_metadata.primary_key.columns]
    return include_list(primary_cols, cols)


##############################


def get_col_types(
    df: pd.DataFrame,
    # DateTime
    timezone: bool = False,
    to_date: bool = False,
    # Float
    decimal_scale: Optional[int] = None,
    float_precision: Optional[int] = None,
    to_decimal: bool = True,
    # String
    string_length: int = 8000,
    to_text: bool = False,
) -> Dict[str, db.TypeEngine]:
    """
    Infers SQLAlchemy column types from the dataframe index and columns.

    Behavior:
        • Inspects element types for the index and the dataframe columns.
        • Maps:
          - bool -> `db.Boolean()`
          - datetime -> `db.DateTime()` or `db.Date()` when `to_date=True`
          - float -> `db.Float(…)`
          - int -> `db.Integer()`
          - otherwise -> `db.String(length=…)` or `db.Text()` when `to_text=True`

    Args:
        df: The source dataframe.

        timezone: When `True`, sets `timezone=True` for date/time types.
        to_date: When `True`, uses `db.Date()` instead of `db.DateTime()`.

        decimal_scale: The `decimal_return_scale` for `db.Float(asdecimal=True)`.
        float_precision: The `precision` for `db.Float(…)`.
        to_decimal: When `True`, sets `asdecimal=True` for float columns.

        string_length: The length for `db.String(…)`.
        to_text: When `True`, uses `db.Text()` instead of `db.String(…)`.

    Returns:
        A mapping `{column_name: sqlalchemy_type}` suitable for `DataFrame.to_sql(dtype=…)`.
    """
    col_types: Dict[str, db.TypeEngine] = {}
    for col, col_type in concat_rows(get_element_types(df.index), get_element_types(df)).items():
        col_type_name = str(col_type)
        if "bool" in col_type_name:
            col_types.update({col: db.Boolean()})
        elif "datetime" in col_type_name:
            col_types.update({col: (db.Date() if to_date else db.DateTime(timezone=timezone))})
        elif "float" in col_type_name:
            col_types.update(
                {
                    col: db.Float(
                        asdecimal=to_decimal,
                        decimal_return_scale=decimal_scale,
                        precision=float_precision,
                    )
                }
            )
        elif "int" in col_type_name:
            col_types.update({col: db.Integer()})
        else:
            col_types.update({col: db.Text() if to_text else db.String(length=string_length)})
    return col_types


__DB_BUILDERS_____________________________________________________________________________ = ""


def build_where_clause(
    *,
    filtering_cols: Optional[ColumnLike] = None,
    filtering_row: Optional[RowLike] = None,
    is_mssql: bool = DEFAULT_IS_MSSQL,
) -> str:
    """
    Builds a SQL WHERE clause from a row-like mapping.

    Behavior:
        • Computes the candidate columns as `include_list(get_keys(filtering_row), filtering_cols)`.
        • Generates predicates per column:
          - `IS NULL` for null values
          - `IN (…)` for structured values (collections)
          - `=` for scalar values
        • Uses `format_name()` for identifiers and `format()` for values.

    Args:
        filtering_cols: Optional list of columns to include.
        filtering_row: A mapping (or row-like object) from column names to values.
        is_mssql: Whether to format values for MSSQL semantics (e.g., booleans).

    Returns:
        The WHERE clause string (without a trailing semicolon), or the empty string when no columns are selected.
    """
    cols = include_list(get_keys(filtering_row), filtering_cols)
    if is_empty(cols):
        return ""
    return paste(
        "WHERE",
        collapse(
            [
                collapse(
                    format_name(col),
                    " IS " if is_null(filtering_row[col]) else " IN " if is_struct(filtering_row[col]) else "=",
                    format(filtering_row[col], is_mssql=is_mssql),
                )
                for col in cols
            ],
            delimiter=" AND ",
        ),
    )


__DB_SELECT_________________________________________________ = ""


def build_select_table_where_query(
    table: str,
    *,
    cols: Optional[ColumnLike] = None,
    filtering_cols: Optional[ColumnLike] = None,
    filtering_row: Optional[RowLike] = None,
    is_mssql: bool = DEFAULT_IS_MSSQL,
    n: Optional[int] = None,
    order_cols: Optional[ColumnLike] = None,
    order_directions: Optional[Iterable[str]] = None,
    schema: Optional[str] = DEFAULT_SCHEMA,
) -> str:
    """
    Builds a SQL SELECT query for a table with an optional WHERE clause and ordering.

    Behavior:
        • Emits `"TOP n"` for MSSQL when `n` is set.
        • Emits `"LIMIT n"` for non-MSSQL when `n` is set.
        • Uses `format_cols()` and `get_full_table_name()` for identifiers.
        • Appends a trailing semicolon.

    Args:
        table: The table name.

        cols: Optional selected columns (selects `*` when empty).
        filtering_cols: Optional filtering columns for the WHERE clause.
        filtering_row: Optional row-like mapping used by `build_where_clause()`.
        is_mssql: Whether the target dialect is MSSQL.
        n: Optional row limit.
        order_cols: Optional ORDER BY columns.
        order_directions: Optional suffixes aligned with `order_cols` (e.g., `"ASC"`, `"DESC"`).
        schema: The schema name (defaults to `None`).

    Returns:
        The SQL query string ending with `";"`.
    """
    return (
        paste(
            "SELECT",
            paste("TOP", n) if not is_null(n) and is_mssql else "",
            "*" if is_empty(cols) else format_cols(cols),
            "FROM",
            get_full_table_name(table, schema=schema),
            build_where_clause(filtering_cols=filtering_cols, filtering_row=filtering_row, is_mssql=is_mssql),
            (paste("ORDER BY", format_cols(order_cols, suffixes=order_directions)) if not is_empty(order_cols) else ""),
            paste("LIMIT", n) if not is_null(n) and not is_mssql else "",
        )
        + ";"
    )


__DB_DELETE_________________________________________________ = ""


def build_delete_table_query(
    table: str,
    *,
    filtering_cols: Optional[ColumnLike] = None,
    filtering_row: Optional[RowLike] = None,
    is_mssql: bool = DEFAULT_IS_MSSQL,
    schema: Optional[str] = DEFAULT_SCHEMA,
) -> str:
    """
    Builds a SQL DELETE query for rows matching a WHERE clause.

    Behavior:
        • Uses `get_full_table_name()` for the target table.
        • Uses `build_where_clause()` to build the WHERE part.
        • Appends a trailing semicolon.

    Args:
        table: The table name.

        filtering_cols: Optional filtering columns for the WHERE clause.
        filtering_row: Optional row-like mapping used by `build_where_clause()`.
        is_mssql: Whether the target dialect is MSSQL.
        schema: The schema name (defaults to `None`).

    Returns:
        The SQL query string ending with `";"`.
    """
    return (
        paste(
            "DELETE FROM",
            get_full_table_name(table, schema=schema),
            build_where_clause(filtering_cols=filtering_cols, filtering_row=filtering_row, is_mssql=is_mssql),
        )
        + ";"
    )


__DB_INSERT_________________________________________________ = ""


def build_insert_table_query(
    table: str,
    cols: ColumnLike,
    row: RowLike,
    *,
    is_mssql: bool = DEFAULT_IS_MSSQL,
    schema: Optional[str] = DEFAULT_SCHEMA,
) -> str:
    """
    Builds a SQL INSERT query for a single row.

    Behavior:
        • Formats identifiers via `format_cols()` and `get_full_table_name()`.
        • Formats values via `format(…)` with dialect-aware behavior.
        • Appends a trailing semicolon.

    Args:
        table: The table name.
        cols: The list of column names to insert.
        row: A row-like mapping providing values for `cols`.

        is_mssql: Whether the target dialect is MSSQL.
        schema: The schema name (defaults to `None`).

    Returns:
        The SQL query string ending with `";"`.

    Raises:
        KeyError: If the `row` does not provide a required value for a column in `cols`.
    """
    return (
        paste(
            "INSERT INTO",
            get_full_table_name(table, schema=schema),
            par(format_cols(cols)),
            "VALUES",
            par(collist([format(row[col], is_mssql=is_mssql) for col in cols])),
        )
        + ";"
    )


__DB_UPDATE_________________________________________________ = ""


def build_update_table_query(
    table: str,
    cols: ColumnLike,
    row: RowLike,
    *,
    filtering_cols: Optional[ColumnLike] = None,
    is_mssql: bool = DEFAULT_IS_MSSQL,
    schema: Optional[str] = DEFAULT_SCHEMA,
) -> str:
    """
    Builds a SQL UPDATE query for a single row, matching by a WHERE clause.

    Behavior:
        • Sets each column in `cols` from the corresponding value in `row`.
        • Builds the WHERE clause from `row` restricted by `filtering_cols`.
        • Appends a trailing semicolon.

    Args:
        table: The table name.
        cols: The list of column names to update (SET clause).
        row: A row-like mapping providing values for both SET and WHERE construction.

        filtering_cols: Optional filtering columns used by `build_where_clause()`.
        is_mssql: Whether the target dialect is MSSQL.
        schema: The schema name (defaults to `None`).

    Returns:
        The SQL query string ending with `";"`.

    Raises:
        KeyError: If the `row` does not provide a required value for a column in `cols`.
    """
    return (
        paste(
            "UPDATE",
            get_full_table_name(table, schema=schema),
            "SET",
            collist([collapse(format_name(col), "=", format(row[col], is_mssql=is_mssql)) for col in cols]),
            build_where_clause(filtering_cols=filtering_cols, filtering_row=row, is_mssql=is_mssql),
        )
        + ";"
    )


__DB_CONVERTERS___________________________________________________________________________ = ""


__DB_MIGRATE________________________________________________ = ""


def metadata_to_lowercase(metadata: db.MetaData) -> None:
    """
    Lowercases table and column identifiers in a SQLAlchemy metadata object.

    Behavior:
        • Iterates over all tables in the `metadata` and applies `table_to_lowercase()`.

    Args:
        metadata: The SQLAlchemy `MetaData` instance to mutate in-place.
    """
    for _, table in metadata.tables.items():
        table_to_lowercase(table)


def table_to_lowercase(table: db.Table) -> None:
    """
    Lowercases identifiers of a SQLAlchemy `db.Table` object in-place.

    Behavior:
        • Lowercases:
          - the table `name` (and `schema` when present)
          - the primary key constraint name (when present)
          - each column `name` and `key`
          - foreign key and FK constraint names (when present)
          - other table-level constraints and index names (when present)
        • Does NOT assign to `table.fullname` (derived/read-only).

    Args:
        table: The SQLAlchemy `db.Table` instance to mutate in-place.
    """
    table.name = to_lowercase(table.name)
    if hasattr(table, "schema"):
        table.schema = to_lowercase(table.schema)

    if not is_null(table.primary_key):
        table.primary_key.name = to_lowercase(table.primary_key.name)

    for constraint in getattr(table, "constraints", []):
        if hasattr(constraint, "name"):
            constraint.name = to_lowercase(constraint.name)

    for index in getattr(table, "indexes", []):
        if hasattr(index, "name"):
            index.name = to_lowercase(index.name)

    for _, column in table.columns.items():
        column.name = to_lowercase(column.name)
        column.key = to_lowercase(column.key)
        for fk in column.foreign_keys:
            fk.name = to_lowercase(fk.name)
            if hasattr(fk, "constraint") and not is_null(fk.constraint):
                fk.constraint.name = to_lowercase(fk.constraint.name)


##############################


def update_col(
    col: db.Column,
    *,
    collation: Optional[str] = None,
    is_mssql_from: bool = DEFAULT_IS_MSSQL,
    is_mssql_to: bool = DEFAULT_IS_MSSQL,
) -> None:
    """
    Updates the column defaults, types, and collation for cross-database migrations.

    Behavior:
        • Updates:
          - the default expression via `update_col_default()`
          - the type via `update_col_type()`
          - the collation via `update_col_collation()`

    Args:
        col: The SQLAlchemy column object to mutate in-place.

        collation: Optional collation name to apply when supported by the type.
        is_mssql_from: Whether the source database is MSSQL.
        is_mssql_to: Whether the target database is MSSQL.
    """
    # - Update the default value
    update_col_default(col, is_mssql_from=is_mssql_from, is_mssql_to=is_mssql_to)
    # - Update the type
    update_col_type(col, is_mssql_from=is_mssql_from, is_mssql_to=is_mssql_to)
    # - Update the collation
    update_col_collation(col, collation=collation)


def update_col_default(
    col: db.Column,
    *,
    is_mssql_from: bool = DEFAULT_IS_MSSQL,
    is_mssql_to: bool = DEFAULT_IS_MSSQL,
) -> None:
    """
    Rewrites certain server default expressions when migrating between MSSQL and non-MSSQL.

    Behavior:
        • Operates only when `col.server_default.arg` is a `TextClause`.
        • MSSQL -> non-MSSQL:
          - BIT defaults: `"0"`/`"1"` -> `"FALSE"`/`"TRUE"`
          - date/time defaults: `"getdate"` -> `"now"`
        • non-MSSQL -> MSSQL:
          - BOOLEAN defaults: `"FALSE"`/`"TRUE"` -> `"0"`/`"1"`
          - date/time defaults: `"now"` -> `"getdate"`

    Args:
        col: The SQLAlchemy column object to mutate in-place.

        is_mssql_from: Whether the source database is MSSQL.
        is_mssql_to: Whether the target database is MSSQL.
    """
    if hasattr(col.server_default, "arg") and isinstance(col.server_default.arg, db.TextClause):
        if is_mssql_from and not is_mssql_to:
            if isinstance(col.type, mssql.base.BIT):
                col.server_default.arg.text = col.server_default.arg.text.replace("0", "FALSE").replace("1", "TRUE")
            elif (
                isinstance(col.type, mssql.base.DATE)
                or isinstance(col.type, mssql.base.DATETIME)
                or isinstance(col.type, mssql.base.DATETIMEOFFSET)
                or isinstance(col.type, mssql.base.SMALLDATETIME)
                or isinstance(col.type, mssql.base.TIME)
                or isinstance(col.type, mssql.base.TIMESTAMP)
            ):
                col.server_default.arg.text = col.server_default.arg.text.replace("getdate", "now")
        elif not is_mssql_from and is_mssql_to:
            if isinstance(col.type, db.BOOLEAN):
                col.server_default.arg.text = col.server_default.arg.text.replace("FALSE", "0").replace("TRUE", "1")
            elif (
                isinstance(col.type, db.DATE) or isinstance(col.type, db.DATETIME) or isinstance(col.type, db.TIMESTAMP)
            ):
                col.server_default.arg.text = col.server_default.arg.text.replace("now", "getdate")


def update_col_type(
    col: db.Column,
    *,
    is_mssql_from: bool = DEFAULT_IS_MSSQL,
    is_mssql_to: bool = DEFAULT_IS_MSSQL,
) -> None:
    """
    Converts certain column types when migrating between MSSQL and non-MSSQL.

    Behavior:
        • MSSQL -> non-MSSQL:
          - BIT -> `db.BOOLEAN()`
          - DATETIME/SMALLDATETIME/TIMESTAMP -> `db.TIMESTAMP()`
        • non-MSSQL -> MSSQL:
          - BOOLEAN -> `mssql.base.BIT()`
          - TIMESTAMP -> `mssql.base.DATETIME()`

    Args:
        col: The SQLAlchemy column object to mutate in-place.

        is_mssql_from: Whether the source database is MSSQL.
        is_mssql_to: Whether the target database is MSSQL.
    """
    if is_mssql_from and not is_mssql_to:
        if isinstance(col.type, mssql.base.BIT):
            col.type = db.BOOLEAN()
        elif (
            isinstance(col.type, mssql.base.DATETIME)
            or isinstance(col.type, mssql.base.SMALLDATETIME)
            or isinstance(col.type, mssql.base.TIMESTAMP)
        ):
            col.type = db.TIMESTAMP()
    elif not is_mssql_from and is_mssql_to:
        if isinstance(col.type, db.BOOLEAN):
            col.type = mssql.base.BIT()
        elif isinstance(col.type, db.TIMESTAMP):
            col.type = mssql.base.DATETIME()


def update_col_collation(
    col: db.Column,
    *,
    collation: Optional[str] = None,
) -> None:
    """
    Applies the specified collation to a column type when supported.

    Behavior:
        • Sets `col.type.collation = collation` only when:
          - `collation` is not null, and
          - the column type exposes a `collation` attribute.

    Args:
        col: The SQLAlchemy column object to mutate in-place.

        collation: The collation name to apply.
    """
    if not is_null(collation) and hasattr(col.type, "collation"):
        col.type.collation = collation


__DB_FACTORIES____________________________________________________________________________ = ""


__DB_CONNECT________________________________________________ = ""


def create_engine(
    *,
    dialect: str = "mssql",
    driver: str = "pyodbc",
    username: Optional[str] = None,
    password: Optional[str] = None,
    host: str = "localhost",
    port: int = 1433,
    database: Optional[str] = None,
    query: Optional[Mapping[str, str]] = None,
) -> db.Engine:
    """
    Creates a SQLAlchemy engine for the specified connection parameters.

    Behavior:
        • Builds a SQLAlchemy `URL` via `URL.create(dialect + "+" + driver, …)`.
        • Creates the engine via `db.create_engine(…)`.

    Args:
        dialect: The database dialect (defaults to `"mssql"`).
        driver: The dialect driver (defaults to `"pyodbc"`).
        username: The username.
        password: The password.
        host: The host (defaults to `"localhost"`).
        port: The port (defaults to `1433`).
        database: The database name.
        query: Optional URL query parameters mapping.

    Returns:
        The SQLAlchemy engine instance.

    Raises:
        SQLAlchemyError: If the engine cannot be created.
    """
    return db.create_engine(
        db.URL.create(
            dialect + "+" + driver,
            username=username,
            password=password,
            host=host,
            port=port,
            database=database,
            query=query,
        )
    )


def create_session(engine: db.Engine) -> db.Session:
    """
    Creates a SQLAlchemy ORM session bound to the specified engine.

    Args:
        engine: The SQLAlchemy engine.

    Returns:
        A SQLAlchemy `Session` bound to the engine.
    """
    return db.Session(bind=engine)


__DB_METADATA_______________________________________________ = ""


def create_metadata(
    *,
    schema: Optional[str] = DEFAULT_SCHEMA,
) -> db.MetaData:
    """
    Creates SQLAlchemy metadata bound to the specified engine and schema.

    Behavior:
        • Returns `db.MetaData(schema=schema)`.

    Args:
        schema: The schema name (defaults to `None`).

    Returns:
        The SQLAlchemy `MetaData` instance.
    """
    return db.MetaData(schema=schema)


__DB_FORMATTERS___________________________________________________________________________ = ""


def escape(name: Any) -> str:
    """
    Escapes a string for safe embedding into SQL string literals.

    Behavior:
        • Replaces single quotes with doubled single quotes.
        • Escapes percent signs (useful for MSSQL LIKE / format strings).

    Args:
        name: The value to escape.

    Returns:
        The escaped string representation of `name`.
    """
    return str(name).replace("'", "''").replace("%", "%%")


##############################


def format_name(name: str) -> str:
    """
    Formats a SQL identifier (column/table/schema name).

    Behavior:
        • If the name contains parentheses, treats it as a raw SQL expression and returns it unchanged.
        • Otherwise wraps the identifier via `dquote(…)`.

    Args:
        name: The identifier (or expression) to format.

    Returns:
        The formatted identifier string.
    """
    if "(" in name and ")" in name:
        return name
    return dquote(name)


def format_cols(
    *cols: ColumnLike,
    suffixes: Optional[Iterable[str]] = None,
) -> str:
    """
    Formats a list of SQL identifiers (e.g., column names), optionally adding suffixes.

    Behavior:
        • Removes empty values via `remove_empty(to_list(*cols))`.
        • Applies `format_name()` to each column.
        • When `suffixes` is provided, appends each suffix to the corresponding column.
        • Collapses the result into a comma-separated list via `collist(…)`.

    Args:
        *cols: The column names (scalars and/or collections).
        suffixes: Optional suffix list aligned with the columns (e.g., `"ASC"`, `"DESC"`).

    Returns:
        A comma-separated identifier list string (e.g., `"a","b" DESC`).
    """
    cols = [format_name(col) for col in remove_empty(to_list(*cols))]
    if not is_null(suffixes):
        cols = [paste(col, suffix) for col, suffix in zip(cols, suffixes)]
    return collist(cols)


def format(
    value: Any,
    *,
    is_mssql: bool = DEFAULT_IS_MSSQL,
) -> Any:
    """
    Formats a Python value as a SQL literal.

    Behavior:
        • Null -> `NULL`
        • Structured values (collections) -> parenthesized list of formatted elements
        • Booleans:
          - MSSQL -> `1` / `0`
          - otherwise -> the boolean value as-is
        • Numbers:
          - NaN -> `NULL`
          - otherwise -> the number
        • Timestamps -> quoted string using `DEFAULT_DATE_TIME_FORMAT` (millisecond precision)
        • Otherwise -> quoted and escaped string via `escape(…)`

    Args:
        value: The value to format.

        is_mssql: Whether to format for MSSQL semantics.

    Returns:
        The SQL literal representation for the value (string-like for most inputs).
    """
    if is_null(value):
        return "NULL"
    elif is_struct(value):
        return par(collist(apply(value, format, is_mssql=is_mssql)))
    elif is_boolean(value):
        if is_mssql:
            return 1 if value else 0
        return "TRUE" if value else "FALSE"
    elif is_number(value):
        if is_nan(value):
            return "NULL"
        return value
    elif is_timestamp(value):
        return quote(value.strftime(DEFAULT_DATE_TIME_FORMAT)[:-3])
    return quote(escape(value))


__DB_LOGGERS______________________________________________________________________________ = ""


def get_query_message(verb: str, count: int, table: str) -> str:
    """
    Builds a human-readable message describing a table-level operation.

    Args:
        verb: The operation verb (e.g., `"select"`, `"insert"`).
        count: The number of affected rows.
        table: The table name.

    Returns:
        A message string such as `select 100 rows in the table "MyTable"` (without capitalization).
    """
    return paste(verb, count, "rows", "in the table", quote(table))


##############################


def debug_query(
    verb: str,
    count: int,
    table: str,
    *,
    index_from: Optional[int] = None,
    index_to: Optional[int] = None,
    # Log
    verbose: bool = VERBOSE,
) -> None:
    """
    Emits a debug log for a table-level operation, optionally including a processed range.

    Behavior:
        • When `index_from` / `index_to` are provided, prefixes with `processing rows from … to …`.
        • Uses `get_query_message(…)` and capitalizes the final message.

    Args:
        verb: The operation verb.
        count: The number of rows (or chunk size) for the message.
        table: The table name.

        index_from: The inclusive start row index (1-based in the message).
        index_to: The inclusive end row index (1-based in the message).

        verbose: When `True`, enables logging.
    """
    if verbose:
        prefix = ""
        if not is_null(index_from):
            prefix += " from " + str(index_from)
        if not is_null(index_to):
            prefix += " to " + str(index_to)
        if not is_empty(prefix):
            prefix = "processing rows" + prefix + ", "
        logging.debug((prefix + get_query_message(verb, count, table)).capitalize())


def warn_query(
    verb: str,
    table: str,
    *,
    exception: Optional[BaseException] = None,
    # Log
    verbose: bool = VERBOSE,
) -> None:
    """
    Emits a warning log for a table-level operation that affected no rows.

    Behavior:
        • Logs the class name of the exception (when provided).
        • Logs the exception details via `logging.debug(…)` when present.

    Args:
        verb: The verb phrase (e.g., `"deleted"`, `"bulk-inserted"`).
        table: The table name.

        exception: Optional exception instance.

        verbose: When `True`, enables logging.
    """
    if verbose:
        logging.warning(
            "No row has been %s in the table '%s'%s",
            verb,
            table,
            " " + par(get_full_class_name(exception)) if not is_null(exception) else "",
        )
        if not is_null(exception):
            logging.debug("%s", exception)


def error_query(
    verb: str,
    table: str,
    *,
    exception: Optional[BaseException] = None,
    # Log
    verbose: bool = VERBOSE,
) -> None:
    """
    Emits an error log for a table-level operation failure.

    Behavior:
        • Logs errors as `logging.error(…)` unless the exception is an `IntegrityError`.
        • For `IntegrityError`, downgrades to `warn_query(…)`.

    Args:
        verb: The verb phrase (e.g., `"deleted"`, `"updated"`).
        table: The table name.

        exception: Optional exception instance.

        verbose: When `True`, enables logging.
    """
    if isinstance(exception, db.IntegrityError):
        warn_query(verb, table, exception=exception, verbose=verbose)
    else:
        logging.error(
            "No row has been %s in the table '%s'%s",
            verb,
            table,
            " " + par(exception) if not is_null(exception) else "",
        )


############################################################


def get_row_message(
    verb: str,
    index: int,
    table: str,
    *,
    cols: Optional[ColumnLike] = None,
    row: Optional[RowLike] = None,
) -> str:
    """
    Builds a human-readable message describing a row-level operation.

    Args:
        verb: The operation verb (e.g., `"insert"`, `"update"`).
        index: The zero-based row index.
        table: The table name.

        cols: Optional inclusion list of columns to print from the row.
        row: Optional row-like mapping (used for message enrichment).

    Returns:
        A message string describing the row operation (without the leading dash/prefix).
    """
    return paste(
        verb,
        "the row",
        index + 1,
        get_items(row, inclusion=cols) if not is_null(row) else "",
        "in the table",
        quote(table),
    )


##############################


def debug_row(
    verb: str,
    index: int,
    table: str,
    *,
    cols: Optional[ColumnLike] = None,
    row: Optional[RowLike] = None,
    # Log
    verbose: bool = VERBOSE,
) -> None:
    """
    Emits a debug log for a successful row-level operation.

    Args:
        verb: The operation verb.
        index: The zero-based row index.
        table: The table name.

        cols: Optional inclusion list of columns to print from the row.
        row: Optional row-like mapping (used for message enrichment).

        verbose: When `True`, enables logging.
    """
    if verbose:
        logging.debug("- %s", get_row_message(verb, index, table, cols=cols, row=row).capitalize())


def warn_row(
    verb: str,
    index: int,
    table: str,
    *,
    exception: Optional[BaseException] = None,
    cols: Optional[ColumnLike] = None,
    row: Optional[RowLike] = None,
    # Log
    verbose: bool = VERBOSE,
) -> None:
    """
    Emits a warning log for a failed row-level operation.

    Behavior:
        • Logs the exception class name (when provided).
        • Logs the exception details via `logging.debug(…)` when present.

    Args:
        verb: The operation verb.
        index: The zero-based row index.
        table: The table name.

        exception: Optional exception instance.
        cols: Optional inclusion list of columns to print from the row.
        row: Optional row-like mapping (used for message enrichment).

        verbose: When `True`, enables logging.
    """
    if verbose:
        logging.warning(
            "- Fail to %s%s",
            get_row_message(verb, index, table, cols=cols, row=row),
            " " + par(get_full_class_name(exception)) if not is_null(exception) else "",
        )
        if not is_null(exception):
            logging.debug("%s", exception)


def error_row(
    verb: str,
    index: int,
    table: str,
    *,
    exception: Optional[BaseException] = None,
    cols: Optional[ColumnLike] = None,
    row: Optional[RowLike] = None,
    # Log
    verbose: bool = VERBOSE,
) -> None:
    """
    Emits an error log for a row-level operation failure.

    Behavior:
        • Logs errors as `logging.error(…)` unless the exception is an `IntegrityError`.
        • For `IntegrityError` (or null exception), downgrades to `warn_row(…)`.

    Args:
        verb: The operation verb.
        index: The zero-based row index.
        table: The table name.

        exception: Optional exception instance.
        cols: Optional inclusion list of columns to print from the row.
        row: Optional row-like mapping (used for message enrichment).

        verbose: When `True`, enables logging.
    """
    if isinstance(exception, db.IntegrityError):
        warn_row(verb, index, table, exception=exception, cols=cols, row=row, verbose=verbose)
    else:
        logging.error(
            "- Fail to %s%s",
            get_row_message(verb, index, table, cols=cols, row=row),
            " " + par(exception) if not is_null(exception) else "",
        )


__DB_PROCESSORS___________________________________________________________________________ = ""


__DB_CREATE_________________________________________________ = ""


def create_table(
    engine: db.Engine,
    df: pd.DataFrame,
    table: str,
    append: bool = False,
    chunk_size: Optional[int] = DEFAULT_CHUNK_SIZE,
    index: bool = False,
    index_cols: Optional[ColumnLike] = None,
    method: Optional[str] = None,
    replace: bool = False,
    schema: Optional[str] = DEFAULT_SCHEMA,
    col_types: Optional[Mapping[str, db.TypeEngine]] = None,
) -> Any:
    """
    Creates or appends to a table using `DataFrame.to_sql(…)`.

    Behavior:
        • Resolves `index_cols` when `index=True`:
          - uses the primary key columns when `append=True`
          - otherwise uses the dataframe index names
        • Uses `if_exists` policy:
          - `"append"` when `append=True`
          - `"replace"` when `replace=True`
          - `"fail"` otherwise
        • Uses `get_col_types(df)` when `col_types` is not provided.

    Args:
        engine: The SQLAlchemy engine.
        df: The dataframe to write.
        table: The destination table name.
        append: When `True`, appends to the existing table.
        chunk_size: The chunksize forwarded to `to_sql(chunksize=…)`.
        index: When `True`, writes the dataframe index as columns.
        index_cols: Optional index column names (used when `index=True`).
        method: Optional Pandas `to_sql` method (e.g., `"multi"`).
        replace: When `True`, replaces the table.
        schema: The schema name (defaults to `None`).
        col_types: Optional mapping of `{column_name: sqlalchemy_type}`.

    Returns:
        The value returned by `df.to_sql(…)` (Pandas-dependent).

    Raises:
        ValueError: If `to_sql` rejects the inputs.
        SQLAlchemyError: If the database write fails.
    """
    if index and is_null(index_cols):
        index_cols = get_primary_cols(engine, table) if append else get_names(df.index)
    return df.to_sql(
        table,
        engine,
        chunksize=chunk_size,
        if_exists="append" if append else "replace" if replace else "fail",
        index=index,
        index_label=index_cols,
        method=method,
        schema=schema,
        dtype=col_types if not is_null(col_types) else get_col_types(df),
    )


__DB_SELECT_________________________________________________ = ""


def select_query(
    engine: db.Engine,
    query: Any,
    *,
    chunk_size: Optional[int] = DEFAULT_CHUNK_SIZE,
    index_cols: Optional[ColumnLike] = None,
    # Log
    verbose: bool = VERBOSE,
) -> Union[pd.DataFrame, Iterator[pd.DataFrame]]:
    """
    Reads a SQL query into Pandas via `pd.read_sql(…)`.

    Behavior:
        • Logs the query when `verbose=True`.
        • Forwards `chunksize=chunk_size` and `index_col=index_cols` to `pd.read_sql(…)`.

    Args:
        engine: The SQLAlchemy engine.
        query: The SQL query string (or an executable statement).

        chunk_size: The Pandas chunk size (when set, Pandas returns an iterator of chunks).
        index_cols: Optional index column(s) for Pandas.

        verbose: When `True`, enables logging.

    Returns:
        The object returned by `pd.read_sql(…)` (a dataframe when `chunk_size` is null, otherwise an iterator).

    Raises:
        Exception: Any exception raised by Pandas or the database driver.
    """
    if verbose:
        logging.debug("Select the query '%s'", query)
    return pd.read_sql(query, engine, chunksize=chunk_size, index_col=index_cols)


def select_table(
    engine: db.Engine,
    table: str,
    *,
    chunk_size: Optional[int] = DEFAULT_CHUNK_SIZE,
    cols: Optional[ColumnLike] = None,
    index: bool = False,
    index_cols: Optional[ColumnLike] = None,
    row_count: int = -1,
    schema: Optional[str] = DEFAULT_SCHEMA,
    # Log
    verbose: bool = VERBOSE,
) -> pd.DataFrame:
    """
    Reads a table into a dataframe, optionally chunking and aggregating.

    Behavior:
        • Uses `pd.read_sql_table(…)` for table reads.
        • When `index=True` and `index_cols` is null, uses the primary key columns as the index.
        • When `chunk_size` is not null, aggregates chunks into a single dataframe and logs per chunk.
        • When `row_count >= 0`, stops early and returns at most `row_count` rows.

    Args:
        engine: The SQLAlchemy engine.
        table: The table name.

        chunk_size: The read chunk size. When not null, reads in chunks and aggregates.
        cols: Optional selected columns.
        index: When `True`, uses `index_cols` (or the primary key) as the dataframe index.
        index_cols: Optional index column(s).
        row_count: When non-negative, limits the returned number of rows.
        schema: The schema name (defaults to `None`).

        verbose: When `True`, enables logging and per-chunk debug messages.

    Returns:
        The resulting dataframe.

    Raises:
        Exception: Any exception raised by Pandas or the database driver.
    """
    if verbose:
        logging.debug("Select the table '%s'", table)
    if index and is_null(index_cols):
        index_cols = get_primary_cols(engine, table)
    chunks = pd.read_sql_table(table, engine, chunksize=chunk_size, columns=cols, index_col=index_cols, schema=schema)
    if is_null(chunk_size):
        if row_count >= 0 and len(chunks) >= row_count:
            return chunks.head(row_count)
        return chunks
    df = to_frame([])
    for i, chunk in enumerate(chunks):
        debug_query(
            "select",
            chunk_size,
            table,
            index_from=i * chunk_size + 1,
            index_to=(i + 1) * chunk_size,
            # Log
            verbose=verbose,
        )
        df = concat_rows(df, chunk)
        if row_count >= 0 and len(df) >= row_count:
            return df.head(row_count)
    return df


def select_table_where(
    engine: db.Engine,
    table: str,
    *,
    chunk_size: Optional[int] = DEFAULT_CHUNK_SIZE,
    cols: Optional[ColumnLike] = None,
    filtering_cols: Optional[ColumnLike] = None,
    filtering_row: Optional[RowLike] = None,
    index: bool = False,
    index_cols: Optional[ColumnLike] = None,
    is_mssql: bool = DEFAULT_IS_MSSQL,
    n: Optional[int] = None,
    order_cols: Optional[ColumnLike] = None,
    order_directions: Optional[Iterable[str]] = None,
    row_count: int = -1,
    schema: Optional[str] = DEFAULT_SCHEMA,
    # Log
    verbose: bool = VERBOSE,
) -> pd.DataFrame:
    """
    Selects rows from a table by building and executing a SELECT query with a WHERE clause.

    Behavior:
        • Logs the selected columns and filtering columns when `verbose=True`.
        • When `index=True` and `index_cols` is null, uses the primary key columns as the index.
        • Executes the query built by `build_select_table_where_query(…)`.
        • When `chunk_size` is not null, aggregates chunks into a single dataframe and logs per chunk.
        • When `row_count >= 0`, stops early and returns at most `row_count` rows.

    Args:
        engine: The SQLAlchemy engine.
        table: The table name.

        chunk_size: The read chunk size. When not null, reads in chunks and aggregates.
        cols: Optional selected columns.
        filtering_cols: Optional filtering columns for the WHERE clause.
        filtering_row: Optional row-like mapping used to build the WHERE clause.
        index: When `True`, uses `index_cols` (or the primary key) as the dataframe index.
        index_cols: Optional index column(s).
        is_mssql: Whether the source database is MSSQL (affects query formatting).
        n: Optional query limit (`"TOP"` / `"LIMIT"`).
        order_cols: Optional ORDER BY columns.
        order_directions: Optional suffixes aligned with `order_cols` (e.g., `"ASC"`, `"DESC"`).
        row_count: When non-negative, limits the returned number of rows.
        schema: The schema name (defaults to `None`).

        verbose: When `True`, enables logging and per-chunk debug messages.

    Returns:
        The resulting dataframe.

    Raises:
        Exception: Any exception raised by Pandas or the database driver.
    """
    if verbose:
        effective_filtering_cols = include_list(get_keys(filtering_row), filtering_cols)
        logging.debug(
            "Select the columns %s from the table '%s'%s",
            "*" if is_empty(cols) else format_cols(cols),
            table,
            (
                paste(" filtering on", format_cols(effective_filtering_cols))
                if not is_empty(effective_filtering_cols)
                else ""
            ),
        )
    if index and is_null(index_cols):
        index_cols = get_primary_cols(engine, table)
    chunks = pd.read_sql(
        build_select_table_where_query(
            table,
            cols=cols,
            filtering_cols=filtering_cols,
            filtering_row=filtering_row,
            is_mssql=is_mssql,
            n=n,
            order_cols=order_cols,
            order_directions=order_directions,
            schema=schema,
        ),
        engine,
        chunksize=chunk_size,
        columns=cols,
        index_col=index_cols,
    )
    if is_null(chunk_size):
        if row_count >= 0 and len(chunks) >= row_count:
            return chunks.head(row_count)
        return chunks
    df = to_frame([])
    for i, chunk in enumerate(chunks):
        debug_query(
            "select",
            chunk_size,
            table,
            index_from=i * chunk_size + 1,
            index_to=(i + 1) * chunk_size,
            # Log
            verbose=verbose,
        )
        df = concat_rows(df, chunk)
        if row_count >= 0 and len(df) >= row_count:
            return df.head(row_count)
    return df


__DB_DELETE_________________________________________________ = ""


def delete_table(
    engine: db.Engine,
    df: pd.DataFrame,
    table: str,
    *,
    filtering_cols: Optional[ColumnLike] = None,
    index: bool = False,
    is_mssql: bool = DEFAULT_IS_MSSQL,
    schema: Optional[str] = DEFAULT_SCHEMA,
    # Test
    test: bool = ASSERT,
    # Log
    verbose: bool = VERBOSE,
) -> int:
    """
    Deletes rows from a table matching each row of the dataframe.

    Behavior:
        • Optionally resets the index into columns when `index=True`.
        • Resolves the filtering columns via `get_filtering_cols(…)`.
        • Builds one DELETE query per row using `build_delete_table_query(…)`.
        • Executes each query and aggregates the deleted row counts.
        • Emits row-level debug/warn/error logs and periodic progress logs.

    Args:
        engine: The SQLAlchemy engine.
        df: The dataframe whose rows define the deletion keys.
        table: The table name.

        filtering_cols: Optional filtering columns for matching (defaults to the primary key when available).
        index: When `True`, includes the dataframe index as columns.
        is_mssql: Whether the target dialect is MSSQL.
        schema: The schema name (defaults to `None`).

        test: When `True`, enables validation warnings.
        verbose: When `True`, enables logging.

    Returns:
        The number of deleted rows (as counted from execution results).
    """
    delete_count = 0

    # Include the index in the columns
    if index:
        df = df.reset_index()

    # Get the metadata of the table
    metadata = create_metadata(schema=schema)
    table_cols = get_cols(engine, table, metadata=metadata, schema=schema)
    filtering_cols = get_filtering_cols(
        engine,
        df,
        table,
        filtering_cols=filtering_cols,
        metadata=metadata,
        schema=schema,
        test=test,
        use_only_primary=False,
    )

    if test:
        # Check the existence of the columns in the table
        get_common_cols(df, table, table_cols, filtering_cols=filtering_cols, test=test)

    debug_query("delete", len(df), table, verbose=verbose)

    def _transact(connection: db.Connection) -> int:
        nonlocal delete_count

        for i, row in df.iterrows():
            # Build the query
            query = build_delete_table_query(
                table,
                filtering_cols=filtering_cols,
                filtering_row=row,
                is_mssql=is_mssql,
                schema=schema,
            )

            # Execute the query
            try:
                # Use a nested transaction (SAVEPOINT) so a single-row failure does not poison the outer transaction
                with connection.begin_nested():
                    result = execute(engine, query, connection=connection)
                result_count = len(result) if is_struct(result) else result
                if result_count > 0:
                    delete_count += result_count
                    debug_row("delete", i, table, cols=filtering_cols, row=row, verbose=verbose)
                else:
                    warn_row("delete", i, table, cols=filtering_cols, row=row, verbose=verbose)
            except Exception as e:
                error_row("delete", i, table, exception=e, cols=filtering_cols, row=row, verbose=verbose)

            if (i + 1) % DEFAULT_DEBUG_INTERVAL == 0:
                debug_query(
                    "deleted",
                    delete_count,
                    table,
                    index_from=i + 1 - DEFAULT_DEBUG_INTERVAL + 1,
                    index_to=i + 1,
                    # Log
                    verbose=verbose,
                )

        return delete_count

    return transact(engine, _transact)


def bulk_delete_table(
    engine: db.Engine,
    df: pd.DataFrame,
    table: str,
    *,
    chunk_size: int = DEFAULT_CHUNK_SIZE,
    filtering_cols: Optional[ColumnLike] = None,
    index: bool = False,
    is_mssql: bool = DEFAULT_IS_MSSQL,
    schema: Optional[str] = DEFAULT_SCHEMA,
    # Test
    test: bool = ASSERT,
    # Log
    verbose: bool = VERBOSE,
) -> int:
    """
    Bulk-deletes rows by concatenating per-row DELETE statements into larger batches.

    Behavior:
        • Optionally resets the index into columns when `index=True`.
        • Resolves the filtering columns via `get_filtering_cols(…)`.
        • When `len(df) > chunk_size`, chunks recursively within the same transaction/connection.
        • Otherwise concatenates per-row DELETE queries and executes the combined SQL string via `exec_driver_sql(…)`.
        • Counts affected rows best-effort as `len(chunk)` on successful batch execution (`rowcount` is unreliable for
          multi-statement strings).

    Args:
        engine: The SQLAlchemy engine.
        df: The dataframe whose rows define the deletion keys.
        table: The table name.

        chunk_size: The maximum rows per bulk batch.
        filtering_cols: Optional filtering columns for matching.
        index: When `True`, includes the dataframe index as columns.
        is_mssql: Whether the target dialect is MSSQL.
        schema: The schema name (defaults to `None`).

        test: When `True`, enables validation warnings.
        verbose: When `True`, enables logging.

    Returns:
        The number of bulk-deleted rows (best-effort count).
    """
    delete_count = 0

    # Include the index in the columns
    if index:
        df = df.reset_index()

    if is_empty(df):
        return delete_count

    # Get the metadata of the table
    metadata = create_metadata(schema=schema)
    table_cols = get_cols(engine, table, metadata=metadata, schema=schema)
    filtering_cols = get_filtering_cols(
        engine,
        df,
        table,
        filtering_cols=filtering_cols,
        metadata=metadata,
        schema=schema,
        test=test,
        use_only_primary=False,
    )

    if test:
        # Check the existence of the columns in the table
        get_common_cols(df, table, table_cols, filtering_cols=filtering_cols, test=test)

    def _bulk_delete(connection: db.Connection, chunk: pd.DataFrame) -> int:
        nonlocal delete_count

        if is_empty(chunk):
            return delete_count

        # Chunk the bulk query
        if len(chunk) > chunk_size:
            chunk_count = ceil(len(chunk) / chunk_size)
            index_to = 0
            for _ in range(chunk_count):
                index_from = index_to
                index_to = minimum(index_from + chunk_size, len(chunk))
                if verbose:
                    logging.debug(
                        "Chunk the bulk-delete query from %d to %d rows",
                        index_from + 1,
                        index_to,
                    )
                _bulk_delete(connection, chunk.iloc[index_from:index_to])
            return delete_count

        debug_query("bulk-delete", len(chunk), table, verbose=verbose)

        # Build the bulk query
        query = ""
        for _, row in chunk.iterrows():
            query += build_delete_table_query(
                table,
                filtering_cols=filtering_cols,
                filtering_row=row,
                is_mssql=is_mssql,
                schema=schema,
            )

        if is_empty(query):
            return delete_count

        # Execute the bulk query
        try:
            connection.exec_driver_sql(query)
            delete_count += len(chunk)
        except Exception as e:
            error_query("bulk-deleted", table, exception=e, verbose=verbose)

        return delete_count

    def _transact(connection: db.Connection) -> int:
        return _bulk_delete(connection, df)

    return transact(engine, _transact)


__DB_INSERT_________________________________________________ = ""


def set_id_insert(
    connection: db.Connection,
    table: str,
    flag: str,
    *,
    is_mssql: bool = DEFAULT_IS_MSSQL,
    schema: Optional[str] = DEFAULT_SCHEMA,
) -> None:
    """
    Enables or disables explicit insertion into identity columns for MSSQL.

    Notes:
        `IDENTITY_INSERT` is connection/session-scoped in MSSQL, so this MUST be executed on the same connection
        used for the corresponding INSERT statements.

    Behavior:
        • For MSSQL (`is_mssql=True`), executes: `SET IDENTITY_INSERT <schema.table> <flag>;`
        • For non-MSSQL, no-op.

    Args:
        connection: The SQLAlchemy connection.
        table: The table name.
        flag: The MSSQL flag string (typically `"ON"` or `"OFF"`).

        is_mssql: Whether the target database is MSSQL.
        schema: The schema name (defaults to `None`).
    """
    if is_mssql:
        connection.exec_driver_sql(paste("SET IDENTITY_INSERT", get_full_table_name(table, schema=schema), flag) + ";")


############################################################


def insert_table(
    engine: db.Engine,
    df: pd.DataFrame,
    table: str,
    *,
    index: bool = False,
    insert_id: Optional[bool] = None,
    is_mssql: bool = DEFAULT_IS_MSSQL,
    schema: Optional[str] = DEFAULT_SCHEMA,
    # Test
    test: bool = ASSERT,
    # Log
    verbose: bool = VERBOSE,
) -> int:
    """
    Inserts rows into a table by executing one INSERT statement per dataframe row.

    Behavior:
        • Optionally resets the index into columns when `index=True`.
        • Resolves insert columns as the intersection of dataframe and table columns.
        • Auto-detects identity insertion when `insert_id` is null and identity columns are present.
        • Executes all inserts in one transaction on one connection.
        • If `insert_id=True`, toggles `IDENTITY_INSERT` ON/OFF on the same connection (best-effort via `finally`).
        • Emits row-level debug/warn/error logs and periodic progress logs.

    Args:
        engine: The SQLAlchemy engine.
        df: The dataframe to insert.
        table: The destination table name.

        index: When `True`, includes the dataframe index as columns.
        insert_id: When set, controls whether to enable `IDENTITY_INSERT` (MSSQL only).
        is_mssql: Whether the target database is MSSQL.
        schema: The schema name (defaults to `None`).

        test: When `True`, enables validation warnings.
        verbose: When `True`, enables logging.

    Returns:
        The number of inserted rows (as counted from execution results).
    """
    insert_count = 0

    # Include the index in the columns
    if index:
        df = df.reset_index()

    # Get the metadata of the table
    metadata = create_metadata(schema=schema)
    table_cols = get_cols(engine, table, metadata=metadata, schema=schema)
    primary_cols = get_primary_cols(engine, table, metadata=metadata, schema=schema)

    # Get the columns to insert
    cols = get_common_cols(df, table, table_cols, test=test)
    if is_null(insert_id):
        insert_id = not is_empty(include_list(cols, get_identity_cols(engine, table, is_mssql=is_mssql)))

    debug_query("insert", len(df), table, verbose=verbose)

    def _transact(connection: db.Connection) -> int:
        nonlocal insert_count

        if insert_id:
            set_id_insert(connection, table, "ON", is_mssql=is_mssql, schema=schema)
        try:
            for i, row in df.iterrows():
                # Build the query
                query = build_insert_table_query(table, cols, row, is_mssql=is_mssql, schema=schema)

                # Execute the query
                try:
                    # Use a nested transaction (SAVEPOINT) so a single-row failure does not poison the outer transaction
                    with connection.begin_nested():
                        result = execute(engine, query, connection=connection)
                    result_count = len(result) if is_struct(result) else result
                    if result_count > 0:
                        insert_count += result_count
                        debug_row("insert", i, table, cols=primary_cols, row=row, verbose=verbose)
                    else:
                        warn_row("insert", i, table, cols=primary_cols, row=row, verbose=verbose)
                except Exception as e:
                    error_row("insert", i, table, exception=e, cols=primary_cols, row=row, verbose=verbose)

                if (i + 1) % DEFAULT_DEBUG_INTERVAL == 0:
                    debug_query(
                        "inserted",
                        insert_count,
                        table,
                        index_from=i + 1 - DEFAULT_DEBUG_INTERVAL + 1,
                        index_to=i + 1,
                        # Log
                        verbose=verbose,
                    )
            return insert_count
        finally:
            if insert_id:
                set_id_insert(connection, table, "OFF", is_mssql=is_mssql, schema=schema)

    return transact(engine, _transact)


def bulk_insert_table(
    engine: db.Engine,
    df: pd.DataFrame,
    table: str,
    *,
    chunk_size: int = DEFAULT_CHUNK_SIZE,
    index: bool = False,
    insert_id: Optional[bool] = None,
    is_mssql: bool = DEFAULT_IS_MSSQL,
    schema: Optional[str] = DEFAULT_SCHEMA,
    # Test
    test: bool = ASSERT,
    # Log
    verbose: bool = VERBOSE,
) -> int:
    """
    Bulk-inserts rows by concatenating per-row INSERT statements into larger batches.

    Behavior:
        • Optionally resets the index into columns when `index=True`.
        • Resolves insert columns as the intersection of dataframe and table columns.
        • Auto-detects identity insertion when `insert_id` is null and identity columns are present.
        • Executes the full bulk insert in one transaction on one connection.
        • If `insert_id=True`, toggles `IDENTITY_INSERT` ON/OFF on the same connection (best-effort via `finally`).
        • When `len(df) > chunk_size`, chunks recursively within the same transaction/connection.
        • Builds `query += …` and executes via `connection.exec_driver_sql(query)`.
        • Counts affected rows best-effort as `len(chunk)` on successful batch execution (`rowcount` is unreliable for
          multi-statement strings).

    Args:
        engine: The SQLAlchemy engine.
        df: The dataframe to insert.
        table: The destination table name.

        chunk_size: The maximum rows per bulk batch.
        index: When `True`, includes the dataframe index as columns.
        insert_id: When set, controls whether to enable `IDENTITY_INSERT` (MSSQL only).
        is_mssql: Whether the target database is MSSQL.
        schema: The schema name (defaults to `None`).

        test: When `True`, enables validation warnings.
        verbose: When `True`, enables logging.

    Returns:
        The number of bulk-inserted rows (best-effort count).
    """
    insert_count = 0

    # Include the index in the columns
    if index:
        df = df.reset_index()

    if is_empty(df):
        return insert_count

    # Get the metadata of the table
    table_cols = get_cols(engine, table, schema=schema)

    # Get the columns to insert
    cols = get_common_cols(df, table, table_cols, test=test)
    if is_null(insert_id):
        insert_id = not is_empty(include_list(cols, get_identity_cols(engine, table, is_mssql=is_mssql)))

    def _bulk_insert(connection: db.Connection, chunk: pd.DataFrame) -> int:
        nonlocal insert_count

        if is_empty(chunk):
            return insert_count

        # Chunk the bulk query
        if len(chunk) > chunk_size:
            chunk_count = ceil(len(chunk) / chunk_size)
            index_to = 0
            for _ in range(chunk_count):
                index_from = index_to
                index_to = minimum(index_from + chunk_size, len(chunk))
                if verbose:
                    logging.debug(
                        "Chunk the bulk-insert query from %d to %d rows",
                        index_from + 1,
                        index_to,
                    )
                _bulk_insert(connection, chunk.iloc[index_from:index_to])
            return insert_count

        debug_query("bulk-insert", len(chunk), table, verbose=verbose)

        # Build the bulk query
        query = ""
        for _, row in chunk.iterrows():
            query += build_insert_table_query(table, cols, row, is_mssql=is_mssql, schema=schema)

        if is_empty(query):
            return insert_count

        # Execute the bulk query
        try:
            connection.exec_driver_sql(query)
            insert_count += len(chunk)
        except Exception as e:
            error_query("bulk-inserted", table, exception=e, verbose=verbose)

        return insert_count

    def _transact(connection: db.Connection) -> int:
        if insert_id:
            set_id_insert(connection, table, "ON", is_mssql=is_mssql, schema=schema)
        try:
            return _bulk_insert(connection, df)
        finally:
            if insert_id:
                set_id_insert(connection, table, "OFF", is_mssql=is_mssql, schema=schema)

    return transact(engine, _transact)


__DB_UPDATE_________________________________________________ = ""


def update_table(
    engine: db.Engine,
    df: pd.DataFrame,
    table: str,
    *,
    filtering_cols: Optional[ColumnLike] = None,
    index: bool = False,
    is_mssql: bool = DEFAULT_IS_MSSQL,
    schema: Optional[str] = DEFAULT_SCHEMA,
    # Test
    test: bool = ASSERT,
    # Log
    verbose: bool = VERBOSE,
) -> int:
    """
    Updates rows in a table by executing one UPDATE statement per dataframe row.

    Behavior:
        • Optionally resets the index into columns when `index=True`.
        • Resolves filtering columns via `get_filtering_cols(…)` (defaults to the primary key when available).
        • Resolves update columns as the intersection of dataframe and table columns excluding filtering columns.
        • Builds one UPDATE query per row using `build_update_table_query(…)`.
        • Emits row-level debug/warn/error logs and periodic progress logs.

    Args:
        engine: The SQLAlchemy engine.
        df: The dataframe whose rows define the update values and matching keys.
        table: The table name.

        filtering_cols: Optional filtering columns for matching.
        index: When `True`, includes the dataframe index as columns.
        is_mssql: Whether the target dialect is MSSQL.
        schema: The schema name (defaults to `None`).

        test: When `True`, enables validation warnings.
        verbose: When `True`, enables logging.

    Returns:
        The number of updated rows (as counted from execution results).
    """
    update_count = 0

    # Include the index in the columns
    if index:
        df = df.reset_index()

    # Get the metadata of the table
    metadata = create_metadata(schema=schema)
    table_cols = get_cols(engine, table, metadata=metadata, schema=schema)
    filtering_cols = get_filtering_cols(
        engine,
        df,
        table,
        filtering_cols=filtering_cols,
        metadata=metadata,
        schema=schema,
        test=test,
    )

    # Get the columns to update
    cols = get_common_cols(df, table, table_cols, filtering_cols=filtering_cols, test=test)
    if is_empty(cols):
        logging.warning(
            "The dataframe contains only the filtering columns %s or no column of the table '%s'",
            par(filtering_cols),
            table,
        )
        return 0

    debug_query("update", len(df), table, verbose=verbose)

    def _transact(connection: db.Connection) -> int:
        nonlocal update_count

        for i, row in df.iterrows():
            # Build the query
            query = build_update_table_query(
                table, cols, row, filtering_cols=filtering_cols, is_mssql=is_mssql, schema=schema
            )

            # Execute the query
            try:
                # Use a nested transaction (SAVEPOINT) so a single-row failure does not poison the outer transaction
                with connection.begin_nested():
                    result = execute(engine, query, connection=connection)
                result_count = len(result) if is_struct(result) else result
                if result_count > 0:
                    update_count += result_count
                    debug_row("update", i, table, cols=filtering_cols, row=row, verbose=verbose)
                else:
                    warn_row("update", i, table, cols=filtering_cols, row=row, verbose=verbose)
            except Exception as e:
                error_row("update", i, table, exception=e, cols=filtering_cols, row=row, verbose=verbose)

            if (i + 1) % DEFAULT_DEBUG_INTERVAL == 0:
                debug_query(
                    "updated",
                    update_count,
                    table,
                    index_from=i + 1 - DEFAULT_DEBUG_INTERVAL + 1,
                    index_to=i + 1,
                    # Log
                    verbose=verbose,
                )

        return update_count

    return transact(engine, _transact)


def bulk_update_table(
    engine: db.Engine,
    df: pd.DataFrame,
    table: str,
    *,
    chunk_size: int = DEFAULT_CHUNK_SIZE,
    filtering_cols: Optional[ColumnLike] = None,
    index: bool = False,
    is_mssql: bool = DEFAULT_IS_MSSQL,
    schema: Optional[str] = DEFAULT_SCHEMA,
    # Test
    test: bool = ASSERT,
    # Log
    verbose: bool = VERBOSE,
) -> int:
    """
    Bulk-updates rows by concatenating per-row UPDATE statements into larger batches.

    Behavior:
        • Optionally resets the index into columns when `index=True`.
        • Resolves filtering columns via `get_filtering_cols(…)`.
        • Resolves update columns as the intersection of dataframe and table columns excluding filtering columns.
        • When `len(df) > chunk_size`, chunks recursively within the same transaction/connection.
        • Otherwise concatenates per-row UPDATE queries and executes the combined SQL string via `exec_driver_sql(…)`.
        • Builds `query += …` and executes via `connection.exec_driver_sql(query)`.
        • Counts affected rows best-effort as `len(chunk)` on successful batch execution (`rowcount` is unreliable for
          multi-statement strings).

    Args:
        engine: The SQLAlchemy engine.
        df: The dataframe whose rows define the update values and matching keys.
        table: The table name.

        chunk_size: The maximum rows per bulk batch.
        filtering_cols: Optional filtering columns for matching.
        index: When `True`, includes the dataframe index as columns.
        is_mssql: Whether the target dialect is MSSQL.
        schema: The schema name (defaults to `None`).

        test: When `True`, enables validation warnings.
        verbose: When `True`, enables logging.

    Returns:
        The number of bulk-updated rows (best-effort count).
    """
    update_count = 0

    # Include the index in the columns
    if index:
        df = df.reset_index()

    if is_empty(df):
        return update_count

    # Get the metadata of the table
    metadata = create_metadata(schema=schema)
    table_cols = get_cols(engine, table, metadata=metadata, schema=schema)
    filtering_cols = get_filtering_cols(
        engine,
        df,
        table,
        filtering_cols=filtering_cols,
        metadata=metadata,
        schema=schema,
        test=test,
    )

    # Get the columns to update
    cols = get_common_cols(df, table, table_cols, filtering_cols=filtering_cols, test=test)

    def _bulk_update(connection: db.Connection, chunk: pd.DataFrame) -> int:
        nonlocal update_count

        if is_empty(chunk):
            return update_count

        # Chunk the bulk query
        if len(chunk) > chunk_size:
            chunk_count = ceil(len(chunk) / chunk_size)
            index_to = 0
            for _ in range(chunk_count):
                index_from = index_to
                index_to = minimum(index_from + chunk_size, len(chunk))
                if verbose:
                    logging.debug(
                        "Chunk the bulk-update query from %d to %d rows",
                        index_from + 1,
                        index_to,
                    )
                _bulk_update(connection, chunk.iloc[index_from:index_to])
            return update_count

        debug_query("bulk-update", len(chunk), table, verbose=verbose)

        # Build the bulk query
        query = ""
        for _, row in chunk.iterrows():
            query += build_update_table_query(
                table,
                cols,
                row,
                filtering_cols=filtering_cols,
                is_mssql=is_mssql,
                schema=schema,
            )

        if is_empty(query):
            return update_count

        # Execute the bulk query
        try:
            connection.exec_driver_sql(query)
            update_count += len(chunk)
        except Exception as e:
            error_query("bulk-updated", table, exception=e, verbose=verbose)

        return update_count

    def _transact(connection: db.Connection) -> int:
        return _bulk_update(connection, df)

    return transact(engine, _transact)


__DB_UPSERT_________________________________________________ = ""


def upsert_table(
    engine: db.Engine,
    df: pd.DataFrame,
    table: str,
    *,
    filtering_cols: Optional[ColumnLike] = None,
    index: bool = False,
    is_mssql: bool = DEFAULT_IS_MSSQL,
    schema: Optional[str] = DEFAULT_SCHEMA,
    # Test
    test: bool = ASSERT,
    # Log
    verbose: bool = VERBOSE,
) -> int:
    """
    Updates existing rows and inserts missing rows for the dataframe into the table.

    Behavior:
        • Optionally resets the index into columns when `index=True`.
        • Calls `update_table(…)` to update matching rows.
        • If not all rows were updated, calls `insert_table(…)` to insert the remaining rows.
        • Optionally verifies the result by re-selecting and checking presence when `verbose=True`.
        • Logs discrepancies between expected and actual affected-row counts.

    Args:
        engine: The SQLAlchemy engine.
        df: The dataframe to upsert.
        table: The table name.

        filtering_cols: Optional filtering columns for matching.
        index: When `True`, includes the dataframe index as columns.
        is_mssql: Whether the target dialect is MSSQL.
        schema: The schema name (defaults to `None`).

        test: When `True`, enables validation warnings.
        verbose: When `True`, enables logging and optional verification.

    Returns:
        The number of updated/inserted rows (best-effort count).
    """
    upsert_count = 0

    # Include the index in the columns
    if index:
        df = df.reset_index()

    # Update the matching rows
    update_count = update_table(
        engine,
        df,
        table,
        filtering_cols=filtering_cols,
        index=False,
        is_mssql=is_mssql,
        schema=schema,
        test=test,
        verbose=False,
    )
    upsert_count += update_count
    if update_count > 0:
        debug_query("update", upsert_count, table, verbose=verbose)

    # Insert the non-matching rows
    if update_count != len(df):
        insert_count = insert_table(
            engine,
            df,
            table,
            index=False,
            is_mssql=is_mssql,
            schema=schema,
            test=test,
            verbose=False,
        )
        upsert_count += insert_count
        if insert_count > 0:
            debug_query("insert", insert_count, table, verbose=verbose)
    else:
        insert_count = 0

    # Verify
    if upsert_count != len(df):
        if verbose:
            t = select_table_where(
                engine,
                table,
                chunk_size=None,
                cols=get_names(df),
                index=False,
                is_mssql=is_mssql,
                schema=schema,
                # Log
                verbose=verbose,
            )
            for i, row in df.iterrows():
                if is_empty(filter_rows(t, row)):
                    warn_row("update/insert", i, table, cols=filtering_cols, row=row, verbose=verbose)
        if upsert_count == 0:
            warn_query("update/insert", table, verbose=verbose)
        elif upsert_count < len(df):
            logging.warning(
                "Update/insert %d/%d rows in the table '%s' which is %d rows less than expected %s",
                update_count,
                insert_count,
                table,
                len(df) - upsert_count,
                par(len(df)),
            )
        elif upsert_count > len(df):
            logging.warning(
                "Update/insert %d/%d rows in the table '%s' which is %d rows more than expected %s",
                update_count,
                insert_count,
                table,
                upsert_count - len(df),
                par(len(df)),
            )
    return upsert_count


__DB_RUNNERS______________________________________________________________________________ = ""


def execute(
    engine: db.Engine,
    query: Any,
    *args: Any,
    connection: Optional[db.Connection] = None,
    **kwargs: Any,
) -> Union[List[Any], int]:
    """
    Executes a single SQL statement and returns either fetched rows or an affected-row count.

    Behavior:
        • Opens a new connection via `engine.begin()`.
        • Executes the statement via `connection.execute(…)` or `connection.exec_driver_sql(…)`.
        • If the result exposes rows, returns `fetchall()`, otherwise returns `rowcount`.

    Args:
        engine: The SQLAlchemy engine.
        query: The SQL query (string or executable statement).
        *args: Positional arguments forwarded to the execution method.
        **kwargs: Keyword arguments forwarded to the execution method.

    Returns:
        A list of fetched rows when the result returns rows, otherwise an integer row count.

    Raises:
        SQLAlchemyError: If execution fails.
    """
    if is_null(connection):
        with engine.begin() as connection:
            result = execute_on_connection(connection, query, *args, **kwargs)
            return result.fetchall() if result.returns_rows else result.rowcount

    result = execute_on_connection(connection, query, *args, **kwargs)
    return result.fetchall() if result.returns_rows else result.rowcount


def execute_on_connection(connection: db.Connection, query: Any, *args: Any, **kwargs: Any):
    if isinstance(query, str):
        return connection.exec_driver_sql(query, *args, **kwargs)
    return connection.execute(query, *args, **kwargs)


##############################


def execute_procedure(engine: db.Engine, procedure: str, *args: Any) -> List[Tuple[Any, ...]]:
    """
    Executes a stored procedure via a DB-API cursor and returns its result set (if any).

    Behavior:
        • Uses `engine.raw_connection()` to access the underlying DB-API connection.
        • Executes `cursor.callproc(procedure, args)`, then advances to the next result set.
        • Commits the transaction and closes the connection.

    Args:
        engine: The SQLAlchemy engine.
        procedure: The stored procedure name.
        *args: Positional procedure arguments.

    Returns:
        A list of rows from the procedure result set (empty when the procedure returns no rows).

    Raises:
        Exception: Any exception raised by the DB-API driver.
    """
    connection = engine.raw_connection()
    cursor = connection.cursor()
    try:
        cursor.callproc(procedure, args)
        cursor.nextset()
        result = cursor.fetchall() if not is_null(cursor.description) else []
        connection.commit()
        return result
    finally:
        try:
            cursor.close()
        finally:
            connection.close()


##############################


def transact(engine: db.Engine, f: Callable[[db.Connection], Any]) -> Any:
    """Executes multiple statements in one transaction on one connection."""
    with engine.begin() as connection:
        return f(connection)


__DB_SERVICES_____________________________________________________________________________ = ""


__DB_MIGRATE________________________________________________ = ""


def migrate(
    engine_from: db.Engine,
    engine_to: db.Engine,
    tables: Iterable[str],
    *,
    chunk_size: int = DEFAULT_CHUNK_SIZE,
    collation: Optional[str] = None,
    create: bool = True,
    drop: bool = False,
    fill: bool = True,
    filtering_cols: Optional[ColumnLike] = None,
    filtering_row: Optional[RowLike] = None,
    is_mssql_from: bool = DEFAULT_IS_MSSQL,
    is_mssql_to: bool = DEFAULT_IS_MSSQL,
    schema: Optional[str] = DEFAULT_SCHEMA,
    upsert: bool = False,
    # Test
    test: bool = ASSERT,
    # Log
    verbose: bool = VERBOSE,
) -> int:
    """
    Migrates tables from one engine to another, optionally transforming metadata for dialect differences.

    Behavior:
        • When `drop` or `create`:
          - Reflects metadata from `engine_from`.
          - Applies `update_col(…)` to each source column to adjust defaults/types/collation.
          - Lowercases identifiers when migrating MSSQL -> non-MSSQL via `metadata_to_lowercase(…)`.
          - Drops and/or creates tables on `engine_to`.
        • When `fill`:
          - Reads data from the source using `select_table(…)` or `select_table_where(…)`.
          - Lowercases table/column names when migrating MSSQL -> non-MSSQL.
          - Writes data using `bulk_insert_table(…)` or `upsert_table(…)`.

    Args:
        engine_from: The source SQLAlchemy engine.
        engine_to: The destination SQLAlchemy engine.
        tables: The list of table names to migrate.

        chunk_size: The chunk size used for reading and bulk writing.
        collation: Optional collation to apply where supported.
        create: When `True`, creates the tables on the destination.
        drop: When `True`, drops the tables on the destination before creating.
        fill: When `True`, migrates the table data.
        filtering_cols: Optional filtering columns for `select_table_where(…)`.
        filtering_row: Optional filtering row for `select_table_where(…)`.
        is_mssql_from: Whether the source database is MSSQL.
        is_mssql_to: Whether the destination database is MSSQL.
        schema: The schema name (defaults to `None`).
        upsert: When `True`, performs upserts instead of bulk inserts.

        test: When `True`, enables validation warnings during writes.
        verbose: When `True`, enables logging.

    Returns:
        The number of migrated rows (best-effort count).
    """
    count = 0

    # Create the tables
    if drop or create:
        metadata = create_metadata(schema=schema)
        for table in tables:
            logging.debug(
                "%s the table '%s'",
                "Recreate" if drop and create else "Drop" if drop else "Create",
                table,
            )
            table_metadata = get_table_metadata(engine_from, table, metadata=metadata, schema=schema)
            for col in table_metadata.columns:
                update_col(col, collation=collation, is_mssql_from=is_mssql_from, is_mssql_to=is_mssql_to)
        if is_mssql_from and not is_mssql_to:
            metadata_to_lowercase(metadata)
        if drop:
            metadata.drop_all(bind=engine_to, checkfirst=True)
        if create:
            metadata.create_all(bind=engine_to)

    # Fill the tables
    if fill:
        for table in tables:
            logging.debug("Fill the table '%s'", table)
            df = (
                select_table_where(
                    engine_from,
                    table,
                    chunk_size=chunk_size,
                    filtering_cols=filtering_cols,
                    filtering_row=filtering_row,
                    is_mssql=is_mssql_from,
                    schema=schema,
                    # Log
                    verbose=verbose,
                )
                if not is_null(filtering_row)
                else select_table(engine_from, table, chunk_size=chunk_size, schema=schema, verbose=verbose)
            )
            if is_mssql_from and not is_mssql_to:
                table = table.lower()
                set_names(df, map(str.lower, get_names(df)))
            if upsert:
                count += upsert_table(
                    engine_to,
                    df,
                    table,
                    is_mssql=is_mssql_to,
                    schema=schema,
                    test=test,
                    # Log
                    verbose=verbose,
                )
            else:
                count += bulk_insert_table(
                    engine_to,
                    df,
                    table,
                    chunk_size=chunk_size,
                    is_mssql=is_mssql_to,
                    schema=schema,
                    test=test,
                    # Log
                    verbose=verbose,
                )
    return count
