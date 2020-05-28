package org.ncgr.gwas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.text.DecimalFormat;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.GenotypeType;
import htsjdk.variant.variantcontext.GenotypesContext;
import htsjdk.variant.variantcontext.VariantContext;

import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;

/**
 * Loads a VCF file and computes the Cochran-Armitage Test p-value for segregation between genotypes in a case/control experiment.
 *
 * Cases and controls are given by a phenotype file in dbGaP format, or by a labels file.
 *
 * @author Sam Hokin
 */
public class VCFSegregation {
    static int DEFAULT_MAX_NOCALLS = 1000;
    static double DEFAULT_MIN_MAF = 0.01;

    static DecimalFormat percf = new DecimalFormat("0.000%");
    static DecimalFormat countf = new DecimalFormat("00000");

    /**
     * Main class outputs a tab-delimited list of the contingency matrix for each locus, plus the Cochran-Armitage trend test p value.
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
        phenoFileOption.setRequired(false);
        options.addOption(phenoFileOption);
	//
        Option vcfFileOption = new Option("vf", "vcffile", true, "VCF file");
        vcfFileOption.setRequired(true);
        options.addOption(vcfFileOption);
	//
        Option ccVarOption = new Option("ccv", "casecontrolvar", true, "case/control variable in dbGaP phenotype file (e.g. ANALYSIS_CAT)");
	ccVarOption.setRequired(false);
        options.addOption(ccVarOption);
        // NOTE: this only allows a single value of case or control in the segregating variable! (Some files have control=1, say, and several case values.)
        Option caseValueOption = new Option("caseval", true, "case value in dbGaP phenotype file (e.g. Case)");
        caseValueOption.setRequired(false);
        options.addOption(caseValueOption);
        //
        Option controlValueOption = new Option("controlval", true, "control value in dbGaP phenotype file (e.g. Control)");
        controlValueOption.setRequired(false);
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
	Option desiredSexOption = new Option("ds", "desiredsex", true, "phenotype value of desired sex (e.g. M or 1)");
	desiredSexOption.setRequired(false);
	options.addOption(desiredSexOption);
	//
	Option minMAFOption = new Option("maf", "minmaf", true, "minimum MAF for a locus to be output ("+DEFAULT_MIN_MAF+")");
	minMAFOption.setRequired(false);
	options.addOption(minMAFOption);
        //
        Option maxNoCallsOption = new Option("mnc", "maxnocalls", true, "maximum number of no-calls for a locus to be output ("+DEFAULT_MAX_NOCALLS+")");
        maxNoCallsOption.setRequired(false);
        options.addOption(maxNoCallsOption);
        //
        Option labelFileOption = new Option("lf", "labelfile", true, "label file containing case/control labels for each subject");
        labelFileOption.setRequired(false);
        options.addOption(labelFileOption);
        //
        Option ignorePhaseOption = new Option("ip", "ignorephase", false, "ignore phasing, so that A|T and T|A are counted as same genotype (false)");
        ignorePhaseOption.setRequired(false);
        options.addOption(ignorePhaseOption);
	
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

        // some general parameters
        double minMAF = DEFAULT_MIN_MAF;
        if (cmd.hasOption("minmaf")) {
            minMAF = Double.parseDouble(cmd.getOptionValue("minmaf"));
        }
        int maxNoCalls = DEFAULT_MAX_NOCALLS;
        if (cmd.hasOption("maxnocalls")) {
            maxNoCalls = Integer.parseInt(cmd.getOptionValue("maxnocalls"));
        }
        boolean ignorePhase = cmd.hasOption("ignorephase");

        // true if case, false if control, keyed by sample ID used in VCF
        Map<String,Boolean> subjectStatus = new HashMap<>();
        int nCases = 0;
        int nControls = 0;
        if (cmd.hasOption("labelfile")) {
            // read sample labels from the instance tab-delimited file. Comment lines start with #.
            // 28304	case
            // 60372	ctrl
            String labelFilename = cmd.getOptionValue("labelfile");
            BufferedReader reader = new BufferedReader(new FileReader(labelFilename));
            String line = null;
            while ((line=reader.readLine())!=null) {
                if (!line.startsWith("#")) {
                    String[] fields = line.split("\t");
                    if (fields.length==2) {
                        String sampleId = fields[0];
                        boolean isCase = fields[1].equals("case");
                        boolean isControl = fields[1].equals("ctrl");
                        subjectStatus.put(sampleId, isCase);
                        if (isCase) {
                            nCases++;
                        } else if (isControl) {
                            nControls++;
                        }
                    }
                }
            }
            reader.close();
        } else {
            // read samples from a set of dbGaP files
            String ccVar = cmd.getOptionValue("casecontrolvar");
            String caseValue = cmd.getOptionValue("caseval");
            String controlValue = cmd.getOptionValue("controlval");
            String sampleVar = cmd.getOptionValue("samplevar");
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
            // # Citation instructions: The study accession (phs000473.v2.p2) is used to cite the study and its data tables and documents.
            // # The data in this file should be cited using the accession pht002600.v2.p2.c1.
            // # To cite columns of data within this file, please use the variable (phv#) accessions below:
            // #
            // # 1) the table name and the variable (phv#) accessions below; or
            // # 2) you may cite a variable as phv#.v2.p2.c1.
            //
            // dbGaP_Subject_ID SUBJID  SEX PRIMARY_DISEASE   ANALYSIS_CAT SITE  Coverage_Pass
            // 1287483          PT-FJ7E M   Bipolar_Disorder  Case         BROAD N
            //
            String line = "";
            boolean headerLine = true;
            int ccVarOffset = -1;
            int sexVarOffset = -1;
            int diseaseVarOffset = -1;
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
                    String sexValue = "";
                    if (sexVarOffset>0) sexValue = data[sexVarOffset];
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
        }
        // find the desired sample names as they appear in the VCF file
	VCFFileReader vcfReader = new VCFFileReader(new File(cmd.getOptionValue("vcffile")));
        VCFHeader vcfHeader = vcfReader.getFileHeader();
        List<String> vcfSampleNames = vcfHeader.getSampleNamesInOrder(); // all subjects in the VCF
        Set<String> caseSampleNames = new HashSet<>();                   // case subjects in the VCF
        Set<String> controlSampleNames = new HashSet<>();                // control subjects in the VCF
        for (String sampleName : subjectStatus.keySet()) {
            boolean found = false;
            if (vcfSampleNames.contains(sampleName)) {
                found = true;
                if (subjectStatus.get(sampleName)) {
                    caseSampleNames.add(sampleName);
                } else {
                    controlSampleNames.add(sampleName);
                }
            } else {
                // SPECIAL CASE: perhaps the VCF uses sample_sample format
                String doubleSampleName = sampleName+"_"+sampleName;
                if (vcfSampleNames.contains(doubleSampleName)) {
                    found = true;
                    if (subjectStatus.get(sampleName)) {
                        caseSampleNames.add(doubleSampleName);
                    } else {
                        controlSampleNames.add(doubleSampleName);
                    }
                }
            }
            if (!found) System.err.println("Subject "+sampleName+" NOT FOUND in VCF.");
	}
	
	// spin through the VCF file and get stats on qualified loci
	for (VariantContext vc : vcfReader) {
            String contig = vc.getContig();
            int start = vc.getStart();
            int end = vc.getEnd();
	    String source = vc.getSource();
	    String id = vc.getID();
	    // no-call count criterion
	    int noCallCount = vc.getNoCallCount();
	    if (noCallCount>maxNoCalls) continue;
	    // require at least two genotpes
	    List<Genotype> genotypes = vc.getGenotypes();
	    if (genotypes.size()<2) continue;
	    // minimum MAF criterion
	    int calledCount = vc.getCalledChrCount();
	    int numAboveMAF = 0;
	    for (Allele a : vc.getAlleles()) {
		int count = vc.getCalledChrCount(a);
		double maf = (double)count / (double)calledCount;
		if (maf>minMAF) numAboveMAF++; // includes max allele, usually REF
	    }
	    if (numAboveMAF<2) continue;
	    // get counts for each genotype per case/control
	    Map<String,Integer> caseCounts = new HashMap<>();
	    Map<String,Integer> controlCounts = new HashMap<>();
	    for (Genotype g : genotypes) {
		if (g.isNoCall()) continue;
		String gString = g.getGenotypeString();
                if (ignorePhase) {
                    // sort the alleles in alphabetic order as unphased genotype
                    gString = gString.replace("|","/");
                    String[] alleles = gString.split("/");
                    if (alleles.length>1 && !alleles[0].equals(alleles[1])) {
                        TreeSet<String> sortedAlleles = new TreeSet<>(Arrays.asList(alleles));
                        boolean first = true;
                        gString = "";
                        for (String allele : sortedAlleles) {
                            if (first) {
                                first = false;
                            } else {
                                gString += "/";
                            }
                            gString += allele;
                        }
                    }
                }
		if (!caseCounts.containsKey(gString)) caseCounts.put(gString, 0);
		if (!controlCounts.containsKey(gString)) controlCounts.put(gString, 0);
		String sampleName = g.getSampleName();
		if (caseSampleNames.contains(sampleName)) {
		    caseCounts.put(gString, caseCounts.get(gString)+1);
		} else if (controlSampleNames.contains(sampleName)) {
		    controlCounts.put(gString, controlCounts.get(gString)+1);
		}
	    }
	    // order genotypes by decreasing control counts by using string sorting
	    TreeSet<String> countsGenotypes = new TreeSet<>();
	    for (String gString : controlCounts.keySet()) {
		countsGenotypes.add(countf.format(controlCounts.get(gString))+":"+gString);
	    }
	    // Cochran-Armitage test
	    int numRows = 2;
	    int numCols = countsGenotypes.size();
	    int[][] countTable = new int[numRows][numCols];
	    int[] weights = new int[numCols];
	    // straight allelic association (not additive)
	    weights[0] = 0;
	    for (int i=1; i<weights.length; i++) weights[i] = 1;
	    CochranArmitage ca = new CochranArmitage(weights);
	    // concatenated representation of counts for output
	    String caseString = "";
	    String controlString = "";
	    String genotypeString = "";
	    // order counts by control descending
	    int j = 0;
	    for (String cg : countsGenotypes.descendingSet()) {
		String[] parts = cg.split(":");
		String gString = parts[1];
		if (j>0) {
		    genotypeString += ":";
		    controlString += ":";
		    caseString += ":";
		}
		genotypeString += gString;
		controlString += controlCounts.get(gString);
		caseString += caseCounts.get(gString);
		countTable[0][j] = controlCounts.get(gString);
		countTable[1][j] = caseCounts.get(gString);
		j++;
	    }
	    double pValue = ca.test(countTable);
	    // we can still get a few cases with 0 alternative counts
	    if (Double.isNaN(pValue)) continue;
	    // output the line
	    System.out.println(contig+"\t"+start+"\t"+id+"\t"+
			       genotypeString+"\t"+
			       caseString+"\t"+controlString+"\t"+
			       noCallCount+"\t"+
			       ca.standardStatistic+"\t"+pValue);
	}
    }
}
