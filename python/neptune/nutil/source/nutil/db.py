#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contain utility functions for databases
#
# SYNOPSIS
#    <NAME>
#
# AUTHOR
#    Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#    Copyright © 2013-2022 Florian Barras <https://barras.io>.
#    The MIT License (MIT) <https://opensource.org/licenses/MIT>.
####################################################################################################

import sqlalchemy as db
from sqlalchemy.dialects import mssql
from sqlalchemy.engine import URL
from sqlalchemy.exc import *
from sqlalchemy.orm import *
from sqlalchemy.sql.elements import *

from nutil.common import *

####################################################################################################
# DB CONSTANTS
####################################################################################################

__DB_CONSTANTS____________________________________ = ''

# The default flag specifying whether the DB is Microsoft SQL Server
DEFAULT_IS_MSSQL = True

# The default schema
DEFAULT_SCHEMA = 'dbo'

#########################

# The default chunk size
DEFAULT_CHUNK_SIZE = 100

#########################

# The default debug interval
DEFAULT_DEBUG_INTERVAL = 1000

####################################################################################################
# DB FUNCTIONS
####################################################################################################

__DB______________________________________________ = ''

# • DB CONNECT #####################################################################################

__DB_CONNECT______________________________________ = ''


def create_engine(dialect='mssql', driver='pyodbc', username=None, password=None, host='localhost',
                  port=1433, database=None, query=None):
	'''Creates an engine with the specified parameters.'''
	return db.create_engine(URL.create(dialect + '+' + driver, username=username, password=password,
	                                   host=host, port=port, database=database, query=query))


def create_session(engine):
	'''Creates a session for the specified engine.'''
	return Session(bind=engine)


# • DB CONSOLE #####################################################################################

__DB_CONSOLE______________________________________ = ''


def get_query_message(verb, count, table):
	return paste(verb, count, 'rows', 'in the table', quote(table))


#########################

def debug_query(verb, count, table, index_from=None, index_to=None, verbose=VERBOSE):
	if verbose:
		prefix = ''
		if not is_null(index_from):
			prefix += ' from ' + str(index_from)
		if not is_null(index_to):
			prefix += ' to ' + str(index_to)
		if not is_empty(prefix):
			prefix = 'processing rows' + prefix + ', '
		debug((prefix + get_query_message(verb, count, table)).capitalize())


def warn_query(verb, table, ex=None, verbose=VERBOSE):
	if verbose:
		warn(paste('No row has been', verb, 'in the table', quote(table)),
		     par(get_full_class_name(ex)) if not is_null(ex) else '')
		if not is_null(ex):
			trace(ex)


def error_query(verb, table, ex=None, verbose=VERBOSE):
	if not isinstance(ex, IntegrityError):
		error(paste('No row has been', verb, 'in the table', quote(table)),
		      par(ex) if not is_null(ex) else '')
	else:
		warn_query(verb, table, ex=ex, verbose=verbose)


##################################################

def get_row_message(verb, index, table, cols=None, row=None):
	return paste(verb, 'the row', index + 1,
	             get_items(row, inclusion=cols) if not is_null(row) else '',
	             'in the table', quote(table))


#########################

def trace_row(verb, index, table, cols=None, row=None, verbose=VERBOSE):
	if verbose:
		trace('-', get_row_message(verb, index, table, cols=cols, row=row).capitalize())


def warn_row(verb, index, table, ex=None, cols=None, row=None, verbose=VERBOSE):
	if verbose:
		warn(paste('- Fail to', get_row_message(verb, index, table, cols=cols, row=row)),
		     par(get_full_class_name(ex)) if not is_null(ex) else '')
		if not is_null(ex):
			trace(ex)


def error_row(verb, index, table, ex=None, cols=None, row=None, verbose=VERBOSE):
	if not is_null(ex) and not isinstance(ex, IntegrityError):
		error(paste('- Fail to', get_row_message(verb, index, table, cols=cols, row=row)),
		      par(ex) if not is_null(ex) else '')
	else:
		warn_row(verb, index, table, ex=ex, cols=cols, row=row, verbose=verbose)


# • DB FORMAT ######################################################################################

__DB_FORMAT_______________________________________ = ''


def create_where_clause(filtering_cols=None, filtering_row=None, is_mssql=DEFAULT_IS_MSSQL):
	'''Creates the WHERE clause with the specified filtering columns and row.'''
	cols = include_list(get_keys(filtering_row), filtering_cols)
	if is_empty(cols):
		return ''
	return paste('WHERE',
	             collapse([collapse(format_name(col),
	                                ' IS ' if is_null(filtering_row[col])
	                                else ' IN ' if is_collection(filtering_row[col])
	                                else '=',
	                                format(filtering_row[col], is_mssql=is_mssql)) for col in cols],
	                      delimiter=' AND '))


##################################################

def escape(name):
	'''Escapes the specified name (for either MSSQL or PostgreSQL).'''
	return str(name).replace('\'', '\'\'').replace('%', '%%')


#########################

