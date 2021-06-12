#!/bin/sh
################################################################################
#NAME
#    <NAME> - contain utility functions for creating GPG keys
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

if [ -z "${URA_GPG:-}" ]
then
	URA_GPG="$0"


################################################################################
# PROFILES
################################################################################

# Load the required URANUS profiles
: "${URA_PROFILE_DIR:=/etc/profile.d}"
if [ -z "${URA_PROFILE:-}" ]
then
	. "$URA_PROFILE_DIR"/'uranus.sh'
fi
if [ -z "${URA_SMS_PROFILE:-}" ]
then
	. "$URA_PROFILE_DIR"/'uranus-sms.sh'
fi


################################################################################
# IMPORTS
################################################################################

# Import the required URANUS libraries
. "$URA_PATH"


################################################################################
# FUNCTIONS
################################################################################

#> createGpgKey
createGpgKey()
{
	checkUraGpg &&

	startList 'Create the key' &&
		gpg --gen-key &&
		gpg --list-keys &&
	endList
}

################################################################################

#> testUraGpg
testUraGpg()
{
	testCommand 'gpg'
}

#> checkUraGpg
checkUraGpg()
{
	testUraGpg ||
		fail 'URANUS GPG is not available.' $URA_ERROR_INVALID
}


################################################################################
# END
################################################################################

	if [ -z "${URA_SMS_NAME:-}" ]
	then
		fail 'URANUS SMS profile is not loaded.' 1
	fi
fi
