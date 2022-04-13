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
from sqlalchemy.dialects import *
from sqlalchemy.exc import *
from sqlalchemy.orm import *
from sqlalchemy.sql.elements import *

from nutil.common import *

####################################################################################################
# DB CONSTANTS
####################################################################################################

__DB_CONSTANTS____________________________________ = ''

# The default DB
if not exists('DEFAULT_DB'):
	DEFAULT_DB = 'mssql'  # Microsoft SQL Server

# The default flag specifying whether the DB is Microsoft SQL Server
DEFAULT_DB_MSSQL = DEFAULT_DB == 'mssql'

# The default schema
if not exists('DEFAULT_SCHEMA'):
	DEFAULT_SCHEMA = 'dbo'

#########################

# The default chunk size
if not exists('DEFAULT_CHUNK_SIZE'):
	DEFAULT_CHUNK_SIZE = 1000

#########################

# The default debug frequency
if not exists('DEFAULT_DEBUG_FREQUENCY'):
	DEFAULT_DEBUG_FREQUENCY = 1000

####################################################################################################
# DB FUNCTIONS
####################################################################################################

__DB______________________________________________ = ''

# • DB CONNECT #####################################################################################

__DB_CONNECT______________________________________ = ''


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
			prefix += 'from ' + str(index_from) + ' '
		if not is_null(index_to):
			prefix += 'to ' + str(index_to) + ' '
		debug((prefix + get_query_message(verb, count, table)).capitalize())


def error_query(verb, table, ex=None, verbose=VERBOSE):
	if not isinstance(ex, IntegrityError):
		error(paste('No row has been', verb, 'in the table', quote(table)),
		      par(ex) if not is_null(ex) else '')
	elif verbose:
		warn(paste('No row has been', verb, 'in the table', quote(table)),
		     par(get_full_class_name(ex)) if not is_null(ex) else '')
		if not is_null(ex):
			trace(ex)


##################################################

def get_row_message(verb, index, table, cols=None, row=None):
	return paste(verb, 'the row', index + 1,
	             par(get_items(row, inclusion=cols)) if not is_null(row) else '',
	             'in the table', quote(table))


#########################

def trace_row(verb, index, table, cols=None, row=None, verbose=VERBOSE):
	if verbose:
		trace('-', get_row_message(verb, index, table, cols=cols, row=row).capitalize())


def error_row(verb, index, table, ex=None, cols=None, row=None, verbose=VERBOSE):
	if not is_null(ex) and not isinstance(ex, IntegrityError):
		error(paste('- Fail to', get_row_message(verb, index, table, cols=cols, row=row)),
		      par(ex) if not is_null(ex) else '')
	elif verbose:
		warn(paste('- Fail to', get_row_message(verb, index, table, cols=cols, row=row)),
		     par(get_full_class_name(ex)) if not is_null(ex) else '')
		if not is_null(ex):
			trace(ex)


# • DB FORMAT ######################################################################################

__DB_FORMAT_______________________________________ = ''


def create_where_clause(filtering_cols=None, filtering_row=None, mssql=DEFAULT_DB_MSSQL):
	'''Creates the WHERE clause with the specified filtering columns and row.'''
	cols = include_list(get_keys(filtering_row), filtering_cols)
	if is_empty(cols):
		return ''
	return paste('WHERE',
	             collapse([collapse(format_name(col),
	                                ' IS ' if filtering_row[col] is None
	                                else ' IN ' if is_collection(filtering_row[col])
	                                else '=',
	                                format(filtering_row[col], mssql=mssql)) for col in cols],
	                      delimiter=' AND '))


##################################################

def escape(name):
	'''Escapes the specified name (for either MSSQL or PostgreSQL).'''
	return name.replace('\'', '\'\'').replace('%', '%%')


#########################

def format_name(name):
	'''Formats the specified name (for either MSSQL or PostgreSQL).'''
	if '(' in name and ')' in name:
		return name
	return dquote(name)


def format_cols(*cols):
	'''Formats the specified column names (for either MSSQL or PostgreSQL).'''
	cols = remove_empty(to_list(*cols))
	return collist([format_name(col) for col in cols])


