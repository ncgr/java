package org.ncgr.pangenomics.fr;

import java.util.List;
import java.util.LinkedList;
import java.util.Set;

import org.ncgr.jgraph.Edge;
import org.ncgr.jgraph.Node;
import org.ncgr.jgraph.NodeSet;
import org.ncgr.jgraph.NullNodeException;
import org.ncgr.jgraph.NullSequenceException;
import org.ncgr.jgraph.PangenomicGraph;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

/**
 * Container for a pair of FrequentedRegions and their merged result with a comparator for ranking it.
 * This is where one implements weighting of certain desired FR characteristics.
 */
public class FRPair implements Comparable {

    FrequentedRegion fr1;
    FrequentedRegion fr2;
    PangenomicGraph graph;
    double alpha, oneMinusAlpha;
    int kappa;
    int priorityOption;

    NodeSet nodes;
    FrequentedRegion merged;

    boolean alphaReject;
    
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
        this.oneMinusAlpha = 1.0 - alpha;
        this.nodes = NodeSet.merge(fr1.nodes, fr2.nodes);
        // nothing to do if an identity merge
        if (fr1.equals(fr2)) this.merged = fr1;
    }

    /**
     * Compute alpha rejection booleans without merging.
     */
    public void computeAlphaRejection() {
        // defaults
        alphaReject = false;
        if (nodes.size()>1) {
            DijkstraShortestPath<Node,Edge> dsp = new DijkstraShortestPath<Node,Edge>(graph);
            int minMissing = Integer.MAX_VALUE;
            for (Node n1 : nodes) {
                for (Node n2 : nodes) {
                    if (n1.getId()<n2.getId()) {
                        GraphPath<Node,Edge> subpath = dsp.getPath(n1, n2);
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
     * @returns the set of supporting path segments
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
     * Reader-friendly string summary.
     */
    public String toString() {
        // return nodes.toString();
        return "fr1="+fr1.toString()+";fr2="+fr2.toString()+";merged.support="+merged.support+";merged.avgLength="+merged.avgLength+";merged.nodes.size="+merged.nodes.size();
    }
}
