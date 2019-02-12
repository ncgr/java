package org.ncgr.pangenomics.fr;

import org.ncgr.pangenomics.Graph;
import org.ncgr.pangenomics.Node;
import org.ncgr.pangenomics.NodeSet;
import org.ncgr.pangenomics.Path;

import java.text.DecimalFormat;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * Represents a cluster of nodes along with the supporting subpaths of the full set of strain/subject/subspecies paths.
 *
 * @author Sam Hokin
 */
public class FrequentedRegion implements Comparable<FrequentedRegion> {

    // static utility stuff
    static DecimalFormat df = new DecimalFormat("0.000");
    
    // the Graph that this FrequentedRegion belongs to
    Graph graph;

    // the set of Nodes that encompass this FR
    NodeSet nodes;
    
    // the subpaths, identified by their originating path name and label, that start and end on this FR's nodes
    Set<Path> subpaths;
    
    // the forward (or total, if rc not enabled) support of this cluster
    int fwdSupport = 0;
    
    // the reverse-complement support of this cluster (if enabled)
    // NOT YET IMPLEMENTED
    int rcSupport = 0;

    // the total support = fwdSupport + rcSupport;
    int support = 0;

    // the average length of the subpath sequences
    double avgLength;

    // a subpath must satisfy the requirement that it traverses at least alpha*nodes.size()
    double alpha;

    // a subpath must satisfy the requirement that its contiguous nodes that do NOT belong in this.nodes have total sequence length no larger than kappa
    int kappa;

    /**
     * Construct given a Graph, NodeSet and alpha and kappa filter parameters.
     */
    FrequentedRegion(Graph graph, NodeSet nodes, double alpha, int kappa) {
        this.graph = graph;
        this.nodes = nodes;
        this.alpha = alpha;
        this.kappa = kappa;
        // compute the subpaths, average length, support, etc.
        this.nodes.update();
        updateSubpaths();
        updateSupport();
        updateAvgLength();
    }

    /**
     * Construct given a Graph, NodeSet and Subpaths
     */
    FrequentedRegion(Graph graph, NodeSet nodes, Set<Path> subpaths, double alpha, int kappa) {
        this.graph = graph;
        this.nodes = nodes;
        this.subpaths = subpaths;
        this.alpha = alpha;
        this.kappa = kappa;
        updateSupport();
        updateAvgLength();
    }
        
    /**
     * Construct given only a NodeSet, used for various post-processing routines.
     */
    FrequentedRegion(NodeSet nodes) {
        this.nodes = nodes;
        this.nodes.update();
    }

    /**
     * Equality is simply based on the NodeSets.
     */
    public boolean equals(FrequentedRegion that) {
        return this.nodes.equals(that.nodes);
    }

    /**
     * Comparison is based on the NodeSet comparator.
     */
    public int compareTo(FrequentedRegion that) {
        return this.nodes.compareTo(that.nodes);
    }
    
    /**
     * Update the average length of this frequented region's subpath sequences.
     */
    void updateAvgLength() {
        int totalLength = 0;
        for (Path subpath : subpaths) {
            for (Node node : subpath.nodes) {
                totalLength += node.sequence.length();
            }
        }
        avgLength = (double)totalLength/(double)subpaths.size();
    }

