#!/bin/bash

set -e

DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

APP=$DIR/target/*.app
SRCFOLDER=$DIR/target/tmp
VOLNAME=JChemPaint
DMGFILE=$DIR/target/$VOLNAME.dmg

[ -e $SRCFOLDER ] && rm -rf $SRCFOLDER  # cleanup existing
mkdir $SRCFOLDER && cp -r $APP $SRCFOLDER && ln -s /Applications $SRCFOLDER

hdiutil create -srcfolder $SRCFOLDER $DMGFILE -volname $VOLNAME


