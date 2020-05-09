package org.ncgr.datastore;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
    
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.biojava.nbio.genome.parsers.gff.FeatureList;
import org.biojava.nbio.genome.parsers.gff.FeatureI;
import org.biojava.nbio.genome.parsers.gff.GFF3Reader;
import org.biojava.nbio.genome.parsers.gff.GFF3Writer;

/**
 * Removes dupe records (same ID) from a GFF.
 */
public class GFFDeduper {
    public static void main(String[] args) throws FileNotFoundException, IOException {
        if (args.length!=1) {
            System.out.println("Usage: GFFDeduper <GFF file>");
            System.exit(0);
        }

        String inFile = args[0];

        // grab the top comment lines
        List<String> headerLines = new ArrayList<>();
        BufferedReader in = new BufferedReader(new FileReader(inFile));
        String line;
        while ((line=in.readLine()).startsWith("#")) {
            headerLines.add(line);
        }
        in.close();

        GFF3Reader gffReader = new GFF3Reader();
        FeatureList featureList = gffReader.read(inFile);

        Map<String,FeatureI> featureMap = new HashMap<>();

        for (FeatureI feature : featureList) {
            String id = feature.getAttribute("ID");
            String name = feature.getAttribute("Name");
            if (featureMap.containsKey(id)) {
                // replace if this name is not based on ID
                if (name!=null && !id.contains(name)) {
                    featureMap.put(id, feature);
                }
            } else {
                featureMap.put(id, feature);
            }
        }

        // output header
        for (String headerLine : headerLines) {
            System.out.println(headerLine);
        }

        // output GFF lines
        FeatureList newList = new FeatureList();
        newList.add(featureMap.values());
        for (FeatureI feature : newList) {
            // System.out.println(new GFF3Feature(feature).toGFF3());
	    System.out.println(feature.toString());
        }
    }
}
