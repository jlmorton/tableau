MKDIR_P = mkdir -p
NAME = tableau-sdk-wrapper
VERSION = 1.1
BUILD_DIR = ${NAME}-${VERSION}
ARTIFACT = tableau-${VERSION}.jar
ARCHIVE = ${NAME}-${VERSION}.zip
TARGET = target
SAMPLES = samples
BIN_DIR = bin

all: mkdir copy_files archive

mkdir:
	${MKDIR_P} ${BUILD_DIR}/bin
	${MKDIR_P} ${BUILD_DIR}/lib
	${MKDIR_P} ${BUILD_DIR}/samples
	${MKDIR_P} ${BUILD_DIR}/tmp
	${MKDIR_P} ${BUILD_DIR}/logs

copy_files:
	cp ${TARGET}/${ARTIFACT} ${BUILD_DIR}/lib
	cp -a ${SAMPLES}/* ${BUILD_DIR}/samples/
	cp -a ${BIN_DIR}/* ${BUILD_DIR}/bin/

archive:
	zip -r ${ARCHIVE} ${BUILD_DIR}

clean:
	rm -rf ${BUILD_DIR}
	rm -f ${ARCHIVE}
