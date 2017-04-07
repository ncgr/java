package org.ncgr.interpro;

import java.util.List;
import java.util.ArrayList;

public class Release {
    List<DBInfo> entries = new ArrayList<DBInfo>();
    void add(DBInfo entry) {
        entries.add(entry);
    }
    public List<DBInfo> getEntries() {
        return entries;
    }
}
