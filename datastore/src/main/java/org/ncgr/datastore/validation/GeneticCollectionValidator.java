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
 * Methods to validate an LIS Datastore /genetic/ collection.
 */
public class GeneticCollectionValidator extends CollectionValidator {

    /**
     * Construct from a /genetic/ directory
     * files: obo.tsv.gz, qtlmrk.tsv.gz, qtl.tsv.gz, result.tsv.gz
     */
    public GeneticCollectionValidator(String dirString) {
        super(dirString);
    }

    public static void main(String[] args) {
        if (args.length!=1) {
            System.err.println("Usage: GeneticCollectionValidator [genome directory]");
            System.exit(1);
        }

        // construct our validator and check required files
        GeneticCollectionValidator validator = new GeneticCollectionValidator(args[0]);
        validator.printHeader();

        // obo.tsv.gz
        // #trait_name     obo_term
        // Seed length to width ratio      SOY:0001979
        if (validator.dataFileExists("obo.tsv.gz")) {
            try {
                File file = validator.getDataFile("obo.tsv.gz");
                System.out.println(" - "+file.getName());
                BufferedReader br = GZIPBufferedReader.getReader(file);
                String line = null;
                while ((line=br.readLine())!=null) {
                    if (line.startsWith("#") || line.trim().length()==0) continue; // comment or blank
                    String[] parts = line.split("\t");
                    if (parts.length!=2) {
                        validator.printError("File does have two values (trait_name,obo_term) in this line:");
                        validator.printError(line);
                        break;
                    }
                }
            } catch (Exception ex) {
                printErrorAndExit(ex.getMessage());
            }
        }

        // qtlmrk.tsv.gz
        // #qtl_identifier trait_name      marker  linkage_group
        // Leaflet area 9-1        Leaflet area    BARC-050677-09819       GmComposite2003_C1
        if (validator.dataFileExists("qtlmrk.tsv.gz")) {
            try {
                File file = validator.getDataFile("qtlmrk.tsv.gz");
                System.out.println(" - "+file.getName());
                BufferedReader br = GZIPBufferedReader.getReader(file);
                String line = null;
                while ((line=br.readLine())!=null) {
                    if (line.startsWith("#") || line.trim().length()==0) continue; // comment or blank
                    String[] parts = line.split("\t");
                    if (parts.length!=4) {
                        validator.printError("File does not have four values (qtl_identifier,trait_name,marker,linkage_group) in this line:");
                        validator.printError(line);
                        break;
                    }
                }
            } catch (Exception ex) {
                printErrorAndExit(ex.getMessage());
            }
        }

        // qtl.tsv.gz
        // #qtl_identifier trait_name      linkage_group   start   end     peak
        // First flower 26-5       First flower    GmComposite2003_C2      104.4   106.4   105.4
        if (validator.dataFileExists("qtl.tsv.gz")) {
            try {
                File file = validator.getDataFile("qtl.tsv.gz");
                System.out.println(" - "+file.getName());
                BufferedReader br = GZIPBufferedReader.getReader(file);
                String line = null;
                while ((line=br.readLine())!=null) {
                    if (line.startsWith("#") || line.trim().length()==0) continue; // comment or blank
                    String[] parts = line.split("\t");
                    if (parts.length<2) {
                        validator.printError("File does not at least two values (qtl_identifier,trait_name,[linkage_group,start,end,peak]) in this line:");
                        validator.printError(line);
                        break;
                    }
                }
            } catch (Exception ex) {
                printErrorAndExit(ex.getMessage());
            }
        }

        // result.tsv.gz
        // #trait_name     marker  pvalue
        // SDS root retention      ss107929748     2.0E-5
        if (validator.dataFileExists("result.tsv.gz")) {
            try {
                File file = validator.getDataFile("result.tsv.gz");
                System.out.println(" - "+file.getName());
                BufferedReader br = GZIPBufferedReader.getReader(file);
                String line = null;
                while ((line=br.readLine())!=null) {
                    if (line.startsWith("#") || line.trim().length()==0) continue; // comment or blank
                    String[] parts = line.split("\t");
                    if (parts.length!=3) {
                        validator.printError("File does not have three values (trait_name,marker,pvalue) in this line:");
                        validator.printError(line);
                        break;
                    }
                }
            } catch (Exception ex) {
                printErrorAndExit(ex.getMessage());
            }
        }
        
        // valid!
        if (validator.valid) printIsValidMessage();
    }

}
    
