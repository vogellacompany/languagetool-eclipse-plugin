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
wget https://languagetool.org/download/LanguageTool-3.8.zip
unzip LanguageTool-3.8.zip
rm LanguageTool-3.8.zip
mkdir ./com.vogella.eclipse.languagetool.spellchecker/libs/
mv LanguageTool-3.8/libs/* ./com.vogella.eclipse.languagetool.spellchecker/libs/
mv LanguageTool-3.8/*.jar ./com.vogella.eclipse.languagetool.spellchecker/libs/
rm -rf LanguageTool-3.8/
wget http://central.maven.org/maven2/org/languagetool/language-en/3.8/language-en-3.8.jar
mv language-en-3.8.jar ./com.vogella.eclipse.languagetool.spellchecker/libs/

# start the Maven build
# mvn clean verify

