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
 * Methods to validate an LIS Datastore /genomes/ collection
 */
public class GenomeCollectionValidator extends CollectionValidator {

    public static List<String> requiredFileTypes = Arrays.asList("genome_main.fna.gz");

    /**
     * Construct from an genome directory
     */
    public GenomeCollectionValidator(String dirString) {
        setVars(dirString);
        System.out.println("## ------------------------------------------------------------------------------");
        System.out.println("## Validating genome collection "+collection+" with gensp="+gensp);
    }

    public static void main(String[] args) {
        if (args.length!=1) {
            System.err.println("Usage: GenomeCollectionValidator [genome directory]");
            System.exit(1);
        }

        // construct our validator
        GenomeCollectionValidator validator = new GenomeCollectionValidator(args[0]);

        // check that the required files are present
        for (String fileType : requiredFileTypes) {
            if (!validator.dataFileExists(fileType)) {
                validator.printErrorAndExit("Required file type "+fileType+" is not present in collection.");
            }
        }

        // genome_main.fna.gz
        try {
            File file = validator.getDataFile("genome_main.fna.gz");
            System.out.println("## "+file.getName()+" ...reading...");
            Map<String,DNASequence> sequenceMap = GZIPFastaReader.readFastaDNASequence(file);
            for (DNASequence sequence : sequenceMap.values()) {
                validator.validateSequence(sequence);
            }
        } catch (Exception ex) {
            validator.printError(ex.getMessage());
        }

        // valid!
        if (validator.valid) printIsValidMessage();
    }

}
    
