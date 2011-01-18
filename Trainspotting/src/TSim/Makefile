CLASSPATH=./classes
DOCPATH=./doc

JAVAC=javac
JAVADOC=javadoc

MKDIR=mkdir -p
RM=rm -f

TAR=tar zcvf

default:	clean tsim docs


tsim:		*.java
		${MKDIR} ${CLASSPATH}
		${JAVAC} -d ${CLASSPATH} *.java


docs:
		${MKDIR} ${DOCPATH}
		${JAVADOC} -d ${DOCPATH} *.java

tar-source:	
		cd .. ; \
		${TAR} TSim-src.tgz TSim/*.java TSim/Makefile
tar-all:
		cd .. ; \
		${TAR} TSim-all.tgz TSim/*

clean:
		${RM} *~
		${RM} -r ${CLASSPATH}/TSim
		${RM} -r ${DOCPATH}/*


