package org.ncgr.gwas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.GenotypeType;
import htsjdk.variant.variantcontext.GenotypesContext;
import htsjdk.variant.variantcontext.VariantContext;

import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;

import org.mskcc.cbio.portal.stats.FisherExact;

/**
 * Loads a VCF file and computes segregation between the case and control sammples using Fisher's exact test.
 * Cases and controls are given by a phenotype file in dbGaP format.
 *
 * @author Sam Hokin
 */
public class VCFSegregation {

    /**
     * Main class outputs a tab-delimited summary of segregation per locus that has calls for both samples.
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        if (args.length!=6) {
            System.out.println("Usage VCFSegregation <segregating variable> <case value> <control value> <HET|HOM> <vcf file> <dbGaP phenotype file>");
            System.exit(0);
        }

        // NOTE: this only allows a single value of case or control in the segregating variable!
        String segVar = args[0];
        String caseValue = args[1];
        String controlValue = args[2];
        boolean callHomOnly = args[3].toUpperCase().equals("HOM");
        String vcfFilename = args[4];
        String phenoFilename = args[5];

        VCFFileReader vcfReader = new VCFFileReader(new File(vcfFilename));
        BufferedReader phenoReader = new BufferedReader(new FileReader(phenoFilename));

        // header stuff
        VCFHeader header = vcfReader.getFileHeader();
        List<String> genotypeSamples = header.getGenotypeSamples();

        // # Study accession: phs001071.v1.p1
        // # Table accession: pht005347.v1.p1.c1
        // # Consent group: General Research Use
        // # Citation instructions: The study accession (phs001071.v1.p1) is used to cite the study and its data tables and documents. The data in this file should be cited using the accession pht005347.v1.p1.c1.
        // # To cite columns of data within this file, please use the variable (phv#) accessions below:
        // #
        // # 1) the table name and the variable (phv#) accessions below; or
        // # 2) you may cite a variable as phv#.v1.p1.c1.
        //
        // ## phv00259733.v1.p1.c1 phv00259734.v1.p1.c1 phv00259735.v1.p1.c1 phv00259736.v1.p1.c1 phv00259737.v1.p1.c1 phv00259738.v1.p1.c1 phv00259739.v1.p1.c1 phv00259740.v1.p1.c1
        // dbGaP_Subject_ID SUBJECT_ID affected age_onset age_assessed sex race      CAG_repeat_size_1 CAG_repeat_size_2
        // 1527550          131922     1        66        73           1   Caucasian 40                17
        // 1527552          219281     2        0         47           1   Caucasian 40                19
        // 1527570          436024     3        1         47           2   Caucasian 22                20

        // phenotype data
        Map<String,Boolean> subjectStatus = new HashMap<>(); // true if case, false if control
        String line = "";
        boolean headerLine = true;
        int segVarOffset = -1;
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
                    if (vars[i].equals(segVar)) segVarOffset = i;
                }
                headerLine = false;
            } else {
                String[] data = line.split("\t");
                String sampleName = data[1];
                String segValue = data[segVarOffset];
                boolean isCase = segValue.equals(caseValue);
                boolean isControl = segValue.equals(controlValue);
                if (isCase || isControl) {
                    subjectStatus.put(sampleName, isCase); // true = case
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
            int ctrlRefs = 0;
            int ctrlVars = 0;
            for (Genotype g : gc) {
                String sampleName = g.getSampleName();
                if (subjectStatus.containsKey(sampleName)) {
                    boolean isCase = subjectStatus.get(sampleName);
                    GenotypeType type = g.getType();
                    if (type.equals(GenotypeType.NO_CALL)) {
                        noCall = true;
                    } else if (type.equals(GenotypeType.MIXED)) {
                        mixed = true;
                    } else if (type.equals(GenotypeType.UNAVAILABLE)) {
                        unavailable = true;
                    } else if (type.equals(GenotypeType.HOM_REF)) {
                        if (isCase) caseRefs++; else ctrlRefs++;
                    } else if (type.equals(GenotypeType.HOM_VAR)) {
                        if (isCase) caseVars++; else ctrlVars++;
                    } else if (type.equals(GenotypeType.HET)) {
                        if (callHomOnly) {
                            if (isCase) caseRefs++; else ctrlRefs++;
                        } else {
                            if (isCase) caseVars++; else ctrlVars++;
                        }
                    }
                }
            }
            if (!noCall && !mixed && !unavailable) {
                // Fisher's exact test on this contingency table
                double p = fisherExact.getP(caseVars, ctrlVars, caseRefs, ctrlRefs);
                System.out.println(contig+"\t"+start+"\t"+caseVars+"\t"+ctrlVars+"\t"+caseRefs+"\t"+ctrlRefs+"\t"+p);
            }
        }
    }
}
