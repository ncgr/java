package org.ncgr.jphyloio;

import info.bioinfweb.jphyloio.events.NodeEvent;

/**
 * Encapsulates a JPhyloIO tree node with appropriate methods.
 */
public class Node {
    public String id;
    public String label;
    public String linkedId;

    /**
     * Construct from a NodeEvent.
     */
    public Node(NodeEvent e) {
        id = e.getID();
        if (e.hasLabel()) {
            label = e.getLabel();
        }
        if (e.hasLink()) {
            linkedId = e.getLinkedID();
        }
    }

    /**
     * Return true if the node represents a feature, which means it has a label.
     */
    public boolean isFeature() {
        return label!=null;
    }

    /**
     * Return a string summarizing this node.
     */
    public String toString() {
        String s = id;
        if (isFeature()) {
            s += ":"+label;
        }
        if (linkedId!=null) {
            s += ":"+linkedId;
        }
        return s;
    }
}
