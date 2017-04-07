package org.ncgr.interpro;

import java.util.List;
import java.util.ArrayList;

public class DeletedEntries {
    List<DelRef> entries = new ArrayList<DelRef>();
    void add(DelRef entry) {
        entries.add(entry);
    }
    public List<DelRef> getEntries() {
        return entries;
    }
}