def format_name(name):
	'''Formats the specified name (for either MSSQL or PostgreSQL).'''
	if '(' in name and ')' in name:
		return name
	return dquote(name)


def format_cols(*cols):
	'''Formats the specified column names (for either MSSQL or PostgreSQL).'''
	cols = remove_empty(to_collection(*cols))
	return collist([format_name(col) for col in cols])


def format(value, is_mssql=DEFAULT_IS_MSSQL):
	'''Formats the specified value (for either MSSQL or PostgreSQL).'''
	if is_null(value):
		return 'NULL'
	elif is_collection(value):
		return par(collist(apply(value, format, is_mssql=is_mssql)))
	elif is_bool(value):
		if is_mssql:
			return 1 if value else 0
		return value
	elif is_number(value):
		if is_nan(value):
			return 'NULL'
		return value
	elif is_timestamp(value):
		return quote(value.strftime(DEFAULT_DATE_TIME_FORMAT)[:-3])
	return quote(escape(value))


# • DB METADATA ####################################################################################

__DB_METADATA_____________________________________ = ''


def create_metadata(engine, schema=DEFAULT_SCHEMA):
	'''Creates the metadata (in the specified schema) using the specified engine.'''
	return db.MetaData(bind=engine, schema=schema)


##################################################

def get_table_metadata(engine, table, metadata=None, schema=DEFAULT_SCHEMA):
	'''Returns the metadata of the specified table (in the specified schema) using the specified
	engine.'''
	if is_null(metadata):
		metadata = create_metadata(engine, schema=schema)
	metadata.reflect(extend_existing=True, only=[table], schema=schema, views=True)
	return metadata.tables[collapse(schema, '.', table)]


def get_full_table_name(table, schema=DEFAULT_SCHEMA):
	'''Returns the full table name (in the specified schema).'''
	return collapse(collapse(format_name(schema), '.') if not is_null(schema) else '',
	                format_name(table))


#########################

def get_common_cols(df, table, table_cols, filtering_cols=None, test=ASSERT):
	'''Returns the columns of the specified dataframe that exist in the specified table and that are
	not the specified filtering columns.'''
	if test:
		# Test the existence of the columns in the table
		for col in df:
			if col not in table_cols:
				warn('The column', quote(col), 'does not exist in the table', quote(table))
	return filter_list(df, inclusion=table_cols, exclusion=filtering_cols)


def get_filtering_cols(engine, df, table, filtering_cols=None, metadata=None, schema=DEFAULT_SCHEMA,
                       test=ASSERT, use_only_primary=True):
	if is_null(filtering_cols):
		if use_only_primary:
			filtering_cols = get_primary_cols(engine, table, metadata=metadata, schema=schema)
			if test:
				# Test the existence of the filtering columns in the dataframe
				for col in filtering_cols:
					if col not in df:
						warn('The filtering column', quote(col), 'does not exist in the dataframe')
		else:
			filtering_cols = get_cols(engine, table, metadata=metadata, schema=schema)
	filtering_cols = include_list(df, filtering_cols)
	if test and is_empty(filtering_cols):
		# Test the existence of any filtering column in the dataframe
		warn('There is no filtering column')
	return filtering_cols


def get_cols(engine, table, metadata=None, schema=DEFAULT_SCHEMA):
	table_metadata = get_table_metadata(engine, table, metadata=metadata, schema=schema)
	return [col.name for col in table_metadata.columns]


def get_identity_cols(engine, table, is_mssql=DEFAULT_IS_MSSQL, verbose=VERBOSE):
	'''Returns the identity columns of the specified table.'''
	if is_mssql:
		return select_table_where(engine, 'identity_columns', chunk_size=None, cols=['name'],
		                          filtering_cols='OBJECT_NAME(object_id)',
		                          filtering_row={'OBJECT_NAME(object_id)': table}, schema='sys',
		                          verbose=verbose)['name']
	return []


def get_primary_cols(engine, table, cols=None, metadata=None, schema=DEFAULT_SCHEMA):
	'''Returns the primary columns of the specified table.'''
	table_metadata = get_table_metadata(engine, table, metadata=metadata, schema=schema)
	primary_cols = [col.name for col in table_metadata.primary_key.columns]
	return include_list(primary_cols, cols)


#########################

def get_col_types(df,
                  # DateTime
                  timezone=False, to_date=False,
                  # Float
                  decimal_scale=None, to_decimal=True, float_precision=None,
                  # String
                  string_length=8000, to_text=False):
	types = {}
	for col, type in concat_rows(get_element_types(df.index), get_element_types(df)).items():
		type_name = str(type)
		if 'bool' in type_name:
			types.update({col: db.Boolean()})
		elif 'datetime' in type_name:
			types.update({col: (db.Date(timezone=timezone) if to_date else
			                    db.DateTime(timezone=timezone))})
		elif 'float' in type_name:
			types.update({col: db.Float(asdecimal=to_decimal,
			                            decimal_return_scale=decimal_scale,
			                            precision=float_precision)})
		elif 'int' in type_name:
			types.update({col: db.Integer()})
		else:
			types.update({col: db.Text() if to_text else db.String(length=string_length)})
	return types


