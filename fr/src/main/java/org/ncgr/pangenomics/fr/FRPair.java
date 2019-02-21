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
    
    public boolean equals(Object o) {
        FRPair that = (FRPair) o;
        return equals(that);
    }
    
    public boolean equals(FRPair that) {
        return (this.fr1.equals(that.fr1) && this.fr2.equals(that.fr2)) ||
            (this.fr1.equals(that.fr2) && this.fr2.equals(that.fr1));
    }

    /**
     * A comparator for PriorityQueue use -- note that it is the opposite of normal comparison because
     * PriorityQueue allows you to take the top (least) object with peek() but not the bottom (most) object.
     *
     * if caseCtrl: try to emphasize case vs. control distinction by measuring distance from case=control support line.
     * else default:  support, then average length, then number of nodes
     */
    public int compareTo(FRPair that) {
        if (caseCtrl) {
            // use distance from case=control line then avgLength then size
            int thisDistance = Math.abs(this.merged.getLabelCount("case")-this.merged.getLabelCount("ctrl"));
            int thatDistance = Math.abs(that.merged.getLabelCount("case")-that.merged.getLabelCount("ctrl"));
            if (thisDistance!=thatDistance) {
                return Integer.compare(thatDistance, thisDistance);
            } else if (that.merged.avgLength!=this.merged.avgLength) {
                return Double.compare(that.merged.avgLength, this.merged.avgLength);
            } else {
                return Integer.compare(that.merged.nodes.size(), this.merged.nodes.size());
            }
        }
        // default: support then avgLength then size
        if (that.merged.support!=this.merged.support) {
            return Integer.compare(that.merged.support, this.merged.support);
        } else if (that.merged.avgLength!=this.merged.avgLength) {
            return Double.compare(that.merged.avgLength, this.merged.avgLength);
        } else {
            return Integer.compare(that.merged.nodes.size(), this.merged.nodes.size());
        }
    }
}
