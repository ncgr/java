package org.ncgr.interpro;

public class DbXref {
    
    public int proteinCount;
    public String db;
    public String dbkey;
    public String name;

    public String toString() {
        return "db:"+db+"; dbkey:"+dbkey+"; name:"+name+"; proteinCount:"+proteinCount;
    }
    
}
