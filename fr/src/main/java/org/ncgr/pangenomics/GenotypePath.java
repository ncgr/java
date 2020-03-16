package org.ncgr.pangenomics;

import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.graph.GraphWalk;

/**                                                                                                                                                                                                                                           * An extension of GraphWalk to provide an individual path through the graph and methods appropriate for frequented regions.                                                                                                                  * It must implement Comparable, otherwise identical paths from different individuals will be regarded equal.                                                                                                                                 *                                                                                                                                                                                                                                            * @author Sam Hokin                                                                                                                                                                                                                          */
public class GenotypePath {
    public String name;     // the name identifying this path, a sample or individual
    public String label;    // an optional label, like "+1", "-1", "case", "control", "M", "F"
    public String sequence; // this path's full genomic sequence (may be null)

    // TEMP
    public List<GenotypeNode> nodeList; // this will be replaced by the graph
    

    /**
     * TEMP create a standalone path for a sample and list of nodes
     */
    public GenotypePath(String name, List<GenotypeNode> nodeList) {
	this.name = name;
	this.nodeList = nodeList;
    }

    public void addNode(GenotypeNode n) {
	nodeList.add(n);
    }

    public String toString() {
	return name+":"+nodeList;
    }
}
