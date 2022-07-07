package org.ncgr.datastore.validation;

import org.ncgr.datastore.Readme;

import java.io.File;
import java.io.IOException;

import java.util.HashSet;
import java.util.Map;

import org.biojava.nbio.core.sequence.template.AbstractSequence;
import org.biojava.nbio.genome.parsers.gff.FeatureI;
import org.biojava.nbio.genome.parsers.gff.Location;

/**
 * Extend this class for each collection type-specific validator.
 * This class contains many methods for validating various types of files.
 */
public abstract class CollectionValidator {

    File dir;
    Readme readme;
    String collection;
    String gensp;
    String collectionSansKey4;
    String collectionSansAnnKey4;
    boolean valid = true;

    /**
     * Use this to construct in an extending class.
     */
    public void setVars(String dirString) {
        // dir
        this.dir = new File(dirString);
        if (!dir.exists() || !dir.isDirectory()) {
            printErrorAndExit(dirString+" does not exist or is not a directory.");
        }
        // collection from dirname
        this.collection = dir.getName();
        File readmeFile = new File(dir, "README."+collection+".yml");
        if (!readmeFile.exists()) {
            printErrorAndExit("README file "+readmeFile.getName()+" is not present in collection.");
        }
        // readme
        try {
            this.readme = Readme.parse(readmeFile);
        } catch (Exception ex) {
            printErrorAndExit(ex.getMessage());
        }
        // README identifier = collection
        if (!readme.identifier.equals(collection)) {
            printErrorAndExit("README.identifier "+readme.identifier+" does not match collection.");
        }
        // gensp
        this.gensp = readme.scientific_name_abbrev;
        if (gensp==null) {
            printErrorAndExit("README file does not contain scientific_name_abbrev entry.");
        }
        // collectionSansKey4 is the prefix for LIS feature identifiers
        String[] fields = collection.split("\\.");
        int len = fields.length;
        collectionSansKey4 = "";
        for (int i=0; i<(len-1); i++) {
            if (i>0) collectionSansKey4 += ".";
            collectionSansKey4 += fields[i];
        }
        // collectionSansAnnKey4 is the prefix for LIS chromosomes/scaffolds
        collectionSansAnnKey4 = "";
        for (int i=0; i<len-2; i++) {
            if (i>0) collectionSansAnnKey4 += ".";
            collectionSansAnnKey4 += fields[i];
        }
    }

    /**
     * Return true if the gensp.fileType file exists in the collection.
     */
    boolean dataFileExists(String fileType) {
        File file = getDataFile(fileType);
        return file.exists();
    }

    /**
     * Return a data file, which starts with gensp.collection.
     */
    public File getDataFile(String fileType) {
        return new File(dir, gensp+"."+collection+"."+fileType);
    }

    /**
     * Print a standard error message.
     */
    public void printError(String error) {
        valid = false;
        System.out.println("## INVALID: "+error);
    }

    /**
     * Print a standard error message then exit with status=1.
     */
    public void printErrorAndExit(String error) {
        printError(error);
        System.exit(1);
    }

    /**
     * Print the message for when the collection is valid.
     */
    public static void printIsValidMessage() {
        System.out.println("## VALID");
    }

    /**
     * Return true if the given identifier is a valid LIS identifier for a feature.
     */
    public boolean featureMatchesCollection(String identifier) {
        return identifier.startsWith(gensp+"."+collectionSansKey4);
    }

    /**
     * Return true if the given identifier is a valid LIS identifier for a chromosome/scaffold.
     */
    public boolean seqMatchesCollection(String identifier) {
        return identifier.startsWith(gensp+"."+collectionSansAnnKey4);
    }
    
    /**
     * Validate a genomic feature.
     * The features set is used to ensure that parents are loaded before children.
     */
    void validateGenomicFeatureI(FeatureI featureI, HashSet<String> features) {
        String seqname = featureI.seqname();
        Location location = featureI.location();
        String type = featureI.type();
        // attributes
        String id = getAttribute(featureI, "ID");
        String name = getAttribute(featureI, "Name");
        String parent = getAttribute(featureI, "Parent");
        String note = getAttribute(featureI, "Note");
        String dbxref = getAttribute(featureI, "Dbxref");
        String ontology_term = getAttribute(featureI, "Ontology_term");
        String alleles = getAttribute(featureI, "alleles");
        String symbol = getAttribute(featureI, "symbol");
        // check that seqname matches collection
        if (!seqMatchesCollection(seqname)) {
            printError("seqname in this GFF record is not a valid LIS identifier:");
            printErrorAndExit(featureI.toString());
        }
        // check that ID exists
        if (id==null) {
            printError("GFF line does not include ID:");
            printErrorAndExit(featureI.toString());
        }
        // check that ID matches collection
        if (!featureMatchesCollection(id)) {
            printError("ID in this GFF record is not a valid LIS identifier:");
            printErrorAndExit(featureI.toString());
        }
        // check that parent is already loaded; could be comma-separated list of parents
        if (parent!=null) {
            boolean parentLoaded = false;
            String[] parents = parent.split(",");
            for (String p : parents) {
                if (features.contains(p)) parentLoaded = true;
            }
            if (!parentLoaded) {
                printError("Parent ID in this GFF record has not yet been loaded. GFF file needs to be sorted.");
                printErrorAndExit(featureI.toString());
            }
        }
        // add this feature to list for future parent check
        features.add(id);
    }

    /**
     * Validate an IPRScan feature which is placed on a protein.
     */
    void validateIPRScanFeatureI(FeatureI featureI) {
        String seqname = featureI.seqname();
        // check that seqname matches collection
        if (!seqMatchesCollection(seqname)) {
            printError("seqname in this GFF record is not a valid LIS identifier:");
            printErrorAndExit(featureI.toString());
        }
    }
    
    /**
     * Validate a BioJava sequence.
     */
    void validateSequence(AbstractSequence sequence) {
        String identifier = getFastaIdentifier(sequence);
        if (!featureMatchesCollection(identifier)) {
            printError("ID in this FASTA sequence is not a valid LIS identifier:");
            printErrorAndExit(sequence.getAccession().getID());
        }
    }

    /**
     * Return an attribute for the given name ignoring case
     */
    static String getAttribute(FeatureI featureI, String name) {
        Map<String,String> attributeMap = featureI.getAttributes();
        for (String attributeName : attributeMap.keySet()) {
            if (attributeName.equalsIgnoreCase(name)) {
                return attributeMap.get(attributeName);
            }
        }
        return null;
    }

    /**
     * Return the identifier of a BioJava AbstractSequence.
     *
     * @param sequence the AbstractSequence
     * @return the identifier
     */
    static String getFastaIdentifier(AbstractSequence sequence) {
        String identifier = null;
        String header = sequence.getAccession().getID();
        String[] bits = header.split(" ");
        if (bits[0].contains("|")) {
            String[] subbits = bits[0].split("\\|");
            identifier = subbits[1];
        } else {
            identifier = bits[0];
        }
        return identifier;
    }

}
