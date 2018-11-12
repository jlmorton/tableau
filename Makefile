MKDIR_P = mkdir -p
NAME = tableau-sdk-wrapper
VERSION = 1.2
BUILD_DIR = ${NAME}-${VERSION}
ARTIFACT = tableau-${VERSION}.jar
ARCHIVE = ${NAME}-${VERSION}.zip
TARGET = target
SAMPLES = samples
BIN_DIR = bin
DOCKER_DIR = docker
DOCKER_REPOSITORY = jlmorton

all: compile mkdir copy_files archive docker

compile:
	mvn install

mkdir:
	${MKDIR_P} ${BUILD_DIR}/bin
	${MKDIR_P} ${BUILD_DIR}/lib
	${MKDIR_P} ${BUILD_DIR}/samples
	${MKDIR_P} ${BUILD_DIR}/tmp
	${MKDIR_P} ${BUILD_DIR}/logs

copy_files:
	mkdir -p lib
	cp ${TARGET}/${ARTIFACT} lib/
	cp ${TARGET}/${ARTIFACT} ${BUILD_DIR}/lib/
	cp -a ${SAMPLES}/* ${BUILD_DIR}/samples/
	cp -a ${BIN_DIR}/* ${BUILD_DIR}/bin/

archive:
	zip -r ${ARCHIVE} ${BUILD_DIR}

clean:
	mvn clean
	rm -rf ${BUILD_DIR}
	rm -rf ${DOCKER_DIR}/${BUILD_DIR}
	rm -f ${ARCHIVE}
	rm -f ${DOCKER_DIR}/Dockerfile
	rm -f ${DOCKER_DIR}/*.jar
	rm -f ${DOCKER_DIR}/*.zip
	rm -rf tmp
	rm -rf lib

generate_docker: all
	cp -a ${BUILD_DIR} ${DOCKER_DIR}/
	sed -e s/%ARTIFACT_DIR%/${BUILD_DIR}/ ${DOCKER_DIR}/Dockerfile.txt > ${DOCKER_DIR}/Dockerfile
	docker build -t ${DOCKER_REPOSITORY}/${NAME}:${VERSION} ${DOCKER_DIR}
