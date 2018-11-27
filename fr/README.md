# org.ncgr.pangenomics.fr
This repository contains FRFinder, an NCGR refactor of the original code written at Montana State by Brendan Mumey.
     
## Building
The project is set up with dependencies managed with the [Gradle build tool](https://gradle.org/).
To build the distribution, simply run
```
$ ./gradlew assembleDist
```
This will create a distribution tarball `build/distributions/fr.tar`. The default main class is set in build.gradle as `mainClassName = "FRFinderRunner"`.

# FRFinderRunner
FRFinderRunner is simply a main class to run FRFinder in the java root.

# FRFinder
FRFinder is a Java implementation of the Frequented Regions algorithm presented at the ACM BCB 2017 conference: "Exploring Frequented Regions in Pan-Genoimc Graphs".
A _Frequented Region_ (FR) is a region in a pan-genome de Bruijn graph that is frequently traversed by a subset of the genome paths in the graph.
A path that contributes to an FR being frequent is called a _supporting path_.
The algorithm works by iteratively constructing FRs via hierarchical aglomerative clsutering and then traversing the hierarchy and selecting nodes that qualify as clusters according to the given parameters.

## Parameters
FRFinder has two required parameters: `alpha` and `kappa`.
`alpha` is the minimum fraction of the nodes in an FR that each subpath must contain.
This is referred to as the _penetrance_.
`kappa` is maximum insertion length (measured in base-pairs) that any supporting path may have.
This is referred to as the _maximum insertion_.

Additionally, there are two optional parameters: `minsup` and `minsize`.
`minsup` is the minimum number of genome paths that must meet the other parameters in order for a region to be considered frequent.
This is referred to as the _minimum support_.
`minsize` is the minimum size (measured in de Bruijn nodes) that an FR that meets the other parameters must be in order to be considered frequent.
This is referred to as the _minimum size_.

It is too early in the project to recommend explicit parameters, but typical values for
`alpha` are in the range 0.6 - 0.9 and typical values for `kappa` are between 0 - 3000.

## De Bruijn Graphs
FRFinder consumes de Bruijn graphs in the `dot` file format.
A `dot` file representation of a pan-genome De Bruijn graph can be constructed from a `fasta` using the one of the programs presented in the following works:
* "SplitMEM: a graphical algorithm for pan-genome analysis with suffix skips"
* "Efficient Construction of a Compressed de Bruijn Graph for Pan-Genome Analysis"

## Running
FRFinder can be run by unpacking the distribution tarball `build/distributions/fr.tar` in some place you'd like to run the code. Then, you can run
```
$ bin/fr -d <dotFile> -f <faFile> -a <alpha> -k <kappa>
```
where `<dotFile>` is the pan-genome de Bruijn graph constructed for the `<faFile>` using, for example, SplitMEM cited above.

## Output
FRFinder produces output in several files.  The files are stored in an output directory that is named based on the input .dot and .fasta files used.  The following files types are produced:

`.bed` : this file indicates the supporting subpath segments found for each FR in [bed](https://genome.ucsc.edu/FAQ/FAQformat.html#format1) format

`.dist.txt` : this file lists the support and average supporing path length of each FR

`.frs.txt` : this file lists the De Bruijn nodes that comprise each FR.

`.frpaths.txt` : this lists the FRs each fasta sequence passes through as it traverses the DB graph.

`.frs.txt` : this file lists the De Bruijn nodes that comprise each FR.

`.csfr.txt` : this file indicates for each fasta seqence, the frequency counts of all FRs that occured in the sequence

## Sample Input
To test FRFinder, a small input set is provided:

`sample/ecoli.pan3.dot` : a dot file constructed for a simple ecoli test file (K = 10)

`sample/ecoli.pan3.fa` : the corresponding fasta sequence file

The script `run-sample` will build the app, run it against these files, and check that it produces the correct output.
