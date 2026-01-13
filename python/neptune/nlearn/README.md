# ♆ NEPTUNE — nlearn ###################################################################################################

**nlearn** — Machine learning utility library for Python.

* Version: `1.0.1a1`
* Python: `>=3.10,<4.0`
* Repository: https://github.com/b-io/io.barras/tree/master/python/neptune/nlearn

## 🎯 Goal ################################################################################

* Provide small machine-learning helpers (clustering, regression, and basic NLP utilities).

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

from nlearn.clustering import create_clustering

points = np.random.normal(size=(200, 2))
model = create_clustering(points, n=3, random_state=42)
print(model.n_clusters)
```

## 🗂️ Package layout #####################################################################

The source code lives under `source/` (not `src/`), and the unit tests live under `test/`.

* `nlearn.clustering` — clustering factories (KMeans / MiniBatchKMeans, Gaussian mixture)
* `nlearn.regression` — regression helpers
* `nlearn.nlp` — NLP utilities (tokenization/vectorization helpers)

## 🧩 Dependencies ########################################################################

Local NEPTUNE dependencies:

* `ngui`, `nmath`, `nutil`

Key external dependencies:

* `statsmodels`
* `gensim`
* `scikit-learn`
* `scikit-lego`
* `tensorflow`

## 📄 License #############################################################################

Released under the [MIT License](LICENSE).

## 📫 Support and feedback ################################################################

If you use nlearn and want to report an issue or share feedback:

* Email: florian@barras.io
* Issues: https://github.com/b-io/io.barras/issues
* Ko-fi: https://ko-fi.com/b_i_o
