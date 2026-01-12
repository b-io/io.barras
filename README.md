# ⊕ IO.BARRAS

**IO.BARRAS** is a unified collection of libraries in **C/C++, Java/Scala, Python, and POSIX Shell**
designed to accelerate software development — particularly in **scientific computing**,
**data processing**, and **automation**.

The ecosystem includes the following language-specific modules:

- ⚳ [**Ceres** (C)](https://github.com/b-io/io.barras/tree/master/c/ceres) — foundational utilities in C
- ♃ [**Jupiter** (Java)](https://github.com/b-io/io.barras/tree/master/java/jupiter) — core libraries for Java
- ♆ [**Neptune** (Python)](https://github.com/b-io/io.barras/tree/master/python/neptune) — modular libraries for Python
- ♇ [**Pluto** (C++)](https://github.com/b-io/io.barras/tree/master/cpp/pluto) — scientific computing tools in C++
- ♄ [**Saturn** (Scala)](https://github.com/b-io/io.barras/tree/master/scala/saturn) — functional utilities for Scala
- ♅ [**Uranus** (POSIX Shell)](https://github.com/b-io/io.barras/tree/master/shell/uranus) — scripting and automation tools

Feel free to share comments or suggestions to improve the libraries.

---

## 🚀 Installation

### 🛠 Pre-Install

#### On Windows (via Chocolatey)
Install required tools:
```bash
choco install git llvm maven mingw python
```

Make sure `clang-format`, `gcc`, `g++`, and  `mvn` are in your system PATH.

#### On Linux (Debian-based)
Install required packages:
```bash
sudo apt update
sudo apt install -y build-essential clang-format git maven python3 python3-pip
```

Also ensure `pre-commit` is installed globally:
```bash
pip3 install --user pre-commit
```

### 📦 Install

Clone the repository and build the project:
```bash
git clone https://github.com/b-io/io.barras.git
cd io.barras/
mvn -Dhttps.protocols=TLSv1.2 -DskipTests clean install
```

Or add the public Maven repository to your `pom.xml`:
```xml
<repository>
  <id>barras.io</id>
  <name>barras.io</name>
  <url>https://repo.barras.io</url>
</repository>
```

---

## ⚙️ Setup

### 🔁 Pre-Commit Setup

Ensure `pre-commit` is installed globally:
```bash
pip3 install --user pre-commit
```

Then enable the hooks:
```bash
pre-commit install --install-hooks --overwrite
```

To run all hooks manually:
```bash
pre-commit run --all-files
```

---

## 📄 License #############################################################################

Released under the [MIT License](LICENSE).

## ☕ Support #############################################################################

If you find NEPTUNE useful, you can support the development here:

* Ko-fi: https://ko-fi.com/b_i_o
