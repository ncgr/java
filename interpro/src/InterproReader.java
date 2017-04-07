package org.ncgr.interpro;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.mapper.MapperWrapper;

/**
 * A reader that uses XStream to parse an Interpro XML file (or URL).
 * The data is stored in two maps and a set for downstream use.
 *
 * getDBInfoMap() returns a map of records inside the release tag at the beginning of the XML file
 * getInterproMap() returns a map of records inside the interpro tag
 * getDelRefSet() returns a set of IDs of deleted refs found at the end of the XML file
 *
 * @author Sam Hokin
 */
public class InterproReader {

    XStream xstream;

    Map<String,DBInfo> dbInfoMap = new HashMap<String,DBInfo>();
    Map<String,Interpro> interproMap = new HashMap<String,Interpro>();
    Set<String> delRefSet = new HashSet<String>();

    /**
     * Constructor sets xstream up, so that read() methods load the maps
     */
    public InterproReader() {
        
        // initialize XStream with NoNameCoder so it allows attributes with underscores
        xstream = new XStream(new StaxDriver(new NoNameCoder()));

        // InterproDB
        xstream.alias("interprodb", InterproDB.class);
        // -- fields
        xstream.aliasField("deleted_entries", InterproDB.class, "deletedEntries");
        // -- collections
        xstream.addImplicitCollection(InterproDB.class, "entries", Interpro.class);

        // Release
        xstream.alias("release", Release.class);
        // -- collections
        xstream.addImplicitCollection(Release.class, "entries", DBInfo.class);

        // DBInfo
        xstream.alias("dbinfo", DBInfo.class);
        xstream.useAttributeFor(DBInfo.class, "dbname");
        xstream.useAttributeFor(DBInfo.class, "entryCount");
        xstream.useAttributeFor(DBInfo.class, "fileDate");
        xstream.useAttributeFor(DBInfo.class, "version");
        xstream.aliasAttribute(DBInfo.class, "entryCount", "entry_count");
        xstream.aliasAttribute(DBInfo.class, "fileDate", "file_date");

        // Interpro
        xstream.alias("interpro", Interpro.class);
        // -- attributes
        xstream.aliasAttribute(Interpro.class, "proteinCount", "protein_count");
        xstream.aliasAttribute(Interpro.class, "shortName", "short_name");
        xstream.useAttributeFor(Interpro.class, "id");
        xstream.useAttributeFor(Interpro.class, "proteinCount");
        xstream.useAttributeFor(Interpro.class, "shortName");
        xstream.useAttributeFor(Interpro.class, "type");
        // -- fields
        xstream.aliasField("abstract", Interpro.class, "description");
        xstream.aliasField("class_list", Interpro.class, "classList");
        xstream.aliasField("pub_list", Interpro.class, "pubList");
        xstream.aliasField("parent_list", Interpro.class, "parentList");
        xstream.aliasField("child_list", Interpro.class, "childList");
        xstream.aliasField("found_in", Interpro.class, "foundIn");
        xstream.aliasField("member_list", Interpro.class, "memberList");
        xstream.aliasField("external_doc_list", Interpro.class, "externalDocList");
        xstream.aliasField("structure_db_links", Interpro.class, "structureDBLinks");
        xstream.aliasField("taxonomy_distribution", Interpro.class, "taxonomyDistribution");
        xstream.aliasField("sec_list", Interpro.class, "secList");

        // Publication
        xstream.alias("publication", Publication.class);
        // -- attributes
        xstream.aliasAttribute(Publication.class, "id", "id");
        // -- fields
        xstream.aliasField("author_list", Publication.class, "authorList");
        xstream.aliasField("db_xref", Publication.class, "dbXref");
        // -- collections
        xstream.addImplicitCollection(PubList.class, "entries", Publication.class);

        // Location
        xstream.alias("location", Location.class);
        // -- attributes
        xstream.aliasAttribute(Location.class, "issue", "issue");
        xstream.aliasAttribute(Location.class, "pages", "pages");
        xstream.aliasAttribute(Location.class, "volume", "volume");

        // RelRef
        xstream.alias("rel_ref", RelRef.class);
        // -- collections
        xstream.addImplicitCollection(ParentList.class, "entries", RelRef.class);
        xstream.addImplicitCollection(Contains.class, "entries", RelRef.class);
        xstream.addImplicitCollection(FoundIn.class, "entries", RelRef.class);
        xstream.addImplicitCollection(ChildList.class, "entries", RelRef.class);

        // DbXref
        xstream.alias("db_xref", DbXref.class);
        // -- attributes
        xstream.aliasAttribute(DbXref.class, "proteinCount", "protein_count");
        xstream.aliasAttribute(DbXref.class, "db", "db");
        xstream.aliasAttribute(DbXref.class, "dbkey", "dbkey");
        xstream.aliasAttribute(DbXref.class, "name", "name");
        // -- collections
        xstream.addImplicitCollection(MemberList.class, "entries", DbXref.class);
        xstream.addImplicitCollection(ExternalDocList.class, "entries", DbXref.class);
        xstream.addImplicitCollection(StructureDBLinks.class, "entries", DbXref.class);

        // TaxonData
        xstream.alias("taxon_data", TaxonData.class);
        // -- collections
        xstream.addImplicitCollection(TaxonomyDistribution.class, "entries", TaxonData.class);

        // Classification
        xstream.alias("classification", Classification.class);
        // -- collections
        xstream.addImplicitCollection(ClassList.class, "entries", Classification.class);

        // SecAc
        xstream.alias("sec_ac", SecAc.class);
        // -- collections
        xstream.addImplicitCollection(SecList.class, "entries", SecAc.class);
        
        // DeletedEntries
        xstream.alias("deleted_entries", DeletedEntries.class);

        // DelRef
        xstream.alias("del_ref", DelRef.class);
        // -- attributes
        xstream.aliasAttribute(DelRef.class, "id", "id");
        // -- collections
        xstream.addImplicitCollection(DeletedEntries.class, "entries", DelRef.class);

    }

