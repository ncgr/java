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

        // construct our validator and check required files
        AnnotationCollectionValidator validator = new AnnotationCollectionValidator(args[0]);

        // header and required files
        validator.printHeader();
        try {
            validator.checkRequiredFiles();
        } catch (ValidationException ex) {
            printErrorAndExit(ex.getMessage());
        }

        // gene_models_main.gff3.gz (required)
        try {
            File file = validator.getDataFile("gene_models_main.gff3.gz");
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
                if (!validator.hasValidSeqname(featureI)) {
                    validator.printError(file.getName()+" record seqname is invalid:");
                    validator.printError(featureI.toString());
                }
                if (!validator.hasValidGenomicID(featureI)) {
                    validator.printError(file.getName()+" record ID attribute is missing or invalid:");
                    validator.printError(featureI.toString());
                }
                if (!validator.hasValidParent(featureI)) {
                    validator.printError(file.getName()+" record parent attribute is invalid; does the file need sorting?");
                    validator.printError(featureI.toString());
                }
                if (!validator.valid) break;
            }
        } catch (Exception ex) {
            validator.printError(ex.getMessage());
        }

        // protein.faa.gz AND/OR protein_primary.faa.gz
        boolean proteinPresent = false;
        for (String fileType : Arrays.asList("protein.faa.gz","protein_primary.faa.gz")) {
            if (validator.dataFileExists(fileType)) {
                proteinPresent = true;
                try {
                    File file = validator.getDataFile(fileType);
                    System.out.println(" - "+file.getName());
                    Map<String,ProteinSequence> sequenceMap = GZIPFastaReader.readFastaProteinSequence(file);
                    for (ProteinSequence sequence : sequenceMap.values()) {
                        validator.validateSequenceIdentifier(file, sequence);
                    }
                } catch (Exception ex) {
                    validator.printError(ex.getMessage());
                }
            }
        }
        if (!proteinPresent) {
            validator.printError("Neither protein.faa.gz nor protein_primary.faa.gz file is present.");
        }

        // cds.fna.gz AND/OR cds_primary.fna.gz
        boolean cdsPresent = false;
        for (String fileType : Arrays.asList("cds.fna.gz","cds_primary.fna.gz")) {
            if (validator.dataFileExists(fileType)) {
                cdsPresent = true;
                try {
                    File file = validator.getDataFile(fileType);
                    System.out.println(" - "+file.getName());
                    Map<String,DNASequence> sequenceMap = GZIPFastaReader.readFastaDNASequence(file);
                    for (DNASequence sequence : sequenceMap.values()) {
                        validator.validateSequenceIdentifier(file, sequence);
                    }
                } catch (Exception ex) {
                    validator.printError(ex.getMessage());
                }
            }
        }
        if (!cdsPresent) {
            validator.printError("Neither cds.fna.gz nor cds_primary.fna.gz file is present.");
        }

        // mrna.fna.gz AND/OR mrna_primary.fna.gz
        boolean mrnaPresent = false;
        for (String fileType : Arrays.asList("mrna.fna.gz","mrna_primary.fna.gz")) {
            if (validator.dataFileExists(fileType)) {
                mrnaPresent = true;
                try {
                    File file = validator.getDataFile(fileType);
                    System.out.println(" - "+file.getName());
                    Map<String,DNASequence> sequenceMap = GZIPFastaReader.readFastaDNASequence(file);
                    for (DNASequence sequence : sequenceMap.values()) {
                        validator.validateSequenceIdentifier(file, sequence);
                    }
                } catch (Exception ex) {
                    validator.printError(ex.getMessage());
                }
            }
        }
        if (!mrnaPresent) {
            validator.printError("Neither mrna.fna.gz nor mrna_primary.fna.gz file is present.");
        }
        
        // iprscan.gff3.gz (optional)
        if (validator.dataFileExists("iprscan.gff3.gz")) {
            try {
                File file = validator.getDataFile("iprscan.gff3.gz");
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
                    if (!validator.hasValidSeqname(featureI)) {
                        validator.printError(file.getName()+" has an invalid seqname:");
                        validator.printError(featureI.toString());
                    }
                    if (!validator.valid) break;
                }
            } catch (Exception ex) {
                validator.printError(ex.getMessage());
            }
        }            

        // legfed_v1_0.M65K.gfa.tsv.gz (required)
        // #gene   family  protein score
        if (validator.dataFileExists("legfed_v1_0.M65K.gfa.tsv.gz")) {
            File file = validator.getDataFile("legfed_v1_0.M65K.gfa.tsv.gz");
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
                    if (!validator.matchesCollection(geneId)) {
                        validator.printError("Gene ID "+geneId+" in "+file.getName()+" is not a valid LIS identifier:");
                        validator.printError(line);
                    }
                    if (!validator.matchesCollection(proteinId)) {
                        validator.printError("Protein ID "+proteinId+" in "+file.getName()+" is not a valid LIS identifier:");
                        validator.printError(line);
                    }
                    if (!family.startsWith("legfed")) {
                        validator.printError("Gene family identifier "+family+" in "+file.getName()+" is not valid:");
                        validator.printError(line);
                    }
                    if (!validator.valid) break;
                }
            } catch (Exception ex) {
                printErrorAndExit(ex.getMessage());
            }
        }

        // legfed_v1_0.M65K.pathway.tsv.gz (optional)
        // #pathway_identifier  pathway_name  gene
        if (validator.dataFileExists("legfed_v1_0.M65K.pathway.tsv.gz")) {
            File file = validator.getDataFile("legfed_v1_0.M65K.pathway.tsv.gz");
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
                    if (!validator.matchesCollection(geneId)) {
                        validator.printError("Gene ID "+geneId+" in "+file.getName()+" is not a valid LIS identifier:");
                        validator.printError(line);
                    }
                    if (!validator.valid) break;
                }
            } catch (Exception ex) {
                printErrorAndExit(ex.getMessage());
            }
        }

        // phytozome_10_2.HFNR.gfa.tsv.gz (optional)
        // #gene   family  protein score
        if (validator.dataFileExists("phytozome_10_2.HFNR.gfa.tsv.gz")) {
            File file = validator.getDataFile("phytozome_10_2.HFNR.gfa.tsv.gz");
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
                    if (!validator.matchesCollection(geneId)) {
                        validator.printError("Gene ID "+geneId+" in "+file.getName()+" is not a valid LIS identifier:");
                        validator.printError(line);
                    }
                    if (!validator.matchesCollection(proteinId)) {
                        validator.printError("Protein ID "+proteinId+" in "+file.getName()+" is not a valid LIS identifier:");
                        validator.printError(line);
                    }
                    if (!family.startsWith("phytozome")) {
                        validator.printError("Gene family identifier "+family+" in "+file.getName()+" is not valid:");
                        validator.printError(line);
                    }
                    if (!validator.valid) break;
                }
            } catch (Exception ex) {
                printErrorAndExit(ex.getMessage());
            }
        }
        
        // valid!
        if (validator.valid) printIsValidMessage();
    }

}
