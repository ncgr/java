package org.ncgr.pangenomics.genotype;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

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

public class PangenomicGraph extends DirectedAcyclicGraph<Node,Edge> {

    // output verbosity
    public boolean verbose = false;

    // a name for this graph
    public String name;

    // the file holding the labels for each path (typically "case" and "control")
    public File labelsFile;

    // the VCF file holding the graph data
    public File vcfFile;

    // the TXT files holding the nodes and paths
    public File nodesFile;
    public File pathsFile;
    
    // list of the paths that traverse this graph
    public List<Path> paths;

    // map Path names to their Path (convenience)
    public Map<String,Path> pathNameMap;

    // map Node ids to their Node (convenience)
    public Map<Long,Node> nodeIdMap;

    // maps a Node to the Paths that traverse it
    public Map<Node,List<Path>> nodePaths;

    // associate samples with labels like "case" and "ctrl"
    public Map<String,String> sampleLabels;
    
    // maps a path label to a count of paths that have that label
    public Map<String,Integer> labelCounts; // keyed by label

    // computed once to save time
    DijkstraShortestPath<Node,Edge> dsp;

    /**
     * Basic constructor.
     */
    public PangenomicGraph() {
        super(Edge.class);
    }

    /**
     * Build this graph from the provided Nodes and sample name collections.
     */
    void buildGraph(Map<String,String> sampleLabels, List<Node> nodes, Map<String,List<Node>> sampleNodesMap, Map<Node,List<String>> nodeSamplesMap) {
        this.sampleLabels = sampleLabels;
        paths = new ArrayList<Path>();
        pathNameMap = new HashMap<>();
        nodeIdMap = new HashMap<>();
        // add the nodes as graph vertices
        if (verbose) System.out.print("Adding nodes to graph vertices...");
        for (Node n : nodes) {
            addVertex(n);
            nodeIdMap.put(n.id, n);
        }
        if (verbose) System.out.println("done.");
        // build the paths and path-labeled graph edges from the sample-nodes map
        if (verbose) System.out.print("Creating paths and adding edges to graph...");
        for (String sampleName : sampleNodesMap.keySet()) {
            List<Node> sampleNodes = sampleNodesMap.get(sampleName);
            Path path = new Path(this, sampleNodes, sampleName, sampleLabels.get(sampleName));
            paths.add(path);
            pathNameMap.put(sampleName, path);
            // add edges
            Node lastNode = null;
            for (Node node : sampleNodesMap.get(sampleName)) {
                if (lastNode!=null) {
                    if (!containsEdge(lastNode, node)) {
                        try {
                            addEdge(lastNode, node);
                        } catch (Exception e) {
                            System.err.println("ERROR adding edge from "+lastNode+" to "+node);
                            System.err.println(e);
                            System.exit(1);
                        }
                    }
                }
                lastNode = node;
            }
        }
        if (verbose) System.out.println("done.");
    }

    /**
     * Build this graph from the provided Lists of Nodes and Paths.
     */
    void buildGraph(List<Node> nodes, List<Path> paths) {
        this.paths = paths;
        pathNameMap = new HashMap<>();
        nodeIdMap = new HashMap<>();
        // add the nodes as graph vertices
        if (verbose) System.out.print("Adding nodes to graph vertices...");
        for (Node n : nodes) {
            addVertex(n);
            nodeIdMap.put(n.id, n);
        }
        if (verbose) System.out.println("done.");
        // build the path-labeled graph edges
        if (verbose) System.out.print("Adding edges to graph from paths...");
        for (Path path : paths) {
            pathNameMap.put(path.name, path);
            // add edges
            Node lastNode = null;
            for (Object n : path.getNodes()) {
                Node node = (Node) n;
                if (lastNode!=null) {
                    if (!containsEdge(lastNode, node)) {
                        try {
                            addEdge(lastNode, node);
                        } catch (Exception e) {
                            System.err.println("ERROR adding edge from "+lastNode+" to "+node);
                            System.err.println(e);
                            System.exit(1);
                        }
                    }
                }
                lastNode = node;
            }
        }
        if (verbose) System.out.println("done.");
    }

