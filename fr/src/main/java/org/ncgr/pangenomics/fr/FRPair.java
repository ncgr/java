package org.ncgr.pangenomics.fr;

import java.util.List;
import java.util.LinkedList;
import java.util.Set;

import org.ncgr.jgraph.Edge;
import org.ncgr.jgraph.Node;
import org.ncgr.jgraph.NodeSet;
import org.ncgr.jgraph.PangenomicGraph;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

/**
 * Container for a pair of FrequentedRegions and their merged result with a comparator for ranking it.
 * This is where one implements weighting of certain desired FR characteristics.
 */
public class FRPair implements Comparable<FRPair> {

    FrequentedRegion fr1;
    FrequentedRegion fr2;
    PangenomicGraph graph;
    double alpha;
    int kappa;
    boolean kappaByNodes;
    boolean caseCtrl;

    NodeSet nodes;
    FrequentedRegion merged;

    boolean alphaReject;
    
    FRPair(FrequentedRegion fr1, FrequentedRegion fr2, PangenomicGraph graph, double alpha, int kappa, boolean kappaByNodes, boolean caseCtrl) {
        this.fr1 = fr1;
        this.fr2 = fr2;
        this.graph = graph;
        this.alpha = alpha;
        this.kappa = kappa;
        this.kappaByNodes = kappaByNodes;
        this.caseCtrl = caseCtrl;
        this.nodes = NodeSet.merge(fr1.nodes, fr2.nodes);
        // nothing to do if an identity merge
        if (fr1.equals(fr2)) this.merged = fr1;
    }

    /**
     * Compute rejection booleans without merging.
     */
    public void computeRejection() {
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
            alphaReject = minMissing>(int)(alpha*nodes.size());
        }
    }
    
    /**
     * Algorithm 2 from Cleary, et al. returns the supporting path segments for the given merge of FRs.
     * @returns the set of supporting path segments
     */
    public void merge() {
        merged = new FrequentedRegion(graph, NodeSet.merge(fr1.nodes,fr2.nodes), alpha, kappa, kappaByNodes);
    }

    /**
     * Two FRPairs are equal if their components are equal.
     */
    public boolean equals(FRPair that) {
        return (this.fr1.equals(that.fr1) && this.fr2.equals(that.fr2)) || (this.fr1.equals(that.fr2) && this.fr2.equals(that.fr1));
    }

    /**
     * A comparator for PriorityQueue use -- note that it is the opposite of normal comparison because
     * PriorityQueue allows you to take the top (least) object with peek() but not the bottom (most) object.
     *
     * caseCtrl: balanced case vs. control difference; else default.
     * default: support, avgLength, nodes.size()
     */
    public int compareTo(FRPair that) {
        if (this.equals(that)) return 0;
        if (caseCtrl) {
            int thisDifference = this.merged.caseControlDifference();
            int thatDifference = that.merged.caseControlDifference();
            if (thisDifference!=thatDifference) return thatDifference - thisDifference;
        }
        // default: total support then avgLength then size
        if (that.merged.support!=this.merged.support) {
            return Integer.compare(that.merged.support, this.merged.support);
        } else if (that.merged.avgLength!=this.merged.avgLength) {
            return Double.compare(that.merged.avgLength, this.merged.avgLength);
        } else {
            return Integer.compare(that.merged.nodes.size(), this.merged.nodes.size());
        }
    }

    /**
     * Reader-friendly string summary.
     */
    public String toString() {
        return nodes.toString();
        // return "fr1="+fr1.toString()+";fr2="+fr2.toString()+";merged.support="+merged.support+";merged.avgLength="+merged.avgLength+";merged.nodes.size="+merged.nodes.size();
    }
}
