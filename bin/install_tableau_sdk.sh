#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/.."
TMP="$DIR/tmp"
LIB_DIR="$DIR/lib"
SDK_DIR="$LIB_DIR/tableausdk-linux64-10300.18.0510.1135"
JAVA_SDK_DIR="$SDK_DIR/lib64/tableausdk/Java/"
TMP_SDK_NAME="$TMP/TableauSDK.tar.gz"
TABLEAU64_SDK_URL="https://downloads.tableau.com/tssoftware/Tableau-SDK-Linux-64Bit-10-3-11.tar.gz"
TABLEAU32_SDK_URL="https://downloads.tableau.com/tssoftware/Tableau-SDK-Linux-32Bit-10-3-11.tar.gz"

function download() {
	URL=$1
	if type wget &>/dev/null; then
		wget -O $TMP_SDK_NAME $URL
	elif type curl &>/dev/null; then
		curl -o $TMP_SDK_NAME $URL
	else
		echo "Error: Could not find wget or curl to install Tableau SDK"
		exit 1
	fi
}

MACHINE_TYPE=`uname -m`
echo "==============================================="
if [ ${MACHINE_TYPE} == 'x86_64' ]; then
	echo "Downloading 64-bit TableauSDK based on detected machine type."
	download $TABLEAU64_SDK_URL
else
	echo "Downloading 32-bit TableauSDK based on detected machine type."
	download $TABLEAU32_SDK_URL
fi;

if [[ ! -s $TMP_SDK_NAME ]]; then
	echo "Could not download Tableau SDK"
	exit 1
fi;

echo "Extracting Tableau SDK to $LIB_DIR"
tar -C $LIB_DIR -xzf $TMP_SDK_NAME

echo "Installing Maven Dependencies"
mvn install:install-file -Dfile=$JAVA_SDK_DIR/jna.jar -DgroupId=com.sun.jna -DartifactId=jna -Dversion=3.5.1 -Dpackaging=jar 
mvn install:install-file -Dfile=$JAVA_SDK_DIR/tableaucommon.jar -DgroupId=com.tableausoftware -DartifactId=tableau-common -Dversion=10.3.11 -Dpackaging=jar 
mvn install:install-file -Dfile=$JAVA_SDK_DIR/tableauextract.jar -DgroupId=com.tableausoftware -DartifactId=tableau-extract -Dversion=10.3.11 -Dpackaging=jar 
mvn install:install-file -Dfile=$JAVA_SDK_DIR/tableauserver.jar -DgroupId=com.tableausoftware -DartifactId=tableau-server -Dversion=10.3.11 -Dpackaging=jar 
