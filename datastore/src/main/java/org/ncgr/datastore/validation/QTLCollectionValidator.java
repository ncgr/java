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
import java.util.Set;

/**
 * Methods to validate an LIS Datastore /qtl/ collection.
 * The qtl.tsv file is required, since we say a "QTL study" has to be on a genetic map with linkage groups.
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

        // README must contain genotype entries
        if (readme.genotype==null) {
            printError("README file is missing genotype key:value.");
        }

        // qtl.tsv.gz OPTIONAL
        // #qtl_identifier   trait_name    genetic_map      linkage_group start   end     [peak favored_allele_source lod likelihood_ratio  marker_r2 total_r2  additivity]
        // First flower 1-1  First flower  GmComposite2003  C2            104.4   106.4   105.4
        // Store the distinct trait names to check that they match the obo file.
        Set<String> qtlTraitNames = new HashSet<>();
        Set<String> qtlIdentifiers = new HashSet<>();
        try {
            File file = getDataFile("qtl.tsv.gz");
            System.out.println(" - "+file.getName());
            BufferedReader br = GZIPBufferedReader.getReader(file);
            String line = null;
            while ((line=br.readLine())!=null) {
                if (line.trim().length()==0) continue;
                String[] parts = line.split("\t");
                if (line.startsWith("#")) {
                    // check header
                    List<String> partsList = Arrays.asList(parts);
                    if (partsList.contains("#qtl_identifier") &&
                        partsList.contains("trait_name") &&
                        partsList.contains("genetic_map") &&
                        partsList.contains("linkage_group") &&
                        partsList.contains("start") &&
                        partsList.contains("end")) {
                        continue;
                    } else {
                        printError("qtl.tsv.gz file does not have correct header:");
                        printError(line);
                        break;
                    }
                } else if (parts.length<6) {
                    printError("File does not have six required values (qtl_identifier,trait_name,genetic_map,linkage_group,start,end) in this line:");
                    printError(line);
                    break;
                }
                qtlIdentifiers.add(parts[0]);
                qtlTraitNames.add(parts[1]);
            }
        } catch (Exception ex) {
            printErrorAndExit(ex.getMessage());
        }

        // qtlmrk.tsv.gz OPTIONAL
        // #qtl_identifier   trait_name    genetic_map     marker             [linkage_group]
        // Leaflet area 9-1  Leaflet area  GmComposite2003 BARC-050677-09819  GmComposite2003_C1
        Set<String> qtlmrkTraitNames = new HashSet<>();
        Boolean qtlmrkExists = dataFileExists("qtlmrk.tsv.gz");
        if (qtlmrkExists) {
            try {
                File file = getDataFile("qtlmrk.tsv.gz");
                System.out.println(" - "+file.getName());
                BufferedReader br = GZIPBufferedReader.getReader(file);
                String line = null;
                while ((line=br.readLine())!=null) {
                    if (line.trim().length()==0) continue;
                    String[] parts = line.split("\t");
                    if (line.startsWith("#")) {
                        // check header
                        List<String> partsList = Arrays.asList(parts);
                        if (partsList.contains("#qtl_identifier") &&
                            partsList.contains("trait_name") &&
                            partsList.contains("genetic_map") &&
                            partsList.contains("marker")) {
                            continue;
                        } else {
                            printError("qtlmrk.tsv.gz file does not have correct header:");
                            printError(line);
                            break;
                        }
                    } else if (parts.length<4) {
                        printError("qtlmrk.tsv.gz file does not have four required values (qtl_identifier,trait_name,genetic_map,marker) in this line:");
                        printError(line);
                        break;
                    } else if (!qtlIdentifiers.contains(parts[0])) {
                        printError("qtlmrk.tsv.gz file contains a QTL "+parts[0]+" that is not present in qtl.tsv.gz.");
                    }
                    qtlmrkTraitNames.add(parts[1]);
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

        // check that OBO trait names match qtl or qtlmrk file.
        if (oboTraitNames.size()>0) {
            for (String oboTraitName : oboTraitNames) {
                if (!qtlTraitNames.contains(oboTraitName)) {
                    printError("OBO file contains a trait that is missing in qtl.tsv.gz file: "+oboTraitName);
                }
                if (qtlmrkExists && !qtlmrkTraitNames.contains(oboTraitName)) {
                    printError("OBO file contains a trait that is missing in qtlmrk.tsv.gz file: "+oboTraitName);
                }
            }
        }
    }

}
