#!/bin/sh
################################################################################
#NAME
#    <NAME> - contain utility functions for POSIX shell scripts
#
#SYNOPSIS
#    <NAME>
#
#AUTHOR
#    Written by Florian Barras (florian@barras.io).
#
#COPYRIGHT
#    Copyright © 2013-2018 Florian Barras <https://barras.io>.
#    The MIT License (MIT) <https://opensource.org/licenses/MIT>.
################################################################################


################################################################################
# BEGIN
################################################################################

if [ -z "${URA:-}" ]
then
	URA="$0"


################################################################################
# PROFILES
################################################################################

# Load the required URANUS profiles
: "${URA_PROFILE_DIR:=/etc/profile.d}"
if [ -z "${URA_PROFILE}" ]
then
	. "$URA_PROFILE_DIR"/'uranus.sh'
fi


################################################################################
# SETTINGS
################################################################################

# Instruct to immediately exit if a command has a non-zero exit status (-e) or
# if there is a reference to an unset variable (-u). Disable filename expansion
# (-f).
set -euf

################################################################################

# The constants used in the template for version information (-i)
: "${AUTHOR:=}"
: "${DATE:=`date +'%Y'`}"
: "${LICENSE:=}"
: "${URL:=}"
: "${VENDOR:=}"
: "${VERSION:=}"

################################################################################

# The list flag
: "${LIST:=$TRUE}"

# The quote to be used
: "${QUOTE:=\"}"

################################################################################

# The log flag
LOG=$FALSE

# The log path
: "${LOG_PATH:=$0.log}"

# The log group
: "${LOG_GROUP:=logger}"


################################################################################
# CONSTANTS
################################################################################

# The specified arguments (without options)
ARGS=''

# The number of arguments (without options)
ARGS_NUMBER=0

# The specified options
OPTS=''

# The number of options
OPTS_NUMBER=0

# The possible options
OPTIONS='hivl:'"${OPTIONS:-}"

################################################################################

# The hierarchy level (-l)
INITIAL_LEVEL=-1
LEVEL=$INITIAL_LEVEL

# The verbose flag (-v)
VERBOSE=$FALSE

################################################################################

# The log types
LOG_INFO_TYPE='INFO'
LOG_WARN_TYPE='WARN'
LOG_FAIL_TYPE='FAIL'


################################################################################
# FUNCTIONS
################################################################################

########################################
# PRINT
########################################

#> print [STRING]...
print()
{
	printf '%s' "${*:-}"
}
#> LINE... | toPrint
toPrint()
{
	while IFS= read line
	do
		print "$line"
	done
}

#> print0 [STRING]...
print0()
{
	printf '%s\0' "${*:-}"
}
#> LINE... | toPrint0
toPrint0()
{
	while IFS= read line
	do
		print0 "$line"
	done
}

#> printn [STRING]...
printn()
{
	printf '%s\n' "${*:-}"
}
#> LINE... | toPrintn
toPrintn()
{
	while IFS= read line
	do
		printn "$line"
	done
}

########################################

#> blue [STRING]...
blue()
{
	printf '\033[0;34m%s\033[0m\n' "${*:-}"
}
#> LINE... | toBlue
toBlue()
{
	while IFS= read line
	do
		blue "$line"
	done
}

#> cyan [STRING]...
cyan()
{
	printf '\033[0;36m%s\033[0m\n' "${*:-}"
}
#> LINE... | toCyan
toCyan()
{
	while IFS= read line
	do
		cyan "$line"
	done
}

#> green [STRING]...
green()
{
	printf '\033[0;32m%s\033[0m\n' "${*:-}"
}
#> LINE... | toGreen
toGreen()
{
	while IFS= read line
	do
		green "$line"
	done
}

#> magenta [STRING]...
magenta()
{
	printf '\033[0;35m%s\033[0m\n' "${*:-}"
}
#> LINE... | toMagenta
toMagenta()
{
	while IFS= read line
	do
		magenta "$line"
	done
}

#> red [STRING]...
red()
{
	printf '\033[0;31m%s\033[0m\n' "${*:-}"
}
#> LINE... | toRed
toRed()
{
	while IFS= read line
	do
		red "$line"
	done
}

#> yellow [STRING]...
yellow()
{
	printf '\033[0;33m%s\033[0m\n' "${*:-}"
}
#> LINE... | toYellow
toYellow()
{
	while IFS= read line
	do
		yellow "$line"
	done
}

########################################
# SYSTEM
########################################

#> assert FOUND EXPECTED
assert()
{
	checkArguments $# 2 &&

	if [ "$1" != "$2" ]
	then
		fail 'Assert failed: '"`quote "$2"`"' expected but '"`quote "$1"`"' found'\
			$URA_ERROR_INVALID
	fi
}
#> FOUND... | toAssert EXPECTED
toAssert()
{
	checkArguments $# 1 &&

	while IFS= read found
	do
		assert "$found" "$1"
	done
}

#> escape STRING
escape()
{
	checkArguments $# 1 &&

	printn "$1" | toEscape
}
#> STRING... | toEscape
toEscape()
{
	sed -e 's|^-|\\-|' -e 's|\.|\\.|'
}

