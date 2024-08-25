package org.ncgr.datastore.validation;

import org.ncgr.zip.GZIPFastaReader;
import org.ncgr.zip.GZIPBufferedReader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
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
    private static final String TEMPFILE = "/tmp/temp.gff3";
    private static final String GFAPREFIX = "legfed_v1_0.M65K";
    private static final int DEFAULT_MAX_GENES_WITHOUT_NOTES = 100;

    HashSet<String> featureIDs = new HashSet<>();
    Map<String,HashSet<String>> featureTypeIDs = new HashMap<>();
    
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
            printError(ex.getMessage());
        }
        
        // store some identifiers for cross-checks
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
            // just show one error message per type of error
            boolean duplicateFound = false;
            boolean invalidSeqnameFound = false;
            boolean invalidGenomicIDFound = false;
            boolean invalidParentFound = false;
            boolean hasGenes = false;
            int genesWithoutNotes = 0;
            int notesWithoutGOTerms = 0;
            // validate the uncompressed GFF3 file
            FeatureList featureList = GFF3Reader.read(TEMPFILE);
            for (FeatureI featureI : featureList) {
                if (isDuplicateID(featureI) && !duplicateFound) {
                    printError(file.getName()+" record ID is duplicate of one already read:");
                    printError(featureI.toString());
                    duplicateFound = true;
                }
                if (!hasValidSeqname(featureI) && !invalidSeqnameFound) {
                    printError(file.getName()+" record seqname is invalid:");
                    printError(featureI.toString());
                    invalidSeqnameFound = true;
                }
                if (!hasValidGenomicID(featureI) && !invalidGenomicIDFound) {
                    Map<String,String> attributeMap = featureI.getAttributes();
                    printError(file.getName()+" record ID attribute is missing or invalid:");
                    printError(featureI.toString());
                    printError(attributeMap.toString());
                    invalidGenomicIDFound = true;
                }
                if (!hasValidParent(featureI) && !invalidParentFound) {
                    Map<String,String> attributeMap = featureI.getAttributes();
                    printError(file.getName()+" record parent attribute is invalid; does the file need sorting?");
                    printError(featureI.toString());
                    printError(attributeMap.toString());
                    invalidParentFound = true;
                }
                // store ID in featureIDs and featureTypeIDs for future checks
                featureIDs.add(getAttribute(featureI, "ID"));
                String type = featureI.type();
                if (featureTypeIDs.containsKey(type)) {
                    featureTypeIDs.get(type).add(getAttribute(featureI, "ID"));
                } else {
                    HashSet<String> set = new HashSet<>();
                    set.add(getAttribute(featureI, "ID"));
                    featureTypeIDs.put(type, set);
                }
                // set flags for presence of genes and Note attribute and GO terms in Note attribute
                if (type.equals("gene")) {
                    hasGenes = true;
                    String note = getAttribute(featureI, "Note");
                    if (note == null) {
                        genesWithoutNotes++;
                    } else if (!note.contains("GO:")) {
                        notesWithoutGOTerms++;
                    } 
                }
            }
            if (!hasGenes) {
                printError(file.getName() + " does not contain any gene records.");
            }
            // check whether to override the default maxGenesWithoutNotes (a negative value means no limit)
            String mgwn = System.getProperty("maxGenesWithoutNotes");
            int maxGenesWithoutNotes = (mgwn == null ? DEFAULT_MAX_GENES_WITHOUT_NOTES : Integer.parseInt(mgwn));
            if (maxGenesWithoutNotes >= 0 && genesWithoutNotes > maxGenesWithoutNotes) {
                printError(file.getName() + " " + genesWithoutNotes + " gene records are missing the Note attribute.");
            } else if (genesWithoutNotes > 0) {
                printWarning(file.getName() + " " + genesWithoutNotes + " gene records are missing the Note attribute.");
            }
            if (notesWithoutGOTerms > 0) {
                printWarning(file.getName() + " " + notesWithoutGOTerms + " gene record Note attributes are missing GO terms.");
            }
            tempfile.delete();
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
                boolean invalidSeqnameFound = false;
                for (FeatureI featureI : featureList) {
                    if (!hasValidSeqname(featureI) && !invalidSeqnameFound) {
                        printError(file.getName()+" has an invalid seqname:");
                        printError(featureI.toString());
                        invalidSeqnameFound = true;
                    }
                }
            } catch (Exception ex) {
                printError(ex.getMessage());
            }
        } else {
            printWarning("optional iprscan.gff3.gz file is not present.");
        }

        // GFAPREFIX.gfa.tsv.gz (required)
        // Check that the gene identifiers exist in the GFF.
        try {
            HashSet<String> geneIDs = featureTypeIDs.get("gene");
            List<String> gfaGenes = new ArrayList<>();
            List<String> gfaProteins = new ArrayList<>();
            File file = getDataFile(GFAPREFIX+".gfa.tsv.gz");
            System.out.println(" - "+file.getName());
            boolean invalidGeneIdFound = false;
            boolean invalidProteinIdFound = false;
            boolean invalidFamilyIdFound = false;
            boolean missingGeneFound = false;
            boolean missingProteinFound = false;
            BufferedReader br = GZIPBufferedReader.getReader(file);
            String line = null;
            while ((line=br.readLine())!=null) {
                if (line.startsWith("#") || line.startsWith("URL") || line.startsWith("ScoreMeaning") || line.trim().length()==0) continue; // comment or blank
                String[] parts = line.split("\t");
                String geneId = parts[0];
                String family = parts[1];
                String proteinId = parts[2];
                // syntax checks
                if (!matchesCollection(geneId) && !invalidGeneIdFound) {
                    printError("Gene ID "+geneId+" in "+file.getName()+" is not a valid LIS identifier:");
                    printError(line);
                    invalidGeneIdFound = true;
                }
                if (!matchesCollection(proteinId) && !invalidProteinIdFound) {
                    printError("Protein ID "+proteinId+" in "+file.getName()+" is not a valid LIS identifier:");
                    printError(line);
                    invalidProteinIdFound = true;
                }
                if (!family.startsWith("legfed") && !invalidFamilyIdFound) {
                    printError("Gene family identifier "+family+" in "+file.getName()+" is not valid:");
                    printError(line);
                    invalidFamilyIdFound = true;
                }
                // cross-check the gene and protein against the GFF and protein FASTA
                if (!geneIDs.contains(geneId) && !missingGeneFound) {
                    printError("GFA file contains gene ID "+geneId+" that is not present in the gene models GFF file.");
                    missingGeneFound = true;
                }
                if (!fastaProteins.contains(proteinId) && !missingProteinFound) {
                    printError("GFA file contains protein ID "+proteinId+" that is not present in the protein FASTA file(s).");
                    missingProteinFound = true;
                }
            }
        } catch (Exception ex) {
            printErrorAndExit(ex.getMessage());
        }

        // phytozome_10_2.HFNR.gfa.tsv.gz (optional)
        if (dataFileExists("phytozome_10_2.HFNR.gfa.tsv.gz")) {
            File file = getDataFile("phytozome_10_2.HFNR.gfa.tsv.gz");
            System.out.println(" - "+file.getName());
            boolean invalidGeneIdFound = false;
            boolean invalidProteinIdFound = false;
            boolean invalidFamilyIdFound = false;
            try {
                BufferedReader br = GZIPBufferedReader.getReader(file);
                String line = null;
                while ((line=br.readLine())!=null) {
                    if (line.startsWith("#") || line.startsWith("ScoreMeaning") || line.trim().length()==0) continue; // comment or blank
                    String[] parts = line.split("\t");
                    String geneId = parts[0];
                    String family = parts[1];
                    String proteinId = parts[2];
                    if (!matchesCollection(geneId) && !invalidGeneIdFound) {
                        printError("Gene ID "+geneId+" in "+file.getName()+" is not a valid LIS identifier:");
                        printError(line);
                        invalidGeneIdFound = true;
                    }
                    if (!matchesCollection(proteinId) && !invalidProteinIdFound) {
                        printError("Protein ID "+proteinId+" in "+file.getName()+" is not a valid LIS identifier:");
                        printError(line);
                        invalidProteinIdFound = true;
                    }
                    if (!family.startsWith("phytozome") && !invalidFamilyIdFound) {
                        printError("Gene family identifier "+family+" in "+file.getName()+" is not valid:");
                        printError(line);
                        invalidFamilyIdFound = true;
                    }
                }
            } catch (Exception ex) {
                printErrorAndExit(ex.getMessage());
            }
        } else {
            printWarning("optional phytozome_10_2.HFNR.gfa.tsv.gz file is not present.");
        }
    }

    /**
     * Return true if this feature's ID has already been read for the same type in the GFF.
     */
    public boolean isDuplicateID(FeatureI featureI) {
        String id = getAttribute(featureI, "ID");
        String type = featureI.type();
        if (featureTypeIDs.containsKey(type)) {
            HashSet<String> set = featureTypeIDs.get(type);
            return set.contains(id);
        } else {
            return false;
        }
    }

    /**
     * Validate a genomic GFF feature's parent using the featureIDs set to ensure that parents are loaded before children.
     * Note: parent attribute may be a comma-separated list of parents, or not present.
     */
    public boolean hasValidParent(FeatureI featureI) {
        String parent = getAttribute(featureI, "Parent");
        // non-parent is valid
        if (parent==null) return true;
        // one of parent entries must be in featureIDs set
        boolean parentLoaded = false;
        for (String p : parent.split(",")) {
            if (featureIDs.contains(p)) parentLoaded = true;
        }
        return parentLoaded;
    }

}