def format(value, mssql=DEFAULT_DB_MSSQL):
	'''Formats the specified value (for either MSSQL or PostgreSQL).'''
	if is_null(value):
		return 'NULL'
	elif is_collection(value):
		return par(collist(apply(value, format, mssql=mssql)))
	elif is_bool(value):
		if mssql:
			return 1 if value else 0
		return value
	elif is_number(value):
		if is_nan(value):
			return 'NULL'
		return value
	elif is_timestamp(value):
		return quote(str(value)[:-3])
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
		# Test the existence of the filtering columns in the dataframe
		if not is_empty(filtering_cols):
			for col in filtering_cols:
				if col not in df:
					warn('The filtering column', quote(col), 'does not exist in the dataframe')
	return filter_list(df, inclusion=table_cols, exclusion=filtering_cols)


def get_identity_cols(engine, table, mssql=DEFAULT_DB_MSSQL, verbose=False):
	'''Returns the identity columns of the specified table.'''
	if mssql:
		return select_table_where(engine, 'identity_columns', cols=['name'],
		                          filtering_cols='OBJECT_NAME(object_id)',
		                          filtering_row={'OBJECT_NAME(object_id)': table}, schema='sys',
		                          verbose=verbose)['name']
	return []


def get_primary_cols(engine, table, cols=None, metadata=None, schema=DEFAULT_SCHEMA):
	'''Returns the primary columns of the specified table.'''
	table_metadata = get_table_metadata(engine, table, metadata=metadata, schema=schema)
	primary_cols = [col.name for col in table_metadata.primary_key.columns]
	return include_list(primary_cols, cols)


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


# • DB SELECT ######################################################################################

__DB_SELECT_______________________________________ = ''


def create_select_table_where_query(table, cols=None, filtering_cols=None, filtering_row=None,
                                    mssql=DEFAULT_DB_MSSQL, n=None, order='ASC',
                                    schema=DEFAULT_SCHEMA):
	'''Creates the query to select the specified columns of the rows matching the specified
	filtering row at the specified filtering columns from the specified table (in the specified
	schema).'''
	return paste('SELECT', paste('TOP', n) if not is_null(n) and mssql else '',
	             '*' if is_empty(cols) else format_cols(cols),
	             'FROM', get_full_table_name(table, schema=schema),
	             create_where_clause(filtering_cols=filtering_cols, filtering_row=filtering_row,
	                                 mssql=mssql),
	             paste('ORDER BY', format_cols(cols), order) if not is_empty(cols) else '',
	             paste('LIMIT', n) if not is_null(n) and not mssql else '') + ';'


##################################################

def select_query(engine, query):
	'''Returns the dataframe read from the specified query.'''
	return pd.read_sql(query, con=engine)


def select_table(engine, table, cols=None, index_cols=None, schema=DEFAULT_SCHEMA, verbose=VERBOSE):
	'''Returns the dataframe read from the specified table (in the specified schema).'''
	if verbose:
		debug('Select the table', quote(table))
	return pd.read_sql_table(table, con=engine, columns=cols, index_col=index_cols, schema=schema)


def select_table_where(engine, table, cols=None, filtering_cols=None, filtering_row=None,
                       index_cols=None, mssql=DEFAULT_DB_MSSQL, n=None, order='ASC',
                       schema=DEFAULT_SCHEMA, verbose=VERBOSE):
	'''Selects the specified columns of the rows matching the specified filtering row at the
	specified filtering columns from the specified table (in the specified schema) and returns them
	in a dataframe.'''
	if verbose:
		filtering_cols = include_list(get_keys(filtering_row), filtering_cols)
		debug('Select the columns', '*' if is_empty(cols) else format_cols(cols),
		      'from the table', quote(table),
		      paste('filtering on',
		            format_cols(filtering_cols)) if not is_empty(filtering_cols) else '')
	return pd.read_sql(create_select_table_where_query(table, cols=cols,
	                                                   filtering_cols=filtering_cols,
	                                                   filtering_row=filtering_row, mssql=mssql,
	                                                   n=n, order=order, schema=schema),
	                   con=engine, columns=cols, index_col=index_cols)


