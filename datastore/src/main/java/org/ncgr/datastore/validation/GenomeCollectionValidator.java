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

/**
 * Methods to validate an LIS Datastore /genomes/ collection
 */
public class GenomeCollectionValidator extends CollectionValidator {

    /**
     * Construct from a /genomes/ directory
     */
    public GenomeCollectionValidator(String dirString) {
        super(dirString);
        requiredFileTypes = Arrays.asList("genome_main.fna.gz");
    }

    public static void main(String[] args) {
        if (args.length!=1) {
            System.err.println("Usage: GenomeCollectionValidator [genome directory]");
            System.exit(1);
        }

        // construct our validator and check required files
        GenomeCollectionValidator validator = new GenomeCollectionValidator(args[0]);
        validator.printHeader();
        try {
            validator.checkRequiredFiles();
        } catch (ValidationException ex) {
            printErrorAndExit(ex.getMessage());
        }

        // genome_main.fna.gz
        try {
            File file = validator.getDataFile("genome_main.fna.gz");
            System.out.println(" - "+file.getName());
            Map<String,DNASequence> sequenceMap = GZIPFastaReader.readFastaDNASequence(file);
            for (DNASequence sequence : sequenceMap.values()) {
                validator.validateSequenceIdentifier(file, sequence);
            }
        } catch (Exception ex) {
            validator.printError(ex.getMessage());
        }

        // valid!
        if (validator.valid) printIsValidMessage();
    }
      
}
