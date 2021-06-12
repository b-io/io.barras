#!/bin/sh
################################################################################
#NAME
#    <NAME> - contain utility functions for creating packages
#
#SYNOPSIS
#    <NAME>
#
#AUTHOR
#    Written by Florian Barras (florian@barras.io).
#
#COPYRIGHT
#    Copyright © 2013-2021 Florian Barras <https://barras.io>.
#    The MIT License (MIT) <https://opensource.org/licenses/MIT>.
################################################################################


################################################################################
# BEGIN
################################################################################

if [ -z "${URA_PMS:-}" ]
then
	URA_PMS="$0"


################################################################################
# PROFILES
################################################################################

# Load the required URANUS profiles
: "${URA_PROFILE_DIR:=/etc/profile.d}"
if [ -z "${URA_PROFILE:-}" ]
then
	. "$URA_PROFILE_DIR"/'uranus.sh'
fi
if [ -z "${URA_PMS_PROFILE:-}" ]
then
	. "$URA_PROFILE_DIR"/'uranus-pms.sh'
fi


################################################################################
# IMPORTS
################################################################################

# Import URANUS
. "$URA_PATH"

################################################################################

# Set the URANUS PMS settings for package information
: "${PMS_ARCH:=`arch`}"
: "${PMS_CATEGORY:=application}"
: "${PMS_DEPENDENCIES:=}"
: "${PMS_GROUP:=Development/Libraries}"
: "${PMS_LICENSE:=}"
: "${PMS_NAME:=}"
: "${PMS_PACKAGER:=}"
: "${PMS_RELEASE:=a}"
: "${PMS_SUMMARY:=}"
: "${PMS_URL:=}"
: "${PMS_VENDOR:=}"
: "${PMS_VERSION:=1.0.0}"

# Set the URANUS PMS settings for package names
: "${PMS_LIB_NAMES:=`createArray '*.sh'`}"
: "${PMS_PROFILE_NAMES:=`createArray "$URA_PROFILE_NAME"`}"

# Set the URANUS PMS settings for package lists
: "${PMS_SOURCE_LIST_PATH:=$URA_PMS_TMP_DIR/$PMS_NAME.source.list}"
: "${PMS_TARGET_LIST_PATH:=$URA_PMS_TMP_DIR/$PMS_NAME.target.list}"

# Set the URANUS PMS settings for target directories
: "${PMS_BIN_DIR:=$BIN_DIR}"
: "${PMS_INCLUDE_DIR:=$INCLUDE_DIR/$PMS_NAME}"
: "${PMS_LIB_DIR:=$LIB_DIR/$PMS_NAME}"
: "${PMS_PROFILE_DIR:=$PROFILE_DIR}"

# Import URANUS PKG and RPM
. "$URA_PKG_PATH"
. "$URA_RPM_PATH"


################################################################################
# FUNCTIONS
################################################################################

#> listPackage SOURCE_DIR
listPackage()
{
	checkArguments $# 1 &&
	checkDir "$1" &&
	sourceDir="`getPath "$1"`" &&

	startList 'List the files of the package' &&
		# Create the lists of the source and target files
		createFile "$PMS_SOURCE_LIST_PATH" $TRUE &&
		createFile "$PMS_TARGET_LIST_PATH" $TRUE &&

		verb 'List the profiles' &&
		# Create the global profile
		globalProfileName="`getElementAt "$PMS_PROFILE_NAMES" 0`"'-all' &&
		globalProfilePath="$sourceDir/$globalProfileName" &&
		createFile "$globalProfilePath" $TRUE &&
		# List the profiles
		ura-find -f -p="$sourceDir" `printArray "$PMS_PROFILE_NAMES"` |
		while IFS= read path
		do
			if [ "$path" != "$globalProfilePath" ]
			then
				quote "$path" >> "$PMS_SOURCE_LIST_PATH" &&
				pathDir="`getDir "$path"`" &&
				if [ "$pathDir" = "$sourceDir" ]
				then
					targetName="$PMS_NAME"'.sh'
				else
					targetName="$PMS_NAME"'-'"`getBase "$pathDir"`"'.sh'
				fi &&
				targetPath="`quote "$PMS_PROFILE_DIR/$targetName"`" &&
				printn "$targetPath" >> "$PMS_TARGET_LIST_PATH" &&
				# Update the global profile
				if [ "`getDir "$path"`" = "$sourceDir" ]
				then
					prepend "$globalProfilePath" '. '"$targetPath"
				else
					printn '. '"$targetPath" >> "$globalProfilePath"
				fi
			fi
		done &&
		prepend "$globalProfilePath" '#!/bin/sh' &&
		# Add the global profile to the lists
		quote "$globalProfilePath" >> "$PMS_SOURCE_LIST_PATH" &&
		quote "$PMS_PROFILE_DIR/$PMS_NAME"'-all.sh' >> "$PMS_TARGET_LIST_PATH" &&
		PMS_PROFILE_NAMES="`appendToArray "$PMS_PROFILE_NAMES" "$globalProfileName"`" &&

		verb 'List the libraries' &&
		# Create the global library
		globalLibName="$PMS_NAME"'-all.sh' &&
		globalLibPath="$sourceDir/$globalLibName" &&
		createFile "$globalLibPath" $TRUE &&
		# List the libraries
		ura-find -f -p="$sourceDir" `printArray "$PMS_LIB_NAMES"` |
		while IFS= read path
		do
			targetBase="`getBase "$path"`" &&
			targetPath="`quote "$PMS_LIB_DIR/$targetBase"`" &&
			quote "$path" >> "$PMS_SOURCE_LIST_PATH" &&
			printn "$targetPath" >> "$PMS_TARGET_LIST_PATH" &&
			# Update the global library
			if [ "$path" != "$globalLibPath" ]
			then
				if [ "`getDir "$path"`" = "$sourceDir" ]
				then
					prepend "$globalLibPath" '. '"$targetPath"
				else
					printn '. '"$targetPath" >> "$globalLibPath"
				fi
			fi
		done &&
		prepend "$globalLibPath" '#!/bin/sh' &&

		verb 'List the includes' &&
		pathDir="`getDir "$sourceDir"`" &&
		ura-find -f -p="$sourceDir" -r -w='*.*' -n `printArray "$PMS_LIB_NAMES"` `printArray "$PMS_PROFILE_NAMES"` |
		while IFS= read path
		do
			if contain "`getBase "$path"`" '.'
			then
				quote "$pathDir/$path" >> "$PMS_SOURCE_LIST_PATH" &&
				path="`extractFrom "$path" '/'`" &&
				quote "$PMS_INCLUDE_DIR/$path" >> "$PMS_TARGET_LIST_PATH"
			fi
		done &&

		verb 'List the executables' &&
		ura-find -f -p="$sourceDir" -n '*.*' `printArray "$PMS_LIB_NAMES"` `printArray "$PMS_PROFILE_NAMES"` |
		while IFS= read path
		do
			quote "$path" >> "$PMS_SOURCE_LIST_PATH" &&
			quote "$PMS_BIN_DIR/`getBase "$path"`" >> "$PMS_TARGET_LIST_PATH"
		done &&
	endList
}

