package org.ncgr.pangenomics.genotype.fr;

import java.util.Comparator;
import java.util.Map;

/**
 * Comparator to sort FRs by p-value.
 */
public class FRpComparator implements Comparator<String> {
    Map<String,FrequentedRegion> frequentedRegions;

    public FRpComparator(Map<String,FrequentedRegion> frequentedRegions) {
        this.frequentedRegions = frequentedRegions;
    }

    // compare by fishing out the p-values
    public int compare(String key1, String key2) {
        if (key1.equals(key2)) return 0;
        FrequentedRegion fr1 = frequentedRegions.get(key1);
        FrequentedRegion fr2 = frequentedRegions.get(key2);
        double p1 = fr1.fisherExactP();
        double p2 = fr2.fisherExactP();
        int dc = Double.compare(p1,p2);
        if (dc==0) {
            return key1.compareTo(key2);
        } else {
            return dc;
        }
    }
}
