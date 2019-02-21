# org.ncgr.pangenomics and org.ncgr.pangenomics.fr
This directory contains some basic utility classes for working with pan-genomic graphs, as well as FRFinder, an implementation of the algorithm described in the paper
```
Cleary, et al., "Exploring Frequented Regions in Pan-Genomic Graphs", IEEE/ACM Trans Comput Biol Bioinform. 2018 Aug 9. PMID:30106690 DOI:10.1109/TCBB.2018.2864564
```
along with classes that support that project.
     
## Building
The project is set up with dependencies managed with the [Gradle build tool](https://gradle.org/). To build the distribution, simply run
```
$ ./gradlew installDist
```
This will create a distribution under `build/install` that is used by the various run scripts.

### org.ncgr.pangenomics.Graph
Graph stores a pan-genomic graph, with methods for reading it in from files and various output methods. There is a `main` class for simply reading in a graph and printing
out its details.

### org.ncgr.pangenomics.Path
Path encapsulates a path through a Graph, along with its full sequence.

### org.ncgr.pangenomics.Node
Node encapsulates a node in a Graph: its id (a long) and its sequence.

### org.ncgr.pangenomics.NodeSet
NodeSet encapsulates a set of nodes in a Graph. NodeSet implements Comparable, based on content. There is a method `merge()` for merging two NodeSets.
(These are called "node clusters" in the paper above, but since I've implemented it as an extension of TreeSet, I've used "set").

### org.ncgr.pangenomics.fr.FrequentedRegion
Frequented Region represents a cluster of nodes (NodeSet) along with the supporting subpaths of the full set of Paths in a Graph.

### org.ncgr.pangenomics.fr.FRFinder
FRFinder contains a `main()` method for finding FRs based on a bunch of parameters, using the `findFRs()` method,
along with a `postprocess()` method for filtering the results of an FR search.

### org.ncgr.pangenomics.fr.FRPair
FRPair is a utility class that contains two FRs and the result of merging them, and is used in a PriorityQueue in `FRFinder.findFRs()`.

