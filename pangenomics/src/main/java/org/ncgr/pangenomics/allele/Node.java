package org.ncgr.pangenomics.allele;

import java.io.Serializable;

/**
 * Encapsulates a node in a Graph.
 *
 * @author Sam Hokin
 */
public class Node implements Comparable, Serializable {

    public Long id;         // the id of this node, assigned by the graph reader
    public String sequence; // the genomic sequence associated with this node

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
     * Append a subsequence to this Node's sequence.
     */
    public void appendSequence(String subsequence) {
        this.sequence += subsequence;
    }

    /**
     * Two nodes are equal if they have the same id.
     */
    @Override
    public boolean equals(Object o) {
	Node that = (Node)o;
	return this.id.equals(that.id);
    }

    /**
     * Hash code uses String.hashCode(), which is LIKELY to be distinct for distinct strings.
     */
    @Override
    public int hashCode() {
        return sequence.hashCode();
    }

    /**
     * Compare based on id.
     */
    @Override
    public int compareTo(Object o) {
	Node that = (Node) o;
	return this.id.compareTo(that.id);
    }

    /**
     * Simply return the id.
     */
    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
    
