package org.ncgr.pangenomics.fr;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * @author bmumey
 * @author Sam Hokin
 */
public class FRFinder {

    Graph g;
    FastaFile f;
    
    List<ClusterEdge> edgeL;
    Map<Integer,ClusterNode> nodeCluster;

    PriorityBlockingQueue<ClusterNode> iFRQ;

    int numClusterNodes = 0;

    // required parameters, set in constructor
    double alpha = 0.7;    // penetrance: the fraction of a supporting strain's sequence that actually supports the FR; alternatively, `1-alpha` is the fraction of inserted sequence
    int kappa = 0;         // maximum insertion: the maximum insertion length (measured in bp) that any supporting path may have
    boolean useRC = false; // indicates if the sequence (e.g. FASTA file) had its reverse complement appended

    // optional parameters, set with set methods
    int minSup = 1;        // minimum support: minimum number of genome paths in order for a region to be considered frequent
    int minSize = 1;       // minimum size: minimum number of de Bruijn nodes that an FR must contain to be considered frequent

    /**
     * Construct with a given Graph, FastaFile and parameters
     */
    public FRFinder(Graph g, FastaFile f, double alpha, int kappa, boolean useRC) {
        // set the instance vars
        this.g = g;
        this.f = f;
        this.alpha = alpha;
        this.kappa = kappa;
        this.useRC = useRC;
    }

    /**
     * Compute the support for each frequented region.
     */
    public List<PathSegment> computeSupport(ClusterNode clust, boolean createPSList, boolean findAvgLen) {
        if (clust.pathLocs == null) {
            clust.findPathLocs();
        }
        List<PathSegment> segList = new ArrayList<PathSegment>();
        int fSup = 0;
        int rSup = 0;
        int supLen = 0;
        for (Integer P : clust.pathLocs.keySet()) {
            int[] locs = clust.pathLocs.get(P);
            int start = 0;
            while (start < locs.length) {
                int last = start;
                while (last + 1 < locs.length
                       && ((locs[last + 1] == locs[last] + 1)
                           || (kappa > 0 && g.findGap(f.paths[P], locs[last], locs[last + 1]) <= kappa))) {
                    last++;
                }
                if (last - start + 1 >= alpha * clust.size) {
                    if (!useRC || 2*P < f.paths.length) {
                        fSup++;
                    }
                    if (useRC && 2*P >= f.paths.length) {
                        rSup++;
                    }
                    if (createPSList) {
                        segList.add(new PathSegment(P, locs[start], locs[last]));
                    }
                    if (findAvgLen) {
                        long[] startStop = f.findLoc(P, locs[start], locs[last]);
                        int len = (int) (startStop[1] - startStop[0]); // last pos is exclusive
                        supLen += len;
                    }
                }
                start = last + 1;
            }
        }
        clust.fwdSup = fSup;
        clust.rcSup = rSup;
        if (findAvgLen && clust.fwdSup + clust.rcSup > 0) {
            clust.avgLen = supLen / (clust.fwdSup + clust.rcSup);
        }
        if (createPSList) {
            return segList;
        }
        return null;
    }

