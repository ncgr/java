import java.io.File;
import java.io.IOException;

import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import java.util.Set;
import java.util.TreeSet;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;

import guru.nidi.graphviz.model.Link;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;

import guru.nidi.graphviz.parse.Parser;

/**
 * Experiment with some graphviz-java classes and methods.
 */
public class GraphVizJava {

    public static void main(String[] args) throws IOException {

        TreeMap<Integer,Integer> lengthMap = new TreeMap<>();
        TreeMap<Integer,Long> anyNodeStartMap = new TreeMap<>();
        TreeMap<Long,Integer> startToNode = new TreeMap<>();
        
        // used for neighbor[][]
        TreeMap<Integer,Set<Integer>> neighborMap = new TreeMap<>();

        MutableGraph g = Parser.read(new File(args[0]));
        Collection<MutableNode> nodes = g.nodes();
        long maxStart = 0;
        for (MutableNode node : nodes) {
            String[] parts = node.get("label").toString().split(":");
            int id = Integer.parseInt(node.name().toString());
            int length = Integer.parseInt(parts[1]);
            lengthMap.put(id,length);
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

        int numNodes = lengthMap.size();
        
        int[] length = new int[numNodes];
        for (int i : lengthMap.keySet()) {
            length[i] = lengthMap.get(i);
        }

        long[] anyNodeStart = new long[numNodes];
        for (int i : anyNodeStartMap.keySet()) {
            anyNodeStart[i] = anyNodeStartMap.get(i);
        }

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

        // Graphviz.fromGraph(g).width(700).render(Format.PNG).toFile(new File("example1.png"));

    }

}
