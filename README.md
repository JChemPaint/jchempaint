[![build](https://github.com/JChemPaint/jchempaint/actions/workflows/maven.yml/badge.svg)](https://github.com/JChemPaint/jchempaint/actions/workflows/maven.yml)

# JChemPaint

***JChemPaint*** (or JCP for short here) is the editor and viewer for 2D chemical structures developed using [CDK](https://cdk.github.io/).
It is implemented in several forms: a Java application and two varieties of Java applet.

Please see the documentation at
https://github.com/JChemPaint/jchempaint/wiki
for more information.

The issue tracker here (on top of this page) is for specific
JCP-related bugs. For problems in CDK proper, please use
https://github.com/JChemPaint/jchempaint/issues

## Download
Compiled program as a *.jar* file can be downloaded from https://github.com/JChemPaint/jchempaint/releases

**Note!** Prerequisite for running **JChemPaint** *.jar* program file is having either Java Runtime Environment (JRE) or Java Development Kit (JDK) installed  which can be downloaded from multiple vendors: [Eclipse Temurin](https://adoptium.net/temurin/releases/), [Azul Zulu](https://www.azul.com/downloads), [Amazon Corretto](https://aws.amazon.com/corretto/), [SAP SapMachine](https://sap.github.io/SapMachine/) or other sources.

## Build

Build requirements (need installation)
 - gettext : http://www.gnu.org/software/gettext/
 - app-bundler: https://github.com/federkasten/appbundler-plugin (optional)

Build with the following commands

```
mvn install -DskipTests
mvn install -DskipTests -Posx-app
mvn install -DskipTests -Pwindows-app
```

The second and third option are to build an OS X application bundle (.app), and to build a windows executable (.exe).


# Notes on Running Tests

JChemPaint uses the [FEST-Swing](https://github.com/alexruiz/fest-swing-1.x) for
UI testing. To get this working you need use a Java version 25 or older and 
enable possibly enable some system settings to allow robots. Java 26+ will not
work because FEST tries to load the removed Applet APIs - note 
JCP doesn't use these. You also need to add the following runtime arguments:

``--enable-native-access=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED``

Also note there are some issues with the "flat" Look-and-Feel so switching to 
the system default typically helps.

## Enable Robots OS X

On Mac OS to get things running, ``System Settings > Privary & Secuirty > Accesibility``
allows the IDE or Java to control the computer.