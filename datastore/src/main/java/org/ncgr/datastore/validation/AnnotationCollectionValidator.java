package org.ncgr.datastore.validation;

import org.ncgr.zip.GZIPFastaReader;
import org.ncgr.zip.GZIPBufferedReader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashSet;
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
    private static final String TEMPGENEFILE = "/tmp/gene_models_main.gff3";
    private static final String TEMPIPRSCANFILE = "/tmp/iprscan.gff3";

    public static List<String> requiredFileTypes = Arrays.asList("gene_models_main.gff3.gz");

    /**
     * Construct from an annotation directory
     */
    public AnnotationCollectionValidator(String dirString) {
        setVars(dirString);
        System.out.println("## ------------------------------------------------------------------------------");
        System.out.println("## Validating annotation collection "+collection+" with gensp="+gensp);
    }

    public static void main(String[] args) {
        if (args.length!=1) {
            System.err.println("Usage: AnnotationCollectionValidator [annotation directory]");
            System.exit(1);
        }

        // construct our validator
        AnnotationCollectionValidator validator = new AnnotationCollectionValidator(args[0]);

        // check that the required files are present
        for (String fileType : requiredFileTypes) {
            if (!validator.dataFileExists(fileType)) {
                validator.printError("Required file type "+fileType+" is not present in collection.");
            }
        }

        // gene_models_main.gff3.gz
        if (validator.dataFileExists("gene_models_main.gff3.gz")) {
            try {
                File file = validator.getDataFile("gene_models_main.gff3.gz");
                System.out.println("## "+file.getName());
                // uncompress the gff3.gz file
                File tempfile = new File(TEMPGENEFILE);
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
                HashSet<String> features = new HashSet<>();
                FeatureList featureList = GFF3Reader.read(TEMPGENEFILE);
                for (FeatureI featureI : featureList) {
                    validator.validateGenomicFeatureI(featureI, features);
                }
            } catch (Exception ex) {
                validator.printError(ex.getMessage());
            }
        } else {
            validator.printError("Required file type gene_models_main.gff3.gz is not present in collection.");
        }            

        // protein.faa.gz AND/OR protein_primary.faa.gz
        boolean proteinPresent = false;
        for (String fileType : Arrays.asList("protein.faa.gz","protein_primary.faa.gz")) {
            if (validator.dataFileExists(fileType)) {
                proteinPresent = true;
                try {
                    File file = validator.getDataFile(fileType);
                    System.out.println("## "+file.getName());
                    Map<String,ProteinSequence> sequenceMap = GZIPFastaReader.readFastaProteinSequence(file);
                    for (ProteinSequence sequence : sequenceMap.values()) {
                        validator.validateSequence(sequence);
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
                    System.out.println("## "+file.getName());
                    Map<String,DNASequence> sequenceMap = GZIPFastaReader.readFastaDNASequence(file);
                    for (DNASequence sequence : sequenceMap.values()) {
                        validator.validateSequence(sequence);
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
                    System.out.println("## "+file.getName());
                    Map<String,DNASequence> sequenceMap = GZIPFastaReader.readFastaDNASequence(file);
                    for (DNASequence sequence : sequenceMap.values()) {
                        validator.validateSequence(sequence);
                    }
                } catch (Exception ex) {
                    validator.printError(ex.getMessage());
                }
            }
        }
        if (!mrnaPresent) {
            validator.printError("Neither mrna.fna.gz nor mrna_primary.fna.gz file is present.");
        }
        
        // iprscan.gff3.gz
        if (validator.dataFileExists("iprscan.gff3.gz")) {
            try {
                File file = validator.getDataFile("iprscan.gff3.gz");
                System.out.println("## "+file.getName());
                // uncompress the gff3.gz file
                File tempfile = new File(TEMPIPRSCANFILE);
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
                FeatureList featureList = GFF3Reader.read(TEMPIPRSCANFILE);
                for (FeatureI featureI : featureList) {
                    validator.validateIPRScanFeatureI(featureI);
                }
            } catch (Exception ex) {
                validator.printError(ex.getMessage());
            }
        }            

        // legfed_v1_0.M65K.gfa.tsv.gz NO VALIDATION
        if (validator.dataFileExists("legfed_v1_0.M65K.gfa.tsv.gz")) {
            File file = validator.getDataFile("legfed_v1_0.M65K.gfa.tsv.gz");
            System.out.println("## "+file.getName()+" (no validation)");
        }

        // legfed_v1_0.M65K.gfa.tsv.gz NO VALIDATION
        if (validator.dataFileExists("legfed_v1_0.M65K.pathway.tsv.gz")) {
            File file = validator.getDataFile("legfed_v1_0.M65K.pathway.tsv.gz");
            System.out.println("## "+file.getName()+" (no validation)");
        }
        
        // valid!
        if (validator.valid) printIsValidMessage();
    }

}
    
