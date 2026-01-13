# ⊕ IO.BARRAS ##########################################################################################################

**IO.BARRAS** is a unified collection of libraries in **C/C++**, **Java/Scala**, **Python**, and **POSIX Shell**
designed to accelerate software development — particularly in **scientific computing**, **data processing**, and *
*automation**.

* Version: `1.0.1a1`
* Website: https://barras.io
* Repository: https://github.com/b-io/io.barras

## 🪐 Libraries ###########################################################################

Each language family is a small, decoupled library set:

- ⚳ [**Ceres** (C)](https://github.com/b-io/io.barras/tree/master/c/ceres) — C utilities
- ♇ [**Pluto** (C++)](https://github.com/b-io/io.barras/tree/master/cpp/pluto) — C++ scientific computing
- ♃ [**Jupiter** (Java)](https://github.com/b-io/io.barras/tree/master/java/jupiter) — Java core libraries
- ♄ [**Saturn** (Scala)](https://github.com/b-io/io.barras/tree/master/scala/saturn) — Scala functional libraries
- ♆ [**Neptune** (Python)](https://github.com/b-io/io.barras/tree/master/python/neptune) — modular Python libraries
- ♅ [**Uranus** (POSIX Shell)](https://github.com/b-io/io.barras/tree/master/shell/uranus) — shell automation

## 🗂️ Repository layout ##################################################################

* `c/` — CERES (C)
* `cpp/` — PLUTO (C++)
* `java/` — JUPITER (Java)
* `scala/` — SATURN (Scala)
* `python/` — NEPTUNE (Python)
* `shell/` — URANUS (POSIX Shell)

## 🚀 Installation ########################################################################

This repository is built with Maven and supports a top-level build or per-module builds.

### 🧱 Install the full workspace ###########################

From the repository root:

```bash
mvn -Dhttps.protocols=TLSv1.2 -DskipTests clean install
```

### 🧩 Build a single module ################################

Examples:

```bash
mvn -pl c/ceres -am -Dhttps.protocols=TLSv1.2 -DskipTests clean install
mvn -pl python/neptune -am -Dhttps.protocols=TLSv1.2 -DskipTests clean install
```

## 🧰 Development #########################################################################

Enable pre-commit hooks (recommended):

```bash
pip3 install --user pre-commit
pre-commit install --install-hooks --overwrite
```

Run hooks manually:

```bash
pre-commit run --all-files
```

## 📄 License #############################################################################

Released under the [MIT License](LICENSE).

## ☕ Support #############################################################################

If you find IO.BARRAS useful, you can support the development here:

* Ko-fi: https://ko-fi.com/b_i_o