    /**
     * Update the subpaths from the graph paths for the current alpha and kappa values.
     */
    void updateSubpaths() {

        subpaths = new HashSet<>();
        for (Path p : graph.paths) {
            subpaths.addAll(computeSupport(nodes, p, alpha, kappa));
        }


        // // we'll replace subpaths with this set
        // Set<Path> newSubpaths = new HashSet<>();
        
        // // loop through each genome path
        // // TODO: handle multiple subpaths of a path
        // for (Path path : graph.paths) {

        //     // find the MAXIMUM subpath, even if it fails tests below!
        //     Node left = null;
        //     Node right = null;
        //     for (Node node : path.nodes) {
        //         if (nodes.contains(node)) {
        //             if (left==null) {
        //                 left = node;
        //             } else {
        //                 right = node;
        //             }
        //         }
        //     }
            
        //     // build the subpath
        //     Path subpath = new Path(path.name, path.genotype, path.label);

        //     if (left!=null && right==null) {
        //         // single-node subpath
        //         subpath.addNode(left);

        //     } else if (left!=null && right!=null) {
        //         // load the insertions into a set for the kappa filter
        //         List<List<Node>> insertions = new LinkedList<>();
        //         // scan across the full path from left to right to create the subpath
        //         boolean started = false;
        //         boolean ended = false;

        //         List<Node> insertion = new LinkedList<>();
        //         for (Node node : path.nodes) {
        //             if (node.equals(left)) {
        //                 // add the leftmost path node
        //                 subpath.addNode(node);
        //                 started = true;
        //             } else if (node.equals(right)) {
        //                 // add the rightmost path node
        //                 subpath.addNode(node);
        //                 ended = true;
        //                 break; // we're done with this loop
        //             } else if (started && !ended) {
        //                 // add all path nodes between left and right
        //                 subpath.addNode(node);
        //                 // deal with insertion
        //                 if (nodes.contains(node)) {
        //                     // NOT an insertion
        //                     if (insertion.size()>0) {
        //                         // finish off the previous insertion and start another
        //                         insertions.add(insertion);
        //                         insertion = new LinkedList<>();
        //                     }
        //                 } else {
        //                     // IS an insertion
        //                     insertion.add(node);
        //                 }
        //             }
        //         }
        //         // add the last insertion if nonzero
        //         if (insertion.size()>0) {
        //             insertions.add(insertion);
        //         }
                
        //         // kappa filter, bail if we have an insertion exceeding kappa in length
        //         boolean kappaOK = true;
        //         for (List<Node> insn : insertions) {
        //             String insnSequence = "";
        //             for (Node node : insn) {
        //                 insnSequence += node.sequence;
        //             }
        //             if (insnSequence.length()>kappa) {
        //                 kappaOK = false;
        //                 break;
        //             }
        //         }

        //         // bail on this path if it fails kappa test
        //         if (!kappaOK) continue;
        //     }

            
        //     // alpha filter = minimum fraction of FR's NODES
        //     int count = 0;
        //     for (Node node : subpath.nodes) {
        //         if (this.nodes.contains(node)) {
        //             count++;
        //         }
        //     }
        //     double frac = (double)(count)/(double)this.nodes.size();
        //     if (frac<alpha) continue;

        //     // filters passed, add this subpath
        //     newSubpaths.add(subpath);
        // }

        // // replace the instance subpaths with the new set
        // subpaths = newSubpaths;
    }

