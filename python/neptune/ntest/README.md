# ♆ NEPTUNE — ntest ####################################################################################################

**ntest** — Testing utility library for Python.

* Version: `1.0.1a1`
* Python: `>=3.10,<4.0`
* Repository: https://github.com/b-io/io.barras/tree/master/python/neptune/ntest

## 🎯 Goal ################################################################################

* Provide test utilities (assertions).

## 🚀 Installation ########################################################################

This repository is a monorepo. Each NEPTUNE package is a Poetry project under `neptune/<package>`.

Developer install (editable, recommended):

```bash
python -m pip install --user --upgrade pip poetry
poetry install
```

Wheel install (build + install the latest wheel):

```bash
sh install.sh
```

## ⚡ Quickstart ##########################################################################

```bash
from __future__ import annotations

import numpy as np

from ntest.common import assert_equals


def test_assert_equals_numpy() -> None:
    first = np.array([[1.0, 2.0], [3.0, 4.0]])
    second = first.copy()
    second[0, 0] += 4e-7
    assert_equals(first, second, precision=6)
```

## 🗂️ Package layout #####################################################################

The source code lives under `source/` (not `src/`), and the unit tests live under `test/`.

* `ntest.common` — assertion helpers + timed iteration utilities

## 🧩 Dependencies ########################################################################

Local NEPTUNE dependencies:

* `nutil`

Key external dependencies:

* `pytest`

## 📄 License #############################################################################

Released under the [MIT License](LICENSE).
