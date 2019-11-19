package org.ncgr.jgraph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Set;
import java.util.HashSet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.jgrapht.graph.DirectedMultigraph;

/**
 * Storage of a pan-genomic graph in a JGraphT object.
 *
 * @author Sam Hokin
 */ 
public class PangenomicGraph extends DirectedMultigraph<Node,Edge> {

    // output verbosity
    boolean verbose = false;

    // genotype preference (default: -1=load all genotypes)
    public static int BOTH_GENOTYPES = -1;
    int genotype = BOTH_GENOTYPES;

    // skip edges (faster if graph is large)
    boolean skipEdges = false;

    // skip building sequences (reduces RAM)
    boolean skipSequences = false;

    // skip building list of paths per node
    boolean skipNodePaths = false;

    // the GFA file that holds this graph
    File gfaFile;

    // the nodes TXT file read in from a previous dump
    File nodesFile;

    // the paths TXT file read in from a previous dump
    File pathsFile;

    // the file holding the labels for each path (typically "case" and "control")
    File labelsFile;
    
    // each Path provides the ordered list of nodes that it traverses, along with its full sequence
    Set<PathWalk> paths; // (ordered simply for convenience)
    
    // maps a Node to a set of Paths that traverse it
    Map<Long,Set<PathWalk>> nodePaths; // keyed and ordered by Node Id, synchronized when constructed

    // maps a path label to a count of paths that have that label
    Map<String,Integer> labelCounts; // keyed by label
    
    /**
     * Constructor instantiates collections; then use read methods to populate the graph from files.
     */
    public PangenomicGraph() {
        super(Edge.class);
        nodePaths = Collections.synchronizedMap(new HashMap<Long,Set<PathWalk>>());
        labelCounts = new HashMap<>();
    }

    /**
     * Import from a GFA file.
     */
    public void importGFA(File gfaFile) throws NullSequenceException {
        this.gfaFile = gfaFile;
        GFAImporter importer = new GFAImporter();
        if (verbose) importer.setVerbose();
        if (skipEdges) importer.setSkipEdges();
	if (skipSequences) importer.setSkipSequences();
        importer.setGenotype(genotype);
        importer.importGraph(this, gfaFile);
        paths = importer.getPaths();
        if (!skipNodePaths) {
            buildNodePaths();
        } else {
            System.out.println("# Skipped building node paths");
        }
    }

    /**
     * Import from paths.txt and nodes.txt files.
     */
    public void importTXT(File nodesFile, File pathsFile) throws IOException, NullSequenceException {
        this.nodesFile = nodesFile;
        this.pathsFile = pathsFile;
        System.out.println("# Loading graph from "+nodesFile.getName()+" and "+pathsFile.getName());
        Map<Long,Node> nodes = readNodes(nodesFile);
        for (Node n : nodes.values()) {
            addVertex(n);
        }
        this.paths = readPaths(pathsFile);
        if (!skipNodePaths) {
            buildNodePaths();
        } else {
            System.out.println("# Skipped building node paths");
        }
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
     * Return true if this and that PangenomicGraph come from GFA files of the same name.
     */
    public boolean equals(Object o) {
    	PangenomicGraph that = (PangenomicGraph) o;
        if (this.gfaFile!=null && that.gfaFile!=null) {
            return this.gfaFile.getName().equals(that.gfaFile.getName());
        } else {
            return false;
        }
    }

    /**
     * Build the node paths: the set of paths that run through each node.
     */
    void buildNodePaths() throws NullSequenceException {
	if (verbose) System.out.println("Building node paths...");
        // initialize empty paths for each node
        for (Node n : vertexSet()) {
	    if (n.getSequence()==null) {
		throw new NullSequenceException("Node "+n.getId()+" has no sequence. Aborting.");
	    }
            nodePaths.put(n.getId(), Collections.synchronizedSet(new HashSet<PathWalk>()));
        }
        // now load the paths (which are already synchronized from GFAImport) in parallel
        paths.parallelStream().forEach(path -> {
                path.getNodes().parallelStream().forEach(n -> {
                        nodePaths.get(n.getId()).add(path);
                    });
            });
    }

    /**
     * Read path labels from a tab-delimited file. Comment lines start with #.
     */
    public void readPathLabels(File labelsFile) throws FileNotFoundException, IOException {
	if (verbose) System.out.println("Reading path labels...");
        this.labelsFile = labelsFile;
        labelCounts = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(labelsFile));
        String line = null;
        Map<String,String> labels = new HashMap<>();
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
        // flag paths that are not labeled
        boolean pathsAllLabeled = true;
        for (PathWalk path : paths) {
            if (path.getLabel()==null) {
                pathsAllLabeled = false;
                System.err.println("ERROR: the path "+path.getName()+" has no label in the labels file.");
            }
        }
    }

