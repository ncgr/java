package org.ncgr.pangenomics;

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
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import org.mskcc.cbio.portal.stats.FisherExact;

/**
 * Storage of a pan-genomic graph in a JGraphT object.
 *
 * @author Sam Hokin
 */ 
public class PangenomicGraph extends DirectedAcyclicGraph<Node,Edge> {

    // output verbosity
    boolean verbose = false;

    // a name for this graph
    String graphName;
    
    // genotype preference (default: -1=load all genotypes)
    public static int BOTH_GENOTYPES = -1;
    int genotype = BOTH_GENOTYPES;

    // skip edges (faster if graph is large)
    boolean skipEdges = false;

    // skip building sequences (reduces RAM)
    boolean skipSequences = false;

    // skip building list of paths per node
    boolean skipNodePaths = false;

    // the file holding the labels for each path (typically "case" and "control")
    File labelsFile;
    
    // each Path provides the ordered list of nodes that it traverses, along with its full sequence
    List<PathWalk> paths;
    
    // maps a Node to the Paths that traverse it
    ConcurrentHashMap<Long,List<PathWalk>> nodePaths; // keyed and ordered by Node Id, synchronized when constructed

    // maps a path label to a count of paths that have that label
    Map<String,Integer> labelCounts; // keyed by label

    // computed once to save time
    DijkstraShortestPath<Node,Edge> dsp;

    /**
     * Construct the empty graph using the Edge class.
     */
    public PangenomicGraph() {
        super(Edge.class);
    }

    /**
     * Import from a GFA file.
     */
    public void importGFA(File gfaFile) throws NullNodeException, NullSequenceException {
        GFAImporter importer = new GFAImporter();
        if (verbose) importer.setVerbose();
        if (skipEdges) importer.setSkipEdges();
	if (skipSequences) importer.setSkipSequences();
        importer.setGenotype(genotype);
        importer.importGraph(this, gfaFile);
        paths = importer.getPaths();
        if (skipNodePaths) {
            System.out.println("# Skipped building node paths");
        } else {
            buildNodePaths();
        }
        dsp = new DijkstraShortestPath<Node,Edge>(this);
    }

