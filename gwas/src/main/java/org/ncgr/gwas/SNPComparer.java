package org.ncgr.gwas;

import java.util.List;
import java.util.LinkedList;

import org.biojava.nbio.genome.parsers.gff.FeatureI;
import org.biojava.nbio.genome.parsers.gff.FeatureList;
import org.biojava.nbio.genome.parsers.gff.Location;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Compares SNPs between a "source" VCF showing SNPs on the source genome, a remap/liftover GFF that maps "target" genes back to the "source" genome,
 * a target GFF for the target genome genes, and a target VCF showing SNPs on the target genome.
 *
 * The idea is to list out target locations that have SNPs on the source genome and NOT on the target genome (subject to given filter parameters).
 * Once a target gene passes all the filters it is only output once, along with all of its SNPs.
 *
 * Parameters:
 *
 * -s --sourceVCF  source VCF file
 * -r --remapGFF   remap GFF file
 * -g --targetGFF  target GFF file
 * -t --targetVCF  target VCF file
 * -satm  --sourceAltTotalMin     minimum number of ALT reads on source SNP to be counted
 * -sarrm --sourceAltReadRatioMin minimum ratio of forward/reverse (and vice versa) ALT reads on source SNP
 * -safm  --sourceAltFractionMin  minimum fraction of ALT reads on source SNP
 * -tafm  --targetRefFractionMin  minimum fraction of REF reads on target SNP - all SNPs within target gene must pass this threshold
 *
 * NOTE: only homozygous calls on the source genome are analyzed.
 *
 * @author Sam Hokin
 */
public class SNPComparer {

    // parameter defaults
    static int SOURCE_ALT_TOTAL_MIN = 4;
    static double SOURCE_ALT_READ_RATIO_MIN = 0.1;
    static double SOURCE_ALT_FRACTION_MIN = 0.1;
    static double TARGET_REF_FRACTION_MIN =  0.9;

    /**
     * Main class does all the work.
     */
    public static void main(String[] args) throws Exception {

        Options options = new Options();

        Option sourceVCFOption = new Option("s", "sourceVCF", true, "source VCF file");
        sourceVCFOption.setRequired(true);
        options.addOption(sourceVCFOption);

        Option remapGFFOption = new Option("r", "remapGFF", true, "remap GFF file (target genes on source genome)");
        remapGFFOption.setRequired(true);
        options.addOption(remapGFFOption);

        Option targetGFFOption = new Option("g", "targetGFF", true, "target GFF file");
        targetGFFOption.setRequired(true);
        options.addOption(targetGFFOption);

        Option targetVCFOption = new Option("t", "targetVCF", true, "target VCF file");
        targetVCFOption.setRequired(true);
        options.addOption(targetVCFOption);

        Option sourceAltTotalMinOption = new Option("satm", "sourceAltTotalMin", true, "minimum ALT reads on source to be included ["+SOURCE_ALT_TOTAL_MIN+"]");
        sourceAltTotalMinOption.setRequired(false);
        options.addOption(sourceAltTotalMinOption);

        Option sourceAltReadRatioMinOption = new Option("sarrm", "sourceAltReadRatioMin", true, "minimum ratio of ALT forward/reverse reads on source to be included ["+SOURCE_ALT_READ_RATIO_MIN+"]");
        sourceAltReadRatioMinOption.setRequired(false);
        options.addOption(sourceAltReadRatioMinOption);

        Option sourceAltFractionMinOption = new Option("safm", "sourceAltFractionMin", true, "minimum fraction of ALT calls on source to be included ["+SOURCE_ALT_FRACTION_MIN+"]");
        sourceAltFractionMinOption.setRequired(false);
        options.addOption(sourceAltFractionMinOption);

        Option targetRefFractionMinOption = new Option("trfm", "targetRefFractionMin", true, "minimum fraction of REF calls on target to be included ["+TARGET_REF_FRACTION_MIN+"]");
        targetRefFractionMinOption.setRequired(false);
        options.addOption(targetRefFractionMinOption);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("SNPComparer", options);
            System.exit(1);
            return;
        }

        // filenames
        String sourceVCFFilename = cmd.getOptionValue("sourceVCF");
        String remapGFFFilename = cmd.getOptionValue("remapGFF");
        String targetGFFFilename = cmd.getOptionValue("targetGFF");
        String targetVCFFilename = cmd.getOptionValue("targetVCF");

        // parameter defaults
        int sourceAltTotalMin = SOURCE_ALT_TOTAL_MIN;
        double sourceAltReadRatioMin = SOURCE_ALT_READ_RATIO_MIN;
        double sourceAltFractionMin = SOURCE_ALT_FRACTION_MIN;
        double targetRefFractionMin = TARGET_REF_FRACTION_MIN;

        if (cmd.hasOption("sourceAltTotalMin")) sourceAltTotalMin = Integer.parseInt(cmd.getOptionValue("sourceAltTotalMin"));
        if (cmd.hasOption("sourceAltReadRatioMin")) sourceAltReadRatioMin = Double.parseDouble(cmd.getOptionValue("sourceAltReadRatioMin"));
        if (cmd.hasOption("sourceAltFractionMin")) sourceAltFractionMin = Double.parseDouble(cmd.getOptionValue("sourceAltFractionMin"));
        if (cmd.hasOption("targetRefFractionMin")) targetRefFractionMin = Double.parseDouble(cmd.getOptionValue("targetRefFractionMin"));

        VCFLoader sourceVCFLoader = new VCFLoader(sourceVCFFilename);
        GFFLoader remapGFFLoader = new GFFLoader(remapGFFFilename);
        GFFLoader targetGFFLoader = new GFFLoader(targetGFFFilename);
        VCFLoader targetVCFLoader = new VCFLoader(targetVCFFilename);

