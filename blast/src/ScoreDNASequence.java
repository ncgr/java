package org.ncgr.blast;

/**
 * Simple main routine to score a DNA sequence with BlastUtils.scoreDNASequence().
 */
public class ScoreDNASequence {

    public static void main(String[] args) {

        if (args.length!=1) {
            System.out.println("Usage: ScoreDNASequence <dna-sequence>");
            System.exit(0);
        }

        System.out.println(args[0]+"\t"+(int) Math.round(100.0*BlastUtils.scoreDNASequence(args[0])));

    }

}
