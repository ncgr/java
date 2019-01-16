package org.ncgr.pangenomics.fr;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a cluster of nodes along with the supporting subpaths.
 *
 * @author Sam Hokin
 */
public class NodeCluster implements Comparable<NodeCluster> {
    // this cluster's nodes
    TreeSet<Long> nodes;

    // the sequences for ALL nodes, keyed by nodeId
    Map<Long,String> nodeSequences;

    // the full paths, keyed and sorted by path name/subject/strain name.
    TreeSet<Path> paths;

    // the subpaths, identified by their originating path name, that start and end with nodes in this cluster
    TreeSet<Path> subpaths;
    
    // the forward (or total) support of this cluster
    int fwdSupport;
    
    // the reverse-complement support of this cluster (if enabled)
    // NOT YET IMPLEMENTED
    int rcSupport;

    // the current value of the filter parameters used to update this NodeCluster
    double alpha;
    int kappa;

    // the average length (in bases) of the subpath sequences
    int avgLength;

    /**
     * Construct given a set of nodes and full paths and sequences, along with alpha and kappa filter parameters.
     */
    NodeCluster(TreeSet<Long> nodes, TreeSet<Path> paths, Map<Long,String> nodeSequences, double alpha, int kappa) {
        this.nodes = nodes;
        this.paths = paths;
        this.nodeSequences = nodeSequences;
        this.alpha = alpha;
        this.kappa = kappa;
        // compute the subpaths, average length, support.
        update();
    }

    /**
     * Update this cluster with its existing contents and the current alpha,kappa values.
     * This should be run any time a new NodeCluster is made.
     */
    void update() {
        updateSubpaths();
        updateAvgLength();
        updateSupport();  // must follow updatesubPaths() since some paths may have been dropped
    }
    
    /**
     * Compare based on support and then number of nodes and finally the first node.
     */
    public int compareTo(NodeCluster that) {
        if (this.fwdSupport!=that.fwdSupport) {
            return Integer.compare(this.fwdSupport, that.fwdSupport);
        } else if (this.nodes.size()!=that.nodes.size()) {
            return Integer.compare(this.nodes.size(), that.nodes.size());
        } else if (this.avgLength!=that.avgLength) {
            return Integer.compare(this.avgLength, that.avgLength);
        } else {
            return Long.compare(this.nodes.first(), that.nodes.first());
        }
    }

    /**
     * Update the current average length this cluster's subpath sequences.
     */
    void updateAvgLength() {
        double dAvg = 0.0;
        for (Path subpath : subpaths) {
            for (Long nodeId : subpath.nodes) {
                String sequence = nodeSequences.get(nodeId);
                if (sequence==null) {
                    System.out.println("Sequence is null for node "+nodeId);
                } else {
                    dAvg += (double) sequence.length();
                }
            }
        }
        avgLength = (int)(dAvg/subpaths.size());
    }

    /**
     * Update the subpaths from the full genome paths for the current alpha and kappa values.
     * NOTE: NOT FINISHED, NEED TO IMPLEMENT kappa FILTER.
     */
    void updateSubpaths() {
        TreeSet<Path> newSubpaths = new TreeSet<>();
        for (Path path : paths) {
            LinkedList<Long> nodeIds = path.nodes;
            long left = 0;
            long right = 0;
            for (Long nodeId : nodeIds) {
                if (nodes.contains(nodeId)) {
                    if (left==0) {
                        left = nodeId;
                    } else if (right==0) {
                        right = nodeId;
                    }
                }
            }
            LinkedList<Long> newNodes = new LinkedList<>();
            if (left!=0 && right==0) {
                // singleton
                newNodes.add(left);
            } else if (left!=0 && right!=0) {
                // create the subpath from left to right
                boolean started = false;
                boolean ended = false;
                for (long nodeId : nodeIds) {
                    if (nodeId==left) {
                        started = true;
                        newNodes.add(nodeId);
                    } else if (nodeId==right) {
                        ended = true;
                        newNodes.add(nodeId);
                    } else if (started && !ended) {
                        newNodes.add(nodeId);
                    }
                }
                // kappa filter goes here
            } else {
                continue; // this subpath is no more
            }
            // alpha filter
            boolean ok = true;
            int in = 0;
            for (Long nodeId : nodes) {
                if (newNodes.contains(nodeId)) {
                    in++;
                }
            }
            double frac = (double)in/(double)nodes.size();
            ok = !(frac<alpha); // (avoid >= on doubles)
            if (ok) {
                newSubpaths.add(new Path(path.name, newNodes));
            }
        }
        // replace the instance subpaths
        subpaths = newSubpaths;
    }

    /**
     * Update the current support of this cluster, which right now is just the size of the subpaths map.
     */
    void updateSupport() {
        fwdSupport = subpaths.size();
    }

    /**
     * Set a new alpha value.
     */
    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    /**
     * Set a new kappa value.
     */
    public void setKappa(int kappa) {
        this.kappa = kappa;
    }

    /**
     * Return a string summary of this cluster.
     */
    public String toString() {
        String s = "Nodes:";
        for (Long nodeId : nodes) {
            s += " "+nodeId;
        }
        s += "\nPaths (avgLength="+avgLength+";fwdSupport="+fwdSupport+")";
        for (Path subpath : subpaths) {
            s += "\n ["+subpath.name+"]"+subpath.nodes.toString().replace(" ","").replace("[","").replace("]","");
        }
        return s;
    }

    /**
     * Merge two NodeClusters and return the result.
     */
    static NodeCluster merge(NodeCluster nc1, NodeCluster nc2, double alpha, int kappa) {
        TreeSet<Long> nodes = new TreeSet<>();
        TreeSet<Path> paths = new TreeSet<>();
        Map<Long,String> nodeSequences = new TreeMap<>();
        nodes.addAll(nc1.nodes);
        nodes.addAll(nc2.nodes);
        nodeSequences.putAll(nc1.nodeSequences);
        nodeSequences.putAll(nc2.nodeSequences);
        paths.addAll(nc1.paths);
        paths.addAll(nc2.paths);
        // // DEBUG
        // System.out.println("merge: nodes="+nodes);
        // for (String pathName : paths.keySet()) {
        //     long left = 0;
        //     long right = 0;
        //     for (Long nodeId : paths.get(pathName)) {
        //         if (nodes.contains(nodeId)) {
        //             left = nodeId;
        //             break;
        //         }
        //     }
        //     Iterator<Long> it = paths.get(pathName).descendingIterator();
        //     while (it.hasNext()) {
        //         Long nodeId = it.next();
        //         if (nodes.contains(nodeId)) {
        //             right = nodeId;
        //             break;
        //         }
        //     }
        //     System.out.print("path="+pathName+" left="+left+" right="+right+":");
        //     boolean started = false;
        //     boolean ended = false;
        //     for (Long nodeId : paths.get(pathName)) {
        //         if (nodeId==left) {
        //             started = true;
        //         }
        //         if (started && !ended) System.out.print(" "+nodeId);
        //         if (nodeId==right) {
        //             ended = true;
        //         }
        //     }
        //     System.out.println(" "+paths.get(pathName));
        // }
        return new NodeCluster(nodes, paths, nodeSequences, alpha, kappa);
    }
}
