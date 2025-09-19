#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contains utility annotations
#
# SYNOPSIS
#    <NAME>
#
# AUTHOR
#    Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#    Copyright © 2013-2025 Florian Barras <https://barras.io>.
#    The MIT License (MIT) <https://opensource.org/licenses/MIT>.
####################################################################################################


####################################################################################################
# ANNOTATIONS
####################################################################################################

__ANNOTATIONS_____________________________________ = ""

class classproperty:
    """Implements a read-only property evaluated on the class (not the instance)."""

    def __init__(self, fget):
        if not callable(fget):
            raise TypeError("'classproperty' expects a callable 'fget'")
        self.fget = fget

    def __get__(self, obj, owner=None):
        owner = owner if owner is not None else type(obj)
        return self.fget(owner)
