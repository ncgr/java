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
import java.util.Set;

/**
 * Methods to validate an LIS Datastore /qtl/ collection.
 */
public class QTLCollectionValidator extends CollectionValidator {

    /**
     * Construct from a /qtl/ directory
     * files: obo.tsv.gz, qtlmrk.tsv.gz, qtl.tsv.gz, trait.tsv.gz
     */
    public QTLCollectionValidator(String dirString) {
        super(dirString);
        requiredFileTypes = Arrays.asList("qtl.tsv.gz");
    }

    public static void main(String[] args) {
        if (args.length!=1) {
            System.err.println("Usage: QTLCollectionValidator [collection directory]");
            System.exit(1);
        }

        // construct our validator and validate
        QTLCollectionValidator validator = new QTLCollectionValidator(args[0]);
        validator.validate();
        if (validator.isValid()) printIsValidMessage();
    }

    /**
     * Validate the current instance.
     */
    public void validate() {
        printHeader();
        try {
            checkRequiredFiles();
        } catch (ValidationException ex) {
            printErrorAndExit(ex.getMessage());
        }

        // README must contain genotype and genetic_map entries
        if (readme.genotype==null) {
            printError("README file is missing genotype key:value.");
        }
        if (readme.genetic_map==null) {
            printError("README file is missing genetic_map key:value.");
        }

        // qtl.tsv.gz REQUIRED
        // #qtl_identifier trait_name    linkage_group start   end     [peak favored_allele_source lod likelihood_ratio  marker_r2 total_r2  additivity]
        // FF5             First flower  C2            104.4   106.4   105.4
        // Store the distinct trait names to check that they match the obo file.
        Set<String> qtlTraitNames = new HashSet<>();
        try {
            File file = getDataFile("qtl.tsv.gz");
            System.out.println(" - "+file.getName());
            BufferedReader br = GZIPBufferedReader.getReader(file);
            String line = null;
            while ((line=br.readLine())!=null) {
                if (line.startsWith("#") || line.trim().length()==0) continue; // comment or blank
                String[] parts = line.split("\t");
                if (parts.length<5) {
                    printError("File does not have required five values (qtl_identifier,trait_name,linkage_group,start,end) in this line:");
                    printError(line);
                    break;
                }
                qtlTraitNames.add(parts[1]);
            }
        } catch (Exception ex) {
            printErrorAndExit(ex.getMessage());
        }

        // qtlmrk.tsv.gz OPTIONAL
        // #qtl_identifier   trait_name    marker             [linkage_group]
        // Leaflet area 9-1  Leaflet area  BARC-050677-09819  GmComposite2003_C1
        if (dataFileExists("qtlmrk.tsv.gz")) {
            try {
                File file = getDataFile("qtlmrk.tsv.gz");
                System.out.println(" - "+file.getName());
                BufferedReader br = GZIPBufferedReader.getReader(file);
                String line = null;
                while ((line=br.readLine())!=null) {
                    if (line.startsWith("#") || line.trim().length()==0) continue; // comment or blank
                    String[] parts = line.split("\t");
                    if (parts.length<3) {
                        printError("File does not have three required values (qtl_identifier,trait_name,marker) in this line:");
                        printError(line);
                        break;
                    }
                }
            } catch (Exception ex) {
                printErrorAndExit(ex.getMessage());
            }
        }

        // obo.tsv.gz OPTIONAL
        // #trait_name     obo_term     [obo_term_description]
        // Seed length to width ratio      SOY:0001979
        // Store the distinct trait names here to be sure the QTL file uses the same names and vice-versa.
        Set<String> oboTraitNames = new HashSet<>();
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
                    oboTraitNames.add(parts[0]);
                }
            } catch (Exception ex) {
                printErrorAndExit(ex.getMessage());
            }
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

        // check that OBO trait names match qtl file.
        if (oboTraitNames.size()>0) {
            for (String oboTraitName : oboTraitNames) {
                if (!qtlTraitNames.contains(oboTraitName)) {
                    printError("OBO file contains a trait that is missing in QTL file: "+oboTraitName);
                }
            }
        }
    }

}
