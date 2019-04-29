package org.ncgr.jgraph;

import org.ncgr.pangenomics.Path;

import org.jgrapht.graph.DefaultEdge;

/**
 * Graph edge labeled with the path name, genotype and label.
 * It contains a source Node and target Node.
 */
class Edge extends DefaultEdge {
    
    private Path path;    // the Path to which this edge belongs

    /**
     * Construct an edge from a path it belongs to.
     *
     * @param path the Path to which this edge belongs
     * 
     */
    public Edge(Path path) { 
        this.path = path;
    }

    /**
     * Get the path name associated with this edge.
     * @return name the name of the path to which this edge belongs
     */
    public String getName() {
        return path.name;
    }
    
    /**
     * Get the path label associated with this edge.
     * @return label the label of the path to which this edge belongs (e.g. "case" or "ctrl")
     */
    public String getLabel() {
        return path.label;
    }

    /**
     * Get the path genotype associated with this edge.
     * @return genotype the genotype of the path to which this edge belongs (usually 0 or 1)
     */
    public int getGenotype() {
        return path.genotype;
    }
    
    /**
     * Return a string representation of this edge.
     */
    @Override
    public String toString() {
        return getSource()+":"+path.name+"."+path.genotype+":"+getTarget();
    }
}
