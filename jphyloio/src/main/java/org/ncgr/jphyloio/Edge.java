package org.ncgr.jphyloio;

import info.bioinfweb.jphyloio.events.EdgeEvent;

/**
 * Encapsulates a JPhyloIO tree edge with appropriate methods.
 */
public class Edge {
    public String sourceId = null;
    public String targetId = null;
    public String label = null;
    public double length = Double.MAX_VALUE;

    /**
     * Construct from a EdgeEvent.
     */
    public Edge(EdgeEvent e) {
        sourceId = e.getSourceID();
        targetId = e.getTargetID();
        if (e.hasLabel()) {
            label = e.getLabel();
        }
        if (e.hasLength()) {
            length = e.getLength();
        }
    }

    /**
     * Return a string summarizing this Event.
     */
    public String toString() {
        String s = "";
        if (label!=null) {
            s += "("+label+")";
        }
        s += sourceId;
        s += "[";
        if (hasLength()) {
            s += length;
        }
        s += "]";
        s += targetId;
        return s;
    }

    /**
     * Return true if this Edge has a length value.
     */
    public boolean hasLength() {
        return length < Double.MAX_VALUE;
    }
}
