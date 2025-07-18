#!/usr/bin/env python
####################################################################################################
# NAME
#    <NAME> - contain common utility functions
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

from nutil.common.collections import (
    MutableSet,
    OrderedDict,
    Sequence,
    configparser,
    get_next,
    has_filter,
    is_collection,
    is_dict,
    is_empty,
    is_null,
    os,
    to_collection,
    to_list,
)

####################################################################################################
# COMMON SET CLASSES
####################################################################################################

__COMMON_SET_CLASSES______________________________ = ""


class OrderedSet(MutableSet, Sequence):

    def __init__(self, *args):
        super().__init__()

        self.elements = OrderedDict.fromkeys(to_collection(*args))

    ##############################################
    # OPERATORS
    ##############################################

    def __getitem__(self, index):
        return self.to_list()[index]

    def __iter__(self):
        return self.elements.__iter__()

    def __len__(self):
        return self.elements.__len__()

    ##############################################

    difference = property(lambda self: self.__sub__)
    difference_update = property(lambda self: self.__isub__)
    intersection = property(lambda self: self.__and__)
    intersection_update = property(lambda self: self.__iand__)
    issubset = property(lambda self: self.__le__)
    issuperset = property(lambda self: self.__ge__)
    symmetric_difference = property(lambda self: self.__xor__)
    symmetric_difference_update = property(lambda self: self.__ixor__)
    union = property(lambda self: self.__or__)

    def __contains__(self, x):
        return self.elements.__contains__(x)

    def __le__(self, other):
        return all(e in other for e in self)

    def __lt__(self, other):
        return self <= other and self != other

    def __gt__(self, other):
        return self >= other and self != other

    def __ge__(self, other):
        return all(e in self for e in other)

    ##############################################

    def __repr__(self):
        return "OrderedSet([%s])" % (", ".join(map(repr, self.elements)))

    def __str__(self):
        return "{%s}" % (", ".join(map(repr, self.elements)))

    ##############################################
    # CONVERTERS
    ##############################################

    def to_list(self):
        return to_list(self.elements.keys())

    def to_set(self):
        return to_set(self.elements.keys())

    ##############################################
    # PROCESSORS
    ##############################################

    def add(self, element):
        self.elements[element] = None

    def discard(self, element):
        self.elements.pop(element, None)


##################################################


class EnvInterpolation(configparser.BasicInterpolation):
    """Extends the basic property parser to handle environment variables."""

    def before_get(self, parser, section, option, value, defaults):
        value = super().before_get(parser, section, option, value, defaults)
        return os.path.expandvars(value)


####################################################################################################
# COMMON SET CONSTANTS
####################################################################################################

__COMMON_SET_CONSTANTS____________________________ = ""

SET_TYPE = set

MUTABLE_SET_TYPE = MutableSet

ORDERED_SET_TYPE = OrderedSet


####################################################################################################
# COMMON SET VERIFIERS
####################################################################################################

__COMMON_SET_VERIFIERS____________________________ = ""


def is_set(x):
    return isinstance(x, SET_TYPE)


def is_mutable_set(x):
    return isinstance(x, MUTABLE_SET_TYPE)


def is_ordered_set(x):
    return isinstance(x, ORDERED_SET_TYPE)


####################################################################################################
# COMMON SET CONVERTERS
####################################################################################################

__COMMON_SET_CONVERTERS___________________________ = ""


def to_set(*args):
    if len(args) == 1:
        arg = args[0]
        if is_set(arg):
            return arg
        elif is_collection(arg):
            return set(arg if not is_dict(arg) else arg.values())
        return {arg}
    return set(args)


def unset(s):
    if is_set(s):
        if len(s) == 1:
            return get_next(s)
        return tuple(s)
    return s


#########################


def to_ordered_set(*args):
    if len(args) == 1:
        arg = args[0]
        if is_ordered_set(arg):
            return arg
    return OrderedSet(*args)


####################################################################################################
# COMMON SET PROCESSORS
####################################################################################################

__COMMON_SET_PROCESSORS___________________________ = ""


def filter_set(s, inclusion=None, exclusion=None):
    """Returns the values of the specified set that are in the specified inclusive set and are not
    in the specified exclusive set."""
    if is_empty(s):
        return set()
    if not has_filter(inclusion=inclusion, exclusion=exclusion):
        return to_set(s)
    elif is_null(inclusion):
        return to_set(s) - to_set(exclusion)
    elif is_empty(exclusion):
        return to_set(s) & to_set(inclusion)
    return to_set(s) & to_set(inclusion) - to_set(exclusion)


def include_set(s, inclusion):
    """Returns the values of the specified set that are in the specified inclusive set."""
    return filter_set(s, inclusion=inclusion)


def exclude_set(s, exclusion):
    """Returns the values of the specified set that are not in the specified exclusive set."""
    return filter_set(s, exclusion=exclusion)


#########################


def filter_ordered_set(s, inclusion=None, exclusion=None):
    """Returns the values of the specified ordered set that are in the specified inclusive set and
    are not in the specified exclusive set."""
    if is_empty(s):
        return OrderedSet()
    if not has_filter(inclusion=inclusion, exclusion=exclusion):
        return to_ordered_set(s)
    elif is_null(inclusion):
        return to_ordered_set(s) - to_set(exclusion)
    elif is_empty(exclusion):
        return to_ordered_set(s) & to_set(inclusion)
    return to_ordered_set(s) & to_set(inclusion) - to_set(exclusion)


def include_ordered_set(s, inclusion):
    """Returns the values of the specified ordered set that are in the specified inclusive set."""
    return filter_ordered_set(s, inclusion=inclusion)


def exclude_ordered_set(s, exclusion):
    """Returns the values of the specified ordered set that are not in the specified exclusive
    set."""
    return filter_ordered_set(s, exclusion=exclusion)
