#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contain statistical utility functions
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

from nutil.ts import *

####################################################################################################
# DESCRIPTIVE FUNCTIONS
####################################################################################################

__DESCRIPTIVE_____________________________________ = ''

# • DESCRIPTIVE FIGURE #############################################################################

__DESCRIPTIVE_FIGURE______________________________ = ''


def draw_hist(x, bins=None, norm='probability', color=None, index=None, name=None, opacity=1,
              show_date=False, show_legend=True, show_name=True, yaxis=0):
	hover_template = get_hover_template(index)
	marker = dict(color=color)
	return go.Histogram(x=x,
	                    histnorm=norm, xbins=bins,
	                    name=get_label(name, show_date=show_date, show_name=show_name, yaxis=yaxis),
	                    customdata=index, hovertemplate=hover_template,
	                    marker=marker, opacity=opacity,
	                    showlegend=show_legend,
	                    yaxis='y' + str(1 if yaxis == 0 else yaxis))


def plot_hist(series, bins=None, norm='probability', fig=None, title=None, title_x=None,
              title_y=None, title_y2=None, colors=DEFAULT_COLORS, index=None, opacity=1,
              show_date=False, show_legend=True, show_name=True, yaxis=0):
	if is_null(fig):
		fig = create_figure(title=title, title_x=title_x, title_y=title_y, title_y2=title_y2)
	colors = get_iterator(to_list(colors), cycle=True)
	for s in to_series(series) if is_frame(series) else [series]:
		fig.add_trace(draw_hist(s,
		                        bins=bins, norm=norm,
		                        color=next(colors), index=index, name=get_name(s),
		                        opacity=opacity, show_date=show_date, show_legend=show_legend,
		                        show_name=show_name, yaxis=yaxis))
	return fig


def plot_multi_hist(df, bins=None, norm='probability', fig=None, row_count=None, col_count=None,
                    share_x=True, share_y=True, title=None, subtitles=None, title_x=None,
                    title_y=None, colors=DEFAULT_COLORS, index=None, opacity=1, show_date=False,
                    show_legend=False, show_name=True):
	return plot_multi(df, draw_hist,
	                  bins=bins, norm=norm,
	                  fig=fig, row_count=row_count, col_count=col_count, share_x=share_x,
	                  share_y=share_y, title=title, subtitles=subtitles, title_x=title_x,
	                  title_y=title_y, colors=colors,
	                  index=index, opacity=opacity, show_date=show_date, show_legend=show_legend,
	                  show_name=show_name)
