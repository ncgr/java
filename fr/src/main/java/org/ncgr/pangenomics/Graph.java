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
import java.util.HashMap;
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
    public static int BOTH_GENOTYPES = -1;
    public static boolean VERBOSE = false;

    // output verbosity
    boolean verbose = VERBOSE;

    // genotype preference (default: load all genotypes)
    public int genotype = BOTH_GENOTYPES;

    // input files
    public String dotFile;
    public String fastaFile;
    public String jsonFile;
    public String gfaFile;
    public String labelsFile;

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
        if (verbose) System.out.print("Reading JSON file:"+jsonFile);
	Reader reader = new InputStreamReader(new FileInputStream(jsonFile));
	Vg.Graph.Builder graphBuilder = Vg.Graph.newBuilder();
	JsonFormat.parser().merge(reader, graphBuilder);
        Vg.Graph graph = graphBuilder.build();
        // a graph is nodes, edges and individual paths through it
        List<Vg.Node> vgNodes = graph.getNodeList();
        List<Vg.Path> vgPaths = graph.getPathList();
        List<Vg.Edge> vgEdges = graph.getEdgeList();
        // populate nodes with their id and sequence, find the minimum sequence length
        for (Vg.Node vgNode : vgNodes) {
            long id = vgNode.getId();
            String sequence = vgNode.getSequence();
	    nodes.put(id, new Node(id,sequence));
        }
        // build paths from the multiple path fragments in the JSON file
        // path fragments have names of the form _thread_sample_chr_genotype_index where genotype=0,1
        // The "_"-split pieces will therefore be:
        // 0:"", 1:"thread", 2:sample1, 3:sample2, L-4:sampleN, L-3:chr, L-2:genotype, L-1:idx where L is the number of pieces,
        // and sample contains N parts separated by "_".
        // NOTE: with unphased calls, genotype 0 or 1 can have the ALT haplotype, so set genotype=BOTH_GENOTYPES to keep both
        Map<String,LinkedList<Node>> nodeListMap = new HashMap<>();
        for (Vg.Path vgPath : vgPaths) {
            String name = vgPath.getName();
            String[] pieces = name.split("_");
            if (pieces.length>=6 && pieces[1].equals("thread")) {
                String pathName = pieces[2];
                for (int i=3; i<pieces.length-3; i++) pathName += "_"+pieces[i]; // for (common) cases where samples have underscores
                // genotype -- append to path name
                int gtype = Integer.parseInt(pieces[pieces.length-2]); // 0, 1, etc.
                pathName += ":"+gtype; // store this way for now
                // append this path fragment if appropriate
                if (genotype==BOTH_GENOTYPES || gtype==genotype) {
                    LinkedList<Node> newNodeList = new LinkedList<>();
                    List<Vg.Mapping> vgMappingList = vgPath.getMappingList();
                    for (Vg.Mapping vgMapping : vgMappingList) {
                        long nodeId = vgMapping.getPosition().getNodeId();
                        newNodeList.add(nodes.get(nodeId));
                    }
                    if (nodeListMap.containsKey(pathName)) {
                        LinkedList<Node> nodeList = nodeListMap.get(pathName);
                        nodeList.addAll(newNodeList);
                        nodeListMap.put(pathName, nodeList);
                    } else {
                        nodeListMap.put(pathName, newNodeList);
                    }
                }
            }
        }
        reader.close();

        // build the paths from the nodeListMap
        for (String pathName : nodeListMap.keySet()) {
            LinkedList<Node> nodeList = nodeListMap.get(pathName);
            String[] parts = pathName.split(":"); // separate out the genotype
            String name = parts[0];
            int genotype = Integer.parseInt(parts[1]);
            paths.add(new Path(name, genotype, nodeList));
        }

        // build the path sequences from their nodes (already populated above)
        buildPathSequences();

        // find the node paths
        buildNodePaths();
    }

    /**
     * Read in from a GFA file as output from vg view --gfa.
     *
     * http://gfa-spec.github.io/GFA-spec/GFA1.html
     *
     * Paths are assumed to be on a single P line per genotype:
     * P	_thread_714413_4_0_0	1+,3+,4+,17+,18+,20+,21+,...,83+,85+,86+	42M,1M,153M,...,1M,85M,1M,161M
     * P	_thread_714413_4_1_0	1+,3+,4+,6+,18+,20+,21+,....,83+,85+,86+	42M,1M,153M,...,1M,85M,1M,161M
     *
     * NOTE: with unphased calls, genotype 0 is the ALT genotype (presuming vg index --force-phasing was used).
     * 
     * @param gfaFile the full path name of the GFA file to parse
     */
    public void readVgGfaFile(String gfaFile) throws IOException {
        this.gfaFile = gfaFile;
        if (verbose) System.out.println("Reading GFA file: "+gfaFile);
        // store the node lists by path name so we can update them
        Map<String,LinkedList<Node>> nodeListMap = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(gfaFile));
        String line = null;
        while ((line=reader.readLine())!=null) {
            String[] parts = line.split("\t");
            String recordType = parts[0];
            if (recordType.equals("H")) {
                // Header line
                String header = parts[1];
                if (verbose) System.out.println(header);
            } else if (recordType.equals("S")) {
                // Segment line -- gives node id and sequence
                long nodeId = Long.parseLong(parts[1]);
                String sequence = parts[2];
                Node node = new Node(nodeId, sequence);
                nodes.put(nodeId, node);
            } else if (recordType.equals("P")) {
                // Path line
                // Paths have names of the form _thread_sample_chr_genotype_index where genotype=0,1
                // and sample often has "_" in it. The "_"-split pieces will therefore be:
                // 0:"", 1:"thread", 2:sample1, 3:sample2, L-4:sampleN, L-3:chr, L-2:genotype, L-1:idx where L is the number of pieces,
                // and sample contains N parts separated by "_".
                String name = parts[1];
                String[] pieces = name.split("_");
                if (pieces.length>=6 && pieces[1].equals("thread")) {
                    // build pathName, assuming it may have underscores
                    String pathName = pieces[2];
                    for (int i=3; i<pieces.length-3; i++) pathName += "_"+pieces[i]; // for (common) cases where samples have underscores
                    // genotype
                    int gtype = Integer.parseInt(pieces[pieces.length-2]); // 0, 1
                    // path segment index
                    int idx = Integer.parseInt(pieces[pieces.length-1]);
                    // append the genotype to pathName
                    pathName += ":"+gtype;
                    // build/append this path's node list
                    if (genotype==BOTH_GENOTYPES || gtype==genotype) {
                        LinkedList<Node> newNodeList = new LinkedList<>();
                        String[] nodeStrings = parts[2].split(","); // e.g. 27+,29+,30+
                        for (String nodeString : nodeStrings) {
                            // NOTE: we're assuming no reverse-complement entries, which would have minus sign!
                            long nodeId = Long.parseLong(nodeString.replace("+",""));
                            newNodeList.add(nodes.get(nodeId)); // nodes should already be loaded from S lines above P lines
                        }
                        if (nodeListMap.containsKey(pathName)) {
                            LinkedList<Node> nodeList = nodeListMap.get(pathName);
                            nodeList.addAll(newNodeList);
                            nodeListMap.put(pathName, nodeList);
                        } else {
                            nodeListMap.put(pathName, newNodeList);
                        }
                    }
                }
            }
        }
        reader.close();

        // build the paths from the nodeListMap
        for (String pathName : nodeListMap.keySet()) {
            LinkedList<Node> nodeList = nodeListMap.get(pathName);
            String[] parts = pathName.split(":"); // separate out the genotype
            String name = parts[0];
            int genotype = Integer.parseInt(parts[1]);
            paths.add(new Path(name, genotype, nodeList));
        }

        // build the path sequences from their nodes (populated above)
        buildPathSequences();

        // build the node paths
        buildNodePaths();
    }        
    
    /**
     * Read in from a splitMEM-style DOT file and a FASTA file using guru.nidi.graphviz and BioJava classes.
     */
    public void readSplitMEMDotFile(String dotFile, String fastaFile) throws IOException {
        this.dotFile = dotFile;
        this.fastaFile = fastaFile;

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
        for (Path path : paths) {
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
                        if (sampleName.equals(path.name) && sampleGenotype==path.genotype) {
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
    public void printNodes(PrintStream out) {
        if (out==System.out) printHeading("NODES");
        for (Node node : nodes.values()) {
            out.println(node.id+"\t"+node.sequence);
        }
    }

    /**
     * Print out a histogram of node sizes.
     */
    public void printNodeHistogram(PrintStream out) {
        if (out==System.out) printHeading("k HISTOGRAM");
        Map<Integer,Integer> countMap = new TreeMap<>();
        for (Node node : nodes.values()) {
            int length = node.sequence.length();
            if (countMap.containsKey(length)) {
                countMap.put(length, ((int)countMap.get(length))+1);
            } else {
                countMap.put(length, 1);
            }
        }
        for (int len : countMap.keySet()) {
            int counts = countMap.get(len);
            out.print("length="+len+"\t("+counts+")\t");
            for (int i=1; i<=counts; i++) out.print("X");
            out.println("");
        }
    }

    /**
     * Print the paths, labeled by pathName.
     */
    public void printPaths(PrintStream out) {
        if (out==System.out) printHeading("PATHS");
        for (Path path : paths) {
            out.print(path.getNameGenotype()+"\t"+path.label+"\t"+path.sequence.length());
            for (Node node : path.nodes) {
                out.print("\t"+node.id);
            }
            out.println("");
        }
    }

    /**
     * Print out the node paths along with counts.
     */
    public void printNodePaths(PrintStream out) {
        if (out==System.out) printHeading("NODE PATHS");
        for (Long nodeId : nodePaths.keySet()) {
            Set<Path> nPaths = nodePaths.get(nodeId);
            out.print(String.valueOf(nodeId));
            for (Path path : nPaths) {
                out.print("\t"+path.getNameGenotype());
            }
            out.println("");
        }
    }

    /**
     * Print the sequences for each path, in FASTA format, labeled by path.name.genotype.
     */
    public void printPathSequences(PrintStream out) {
        if (out==System.out) printHeading("PATH SEQUENCES");
        for (Path path : paths) {
            String heading = ">"+path.getNameGenotype()+" ("+path.sequence.length()+")";
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
            for (int i=1; i<=path.sequence.length(); i++) {
                out.print(path.sequence.charAt((i-1)));
                if (i%100==0) out.print("\n");
            }
            out.println("");
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
        // header is paths
        boolean first = true;
        for (Path path : paths) {
            if (first) {
                out.print(path.getNameGenotype());
                first = false;
            } else {
                out.print("\t"+path.getNameGenotype());
            }
            if (path.label!=null) out.print("."+path.label);
        }
        out.println("");

        // rows are nodes
        for (long nodeId : nodes.keySet()) {
            Node node = nodes.get(nodeId);
            Set<Path> nPaths = nodePaths.get(nodeId);
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

    /**
     * Run all the Graph printing methods to files.
     */
    public void printAll(String outputPrefix) throws FileNotFoundException, IOException {
        if (outputPrefix==null) return;
        PrintStream nodesOut = new PrintStream(outputPrefix+".nodes.txt");
        PrintStream nodeHistogramOut = new PrintStream(outputPrefix+".nodehistogram.txt");
        PrintStream pathsOut = new PrintStream(outputPrefix+".paths.txt");
        PrintStream nodePathsOut = new PrintStream(outputPrefix+".nodepaths.txt");
        PrintStream pathSequencesOut = new PrintStream(outputPrefix+".pathsequences.fasta");
        PrintStream pcaOut = new PrintStream(outputPrefix+".pathpca.txt");
        printNodes(nodesOut);
        printNodeHistogram(nodeHistogramOut);
        printPaths(pathsOut);
        printNodePaths(nodePathsOut);
        printPathSequences(pathSequencesOut);
        printPcaData(pcaOut);
        if (labelCounts!=null && labelCounts.size()>0) {
            PrintStream labelCountsOut = new PrintStream(outputPrefix+".labelcounts.txt");
            printLabelCounts(labelCountsOut);
        }
    }

    /**
     * Run all the Graph printing methods to stdout.
     */
    public void printAll() throws FileNotFoundException, IOException {
        printNodes(System.out);
        printNodeHistogram(System.out);
        printPaths(System.out);
        printNodePaths(System.out);
        printPathSequences(System.out);
        printPcaData(System.out);
        if (labelCounts!=null && labelCounts.size()>0) {
            printLabelCounts(System.out);
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
        Option genotypeOption = new Option("g", "genotype", true, "which genotype to include (0,1) from the JSON/GFA file; "+BOTH_GENOTYPES+" to include both ("+BOTH_GENOTYPES+")");
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
            formatter.printHelp("Graph", options);
            System.exit(1);
            return;
        }

        // none required, so spit out help if nothing supplied
        if (cmd.getOptions().length==0) {
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

        // if a labels file is given, add them to the paths
        if (pathLabelsFile!=null) {
            g.readPathLabels(pathLabelsFile);
        }

        // output
        if (cmd.hasOption("outputprefix")) {
            // files
            g.printAll(cmd.getOptionValue("outputprefix"));
        } else {
            // stdout
            g.printAll();
        }
    }
}
