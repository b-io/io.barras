# ♆ NEPTUNE — nmath ####################################################################################################

**nmath** — Mathematical utility library for Python.

* Version: `1.0.1a1`
* Python: `>=3.10,<4.0`
* Repository: https://github.com/b-io/io.barras/tree/master/python/neptune/nmath

## 🎯 Goal ################################################################################

* Provide statistical helpers and distribution utilities used across NEPTUNE.

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
import numpy as np

from nmath.stats.normal import Normal

d = Normal(mu=0.0, sigma=1.0)
x = np.array([0.0, 1.0, 2.0])
print(d.pdf(x))
print(d.cdf(x))
```

## 🗂️ Package layout #####################################################################

The source code lives under `source/` (not `src/`), and the unit tests live under `test/`.

* `nmath.stats.descriptive` — descriptive statistics helpers
* `nmath.stats.normal` / `lognormal` / `binomial` / `poisson` — distribution helpers and thin wrappers

## 🧩 Dependencies ########################################################################

Local NEPTUNE dependencies:

* `ngui`, `nutil`

Key external dependencies:

* `scipy` ^1.15.3

## 📄 License #############################################################################

Released under the [MIT License](LICENSE).
