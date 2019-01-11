package org.ncgr.pangenomics.fr;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

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
    
    // maps nodes to their sequences; ordered by node because why not?
    private TreeMap<Long,String> sequenceMap; // keyed and ordered by nodeId

    // maps each start location on the concatenated sample sequence to its node
    private TreeMap<Long,Long> startToNode; // keyed and ordered by start location

    // maps a node to one of its start locations on the concatenated sample sequence
    private TreeMap<Long,Long> nodeToStart; // keyed and ordered by nodeId

    // maps a sample name to its full sequence, ordered by sample name
    private TreeMap<String,String> sampleSequenceMap; // keyed and ordered by sample name

    // maps a sample to its path through the graph
    private TreeMap<String,LinkedList<Long>> samplePathMap; // keyed and sorted by sample name; path list must preserve order
    
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

        // instantiate the instance maps
        sequenceMap = new TreeMap<>();       // keyed and ordered by nodeId
        startToNode = new TreeMap<>();       // keyed and ordered by start location
        nodeToStart = new TreeMap<>();       // keyed and ordered by nodeId
        sampleSequenceMap = new TreeMap<>(); // keyed and ordered by sample name
        samplePathMap= new TreeMap<>();      // keyed and ordered by sample name

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

        // populate sequenceMap
        for (Vg.Node node : vgNodes) {
            sequenceMap.put(node.getId(), node.getSequence());
	}

        // populate samplePathMap and sampleSequenceMap
        for (Vg.Path path : vgPaths) {
            String name = path.getName();
            // sample paths are assumed to start with "_thread_"
            String[] parts = name.split("_");
            if (parts.length>1 && parts[1].equals("thread")) {
                // we've got a sample path
		String sample = parts[2];
                int allele = Integer.parseInt(parts[4]); // 0 or 1; assume unphased calls, so 0 is essentially reference, so ignore
                if (allele==1) {
                    List<Vg.Mapping> mappingList = path.getMappingList();
                    // retreive or initialize this sample's sequence (so far)
                    String sequence = null;
                    if (sampleSequenceMap.containsKey(sample)) {
                        sequence = sampleSequenceMap.get(sample);
                    } else {
                        sequence = "";
                    }
                    // retreive or initialize this sample's path list
                    LinkedList<Long> pathList = null;
                    if (samplePathMap.containsKey(sample)) {
                        pathList = samplePathMap.get(sample);
                    } else {
                        pathList = new LinkedList<>();
                    }
                    // run through this particular mapping and append the node id to the path list, and the node sequence to the total-as-of-yet sample sequence
                    boolean first = true;
                    for (Vg.Mapping mapping : mappingList) {
                        if (first && pathList.size()>0) {
                            // skip unless very first node
                        } else {
                            long nodeId = mapping.getPosition().getNodeId();
                            String nodeSequence = sequenceMap.get(nodeId);
                            pathList.add(nodeId);
                            sequence += nodeSequence;
                        }
                        first = false;
                    }
                    // update the maps with the new pathList and sequence
                    samplePathMap.put(sample, pathList);
                    sampleSequenceMap.put(sample, sequence);
                }
            }
        }
        
	// print a verbose summary
	if (verbose) printSummary();
    }

    // getters
    public String getDotFile() {
        return dotFile;
    }
    public String getFastaFile() {
        return fastaFile;
    }
    public String getJsonFile() {
        return jsonFile;
    }

    public TreeMap<Long,String> getSequenceMap() {
        return sequenceMap;
    }
    public TreeMap<Long,Long> getStartToNode() {
        return startToNode;
    }
    public TreeMap<Long,Long> getNodeToStart() {
        return nodeToStart;
    }
    public TreeMap<String,String> getSampleSequenceMap() {
        return sampleSequenceMap;
    }
    public TreeMap<String,LinkedList<Long>> getSamplePathMap() {
        return samplePathMap;
    }

    // setters
    public void setVerbose() {
        verbose = true;
    }

    /**
     * Print a summary of this graph's data.
     */
    void printSummary() {
        printHeading("NODES");
        for (long nodeId : sequenceMap.keySet()) {
            System.out.println(nodeId+":"+sequenceMap.get(nodeId));
        }

        printHeading("SAMPLE PATHS");
        for (String sample : samplePathMap.keySet()) {
            System.out.print(sample+":");
            List<Long> pathList = samplePathMap.get(sample);
            for (long nodeId : pathList) {
                System.out.print(" "+nodeId);
            }
            System.out.println("");
        }

        printHeading("SAMPLE SEQUENCES");
        for (String sample : sampleSequenceMap.keySet()) {
            String sequence = sampleSequenceMap.get(sample);
            int length = sequence.length();
            String heading = ">"+sample+" ("+length+")";
            System.out.print(heading);
            for (int i=heading.length(); i<19; i++) System.out.print(" "); System.out.print(".");
            for (int n=0; n<10; n++) {
                for (int i=0; i<9; i++) System.out.print(" "); System.out.print(".");
            }
            System.out.println("");
            System.out.println(sequence);
            // System.out.println(sample+"("+length+"):"+sequence.substring(0,50)+"..."+sequence.substring(length-50,length));
        }
    }

    /**
     * Print a delineating heading
     */
    void printHeading(String heading) {
        for (int i=0; i<heading.length(); i++) System.out.print("="); System.out.println("");
        System.out.println(heading);
        for (int i=0; i<heading.length(); i++) System.out.print("="); System.out.println("");
    }

}
