package org.ncgr.datastore.validation;

import org.ncgr.zip.GZIPBufferedReader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Methods to validate an LIS Datastore /pangenes/ collection.
 */
public class PangeneCollectionValidator extends CollectionValidator {

    /**
     * Construct from a /pangenes/ directory
     * files: hsh.tsv.gz, clust.tsv.gz
     */
    public PangeneCollectionValidator(String dirString) {
        super(dirString);
        requiredFileTypes = Arrays.asList("clust.tsv.gz", "hsh.tsv.gz");
    }

    public static void main(String[] args) {
        if (args.length!=1) {
            System.err.println("Usage: PangeneCollectionValidator [pangenes directory]");
            System.exit(1);
        }
        PangeneCollectionValidator validator = new PangeneCollectionValidator(args[0]);
        validator.validate();
        validator.printHeader();
        if (validator.valid) printIsValidMessage();
    }

    /**
     * Validate the current instance.
     */
    public void validate() {
        try {
            checkRequiredFiles();
        } catch (ValidationException ex) {
            printErrorAndExit(ex.getMessage());
        }
        // clust.tsv.gz NOT VALIDATED
        File clustFile = getDataFile("clust.tsv.gz");
        System.out.println(" - "+clustFile.getName()+" (not validated)");
        // hsh.tsv.gz NOT VALIDATED
        File hshFile = getDataFile("hsh.tsv.gz");
        System.out.println(" - "+hshFile.getName()+" (not validated)");
    }

}
    