##################################################

def metadata_to_lowercase(metadata):
	for _, table in metadata.tables.items():
		table_to_lowercase(table)


def table_to_lowercase(table):
	table.name = table.name.lower()
	table.fullname = table.fullname.lower()
	if not is_null(table.primary_key):
		table.primary_key.name = table.primary_key.name.lower()
	for _, column in table.columns.items():
		column.name = column.name.lower()
		column.key = column.key.lower()
		for fk in column.foreign_keys:
			fk.name = fk.name.lower()
			fk.constraint.name = fk.constraint.name.lower()


# • DB EXECUTE #####################################################################################

def execute(engine, query, *args, **kwargs):
	'''Returns the result of the execution of the specified query using the specified engine.'''
	with engine.connect() as connection:
		result = connection.execute(query, *args, **kwargs)
		return result.fetchall() if not is_null(result.cursor) else result.rowcount


def execute_procedure(engine, procedure, *args):
	'''Returns the result of the execution of the specified procedure using the specified engine.'''
	connection = engine.raw_connection()
	try:
		with connection.cursor() as cursor:
			cursor.callproc(procedure, args)
			cursor.nextset()
			result = cursor.fetchall() if not is_null(cursor.description) else []
			connection.commit()
			return result
	finally:
		connection.close()


def transact(engine, query, *args, **kwargs):
	'''Returns the result of the transaction of the specified query using the specified engine.'''
	with engine.begin() as connection:
		result = connection.execute(query, *args, **kwargs)
		return result.fetchall() if not is_null(result.cursor) else result.rowcount


# • DB CREATE ######################################################################################

__DB_CREATE_______________________________________ = ''


def create_table(engine, df, table, append=False, chunk_size=DEFAULT_CHUNK_SIZE, index=False,
                 index_cols=None, method=None, replace=False, schema=DEFAULT_SCHEMA, type=None):
	if index and is_null(index_cols):
		index_cols = get_primary_cols(engine, table) if append else get_names(df.index)
	return df.to_sql(table, engine,
	                 chunksize=chunk_size,
	                 if_exists='append' if append else 'replace' if replace else 'fail',
	                 index=index, index_label=index_cols, method=method, schema=schema,
	                 dtype=type if not is_null(type) else get_col_types(df))


# • DB SELECT ######################################################################################

__DB_SELECT_______________________________________ = ''


def create_select_table_where_query(table, cols=None, filtering_cols=None, filtering_row=None,
                                    is_mssql=DEFAULT_IS_MSSQL, n=None, order='ASC',
                                    schema=DEFAULT_SCHEMA):
	'''Creates the query to select the specified columns of the rows matching the specified
	filtering row at the specified filtering columns from the specified table (in the specified
	schema).'''
	return paste('SELECT', paste('TOP', n) if not is_null(n) and is_mssql else '',
	             '*' if is_empty(cols) else format_cols(cols),
	             'FROM', get_full_table_name(table, schema=schema),
	             create_where_clause(filtering_cols=filtering_cols, filtering_row=filtering_row,
	                                 is_mssql=is_mssql),
	             paste('ORDER BY', format_cols(cols), order) if not is_empty(cols) else '',
	             paste('LIMIT', n) if not is_null(n) and not is_mssql else '') + ';'


##################################################

def select_query(engine, query, chunk_size=DEFAULT_CHUNK_SIZE, index_cols=None, verbose=VERBOSE):
	'''Returns the dataframe read from the specified query.'''
	if verbose:
		debug('Select the query', quote(query))
	return pd.read_sql(query, engine, chunksize=chunk_size, index_col=index_cols)


def select_table(engine, table, chunk_size=DEFAULT_CHUNK_SIZE, cols=None, index=False,
                 index_cols=None, row_count=-1, schema=DEFAULT_SCHEMA, verbose=VERBOSE):
	'''Returns the dataframe read from the specified table (in the specified schema).'''
	if verbose:
		debug('Select the table', quote(table))
	if index and is_null(index_cols):
		index_cols = get_primary_cols(engine, table)
	chunks = pd.read_sql_table(table, engine, chunksize=chunk_size, columns=cols,
	                           index_col=index_cols, schema=schema)
	if is_null(chunk_size):
		if row_count >= 0 and len(chunks) >= row_count:
			return chunks.head(row_count)
		return chunks
	df = to_frame([])
	for i, chunk in enumerate(chunks):
		debug_query('select', chunk_size, table, index_from=i * chunk_size + 1,
		            index_to=(i + 1) * chunk_size, verbose=verbose)
		df = concat_rows(df, chunk)
		if row_count >= 0 and len(df) >= row_count:
			return df.head(row_count)
	return df


