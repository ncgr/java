package org.ncgr.pangenomics.fr;

import java.util.LinkedList;
import java.util.StringJoiner;

/**
 * Encapsulates a path through a Graph, along with its full sequence.
 *
 * @author Sam Hokin
 */
public class Path implements Comparable<Path> {

    String name;             // the name of this path, typically a subject ID
    String label;            // an optional label, like "+1", "-1", "case", "control", "M", "F"
    String sequence;         // this path's full sequence
    LinkedList<Node> nodes;  // the ordered list of nodes that this path travels

    /**
     * Construct given a path name and a list of nodes (minimum requirement)
     */
    Path(String name, LinkedList<Node> nodes) {
        this.name = name;
        this.nodes = nodes;
        buildSequence();
    }

    /**
     * Construct given a path name, label and a list of nodes
     */
    Path(String name, String label, LinkedList<Node> nodes) {
        this.name = name;
        this.label = label;
        this.nodes = nodes;
        buildSequence();
    }

    /**
     * Construct an empty Path given just a path name and label
     */
    Path(String name, String label) {
        this.name = name;
        this.label = label;
        this.nodes = new LinkedList<>();
    }

    /**
     * Set this path's label (could be some reason you'd like to relabel the paths).
     */
    void setLabel(String label) {
        this.label = label;
    }

    /**
     * Add a Node to this Path and update the sequence.
     */
    void addNode(Node node) {
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
