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
        validator.validate();
        // valid!
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

        // README
        if (readme.identifier==null) printError("README.identifier is missing.");
        if (readme.taxid==0) printError("README.taxid is missing.");
        if (readme.synopsis==null) printError("README.synopsis is missing.");
        if (readme.description==null) printError("README.description is missing.");
        if (readme.scientific_name_abbrev==null) printError("README.scientific_name_abbrev is missing.");
        if (readme.chromosome_prefix==null && readme.supercontig_prefix==null) printError("README.chromsome_prefix and README.supercontig_prefix are BOTH missing.");

        // genome_main.fna.gz
        try {
            File file = getDataFile("genome_main.fna.gz");
            System.out.println(" - "+file.getName());
            Map<String,DNASequence> sequenceMap = GZIPFastaReader.readFastaDNASequence(file);
            int chromosomeCount = 0;
            int supercontigCount = 0;
            for (DNASequence sequence : sequenceMap.values()) {
                validateSequenceIdentifier(file, sequence);
                String identifier = getFastaSequenceIdentifier(sequence);
                if (readme.chromosome_prefix!=null && identifier.startsWith(genspStrainGnm+"."+readme.chromosome_prefix)) {
                    chromosomeCount++;
                }
                if (readme.supercontig_prefix!=null && identifier.startsWith(genspStrainGnm+"."+readme.supercontig_prefix)) {
                    supercontigCount++;
                }
            }
            if (chromosomeCount>0 || supercontigCount>0) {
                printInfo(chromosomeCount+" chromosomes and "+supercontigCount+" supercontigs");
            } else {
                printError("No chromosomes or supercontigs found!");
            }
        } catch (Exception ex) {
            printError(ex.getMessage());
        }
    }
      
}
