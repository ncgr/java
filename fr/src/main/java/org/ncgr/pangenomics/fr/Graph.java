package org.ncgr.pangenomics.fr;

import java.io.BufferedReader;
import java.io.FileReader;

import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.TreeSet;

/**
 * Storage of a graph.
 *
 * @author bmumey
 */ 
public class Graph {

    static int BUFSIZE = 10000;

    String dotFile;
    
    String name;
    int numNodes;
    int[][] neighbor;
    long[] anyNodeStart;
    long maxStart;
    int[] length;
    boolean[] containsN;
    int K;
    int minLen = Integer.MAX_VALUE;

    boolean verbose = false;

    Map<Long,Integer> startToNode;
    Map<Integer,TreeSet<Integer>> nodePaths;

    /**
     * Constructor does nothing; use read methods to populate the graph.
     */
    public Graph() {
    }

    /**
     * Read a Graph in from a DOT file.
     */
    public void readDotFile(String filename) {

        this.dotFile = filename;
        if (verbose) System.out.println("Reading dot file: " + filename);

        startToNode = new TreeMap<Long,Integer>();
        maxStart = 0;

        Map<Integer,Set<Integer>> nodeNeighbors = new TreeMap<Integer,Set<Integer>>();
        Map<Integer,Long> anyNStarts = new TreeMap<Integer,Long>();
        Map<Integer,Integer> nodeLength = new TreeMap<Integer,Integer>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            char[] c = new char[(2 * BUFSIZE)];
            br.read(c, 0, BUFSIZE);
            int r = 0, pos = 0, par = 1;
            long v = 0;
            boolean innumber = false;
            boolean readfirst = false;
            boolean labelline = false;
            boolean colon = false;
            int firstN = -1;
            while (true) {
                if (c[pos] >= '0' && c[pos] <= '9') {
                    v = 10 * v + (c[pos] - '0');
                    innumber = true;
                } else {
                    if (innumber) {
                        if (!readfirst) {
                            firstN = (int) v;
                            readfirst = true;
                        } else if (labelline && !colon) {
                            if (!anyNStarts.containsKey(firstN)) {
                                anyNStarts.put(firstN, v);
                            }
                            startToNode.put(v, firstN);
                            maxStart = Math.max(maxStart, v);
                        } else if (labelline && colon) {
                            nodeLength.put(firstN, (int) v);
                            nodeNeighbors.put(firstN, new TreeSet<Integer>());
                            minLen = Math.min(minLen, (int) v);

                            if (firstN % 50000 == 0) {
                                if (verbose) System.out.println("Reading node: " + firstN);
                            }
                        } else if (!labelline) {
                            nodeNeighbors.get(firstN).add((int) v);
                        }
                        v = 0;
                        innumber = false;
                    }
                    if (c[pos] == '[') {
                        labelline = true;
                    }
                    if (c[pos] == ':') {
                        colon = true;
                    }
                    if (c[pos] == '\n') {
                        readfirst = false;
                        labelline = false;
                        colon = false;
                    }
                    if (c[pos] == '}') {
                        break;
                    }
                }

                int mid = BUFSIZE * par;
                int end = (mid + BUFSIZE) % (2 * BUFSIZE);
                pos = (pos + 1) % (2 * BUFSIZE);
                if (pos == mid) {
                    r = br.read(c, mid, BUFSIZE);
                    par = 1 - par;
                }
            }

            br.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }

        if (verbose) System.out.println("K = " + minLen);
        K = minLen;
        numNodes = nodeNeighbors.keySet().size();
        if (verbose) System.out.println("number of nodes: " + numNodes);
        neighbor = new int[numNodes][];
        for (int i = 0; i < neighbor.length; i++) {
            neighbor[i] = new int[nodeNeighbors.get(i).size()];
            int j = 0;
            for (Integer jobj : nodeNeighbors.get(i)) {
                neighbor[i][j++] = jobj;
            }
            nodeNeighbors.get(i).clear();
        }
        nodeNeighbors.clear();

        anyNodeStart = new long[numNodes];
        for (int i = 0; i < neighbor.length; i++) {
            anyNodeStart[i] = anyNStarts.get(i);
        }
        anyNStarts.clear();
        length = new int[numNodes];
        for (int i = 0; i < neighbor.length; i++) {
            length[i] = nodeLength.get(i);
        }
        nodeLength.clear();
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
     * Find node paths corresponding to FASTA paths.
     */
    public void findNodePaths(int[][] paths, TreeSet<Long> Nlocs) {
        containsN = new boolean[numNodes];
        for (int i = 0; i < numNodes; i++) {
            containsN[i] = false;
            Long test = Nlocs.ceiling(anyNodeStart[i]);
            if (test != null && test.longValue() < anyNodeStart[i] + length[i]) {
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
    }

    // getters
    public String getDotFile() {
        return dotFile;
    }
    public int getMinLen() {
        return minLen;
    }
    public int getK() {
        return K;
    }
    public Map<Long,Integer> getStartToNode() {
        return startToNode;
    }
    public Map<Integer,TreeSet<Integer>> getNodePaths() {
        return nodePaths;
    }

}
