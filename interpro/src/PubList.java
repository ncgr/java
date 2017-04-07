package org.ncgr.interpro;

import java.util.List;
import java.util.ArrayList;

public class PubList {
    List<Publication> entries = new ArrayList<Publication>();
    void add(Publication entry) {
        entries.add(entry);
    }
    public List<Publication> getEntries() {
        return entries;
    }
}
