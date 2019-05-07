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
public class PathWalk extends GraphWalk<Node,Edge> implements Comparable<PathWalk> {

    private String name;     // the name identifying this path, a sample or individual
    private int genotype;    // the genotype identifier for this path: 0 or 1
    private String label;    // an optional label, like "+1", "-1", "case", "control", "M", "F"
    private String sequence; // this path's full genomic sequence

    /**
     * Creates a walk defined by a sequence of nodes; weight=1.0.
     */
    PathWalk(Graph<Node,Edge> graph, List<Node> nodeList) {
        super(graph, nodeList, 1.0);
    }

    /**
     * Creates a walk defined by a sequence of edges; weight=1.0.
     */
    PathWalk(Graph<Node,Edge> graph, Node startNode, Node endNode, List<Edge> edgeList) {
        super(graph, startNode, endNode, edgeList, 1.0);
    }

    /**
     * Creates a walk defined by both a sequence of edges and a sequence of nodes; weight=1.0.
     */
    PathWalk(Graph<Node,Edge> graph, Node startNode, Node endNode, List<Node> nodeList, List<Edge> edgeList) {
        super(graph, startNode, endNode, nodeList, edgeList, 1.0);
    }

    /**
     * Creates a walk defined by a list of nodes as well as identifying info; weight=1.0.
     */
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
                return (int)(thisNodes.get(0).getId() - thatNodes.get(0).getId());
            }
        }
    }

    /**
     * Set the name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the name.
     */
    public String getName() {
        return name;
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
     * Set the sequence.
     */
    public void setSequence() {
        this.sequence = sequence;
    }

    /**
     * Get the sequence.
     */
    public String getSequence() {
        return sequence;
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
    public List<Node> getNodes() {
        return (List<Node>) getVertexList();
    }

    /**
     * Build this path's sequence from its list of nodes.
     */
    public void buildSequence() {
        sequence = "";
        for (Node node : getNodes()) {
            sequence += node.getSequence();
        }
    }

    /**
     * Return the length of this path's sequence exclusively between the two given nodes (0 if one of the nodes is not in this path, or if nl=nr).
     * @param nl the "left" node
     * @param nr the "right" node
     * @return the length of this path's sequence exclusively between nl and nr
     */
    public int gap(Node nl, Node nr) {
        if (!getNodes().contains(nl) || !getNodes().contains(nr)) {
            return 0;
        } else if (nl.equals(nr)) {
            return 0;
        } else {
            return subsequence(nl,nr).length() - nl.getSequence().length() - nr.getSequence().length();
        }
    }

    /**
     * Return the subsequence inclusively between the two given nodes (empty String if one of the nodes is not present in this path).
     * @param nl the "left" node
     * @param nr the "right" node
     * @return the subsequence inclusively between nl and nr
     */
    public String subsequence(Node nl, Node nr) {
        if (!getNodes().contains(nl) || !getNodes().contains(nr)) return "";
        return subpath(nl,nr).sequence;
    }

    /**
     * Return the subpath inclusively between the two given nodes (empty if one of the nodes is not present in this path).
     * @param nl the "left" node
     * @param nr the "right" node
     * @return the PathWalk inclusively between nl and nr
     */
    public PathWalk subpath(Node nl, Node nr) {
        List<Node> subnodes = new LinkedList<>();
        if (getNodes().contains(nl) && getNodes().contains(nr)) {
            if (nl.equals(nr)) {
                subnodes.add(nl);
            } else {
                boolean started = false;
                boolean finished = false;
                for (Node node : getNodes()) {
                    if (!started && node.equals(nl)) {
                        started = true;
                        subnodes.add(node);
                    } else if (node.equals(nr) && !finished) {
                        subnodes.add(node);
                        finished = true;
                    } else if (started && !finished) {
                        subnodes.add(node);
                    }
                }
            }
        }
        return new PathWalk(this.graph, subnodes, name, genotype);
    }
}

