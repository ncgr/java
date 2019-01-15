package org.ncgr.pangenomics.fr;

import java.util.LinkedList;

/**
 * Encapsulates a subpath of a sample/path, the latter being identified by its sample name.
 *
 * @author Sam Hokin
 */
public class Subpath implements Comparable<Subpath> {

    String pathName;
    LinkedList<Long> nodes;

    /**
     * Construct given a path name and a list of nodes.
     */
    Subpath(String pathName, LinkedList<Long> nodes) {
        this.pathName = pathName;
        this.nodes = nodes;
    }
    
    /**
     * Construct given a pathName name, a list of nodes, and the 1-based start and end node indices for this subpath.
     */
    Subpath(String pathName, LinkedList<Long> allNodes, int i, int j) {
        nodes = new LinkedList<>();
        for (int k=i-1; k<=j-1; k++) {
            nodes.add(allNodes.get(k));
        }
    }

    /**
     * Two Subpaths are equal if they contain the same nodes, in the same order, and belong to the same path.
     */
    public boolean equals(Subpath that) {
        if (!this.pathName.equals(that.pathName)) return false;
        if (this.nodes.size()!=that.nodes.size()) return false;
        int i = 0;
        for (long thisNodeId : this.nodes) {
            long thatNodeId = that.nodes.get(i++);
            if (thisNodeId!=thatNodeId) return false;
        }
        return true;
    }

    /**
     * Compare Subpaths by path name and then size and then node by node comparison.
     */
    public int compareTo(Subpath that) {
        if (!this.pathName.equals(that.pathName)) return this.pathName.compareTo(that.pathName);
        if (this.nodes.size()!=that.nodes.size()) return Integer.compare(this.nodes.size(), that.nodes.size());
        int i = 0;
        for (long thisNodeId : this.nodes) {
            long thatNodeId = that.nodes.get(i++);
            if (thisNodeId!=thatNodeId) return Long.compare(thisNodeId, thatNodeId);
        }
        return 0;
    }
}
    
