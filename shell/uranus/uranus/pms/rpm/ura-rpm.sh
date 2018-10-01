#!/bin/sh
################################################################################
#NAME
#    <NAME> - contain utility functions for creating (Red Hat) RPM files
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

if [ -z "${URA_RPM:-}" ]
then
	URA_RPM="$0"


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
. "$URA_YUM_PATH"


################################################################################
# SETTINGS
################################################################################

# The path to the RPM MACROS file
: "${RPM_MACROS_PATH:=$HOME/.rpmmacros}"

# The build directory
: "${RPM_BUILD_DIR:=$HOME/rpmbuild}"


################################################################################
# CONSTANTS
################################################################################

# The RPMS, SOURCES and SPECS directories
RPM_RPMS_DIR="$RPM_BUILD_DIR"/'RPMS'
RPM_SOURCES_DIR="$RPM_BUILD_DIR"/'SOURCES'
RPM_SPECS_DIR="$RPM_BUILD_DIR"/'SPECS'


################################################################################
# FUNCTIONS
################################################################################

#> createRpmFile
createRpmFile()
{
	checkUraRpm &&
	checkFile "$PMS_SOURCE_LIST_PATH" &&
	checkFile "$PMS_TARGET_LIST_PATH" &&

	# The package filename and log path
	filename="`getRpmFilename`" &&
	logPath="`getRpmLogPath`" &&

	startList 'Prepare' &&
		createPackageLog &&

		verb 'Remove the previous package' &&
		removePath "$filename" &&

		testCommand 'rpmbuild' ||
		(
			verb 'Install the build command' &&
			if testCommand 'apt-get'
			then
				apt-get install 'rpm'
			else
				installYum 'rpm-build'
			fi
		) &&

		verb 'Create the build directory' &&
		createDir "$RPM_BUILD_DIR"/{'BUILD','SRPMS'} &&
		createDir "$RPM_RPMS_DIR" &&
		createDir "$RPM_SOURCES_DIR" &&
		createDir "$RPM_SPECS_DIR" &&
		createRpmMacrosFile &&

		verb 'Copy the sources to the build directory' &&
		createDir "$PMS_NAME" &&
		tar czf "$RPM_SOURCES_DIR/$PMS_NAME"'.tar.gz' "$PMS_NAME" &&

		verb 'Create the SPEC file' &&
		specPath="$RPM_SPECS_DIR/$PMS_NAME"'.spec' &&
		cat "$URA_RPM_SPEC_PATH" |
		sed -e 's|<NAME>|'"$PMS_NAME"'|g'\
			-e 's|<VERSION>|'"$PMS_VERSION"'|g'\
			-e 's|<RELEASE>|'"$PMS_RELEASE"'|g'\
			-e 's|<DEPENDENCIES>|'"$PMS_DEPENDENCIES"'|g'\
			-e 's|<GROUP>|'"$PMS_GROUP"'|g'\
			-e 's|<LICENSE>|'"$PMS_LICENSE"'|g'\
			-e 's|<SUMMARY>|'"$PMS_SUMMARY"'|g'\
			-e 's|<URL>|'"$PMS_URL"'|g'\
			-e 's|<VENDOR>|'"$PMS_VENDOR"'|g'\
			-e 's|<SOURCE_LIST_PATH>|'"$PMS_SOURCE_LIST_PATH"'|g'\
			-e 's|<TARGET_LIST_PATH>|'"$PMS_TARGET_LIST_PATH"'|g' > "$specPath" &&
	endList &&

	startList 'Build the package' &&
		rpmbuild -ba "$specPath" >> "$logPath" 2>&1 ||
			tail -1 "$logPath" | toFail &&
		mv "$RPM_RPMS_DIR/$filename" '.' &&
	endList &&

	startList 'Clean' &&
		removePath "$RPM_BUILD_DIR" &&
		removePath "$URA_PKG_TMP_DIR" &&
	endList
}

#> createRpmMacrosFile
createRpmMacrosFile()
{
	printn '%packager     '"$PMS_PACKAGER"'>
%_topdir      '"$RPM_BUILD_DIR"'
%_tmppath     '"$URA_RPM_TMP_DIR"'
%_rpmfilename %%{name}-%%{version}-%%{release}.%%{arch}.rpm
%_buildroot   %{_tmppath}/%{_rpmfilename}' > "$RPM_MACROS_PATH"
}

################################################################################

#> getRpmFilename
getRpmFilename()
{
	printn "`getPackageName`"'.rpm'
}

#> getRpmLogPath
getRpmLogPath()
{
	printn "$URA_RPM_LOG_DIR/`getPackageName`"'.log'
}

################################################################################

#> installRpmFile [FILENAME]
installRpmFile()
{
	checkUraRpm &&
	if [ -n "${1:-}" ]
	then
		filename="$1"
	else
		filename="`getRpmFilename`"
	fi &&
	checkFile "$filename" &&

	startList 'Uninstall the previous package (if present)' &&
		uninstallRpmFile "`getBaseWithoutExtension "$filename"`" &&
	endList &&

	verb 'Install the package' &&
	if testCommand 'alien'
	then
		alien -i "$filename"
	elif testUraYum
	then
		installYum "$filename"
	else
		rpm --replacepkgs -ivh "$filename"
	fi
}

#> uninstallRpmFile [PACKAGE]
uninstallRpmFile()
{
	checkUraRpm &&
	if [ -n "${1:-}" ]
	then
		package="$1"
	else
		package="`getPackageName`"
	fi &&

	if testCommand 'apt-get'
	then
		packageName="`extractTo "$package" '-'`" &&
		if apt-cache show "$packageName" > "$NULL_PATH" 2>&1
		then
			verb 'Uninstall the package' &&
			sudo apt-get -y remove "$packageName"
		fi
	elif rpm -qa | toContainWord "$package"
	then
		verb 'Uninstall the package' &&
		rpm -e "$package"
	fi
}

################################################################################

#> testUraRpm
testUraRpm()
{
	testCommand 'rpmbuild' || testCommand 'apt-get' || testUraYum
}

#> checkUraRpm
checkUraRpm()
{
	testUraRpm ||
		fail 'URANUS RPM is not available.' $URA_ERROR_INVALID
}


################################################################################
# END
################################################################################

	if [ -z "${URA_PMS_NAME:-}" ]
	then
		fail 'URANUS PMS profile is not loaded.' 1
	fi
fi
