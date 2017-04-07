package org.ncgr.blast;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.Set;
import java.util.TreeSet;
import java.text.DecimalFormat;

import org.biojava.nbio.alignment.Alignments;
import org.biojava.nbio.alignment.FractionalIdentityScorer;
import org.biojava.nbio.alignment.FractionalIdentityInProfileScorer;
import org.biojava.nbio.alignment.SimpleGapPenalty;
import org.biojava.nbio.alignment.NeedlemanWunsch;
import org.biojava.nbio.alignment.SmithWaterman;
import org.biojava.nbio.alignment.routines.AnchoredPairwiseSequenceAligner;
import org.biojava.nbio.alignment.routines.GuanUberbacher;
import org.biojava.nbio.alignment.template.AbstractMatrixAligner;
import org.biojava.nbio.alignment.template.GapPenalty;
import org.biojava.nbio.core.alignment.matrices.SubstitutionMatrixHelper;
import org.biojava.nbio.core.alignment.template.AlignedSequence;
import org.biojava.nbio.core.alignment.template.Profile;
import org.biojava.nbio.core.alignment.template.SequencePair;
import org.biojava.nbio.core.alignment.template.SubstitutionMatrix;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.compound.NucleotideCompound;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;
import org.biojava.nbio.core.sequence.io.FastaWriterHelper;
import org.biojava.nbio.core.sequence.template.Sequence;
import org.biojava.nbio.core.util.ConcurrencyTools;

/**
 * Load a multi-fasta containing sequences, and search them for common motifs.
 *
 * @author Sam Hokin
 */
public class SequenceBlaster {

    // BLAST parameters
    static String STRAND = "plus";
    static String WORD_SIZE = "8";
    static String PERC_IDENTITY = "80";
    static boolean UNGAPPED = true;

    // BlastUtils.blastSequenceHits parameters
    static int MAX_MOTIF_LENGTH = 27;
    
    // BioJava alignment parameters
    static double MAX_DISTANCE = 0.2; // maximum distance of a motif from top-scoring motif to be used in sequence logo
    static int GOP = 10;              // gap open penalty for pairwise and multi-sequence alignments
    static int GEP = 1;               // gap extension penalty for pairwise and multi-sequence alignments
    static String ALIGNER = "SmithWaterman"; // AnchoredPairwiseSequenceAligner, GuanUberbacher, NeedlemanWunsch, SmithWaterman

    static DecimalFormat dec = new DecimalFormat("0.0000");
    static DecimalFormat rnd = new DecimalFormat("+00;-00");
    
