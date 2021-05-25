package org.ncgr.datastore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.LinkedList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import org.biojava.nbio.genome.parsers.gff.FeatureI;
import org.biojava.nbio.genome.parsers.gff.FeatureList;
import org.biojava.nbio.genome.parsers.gff.GeneMarkGTFReader;

import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.GenotypeBuilder;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.VCFFormatHeaderLine;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLine;
import htsjdk.variant.vcf.VCFHeaderLineType;

/**
 * Converts a custom genotyping matrix .gt file to an LIS-standard VCF.
 * A GFF file is specified to map the markers in the gt file to genomic coordinates.
 * 
 * TaxonID 3920
 * MappingPopulation	CB27_x_IT82E-18
 * Parent	CB27
 * Parent	IT82E-18
 * PMID	24659904
 * Lines        CB27/BB-001 CB27/BB-003 CB27/BB-004 ...
 * # comment
 * 2_15811      GT          GG          AT          ...
 *
 * glyma.Wm82.gnm2.Gm01  490  source  G  A  24.4798  .  .  GT:PL  0/0:0,12,92  0/0:0,27,185  0/0:0,114,255  ...
 */
public class GTtoVCFConverter {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        // input
        if (args.length!=3) {
            System.out.println("Usage: GTtoVCFConverter <gt file> <marker GFF file> <output VCF file>");
            System.exit(0);
        }
	String gtFilename = args[0];
        String gffFilename = args[1];
        String vcfFilename = args[2];

        // read in the GFF file
        FeatureList featureList = GeneMarkGTFReader.read(gffFilename);

