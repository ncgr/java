package org.ncgr.pangenomics.allele;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.StringJoiner;

import org.jgrapht.Graph;
import org.jgrapht.graph.GraphWalk;

/**
 * An extension of GraphWalk to provide a genomic path through the graph and methods appropriate for frequented regions.
 * It must implement Comparable, otherwise identical paths from different individuals will be regarded equal.
 *
 * @author Sam Hokin
 */
public class Path extends GraphWalk<Node,Edge> implements Comparable {

    public String name;     // the name identifying this path, a sample or individual
    public int genotype;    // the genotype identifier for this path: 0 or 1
    public String label;    // an optional label, like "+1", "-1", "case", "control", "M", "F"
    public String sequence; // this path's full genomic sequence

    /**
     * Create a path defined by a sequence of nodes; weight=1.0.
     */
    public Path(Graph<Node,Edge> graph, List<Node> nodeList, boolean skipSequence) {
        super(graph, nodeList, 1.0);
        if (!skipSequence) buildSequence();
    }

    /**
     * Create a path defined by a list of nodes as well as identifying info; weight=1.0.
     */
    public Path(Graph<Node,Edge> graph, List<Node> nodeList, String name, int genotype, boolean skipSequence) {
        super(graph, nodeList, 1.0);
        this.name = name;
        this.genotype = genotype;
        if (!skipSequence) buildSequence();
    }

    /**
     * Create a path defined by a sequence of edges; weight=1.0.
     */
    public Path(Graph<Node,Edge> graph, Node startNode, Node endNode, List<Edge> edgeList, boolean skipSequence) {
        super(graph, startNode, endNode, edgeList, 1.0);
        if (!skipSequence) buildSequence();
    }

    /**
     * Create a path defined by both a sequence of edges and a sequence of nodes; weight=1.0.
     */
    public Path(Graph<Node,Edge> graph, Node startNode, Node endNode, List<Node> nodeList, List<Edge> edgeList, boolean skipSequence) {
        super(graph, startNode, endNode, nodeList, edgeList, 1.0);
        if (!skipSequence) buildSequence();
    }

    /**
     * Create a path defined by a list of nodes as well as identifying info; weight=1.0.
     */
    public Path(Graph<Node,Edge> graph, List<Node> nodeList, String name, int genotype, String label, boolean skipSequence) {
        super(graph, nodeList, 1.0);
        this.name = name;
        this.genotype = genotype;
        this.label = label;
        if (!skipSequence) buildSequence();
    }

    /**
     * Two paths are equal if they have the same name and genotype and traverse the same nodes,
     * which means they have the same String representation.
     */
    @Override
    public boolean equals(Object o) {
        Path that = (Path) o;
        return this.toString().equals(that.toString());
    }
    
    /**
     * Compare based on name then genotype then node size then first node id.
     */
    @Override
    public int compareTo(Object o) {
    	Path that = (Path) o;
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
     * Return true if the label indicates a "case" path.
     */
    public boolean isCase() {
        return label.equals("case");
    }

    /**
     * Return true if the label indicates a "control" path.
     */
    public boolean isControl() {
        return label.equals("ctrl") || label.equals("control");
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
     * Return the edges that this path follows along, in order of traversal.
     */
    public List<Edge> getEdges() {
        return (List<Edge>) getEdgeList();
    }

    /**
     * Build this path's sequence from its list of nodes.
     */
    public void buildSequence() {
        StringBuilder builder = new StringBuilder();
        for (Node node : getNodes()) {
            builder.append(node.sequence);
        }
        sequence = builder.toString();
    }

    /**
     * Return the subsequence inclusively between the two given nodes (empty String if one of the nodes is not present in this path).
     * @param nl the "left" node
     * @param nr the "right" node
     * @return the subsequence inclusively between nl and nr
     */
    public String subsequence(Node nl, Node nr) {
        List<Node> nodeList = getNodes();
        if (!nodeList.contains(nl) || !nodeList.contains(nr)) return "";
        return subpath(nl,nr).sequence;
    }

    /**
     * Return the subpath inclusively between the two given nodes (empty if one of the nodes is not present in this path).
     * @param nl the "left" node
     * @param nr the "right" node
     * @return the Path inclusively between nl and nr
     */
    public Path subpath(Node nl, Node nr) {
        List<Node> subnodes = new ArrayList<>();
        List<Node> nodeList = getNodes();
        if (nodeList.contains(nl) && nodeList.contains(nr)) {
            if (nl.equals(nr)) {
                subnodes.add(nl);
            } else {
                boolean started = false;
                boolean finished = false;
                for (Node node : nodeList) {
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
        return new Path(this.graph, subnodes, name, genotype, label, false);
    }

    /**
     * Return true if the given Path represents a subpath of this Path.
     * @param path the path to be compared with this one
     * @return true if path is a subpath of this
     */
    public boolean contains(Path path) {
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
            joiner.add(String.valueOf(node.id));
        }
        s += joiner.toString();
        s += "]";
        return s;
    }

    /**
     * Algorithm 1 from Cleary, et al. generates the supporting path segments of this path for the given NodeSet and alpha and kappa parameters.
     *
     * @param nodes the NodeSet, or cluster C as it's called in Algorithm 1
     * @param alpha the penetrance parameter = minimum fraction of nodes in C that are in subpath
     * @param kappa the insertion parameter = maximum inserted number of nodes
     * @return the set of supporting path segments
     */
    public List<Path> computeSupport(NodeSet nodes, double alpha, int kappa) {
        // s = the supporting subpaths
        List<Path> s = new ArrayList<>();
        // m = the list of this path's nodes that are in C=nodes
        List<Node> m = new ArrayList<>();
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
                Path subpath = subpath(nl, m.get(j));
                int maxInsertion = 0; // max insertion
                int insertion = 0; // continguous insertion
                for (Node n : subpath.getNodes()) {
                    if (nodes.contains(n)) {
                        // reset and save previous insertion if large
                        if (insertion>maxInsertion) maxInsertion = insertion;
                        insertion = 0;
                    } else {
                        insertion += 1;
                    }
                }
                if (maxInsertion>kappa) break;
                // we're good, set nr from this cycle
                nr = m.get(j);
                num = j - i + 1; // number of this path's nodes in nodes collection
            }
            // is this a subpath of an already counted subpath? (maximality test)
            Path subpath = subpath(nl,nr);
            boolean ignore = false;
            for (Path checkpath : s) {
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

