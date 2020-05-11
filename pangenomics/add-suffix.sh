#!/bin/sh
# Add a suffix to a set of FRFinder output files
PREFIX=$1
SUFFIX=$2
mv $PREFIX.frs.txt       $PREFIX-$SUFFIX.frs.txt
mv $PREFIX.params.txt    $PREFIX-$SUFFIX.params.txt
mv $PREFIX.pathfrs.txt   $PREFIX-$SUFFIX.pathfrs.txt
mv $PREFIX.subpaths.txt  $PREFIX-$SUFFIX.subpaths.txt
