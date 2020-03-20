package org.ncgr.pangenomics.genotype;

import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.graph.GraphWalk;

/**
 * An extension of GraphWalk to provide an individual path through the graph and methods appropriate for frequented regions.
 * It must implement Comparable, otherwise identical paths from different individuals will be regarded equal.
 *
 * @author Sam Hokin
 */
public class Path extends GraphWalk<Node,Edge> implements Comparable {
    public String name;              // the name identifying this path, a sample or individual
    public String label;             // an optional label, like "+1", "-1", "case", "control", "M", "F"
    public String sequence;          // this path's full genomic sequence (may be null)

    /**
     * Create a path defined by a List of Nodes; weight=1.0.
     */
    public Path(Graph<Node,Edge> graph, List<Node> nodes, String name, String label) {
        super(graph, nodes, 1.0);
        this.name = name;
        this.label = label;
    }

    /**
     * Return a bespoke string representation.
     */
    @Override
    public String toString() {
	return name+"("+label+")"+getNodes();
    }

    /**
     * Return true if the two paths have the same name.
     */
    @Override
    public boolean equals(Object o) {
        Path that = (Path) o;
        return this.name.equals(that.name);
    }

    /**
     * Compare based on name.
     */
    @Override
    public int compareTo(Object o) {
        Path that = (Path) o;
        return this.name.compareTo(that.name);
    }

    /**
     * Return the nodes that this path traverses, in order of traversal.
     */
    public List<Node> getNodes() {
        return (List<Node>) getVertexList();
    }

    /**
     * Return the edges that this path follows along, in order of traversal.
     */
    public List<Edge> getEdges() {
        return (List<Edge>) getEdgeList();
    }

    /**
     * Return true if this path belongs to a "case" sample.
     */
    public boolean isCase() {
        return label.toLowerCase().equals("case");
    }

    /**
     * Return true if this path belongs to a "control" sample.
     */
    public boolean isControl() {
        return label.toLowerCase().equals("ctrl") || label.toLowerCase().equals("control");
    }
}
