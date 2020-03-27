package org.ncgr.pangenomics.genotype;

import java.io.Serializable;

/**
 * Container for a node with genotype information as well as the range on the genome.
 * An int id is used for simplicity and the calling code is expected to enforce uniqueness.
 */
public class Node implements Comparable, Serializable {
    public long id;
    public String contig;
    public int start;
    public int end;
    public String rs;
    public String genotype;

    /**
     * Minimal constructor.
     */
    public Node(long id) {
        this.id = id;
    }

    /**
     * Construct the full monty.
     */
    public Node(long id, String rs, String contig, int start, int end, String genotype) {
        this.id = id;
	this.contig = contig;
	this.start = start;
        this.end = end;
        this.rs = rs;
	this.genotype = genotype;
    }

    /**
     * Return the id as a string.
     */
    public String toString() {
	return String.valueOf(id);
    }

    /**
     * Nodes are equal if they have the same id.
     */
    public boolean equals(Object o) {
	Node that = (Node) o;
        return this.id==that.id;
    }

    /**
     * Nodes are compared by their id.
     */
    public int compareTo(Object o) {
	Node that = (Node) o;
        return Long.compare(this.id,that.id);
    }
}
