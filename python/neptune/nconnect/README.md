# ♆ NEPTUNE — nconnect #################################################################################################

**nconnect** — Connecting utility library for Python.

* Version: `1.0.1a1`
* Python: `>=3.10,<4.0`
* Repository: https://github.com/b-io/io.barras/tree/master/python/neptune/nconnect

## 🎯 Goal ################################################################################

* Provide pragmatic connectivity helpers for HTTP and databases.

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
from nconnect.network.http import create_session_with_retries, request_json

session = create_session_with_retries()
payload = request_json("https://httpbin.org/json", session=session, api_name="httpbin", )
print(type(payload), payload.keys() if isinstance(payload, dict) else len(payload))
```

## 🗂️ Package layout #####################################################################

The source code lives under `source/` (not `src/`), and the unit tests live under `test/`.

* `nconnect.db` — SQLAlchemy helpers for table reads/writes (select/insert/update/upsert, procedures, migrations)
* `nconnect.network` — network helpers (host info, HTTP requests with retries/throttling, downloads)

## 🧩 Dependencies ########################################################################

Local NEPTUNE dependencies:

* `nutil`

Key external dependencies:

* `sqlalchemy`
* `httpcore`
* `httpx`
* `python-multipart`
* `requests`
* `urllib3`

## 📄 License #############################################################################

Released under the [MIT License](LICENSE).
