package org.ncgr.pangenomics.fr;

import java.util.LinkedList;

/**
 * Encapsulates a path through a Graph.
 *
 * @author Sam Hokin
 */
public class Path implements Comparable<Path> {

    String name;             // the name of this path, typically a subject ID
    String label;         // an optional label label, like +1/-1 for case/control
    LinkedList<Long> nodes;  // the ordered list of nodes that this path travels

    /**
     * Construct given a path name and a list of nodes.
     */
    Path(String name, LinkedList<Long> nodes) {
        this.name = name;
        this.nodes = nodes;
    }

    /**
     * Construct given a path name, label label and a list of nodes.
     */
    Path(String name, String label, LinkedList<Long> nodes) {
        this.name = name;
        this.label = label;
        this.nodes = nodes;
    }

    
    /**
     * Construct given a name name, a list of nodes, and the 1-based start and end node indices for this path.
     */
    Path(String name, LinkedList<Long> allNodes, int i, int j) {
        this.name = name;
        nodes = new LinkedList<>();
        for (int k=i-1; k<=j-1; k++) {
            nodes.add(allNodes.get(k));
        }
    }

    /**
     * Construct given a path name, a label label, and a list of nodes, and the 1-based start and end node indices for this path.
     */
    Path(String name, String label, LinkedList<Long> allNodes, int i, int j) {
        this.name = name;
        this.label = label;
        nodes = new LinkedList<>();
        for (int k=i-1; k<=j-1; k++) {
            nodes.add(allNodes.get(k));
        }
    }

    /**
     * Two paths are equal if they contain the same nodes, in the same order, and have the same name.
     */
    public boolean equals(Path that) {
        if (!this.name.equals(that.name)) return false;
        if (this.nodes.size()!=that.nodes.size()) return false;
        int i = 0;
        for (long thisNodeId : this.nodes) {
            long thatNodeId = that.nodes.get(i++);
            if (thisNodeId!=thatNodeId) return false;
        }
        return true;
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
     * Return the concated name and label of this path
     */
    public String getNameAndLabel() {
        if (label==null) {
            return name;
        } else {
            return name+"{"+label+"}";
        }
    }

    // setters
    public void setNodes(LinkedList<Long> nodes) {
        this.nodes = nodes;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
    
