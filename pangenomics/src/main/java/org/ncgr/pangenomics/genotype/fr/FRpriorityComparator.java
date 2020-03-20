package org.ncgr.pangenomics.genotype.fr;

import java.util.Comparator;
import java.util.Map;

/**
 * Comparator to sort FRs by priority, highest first.
 */
public class FRpriorityComparator implements Comparator<String> {
    Map<String,FrequentedRegion> frequentedRegions;

    public FRpriorityComparator(Map<String,FrequentedRegion> frequentedRegions) {
        this.frequentedRegions = frequentedRegions;
    }

    // compare priorities
    public int compare(String key1, String key2) {
        if (key1.equals(key2)) return 0;
        FrequentedRegion fr1 = frequentedRegions.get(key1);
        FrequentedRegion fr2 = frequentedRegions.get(key2);
        if (fr2.priority==fr1.priority) {
            return key1.compareTo(key2);
        } else {
            return fr2.priority - fr1.priority;
        }
    }
}
