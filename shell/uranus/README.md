# ♅ URANUS #############################################################################################################

**URANUS** is the POSIX Shell family of **io.barras**: a library intended to facilitate shell scripting and automation.

* Version: `1.0.1a1`
* Shell: POSIX-compliant
* Repository: https://github.com/b-io/io.barras/tree/master/shell/uranus

## ✨ Highlights ##########################################################################

* POSIX-compliant arrays that are simple to use
* Argument and option handling
* Packaging utilities (RPM / Solaris package)
* Linux container helpers (LXC)

URANUS is fully compliant with POSIX and does not require any other third-party dependency.

## 🗂️ Repository layout ##################################################################

* `uranus/uranus/` — shell functions
* `uranus/uranus/pms/` — package management system helpers
* `uranus/uranus/vms/` — virtualization management system helpers

## 🚀 Installation ########################################################################

### 🧱 Install the full workspace ###########################

From the repository root:

```bash
mvn -Dhttps.protocols=TLSv1.2 -DskipTests clean install
```

### 🧩 Build only URANUS ####################################

```bash
mvn -pl shell/uranus -am -Dhttps.protocols=TLSv1.2 -DskipTests clean install
```

## 💡 Examples ############################################################################

> *"Talk is cheap. Show me the code."*  
> — Linus Torvalds

#### 1. POSIX-Compliant Arrays

```bash
verb 'Create an array'
A="`createSequence 1 3 | toCreateArray`" # "1" "2" "3"

verb 'Get the size of an array'
createArray | toGetArraySize # 0
createArray '' | toGetArraySize # 1
createArray 'a' 'b' | toGetArraySize # 2
getArraySize "$A" # 3

verb 'Get the first element of an array'
printn "$A" | toGetElementAt 0 # 1
getElementAt "$A" 0 # 1

verb 'Append an item to an array'
item=4
A="`appendToArray "$A" "$item"`" # "1" "2" "3" "4"

verb 'Find an item in an array'
printn "$A" | toGetElementIndex "$item" && printn "'$item' found" ||
	printn "'$item' not found"
getElementIndex "$A" "$item" # 3

verb 'Remove an item from an array'
printn "$A" | toRemoveElement "$item" # "1" "2" "3"
removeElement "$A" "$item" # "1" "2" "3"
```

#### 2. Arguments Handler

```shell
verb 'Load the arguments'
loadArguments $# "${@:-}"

verb 'List the options'
printn "'`getFlags`'"

verb 'List the arguments (without options)'
printArray "$ARGS"
```

#### 3. Package Management System (PMS)

Package on Red-Hat or Solaris operating systems thanks to the PMS library.
Have a look to **ura-package**:

```bash
startList 'Prepare'
	preparePackage "$SOURCE_DIR"
endList

startList 'Create'
	createPackage
endList

startList 'Install'
	installPackage
endList
```

#### 4. Virtualization Management System (VMS)

Manage Linux containers conveniently thanks to the VMS library. Have a look to:

- **ura-create** for creating a LXC container:

```bash
createContainer "$NAME"
```

- **ura-start** for starting a LXC container:

```bash
startContainer "$NAME"
```

- **ura-stop** for stopping a LXC container:

```bash
stopContainer "$NAME"
```

---

## 📄 License #############################################################################

Released under the [MIT License](LICENSE).

## ☕ Support #############################################################################

If you find URANUS useful, you can support the development here:

* Ko-fi: https://ko-fi.com/b_i_o
