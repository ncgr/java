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
import org.biojava.nbio.genome.parsers.gff.Feature;
import org.biojava.nbio.genome.parsers.gff.FeatureI;
import org.biojava.nbio.genome.parsers.gff.GFF3Reader;
import org.biojava.nbio.genome.parsers.gff.GFF3Writer;

/**
 * Removes extra parent entries (separated by commas) from GFF records, preserving only the first.
 *
 * medtr.R108_HM340.gnm1.scf000	maker exon 593191 593569 . - . ID=medtr.R108_HM340.gnm1.ann1.BZG31_000s000470:exon:6;Parent=medtr.R108_HM340.gnm1.ann1.BZG31_000s000470.1,medtr.R108_HM340.gnm1.ann1.BZG31_000s000470.2
 */
public class GFFParentSingularizer {
    public static void main(String[] args) throws FileNotFoundException, IOException {
        if (args.length!=1) {
            System.out.println("Usage: GFFParentSingularizer <GFF file>");
            System.exit(0);
        }
	
        String inFile = args[0];

        BufferedReader in = new BufferedReader(new FileReader(inFile));
        String line;
        while ((line=in.readLine())!=null) {
	    if (line.startsWith("#")) {
		System.out.println(line);
		continue;
	    }
	    GFF3Feature feature = new GFF3Feature(line);
	    String id = "";
	    String name = "";
	    String otherAttributes = "";
	    Map<String,String> attributeMap = feature.getAttributes();
	    for (String attributeName : attributeMap.keySet()) {
		String attributeValue = attributeMap.get(attributeName);
		if (attributeName.equals("ID")) {
		    id = attributeValue;
		} else if (attributeName.equals("Name")) {
		    name = attributeValue;
		} else {
		    if (attributeName.equals("Parent")) {
			attributeValue = attributeValue.split(",")[0];
		    }
		    otherAttributes += ";"+attributeName+"="+attributeValue;
		}
	    }
	    String attributes = "ID="+id;
	    if (name.length()>0) attributes += ";Name="+name;
	    attributes += otherAttributes;
	    GFF3Feature singleParentFeature =  new GFF3Feature(feature.seqname(), feature.source(), feature.type(), feature.location(), feature.score(), feature.frame(), attributes);
	    System.out.println(singleParentFeature);
        }
        in.close();
    }
}
