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
    }

    public static void main(String[] args) {
        if (args.length!=1) {
            System.err.println("Usage: MSTMapCollectionValidator [genome directory]");
            System.exit(1);
        }

        // construct our validator and check required files
        MSTMapCollectionValidator validator = new MSTMapCollectionValidator(args[0]);
        validator.printHeader();

        // mstmap.tsv.gz
        // NOT VALIDATED
        if (validator.dataFileExists("mstmap.tsv.gz")) {
            File file = validator.getDataFile("mstmap.tsv.gz");
            System.out.println(" - "+file.getName()+" (not validated)");
        }            
        
        // valid!
        if (validator.valid) printIsValidMessage();
    }

}
    