def select_table_where(engine, table, chunk_size=DEFAULT_CHUNK_SIZE, cols=None, filtering_cols=None,
                       filtering_row=None, index=False, index_cols=None, is_mssql=DEFAULT_IS_MSSQL,
                       n=None, order='ASC', row_count=-1, schema=DEFAULT_SCHEMA, verbose=VERBOSE):
	'''Selects the specified columns of the rows matching the specified filtering row at the
	specified filtering columns from the specified table (in the specified schema) and returns them
	in a dataframe.'''
	if verbose:
		filtering_cols = include_list(get_keys(filtering_row), filtering_cols)
		debug('Select the columns', '*' if is_empty(cols) else format_cols(cols),
		      'from the table', quote(table),
		      paste('filtering on',
		            format_cols(filtering_cols)) if not is_empty(filtering_cols) else '')
	if index and is_null(index_cols):
		index_cols = get_primary_cols(engine, table)
	chunks = pd.read_sql(create_select_table_where_query(table, cols=cols,
	                                                     filtering_cols=filtering_cols,
	                                                     filtering_row=filtering_row,
	                                                     is_mssql=is_mssql, n=n, order=order,
	                                                     schema=schema),
	                     engine, chunksize=chunk_size, columns=cols, index_col=index_cols)
	if is_null(chunk_size):
		if row_count >= 0 and len(chunks) >= row_count:
			return chunks.head(row_count)
		return chunks
	df = to_frame([])
	for i, chunk in enumerate(chunks):
		debug_query('select', chunk_size, table, index_from=i * chunk_size + 1,
		            index_to=(i + 1) * chunk_size, verbose=verbose)
		df = concat_rows(df, chunk)
		if row_count >= 0 and len(df) >= row_count:
			return df.head(row_count)
	return df


# • DB DELETE ######################################################################################

__DB_DELETE_______________________________________ = ''


def create_delete_table_query(table, filtering_cols=None, filtering_row=None,
                              is_mssql=DEFAULT_IS_MSSQL, schema=DEFAULT_SCHEMA):
	'''Creates the query to delete the rows matching the specified filtering row at the specified
	filtering columns from the specified table (in the specified schema).'''
	return paste('DELETE FROM', get_full_table_name(table, schema=schema),
	             create_where_clause(filtering_cols=filtering_cols, filtering_row=filtering_row,
	                                 is_mssql=is_mssql)) + ';'


##################################################

def delete_table(engine, df, table, filtering_cols=None, index=False, is_mssql=DEFAULT_IS_MSSQL,
                 schema=DEFAULT_SCHEMA, test=ASSERT, verbose=VERBOSE):
	'''Deletes the rows matching the rows of the specified dataframe at the specified filtering
	columns from the specified table (in the specified schema) and returns the number of deleted
	rows.'''
	delete_count = 0

	# Include the index in the columns
	if index:
		df = df.reset_index()

	# Get the metadata of the table
	metadata = create_metadata(engine, schema=schema)
	table_cols = get_cols(engine, table, metadata=metadata, schema=schema)
	filtering_cols = get_filtering_cols(engine, df, table, filtering_cols=filtering_cols,
	                                    metadata=metadata, schema=schema, test=test,
	                                    use_only_primary=False)

	if test:
		# Test the existence of the columns in the table
		get_common_cols(df, table, table_cols, filtering_cols=filtering_cols, test=test)

	debug_query('delete', len(df), table, verbose=verbose)

	for index, row in df.iterrows():
		# Build the query
		query = create_delete_table_query(table, filtering_cols=filtering_cols, filtering_row=row,
		                                  is_mssql=is_mssql, schema=schema)

		# Execute the query
		try:
			result = execute(engine, query)
			result_count = len(result) if is_collection(result) else result
			if result_count > 0:
				delete_count += result_count
				trace_row('delete', index, table, cols=filtering_cols, row=row, verbose=verbose)
			else:
				warn_row('delete', index, table, cols=filtering_cols, row=row, verbose=verbose)
		except Exception as ex:
			error_row('delete', index, table, ex=ex, cols=filtering_cols, row=row, verbose=verbose)
		if (index + 1) % DEFAULT_DEBUG_INTERVAL == 0:
			debug_query('deleted', delete_count, table,
			            index_from=index + 1 - DEFAULT_DEBUG_INTERVAL + 1,
			            index_to=index + 1, verbose=verbose)
	return delete_count