# • DB DELETE ######################################################################################

__DB_DELETE_______________________________________ = ''


def create_delete_table_query(table, filtering_cols=None, filtering_row=None,
                              mssql=DEFAULT_DB_MSSQL, schema=DEFAULT_SCHEMA):
	'''Creates the query to delete the rows matching the specified filtering row at the specified
	filtering columns from the specified table (in the specified schema).'''
	return paste('DELETE FROM', get_full_table_name(table, schema=schema),
	             create_where_clause(filtering_cols=filtering_cols, filtering_row=filtering_row,
	                                 mssql=mssql)) + ';'


##################################################

def delete_table(engine, df, table, filtering_cols=None, mssql=DEFAULT_DB_MSSQL,
                 schema=DEFAULT_SCHEMA, test=ASSERT, verbose=VERBOSE):
	'''Deletes the rows matching the rows of the specified dataframe at the specified filtering
	columns from the specified table (in the specified schema) and returns the number of deleted
	rows.'''
	delete_count = 0

	# Get the metadata of the table
	table_metadata = get_table_metadata(engine, table, schema=schema)
	if is_null(filtering_cols):
		filtering_cols = [col.name for col in table_metadata.primary_key.columns]
	else:
		filtering_cols = include(df, filtering_cols)
	table_cols = [col.name for col in table_metadata.columns]

	# Test the existence of the columns
	get_common_cols(df, table, table_cols, filtering_cols=filtering_cols, test=test)

	debug_query('delete', len(df), table, verbose=verbose)

	for index, row in df.iterrows():
		# Build the query
		query = create_delete_table_query(table, filtering_cols=filtering_cols, filtering_row=row,
		                                  mssql=mssql, schema=schema)

		if index > 0 and index % DEFAULT_DEBUG_FREQUENCY == 0:
			debug_query('delete', delete_count, table, index_from=index - DEFAULT_DEBUG_FREQUENCY,
			            index_to=index, verbose=verbose)

		# Execute the query
		try:
			result = execute(engine, query)
			result_count = len(result) if is_collection(result) else result
			if result_count > 0:
				delete_count += result_count
				trace_row('delete', index, table, cols=filtering_cols, row=row, verbose=verbose)
			else:
				error_row('delete', index, table, cols=filtering_cols, row=row, verbose=verbose)
		except Exception as ex:
			error_row('delete', index, table, ex=ex, cols=filtering_cols, row=row, verbose=verbose)
	return delete_count


def bulk_delete_table(engine, df, table, chunk_size=DEFAULT_CHUNK_SIZE, filtering_cols=None,
                      mssql=DEFAULT_DB_MSSQL, schema=DEFAULT_SCHEMA, test=ASSERT,
                      verbose=VERBOSE):
	'''Bulk-deletes the rows matching the rows of the specified dataframe at the specified filtering
	columns from the specified table (in the specified schema) and returns the number of
	bulk-deleted rows.'''
	delete_count = 0

	# Get the metadata of the table
	table_metadata = get_table_metadata(engine, table, schema=schema)
	if is_null(filtering_cols):
		filtering_cols = [col.name for col in table_metadata.primary_key.columns]
	else:
		filtering_cols = include(df, filtering_cols)
	table_cols = [col.name for col in table_metadata.columns]

	# Test the existence of the columns
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
			delete_count += bulk_delete_table(engine, df[index_from:index_to], table,
			                                  chunk_size=chunk_size, filtering_cols=filtering_cols,
			                                  mssql=mssql, schema=schema, test=False,
			                                  verbose=verbose)
		return delete_count

	debug_query('bulk-delete', len(df), table, verbose=verbose)

	# Build the bulk query
	query = ''
	for index, row in df.iterrows():
		query += create_delete_table_query(table, filtering_cols=filtering_cols, filtering_row=row,
		                                   mssql=mssql, schema=schema)

	# Execute the bulk query
	try:
		result = execute(engine, query, multi=True)
		result_count = len(result) if is_collection(result) else result
		if result_count > 0:
			delete_count = len(df)
		else:
			error_query('bulk-deleted', table, verbose=verbose)
	except Exception as ex:
		error_query('bulk-deleted', table, ex=ex, verbose=verbose)
	return delete_count


