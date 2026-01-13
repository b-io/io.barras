# ⊕ IO.BARRAS ##########################################################################################################

**IO.BARRAS** is a unified collection of libraries in **C**, **C++**, **Java**, **Scala**, **Python**, and
**POSIX Shell** designed to accelerate software development — particularly for **scientific computing**,
**data processing**, and **automation**.

* Workspace version: `1.0.1a1`
* Website: https://barras.io
* Repository: https://github.com/b-io/io.barras

## 🪐 Libraries ###########################################################################

Each language family is a small, decoupled library set. See the family README for details:

- ⚳ [**Ceres** (C)](c/ceres) — C utilities
- ♇ [**Pluto** (C++)](cpp/pluto) — C++ scientific computing
- ♃ [**Jupiter** (Java)](java/jupiter) — Java core libraries
- ♄ [**Saturn** (Scala)](scala/saturn) — Scala functional libraries
- ♆ [**Neptune** (Python)](python/neptune) — modular Python libraries
- ♅ [**Uranus** (POSIX Shell)](shell/uranus) — shell automation

## 🔖 Versioning ##########################################################################

Each family is versioned independently.

* The Java family uses a Java-baseline version (for example, `1.8.x` targets Java 8).
* Other families may use their own semantic or pre-release versioning.

## 🗂️ Repository layout ##################################################################

* `c/` — CERES (C)
* `cpp/` — PLUTO (C++)
* `java/` — JUPITER (Java)
* `scala/` — SATURN (Scala)
* `python/` — NEPTUNE (Python)
* `shell/` — URANUS (POSIX Shell)

## 🚀 Build ###############################################################################

This repository is built with Maven and supports a top-level build or per-module builds.

### 🧱 Build the full workspace #############################

```bash
mvn -Dhttps.protocols=TLSv1.2 -DskipTests clean install
```

### 🧩 Build a single module ################################

```bash
mvn -pl c/ceres -am -Dhttps.protocols=TLSv1.2 -DskipTests clean install
mvn -pl python/neptune -am -Dhttps.protocols=TLSv1.2 -DskipTests clean install
```

For language-specific setup (Poetry, shell installation, etc.), see the corresponding family README.

## 📄 License #############################################################################

Released under the [MIT License](LICENSE).

## 📫 Support and feedback ################################################################

If you use IO.BARRAS and want to report an issue or share feedback:

* Email: florian@barras.io
* Issues: https://github.com/b-io/io.barras/issues
* Ko-fi: https://ko-fi.com/b_i_o
