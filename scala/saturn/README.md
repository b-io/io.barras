# ♄ SATURN #############################################################################################################

**SATURN** is the Scala family of **io.barras**: a set of Scala libraries intended to facilitate software development,
with an emphasis on functional utilities and reusable building blocks.

* Version: `1.0.1a1`
* Language: Scala (built with Maven)
* Repository: https://github.com/b-io/io.barras/tree/master/scala/saturn

## 🧩 Modules #############################################################################

SATURN is a multi-module Maven project:

| Module            | Description                                       |
|-------------------|---------------------------------------------------|
| `common`          | Common library                                    |
| `security`        | Security library (aggregator)                     |
| `security/crypto` | Asymmetric and symmetric cryptographic algorithms |

## 🗂️ Repository layout ##################################################################

* `saturn/<module>/source/...` — implementation

## 🚀 Installation ########################################################################

### 🧱 Install the full workspace ###########################

From the repository root:

```bash
mvn -Dhttps.protocols=TLSv1.2 -DskipTests clean install
```

### 🧩 Build only SATURN ####################################

```bash
mvn -pl scala/saturn -am -Dhttps.protocols=TLSv1.2 -DskipTests clean install
```

## 📄 License #############################################################################

Released under the [MIT License](LICENSE).

## ☕ Support #############################################################################

If you find SATURN useful, you can support the development here:

* Ko-fi: https://ko-fi.com/b_i_o