# • DB INSERT ######################################################################################

__DB_INSERT_______________________________________ = ''


def set_id_insert(engine, table, flag, mssql=DEFAULT_DB_MSSQL, schema=DEFAULT_SCHEMA):
	'''Allows the insertion of identifiers into the specified table (in the specified schema).'''
	if mssql:
		return execute(engine, paste('SET IDENTITY_INSERT',
		                             get_full_table_name(table, schema=schema),
		                             flag) + ';')


##################################################

def create_insert_table_query(table, cols, row, mssql=DEFAULT_DB_MSSQL, schema=DEFAULT_SCHEMA):
	'''Creates the query to insert the specified row with the specified columns into the specified
	table (in the specified schema).'''
	return paste('INSERT INTO', get_full_table_name(table, schema=schema),
	             par(format_cols(cols)),
	             'VALUES', par(collist([format(row[col], mssql=mssql) for col in cols]))) + ';'


##################################################

def insert_table(engine, df, table, insert_id=None, mssql=DEFAULT_DB_MSSQL, schema=DEFAULT_SCHEMA,
                 test=ASSERT, verbose=VERBOSE):
	'''Inserts the rows of the specified dataframe into the specified table (in the specified
	schema) and returns the number of inserted rows.'''
	insert_count = 0

	# Get the metadata of the table
	metadata = create_metadata(engine, schema=schema)
	table_metadata = get_table_metadata(engine, table, metadata=metadata, schema=schema)
	primary_cols = get_primary_cols(engine, table, metadata=metadata, schema=schema)
	table_cols = [col.name for col in table_metadata.columns]

	# Get the columns to insert
	cols = get_common_cols(df, table, table_cols, test=test)
	if is_null(insert_id):
		insert_id = not is_empty(include_list(cols, get_identity_cols(engine, table, mssql=mssql)))

	debug_query('insert', len(df), table, verbose=verbose)

	if insert_id:
		set_id_insert(engine, table, 'ON', mssql=mssql, schema=schema)
	for index, row in df.iterrows():
		# Build the query
		query = create_insert_table_query(table, cols, row, mssql=mssql, schema=schema)

		if index > 0 and index % DEFAULT_DEBUG_FREQUENCY == 0:
			debug_query('insert', insert_count, table, index_from=index - DEFAULT_DEBUG_FREQUENCY,
			            index_to=index, verbose=verbose)

		# Execute the query
		try:
			result = execute(engine, query)
			result_count = len(result) if is_collection(result) else result
			if result_count > 0:
				insert_count += result_count
				trace_row('insert', index, table, cols=primary_cols, row=row, verbose=verbose)
			else:
				error_row('insert', index, table, cols=primary_cols, row=row, verbose=verbose)
		except Exception as ex:
			error_row('insert', index, table, ex=ex, cols=primary_cols, row=row, verbose=verbose)
	if insert_id:
		set_id_insert(engine, table, 'OFF', mssql=mssql, schema=schema)
	return insert_count


