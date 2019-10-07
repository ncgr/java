package org.ncgr.jgraph;

/**
 * An exception thrown when a Graph has no Nodes (and needs to).
 */
public class NoNodesException extends Exception {
    public NoNodesException(String e) {
        super(e);
    }
}
