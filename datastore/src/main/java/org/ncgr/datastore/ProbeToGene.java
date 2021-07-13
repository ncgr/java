package org.ncgr.datastore;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.text.DecimalFormat;

/**
 * Converts an expression file with microarray probesets to a datastore-compliant expression file with genes.
 *
 * probe to gen mapping:
 * 0              1     2                    3               4  5                   6                  7            8                   9             10  11      12    13          14
 * Medtr8g042010  GENE  Medicago truncatula  jemalong A17	JCVI-Mt4.0v2-gene   Mtr.46724.1.S1_at  AFFY_PROBE   Medicago truncatula	jemalong A17	  affx-1  gmap	qcov=97%,.. parameters:...
 *
 * expression table:
 * 0id  1probeset       2firstsample      3secondsample     4thirdsample      ...
 * 387  AFFX-BioB-5_at  478.487027038067  361.905444116024  543.959714640695  ...
 *
 * Since multiple probesets map to a single gene, we compute the arithmetic average of the values per gene for output.
 *
 */
public class ProbeToGene {

    static DecimalFormat idx = new DecimalFormat("000"); // ensure alphabeticity of geneSampleKey 

    public static void main(String[] args) throws FileNotFoundException, IOException {
        // check args
        if (args.length!=2) {
            System.err.println("Usage: ProbeToGene <probe-to-gene-mapping-file> <expression-table-file>");
        }

        String mappingFilename = args[0];
        String expressionFilename = args[1];
        String line;

        Map<String,Integer> geneCounts = new HashMap<>(); // number of probesets per gene
        Map<String,List<Double>> geneValues = new TreeMap<>(); // cumulative values per gene, sorted

        // load the probeset-gene mappings into a HashMap
        HashMap<String,String> probesToGenes = new HashMap<>();
        BufferedReader mappingReader = new BufferedReader(new FileReader(mappingFilename));
        while ((line=mappingReader.readLine())!=null) {
            if (line.startsWith("#")) continue;
            String[] parts = line.split("\t");
            if (parts.length>6) {
                String gene = parts[0];
                String probe = parts[6];
                probesToGenes.put(probe,gene);
            }
        }
        mappingReader.close();

        // spin through the expression file and accumulate values per gene per sample
        BufferedReader expressionReader = new BufferedReader(new FileReader(expressionFilename));
        while ((line=expressionReader.readLine())!=null) {
            if (line.startsWith("#")) continue;
            String[] parts = line.split("\t");
            String probe = parts[1];
            String gene = probesToGenes.get(probe);
            if (gene==null) continue;
            // increment gene counter
            if (geneCounts.containsKey(gene)) {
                int count = geneCounts.get(gene) + 1;
                geneCounts.put(gene, count);
            } else {
                geneCounts.put(gene, 1);
            }
            // get the values for this gene
            List<Double> values = new LinkedList<>();
            for (int i=2; i<parts.length; i++) {
                double value = Double.parseDouble(parts[i]);
                values.add(value);
            }
            List<Double> oldValues = geneValues.get(gene);
            if (oldValues==null) {
                geneValues.put(gene, values);
            } else {
                List<Double> newValues = new LinkedList<>();
                for (int i=0; i<values.size(); i++) {
                    double oldValue = oldValues.get(i);
                    double value = values.get(i);
                    double newValue = oldValue + value;
                    newValues.add(newValue);
                }
                geneValues.put(gene, newValues);
            }
        }
        expressionReader.close();

        // output the average values per gene
        for (String gene : geneValues.keySet()) {
            List<Double> cumValues = geneValues.get(gene);
            int count = geneCounts.get(gene);
            System.out.print(gene);
            for (double cumValue : cumValues) {
                System.out.print("\t"+(cumValue/count));
            }
            System.out.println("");
        }
    }
}
                

        
        