#> getStackTrace [FROM]
getStackTrace()
{
	increment "${1:-0}" | toGetStackTrace
}
#> FROM... | toGetStackTrace
toGetStackTrace()
{
	if [ -n "${FUNCNAME:-}" ]
	then
		toIncrement |
		toIterate ${#FUNCNAME[@]} |
		while IFS= read index
		do
			checkIndex "$index" &&

			printn 'at '"${FUNCNAME[$index]}"
		done
	fi
}

#> getLibPath LIBRARY [PROJECT]
getLibPath()
{
	checkArguments $# 1 &&
	checkNonEmpty "$1" &&
	if [ -n "${2:-}" ]
	then
		project="$2"
	else
		project="$URA_NAME"
	fi &&

	find "$LIB_DIR/$project" -type f -name '*'"$1"'*.sh'
}
#> LIBRARY... | toGetLibPath [PROJECT]
toGetLibPath()
{
	while IFS= read lib
	do
		getLibPath "$lib" "${1:-}"
	done
}

#> pause
pause()
{
	printn 'Press '"`quote 'ENTER'`"' to continue or '"`quote 'CTRL+C'`"' to exit...' &&
	read key
}

#> testCommand COMMAND
testCommand()
{
	checkArguments $# 1 &&
	checkNonEmpty "$1" &&

	command -v "$1" > "$NULL_PATH" 2>&1
}
#> COMMAND... | toTestCommand
toTestCommand()
{
	while IFS= read command
	do
		testCommand "$command"
	done
}

########################################

#> listKeymaps
listKeymaps()
{
	if [ $SOLARIS -eq $TRUE ]
	then
		kbd -s
	else
		localectl list-keymaps
	fi
}

#> selectKeymap LAYOUT
selectKeymap()
{
	checkArguments $# 1 &&
	checkNonEmpty "$1" &&

	if [ $SOLARIS -eq $TRUE ]
	then
		kbd -s "$1"
	else
		localectl set-keymap "$1"
	fi
}
#> LAYOUT... | toSelectKeymap
toSelectKeymap()
{
	while IFS= read layout
	do
		selectKeymap "$layout"
	done
}

########################################
# NUMBERS
########################################

#> castToInt STRING
castToInt()
{
	checkArguments $# 1 &&
	checkNonEmpty "$1" &&

	$AWK 'BEGIN {printf "%.0f\n", "'"$1"'"}'
}
#> STRING... | toCastToInt
toCastToInt()
{
	$AWK '{printf "%.0f\n", $0}'
}

#> compute [EXPRESSION]
compute()
{
	$AWK 'BEGIN {print '"${*:-}"'}'
}
#> EXPRESSION... | toCompute
toCompute()
{
	xargs -I '{}' $AWK 'BEGIN {print {}}'
}

#> testInteger STRING
testInteger()
{
	checkArguments $# 1 &&
	checkNonEmpty "$1" &&

	printf '%d' "$1" > "$NULL_PATH" 2>&1
}
#> STRING... | toTestInteger
toTestInteger()
{
	while IFS= read string
	do
		testInteger "$string"
	done
}

########################################

#> add AUGEND ADDEND
add()
{
	checkArguments $# 2 &&
	checkInteger "$1" &&
	checkInteger "$2" &&

	expr $1 \+ $2 || true
}
#> AUGEND... | toAdd ADDEND
toAdd()
{
	checkArguments $# 1 &&

	while IFS= read augend
	do
		add "$augend" "$1"
	done
}

#> subtract MINUEND SUBTRAHEND
subtract()
{
	checkArguments $# 2 &&
	checkInteger "$1" &&
	checkInteger "$2" &&

	expr $1 \- $2 || true
}
#> MINUEND... | toSubtract SUBTRAHEND
toSubtract()
{
	checkArguments $# 1 &&

	while IFS= read minuend
	do
		subtract "$minuend" "$1"
	done
}

########################################

#> increment NUMBER
increment()
{
	checkArguments $# 1 &&

	add "$1" 1
}
#> NUMBER... | toIncrement
toIncrement()
{
	toAdd 1
}

#> decrement NUMBER
decrement()
{
	checkArguments $# 1 &&

	subtract "$1" 1
}
#> NUMBER... | toDecrement
toDecrement()
{
	toSubtract 1
}

########################################

#> sum NUMBER...
sum()
{
	checkArguments $# 1 &&
	checkInteger "$1" &&
	sum=$1 &&
	shift &&

	for number in "$@"
	do
		sum=`add $sum "$number"`
	done &&
	printn $sum
}
#> NUMBER... | toSum
toSum()
{
	read sum &&
	checkInteger "$sum" &&

	while IFS= read number
	do
		sum=`add $sum "$number"`
	done &&
	printn $sum
}

########################################

#> getMin NUMBER...
getMin()
{
	checkArguments $# 1 &&
	checkInteger "$1" &&
	min=$1 &&
	shift &&

	for number in "$@"
	do
		checkInteger "$number" &&

		if [ $number -lt $min ]
		then
			min=$number
		fi
	done &&
	printn $min
}
#> NUMBER... | toGetMin
toGetMin()
{
	read min &&
	checkInteger "$min" &&

	while IFS= read number
	do
		checkInteger "$number" &&

		if [ $number -lt $min ]
		then
			min=$number
		fi
	done &&
	printn $min
}

#> getMax NUMBER...
getMax()
{
	checkArguments $# 1 &&
	checkInteger "$1" &&
	max=$1 &&
	shift &&

	for number in "$@"
	do
		checkInteger "$number" &&

		if [ $number -gt $max ]
		then
			max=$number
		fi
	done &&
	printn $max
}
#> NUMBER... | toGetMax
toGetMax()
{
	read max &&
	checkInteger "$max" &&

	while IFS= read number
	do
		checkInteger "$number" &&

		if [ $number -gt $max ]
		then
			max=$number
		fi
	done &&
	printn $max
}

########################################

# Create a sequence of indices [FROM, TO].
#> createSequence FROM TO
createSequence()
{
	checkArguments $# 2 &&
	checkInteger "$1" &&
	checkInteger "$2" &&

	$AWK 'BEGIN {for (i = '$1'; i <= '$2'; ++i) print i}'
}
#> FROM... | toCreateSequence TO
toCreateSequence()
{
	checkArguments $# 1 &&
	checkInteger "$1" &&

	$AWK '{for (i = $0; i <= '$1'; ++i) print i}'
}

# Create a sequence of indices [FROM, TO).
#> iterate FROM TO
iterate()
{
	checkArguments $# 2 &&
	checkIndex "$1" &&
	checkIndex "$2" &&

	$AWK 'BEGIN {for (i = '$1'; i < '$2'; ++i) print i}'
}
#> FROM... | toIterate TO
toIterate()
{
	checkArguments $# 1 &&
	checkIndex "$1" &&

	$AWK '{for (i = $0; i < '$1'; ++i) print i}'
}

########################################
# STRINGS
########################################

#> LINE... | toAppend [STRING]
toAppend()
{
	while IFS= read line
	do
		print "$line${1:-}"
	done
}

#> LINE... | toAppend0 [STRING]
toAppend0()
{
	while IFS= read line
	do
		print0 "$line${1:-}"
	done
}

#> LINE... | toAppendn [STRING]
toAppendn()
{
	while IFS= read line
	do
		printn "$line${1:-}"
	done
}

########################################


#> appendLine LINE [LINE]
appendLine()
{
	checkArguments $# 1 &&

	printn "$1" | toAppendLine "${2:-}"
}
#> LINE... | toAppendLine [LINE]
toAppendLine()
{
	while IFS= read line
	do
		printf '%s\n%s\n' "$line" "${1:-}"
	done
}

########################################

#> captureFile FILE PATTERN
captureFile()
{
	checkArguments $# 2 &&
	checkFile "$1" &&
	checkNonEmpty "$2" &&

	grep "`escape "$2"`" "`escape "$1"`"
}
#> FILE... | toCaptureFile PATTERN
toCaptureFile()
{
	checkArguments $# 1 &&

	while IFS= read file
	do
		captureFile "$file" "$1"
	done
}

#> contain STRING PATTERN
contain()
{
	checkArguments $# 2 &&

	printn "$1" | toContain "$2"
}
#> STRING... | toContain PATTERN
toContain()
{
	checkArguments $# 1 &&
	checkNonEmpty "$1" &&

	grep "`escape "$1"`" > "$NULL_PATH"
}

#> containWord STRING PATTERN
containWord()
{
	checkArguments $# 2 &&

	printn "$1" | toContainWord "$2"
}
#> STRING... | toContainWord PATTERN
toContainWord()
{
	checkArguments $# 1 &&
	checkNonEmpty "$1" &&

	grep -w "`escape "$1"`" > "$NULL_PATH"
}

########################################

# Return a substring of STRING, cut from the delimiter FROM (and optionally
# to the delimiter TO).
#> cutBetween STRING FROM [TO]
cutBetween()
{
	checkArguments $# 2 &&

	printn "$1" | toCutBetween "$2" "${3:-}"
}
#> STRING... | toCutBetween FROM [TO]
toCutBetween()
{
	checkArguments $# 1 &&

	cut -d "$1" -f 2 | cut -d "${2:-}" -f 1
}

# Return a substring of STRING, starting at the delimiter FROM.
#> extractFrom STRING FROM
extractFrom()
{
	checkArguments $# 2 &&

	$AWK 'BEGIN {
			if (length("'"$2"'") > 0) {
				from = index("'"$1"'", "'"$2"'")
				if (from > 0) print substr("'"$1"'", from + 1)
				else print "'"$1"'"
			} else {
				print "'"$1"'"
			}
		}'
}
#> STRING... | toExtractFrom FROM
toExtractFrom()
{
	checkArguments $# 1 &&

	$AWK '{
			if (length("'"$1"'") > 0) {
				from = index($0, "'"$1"'")
				if (from > 0) print substr($0, from + 1)
				else print $0
			} else {
				print $0
			}
		}'
}

