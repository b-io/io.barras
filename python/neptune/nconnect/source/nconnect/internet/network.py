#!/usr/bin/env python3
# -*- coding: utf-8 -*-
#  SPDX-FileCopyrightText: 2013–2025 Florian Barras <florian@barras.io>
#  SPDX-License-Identifier: MIT

# HTTP #################################################################################################################
# Goal
#   Provide network utilities.
########################################################################################################################

import socket

## NETWORK ACCESSORS #####################################################################

__NETWORK_ACCESSORS_________________________________________ = ""


def get_host_ip():
    """Returns the IP of the host."""
    return socket.gethostbyname(get_host_name())


def get_host_name():
    """Returns the name of the host."""
    return socket.gethostname()