    /**
     * Import from a pair of TXT files.
     */
    public void importTXT(File nodesFile, File pathsFile) throws IOException, NullNodeException, NullSequenceException {
        TXTImporter importer = new TXTImporter();
        if (verbose) importer.setVerbose();
        if (skipEdges) importer.setSkipEdges();
	if (skipSequences) importer.setSkipSequences();
        importer.setGenotype(genotype);
        importer.importGraph(this, nodesFile, pathsFile);
        paths = importer.getPaths();
        if (skipNodePaths) {
            System.out.println("# Skipped building node paths");
        } else {
            buildNodePaths();
        }
        dsp = new DijkstraShortestPath<Node,Edge>(this);
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
     * Build the node paths: the set of paths that run through each node.
     */
    void buildNodePaths() throws NullSequenceException {
	if (verbose) System.out.print("Building node paths...");
        nodePaths = new ConcurrentHashMap<>();
        // initialize empty paths for each node
        for (Node n : vertexSet()) {
            nodePaths.put(n.getId(), Collections.synchronizedList(new ArrayList<PathWalk>()));
        }
        // now load the paths (which are already synchronized from the importer) in parallel
        paths.parallelStream().forEach(path -> {
                List<Node> nodeList = Collections.synchronizedList(path.getNodes());
                nodeList.parallelStream().forEach(n -> {
                        nodePaths.get(n.getId()).add(path);
                    });
            });
	if (verbose) System.out.println("done.");
    }

    /**
     * Read path labels from a tab-delimited file. Comment lines start with #.
     * 28304	case
     * 60372	ctrl
     */
    public void readPathLabels(File labelsFile) throws FileNotFoundException, IOException {
	if (verbose) System.out.print("Reading path labels...");
        this.labelsFile = labelsFile;
        BufferedReader reader = new BufferedReader(new FileReader(labelsFile));
        String line = null;
        ConcurrentHashMap<String,String> labels = new ConcurrentHashMap<>();
        while ((line=reader.readLine())!=null) {
            if (!line.startsWith("#")) {
                String[] fields = line.split("\t");
                if (fields.length==2) {
                    labels.put(fields[0], fields[1]);
                }
            }
        }
        reader.close();
        // set the labels for each path
        paths.parallelStream().forEach(path -> {
                if (labels.containsKey(path.getName())) {
                    path.setLabel(labels.get(path.getName()));
                } else if (labels.containsKey(path.getName()+".0")) {
                    path.setLabel(labels.get(path.getName()+".0"));
                } else if (labels.containsKey(path.getName()+".1")) {
                    path.setLabel(labels.get(path.getName()+".1"));
                } else {
                    System.err.println("ERROR: the path "+path.getName()+" has no label in the labels file.");
                }
            });
        if (verbose) System.out.println("done.");
    }

    /**
     * Tally the label counts from the labeled paths.
     * NOTE: this has to be done serially since it's an increasing tally.
     */
    public void tallyLabelCounts() {
        labelCounts = new TreeMap<>();
        for (PathWalk path : paths) {
            String label = path.getLabel();
            if (labelCounts.containsKey(label)) {
                int count = labelCounts.get(label);
                labelCounts.put(label, count+1);
            } else {
                labelCounts.put(label, 1);
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
     * Return the DijkstraShortestPath of this graph
     */
    public DijkstraShortestPath<Node,Edge> getDSP() {
        return dsp;
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
     * Set this graph's name.
     */
    public void setName(String graphName) {
        this.graphName = graphName;
    }

    /**
     * Return this graph's name.
     */
    public String getName() {
        return graphName;
    }

    /**
     * Prune this graph: remove all nodes that are traversed by ALL paths.
     * paths and nodePaths must have been populated before this is called.
     * @return the number of removed nodes
     */
    public int prune() throws NoPathsException, NoNodePathsException {
        if (verbose) System.out.print("Pruning graph...");
        if (paths==null || paths.size()==0) {
            throw new NoPathsException("PangenomicGraph.paths is not populated; cannot prune.");
        }
        if (nodePaths==null || nodePaths.size()==0) {
            throw new NoNodePathsException("PangenomicGraph.nodePaths is not populated; cannot prune.");
        }
        List<Node> nodesToRemove = new ArrayList<>();
        for (Node n : getNodes()) {
            List<PathWalk> thisPaths = nodePaths.get(n.id);
            if (thisPaths.size()==paths.size()) nodesToRemove.add(n);
        }
        for (Node n : nodesToRemove) {
            removeVertex(n);
        }
        if (verbose) System.out.println("done.");
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
     * Return the node with the given id, else null.
     */
    public Node getNode(long id) {
        Node node = null;
        for (Node n : getNodes()) {
            if (n.id==id) {
                node = n;
                break;
            }
        }
        return node;
    }

    /**
     * Get this graph's paths.
     */
    public List<PathWalk> getPaths() {
        return paths;
    }

    /**
     * Get the paths that traverse the given node.
     */
    public List<PathWalk> getPaths(Node n) {
        return nodePaths.get(n.getId());
    }

    /**
     * Return the odds ratio for case paths versus control paths that traverse the given node.
     */
    public double oddsRatio(Node n) {
        Map<String,Integer> nodeLabelCounts = getLabelCounts(n);
        int allCasePaths = labelCounts.get("case");
        int allCtrlPaths = labelCounts.get("ctrl");
        int nodeCasePaths = 0; if (nodeLabelCounts.containsKey("case")) nodeCasePaths = nodeLabelCounts.get("case");
        int nodeCtrlPaths = 0; if (nodeLabelCounts.containsKey("ctrl")) nodeCtrlPaths = nodeLabelCounts.get("ctrl");
        return ((double)nodeCasePaths/(double)nodeCtrlPaths) / ((double)allCasePaths/(double)allCtrlPaths);
    }

    /**
     * Return the Fisher's exact test p value for case paths vs control paths that traverse the given node.
     */
    public double fisherExactP(Node n) {
        Map<String,Integer> nodeLabelCounts = getLabelCounts(n);
        int allCasePaths = labelCounts.get("case");
        int allCtrlPaths = labelCounts.get("ctrl");
        int nodeCasePaths = 0; if (nodeLabelCounts.containsKey("case")) nodeCasePaths = nodeLabelCounts.get("case");
        int nodeCtrlPaths = 0; if (nodeLabelCounts.containsKey("ctrl")) nodeCtrlPaths = nodeLabelCounts.get("ctrl");
        int maxSize = allCasePaths + allCtrlPaths + nodeCasePaths + nodeCtrlPaths;
        FisherExact fisherExact = new FisherExact(maxSize);
        return fisherExact.getTwoTailedP(nodeCasePaths, nodeCtrlPaths, allCasePaths, allCtrlPaths);
    }

    /**
     * Get this graph's label counts.
     */
    public Map<String,Integer> getLabelCounts() {
        return labelCounts;
    }

    /**
     * Get the label counts for paths that traverse the given node.
     */
    public Map<String,Integer> getLabelCounts(Node n) {
        Map<String,Integer> nLabelCounts = new HashMap<>();
        for (PathWalk p : nodePaths.get(n.getId())) {
            if (nLabelCounts.containsKey(p.getLabel())) {
                int count = nLabelCounts.get(p.getLabel()) + 1;
                nLabelCounts.put(p.getLabel(), count);
            } else {
                nLabelCounts.put(p.getLabel(), 1);
            }
        }
        return nLabelCounts;
    }

    /**
     * Get the label counts for paths that follow the given Edge.
     * TODO: restrict path loop to those that are on Nodes connected by Edge. May not be worth it.
     */
    public Map<String,Integer> getLabelCounts(Edge e) {
        Map<String,Integer> eLabelCounts = new HashMap<>();
        for (PathWalk p : paths) {
            List<Edge> edges = p.getEdges();
            if (edges.contains(e)) {
                if (eLabelCounts.containsKey(p.getLabel())) {
                    int count = eLabelCounts.get(p.getLabel()) + 1;
                    eLabelCounts.put(p.getLabel(), count);
                } else {
                    eLabelCounts.put(p.getLabel(), 1);
                }
            }
        }
        return eLabelCounts;
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
        for (Edge e : edgeSet()) {
            builder.append(e.toString());
        }
        out.println(builder.toString());
    }

    /**
     * Print out the node paths along with counts.
     */
    public void printNodePaths(PrintStream out) {
        if (out==System.out) printHeading("NODE PATHS");
        for (Long nodeId : nodePaths.keySet()) {
            List<PathWalk> pathList = nodePaths.get(nodeId);
            StringBuilder builder = new StringBuilder();
            builder.append(nodeId);
            for (PathWalk path : pathList) {
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
            List<PathWalk> nPaths = nodePaths.get(node.getId());
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

        if (verbose) System.out.print("Writing nodes file...");
        PrintStream nodesOut = new PrintStream(outputPrefix+".nodes.txt");
        printNodes(nodesOut);
        if (verbose) System.out.println("done.");

        if (verbose) System.out.print("Writing nodes histogram...");
        PrintStream nodeHistogramOut = new PrintStream(outputPrefix+".nodehistogram.txt");
        printNodeHistogram(nodeHistogramOut);
        if (verbose) System.out.println("done.");

        if (verbose) System.out.print("Writing paths file...");
        PrintStream pathsOut = new PrintStream(outputPrefix+".paths.txt");
        printPaths(pathsOut);
        if (verbose) System.out.println("done.");

        if (!skipNodePaths) {
            if (verbose) System.out.print("Writing node paths file...");
            PrintStream nodePathsOut = new PrintStream(outputPrefix+".nodepaths.txt");
            printNodePaths(nodePathsOut);
            if (verbose) System.out.println("done.");
            if (verbose) System.out.print("Writing path PCA file...");
            PrintStream pcaDataOut = new PrintStream(outputPrefix+".pathpca.txt");
            printPcaData(pcaDataOut);
            if (verbose) System.out.println("done.");
        }
        
        if (!skipSequences) {
            if (verbose) System.out.print("Writing path sequences file...");
            PrintStream pathSequencesOut = new PrintStream(outputPrefix+".pathsequences.fasta");
            printPathSequences(pathSequencesOut);
            if (verbose) System.out.println("done.");
        }
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
    public static void main(String[] args) throws FileNotFoundException, IOException, NullNodeException, NullSequenceException {

        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        Option graphOption = new Option("graph", "graph", true, "name of graph");
        graphOption.setRequired(true);
        options.addOption(graphOption);
        //
        Option gfaOption = new Option("gfa", "gfa", false, "load from a vg GFA file");
        gfaOption.setRequired(false);
        options.addOption(gfaOption);
        //
        Option txtOption = new Option("txt", "txt", false, "load from previously dumped TXT files");
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
            System.err.println("You must specify loading from a vg GFA file (--gfa) or previously dumped TXT files (--txt)");
            System.exit(1);
            return;
        }

        // create the graph
        PangenomicGraph graph = new PangenomicGraph();
        String graphName = cmd.getOptionValue("graph");
        graph.setName(graphName);
        
        // apply various options
        if (cmd.hasOption("verbose")) graph.setVerbose();
        if (cmd.hasOption("skipedges")) graph.setSkipEdges();
	if (cmd.hasOption("skipsequences")) graph.setSkipSequences();
        if (cmd.hasOption("skipnodepaths")) graph.setSkipNodePaths();
        if (cmd.hasOption("genotype")) graph.setGenotype(Integer.parseInt(cmd.getOptionValue("genotype")));
        // import the graph from a GFA file or pair of TXT files
        long importStart = System.currentTimeMillis();
        if (cmd.hasOption("gfa")) {
            File gfaFile = new File(graphName+".paths.gfa");
            graph.importGFA(gfaFile);
            // if a labels file is given, add them to the paths
            if (cmd.hasOption("pathlabels")) {
                File labelsFile = new File(cmd.getOptionValue("pathlabels"));
                graph.readPathLabels(labelsFile);
                graph.tallyLabelCounts();
            }
        } else if (cmd.hasOption("txt")) {
            File nodesFile = new File(graphName+".nodes.txt");
            File pathsFile = new File(graphName+".paths.txt");
            graph.importTXT(nodesFile, pathsFile);
            graph.tallyLabelCounts();
        }
        long importEnd = System.currentTimeMillis();
        if (graph.verbose) System.out.println("Graph import took "+(importEnd-importStart)+" ms.");

        // output
        if (cmd.hasOption("gfa")) {
            if (cmd.hasOption("outputprefix")) {
                // verbosity
                if (cmd.hasOption("verbose")) graph.printLabelCounts(System.out);
                // files
                graph.printAll(cmd.getOptionValue("outputprefix"));
            } else {
                // stdout
                graph.printAll();
            }
        }
    }
}
