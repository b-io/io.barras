#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contain graphical utility functions for Web
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

import socket

import requests
from xhtml2pdf import pisa

from nutil.common import *

####################################################################################################
# WEB FUNCTIONS
####################################################################################################

__WEB_____________________________________________ = ''

# • WEB HTML #######################################################################################

__WEB_HTML________________________________________ = ''


def string_to_html(s):
	s = s.replace('&', '&amp;') \
		.replace(' ', '&nbsp;') \
		.replace('\'', '&apos;') \
		.replace('"', '&quot;') \
		.replace('<', '&lt;') \
		.replace('>', '&gt;')
	s = replace(s, '\r\n|\r|\n', '<br />')
	s = replace(s, '\t', '&nbsp;&nbsp;&nbsp;&nbsp;')
	return s


#########################

def html_to_pdf(html, path, encoding=None):
	'''Converts the specified HTML code to PDF.'''
	with open(path, mode='wb', encoding=encoding) as f:
		status = pisa.CreatePDF(html, dest=f)
		return status.err == 0


##################################################

def tag(name, value=None, attributes=None):
	if is_null(attributes):
		attributes = {}
	return collapse('<', name,
	                collapse([collapse(' ', k, '=', dquote(v)) for k, v in attributes.items()]),
	                collapse('>', value, '</', name, '>') if not is_empty(value) else ' />')


#########################

def a(value, attributes=None):
	return tag('a', value, attributes)


def abbr(value, attributes=None):
	return tag('abbr', value, attributes)


def address(value, attributes=None):
	return tag('address', value, attributes)


def area(value, attributes=None):
	return tag('area', value, attributes)


def article(value, attributes=None):
	return tag('article', value, attributes)


def aside(value, attributes=None):
	return tag('aside', value, attributes)


def audio(value, attributes=None):
	return tag('audio', value, attributes)


def b(value, attributes=None):
	return tag('b', value, attributes)


def base(value, attributes=None):
	return tag('base', value, attributes)


def bdi(value, attributes=None):
	return tag('bdi', value, attributes)


def bdo(value, attributes=None):
	return tag('bdo', value, attributes)


def blockquote(value, attributes=None):
	return tag('blockquote', value, attributes)


def body(value, attributes=None):
	return tag('body', value, attributes)


def br(attributes=None):
	return tag('br', None, attributes)


def button(value, attributes=None):
	return tag('button', value, attributes)


def canvas(value, attributes=None):
	return tag('canvas', value, attributes)


def caption(value, attributes=None):
	return tag('caption', value, attributes)


def cite(value, attributes=None):
	return tag('cite', value, attributes)


def code(value, attributes=None):
	return tag('code', value, attributes)


def col(value, attributes=None):
	return tag('col', value, attributes)


def colgroup(value, attributes=None):
	return tag('colgroup', value, attributes)


def data(value, attributes=None):
	return tag('data', value, attributes)


def datalist(value, attributes=None):
	return tag('datalist', value, attributes)


def dd(value, attributes=None):
	return tag('dd', value, attributes)


def delete(value, attributes=None):
	return tag('del', value, attributes)


def details(value, attributes=None):
	return tag('details', value, attributes)


def dfn(value, attributes=None):
	return tag('dfn', value, attributes)


def dialog(value, attributes=None):
	return tag('dialog', value, attributes)


def div(value, attributes=None):
	return tag('div', value, attributes)


def dl(value, attributes=None):
	return tag('dl', value, attributes)


def dt(value, attributes=None):
	return tag('dt', value, attributes)


def em(value, attributes=None):
	return tag('em', value, attributes)


def embed(value, attributes=None):
	return tag('embed', value, attributes)


def fieldset(value, attributes=None):
	return tag('fieldset', value, attributes)


def figcaption(value, attributes=None):
	return tag('figcaption', value, attributes)


def figure(value, attributes=None):
	return tag('figure', value, attributes)


def footer(value, attributes=None):
	return tag('footer', value, attributes)


def form(value, attributes=None):
	return tag('form', value, attributes)


def h(value, heading=1, attributes=None):
	return tag('h' + str(heading), value, attributes)


def head(value, attributes=None):
	return tag('head', value, attributes)


def header(value, attributes=None):
	return tag('header', value, attributes)


def hr(value, attributes=None):
	return tag('hr', value, attributes)


def html(value, attributes=None):
	return tag('html', value, attributes)


def i(value, attributes=None):
	return tag('i', value, attributes)


def iframe(value, attributes=None):
	return tag('iframe', value, attributes)


def img(value, attributes=None):
	return tag('img', value, attributes)


