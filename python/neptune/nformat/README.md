# ♆ NEPTUNE — nformat ##################################################################################################

**nformat** — Formatting utility library for Python.

* Version: `1.0.1a1`
* Python: `>=3.10,<4.0`
* Repository: https://github.com/b-io/io.barras/tree/master/python/neptune/nformat

## 🎯 Goal ################################################################################

* Provide small formatting helpers for colors, HTML, images, and bulleted output.

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
from nformat.color import to_rgb, scale_color
from nformat.struct import format_bulleted_dict

print(to_rgb("tab:blue"))
print(scale_color((0.1, 0.2, 0.3)))
print(format_bulleted_dict({"a": 1, "b": 2}))
```

## 🗂️ Package layout #####################################################################

The source code lives under `source/` (not `src/`), and the unit tests live under `test/`.

* `nformat.color` — color naming/conversion utilities (RGB/HSV, scaling)
* `nformat.html` — lightweight tag parsing helpers
* `nformat.image` — OpenCV-based image helpers + Base64/HTML encoding
* `nformat.struct` — formatting helpers for bulleted output

## 🧩 Dependencies ########################################################################

Local NEPTUNE dependencies:

* `nutil`

Key external dependencies:

* `opencv-python` ^4.12.0.88
* `beautifulsoup4` ^4.14.3
* `scipy` ^1.15.3
* `matplotlib` ^3.10.8

## 📄 License #############################################################################

Released under the [MIT License](LICENSE).
