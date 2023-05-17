#!/bin/sh
FILENAME=$1
java -cp "build/install/ncgr-pubmed/lib/*" org.ncgr.pubmed.Abstract $FILENAME
