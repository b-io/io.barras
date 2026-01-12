#!/bin/sh
####################################################################################################
# NAME
#   <NAME> - install nfin
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

# 2. Build the package (create the `.whl` and `.tar.gz` files in the `dist` directory)
python -m poetry build

# 3. Install the latest built wheel
LATEST_WHEEL=$(ls -1 dist/*.whl | sort | tail -n 1)
python -m pip install --upgrade --force-reinstall "$LATEST_WHEEL"