################################################################################

#> deployPackage SOURCE_DIR
deployPackage()
{
	checkArguments $# 1 &&
	checkDir "$1" &&

	listPackage "$1" &&

	startList 'Deploy the files of the package' &&
		targetPaths="`cat "$PMS_TARGET_LIST_PATH" | toCreateArray`" &&
		index=0 &&
		while IFS= read sourcePath
		do
			sourcePath="`unquote "$sourcePath"`" &&
			targetPath="`getElementAt "$targetPaths" $index | toUnquote`" &&
			createDir "`getDir "$targetPath"`" &&
			install -p "$sourcePath" "$targetPath" &&
			index=`increment $index`
		done < "$PMS_SOURCE_LIST_PATH" &&
	endList &&
	removePath "$URA_PMS_TMP_DIR"
}

################################################################################

#> createPackage SOURCE_DIR
createPackage()
{
	checkArguments $# 1 &&
	checkDir "$1" &&

	listPackage "$1" &&

	if [ $SOLARIS -eq $TRUE ]
	then
		createPkgFile
	else
		createRpmFile
	fi &&
	removePath "$URA_PMS_TMP_DIR"
}

#> createPackageLog [FORCE]
createPackageLog()
{
	createLog "`getPackageLogPath`" ${1:-$FALSE}
}

################################################################################

#> getPackageName
getPackageName()
{
	checkNonEmpty "$PMS_NAME" &&
	checkNonEmpty "$PMS_VERSION" &&
	checkNonEmpty "$PMS_RELEASE" &&
	checkNonEmpty "$PMS_ARCH" &&

	printn "$PMS_NAME"'-'"$PMS_VERSION"'-'"$PMS_RELEASE"'.'"$PMS_ARCH"
}

#> getPackageFilename
getPackageFilename()
{
	if [ $SOLARIS -eq $TRUE ]
	then
		getPkgFilename
	else
		getRpmFilename
	fi
}

#> getPackageLogPath
getPackageLogPath()
{
	if [ $SOLARIS -eq $TRUE ]
	then
		getPkgLogPath
	else
		getRpmLogPath
	fi
}

################################################################################

#> installPackage [FILENAME]
installPackage()
{
	if [ $SOLARIS -eq $TRUE ]
	then
		installPkgFile "${1:-}"
	else
		installRpmFile "${1:-}"
	fi
}

#> uninstallPackage [NAME]
uninstallPackage()
{
	if [ $SOLARIS -eq $TRUE ]
	then
		uninstallPkgFile "${1:-}"
	else
		uninstallRpmFile "${1:-}"
	fi
}

################################################################################

#> testUraPms
testUraPms()
{
	testUraPkg ||
		testUraRpm
}

#> checkUraPms
checkUraPms()
{
	testUraPms ||
		fail 'URANUS PMS is not available.' $URA_ERROR_INVALID
}


################################################################################
# END
################################################################################

	if [ -z "${URA_PMS_NAME:-}" ]
	then
		fail 'URANUS PMS profile is not loaded.' 1
	fi
fi
