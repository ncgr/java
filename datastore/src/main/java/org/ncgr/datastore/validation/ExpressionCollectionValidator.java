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
 * Methods to validate an LIS Datastore /expression/ collection.
 */
public class ExpressionCollectionValidator extends CollectionValidator {

    /**
     * Construct from an genome directory
     */
    public ExpressionCollectionValidator(String dirString) {
        super(dirString);
        requiredFileTypes = Arrays.asList("samples.tsv.gz", "values.tsv.gz", "obo.tsv.gz");
    }

    public static void main(String[] args) {
        if (args.length!=1) {
            System.err.println("Usage: ExpressionCollectionValidator [genome directory]");
            System.exit(1);
        }

        // construct our validator and check required files
        ExpressionCollectionValidator validator = new ExpressionCollectionValidator(args[0]);
        validator.printHeader();
        try {
            validator.checkRequiredFiles();
        } catch (ValidationException ex) {
            printErrorAndExit(ex.getMessage());
        }

        // samples.tsv.gz
        if (validator.dataFileExists("samples.tsv.gz")) {
            File file = validator.getDataFile("samples.tsv.gz");
            System.out.println(" - "+file.getName()+" (no validation)");
        }

        // obo.tsv.gz
        if (validator.dataFileExists("obo.tsv.gz")) {
            File file = validator.getDataFile("obo.tsv.gz");
            System.out.println(" - "+file.getName()+" (no validation)");
        }

        // values.tsv.gz
        try {
            File file = validator.getDataFile("values.tsv.gz");
            System.out.println(" - "+file.getName());
            BufferedReader br = GZIPBufferedReader.getReader(file);
            String line = null;
            while ((line=br.readLine())!=null) {
                if (line.startsWith("#") || line.trim().length()==0) continue; // comment or blank
                String[] parts = line.split("\t");
                if (parts[0].toLowerCase().equals("geneid")) continue; // header line
                // a gene line
                String geneId = parts[0];
                if (!validator.matchesCollection(geneId)) {
                    validator.printError("Gene ID "+geneId+" in "+file.getName()+" is not a valid LIS identifier:");
                }
            }
        } catch (Exception ex) {
            printErrorAndExit(ex.getMessage());
        }
            
        // valid!
        if (validator.valid) printIsValidMessage();
    }

}
    
