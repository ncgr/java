package org.ncgr.pangenomics.fr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.TreeSet;

import vg.Vg;

import com.google.protobuf.util.JsonFormat;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;

import guru.nidi.graphviz.model.Link;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;

import guru.nidi.graphviz.parse.Parser;

/**
 * Storage of a graph.
 *
 * @author bmumey
 */ 
public class Graph {

    // output verbosity
    private boolean verbose = false;

    // made available so directory names can be formed from it
    private String dotFile;
    private String jsonFile;
    
    // equals minimum length of a node
    private int K;

    // number of nodes and their lengths
    private int numNodes;
    private int[] length;
    
    // the maximum start value
    private long maxStart;

    // the first start value of each node
    private long[] anyNodeStart;

    // list of each node's unique neighbors
    private int[][] neighbor;

    // maps a start location to a node
    private Map<Long,Integer> startToNode;

    /**
     * Constructor does nothing; use read methods to populate the graph.
     */
    public Graph() {
    }

    /**
     * Read a Graph in from a JSON file using Vg.
     */
    public void readJsonFile(String filename) throws FileNotFoundException, IOException {
        jsonFile = filename;
        if (verbose) System.out.println("Reading JSON file: "+jsonFile);

        startToNode = new TreeMap<Long,Integer>();
        maxStart = 0;

        int minLen = Integer.MAX_VALUE;

        TreeMap<Integer,Integer> lengthMap = new TreeMap<>();
        TreeMap<Integer,Long> anyNodeStartMap = new TreeMap<>();

        // used to make neighbor[][]
        TreeMap<Integer,Set<Integer>> neighborMap = new TreeMap<>();

        FileInputStream input = null;
        Reader reader = null;
        try {
            input = new FileInputStream(jsonFile);
            reader = new InputStreamReader(input);
            Vg.Graph.Builder graphBuilder = Vg.Graph.newBuilder();
            JsonFormat.parser().merge(reader, graphBuilder);
            Vg.Graph vgGraph = graphBuilder.build();






            
        } finally {
            if (reader!=null) reader.close();
            if (input!=null) input.close();
        }
    }


    /**
     * Read a Graph in from a splitMEM-style DOT file using guru.nidi.graphviz.mode classes.
     */
    public void readSplitMEMDotFile(String filename) throws IOException {
        this.dotFile = filename;
        if (verbose) System.out.println("Reading dot file: "+dotFile);

        startToNode = new TreeMap<Long,Integer>();
        maxStart = 0;

        int minLen = Integer.MAX_VALUE;

        TreeMap<Integer,Integer> lengthMap = new TreeMap<>();
        TreeMap<Integer,Long> anyNodeStartMap = new TreeMap<>();

        // used to make neighbor[][]
        TreeMap<Integer,Set<Integer>> neighborMap = new TreeMap<>();

        MutableGraph g = Parser.read(new File(filename));
        Collection<MutableNode> nodes = g.nodes();
        for (MutableNode node : nodes) {
            String[] parts = node.get("label").toString().split(":");
            int id = Integer.parseInt(node.name().toString());
            int length = Integer.parseInt(parts[1]);
            lengthMap.put(id,length);
            if (length<minLen) minLen = length;
            String[] startStrings = parts[0].split(",");
            long[] starts = new long[startStrings.length];
            for (int i=0; i<startStrings.length; i++) {
                starts[i] = Long.parseLong(startStrings[i]);
                startToNode.put(starts[i], id);
                if (starts[i]>maxStart) maxStart = starts[i];
            }
            anyNodeStartMap.put(id, starts[0]);
            Set<Integer> linkSet = new TreeSet<>();
            List<Link> links = node.links();
            for (Link link : links) {
                String toString = link.to().toString();
                String[] chunks = toString.split(":");
                int to = Integer.parseInt(chunks[0]);
                linkSet.add(to);
            }
            neighborMap.put(id, linkSet);
        }

        numNodes = lengthMap.size();
        K = minLen;
        if (verbose) System.out.println("numNodes="+numNodes+" K="+K+" maxStart="+maxStart);
        
        length = new int[numNodes];
        for (int i : lengthMap.keySet()) {
            length[i] = lengthMap.get(i);
        }

        anyNodeStart = new long[numNodes];
        for (int i : anyNodeStartMap.keySet()) {
            anyNodeStart[i] = anyNodeStartMap.get(i);
        }

        neighbor = new int[numNodes][];
        for (int i : neighborMap.keySet()) {
            Set<Integer> linkSet = neighborMap.get(i);
            neighbor[i] = new int[linkSet.size()];
            int j = 0;
            for (Integer to : linkSet) {
                neighbor[i][j++] = to;
            }
        }
    }

    /**
     * Find the gap on a path between a start and stop
     */
    public int findGap(int[] path, int start, int stop) {
        int curStartLoc = 1;
        int curEndLoc = 1;
        for (int i = start; i <= stop; i++) {
            curEndLoc = curStartLoc + length[path[i]] - 1;
            curStartLoc += length[path[i]] - (K - 1);
        }
        int gp = curEndLoc - length[path[start]] - length[path[stop]];
        if (gp <= 0) {
            gp = 0;
        }
        return gp;
    }

    /**
     * Find node paths corresponding to FASTA paths.
     */
    public Map<Integer,TreeSet<Integer>> findNodePaths(int[][] paths, TreeSet<Long> Nlocs) {
        Map<Integer,TreeSet<Integer>> nodePaths;
        boolean[] containsN = new boolean[numNodes];
        for (int i = 0; i < numNodes; i++) {
            containsN[i] = false;
            Long test = Nlocs.ceiling(anyNodeStart[i]);
            if (test != null && test.longValue() < anyNodeStart[i] + length[i]) {
                containsN[i] = true;
            }
        }
        // find paths for each node:
        nodePaths = new TreeMap<Integer,TreeSet<Integer>>();
        for (int i = 0; i < numNodes; i++) {
            if (!containsN[i]) {
                nodePaths.put(i, new TreeSet<Integer>());
            }
        }
        for (int i = 0; i < paths.length; i++) {
            for (int j = 0; j < paths[i].length; j++) {
                if (!containsN[paths[i][j]]) {
                    nodePaths.get(paths[i][j]).add(i);
                }
            }
        }
        return nodePaths;
    }

    // getters
    public String getDotFile() {
        return dotFile;
    }
    public String getJsonFile() {
        return jsonFile;
    }
    public int getK() {
        return K;
    }
    public int getNumNodes() {
        return numNodes;
    }
    public int[] getLength() {
        return length;
    }
    public long getMaxStart() {
        return maxStart;
    }
    public long[] getAnyNodeStart() {
        return anyNodeStart;
    }
    public Map<Long,Integer> getStartToNode() {
        return startToNode;
    }
    public int[][] getNeighbor() {
        return neighbor;
    }

    // setters
    public void setVerbose() {
        verbose = true;
    }

}