def bulk_delete_table(engine, df, table, chunk_size=DEFAULT_CHUNK_SIZE, filtering_cols=None,
                      index=False, is_mssql=DEFAULT_IS_MSSQL, schema=DEFAULT_SCHEMA, test=ASSERT,
                      verbose=VERBOSE):
	'''Bulk-deletes the rows matching the rows of the specified dataframe at the specified filtering
	columns from the specified table (in the specified schema) and returns the number of
	bulk-deleted rows.'''
	delete_count = 0

	# Include the index in the columns
	if index:
		df = df.reset_index()

	# Get the metadata of the table
	metadata = create_metadata(engine, schema=schema)
	table_cols = get_cols(engine, table, metadata=metadata, schema=schema)
	filtering_cols = get_filtering_cols(engine, df, table, filtering_cols=filtering_cols,
	                                    metadata=metadata, schema=schema, test=test,
	                                    use_only_primary=False)

	if test:
		# Test the existence of the columns in the table
		get_common_cols(df, table, table_cols, filtering_cols=filtering_cols, test=test)

	# Chunk the bulk query
	if len(df) > chunk_size:
		chunk_count = ceil(len(df) / chunk_size)
		index_to = 0
		for i in range(chunk_count):
			index_from = index_to
			index_to = minimum(index_from + chunk_size, len(df))
			if verbose:
				debug('Chunk the bulk-delete query from', index_from + 1, 'to', index_to, 'rows')
			delete_count += bulk_delete_table(engine, df.iloc[index_from:index_to], table,
			                                  chunk_size=chunk_size,
			                                  filtering_cols=filtering_cols, index=False,
			                                  is_mssql=is_mssql, schema=schema, test=False,
			                                  verbose=verbose)
		return delete_count

	debug_query('bulk-delete', len(df), table, verbose=verbose)

	# Build the bulk query
	query = ''
	for index, row in df.iterrows():
		query += create_delete_table_query(table, filtering_cols=filtering_cols, filtering_row=row,
		                                   is_mssql=is_mssql, schema=schema)

	# Execute the bulk query
	try:
		result = execute(engine, query)
		result_count = len(result) if is_collection(result) else result
		if result_count > 0:
			delete_count = len(df)
		else:
			warn_query('bulk-deleted', table, verbose=verbose)
	except Exception as ex:
		error_query('bulk-deleted', table, ex=ex, verbose=verbose)
	return delete_count


# • DB INSERT ######################################################################################

__DB_INSERT_______________________________________ = ''


def set_id_insert(engine, table, flag, is_mssql=DEFAULT_IS_MSSQL, schema=DEFAULT_SCHEMA):
	'''Allows the insertion of identifiers into the specified table (in the specified schema).'''
	if is_mssql:
		return execute(engine, paste('SET IDENTITY_INSERT',
		                             get_full_table_name(table, schema=schema),
		                             flag) + ';')


##################################################

def create_insert_table_query(table, cols, row, is_mssql=DEFAULT_IS_MSSQL, schema=DEFAULT_SCHEMA):
	'''Creates the query to insert the specified row with the specified columns into the specified
	table (in the specified schema).'''
	return paste('INSERT INTO', get_full_table_name(table, schema=schema),
	             par(format_cols(cols)),
	             'VALUES', par(collist([format(row[col], is_mssql=is_mssql) for col in cols]))) + ';'


##################################################

def insert_table(engine, df, table, index=False, insert_id=None, is_mssql=DEFAULT_IS_MSSQL,
                 schema=DEFAULT_SCHEMA, test=ASSERT, verbose=VERBOSE):
	'''Inserts the rows of the specified dataframe into the specified table (in the specified
	schema) and returns the number of inserted rows.'''
	insert_count = 0

	# Include the index in the columns
	if index:
		df = df.reset_index()

	# Get the metadata of the table
	metadata = create_metadata(engine, schema=schema)
	table_cols = get_cols(engine, table, metadata=metadata, schema=schema)
	primary_cols = get_primary_cols(engine, table, metadata=metadata, schema=schema)

	# Get the columns to insert
	cols = get_common_cols(df, table, table_cols, test=test)
	if is_null(insert_id):
		insert_id = not is_empty(include_list(cols, get_identity_cols(engine, table,
		                                                              is_mssql=is_mssql)))

	debug_query('insert', len(df), table, verbose=verbose)

	if insert_id:
		set_id_insert(engine, table, 'ON', is_mssql=is_mssql, schema=schema)
	for index, row in df.iterrows():
		# Build the query
		query = create_insert_table_query(table, cols, row, is_mssql=is_mssql, schema=schema)

		# Execute the query
		try:
			result = execute(engine, query)
			result_count = len(result) if is_collection(result) else result
			if result_count > 0:
				insert_count += result_count
				trace_row('insert', index, table, cols=primary_cols, row=row, verbose=verbose)
			else:
				warn_row('insert', index, table, cols=primary_cols, row=row, verbose=verbose)
		except Exception as ex:
			error_row('insert', index, table, ex=ex, cols=primary_cols, row=row, verbose=verbose)
		if (index + 1) % DEFAULT_DEBUG_INTERVAL == 0:
			debug_query('inserted', insert_count, table,
			            index_from=index + 1 - DEFAULT_DEBUG_INTERVAL + 1,
			            index_to=index + 1, verbose=verbose)
	if insert_id:
		set_id_insert(engine, table, 'OFF', is_mssql=is_mssql, schema=schema)
	return insert_count


