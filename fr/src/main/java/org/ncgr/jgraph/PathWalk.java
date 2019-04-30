package org.ncgr.jgraph;

import org.jgrapht.*;
import org.jgrapht.graph.*;

import java.util.*;

import org.ncgr.pangenomics.Node;
import org.ncgr.pangenomics.Path;

/**
 * An extension of GraphWalk to provide a genomic path through the graph.
 *
 * @author Sam Hokin
 */
public class PathWalk extends GraphWalk implements Comparable<PathWalk> {

    String name;     // the name identifying this path, a sample or individual
    int genotype;    // the genotype identifier for this path: 0 or 1
    String label;    // an optional label, like "+1", "-1", "case", "control", "M", "F"
    String sequence; // this path's full genomic sequence

    /**
     * Creates a walk defined by a sequence of nodes; weight=1.0.
     */
    @SuppressWarnings("unchecked")
    PathWalk(Graph<Node,Edge> graph, List<Node> nodeList) {
        super(graph, nodeList, 1.0);
    }

    /**
     * Creates a walk defined by a sequence of edges; weight=1.0.
     */
    @SuppressWarnings("unchecked")
    PathWalk(Graph<Node,Edge> graph, Node startNode, Node endNode, List<Edge> edgeList) {
        super(graph, startNode, endNode, edgeList, 1.0);
    }

    /**
     * Creates a walk defined by both a sequence of edges and a sequence of nodes; weight=1.0.
     */
    @SuppressWarnings("unchecked")
    PathWalk(Graph<Node,Edge> graph, Node startNode, Node endNode, List<Node> nodeList, List<Edge> edgeList) {
        super(graph, startNode, endNode, nodeList, edgeList, 1.0);
    }

    /**
     * Creates a walk defined by a list of nodes as well as identifying info; weight=1.0.
     */
    @SuppressWarnings("unchecked")
    PathWalk(Graph<Node,Edge> graph, List<Node> nodeList, String name, int genotype) {
        super(graph, nodeList, 1.0);
        this.name = name;
        this.genotype = genotype;
    }

    /**
     * Two paths are equal if they have the same name and genotype and traverse the same nodes.
     */
    public boolean equals(PathWalk that) {
        if (!this.name.equals(that.name)) {
            return false;
        } else if (this.genotype!=that.genotype) {
            return false;
        } else {
            List<Node> thisNodes = this.getNodes();
            List<Node> thatNodes = that.getNodes();
            for (int i=0; i<thisNodes.size(); i++) {
                if (!thisNodes.get(i).equals(thatNodes.get(i))) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Compare based on name then genotype then node size then first node id.
     */
    public int compareTo(PathWalk that) {
        if (!this.name.equals(that.name)) {
            return this.name.compareTo(that.name);
        } else if (this.genotype!=that.genotype) {
            return this.genotype - that.genotype;
        } else {
            List<Node> thisNodes = this.getNodes();
            List<Node> thatNodes = that.getNodes();
            if (thisNodes.size()!=thatNodes.size()) {
                return thisNodes.size() - thatNodes.size();
            } else {
                return (int)(thisNodes.get(0).id - thatNodes.get(0).id);
            }
        }
    }

    /**
     * Set the label.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Get the label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Set the genotype.
     */
    public void setGenotype(int genotype) {
        this.genotype = genotype;
    }

    /**
     * Get the genotype.
     */
    public int getGenotype() {
        return genotype;
    }

    /**
     * Return the concated name and genotype of this path, if specific genotype.
     */
    public String getNameGenotype() {
        if (genotype==PangenomicGraph.BOTH_GENOTYPES) {
            return name;
        } else {
            return name+"."+genotype;
        }
    }

    /**
     * Return the concated name, genotype and, if present, label of this path.
     */
    public String getNameGenotypeLabel() {
        String n = getNameGenotype();
        if (label!=null) n += "."+label;
        return n;
    }

    /**
     * Return the nodes that this path traverses.
     */
    @SuppressWarnings("unchecked")
    public List<Node> getNodes() {
        return (List<Node>) getVertexList();
    }

}

