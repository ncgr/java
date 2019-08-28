package org.ncgr.jgraph;

/**
 * Encapsulates a node in a Graph.
 *
 * @author Sam Hokin
 */
public class Node implements Comparable<Node> {

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

    // /**
    //  * Two nodes are equal if they have the same id.
    //  */
    // public boolean equals(Node that) {
    //     return this.id==that.id;
    // }

    /**
     * Two nodes are equal if they have the same sequence.
     */
    public boolean equals(Node that) {
        return this.sequence.equals(that.sequence);
    }

    /**
     * Hash code uses String.hashCode(), which is LIKELY to be distinct for distinct strings.
     */
    @Override
    public int hashCode() {
        return sequence.hashCode();
    }

    /**
     * Compare based on sequence.
     */
    @Override
    public int compareTo(Node that) {
        return this.sequence.compareTo(that.sequence);
    }

    /**
     * Simply return the id. OR SEQUENCE? OR hashCode()?
     */
    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
    