def bulk_insert_table(engine, df, table, chunk_size=DEFAULT_CHUNK_SIZE, insert_id=None,
                      mssql=DEFAULT_DB_MSSQL, schema=DEFAULT_SCHEMA, test=ASSERT,
                      verbose=VERBOSE):
	'''Bulk-inserts the rows of the specified dataframe into the specified table (in the specified
	schema) and returns the number of bulk-inserted rows.'''
	insert_count = 0

	# Get the metadata of the table
	table_metadata = get_table_metadata(engine, table, schema=schema)
	table_cols = [col.name for col in table_metadata.columns]

	# Get the columns to insert
	cols = get_common_cols(df, table, table_cols, test=test)
	if is_null(insert_id):
		insert_id = not is_empty(include_list(cols, get_identity_cols(engine, table, mssql=mssql)))

	# Chunk the bulk query
	if len(df) > chunk_size:
		if insert_id:
			set_id_insert(engine, table, 'ON', mssql=mssql, schema=schema)
		chunk_count = ceil(len(df) / chunk_size)
		index_to = 0
		for i in range(chunk_count):
			index_from = index_to
			index_to = minimum(index_from + chunk_size, len(df))
			if verbose:
				debug('Chunk the bulk-insert query from', index_from + 1, 'to', index_to, 'rows')
			insert_count += bulk_insert_table(engine, df[index_from:index_to], table,
			                                  chunk_size=chunk_size, insert_id=False, mssql=mssql,
			                                  schema=schema, test=False, verbose=verbose)
		if insert_id:
			set_id_insert(engine, table, 'OFF', mssql=mssql, schema=schema)
		return insert_count

	debug_query('bulk-insert', len(df), table, verbose=verbose)

	# Build the bulk query
	query = ''
	for index, row in df.iterrows():
		query += create_insert_table_query(table, cols, row, mssql=mssql, schema=schema)

	# Execute the bulk query
	if insert_id:
		set_id_insert(engine, table, 'ON', mssql=mssql, schema=schema)
	try:
		result = execute(engine, query, multi=True)
		result_count = len(result) if is_collection(result) else result
		if result_count > 0:
			insert_count = len(df)
		else:
			error_query('bulk-inserted', table, verbose=verbose)
	except Exception as ex:
		error_query('bulk-inserted', table, ex=ex, verbose=verbose)
	if insert_id:
		set_id_insert(engine, table, 'OFF', mssql=mssql, schema=schema)
	return insert_count


# • DB UPDATE ######################################################################################

__DB_UPDATE_______________________________________ = ''


def create_update_table_query(table, cols, row, filtering_cols=None, mssql=DEFAULT_DB_MSSQL,
                              schema=DEFAULT_SCHEMA):
	'''Creates the query to update the rows matching the rows of the specified dataframe at the
	specified filtering columns of the specified table (in the specified schema).'''
	return paste('UPDATE', get_full_table_name(table, schema=schema),
	             'SET', collist([collapse(format_name(col), '=',
	                                      format(row[col], mssql=mssql)) for col in cols]),
	             create_where_clause(filtering_cols=filtering_cols, filtering_row=row,
	                                 mssql=mssql)) + ';'


##################################################

def update_table(engine, df, table, filtering_cols=None, mssql=DEFAULT_DB_MSSQL,
                 schema=DEFAULT_SCHEMA, test=ASSERT, verbose=VERBOSE):
	'''Updates the rows matching the rows of the specified dataframe at the specified filtering
	columns of the specified table (in the specified schema) and returns the number of updated
	rows.'''
	update_count = 0

	# Get the metadata of the table
	metadata = create_metadata(engine, schema=schema)
	table_metadata = get_table_metadata(engine, table, metadata=metadata, schema=schema)
	if is_null(filtering_cols):
		filtering_cols = get_primary_cols(engine, table, metadata=metadata, schema=schema)
	else:
		filtering_cols = include(df, filtering_cols)
	table_cols = [col.name for col in table_metadata.columns]

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
		                                  mssql=mssql, schema=schema)

		if index > 0 and index % DEFAULT_DEBUG_FREQUENCY == 0:
			debug_query('update', update_count, table, index_from=index - DEFAULT_DEBUG_FREQUENCY,
			            index_to=index, verbose=verbose)

		# Execute the query
		try:
			result = execute(engine, query)
			result_count = len(result) if is_collection(result) else result
			if result_count > 0:
				update_count += result_count
				trace_row('update', index, table, cols=filtering_cols, row=row, verbose=verbose)
			else:
				error_row('update', index, table, cols=filtering_cols, row=row, verbose=verbose)
		except Exception as ex:
			error_row('update', index, table, ex=ex, cols=filtering_cols, row=row, verbose=verbose)
	return update_count


