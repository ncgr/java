package org.ncgr.gwas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

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
	Option zygosityOption = new Option("z", "zygosity", true, "zygosity for VAR call: HET or HOM");
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
            formatter.printHelp("PangenomicGraph", options);
            System.exit(1);
            return;
        }

	VCFFileReader vcfReader = new VCFFileReader(new File(cmd.getOptionValue("vcffile")));
	BufferedReader phenoReader = new BufferedReader(new FileReader(cmd.getOptionValue("phenofile")));
	String ccVar = cmd.getOptionValue("casecontrolvar");
	String caseValue = cmd.getOptionValue("caseval");
	String controlValue = cmd.getOptionValue("controlval");
	boolean callHomOnly = cmd.getOptionValue("zygosity").toUpperCase().equals("HOM");

	String diseaseVar = null;
	String diseaseName = null;
	if (cmd.hasOption("diseasevar")) {
	    diseaseVar = cmd.getOptionValue("diseasevar");
	    diseaseName = cmd.getOptionValue("diseasename");
	}

	BufferedReader sampleReader = null;
	String sampleVar = null;
	if (cmd.hasOption("samplefile")) {
	    sampleReader = new BufferedReader(new FileReader(cmd.getOptionValue("samplefile")));
	    sampleVar = cmd.getOptionValue("samplevar");
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
	// ##			phv00167455.v2.p2	phv00167456.v2.p2	phv00167457.v2.p2	phv00167458.v2.p2	phv00167459.v2.p2
	// dbGaP_Subject_ID	dbGaP_Sample_ID	BioSample Accession	SUBJID	SAMPID	SAMP_SOURCE	SOURCE_SAMPID	SAMPLE_USE
	// 1284423	1836728	SAMN03897975	PT-1S8D	28278	KAROLINSKA	28278	Seq_DNA_WholeExome; Seq_DNA_SNP_CNV
	Map<String,String> sampleIds = new HashMap<>(); // keyed by dbGaPSubjectId=dbGaP_Subject_ID
	if (sampleReader!=null) {
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
		    String dbGaPSubjectId = data[0]; // assume first column is dbGaP_Subject_ID, which I hope is always true
		    String sampleId = data[sampleVarOffset];
		    sampleIds.put(dbGaPSubjectId, sampleId);
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
	// ##      phv00167460.v2.p2.c1    phv00167461.v2.p2.c1    phv00167462.v2.p2.c1    phv00167463.v2.p2.c1    phv00167464.v2.p2.c1    phv00169020.v2.p2.c1
	// dbGaP_Subject_ID        SUBJID  SEX     PRIMARY_DISEASE ANALYSIS_CAT    SITE    Coverage_Pass
	// 1287483 PT-FJ7E M       Bipolar_Disorder        Case    BROAD   N
        Map<String,Boolean> subjectStatus = new HashMap<>(); // true if case, false if control, keyed by study ID used in VCF
        String line = "";
        boolean headerLine = true;
        int ccVarOffset = -1;
	int diseaseVarOffset = -1;
        int nCases = 0;
        int nControls = 0;
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
		    if (diseaseVar!=null && vars[i].equals(diseaseVar)) diseaseVarOffset = i;
                }
                headerLine = false;
            } else {
                String[] data = line.split("\t");
		String dbGaPSubjectId = data[0]; // assume first column is dbGaP_Subject_ID, which I hope is always true
		String sampleId = data[1]; // if no samples file we assume ID in the second column is used in the VCF
		if (sampleIds.size()>0) {
		    sampleId = sampleIds.get(dbGaPSubjectId);
		}
                String ccValue = data[ccVarOffset];
		String diseaseValue = null;
		if (diseaseVar!=null) diseaseValue = data[diseaseVarOffset];
                boolean isCase = ccValue.equals(caseValue);
                boolean isControl = ccValue.equals(controlValue);
		boolean isDisease = diseaseVar==null || diseaseValue.equals(diseaseName);
                if ((isDisease && isCase) || isControl) {
		    subjectStatus.put(sampleId, isCase); // true = case
		    if (isCase) {
			nCases++;
		    } else {
			nControls++;
		    }
		}
            }
        }

        // initialize FisherExact with max a+b+c+d
        FisherExact fisherExact = new FisherExact(nCases+nControls);

        // GenotypeType:
        // HET         The sample is heterozygous, with at least one ref and at least one one alt in any order
        // HOM_REF     The sample is homozygous reference
        // HOM_VAR     All alleles are non-reference
        // MIXED       Some chromosomes are NO_CALL and others are called
        // NO_CALL     The sample is no-called (all alleles are NO_CALL
        // UNAVAILABLE There is no allele data availble for this sample (alleles.isEmpty)
        for (VariantContext vc : vcfReader) {
            String id = vc.getID();
            String source = vc.getSource();
            String contig = vc.getContig();
            int start = vc.getStart();
            GenotypesContext gc = vc.getGenotypes();
            boolean noCall = false;
            boolean mixed = false;
            boolean unavailable = false;
            int caseRefs = 0;
            int caseVars = 0;
            int controlRefs = 0;
            int controlVars = 0;
	    // spin through the samples of interest
	    for (String sampleId : subjectStatus.keySet()) {
		Genotype g = gc.get(sampleId);
		if (g!=null) {
                    boolean isCase = subjectStatus.get(sampleId);
                    GenotypeType type = g.getType();
                    if (type.equals(GenotypeType.NO_CALL)) {
                        noCall = true;
                    } else if (type.equals(GenotypeType.MIXED)) {
                        mixed = true;
                    } else if (type.equals(GenotypeType.UNAVAILABLE)) {
                        unavailable = true;
                    } else if (type.equals(GenotypeType.HOM_REF)) {
                        if (isCase) caseRefs++; else controlRefs++;
                    } else if (type.equals(GenotypeType.HOM_VAR)) {
                        if (isCase) caseVars++; else controlVars++;
                    } else if (type.equals(GenotypeType.HET)) {
                        if (callHomOnly) {
                            if (isCase) caseRefs++; else controlRefs++;
                        } else {
                            if (isCase) caseVars++; else controlVars++;
                        }
                    }
		}
            }
            if (!noCall && !mixed && !unavailable) {
                // Fisher's exact test on this contingency table
                double p = fisherExact.getP(caseVars, controlVars, caseRefs, controlRefs);
		// Odds ratio
		double or = 0;
		if (caseVars==0 || controlRefs==0) {
		    or = 1e-6; // default if zero numerator to avoid log(0)
		} else if (controlVars==0 || caseRefs==0) {
		    or = 1e+6; // default if zero denominator to avoid infinity
		} else {
		    or = (double)(caseVars*controlRefs)/(double)(controlVars*caseRefs);
		}
		// output the line
                System.out.println(contig+"\t"+start+"\t"+caseVars+"\t"+controlVars+"\t"+caseRefs+"\t"+controlRefs+"\t"+p+"\t"+or);
            }
        }
    }
}