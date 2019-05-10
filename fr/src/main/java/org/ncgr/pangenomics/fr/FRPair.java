package org.ncgr.pangenomics.fr;

import java.util.List;

import org.ncgr.jgraph.Edge;
import org.ncgr.jgraph.PangenomicGraph;
import org.ncgr.jgraph.PathWalk;

import org.ncgr.pangenomics.Node;
import org.ncgr.pangenomics.NodeSet;

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
    boolean caseCtrl;
    int support;
    int size;
    double avgLength;

    FrequentedRegion merged;
    
    FRPair(FrequentedRegion fr1, FrequentedRegion fr2, PangenomicGraph graph, double alpha, int kappa, boolean caseCtrl) {
        this.fr1 = fr1;
        this.fr2 = fr2;
        this.graph = graph;
        this.alpha = alpha;
        this.kappa = kappa;
        this.caseCtrl = caseCtrl;
        // we should calculate support without merging!
        support = 0;
        // DEBUG
        merged = merge();
        if (!fr1.equals(fr2)) {
            System.out.println(fr1.nodes.toString()+fr2.nodes.toString());
            DijkstraShortestPath<Node,Edge> dsp = new DijkstraShortestPath<Node,Edge>(graph);
            int minMissing = Integer.MAX_VALUE;
            for (Node n1 : fr1.nodes) {
                for (Node n2 : fr2.nodes) {
                    if (!n1.equals(n2)) {
                        GraphPath<Node,Edge> path = dsp.getPath(n1, n2);
                        if (path!=null) {
                            List<Node> nodeList = path.getVertexList();
                            int missing = 0;
                            System.out.print(n1.getId()+"-"+n2.getId()+":");
                            for (Node n : nodeList) {
                                if (!fr1.nodes.contains(n) && !fr2.nodes.contains(n)) missing++;
                                System.out.print(" "+n.getId());
                            }
                            System.out.println(" missing="+missing);
                            if (missing<minMissing) minMissing = missing;
                        }
                    }
                }
            }
            System.out.println("MIN MISSING="+minMissing);
        }
    }

    /**
     * Algorithm 2 from Cleary, et al. returns the supporting path segments for the given merge of FRs.
     * @returns the set of supporting path segments
     */
    public FrequentedRegion merge() {
        return new FrequentedRegion(graph, NodeSet.merge(fr1.nodes,fr2.nodes), alpha, kappa);
    }
    
    /**
     * Two FRPairs are equal if their components are equal.
     */
    public boolean equals(FRPair that) {
        return (this.fr1.equals(that.fr1) && this.fr2.equals(that.fr2)) ||
            (this.fr1.equals(that.fr2) && this.fr2.equals(that.fr1));
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
        // if (caseCtrl) {
        //     int thisDifference = this.merged.caseControlDifference();
        //     int thatDifference = that.merged.caseControlDifference();
        //     if (thisDifference!=thatDifference) return thatDifference - thisDifference;
        // }
        // default: total support then avgLength then size
        if (that.support!=this.support) {
            return Integer.compare(that.support, this.support);
        } else if (that.avgLength!=this.avgLength) {
            return Double.compare(that.avgLength, this.avgLength);
        } else {
            return Integer.compare(that.size, this.size);
        }
    }
}
