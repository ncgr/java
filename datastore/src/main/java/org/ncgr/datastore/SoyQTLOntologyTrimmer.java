package org.ncgr.datastore;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.Set;
import java.util.HashSet;

/**
 * Trims out the Soybase QTL IDs to make a simple phenotype-ontology term file.
 * 0        1          N
 * Whitefly resistance 1-2	GO:0009625 <--- skip these, they should be with associated genes, not phenotypes
 * Whitefly resistance 1-2	TO:0000961
 */
public class SoyQTLOntologyTrimmer {
    public static void main(String[] args) throws FileNotFoundException, IOException {
        if (args.length!=1) {
            System.out.println("Usage: SoyQTLOntologyTrimmer <Soy QTL-ontology term file>");
            System.exit(0);
        }

	// store the unique phenotype-terms in a Set 
	Set<String> phenotypeTerms = new HashSet<>();


        String inFile = args[0];
        BufferedReader in = new BufferedReader(new FileReader(inFile));
        String line;
        while ((line=in.readLine())!=null) {
            String[] parts = line.split("\t");
	    String qtlString = parts[0];
	    String ontologyTerm = parts[1];
	    // drop GO terms, inappropriate for phenotypes
	    if (ontologyTerm.startsWith("GO:")) continue;
	    // SoyBase has mistakes
	    if (ontologyTerm.length()!=10) continue;
	    // form the entry
	    String[] qtlParts = qtlString.split(" ");
	    String phenotype = qtlParts[0];
	    for (int i=1; i<qtlParts.length-1; i++) {
		phenotype += " "+qtlParts[i];
	    }
	    phenotypeTerms.add(phenotype+"\t"+ontologyTerm);
        }
	in.close();
	for (String phenotypeTerm : phenotypeTerms) {
	    System.out.println(phenotypeTerm);
	}
    }
}
