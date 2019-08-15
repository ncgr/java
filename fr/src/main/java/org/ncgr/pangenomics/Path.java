package org.ncgr.pangenomics;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
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
    public List<Node> nodes;        // the ordered list of nodes that this path travels

    /**
     * Construct given a path name, genotype and a list of nodes (minimum requirement)
     */
    public Path(String name, int genotype, List<Node> nodes) {
        this.name = name;
        this.genotype = genotype;
        this.nodes = nodes;
        buildSequence();
    }

    /**
     * Construct given a path name, genotype, label and a list of nodes
     */
    public Path(String name, int genotype, String label, List<Node> nodes) {
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
     * Construct given a name.genotype, label and list of Nodes
     */
    public Path(String nameGenotype, String label, List<Node> nodes) {
        this.parseNameGenotype(nameGenotype);
        this.label = label;
        this.nodes = nodes;
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
     * Return a List of node IDs.
     */
    public List<Long> getNodeIds() {
        List<Long> nodeIds = new LinkedList<>();
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
     * Parse out the path name and genotype from a string like "S12345.0"
     */
    public void parseNameGenotype(String nameGenotype) {
        String[] parts = nameGenotype.split("\\.");
        if (parts.length==1)  {
            this.name = nameGenotype; // no genotype suffix
        } else {
            this.genotype = Integer.parseInt(parts[parts.length-1]);
            this.name = parts[0];
            for (int i=1; i<(parts.length-1); i++) this.name += "."+parts[i];
        }
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
        List<Node> subnodes = new LinkedList<>();
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
     * Return true if this path contains the given Node, matched by id.
     */
    public boolean containsNode(Node n) {
        for (Node node : nodes) {
            if (n.equals(node)) {
                return true;
            }
        }
        return false;
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


    /**
     * Load a Map of paths from a file: the paths contain their node lists but the node sequences are NOT constructed.
     * 0       1       2       3       4       5       6       7       ...
     * 28278.0 case    5722432 1       2       3       4       5       ...
     */
    public static Map<String,Path> readFromFile(String filename) throws FileNotFoundException, IOException {
        Map<String,Path> map = new TreeMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line = null;
        while ((line=reader.readLine())!=null) {
            String[] parts = line.split("\t");
            String nameGenotype = parts[0];
            String label = parts[1];
            long length = Long.parseLong(parts[2]);
            List<Node> nodes = new LinkedList<>();
            for (int i=3; i<parts.length; i++) {
                long id = Long.parseLong(parts[i]);
                Node n = new Node(id);
                nodes.add(n);
            }
            // build the Path
            Path p = new Path(nameGenotype, label, nodes);
            map.put(nameGenotype, p);
        }
        return map;
    }
}
