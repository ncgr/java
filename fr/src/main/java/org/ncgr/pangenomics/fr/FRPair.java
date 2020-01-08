package org.ncgr.pangenomics.fr;

import java.util.List;
import java.util.LinkedList;
import java.util.Set;

import org.ncgr.pangenomics.Edge;
import org.ncgr.pangenomics.Node;
import org.ncgr.pangenomics.NodeSet;
import org.ncgr.pangenomics.NullNodeException;
import org.ncgr.pangenomics.NullSequenceException;
import org.ncgr.pangenomics.PangenomicGraph;

import org.jgrapht.GraphPath;

/**
 * Container for a pair of FrequentedRegions and their merged result with a comparator for ranking it.
 * This is where one implements weighting of certain desired FR characteristics.
 */
public class FRPair implements Comparable {
    FrequentedRegion fr1;
    FrequentedRegion fr2;
    FrequentedRegion merged;
    PangenomicGraph graph;

    double alpha;
    int kappa;
    String priorityOption;
    boolean alphaReject;

    NodeSet nodes;
    double oneMinusAlpha;
    
    /**
     * Construct from a pair of FrequentedRegions and run parameters.
     */
    FRPair(FrequentedRegion fr1, FrequentedRegion fr2) {
        this.fr1 = fr1;
        this.fr2 = fr2;
        this.graph = fr1.graph;
        this.alpha = fr1.alpha;
        this.kappa = fr1.kappa;
        this.priorityOption = fr1.priorityOption;
        oneMinusAlpha = 1.0 - alpha;
        nodes = NodeSet.merge(fr1.nodes, fr2.nodes);
        // nothing to do if an identity merge
        if (fr1.equals(fr2)) merged = fr1;
    }

    /**
     * Construct from an FRPair.toString() line
     * [15] 1 1 66.00 1 0 0 ∞ 1.000E0;[2,5] 2 12 34.33 12 0 122 ∞ 6.041E-2;
     */
    FRPair(String line, PangenomicGraph graph, double alpha, int kappa, String priorityOption, boolean alphaReject) throws NullNodeException, NullSequenceException {
        String[] parts = line.split(";");
        fr1 = new FrequentedRegion(graph, parts[0], alpha, kappa, priorityOption);
        fr2 = new FrequentedRegion(graph, parts[1], alpha, kappa, priorityOption);
        merged = new FrequentedRegion(graph, parts[3], alpha, kappa, priorityOption);
        this.alpha = alpha;
        this.kappa = kappa;
        this.priorityOption = priorityOption;
        this.alphaReject = alphaReject;
        nodes = merged.getNodes();
        oneMinusAlpha = 1.0 - alpha;
    }

    /**
     * Compute alpha rejection booleans without merging.
     */
    public void computeAlphaRejection() {
        alphaReject = false;
        if (nodes.size()>1) {
            int minMissing = Integer.MAX_VALUE;
            for (Node n1 : nodes) {
                for (Node n2 : nodes) {
                    if (n1.getId()<n2.getId()) {
                        GraphPath<Node,Edge> subpath = graph.getDSP().getPath(n1, n2);
                        if (subpath!=null) {
                            List<Node> missingNodes = new LinkedList<>();
                            for (Node n : subpath.getVertexList()) {
                                if (!nodes.contains(n)) {
                                    missingNodes.add(n);
                                }
                            }
                            if (missingNodes.size()<minMissing) minMissing = missingNodes.size();
                        }
                    }
                }
            }
            alphaReject = minMissing>(int)(nodes.size()*oneMinusAlpha);
        }
    }
    
    /**
     * Algorithm 2 from Cleary, et al. returns the supporting path segments for the given merge of FRs.
     */
    public void merge() throws NullNodeException, NullSequenceException {
        merged = new FrequentedRegion(graph, NodeSet.merge(fr1.nodes,fr2.nodes), alpha, kappa, priorityOption);
    }

    /**
     * Two FRPairs are equal if their components are equal.
     */
    public boolean equals(Object o) {
	FRPair that = (FRPair) o;
        return (this.fr1.equals(that.fr1) && this.fr2.equals(that.fr2)) || (this.fr1.equals(that.fr2) && this.fr2.equals(that.fr1));
    }

    /**
     * A comparator for PriorityQueue use -- note that it is the opposite of normal comparison because
     * PriorityQueue allows you to take the top (least) object with peek() but not the bottom (most) object.
     * Uses the priorty set with the priorityOption value.
     */
    @Override
    public int compareTo(Object o) {
	FRPair that = (FRPair) o;
        // DEBUG: NORMAL COMPARISON
        return this.merged.compareTo(that.merged);
    }

    /**
     * Reader-friendly string summary, just three FRs separated by semicolons.
     */
    public String toString() {
        return fr1.toString()+";" +
            fr2.toString()+";" +
            merged.toString();
    }
}
