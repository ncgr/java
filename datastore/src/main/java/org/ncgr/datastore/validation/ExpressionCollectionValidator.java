package org.ncgr.datastore.validation;

import org.ncgr.zip.GZIPBufferedReader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
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
        // 0            1     2            3          4       5                  6        7         8
        // #identifier  name  description  treatment  tissue  development_stage  species  genotype  replicate_group
        // store identifiers for check against values.tsv and obo.tsv
        List<String> sampleIdentifiers = new ArrayList<>();
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
                    sampleIdentifiers.add(parts[0]);
                }
            }
        } catch (Exception ex) {
            printErrorAndExit(ex.getMessage());
        }
        
        // obo.tsv.gz REQUIRED
        // #identifier     ontology_term
        // check that identifiers are in samples.tsv
        List<String> oboIdentifiers = new ArrayList<>();
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
                if (!sampleIdentifiers.contains(parts[0])) {
                    printError(file.getName()+" contains a sample that is not present in samples.tsv file.");
                    printError(line);
                }
            }
        } catch (Exception ex) {
            printErrorAndExit(ex.getMessage());
        }
        
        // values.tsv.gz REQUIRED
        // gene_id sample1 sample2 .... sampleN
        // check that all sample identifiers match those in samples.tsv
        try {
            File file = getDataFile("values.tsv.gz");
            System.out.println(" - "+file.getName());
            BufferedReader br = GZIPBufferedReader.getReader(file);
            String line = null;
            boolean first = true;
            while ((line=br.readLine())!=null) {
                if (line.startsWith("#") || line.trim().length()==0) continue; // comment or blank
                String[] parts = line.split("\t");
                if (first) {
                    first = false;
                    if (line.startsWith("gene_id")) {
                        // header line, check all identifiers
                        for (int i=1; i<parts.length; i++) {
                            if (!sampleIdentifiers.contains(parts[i])) {
                                printError(file.getName()+" contains a sample "+parts[i]+" in the header that is not present in samples.tsv file.");
                            }
                        }
                        continue;
                    } else {
                        printError(file.getName()+" first line does not start with gene_id.");
                        break;
                    }
                } else {
                    // a gene line
                    String geneId = parts[0];
                    if (!matchesCollection(geneId)) {
                        printError("Gene ID "+geneId+" in "+file.getName()+" is not a valid LIS identifier:");
                    }
                }
            }
        } catch (Exception ex) {
            printErrorAndExit(ex.getMessage());
        }
    }

}
