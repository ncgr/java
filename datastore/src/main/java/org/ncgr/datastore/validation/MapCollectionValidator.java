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
 * Methods to validate an LIS Datastore /maps/ collection.
 */
public class MapCollectionValidator extends CollectionValidator {

    /**
     * Construct from a /maps/ directory
     */
    public MapCollectionValidator(String dirString) {
        super(dirString);
        requiredFileTypes = Arrays.asList("lg.tsv.gz", "mrk.tsv.gz");
    }

    public static void main(String[] args) {
        if (args.length!=1) {
            System.err.println("Usage: MapCollectionValidator [genome directory]");
            System.exit(1);
        }

        // construct our validator and check required files
        MapCollectionValidator validator = new MapCollectionValidator(args[0]);
        validator.printHeader();
        try {
            validator.checkRequiredFiles();
        } catch (ValidationException ex) {
            printErrorAndExit(ex.getMessage());
        }

        // README must contain genetic_map entry
        if (validator.readme.genetic_map==null) {
            printErrorAndExit(red("genetic_map attribute is missing from README."));
        }

        // lg.tsv.gz
        // #linkage_group  length  chromosome
        // GmComposite1999_A1      96.60   5
        try {
            File file = validator.getDataFile("lg.tsv.gz");
            System.out.println(" - "+file.getName());
            BufferedReader br = GZIPBufferedReader.getReader(file);
            String line = null;
            while ((line=br.readLine())!=null) {
                if (line.startsWith("#") || line.trim().length()==0) continue; // comment or blank
                String[] parts = line.split("\t");
                if (parts.length<2) {
                    validator.printError("File does not at least two values (linkage_group,length,[chromosome]) in this line:");
                    validator.printError(line);
                    break;
                }
            }
        } catch (Exception ex) {
            printErrorAndExit(ex.getMessage());
        }

        // mrk.tsv.gz
        // #marker linkage_group   position
        // A053_2  GmComposite1999_A1      32.20
        try {
            File file = validator.getDataFile("mrk.tsv.gz");
            System.out.println(" - "+file.getName());
            BufferedReader br = GZIPBufferedReader.getReader(file);
            String line = null;
            while ((line=br.readLine())!=null) {
                if (line.startsWith("#") || line.trim().length()==0) continue; // comment or blank
                String[] parts = line.split("\t");
                if (parts.length!=3) {
                    validator.printError("File does not have three values (marker,linkage_group,position) in this line:");
                    validator.printError(line);
                    break;
                }
            }
        } catch (Exception ex) {
            printErrorAndExit(ex.getMessage());
        }
            
        // valid!
        if (validator.valid) printIsValidMessage();
    }

}
    
