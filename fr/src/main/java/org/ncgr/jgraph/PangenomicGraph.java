package org.ncgr.jgraph;

import org.ncgr.pangenomics.Node;

import java.io.*;
import java.util.*;

import org.apache.commons.cli.*;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.io.*;
import org.jgrapht.traverse.*;

/**
 * Storage of a pan-genomic graph in a JGraphT object.
 *
 * @author Sam Hokin
 */ 
public class PangenomicGraph extends DirectedMultigraph<Node,Edge> {

    // defaults
    public static int BOTH_GENOTYPES = -1;
    public static boolean VERBOSE = false;

    // output verbosity
    boolean verbose = VERBOSE;

    // genotype preference (default: load all genotypes)
    int genotype = BOTH_GENOTYPES;

    // each Path provides the ordered list of nodes that it traverses, along with its full sequence
    public TreeSet<PathWalk> paths; // (ordered simply for convenience)
    
    // maps a Node to a set of Paths that traverse it
    // public TreeMap<Long,Set<Path>> nodePaths; // keyed and ordered by Node Id (for convenience)

    // maps a path label to a count of paths that have that label
    public Map<String,Integer> labelCounts; // keyed by label

    /**
     * Constructor instantiates collections; then use read methods to populate the graph from files.
     */
    public PangenomicGraph() {
        super(Edge.class);
        paths = new TreeSet<>();
        // nodePaths = new TreeMap<>();
        labelCounts = new TreeMap<>();
    }

    /**
     * Import from a GFA file.
     */
    public void importGFA(File gfaFile) {
        GFAImporter importer = new GFAImporter();
        if (verbose) importer.setVerbose();
        importer.setGenotype(genotype);
        importer.importGraph(this, gfaFile);
    }


    /**
     * Return the long 1-based nodeId associated with the given MutableNode.
     */
    // long getNodeId(MutableNode mNode) {
    //     return Long.parseLong(mNode.name().toString()) + 1;
    // }


    /**
     * Return true if this and that PangenomicGraph come from the same file.
     */
    // public boolean equals(PangenomicGraph that) {
    //     if (this.jsonFile!=null && that.jsonFile!=null) {
    //         return this.jsonFile.equals(that.jsonFile);
    //     } else if (this.gfaFile!=null && that.gfaFile!=null) {
    //         return this.gfaFile.equals(that.gfaFile);
    //     } else if (this.dotFile!=null && that.dotFile!=null && this.fastaFile!=null && that.fastaFile!=null) {
    //         return this.dotFile.equals(that.dotFile) && this.fastaFile.equals(that.fastaFile);
    //     } else {
    //         return false;
    //     }
    // }

    /**
     * Build the path sequences - just calls Path.buildSequence() for each path.
     */
    // void buildPathSequences() {
    //     for (Path path : paths) {
    //         path.buildSequence();
    //     }
    // }

    /**
     * Find node paths: the set of paths that run through each node.
     */
    // void buildNodePaths() {
    //     // init empty paths for each node
    //     for (Long nodeId : nodes.keySet()) {
    //         nodePaths.put(nodeId, new TreeSet<Path>());
    //     }
    //     // now load the paths
    //     for (Path path : paths) {
    //         for (Node node : path.getNodes()) {
    //             nodePaths.get(node.id).add(path);
    //         }
    //     }
    // }