def input(value, attributes=None):
	return tag('input', value, attributes)


def insert(value, attributes=None):
	return tag('ins', value, attributes)


def kbd(value, attributes=None):
	return tag('kbd', value, attributes)


def label(value, attributes=None):
	return tag('label', value, attributes)


def legend(value, attributes=None):
	return tag('legend', value, attributes)


def li(value, attributes=None):
	return tag('li', value, attributes)


def link(value, attributes=None):
	return tag('link', value, attributes)


def main(value, attributes=None):
	return tag('main', value, attributes)


def map(value, attributes=None):
	return tag('map', value, attributes)


def mark(value, attributes=None):
	return tag('mark', value, attributes)


def meta(value, attributes=None):
	return tag('meta', value, attributes)


def meter(value, attributes=None):
	return tag('meter', value, attributes)


def nav(value, attributes=None):
	return tag('nav', value, attributes)


def noscript(value, attributes=None):
	return tag('noscript', value, attributes)


def object(value, attributes=None):
	return tag('object', value, attributes)


def ol(value, attributes=None):
	return tag('ol', value, attributes)


def optgroup(value, attributes=None):
	return tag('optgroup', value, attributes)


def option(value, attributes=None):
	return tag('option', value, attributes)


def output(value, attributes=None):
	return tag('output', value, attributes)


def p(value, attributes=None):
	return tag('p', value, attributes)


def param(value, attributes=None):
	return tag('param', value, attributes)


def picture(value, attributes=None):
	return tag('picture', value, attributes)


def pre(value, attributes=None):
	return tag('pre', value, attributes)


def progress(value, attributes=None):
	return tag('progress', value, attributes)


def q(value, attributes=None):
	return tag('q', value, attributes)


def rp(value, attributes=None):
	return tag('rp', value, attributes)


def rt(value, attributes=None):
	return tag('rt', value, attributes)


def ruby(value, attributes=None):
	return tag('ruby', value, attributes)


def s(value, attributes=None):
	return tag('s', value, attributes)


def samp(value, attributes=None):
	return tag('samp', value, attributes)


def script(value, attributes=None):
	return tag('script', value, attributes)


def section(value, attributes=None):
	return tag('section', value, attributes)


def select(value, attributes=None):
	return tag('select', value, attributes)


def small(value, attributes=None):
	return tag('small', value, attributes)


def source(value, attributes=None):
	return tag('source', value, attributes)


def span(value, attributes=None):
	return tag('span', value, attributes)


def strong(value, attributes=None):
	return tag('strong', value, attributes)


def style(value, attributes=None):
	return tag('style', value, attributes)


def sub(value, attributes=None):
	return tag('sub', value, attributes)


def summary(value, attributes=None):
	return tag('summary', value, attributes)


def sup(value, attributes=None):
	return tag('sup', value, attributes)


def svg(value, attributes=None):
	return tag('svg', value, attributes)


def table(value, attributes=None):
	return tag('table', value, attributes)


def tbody(value, attributes=None):
	return tag('tbody', value, attributes)


def td(value, attributes=None):
	return tag('td', value, attributes)


def template(value, attributes=None):
	return tag('template', value, attributes)


def textarea(value, attributes=None):
	return tag('textarea', value, attributes)


def tfoot(value, attributes=None):
	return tag('tfoot', value, attributes)


def th(value, attributes=None):
	return tag('th', value, attributes)


def thead(value, attributes=None):
	return tag('thead', value, attributes)


def time(value, attributes=None):
	return tag('time', value, attributes)


def title(value, attributes=None):
	return tag('title', value, attributes)


def tr(value, attributes=None):
	return tag('tr', value, attributes)


def track(value, attributes=None):
	return tag('track', value, attributes)


def u(value, attributes=None):
	return tag('u', value, attributes)


def ul(value, attributes=None):
	return tag('ul', value, attributes)


def var(value, attributes=None):
	return tag('var', value, attributes)


def video(value, attributes=None):
	return tag('video', value, attributes)


def wbr(value, attributes=None):
	return tag('wbr', value, attributes)


# • WEB NETWORK ####################################################################################

__WEB_NETWORK_____________________________________ = ''


def get_host_ip():
	'''Returns the IP of the host.'''
	return socket.gethostbyname(get_host_name())


def get_host_name():
	'''Returns the name of the host.'''
	return socket.gethostname()


# • WEB QUERY ######################################################################################

__WEB_QUERY_______________________________________ = ''


def download(url, dir=None):
	'''Downloads the file pointed by the specified URL and writes it to the specified directory.'''
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
