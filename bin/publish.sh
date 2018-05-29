#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
BASE_PATH="$DIR/..";
LIB="$BASE_PATH/lib"
JARFILE="$LIB/tableau-1.2.jar"
SDK_DIR="$LIB/tableausdk-linux64-10300.18.0510.1135"
JAVA_SDK_DIR="$SDK_DIR/lib64/tableausdk/Java/"

export LD_LIBRARY_PATH="$SDK_DIR/lib64/tableausdk"

#export http_proxy="http://$PROXY_HOST:$PROXY_PORT"
#export https_proxy="http://$PROXY_HOST:$PROXY_PORT"

usage() { echo "Usage: $0 -e <extract path> -s <site name> -d <datasource name>"; }
while getopts "e:s:d:p:" i; do
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

if [[ -z $extract ]] || [[ -z $site ]] || [[ -z $datasource_name ]] || [[ -z $project_name ]]; then
        usage
        exit
fi

java -jar $JARFILE -p -extract $extract -site $site -project $project_name -name $datasource_name
