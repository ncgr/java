package org.ncgr.datastore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.biojava.nbio.genome.parsers.gff.Location;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

/**
 * Writes marker data from a VCF to a GFF. Also supports a simple tab-delimited file of the form:
 * #Chr	Pos	Marker	Ref	Alt	Qual	Filt	Info	
 * Vu01	532169	1_0052	A	C	999	.	DP=999
 */
public class VCFToGFFConverter {
    public static void main(String[] args) throws FileNotFoundException, IOException {
        if (args.length!=3) {
            System.out.println("Usage: VCFToGFFConverter <VCF/TXT> <source> <input-file>");
            System.exit(0);
        }

	String fileType = args[0];
	String source = args[1];
        String inFile = args[2];

	if (fileType.toUpperCase().equals("VCF")) {
	    // output GFF3 header
	    System.out.println("##gff-version 3");
	    // read in the variants, only using the first ALT allele
	    VCFFileReader reader = new VCFFileReader(new File(inFile));
	    for (VariantContext vc : reader) {
		String id = vc.getID();
		String contig = vc.getContig();
		int start = vc.getStart();
		int end = vc.getEnd();
		Allele ref = vc.getReference();
		Allele alt = vc.getAlternateAlleles().get(0);
		char strand = '.';
		Location location = Location.fromBio(start, end, strand); // 1-based
		String attributes = "ID="+id+";Alleles="+ref.toString()+"/"+alt.toString();
		GFF3Feature feature = new GFF3Feature(contig, source, "genetic_marker", location, 0.0, 0, attributes);
		System.out.println(feature.toString());
	    }
	    reader.close();
	} else if (fileType.toUpperCase().equals("TXT")) {
	    // output GFF3 header
	    System.out.println("##gff-version 3");
	    // read in the variants
	    // 0Chr	1Pos	2Marker	3Ref	4Alt	5Qual	6Filt	7Info	
	    // Vu01	532169	1_0052	A	C	999	.	DP=999
	    BufferedReader reader = new BufferedReader(new FileReader(inFile));
	    String line = null;
	    while ((line=reader.readLine())!=null) {
		if (line.startsWith("#") || line.trim().length()==0) continue;
		String[] fields = line.split("\\t");
		String chr = fields[0];
		int pos = Integer.parseInt(fields[1]);
		String id = fields[2];
		String ref = fields[3];
		String alt = fields[4];
		char strand = '+';
		Location location = Location.fromBio(pos, pos, strand); // 1-based
		String attributes = "ID="+id+";Name="+id+";Alleles="+ref+"/"+alt;
		GFF3Feature feature = new GFF3Feature(chr, source, "genetic_marker", location, 0.0, 0, attributes);
		System.out.println(feature.toString());
	    }
	    reader.close();
	} else {
	    System.err.println("Error: you must specify either VCF or TXT file type.");
	    System.exit(1);
	}	    
    }
}
