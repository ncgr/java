package org.ncgr.datastore;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.Map;
import java.util.HashMap;

/**
 * Converts a Cmap file to the set of files used in the LIS datastore.
 * 0                  1        2         3         4                             5            6               7              8            9                10
 * map_acc            map_name map_start map_stop  feature_acc                   feature_name feature_aliases feature_start  feature_stop feature_type_acc is_landmark
 * GmComposite2003_C1 C1      -1.00      136.06    GmComposite2003_C1_AAACCC1000 AAACCC1000                   83.00          83.00        AFLP             1
 * GmComposite2003_C1 C1      -1.00      136.06    GmComposite2003_C1_Dia        Dia                          71.08          71.08        Gene             1
 * GmComposite2003_C1 C1      -1.00      136.06    GmComposite2003_C1_V38a       V38a                         54.18          54.18        Marker           1
 * GmComposite2003_C1 C1      -1.00      136.06    GmComposite2003_C1_A059_1     A059_1       A059            18.62          18.62        RFLP             1
 */
public class CmapConverter {

    public static void main(String[] args) throws FileNotFoundException, IOException {

        if (args.length!=1) {
            System.out.println("Usage: CmapConverter <Cmap file>");
            System.exit(0);
        }

        String inFile = args[0];

        Map<String,Double> linkageGroups = new HashMap<>();

        PrintWriter mrkWriter = new PrintWriter("mrk.tsv");
        mrkWriter.println("MapName\t");
        mrkWriter.println("#Marker\tLinkageGroup\tPosition");

        PrintWriter qtlWriter = new PrintWriter("qtl.tsv");
        qtlWriter.println("MapName\t");
        qtlWriter.println("IntervalDescription\t");
        qtlWriter.println("#Identifier\tTrait\tLinkageGroup\tStart\tEnd");

        BufferedReader in = new BufferedReader(new FileReader(inFile));
        String line;
        while ((line=in.readLine())!=null) {
            if (line.startsWith("map")) continue;
            // load fields as strings
            String[] parts = line.split("\t");
            String map_acc = parts[0];
            String map_name = parts[1];
            String map_start = parts[2];
            String map_stop = parts[3];
            String feature_acc = parts[4];
            String feature_name = parts[5];
            String feature_aliases = parts[6];
            String feature_start = parts[7];
            String feature_stop = parts[8];
            String feature_type_acc = parts[9];
            String is_landmark = parts[10];
            // deal
            linkageGroups.put(map_acc, Double.parseDouble(map_stop));
            if (feature_start.equals(feature_stop)) {
                double position = Double.parseDouble(feature_start);
                mrkWriter.println(feature_acc+"\t"+map_acc+"\t"+position);
            } else {
                double start = Double.parseDouble(feature_start);
                double end = Double.parseDouble(feature_stop);
                // SoyBase hack
                String[] chunks = feature_name.split(" ");
                String trait = "";
                if (chunks[chunks.length-1].contains("-")) {
                    trait = chunks[0];
                    for (int i=1; i<(chunks.length-1); i++) trait += " "+chunks[i];
                } else {
                    trait = feature_name;
                }
                if (start>0.0) {
                    qtlWriter.println(feature_acc+"\t"+trait+"\t"+map_acc+"\t"+start+"\t"+end);
                }
            }

        }
        mrkWriter.close();
        qtlWriter.close();

        // expt file
        PrintWriter exptWriter = new PrintWriter("expt.tsv");
        exptWriter.println("MapName\t");
        exptWriter.println("Description\t");
        exptWriter.println("MappingParent\t");
        exptWriter.println("PMID\t");
        exptWriter.println("DOI\t");
        exptWriter.println("#Linkage Group\tNum\tLength (cM)");
        for (String linkageGroup : linkageGroups.keySet()) {
            exptWriter.println(linkageGroup+"\t0\t"+linkageGroups.get(linkageGroup));
        }
        exptWriter.close();

        // marker file
        // MapName iSelect-consensus-2016
        // #Marker Linkage Group                   Position
        // 2_30247 iSelect-consensus-2016_1        0.00
        // 2_52445 iSelect-consensus-2016_1        0.00
        // 2_15811 iSelect-consensus-2016_1        1.35
        // 2_45924 iSelect-consensus-2016_1        1.35
        // 2_18633 iSelect-consensus-2016_1        1.41
    }
}
