package org.ncgr.jgraph;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.StringJoiner;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Map;
import java.util.HashMap;

/**
 * Encapsulates a set of nodes in a Graph. NodeSets are comparable based on their content.
 *
 * @author Sam Hokin
 */
public class NodeSet extends TreeSet<Node> implements Comparable {

    public int totalBases;

    /**
     * Empty constructor.
     */
    public NodeSet() {
        super();
    }
    
    /**
     * Construct given a Collection of Nodes.
     */
    public NodeSet(Collection<Node> nodes) {
        this.addAll(nodes);
        update();
    }

    /**
     * Construct from a string representation and the underlying graph.
     * [1350,1352,1353,1355,1356,1357,1359,1360,...,1447,1449,1450,1451,1453,1454,1456,1457,1459,1460,1463,1464,1465,1467,1468,1469]
     */
    public NodeSet(PangenomicGraph graph, String str) {
        Set<Node> graphNodes = graph.vertexSet();
        Map<Long,Node> graphNodeMap = new HashMap<>();
        for (Node n : graphNodes) graphNodeMap.put(n.getId(), n);
        Set<String> nodeStrings = new HashSet<>(Arrays.asList(str.replace("[","").replace("]","").split(",")));
        for (String s : nodeStrings) {
            long id = Long.parseLong(s);
            if (graphNodeMap.containsKey(id)) {
		this.add(graphNodeMap.get(id));
	    } else {
		// bail, we're asked for a node that is not in the graph
		System.err.println("ERROR: graph does not contain node "+id);
		System.exit(1);
	    }
        }
    }

    /**
     * Construct from a map of id to Nodes and a string representation, e.g. "[5,7,15,33]".
     */
    public NodeSet(Map<Long,Node> nodeMap, String str) {
        Set<String> nodeStrings = new HashSet<>(Arrays.asList(str.replace("[","").replace("]","").split(",")));
        for (String s : nodeStrings) {
            long id = Long.parseLong(s);
            if (nodeMap.containsKey(id)) this.add(nodeMap.get(id));
        }
    }

    /**
     * Update derived quantities like totalBases.
     */
    public void update() {
        StringBuilder bases = new StringBuilder();
        for (Node n : this) {
            bases.append(n.sequence);
        }
        this.totalBases = bases.length();
    }

    /**
     * Equality if exactly the same nodes, meaning the same string.
     */
    public boolean equals(Object o) {
	NodeSet that = (NodeSet) o;
        return this.toString().equals(that.toString());
    }

    /**
     * Compare alphabetically.
     */
    public int compareTo(Object o) {
	NodeSet that = (NodeSet) o;
        return this.toString().compareTo(that.toString());
    }

    /**
     * Return a readable summary string.
     */
    public String toString() {
        String s = "[";
        StringJoiner joiner = new StringJoiner(",");
        for (Node node : this) {
            joiner.add(String.valueOf(node.id));
        }
        s += joiner.toString();
        s += "]";
        return s;
    }

    /**
     * Return true if this NodeSet is a parent of the given NodeSet, meaning its nodes are a subset of the latter.
     */
    public boolean parentOf(NodeSet that) {
        return that.size()>this.size() && that.containsAll(this);
    }

    /**
     * Return true if this NodeSet is a child of the given NodeSet, meaning it contains the exact sequence without insertions, but is not equal.
     * Example: [1,2,3,4,5,6,7,8,9,10] is a child of [4,5,6,7]
     */
    public boolean childOf(NodeSet that) {
        if (this.size()<=that.size()) {
            return false;
        } else {
            List<Node> thisList = new ArrayList<>(this);
            List<Node> thatList = new ArrayList<>(that);
            int i = 0;
            for (i=0; i<thisList.size(); i++) {
                if (thisList.get(i).equals(thatList.get(0))) {
                    break;
                }
            }
            boolean child = false;
            for (int j=0; j<thatList.size(); j++) {
                int k = i + j;
                child = k<thisList.size() && thisList.get(k).equals(thatList.get(j));
            }
            return child;
        }
    }
    
    /**
     * Return the result of merging two NodeSets.
     * NOTE: does NOT run update() on the result!
     */
    public static NodeSet merge(NodeSet ns1, NodeSet ns2) {
        NodeSet merged = new NodeSet();
        merged.addAll(ns1);
        merged.addAll(ns2);
        return merged;
    }
}
    
