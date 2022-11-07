package org.ncgr.datastore.validation;

import org.ncgr.zip.GZIPFastaReader;
import org.ncgr.zip.GZIPBufferedReader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.ArrayList;
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
    private static final String GFAPREFIX = "legfed_v1_0.M65K";

    /**
     * Construct from an /annotations/ directory
     */
    public AnnotationCollectionValidator(String dirString) {
        super(dirString);
        requiredFileTypes = Arrays.asList("gene_models_main.gff3.gz",
                                          "protein.faa.gz",
                                          "mrna.fna.gz",
                                          "cds.fna.gz",
                                          GFAPREFIX+".gfa.tsv.gz");
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
        
        // store some identifiers for cross-checks
        List<String> gffGenes = new ArrayList<>();
        List<String> fastaProteins = new ArrayList<>();
        
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
                // add the identifier to the list for later cross-checks
                gffGenes.add(getAttribute(featureI, "ID"));
            }
        } catch (Exception ex) {
            printError(ex.getMessage());
        }
        
        // protein.faa.gz (required) and protein_primary.faa.gz (optional)
        for (String fileType : Arrays.asList("protein.faa.gz","protein_primary.faa.gz")) {
            if (dataFileExists(fileType)) {
                try {
                    File file = getDataFile(fileType);
                    System.out.println(" - "+file.getName());
                    Map<String,ProteinSequence> sequenceMap = GZIPFastaReader.readFastaProteinSequence(file);
                    for (ProteinSequence sequence : sequenceMap.values()) {
                        validateSequenceIdentifier(file, sequence);
                        // add the identifier to the list for later cross-checks
                        fastaProteins.add(getFastaSequenceIdentifier(sequence));
                    }
                } catch (Exception ex) {
                    printError(ex.getMessage());
                }
            }
        }

        // cds.fna.gz (required) and cds_primary.fna.gz (optional)
        for (String fileType : Arrays.asList("cds.fna.gz","cds_primary.fna.gz")) {
            if (dataFileExists(fileType)) {
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

        // mrna.fna.gz (required) and mrna_primary.fna.gz (optional)
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
        } else {
            printWarning("Optional iprscan.gff3.gz file is not present.");
        }

        // GFAPREFIX.gfa.tsv.gz (required)
        // Check that the FIRST record gene and protein identifiers exist in the GFF and protein FASTA
        // (It takes too long to check every record.)
        try {
            List<String> gfaGenes = new ArrayList<>();
            List<String> gfaProteins = new ArrayList<>();
            File file = getDataFile(GFAPREFIX+".gfa.tsv.gz");
            System.out.println(" - "+file.getName());
            BufferedReader br = GZIPBufferedReader.getReader(file);
            String line = null;
            boolean first = true;
            while ((line=br.readLine())!=null) {
                if (line.startsWith("#") || line.startsWith("URL") || line.startsWith("ScoreMeaning") || line.trim().length()==0) continue; // comment or blank
                String[] parts = line.split("\t");
                String geneId = parts[0];
                String family = parts[1];
                String proteinId = parts[2];
                // syntax checks
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
                // cross-check the first gene and protein against the GFF and protein FASTA
                if (first) {
                    if (!gffGenes.contains(geneId)) {
                        printError("GFA file contains gene ID "+geneId+" that is not present in the gene models GFF file.");
                    }
                    if (!fastaProteins.contains(proteinId)) {
                        printError("GFA file contains protein ID "+proteinId+" that is not present in the protein FASTA file(s).");
                    }
                    first = false;
                }
                if (!valid) break;
            }
        } catch (Exception ex) {
            printErrorAndExit(ex.getMessage());
        }

        // pathway.tsv.gz OR GFAPREFIX.pathway.tsv.gz (optional)
        if (dataFileExists("pathway.tsv.gz") || dataFileExists(GFAPREFIX+".pathway.tsv.gz")) {
            File file = getDataFile("pathway.tsv.gz");
            if (!file.exists()) file = getDataFile(GFAPREFIX+".pathway.tsv.gz");
            System.out.println(" - "+file.getName());
            try {
                BufferedReader br = GZIPBufferedReader.getReader(file);
                String line = null;
                while ((line=br.readLine())!=null) {
                    if (line.startsWith("#") || line.startsWith("URL") || line.startsWith("SourceSpecies") || line.trim().length()==0) continue; // comment or blank
                    String[] parts = line.split("\t");
                    if (parts.length==3) {
                        String pathwayId = parts[0];
                        String pathwayName = parts[1];
                        String geneId = parts[2];
                        if (!matchesCollection(geneId)) {
                            printError("Gene ID "+geneId+" in "+file.getName()+" is not a valid LIS identifier:");
                            printError(line);
                        }
                    } else {
                        printError("pathway file "+file.getName()+" contains data line with other than exactly 3 columns:");
                        printError(line);
                    }                        
                    if (!valid) break;
                }
            } catch (Exception ex) {
                printErrorAndExit(ex.getMessage());
            }
        } else {
            printWarning("Optional pathway.tsv.gz file is not present.");
        }

        // phytozome_10_2.HFNR.gfa.tsv.gz (optional)
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
        } else {
            printWarning("Optional phytozome_10_2.HFNR.gfa.tsv.gz file is not present.");
        }
    }

}
