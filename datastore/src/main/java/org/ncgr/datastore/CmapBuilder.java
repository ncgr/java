package org.ncgr.datastore;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.Map;
import java.util.HashMap;

/**
 * Builds a CMap file from the various modular files in legfed-intermine-data.
 *
 * OUTPUT:
 * map_acc                map_name map_start map_stop feature_acc                feature_name feature_aliases feature_start feature_stop feature_type_acc is_landmark
 * PvCookUCDavis2009_Pv09 Pv09     0         66.1     PvCook...Pv09_Pv_TOG913042 Pv_TOG913042 TOG913042       66.1          66.1         SNP              0
 * PvCookUCDavis2009_Pv09 Pv09     0         66.1     PvCook...Pv09_Pv_TOG899751 Pv_TOG899751 TOG899751       44.4          44.4         SNP              0
 *
 * Linkage Groups to Genetic Map:
 * GmBSR.tsv
 * TaxonID	3847
 * #Linkage Group	Number	Genetic Map
 * GmBSR_J.1	        1	GmBSR
 * GmBSR_J.2	        2	GmBSR
 * GmBSR_J.3	        3	GmBSR
 *
 * Markers to Linkage Group:
 * markers-maps.tsv
 * TaxonID	3847
 * #Marker		Linkage Group	Position
 * ...
 * G815_1  GmBSR_J.2       39.00
 * G815_2  GmBSR_J.2       45.00
 * B122_1  GmBSR_J.3       10.00
 * K375_1  GmBSR_J.3       20.00
 * ...
 */
public class CmapBuilder {


    public static void main(String[] args) throws FileNotFoundException, IOException {
        if (args.length!=2) {
            System.err.println("Usage: CmapBuilder <linkage group file> <marker file>");
            System.exit(1);
        }

        String linkageGroupFilename = args[0];
        String markerFilename = args[1];

        // assume one genetic map per linkage group file
        String geneticMap = "";
        
        // map linkage group to its number
        Map<String,Integer> linkageGroups = new HashMap<>();

        // load the linkage groups for this run
        BufferedReader lgin = new BufferedReader(new FileReader(linkageGroupFilename));
        String lgline;
        while ((lgline=lgin.readLine())!=null) {
            String[] parts = lgline.split("\t");
            if (parts[0].toLowerCase().equals("taxonid")) continue;
            if (parts[0].startsWith("#")) continue;
            geneticMap = parts[2];
            linkageGroups.put(parts[0], Integer.parseInt(parts[1]));
        }
        lgin.close();

        // output header
        System.out.println("map_acc\tmap_name\tmap_start\tmap_stop\tfeature_acc\tfeature_name\tfeature_aliases\tfeature_start\tfeature_stop\tfeature_type_acc\tis_landmark");

        // now spit out lines for the markers that belong to these linkage groups
        // PvCookUCDavis2009_Pv09 Pv09     0         66.1     PvCook...Pv09_Pv_TOG913042 Pv_TOG913042 TOG913042       66.1          66.1         SNP              0
        BufferedReader min = new BufferedReader(new FileReader(markerFilename));
        String mline;
        while ((mline=min.readLine())!=null) {
            String[] parts = mline.split("\t");
            if (parts[0].toLowerCase().equals("taxonid")) continue;
            if (parts[0].startsWith("#")) continue;
            String markerName = parts[0];
            String lgName = parts[1];
            double position = Double.parseDouble(parts[2]);
            String[] lgParts = lgName.split("_");
            if (linkageGroups.containsKey(lgName)) {
                System.out.println(lgName+"\t"+lgParts[1]+"\t"+"0"+"\t"+position+"\t"+markerName+"\t\t\t"+position+"\t"+position+"\t\t");
            }
        }
        min.close();
    }
}
