package org.ncgr.pangenomics.allele;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.StringJoiner;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Map;
import java.util.HashMap;

/**
 * Encapsulates a set of nodes in a Graph. NodeSets are comparable based on their content.
 *
 * @author Sam Hokin
 */
public class NodeSet extends TreeSet<Node> implements Comparable {

    public int totalBases;

    /**
     * Empty constructor.
     */
    public NodeSet() {
        super();
    }
    
    /**
     * Construct given a Collection of Nodes.
     */
    public NodeSet(Collection<Node> nodes) {
        this.addAll(nodes);
        updateTotalBases();
    }

    /**
     * Construct given a string representation but no underlying graph.
     */
    public NodeSet(String str) {
        List<String> nodeStrings = Arrays.asList(str.replace("[","").replace("]","").split(","));
        for (String s : nodeStrings) {
            long id = Long.parseLong(s);
            this.add(new Node(id));
        }
    }

    /**
     * Construct from a map of id to Nodes and a string representation, e.g. "[5,7,15,33]".
     */
    public NodeSet(Map<Long,Node> nodeMap, String str) {
        Set<String> nodeStrings = new HashSet<>(Arrays.asList(str.replace("[","").replace("]","").split(",")));
        for (String s : nodeStrings) {
            long id = Long.parseLong(s);
            if (nodeMap.containsKey(id)) this.add(nodeMap.get(id));
        }
        updateTotalBases();
    }

    /**
     * Update totalBases.
     */
    public void updateTotalBases() {
        StringBuilder bases = new StringBuilder();
        for (Node n : this) {
            bases.append(n.sequence);
        }
        totalBases = bases.length();
    }

    /**
     * Equality if exactly the same nodes, meaning the same string.
     */
    public boolean equals(Object o) {
	NodeSet that = (NodeSet) o;
        return this.toString().equals(that.toString());
    }

    /**
     * Compare alphabetically.
     */
    public int compareTo(Object o) {
	NodeSet that = (NodeSet) o;
        return this.toString().compareTo(that.toString());
    }

    /**
     * Return a readable summary string.
     */
    public String toString() {
        String s = "[";
        StringJoiner joiner = new StringJoiner(",");
        for (Node node : this) {
            joiner.add(String.valueOf(node.id));
        }
        s += joiner.toString();
        s += "]";
        return s;
    }

    /**
     * Return true if this NodeSet is a superset of that NodeSet.
     */
    public boolean isSupersetOf(NodeSet that) {
        return this.size()>that.size() && this.containsAll(that);
    }
    
    /**
     * Find the Levenshtein distance between this and another NodeSet.
     *
     * @param that the other Nodeset
     * @return result distance, or -1
     */
    public int distanceFrom(NodeSet that) {
        // copy the NodeSets into lists for indexed access
        List<Node> left = new ArrayList<>(this);
        List<Node> right = new ArrayList<>(that);
        int n = left.size();
        int m = right.size();
        // trivial distance
        if (n == 0) {
            return m;
        } else if (m == 0) {
            return n;
        }
        if (n>m) {
            // swap the Lists to consume less memory
            final List<Node> tmp = left;
            left = right;
            right = tmp;
            n = m;
            m = right.size();
        }
        int[] p = new int[n + 1];
        // indexes into Lists left and right
        int i; // iterates through left
        int j; // iterates through right
        int upper_left;
        int upper;
        Node rightJ; // jth Node of right
        int cost; // cost
        for (i=0; i<=n; i++) {
            p[i] = i;
        }
        for (j=1; j<=m; j++) {
            upper_left = p[0];
            rightJ = right.get(j - 1);
            p[0] = j;
            for (i=1; i<=n; i++) {
                upper = p[i];
                cost = left.get(i-1).equals(rightJ) ? 0 : 1;
                // minimum of cell to the left+1, to the top+1, diagonally left and up +cost
                p[i] = Math.min(Math.min(p[i - 1] + 1, p[i] + 1), upper_left + cost);
                upper_left = upper;
            }
        }
        return p[n];
    }

    /**
     * Return the result of merging two NodeSets.
     * NOTE: does NOT run update() on the result!
     */
    public static NodeSet merge(NodeSet ns1, NodeSet ns2) {
        NodeSet merged = new NodeSet();
        merged.addAll(ns1);
        merged.addAll(ns2);
        return merged;
    }
}
    