    public static void main(String[] args) {

        if (args.length<1) {
            System.err.println("Usage: SequenceBlaster <multi-sequence.fasta> [maxDistance] [gop] [gep]");
            System.exit(0);
        }

        // defaults
        double maxDistance = MAX_DISTANCE;
        int gop = GOP;
        int gep = GEP;

        String fastaFilename = args[0];
        if (args.length>1) maxDistance = Double.parseDouble(args[1]);
        if (args.length>3) gop = Integer.parseInt(args[3]);
        if (args.length>4) gep = Integer.parseInt(args[4]);

        GapPenalty gapPenalty = new SimpleGapPenalty(gop, gep);
        SubstitutionMatrix<NucleotideCompound> subMatrix = SubstitutionMatrixHelper.getNuc4_4();

        try {

            // the blastn parameters without the dash
            Map<String,String> blastParameters = new HashMap<String,String>();
            blastParameters.put("strand", STRAND);
            blastParameters.put("word_size", WORD_SIZE);
            blastParameters.put("perc_identity", PERC_IDENTITY);
            if (UNGAPPED) blastParameters.put("ungapped", "");
            
            // timing
            long blastStart = System.currentTimeMillis();

            // Run blast between all the sequences in the provided file, returning a TreeSet of SequenceHits summarizing the results.
            // Uses temp storage to create the many FASTA files used in the BLAST command line.

            // we'll add the found hits to this map of SequenceHits
            TreeMap<String,SequenceHits> seqHitsMap = new TreeMap<String,SequenceHits>();
            FastaReaderHelper frh = new FastaReaderHelper();
            FastaWriterHelper fwh = new FastaWriterHelper();

            // pull out the individual sequences with BioJava help
            File multiFasta = new File(fastaFilename);
            LinkedHashMap<String,DNASequence> sequenceMap = null;
            try {
                sequenceMap = frh.readFastaDNASequence(multiFasta);
            } catch (IOException ex) {
                ex.printStackTrace();
                System.exit(1);
            }

            // loop through each sequence as query against the remaining as subject
            for (DNASequence querySequence : sequenceMap.values()) {
                
                // write out the query fasta
                String queryID = querySequence.getOriginalHeader();
                File queryFile = File.createTempFile("query", ".fasta");
                fwh.writeSequence(queryFile, querySequence);
                String queryFilePath = queryFile.getAbsolutePath();
                
                // create the subject multi-fasta = all sequences but the query sequence
                LinkedHashMap<String,DNASequence> subjectMap = new LinkedHashMap<String,DNASequence>(sequenceMap);
                subjectMap.remove(queryID);
                
                // write out the subject file
                File subjectFile = File.createTempFile("subject", ".fasta");
                fwh.writeNucleotideSequence(subjectFile, subjectMap.values());
                String subjectFilePath = subjectFile.getAbsolutePath();
                
                // now run BLAST with given parameters
                BlastOutput blastOutput = BlastUtils.runBlastn(subjectFilePath, queryFilePath, blastParameters);
                BlastOutputIterations iterations = blastOutput.getBlastOutputIterations();
                if (iterations!=null) {
                    List<Iteration> iterationList = iterations.getIteration();
                    if (iterationList!=null) {
                        for (Iteration iteration : iterationList) {
                            if (iteration.getIterationMessage()==null) {
                                List<Hit> hitList = iteration.getIterationHits().getHit();
                                for (Hit hit : hitList) {
                                    String hitID = hit.getHitDef();
                                    HitHsps hsps = hit.getHitHsps();
                                    if (hsps!=null) {
                                        List<Hsp> hspList = hsps.getHsp();
                                        if (hspList!=null) {
                                            for (Hsp hsp : hspList) {
                                                SequenceHit seqHit = new SequenceHit(queryID, hitID, hsp);
                                                // cull motifs based on their size and content
                                                boolean keep = true;
                                                keep = keep && (seqHit.sequence.contains("C") || seqHit.sequence.contains("G"));
                                                keep = keep && seqHit.sequence.length()<=MAX_MOTIF_LENGTH;
                                                if (keep) {
                                                    if (seqHitsMap.containsKey(seqHit.sequence)) {
                                                        SequenceHits seqHits = seqHitsMap.get(seqHit.sequence);
                                                        seqHits.addSequenceHit(seqHit);
                                                    } else {
                                                        SequenceHits seqHits = new SequenceHits(seqHit);
                                                        seqHitsMap.put(seqHit.sequence, seqHits);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // load the collected SequenceHits into a sorted set
            TreeSet<SequenceHits> seqHitsSet = new TreeSet<SequenceHits>(seqHitsMap.values());
            
            // timing
            long blastEnd = System.currentTimeMillis();

            // now scan through the motifs, doing pairwise alignment with the top one to create a list for logo creation
            long pairwiseStart = System.currentTimeMillis();
            int count = 0;
            boolean first = true;
            DNASequence topMotif = null;
            List<DNASequence> logoMotifs = new ArrayList<DNASequence>();
            for (SequenceHits seqHits : seqHitsSet.descendingSet()) {
                count++;
                if (first) {
                    first = false;
                    // save the top motif for pairwise alignments
                    topMotif = new DNASequence(seqHits.sequence);
                    logoMotifs.add(topMotif);
                    System.out.print(count+"."+seqHits.sequence+"\t["+seqHits.score+"]["+seqHits.uniqueIDs.size()+"]");
                    System.out.println("\tscore\tsimilarity\tdistance");
                } else {
                    // do a pairwise alignment with topMotif and add to logo list if close enough
                    DNASequence thisMotif = new DNASequence(seqHits.sequence);
                    // choose the desired pairwise aligner
                    AbstractMatrixAligner<DNASequence,NucleotideCompound> aligner = null;
                    if (ALIGNER.equals("AnchoredPairwiseSequenceAligner")) {
                        aligner = new AnchoredPairwiseSequenceAligner<DNASequence,NucleotideCompound>(thisMotif, topMotif, gapPenalty, subMatrix);
                    } else if (ALIGNER.equals("GuanUberbacher")) {
                        aligner = new GuanUberbacher<DNASequence,NucleotideCompound>(thisMotif, topMotif, gapPenalty, subMatrix);
                    } else if (ALIGNER.equals("NeedlemanWunsch")) {
                        aligner = new NeedlemanWunsch<DNASequence,NucleotideCompound>(thisMotif, topMotif, gapPenalty, subMatrix);
                    } else if (ALIGNER.equals("SmithWaterman")) {
                        aligner = new SmithWaterman<DNASequence,NucleotideCompound>(thisMotif, topMotif, gapPenalty, subMatrix);
                    } else {
                        System.err.println("ERROR: ALIGNER must be one of AnchoredPairwiseSequenceAligner, GuanUberbacher, NeedlemanWunsch, SmithWaterman");
                        System.exit(1);
                    }
                    double score = aligner.getScore();
                    double distance = aligner.getDistance();
                    double similarity = aligner.getSimilarity();
                    System.out.print(count+"."+seqHits.sequence+"\t["+seqHits.score+"]["+seqHits.uniqueIDs.size()+"]");
                    System.out.print("\t"+rnd.format(score)+"\t"+dec.format(similarity)+"\t"+dec.format(distance));
                    if (distance<maxDistance) {
                        logoMotifs.add(thisMotif);
                        System.out.println("\t*");
                    } else {
                        System.out.println();
                    }
                }
            }

            long pairwiseEnd = System.currentTimeMillis();
            System.out.println();
            System.out.println("------- "+logoMotifs.size()+" motifs gathered for sequence logo -----");

            long multiStart = 0;
            long multiEnd = 0;

            if (logoMotifs.size()>1) {

                // do a multiple alignment of the logo motifs and write to a FASTA for sequence logo generation (which may be uninformative)
                multiStart = System.currentTimeMillis();
                Object[] settings = new Object[3];
                settings[0] = gapPenalty;
                settings[1] = Alignments.PairwiseSequenceScorerType.GLOBAL_IDENTITIES;
                settings[2] = Alignments.ProfileProfileAlignerType.GLOBAL;
                Profile<DNASequence,NucleotideCompound> profile = Alignments.getMultipleSequenceAlignment(logoMotifs, settings);
                multiEnd = System.currentTimeMillis();
                List<DNASequence> dseqs = new ArrayList<DNASequence>();
                for (AlignedSequence aseq : profile) {
                    DNASequence dseq = new DNASequence(aseq.getSequenceAsString());
                    dseq.setOriginalHeader(aseq.getOriginalSequence().getSequenceAsString());
                    dseqs.add(dseq);
                    System.out.println(dseq.getSequenceAsString());
                }
                FastaWriterHelper.writeNucleotideSequence(new File("/tmp/alignment.fasta"), dseqs);

            }

            // timing output
            System.out.println();
            System.out.println("BLAST runs took "+(blastEnd-blastStart)+" ms.");
            System.out.println("Pairwise alignments with top motif took "+(pairwiseEnd-pairwiseStart)+" ms.");
            if (multiStart>0) System.out.println("Multiple sequence alignment took "+(multiEnd-multiStart)+" ms.");

        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        } finally {
            ConcurrencyTools.shutdown();  
        }            

    }
    
}
