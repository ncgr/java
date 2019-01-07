package org.ncgr.pangenomics.fr;

/**
 *
 * @author bmumey
 */
public class Sequence {

    private String label;
    private int length;
    private long startPos;

    public Sequence(String label, int length, long startPos) {
        this.label = label;
        this.length = length;
        this.startPos = startPos;
    }

    public String getLabel() {
        return label;
    }
    public int getLength() {
        return length;
    }
    public long getStartPos() {
        return startPos;
    }
    
}
