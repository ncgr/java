package org.ncgr.pangenomics;

import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.StringJoiner;

/**
 * Encapsulates a path through a Graph, along with its full sequence.
 *
 * @author Sam Hokin
 */
public class Path implements Comparable<Path> {

    // probably ought to use getters/setters for these...
    public String name;             // the name of this path, typically a subject ID
    public int genotype;            // the genotype assigned to the path (0/1)
    public String label;            // an optional label, like "+1", "-1", "case", "control", "M", "F"
    public String sequence;         // this path's full sequence
    public LinkedList<Node> nodes;  // the ordered list of nodes that this path travels

    /**
     * Construct given a path name, genotype and a list of nodes (minimum requirement)
     */
    public Path(String name, int genotype, LinkedList<Node> nodes) {
        this.name = name;
        this.genotype = genotype;
        this.nodes = nodes;
        buildSequence();
    }

    /**
     * Construct given a path name, genotype, label and a list of nodes
     */
    public Path(String name, int genotype, String label, LinkedList<Node> nodes) {
        this.name = name;
        this.genotype = genotype;
        this.label = label;
        this.nodes = nodes;
        buildSequence();
    }

    /**
     * Construct given a path name, genotype, label and a node string of the form [3,56,98,126].
     * There are no sequences associated with these nodes.
     */
    public Path(String name, int genotype, String label, String nodeString) {
        this.name = name;
        this.genotype = genotype;
        this.label = label;
        nodes = new LinkedList<>();
        String[] nodesAsStrings = nodeString.replace("[","").replace("]","").split(",");
        for (String nodeAsString : nodesAsStrings) {
            nodes.add(new Node(Long.parseLong(nodeAsString)));
        }
    }

    /**
     * Construct an empty Path given just a path name and genotype
     */
    public Path(String name, int genotype) {
        this.name = name;
        this.genotype = genotype;
        this.nodes = new LinkedList<>();
    }

    /**
     * Construct an empty Path given just a path name, genotype and label (which can be null)
     */
    public Path(String name, int genotype, String label) {
        this.name = name;
        this.genotype = genotype;
        this.label = label;
        this.nodes = new LinkedList<>();
    }

    /**
     * Set this path's label (could be some reason you'd like to relabel the paths).
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Add a Node to this Path and update the sequence.
     */
    public void addNode(Node node) {
        this.nodes.add(node);
        buildSequence();
    }

    /**
     * Return the Nodes in this Path that are adjacent to the given Node.
     */
    public Set<Node> getAdjacentNodes(Node node) {
        Set<Node> adjacents = new HashSet<>();
        Node prev = null;
        boolean following = false;
        for (Node n : nodes) {
            if (following) {
                adjacents.add(n);
                following = false;
            } else if (n.equals(node)) {
                if (prev!=null) adjacents.add(prev);
                following = true;
            }
            prev = n;
        }
        return adjacents;
    }

    /**
     * Return a LinkedList of node IDs.
     */
    public LinkedList<Long> getNodeIds() {
        LinkedList<Long> nodeIds = new LinkedList<>();
        for (Node node : nodes) nodeIds.add(node.id);
        return nodeIds;
    }

    /**
     * Two paths are equal if they have the same name and genotype and traverse the same nodes.
     */
    public boolean equals(Path that) {
        if (!this.name.equals(that.name)) {
            return false;
        } else if (this.genotype!=that.genotype) {
            return false;
        } else {
            for (int i=0; i<this.nodes.size(); i++) {
                if (!this.nodes.get(i).equals(that.nodes.get(i))) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Compare based on name then genotype then node size then first node id.
     */
    public int compareTo(Path that) {
        if (!this.name.equals(that.name)) {
            return this.name.compareTo(that.name);
        } else if (this.genotype!=that.genotype) {
            return this.genotype - that.genotype;
        } else if (this.nodes.size()!=that.nodes.size()) {
            return this.nodes.size() - that.nodes.size();
        } else {
            return (int)(this.nodes.get(0).id - that.nodes.get(0).id);
        }
    }

    /**
     * Return the concated name and genotype of this path, if specific genotype.
     */
    public String getNameGenotype() {
        if (genotype==Graph.BOTH_GENOTYPES) {
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
     * Build this path's sequence from its Node list.
     */
    public void buildSequence() {
        sequence = "";
        for (Node node : nodes) {
            sequence += node.sequence;
        }
    }

    /**
     * Return the subpath inclusively between the two given nodes (empty if one of the nodes is not present in this path).
     * @param nl the "left" node
     * @param nr the "right" node
     * @return the Path inclusively between nl and nr
     */
    public Path subpath(Node nl, Node nr) {
        LinkedList<Node> subnodes = new LinkedList<>();
        if (nodes.contains(nl) && nodes.contains(nr)) {
            if (nl.equals(nr)) {
                subnodes.add(nl);
            } else {
                boolean started = false;
                boolean finished = false;
                for (Node node : nodes) {
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
        return new Path(this.name, this.genotype, this.label, subnodes);
    }

    /**
     * Return the subsequence inclusively between the two given nodes (empty String if one of the nodes is not present in this path).
     * @param nl the "left" node
     * @param nr the "right" node
     * @return the subsequence inclusively between nl and nr
     */
    public String subsequence(Node nl, Node nr) {
        if (!nodes.contains(nl) || !nodes.contains(nr)) return "";
        return subpath(nl,nr).sequence;
    }

    /**
     * Return the length of this path's sequence exclusively between the two given nodes (0 if one of the nodes is not in this path, or if nl=nr).
     * @param nl the "left" node
     * @param nr the "right" node
     * @return the length of this path's sequence exclusively between nl and nr
     */
    public int gap(Node nl, Node nr) {
        if (!nodes.contains(nl) || !nodes.contains(nr)) {
            return 0;
        } else if (nl.equals(nr)) {
            return 0;
        } else {
            return subsequence(nl,nr).length() - nl.sequence.length() - nr.sequence.length();
        }
    }

    /**
     * Return a summary string.
     */
    public String toString() {
        String s = getNameGenotypeLabel();
        s += ":[";
        StringJoiner joiner = new StringJoiner(",");
        for (Node node : nodes) {
            joiner.add(String.valueOf(node.id));
        }
        s += joiner.toString();
        s += "]";
        return s;
    }
}