    /**
     * Read path labels from a tab-delimited file. Comment lines start with #.
     */
    public void readPathLabels(File labelsFile) throws FileNotFoundException, IOException {
        labelCounts = new TreeMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(labelsFile));
        String line = null;
        Map<String,String> labels = new TreeMap<String,String>();
        while ((line=reader.readLine())!=null) {
            if (!line.startsWith("#")) {
                String[] fields = line.split("\t");
                if (fields.length==2) {
                    labels.put(fields[0], fields[1]);
                }
            }
        }
        // find the labels for path names (which may have .genotype suffix)
        for (PathWalk path : paths) {
            for (String sample : labels.keySet()) {
                String label = labels.get(sample); 
                if (sample.equals(path.name)) {
                    // sample = path name labeling
                    path.setLabel(label);
                    if (labelCounts.containsKey(label)) {
                        int count = labelCounts.get(label);
                        labelCounts.put(label, count+1);
                    } else {
                        labelCounts.put(label, 1);
                    }
                } else {
                    // sample = path name.genotype labeling
                    String[] parts = sample.split("\\.");
                    String sampleName = parts[0];
                    if (parts.length>1) {
                        int sampleGenotype = Integer.parseInt(parts[1]);
                        if (sampleName.equals(path.name) && sampleGenotype==path.getGenotype()) {
                            path.setLabel(label);
                            if (labelCounts.containsKey(label)) {
                                int count = labelCounts.get(label);
                                labelCounts.put(label, count+1);
                            } else {
                                labelCounts.put(label, 1);
                            }
                        }
                    }
                }
            }
        }
        // verbosity
        if (verbose) printLabelCounts(System.out);

