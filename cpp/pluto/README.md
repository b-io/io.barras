# ♇ PLUTO ##############################################################################################################

**PLUTO** is the C++ family of **io.barras**: a standard **C++11** library intended to facilitate software development,
with an emphasis on scientific computing.

* Version: `1.0.1a1`
* C++: `>=11`
* Repository: https://github.com/b-io/io.barras/tree/master/cpp/pluto

PLUTO is based on the standard C++ library and does not require any other third-party dependency.

## 🗂️ Repository layout ##################################################################

* `source/pluto/...` — implementation
* `test/` — small test / sample programs

## 🚀 Installation ########################################################################

### 🧱 Install the full workspace ###########################

From the repository root:

```bash
mvn -Dhttps.protocols=TLSv1.2 -DskipTests clean install
```

### 🧩 Build only PLUTO #####################################

```bash
mvn -pl cpp/pluto -am -Dhttps.protocols=TLSv1.2 -DskipTests clean install
```

## 📄 License #############################################################################

Released under the [MIT License](LICENSE).

## ☕ Support #############################################################################

If you find PLUTO useful, you can support the development here:

* Ko-fi: https://ko-fi.com/b_i_o
