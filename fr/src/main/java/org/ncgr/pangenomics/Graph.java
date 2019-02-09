package org.ncgr.pangenomics;

import vg.Vg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;

import java.util.Collection;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.StringJoiner;

import com.google.protobuf.util.JsonFormat;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Link;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;
import guru.nidi.graphviz.parse.Parser;

import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Storage of a pan-genomic graph.
 *
 * @author Sam Hokin
 */ 
public class Graph {

    // defaults
    public static int GENOTYPE = -1;
    public static boolean VERBOSE = false;
    public static boolean DEBUG = false;

    // output verbosity
    boolean verbose = VERBOSE;
    boolean debug = DEBUG;

    // genotype preference (-1 to append all genotypes)
    public int genotype = GENOTYPE;

    // input files
    public String dotFile;
    public String fastaFile;
    public String jsonFile;
    public String gfaFile;
    
    // minimum sequence length found in the graph
    public long minLen;

    // the nodes contained in this graph, in the form of an id->Node map
    // (Redundant, since Node also contains id, but this allows quick retrieval on id)
    public TreeMap<Long,Node> nodes;

    // each Path provides the ordered list of nodes that it traverses, along with its full sequence
    public TreeSet<Path> paths; // (ordered simply for convenience)
    
    // maps a Node to a set of Paths that traverse it
    public TreeMap<Long,Set<Path>> nodePaths; // keyed and ordered by Node Id (for convenience)

    // maps a path label to a count of paths that have that label
    public Map<String,Integer> labelCounts; // keyed by label

    /**
     * Constructor instantiates collections; then use read methods to populate the graph from files.
     */
    public Graph() {
        nodes = new TreeMap<>();
        paths = new TreeSet<>();
        nodePaths = new TreeMap<>();
    }

    /**
     * Read a Graph in from a Vg-generated JSON file.
     */
    public void readVgJsonFile(String jsonFile) throws FileNotFoundException, IOException {
        // set instance objects from parameters
        this.jsonFile = jsonFile;

        // read the vg-created JSON into a Vg.Graph
        if (verbose || debug) System.out.print("Reading "+jsonFile+"...");
	Reader reader = new InputStreamReader(new FileInputStream(jsonFile));
	Vg.Graph.Builder graphBuilder = Vg.Graph.newBuilder();
	JsonFormat.parser().merge(reader, graphBuilder);
        Vg.Graph graph = graphBuilder.build();
	if (verbose || debug) System.out.println("done.");

        // a graph is nodes, edges and individual paths through it
        List<Vg.Node> vgNodes = graph.getNodeList();
        List<Vg.Path> vgPaths = graph.getPathList();
        List<Vg.Edge> vgEdges = graph.getEdgeList();

        // populate nodes with their id and sequence, find the minimum sequence length
	if (verbose || debug) System.out.print("Populating nodes...");
        minLen = Long.MAX_VALUE;
        for (Vg.Node vgNode : vgNodes) {
            long id = vgNode.getId();
            String sequence = vgNode.getSequence();
            if (sequence.length()<minLen) minLen = sequence.length();
	    nodes.put(id, new Node(id,sequence));
        }
	if (verbose || debug) System.out.println(" done.");
        
        // build paths from the multiple path fragments in the JSON file
        // path fragments have names of the form _thread_sample_chr_genotype_index where genotype=0,1
        // The "_"-split pieces will therefore be:
        // 0:"", 1:"thread", 2:sample1, 3:sample2, L-4:sampleN, L-3:chr, L-2:genotype, L-1:idx where L is the number of pieces,
        // and sample contains N parts separated by "_".
        // NOTE: with unphased calls, genotype 0 is nearly reference; so typically set genotype=1 to avoid REF dilution
        for (Vg.Path vgPath : vgPaths) {
            String name = vgPath.getName();
            String[] pieces = name.split("_");
            if (pieces.length>=6 && pieces[1].equals("thread")) {
                // we've got a sample path fragment
                String pathName = pieces[2];
                for (int i=3; i<pieces.length-3; i++) pathName += "_"+pieces[i]; // for (common) cases where samples have underscores
                int gtype = Integer.parseInt(pieces[pieces.length-2]); // 0, 1, etc.
                // append the genotype if we're including them all
                if (genotype==-1) pathName += ":"+gtype;
                // append this path fragment if appropriate
                if (genotype==-1 || gtype==genotype) {
                    List<Vg.Mapping> vgMappingList = vgPath.getMappingList();
                    // retrieve or initialize this path's nodes
                    LinkedList<Long> nodeIds = new LinkedList<>();
                    for (Path path : paths) {
                        if (path.name.equals(pathName)) {
                            nodeIds = path.getNodeIds();
                        }
                    }
                    // run through this particular mapping and append each node id to the node list
                    boolean first = true;
                    for (Vg.Mapping vgMapping : vgMappingList) {
                        if (first && nodeIds.size()>0) {
                            // skip node overlap between fragments
                        } else {
                            nodeIds.add(vgMapping.getPosition().getNodeId());
                        }
                        first = false;
                    }
                    // build the new set of nodes for this path
                    LinkedList<Node> pathNodes = new LinkedList<>();
                    for (Long id : nodeIds) {
			Node n = nodes.get(id);
			if (n==null) {
			    System.err.println("NULL node retrieved for id="+id);
			    System.exit(1);
			} else {
			    pathNodes.add(n);
			}
                    }
                    // update existing path with the new nodes
                    boolean updated = false;
                    for (Path path : paths) {
                        if (path.name.equals(pathName)) {
                            path.nodes = pathNodes;
                            updated = true;
                        }
                    }
                    if (!updated) {
                        // create this new path
			if (pathName!=null && pathNodes.size()>0) {
			    try {
				paths.add(new Path(pathName, pathNodes));
			    } catch (Exception e) {
				System.err.println("paths="+paths);
				System.err.println("pathName="+pathName+" pathNodes="+pathNodes);
				e.printStackTrace();
				System.exit(1);
			    }
			}
                    }
                }
            }
        }

        // build the path sequences from their nodes (already populated above)
        buildPathSequences();

        // find the node paths
        buildNodePaths();
    }

