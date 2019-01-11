import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Set;
import java.util.TreeSet;

import org.ncgr.pangenomics.fr.Sequence;

import vg.Vg;

import com.google.protobuf.util.JsonFormat;

/**
 * Read a vg-generated JSON file into a Vg.Graph object and create some objects.
 *
 * @author Sam Hokin
 */
public class VgJsonReader {

    public static void main(String[] args) throws IOException {

        String jsonFile = args[0];

        Map<Long,String> sequenceMap = new HashMap<>(); // keyed by nodeId, no need to sort
        Map<String,String> sampleSequenceMap = new TreeMap<>(); // keyed and sorted by sample name
	Map<String,LinkedList<Long>> samplePathMap = new TreeMap<>(); // keyed and sorted by sample name; List must preserve order of insertion
        
        // read the vg-created JSON into a Vg.Graph
	System.out.println("Reading "+jsonFile+"...");
	Reader reader = new InputStreamReader(new FileInputStream(jsonFile));
	Vg.Graph.Builder graphBuilder = Vg.Graph.newBuilder();
	JsonFormat.parser().merge(reader, graphBuilder);
        Vg.Graph graph = graphBuilder.build();

        List<Vg.Node> vgNodes = graph.getNodeList();
        List<Vg.Path> vgPaths = graph.getPathList();
        List<Vg.Edge> vgEdges = graph.getEdgeList();

        for (Vg.Node node : vgNodes) {
            sequenceMap.put(node.getId(), node.getSequence());
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

	// // let's look at a sample's path
	// List<Long> pathList = samplePathMap.get(desiredSample);
	// System.out.println("Path:");
	// for (long nodeId : pathList) {
	//     System.out.print(" "+nodeId);
	// }
	// System.out.println("");

	// // let's see how a sample's sequence was reconstructed
	// System.out.println(">"+desiredSample);
        // System.out.println(sampleSequenceMap.get(desiredSample));

	/////////////////////////////////////////////////////////////
	////////// org.ncgr.pangenomics.fr.Graph stuff //////////////
	/////////////////////////////////////////////////////////////
	
	// Graph.numNodes
	int numNodes = vgNodes.size();
	
	// Graph.length[] and Graph.neighbor[][]
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
	int[] length = new int[numNodes];
	for (long nodeId : lengthMap.keySet()) {
	    int i = (int)(nodeId-1);
	    length[i] = lengthMap.get(nodeId);
	}
	// Graph.neighbor[][]
	int[][] neighbor = new int[numNodes][];
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
	int[][] paths = new int[samplePathMap.size()][];
	int si = 0;
	for (String sample : samplePathMap.keySet()) {
	    List<Long> pathList = samplePathMap.get(sample);
	    paths[si] = new int[pathList.size()];
	    int sj = 0;
	    for (long nodeId : pathList) {
		paths[si][sj++] = (int)(nodeId-1);
	    }
	    si++;
	}
	
	// Graph.startToNode and Graph.anyNodeStart and Graph.sequences
	Map<Long,Integer> startToNode = new TreeMap<>();  // every start position --> node
	Map<Long,Long> anyNodeStartMap = new TreeMap<>(); // node --> any start position
	List<Sequence> sequences = new LinkedList<>();    // sample,length,start
	long maxStart = 0;
	long start = 0;                                   // 0-based start location (?)
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
	long[] anyNodeStart = new long[numNodes];
	for (long nodeId : anyNodeStartMap.keySet()) {
	    int i = (int)(nodeId-1);
	    anyNodeStart[i] = anyNodeStartMap.get(nodeId);
	}

	// OUTPUT
        System.out.println("numNodes="+numNodes+" maxStart="+maxStart);
        System.out.print("length:");
        for (Integer l : length) System.out.print(" "+l);
        System.out.println("");
        System.out.println("Sequences:");
        for (Sequence s : sequences) {
            System.out.println(s.toString());
        }

        System.out.println("paths:");
        for (int i=0; i<paths.length; i++) {
            for (int j=0; j<paths[i].length; j++) {
                System.out.print(" "+paths[i][j]);
            }
            System.out.println("");
        }
        
        System.out.print("anyNodeStart:");
        for (Long s : anyNodeStart) System.out.print(" "+s);
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
