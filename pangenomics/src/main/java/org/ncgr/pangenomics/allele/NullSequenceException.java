package org.ncgr.pangenomics.allele;

/**
 * An exception thrown when the sequence of a Node in the graph is null.
 */
public class NullSequenceException extends Exception {
    public NullSequenceException(String e) {
        super(e);
    }
}
