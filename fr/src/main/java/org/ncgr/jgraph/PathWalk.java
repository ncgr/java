package org.ncgr.jgraph;

import org.jgrapht.*;
import org.jgrapht.graph.*;

import java.util.*;

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
    public PathWalk(Graph<Node,Edge> graph, List<Node> nodeList) {
        super(graph, nodeList, 1.0);
        buildSequence();
    }

    /**
     * Creates a walk defined by a sequence of edges; weight=1.0.
     */
    public PathWalk(Graph<Node,Edge> graph, Node startNode, Node endNode, List<Edge> edgeList) {
        super(graph, startNode, endNode, edgeList, 1.0);
        buildSequence();
    }

    /**
     * Creates a walk defined by both a sequence of edges and a sequence of nodes; weight=1.0.
     */
    public PathWalk(Graph<Node,Edge> graph, Node startNode, Node endNode, List<Node> nodeList, List<Edge> edgeList) {
        super(graph, startNode, endNode, nodeList, edgeList, 1.0);
        buildSequence();
    }

    /**
     * Creates a walk defined by a list of nodes as well as identifying info; weight=1.0.
     */
    public PathWalk(Graph<Node,Edge> graph, List<Node> nodeList, String name, int genotype) {
        super(graph, nodeList, 1.0);
        this.name = name;
        this.genotype = genotype;
        buildSequence();
    }

    /**
     * Creates a walk defined by a list of nodes as well as identifying info; weight=1.0.
     */
    public PathWalk(Graph<Node,Edge> graph, List<Node> nodeList, String name, int genotype, String label) {
        super(graph, nodeList, 1.0);
        this.name = name;
        this.genotype = genotype;
        this.label = label;
        buildSequence();
    }

    /**
     * Do we need to implement equals(Object)?
     */
    public boolean equals(Object o) {
        PathWalk that = (PathWalk) o;
        return this.equals(that);
    }
    
    /**
     * Two paths are equal if they have the same name and genotype and traverse the same nodes.
     */
    public boolean equals(PathWalk that) {
        return this.toString().equals(that.toString());
        // if (!this.name.equals(that.name)) {
        //     return false;
        // } else if (this.genotype!=that.genotype) {
        //     return false;
        // } else {
        //     List<Node> thisNodes = this.getNodes();
        //     List<Node> thatNodes = that.getNodes();
        //     if (thisNodes.size()!=thatNodes.size()) {
        //         return false;
        //     } else {
        //         for (int i=0; i<thisNodes.size(); i++) {
        //             if (!thisNodes.get(i).equals(thatNodes.get(i))) {
        //                 return false;
        //             }
        //         }
        //         return true;
        //     }
        // }
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
     * Return the nodes that this path traverses, in order of traversal.
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
        return new PathWalk(this.graph, subnodes, name, genotype, label);
    }

    /**
     * Return true if the given PathWalk represents a subpath of this PathWalk.
     * @param path the path to be compared with this one
     * @return true if path is a subpath of this
     */
    public boolean contains(PathWalk path) {
        List<Node> thisNodes = this.getNodes();
        List<Node> pathNodes = path.getNodes();
        boolean match = false;
        for (Node m : pathNodes) {
            if (!match && thisNodes.contains(m)) {
                match = true;
            } else if (match && !thisNodes.contains(m)) {
                match = false;
                break;
            }
        }
        return match;
    }

    /**
     * Return a summary string.
     */
    public String toString() {
        String s = getNameGenotypeLabel();
        s += ":[";
        StringJoiner joiner = new StringJoiner(",");
        for (Node node : getNodes()) {
            joiner.add(String.valueOf(node.getId()));
        }
        s += joiner.toString();
        s += "]";
        return s;
    }

    /**
     * Algorithm 1 from Cleary, et al. generates the supporting path segments of this path for the given NodeSet and alpha and kappa parameters.
     * @param nodes the NodeSet, or cluster C as it's called in Algorithm 1
     * @param alpha the penetrance parameter
     * @param kappa the insertion parameter
     * @returns the set of supporting path segments
     */
    public Set<PathWalk> computeSupport(NodeSet nodes, double alpha, int kappa) {
        Set<PathWalk> s = new HashSet<>();
        // m = the list of p's nodes that are in c
        LinkedList<Node> m = new LinkedList<>();
        for (Node n : getNodes()) {
            if (nodes.contains(n)) m.add(n);
        }

        // find maximal subpaths
        for (int i=0; i<m.size(); i++) {
            Node nl = m.get(i);
            Node nr = null;
            int num = 0;
            for (int j=i; j<m.size(); j++) {
                // kappa test
                PathWalk subpath = subpath(nl, m.get(j));
                int maxInsertion = 0; // max insertion
                int insertion = 0; // continguous insertion
                for (Node n : subpath.getNodes()) {
                    if (nodes.contains(n)) {
                        // reset and save previous insertion if large
                        if (insertion>maxInsertion) maxInsertion = insertion;
                        insertion = 0;
                    } else {
                        // increment insertion
                        insertion += n.getSequence().length();
                    }
                }
                if (maxInsertion>kappa) break;
                // we're good, set nr from this cycle
                nr = m.get(j);
                num = j - i + 1; // number of this path's nodes in nodes collection
            }
            // is this a subpath of an already counted subpath?
            PathWalk subpath = subpath(nl,nr);
            boolean ignore = false;
            for (PathWalk checkpath : s) {
                if (checkpath.contains(subpath)) {
                    ignore = true;
                    break;
                }
            }
            // sanity check
            if (subpath.getNodes().size()==0) {
                System.err.println("ERROR: subpath.getNodes().size()=0; path="+this.toString()+" nl="+nl+" nr="+nr);
                ignore = true;
            }
            // alpha test on maximal subpath
            if (!ignore && num>=alpha*nodes.size()) s.add(subpath);
        }
        return s;
    }
}