def bulk_insert_table(engine, df, table, chunk_size=DEFAULT_CHUNK_SIZE, index=False, insert_id=None,
                      is_mssql=DEFAULT_IS_MSSQL, schema=DEFAULT_SCHEMA, test=ASSERT,
                      verbose=VERBOSE):
	'''Bulk-inserts the rows of the specified dataframe into the specified table (in the specified
	schema) and returns the number of bulk-inserted rows.'''
	insert_count = 0

	# Include the index in the columns
	if index:
		df = df.reset_index()

	# Get the metadata of the table
	table_cols = get_cols(engine, table, schema=schema)

	# Get the columns to insert
	cols = get_common_cols(df, table, table_cols, test=test)
	if is_null(insert_id):
		insert_id = not is_empty(include_list(cols, get_identity_cols(engine, table,
		                                                              is_mssql=is_mssql)))

	# Chunk the bulk query
	if len(df) > chunk_size:
		if insert_id:
			set_id_insert(engine, table, 'ON', is_mssql=is_mssql, schema=schema)
		chunk_count = ceil(len(df) / chunk_size)
		index_to = 0
		for i in range(chunk_count):
			index_from = index_to
			index_to = minimum(index_from + chunk_size, len(df))
			if verbose:
				debug('Chunk the bulk-insert query from', index_from + 1, 'to', index_to, 'rows')
			insert_count += bulk_insert_table(engine, df.iloc[index_from:index_to], table,
			                                  chunk_size=chunk_size, index=False, insert_id=False,
			                                  is_mssql=is_mssql, schema=schema, test=False,
			                                  verbose=verbose)
		if insert_id:
			set_id_insert(engine, table, 'OFF', is_mssql=is_mssql, schema=schema)
		return insert_count

	debug_query('bulk-insert', len(df), table, verbose=verbose)

	# Build the bulk query
	query = ''
	for index, row in df.iterrows():
		query += create_insert_table_query(table, cols, row, is_mssql=is_mssql, schema=schema)

	# Execute the bulk query
	if insert_id:
		set_id_insert(engine, table, 'ON', is_mssql=is_mssql, schema=schema)
	try:
		result = execute(engine, query)
		result_count = len(result) if is_collection(result) else result
		if result_count > 0:
			insert_count = len(df)
		else:
			warn_query('bulk-inserted', table, verbose=verbose)
	except Exception as ex:
		error_query('bulk-inserted', table, ex=ex, verbose=verbose)
	if insert_id:
		set_id_insert(engine, table, 'OFF', is_mssql=is_mssql, schema=schema)
	return insert_count


# • DB UPDATE ######################################################################################

__DB_UPDATE_______________________________________ = ''


def create_update_table_query(table, cols, row, filtering_cols=None, is_mssql=DEFAULT_IS_MSSQL,
                              schema=DEFAULT_SCHEMA):
	'''Creates the query to update the rows matching the rows of the specified dataframe at the
	specified filtering columns of the specified table (in the specified schema).'''
	return paste('UPDATE', get_full_table_name(table, schema=schema),
	             'SET', collist([collapse(format_name(col), '=',
	                                      format(row[col], is_mssql=is_mssql)) for col in cols]),
	             create_where_clause(filtering_cols=filtering_cols, filtering_row=row,
	                                 is_mssql=is_mssql)) + ';'


##################################################

def update_table(engine, df, table, filtering_cols=None, index=False, is_mssql=DEFAULT_IS_MSSQL,
                 schema=DEFAULT_SCHEMA, test=ASSERT, verbose=VERBOSE):
	'''Updates the rows matching the rows of the specified dataframe at the specified filtering
	columns of the specified table (in the specified schema) and returns the number of updated
	rows.'''
	update_count = 0

	# Include the index in the columns
	if index:
		df = df.reset_index()

	# Get the metadata of the table
	metadata = create_metadata(engine, schema=schema)
	table_cols = get_cols(engine, table, metadata=metadata, schema=schema)
	filtering_cols = get_filtering_cols(engine, df, table, filtering_cols=filtering_cols,
	                                    metadata=metadata, schema=schema, test=test)

	# Get the columns to update
	cols = get_common_cols(df, table, table_cols, filtering_cols=filtering_cols, test=test)
	if is_empty(cols):
		warn('The dataframe contains only the filtering columns', par(filtering_cols),
		     'or no column of the table', quote(table))
		return 0

	debug_query('update', len(df), table, verbose=verbose)

	for index, row in df.iterrows():
		# Build the query
		query = create_update_table_query(table, cols, row, filtering_cols=filtering_cols,
		                                  is_mssql=is_mssql, schema=schema)

		# Execute the query
		try:
			result = execute(engine, query)
			result_count = len(result) if is_collection(result) else result
			if result_count > 0:
				update_count += result_count
				trace_row('update', index, table, cols=filtering_cols, row=row, verbose=verbose)
			else:
				warn_row('update', index, table, cols=filtering_cols, row=row, verbose=verbose)
		except Exception as ex:
			error_row('update', index, table, ex=ex, cols=filtering_cols, row=row, verbose=verbose)
		if (index + 1) % DEFAULT_DEBUG_INTERVAL == 0:
			debug_query('updated', update_count, table,
			            index_from=index + 1 - DEFAULT_DEBUG_INTERVAL + 1,
			            index_to=index + 1, verbose=verbose)
	return update_count


