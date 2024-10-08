package org.ncgr.datastore.validation;

import org.ncgr.zip.GZIPBufferedReader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.ArrayList;
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
        validator.validate();
        if (validator.isValid()) printIsValidMessage();
    }

    /**
     * Validate this instance.
     */
    public void validate() {
        printHeader();
        try {
            checkRequiredFiles();
        } catch (ValidationException ex) {
            printErrorAndExit(ex.getMessage());
        }

        // README must contain genetic_map entry
        if (readme.genetic_map==null) {
            printErrorAndExit(red("genetic_map attribute is missing from README."));
        }

        // lg.tsv.gz
        // #linkage_group  length  chromosome
        // GmComposite1999_A1      96.60   5
        // load the LG names into a list to check they match those in marker.tsv.gz.
        List<String> lgList = new ArrayList<>();
        try {
            File file = getDataFile("lg.tsv.gz");
            System.out.println(" - "+file.getName());
            BufferedReader br = GZIPBufferedReader.getReader(file);
            String line = null;
            while ((line=br.readLine())!=null) {
                if (line.startsWith("#") || line.trim().length()==0) continue; // comment or blank
                String[] parts = line.split("\t");
                if (parts.length<2) {
                    printError("LG file does not at least two values (linkage_group,length,[chromosome]) in this line:");
                    printError(line);
                    break;
                } else {
                    lgList.add(parts[0]);
                }
                    
            }
        } catch (Exception ex) {
            printErrorAndExit(ex.getMessage());
        }

        // mrk.tsv.gz
        // #marker linkage_group   position
        // A053_2  GmComposite1999_A1      32.20
        // Check that LG name matches one in the lg.tsv file.
        // Also check that marker names aren't empty.
        try {
            File file = getDataFile("mrk.tsv.gz");
            System.out.println(" - "+file.getName());
            BufferedReader br = GZIPBufferedReader.getReader(file);
            String line = null;
            while ((line=br.readLine())!=null) {
                if (line.startsWith("#") || line.trim().length()==0) continue; // comment or blank
                String[] parts = line.split("\t");
                if (parts.length!=3) {
                    printError("Marker file does not have three values (marker,linkage_group,position) in this line:");
                    printError(line);
                    break;
                } else if (!lgList.contains(parts[1])) {
                    printError("Marker file contains an LG identifier missing in LG file in this line:");
                    printError(line);
                    break;
                } else if (parts[0].trim().length()==0) {
                    printError("Marker file has empty marker name in this line:");
                    printError(line);
                    break;
                }
            }
        } catch (Exception ex) {
            printErrorAndExit(ex.getMessage());
        }
    }

}
    
