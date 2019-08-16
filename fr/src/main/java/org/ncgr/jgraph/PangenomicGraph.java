package org.ncgr.jgraph;

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

    // the GFA file that holds this graph
    File gfaFile;

    // the file holding the labels for each path (typically "case" and "control")
    File labelsFile;
    
    // each Path provides the ordered list of nodes that it traverses, along with its full sequence
    TreeSet<PathWalk> paths; // (ordered simply for convenience)
    
    // maps a Node to a set of Paths that traverse it
    TreeMap<Long,Set<PathWalk>> nodePaths; // keyed and ordered by Node Id (for convenience)

    // maps a path label to a count of paths that have that label
    Map<String,Integer> labelCounts; // keyed by label
    
    /**
     * Constructor instantiates collections; then use read methods to populate the graph from files.
     */
    public PangenomicGraph() {
        super(Edge.class);
        nodePaths = new TreeMap<>();
        labelCounts = new TreeMap<>();
    }

    /**
     * Import from a GFA file.
     */
    public void importGFA(File gfaFile) {
        this.gfaFile = gfaFile;
        GFAImporter importer = new GFAImporter();
        if (verbose) importer.setVerbose();
        importer.setGenotype(genotype);
        importer.importGraph(this, gfaFile);
        paths = importer.getPaths();
        buildNodePaths();
    }

    /**
     * Return the GFA filename.
     */
    public String getGFAFilename() {
        if (gfaFile==null) {
            return null;
        } else {
            return gfaFile.getPath();
        }
    }

    /**
     * Return the path labels filename.
     */
    public String getPathLabelsFilename() {
        if (labelsFile==null) {
            return null;
        } else {
            return labelsFile.getPath();
        }
    }

    /**
     * Return true if this and that PangenomicGraph come from the same file.
     */
    public boolean equals(PangenomicGraph that) {
        if (this.gfaFile!=null && that.gfaFile!=null) {
            return this.gfaFile.equals(that.gfaFile);
        } else {
            return false;
        }
    }

    /**
     * Build the node paths: the set of paths that run through each node.
     */
    void buildNodePaths() {
        // init empty paths for each node
        for (Node n : vertexSet()) {
            nodePaths.put(n.getId(), new TreeSet<PathWalk>());
        }
        // now load the paths
        for (PathWalk path : paths) {
            for (Node n : path.getNodes()) {
                nodePaths.get(n.getId()).add(path);
            }
        }
    }

    /**
     * Read path labels from a tab-delimited file. Comment lines start with #.
     */
    public void readPathLabels(File labelsFile) throws FileNotFoundException, IOException {
        this.labelsFile = labelsFile;
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
                if (sample.equals(path.getName())) {
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
                        if (sampleName.equals(path.getName()) && sampleGenotype==path.getGenotype()) {
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
        // check that we've labeled all the paths
        boolean pathsAllLabeled = true;
        for (PathWalk path : paths) {
            if (path.getLabel()==null) {
                pathsAllLabeled = false;
                System.err.println("ERROR: the path "+path.getName()+" has no label in the labels file.");
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
     * Prune this graph: remove all nodes that are traversed by ALL paths.
     * paths and nodePaths must have been populated before this is called.
     * @return the number of removed nodes
     */
    public int prune() throws Exception {
        if (paths==null || paths.size()==0) {
            throw new Exception("PangenomicGraph.paths is not populated; cannot prune.");
        }
        if (nodePaths==null || nodePaths.size()==0) {
            throw new Exception("PangenomicGraph.nodePaths is not populated; cannot prune.");
        }
        Set<Node> nodesToRemove = new TreeSet<>();
        for (Node n : getNodes()) {
            Set<PathWalk> thisPaths = nodePaths.get(n.id);
            if (thisPaths.size()==paths.size()) nodesToRemove.add(n);
        }
        for (Node n : nodesToRemove) {
            removeVertex(n);
        }
        return nodesToRemove.size();
    }

    /**
     * Return the genotype preference: -1=both; 0 and 1
     */
    public int getGenotype() {
        return genotype;
    }

    /**
     * Just a synonym for vertexSet().
     */
    public Set<Node> getNodes() {
        return vertexSet();
    }

    /**
     * Get this graph's paths.
     */
    public TreeSet<PathWalk> getPaths() {
        return paths;
    }

    /**
     * Get this graph's label counts.
     */
    public Map<String,Integer> getLabelCounts() {
        return labelCounts;
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
        for (Node n : vertexSet()) {
            out.println(n.getId()+"\t"+n.getSequence());
        }
    }

    /**
     * Print out a histogram of node sizes.
     */
    public void printNodeHistogram(PrintStream out) {
        if (out==System.out) printHeading("k HISTOGRAM");
        Map<Integer,Integer> countMap = new TreeMap<>();
        for (Node node : vertexSet()) {
            int length = node.getSequence().length();
            if (countMap.containsKey(length)) {
                countMap.put(length, ((int)countMap.get(length))+1);
            } else {
                countMap.put(length, 1);
            }
        }
        for (int len : countMap.keySet()) {
            int counts = countMap.get(len);
            out.print("length="+len+"\t("+counts+")\t");
            StringBuilder builder = new StringBuilder();
            for (int i=1; i<=counts; i++) builder.append("X");
            out.println(builder.toString());
        }
    }

    /**
     * Print the paths, labeled by pathName.
     */
    public void printPaths(PrintStream out) {
        if (out==System.out) printHeading("PATHS");
        for (PathWalk path : paths) {
            out.print(path.getNameGenotype()+"\t"+path.getLabel()+"\t"+path.getSequence().length());
            StringBuilder builder = new StringBuilder();
            for (Node node : path.getNodes()) {
                builder.append("\t"+node.getId());
            }
            out.println(builder.toString());
        }
    }

    /**
     * Print the edges.
     */
    public void printEdges(PrintStream out) {
        if (out==System.out) printHeading("EDGES");
        StringBuilder builder = new StringBuilder();
        String lastNameGenotype = "";
        for (Edge e : edgeSet()) {
            if (!e.getNameGenotype().equals(lastNameGenotype)) {
                out.println(builder.toString());
                builder = new StringBuilder();
                builder.append(e.toString());
            } else {
                builder.append(" "+e.toString());
            }
            lastNameGenotype = e.getNameGenotype();
        }
        out.println(builder.toString());
    }

    /**
     * Print out the node paths along with counts.
     */
    public void printNodePaths(PrintStream out) {
        if (out==System.out) printHeading("NODE PATHS");
        for (Long nodeId : nodePaths.keySet()) {
            Set<PathWalk> pathSet = nodePaths.get(nodeId);
            out.print(String.valueOf(nodeId));
            for (PathWalk path : pathSet) {
                out.print("\t"+path.getNameGenotype());
            }
            out.println("");
        }
    }

    /**
     * Print the sequences for each path, in FASTA format, labeled by path.getName().genotype.
     */
    public void printPathSequences(PrintStream out) {
        if (out==System.out) printHeading("PATH SEQUENCES");
        for (PathWalk path : paths) {
            String heading = ">"+path.getNameGenotype()+" ("+path.getSequence().length()+")";
            out.print(heading);
            // add dots every 10 bases to the heading
            int h = 19;
            int m = 8;
            if (heading.length()>=39) {
                h = 49;
                m = 5;
            } else if (heading.length()>=29) {
                h = 39;
                m = 6;
            } else if (heading.length()>=19) {
                h = 29;
                m = 7;
            }
            for (int i=heading.length(); i<h; i++) out.print(" "); out.print(".");
            for (int n=0; n<m; n++) {
                for (int i=0; i<9; i++) out.print(" "); out.print(".");
            }
            out.println("");
            // print out the sequence, 100 chars to a line
            String sequence = path.getSequence();
            for (int i=0; i<sequence.length(); i+=100) {
                int j = i + 100;
                if (j>sequence.length()) j = sequence.length();
                out.println(sequence.substring(i, j));
            }
        }
    }

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
    public void printPcaData(PrintStream out) throws FileNotFoundException, IOException {
        StringBuilder builder = new StringBuilder();
        // header is paths
        boolean first = true;
        for (PathWalk path : paths) {
            if (first) {
                builder.append(path.getNameGenotype());
                first = false;
            } else {
                builder.append("\t"+path.getNameGenotype());
            }
            if (path.getLabel()!=null) builder.append("."+path.getLabel());
        }
        out.println(builder.toString());
        // rows are nodes
        for (Node node : vertexSet()) {
            builder = new StringBuilder();
            Set<PathWalk> nPaths = nodePaths.get(node.getId());
            builder.append("N"+node.getId());
            for (PathWalk path : paths) {
                if (path.getNodes().contains(node)) {
                    builder.append("\t1");
                } else {
                    builder.append("\t0");
                }
            }
            out.println(builder.toString());
        }
    }

    /**
     * Run all the PangenomicGraph printing methods to files.
     */
    public void printAll(String outputPrefix) throws FileNotFoundException, IOException {
        if (outputPrefix==null) return;
        if (labelCounts.size()>0) {
            PrintStream labelCountsOut = new PrintStream(outputPrefix+".labelcounts.txt");
            printLabelCounts(labelCountsOut);
        }

        PrintStream nodesOut = new PrintStream(outputPrefix+".nodes.txt");
        printNodes(nodesOut);

        PrintStream nodeHistogramOut = new PrintStream(outputPrefix+".nodehistogram.txt");
        printNodeHistogram(nodeHistogramOut);

        PrintStream pathsOut = new PrintStream(outputPrefix+".paths.txt");
        printPaths(pathsOut);

        PrintStream nodePathsOut = new PrintStream(outputPrefix+".nodepaths.txt");
        printNodePaths(nodePathsOut);
        
        PrintStream pathSequencesOut = new PrintStream(outputPrefix+".pathsequences.fasta");
        long printPathSequencesStart = System.currentTimeMillis();
        printPathSequences(pathSequencesOut);
        long printPathSequencesEnd = System.currentTimeMillis();
        if (verbose) System.out.println("printPathSequences took "+(printPathSequencesEnd-printPathSequencesStart)+" ms.");
        
        PrintStream pcaDataOut = new PrintStream(outputPrefix+".pathpca.txt");
        long printPcaDataStart = System.currentTimeMillis();
        printPcaData(pcaDataOut);
        long printPcaDataEnd = System.currentTimeMillis();
        if (verbose) System.out.println("printPcaData took "+(printPcaDataEnd-printPcaDataStart)+" ms.");
    }

    /**
     * Run all the PangenomicGraph printing methods to stdout.
     */
    public void printAll() throws FileNotFoundException, IOException {
        if (labelCounts.size()>0) {
            printLabelCounts(System.out);
        }
        printNodes(System.out);
        printNodeHistogram(System.out);
        printPaths(System.out);
        printNodePaths(System.out);
        printPathSequences(System.out);
        printPcaData(System.out);
    }

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
        long importStart = System.currentTimeMillis();
        pg.importGFA(gfaFile);
        long importEnd = System.currentTimeMillis();
        if (pg.verbose) System.out.println("GFA import took "+(importEnd-importStart)+" ms.");

        // if a labels file is given, add them to the paths
        if (labelsFile!=null) {
            pg.readPathLabels(labelsFile);
        }

        // output
        if (cmd.hasOption("outputprefix")) {
            // verbosity
            if (cmd.hasOption("verbose")) pg.printLabelCounts(System.out);
            // files
            pg.printAll(cmd.getOptionValue("outputprefix"));
        } else {
            // stdout
            pg.printAll();
        }
    }
}