    /**
     * Update the current support of this frequented region, which right now is just the size of the subpaths map.
     * NOTE: haven't yet implemented rc option
     */
    void updateSupport() {
        fwdSupport = subpaths.size();
        support = fwdSupport + rcSupport;
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
     * Return the column heading for the toString() fields
     */
    public String columnHeading() {
        String s = "nodes\tsupport\tavgLen";
        for (String label : graph.labelCounts.keySet()) {
            s += "\t"+label;
        }
        return s;
    }

    /**
     * Return the count of subpaths labeled with the given label.
     */
    public int getLabelCount(String label) {
        int count = 0;
        for (Path subpath : subpaths) {
            if (subpath.label!=null && subpath.label.equals(label)) count++;
        }
        return count;
    }

    /**
     * Return the count of subpaths labeled with the given label and genotype.
     */
    public int getLabelGenotypeCount(String label, int genotype) {
        int count = 0;
        for (Path subpath : subpaths) {
            if (subpath.label!=null) {
                if (subpath.label.equals(label) && subpath.genotype==genotype) count++;
            }
        }
        return count;
    }

    /**
     * Return a string summary of this frequented region.
     */
    public String toString() {
        // count the support per label if present
        Map<String,Integer> labelCounts = new TreeMap<>();
        for (Path subpath : subpaths) {
            if (subpath.label!=null) {
                if (!labelCounts.containsKey(subpath.label)) {
                    labelCounts.put(subpath.label, getLabelCount(subpath.label));
                }
            }
        }
        String s = nodes.toString()+"\t"+support+"\t"+Math.round(avgLength);
        // show labels (fractions) if available
        if (graph.labelCounts!=null && graph.labelCounts.size()>0) {
            for (String label : graph.labelCounts.keySet()) {
                if (labelCounts.containsKey(label)) {
                    s += "\t"+labelCounts.get(label);
                } else {
                    s += "\t"+0;
                }
            }
        }
        return s;
    }

    /**
     * Merge two FrequentedRegions and return the result.
     */
    // static FrequentedRegion merge(FrequentedRegion fr1, FrequentedRegion fr2) {
    //     // validation
    //     if (!fr1.graph.equals(fr2.graph)) {
    //         System.err.println("ERROR: attempt to merge FRs in different graphs.");
    //         System.exit(1);
    //         return null;
    //     } else if (fr1.alpha!=fr2.alpha) {
    //         System.err.println("ERROR: attempt to merge FRs with different alpha values.");
    //         System.exit(1);
    //         return null;
    //     } else if (fr1.kappa!=fr2.kappa) {
    //         System.err.println("ERROR: attempt to merge FRs with different kappa values.");
    //         System.exit(1);
    //         return null;
    //     } else {
    //         NodeSet nodes = new NodeSet();
    //         nodes.addAll(fr1.nodes);
    //         nodes.addAll(fr2.nodes);
    //         return new FrequentedRegion(fr1.graph, nodes, fr1.alpha, fr1.kappa);
    //     }
    // }

    /**
     * Algorithm 1 from Cleary, et al. generates the supporting path segments for the given NodeSet c and and Path p.
     * @param c the NodeSet, or cluster C as it's called in Algorithm 1
     * @param p the Path for which we want the set of supporting paths
     * @param alpha the penetrance parameter
     * @param kappa the insertion parameter
     * @returns the set of supporting path segments
     */
    static Set<Path> computeSupport(NodeSet c, Path p, double alpha, int kappa) {
        Set<Path> s = new HashSet<>();
        // m = the list of p's nodes that are in c
        LinkedList<Node> m = new LinkedList<>();
        for (Node n : p.nodes) {
            if (c.contains(n)) m.add(n);
        }
        // find subpaths that satisfy alpha, kappa criteria
        int start = 0;
        while (start<m.size()) {
            int i = start;
            Node nl = m.get(i);
            Node nr = nl;
            while ((i<m.size()-1)) {
                if (p.gap(nl,m.get(i+1))>kappa) break;
                i = i + 1;
                nr = m.get(i);
            }
            if ((i-start+1)>=alpha*c.size()) {
                Path subpath = p.subpath(nl,nr);
                if (subpath.nodes.size()==0) {
                    System.err.println("ERROR: subpath.nodes.size()=0; p="+p+" nl="+nl+" nr="+nr);
                } else {
                    s.add(subpath);
                }
            }
            start = i + 1;
        }
        return s;
    }

    /**
     * Algorithm 2 from Cleary, et al. returns the supporting path segments for the given merge of FRs.
     * @param fr1 the "left" FR (represented by (C_L,S_L) in the paper)
     * @param fr2 the "right FR (represented by (C_R,S_R) in the paper)
     * @returns the set of supporting path segments
     */
    static FrequentedRegion merge(FrequentedRegion fr1, FrequentedRegion fr2, Graph graph, double alpha, int kappa) {
        NodeSet c = NodeSet.merge(fr1.nodes, fr2.nodes);
        return new FrequentedRegion(graph, c, alpha, kappa);
    }

    /**
     * Return true if this FR contains a subpath which belongs to the given Path.
     */
    public boolean containsSubpathOf(Path path) {
        for (Path sp : subpaths) {
            if (sp.equals(path)) return true;
        }
        return false;
    }

    /**
     * Return a count of subpaths of FR that belong to the given Path.
     */
    public int countSubpathsOf(Path path) {
        int count = 0;
        for (Path sp : subpaths) {
            if (sp.equals(path)) count++;
        }
        return count;
    }

    /**
     * Return true if the nodes in this FR are a subset of the nodes in the given FR (but they are not equal!).
     */
    public boolean isSubsetOf(FrequentedRegion fr) {
        if (this.equals(fr)) {
            return false;
        } else {
            return this.nodes.equals(fr.nodes);
        }
    }
    
    /**
     * Return the count of subpaths that have the given label.
     */
    public int labelCount(String label) {
        int count = 0;
        for (Path sp : subpaths) {
            if (sp.label.equals(label)) count++;
        }
        return count;
    }
}
