package org.ncgr.pangenomics;

import java.io.File;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.GenotypeType;
import htsjdk.variant.variantcontext.GenotypesContext;
import htsjdk.variant.variantcontext.VariantContext;

import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;

public class GenotypeGraph {

    public static void main(String[] args) {
	Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        Option vcfFileOption = new Option("vf", "vcffile", true, "VCF file");
        vcfFileOption.setRequired(true);
        options.addOption(vcfFileOption);
	
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp("GenotypeGraph", options);
            System.exit(1);
            return;
        }

        // spit out help if nothing supplied
        if (cmd.getOptions().length==0) {
            formatter.printHelp("GenotypeGraph", options);
            System.exit(1);
            return;
        }


	// GenotypeType:
	// HET         The sample is heterozygous, with at least one ref and at least one one alt in any order
	// HOM_REF     The sample is homozygous reference
	// HOM_VAR     All alleles are non-reference
	// MIXED       Some chromosomes are NO_CALL and others are called
	// NO_CALL     The sample is no-called (all alleles are NO_CALL)
	// UNAVAILABLE There is no allele data available for this sample (alleles.isEmpty)
	//
	// 1 877558 rs4372192 C T 71.55 PASS AC=1;AF=4.04e-05;AN=24736;BaseQRankSum=-1.369;CCC=24750;... GT:AD:DP:GQ:PL 0/0:7,0:7:21:0,21,281 0/0:7,0:7:21:0,21,218 ...
	//
	VCFFileReader vcfReader = new VCFFileReader(new File(cmd.getOptionValue("vcffile")));
    
	// load the VCF sample names
	VCFHeader vcfHeader = vcfReader.getFileHeader();
	List<String> vcfSampleNames = vcfHeader.getSampleNamesInOrder(); // all subjects in the VCF

	// map GenotypeNodes to the samples that traverse them
	Map<GenotypeNode, List<String>> nodeSamples = new TreeMap<>();

	// map samples to the path they follow
	Map<String, GenotypePath> samplePaths = new TreeMap<>();
	
	// spin through the VCF records storing the samples in the map of GenotypeNodes
	for (VariantContext vc : vcfReader) {
	    String contig = vc.getContig();
	    int start = vc.getStart();
	    for (String sampleName : vcfSampleNames) {
		Genotype g = vc.getGenotype(sampleName);
		GenotypeNode n = new GenotypeNode(contig, start, g.getGenotypeString());
		if (nodeSamples.containsKey(n)) {
		    List<String> samples = nodeSamples.get(n);
		    samples.add(sampleName);
		    nodeSamples.put(n, samples);
		} else {
		    List<String> samples = new ArrayList<>();
		    samples.add(sampleName);
		    nodeSamples.put(n, samples);
		}
		if (samplePaths.containsKey(sampleName)) {
		    GenotypePath path = samplePaths.get(sampleName);
		    path.addNode(n);
		} else {
		    List<GenotypeNode> nodes = new ArrayList<>();
		    nodes.add(n);
		    GenotypePath path = new GenotypePath(sampleName, nodes);
		    samplePaths.put(sampleName, path);
		}
	    }
        }
	// DEBUG
	for (String sampleName : samplePaths.keySet()) {
	    System.out.println(samplePaths.get(sampleName));
	}
	//
    }
}
