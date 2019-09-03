package org.ncgr.pangenomics;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.Map;
import java.util.TreeMap;

/**
 * Encapsulates a node in a Graph.
 *
 * @author Sam Hokin
 */
public class Node implements Comparable {

    Long id;         // the id of this node, assigned by the graph reader
    String sequence; // the genomic sequence associated with this node

    /**
     * Construct given a node id and sequence.
     */
    public Node(Long id, String sequence) {
        this.id = id;
        this.sequence = sequence;
    }

    /**
     * Construct without a sequence.
     */
    public Node(Long id) {
        this.id = id;
        this.sequence = null;
    }

    /**
     * Get the id.
     */
    public long getId() {
        return id;
    }

    /**
     * Get the sequence.
     */
    public String getSequence() {
        return sequence;
    }

    /**
     * Set the sequence.
     */
    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    /**
     * Two nodes are equal if they have the same id.
     */
    public boolean equals(Object o) {
	Node that = (Node) o;
        return this.getId()==that.getId();
    }

    /**
     * Compare based simply on id.
     */
    @Override
    public int compareTo(Object o) {
	Node that = (Node) o;
        return (int)(this.id - that.id);
    }

    /**
     * Simply return the id.
     */
    public String toString() {
        return String.valueOf(id);
    }

    /**
     * Read a map of Nodes (keyed by id) from a tab-delimited file.
     */
    public static Map<Long,Node> readFromFile(String filename) throws FileNotFoundException, IOException {
        Map<Long,Node> map = new TreeMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line = null;
        while ((line=reader.readLine())!=null) {
            String[] parts = line.split("\t");
            Long id = Long.parseLong(parts[0]);
            String sequence = parts[1];
            Node n = new Node(id, sequence);
            map.put(id, n);
        }
        return map;
    }
}
    
