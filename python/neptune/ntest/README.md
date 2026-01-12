# ♆ NEPTUNE — ntest ####################################################################################################

**ntest** — Testing utility library for Python.

* Version: `1.0.1a1`
* Python: `>=3.10,<4.0`
* Repository: https://github.com/b-io/io.barras/tree/master/python/neptune/ntest

## 🎯 Goal ################################################################################

* Provide test and tooling utilities (style checks/fixes, packaging helpers, assertions).

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
python -m ntest.style.style_checker --root . --config STYLE.yml

# Fix simple issues in-place (or use --dry-run)
python -m ntest.style.style_fixer --root . --config STYLE.yml --dry-run
```

## 🗂️ Package layout #####################################################################

The source code lives under `source/` (not `src/`), and the unit tests live under `test/`.

* `ntest.style` — regex-based style checker + in-place fixer (banner rules, comment rules, etc.)
* `ntest.packaging` — Poetry dependency updater for `pyproject.toml`
* `ntest.common` — assertion helpers + timed iteration utilities

## 🧩 Dependencies ########################################################################

Local NEPTUNE dependencies:

* `nutil`

Key external dependencies:

* `packaging` ^25.0

## 📄 License #############################################################################

Released under the [MIT License](LICENSE).
