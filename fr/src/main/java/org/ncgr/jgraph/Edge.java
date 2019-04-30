package org.ncgr.jgraph;

import org.ncgr.pangenomics.Path;

import org.jgrapht.graph.DefaultEdge;

/**
 * Graph edge labeled with the path name, genotype and label.
 * It contains a source Node and target Node.
 */
class Edge extends DefaultEdge {
    
    private PathWalk path;    // the path to which this edge belongs

    /**
     * Construct an edge from a path it belongs to.
     *
     * @param path the PathWalk to which this edge belongs
     * 
     */
    public Edge(PathWalk path) { 
        this.path = path;
    }

    /**
     * Get the path name associated with this edge.
     * @return the name of the path to which this edge belongs
     */
    public String getName() {
        return path.getName();
    }
    
    /**
     * Get the path label associated with this edge.
     * @return the label of the path to which this edge belongs (e.g. "case" or "ctrl")
     */
    public String getLabel() {
        return path.getLabel();
    }

    /**
     * Get the path genotype associated with this edge.
     * @return the genotype of the path to which this edge belongs (usually 0 or 1)
     */
    public int getGenotype() {
        return path.getGenotype();
    }

    /**
     * get the path name and genotype associated with this edge.
     * @return the name and genotype of the path
     */
    public String getNameGenotype() {
        return path.getNameGenotype();
    }
    
    /**
     * Return a string representation of this edge.
     */
    @Override
    public String toString() {
        return getSource()+":"+getNameGenotype()+":"+getTarget();
    }
}