    /**
     * Set the verbose flag.
     */
    public void setVerbose() {
        verbose = true;
    }

    /**
     * Set the skipEdges flag.
     */
    public void setSkipEdges() {
        skipEdges = true;
    }
    /**
     * Return the skipEdges boolean.
     */
    public boolean getSkipEdges() {
        return skipEdges;
    }

    /**
     * Set the skipSequences flag.
     */
    public void setSkipSequences() {
	skipSequences = true;
    }
    /**
     * Return the skipSequences boolean.
     */
    public boolean getSkipSequences() {
        return skipSequences;
    }

    /**
     * Set the skipNodePaths flag.
     */
    public void setSkipNodePaths() {
	skipNodePaths = true;
    }
    /**
     * Return the skipNodePaths boolean.
     */
    public boolean getSkipNodePaths() {
        return skipNodePaths;
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
    public int prune() throws NoPathsException, NoNodePathsException {
        if (verbose) System.out.println("Pruning graph...");
        if (paths==null || paths.size()==0) {
            throw new NoPathsException("PangenomicGraph.paths is not populated; cannot prune.");
        }
        if (nodePaths==null || nodePaths.size()==0) {
            throw new NoNodePathsException("PangenomicGraph.nodePaths is not populated; cannot prune.");
        }
        Set<Node> nodesToRemove = new HashSet<>();
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
    public Set<PathWalk> getPaths() {
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
     * Return a map of nodes read from a file written by printNodes().
     * 1       C
     * 2       T
     * 3       TCCTTCTGCTCAACTTTC
     */
    public static Map<Long,Node> readNodes(File nodesFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(nodesFile));
        String line = null;
        Map<Long,Node> nodes = new HashMap<>();
        while ((line=reader.readLine())!=null) {
            String[] parts = line.split("\t");
            long id = Long.parseLong(parts[0]);
            String sequence = parts[1];
            Node n = new Node(id, sequence);
            nodes.put(id, n);
        }
        return nodes;
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
            StringBuilder builder = new StringBuilder();
            builder.append("length="+len+"\t("+counts+")\t");
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
            StringBuilder builder = new StringBuilder();
            builder.append(path.getNameGenotype());
            builder.append("\t"+path.getLabel());
            if (!skipSequences) builder.append("\t"+path.getSequence().length());
            for (Node node : path.getNodes()) {
                builder.append("\t"+node.getId());
            }
            out.println(builder.toString());
        }
    }

    /**
     * Return a set of paths read from a file written by printPaths(). Assume that sequences are NOT included.
     * 0        1       2       3       4       ...
     * 974679.0 case    473     1       3       4
     * 128686.1 case    473     1       3       4
     * 412119.0 ctrl    406     1       3       4
     * 434159.1 case    461     1       3       4
     */
    public static Set<PathWalk> readPaths(File pathsFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(pathsFile));
        String line = null;
        Set<PathWalk> paths = new HashSet<>();
        while ((line=reader.readLine())!=null) {
            String[] parts = line.split("\t");
            String nameGenotype = parts[0];
            String label = parts[1];
            int sequenceLength = Integer.parseInt(parts[2]);
            String[] pieces = nameGenotype.split("\\.");
            String name = pieces[0];
            int genotype = Integer.parseInt(pieces[1]);
            List<Node> nodeList = new ArrayList<>();
            for (int i=3; i<parts.length; i++) {
                nodeList.add(new Node(Long.parseLong(parts[i]), parts[i]));
            }
            paths.add(new PathWalk(nodeList, name, genotype, label));
        }
        return paths;
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
            StringBuilder builder = new StringBuilder();
            builder.append(nodeId);
            for (PathWalk path : pathSet) {
                builder.append("\t"+path.getNameGenotype());
            }
            out.println(builder.toString());
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
        // rows are nodes and counts of path support of each node
        for (Node node : vertexSet()) {
            builder = new StringBuilder();
            builder.append("N"+node.getId());
            Set<PathWalk> nPaths = nodePaths.get(node.getId());
            for (PathWalk path : paths) {
		// spin through the path, counting occurrences of this node
		List<Node> nodeList = path.getNodes();
		int count = 0;
		for (Node n : nodeList) {
		    if (n.equals(node)) count++;
		}
		builder.append("\t"+count);
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

        if (verbose) System.out.println("Writing nodes file...");
        PrintStream nodesOut = new PrintStream(outputPrefix+".nodes.txt");
        printNodes(nodesOut);

        if (verbose) System.out.println("Writing nodes histogram...");
        PrintStream nodeHistogramOut = new PrintStream(outputPrefix+".nodehistogram.txt");
        printNodeHistogram(nodeHistogramOut);

        if (verbose) System.out.println("Writing paths file...");
        PrintStream pathsOut = new PrintStream(outputPrefix+".paths.txt");
        printPaths(pathsOut);

        if (!skipNodePaths) {
            if (verbose) System.out.println("Writing node paths file...");
            PrintStream nodePathsOut = new PrintStream(outputPrefix+".nodepaths.txt");
            printNodePaths(nodePathsOut);
        }
        
        if (!skipSequences) {
            if (verbose) System.out.println("Writing path sequences file...");
            PrintStream pathSequencesOut = new PrintStream(outputPrefix+".pathsequences.fasta");
            long printPathSequencesStart = System.currentTimeMillis();
            printPathSequences(pathSequencesOut);
            long printPathSequencesEnd = System.currentTimeMillis();
            if (verbose) System.out.println("printPathSequences took "+(printPathSequencesEnd-printPathSequencesStart)+" ms.");
        }
        
        if (verbose) System.out.println("Writing path PCA file...");
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
        if (!skipNodePaths) printNodePaths(System.out);
        if (!skipSequences) printPathSequences(System.out);
        printPcaData(System.out);
    }

    /**
     * Command-line utility
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, NullSequenceException {

        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        Option gfaOption = new Option("g", "gfa", true, "vg GFA file");
        gfaOption.setRequired(false);
        options.addOption(gfaOption);
        //
        Option txtOption = new Option("t", "txt", true, "prefix of previously dumped TXT files");
        txtOption.setRequired(false);
        options.addOption(txtOption);
        //
        Option genotypeOption = new Option("gt", "genotype", true, "which genotype to include (0,1) from the GFA file; -1 to include both (-1)");
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
        Option verboseOption = new Option("v", "verbose", false, "verbose output (false)");
        verboseOption.setRequired(false);
        options.addOption(verboseOption);
        //
        Option skipEdgesOption = new Option("se", "skipedges", false, "skip adding edges to graph (false)");
        skipEdgesOption.setRequired(false);
        options.addOption(skipEdgesOption);
        //
        Option skipSequencesOption = new Option("ss", "skipsequences", false, "skip building sequences of paths (false)");
        skipSequencesOption.setRequired(false);
        options.addOption(skipSequencesOption);
        //
        Option skipNodePathsOption = new Option("snp", "skipnodepaths", false, "skip building list of paths per node (false)");
        skipNodePathsOption.setRequired(false);
        options.addOption(skipNodePathsOption);

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
        if (!cmd.hasOption("gfa") && !cmd.hasOption("txt")) {
            System.err.println("You must specify a vg GFA file (--gfa) or prefix of previously dumped TXT files (--txt)");
            System.exit(1);
            return;
        }
        
        // files
        File gfaFile = null;
        File nodesFile = null;
        File pathsFile = null;
        if (cmd.hasOption("gfa")) {
            gfaFile = new File(cmd.getOptionValue("gfa"));
        } else if (cmd.hasOption("txt")) {
            String prefix = cmd.getOptionValue("txt");
            nodesFile = new File(prefix+".nodes.txt");
            pathsFile = new File(prefix+".paths.txt");
        }
        File labelsFile = new File(cmd.getOptionValue("pathlabels"));

        // create a PangenomicGraph from a GFA file or pair of TXT files
        PangenomicGraph pg = new PangenomicGraph();
        if (cmd.hasOption("verbose")) pg.setVerbose();
        if (cmd.hasOption("skipedges")) pg.setSkipEdges();
	if (cmd.hasOption("skipsequences")) pg.setSkipSequences();
        if (cmd.hasOption("skipnodepaths")) pg.setSkipNodePaths();
        if (cmd.hasOption("genotype")) pg.setGenotype(Integer.parseInt(cmd.getOptionValue("genotype")));
        long importStart = System.currentTimeMillis();
        if (gfaFile!=null) {
            pg.importGFA(gfaFile);
        } else if (nodesFile!=null && pathsFile!=null) {
            pg.importTXT(nodesFile, pathsFile);
        }
        long importEnd = System.currentTimeMillis();
        if (pg.verbose) System.out.println("Graph import took "+(importEnd-importStart)+" ms.");

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