# Return a substring of STRING, ending at the delimiter TO.
#> extractTo STRING TO
extractTo()
{
	checkArguments $# 2 &&

	$AWK 'BEGIN {
			if (length("'"$2"'") > 0) {
				to = index("'"$1"'", "'"$2"'")
				if (to > 0) print substr("'"$1"'", 1, to - 1)
				else print "'"$1"'"
			} else {
				print "'"$1"'"
			}
		}'
}
#> STRING... | toExtractTo TO
toExtractTo()
{
	checkArguments $# 1 &&

	$AWK '{
			if (length("'"$1"'") > 0) {
				to = index($0, "'"$1"'")
				if (to > 0) print substr($0, 1, to - 1)
				else print $0
			} else {
				print $0
			}
		}'
}

# Return a substring of STRING, starting at the delimiter FROM (and optionally
# ending at the delimiter TO).
#> extract STRING FROM [TO]
extract()
{
	checkArguments $# 2 &&

	extractFrom "$1" "$2" | toExtractTo "${3:-}"
}
#> STRING... | toExtract FROM [TO]
toExtract()
{
	checkArguments $# 1 &&

	toExtractFrom "$1" | toExtractTo "${2:-}"
}

# Return the (1-based) numerical position in STRING of the first character in
# CHARS that matches, 0 otherwise.
#> getIndex STRING CHARS
getIndex()
{
	checkArguments $# 2 &&
	checkNonEmpty "$2" &&

	$AWK 'BEGIN {print index("'"$1"'", "'"$2"'")}'
}
#> STRING... | toGetIndex CHARS
toGetIndex()
{
	checkArguments $# 1 &&
	checkNonEmpty "$1" &&

	$AWK '{print index($0, "'"$1"'")}'
}

#> getLength STRING
getLength()
{
	checkArguments $# 1 &&

	print "$1" | toGetLength
}
#> STRING... | toGetLength
toGetLength()
{
	wc -m | toTrim
}

# Return a substring of STRING, starting at the (1-based) numerical position
# FROM (and optionally ending after LENGTH characters).
#> getSubstring STRING FROM [LENGTH]
getSubstring()
{
	checkArguments $# 2 &&
	checkIndex "$2" &&

	if [ -n "${3:-}" ]
	then
		checkIndex "$3" &&

		$AWK 'BEGIN {print substr("'"$1"'", '$2', '$3')}'
	else
		$AWK 'BEGIN {print substr("'"$1"'", '$2')}'
	fi
}
#> STRING... | toGetSubstring FROM [LENGTH]
toGetSubstring()
{
	checkArguments $# 1 &&
	checkIndex "$1" &&

	if [ -n "${2:-}" ]
	then
		checkIndex "$2" &&

		$AWK '{print substr($0, '$1', '$2')}'
	else
		$AWK '{print substr($0, '$1')}'
	fi
}

#> repeat STRING NUMBER
repeat()
{
	checkArguments $# 2 &&
	checkIndex "$2" &&

	$AWK 'BEGIN {for (i = 0; i < '$2'; ++i) printf "%s", "'"$1"'"}'
}
#> STRING... | toRepeat NUMBER
toRepeat()
{
	checkArguments $# 1 &&
	checkIndex "$1" &&

	$AWK '{for (i = 0; i < '$1'; ++i) printf "%s", $0}'
}

#> split STRING DELIMITERS
split()
{
	checkArguments $# 2 &&

	$AWK 'BEGIN {
			s = "'"$1"'"
			i = index(s, "'"$2"'")
			while (i > 0) {
				print substr(s, 1, i - 1)
				s = substr(s, i + 1)
				i = index(s, "'"$2"'")
			}
			print s
		}'
}
#> STRING... | toSplit DELIMITERS
toSplit()
{
	checkArguments $# 1 &&

	$AWK '{
			s = $0
			i = index(s, "'"$1"'")
			while (i > 0) {
				print substr(s, 1, i - 1)
				s = substr(s, i + 1)
				i = index(s, "'"$1"'")
			}
			print s
		}'
}

