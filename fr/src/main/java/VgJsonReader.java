import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import java.util.Set;
import java.util.TreeSet;

import vg.Vg;

import com.google.protobuf.util.JsonFormat;

/**
 * Experiment with Vg classes.
 */
public class VgJsonReader {

    public static void main(String[] args) throws IOException {

        TreeMap<Integer,Integer> lengthMap = new TreeMap<>();
        TreeMap<Integer,Long> anyNodeStartMap = new TreeMap<>();
        TreeMap<Long,Integer> startToNode = new TreeMap<>();
        
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

        List<Vg.Node> nodes = graph.getNodeList();
        List<Vg.Path> paths = graph.getPathList();
        List<Vg.Edge> edges = graph.getEdgeList();

        int numNodes = nodes.size();
        for (Vg.Node node : nodes) {
            int id = (int) node.getId() - 1; // vg graphs are 1-based; splitMEM are 0-based
            String sequence = node.getSequence();
            int length = sequence.length();
            lengthMap.put(id,length);
            System.out.println("node:"+id+" length="+length);
            // initialize link set for population
            Set<Integer> linkSet = new TreeSet<>();
            neighborMap.put(id, linkSet);
        }

        // for (Vg.Path path : paths) {
        //     System.out.println(path.toString());
        // }
        
        for (Vg.Edge edge : edges) {
            int from = (int) edge.getFrom() - 1;
            int to = (int) edge.getTo() - 1;
            Set<Integer> linkSet = neighborMap.get(from);
            linkSet.add(to);
        }

        // MutableGraph g = Parser.read(new File(args[0]));
        // Collection<MutableNode> nodes = g.nodes();
        // for (MutableNode node : nodes) {
        //     String[] parts = node.get("label").toString().split(":");
        //     int id = Integer.parseInt(node.name().toString());
        //     int length = Integer.parseInt(parts[1]);
        //     lengthMap.put(id,length);
        //     String[] startStrings = parts[0].split(",");
        //     long[] starts = new long[startStrings.length];
        //     for (int i=0; i<startStrings.length; i++) {
        //         starts[i] = Long.parseLong(startStrings[i]);
        //         startToNode.put(starts[i], id);
        //         if (starts[i]>maxStart) maxStart = starts[i];
        //     }
        //     anyNodeStartMap.put(id, starts[0]);
        //     Set<Integer> linkSet = new TreeSet<>();
        //     List<Link> links = node.links();
        //     for (Link link : links) {
        //         String toString = link.to().toString();
        //         String[] chunks = toString.split(":");
        //         int to = Integer.parseInt(chunks[0]);
        //         linkSet.add(to);
        //     }
        //     neighborMap.put(id, linkSet);
        // }

        // int numNodes = lengthMap.size();
        
        int[] length = new int[numNodes];
        for (int i : lengthMap.keySet()) {
            length[i] = lengthMap.get(i);
        }

        // long[] anyNodeStart = new long[numNodes];
        // for (int i : anyNodeStartMap.keySet()) {
        //     anyNodeStart[i] = anyNodeStartMap.get(i);
        // }

        int[][] neighbor = new int[numNodes][];
        for (int i : neighborMap.keySet()) {
            Set<Integer> linkSet = neighborMap.get(i);
            neighbor[i] = new int[linkSet.size()];
            int j = 0;
            for (Integer to : linkSet) {
                neighbor[i][j++] = to;
            }
        }

        // output
        System.out.println("numNodes="+numNodes+" maxStart="+maxStart);
        System.out.print("length:");
        for (Integer l : length) System.out.print(" "+l);
        System.out.println("");
        
        // System.out.print("anyNodeStart:");
        // for (Long s : anyNodeStart) System.out.print(" "+s);
        // System.out.println("");
        // System.out.print("startToNode:");
        // for (Long s : startToNode.keySet()) System.out.print(" "+s+":"+startToNode.get(s));
        // System.out.println("");

        System.out.println("neighbor:");
        for (int i=0; i<numNodes; i++) {
            System.out.print(i+":");
            for (int j=0; j<neighbor[i].length; j++) System.out.print(" "+neighbor[i][j]);
            System.out.println("");
        }

    }

}
