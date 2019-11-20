package org.ncgr.gwas;

import java.io.File;

import java.util.List;
import java.util.LinkedList;

import htsjdk.samtools.util.CloseableIterator;
import htsjdk.samtools.util.IntervalList;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

/**
 * Loads a VCF file and provides handy methods.
 *
 * @author Sam Hokin
 */
public class VCFLoader {

    public VCFFileReader reader;
    public List<VariantContext> vcList;

    /**
     * Construct by setting the VCFFileReader (but not reading through it, which can take a long time)
     */
    public VCFLoader(String filename) {
        this.reader = new VCFFileReader(new File(filename));
    }

    /**
     * Load the VCF records into the vcList
     */
    public void load() {
        vcList = new LinkedList<VariantContext>();
        for (VariantContext vc : reader) {
            vcList.add(vc);
        }
    }

    /**
     * Main class outputs a tab-delimited remix of a VCF file. Specify whether you want SNPs only.
     */
    public static void main(String[] args) {
        if (args.length==0 || args.length>2) {
            System.out.println("Usage VCFLoader <vcf-file> [allrecords]");
            System.exit(0);
        }

        String vcfFilename = args[0];
        // default is to output only SNP records
        boolean allRecords = false;
        if (args.length>1) {
            allRecords = Boolean.parseBoolean(args[1]);
        }
        
        VCFLoader loader = new VCFLoader(vcfFilename);
        loader.load();

        for (VariantContext vc : loader.vcList) {
            if (allRecords || vc.isSNP()) {
                // values
                String id = vc.getID();
                String source = vc.getSource();
                String contig = vc.getContig();
                List<String> sampleNames = vc.getSampleNamesOrderedByName();
                int start = vc.getStart();
                Allele ref = vc.getReference();
                List<Allele> alts = vc.getAlternateAlleles();
                List<Integer> dp4List = vc.getAttributeAsIntList("DP4", 0);
                // output
                String altString = "";
                for (Allele alt : alts) {
                    if (altString.length()>0) altString += ",";
                    altString += alt.getBaseString();
                }                
                System.out.println(contig+"\t"+start+"\t"+ref.getBaseString()+"\t"+altString+"\t"+dp4List.get(0)+"\t"+dp4List.get(1)+"\t"+dp4List.get(2)+"\t"+dp4List.get(3));
            }
        }
    }

    /**
     * Query for records within the specified region and return a CloseableIterator.
     */
    public CloseableIterator<VariantContext> query(String chrom, int start, int end) {
        return reader.query(chrom, start, end);
    }

    /**
     * Return an iterator over all records.
     */
    public CloseableIterator<VariantContext> iterator() {
        return reader.iterator();
    }
    
}
