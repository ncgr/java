package org.ncgr.pangenomics.fr;

import java.util.TreeSet;

/**
 * Encapsulates a set of nodes in a Graph. NodeSets are comparable based on their content.
 *
 * @author Sam Hokin
 */
public class NodeSet extends TreeSet implements Comparable<NodeSet> {

    TreeSet<Node> nodes;

    /**
     * Construct given a TreeSet of Nodes.
     */
    Node(TreeSet nodes) {
        this.nodes = nodes;
    }

    /**
     * Two NodeSets are equal if they contain the same nodes.
     */
    public boolean equals(NodeSet that) {
        return this.nodes.equals(that.nodes);
    }

    /**
     * Compare based on tree depth, then initial node id.
     */
    public int compareTo(NodeSet that) {
        Long thisId = this.nodes.first();
        Long thatId = that.nodes.first();
        while (thisId==thatId) {
            if (this.nodes.higher(thisId)==null || that.nodes.higher(thatId)==null) {
                return Integer.compare(this.nodes.size(), that.nodes.size());
            } else {
                thisId = this.nodes.higher(thisId);
                thatId = that.nodes.higher(thatId);
            }
        }
        return Long.compare(thisId, thatId);
    }
}
    
