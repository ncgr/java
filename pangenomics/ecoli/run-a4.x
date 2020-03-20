#!/bin/sh
a4.x construct -i ecoli.pan3.fa -o ecoli.pan3 -k kfile.txt
a4.x impl2expl -g ecoli.pan3.k10.bin  -o ecoli.pan3.k10
a4.x impl2expl -g ecoli.pan3.k30.bin  -o ecoli.pan3.k30
a4.x impl2expl -g ecoli.pan3.k60.bin  -o ecoli.pan3.k60
a4.x impl2expl -g ecoli.pan3.k120.bin -o ecoli.pan3.k120