    /**
     * Read in from a GFA file as output from vg view --gfa.
     */
    public void readVgGfaFile(String gfaFile) throws IOException {
        this.gfaFile = gfaFile;
        if (verbose) System.out.println("Reading GFA file: "+gfaFile);
        BufferedReader reader = new BufferedReader(new FileReader(gfaFile));
        String line = null;
        while ((line=reader.readLine())!=null) {
            String[] parts = line.split("\t");
            String recordType = parts[0];
            if (recordType.equals("H")) {
                // header gives version
                String version = parts[1];
                if (verbose) System.out.println(version);
            } else if (recordType.equals("S")) {
                // node sequence
                long nodeId = Long.parseLong(parts[1]);
                String sequence = parts[2];
                Node node = new Node(nodeId, sequence);
                nodes.put(nodeId, node);
            } else if (recordType.equals("P")) {
                // build paths from the multiple path fragments in the GFA
                // path fragments have names of the form _thread_sample_chr_genotype_index where genotype=0,1
                // The "_"-split pieces will therefore be:
                // 0:"", 1:"thread", 2:sample1, 3:sample2, L-4:sampleN, L-3:chr, L-2:genotype, L-1:idx where L is the number of pieces,
                // and sample contains N parts separated by "_".
                // NOTE: with unphased calls, genotype 0 is nearly reference; so typically set genotype=1 to avoid REF dilution
                String name = parts[1];
                String[] pieces = name.split("_");
                if (pieces.length>=6 && pieces[1].equals("thread")) {
                    // we've got a sample path fragment
                    String pathName = pieces[2];
                    for (int i=3; i<pieces.length-3; i++) pathName += "_"+pieces[i]; // for (common) cases where samples have underscores
                    int gtype = Integer.parseInt(pieces[pieces.length-2]); // 0, 1, etc.
                    // append the genotype if we're including them all
                    if (genotype==-1) pathName += ":"+gtype;
                    // append this path fragment if appropriate
                    if (genotype==-1 || gtype==genotype) {
                        List<Long> mappingList = new LinkedList<>();
                        String[] mappingNodes = parts[2].split(","); // e.g. 27+,29+,30+
                        for (String mappingNode : mappingNodes) {
                            long nodeId = Long.parseLong(mappingNode.replace("+",""));
                            mappingList.add(nodeId);
                        }
                        LinkedList<Long> nodeIds = new LinkedList<>();
                        for (Path path : paths) {
                            if (path.name.equals(pathName)) {
                                nodeIds = path.getNodeIds();
                            }
                        }
                        // run through this particular mapping and append each node id to the path's node list
                        boolean first = true;
                        for (long nodeId : mappingList) {
                            if (first && nodeIds.size()>0) {
                                // skip node overlap between fragments
                            } else {
                                nodeIds.add(nodeId);
                            }
                            first = false;
                        }
                        // build the new set of Nodes for this path
                        LinkedList<Node> pathNodes = new LinkedList<>();
                        for (long id : nodeIds) {
                            Node n = nodes.get(id);
                            if (n==null) {
                                System.err.println("NULL node retrieved for id="+id);
                                System.exit(1);
                            } else {
                                pathNodes.add(n);
                            }
                        }
                        // update existing path with the new nodes
                        boolean updated = false;
                        for (Path path : paths) {
                            if (path.name.equals(pathName)) {
                                path.nodes = pathNodes;
                                updated = true;
                            }
                        }
                        if (!updated) {
                            // create this new path
                            if (pathName!=null && pathNodes.size()>0) {
                                try {
                                    paths.add(new Path(pathName, pathNodes));
                                } catch (Exception e) {
                                    System.err.println("paths="+paths);
                                    System.err.println("pathName="+pathName+" pathNodes="+pathNodes);
                                    e.printStackTrace();
                                    System.exit(1);
                                }
                            }
                        }
                    }
                }
            } else if (recordType.equals("L")) {
                // link, not used
            }
        }
        reader.close();
        
        // build the path sequences from their nodes (populated above)
        buildPathSequences();

        // find the node paths
        buildNodePaths();
    }        
    
