import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
 * Experiment with Vg classes.
 */
public class VgJsonReader {

    public static void main(String[] args) throws IOException {

        Map<Integer,Integer> lengthMap = new TreeMap<>();
        Map<Integer,Long> anyNodeStartMap = new TreeMap<>();
        Map<Long,Integer> startToNode = new TreeMap<>();

        Map<Long,String> sequenceMap = new HashMap<>();
        Map<String,String> sampleSequenceMap = new HashMap<>();
        List<Sequence> sequences = new LinkedList<>();
        Map<String,List<Integer>> samplePaths = new HashMap<>();
        
        long maxStart = 0;

        // used for neighbor[][]
        TreeMap<Integer,Set<Integer>> neighborMap = new TreeMap<>();

        // read the vg-created JSON into a Vg.Graph
        Vg.Graph graph;
        FileInputStream input = null;
        Reader reader = null;
        try {
            input = new FileInputStream(args[0]);
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

        // This is what the JSON will have in it for three genomes.
        // P	_thread_genome1_1_0_0	1+,3+,4+,6+,7+,9+,10+,12+,13+	301M,2M,2M,3M,53M,3M,225M,3M,408M
        // P	_thread_genome2_1_0_0	1+,3+,4+,6+,7+,8+,10+,11+,13+	301M,2M,2M,3M,53M,3M,225M,3M,408M
        // P	_thread_genome3_1_0_0	1+,2+,4+,5+,7+,9+,10+,12+,13+	301M,2M,2M,3M,53M,3M,225M,3M,408M

        System.out.println("===================== PATHS ====================");
        long totalLength = 0; // we'll pretend we're appending the sequences
        for (Vg.Path path : vgPaths) {
            String name = path.getName();
            // let's focus on sample paths, which start with "_thread_"
            String[] parts = name.split("_");
            if (parts.length>1 && parts[1].equals("thread")) {
                String sample = parts[2];
                int mappingCount = path.getMappingCount();
                List<Vg.Mapping> mappingList = path.getMappingList();
                System.out.println("name="+name+"; mappingCount="+mappingCount+"; sample="+sample);
                String sequence = "";
                List<Integer> pathList = new LinkedList<>();
                for (Vg.Mapping mapping : mappingList) {
                    long rank = mapping.getRank();
                    Vg.Position position = mapping.getPosition();
                    long nodeId = position.getNodeId();
                    int nodeId0 = (int)(nodeId-1); // vg graphs are 1-based; splitMEM are 0-based
                    pathList.add(nodeId0); 
                    long start = (long)(totalLength+sequence.length()); // offset as if we're appending the samples
                    startToNode.put(start, nodeId0);
                    if (!anyNodeStartMap.containsKey(nodeId0)) {
                        anyNodeStartMap.put(nodeId0, start);
                    }
                    System.out.println("-- rank:"+rank+" node:"+nodeId0+" start:"+start+" length:"+sequenceMap.get(nodeId).length());
                    sequence += sequenceMap.get(nodeId);
                }
                sampleSequenceMap.put(sample, sequence);
                samplePaths.put(sample, pathList);
                Sequence s = new Sequence(sample, sequence.length(), totalLength);
                sequences.add(s);
                totalLength += sequence.length();
            }
        }
        System.out.println("totalLength="+totalLength);
        System.out.println("================================================");

        for (String sample : sampleSequenceMap.keySet()) {
            System.out.println(">"+sample);
            System.out.println(sampleSequenceMap.get(sample));
        }
        System.out.println("================================================");

        long[] anyNodeStart = new long[numNodes];
        for (int i : anyNodeStartMap.keySet()) {
            anyNodeStart[i] = anyNodeStartMap.get(i);
        }

        // FRFinder style length[]
        int[] length = new int[numNodes];
        for (int i : lengthMap.keySet()) {
            length[i] = lengthMap.get(i);
        }

        // FRFinder style neighbor[][]
        int[][] neighbor = new int[numNodes][];
        for (int i : neighborMap.keySet()) {
            Set<Integer> linkSet = neighborMap.get(i);
            neighbor[i] = new int[linkSet.size()];
            int j = 0;
            for (Integer to : linkSet) {
                neighbor[i][j++] = to;
            }
        }

        // FRFinder style paths[][]
        int[][] paths = new int[samplePaths.size()][];
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

        // output
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
