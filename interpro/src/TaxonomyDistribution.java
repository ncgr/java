package org.ncgr.interpro;

import java.util.List;
import java.util.ArrayList;

public class TaxonomyDistribution {
    List<TaxonData> entries = new ArrayList<TaxonData>();
    void add(TaxonData entry) {
        entries.add(entry);
    }
    public List<TaxonData> getEntries() {
        return entries;
    }
}
