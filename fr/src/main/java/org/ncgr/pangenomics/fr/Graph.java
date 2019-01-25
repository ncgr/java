package org.ncgr.pangenomics.fr;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.StringJoiner;

import vg.Vg;

import com.google.protobuf.util.JsonFormat;

/**
 * Storage of a graph.
 *
 * Getting rid of 0-based primitive arrays in favor of Collections and readability and compatibility with Vg numbering, etc.
 *
 * @author Sam Hokin
 */ 
public class Graph {

    // output verbosity
    boolean verbose = false;

    // made available so directory names can be formed from it
    String dotFile;
    String fastaFile;
    String jsonFile;
    
    // minimum sequence length
    long minLen = Long.MAX_VALUE;

    // genotype preference (-1 to append all genotypes)
    int genotype = -1;

    // the nodes contained in this graph (which, in turn, contain their sequences)
    TreeSet<Node> nodes;

    // each Path provides the ordered list of nodes that it traverses, along with its full sequence
    TreeSet<Path> paths; // (ordered simply for convenience)
    
    // maps a Node to a set of Paths that traverse it
    TreeMap<Node,Set<Path>> nodePaths; // keyed and ordered by Node (for convenience)

    // maps a path label to a count of paths that have that label
    Map<String,Integer> labelCounts; // keyed by label

    /**
     * Constructor does nothing; use read methods to populate the graph from files.
     */
    public Graph() {
    }

    /**
     * Read a Graph in from a Vg-generated JSON file.
     */
    public void readVgJsonFile(String jsonFile) throws FileNotFoundException, IOException {
        // set instance objects from parameters
        this.jsonFile = jsonFile;

        // instantiate the instance collections
        nodes = new TreeSet<>();
        paths = new TreeSet<>();
        nodePaths = new TreeMap<>();

        // read the vg-created JSON into a Vg.Graph
        if (verbose) System.out.println("Reading "+jsonFile+"...");
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
            if (sequence.length()<minLen) minLen = sequence.length();
            nodes.add(new Node(id, sequence));
        }
        
        // build paths from the multiple path fragments in the JSON file
        // path fragments have names of the form _thread_sample_chr_genotype_index where genotype=0,1,...
        // The "_"-split pieces will therefore be:
        // 0:"", 1:"thread", 2:sample1, 3:sample2, L-4:sampleN, L-3:chr, L-2:genotype, L-1:i2 where L is the number of pieces,
        // and sample contains N parts separated by "_".
        // NOTE: with unphased calls, so genotype 0 is nearly reference; so typically set genotype=1 to avoid REF dilution
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
                        pathNodes.add(getNodeForId(id));
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
                        paths.add(new Path(pathName, pathNodes));
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
     * Return the Node with the given id, null if the id is not in this graph.
     */
    public Node getNodeForId(Long id) {
        for (Node node : nodes) {
            if (node.id==id) return node;
        }
        return null;
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
        for (Node node : nodes) {
            nodePaths.put(node, new TreeSet<Path>());
        }
        // now load the paths
        for (Path path : paths) {
            for (Node node : path.nodes) {
                nodePaths.get(node).add(path);
            }
        }
    }

    /**
     * Read path labels from a tab-delimited file. Comment lines start with #.
     */
    void readPathLabels(String labelsFile) throws FileNotFoundException, IOException {
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
        if (verbose) {
            printHeading("LABEL COUNTS");
            for (String label : labelCounts.keySet()) {
                System.out.println(label+":"+labelCounts.get(label));
            }
        }
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
    
    // getters of non-public vars
    public String getDotFile() {
        return dotFile;
    }
    public String getFastaFile() {
        return fastaFile;
    }
    public String getJsonFile() {
        return jsonFile;
    }

    // setters of non-public vars
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
    void printNodes() {
        Map<Integer,Integer> countMap = new TreeMap<>();
        printHeading("NODES");
        for (Node node : nodes) {
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
    void printPaths() {
        printHeading("PATHS");
        for (Path path : paths) {
            System.out.print(path.getNameAndLabel()+":");
            for (Node node : path.nodes) {
                System.out.print(" "+node.id);
            }
            System.out.println("");
        }
    }

    /**
     * Print out the node paths along with counts.
     */
    void printNodePaths() {
        printHeading("NODE PATHS");
        for (Node node : nodePaths.keySet()) {
            Set<Path> paths = nodePaths.get(node);
            String asterisk = " ";
            if (paths.size()==paths.size()) asterisk="*";
            System.out.print(asterisk+node.id+"("+paths.size()+"):");
            for (Path path : paths) {
                System.out.print(" "+path.name);
            }
            System.out.println("");
        }
    }

    /**
     * Print the sequences for each path, labeled by pathName.
     */
    void printPathSequences() {
        printHeading("PATH SEQUENCES");
        for (Path path : paths) {
            int length = path.sequence.length();
            String heading = ">"+path.name+" ("+length+")";
            System.out.print(heading);
            for (int i=heading.length(); i<19; i++) System.out.print(" "); System.out.print(".");
            for (int n=0; n<19; n++) {
                for (int i=0; i<9; i++) System.out.print(" "); System.out.print(".");
            }
            System.out.println("");
            // // entire sequence
            // System.out.println(sequence);
            // trimmed sequence beginning and end
            System.out.println(path.sequence.substring(0,100)+"........."+path.sequence.substring(length-101,length));
        }
    }

    /**
     * Set the preferred genotype.
     */
    public void setGenotype(int genotype) {
        this.genotype = genotype;
    }
}
