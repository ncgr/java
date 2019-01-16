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
    private boolean verbose = false;

    // made available so directory names can be formed from it
    private String dotFile;
    private String fastaFile;
    private String jsonFile;
    
    long minLen = Long.MAX_VALUE;  // minimum sequence length

    // maps nodes to their DNA sequences
    TreeMap<Long,String> nodeSequences; // keyed by nodeId, ordered by nodeId for convenience

    // maps a nodeId to the set of paths that traverse it
    TreeMap<Long,Set<Path>> nodePaths; // keyed and ordered by nodeId
    
    // each Path provides the ordered list of nodes that it traverses; ordered by path category/name
    TreeSet<Path> paths; // ordered by category/name/nodes
    
    // maps a Path to its full DNA sequence, ordered by path name
    TreeMap<String,String> pathSequences; // keyed and ordered by path name

    /**
     * Constructor does nothing; use read methods to populate the graph from files.
     */
    public Graph() {
    }

    /**
     * Read a Graph in from a Vg-generated JSON file.
     */
    public void readVgJsonFile(String jsonFile) throws FileNotFoundException, IOException {
        this.jsonFile = jsonFile;

        // instantiate the instance objects
        nodeSequences = new TreeMap<>();   // keyed and ordered by nodeId
        nodePaths = new TreeMap<>();       // keyed and ordered by nodeId
        paths = new TreeSet<>();           // ordered by path
        pathSequences = new TreeMap<>();   // keyed and ordered by path name

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

        // populate nodeSequences
        for (Vg.Node node : vgNodes) {
            long nodeId = node.getId();
            String sequence = node.getSequence();
            nodeSequences.put(nodeId, sequence);
            if (sequence.length()<minLen) minLen = sequence.length();
	}

        // populate paths and pathSequences
        for (Vg.Path vgPath : vgPaths) {
            String name = vgPath.getName();
            // sample paths are assumed to start with "_thread_"
            String[] parts = name.split("_");
            if (parts.length>1 && parts[1].equals("thread")) {
                // we've got a subject/strain/whatever path
		String pathName = parts[2];
                int allele = Integer.parseInt(parts[4]); // 0 or 1
                // assume unphased calls, so allele 0 is essentially reference, so only use allele 1
                if (allele==1) {
                    List<Vg.Mapping> vgMappingList = vgPath.getMappingList();
                    // retreive or initialize this sample's sequence (so far)
                    String sequence = null;
                    if (pathSequences.containsKey(pathName)) {
                        sequence = pathSequences.get(pathName);
                    } else {
                        sequence = "";
                    }
                    // retrieve or initialize this path
                    LinkedList<Long> nodes = new LinkedList<>();
                    for (Path path : paths) {
                        if (path.name.equals(pathName)) {
                            nodes = path.nodes;
                        }
                    }
                    // run through this particular mapping and append each node id to the node list, and the node sequence to the total-as-of-yet path sequence
                    boolean first = true;
                    for (Vg.Mapping vgMapping : vgMappingList) {
                        if (first && nodes.size()>0) {
                            // skip unless very first node
                        } else {
                            long nodeId = vgMapping.getPosition().getNodeId();
                            String nodeSequence = nodeSequences.get(nodeId);
                            nodes.add(nodeId);
                            sequence += nodeSequence;
                        }
                        first = false;
                    }
                    // update the path with the new nodes and sequence
                    boolean updated = false;
                    for (Path path : paths) {
                        if (path.name.equals(pathName)) {
                            path.setNodes(nodes);
                            updated = true;
                        }
                    }
                    if (!updated) {
                        // create this new path
                        Path path = new Path(pathName, nodes);
                        paths.add(path);
                    }
                    // update/add path sequence
                    pathSequences.put(pathName, sequence);
                }
            }
        }

        // find the node paths
        findNodePaths();
    }

    /**
     * Find node paths: the set of paths that run through each node.
     * NOTE: no support for Nlocs yet!
     */
    void findNodePaths() {
        // init empty paths for each node
        for (long nodeId : nodeSequences.keySet()) {
            nodePaths.put(nodeId, new TreeSet<Path>());
        }
        // now load the paths
        for (Path path : paths) {
            for (long nodeId : path.nodes) {
                nodePaths.get(nodeId).add(path);
            }
        }
    }

    // /**
    //  * Find the gap on a path between a start and stop.
    //  * NOTE: NOT SURE IF THIS WORKS USING INDEXES RATHER THAN SAMPLE NAMES
    //  */
    // public int findGap(List<Long> pathList, long start, long stop) {
    //     int curStart = 1;
    //     int curEnd = 1;
    //     for (long s=start; s<=stop; s++) {
    //         curEnd = curStart + nodeSequences.get(s).length() - 1;
    //         curStart += nodeSequences.get(s).length() - (minLen-1);
    //     }
    //     int gap = curEnd - nodeSequences.get(start).length() - nodeSequences.get(stop).length();
    //     if (gap<0) gap = 0;
    //     // DEBUG
    //     System.out.println("pathList="+pathList);
    //     System.out.println(" start="+start+" stop="+stop+" gap="+gap);
    //     return gap;
    // }

    /**
     * Set path categories from a tab-delimited file. Comment lines start with #.
     */
    void setPathCategories(String categoriesFile) throws FileNotFoundException, IOException {
        BufferedReader reader = new BufferedReader(new FileReader(categoriesFile));
        String line = null;
        Map<String,String> categories = new TreeMap<String,String>();
        while ((line=reader.readLine())!=null) {
            if (!line.startsWith("#")) {
                String[] fields = line.split("\t");
                if (fields.length==2) {
                    categories.put(fields[0], fields[1]);
                }
            }
        }
        if (categories.size()==paths.size()) {
            for (Path path : paths) {
                path.setCategory(categories.get(path.name));
            }
        } else {
            System.err.println("ERROR: the categories file "+categoriesFile+" contains "+categories.size()+" category labels while there are "+paths.size()+" paths in the graph.");
            System.exit(1);
        }
    }
    
    // getters of private vars
    public String getDotFile() {
        return dotFile;
    }
    public String getFastaFile() {
        return fastaFile;
    }
    public String getJsonFile() {
        return jsonFile;
    }

    // setters of private vars
    public void setVerbose() {
        verbose = true;
    }

}
