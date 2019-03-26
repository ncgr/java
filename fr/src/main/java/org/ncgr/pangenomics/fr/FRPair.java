package org.ncgr.pangenomics.fr;

import org.ncgr.pangenomics.Graph;

/**
 * Container for a pair of FrequentedRegions and their merged result with a comparator for ranking it.
 * This is where one implements weighting of certain desired FR characteristics.
 */
public class FRPair implements Comparable<FRPair> {

    FrequentedRegion fr1;
    FrequentedRegion fr2;
    FrequentedRegion merged;
    boolean caseCtrl;
    
    FRPair(FrequentedRegion fr1, FrequentedRegion fr2, Graph graph, double alpha, int kappa, boolean caseCtrl) {
        this.fr1 = fr1;
        this.fr2 = fr2;
        this.caseCtrl = caseCtrl;
        merged = FrequentedRegion.merge(fr1, fr2, graph, alpha, kappa);
    }
    
    /**
     * Two FRPairs are equal if their merged FRs are equal.
     */
    public boolean equals(FRPair that) {
        return this.merged.equals(that.merged);
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
}
