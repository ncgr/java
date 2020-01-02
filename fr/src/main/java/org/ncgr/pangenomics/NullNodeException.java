package org.ncgr.pangenomics;

/**
 * An exception thrown when a Node in the graph is null.
 */
public class NullNodeException extends Exception {
    public NullNodeException(String e) {
        super(e);
    }
}
