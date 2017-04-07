package org.ncgr.interpro;

import java.util.List;
import java.util.ArrayList;

public class ChildList {
    List<RelRef> entries = new ArrayList<RelRef>();
    void add(RelRef entry) {
        entries.add(entry);
    }
    public List<RelRef> getEntries() {
        return entries;
    }
}
