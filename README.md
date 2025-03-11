[![build](https://github.com/JChemPaint/jchempaint/actions/workflows/maven.yml/badge.svg)](https://github.com/JChemPaint/jchempaint/actions/workflows/maven.yml)

# JChemPaint

***JChemPaint*** (or JCP for short here) is the editor and viewer for 2D chemical structures developed using [CDK](https://cdk.github.io/].
It is implemented in several forms: a Java application and two varieties of Java applet.

Please see the documentation at
https://github.com/JChemPaint/jchempaint/wiki
for more information.

The issue tracker here (on top of this page) is for specific
JCP-related bugs. For problems in CDK proper, please use
https://github.com/JChemPaint/jchempaint/issues


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
