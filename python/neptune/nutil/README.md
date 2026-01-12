# ♆ NEPTUNE — nutil ####################################################################################################

**nutil** — NEPTUNE is a collection of decoupled Python modules for robust application development.

* Version: `1.0.1a1`
* Python: `>=3.10,<4.0`
* Repository: https://github.com/b-io/io.barras/tree/master/python/neptune/nutil

## 🎯 Goal ################################################################################

* Provide the core utilities used by the other NEPTUNE packages.

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
from pathlib import Path

from nutil.io.file import atomic_write, resolve_path

path = resolve_path("example.json")
atomic_write(path, b'{"ok": true}\n')  # atomic write + os.replace
print(Path(path).read_text(encoding="utf-8"))
```

## 🗂️ Package layout #####################################################################

The source code lives under `source/` (not `src/`), and the unit tests live under `test/`.

* `nutil.io` — path utilities, JSON/CSV helpers, atomic writes, logging, sanitizers, and a sectioned JSON cache
* `nutil.struct` — utilities for collections and structural helpers
* `nutil.scalar` — scalar utilities (dates/times, strings, numbers, etc.)
* `nutil.typing` — typing helpers
* `nutil.decorators` — small decorators (typing, caching, etc.)

## 🧩 Dependencies ########################################################################

Local NEPTUNE dependencies:

* None

Key external dependencies:

* `multiprocess`
* `psutil`
* `numpy`
* `pandas`
* `tabulate`
* `chardet`
* `openpyxl`
* `orjson`
* … (+16 more)

## 📄 License #############################################################################

Released under the [MIT License](LICENSE).
