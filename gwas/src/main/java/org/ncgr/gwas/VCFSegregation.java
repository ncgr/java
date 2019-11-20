package org.ncgr.gwas;

import java.io.File;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import htsjdk.samtools.util.CloseableIterator;
import htsjdk.samtools.util.IntervalList;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

import org.mskcc.cbio.portal.stats.FisherExact;

/**
 * Loads a VCF file and computes segregation between the case and control sammples using Fisher's exact test.
 * Cases and controls are given by a phenotype file.
 *
 * @author Sam Hokin
 */
public class VCFSegregation {

    /**
     * Main class outputs a tab-delimited summary of segregation per locus that has calls for both samples.
     */
    public static void main(String[] args) {
        if (args.length!=5) {
            System.out.println("Usage VCFSegregation <vcf-file> <phenotype-file>");
            System.exit(0);
        }

        String vcfFilename = args[0];
        VCFFileReader reader = new VCFFileReader(new File(vcfFilename));

        String sample1 = args[1];
        String sample2 = args[2];
        int maxSize = Integer.parseInt(args[3]);
        String filetype = args[4];

        boolean tsvOutput = filetype.equals("tsv");
        boolean wiggleOutput = filetype.equals("wig");

        FisherExact fisherExact = new FisherExact(maxSize);

        // output heading
        if (tsvOutput) {
            System.out.println("contig\tstart\tREF\tALT\ta\tb\tc\td\tsize\tp\tmlog10p\tsignif");
        } else if (wiggleOutput) {
            System.out.println("track type=wiggle_0 name="+sample1+"_x_"+sample2);
        }

        String lastContig = "";
        int lastStart = 0;
        for (VariantContext vc : reader) {
            String id = vc.getID();
            String source = vc.getSource();
            String contig = vc.getContig();
            if (!contig.equals(lastContig)) {
                lastContig = contig;
                lastStart = 0;
                if (wiggleOutput) {
                    System.out.println("variableStep chrom="+contig);
                }
            }
            Set<String> sampleNames = vc.getSampleNames();
            if (sampleNames.contains(sample1) && sampleNames.contains(sample2)) {
                int start = vc.getStart();
                if (start!=lastStart) {
                    lastStart = start;
                    Allele ref = vc.getReference();
                    List<Allele> alts = vc.getAlternateAlleles();
                    List<Integer> dp4List = vc.getAttributeAsIntList("DP4", 0);
                    if (dp4List.size()==8) {
                        // output
                        String altString = "";
                        for (Allele alt : alts) {
                            if (altString.length()>0) altString += ",";
                            altString += alt.getBaseString();
                        }
                        int size = dp4List.get(0)+dp4List.get(1)+dp4List.get(2)+dp4List.get(3)+dp4List.get(4)+dp4List.get(5)+dp4List.get(6)+dp4List.get(7);
                        int a = dp4List.get(0)+dp4List.get(1);
                        int b = dp4List.get(2)+dp4List.get(3);
                        int c = dp4List.get(4)+dp4List.get(5);
                        int d = dp4List.get(6)+dp4List.get(7);
                        double p = fisherExact.getP(a, b, c, d);
                        double minusLog10p = -Math.log10(p);
                        boolean significant = (p<0.05);
                        if (tsvOutput) {
                            System.out.println(contig+"\t"+start+"\t"+ref.getBaseString()+"\t"+altString+"\t"+
                                               a+"\t"+b+"\t"+c+"\t"+d+"\t"+size+"\t"+p+"\t"+minusLog10p+"\t"+significant);
                        } else if (wiggleOutput) {
                            System.out.println(start+"\t"+minusLog10p);
                        }
                    }
                }
            }
        }
    }
}
