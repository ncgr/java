package org.ncgr.pangenomics;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * Some handy tools for working with graph output files.
 *
 * 3q29.labelcounts.txt
 * 3q29.nodehistogram.txt
 * 3q29.nodepaths.txt
 *
 * 3q29.nodes.txt
 * 1 ACGTGT......CAT
 * ...
 * 1234 GT
 *
 * 3q29.pathpca.txt
 * 3q29.pathsequences.fasta
 *
 * 3q29.paths.txt
 * 28278.0 case    5722432 1       2       3       4       5       ...
 */
public class GraphTools {

    /**
     * Command-line utility
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        if (args.length<2) {
            System.out.println("GraphTools nodeSequences [filePrefix] [startingNodeId] [endingNodeId]\tprints out path sequences corresponding to a range of nodes");
            System.exit(0);
        }
        
        String task = args[0];
        boolean nodeSequences = task.equals("nodeSequences");

        if (nodeSequences) {
            if (args.length!=4) {
                System.err.println("Usage: GraphTools nodeSequences [filePrefix] [startingNodeId] [endingNodeId]");
                System.exit(1);
            }
            String filePrefix = args[1];
            long startingNodeId = Long.parseLong(args[2]);
            long endingNodeId = Long.parseLong(args[3]);
            
            // read in the nodes
            Map<Long,Node> nodes = Node.readFromFile(filePrefix+".nodes.txt");

            // read in the paths -- which lack node sequences, but we don't need them there
            Map<String,Path> paths = Path.readFromFile(filePrefix+".paths.txt");

            // build each path's sequence within the given node range
            for (String nameGenotype : paths.keySet()) {
                Path p = paths.get(nameGenotype);
                String sequence = "";
                List<Long> nodeIdList = new ArrayList<>();
                System.out.print(">"+nameGenotype+" ");
                if (p.containsNode(nodes.get(startingNodeId)) && p.containsNode(nodes.get(endingNodeId))) {
                    for (long id=startingNodeId; id<=endingNodeId; id++) {
                        Node n = nodes.get(id);
                        if (p.containsNode(n)) {
                            nodeIdList.add(id);
                            sequence += n.getSequence();
                        }
                    }
                }
                System.out.println(nodeIdList);
                System.out.println(sequence);
            }
        }
    }
}
