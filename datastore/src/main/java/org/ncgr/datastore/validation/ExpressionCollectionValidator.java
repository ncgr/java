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
     * Construct from an /expression/ directory
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
        ExpressionCollectionValidator validator = new ExpressionCollectionValidator(args[0]);
        validator.validate();
        if (validator.valid) printIsValidMessage();
    }

    /**
     * Validate current instance.
     */
    public void validate() {
        printHeader();
        try {
            checkRequiredFiles();
        } catch (ValidationException ex) {
            printErrorAndExit(ex.getMessage());
        }

        // check required extra README keys
        if (readme.expression_unit==null) {
            printError("README file lacks required expression_unit key:value.");
        }

        // samples.tsv.gz REQUIRED
        // #identifier     name    description     treatment       tissue  development_stage       species genotype        replicate_group
        try {
            File file = getDataFile("samples.tsv.gz");
            System.out.println(" - "+file.getName());
            BufferedReader br = GZIPBufferedReader.getReader(file);
            String line = null;
            while ((line=br.readLine())!=null) {
                if (line.trim().length()==0) continue;
                String[] parts = line.split("\t");
                if (line.startsWith("#")) {
                    if (!parts[0].equals("#identifier")) {
                        printError(file.getName()+" heading does not start with #identifier.");
                        printError(line);
                    }
                } else {
                    if (parts.length<3) {
                        printError(file.getName()+" does not contain at least three values in this line:");
                        printError(line);
                    }
                }
            }
        } catch (Exception ex) {
            printErrorAndExit(ex.getMessage());
        }
        
        // obo.tsv.gz REQUIRED
        // #identifier     ontology_term
        try {
            File file = getDataFile("obo.tsv.gz");
            System.out.println(" - "+file.getName());
            BufferedReader br = GZIPBufferedReader.getReader(file);
            String line = null;
            while ((line=br.readLine())!=null) {
                if (line.startsWith("#") || line.trim().length()==0) continue; // comment or blank
                String[] parts = line.split("\t");
                if (parts.length<2) {
                    printError(file.getName()+" does not contain two values in this line:");
                    printError(line);
                }
            }
        } catch (Exception ex) {
            printErrorAndExit(ex.getMessage());
        }

        // values.tsv.gz REQUIRED
        try {
            File file = getDataFile("values.tsv.gz");
            System.out.println(" - "+file.getName());
            BufferedReader br = GZIPBufferedReader.getReader(file);
            String line = null;
            while ((line=br.readLine())!=null) {
                if (line.startsWith("#") || line.trim().length()==0) continue; // comment or blank
                String[] parts = line.split("\t");
                if (parts[0].toLowerCase().equals("gene_id")) continue; // header line
                // a gene line
                String geneId = parts[0];
                if (!matchesCollection(geneId)) {
                    printError("Gene ID "+geneId+" in "+file.getName()+" is not a valid LIS identifier:");
                }
            }
        } catch (Exception ex) {
            printErrorAndExit(ex.getMessage());
        }
    }

}
    
