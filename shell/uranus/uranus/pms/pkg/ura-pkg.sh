#!/bin/sh
################################################################################
#NAME
#    <NAME> - contain utility functions for creating (Solaris) PKG files
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

if [ -z "${URA_PKG:-}" ]
then
	URA_PKG="$0"


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

# Import the required URANUS libraries
. "$URA_PATH"
. "$URA_PMS_PATH"


################################################################################
# FUNCTIONS
################################################################################

#> createPkgFile
createPkgFile()
{
	checkUraPkg &&
	checkFile "$PMS_SOURCE_LIST_PATH" &&
	checkFile "$PMS_TARGET_LIST_PATH" &&

	# The package filename and log path
	filename="`getPkgFilename`" &&
	logPath="`getPkgLogPath`" &&

	startList 'Prepare' &&
		createPackageLog &&

		if [ "$PMS_SOURCE_LIST_PATH" != "$PMS_TARGET_LIST_PATH" ]
		then
			startList 'Deploy the files to be packaged' &&
				targetList="`cat "$PMS_TARGET_LIST_PATH" | toCreateArray`" &&
				index=0 &&
				while IFS= read sourcePath
				do
					sourcePath="`unquote "$sourcePath"`" &&
					targetPath="`getElementAt "$targetList" $index | toUnquote`" &&
					targetDir="`getDir "$targetPath"`" &&
					if [ ! -d "$targetDir" ]
					then
						createDir "$targetDir" &&
						printn "$targetDir"
					fi &&
					copyPath "$sourcePath" "$targetPath" &&
					printn "$targetPath" &&
					index=`increment $index`
				done < "$PMS_SOURCE_LIST_PATH" > "$PMS_TARGET_LIST_PATH"
			endList
		fi &&

		verb 'Remove the previous package' &&
		removePath "$filename" &&

		verb 'Create the temporary directory' &&
		createDir "$URA_PKG_TMP_DIR" &&

		verb 'Create the information file' &&
		pkginfoPath="$URA_PKG_TMP_DIR"/'pkginfo' &&
		printn "PKG='$PMS_NAME'
NAME='$PMS_NAME'
DESC='$PMS_SUMMARY <$PMS_URL> licensed under $PMS_LICENSE'
CATEGORY='$PMS_CATEGORY'
ARCH='$PMS_ARCH'
VERSION='release $PMS_VERSION-$PMS_RELEASE'
VENDOR='$PMS_VENDOR'" > "$pkginfoPath" &&

		verb 'Create the prototype file' &&
		prototypePath="$URA_PKG_TMP_DIR"/'prototype' &&
		printn 'i pkginfo='"$pkginfoPath"'
! default 0755 root bin' > "$prototypePath" &&
		cat "$PMS_TARGET_LIST_PATH" | pkgproto >> "$prototypePath" &&
	endList &&

	startList 'Build the package' &&
		pkgmk -o -r / -d "$URA_PKG_TMP_DIR" -f "$prototypePath" >> "$logPath" 2>&1 ||
			tail -1 "$logPath" | toFail &&
		targetDir="`pwd`" &&
		cd "$URA_PKG_TMP_DIR" &&
		zip -qr "$targetDir/$filename" "$PMS_NAME" &&
		cd "$targetDir" &&
	endList &&

	verb 'Clean' &&
	removePath "$URA_PKG_TMP_DIR"
}

################################################################################

#> getPkgFilename
getPkgFilename()
{
	printn "`getPackageName`"'.pkg'
}

#> getPkgLogPath
getPkgLogPath()
{
	printn "$URA_PKG_LOG_DIR/`getPackageName`"'.log'
}

################################################################################

#> installPkgFile [FILENAME]
installPkgFile()
{
	checkUraPkg &&
	if [ -n "${1:-}" ]
	then
		filename="$1"
	else
		filename="`getPkgFilename`"
	fi &&
	checkFile "$filename" &&

	verb 'Unzip the package' &&
	targetDir="`getBaseWithoutExtension "$filename"`" &&
	createDir "$targetDir" &&
	unzip -q "$filename" -d "$targetDir" &&
	package="`ls "$targetDir"`" &&

	startList 'Uninstall the previous package (if it exists)' &&
		uninstallPkgFile "$package" &&
	endList &&

	verb 'Install the package' &&
	pkgadd -d "$targetDir" "$package" &&

	verb 'Clean' &&
	removePath "$targetDir"
}

#> uninstallPkgFile [NAME]
uninstallPkgFile()
{
	checkUraPkg &&
	if [ -n "${1:-}" ]
	then
		package="$1"
	else
		checkNonEmpty "$PMS_NAME" &&
		package="$PMS_NAME"
	fi &&

	if pkginfo -q "$package"
	then
		verb 'Uninstall the package' &&
		pkgrm -n "$package" || true &&
		pkgchk -v "$package"
	fi
}

################################################################################

#> testUraPkg
testUraPkg()
{
	testCommand 'pkgmk'
}

#> checkUraPkg
checkUraPkg()
{
	testUraPkg ||
		fail 'URANUS PKG is not available.' $URA_ERROR_INVALID
}


################################################################################
# END
################################################################################

	if [ -z "${URA_PMS_NAME:-}" ]
	then
		fail 'URANUS PMS profile is not loaded.' 1
	fi
fi
