package org.ncgr.pangenomics.fr;

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
    Node(Long id, String sequence) {
        this.id = id;
        this.sequence = sequence;
    }

    /**
     * Two nodes are equal if they have the same id.
     */
    public boolean equals(Node that) {
        return this.id==that.id;
    }

    /**
     * Compare based simply on id.
     */
    public int compareTo(Node that) {
        return Long.compare(this.id, that.id);
    }

    /**
     * Simply return the id.
     */
    public String toString() {
        return String.valueOf(id);
    }
}
    