#> trim STRING
trim()
{
	checkArguments $# 1 &&

	$AWK 'BEGIN {
			sub(/^[ \t\v\f]+/, "", "'"$1"'")
			sub(/[ \t\v\f]+$/, "", "'"$1"'")
			print
		}'
}
#> STRING... | toTrim
toTrim()
{
	$AWK '{
			sub(/^[ \t\v\f]+/, "", $0)
			sub(/[ \t\v\f]+$/, "", $0)
			print
		}'
}

########################################

#> wrap STRING CHAR
wrap()
{
	checkArguments $# 2 &&

	printn "$1" | toWrap "$2"
}
#> STRING... | toWrap CHAR
toWrap()
{
	checkArguments $# 1 &&

	sed -e 's|^|'"$1"'|' -e 's|$|'"$1"'|'
}

#> unwrap STRING CHAR
unwrap()
{
	checkArguments $# 2 &&

	printn "$1" | toUnwrap "$2"
}
#> STRING... | toUnwrap CHAR
toUnwrap()
{
	checkArguments $# 1 &&

	sed -e 's|^'"$1"'||' -e 's|'"$1"'$||'
}

########################################

#> quote STRING
quote()
{
	checkArguments $# 1 &&

	printn "$1" | toQuote
}
#> STRING... | toQuote
toQuote()
{
	toWrap "$QUOTE"
}

#> unquote STRING
unquote()
{
	checkArguments $# 1 &&

	printn "$1" | toUnquote
}
#> STRING... | toUnquote
toUnquote()
{
	toUnwrap "$QUOTE"
}

########################################
# ARRAY
########################################

