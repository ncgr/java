package org.ncgr.jgraph;

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
        Node thisSource = (Node) this.getSource();
        Node thatSource = (Node) that.getSource();
        Node thisTarget = (Node) this.getTarget();
        Node thatTarget = (Node) that.getTarget();
        return thisSource.equals(thatSource) && thisTarget.equals(thatTarget);
    }

    /**
     * Return a String representation.
     */
    @Override
    public String toString() {
        Node thisSource = (Node) this.getSource();
        Node thisTarget = (Node) this.getTarget();
        return thisSource.getId()+":"+thisTarget.getId();
    }
}
