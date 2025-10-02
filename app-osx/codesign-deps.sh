#!/bin/bash

set -e

MACOS_DEVELOPER_IDENTITY=$1

if [[ -z $MACOS_DEVELOPER_IDENTITY ]]; then
    echo "usage: ./codesign-dep.sh \${{ secrets.MACOS_DEVELOPER_IDENTITY }}"
    exit 1;
fi

DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

TARGETDIR=$DIR/target
APPNAME=$TARGETDIR/JChemPaint.app
CLSPATH=$APPNAME/Contents/Java/classpath

# these JARs have native jnilib/dylib's which need to be signed
JNA=$CLSPATH/net/java/dev/jna/jna/*/jna-*.jar
JNAINCHI_AARCH64=$CLSPATH/io/github/dan2097/jna-inchi-darwin-aarch64/*/jna-inchi-darwin-aarch64-*.jar
JNAINCHI_X86_64=$CLSPATH/io/github/dan2097/jna-inchi-darwin-x86-64/*/jna-inchi-darwin-x86-64-*.jar
FLATLAF=$CLSPATH/com/formdev/flatlaf/*/flatlaf-*.jar

TMPDIR=$TARGETDIR/tmp
[[ -e $TMPDIR ]] && rm -rf $TMPDIR

JARFILES=($JNA $JNAINCHI_AARCH64 $JNAINCHI_X86_64 $FLATLAF)
for JARFILE in "${JARFILES[@]}"; do
    echo "Repacking $JARFILE"
    
    mkdir -p $TMPDIR
    pushd $TMPDIR
    jar xf $JARFILE
    find . -name "*.dylib" -exec codesign --verbose --force --timestamp --options=runtime --entitlements entitlements.plist --sign $MACOS_DEVELOPER_IDENTITY --deep {} \;
    find . -name "*.jnilib" -exec codesign --verbose --force --timestamp --options=runtime --entitlements entitlements.plist --sign $MACOS_DEVELOPER_IDENTITY --deep {} \;
    rm $JARFILE
    jar cf $JARFILE *
    popd
    rm -rf $TMPDIR
done