def bulk_update_table(engine, df, table, chunk_size=DEFAULT_CHUNK_SIZE, filtering_cols=None,
                      mssql=DEFAULT_DB_MSSQL, schema=DEFAULT_SCHEMA, test=ASSERT,
                      verbose=VERBOSE):
	'''Bulk-updates the rows matching the rows of the specified dataframe at the specified filtering
	columns of the specified table (in the specified schema) and returns the number of bulk-updated
	rows.'''
	update_count = 0

	# Get the metadata of the table
	metadata = create_metadata(engine, schema=schema)
	table_metadata = get_table_metadata(engine, table, metadata=metadata, schema=schema)
	if is_null(filtering_cols):
		filtering_cols = get_primary_cols(engine, table, metadata=metadata, schema=schema)
	else:
		filtering_cols = include(df, filtering_cols)
	table_cols = [col.name for col in table_metadata.columns]

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
			update_count += bulk_update_table(engine, df[index_from:index_to], table,
			                                  chunk_size=chunk_size, filtering_cols=filtering_cols,
			                                  mssql=mssql, schema=schema, test=False,
			                                  verbose=verbose)
		return update_count

	debug_query('bulk-update', len(df), table, verbose=verbose)

	# Build the bulk query
	query = ''
	for index, row in df.iterrows():
		query += create_update_table_query(table, cols, row, filtering_cols=filtering_cols,
		                                   mssql=mssql, schema=schema)

	# Execute the bulk query
	try:
		result = execute(engine, query, multi=True)
		result_count = len(result) if is_collection(result) else result
		if result_count > 0:
			update_count = len(df)
		else:
			error_query('bulk-updated', table, verbose=verbose)
	except Exception as ex:
		error_query('bulk-updated', table, ex=ex, verbose=verbose)
	return update_count


# • DB UPSERT ######################################################################################

__DB_UPSERT_______________________________________ = ''


def upsert_table(engine, df, table, filtering_cols=None, mssql=DEFAULT_DB_MSSQL,
                 schema=DEFAULT_SCHEMA, test=ASSERT, verbose=VERBOSE):
	'''Updates/inserts the rows matching the rows of the specified dataframe at the specified
	filtering columns of/into the specified table (in the specified schema) and returns the number
	of updated/inserted rows.'''
	upsert_count = 0

	# Update the matching rows
	update_count = update_table(engine, df, table, filtering_cols=filtering_cols, mssql=mssql,
	                            schema=schema, test=test, verbose=False)
	upsert_count += update_count
	if update_count > 0:
		debug_query('update', upsert_count, table, verbose=verbose)

	# Insert the non-matching rows
	if update_count != len(df):
		insert_count = insert_table(engine, df, table, mssql=mssql, schema=schema, test=test,
		                            verbose=False)
		upsert_count += insert_count
		if insert_count > 0:
			debug_query('insert', insert_count, table, verbose=verbose)
	else:
		insert_count = 0

	# Test
	if upsert_count != len(df):
		if verbose:
			t = select_table_where(engine, table, cols=get_names(df), mssql=mssql, schema=schema,
			                       verbose=verbose)
			for index, row in df.iterrows():
				if is_empty(filter_rows(t, row)):
					error_row('update/insert', index, table, cols=filtering_cols, row=row,
					          verbose=verbose)
		if upsert_count == 0:
			error_query('update/insert', table, verbose=verbose)
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
            mssql_from=DEFAULT_DB_MSSQL, mssql_to=DEFAULT_DB_MSSQL, schema=DEFAULT_SCHEMA,
            test=ASSERT, upsert=False, verbose=VERBOSE):
	'''Migrates the specified tables (in the specified schema) from the specified engine to the
	specified engine using the specified collation and returns the number of migrated rows.'''
	count = 0

	# Create the tables
	if drop or create:
		metadata = create_metadata(engine_from, schema=schema)
		for table in tables:
			info('Recreate' if drop and create else 'Drop' if drop else 'Create', 'the table',
			     quote(table))
			table_metadata = get_table_metadata(engine_from, table, metadata=metadata,
			                                    schema=schema)
			for col in table_metadata.columns:
				update_col(col, collation=collation, mssql_from=mssql_from, mssql_to=mssql_to)
		if mssql_from and not mssql_to:
			metadata_to_lowercase(metadata)
		if drop:
			metadata.drop_all(engine_to, checkfirst=True)
		if create:
			metadata.create_all(engine_to)

	# Fill the tables
	if fill:
		for table in tables:
			info('Fill the table', quote(table))
			if is_null(filtering_row):
				df = select_table(engine_from, table, schema=schema)
			else:
				df = select_table_where(engine_from, table, filtering_cols=filtering_cols,
				                        filtering_row=filtering_row, mssql=mssql_from,
				                        schema=schema, verbose=verbose)
			if mssql_from and not mssql_to:
				table = table.lower()
				set_names(df, map(str.lower, get_names(df)))
			if upsert:
				count += upsert_table(engine_to, df, table, mssql=mssql_to, schema=schema,
				                      test=test, verbose=verbose)
			else:
				count += bulk_insert_table(engine_to, df, table, chunk_size=chunk_size,
				                           mssql=mssql_to, schema=schema, test=test,
				                           verbose=verbose)
	return count


