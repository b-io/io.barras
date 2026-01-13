# ♃ JUPITER ############################################################################################################

**JUPITER** is the Java family of **io.barras**: a set of core libraries intended to facilitate software development,
with an emphasis on scientific computing.

* Version: `1.8.0`
* Java: targets Java 8 bytecode by default (see `java/pom.xml` properties)
* Repository: https://github.com/b-io/io.barras/tree/master/java/jupiter

## 🧩 Modules #############################################################################

JUPITER is a multi-module Maven project:

| Module      | Description                                       |
|-------------|---------------------------------------------------|
| `common`    | Common utilities                                  |
| `connect`   | Connectors                                        |
| `execution` | Execution handlers                                |
| `gui`       | Graphical user interfaces                         |
| `hardware`  | Hardware interfaces                               |
| `lang`      | Programming language interfaces                   |
| `learn`     | Machine learning algorithms                       |
| `log`       | Log handlers                                      |
| `math`      | Mathematical functions                            |
| `media`     | Media interfaces                                  |
| `mobile`    | Mobile interfaces                                 |
| `network`   | Network libraries                                 |
| `security`  | Asymmetric and symmetric cryptographic algorithms |

## 🗂️ Repository layout ##################################################################

* `jupiter/<module>/source/...` — implementation
* `jupiter/<module>/test/...` — unit tests (when present)

## 🚀 Installation ########################################################################

### 🧱 Install the full workspace ###########################

From the repository root:

```bash
mvn -Dhttps.protocols=TLSv1.2 -DskipTests clean install
```

### 🧩 Build only JUPITER ###################################

```bash
mvn -pl java/jupiter -am -Dhttps.protocols=TLSv1.2 -DskipTests clean install
```

## 📄 License #############################################################################

Released under the [MIT License](LICENSE).

## 📫 Support and feedback ################################################################

If you use JUPITER and want to report an issue or share feedback:

* Email: florian@barras.io
* Issues: https://github.com/b-io/io.barras/issues
* Ko-fi: https://ko-fi.com/b_i_o
