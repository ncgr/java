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
    private String fastaFile;
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

    // these are loaded by FastaFile if FASTA; from the JSON if Vg JSON
    private List<Sequence> sequences;
    private int[][] paths;
    private TreeSet<Long> Nlocs = new TreeSet<>();

    // handy to have this available
    private List<String> samples;

    /**
     * Constructor does nothing; use read methods to populate the graph.
     */
    public Graph() {
    }

    /**
     * Read a Graph in from a Vg-generated JSON file.
     */
    public void readVgJsonFile(String jsonFile) throws FileNotFoundException, IOException {
        this.jsonFile = jsonFile;

        Map<Long,String> sequenceMap = new HashMap<>(); // keyed by nodeId, no need to sort
        Map<String,String> sampleSequenceMap = new TreeMap<>(); // keyed and sorted by sample name
	Map<String,LinkedList<Long>> samplePathMap = new TreeMap<>(); // keyed and sorted by sample name; List must preserve order of insertion

	samples = new LinkedList<>();

        // read the vg-created JSON into a Vg.Graph
	if (verbose) System.out.println("Reading "+jsonFile+"...");
	Reader reader = new InputStreamReader(new FileInputStream(jsonFile));
	Vg.Graph.Builder graphBuilder = Vg.Graph.newBuilder();
	JsonFormat.parser().merge(reader, graphBuilder);
        Vg.Graph graph = graphBuilder.build();

        List<Vg.Node> vgNodes = graph.getNodeList();
        List<Vg.Path> vgPaths = graph.getPathList();
        List<Vg.Edge> vgEdges = graph.getEdgeList();

        for (Vg.Node node : vgNodes) {
            sequenceMap.put(node.getId(), node.getSequence());
	    // DEBUG
	    System.out.println(node.getId()+":"+node.getSequence());
	}
	
        for (Vg.Path path : vgPaths) {
            String name = path.getName();
            // let's focus on sample paths, which start with "_thread_"
            String[] parts = name.split("_");
            if (parts.length>1 && parts[1].equals("thread")) {
		String sample = parts[2];
		List<Vg.Mapping> mappingList = path.getMappingList();
		// initialize or retreive this sample's sequence
		String sequence = null;
		if (sampleSequenceMap.containsKey(sample)) {
		    sequence = sampleSequenceMap.get(sample);
		} else {
		    sequence = "";
		}
		// initialize or retreive this sample's path list
		LinkedList<Long> pathList = null;
		if (samplePathMap.containsKey(sample)) {
		    pathList = samplePathMap.get(sample);
		} else {
		    pathList = new LinkedList<>();
		}
		// run through this particular mapping and append the node id to the path list, and the node sequence to the sample sequence
		for (Vg.Mapping mapping : mappingList) {
		    long nodeId = mapping.getPosition().getNodeId();
		    pathList.add(nodeId);
		    sequence += sequenceMap.get(nodeId);
		}
		// update the maps
		samplePathMap.put(sample, pathList);
		sampleSequenceMap.put(sample, sequence);
	    }
        }

	/////////////////////////////////////////////////////////////
	////////// org.ncgr.pangenomics.fr.Graph stuff //////////////
	/////////////////////////////////////////////////////////////

	// Graph.numNodes
	numNodes = vgNodes.size();
	
	// prep for length and neighbor
	Map<Long,Integer> lengthMap = new TreeMap<>(); // keyed by nodeId-1
	TreeMap<Long,Set<Long>> neighborMap = new TreeMap<>();
	for (long nodeId : sequenceMap.keySet()) {
	    lengthMap.put(nodeId, sequenceMap.get(nodeId).length());
	    // initialize linkSet for population next
	    Set<Long> linkSet = new TreeSet<>();
	    neighborMap.put(nodeId, linkSet);
	}
	for (Vg.Edge edge : vgEdges) {
	    Set<Long> linkSet = neighborMap.get(edge.getFrom());
	    linkSet.add(edge.getTo());
        }

	// Graph.length[]
	length = new int[numNodes];
	for (long nodeId : lengthMap.keySet()) {
	    int i = (int)(nodeId-1);
	    length[i] = lengthMap.get(nodeId);
	}

	// Graph.neighbor[][]
	neighbor = new int[numNodes][];
	for (long nodeId : neighborMap.keySet()) {
	    Set<Long> linkSet = neighborMap.get(nodeId);
	    int i = (int)(nodeId-1);
	    neighbor[i] = new int[linkSet.size()];
	    int j = 0;
	    for (Long to : linkSet) {
		neighbor[i][j++] = (int)(to-1);
	    }
	}

	// Graph.paths[][]
	paths = new int[samplePathMap.size()][];
	int si = 0;
	for (String sample : samplePathMap.keySet()) {
	    samples.add(sample);
	    List<Long> pathList = samplePathMap.get(sample);
	    paths[si] = new int[pathList.size()];
	    int sj = 0;
	    for (long nodeId : pathList) {
		paths[si][sj++] = (int)(nodeId-1);
	    }
	    si++;
	}
	
	// Graph.startToNode, Graph.sequences, Graph.maxStart
	startToNode = new TreeMap<>();  // every start position --> node
	sequences = new LinkedList<>(); // sample,length,start
	maxStart = 0;
	long start = 0;                                   // 0-based start location (?)
	Map<Long,Long> anyNodeStartMap = new TreeMap<>(); // node --> any start position
	for (String sample : samplePathMap.keySet()) {
	    List<Long> pathList = samplePathMap.get(sample);
	    for (long nodeId : pathList) {
		String sequence = sequenceMap.get(nodeId);
		if (!anyNodeStartMap.containsKey(nodeId)) {
		    anyNodeStartMap.put(nodeId,start);
		}
		startToNode.put(start,(int)(nodeId-1));
		Sequence s = new Sequence(sample, sequence.length(), start);
		sequences.add(s);
		maxStart = start;
		// increment universal start position for next round
		start += sequence.length();
	    }
	}

	// Graph.anyNodeStart
	anyNodeStart = new long[numNodes];
	for (long nodeId : anyNodeStartMap.keySet()) {
	    int i = (int)(nodeId-1);
	    anyNodeStart[i] = anyNodeStartMap.get(nodeId);
	}

	// print a summary
	if (verbose) printSummary();
    }

    /**
     * Read a Graph in from a splitMEM-style DOT file using guru.nidi.graphviz.mode classes.
     */
    public void readSplitMEMDotFile(String dotFile, String fastaFile) throws IOException {
        this.dotFile = dotFile;
        this.fastaFile = fastaFile;
        if (verbose) System.out.println("Reading dot file: "+dotFile);

        maxStart = 0;
        startToNode = new TreeMap<Long,Integer>();

        int minLen = Integer.MAX_VALUE;

        TreeMap<Integer,Integer> lengthMap = new TreeMap<>();
        TreeMap<Integer,Long> anyNodeStartMap = new TreeMap<>();
        TreeMap<Integer,Set<Integer>> neighborMap = new TreeMap<>();

        MutableGraph g = Parser.read(new File(dotFile));
        Collection<MutableNode> nodes = g.nodes();
        for (MutableNode node : nodes) {
            String[] parts = node.get("label").toString().split(":");
            int id = Integer.parseInt(node.name().toString());
            int l = Integer.parseInt(parts[1]);
            lengthMap.put(id,l);
            if (l<minLen) minLen = l;
            String[] startStrings = parts[0].split(",");
            long[] starts = new long[startStrings.length];
            for (int i=0; i<startStrings.length; i++) {
                starts[i] = Long.parseLong(startStrings[i]) - 1; // ADDED - 1
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

        // get the FASTA file parameters
        FastaFile f = new FastaFile(this);
        if (verbose) f.setVerbose();
        f.readFastaFile(fastaFile);
        sequences = f.getSequences();
        paths = f.getPaths();
        Nlocs = f.getNlocs();

	// print a summary
	if (verbose) printSummary();
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
	    checkStartToNode(curStart);
            curStart += length[startToNode.get(curStart)] - (K-1);
            curIndex++;
        }
        long offset = Math.max(0, sequences.get(path).getStartPos() - curStart);
        startStop[0] = curStart - sequences.get(path).getStartPos() + offset; // assume fasta seq indices start at 0
        while (curIndex!=stop) {
	    checkStartToNode(curStart);
            curStart += length[startToNode.get(curStart)] - (K);
            curIndex++;
        }
        long seqLastPos = sequences.get(path).getStartPos() + sequences.get(path).getLength() - 1;
        // last position is excluded in BED format
	checkStartToNode(curStart);
        startStop[1] = Math.min(seqLastPos, curStart+length[startToNode.get(curStart)]-1) - sequences.get(path).getStartPos() + 1;
        return startStop;
    }

    /**
     * Check that startToNode actually contains the given key
     */
    void checkStartToNode(long curStart) {
	if (!startToNode.containsKey(curStart)) {
	    System.err.println("ERROR: startToNode("+curStart+") does not exist.");
	    System.err.print("Nearby startToNode keys:");
	    for (long key : startToNode.keySet()) {
		if (Math.abs(curStart-key)<10) System.err.print(" "+key);
	    }
	    System.err.println("");
	    System.exit(1);
	}
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

    /**
     * Print a summary of this graph's data.
     */
    void printSummary() {
	System.out.println("numNodes="+numNodes+" maxStart="+maxStart);

	System.out.print("length:");
	for (int i=0; i<length.length; i++) {
	    System.out.print(" "+i+":"+length[i]);
	}
	System.out.println("");

	// System.out.println("sequences:");
	// for (Sequence s : sequences) {
	//     System.out.println(s.toString());
	// }
	
	System.out.println("paths:");
	for (int i=0; i<paths.length; i++) {
	    System.out.print(samples.get(i)+":");
	    for (int j=0; j<paths[i].length; j++) {
		System.out.print(" "+paths[i][j]);
	    }
	    System.out.println("");
	}
	
	System.out.print("anyNodeStart:");
	for (int i=0; i<anyNodeStart.length; i++) {
	    System.out.print(" "+i+":"+anyNodeStart[i]);
	}
	System.out.println("");
	
	System.out.print("startToNode:");
	for (Long s : startToNode.keySet()) System.out.print(" "+s+":"+startToNode.get(s));
	System.out.println("");
	
	System.out.println("neighbor:");
	for (int i=0; i<numNodes; i++) {
	    System.out.print(i+":");
	    for (int j=0; j<neighbor[i].length; j++) System.out.print(" "+neighbor[i][j]);
	    System.out.println("");
	}
    }
}
