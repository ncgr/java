package org.ncgr.pangenomics.genotype.fr;

import org.ncgr.pangenomics.genotype.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;

/**
 * Container for a pair of FrequentedRegions and their merged result with a comparator for ranking it.
 * This is where one implements weighting of certain desired FR characteristics.
 */
public class FRPair implements Comparable {
    FrequentedRegion fr1;
    FrequentedRegion fr2;
    FrequentedRegion merged;
    PangenomicGraph graph;

    double alpha;
    int kappa;
    boolean alphaSatisfied;

    int priorityOptionKey;
    String priorityOptionLabel;

    NodeSet nodes;
    double oneMinusAlpha;
    
    /**
     * Construct from a pair of FrequentedRegions and run parameters.
     */
    FRPair(FrequentedRegion fr1, FrequentedRegion fr2, int priorityOptionKey, String priorityOptionLabel) {
        this.fr1 = fr1;
        this.fr2 = fr2;
        this.graph = fr1.graph;
        this.alpha = fr1.alpha;
        this.kappa = fr1.kappa;
        this.priorityOptionKey = fr1.priorityOptionKey;
        this.priorityOptionLabel = fr1.priorityOptionLabel;
        oneMinusAlpha = 1.0 - alpha;
        nodes = NodeSet.merge(fr1.nodes, fr2.nodes);
        // nothing to do if an identity merge
        if (fr1.equals(fr2)) merged = fr1;
    }

    /**
     * Construct from an FRPair.toString() line
     * [15] 1 1 66.00 1 0 0 ∞ 1.000E0;[2,5] 2 12 34.33 12 0 122 ∞ 6.041E-2;
     */
    FRPair(String line, PangenomicGraph graph, double alpha, int kappa, int priorityOptionKey, String priorityOptionLabel, boolean alphaSatisfied) {
        String[] parts = line.split(";");
        this.alpha = alpha;
        this.kappa = kappa;
        this.priorityOptionKey = priorityOptionKey;
        this.priorityOptionLabel = priorityOptionLabel;
        this.alphaSatisfied = alphaSatisfied;
        fr1 = new FrequentedRegion(graph, parts[0], alpha, kappa, priorityOptionKey, priorityOptionLabel);
        fr2 = new FrequentedRegion(graph, parts[1], alpha, kappa, priorityOptionKey, priorityOptionLabel);
        merged = new FrequentedRegion(graph, parts[3], alpha, kappa, priorityOptionKey, priorityOptionLabel);
        nodes = merged.getNodes();
        oneMinusAlpha = 1.0 - alpha;
    }
    
    /**
     * Algorithm 2 from Cleary, et al. returns the supporting path segments for the given merge of FRs.
     */
    public void merge() {
        NodeSet mergedNodes = NodeSet.merge(fr1.nodes,fr2.nodes);
        try {
            merged = new FrequentedRegion(graph, mergedNodes, alpha, kappa, priorityOptionKey, priorityOptionLabel);
        } catch (Exception e) {
            System.err.println(e);
            System.err.println("ERROR MERGING "+mergedNodes);
            System.exit(1);
        }
    }

    /**
     * Two FRPairs are equal if their components are equal.
     */
    public boolean equals(Object o) {
	FRPair that = (FRPair) o;
        return (this.fr1.equals(that.fr1) && this.fr2.equals(that.fr2)) || (this.fr1.equals(that.fr2) && this.fr2.equals(that.fr1));
    }

    /**
     * Compare the merged FR.
     */
    @Override
    public int compareTo(Object o) {
	FRPair that = (FRPair) o;
        return this.merged.compareTo(that.merged);
    }

    /**
     * Reader-friendly string summary, just three FRs separated by semicolons.
     */
    public String toString() {
        return fr1.toString()+";" +
            fr2.toString()+";" +
            merged.toString();
    }
}