#> createArray [ELEMENT]...
createArray()
{
	if [ $# -gt 0 ]
	then
		for element in "$@"
		do
			encodeElement "$element"
		done
	fi
}
#> ELEMENT... | toCreateArray
toCreateArray()
{
	while IFS= read element
	do
		encodeElement "$element"
	done
}

#> appendToArray ARRAY [ELEMENT]...
appendToArray()
{
	checkArguments $# 1 &&

	if [ -n "$1" ]
	then
		printn "$1"
	fi &&
	shift &&
	if [ $# -gt 0 ]
	then
		createArray "$@"
	fi
}
#> ARRAY | toAppendToArray [ELEMENT]...
toAppendToArray()
{
	toPrintn &&
	if [ $# -gt 0 ]
	then
		createArray "$@"
	fi
}

########################################

#> getArraySize ARRAY
getArraySize()
{
	checkArguments $# 1 &&

	if [ -n "$1" ]
	then
		printn "$1" | toGetArraySize
	else
		printn 0
	fi
}
#> ARRAY | toGetArraySize
toGetArraySize()
{
	wc -l | toTrim
}

# Get the element at the (0-based) INDEX in the ARRAY.
#> getElementAt ARRAY INDEX
getElementAt()
{
	checkArguments $# 2 &&
	checkIndex "$2" `getArraySize "$1"` &&

	printn "$1" | toGetElementAt $2
}
#> ARRAY | toGetElementAt INDEX
toGetElementAt()
{
	checkArguments $# 1 &&
	checkIndex "$1" &&

	$AWK -v i=$1 'BEGIN {code = '$URA_ERROR_INVALID'}
		NR == i + 1 {print $1; code = 0}
		END {exit code}' |
	toDecodeElement
}

# Get the index of the first occurrence of ELEMENT in the ARRAY (or return -1).
#> getElementIndex ARRAY ELEMENT
getElementIndex()
{
	checkArguments $# 2 &&

	printn "$1" | toGetElementIndex "$2" ||
		printn -1
}
#> ARRAY | toGetElementIndex ELEMENT
toGetElementIndex()
{
	checkArguments $# 1 &&

	e="`printn "$1" | toEncodeElement`" $AWK '
		BEGIN {e = ENVIRON["e"]; i = -1; code = '$URA_ERROR_INVALID'}
		i < 0 && e == $1 {i = NR - 1; print i; code = 0}
		END {exit code}'
}

# Remove the first occurrence of ELEMENT from the ARRAY.
#> removeElement ARRAY ELEMENT
removeElement()
{
	checkArguments $# 2 &&

	printn "$1" | toRemoveElement "$2"
}
#> ARRAY | toRemoveElement ELEMENT
toRemoveElement()
{
	checkArguments $# 1 &&

	e="`printn "$1" | toEncodeElement`" $AWK '
		BEGIN {e = ENVIRON["e"]; i = -1; code = '$URA_ERROR_INVALID'}
		{if (i < 0 && e == $1) {i = NR - 1; code = 0} else {print $1}}
		END {exit code}'
}

# Remove the element at the (0-based) INDEX from the ARRAY.
#> removeElementAt ARRAY INDEX
removeElementAt()
{
	checkArguments $# 2 &&
	checkIndex "$2" `getArraySize "$1"` &&

	printn "$1" | toRemoveElementAt $2
}
#> ARRAY | toRemoveElementAt INDEX
toRemoveElementAt()
{
	checkArguments $# 1 &&
	checkIndex "$1" &&

	$AWK -v i=$1 'BEGIN {code = '$URA_ERROR_INVALID'}
		{if (i == NR - 1) {code = 0} else {print $1}}
		END {exit code}'
}

########################################

# Return the elements of the ARRAY delimited by a space (and optionally wrapped
# with QUOTE).
#> printArray ARRAY [QUOTE]
printArray()
{
	checkArguments $# 1 &&

	if [ -n "$1" ]
	then
		printn "$1" | toPrintArray "${2:-}"
	fi
}
#> ARRAY | toPrintArray [QUOTE]
toPrintArray()
{
	while IFS= read element
	do
		printn "$element" |
		toDecodeElement |
		toWrap "${1:-}" |
		toAppend ' '
	done
}

# Return the elements of the ARRAY delimited by \0 (and optionally wrapped with
# QUOTE).
#> print0Array ARRAY [QUOTE]
print0Array()
{
	checkArguments $# 1 &&

	if [ -n "$1" ]
	then
		printn "$1" | toPrint0Array "${2:-}"
	fi
}
#> ARRAY | toPrint0Array [QUOTE]
toPrint0Array()
{
	while IFS= read element
	do
		decodeElement "$element" | toWrap "${1:-}" | toPrint0
	done
}

# Return the elements of the ARRAY delimited by \n (and optionally wrapped with
# QUOTE).
#> printnArray ARRAY [QUOTE]
printnArray()
{
	checkArguments $# 1 &&

	if [ -n "$1" ]
	then
		printn "$1" | toPrintnArray "${2:-}"
	fi
}
#> ARRAY | toPrintnArray [QUOTE]
toPrintnArray()
{
	while IFS= read element
	do
		decodeElement "$element" | toWrap "${1:-}"
	done
}

########################################

# Encode an array ELEMENT. Similar to urlencode but only for %, ' and \n.
#> encodeElement ELEMENT
encodeElement()
{
	checkArguments $# 1 &&

	printn "$1" | toEncodeElement
}
#> ELEMENT... | toEncodeElement
toEncodeElement()
{
	sed 's|%|%25|g' |
	sed -e ':a' -e '$!N; s|'"$QUOTE"'|%22|; s|\n|%0A|; ta' |
	toQuote
}

# Decode an array ELEMENT. Similar to urldecode but only for %, ' and \n.
#> decodeElement ELEMENT
decodeElement()
{
	checkArguments $# 1 &&

	printn "$1" | toDecodeElement
}
#> ELEMENT... | toDecodeElement
toDecodeElement()
{
	toUnquote |
	sed -e ':a' -e '$!N; s|%22|'"$QUOTE"'|; s|%0A|\n|; ta' |
	sed 's|%25|%|g'
}

########################################
# ENVIRONMENT
########################################

#> addToPath PATH
addToPath()
{
	checkArguments $# 1 &&

	contain "$PATH" "$1" ||
		(PATH="$PATH:$1"; export PATH)
}

########################################
# FILES
########################################

#> createDir DIR
createDir()
{
	checkArguments $# 1 &&

	testPath "$1" ||
		(mkdir -p "$1" && setPathPermissions "$1")
}
#> DIR... | toCreateDir
toCreateDir()
{
	while IFS= read dir
	do
		createDir "$dir"
	done
}

#> createFile FILE [FORCE]
createFile()
{
	checkArguments $# 1 &&

	if [ ${2:-$FALSE} -eq $TRUE ]
	then
		removePath "$1"
	fi &&
	testPath "$1" ||
		(
			getDir "$1" |
			toCreateDir &&
			touch "$1" &&
			setPathPermissions "$1"
		)
}
#> FILE... | toCreateFile [FORCE]
toCreateFile()
{
	while IFS= read file
	do
		createDir "$file" ${1:-$FALSE}
	done
}

#> prepend FILE [LINE]
prepend()
{
	checkArguments $# 1 &&

	printn "$1" | toPrepend "${2:-}"
}
#> FILE... | toPrepend [LINE]
toPrepend()
{
	while IFS= read file
	do
		checkNonEmpty "$file" &&

		appendLine "${1:-}" "`cat "$file"`" > "$file"
	done
}

########################################

#> copyPath SOURCE TARGET [FORCE]
copyPath()
{
	checkArguments $# 2 &&
	checkPath "$1" &&
	source="`getPath "$1"`" &&
	target="`getPath "$2"`" &&
	if [ -f "$source" -a -d "$target" ]
	then
		target="$target/`getBase "$source"`"
	fi &&

	if [ ${3:-$FALSE} -eq $TRUE ]
	then
		cp -fr "$source" "$target"
	else
		if [ -d "$target" ]
		then
			warn 'Directory '"`quote $target`"' exists.'
		else
			warn 'File '"`quote $target`"' exists.'
		fi &&
		cp -r "$source" "$target"
	fi &&
	setPathPermissions "$target"
}
#> SOURCE... | toCopyPath TARGET [FORCE]
toCopyPath()
{
	checkArguments $# 1 &&

	while IFS= read source
	do
		copyPath "$source" "$1" ${2:-$FALSE}
	done
}

#> removePath PATH
removePath()
{
	checkArguments $# 1 &&
	checkNonEmpty "$1" &&

	rm -fr "$1"
}
#> PATH... | toRemovePath
toRemovePath()
{
	while IFS= read path
	do
		removePath "$path"
	done
}

#> setPathPermissions PATH...
setPathPermissions()
{
	checkArguments $# 1 &&

	for path in "$@"
	do
		checkPath "$path" &&

		chmod -R $FILE_SYSTEM_PERMISSIONS "$path"
	done
}
#> PATH... | toSetPathPermissions
toSetPathPermissions()
{
	while IFS= read path
	do
		setPathPermissions "$path"
	done
}

#> testPath PATH
testPath()
{
	checkArguments $# 1 &&
	checkNonEmpty "$1" &&

	test -d "$1" || test -f "$1"
}
#> PATH... | toTestPath
toTestPath()
{
	while IFS= read path
	do
		testPath "$path"
	done
}

########################################

#> getPath [PATH]
getPath()
{
	if [ $# -gt 0 ]
	then
		checkNonEmpty "$1" &&
		path="$1"
	else
		path="$0"
	fi &&

	(which "$path" > "$NULL_PATH" 2>&1 && which "$path") ||
		(testCommand 'readlink' && readlink -f "$path") ||
		(cd "`getDir "$path"`" && printn "`pwd`/`getBase "$path"`")
}
#> PATH... | toGetPath
toGetPath()
{
	while IFS= read path
	do
		getPath "$path"
	done
}

#> getBase [PATH]
getBase()
{
	if [ -n "${1:-}" ]
	then
		path="$1"
	else
		path="$0"
	fi &&

	basename "$path"
}
#> PATH... | toGetBase
toGetBase()
{
	while IFS= read path
	do
		getBase "$path"
	done
}

#> getBaseWithoutExtension [PATH]
getBaseWithoutExtension()
{
	getBase "${1:-}" | sed 's|\.[^.]*$||'
}
#> PATH... | toGetBaseWithoutExtension
toGetBaseWithoutExtension()
{
	while IFS= read path
	do
		getBaseWithoutExtension "$path"
	done
}

#> getBaseExtension [PATH]
getBaseExtension()
{
	getBase "${1:-}" | sed 's|^[^.]*\.||'
}
#> PATH... | toGetBaseExtension
toGetBaseExtension()
{
	while IFS= read path
	do
		getBaseExtension "$path"
	done
}

#> getDir [PATH]
getDir()
{
	dirname "${1:-`pwd`}"
}
#> PATH... | toGetDir
toGetDir()
{
	while IFS= read path
	do
		getDir "$path"
	done
}

########################################

#> getFileHeader [FILE] [DELIMITER]
getFileHeader()
{
	if [ -n "${1:-}" ]
	then
		checkFile "$1" &&
		file="$1"
	else
		file="`getPath`"
	fi &&
	if [ -n "${2:-}" ]
	then
		delimiter="$2"
	else
		delimiter='^##'
	fi &&

	$AWK 'BEGIN {i = 0}
		/'"$delimiter"'/ {if (i == 1) exit; else ++i; next} i == 1'\
		"$file"
}

########################################
# LEVEL
########################################

#> resetLevel
resetLevel()
{
	LEVEL=$INITIAL_LEVEL
}

#> updateLevel LEVEL_OPERATOR
updateLevel()
{
	checkArguments $# 1 &&

	case "$1" in
		+)	incrementLevel
			;;
		-)	decrementLevel
			;;
		*)	fail 'Unknown operator: '"`quote "$1"`" $URA_ERROR_ARGUMENTS
			;;
	esac
}

########################################

#> incrementLevel
incrementLevel()
{
	LEVEL=`increment $LEVEL`
}

#> decrementLevel
decrementLevel()
{
	LEVEL=`decrement $LEVEL`
}

########################################
# MESSAGES
########################################

#> printBar [LENGTH]
printBar()
{
	printn "`repeat '#' "${1:-$LINE_LENGTH}"`" |
	toPrintToLog
}

#> printPrefix [TYPE]
printPrefix()
{
	printn '['"`date +'%Y-%m-%d %H:%M:%S'`"']' |
	if [ -n "${1:-}" ]
	then
		toAppendn '['"$1"'] '
	else
		toAppendn ' '
	fi
}

#> printMessage STRING [TYPE]
printMessage()
{
	checkArguments $# 1 &&

	if [ -n "$1" ]
	then
		printPrefix "${2:-}" |
		toAppendn "$1"
	else
		printn
	fi |
	toPrintToLog
}
#> LINE... | toPrintMessage [TYPE]
toPrintMessage()
{
	while IFS= read line
	do
		printMessage "$line" "${1:-}"
	done
}

#> printTitle STRING
printTitle()
{
	checkArguments $# 1 &&

	if [ -n "$1" ]
	then
		length=`getLength "$1"` &&
		number=`compute $LINE_LENGTH - $length - 3` &&
		printn '# '"$1" |
		if [ $number -gt 0 ]
		then
			toAppendn ' '"`repeat '#' $number`"
		else
			toPrintn
		fi |
		toPrintToLog
	else
		printBar
	fi
}
#> LINE... | toPrintTitle
toPrintTitle()
{
	while IFS= read line
	do
		printTitle "$line"
	done
}

#> printStackTrace [FROM]
printStackTrace()
{
	increment "${1:-0}" | toGetStackTrace | toPrintToLog
}

# Print the last access denied of SERVICE (if exists).
#> printAccessDenied SERVICE
printAccessDenied()
{
	checkArguments $# 1 &&
	checkNonEmpty "$1" &&

	# Get the last access denied from the audit log
	entry="`captureFile "$AUDIT_LOG_PATH" "$1" | toContain 'denied' | tail -1`" &&

	if [ -n "$entry" ]
	then
		# Extract information from the last access denied
		pid="`printn "$entry" | sed -n 's|.*pid\=\([^ ]*\).*|\1|p'`" &&
		comm="`printn "$entry" | sed -n 's|.*comm\=\"\([^\"]*\).*|\1|p'`" &&
		name="`printn "$entry" | sed -n 's|.*name\=\"\([^\"]*\).*|\1|p'`" &&
		dev="`printn "$entry" | sed -n 's|.*dev\=\"\([^\"]*\).*|\1|p'`" &&
		ino="`printn "$entry" | sed -n 's|.*ino\=\([^ ]*\).*|\1|p'`" &&
		scontext="`printn "$entry" | sed -n 's|.*scontext\=\([^ ]*\).*|\1|p'`" &&
		tcontext="`printn "$entry" | sed -n 's|.*tcontext\=\([^ ]*\).*|\1|p'`" &&
		tclass="`printn "$entry" | sed -n 's|.*tclass\=\([^ ]*\).*|\1|p'`" &&

		# Formulate and print the error message
		printn 'The '"`quote "$comm"`"' process with PID '"`quote "$pid"`"\
			'tried to read a '"`quote "$tclass"`"' called '"`quote "$name"`"\
			'on a file system hosted on the '"`quote "$dev"`"' device. This'\
			'file has inode number '"`quote "$ino"`"', and has the security'\
			'context '"`quote "$scontext"`"' assigned to it. The'\
			"`quote "$comm"`"' process itself is running with the'\
			"`quote "$tcontext"`"' context (domain).' |
		toPrintToLog
	fi
}

########################################

#> verb STRING [LEVEL_OPERATOR]
verb()
{
	if [ $VERBOSE -eq $TRUE ]
	then
		checkArguments $# 1 &&

		# Print the specified message
		if [ $LIST -eq $TRUE ]
		then
			case $LEVEL in
				-1)	printTitle "$1" | toCyan 1>&2
					;;
				0)	printMessage 'o '"$1" 1>&2
					;;
				*)	indentation="`repeat '  ' $LEVEL`" &&
					if [ `compute $LEVEL % 2` -eq 0 ]
					then
						printMessage "$indentation"'- '"$1" 1>&2
					else
						printMessage "$indentation"'* '"$1" 1>&2
					fi
					;;
			esac
		else
			printMessage "$1" 1>&2
		fi &&

		# Update the hierarchy level
		if [ -n "${2:-}" ]
		then
			updateLevel "$2"
		fi
	fi
}
#> LINE... | toVerb [LEVEL_OPERATOR]
toVerb()
{
	while IFS= read line
	do
		verb "$line"
	done &&

	# Update the hierarchy level
	if [ -n "${1:-}" ]
	then
		updateLevel "$1"
	fi
}

