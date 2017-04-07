package org.ncgr.interpro;

import java.util.List;
import java.util.ArrayList;

public class MemberList {
    List<DbXref> entries = new ArrayList<DbXref>();
    void add(DbXref entry) {
        entries.add(entry);
    }
    public List<DbXref> getEntries() {
        return entries;
    }
}