        // output the parameters
        System.out.println("org.ncgr.gwas.SNPComparer");
        System.out.println("sourceVCFFilename:"+sourceVCFFilename);
        System.out.println("remapGFFFilename:"+remapGFFFilename);
        System.out.println("targetGFFFilename:"+targetGFFFilename);
        System.out.println("targetVCFFilename:"+targetVCFFilename);
        System.out.println("sourceAltTotalMin="+sourceAltTotalMin);
        System.out.println("sourceAltReadRatioMin="+sourceAltReadRatioMin);
        System.out.println("sourceAltFractionMin="+sourceAltFractionMin);
        System.out.println("targetRefFractionMin="+targetRefFractionMin);
        System.out.println();
        
        // output header
        System.out.println("Gene\tChromosome\tStart\tEnd\tStrand\tMinRefFrac");

        // list keeps track of target genes already output
        List<String> targetGeneList = new LinkedList<>();

        // load and spin through the source VCF file
        sourceVCFLoader.load();
        for (VariantContext sourceVC : sourceVCFLoader.vcList) {
            if (sourceVC.isSNP()) {

                // source VCF values
                String sourceID = sourceVC.getID();
                String sourceContig = sourceVC.getContig();
                int sourceStart = sourceVC.getStart();
                Allele sourceRef = sourceVC.getReference();
                List<Allele> sourceAlts = sourceVC.getAlternateAlleles();
                List<Integer> dp4List = sourceVC.getAttributeAsIntList("DP4", 0);
                int sourceRefForward = dp4List.get(0);
                int sourceRefReverse = dp4List.get(1);
                int sourceAltForward = dp4List.get(2);
                int sourceAltReverse = dp4List.get(3); 
                int sourceRefTotal = sourceRefForward + sourceRefReverse;
                int sourceAltTotal = sourceAltForward + sourceAltReverse;
                String sourceRefString = sourceRef.getBaseString();
                boolean sourceIsHet = sourceAlts.size()>1;
                String sourceAltString = "";
                for (Allele alt : sourceAlts) {
                    if (sourceAltString.length()>0) sourceAltString += ",";
                    sourceAltString += alt.getBaseString();
                }
                double sourceAltFraction = (double)(sourceAltTotal)/(double)(sourceRefTotal+sourceAltTotal);

                // source filtering NOTE: only homozygous calls allowed!
                boolean sourceOK =
                    (!sourceIsHet)
                    && (sourceAltTotal>sourceAltTotalMin)
                    && ((double)Math.min(sourceAltForward,sourceAltReverse)/(double)Math.max(sourceAltForward,sourceAltReverse)>sourceAltReadRatioMin)
                    && (sourceAltFraction>=sourceAltFractionMin);
                
                if (sourceOK) {

                    // search for the target gene(s) spanning this location on the source genome
                    Location sourceLocation = new Location(sourceStart,sourceStart);
                    FeatureList overlapping = remapGFFLoader.search(sourceContig, sourceLocation);
                    for (FeatureI feature : overlapping) {
                        String geneID = feature.getAttribute("ID");
                        // only process new genes
                        if (!targetGeneList.contains(geneID)) {

                            // find this gene on the target genome
                            FeatureList genes = targetGFFLoader.searchID(geneID);
                            for (FeatureI gene : genes) {
                                String chromosome = gene.seqname();
                                String type = gene.type();
                                Location loc = gene.location();
                                int start = loc.start();
                                int end = loc.end();
                                char strand = '+';
                                // if minus strand, indicate with "-" but make start<end
                                if (start<0) {
                                    strand = '-';
                                    int minusStart = -start;
                                    int minusEnd = -end;
                                    start = minusEnd;
                                    end = minusStart;
                                }
                                
                                // now search the target VCF for SNPs on the target genome
                                List<VariantContext> targetVCList = targetVCFLoader.query(chromosome, start, end).toList();
                                boolean targetHasSNPs = targetVCList.size()>0;
                                double minTargetRefFraction = 1.0;
                                for (VariantContext targetVC : targetVCList) {
                                    int targetStart = targetVC.getStart();
                                    Allele targetRef = targetVC.getReference();
                                    List<Allele> targetAlts = targetVC.getAlternateAlleles();
                                    List<Integer> targetDP4List = targetVC.getAttributeAsIntList("DP4", 0);
                                    int targetRefForward = targetDP4List.get(0);
                                    int targetRefReverse = targetDP4List.get(1);
                                    int targetAltForward = targetDP4List.get(2);
                                    int targetAltReverse = targetDP4List.get(3);
                                    int targetRefTotal = targetRefForward + targetRefReverse;
                                    int targetAltTotal = targetAltForward + targetAltReverse;
                                    String targetRefString = targetRef.getBaseString();
                                    String targetAltString = "";
                                    for (Allele alt : targetAlts) {
                                        if (targetAltString.length()>0) targetAltString += ",";
                                        targetAltString += alt.getBaseString();
                                    }
                                    double targetRefFraction = (double)targetRefTotal/(double)(targetRefTotal+targetAltTotal);
                                    minTargetRefFraction = Math.min(targetRefFraction,minTargetRefFraction);
                                }
                                    
                                // output record if passes target filter
                                boolean targetOK = (!targetHasSNPs) || (minTargetRefFraction>=targetRefFractionMin);
                                if (targetOK) {
                                    System.out.println(geneID+"\t"+chromosome+"\t"+start+"\t"+end+"\t"+strand+"\t"+minTargetRefFraction);
                                    targetGeneList.add(geneID);
                                }
                                    
                            }
                            
                        }
                    }
                }
            }
        }
    }

}
