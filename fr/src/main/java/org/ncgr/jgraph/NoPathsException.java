package org.ncgr.jgraph;

/**
 * An exception thrown when a Graph has no paths in it (and needs to).
 */
public class NoPathsException extends Exception {
    public NoPathsException(String e) {
        super(e);
    }
}
