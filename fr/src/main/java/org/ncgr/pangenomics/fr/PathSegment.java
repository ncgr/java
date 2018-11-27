package org.ncgr.pangenomics.fr;

/**
 *
 * @author bmumey
 */
public class PathSegment {
    int path;
    int start;
    int stop;
    
    public PathSegment(int path, int start, int stop) {
        this.path = path;
        this.start = start;
        this.stop = stop;
    }

    // getters
    public int getPath() {
        return path;
    }
    public int getStart() {
        return start;
    }
    public int getStop() {
        return stop;
    }
    
}
