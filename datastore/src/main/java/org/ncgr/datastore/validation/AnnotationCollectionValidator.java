package org.ncgr.datastore.validation;

import org.ncgr.zip.GZIPFastaReader;
import org.ncgr.zip.GZIPBufferedReader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.template.AbstractSequence;
import org.biojava.nbio.genome.parsers.gff.FeatureI;
import org.biojava.nbio.genome.parsers.gff.FeatureList;
import org.biojava.nbio.genome.parsers.gff.GFF3Reader;

/**
 * Methods to validate an LIS Datastore /annotations/ collection
 */
public class AnnotationCollectionValidator extends CollectionValidator {
    private static final String TEMPFILE = "/tmp/temp.gff3";

    /**
     * Construct from an /annotations/ directory
     */
    public AnnotationCollectionValidator(String dirString) {
        super(dirString);
        requiredFileTypes = Arrays.asList("gene_models_main.gff3.gz");
    }

    public static void main(String[] args) {
        if (args.length!=1) {
            System.err.println("Usage: AnnotationCollectionValidator [annotation directory]");
            System.exit(1);
        }
        AnnotationCollectionValidator validator = new AnnotationCollectionValidator(args[0]);
        validator.validate();
        if (validator.valid) printIsValidMessage();
    }

    /**
     * Validate the current instance.
     */
    public void validate() {
        // header and required files
        printHeader();
        try {
            checkRequiredFiles();
        } catch (ValidationException ex) {
            printErrorAndExit(ex.getMessage());
        }
        
        // gene_models_main.gff3.gz (required)
        try {
            File file = getDataFile("gene_models_main.gff3.gz");
            System.out.println(" - "+file.getName());
            // uncompress the gff3.gz file
            File tempfile = new File(TEMPFILE);
            tempfile.delete();
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempfile));
            BufferedReader reader = GZIPBufferedReader.getReader(file);
            String line = null;
            while ( (line=reader.readLine())!=null ) {
                writer.write(line);
                writer.newLine();
            }
            writer.close();
            // validate the uncompressed GFF3 file
            FeatureList featureList = GFF3Reader.read(TEMPFILE);
            for (FeatureI featureI : featureList) {
                if (!hasValidSeqname(featureI)) {
                    printError(file.getName()+" record seqname is invalid:");
                    printError(featureI.toString());
                }
                if (!hasValidGenomicID(featureI)) {
                    printError(file.getName()+" record ID attribute is missing or invalid:");
                    printError(featureI.toString());
                }
                if (!hasValidParent(featureI)) {
                    printError(file.getName()+" record parent attribute is invalid; does the file need sorting?");
                    printError(featureI.toString());
                }
                if (!valid) break;
            }
        } catch (Exception ex) {
            printError(ex.getMessage());
        }
        
        // protein.faa.gz AND/OR protein_primary.faa.gz
        boolean proteinPresent = false;
        for (String fileType : Arrays.asList("protein.faa.gz","protein_primary.faa.gz")) {
            if (dataFileExists(fileType)) {
                proteinPresent = true;
                try {
                    File file = getDataFile(fileType);
                    System.out.println(" - "+file.getName());
                    Map<String,ProteinSequence> sequenceMap = GZIPFastaReader.readFastaProteinSequence(file);
                    for (ProteinSequence sequence : sequenceMap.values()) {
                        validateSequenceIdentifier(file, sequence);
                    }
                } catch (Exception ex) {
                    printError(ex.getMessage());
                }
            }
        }
        if (!proteinPresent) {
            printError("Neither protein.faa.gz nor protein_primary.faa.gz file is present.");
        }

        // cds.fna.gz AND/OR cds_primary.fna.gz
        boolean cdsPresent = false;
        for (String fileType : Arrays.asList("cds.fna.gz","cds_primary.fna.gz")) {
            if (dataFileExists(fileType)) {
                cdsPresent = true;
                try {
                    File file = getDataFile(fileType);
                    System.out.println(" - "+file.getName());
                    Map<String,DNASequence> sequenceMap = GZIPFastaReader.readFastaDNASequence(file);
                    for (DNASequence sequence : sequenceMap.values()) {
                        validateSequenceIdentifier(file, sequence);
                    }
                } catch (Exception ex) {
                    printError(ex.getMessage());
                }
            }
        }
        if (!cdsPresent) {
            printError("Neither cds.fna.gz nor cds_primary.fna.gz file is present.");
        }

        // mrna.fna.gz AND/OR mrna_primary.fna.gz
        boolean mrnaPresent = false;
        for (String fileType : Arrays.asList("mrna.fna.gz","mrna_primary.fna.gz")) {
            if (dataFileExists(fileType)) {
                mrnaPresent = true;
                try {
                    File file = getDataFile(fileType);
                    System.out.println(" - "+file.getName());
                    Map<String,DNASequence> sequenceMap = GZIPFastaReader.readFastaDNASequence(file);
                    for (DNASequence sequence : sequenceMap.values()) {
                        validateSequenceIdentifier(file, sequence);
                    }
                } catch (Exception ex) {
                    printError(ex.getMessage());
                }
            }
        }
        if (!mrnaPresent) {
            printError("Neither mrna.fna.gz nor mrna_primary.fna.gz file is present.");
        }
        
        // iprscan.gff3.gz (optional)
        if (dataFileExists("iprscan.gff3.gz")) {
            try {
                File file = getDataFile("iprscan.gff3.gz");
                System.out.println(" - "+file.getName());
                // uncompress the gff3.gz file
                File tempfile = new File(TEMPFILE);
                tempfile.delete();
                BufferedWriter writer = new BufferedWriter(new FileWriter(tempfile));
                BufferedReader reader = GZIPBufferedReader.getReader(file);
                String line = null;
                while ( (line=reader.readLine())!=null ) {
                    writer.write(line);
                    writer.newLine();
                }
                writer.close();
                // validate the uncompressed GFF3 file
                FeatureList featureList = GFF3Reader.read(TEMPFILE);
                for (FeatureI featureI : featureList) {
                    if (!hasValidSeqname(featureI)) {
                        printError(file.getName()+" has an invalid seqname:");
                        printError(featureI.toString());
                    }
                    if (!valid) break;
                }
            } catch (Exception ex) {
                printError(ex.getMessage());
            }
        }            

        // legfed_v1_0.M65K.gfa.tsv.gz (required)
        // #gene   family  protein score
        if (dataFileExists("legfed_v1_0.M65K.gfa.tsv.gz")) {
            File file = getDataFile("legfed_v1_0.M65K.gfa.tsv.gz");
            System.out.println(" - "+file.getName());
            try {
                BufferedReader br = GZIPBufferedReader.getReader(file);
                String line = null;
                while ((line=br.readLine())!=null) {
                    if (line.startsWith("#") || line.startsWith("URL") || line.startsWith("ScoreMeaning") || line.trim().length()==0) continue; // comment or blank
                    String[] parts = line.split("\t");
                    String geneId = parts[0];
                    String family = parts[1];
                    String proteinId = parts[2];
                    if (!matchesCollection(geneId)) {
                        printError("Gene ID "+geneId+" in "+file.getName()+" is not a valid LIS identifier:");
                        printError(line);
                    }
                    if (!matchesCollection(proteinId)) {
                        printError("Protein ID "+proteinId+" in "+file.getName()+" is not a valid LIS identifier:");
                        printError(line);
                    }
                    if (!family.startsWith("legfed")) {
                        printError("Gene family identifier "+family+" in "+file.getName()+" is not valid:");
                        printError(line);
                    }
                    if (!valid) break;
                }
            } catch (Exception ex) {
                printErrorAndExit(ex.getMessage());
            }
        }

        // legfed_v1_0.M65K.pathway.tsv.gz (optional)
        // #pathway_identifier  pathway_name  gene
        if (dataFileExists("legfed_v1_0.M65K.pathway.tsv.gz")) {
            File file = getDataFile("legfed_v1_0.M65K.pathway.tsv.gz");
            System.out.println(" - "+file.getName());
            try {
                BufferedReader br = GZIPBufferedReader.getReader(file);
                String line = null;
                while ((line=br.readLine())!=null) {
                    if (line.startsWith("#") || line.startsWith("URL") || line.startsWith("SourceSpecies") || line.trim().length()==0) continue; // comment or blank
                    String[] parts = line.split("\t");
                    String pathwayId = parts[0];
                    String pathwayName = parts[1];
                    String geneId = parts[2];
                    if (!matchesCollection(geneId)) {
                        printError("Gene ID "+geneId+" in "+file.getName()+" is not a valid LIS identifier:");
                        printError(line);
                    }
                    if (!valid) break;
                }
            } catch (Exception ex) {
                printErrorAndExit(ex.getMessage());
            }
        }

        // phytozome_10_2.HFNR.gfa.tsv.gz (optional)
        // #gene   family  protein score
        if (dataFileExists("phytozome_10_2.HFNR.gfa.tsv.gz")) {
            File file = getDataFile("phytozome_10_2.HFNR.gfa.tsv.gz");
            System.out.println(" - "+file.getName());
            try {
                BufferedReader br = GZIPBufferedReader.getReader(file);
                String line = null;
                while ((line=br.readLine())!=null) {
                    if (line.startsWith("#") || line.startsWith("ScoreMeaning") || line.trim().length()==0) continue; // comment or blank
                    String[] parts = line.split("\t");
                    String geneId = parts[0];
                    String family = parts[1];
                    String proteinId = parts[2];
                    if (!matchesCollection(geneId)) {
                        printError("Gene ID "+geneId+" in "+file.getName()+" is not a valid LIS identifier:");
                        printError(line);
                    }
                    if (!matchesCollection(proteinId)) {
                        printError("Protein ID "+proteinId+" in "+file.getName()+" is not a valid LIS identifier:");
                        printError(line);
                    }
                    if (!family.startsWith("phytozome")) {
                        printError("Gene family identifier "+family+" in "+file.getName()+" is not valid:");
                        printError(line);
                    }
                    if (!valid) break;
                }
            } catch (Exception ex) {
                printErrorAndExit(ex.getMessage());
            }
        }
    }

}
