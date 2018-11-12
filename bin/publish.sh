#!/bin/bash
BASE_PATH="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/.."
LIB="$BASE_PATH/lib"
JARFILE="$LIB/tableau-1.2.jar"
SDK_DIR="$LIB/tableausdk-linux64-10300.18.0510.1135"
JAVA_SDK_DIR="$SDK_DIR/lib64/tableausdk/Java/"

export LD_LIBRARY_PATH="$SDK_DIR/lib64/tableausdk:$LD_LIBRARY_PATH"

#export http_proxy="http://$PROXY_HOST:$PROXY_PORT"
#export https_proxy="http://$PROXY_HOST:$PROXY_PORT"

set -e

usage() { echo "Usage: $0 -e <extract path> -s <site name> -d <datasource name> -u <url> -p <project name> -n <username> -x <password>"; exit 1; }
while getopts "u:n:x:e:s:d:p:" i; do
    case "${i}" in
	u) url=${OPTARG};;
	n) username=${OPTARG};;
	x) password=${OPTARG};;
        e) extract=${OPTARG};;
        s) site=${OPTARG};;
        d) datasource_name=${OPTARG};;
        p) project_name=${OPTARG};;
        h) java -jar $JARFILE -help;;
    esac
done
shift $((OPTIND-1))

if [[ -z $extract ]]; then
	echo "Error: extract argument expected (-e <extract>)";
	usage;
fi

if [[ -z $site ]]; then
	echo "Error: site name argument expected (-s <site name>)";
	usage;
fi

if [[ -z $datasource_name ]]; then
	echo "Error: datasource name argument expected (-d <datasource name>)";
	usage;
fi

if [[ -z $url ]]; then
	echo "Error: Tableau servre URL argument expected (-u <url>)";
	usage;
fi

if [[ -z $project_name ]]; then
	echo "Error: Project Name argument expected (-p <project>)";
	usage;
fi

if [[ -z $username ]]; then
	echo "Error: Username argument expected (-n <username>)";
	usage;
fi

if [[ -z $password ]]; then
	echo "Error: Password argument expected (-x <password>)";
	usage;
fi

java -jar $JARFILE -p -extract $extract -site $site -project $project_name -datasource $datasource_name -url $url -username $username -password $password
