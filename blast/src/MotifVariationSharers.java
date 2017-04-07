package org.ncgr.blast;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.TreeSet;

import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.compound.NucleotideCompound;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;
import org.biojava.nbio.core.sequence.io.FastaWriterHelper;
import org.biojava.nbio.core.sequence.template.Sequence;

/**
 * Find the set of features which each contain one of each group of motif variations.
 */
public class MotifVariationSharers  {


    public static void main(String[] args) {

        if (args.length<2) {
            System.out.println("Usage: MotifVariationSharers <subject-fasta> <motif-fasta1> [motif-fasta2] [motif-fasta3] ...");
            System.exit(0);
        }

        try {

            FastaReaderHelper frh = new FastaReaderHelper();
            
            LinkedHashMap<String,DNASequence> subjectSequenceMap = frh.readFastaDNASequence(new File(args[0]));

            LinkedHashSet<LinkedHashMap<String,DNASequence>> motifSequenceMaps = new LinkedHashSet<LinkedHashMap<String,DNASequence>>();
            for (int i=1; i<args.length; i++) {
                motifSequenceMaps.add(frh.readFastaDNASequence(new File(args[i])));
            }

            // plow through the subject sequences looking for one of the motif variations from each group
            for (String subjectID : subjectSequenceMap.keySet()) {
                DNASequence subjectSequence = subjectSequenceMap.get(subjectID);
                String subject = subjectSequence.getSequenceAsString();
                boolean containedInAll = true;
                for (LinkedHashMap<String,DNASequence> motifSequenceMap : motifSequenceMaps) {
                    boolean containedInMotifGroup = false;
                    for (String motifID : motifSequenceMap.keySet()) {
                        DNASequence motifSequence = motifSequenceMap.get(motifID);
                        String motif = motifSequence.getSequenceAsString();
                        if (subject.contains(motif)) containedInMotifGroup = true;
                    }
                    containedInAll = containedInAll && containedInMotifGroup;
                }
                if (containedInAll) System.out.println(subjectID);
            }
            





        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }

    }

}
