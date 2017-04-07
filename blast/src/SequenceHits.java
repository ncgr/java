package org.ncgr.blast;

import java.util.TreeMap;
import java.util.TreeSet;

/**
 * A container class that stores SequenceHit instances for the same combined sequence (motif).
 * The method equals() matches against sequence only; compareTo() is designed to sort by score, then number of hits, then sequence alpha.
 * The score is the sum of SequenceHit.score values for DISTINCT query/subject IDs.
 *
 * @author Sam Hokin
 */
public class SequenceHits implements Comparable {

    public String sequence;                   // the sequence associated with the SequenceHit objects
    public int score;                         // the full score = sum of SequenceHit.score values.
    public TreeSet<SequenceHit> sequenceHits; // the SequenceHit instances contained within
    public TreeSet<String> uniqueHits;        // a set of strings representing unique hits of the form seqID:start-end
    public TreeSet<String> uniqueIDs;         // a set of unique query and hit IDs

    /**
     * Create a new SequenceHits instance from a SequenceHit
     */
    public SequenceHits(SequenceHit sequenceHit) {
        this.sequenceHits = new TreeSet<SequenceHit>();
        this.uniqueHits = new TreeSet<String>();
        this.uniqueIDs = new TreeSet<String>();
        this.sequence = sequenceHit.sequence;
        this.score = sequenceHit.score*2; // double since already two hits from the start
        this.sequenceHits.add(sequenceHit);
        this.uniqueHits.add(sequenceHit.getQueryLoc());
        this.uniqueHits.add(sequenceHit.getHitLoc());
        this.uniqueIDs.add(sequenceHit.queryID);
        this.uniqueIDs.add(sequenceHit.hitID);
    }
        
    /**
     * Two are equal if they have the same sequence (regardless of hits)
     */
    public boolean equals(Object o) {
        SequenceHits that = (SequenceHits) o;
        return this.sequence.equals(that.sequence);
    }

    /**
     * Order by score, then alphabetic
     */
    public int compareTo(Object o) {
        SequenceHits that = (SequenceHits) o;
        if (this.score!=that.score) {
            return this.score-that.score;
        } else {
            return this.sequence.compareTo(that.sequence);
        }
    }

    /**
     * Adjust the score and add a SequenceHit to the set and add to uniqueHits set. The score is only incremented for new IDs.
     */
    public void addSequenceHit(SequenceHit sequenceHit) {
        sequenceHits.add(sequenceHit);
        uniqueHits.add(sequenceHit.getQueryLoc());
        uniqueHits.add(sequenceHit.getHitLoc());
        uniqueIDs.add(sequenceHit.queryID);
        uniqueIDs.add(sequenceHit.hitID);
        score = uniqueIDs.size()*sequenceHit.score;
    }

    /**
     * Return true if this instance contains a SequenceHit with the given queryID
     */
    public boolean containsQueryID(String queryID) {
        for (SequenceHit seqHit : sequenceHits) {
            if (seqHit.queryID.equals(queryID)) return true;
        }
        return false;
    }

    /**
     * Return true if this instance contains a SequenceHit with the given hitID
     */
    public boolean containsHitID(String hitID) {
        for (SequenceHit seqHit : sequenceHits) {
            if (seqHit.hitID.equals(hitID)) return true;
        }
        return false;
    }

    /**
     * Return true if this instance contains a SequenceHit with either queryID or hitID matching the given ID
     */
    public boolean containsID(String id) {
        for (SequenceHit seqHit : sequenceHits) {
            if (seqHit.queryID.equals(id) || seqHit.hitID.equals(id)) return true;
        }
        return false;
    }

}