    /**
     * Find the frequented regions.
     */
    public void findFRs() {
        System.out.println("Creating node clusters...");
        nodeCluster = new ConcurrentHashMap<Integer,ClusterNode>(g.numNodes);

        // create initial node clusters
        g.nodePaths.keySet().parallelStream().forEach((N) -> {
                if (!g.nodePaths.get(N).isEmpty()
                    && (!useRC || 2*g.nodePaths.get(N).first() < f.paths.length)) { // only start with nodes from non-rc'ed paths
                    ClusterNode nodeClst = new ClusterNode();
                    nodeClst.parent = nodeClst.left = nodeClst.right = null;
                    nodeClst.node = N;
                    nodeClst.size = 1;
                    nodeClst.findPathLocs();
                    nodeClst.neighbors = new ConcurrentHashMap<ClusterNode,ClusterEdge>();
                    nodeCluster.put(N, nodeClst);
                }
            });
        Map<Integer,Map<Integer,List<Integer>>> nodePathLocs = new TreeMap<Integer,Map<Integer,List<Integer>>>();
        for (int p = 0; p < f.paths.length; p++) {
            for (int i = 0; i < f.paths[p].length; i++) {
                int n = f.paths[p][i];
                if (!nodePathLocs.containsKey(n)) {
                    nodePathLocs.put(n, new TreeMap<Integer,List<Integer>>());
                }
                if (!nodePathLocs.get(n).containsKey(p)) {
                    nodePathLocs.get(n).put(p, new ArrayList<Integer>());
                }
                nodePathLocs.get(n).get(p).add(i);
            }
        }
        for (Integer node : nodePathLocs.keySet()) {
            for (Integer P : nodePathLocs.get(node).keySet()) {
                List<Integer> al = nodePathLocs.get(node).get(P);
                int[] ar = new int[al.size()];
                int i = 0;
                for (Integer x : al) {
                    ar[i++] = x;
                }
                if (nodeCluster.containsKey(node)) {
                    nodeCluster.get(node).pathLocs.put(P, ar);
                }
            }
        }
        nodePathLocs.clear();

        System.out.println("Computing node support...");
        for (ClusterNode c : nodeCluster.values()) {
            computeSupport(c, false, false);
        }
        // create initial edges
        edgeL = new ArrayList<ClusterEdge>();
        Set<ClusterNode> checkNodes = ConcurrentHashMap.newKeySet();
        for (Integer N : nodeCluster.keySet()) {
            for (int i = 0; i < g.neighbor[N].length; i++) {
                if (nodeCluster.containsKey(g.neighbor[N][i])) {
                    ClusterNode u = nodeCluster.get(N);
                    ClusterNode v = nodeCluster.get(g.neighbor[N][i]);
                    if (!u.neighbors.containsKey(v)) {
                        ClusterEdge e = new ClusterEdge(u, v, -1, 0);
                        edgeL.add(e);
                        u.neighbors.put(v, e);
                        v.neighbors.put(u, e);
                    }
                }
            }
        }
        List<ClusterEdge> edgeM = new ArrayList<ClusterEdge>();

        do {
            System.out.println("# of edges: " + edgeL.size());
            edgeL.parallelStream().forEach((E) -> {
                    if (E.fwdSup < 0) {
                        ClusterNode tmpClst = new ClusterNode();
                        tmpClst.left = E.u;
                        tmpClst.right = E.v;
                        tmpClst.parent = null;
                        tmpClst.size = E.u.size + E.v.size;
                        computeSupport(tmpClst, false, false);
                        tmpClst.pathLocs.clear();
                        E.fwdSup = tmpClst.fwdSup;
                        E.rcSup = tmpClst.rcSup;
                        checkNodes.add(E.u);
                        checkNodes.add(E.v);
                    }
                });
            checkNodes.parallelStream().forEach((u) -> {
                    u.bestNsup = -1;
                    for (ClusterNode v : u.neighbors.keySet()) {
                        ClusterEdge E = u.neighbors.get(v);
                        if (E.sup() > u.bestNsup) {
                            u.bestNsup = E.sup();
                        }
                    }
                });
            checkNodes.clear();
            edgeM.clear();
            for (ClusterEdge E : edgeL) {
                if (E.sup() > 0 && E.sup() == E.u.bestNsup && E.sup() == E.v.bestNsup) {
                    edgeM.add(E);
                    E.u.bestNsup = -1;
                    E.v.bestNsup = -1;
                }
            }
            System.out.println("matching size: " + edgeM.size());
            edgeM.parallelStream().forEach((E) -> {
                    ClusterNode newRoot = new ClusterNode();
                    newRoot.node = -(numClusterNodes++);
                    newRoot.left = E.u;
                    newRoot.right = E.v;
                    newRoot.parent = null;
                    newRoot.size = newRoot.left.size + newRoot.right.size;
                    newRoot.left.parent = newRoot;
                    newRoot.right.parent = newRoot;
                    newRoot.fwdSup = E.fwdSup;
                    newRoot.rcSup = E.rcSup;
                    newRoot.neighbors = new ConcurrentHashMap<ClusterNode, ClusterEdge>();
                    newRoot.findPathLocs();
                });
            for (ClusterEdge E : edgeM) {
                ClusterNode newClst = E.u.parent;
                for (ClusterNode n : newClst.left.neighbors.keySet()) {
                    if (n != newClst.right) {
                        ClusterEdge e = newClst.left.neighbors.get(n);
                        e.u = newClst;
                        e.fwdSup = -1;
                        e.v = n;
                        newClst.neighbors.put(n, e);
                    }
                }
                for (ClusterNode n : newClst.right.neighbors.keySet()) {
                    if (n != newClst.left) {
                        ClusterEdge e = newClst.right.neighbors.get(n);
                        if (newClst.neighbors.containsKey(n)) {
                            edgeL.remove(e);
                        } else {
                            e.u = newClst;
                            e.fwdSup = -1;
                            e.v = n;
                            newClst.neighbors.put(n, e);
                        }
                    }
                }
                for (ClusterNode n : newClst.neighbors.keySet()) {
                    ClusterEdge e = newClst.neighbors.get(n);
                    n.neighbors.put(newClst, e);
                    n.neighbors.remove(newClst.left);
                    n.neighbors.remove(newClst.right);
                }
                newClst.left.neighbors.clear();
                newClst.right.neighbors.clear();
                edgeL.remove(E);
            }
        } while (!edgeM.isEmpty());

        Set<ClusterNode> roots = ConcurrentHashMap.newKeySet();
        nodeCluster.values().parallelStream().forEach((leaf) -> {
                roots.add(leaf.findRoot());
            });

        iFRQ = new PriorityBlockingQueue<ClusterNode>();

        System.out.println("number of root FRs: " + roots.size());
        roots.parallelStream().forEach((root) -> {
                reportIFRs(root, 0);
            });

        System.out.println("number of iFRs: " + iFRQ.size());
    }

    void reportIFRs(ClusterNode clust, int parentSup) {
        if ((clust.fwdSup + clust.rcSup) > parentSup
            && (clust.fwdSup + clust.rcSup) >= minSup
            && clust.fwdSup >= clust.rcSup) {
            computeSupport(clust, false, true);
            iFRQ.add(clust);
        }
        if (clust.left != null) {
            reportIFRs(clust.left, Math.max(parentSup, clust.fwdSup + clust.rcSup));
        }
        if (clust.right != null) {
            reportIFRs(clust.right, Math.max(parentSup, clust.fwdSup + clust.rcSup));
        }
    }

    // getters for required parameters
    public double getAlpha() {
        return alpha;
    }
    public int getKappa() {
        return kappa;
    }
    public boolean getUseRC() {
        return useRC;
    }

    // setters/getters for optional parameters
    public void setMinSup(int minSup) {
        this.minSup = minSup;
    }
    public void setMinSize(int minSize) {
        this.minSize = minSize;
    }
    public int getMinSup() {
        return minSup;
    }
    public int getMinSize() {
        return minSize;
    }

    // getters for instance objects
    public Graph getGraph() {
        return g;
    }
    public FastaFile getFastaFile() {
        return f;
    }
    public PriorityBlockingQueue<ClusterNode> getIFRQ() {
        return iFRQ;
    }

}
