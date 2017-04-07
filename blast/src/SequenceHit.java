package org.ncgr.blast;

/**
 * A simple class that stores a "hit", containing a combined sequence (including N, for example), a query ID, a subject ID, the hit HSP and a score.
 *
 * @author Sam Hokin
 */
public class SequenceHit implements Comparable {

    public String sequence = ""; // the combined sequence, maybe including N at mismatches
    public String queryID;       // the ID of the query sequence
    public String hitID;         // the ID of the hit sequence
    public Hsp hsp;              // the HSP representing the hit from blastn.
    public int score;            // the int score based on BlastUtils.scoreDNASequence().

    /**
     * Create a new SequenceHit from query and hit IDs and the HSP
     */
    public SequenceHit(String queryID, String hitID, Hsp hsp) {
        this.sequence = BlastUtils.combineDNASequences(hsp.getHspQseq(), hsp.getHspHseq(), false);
        this.queryID = queryID;
        this.hitID = hitID;
        this.hsp = hsp;
        // set score to zero if sequence doesn't contain C or G with true below
        this.score = (int) Math.round(100.0*BlastUtils.scoreDNASequence(sequence));
    }

    /**
     * Two are equal if they have the same sequence, and same queryID and hitID in either order
     */
    public boolean equals(Object o) {
        SequenceHit that = (SequenceHit) o;
        if (this.sequence.equals(that.sequence)) {
            if (this.queryID.equals(that.queryID) && this.hitID.equals(that.hitID)) return true;
            if (this.queryID.equals(that.hitID) && this.hitID.equals(that.queryID)) return true;
        }
        return false;
    }

    /**
     * Order by score, then sequence, then queryID then hitID
     */
    public int compareTo(Object o) {
        SequenceHit that = (SequenceHit) o;
        if (this.score!=that.score) {
            return this.score - that.score;
        } else if (!this.sequence.equals(that.sequence)) {
            return this.sequence.compareTo(that.sequence);
        } else if (this.queryID.equals(that.queryID)) {
            return this.hitID.compareTo(that.hitID);
        } else if (this.queryID.equals(that.hitID)) {
            return this.hitID.compareTo(that.queryID);
        } else if (this.hitID.equals(that.queryID)) {
            return this.queryID.compareTo(that.hitID);
        } else {
            return this.queryID.compareTo(that.queryID);
        }
    }

    /**
     * return a string representation of the query ID and range
     */
    public String getQueryLoc() {
        return queryID+":"+hsp.getHspQueryFrom()+"-"+hsp.getHspQueryTo();
    }

    /**
     * return a string representation of the hit ID and range
     */
    public String getHitLoc() {
        return hitID+":"+hsp.getHspHitFrom()+"-"+hsp.getHspHitTo();
    }

}



