package org.ncgr.jgraph;

import org.jgrapht.Graph;
import org.jgrapht.io.GraphImporter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/**
 * Importer for TXT files [graph].nodes.txt and [graph].paths.txt.
 *
 * @author Sam Hokin
 */
public class TXTImporter {

    // genotype preference (default: -1=load all genotypes)
    public static int BOTH_GENOTYPES = -1;
    public int genotype = BOTH_GENOTYPES;

    // verbosity flag
    private boolean verbose = false;

    // skip-edges flag (to speed things up on big graphs)
    private boolean skipEdges = false;

    // skip-sequences flag (to reduce memory)
    private boolean skipSequences = false;

    // we keep track of the genomic paths here since it's the only place we follow them
    List<PathWalk> paths;

    /**
     * Import a Graph from a pair of nodes and paths text files.
     *
     * @param g the graph to load
     * @param nodesFile the nodes file (typically [graph].nodes.txt)
     * @param pathsFile the paths file (typically [graph].paths.txt)
     */
    public void importGraph(Graph<Node,Edge> g, File nodesFile, File pathsFile) throws IOException, NullSequenceException {
        if (verbose) System.out.println("Loading graph from "+nodesFile.getName()+" and "+pathsFile.getName());

        // read the nodes, storing in a map for path building
        if (verbose) System.out.print("Reading nodes...");
        Map<Long,Node> nodeMap = new HashMap<>();
        BufferedReader nodesReader = new BufferedReader(new FileReader(nodesFile));
        String line = null;
        while ((line=nodesReader.readLine())!=null) {
            String[] parts = line.split("\t");
            long id = Long.parseLong(parts[0]);
            String sequence = parts[1];
            Node n = new Node(id, sequence);
            nodeMap.put(id, n);
            g.addVertex(n);
        }
        nodesReader.close();
        if (verbose) System.out.println("done.");

        // read the paths file lines into a list
        if (verbose) System.out.print("Reading paths...");
        BufferedReader pathsReader = new BufferedReader(new FileReader(pathsFile));
        List<String> lines = Collections.synchronizedList(new ArrayList<>());
        while ((line=pathsReader.readLine())!=null) {
            lines.add(line);
        }
        if (verbose) System.out.println("done.");
        
        // now create the paths in parallel
        if (verbose) System.out.print("Building paths...");
        paths = Collections.synchronizedList(new ArrayList<>());
        lines.parallelStream().forEach(l -> {
                String[] parts = l.split("\t");
                String nameGenotype = parts[0];
                String label = parts[1];
                int sequenceLength = Integer.parseInt(parts[2]);
                String[] pieces = nameGenotype.split("\\.");
                String name = pieces[0];
                int genotype = Integer.parseInt(pieces[1]);
                List<Node> nodeList = new ArrayList<>();
                for (int i=3; i<parts.length; i++) {
                    nodeList.add(nodeMap.get(Long.parseLong(parts[i])));
                }
                PathWalk path = new PathWalk(nodeList, name, genotype, label);
                paths.add(path);
            });
        if (verbose) System.out.println("done.");

        // build the path-labeled graph edges from the paths
        // this can take a long time on a large graph, so skip if skipEdges==true
        // cannot parallelize this because of building the path nodes in order
        if (skipEdges) {
            if (verbose) System.out.println("# Skipped adding edges to graph.");
        } else {
            if (verbose) System.out.print("Adding edges to graph...");
            // this cannot be done in parallel because of g.addEdge() inside
            for (PathWalk path : paths) {
                boolean first = true;
                Node lastNode = null;
                for (Node node : path.getNodes()) {
                    if (!first) g.addEdge(lastNode, node);
                    first = false;
                    lastNode = node;
                }
            }
            if (verbose) System.out.println("done.");
        }
    }

    /**
     * Build the path sequences - just calls PathWalk.buildSequence() for each path.
     */
    void buildPathSequences() {
        if (verbose) System.out.print("Building path sequences...");
        paths.parallelStream().forEach(path -> {
                try {
                    path.buildSequence();
                } catch (NullNodeException e) {
                    System.err.println(e);
                    System.exit(1);
                } catch (NullSequenceException e) {
                    System.err.println(e);
                    System.exit(1);
                }
            });
        if (verbose) System.out.println("done.");
    }

    /**
     * Toggle verbosity on.
     */
    public void setVerbose() {
        verbose = true;
    }

    /**
     * Toggle skipEdges on.
     */
    public void setSkipEdges() {
        skipEdges = true;
    }

    /**
     * Toggle skipSequences on.
     */
    public void setSkipSequences() {
	skipSequences = true;
    }

    /**
     * Set the genotype preference: -1=both; 0 and 1
     */
    public void setGenotype(int g) throws IllegalArgumentException {
        if (g<-1 || g>1) {
            throw new IllegalArgumentException("genotype value must be -1 (both), 0, or 1.");
        } else {
            genotype = g;
        }
    }

    /**
     * Get the paths.
     */
    public List<PathWalk> getPaths() {
        return paths;
    }
}
