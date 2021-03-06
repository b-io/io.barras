# ⚳ CERES

**CERES** is an ANSI C library whose goal is to have a higher level of abstraction than the standard
library without losing the efficiency and the portability of C. Its syntax is very easy to learn
(similar to Java) and it contains a set of structures that are easy to handle; each structure
inherits a set of core structures that offer numerous functionalities; among them:

  * handlers of basic types (character, digit, natural, integer and real),
  * string operations (including safe copy, concatenate, find and replace),
  * mathematical operations (including vector calculations) and
  * iterable structures (array, collection, array list, linked list, sorted list) that can generate
    an iterator over the elements.

CERES is based on the ANSI C standard library and does not require any other library.

Please do not hesitate to leave your comments for further improvement!


## Install

Launch the following commands in a shell:
~~~bash
git clone https://github.com/b-io/io.barras.git
cd io.barras/c/ceres/
mvn clean install
~~~


## License

Feel free to download, try and share your suggestions about the libraries,
while respecting [this MIT License (MIT)][license].

[license]: <LICENSE>


## Examples

>*Talk is cheap. Show me the code.*
>- Linus Torvalds


#### 1. Number and Object

~~~c

/* Create a Number able to change its base from 2 to _NUMBER_BASE_MAX (36) */
const natural number = 20;
const digit base = 10;
Number n = Number_create(number, base);
printn(_S("%N"), &n); /* 20 */

/* Change the base of the Number to binary and store the result in a string */
string output;
n.changeBase(&n, 2);
n.toString(&n, output);
printn(output); /* 10100 */

/* Encapsulate the Number in an Object (only the reference to the Number is kept) */
const integer type = _NUMBER_TYPE; /* (_NUMBER_TYPE is a preprocessor definition) */
Structure s = Structure_create(type, &n);
Object o = Object_create(&s);

/* Change the base of the Number to hexadecimal (0 -> 0, 1 -> 1, ..., 10 -> A, 11 -> B, ...) */
n.changeBase(&n, 16);
printn(_S("%O"), &o); /* 14 */

~~~


#### 2. Array

~~~c

/* Define the constant(s) */
string s1 = _S("hello");
string s2 = _S("world");
string s3 = _S("!");
const natural initialSize = 10;
const type elementType = _STRING_TYPE; /* (_STRING_TYPE is a preprocessor definition) */
const size elementSize = STRING_SIZE;

/* Construct an Array of strings */
Array* a = Array_new(elementType, elementSize, initialSize);
a->addValue(a, s1); a->addValue(a, s1); /* (2x) */
a->addValue(a, s2);
a->addValue(a, s3);

/* Print some information about the Array */
printn(_S("The number of elements in %A is %n."), a, a->length);
printn(_S("The number of '%s' in %A is %n."), s1, a, a->countValue(a, s1));
{
  /* Count the number of specific strings in a string */
  Array* set = Array_new(elementType, elementSize, 2);
  set->addValue(set, s1);
  set->addValue(set, s2);
  printn(_S("The number of '%s' and '%s' in %A is %n."), s1, s2, a, a->countAll(a, set));
  _RELEASE(set);
}

/* Remove a string */
printn(_S("Let us remove the first '%s' from %A."), s1, a);
a->removeValue(a, s1);
printn(_S("Then the number of '%s' in %A is %n."), s1, a, a->countValue(a, s1));

/* Get and print the elements */
printn(_S("%s %s%s"), a->get(a, 0).value, a->get(a, 1).value, a->get(a, 2).value); /* hello world! */

/* Release the Array */
_RELEASE(a);

~~~


#### 3. Time

~~~c

const tick t = chrono_start();

/* Construct a Number */
#if _32_BITS
const natural number = real_to_natural(1E9);
#else
const natural number = real_to_natural(1E18);
#endif
const natural base = 10;
Number n = Number_create(number, base);

/* Change the base of the Number to binary */
n.changeBase(&n, 2);
printn(_S("Binary: %N"), &n); /* 111011100110101100101000000000 or 110111100000101101101011001110100111011001000000000000000000 */

/* Change the base of the Number to hexavigesimal */
n.changeBase(&n, 26);
printn(_S("Hexavigesimal: %N"), &n); /* DGEHTYM or KMLUXINKECOJO */

chrono_end(t); /* #Ticks: ... | Elapsed time: ... [ms] */

~~~


4. Sort

~~~c

/* Fill the Array with integers (full copy) */
const natural size = real_to_natural(1E6);
Array* a = Array_new(_INTEGER_TYPE, INTEGER_SIZE, size);
integer i;
for (i = n; i > 0; --i)
{
  a->addValue(a, &i);
}
printn(_S("%A"), a); /* (10000000, 9999999, 9999998, ...) */

/* Sort the Array */
{
  const tick t = chrono_start();

  integers_quicksort(a->elements, a->length);

  chrono_end(t); /* #Ticks: ... | Elapsed time: .... [ms] */
  printn(_S("%A"), a); /* (1, 2, 3, ...) */
}

/* Release the Array */
_RELEASE(a);

~~~