    /**
     * Read sample labels from the instance tab-delimited file. Comment lines start with #.
     *
     * 28304	case
     * 60372	ctrl
     */
    public void readSampleLabels() throws FileNotFoundException, IOException {
        if (labelsFile==null) {
            System.err.println("ERROR: graph.labelsFile is not set!");
            System.exit(1);
        }
	if (verbose) System.out.print("Reading sample labels...");
        sampleLabels = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(labelsFile));
        String line = null;
        while ((line=reader.readLine())!=null) {
            if (!line.startsWith("#")) {
                String[] fields = line.split("\t");
                if (fields.length==2) {
                    sampleLabels.put(fields[0], fields[1]); // sample,label
                }
            }
        }
        reader.close();
        if (verbose) System.out.println("done.");
    }

    /**
     * Tally the label counts.
     */
    public void tallyLabelCounts() {
        labelCounts = new TreeMap<>();
        for (String sampleName : sampleLabels.keySet()) {
            String label = sampleLabels.get(sampleName);
            if (labelCounts.containsKey(label)) {
                int count = labelCounts.get(label);
                labelCounts.put(label, count+1);
            } else {
                labelCounts.put(label, 1);
            }
        }
    }

    /**
     * Build the node paths: the set of paths that run through each node.
     */
    void buildNodePaths() {
	if (verbose) System.out.print("Building node paths...");
        nodePaths = new HashMap<>();
        // initialize empty paths for each node
        for (Node n : getNodes()) {
            nodePaths.put(n, new ArrayList<Path>());
        }
        // now load the paths in parallel
        ConcurrentSkipListSet<Path> concurrentPaths = new ConcurrentSkipListSet<Path>(paths);
        concurrentPaths.parallelStream().forEach(path -> {
                ConcurrentSkipListSet<Node> concurrentNodes = new ConcurrentSkipListSet<Node>((List<Node>) path.getNodes());
                concurrentNodes.parallelStream().forEach(n -> {
                        nodePaths.get(n).add(path);
                    });
            });
	if (verbose) System.out.println("done.");
    }

    /**
     * Remove orphan nodes, nodes which have no edges.
     */
    public void removeOrphanNodes() {
        if (verbose) System.out.print("Removing orphan nodes...");
        List<Node> removeableNodes = new ArrayList<>();
        for (Node node : getNodes()) {
            if (getPathCount(node)==0) {
                removeableNodes.add(node);
            }
        }
        for (Node node : removeableNodes) {
            removeVertex(node);
        }
        if (verbose) System.out.println(removeableNodes.size()+" removed.");
    }

    /**
     * Get the count of "case"-labeled paths, if any.
     */
    public int getCasePathCount() {
        if (labelCounts.containsKey("case")) {
            return labelCounts.get("case");
        } else {
            return 0;
        }
    }

    /**
     * Get the count of "ctrl"-labeled paths, if any.
     */
    public int getCtrlPathCount() {
        if (labelCounts.containsKey("ctrl")) {
            return labelCounts.get("ctrl");
        } else {
            return 0;
        }
    }

    /**
     * Return the node with the given id, else null.
     */
    public Node getNode(long id) {
        return nodeIdMap.get(id);
    }

    /**
     * Return true if this graph contains the path with the given name.
     */
    public boolean hasPath(String name) {
        return pathNameMap.containsKey(name);
    }
    
    /**
     * Return the path with the given name.
     */
    public Path getPath(String name) {
        return pathNameMap.get(name);
    }

    /**
     * Return the number of this graph's paths.
     */
    public int getPathCount() {
        return paths.size();
    }

    /**
     * Return the number of paths that traverse the given node.
     */
    public int getPathCount(Node n) {
        return getPaths(n).size();
    }

    /**
     * Return the paths that traverse the given node.
     */
    public List<Path> getPaths(Node n) {
        return nodePaths.get(n);
    }

    /**
     * Just a synonym for vertexSet(), but puts the Nodes in a List.
     */
    public List<Node> getNodes() {
        return new ArrayList<Node>(vertexSet());
    }

    /**
     * Construct a NodeSet from a string representation, e.g. [1350,1352,1353,1465,1467,1468,1469].
     */
    public NodeSet getNodeSet(String str) {
        NodeSet nodes = new NodeSet();
        List<Node> allNodes = getNodes();
        Map<Long,Node> allNodeMap = new HashMap<>();
        for (Node n : allNodes) allNodeMap.put(n.id, n);
        List<String> nodeStrings = Arrays.asList(str.replace("[","").replace("]","").split(","));
        for (String s : nodeStrings) {
            if (s.length()>0) {
                long id = Long.parseLong(s);
                if (allNodeMap.containsKey(id)) {
                    nodes.add(allNodeMap.get(id));
                } else {
                    // bail, we're asked for a node that is not in the graph
                    System.err.println("ERROR: graph does not contain node "+id);
                    System.exit(1);
                }
            }
        }
        return nodes;
    }

    /**
     * Get this graph's label counts map.
     */
    public Map<String,Integer> getLabelCounts() {
        return labelCounts;
    }

    /**
     * Get the label counts map for paths that traverse the given node.
     */
    public Map<String,Integer> getLabelCounts(Node n) {
        Map<String,Integer> map = new HashMap<>();
        for (Path p : nodePaths.get(n)) {
            if (map.containsKey(p.label)) {
                // increment count
                int count = map.get(p.label) + 1;
                map.put(p.label, count);
            } else {
                // initialize count
                map.put(p.label, 1);
            }
        }
        return map;
    }

    /**
     * Get the label counts for paths that follow the given Edge.
     * TODO: restrict path loop to those that are on Nodes connected by Edge. May not be worth it.
     */
    public Map<String,Integer> getLabelCounts(Edge e) {
        Map<String,Integer> map = new HashMap<>();
        for (Path p : paths) {
            List<Edge> edges = p.getEdges();
            if (edges.contains(e)) {
                if (map.containsKey(p.label)) {
                    // increment count
                    int count = map.get(p.label) + 1;
                    map.put(p.label, count);
                } else {
                    // initialize count
                    map.put(p.label, 1);
                }
            }
        }
        return map;
    }

    /**
     * Get the total count of paths that follow the given Edge.
     */
    public int getPathCount(Edge e) {
        int count = 0;
        for (Path p : paths) {
            List<Edge> edges = p.getEdges();
            if (edges.contains(e)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Return the path names as a string array.
     */
    public String[] getPathNames() {
        String[] names = new String[paths.size()];
        for (int i=0; i<names.length; i++) {
            Path p = paths.get(i);
            names[i] = p.name;
        }
        return names;
    }

    /**
     * Return the odds ratio for case paths versus control paths that traverse the given node.
     * 0                 = zero case paths on node or all control paths on node
     * POSITIVE_INFINITY = zero ctrl paths on node or all case paths on node (including all paths total on node)
     */
    public double oddsRatio(Node n) {
        Map<String,Integer> map = getLabelCounts(n);
        int nodeCasePaths = 0; if (map.containsKey("case")) nodeCasePaths = map.get("case");
        int nodeCtrlPaths = 0; if (map.containsKey("ctrl")) nodeCtrlPaths = map.get("ctrl");
        int otherCasePaths = labelCounts.get("case") - nodeCasePaths;
        int otherCtrlPaths = labelCounts.get("ctrl") - nodeCtrlPaths;
        if (nodeCtrlPaths>0 && otherCasePaths>0) {
            return (double)nodeCasePaths * (double)otherCtrlPaths / ( (double)nodeCtrlPaths * (double)otherCasePaths );
        } else {
            return Double.POSITIVE_INFINITY;
        }
    }

    /**
     * Return the Fisher's exact test p value for case paths vs control paths that traverse the given node.
     *
     *      | node paths    | other paths    |
     *      |--------------------------------|
     * case | nodeCasePaths | otherCasePaths |
     * ctrl | nodeCtrlPaths | otherCtrlPaths |
     */
    public double fisherExactP(Node n) {
        Map<String,Integer> map = getLabelCounts(n);
        int nodeCasePaths = 0; if (map.containsKey("case")) nodeCasePaths = map.get("case");
        int nodeCtrlPaths = 0; if (map.containsKey("ctrl")) nodeCtrlPaths = map.get("ctrl");
        int otherCasePaths = labelCounts.get("case") - nodeCasePaths;
        int otherCtrlPaths = labelCounts.get("ctrl") - nodeCtrlPaths;
        int maxSize = nodeCasePaths + nodeCtrlPaths + otherCasePaths + otherCtrlPaths;
        FisherExact fisherExact = new FisherExact(maxSize);
        return fisherExact.getTwoTailedP(nodeCasePaths, otherCasePaths, nodeCtrlPaths, otherCtrlPaths);
    }

    /**
     * Print out the nodes
     * public long id;
     * public String rs;
     * public String contig;
     * public int start;
     * public int end;
     * public String genotype;
     */
    public void printNodes(PrintStream out) {
        if (out==System.out) printHeading("NODES");
        for (Node n : getNodes()) {
            out.println(n.id+"\t"+n.rs+"\t"+n.contig+"\t"+n.start+"\t"+n.end+"\t"+n.genotype);
        }
    }

    /**
     * Print the paths, labeled by Path.name.
     */
    public void printPaths(PrintStream out) {
        if (out==System.out) printHeading("PATHS");
        for (Path path : paths) {
            StringBuilder builder = new StringBuilder();
            builder.append(path.name);
            builder.append("\t"+path.label);
            for (Node node : path.getNodes()) {
                builder.append("\t"+node.id);
            }
            out.println(builder.toString());
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
     * Run all the PangenomicGraph printing methods to files.
     */
    public void printAll() throws FileNotFoundException, IOException {
        if (name==null) return;
        // if (labelCounts!=null && labelCounts.size()>0) {
        //     PrintStream labelCountsOut = new PrintStream(name+".labelcounts.txt");
        //     printLabelCounts(labelCountsOut);
        // }

        if (verbose) System.out.print("Writing nodes file...");
        PrintStream nodesOut = new PrintStream(name+".nodes.txt");
        printNodes(nodesOut);
        if (verbose) System.out.println("done.");

        // if (verbose) System.out.print("Writing nodes histogram...");
        // PrintStream nodeHistogramOut = new PrintStream(name+".nodehistogram.txt");
        // printNodeHistogram(nodeHistogramOut);
        // if (verbose) System.out.println("done.");

        if (verbose) System.out.print("Writing paths file...");
        PrintStream pathsOut = new PrintStream(name+".paths.txt");
        printPaths(pathsOut);
        if (verbose) System.out.println("done.");

        // if (!skipNodePaths) {
        //     if (verbose) System.out.print("Writing node paths file...");
        //     PrintStream nodePathsOut = new PrintStream(name+".nodepaths.txt");
        //     printNodePaths(nodePathsOut);
        //     if (verbose) System.out.println("done.");
        //     if (verbose) System.out.print("Writing path PCA file...");
        //     PrintStream pcaDataOut = new PrintStream(name+".pathpca.txt");
        //     printPcaData(pcaDataOut);
        //     if (verbose) System.out.println("done.");
        // }
        
        // if (!skipSequences) {
        //     if (verbose) System.out.print("Writing path sequences file...");
        //     PrintStream pathSequencesOut = new PrintStream(name+".pathsequences.fasta");
        //     printPathSequences(pathSequencesOut);
        //     if (verbose) System.out.println("done.");
        // }
    }

    /**
     * Load the graph from a VCF file.
     * NOTE: vcfFile must be instantiated!
     */
    public void loadVCF() throws FileNotFoundException, IOException {
        if (vcfFile==null) {
            System.err.println("ERROR: graph.vcfFile has not been set!");
            System.exit(1);
        }
        VCFImporter vcfImporter = new VCFImporter();
        vcfImporter.read(vcfFile);
        buildGraph(sampleLabels, vcfImporter.nodes, vcfImporter.sampleNodesMap, vcfImporter.nodeSamplesMap);
    }

    /**
     * Load the graph from a pair of TXT files.
     * nodesFile and pathsFile must already be set.
     */
    public void loadTXT() throws FileNotFoundException, IOException {
        if (nodesFile==null || pathsFile==null) {
            System.err.println("ERROR: graph.nodesFile and/or graph.pathsFile has not been set!");
            System.exit(1);
        }
        TXTImporter txtImporter = new TXTImporter();
        txtImporter.read(nodesFile, pathsFile);
        buildGraph(txtImporter.sampleLabels, txtImporter.nodes, txtImporter.sampleNodesMap, txtImporter.nodeSamplesMap);
    }


    /**
     * The main event.
     */
    public static void main(String[] args) throws IOException, FileNotFoundException {
	Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        // REQUIRED parameters
        Option graphOption = new Option("g", "graph", true, "name of graph");
        graphOption.setRequired(true);
        options.addOption(graphOption);
        //
        Option labelsOption = new Option("l", "labelfile", true, "tab-delimited file containing one sample<tab>label per line");
        labelsOption.setRequired(true);
        options.addOption(labelsOption);

        // OPTIONAL parameters
        Option vcfFileOption = new Option("vcf", "vcffile", false, "load graph from <graph>.vcf.gz");
        vcfFileOption.setRequired(false);
        options.addOption(vcfFileOption);
        //
        Option txtFileOption = new Option("txt", "txtfile", false, "load graph from <graph>.nodes.txt and <graph>.paths.txt");
        txtFileOption.setRequired(false);
        options.addOption(txtFileOption);
        //
        Option verboseOption = new Option("v", "verbose", false, "verbose output (false)");
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
        // spit out help and exit if nothing supplied
        if (cmd.getOptions().length==0) {
            formatter.printHelp("PangenomicGraph", options);
            System.exit(1);
            return;
        }
        // validation
        boolean loadVCF = cmd.hasOption("vcffile");
        boolean loadTXT = cmd.hasOption("txtfile");
        if (!loadVCF && !loadTXT) {
            System.err.println("ERROR: You must specify loading from either a VCF or pair of TXT files using --vcffile or --txtfile.");
            System.exit(1);
        }

        // our PangenomicGraph
        PangenomicGraph graph = new PangenomicGraph();
        if (cmd.hasOption("verbose")) graph.verbose = true;

        // populate graph instance vars from parameters
        graph.name = cmd.getOptionValue("graph");
        if (loadVCF) {
            graph.vcfFile = new File(graph.name+".vcf.gz");
            graph.labelsFile = new File(cmd.getOptionValue("labelfile"));
            graph.readSampleLabels();
            graph.loadVCF();
            graph.tallyLabelCounts();
        }
        if (loadTXT) {
            graph.nodesFile = new File(graph.name+".nodes.txt");
            graph.pathsFile = new File(graph.name+".paths.txt");
            graph.loadTXT();
            graph.tallyLabelCounts();
        }

        // build the node-paths map (this can be optional)
        graph.buildNodePaths();

        // output
        graph.printAll();
    }
}
