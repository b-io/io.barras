# ♆ NEPTUNE — nfin #####################################################################################################

**nfin** — Financial utility library for Python.

* Version: `1.0.1a1`
* Python: `>=3.10,<4.0`
* Repository: https://github.com/b-io/io.barras/tree/master/python/neptune/nfin

## 🎯 Goal ################################################################################

* Provide financial time-series utilities (cleaning, transforms, decomposition, and forecasting).

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

```python
import pandas as pd

from nfin.time_series import get_returns, cum_returns

series = pd.Series([100.0, 101.0, 99.0, 102.0])
r = get_returns(series)
cr = cum_returns(series)
print(r.tolist())
print(cr.tolist())
```

## 🗂️ Package layout #####################################################################

The source code lives under `source/` (not `src/`), and the unit tests live under `test/`.

* `nfin.time_series` — time-series cleaning, aggregation, transforms, decomposition, and forecasting

## 🧩 Dependencies ########################################################################

Local NEPTUNE dependencies:

* `ngui`, `nutil`

Key external dependencies:

* `statsmodels` ^0.14.6

## 📄 License #############################################################################

Released under the [MIT License](LICENSE).
