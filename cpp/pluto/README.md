# ♇ PLUTO ##############################################################################################################

**PLUTO** is the C++ family of **io.barras**: a standard **C++11** library intended to facilitate software development,
with an emphasis on scientific computing.

* Version: `1.0.1a1`
* C++: `>=11`
* Repository: https://github.com/b-io/io.barras/tree/master/cpp/pluto

PLUTO is based on the standard C++ library and does not require any other third-party dependency.

## ✨ Highlights ##########################################################################

* Console I/O helpers (severity-style messages and progress bars)
* File utilities (basic file handling helpers)
* Math helpers and small reusable utilities (strings, formats, arguments)
* Lightweight base types (exceptions, copyable entities)

## 💡 Example #############################################################################

```cpp
#include "pluto/io/ConsoleHandler.h"

int main()
{
    const double n = 100;
    for (double i = 0; i <= n; ++i)
    {
        // Prints a one-line loading bar when progress increases
        ConsoleHandler::printLoadingBar(i, n);
    }
    return 0;
}
```

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

## 📫 Support and feedback ################################################################

If you use PLUTO and want to report an issue or share feedback:

* Email: florian@barras.io
* Issues: https://github.com/b-io/io.barras/issues
* Ko-fi: https://ko-fi.com/b_i_o