        // read in the gt file
        VCFHeader vcfHeader = null;
        VariantContextWriterBuilder vcwb = new VariantContextWriterBuilder();
        vcwb.setOutputFile(vcfFilename);
        vcwb.setReferenceDictionary(new SAMSequenceDictionary());
        VariantContextWriter vcfWriter = vcwb.build();
        LinkedHashSet<VCFHeaderLine> metaData = new LinkedHashSet<>();
        String source = null;
        LinkedList<String> genotypeSampleNames = new LinkedList<>();
        String row = null;
        BufferedReader br = new BufferedReader(new FileReader(gtFilename));
        while ((row=br.readLine())!=null) {
            String[] fields = row.split("\t");
            if (row.startsWith("#") || fields.length<2) continue;
            String key = fields[0].trim();
            String value = fields[1].trim();
            if (key.toLowerCase().equals("taxonid")) {
                metaData.add(new VCFHeaderLine("taxon_id", value));
            } else if (key.toLowerCase().equals("genotypingstudy")) {
                metaData.add(new VCFHeaderLine("genotyping_study", value));
                source = value;
            } else if (key.toLowerCase().equals("description")) {
                metaData.add(new VCFHeaderLine("description", value));
            } else if (key.toLowerCase().equals("matrixnotes")) {
                metaData.add(new VCFHeaderLine("notes", value));
            } else if (key.toLowerCase().equals("markertype")) {
                metaData.add(new VCFHeaderLine("marker_type", value));
            } else if (key.toLowerCase().equals("pmid")) {
                metaData.add(new VCFHeaderLine("PMID", value));
            } else if (key.toLowerCase().equals("lines")) {
                // Lines  CB27/BB-001  CB27/BB-003  CB27/BB-004  ...
                int num = fields.length - 1;
                for (int i=0; i<num; i++) {
                    genotypeSampleNames.add(fields[i+1]);
                }
                metaData.add(new VCFFormatHeaderLine("GT", 1, VCFHeaderLineType.String, "Genotype"));
                vcfHeader = new VCFHeader(metaData, genotypeSampleNames);
                vcfWriter.writeHeader(vcfHeader);
            } else {
                // 2_15811  GT  GG  AT  ...
                String marker = fields[0];
                FeatureList fl = featureList.selectByAttribute("Name", marker);
                LinkedList<Allele> noCallAlleles = new LinkedList<>();
                LinkedList<Genotype> genotypes = new LinkedList<>();
                noCallAlleles.add(Allele.NO_CALL);
                noCallAlleles.add(Allele.NO_CALL);
                if (fl.size()>0) {
                    // marker position
                    FeatureI feature = fl.get(0);
                    String contig = feature.seqname();
                    long start = (long) feature.location().bioStart();
                    long stop = (long) feature.location().bioEnd();
                    String gffAlleles = (String) feature.getAttribute("Alleles");
                    String gffAllele1 = null;
                    String gffAllele2 = null;
                    if (gffAlleles.contains("/")) {
                        String parts[] = gffAlleles.split("/");
                        gffAllele1 = parts[0];
                        gffAllele2 = parts[1];
                    }
                    LinkedHashMap<String,Allele> alleles = new LinkedHashMap<>(); // keyed by letter without *
                    boolean isRef = true;
                    for (int i=0; i<genotypeSampleNames.size(); i++) {
                        String sampleName = genotypeSampleNames.get(i);
                        String bothAlleles  = fields[i+1];
                        Genotype genotype = null;
                        if (bothAlleles.length()==1) {
                            // single letter designation, refers to parental inheratance like parent A or B
                            if (gffAllele1!=null && gffAllele2!=null) {
                                if (bothAlleles.toUpperCase().equals("A")) {
                                    // HOM parent A
                                    Allele A = Allele.create(gffAllele1, true);
                                    alleles.put("A", A);
                                    LinkedList<Allele> sampleAlleles = new LinkedList<>();
                                    sampleAlleles.add(A);
                                    sampleAlleles.add(A);
                                    genotype = GenotypeBuilder.create(sampleName, sampleAlleles);
                                } else if (bothAlleles.toUpperCase().equals("B")) {
                                    // HOM parent B
                                    Allele B = Allele.create(gffAllele2, false);
                                    alleles.put("B", B);
                                    LinkedList<Allele> sampleAlleles = new LinkedList<>();
                                    sampleAlleles.add(B);
                                    sampleAlleles.add(B);
                                    genotype = GenotypeBuilder.create(sampleName, sampleAlleles);
                                } else if (bothAlleles.toUpperCase().equals("U")) {
                                    // unknown?
                                    genotype = GenotypeBuilder.create(sampleName, noCallAlleles);
                                } else if (bothAlleles.toUpperCase().equals("X")) {
                                    // both parents?
                                    Allele A = Allele.create(gffAllele1, true);
                                    Allele B = Allele.create(gffAllele2, false);
                                    alleles.put("A", A);
                                    alleles.put("B", B);
                                    LinkedList<Allele> sampleAlleles = new LinkedList<>();
                                    sampleAlleles.add(A);
                                    sampleAlleles.add(B);
                                    genotype = GenotypeBuilder.create(sampleName, sampleAlleles);
                                } else {
                                    System.err.println("Don't know what to do with genotype "+bothAlleles);
                                    System.exit(1);
                                }
                            } else {
                                System.err.println("Don't know what to do with genotype "+bothAlleles);
                                System.exit(1);
                            }
                        } else if (bothAlleles.contains("-")) {
                            // no-call
                            genotype = GenotypeBuilder.create(sampleName, noCallAlleles);
                        } else if (bothAlleles.contains("/")) {
                            // TODO: split
                            System.err.println("Don't know what to do with genotype "+bothAlleles);
                            System.exit(1);
                        } else if (bothAlleles.contains("|")) {
                            // TODO: split
                            System.err.println("Don't know what to do with genotype "+bothAlleles);
                            System.exit(1);
                        } else {
                            // split two adjacent letters, e.g. CT
                            String a1 = String.valueOf(bothAlleles.charAt(0)).toUpperCase();
                            String a2 = String.valueOf(bothAlleles.charAt(1)).toUpperCase();
                            if (!alleles.containsKey(a1)) {
                                alleles.put(a1, Allele.create(a1,isRef));
                                isRef = false;
                            }
                            if (!alleles.containsKey(a2)) {
                                alleles.put(a2, Allele.create(a2,isRef));
                                isRef = false;
                            }
                            LinkedList<Allele> sampleAlleles = new LinkedList<>();
                            sampleAlleles.add(alleles.get(a1));
                            sampleAlleles.add(alleles.get(a2));
                            genotype = GenotypeBuilder.create(sampleName, sampleAlleles);
                        }
                        genotypes.add(genotype);
                    }
                    // create the VariantContext
                    VariantContextBuilder vcBuilder = new VariantContextBuilder();
                    vcBuilder.source(source);
                    vcBuilder.id(marker);
                    vcBuilder.loc(contig, start, stop);
                    try {
                        vcBuilder.alleles(new LinkedList<Allele>(alleles.values()));
                        vcBuilder.genotypes(genotypes);
                    } catch (Exception ex) {
                        System.err.println(ex);
                        System.err.println("----------");
                        System.err.println(row);
                        System.exit(1);
                    }
                    // add the VCF record
                    VariantContext vc = vcBuilder.make();
                    vcfWriter.add(vc);
                }
            }
        }
        vcfWriter.close();
    }
}
