package org.ncgr.gwas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Performs SNP liftover based on rs# from an existing VCF file using snpBatch files downloaded from NCBI dbSNP.
 * 0           1               2       3           4           5            6    7         8               9           10              11         12                                                
 * #ss#        loc_snp_id      allele  samplesize  rs#         ss2rs_orien  chr  chr_pos   contig_acc      contig_pos  rs2genome_orien assembly   weight
 * ss65979011  SNP_A-1712762   A/G     270         rs11564776  1            1    824920    NT_032977.10    238932      1               GRCh38.p7  1
 * ss65923548  SNP_A-1660027   C/G     270         rs380390    0            1	 196731921 NT_004487.20	   53547334    1               GRCh38.p7  1
 */
public class SNPLifter {

    /**
     * Main class outputs a VCF with the new SNP positions.
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {

	if (args.length!=2) {
	    System.err.println("SNPLifter <VCF file> <snpBatch1,snpBatch2,...>");
	    System.exit(0);
	}

	String vcfFilename = args[0];
	String[] snpBatchFilenames = args[1].split(",");

	// load the snpBatch files into the rs#->chr_pos map
	Map<String,Long> snpPositionMap = new HashMap<>();
	for (String snpBatchFilename : snpBatchFilenames) {
	    BufferedReader snpBatchReader = new BufferedReader(new FileReader(snpBatchFilename));
	    String line = null;
	    while ((line=snpBatchReader.readLine())!=null) {
		if (line.startsWith("#") || line.trim().length()==0) continue; // comment
		String[] fields = line.split("\t");
		if (fields.length!=13) {
		    System.err.println("Line does not have 13 fields:");
		    System.err.println(line);
		    System.exit(1);
		}
		try {
		    snpPositionMap.put(fields[4], Long.parseLong(fields[7]));
		} catch (NumberFormatException e) {
		    // position not determined
		}
	    }
	}

	// Now spin through the VCF file spitting out replaced lines if SNP present in map.
	// 0    1               2               3       4       5       6       7       8       9        ...
	// 1	194967674	rs380390	G	C	.	.	PR	GT	0/1      ...
	// Since the order of positions can change, we need to sort the output VCF data before printing it out
	// for each chromosome.
	TreeMap<String,TreeMap<Long,String>> chrMap = new TreeMap<String,TreeMap<Long,String>>(); // keyed by chromosome
	BufferedReader vcfReader = new BufferedReader(new FileReader(vcfFilename));
	String line = null;
	while ((line=vcfReader.readLine())!=null) {
	    if (line.startsWith("#")) {
		// comments come first so dump them
		System.out.println(line);
		continue;
	    }
	    String[] fields = line.split("\t");
	    String chr = fields[0];
	    if (chr.equals("23")) chr = "X"; // ridiculous
	    if (chr.equals("24")) chr = "Y"; // more ridiculous
	    String rs = fields[2];
	    TreeMap<Long,String> posMap = chrMap.get(chr);
	    if (posMap==null) {
		posMap = new TreeMap<Long,String>(); // keyed by pos on this chr
		chrMap.put(chr, posMap);
	    }
	    if (snpPositionMap.containsKey(rs)) {
		long pos = snpPositionMap.get(rs); // the new position
		String vcfLine = chr+"\t"+pos+"\t"+rs;
		for (int i=3; i<fields.length; i++) vcfLine += "\t"+fields[i];
		posMap.put(pos, vcfLine);
	    } else {
		// rs missing, nothing we can do
	    }
	}
	// Dump out the VCF contents by increasing position per chr
	for (String chr : chrMap.keySet()) {
	    TreeMap<Long,String> posMap = chrMap.get(chr);
	    for (long pos : posMap.keySet()) {
		System.out.println(posMap.get(pos));
	    }
	}
    }
}
