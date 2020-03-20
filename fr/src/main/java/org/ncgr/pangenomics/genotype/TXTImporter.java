package org.ncgr.pangenomics.genotype;

import org.jgrapht.Graph;
import org.jgrapht.io.GraphImporter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Importer for TXT files [graph].nodes.txt and [graph].paths.txt containing Nodes and Paths.
 *
 * NOTE: no node or path sequences!!
 *
 * @author Sam Hokin
 */
public class TXTImporter {

    // verbosity flag
    public boolean verbose = false;

    // skip-edges flag (to speed things up on big graphs)
    public boolean skipEdges = false;

    // skip-sequences flag (to reduce memory)
    public boolean skipSequences = false;

    // the Nodes we import
    public List<Node> nodes;

    // map the sample names to the List of Nodes they traverse
    public Map<String,List<Node>> sampleNodesMap;

    // map the Nodes to the list of samples that traverse them
    public Map<Node,List<String>> nodeSamplesMap;

    // map the sample names to their label
    public Map<String,String> sampleLabels;
    
    /**
     * Import a Graph from a pair of nodes and paths text files.
     *
     * node line:
     * 0id    1rs     2contig 3start  4end    5genotype  
     * 12764  rs12345 2       3228938 3229006 CCCACCCCTGCCCTGTCTGGGGCTGAAGTACAGTGCCACCCCTGCCCTGTCTGGGGCTGAAGGACAGTG/C
     *
     * path line:
     * 0name    1label  2...nodes...
     * 642913	case	1	8	17	21	24	25	28	33	35	37 ...
     *
     * @param nodesFile the nodes file (typically [graph].nodes.txt)
     * @param pathsFile the paths file (typically [graph].paths.txt)
     */
    public void read(File nodesFile, File pathsFile) throws IOException {
        // read the nodes, storing in a map for path building
        if (verbose) System.out.print("Reading nodes from TXT file...");
        // instantiate the class collections
        nodes = new ArrayList<>();
        sampleNodesMap = new HashMap<>();
        nodeSamplesMap = new HashMap<>();
        sampleLabels = new HashMap<>();
        // read the nodes file
        Map<Long,Node> nodeMap = new HashMap<>();
        BufferedReader nodesReader = new BufferedReader(new FileReader(nodesFile));
        String line = null;
        while ((line=nodesReader.readLine())!=null) {
            String[] parts = line.split("\t");
            long id = Long.parseLong(parts[0]);
            String rs = parts[1];
            String contig = parts[2];
            int start = Integer.parseInt(parts[3]);
            int end = Integer.parseInt(parts[4]);
            String genotype = parts[5];
            if (rs.equals(".")) rs = null;
            Node n = new Node(id, rs, contig, start, end, genotype);
            nodes.add(n);
            nodeMap.put(id, n);
        }
        nodesReader.close();
        if (verbose) System.out.println("done.");
        // read the paths file
        if (verbose) System.out.print("Reading path lines from TXT file...");
        BufferedReader pathsReader = new BufferedReader(new FileReader(pathsFile));
        ConcurrentSkipListSet<String> lines = new ConcurrentSkipListSet<String>();
        while ((line=pathsReader.readLine())!=null) {
            lines.add(line);
        }
        if (verbose) System.out.println("done.");
        // now build the maps in parallel, since each line contains a distinct sample
        if (verbose) System.out.print("Building sample/node maps...");
        ConcurrentSkipListSet concurrentPaths = new ConcurrentSkipListSet<Path>();
        lines.parallelStream().forEach(l -> {
                String[] parts = l.split("\t");
                String name = parts[0];
                String label = parts[1];
                sampleLabels.put(name, label);
                List<Node> nodeList = new ArrayList<>();
                for (int i=2; i<parts.length; i++) {
                    nodeList.add(nodeMap.get(Long.parseLong(parts[i])));
                }
                sampleNodesMap.put(name, nodeList);
                for (Node n : nodeList) {
                    List<String> nodeSamples = nodeSamplesMap.get(n);
                    if (nodeSamples==null) nodeSamples = new ArrayList<>();
                    nodeSamples.add(name);
                    nodeSamplesMap.put(n, nodeSamples);
                }
            });
        if (verbose) System.out.println("done.");
    }
}
