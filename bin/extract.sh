#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/.."
LIB="$DIR/lib"
MAX_MEMORY="2g"
JARFILE="$LIB/tableau-1.0.jar"
SDK_DIR="$LIB/tableausdk-linux64-10300.18.0510.1135"
JAVA_SDK_DIR="$SDK_DIR/lib64/tableausdk/Java/"

export LD_LIBRARY_PATH="$SDK_DIR/lib64/tableausdk"

usage() { echo "Usage: $0 -s <schema> -f <input file> -o <output file> [-t <threads>] [-a, --append]"; }
while getopts ":s:f:o:t:ah" i; do
    case "${i}" in
        s) schema=${OPTARG};;
        f) inputFile=${OPTARG};;
        o) outputFile=${OPTARG};;
        t) numThreads=${OPTARG};;
        a) optionalArguments="--append";;
        h) java -jar $JARFILE -help;;
    esac
done
shift $((OPTIND-1))

numThreads=${numThreads:-1} # Default to 1

if [[ -z $schema ]] || [[ -z $inputFile ]] || [[ -z $outputFile ]]; then
        usage
        exit
fi

java -Xmx${MAX_MEMORY} -XX:-UseCompressedOops -XX:+UseConcMarkSweepGC -jar $JARFILE --schema $schema --threads $numThreads --file $inputFile --extract $outputFile $optionalArguments

