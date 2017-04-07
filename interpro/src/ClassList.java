package org.ncgr.interpro;

import java.util.List;
import java.util.ArrayList;

public class ClassList {
    List<Classification> entries = new ArrayList<Classification>();
    void add(Classification entry) {
        entries.add(entry);
    }
    public List<Classification> getEntries() {
        return entries;
    }
}
