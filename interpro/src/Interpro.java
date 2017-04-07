package org.ncgr.interpro;

import java.util.List;

/**
 * The core class containing an Intepro record.
 */
public class Interpro {
    
    // attributes
    public String id;
    public String proteinCount;
    public String shortName;
    public String type;

    // fields
    public String name;
    public String description; // = abstract

    // everything else is lists
    public ClassList classList;
    public PubList pubList;
    public ChildList childList;
    public ParentList parentList;
    public Contains contains;
    public FoundIn foundIn;
    public MemberList memberList;
    public ExternalDocList externalDocList;
    public StructureDBLinks structureDBLinks;
    public TaxonomyDistribution taxonomyDistribution;
    public SecList secList;

    /**
     * Return a string containing some of the data.
     */
    public String toString() {
        List<Publication> pubs = pubList.getEntries();
        List<DbXref> members = memberList.getEntries();
        String str = "id:"+id+"; proteinCount:"+proteinCount+"; shortName:"+shortName+"; type:"+type+"; name:"+name;
        str += "abstract:"+description+"; ";
        for (DbXref member : members) str += "member:"+member.toString()+"; ";
        for (Publication pub : pubs) str += "publication:"+pub.toString()+"; ";
        return str;
    }
    
}
