package org.ncgr.pangenomics.genotype;

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
        Node thisSourceNode = this.getSourceNode();
        Node thatSourceNode = that.getSourceNode();
        Node thisTargetNode = this.getTargetNode();
        Node thatTargetNode = that.getTargetNode();
        return thisSourceNode.equals(thatSourceNode) && thisTargetNode.equals(thatTargetNode);
    }

    /**
     * Return a bespoke String representation.
     */
    @Override
    public String toString() {
        return this.getSourceNode().toString()+":"+this.getTargetNode().toString();
    }

    /**
     * Return the source Node.
     */
    public Node getSourceNode() {
        return (Node) getSource();
    }

    /**
     * Return the target Node.
     */
    public Node getTargetNode() {
        return (Node) getTarget();
    }
}
