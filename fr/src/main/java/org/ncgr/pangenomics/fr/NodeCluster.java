package org.ncgr.pangenomics.fr;

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

    // this cluster's subpaths, identified by their path name, that pass through nodes in this cluster
    TreeSet<Subpath> subpaths;
    
    // the forward (or total) support of this cluster
    int fwdSupport = 0;
    
    // the reverse-complement support of this cluster (if enabled)
    int rcSupport = 0;

    // the current value of the filter parameters used to update this NodeCluster
    double alpha;
    int kappa;

    // the average length (in bases) of the subpath sequences
    int avgLength = 0;

    /**
     * Construct given a set of nodes and subpaths and alpha and kappa filter parameters.
     */
    NodeCluster(TreeSet<Long> nodes, TreeSet<Subpath> subpaths, Map<Long,String> nodeSequences, double alpha, int kappa) {
        this.nodes = nodes;
        this.subpaths = subpaths;
        this.nodeSequences = nodeSequences;
        this.alpha = alpha;
        this.kappa = kappa;
        update();
    }

    /**
     * Update this cluster with its existing contents and the current alpha,kappa values.
     * This should be run any time a new NodeCluster is made.
     */
    void update() {
        updateAvgLength();
        updateSupport();
        updateSubpaths();
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
        for (Subpath subpath : subpaths) {
            for (Long nodeId : subpath.nodes) {
                String sequence = nodeSequences.get(nodeId);
                if (sequence==null) {
                    System.out.println("sequence is null for node "+nodeId);
                } else {
                    dAvg += (double) sequence.length();
                }
            }
        }
        avgLength = (int)(dAvg/subpaths.size());
    }

    /**
     * Update the subpaths for the current alpha and kappa values.
     * NOTE: NOT FINISHED, NEED TO IMPLEMENT kappa FILTER.
     */
    void updateSubpaths() {
        TreeSet<Subpath> newSubpaths = new TreeSet<>();
        for (Subpath subpath : subpaths) {
            boolean ok = true;
            // alpha filter
            int in = 0;
            for (Long nodeId : subpath.nodes) {
                if (nodes.contains(nodeId)) {
                    in++;
                }
            }
            double frac = (double)in/(double)nodes.size();
            ok = !(frac<alpha); // avoid == on doubles
            // kappa filter
            // TO DO
            // update
            if (ok) newSubpaths.add(subpath);
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

    // /**
    //  * Return true if this ClusterNode contains the given node (identified by its nodeId).
    //  */
    // boolean containsNode(long n) {
    //     if (n==nodeId) {
    //         return true;
    //     } else if (left!=null) {
    //         return left.containsNode(n);
    //     } else if (right!=null) {
    //         return right.containsNode(n);
    //     }
    //     return false;
    // }

    // /**
    //  * Return the depth of this ClusterNode, itself plus parents.
    //  */
    // int depth() {
    //     if (parent == null) {
    //         return 1;
    //     } else {
    //         return parent.depth() + 1;
    //     }
    // }

    // /**
    //  * Return the root node, which is this one if it's an orphan.
    //  */
    // ClusterNode findRoot() {
    //     if (parent == null) {
    //         return this;
    //     } else {
    //         return parent.findRoot();
    //     }
    // }

    // /**
    //  * Add this node to a Set of nodes (identified by nodeId) or left/right if they exist.
    //  */
    // void addNodes(Set<Long> nodeSet) {
    //     if (left==null && right==null) {
    //         nodeSet.add(nodeId);
    //     }
    //     if (left!=null) {
    //         left.addNodes(nodeSet);
    //     }
    //     if (right!=null) {
    //         right.addNodes(nodeSet);
    //     }
    // }

    // /**
    //  * Find the pathLocs.
    //  */
    // synchronized void findPathLocs() {
    //     pathLocs = new TreeMap<String,List<Long>>();
    //     if (left!=null && left.pathLocs==null) {
    //         left.findPathLocs();
    //     }
    //     if (right!=null && right.pathLocs==null) {
    //         right.findPathLocs();
    //     }
    //     TreeSet<String> paths = new TreeSet<String>();
    //     if (left!=null) {
    //         paths.addAll(left.pathLocs.keySet());
    //     }
    //     if (right!=null) {
    //         paths.addAll(right.pathLocs.keySet());
    //     }
    //     for (String sample : paths) {
    //         int numlocs = 0;
    //         if (left.pathLocs.containsKey(sample)) {
    //             numlocs += left.pathLocs.get(sample).size();
    //         }
    //         if (right.pathLocs.containsKey(nodeId)) {
    //             numlocs += right.pathLocs.get(sample).size();
    //         }
    //         List<Long> pathList = new LinkedList<>();
    //         if (left.pathLocs.containsKey(sample)) {
    //             for (long n : left.pathLocs.get(sample)) {
    //                 pathList.add(n);
    //             }
    //         }
    //         if (right.pathLocs.containsKey(sample)) {
    //             for (long n : right.pathLocs.get(sample)) {
    //                 pathList.add(n);
    //             }
    //         }
    //         pathLocs.put(sample, pathList);
    //     }
    // }


    /**
     * Return a string summary of this cluster.
     */
    public String toString() {
        String s = "Nodes:";
        for (Long nodeId : nodes) {
            s += " "+nodeId;
        }
        s += "\nSubpaths (avgLength="+avgLength+";fwdSupport="+fwdSupport+")";
        for (Subpath subpath : subpaths) {
            s += "\n ["+subpath.pathName+"]"+subpath.nodes.toString().replace(" ","").replace("[","").replace("]","");
        }
        return s;
    }

    /**
     * Merge two NodeClusters and return the result.
     */
    static NodeCluster merge(NodeCluster nc1, NodeCluster nc2, double alpha, int kappa) {
        TreeSet<Long> nodes = new TreeSet<>();
        TreeSet<Subpath> subpaths = new TreeSet<>();
        Map<Long,String> nodeSequences = new TreeMap<>();
        nodes.addAll(nc1.nodes);
        nodes.addAll(nc2.nodes);
        subpaths.addAll(nc1.subpaths);
        subpaths.addAll(nc2.subpaths);
        nodeSequences.putAll(nc1.nodeSequences);
        return new NodeCluster(nodes, subpaths, nodeSequences, alpha, kappa);
    }
}
