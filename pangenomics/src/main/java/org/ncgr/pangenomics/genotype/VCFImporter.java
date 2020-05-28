package org.ncgr.pangenomics.genotype;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeSet;

import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;

/**
 * Importer for VCF files. Creates public collections that can be used to build a graph.
 *
 * @author Sam Hokin
 */
public class VCFImporter {

    // verbosity flag
    public boolean verbose = false;

    // the File we've imported
    public File vcfFile;

    // the Nodes we import
    public List<Node> nodes;

    // map the sample names to the List of Nodes they traverse
    public Map<String,List<Node>> sampleNodesMap;

    // map the Nodes to the list of samples that traverse them
    public Map<Node,List<String>> nodeSamplesMap;

    /**
     * Read the nodes and paths in from a VCF file.
     *
     * 1 877558 rs4372192 C T 71.55 PASS AC=1;AF=4.04e-05;AN=24736;BaseQRankSum=-1.369;CCC=24750;... GT:AD:DP:GQ:PL 0/0:7,0:7:21:0,21,281 0/0:7,0:7:21:0,21,218 ...
     *
     * NOTE: non-calls (./.) are treated as a true lack of sequence, i.e. no node is created for that sample at that location.
     */
    public void read(File vcfFile, boolean ignorePhase) throws FileNotFoundException, IOException {
        if (verbose) System.out.print("Reading samples and nodes from VCF...");
        // initiate the class collections
        nodes = new ArrayList<>();
        sampleNodesMap = new HashMap<>();
        nodeSamplesMap = new HashMap<>();
        // create the VCF file reader
	VCFFileReader vcfReader = new VCFFileReader(vcfFile);
	// load the VCF sample names
	VCFHeader vcfHeader = vcfReader.getFileHeader();
	List<String> sampleNameList = vcfHeader.getSampleNamesInOrder(); // all subjects in the VCF
        Map<String,Node> nodesMap = new HashMap<>(); // keep track of nodes we've already instantiated
        long nodeId = 0;
	// spin through the VCF records storing the samples and nodes in local maps
	for (VariantContext vc : vcfReader) {
	    for (String sampleName : sampleNameList) {
		Genotype g = vc.getGenotype(sampleName);
                String genotypeString = g.getGenotypeString();
                if (ignorePhase) {
                    genotypeString = genotypeString.replace("|","/");
                    String[] alleles = genotypeString.split("/");
                    if (alleles.length>1 && !alleles[0].equals(alleles[1])) {
                        TreeSet<String> sortedAlleles = new TreeSet<>(Arrays.asList(alleles));
                        boolean first = true;
                        genotypeString = "";
                        for (String allele : sortedAlleles) {
                            if (first) {
                                first = false;
                            } else {
                                genotypeString += "/";
                            }
                            genotypeString += allele;
                        }
                    }
                }
                String nodeString = vc.getContig()+"_"+vc.getStart()+"_"+vc.getEnd()+"_"+genotypeString;
                Node n = nodesMap.get(nodeString);
                if (n==null) {
                    nodeId++;
                    n = new Node(nodeId, vc.getID(), vc.getContig(), vc.getStart(), vc.getEnd(), genotypeString, 0.0); // AF will be updated later
                    nodes.add(n);
                    nodesMap.put(nodeString, n);
                }
                List<Node> sampleNodes = sampleNodesMap.get(sampleName);
                if (sampleNodes==null) sampleNodes = new ArrayList<>();
                sampleNodes.add(n);
                sampleNodesMap.put(sampleName, sampleNodes);
                List<String> nodeSamples = nodeSamplesMap.get(n);
                if (nodeSamples==null) nodeSamples = new ArrayList<>();
                nodeSamples.add(sampleName);
                nodeSamplesMap.put(n, nodeSamples);
	    }
        }
        // update the nodes with their allele frequencies
        for (Node n : nodeSamplesMap.keySet()) {
            List<String> nodeSamples = nodeSamplesMap.get(n);
            n.af = (double)nodeSamples.size() / (double)sampleNameList.size();
        }
        if (verbose) System.out.println("done.");
    }
}
