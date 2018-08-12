# ♅ URANUS

**URANUS** is a **Shell library** intended to facilitate POSIX Shell development.
The library contains utility functions; among them:

  * create POSIX-compliant arrays that are simple to use,
  * store arguments and parameterized options in an array,
  * package any project in a structured way (RPM or Solaris package) and
  * generate, start and stop Linux containers (LXC).

URANUS is fully compliant with POSIX and does not require any other library.


## Install

Launch the following commands in a shell:
~~~bash
git clone https://github.com/b-io/io.barras.git
cd io.barras/
mvn clean install
~~~

or

1. Change to the directory containing **ura-install**.
2. Launch the following command:
~~~bash
sh ura-install -v
~~~


## License

Feel free to download, try and share your suggestions about the libraries,
while respecting [this MIT License][license].

[license]: <LICENSE>


## Examples

>*Talk is cheap. Show me the code.*
>- Linus Torvalds

#### 1. POSIX-Compliant Arrays

~~~bash
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
~~~

#### 2. Arguments Handler

~~~shell
verb 'Load the arguments'
loadArguments $# "${@:-}"

verb 'List the options'
printn "'`getFlags`'"

verb 'List the arguments (without options)'
printArray "$ARGS"
~~~

#### 3. Package Management System (PMS)

Package on Red-Hat or Solaris operating systems thanks to the PMS library.
Have a look to **ura-package**:
~~~bash
startList 'Prepare'
	preparePackage "$SOURCE_DIR"
endList

startList 'Create'
	createPackage
endList

startList 'Install'
	installPackage
endList
~~~

#### 4. Virtualization Management System (VMS)

Manage Linux containers conveniently thanks to the VMS library. Have a look to:
- **ura-create** for creating a LXC container:
~~~bash
createContainer "$NAME"
~~~
- **ura-start** for starting a LXC container:
~~~bash
startContainer "$NAME"
~~~
- **ura-stop** for stopping a LXC container:
~~~bash
stopContainer "$NAME"
~~~