def bulk_update_table(engine, df, table, chunk_size=DEFAULT_CHUNK_SIZE, filtering_cols=None,
                      index=False, is_mssql=DEFAULT_IS_MSSQL, schema=DEFAULT_SCHEMA, test=ASSERT,
                      verbose=VERBOSE):
	'''Bulk-updates the rows matching the rows of the specified dataframe at the specified filtering
	columns of the specified table (in the specified schema) and returns the number of bulk-updated
	rows.'''
	update_count = 0

	# Include the index in the columns
	if index:
		df = df.reset_index()

	# Get the metadata of the table
	metadata = create_metadata(engine, schema=schema)
	table_cols = get_cols(engine, table, metadata=metadata, schema=schema)
	filtering_cols = get_filtering_cols(engine, df, table, filtering_cols=filtering_cols,
	                                    metadata=metadata, schema=schema, test=test)

	# Get the columns to update
	cols = get_common_cols(df, table, table_cols, filtering_cols=filtering_cols, test=test)

	# Chunk the bulk query
	if len(df) > chunk_size:
		chunk_count = ceil(len(df) / chunk_size)
		index_to = 0
		for i in range(chunk_count):
			index_from = index_to
			index_to = minimum(index_from + chunk_size, len(df))
			if verbose:
				debug('Chunk the bulk-update query from', index_from + 1, 'to', index_to, 'rows')
			update_count += bulk_update_table(engine, df.iloc[index_from:index_to], table,
			                                  chunk_size=chunk_size,
			                                  filtering_cols=filtering_cols, index=False,
			                                  is_mssql=is_mssql, schema=schema, test=False,
			                                  verbose=verbose)
		return update_count

	debug_query('bulk-update', len(df), table, verbose=verbose)

	# Build the bulk query
	query = ''
	for index, row in df.iterrows():
		query += create_update_table_query(table, cols, row, filtering_cols=filtering_cols,
		                                   is_mssql=is_mssql, schema=schema)

	# Execute the bulk query
	try:
		result = execute(engine, query)
		result_count = len(result) if is_collection(result) else result
		if result_count > 0:
			update_count = len(df)
		else:
			warn_query('bulk-updated', table, verbose=verbose)
	except Exception as ex:
		error_query('bulk-updated', table, ex=ex, verbose=verbose)
	return update_count


# • DB UPSERT ######################################################################################

__DB_UPSERT_______________________________________ = ''


def upsert_table(engine, df, table, filtering_cols=None, index=False, is_mssql=DEFAULT_IS_MSSQL,
                 schema=DEFAULT_SCHEMA, test=ASSERT, verbose=VERBOSE):
	'''Updates/inserts the rows matching the rows of the specified dataframe at the specified
	filtering columns of/into the specified table (in the specified schema) and returns the number
	of updated/inserted rows.'''
	upsert_count = 0

	# Include the index in the columns
	if index:
		df = df.reset_index()

	# Update the matching rows
	update_count = update_table(engine, df, table, filtering_cols=filtering_cols, index=False,
	                            is_mssql=is_mssql, schema=schema, test=test, verbose=False)
	upsert_count += update_count
	if update_count > 0:
		debug_query('update', upsert_count, table, verbose=verbose)

	# Insert the non-matching rows
	if update_count != len(df):
		insert_count = insert_table(engine, df, table, index=False, is_mssql=is_mssql,
		                            schema=schema, test=test, verbose=False)
		upsert_count += insert_count
		if insert_count > 0:
			debug_query('insert', insert_count, table, verbose=verbose)
	else:
		insert_count = 0

	# Verify
	if upsert_count != len(df):
		if verbose:
			t = select_table_where(engine, table, chunk_size=None, cols=get_names(df), index=False,
			                       is_mssql=is_mssql, schema=schema, verbose=verbose)
			for index, row in df.iterrows():
				if is_empty(filter_rows(t, row)):
					warn_row('update/insert', index, table, cols=filtering_cols, row=row,
					         verbose=verbose)
		if upsert_count == 0:
			warn_query('update/insert', table, verbose=verbose)
		elif upsert_count < len(df):
			warn('Update/insert', collapse(update_count, '/', insert_count), 'rows in the table',
			     quote(table), 'which is', len(df) - upsert_count, 'rows less than expected',
			     par(len(df)))
		elif upsert_count > len(df):
			warn('Update/insert', collapse(update_count, '/', insert_count), 'rows in the table',
			     quote(table), 'which is', upsert_count - len(df), 'rows more than expected',
			     par(len(df)))
	return upsert_count


# • DB MIGRATE #####################################################################################

__DB_MIGRATE______________________________________ = ''


