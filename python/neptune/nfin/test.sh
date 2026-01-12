#!/bin/sh
####################################################################################################
# NAME
#   <NAME> - test nfin
#
# SYNOPSIS
#   <NAME>
#
# AUTHOR
#   Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#   Copyright © 2013-2026 Florian Barras <https://barras.io>.
#   The MIT License (MIT) <https://opensource.org/licenses/MIT>.
####################################################################################################

set -eu

# 1. Ensure Poetry is available
python -m pip install --user --upgrade pip poetry

# 2. Install the project dependencies and create a lockfile
python -m poetry lock

# 3. Run the test suite
python -m poetry run pytest test
