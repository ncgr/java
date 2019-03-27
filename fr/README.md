This directory contains classes for working with pan-genomic graphs and frequented regions, based on the paper
```
Cleary, et al., "Exploring Frequented Regions in Pan-Genomic Graphs", IEEE/ACM Trans Comput Biol Bioinform. 2018 Aug 9. PMID:30106690 DOI:10.1109/TCBB.2018.2864564
``` 
## Building
The project is set up with dependencies managed with the [Gradle build tool](https://gradle.org/). To build the distribution, simply run
```
$ ./gradlew installDist
```
This will create a distribution under `build/install` that is used by the various run scripts.

### no package
These are files with main methods for testing code.

### libsvm
The Java port of libsvm. Placed here for possible tweakage.

### org.ncgr.pangenomics
Basic graph-related classes, not particularly specific to frequented regions or SVM.

**Graph** stores a pan-genomic graph, with methods for reading it in from files and various output methods. There is a `main` class for simply reading in a graph and printing
out its details.

**Node** encapsulates a node in a Graph: its id (a long) and its sequence.

**NodeSet** encapsulates a set of nodes in a Graph. NodeSet implements Comparable, based on content. There is a method `merge()` for merging two NodeSets.
(These are called "node clusters" in the paper above, but since I've implemented it as an extension of TreeSet, I've used "Set").

**Path** encapsulates a path through a Graph, along with its full sequence.

### org.ncgr.pangenomics.fr
Frequented regions code.

**FrequentedRegion** represents a cluster of nodes (NodeSet) along with the supporting subpaths of the full set of Paths in a Graph.

**FRFinder** contains a `main()` method for finding FRs based on a bunch of parameters, using the `findFRs()` method,
along with a `postprocess()` method for filtering the results of an FR search.

**FRPair** is a utility class that contains two FRs and the result of merging them, and is used in a PriorityQueue in `FRFinder.findFRs()`.

### org.ncgr.svm
Support Vector Machine code.

**GridSearcher** searches a grid of [C,gamma] using SvmCrossValidator to find the optimal values.

**Sample** represents a single sample (which may contain several paths in the graph, e.g. 2 for diploid organisms).

**SvmCrossValidator** runs a libsvm stratified cross-validation.

**SvmPredictor** performs a libsvm prediction on a test path, based on the results of training.

**SvmScaler** scales the data to a balanced set for training. Run this before GridSearcher and SvmCrossValidation. (This does not use libsvm.)

**SvmTrainer** trains an SVM on a set of training data using libsvm.

**SvmUtil.java** contains some handy static methods.

### vg.Vg
Package and class created by the protocol compiler run against vg.proto from the vg repository.
