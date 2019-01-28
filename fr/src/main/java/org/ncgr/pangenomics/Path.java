package org.ncgr.pangenomics;

import java.util.LinkedList;
import java.util.StringJoiner;

/**
 * Encapsulates a path through a Graph, along with its full sequence.
 *
 * @author Sam Hokin
 */
public class Path implements Comparable<Path> {

    // probably ought to use getters/setters for these...
    public String name;             // the name of this path, typically a subject ID
    public String label;            // an optional label, like "+1", "-1", "case", "control", "M", "F"
    public String sequence;         // this path's full sequence
    public LinkedList<Node> nodes;  // the ordered list of nodes that this path travels

    /**
     * Construct given a path name and a list of nodes (minimum requirement)
     */
    public Path(String name, LinkedList<Node> nodes) {
        this.name = name;
        this.nodes = nodes;
        buildSequence();
    }

    /**
     * Construct given a path name, label and a list of nodes
     */
    public Path(String name, String label, LinkedList<Node> nodes) {
        this.name = name;
        this.label = label;
        this.nodes = nodes;
        buildSequence();
    }

    /**
     * Construct an empty Path given just a path name
     */
    public Path(String name) {
        this.name = name;
        this.nodes = new LinkedList<>();
    }

    /**
     * Construct an empty Path given just a path name and label (which can be null)
     */
    public Path(String name, String label) {
        this.name = name;
        this.label = label;
        this.nodes = new LinkedList<>();
    }

    /**
     * Set this path's label (could be some reason you'd like to relabel the paths).
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Add a Node to this Path and update the sequence.
     */
    public void addNode(Node node) {
        this.nodes.add(node);
        buildSequence();
    }

    /**
     * Return a LinkedList<Long> of node IDs.
     */
    public LinkedList<Long> getNodeIds() {
        LinkedList<Long> nodeIds = new LinkedList<>();
        for (Node node : nodes) nodeIds.add(node.id);
        return nodeIds;
    }

    /**
     * Two paths are equal if they have the same name.
     */
    public boolean equals(Path that) {
        return this.name.equals(that.name) && this.compareTo(that)==0;
    }

    /**
     * Compare based on name.
     */
    public int compareTo(Path that) {
        return this.name.compareTo(that.name);
    }


    /**
     * Return the concated name and label of this path
     */
    public String getNameAndLabel() {
        if (label==null) {
            return name;
        } else {
            return name+"{"+label+"}";
        }
    }

    /**
     * Build this path's sequence from its Node list.
     */
    public void buildSequence() {
        sequence = "";
        for (Node node : nodes) {
            sequence += node.sequence;
        }
    }

    /**
     * Return a summary string.
     */
    public String toString() {
        String s = getNameAndLabel();
        s += ":[";
        StringJoiner joiner = new StringJoiner(",");
        for (Node node : nodes) {
            joiner.add(String.valueOf(node.id));
        }
        s += joiner.toString();
        s += "]";
        return s;
    }
}
