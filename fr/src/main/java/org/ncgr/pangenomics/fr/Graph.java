package org.ncgr.pangenomics.fr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import java.util.Collection;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
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

    // these are loaded by FastaFile if FASTA; but here if Vg JSON
    private List<Sequence> sequences;
    private int[][] paths;
    private TreeSet<Long> Nlocs = new TreeSet<>();

    /**
     * Constructor does nothing; use read methods to populate the graph.
     */
    public Graph() {
    }

    /**
     * Read a Graph in from a Vg-generated JSON file.
     */
    public void readVgJsonFile(String filename) throws FileNotFoundException, IOException {
        jsonFile = filename;
        if (verbose) System.out.println("Reading JSON file: "+jsonFile);

        maxStart = 0;
        startToNode = new TreeMap<Long,Integer>();
        sequences = new LinkedList<>();

        Map<Integer,Integer> lengthMap = new TreeMap<>();
        Map<Integer,Long> anyNodeStartMap = new TreeMap<>();

        Map<Integer,Set<Integer>> neighborMap = new TreeMap<>();
        Map<Long,String> sequenceMap = new HashMap<>();
        Map<String,String> sampleSequenceMap = new HashMap<>();
        Map<String,List<Integer>> samplePaths = new HashMap<>();
        
        FileInputStream input = null;
        Reader reader = null;
        Vg.Graph graph;
        try {
            input = new FileInputStream(filename);
            reader = new InputStreamReader(input);
            Vg.Graph.Builder graphBuilder = Vg.Graph.newBuilder();
            JsonFormat.parser().merge(reader, graphBuilder);
            graph = graphBuilder.build();
        } finally {
            if (reader!=null) reader.close();
            if (input!=null) input.close();
        }

        List<Vg.Node> vgNodes = graph.getNodeList();
        List<Vg.Path> vgPaths = graph.getPathList();
        List<Vg.Edge> vgEdges = graph.getEdgeList();

        int numNodes = vgNodes.size();
        for (Vg.Node node : vgNodes) {
            int id = (int)(node.getId() - 1); // vg graphs are 1-based; splitMEM are 0-based
            String sequence = node.getSequence();
            sequenceMap.put(node.getId(), sequence);
            int length = sequence.length();
            lengthMap.put(id,length);
            // initialize link set for population
            Set<Integer> linkSet = new TreeSet<>();
            neighborMap.put(id, linkSet);
        }

        for (Vg.Edge edge : vgEdges) {
            int from = (int) edge.getFrom() - 1;
            int to = (int) edge.getTo() - 1;
            Set<Integer> linkSet = neighborMap.get(from);
            linkSet.add(to);
        }

        long totalLength = 0; // we'll pretend we're appending the sequences
        for (Vg.Path path : vgPaths) {
            String name = path.getName();
            // let's focus on sample paths, which start with "_thread_"
            String[] parts = name.split("_");
            if (parts.length>1 && parts[1].equals("thread")) {
                String sample = parts[2];
                int mappingCount = path.getMappingCount();
                List<Vg.Mapping> mappingList = path.getMappingList();
                String sequence = "";
                List<Integer> pathList = new LinkedList<>();
                for (Vg.Mapping mapping : mappingList) {
                    long rank = mapping.getRank();
                    Vg.Position position = mapping.getPosition();
                    long nodeId = position.getNodeId();
                    int nodeId0 = (int)(nodeId-1); // vg graphs are 1-based; splitMEM are 0-based
                    pathList.add(nodeId0); 
                    long start = (long)(totalLength+sequence.length()); // offset as if we're appending the samples
                    if (start>maxStart) maxStart = start;
                    startToNode.put(start, nodeId0);
                    if (!anyNodeStartMap.containsKey(nodeId0)) {
                        anyNodeStartMap.put(nodeId0, start);
                    }
                    sequence += sequenceMap.get(nodeId);
                }
                sampleSequenceMap.put(sample, sequence);
                samplePaths.put(sample, pathList);
                Sequence s = new Sequence(sample, sequence.length(), totalLength);
                sequences.add(s);
                totalLength += sequence.length();
            }
        }

        anyNodeStart = new long[numNodes];
        for (int i : anyNodeStartMap.keySet()) {
            anyNodeStart[i] = anyNodeStartMap.get(i);
        }

        int minLen = Integer.MAX_VALUE;
        length = new int[numNodes];
        for (int i : lengthMap.keySet()) {
            length[i] = lengthMap.get(i);
            if (length[i]<minLen) minLen = length[i];
        }
        K = minLen;

        neighbor = new int[numNodes][];
        for (int i : neighborMap.keySet()) {
            Set<Integer> linkSet = neighborMap.get(i);
            neighbor[i] = new int[linkSet.size()];
            int j = 0;
            for (Integer to : linkSet) {
                neighbor[i][j++] = to;
            }
        }

        paths = new int[samplePaths.size()][];
        int si = 0;
        for (String sample : samplePaths.keySet()) {
            List<Integer> pathList = samplePaths.get(sample);
            paths[si] = new int[pathList.size()];
            int sj = 0;
            for (Integer n : pathList) {
                paths[si][sj++] = n;
            }
            si++;
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
     * Find node paths.
     */
    public Map<Integer,TreeSet<Integer>> findNodePaths(int[][] paths, TreeSet<Long> Nlocs) {
        Map<Integer,TreeSet<Integer>> nodePaths;
        boolean[] containsN = new boolean[numNodes];
        for (int i = 0; i < numNodes; i++) {
            containsN[i] = false;
            Long test = Nlocs.ceiling(anyNodeStart[i]);
            if (test!=null && test.longValue()<anyNodeStart[i]+length[i]) {
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

    /**
     * Find a location in a sequence underlying a path
     */
    public long[] findLoc(List<Sequence> sequences, int path, int start, int stop) {
        long[] startStop = new long[2];
        long curStart = sequences.get(path).getStartPos();
        while (curStart>0 && !startToNode.containsKey(curStart)) {
            curStart--;
        }
        int curIndex = 0;
        while (curIndex!=start) {
            curStart += length[startToNode.get(curStart)] - (K - 1);
            curIndex++;
        }
        long offset = Math.max(0, sequences.get(path).getStartPos() - curStart);
        startStop[0] = curStart - sequences.get(path).getStartPos() + offset; // assume fasta seq indices start at 0
        while (curIndex != stop) {
            curStart += length[startToNode.get(curStart)] - (K - 1);
            curIndex++;
        }
        long seqLastPos = sequences.get(path).getStartPos() + sequences.get(path).getLength() - 1;
        // last position is excluded in BED format
        startStop[1] = Math.min(seqLastPos, curStart+length[startToNode.get(curStart)]-1) - sequences.get(path).getStartPos() + 1;
        return startStop;
    }

    // getters
    public String getFilename() {
        if (dotFile!=null) {
            return dotFile;
        } else if (jsonFile!=null) {
            return jsonFile;
        } else {
            return null;
        }
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
    public List<Sequence> getSequences() {
        return sequences;
    }
    public int[][] getPaths() {
        return paths;
    }
    public TreeSet<Long> getNlocs() {
        return Nlocs;
    }

    // setters
    public void setVerbose() {
        verbose = true;
    }

}
