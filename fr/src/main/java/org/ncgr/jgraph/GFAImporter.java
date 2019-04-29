package org.ncgr.jgraph;

import org.jgrapht.io.*;

public class GFAImporter implements GraphImporter {

    /**
     * Read in from a GFA file as output from vg view --gfa.
     *
     * http://gfa-spec.github.io/GFA-spec/GFA1.html
     *
     * Paths are assumed to be on a single P line per genotype:
     * P	_thread_714413_4_0_0	1+,3+,4+,17+,18+,20+,21+,...,83+,85+,86+	42M,1M,153M,...,1M,85M,1M,161M
     * P	_thread_714413_4_1_0	1+,3+,4+,6+,18+,20+,21+,....,83+,85+,86+	42M,1M,153M,...,1M,85M,1M,161M
     *
     * NOTE: with unphased calls, genotype 0 is the ALT genotype (presuming vg index --force-phasing was used).
     * 
     * @param gfaFile the full path name of the GFA file to parse
     */
    // public void readVgGfaFile(String gfaFile) throws IOException {
    //     this.gfaFile = gfaFile;
    //     if (verbose) System.out.println("Reading GFA file: "+gfaFile);
    //     // store the node lists by path name so we can update them
    //     Map<String,LinkedList<Node>> nodeListMap = new HashMap<>();
    //     BufferedReader reader = null;
    //     try {
    //         reader = new BufferedReader(new FileReader(gfaFile));
    //     } catch (FileNotFoundException e) {
    //         System.err.println(e);
    //         System.exit(1);
    //     }
    //     String line = null;
    //     while ((line=reader.readLine())!=null) {
    //         String[] parts = line.split("\t");
    //         String recordType = parts[0];
    //         if (recordType.equals("H")) {
    //             // Header line
    //             String header = parts[1];
    //             if (verbose) System.out.println(header);
    //         } else if (recordType.equals("S")) {
    //             // Segment line -- gives node id and sequence
    //             long nodeId = Long.parseLong(parts[1]);
    //             String sequence = parts[2];
    //             Node node = new Node(nodeId, sequence);
    //             nodes.put(nodeId, node);
    //         } else if (recordType.equals("P") && parts.length==4) {
    //             // Path line
    //             // Sometimes path entries do not contain any nodes, so required parts.length==4.
    //             // Paths have names of the form _thread_sample_chr_genotype_index where genotype=0,1
    //             // and sample often has "_" in it. The "_"-split pieces will therefore be:
    //             // 0:"", 1:"thread", 2:sample1, 3:sample2, L-4:sampleN, L-3:chr, L-2:genotype, L-1:idx where L is the number of pieces,
    //             // and sample contains N parts separated by "_".
    //             String name = parts[1];
    //             String[] pieces = name.split("_");
    //             if (pieces.length>=6 && pieces[1].equals("thread")) {
    //                 // build pathName, assuming it may have underscores
    //                 String pathName = pieces[2];
    //                 for (int i=3; i<pieces.length-3; i++) pathName += "_"+pieces[i]; // for (common) cases where samples have underscores
    //                 // genotype
    //                 int gtype = Integer.parseInt(pieces[pieces.length-2]); // 0, 1
    //                 // path segment index
    //                 int idx = Integer.parseInt(pieces[pieces.length-1]);
    //                 // append the genotype to pathName
    //                 pathName += ":"+gtype;
    //                 // build/append this path's node list
    //                 if (genotype==BOTH_GENOTYPES || gtype==genotype) {
    //                     LinkedList<Node> newNodeList = new LinkedList<>();
    //                     String[] nodeStrings = parts[2].split(","); // e.g. 27+,29+,30+
    //                     for (String nodeString : nodeStrings) {
    //                         // NOTE: we're assuming no reverse-complement entries, which would have minus sign!
    //                         long nodeId = Long.parseLong(nodeString.replace("+",""));
    //                         newNodeList.add(nodes.get(nodeId)); // nodes should already be loaded from S lines above P lines
    //                     }
    //                     if (nodeListMap.containsKey(pathName)) {
    //                         LinkedList<Node> nodeList = nodeListMap.get(pathName);
    //                         nodeList.addAll(newNodeList);
    //                         nodeListMap.put(pathName, nodeList);
    //                     } else {
    //                         nodeListMap.put(pathName, newNodeList);
    //                     }
    //                 }
    //             }
    //         }
    //     }
    //     reader.close();

    //     // build the paths from the nodeListMap
    //     for (String pathName : nodeListMap.keySet()) {
    //         LinkedList<Node> nodeList = nodeListMap.get(pathName);
    //         String[] parts = pathName.split(":"); // separate out the genotype
    //         String name = parts[0];
    //         int genotype = Integer.parseInt(parts[1]);
    //         paths.add(new Path(name, genotype, nodeList));
    //     }

    //     // build the graph
    //     for (Node node : nodes.values()) {
    //         addVertex(node);
    //     }
    //     for (Path path : paths) {
    //         boolean first = true;
    //         Node lastNode = null;
    //         for (Node node : path.nodes) {
    //             if (first) {
    //                 first = false;
    //             } else {
    //                 addEdge(lastNode, node, new Edge(path));
    //             }
    //             lastNode = node;
    //         }
    //     }

    //     // build the path sequences from their nodes (populated above)
    //     // buildPathSequences();

    //     // build the node paths
    //     // buildNodePaths();
    
}