def migrate(engine_from, engine_to, tables, chunk_size=DEFAULT_CHUNK_SIZE, collation=None,
            create=True, drop=False, fill=True, filtering_cols=None, filtering_row=None,
            is_mssql_from=DEFAULT_IS_MSSQL, is_mssql_to=DEFAULT_IS_MSSQL, schema=DEFAULT_SCHEMA,
            test=ASSERT, upsert=False, verbose=VERBOSE):
	'''Migrates the specified tables (in the specified schema) from the specified engine to the
	specified engine using the specified collation and returns the number of migrated rows.'''
	count = 0

	# Create the tables
	if drop or create:
		metadata = create_metadata(engine_from, schema=schema)
		for table in tables:
			debug('Recreate' if drop and create else 'Drop' if drop else 'Create', 'the table',
			      quote(table))
			table_metadata = get_table_metadata(engine_from, table, metadata=metadata,
			                                    schema=schema)
			for col in table_metadata.columns:
				update_col(col, collation=collation, is_mssql_from=is_mssql_from,
				           is_mssql_to=is_mssql_to)
		if is_mssql_from and not is_mssql_to:
			metadata_to_lowercase(metadata)
		if drop:
			metadata.drop_all(engine_to, checkfirst=True)
		if create:
			metadata.create_all(engine_to)

	# Fill the tables
	if fill:
		for table in tables:
			debug('Fill the table', quote(table))
			if is_null(filtering_row):
				df = select_table(engine_from, table, chunk_size=chunk_size, schema=schema,
				                  verbose=verbose)
			else:
				df = select_table_where(engine_from, table, chunk_size=chunk_size,
				                        filtering_cols=filtering_cols, filtering_row=filtering_row,
				                        is_mssql=is_mssql_from, schema=schema, verbose=verbose)
			if is_mssql_from and not is_mssql_to:
				table = table.lower()
				set_names(df, map(str.lower, get_names(df)))
			if upsert:
				count += upsert_table(engine_to, df, table, is_mssql=is_mssql_to, schema=schema,
				                      test=test, verbose=verbose)
			else:
				count += bulk_insert_table(engine_to, df, table, chunk_size=chunk_size,
				                           is_mssql=is_mssql_to, schema=schema, test=test,
				                           verbose=verbose)
	return count


#########################

def update_col(col, collation=None, is_mssql_from=DEFAULT_IS_MSSQL, is_mssql_to=DEFAULT_IS_MSSQL):
	'''Updates the default value, type and collation of the specified column.'''
	# - Update the default value
	update_col_default(col, is_mssql_from=is_mssql_from, is_mssql_to=is_mssql_to)
	# - Update the type
	update_col_type(col, is_mssql_from=is_mssql_from, is_mssql_to=is_mssql_to)
	# - Update the collation
	update_col_collation(col, collation=collation)


def update_col_default(col, is_mssql_from=DEFAULT_IS_MSSQL, is_mssql_to=DEFAULT_IS_MSSQL):
	'''Updates the default value of the specified column.'''
	if hasattr(col.server_default, 'arg') and isinstance(col.server_default.arg, TextClause):
		if is_mssql_from and not is_mssql_to:
			if isinstance(col.type, mssql.base.BIT):
				col.server_default.arg.text = (col.server_default.arg.text.replace('0', 'FALSE')
				                               .replace('1', 'TRUE'))
			elif (isinstance(col.type, mssql.base.DATE) or
			      isinstance(col.type, mssql.base.DATETIME) or
			      isinstance(col.type, mssql.base.DATETIMEOFFSET) or
			      isinstance(col.type, mssql.base.SMALLDATETIME) or
			      isinstance(col.type, mssql.base.TIME) or
			      isinstance(col.type, mssql.base.TIMESTAMP)):
				col.server_default.arg.text = col.server_default.arg.text.replace('getdate', 'now')
		elif not is_mssql_from and is_mssql_to:
			if isinstance(col.type, db.BOOLEAN):
				col.server_default.arg.text = (col.server_default.arg.text.replace('FALSE', '0')
				                               .replace('TRUE', '1'))
			elif (isinstance(col.type, db.DATE) or
			      isinstance(col.type, db.DATETIME) or
			      isinstance(col.type, db.TIMESTAMP)):
				col.server_default.arg.text = col.server_default.arg.text.replace('now', 'getdate')


def update_col_type(col, is_mssql_from=DEFAULT_IS_MSSQL, is_mssql_to=DEFAULT_IS_MSSQL):
	'''Updates the type of the specified column.'''
	if is_mssql_from and not is_mssql_to:
		if isinstance(col.type, mssql.base.BIT):
			col.type = db.BOOLEAN()
		elif (isinstance(col.type, mssql.base.DATETIME) or
		      isinstance(col.type, mssql.base.SMALLDATETIME) or
		      isinstance(col.type, mssql.base.TIMESTAMP)):
			col.type = db.TIMESTAMP()
	elif not is_mssql_from and is_mssql_to:
		if isinstance(col.type, db.BOOLEAN):
			col.type = mssql.base.BIT()
		elif isinstance(col.type, db.TIMESTAMP):
			col.type = mssql.base.DATETIME()


def update_col_collation(col, collation=None):
	'''Updates the collation of the specified column.'''
	if not is_null(collation) and hasattr(col.type, 'collation'):
		col.type.collation = collation
