#!/bin/sh
################################################################################
#NAME
#    <NAME> - contain utility functions for creating LXC containers
#
#SYNOPSIS
#    <NAME>
#
#AUTHOR
#    Written by Florian Barras (florian@barras.io).
#
#COPYRIGHT
#    Copyright © 2013-2022 Florian Barras <https://barras.io>.
#    The MIT License (MIT) <https://opensource.org/licenses/MIT>.
################################################################################


################################################################################
# BEGIN
################################################################################

if [ -z "${URA_LXC:-}" ]
then
	URA_LXC="$0"


################################################################################
# PROFILES
################################################################################

# Load the required URANUS profiles
: "${URA_PROFILE_DIR:=/etc/profile.d}"
if [ -z "${URA_PROFILE:-}" ]
then
	. "$URA_PROFILE_DIR"/'uranus.sh'
fi
if [ -z "${URA_VMS_PROFILE:-}" ]
then
	. "$URA_PROFILE_DIR"/'uranus-vms.sh'
fi


################################################################################
# IMPORTS
################################################################################

# Import the required URANUS libraries
. "$URA_PATH"
. "$URA_YUM_PATH"


################################################################################
# SETTINGS
################################################################################

# The LXC directory
: "${LXC_DIR:=/var/lib/lxc}"

# The default template and backing store
: "${LXC_DEFAULT_TEMPLATE:=/usr/share/lxc/templates/lxc-centos}"
: "${LXC_DEFAULT_BACKING_STORE:=overlayfs}"


################################################################################
# FUNCTIONS
################################################################################

#> installLxc
installLxc()
{
	checkUraLxc &&
	testYum 'lxc' ||
		(
			startList 'Install LXC' &&
				verb 'Install the EPEL repository' &&
				# - epel-release: the Extra Packages for Enterprise Linux (EPEL),
				#                 which provides LXC virtualization
				installYum 'epel-release' &&

				verb 'Install debootstrap, libvirt and perl' &&
				# - debootstrap: a tool which will install a Debian base system into
				#                a subdirectory of another, already installed system
				# - libvirt: a library which provides a single way to manage
				#            multiple different virtualization providers/hypervisors
				# - perl: the Perl language interpreter
				installYum 'debootstrap' 'libvirt' 'perl' &&

				verb 'Install the LXC virtualization solution' &&
				installYum 'lxc' 'lxc-templates' &&

				if [ -z "`captureFile '/proc/filesystems' 'overlay'`" ]
				then
					verb 'Enable OverlayFS' &&
					printn 'overlay' > '/etc/modules-load.d/overlay.conf' &&
					modprobe 'overlay'
				fi &&
			endList
		)
}

#> printLxc
printLxc()
{
	checkUraLxc &&
	startList 'Print LXC' &&
		verb 'LXC configuration' &&
		lxc-checkconfig &&

		verb 'LXC templates' &&
		ls -alh '/usr/share/lxc/templates' &&

		verb 'LXC active containers' &&
		lxc-ls --active &&
	endList
}

#> testLxc
testLxc()
{
	checkUraLxc &&
	systemctl status lxc.service > "$NULL_PATH" 2>&1
}

################################################################################

#> startLxc
startLxc()
{
	checkUraLxc &&
	testLxc ||
		(
			startList 'Start the LXC service' &&
				systemctl start lxc.service &&
				systemctl start libvirtd &&
			endList
		)
}

#> stopLxc
stopLxc()
{
	checkUraLxc &&
	if testLxc
	then
		startList 'Stop the LXC service' &&
			systemctl stop lxc.service &&
			systemctl stop libvirtd &&
		endList
	fi
}

################################################################################

#> createContainer CONTAINER [TEMPLATE] [BACKING_STORE]
createContainer()
{
	checkUraLxc &&
	checkArguments $# 1 &&
	checkNonEmpty "$1" &&
	container="$1" &&
	if [ -n "${2:-}" ]
	then
		checkFile "$2" &&
		template="$2"
	else
		checkFile "$LXC_DEFAULT_TEMPLATE" &&
		template="$LXC_DEFAULT_TEMPLATE"
	fi &&
	if [ -n "${3:-}" ]
	then
		backingStore="$3"
	else
		checkNonEmpty "$LXC_DEFAULT_BACKING_STORE" &&
		backingStore="$LXC_DEFAULT_BACKING_STORE"
	fi &&

	startList 'Create the LXC container' &&
		installLxc &&

		verb 'Build the LXC container' &&
		lxc-create -n "$container" -t "$template" -B "$backingStore" &&

		verb 'Change the apparent root directory of the LXC container' &&
		chroot "$LXC_DIR/$container/rootfs" passwd &&

		verb 'Test the LXC container' &&
		lxc-info -n "$container" &&
	endList
}

################################################################################

#> startContainer CONTAINER
startContainer()
{
	checkUraLxc &&
	checkArguments $# 1 &&
	checkNonEmpty "$1" &&
	container="$1" &&

	startLxc &&
	startList 'Start the LXC container' &&
		lxc-start -n "$container" &&
		lxc-info -n "$container" &&
	endList
}

#> stopContainer CONTAINER
stopContainer()
{
	checkUraLxc &&
	checkArguments $# 1 &&
	checkNonEmpty "$1" &&
	container="$1" &&

	startList 'Stop the LXC container' &&
		lxc-stop -n "$container" &&
		lxc-info -n "$container" &&
	endList
}

################################################################################

#> testUraLxc
testUraLxc()
{
	testCommand 'lxc-create' ||
		testUraYum
}

#> checkUraLxc
checkUraLxc()
{
	testUraLxc ||
		fail 'URANUS LXC is not available.' $URA_ERROR_INVALID
}


################################################################################
# END
################################################################################

	if [ -z "${URA_LXC_NAME:-}" ]
	then
		fail 'URANUS LXC profile is not loaded.' 1
	fi
fi
