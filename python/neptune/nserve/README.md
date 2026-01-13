# ♆ NEPTUNE — nserve ###################################################################################################

**nserve** — Web-serving utility library for Python.

* Version: `1.0.1a1`
* Python: `>=3.10,<4.0`
* Repository: https://github.com/b-io/io.barras/tree/master/python/neptune/nserve

## 🎯 Goal ################################################################################

* Provide an opinionated FastAPI application factory and a minimal service CLI.

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
# Run the development server (FastAPI + Uvicorn)
python -m nserve --help
python -m nserve --reload --port 8000
```

## 🗂️ Package layout #####################################################################

The source code lives under `source/` (not `src/`), and the unit tests live under `test/`.

* `nserve.app` — FastAPI application factory (`create_app`) + options (`ServeAppOptions`)
* `nserve.middleware` — request ID + access logging middleware
* `nserve.health` — standard health endpoints
* `nserve.uvicorn` — `uvicorn.run` wrapper + options
* `nserve.cli` — CLI exposed as `python -m nserve`

## 🧩 Dependencies ########################################################################

Local NEPTUNE dependencies:

* `nutil`

Key external dependencies:

* `asgiref`
* `fastapi`
* `uvicorn`

## 📄 License #############################################################################

Released under the [MIT License](LICENSE).

## 📫 Support and feedback ################################################################

If you use nserve and want to report an issue or share feedback:

* Email: florian@barras.io
* Issues: https://github.com/b-io/io.barras/issues
* Ko-fi: https://ko-fi.com/b_i_o
