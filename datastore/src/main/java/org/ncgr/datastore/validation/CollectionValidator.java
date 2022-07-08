package org.ncgr.datastore.validation;

import org.ncgr.datastore.Readme;

import java.io.File;
import java.io.IOException;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.biojava.nbio.core.sequence.template.AbstractSequence;
import org.biojava.nbio.genome.parsers.gff.FeatureI;
import org.biojava.nbio.genome.parsers.gff.Location;

/**
 * Extend this class for each collection type-specific validator.
 * This class contains many methods for validating various types of files.
 */
public abstract class CollectionValidator {

    final static String NC="\033[0m";    // no color
    final static String RD="\033[1;31m"; // red
    final static String GR="\033[1;32m"; // green
    final static String LP="\033[1;35m"; // light purple
    
    File dir;
    Readme readme;
    String gensp;
    String collection;         // Strain.gnm.x.y.z.KEY4
    String collectionSansKey4; // Strain.gnm.x.y.z
    String genspStrainGnm;     // gensp.Strain.gnm
    boolean valid = true;
    List<String> requiredFileTypes;

    HashSet<String> features = new HashSet<>(); // utility for keeping track of GFF parents

    /**
     * Print out a couple header lines
     */
    public void printHeader() {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("### Validating "+purple(gensp)+" collection "+purple(collection));
    }

    /**
     * Use this to construct in an extending class.
     */
    public void setVars(String dirString) {
        // dir
        this.dir = new File(dirString);
        if (!dir.exists() || !dir.isDirectory()) {
            printErrorAndExit(red(dirString)+" does not exist or is not a directory.");
        }
        // collection from dirname
        this.collection = dir.getName();
        File readmeFile = new File(dir, "README."+collection+".yml");
        if (!readmeFile.exists()) {
            printErrorAndExit(red(readmeFile.getName())+" is not present in collection "+purple(collection));
        }
        // readme
        try {
            this.readme = Readme.parse(readmeFile);
        } catch (Exception ex) {
            printErrorAndExit(ex.getMessage());
        }
        // README identifier = collection
        if (!readme.identifier.equals(collection)) {
            printErrorAndExit("README.identifier "+red(readme.identifier)+" does not match collection "+purple(collection));
        }
        // gensp
        this.gensp = readme.scientific_name_abbrev;
        if (gensp==null) {
            printErrorAndExit("README file does not contain "+red("scientific_name_abbrev")+" entry.");
        }
        // collectionSansKey4 is the KEY4-removed prefix for LIS feature identifiers
        String[] fields = collection.split("\\.");
        int len = fields.length;
        collectionSansKey4 = "";
        for (int i=0; i<(len-1); i++) {
            if (i>0) collectionSansKey4 += ".";
            collectionSansKey4 += fields[i];
        }
        // genspStrainGnm is the gensp.strain.gnm prefix for LIS chromosomes/scaffolds, etc.
        genspStrainGnm = gensp;
        for (int i=0; i<2; i++) {
            genspStrainGnm += "." + fields[i];
        }
    }

    /**
     * Check that required files are present, listed in extending class requiredFileTypes List.
     * Exit if any files are missing.
     */
    public void checkRequiredFiles() {
        for (String fileType : requiredFileTypes) {
            if (!dataFileExists(fileType)) {
                printError("Required file type "+red(fileType)+" is not present in "+purple(collection));
            }
        }
        if (!valid) System.exit(1);
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
        System.out.println("### "+red("INVALID: ")+error);
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
        System.out.println("### "+green("VALID"));
    }

    /**
     * Return true if the given string is a valid LIS identifier or data filename.
     */
    public boolean matchesCollection(String identifier) {
        return identifier.startsWith(genspStrainGnm);
    }
    

    /**
     * Validate the seqname of a GFF record, ensuring that it matches the collection.
     */
    public boolean hasValidSeqname(FeatureI featureI) {
        return matchesCollection(featureI.seqname());
    }

    /**
     * Validate the ID in a genomic GFF file (must exist and match collection).
     */
    public boolean hasValidGenomicID(FeatureI featureI) {
        String id = getAttribute(featureI, "ID");
        return (id!=null && matchesCollection(id));
    }
    
    /**
     * Validate a genomic GFF feature's parent using the features set to ensure that parents are loaded before children.
     * Note: parent attribute may be a comma-separated list of parents, or not present.
     */
    public boolean hasValidParent(FeatureI featureI) {
        String id = getAttribute(featureI, "ID");
        // add this feature to list for future parent check
        features.add(id);
        String parent = getAttribute(featureI, "Parent");
        // non-parent is valid
        if (parent==null) return true;
        // one of parent entries must be in features set
        boolean parentLoaded = false;
        for (String p : parent.split(",")) {
            if (features.contains(p)) parentLoaded = true;
        }
        return parentLoaded;
    }

    /**
     * Validate a BioJava sequence identifier, making sure it matches the collection.
     */
    public boolean hasValidSequenceIdentifier(AbstractSequence sequence) {
        String identifier = getFastaSequenceIdentifier(sequence);
        return (matchesCollection(identifier));
    }

    /**
     * Return a GFF attribute for the given name ignoring case
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
    static String getFastaSequenceIdentifier(AbstractSequence sequence) {
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

    /**
     * Print an error message and exit if sequence has an invalid identifier.
     *
     * @param file the file (used for informative output)
     * @param sequence the sequence being tested
     */
    void validateSequenceIdentifier(File file, AbstractSequence sequence) {
        if (!hasValidSequenceIdentifier(sequence)) {
            printError(red(file.getName())+" has an invalid sequence identifier in header:");
            printError(sequence.getAccession().getID());
        }
        if (!valid) System.exit(1);
    }

    /**
     * ANSI color string red
     */
    static String red(String s) {
        return RD+s+NC;
    }

    /**
     * ANSI color string light purple
     */
    static String purple(String s) {
        return LP+s+NC;
    }

    /**
     * ANSI color string green
     */
    static String green(String s) {
        return GR+s+NC;
    }

}
