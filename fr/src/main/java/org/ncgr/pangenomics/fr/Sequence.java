package org.ncgr.pangenomics.fr;

/**
 *
 * @author bmumey
 */
public class Sequence implements Comparable {

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

    public int compareTo(Object o) {
        Sequence that = (Sequence) o;
        return (int)(this.startPos - that.startPos);
    }

    public String toString() {
        return label+": length="+length+"; starts at "+startPos;
    }
}
