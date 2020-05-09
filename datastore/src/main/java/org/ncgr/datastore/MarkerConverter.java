package org.ncgr.datastore;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Converts a SNP marker file from SoyBase to GFF format.
 *
 * INPUT:
 * 0      1                 2           3          4                        5                      6            7          8                 9             10       11
 * snp_ID snp_name          dbSNP_ID    chromosome gbrowse_L_flank_start_bp gbrowse_R_flank_end_bp snp_start_bp snp_end_bp coordinate_system experiment_ID comments entered_by
 * 1278   BARC-054163-12369 ss107923907 Gm05       27214347                 27215099               27214807     27214808   Glyma1.01         0                      Grant, David
 *
 * OUTPUT:
 * ##gff-version 3
 * ##annot-version Wm82.gnm2.ann1
 * glyma.Wm82.gnm2.ann1.Gm01    soybase    genetic_marker    27214807 27214808   .       -       .       ID=glyma.Wm82.gnm2.ann1.ss107923907;Name=BARC-054163-12369
 */
public class MarkerConverter {

    public static void main(String[] args) throws FileNotFoundException, IOException {

        if (args.length!=5) {
            System.out.println("Usage: MarkerConverter <GWAS file> <source> <coordVersion [Glyma2.0]> <chromosome prefix [glyma.Wm82.gnm2]> <marker prefix [glyma.Wm82.gnm2.ann1]>");
            System.exit(0);
        }

        String inFile = args[0];
        String source = args[1];
        String coordVersion = args[2];
        String chrPrefix = args[3];
        String markerPrefix = args[4];

        // output header lines
        System.out.println("##gff-version 3");
        System.out.println("##annot-version "+chrPrefix);
        
        BufferedReader in = new BufferedReader(new FileReader(inFile));
        String line;
        while ((line=in.readLine())!=null) {
            String[] parts = line.split("\t");
            if (parts[0].equals("snp_ID") || parts.length!=12) {
                // header line or missing data
                continue;
            }
            String snpID = parts[0];
            String snpName = parts[1];
            String dbSNPId = parts[2];
            String chromosome = parts[3];
            String gbFlankStart = parts[4];
            String gbFlankEnd = parts[5];
            String snpStart = parts[6];
            String snpEnd = parts[7];
            String coordSystem = parts[8];
            String exptId = parts[9];
            String comments = parts[10];
            String enteredBy = parts[11];
            // bail if we're not on the correct coordinate version
            if (!coordSystem.equals(coordVersion)) {
                continue;
            }
            // bail if the start/end are not provided
            if (snpStart.length()==0 || snpEnd.length()==0) {
                continue;
            }
            // choose dbSNP_ID as NAME if present, else snp_name
            String name = dbSNPId;
            String altName = snpName;
            if (name.length()==0 || name.equals("NULL")) {
                name = snpName;
                altName = "";
            }
            // prepend the chrPrefix
            String chr = chrPrefix+"."+chromosome;
            // positions
            int start = Integer.parseInt(snpStart); // better be int
            int end = Integer.parseInt(snpEnd); // ditto
            // output
            String attributes = "ID="+markerPrefix+"."+name;
            if (altName.length()>0) attributes += ";Name="+altName;
            // glyma.Wm82.gnm2.Gm01    soybase    marker    27214807 27214808   .       -       .       ID=ss107923907;Name=BARC-054163-12369
            System.out.println(chr+"\t"+source+"\tgenetic_marker\t"+start+"\t"+end+"\t.\t-\t.\t"+attributes);
        }
    }
}
