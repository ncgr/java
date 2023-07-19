package org.ncgr.datastore.validation;

import org.ncgr.datastore.Readme;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.biojava.nbio.core.sequence.template.AbstractSequence;
import org.biojava.nbio.genome.parsers.gff.FeatureI;

/**
 * Extend this class for each collection type-specific validator.
 * This class contains many methods for validating various types of files.
 */
public abstract class CollectionValidator {

    final static String NC="\033[0m";    // no color
    final static String RD="\033[1;31m"; // red
    final static String GR="\033[1;32m"; // green
    final static String YL="\033[1;33m"; // yellow
    final static String BL="\033[1;34m"; // blue
    final static String LP="\033[1;35m"; // light purple
    
    File dir;
    Readme readme;
    String gensp;
    String collection;         // Strain.gnm.x.y.z.KEY4
    String genspStrainGnm;     // gensp.Strain.gnm
    boolean valid = true;
    List<String> requiredFileTypes;

    /**
     * Construct given a directory string. Print error and exit if collection has fundamental problems.
     *
     * @param dirString the directory, e.g. /data/v2/Glycine/max/genomes/Wm82.gnm1.ann1.ABCD (with or without trailing slash).
     */
    public CollectionValidator(String dirString) {
        // dir
        this.dir = new File(dirString);
        if (!dir.exists() || !dir.isDirectory()) {
            printErrorAndExit(red(dirString)+" does not exist or is not a directory.");
        }
        // collection from dirname
        this.collection = dir.getName();
        // README
        File readmeFile = new File(dir, "README."+collection+".yml");
        if (!readmeFile.exists()) {
            printHeader();
            printErrorAndExit(red(readmeFile.getName())+" is not present in collection "+purple(collection));
        }
        try {
            this.readme = Readme.parse(readmeFile);
        } catch (IOException ex) {
            printHeader();
            printErrorAndExit(red(ex.getMessage()));
        }
        // README identifier = collection
        if (!readme.identifier.equals(collection)) {
            printHeader();
            printErrorAndExit("README.identifier "+red(readme.identifier)+" does not match collection "+purple(collection));
        }
        // gensp
        this.gensp = readme.scientific_name_abbrev;
        if (gensp==null) {
            printHeader();
            printErrorAndExit("README file does not contain "+red("scientific_name_abbrev")+" entry.");
        }
        // check that publication_doi looks legit if it's present
        if (readme.publication_doi!=null) {
            boolean looksLegit = readme.publication_doi.contains(".") && readme.publication_doi.contains("/");
            if (!looksLegit) {
                printHeader();
                printErrorAndExit("README file contains invalid "+red("publication_doi")+" entry: " + readme.publication_doi);
            }
        }
        // form the gensp.Strain.gnm prefix for LIS genomic feature identifiers.
        String[] fields = collection.split("\\.");
        genspStrainGnm = gensp;
        for (int i=0; i<2; i++) {
            genspStrainGnm += "." + fields[i];
        }
    }

    /**
     * Print out a couple header lines
     */
    public void printHeader() {
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("## Validating "+purple(gensp)+" collection "+purple(collection));
    }

    /**
     * Check that required files are present, listed in extending class requiredFileTypes List.
     * Exit if any files are missing.
     */
    public void checkRequiredFiles() throws ValidationException {
        for (String fileType : requiredFileTypes) {
            File file = getDataFile(fileType);
            if (!file.exists()) {
                printError("Required file "+red(file.getName())+" is not present in "+purple(collection));
            }
        }
        if (!valid) throw new ValidationException("Missing required files.");
    }

    /**
     * Return true if the gensp.collection.fileType file exists in the collection.
     */
    boolean dataFileExists(String fileType) {
        File file = getDataFile(fileType);
        return file.exists();
    }

    /**
     * Return a data file, which starts with gensp.collection.
     */
    public File getDataFile(String fileType) {
        if (fileType.equals("clust.tsv.gz") || fileType.equals("hsh.tsv.gz")) {
            return new File(dir, collection + "." + fileType);
        } else {
            return new File(dir, gensp + "." + collection + "." + fileType);
        }
    }

    /**
     * Print an error message and set valid = false.
     */
    public void printError(String error) {
        valid = false;
        System.out.println("## "+red("INVALID: ")+error);
    }

    /**
     * Print a warning.
     */
    public void printWarning(String warning) {
        System.out.println(" x "+yellow(warning));
    }

    /**
     * Print some info.
     */
    public void printInfo(String info) {
        System.out.println(" + "+green(info));
    }
    
    /**
     * Print a standard error message then exit with status=1.
     */
    public static void printErrorAndExit(String error) {
        System.out.println("## "+red("INVALID: ")+error);
        System.exit(1);
    }

    /**
     * Print the message for when the collection is valid.
     */
    public static void printIsValidMessage() {
        System.out.println("## "+green("VALID"));
    }

    /**
     * Return true if the given string is a valid LIS identifier or data filename.
     */
    public boolean matchesCollection(String identifier) {
        return identifier.startsWith(genspStrainGnm);
    }
        
    /**
     * Validate a BioJava sequence identifier, making sure it matches the collection.
     */
    public boolean hasValidSequenceIdentifier(AbstractSequence sequence) {
        String identifier = getFastaSequenceIdentifier(sequence);
        return (matchesCollection(identifier));
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
    void validateSequenceIdentifier(File file, AbstractSequence sequence) throws ValidationException {
        if (!hasValidSequenceIdentifier(sequence)) {
            throw new ValidationException(file.getName()+" has an invalid sequence identifier in header:\n"+sequence.getAccession().getID());
        }
    }

    /**
     * Return true if the given data filename matches the current collection, i.e. starts with gensp.collection.
     *
     * @param filename the filename to be checked
     * @return true if the filename starts with the collection
     */
    boolean isValidDataFilename(String filename) {
        return filename.startsWith(gensp+"."+collection);
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
     * Return the value of the valid variable.
     */
    public boolean isValid() {
        return valid;
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

    /**
     * ANSI coloir string yellow
     */
    static String yellow(String s) {
        return YL+s+NC;
    }

}
