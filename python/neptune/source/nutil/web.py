#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contain Web utility functions
#
# SYNOPSIS
#    <NAME>
#
# AUTHOR
#    Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#    Copyright © 2013-2021 Florian Barras <https://barras.io>.
#    The MIT License (MIT) <https://opensource.org/licenses/MIT>.
####################################################################################################

import socket

import requests

from nutil.common import *

####################################################################################################
# WEB FUNCTIONS
####################################################################################################

# • WEB NETWORK ####################################################################################

__WEB_NETWORK_____________________________________ = ''


def get_host_ip():
	"""Returns the IP of the host."""
	return socket.gethostbyname(get_host_name())


def get_host_name():
	"""Returns the name of the host."""
	return socket.gethostname()


# • WEB QUERY ######################################################################################

__WEB_QUERY_______________________________________ = ''


def download(url, dir=None):
	"""Downloads the file pointed by the specified URL and writes it to the specified directory."""
	if is_null(dir):
		dir = get_dir('.')
	info('Download the file', quote(url), 'to the directory', quote(dir))
	try:
		content = requests.get(url).content
		filename = get_filename(url).split('?')[0]
		return write_bytes(collapse(dir, '/', filename), content)
	except Exception as ex:
		error(ex)
		raise
