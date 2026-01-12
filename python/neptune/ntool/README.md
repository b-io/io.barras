# ♆ NEPTUNE — ntool ####################################################################################################

**ntool** — Tooling utility library for Python.

* Version: `1.0.1a1`
* Python: `>=3.10,<4.0`
* Repository: https://github.com/b-io/io.barras/tree/master/python/neptune/ntool

## 🎯 Goal ################################################################################

* Provide tooling utilities (style checks/fixes, packaging helpers).

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
# Check style rules (regex-based)
python -m ntool.style.style_checker --root . --config STYLE.yml

# Fix simple issues in-place (or use --dry-run)
python -m ntool.style.style_fixer --root . --config STYLE.yml --dry-run
```

## 🗂️ Package layout #####################################################################

The source code lives under `source/` (not `src/`), and the unit tests live under `test/`.

* `ntool.style` — regex-based style checker + in-place fixer (banner rules, comment rules, etc.)
* `ntool.packaging` — Poetry dependency updater for `pyproject.toml`
* `ntool.common` — assertion helpers + timed iteration utilities

## 🧩 Dependencies ########################################################################

Local NEPTUNE dependencies:

* `nutil`

Key external dependencies:

* `packaging`

## 📄 License #############################################################################

Released under the [MIT License](LICENSE).
