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
 * Methods to validate an LIS Datastore /mstmap/ collection.
 */
public class MSTMapCollectionValidator extends CollectionValidator {

    /**
     * Construct from a /mstmap/ directory
     * files: mstmap.tsv.gz
     */
    public MSTMapCollectionValidator(String dirString) {
        super(dirString);
        requiredFileTypes = Arrays.asList("mstmap.tsv.gz");
    }

    public static void main(String[] args) {
        if (args.length!=1) {
            System.err.println("Usage: MSTMapCollectionValidator [genome directory]");
            System.exit(1);
        }
        MSTMapCollectionValidator validator = new MSTMapCollectionValidator(args[0]);
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
        // mstmap.tsv.gz
        // NOT VALIDATED
        File file = getDataFile("mstmap.tsv.gz");
        System.out.println(" - "+file.getName()+" (not validated)");
    }

}
    
