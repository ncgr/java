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
 * Methods to validate an LIS Datastore /gwas/ collection.
 */
public class GWASCollectionValidator extends CollectionValidator {

    /**
     * Construct from a /gwas/ directory
     * files: obo.tsv.gz, result.tsv.gz, trait.tsv.gz
     */
    public GWASCollectionValidator(String dirString) {
        super(dirString);
        requiredFileTypes = Arrays.asList("result.tsv.gz","trait.tsv.gz");
    }

    public static void main(String[] args) {
        if (args.length!=1) {
            System.err.println("Usage: GWASCollectionValidator [genome directory]");
            System.exit(1);
        }

        // construct our validator and check required files
        GWASCollectionValidator validator = new GWASCollectionValidator(args[0]);
        validator.validate();
        if (validator.isValid()) printIsValidMessage();
    }
        
    /**
     * Validate the current instance.
     */
    public void validate() {
        printHeader();
        // README must contain genotype and genotyping_platform entries
        if (readme.genotype==null) {
            printError("README does not contain genotype key:value.");
        }
        if (readme.genotyping_platform==null) {
            printError("README does not contain genotyping_platform key:value.");
        }

        // obo.tsv.gz OPTIONAL
        // #trait_name     obo_term     [obo_term_description]
        // Seed length to width ratio      SOY:0001979
        if (dataFileExists("obo.tsv.gz")) {
            try {
                File file = getDataFile("obo.tsv.gz");
                System.out.println(" - "+file.getName());
                BufferedReader br = GZIPBufferedReader.getReader(file);
                String line = null;
                while ((line=br.readLine())!=null) {
                    if (line.startsWith("#") || line.trim().length()==0) continue; // comment or blank
                    String[] parts = line.split("\t");
                    if (parts.length<2) {
                        printError("File does have at least two values (trait_name,obo_term) in this line:");
                        printError(line);
                        break;
                    }
                }
            } catch (Exception ex) {
                printErrorAndExit(ex.getMessage());
            }
        }

        // result.tsv.gz REQUIRED
        // #trait_name     marker  pvalue
        // SDS root retention      ss107929748     2.0E-5
        if (!dataFileExists("result.tsv.gz")) {
            printErrorAndExit("(Correctly named) result.tsv.gz file is not present in collection.");
        }
        try {
            File file = getDataFile("result.tsv.gz");
            System.out.println(" - "+file.getName());
            BufferedReader br = GZIPBufferedReader.getReader(file);
            String line = null;
            while ((line=br.readLine())!=null) {
                if (line.startsWith("#") || line.trim().length()==0) continue; // comment or blank
                String[] parts = line.split("\t");
                if (parts.length<3) {
                    printError("File does not have at least three values (trait_name,marker,pvalue) in this line:");
                    printError(line);
                    break;
                }
            }
        } catch (Exception ex) {
            printErrorAndExit(ex.getMessage());
        }

        // trait.tsv.gz OPTIONAL
        // #trait_name          description
        // SDS root retention   roots were removed and sent to the Students for a Democratic Society
        if (dataFileExists("trait.tsv.gz")) {
            try {
                File file = getDataFile("trait.tsv.gz");
                System.out.println(" - "+file.getName());
                BufferedReader br = GZIPBufferedReader.getReader(file);
                String line = null;
                while ((line=br.readLine())!=null) {
                    if (line.startsWith("#") || line.trim().length()==0) continue; // comment or blank
                    String[] parts = line.split("\t");
                    if (parts.length!=2) {
                        printError("File does not have two values (trait_name,description) in this line:");
                        printError(line);
                        break;
                    }
                }
            } catch (Exception ex) {
                printErrorAndExit(ex.getMessage());
            }
        }
    }

}