#> info STRING
info()
{
	checkArguments $# 1 &&

	printMessage "$1" "$LOG_INFO_TYPE" | toGreen 1>&2
}
#> LINE... | toInfo
toInfo()
{
	while IFS= read line
	do
		info "$line"
	done
}

#> warn STRING
warn()
{
	checkArguments $# 1 &&

	printMessage "$1" "$LOG_WARN_TYPE" | toYellow 1>&2
}
#> LINE... | toWarn
toWarn()
{
	while IFS= read line
	do
		warn "$line"
	done
}

#> fail STRING [ERROR_CODE]
fail()
{
	checkArguments $# 1 &&

	printMessage "$1" "$LOG_FAIL_TYPE" | toRed 1>&2 &&
	printStackTrace 1 | toRed 1>&2 &&
	exit ${2:-$URA_ERROR_INVALID}
}
#> LINE... | toFail [ERROR_CODE]
toFail()
{
	while IFS= read line
	do
		printMessage "$line" "$LOG_FAIL_TYPE" | toRed 1>&2 &&
		printStackTrace 1 | toRed 1>&2 &&
		exit ${1:-$URA_ERROR_INVALID}
	done
}

########################################

#> startList STRING
startList()
{
	checkArguments $# 1 &&

	verb "$1" \+
}

