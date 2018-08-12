################################################################################
# CONSTANTS
################################################################################

Name:		<NAME>
Version:	<VERSION>
Release:	<RELEASE>
Summary:	<SUMMARY>

Requires:	<DEPENDENCIES>

Group:		<GROUP>
License:	<LICENSE>
URL:		<URL>
Vendor:		<VENDOR>

Source:		<NAME>.tar.gz

%description
<SUMMARY>


################################################################################
# PROCESS
################################################################################

########################################
%prep
########################################

%setup -q -n '<NAME>'

########################################
%build
########################################

########################################
%install
########################################

# Load the required URANUS profiles
: "${URA_PROFILE_DIR:=/etc/profile.d}" &&
if [ -z "${URA_PROFILE:-}" ]
then
	. "$URA_PROFILE_DIR"/'uranus.sh'
fi &&
if [ -z "${URA_PMS_PROFILE:-}" ]
then
	. "$URA_PROFILE_DIR"/'uranus-pms.sh'
fi &&

# Import the required URANUS libraries
. "$URA_PATH" &&

# Install the files
targetPaths="`cat '<TARGET_LIST_PATH>' | toCreateArray`" &&
index=0 &&
while IFS= read sourcePath
do
	sourcePath="`unquote "$sourcePath"`" &&
	targetPath='%{buildroot}'/"`getElementAt "$targetPaths" $index | toUnquote`" &&
	createDir "`getDir "$targetPath"`" &&
	install -p "$sourcePath" "$targetPath" &&
	index=`increment $index`
done < '<SOURCE_LIST_PATH>'

########################################
%clean
########################################

rm -fr %{buildroot}

########################################
%files -f '<TARGET_LIST_PATH>'
########################################

# Set the default attributes for files and directives
%defattr(755, root, root, 755)

########################################
%changelog
########################################
