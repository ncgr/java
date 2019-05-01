package org.ncgr.pangenomics.fr;

import org.ncgr.jgraph.PangenomicGraph;
import org.ncgr.jgraph.PathWalk;

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
    
    FRPair(FrequentedRegion fr1, FrequentedRegion fr2, PangenomicGraph graph, double alpha, int kappa, boolean caseCtrl) {
        this.fr1 = fr1;
        this.fr2 = fr2;
        this.graph = graph;
        this.alpha = alpha;
        this.kappa = kappa;
        this.caseCtrl = caseCtrl;
        // calculate support without merging!
        support = 0;
    }

    /**
     * Return the result of merging this pair of FRs.
     */
    public FrequentedRegion merge() {
        return FrequentedRegion.merge(fr1, fr2, graph, alpha, kappa);
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
