package org.ncgr.interpro;

public class Publication {

    public String id;
    public String authorList;
    public String title;
    public DbXref dbXref;
    public String journal;
    public Location location;
    public int year;

    public String toString() {
        String str = "id:"+id+"; authors:"+authorList+"; title:"+title+"; journal:"+journal+"; year:"+year+"; ";
        if (location!=null) str += "location:issue="+location.issue+",pages="+location.pages+",volume="+location.volume+"; ";
        if (dbXref!=null) {
            str += "db_xref:";
            if (dbXref.proteinCount>0) str += "proteinCount="+dbXref.proteinCount+",";
            if (dbXref.db!=null) str += "db="+dbXref.db+",";
            if (dbXref.dbkey!=null) str += "dbkey="+dbXref.dbkey+",";
            if (dbXref.name!=null) str += "name="+dbXref.name+";";
        }
        return str;
    }

}