    /**
     * Read the Interpro XML data from the given file into the three maps
     */
    public void read(File file) throws XStreamException {
        // read the whole file
        InterproDB interproDB = (InterproDB) xstream.fromXML(file);
        populateMaps(interproDB);
    }

    /**
     * Read the Interpro XML data from the given URL into the three maps
     */
    public void read(URL url) throws XStreamException {
        // read the whole thing
        InterproDB interproDB = (InterproDB) xstream.fromXML(url);
        populateMaps(interproDB);
    }

    /**
     * Populate the maps from a populated InterproDB instance
     */
    void populateMaps(InterproDB interproDB) {
        // releases dbInfo map
        for (DBInfo dbinfo : interproDB.release.getEntries()) {
            dbInfoMap.put(dbinfo.dbname, dbinfo);
        }
        // interpro entries map
        for (Interpro interpro : interproDB.getEntries()) {
            interproMap.put(interpro.id, interpro);
        }
        // deleted entries map
        for (DelRef delref : interproDB.deletedEntries.getEntries()) {
            delRefSet.add(delref.id);
        }
    }

    /**
     * Return the DB Info map
     */
    public Map<String,DBInfo> getDBInfoMap() {
        return dbInfoMap;
    }

    /**
     * Return the Interpro map
     */
    public Map<String,Interpro> getInterproMap() {
        return interproMap;
    }
    
    /**
     * Return the deleted ref set
     */
    public Set<String> getDelRefSet() {
        return delRefSet;
    }


    class InterproDB {
        Release release;
        List<Interpro> entries = new ArrayList<Interpro>();
        void add(Interpro entry) {
            entries.add(entry);
        }
        List<Interpro> getEntries() {
            return entries;
        }
        DeletedEntries deletedEntries;
    }

}