#> endList
endList()
{
	decrementLevel
}

########################################
# ARGUMENTS
########################################

#> loadArguments NUMBER [ARGUMENT]...
loadArguments()
{
	checkArguments $# 1 &&
	checkIndex "$1" &&
	number=$1 &&
	shift &&

	# Load the specified options
	if [ $number -gt 0 ]
	then
		while getopts "$OPTIONS" option
		do
			case "$option" in
				h)	OPTS="`appendToArray "$OPTS" "$option"`" &&
					printManual &&
					exit 0
					;;
				i)	OPTS="`appendToArray "$OPTS" "$option"`" &&
					printVersion &&
					exit 0
					;;
				v)	OPTS="`appendToArray "$OPTS" "$option"`" &&
					VERBOSE=$TRUE
					;;
				:)	printUsage &&
					fail 'Option requires an argument.' $URA_ERROR_ARGUMENTS
					;;
				\?)	printUsage &&
					fail 'Option '"$option"' is invalid.' $URA_ERROR_ARGUMENTS
					;;
				*)	if [ -n "${OPTARG:-}" ]
					then
						value="`getOptionValue "$OPTARG"`" &&
						case "$option" in
							l)	INITIAL_LEVEL=$value &&
								resetLevel
								;;
						esac &&
						OPTS="`appendToArray "$OPTS" "$option=$value"`"
					else
						OPTS="`appendToArray "$OPTS" "$option"`"
					fi
					;;
			esac
		done
	fi &&

	# Set the number of options
	OPTS_NUMBER=`getArraySize "$OPTS"` &&

	# Set the number of arguments (without options)
	ARGS_NUMBER=`compute $number - $OPTS_NUMBER` &&

	# Load the specified arguments (without options)
	if [ $ARGS_NUMBER -gt 0 ]
	then
		offset=$OPTS_NUMBER &&
		i=0 &&
		for argument in "$@"
		do
			if [ $i -ge $offset ]
			then
				ARGS="`appendToArray "$ARGS" "$argument"`"
			fi &&
			i=`increment $i`
		done
	fi
}

########################################

# Get the flags (LEVEL and VERBOSE).
#> getFlags
getFlags()
{
	printn '-l'$LEVEL |
	if [ $VERBOSE -eq $TRUE ]
	then
		toAppendn ' -v'
	else
		toPrintn
	fi
}

#> getOptionValue OPTION
getOptionValue()
{
	checkArguments $# 1 &&
	checkNonEmpty "$1" &&

	extractFrom "$1" \=
}

########################################

#> printManual
printManual()
{
	getFileHeader | sed -e 's|^#||g' -e 's|<NAME>|'"`getBase`"'|g'
}

