package org.ncgr.datastore;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Converts a GWAS file previously used by the IM loader to the data store file format.
 * IGV standard has four columns:
 *  CHR: chromosome (aliases chr, chromosome)
 *  BP: nucleotide location (aliases bp, pos, position)
 *  SNP: SNP identifier (aliases snp, rs, rsid, rsnum, id, marker, markername)
 *  P: p-value for the association (aliases p, pval, p-value, pvalue, p.value)
 *
 * INPUT:
 *
 * TaxonID 3847
 * Strain  Williams82
 * Name    KGK20170714.1
 * PlatformName    SoySNP50k
 * PlatformDetails Illumina Infinium BeadChip
 * NumberLociTested        52,041
 * NumberGermplasmTested   12,116
 * Assembly        Wm82.a2.v1
 * DOI     10.3835/plantgenome2015.04.0024
 * #phenotype  ontology_identifier  marker       p_value   chromosome   start     end
 * Seed oil    SOY:0001668          ss715591649  1.12E-09  Gm05         41780982  41780982
 *
 * OUTPUT:
 *
 * #Name=KGK20170714.1
 * #PlatformName=SoySNP50k
 * #PlatformDetails=Illumina Infinium BeadChip
 * #NumberLociTested=52041
 * #NumberGermplasmTested=12116
 * #Assembly=Wm82.a2.v1
 * #DOI=10.3835/plantgenome2015.04.0024
 * CHR                   BP       MARKER       PVAL      BPEND     PHENOTYPE   ONTOLOGY_IDENTIFIER 
 * glyma.Wm82.gnm4.Gm05  4178098  ss715591649  1.12E-09  41780982  Seed oil    SOY:0001668
 */
public class GWASConverter {

    public static void main(String[] args) throws FileNotFoundException, IOException {

        if (args.length!=3) {
            System.out.println("Usage: GWASConverter <GWAS file> <gensp [glyma]> <chromosome prefix [Wm82.gnm2]>");
            System.exit(0);
        }

        String inFile = args[0];
        String gensp = args[1];
        String chrPrefix = args[2];
        
        BufferedReader in = new BufferedReader(new FileReader(inFile));
        String line;
        while ((line=in.readLine())!=null) {
            String[] parts = line.split("\t");
            if (parts.length==1) {
                // skip metadata name without value
            } else if (parts.length==2) {
                // metadata: switch tab to = and output again as comment
                boolean skip = false;
                if (parts[0].toLowerCase().equals("numberlocitested")) parts[1] = parts[1].replace(",","");
                if (parts[0].toLowerCase().equals("taxonid")) skip = true;
                if (parts[0].toLowerCase().equals("strain")) skip = true;
                if (parts[0].toLowerCase().equals("assembly")) parts[1] = parts[1].replace(".v1.1", "").replace(".v1","");
                if (!skip) System.out.println("#"+parts[0]+"="+parts[1]);
            } else if (parts[0].toLowerCase().equals("#phenotype")) {
                // output new header line
                System.out.println("CHR\tBP\tMARKER\tPVAL\tBPEND\tPHENOTYPE\tONTOLOGY_IDENTIFIER");
            } else if (line.startsWith("#")) {
                // echo out a comment line
                System.out.println(line);
            } else {
                //  Seed oil    SOY:0001668          ss715591649  1.12E-09  Gm05         41780982  41780982
                String phenotype = parts[0];
                String ontologyIdentifier = parts[1];
                String marker = gensp+"."+parts[2];
                String pval = parts[3];
                String chr = chrPrefix+"."+parts[4];
                String bp = parts[5];
                String bpEnd = parts[6];
                bp = String.valueOf(Integer.parseInt(bp));
                if (pval.length()>0) pval = String.valueOf(Double.parseDouble(pval));
                if (bpEnd.length()>0) bpEnd = String.valueOf(Integer.parseInt(bpEnd));
                System.out.println(chr+"\t"+bp+"\t"+marker+"\t"+pval+"\t"+bpEnd+"\t"+phenotype+"\t"+ontologyIdentifier);
            }
        }
    }
}
