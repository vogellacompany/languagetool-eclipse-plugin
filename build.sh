#!/bin/bash

MVN_BIN=`which mvn` || echo "Please install mvn."
JAVA_BIN=`which java` || echo "Please install java."
JAVA_BIN=`which unzip` || echo "Please install unzip."
JAVA_BIN=`which wget` || echo "Please install wget."

# clean up old leftovers
test -e bin && rm -rf bin
rm -rf ../cx.ath.remisoft.languagetool/libs/*.jar

# get dependencies
YESTERDAY=`date --date="-1 days" +%Y%m%d`
wget https://languagetool.org/download/snapshots/LanguageTool-$YESTERDAY-snapshot.zip
unzip LanguageTool-$YESTERDAY-snapshot.zip
rm LanguageTool-$YESTERDAY-snapshot.zip
pushd LanguageTool-3.1-SNAPSHOT
	mv libs/* ../com.vogella.eclipse.languagetool.spellchecker/libs/
	zip -r languagetool-standalone.jar *
	mv languagetool-standalone.jar ../com.vogella.eclipse.languagetool.spellchecker/libs/
popd
rm -rf LanguageTool-$YESTERDAY-snapshot

pushd com.vogella.eclipse.languagetool.master
mvn clean verify
popd

