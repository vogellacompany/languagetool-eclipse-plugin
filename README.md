languagetool-eclipse-plugin
===========================

This plugin contains an Eclipse plug-in to use the spell checking engine  [LanguageTool](https://languagetool.org) in the Eclipse IDE.  This repository started of as a fork of https://github.com/auguster/languagetool-eclipse-plugin but as the original remote included the languagetools JARs it was recreated from scratch.

## Extension

The plugin provide an extension for `org.eclipse.ui.workbench.texteditor.spellingEngine` to provide Eclipe wide grammar checking in the various editors.

# Get it

## Eclipse p2 Update Site

You can install it via the following repository (update site currently broken)

```
http://download.vogella.com/p2/C-MASTER-Eclipse-LanguageTool/workspace/cx.ath.remisoft.languagetool.p2updatesite/target/repository/
```

## How to run

Onces the export is done the provided jar can be copied into the plugins/ folder of your Eclipse install

The (few) options can be access through the menu: _Window_ → _Preferences_ → _General_ → _Editors_ → _Text Editors_ → _Spelling_

# Build it

## Get the dependencies and build with Maven

You have to ensure that you have installed Maven 3 properly.
See http://maven.apache.org/download.html#Installation for further information.


~~Linux and Mac users can start the build via the build.sh shell script from the command line which downloads the most recent version of LanguageTools, extract the JARS and libs and copy them to the com.vogella.eclipse.languagetool.spellchecker plug-in.~~

~~You can also manually download the required libaries. Go to https://languagetool.org/download/snapshots/ and download a recent ZIP file. Unzip it and move all JAR files into the `cx.ath.remisoft.languagetool/libs` folder.
Zip the rest of the folder as `languagetool-standalone.jar` and also place it into the `cx.ath.remisoft.languagetool/libs` folder.~~

All dependencies are now declared in the project pom! That means that no more `build.sh` or any other download is required. Just `cd` into the project root and run `mvn package` to run a complete build.


Example:

```bash
mvn package
```