#> printUsage
printUsage()
{
	info 'Usage: '"`printManual |
		$AWK 'BEGIN {i = 0}
			/SYNOPSIS/ {i = 1; next}
			/^$/ {if (i == 1) exit} i' |
		toTrim`"
}

#> printVersion
printVersion()
{
	cat "$URA_VERSION_PATH" |
	sed -e 's|<AUTHOR>|'"$AUTHOR"'|g'\
		-e 's|<DATE>|'"$DATE"'|g'\
		-e 's|<LICENSE>|'"$LICENSE"'|g'\
		-e 's|<NAME>|'"`getBase`"'|g'\
		-e 's|<VENDOR>|'"$VENDOR"'|g'\
		-e 's|<VERSION>|'"$VERSION"'|g'
}

########################################
# USERS
########################################

#> getUserId
getUserId()
{
	id | toExtract \= \(
}

#> getUsername
getUsername()
{
	id | toExtract \( \)
}

########################################

#> addUserToGroup USER GROUP
addUserToGroup()
{
	checkArguments $# 2 &&
	checkNonEmpty "$1" &&

	# Create the group (if not exists)
	createGroup "$2" &&

	# Add the user to the group
	if [ $SOLARIS -eq $TRUE ]
	then
		usermod -G "$2" "$1" > "$NULL_PATH" 2>&1 ||
			rolemod -G "$2" "$1" > "$NULL_PATH" 2>&1
	else
		usermod -G "$2" "$1" > "$NULL_PATH" 2>&1
	fi ||
		fail 'User '"`quote "$1"`"' cannot be added to the group '"`quote "$2"`"'.'
}

#> createGroup GROUP
createGroup()
{
	checkArguments $# 1 &&

	testGroup "$1" ||
		groupadd "$1"
}

#> testGroup GROUP
testGroup()
{
	checkArguments $# 1 &&
	checkNonEmpty "$1" &&

	getent group "$1" > "$NULL_PATH" 2>&1
}
#> GROUP... | toTestGroup
toTestGroup()
{
	while IFS= read group
	do
		testGroup "$group"
	done
}

########################################
# LOGS
########################################

#> addLogger [USER]
addLogger()
{
	addUserToGroup "${1:-`getUsername`}" "$LOG_GROUP"
}

#> createLog [LOG] [FORCE]
createLog()
{
	LOG=$TRUE &&
	if [ -n "${1:-}" ]
	then
		LOG_PATH="$1"
	fi &&
	createLogGroup &&
	addLogger &&
	createFile "$LOG_PATH" ${2:-$FALSE}
}

#> createLogGroup
createLogGroup()
{
	getent group "$LOG_GROUP" > "$NULL_PATH" 2>&1 ||
		(
			verb 'Create the log group' &&
			groupadd "$LOG_GROUP"
		) &&
	testPath "$URA_LOG_DIR" ||
		(
			verb 'Create the log directory' &&
			createDir "$URA_LOG_DIR" &&
			chown -R 'root':"$LOG_GROUP" "$URA_LOG_DIR"
		)
}

#> printToLog STRING
printToLog()
{
	checkArguments $# 1 &&

	printn "$1" | toPrintToLog
}
#> STRING... | toPrintToLog
toPrintToLog()
{
	if [ $LOG -eq $TRUE ]
	then
		tee -a "$LOG_PATH" | toPrintn
	else
		toPrintn
	fi
}

########################################
# CHECKS
########################################

# Check if the FOUND number of arguments corresponds to the EXPECTED number
# (exit otherwise).
#> checkArguments [FOUND] EXPECTED
checkArguments()
{
	if [ -n "${1:-}" ]
	then
		if [ -n "${2:-}" ]
		then
			found=$1 &&
			expected=$2
		else
			found=$ARGS_NUMBER &&
			expected=$1
		fi &&
		if [ $found -lt $expected ]
		then
			(printUsage) &&
			fail 'Wrong number of arguments: '$expected' expected but '$found' found'\
				$URA_ERROR_ARGUMENTS
		fi
	else
		fail 'Wrong number of arguments: 1 expected but '$#' found'\
			$URA_ERROR_ARGUMENTS
	fi
}

########################################

# Check if the NUMBER is an integer (exit otherwise).
#> checkInteger NUMBER
checkInteger()
{
	checkArguments $# 1 &&
	testInteger "$1" ||
		fail 'Wrong number format: integer expected but '"`quote "$1"`"' found'\
			$URA_ERROR_ARGUMENTS
}

########################################

# Check if the NUMBER is strictly less than the LIMIT (exit otherwise).
#> checkLessThan NUMBER LIMIT
checkLessThan()
{
	checkArguments $# 2 &&
	checkInteger "$1" &&
	checkInteger "$2" &&
	if [ $1 -ge $2 ]
	then
		fail 'Out of range: '$1' is greater or equal to '$2\
			$URA_ERROR_ARGUMENTS
	fi
}

# Check if the NUMBER is less or equal to the LIMIT (exit otherwise).
#> checkLessOrEqualTo NUMBER LIMIT
checkLessOrEqualTo()
{
	checkArguments $# 2 &&
	checkInteger "$1" &&
	checkInteger "$2" &&
	if [ $1 -gt $2 ]
	then
		fail 'Out of range: '$1' is greater than '$2\
			$URA_ERROR_ARGUMENTS
	fi
}

# Check if the NUMBER is strictly greater than the LIMIT (exit otherwise).
#> checkGreaterThan NUMBER LIMIT
checkGreaterThan()
{
	checkArguments $# 2 &&
	checkInteger "$1" &&
	checkInteger "$2" &&
	if [ $1 -le $2 ]
	then
		fail 'Out of range: '$1' is less or equal to '$2\
			$URA_ERROR_ARGUMENTS
	fi
}

# Check if the NUMBER is greater or equal to the LIMIT (exit otherwise).
#> checkGreaterOrEqualTo NUMBER LIMIT
checkGreaterOrEqualTo()
{
	checkArguments $# 2 &&
	checkInteger "$1" &&
	checkInteger "$2" &&
	if [ $1 -lt $2 ]
	then
		fail 'Out of range: '$1' is less than '$2\
			$URA_ERROR_ARGUMENTS
	fi
}

########################################

# Check if the NUMBER is non-negative (exit otherwise).
#> checkNonNegative NUMBER
checkNonNegative()
{
	checkArguments $# 1 &&
	checkInteger "$1" &&
	checkGreaterOrEqualTo $1 0
}

# Check if the NUMBER is positive (exit otherwise).
#> checkPositive NUMBER
checkPositive()
{
	checkArguments $# 1 &&
	checkInteger "$1" &&
	checkGreaterThan $1 0
}

########################################

# Check if the STRING is non-empty (exit otherwise).
#> checkNonEmpty STRING
checkNonEmpty()
{
	checkArguments $# 1 &&
	if [ -z "$1" ]
	then
		fail 'Empty string' $URA_ERROR_ARGUMENTS
	fi
}

########################################

# Check if the ARRAY has the SIZE (exit otherwise).
#> checkArraySize ARRAY SIZE
checkArraySize()
{
	checkArguments $# 2 &&
	checkIndex "$2" &&
	size=`getArraySize "$1"` &&
	if [ $size -ne $2 ]
	then
		fail 'Wrong array size: '$2' expected but '$size' found'\
			$URA_ERROR_ARGUMENTS
	fi
}

# Check if the INDEX is in [0, LIMIT) (exit otherwise).
#> checkIndex INDEX [LIMIT]
checkIndex()
{
	checkArguments $# 1 &&
	checkInteger "$1" &&
	checkNonNegative $1 &&
	if [ -n "${2:-}" ]
	then
		checkInteger "$2" &&
		checkNonNegative $2 &&
		checkLessThan $1 $2
	fi
}

########################################

# Check if the DIR exists (exit otherwise).
#> checkDir DIR
checkDir()
{
	checkArguments $# 1 &&
	path="`getPath "$1"`" &&
	if [ ! -d "$path" ]
	then
		fail 'Directory '"`quote "$path"`"' cannot be accessed.'\
			$URA_ERROR_PERMISSIONS
	fi
}

# Check if the FILE exists (exit otherwise).
#> checkFile FILE
checkFile()
{
	checkArguments $# 1 &&
	path="`getPath "$1"`" &&
	if [ ! -f "$path" ]
	then
		fail 'File '"`quote "$path"`"' cannot be accessed.'\
			$URA_ERROR_PERMISSIONS
	fi
}

# Check if the PATH exists (exit otherwise).
#> checkPath PATH
checkPath()
{
	checkArguments $# 1 &&
	testPath "$1" ||
		fail 'Path '"`quote "$1"`"' cannot be accessed.' $URA_ERROR_PERMISSIONS
}

# Check if the PATH does not exist (exit otherwise).
#> checkNotPath PATH
checkNotPath()
{
	checkArguments $# 1 &&
	if testPath "$1"
	then
		path="`getPath "$1"`" &&
		if [ -d "$path" ]
		then
			fail 'Directory '"`quote "$path"`"' exists.' $URA_ERROR_EXISTENCE
		else
			fail 'File '"`quote "$path"`"' exists.' $URA_ERROR_EXISTENCE
		fi
	fi
}

########################################

# Check if the user is root (exit otherwise).
#> checkRoot
checkRoot()
{
	if [ `getUserId` -ne 0 ]
	then
		fail 'User must be root.' $URA_ERROR_PERMISSIONS
	fi
}

# Check if the user is not root (exit otherwise).
#> checkNotRoot
checkNotRoot()
{
	if [ `getUserId` -eq 0 ]
	then
		fail 'User must not be root.' $URA_ERROR_PERMISSIONS
	fi
}


################################################################################
# END
################################################################################

	if [ -z "${URA_NAME:-}" ]
	then
		fail 'URANUS profile is not loaded.' 1
	fi
fi
