#!/bin/bash
set -ex

declare TOP=$(dirname "$0")
declare FREEPLANE_DIR=
[[ ! -z $OS && $OS =~ Windows_NT ]] && FREEPLANE_DIR="$HOME/AppData/Roaming/Freeplane/1.6.x/" || FREEPLANE_DIR="$HOME/.config/freeplane/1.6.x"

find $TOP/src/zips/lib/freeplaneGTD -name "*.groovy" | xargs -I{} cp {} $FREEPLANE_DIR/lib/freeplaneGTD
find $TOP/src/scripts -name "*.groovy" | xargs -I{} cp {} $FREEPLANE_DIR/addons/freeplaneGTD/scripts