    /**
     * Read in from a splitMEM-style DOT file and a FASTA file using guru.nidi.graphviz and BioJava classes.
     */
    public void readSplitMEMDotFile(String dotFile, String fastaFile) throws IOException {
        this.dotFile = dotFile;
        this.fastaFile = fastaFile;
        this.minLen = Long.MAX_VALUE;

        if (verbose) System.out.println("Reading FASTA file: "+fastaFile);
        Map<String,DNASequence> fastaMap = FastaReaderHelper.readFastaDNASequence​(new File(fastaFile));

        String fasta = "";
        for (String seqName : fastaMap.keySet()) {
            DNASequence dnaSequence = fastaMap.get(seqName);
            if (verbose) System.out.println(dnaSequence.getOriginalHeader()+":"+dnaSequence.getLength());
            System.out.println(dnaSequence.getSequenceAsString());
            fasta += "$" + dnaSequence.getSequenceAsString(); // splitMEM appends a termination character
        }

        if (verbose) System.out.println("Reading dot file: "+dotFile);
        MutableGraph g = Parser.read(new File(dotFile));
        
        Collection<MutableNode> mNodes = g.nodes();
        for (MutableNode mNode : mNodes) {
            long nodeId = getNodeId(mNode);

            System.out.println("----- NODE "+nodeId+" -----");
            System.out.println(mNode.get("label").toString());

            String[] parts = mNode.get("label").toString().split(":");
            int length = Integer.parseInt(parts[1]);
            if (length<minLen) minLen = length;
            String[] startStrings = parts[0].split(",");
            String sequence = null;
            boolean first = true;
            for (String startString : startStrings) {
                int start = Integer.parseInt(startString);
                String s = fasta.substring(start,start+length);
                if (first) {
                    sequence = s;
                    first = false;
                    System.out.println(sequence);
                } else if (!s.equals(sequence)) {
                    // ERROR out if we've got mismatched sequences for same node
                    System.err.println("ERROR: sequences at node "+nodeId+" are not equal!");
                    System.err.println("First="+sequence);
                    System.err.println(" This="+s);
                    System.exit(1);
                }
            }
            Node node = new Node(nodeId, sequence);
            nodes.put(nodeId, node);

            List<Link> links = mNode.links();
            for (Link link : links) {
                System.out.println("from["+link.from().toString()+"] to["+link.to().toString()+"]");
            }
        }
        
        // DEBUG
        System.exit(0);
    }

    /**
     * Return the long 1-based nodeId associated with the given MutableNode.
     */
    long getNodeId(MutableNode mNode) {
        return Long.parseLong(mNode.name().toString()) + 1;
    }


    /**
     * Return true if this and that Graph come from the same file.
     */
    public boolean equals(Graph that) {
        if (this.jsonFile!=null && that.jsonFile!=null) {
            return this.jsonFile.equals(that.jsonFile);
        } else if (this.dotFile!=null && that.dotFile!=null && this.fastaFile!=null && that.fastaFile!=null) {
            return this.dotFile.equals(that.dotFile) && this.fastaFile.equals(that.fastaFile);
        } else {
            return false;
        }
    }

