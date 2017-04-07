package org.ncgr.interpro;

import java.util.List;
import java.util.ArrayList;

public class SecList {
    List<SecAc> entries = new ArrayList<SecAc>();
    void add(SecAc entry) {
        entries.add(entry);
    }
    public List<SecAc> getEntries() {
        return entries;
    }
}