        // check that we've labeled all the paths
        boolean pathsAllLabeled = true;
        for (PathWalk path : paths) {
            if (path.getLabel()==null) {
                pathsAllLabeled = false;
                System.err.println("ERROR: the path "+path.name+" has no label in the labels file.");
            }
        }
        if (!pathsAllLabeled) System.exit(1);
    }

    /**
     * Set the verbose flag.
     */
    public void setVerbose() {
        verbose = true;
    }

    /**
     * Set the genotype preference: -1=both; 0 and 1
     */
    public void setGenotype(int g) throws IllegalArgumentException {
        if (g<-1 || g>1) {
            throw new IllegalArgumentException("genotype value must be -1 (both), 0, or 1.");
        } else {
            genotype = g;
        }
    }

    /**
     * Print a delineating heading, for general use.
     */
    static void printHeading(String heading) {
        for (int i=0; i<heading.length(); i++) System.out.print("="); System.out.println("");
        System.out.println(heading);
        for (int i=0; i<heading.length(); i++) System.out.print("="); System.out.println("");
    }

    /**
     * Print out the nodes
     */
    public void printNodes(PrintStream out) {
        if (out==System.out) printHeading("NODES");
        Set<Node> nodeSet = vertexSet();
        for (Node n : nodeSet) {
            System.out.println(n.id+"\t"+n.sequence);
        }
    }

    /**
     * Print out a histogram of node sizes.
     */
    // public void printNodeHistogram(PrintStream out) {
    //     if (out==System.out) printHeading("k HISTOGRAM");
    //     Map<Integer,Integer> countMap = new TreeMap<>();
    //     for (Node node : nodes.values()) {
    //         int length = node.sequence.length();
    //         if (countMap.containsKey(length)) {
    //             countMap.put(length, ((int)countMap.get(length))+1);
    //         } else {
    //             countMap.put(length, 1);
    //         }
    //     }
    //     for (int len : countMap.keySet()) {
    //         int counts = countMap.get(len);
    //         out.print("length="+len+"\t("+counts+")\t");
    //         for (int i=1; i<=counts; i++) out.print("X");
    //         out.println("");
    //     }
    // }

    /**
     * Print the paths, labeled by pathName.
     */
    public void printPaths(PrintStream out) {
        if (out==System.out) printHeading("PATHS");
        for (PathWalk path : paths) {
            out.print(path.getNameGenotype()+"\t"+path.getLabel()+"\t"+path.sequence.length());
            for (Node node : path.getNodes()) {
                out.print("\t"+node.id);
            }
            out.println("");
        }
    }

    /**
     * Print out the node paths along with counts.
     */
    // public void printNodePaths(PrintStream out) {
    //     if (out==System.out) printHeading("NODE PATHS");
    //     for (Long nodeId : nodePaths.keySet()) {
    //         Set<Path> nPaths = nodePaths.get(nodeId);
    //         out.print(String.valueOf(nodeId));
    //         for (Path path : nPaths) {
    //             out.print("\t"+path.getNameGenotype());
    //         }
    //         out.println("");
    //     }
    // }

    /**
     * Print the sequences for each path, in FASTA format, labeled by path.name.genotype.
     */
    // public void printPathSequences(PrintStream out) {
    //     if (out==System.out) printHeading("PATH SEQUENCES");
    //     for (Path path : paths) {
    //         String heading = ">"+path.getNameGenotype()+" ("+path.sequence.length()+")";
    //         out.print(heading);
    //         // add dots every 10 bases to the heading
    //         int h = 19;
    //         int m = 8;
    //         if (heading.length()>=39) {
    //             h = 49;
    //             m = 5;
    //         } else if (heading.length()>=29) {
    //             h = 39;
    //             m = 6;
    //         } else if (heading.length()>=19) {
    //             h = 29;
    //             m = 7;
    //         }
    //         for (int i=heading.length(); i<h; i++) out.print(" "); out.print(".");
    //         for (int n=0; n<m; n++) {
    //             for (int i=0; i<9; i++) out.print(" "); out.print(".");
    //         }
    //         out.println("");
    //         // print out the sequence, 100 chars to a line
    //         for (int i=1; i<=path.sequence.length(); i++) {
    //             out.print(path.sequence.charAt((i-1)));
    //             if (i%100==0) out.print("\n");
    //         }
    //         out.println("");
    //     }
    // }

    /**
     * Print the counts of paths per label.
     */
    public void printLabelCounts(PrintStream out) {
        if (out==System.out) printHeading("LABEL COUNTS");
        for (String label : labelCounts.keySet()) {
            out.println(label+"\t"+labelCounts.get(label));
        }
    }

    /**
     * Print node participation by path, appropriate for PCA analysis.
     */
    // public void printPcaData(PrintStream out) throws FileNotFoundException, IOException {
    //     // header is paths
    //     boolean first = true;
    //     for (Path path : paths) {
    //         if (first) {
    //             out.print(path.getNameGenotype());
    //             first = false;
    //         } else {
    //             out.print("\t"+path.getNameGenotype());
    //         }
    //         if (path.getLabel()!=null) out.print("."+path.getLabel());
    //     }
    //     out.println("");

    //     // rows are nodes
    //     for (long nodeId : nodes.keySet()) {
    //         Node node = nodes.get(nodeId);
    //         Set<Path> nPaths = nodePaths.get(nodeId);
    //         out.print("N"+nodeId);
    //         for (Path path : paths) {
    //             if (path.getNodes().contains(node)) {
    //                 out.print("\t1");
    //             } else {
    //                 out.print("\t0");
    //             }
    //         }
    //         out.println("");
    //     }
    // }

    /**
     * Run all the PangenomicGraph printing methods to files.
     */
    // public void printAll(String outputPrefix) throws FileNotFoundException, IOException {
    //     if (outputPrefix==null) return;
    //     PrintStream nodesOut = new PrintStream(outputPrefix+".nodes.txt");
    //     PrintStream nodeHistogramOut = new PrintStream(outputPrefix+".nodehistogram.txt");
    //     PrintStream pathsOut = new PrintStream(outputPrefix+".paths.txt");
    //     PrintStream nodePathsOut = new PrintStream(outputPrefix+".nodepaths.txt");
    //     PrintStream pathSequencesOut = new PrintStream(outputPrefix+".pathsequences.fasta");
    //     PrintStream pcaOut = new PrintStream(outputPrefix+".pathpca.txt");
    //     printNodes(nodesOut);
    //     printNodeHistogram(nodeHistogramOut);
    //     printPaths(pathsOut);
    //     printNodePaths(nodePathsOut);
    //     printPathSequences(pathSequencesOut);
    //     printPcaData(pcaOut);
    //     if (labelCounts.size()>0) {
    //         PrintStream labelCountsOut = new PrintStream(outputPrefix+".labelcounts.txt");
    //         printLabelCounts(labelCountsOut);
    //     }
    // }

    /**
     * Run all the PangenomicGraph printing methods to stdout.
     */
    // public void printAll() throws FileNotFoundException, IOException {
    //     printNodes(System.out);
    //     printNodeHistogram(System.out);
    //     printPaths(System.out);
    //     printNodePaths(System.out);
    //     printPathSequences(System.out);
    //     printPcaData(System.out);
    //     if (labelCounts.size()>0) {
    //         printLabelCounts(System.out);
    //     }
    // }

    /**
     * Command-line utility
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {

        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        Option gfaOption = new Option("g", "gfa", true, "vg GFA file");
        gfaOption.setRequired(false);
        options.addOption(gfaOption);
        //
        Option genotypeOption = new Option("gt", "genotype", true, "which genotype to include (0,1) from the GFA file; "+BOTH_GENOTYPES+" to include both ("+BOTH_GENOTYPES+")");
        genotypeOption.setRequired(false);
        options.addOption(genotypeOption);
        //
        Option labelsOption = new Option("p", "pathlabels", true, "tab-delimited file containing one pathname<tab>label per line");
        labelsOption.setRequired(false);
        options.addOption(labelsOption);
        //
        Option outputprefixOption = new Option("o", "outputprefix", true, "output file prefix (stdout)");
        outputprefixOption.setRequired(false);
        options.addOption(outputprefixOption);
        //
        Option verboseOption = new Option("v", "verbose", false, "verbose output ("+VERBOSE+")");
        verboseOption.setRequired(false);
        options.addOption(verboseOption);

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp("PangenomicGraph", options);
            System.exit(1);
            return;
        }

        // none required, so spit out help if nothing supplied
        if (cmd.getOptions().length==0) {
            formatter.printHelp("PangenomicGraph", options);
            System.exit(1);
            return;
        }
        
        // parameter validation
        if (!cmd.hasOption("gfa")) {
            System.err.println("You must specify a vg GFA file (--gfa)");
            System.exit(1);
            return;
        }
        
        // files
        File gfaFile = new File(cmd.getOptionValue("gfa"));
        File labelsFile = new File(cmd.getOptionValue("pathlabels"));

        // create a PangenomicGraph from a GFA file
        PangenomicGraph pg = new PangenomicGraph();
        if (cmd.hasOption("verbose")) pg.setVerbose();
        if (cmd.hasOption("genotype")) pg.setGenotype(Integer.parseInt(cmd.getOptionValue("genotype")));
        pg.importGFA(gfaFile);

        // if a labels file is given, add them to the paths
        if (labelsFile!=null) {
            pg.readPathLabels(labelsFile);
        }

        // output
        pg.printNodes(System.out);
        pg.printPaths(System.out);

        // if (cmd.hasOption("outputprefix")) {
        //     files
        //     pg.printAll(cmd.getOptionValue("outputprefix"));
        // } else {
        //     stdout
        //     pg.printAll();
        // }
    }

    // /**
    //  * Required by Graph interface.
    //  */
    // public void setEdgeWeight(Object o, double w) {
    //     throw new java.lang.UnsupportedOperationException("PangenomicGraph does not (yet?) support edge weights.");
    //     return;
    // }

    // /**
    //  * Required by Graph interface.
    //  */
    // public double getEdgeWeight(Object o) {
    //     return Graph.DEFAULT_EDGE_WEIGHT;
    // }

    // /**
    //  * Required by Graph interface.
    //  */
    // public GraphType getType() {
    //     DefaultGraphType.Builder builder = new DefaultGraphType.Builder.Builder();
    //     builder.directed();
    //     builder.modifiable(true);
    //     builder.weighted(false);
    //     builder.allowCycles(false);
    //     builder.allowMultipleEdges(true);
    //     builder.allowSelfLoops(false);
    //     return builder.build();
    // }

    // /**
    //  * Required by Graph interface.
    //  */
    // public Node getEdgeSource(Object o) {
    //     Edge e = (Edge) o;
    //     return e.getSource();
    // }

    // /**
    //  * Required by Graph interface.
    //  */
    // public Node getEdgeTarget(Object o) {
    //     Edge e = (Edge) o;
    //     return e.getTarget();
    // }
}
