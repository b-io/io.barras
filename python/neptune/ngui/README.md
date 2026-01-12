# ♆ NEPTUNE — ngui #####################################################################################################

**ngui** — Graphical utility library for Python.

* Version: `1.0.1a1`
* Python: `>=3.10,<4.0`
* Repository: https://github.com/b-io/io.barras/tree/master/python/neptune/ngui

## 🎯 Goal ################################################################################

* Provide plotting and Web-rendering helpers around Plotly, Matplotlib, and XHTML-to-PDF.

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

from ngui.charts import plot_series

series = pd.Series([1.0, 2.0, 3.0])
fig = plot_series(series, title="Example series")
# In notebooks: fig.show()
print(type(fig))
```

## 🗂️ Package layout #####################################################################

The source code lives under `source/` (not `src/`), and the unit tests live under `test/`.

* `ngui.charts` — plotting helpers (Plotly/Matplotlib conversions, export helpers, common layouts)
* `ngui.web` — HTML escaping + HTML→PDF conversion (`xhtml2pdf`)

## 🧩 Dependencies ########################################################################

Local NEPTUNE dependencies:

* `nformat`, `nutil`

Key external dependencies:

* `xhtml2pdf` ^0.2.17
* `kaleido` ^1.2.0
* `matplotlib` ^3.10.8
* `plotly` ^6.5.1
* `seaborn` ^0.13.2
* `tqdm` ^4.67.1

## 📄 License #############################################################################

Released under the [MIT License](LICENSE).
