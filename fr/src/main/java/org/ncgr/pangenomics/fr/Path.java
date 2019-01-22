package org.ncgr.pangenomics.fr;

import java.util.LinkedList;

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
    Path(String name, LinkedList<Node> nodes, String sequence) {
        this.name = name;
        this.nodes = nodes;
        buildSequence();
    }

    /**
     * Construct given a path name, label and a list of nodes (minimum requirement)
     */
    Path(String name, String label, LinkedList<Node> nodes) {
        this.name = name;
        this.label = label;
        this.nodes = nodes;
        buildSequence();
    }

    /**
     * Set this path's label (could be some reason you'd like to relabel the paths).
     */
    void setLabel(String label) {
        this.label = label;
    }

    /**
     * Two paths are equal if they contain the same nodes, in the same order.
     */
    public boolean equals(Path that) {
        if (this.nodes.size()!=that.nodes.size()) {
            return false;
        } else {
            int i = 0;
            for (long thisNodeId : this.nodes) {
                long thatNodeId = that.nodes.get(i++);
                if (thisNodeId!=thatNodeId) return false;
            }
            return true;
        }
    }

    /**
     * Compare paths by label, name, size and then node by node comparison.
     */
    public int compareTo(Path that) {
        if (!this.name.equals(that.name)) return this.name.compareTo(that.name);
        if (this.nodes.size()!=that.nodes.size()) return Integer.compare(this.nodes.size(), that.nodes.size());
        int i = 0;
        for (long thisNodeId : this.nodes) {
            long thatNodeId = that.nodes.get(i++);
            if (thisNodeId!=thatNodeId) return Long.compare(thisNodeId, thatNodeId);
        }
        return 0;
    }

    /**
     * Compare based on node tree depth, then initial node id.
     */
    public int compareTo(Path that) {
        int i = 0;
        Long thisId = this.nodes.get(0);
        Long thatId = that.nodes.get(0);
        while (thisId==thatId) {
            i++;
            if (this.nodes.get(i)==null || that.nodes.get(i)==null) {
                return Integer.compare(this.nodes.size(), that.nodes.size());
            } else {
                thisId = this.nodes.get(i);
                thatId = that.nodes.get(i);
            }
        }
        return Long.compare(thisId, thatId);
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
}
