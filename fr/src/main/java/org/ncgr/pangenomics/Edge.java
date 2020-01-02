package org.ncgr.pangenomics;

import org.jgrapht.graph.DefaultEdge;

/**
 * Graph edge with equals implemented using Node.equals().
 */
public class Edge extends DefaultEdge {
    /**
     * Two edges are equal if they connect the same nodes.
     */
    @Override
    public boolean equals(Object o) {
	Edge that = (Edge) o;
        String thisSource = this.getSource().toString();
        String thatSource = that.getSource().toString();
        String thisTarget = this.getTarget().toString();
        String thatTarget = that.getTarget().toString();
        return thisSource.equals(thatSource) && thisTarget.equals(thatTarget);
    }

    /**
     * Return a String representation.
     */
    @Override
    public String toString() {
        return this.getSource().toString()+":"+this.getTarget().toString();
    }
}
