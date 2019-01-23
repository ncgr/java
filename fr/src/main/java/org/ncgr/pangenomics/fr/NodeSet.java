package org.ncgr.pangenomics.fr;

import java.util.StringJoiner;
import java.util.TreeSet;

/**
 * Encapsulates a set of nodes in a Graph. NodeSets are comparable based on their content.
 *
 * @author Sam Hokin
 */
public class NodeSet extends TreeSet<Node> implements Comparable <NodeSet> {

    /**
     * Empty constructor.
     */
    NodeSet() {
        super();
    }
    
    /**
     * Construct given a TreeSet of Nodes.
     */
    NodeSet(TreeSet<Node> nodes) {
        for (Node node : nodes) add(node);
    }

    /**
     * Equality if exactly the same nodes.
     */
    public boolean equals(NodeSet that) {
        if (this.size()!=that.size()) {
            return false;
        } else {
            Node thisNode = this.first();
            Node thatNode = that.first();
            while (thisNode.equals(thatNode)) {
                if (this.higher(thisNode)==null) {
                    return true;
                } else {
                    thisNode = this.higher(thisNode);
                    thatNode = that.higher(thatNode);
                }
            }
            return false;
        }
    }

    /**
     * Compare based on tree depth, then initial node id.
     */
    public int compareTo(NodeSet that) {
        if (this.equals(that)) {
            return 0;
        } else {
            Node thisNode = this.first();
            Node thatNode = that.first();
            while (thisNode.equals(thatNode)) {
                if (this.higher(thisNode)==null || that.higher(thatNode)==null) {
                    return Integer.compare(this.size(), that.size());
                } else {
                    thisNode = this.higher(thisNode);
                    thatNode = that.higher(thatNode);
                }
            }
            return thisNode.compareTo(thatNode);
        }
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
     * Return the result of merging two NodeSets.
     */
    static NodeSet merge(NodeSet ns1, NodeSet ns2) {
        NodeSet merged = new NodeSet();
        merged.addAll(ns1);
        merged.addAll(ns2);
        return merged;
    }
}
    
