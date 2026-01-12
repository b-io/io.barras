# ♆ NEPTUNE ############################################################################################################

**NEPTUNE** is the Python family of **io.barras**: a set of small, decoupled libraries designed for
scientific and analytical development (data handling, plotting, statistics, ML, and service utilities).

* Version: `1.0.1a1`
* Python: `>=3.10,<4.0`
* Repository: https://github.com/b-io/io.barras/tree/master/python/neptune

## 🧩 Modules #############################################################################

Each subdirectory under `neptune/` is an installable Poetry package:

| Package    | Description                                 | Key dependencies                                          |
|------------|---------------------------------------------|-----------------------------------------------------------|
| `nconnect` | Connecting utility library for Python       | `sqlalchemy`, `httpcore`, `httpx`, `python-multipart`, …  |
| `nfin`     | Financial utility library for Python        | `statsmodels`                                             |
| `nformat`  | Formatting utility library for Python       | `opencv-python`, `beautifulsoup4`, `scipy`, `matplotlib`  |
| `ngui`     | Graphical utility library for Python        | `xhtml2pdf`, `kaleido`, `matplotlib`, `plotly`, …         |
| `nlearn`   | Machine learning utility library for Python | `statsmodels`, `gensim`, `scikit-learn`, `scikit-lego`, … |
| `nmath`    | Mathematical utility library for Python     | `scipy`                                                   |
| `nserve`   | Web-serving utility library for Python      | `asgiref`, `fastapi`, `uvicorn`                           |
| `ntest`    | Testing utility library for Python          | `pytest`                                                  |
| `ntool`    | Tooling utility library for Python          | `packaging`                                               |
| `nutil`    | Core utility library for Python             | `multiprocess`, `psutil`, `numpy`, `pandas`, …            |

## 🗂️ Repository layout ##################################################################

* `neptune/<package>/source/<package>/...` — implementation
* `neptune/<package>/test/` — unit tests
* `neptune/<package>/pyproject.toml` — per-package build metadata (Poetry)

## 🚀 Installation ########################################################################

This repository supports two common workflows.

### 🧱 Install the full workspace ###########################

Installs all NEPTUNE packages in a single Poetry environment (useful for development across modules):

```bash
mvn -Dhttps.protocols=TLSv1.2 -DskipTests clean install
```

### 🧩 Install a single package #############################

```bash
mvn -pl <package> -am -Dhttps.protocols=TLSv1.2 -DskipTests clean install
```

To build and install a wheel for a single package, run `sh install.sh` inside that package directory.

## 🧰 Development #########################################################################

Run unit tests per package:

```bash
poetry run pytest
```

## 📄 License #############################################################################

Released under the [MIT License](LICENSE).

## ☕ Support #############################################################################

If you find NEPTUNE useful, you can support the development here:

* Ko-fi: https://ko-fi.com/b_i_o
