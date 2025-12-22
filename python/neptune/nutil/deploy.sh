#!/bin/sh
####################################################################################################
# NAME
#   <NAME> - deploy nutil
#
# SYNOPSIS
#   <NAME>
#
# AUTHOR
#   Written by Florian Barras (florian@barras.io).
#
# COPYRIGHT
#   Copyright © 2013-2025 Florian Barras <https://barras.io>.
#   The MIT License (MIT) <https://opensource.org/licenses/MIT>.
####################################################################################################

set -eu

# 1. Ensure Poetry is available
python -m pip install --user --upgrade pip poetry

# 2. Publish the package (build and upload)
python -m poetry publish --build --no-interaction
