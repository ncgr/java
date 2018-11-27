package org.ncgr.pangenomics.fr;

/**
 *
 * @author bmumey
 */
public class ClusterEdge implements Comparable<ClusterEdge> {

    ClusterNode u, v;
    int fwdSup, rcSup;

    ClusterEdge(ClusterNode u, ClusterNode v, int f, int r) {
        this.u = u;
        this.v = v;
        fwdSup = f;
        rcSup = r;
    }
    
    int sup() {
        return fwdSup + rcSup;
    }

    ClusterNode other(ClusterNode x) {
        if (u == x) {
            return v;
        } else {
            return u;
        }
    }

    public int compareTo(ClusterEdge other) {
        int result = Integer.compare(other.sup(), sup());
        if (result == 0) {
            result = Integer.compare(Math.abs(u.size - v.size), Math.abs(other.u.size - other.v.size));
        }
        if (result == 0) {
            result = u.compareTo(other.u);
        }
        return result;
    }
}
