import java.io.IOException;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.ncgr.pangenomics.fr.Graph;

/**
 * Do some diagnostics on the Graph class.
 */
public class GraphReader {
    
    public static void main(String[] args) throws IOException {

        String dotfile = args[0];

        // create a Graph from the dot file
        Graph g = new Graph();
        g.readSplitMEMDotFile(dotfile);

        System.out.println("DOT file:"+g.getDotFile());
        System.out.println("K="+g.getK());
        System.out.println("numNodes="+g.getNumNodes());
        System.out.println("length:");
        int[] length = g.getLength();
        for (int l : length) {
            System.out.print(" "+l);
        }
        System.out.println("");
        System.out.println("maxStart="+g.getMaxStart());
        System.out.println("anyNodeStart:");
        for (long l : g.getAnyNodeStart()) {
            System.out.print(" "+l);
        }
        System.out.println("");
        System.out.println("startToNode:");
        Map<Long,Integer> startToNode =  g.getStartToNode();
        for (long l : startToNode.keySet()) {
            System.out.println(l+":"+startToNode.get(l));
        }
        int[][] neighbor = g.getNeighbor();
        for (int i=0; i<g.getNumNodes(); i++) {
            System.out.print(i+" neighbors:");
            for (int j=0; j<neighbor[i].length; j++) {
                System.out.print(" "+neighbor[i][j]);
            }
            System.out.println("");
        }

        // System.out.println("nodePaths:");
        // Map<Integer,TreeSet<Integer>> nodePaths = g.getNodePaths();
        // for (Integer i : nodePaths.keySet()) {
        //     System.out.print(i);
        //     Set<Integer> paths = nodePaths.get(i);
        //     for (Integer p : paths) {
        //         System.out.print("\t"+p);
        //     }
        //     System.out.println("");
        // }

    }

}
