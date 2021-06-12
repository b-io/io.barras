#!/bin/sh
################################################################################
#NAME
#    <NAME> - contain utility functions for using YUM
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

if [ -z "${URA_YUM:-}" ]
then
	URA_YUM="$0"


################################################################################
# PROFILES
################################################################################

# Load the required URANUS profiles
: "${URA_PROFILE_DIR:=/etc/profile.d}"
if [ -z "${URA_PROFILE:-}" ]
then
	. "$URA_PROFILE_DIR"/'uranus.sh'
fi


################################################################################
# IMPORTS
################################################################################

# Import the required URANUS libraries
. "$URA_PATH"


################################################################################
# FUNCTIONS
################################################################################

#> installYum PACKAGE...
installYum()
{
	checkUraYum &&
	checkArguments $# 1 &&

	for package in "$@"
	do
		testYum "$package" ||
			yum install -y "$package"
	done
}
#> PACKAGE... | toInstallYum
toInstallYum()
{
	while IFS= read package
	do
		installYum "$package"
	done
}

#> skipYumRepo
skipYumRepo()
{
	checkUraYum &&
	yum-config-manager --setopt=base.skip_if_unavailable=true > "$NULL_PATH" 2>&1 &&
	yum-config-manager --setopt=extras.skip_if_unavailable=true > "$NULL_PATH" 2>&1 &&
	yum-config-manager --setopt=updates.skip_if_unavailable=true > "$NULL_PATH" 2>&1
}

#> testYum PACKAGE
testYum()
{
	checkUraYum &&
	checkArguments $# 1 &&
	checkNonEmpty "$1" &&

	skipYumRepo &&
	yum -q list installed "$1" > "$NULL_PATH" 2>&1
}
#> PACKAGE... | toTestYum
toTestYum()
{
	while IFS= read package
	do
		testYum "$package"
	done
}

################################################################################

#> testUraYum
testUraYum()
{
	testCommand 'yum'
}

#> checkUraYum
checkUraYum()
{
	testUraYum ||
		fail 'URANUS YUM is not available.' $URA_ERROR_INVALID
}


################################################################################
# END
################################################################################

	if [ -z "${URA_NAME:-}" ]
	then
		fail 'URANUS profile is not loaded.' 1
	fi
fi