    /**
     * Build the path sequences - just calls Path.buildSequence() for each path.
     */
    void buildPathSequences() {
        for (Path path : paths) {
            path.buildSequence();
        }
    }

    /**
     * Find node paths: the set of paths that run through each node.
     */
    void buildNodePaths() {
        // init empty paths for each node
        for (Long nodeId : nodes.keySet()) {
            nodePaths.put(nodeId, new TreeSet<Path>());
        }
        // now load the paths
        for (Path path : paths) {
            for (Node node : path.nodes) {
                nodePaths.get(node.id).add(path);
            }
        }
    }

    /**
     * Read path labels from a tab-delimited file. Comment lines start with #.
     */
    public void readPathLabels(String labelsFile) throws FileNotFoundException, IOException {
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
        // find the labels for path names (which may have :genotype suffix to ignore)
        for (Path path : paths) {
            String[] pieces = path.name.split(":");
            String pathName = pieces[0];
            for (String sampleName : labels.keySet()) {
                if (sampleName.equals(pathName)) {
                    String label = labels.get(sampleName); 
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
        // verbosity
        if (verbose) printLabelCounts();

        // check that we've labeled all the paths
        boolean pathsAllLabeled = true;
        for (Path path : paths) {
            if (path.label==null) {
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
     * Set the debug flag.
     */
    public void setDebug() {
	debug = true;
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
     * Print out the nodes along with a k histogram.
     */
    public void printNodes() {
        Map<Integer,Integer> countMap = new TreeMap<>();
        printHeading("NODES");
        for (Node node : nodes.values()) {
            int length = node.sequence.length();
            if (countMap.containsKey(length)) {
                countMap.put(length, ((int)countMap.get(length))+1);
            } else {
                countMap.put(length, 1);
            }
            System.out.println(node.id+"("+length+"):"+node.sequence);
        }
        printHeading("k HISTOGRAM");
        for (int len : countMap.keySet()) {
            int counts = countMap.get(len);
            System.out.print("length="+len+"\t("+counts+")\t");
            for (int i=1; i<=counts; i++) System.out.print("X");
            System.out.println("");
        }
    }

    /**
     * Print the paths, labeled by pathName.
     */
    public void printPaths() {
        printHeading("PATHS");
        for (Path path : paths) {
            System.out.print(path.getNameAndLabel()+"("+path.sequence.length()+"):");
            for (Node node : path.nodes) {
                System.out.print(" "+node.id);
            }
            System.out.println("");
        }
    }

    /**
     * Print out the node paths along with counts.
     */
    public void printNodePaths() {
        printHeading("NODE PATHS");
        for (Long nodeId : nodePaths.keySet()) {
            Set<Path> nPaths = nodePaths.get(nodeId);
            String asterisk = " ";
            if (nPaths.size()==paths.size()) asterisk="*"; // flag a node that is supported by ALL paths
            System.out.print(asterisk+nodeId+"("+nPaths.size()+"):");
            for (Path path : nPaths) {
                System.out.print(" "+path.name);
            }
            System.out.println("");
        }
    }

    /**
     * Print the sequences for each path, in FASTA style, labeled by path.name (and path.label if present).
     */
    public void printPathSequences() {
        printHeading("PATH SEQUENCES");
        for (Path path : paths) {
            String heading = ">"+path.getNameAndLabel()+" ("+path.sequence.length()+")";
            System.out.print(heading);
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
            for (int i=heading.length(); i<h; i++) System.out.print(" "); System.out.print(".");
            for (int n=0; n<m; n++) {
                for (int i=0; i<9; i++) System.out.print(" "); System.out.print(".");
            }
            System.out.println("");
            // print out the sequence, 100 chars to a line
            for (int i=1; i<=path.sequence.length(); i++) {
                System.out.print(path.sequence.charAt((i-1)));
                if (i%100==0) System.out.print("\n");
            }
            System.out.println("");
        }
    }

    /**
     * Print the counts of paths per label.
     */
    public void printLabelCounts() {
        printHeading("LABEL COUNTS");
        for (String label : labelCounts.keySet()) {
            System.out.println(label+":"+labelCounts.get(label));
        }
    }

    /**
     * Print node participation by path, appropriate for PCA analysis.
     * Prints to file if jsonFile or dotFile exist, otherwise stdout.
     * Ignore nodes which are traversed by all paths - they dilute the PCA.
     */
    public void printPcaData() throws FileNotFoundException, IOException {
        PrintStream out = null;
        if (jsonFile!=null) {
            out = new PrintStream(jsonFile+".pca.txt");
        } else if (dotFile!=null) {
            out = new PrintStream(dotFile+".pca.txt");
        } else {
            out = System.out;
        }
        // header is paths
        boolean first = true;
        for (Path path : paths) {
            if (first) {
                out.print("P"+path.name);
                first = false;
            } else {
                out.print("\tP"+path.name);
            }
            if (path.label!=null) out.print("."+path.label);
        }
        out.println("");

        // rows are nodes
        for (long nodeId : nodes.keySet()) {
            Node node = nodes.get(nodeId);
            Set<Path> nPaths = nodePaths.get(nodeId);
            if (nPaths.size()<paths.size()) {
                out.print("N"+nodeId);
                for (Path path : paths) {
                    if (path.nodes.contains(node)) {
                        out.print("\t1");
                    } else {
                        out.print("\t0");
                    }
                }
                out.println("");
            }
        }
    }

    /**
     * Command-line utility
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {

        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        Option dotOption = new Option("d", "dot", true, "DOT file (requires FASTA file)");
        dotOption.setRequired(false);
        options.addOption(dotOption);
        //
        Option fastaOption = new Option("f", "fasta", true, "FASTA file (requires DOT file)");
        fastaOption.setRequired(false);
        options.addOption(fastaOption);
        //
        Option gfaOption = new Option("gfa", "gfa", true, "GFA file");
        gfaOption.setRequired(false);
        options.addOption(gfaOption);
        //
        Option genotypeOption = new Option("g", "genotype", true, "which genotype to include (0,1) from the JSON/GFA file; -1 to include all ("+GENOTYPE+")");
        genotypeOption.setRequired(false);
        options.addOption(genotypeOption);
        //
        Option jsonOption = new Option("j", "json", true, "vg JSON file");
        jsonOption.setRequired(false);
        options.addOption(jsonOption);
        //
        Option labelsOption = new Option("p", "pathlabels", true, "tab-delimited file with pathname<tab>label");
        labelsOption.setRequired(false);
        options.addOption(labelsOption);
        //
        Option verboseOption = new Option("v", "verbose", false, "verbose output ("+VERBOSE+")");
        verboseOption.setRequired(false);
        options.addOption(verboseOption);
        //
        Option debugOption = new Option("do", "debug", false, "debug output ("+DEBUG+")");
        debugOption.setRequired(false);
        options.addOption(debugOption);

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp("Graph", options);
            System.exit(1);
            return;
        }

        // parameter validation
        if (!cmd.hasOption("dot") && !cmd.hasOption("json") && !cmd.hasOption("gfa")) {
            System.err.println("You must specify a splitMEM-style DOT file plus FASTA (-d/--dot and -f/--fasta ), a vg JSON file (-j, --json) or a vg GFA file (--gfa)");
            System.exit(1);
            return;
        }
        if (cmd.hasOption("dot") && !cmd.hasOption("fasta")) {
            System.err.println("If you specify a splitMEM-style DOT file (-d/--dot) you MUST ALSO specify a FASTA file (-f/--fasta)");
            System.exit(1);
            return;
        }
        
        // files
        String dotFile = cmd.getOptionValue("dot");
        String fastaFile = cmd.getOptionValue("fasta");
        String jsonFile = cmd.getOptionValue("json");
        String gfaFile = cmd.getOptionValue("gfa");
        String pathLabelsFile = cmd.getOptionValue("pathlabels");

        // create a Graph from the dot+FASTA or JSON or GFA file
        Graph g = new Graph();
        if (cmd.hasOption("verbose")) g.setVerbose();
        if (cmd.hasOption("debug")) g.setDebug();
        if (cmd.hasOption("genotype")) g.genotype = Integer.parseInt(cmd.getOptionValue("genotype"));
        if (dotFile!=null && fastaFile!=null) {
            g.readSplitMEMDotFile(dotFile, fastaFile);
        } else if (jsonFile!=null) {
            g.readVgJsonFile(jsonFile);
        } else if (gfaFile!=null) {
            g.readVgGfaFile(gfaFile);
        } else {
            System.err.println("ERROR: no DOT+FASTA or JSON or GFA provided.");
            System.exit(1);
        }

        // if a labels file is given, append the labels to the path names
        if (pathLabelsFile!=null) {
            g.readPathLabels(pathLabelsFile);
        }

        // output
        g.printNodes();
        g.printPaths();
        g.printNodePaths();
        if (g.debug) g.printPathSequences();

        g.printPcaData();
    }
}
