package org.ncgr.gwas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

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

import org.mskcc.cbio.portal.stats.FisherExact;

/**
 * Loads a VCF file and computes segregation between the case and control samples using Fisher's exact test.
 * Counts are in terms of individual chromosomes:
 *
 *   HOM REF = +2 ref count
 *   HET VAR = +1 ref count, +1 var count
 *   HOM VAR = +2 var count
 *
 * Cases and controls are given by a phenotype file in dbGaP format.
 *
 * @author Sam Hokin
 */
public class VCFSegregation {

    /**
     * Main class outputs a tab-delimited list of the contingency matrix for each locus, plus Fisher's exact test p value.
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

	Option sampleFileOption = new Option("sf", "samplefile", true, "dbGaP samples file (needed if contains mapping from dbGaP_Subject_ID to sample ID used in VCF file)");
	sampleFileOption.setRequired(false);
	options.addOption(sampleFileOption);
        //
	Option sampleVarOption = new Option("sv", "samplevar", true, "study sample ID variable in dbGaP samples file (e.g. SAMPID; required if -sf)");
	sampleVarOption.setRequired(false);
	options.addOption(sampleVarOption);
	//
        Option phenoFileOption = new Option("pf", "phenofile", true, "dbGaP phenotype file");
        phenoFileOption.setRequired(true);
        options.addOption(phenoFileOption);
	//
        Option vcfFileOption = new Option("vf", "vcffile", true, "VCF file");
        vcfFileOption.setRequired(true);
        options.addOption(vcfFileOption);
	//
	Option zygosityOption = new Option("z", "zygosity", true, "zygosity for VAR call: HET or HOM or BOTH (BOTH)");
        zygosityOption.setRequired(true);
        options.addOption(zygosityOption);
        //
        Option ccVarOption = new Option("ccv", "casecontrolvar", true, "case/control variable in dbGaP phenotype file (e.g. ANALYSIS_CAT)");
	ccVarOption.setRequired(true);
        options.addOption(ccVarOption);
        // NOTE: this only allows a single value of case or control in the segregating variable! (Some files have control=1, say, and several case values.)
        Option caseValueOption = new Option("caseval", true, "case value in dbGaP phenotype file (e.g. Case)");
        caseValueOption.setRequired(true);
        options.addOption(caseValueOption);
        //
        Option controlValueOption = new Option("controlval", true, "control value in dbGaP phenotype file (e.g. Control)");
        controlValueOption.setRequired(true);
        options.addOption(controlValueOption);
	//
	Option diseaseVarOption = new Option("dv", "diseasevar", true, "disease variable in dbGaP phenotype file (e.g. PRIMARY_DISEASE; required if -dn)");
        diseaseVarOption.setRequired(false);
        options.addOption(diseaseVarOption);
	//
	Option diseaseNameOption = new Option("dn", "diseasename", true, "desired case disease name in dbGaP phenotype file (e.g. Schizophrenia; required if -dv)");
        diseaseNameOption.setRequired(false);
        options.addOption(diseaseNameOption);
	//
	Option minVarsOption = new Option("mv", "minvars", true, "minimum number of VAR calls for a locus to be output (0)");
	minVarsOption.setRequired(false);
	options.addOption(minVarsOption);
        //
        Option maxNoCallsOption = new Option("mnc", "maxnocalls", true, "maximum number of no-calls for a locus to be output (1000)");
        maxNoCallsOption.setRequired(false);
        options.addOption(maxNoCallsOption);
	//
	Option desiredSexOption = new Option("ds", "desiredsex", true, "phenotype value of desired sex (e.g. M or 1)");
	desiredSexOption.setRequired(false);
	options.addOption(desiredSexOption);
	
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp("VCFSegregation", options);
            System.exit(1);
            return;
        }

        // spit out help if nothing supplied
        if (cmd.getOptions().length==0) {
            formatter.printHelp("VCFSegregation", options);
            System.exit(1);
            return;
        }

	String ccVar = cmd.getOptionValue("casecontrolvar");
	String caseValue = cmd.getOptionValue("caseval");
	String controlValue = cmd.getOptionValue("controlval");
	String sampleVar = cmd.getOptionValue("samplevar");

	boolean callHetOnly = cmd.getOptionValue("zygosity").toUpperCase().equals("HET");
	boolean callHomOnly = cmd.getOptionValue("zygosity").toUpperCase().equals("HOM");
	boolean callBoth = cmd.getOptionValue("zygosity").toUpperCase().equals("BOTH");

	int minVars = 0;
	if (cmd.hasOption("minvars")) {
	    minVars = Integer.parseInt(cmd.getOptionValue("minvars"));
	}

        int maxNoCalls = 1000;
        if (cmd.hasOption("maxnocalls")) {
            maxNoCalls = Integer.parseInt(cmd.getOptionValue("maxnocalls"));
        }

	String diseaseVar = null;
	String diseaseName = null;
	if (cmd.hasOption("diseasevar")) {
	    diseaseVar = cmd.getOptionValue("diseasevar");
	    diseaseName = cmd.getOptionValue("diseasename");
	}

	String desiredSexValue = null;
	if (cmd.hasOption("desiredsex")) {
	    desiredSexValue = cmd.getOptionValue("desiredsex");
	}

	// the optional sample file relates dbGaP_Subject_ID in the phenotypes file to the sample ID used in the VCF file
	//
	// # Study accession: phs000473.v2.p2
	// # Table accession: pht002599.v2.p2
	// # Consent group: All
	// # Citation instructions: The study accession (phs000473.v2.p2) is used to cite the study and its data tables and documents. The data in this file should be cited using the accession....
	// # To cite columns of data within this file, please use the variable (phv#) accessions below:
	// #
	// # 1) the table name and the variable (phv#) accessions below; or
	// # 2) you may cite a variable as phv#.v2.p2
	//
	// dbGaP_Subject_ID dbGaP_Sample_ID BioSample Accession SUBJID  SAMPID SAMP_SOURCE SOURCE_SAMPID SAMPLE_USE
	// 1284423          1836728         SAMN03897975        PT-1S8D	28278  KAROLINSKA  28278         Seq_DNA_WholeExome; Seq_DNA_SNP_CNV
	//
	// NOTE: there may be MORE THAN ONE LINE for the same dbGaP_Subject_ID! We'll assume that SAMPLE IDs are unique.
	//
	Map<String,String> sampleIdMap = new HashMap<>(); // keyed by dbGaPSubjectId=dbGaP_Subject_ID
	if (cmd.hasOption("samplefile")) {
	    BufferedReader sampleReader = new BufferedReader(new FileReader(cmd.getOptionValue("samplefile")));
	    int sampleVarOffset = -1;
	    boolean headerLine = true;
	    String line = null;
	    while ((line=sampleReader.readLine())!=null) {
		if (line.startsWith("#")) {
		    continue; // comment
		} else if (line.trim().length()==0) {
		    continue; // blank
		} else if (headerLine) {
		    // variable header
		    String[] vars = line.split("\t");
		    for (int i=0; i<vars.length; i++) {
			if (vars[i].equals(sampleVar)) sampleVarOffset = i;
		    }
		    headerLine = false;
		} else {
		    String[] data = line.split("\t");
		    String dbGaPSubjectId = data[0]; // assume first column is dbGaP_Subject_ID, which I hope is always true -- not necessarily unique!!
		    String sampleId = data[sampleVarOffset]; // presume this is unique
		    sampleIdMap.put(sampleId, dbGaPSubjectId);
		}
            }
	}

        // the required phenotypes file provides case/control information per sample
	// 
	// # Study accession: phs000473.v2.p2
	// # Table accession: pht002600.v2.p2.c1
	// # Consent group: General Research Use
	// # Citation instructions: The study accession (phs000473.v2.p2) is used to cite the study and its data tables and documents. The data in this file should be cited using the accession pht002600.v2.p2.c1.
	// # To cite columns of data within this file, please use the variable (phv#) accessions below:
	// #
	// # 1) the table name and the variable (phv#) accessions below; or
	// # 2) you may cite a variable as phv#.v2.p2.c1.
	//
	// dbGaP_Subject_ID SUBJID  SEX PRIMARY_DISEASE   ANALYSIS_CAT SITE  Coverage_Pass
	// 1287483          PT-FJ7E M   Bipolar_Disorder  Case         BROAD N
	//
        Map<String,Boolean> subjectStatus = new HashMap<>(); // true if case, false if control, keyed by study ID used in VCF
        String line = "";
        boolean headerLine = true;
        int ccVarOffset = -1;
	int sexVarOffset = -1;
	int diseaseVarOffset = -1;
        int nCases = 0;
        int nControls = 0;
	BufferedReader phenoReader = new BufferedReader(new FileReader(cmd.getOptionValue("phenofile")));
        while ((line=phenoReader.readLine())!=null) {
            if (line.startsWith("#")) {
                continue; // comment
            } else if (line.trim().length()==0) {
                continue; // blank
            } else if (headerLine) {
                // variable header
                String[] vars = line.split("\t");
                for (int i=0; i<vars.length; i++) {
                    if (vars[i].equals(ccVar)) ccVarOffset = i;
		    if (vars[i].equals("SEX")) sexVarOffset = i;
		    if (diseaseVar!=null && vars[i].equals(diseaseVar)) diseaseVarOffset = i;
                }
                headerLine = false;
            } else {
                String[] data = line.split("\t");
		String dbGaPSubjectId = data[0]; // assume first column is dbGaP_Subject_ID, which I hope is always true
		List<String> sampleIds = new ArrayList<>(); // we may have more than one sample ID per dbGaP_Subject_ID!
		if (sampleIdMap.size()==0) {
		    // no samples file, so assume ID in the second column is used in the VCF
		    sampleIds.add(data[1]);
		} else {
		    // spin through the records to get all the sample IDs for this dbGaPSubjectId
		    for (String sampleId : sampleIdMap.keySet()) {
			String dgsId = sampleIdMap.get(sampleId);
			if (dgsId.equals(dbGaPSubjectId)) sampleIds.add(sampleId);
		    }
		}
                String ccValue = data[ccVarOffset];
		String sexValue = data[sexVarOffset];
		String diseaseValue = null;
		if (diseaseVar!=null) diseaseValue = data[diseaseVarOffset];
                boolean isCase = ccValue.equals(caseValue);
                boolean isControl = ccValue.equals(controlValue);
		boolean isDisease = diseaseVar==null || diseaseValue.contains(diseaseName);
		boolean isDesiredSex = true;
		if (desiredSexValue!=null) isDesiredSex = sexValue.equals(desiredSexValue);
                if (((isDisease && isCase) || isControl) && isDesiredSex) {
		    for (String sampleId : sampleIds) {
			subjectStatus.put(sampleId, isCase); // true = case
		    }
		    if (isCase) {
			nCases++;
		    } else {
			nControls++;
		    }
		}
            }
        }

        // initialize FisherExact with max a+b+c+d
        FisherExact fisherExact = new FisherExact(nCases*2+nControls*2);

        // GenotypeType:
        // HET         The sample is heterozygous, with at least one ref and at least one one alt in any order
        // HOM_REF     The sample is homozygous reference
        // HOM_VAR     All alleles are non-reference
        // MIXED       Some chromosomes are NO_CALL and others are called
        // NO_CALL     The sample is no-called (all alleles are NO_CALL)
        // UNAVAILABLE There is no allele data available for this sample (alleles.isEmpty)
        // 1 877558 rs4372192 C T 71.55 PASS AC=1;AF=4.04e-05;AN=24736;BaseQRankSum=-1.369;CCC=24750;... GT:AD:DP:GQ:PL 0/0:7,0:7:21:0,21,281 0/0:7,0:7:21:0,21,218 ...
	VCFFileReader vcfReader = new VCFFileReader(new File(cmd.getOptionValue("vcffile")));

        // find the desired sample names as they appear in the VCF file
        VCFHeader vcfHeader = vcfReader.getFileHeader();
        List<String> vcfSampleNames = vcfHeader.getSampleNamesInOrder(); // all subjects in the VCF
        Set<String> vcfCaseSampleNames = new HashSet<>();                // case subjects in the VCF
        Set<String> vcfControlSampleNames = new HashSet<>();             // control subjects in the VCF
        for (String sampleName : subjectStatus.keySet()) {
            boolean found = false;
            if (vcfSampleNames.contains(sampleName)) {
                found = true;
                if (subjectStatus.get(sampleName)) {
                    vcfCaseSampleNames.add(sampleName);
                } else {
                    vcfControlSampleNames.add(sampleName);
                }
            } else {
                // perhaps the VCF uses sample_sample format
                String doubleSampleName = sampleName+"_"+sampleName;
                if (vcfSampleNames.contains(doubleSampleName)) {
                    found = true;
                    if (subjectStatus.get(sampleName)) {
                        vcfCaseSampleNames.add(doubleSampleName);
                    } else {
                        vcfControlSampleNames.add(doubleSampleName);
                    }
                }
            }
            if (!found) System.err.println("Subject "+sampleName+" NOT FOUND in VCF.");
	}

        // spin through the VCF records, getting case/control counts from case/control subcontexts
        for (VariantContext vc : vcfReader) {
            String id = vc.getID();
            String source = vc.getSource();
            String contig = vc.getContig();
            int start = vc.getStart();
            VariantContext caseVC = vc.subContextFromSamples(vcfCaseSampleNames);
            VariantContext controlVC = vc.subContextFromSamples(vcfControlSampleNames);
            // no-call count criterion
            int caseNoCallCount = caseVC.getNoCallCount();
            int controlNoCallCount = controlVC.getNoCallCount();
            if ((caseNoCallCount+controlNoCallCount)>maxNoCalls) {
                continue;
            }
            // var count criterion
            int caseHomVarCount = caseVC.getHomVarCount();
            int caseHetCount = caseVC.getHetCount();
            int controlHomVarCount = controlVC.getHomVarCount();
            int controlHetCount = controlVC.getHetCount();
            // count var chromosomes
            int caseVars = caseHomVarCount*2 + caseHetCount;
            int controlVars = controlHomVarCount*2 + controlHetCount;
            if ((caseVars+controlVars)<minVars) {
                continue;
            }
            // output this record
            int caseHomRefCount = caseVC.getHomRefCount();
            int controlHomRefCount = controlVC.getHomRefCount();
            // count ref chromosomes
            int caseRefs = caseHomRefCount*2 + caseHetCount;
            int controlRefs = controlHomRefCount*2 + controlHetCount;
            // Fisher's exact test on this contingency table -- always use two-tailed p!!
            double p = fisherExact.getTwoTailedP(caseVars, controlVars, caseRefs, controlRefs);
            // Odds ratio
            double or = (double)(caseVars*controlRefs)/(double)(controlVars*caseRefs);
            // output the line
            System.out.println(contig+"\t"+start+"\t"+id+"\t"+
                               vc.getReference().toString()+"\t"+vc.getAlternateAlleles().toString().replace(" ","")+"\t"+
                               caseVars+"\t"+controlVars+"\t"+caseRefs+"\t"+controlRefs+"\t"+p+"\t"+or+"\t"+
                               caseNoCallCount+"\t"+controlNoCallCount);
        }
    }
}