#########################

def update_col(col, collation=None, mssql_from=True, mssql_to=True):
	'''Updates the default value, type and collation of the specified column.'''
	# - Update the default value
	update_col_default(col, mssql_from=mssql_from, mssql_to=mssql_to)
	# - Update the type
	update_col_type(col, mssql_from=mssql_from, mssql_to=mssql_to)
	# - Update the collation
	update_col_collation(col, collation=collation)


def update_col_default(col, mssql_from=True, mssql_to=True):
	'''Updates the default value of the specified column.'''
	if hasattr(col.server_default, 'arg') and isinstance(col.server_default.arg, TextClause):
		if mssql_from and not mssql_to:
			if isinstance(col.type, mssql.base.BIT):
				col.server_default.arg.text = col.server_default.arg.text.replace('0', 'FALSE') \
					.replace('1', 'TRUE')
			elif isinstance(col.type, mssql.base.DATE) or \
					isinstance(col.type, mssql.base.DATETIME) or \
					isinstance(col.type, mssql.base.DATETIMEOFFSET) or \
					isinstance(col.type, mssql.base.SMALLDATETIME) or \
					isinstance(col.type, mssql.base.TIME) or \
					isinstance(col.type, mssql.base.TIMESTAMP):
				col.server_default.arg.text = col.server_default.arg.text.replace('getdate', 'now')
		elif not mssql_from and mssql_to:
			if isinstance(col.type, db.BOOLEAN):
				col.server_default.arg.text = col.server_default.arg.text.replace('FALSE', '0') \
					.replace('TRUE', '1')
			elif isinstance(col.type, db.DATE) or \
					isinstance(col.type, db.DATETIME) or \
					isinstance(col.type, db.TIMESTAMP):
				col.server_default.arg.text = col.server_default.arg.text.replace('now', 'getdate')


def update_col_type(col, mssql_from=DEFAULT_DB_MSSQL, mssql_to=DEFAULT_DB_MSSQL):
	'''Updates the type of the specified column.'''
	if mssql_from and not mssql_to:
		if isinstance(col.type, mssql.base.BIT):
			col.type = db.BOOLEAN()
		elif isinstance(col.type, mssql.base.DATETIME) or \
				isinstance(col.type, mssql.base.SMALLDATETIME) or \
				isinstance(col.type, mssql.base.TIMESTAMP):
			col.type = db.TIMESTAMP()
	elif not mssql_from and mssql_to:
		if isinstance(col.type, db.BOOLEAN):
			col.type = mssql.base.BIT()
		elif isinstance(col.type, db.TIMESTAMP):
			col.type = mssql.base.DATETIME()


def update_col_collation(col, collation=None):
	'''Updates the collation of the specified column.'''
	if not is_null(collation) and hasattr(col.type, 'collation'):
		col.type.collation = collation
