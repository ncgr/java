package org.ncgr.pangenomics.fr;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents a cluster of nodes along with the supporting subpaths of the full set of strain/subject/subspecies paths.
 *
 * @author Sam Hokin
 */
public class FrequentedRegion implements Comparable<FrequentedRegion> {

    // the Graph that this FrequentedRegion belongs to
    Graph graph;

    // the set of Nodes that encompass this FR
    NodeSet nodes;
    
    // the subpaths, identified by their originating path name and label, that start and end on this FR's nodes
    List<Path> subpaths;
    
    // the forward (or total, if rc not enabled) support of this cluster
    int fwdSupport = 0;
    
    // the reverse-complement support of this cluster (if enabled)
    // NOT YET IMPLEMENTED
    int rcSupport = 0;

    // the total support = fwdSupport + rcSupport;
    int support = 0;

    // a subpath must satisfy the requirement that it traverses at least alpha*nodes.size()
    double alpha;

    // a subpath must satisfy the requirement that its contiguous nodes that do NOT belong in this.nodes have total sequence length no larger than kappa
    int kappa;

    // the (rounded) average length (in bases) of the subpath sequences
    int avgLength;

    /**
     * Construct given a Graph, NodeSet and alpha and kappa filter parameters.
     */
    FrequentedRegion(Graph graph, NodeSet nodes, double alpha, int kappa) {
        this.graph = graph;
        this.nodes = nodes;
        this.alpha = alpha;
        this.kappa = kappa;
        // compute the subpaths, average length, support, etc.
        update();
    }

    /**
     * Update this frequented region with its existing contents and the current alpha,kappa values.
     * This should be run any time a new FrequentedRegion is made.
     */
    void update() {
        updateSubpaths();
        updateSupport();  // must follow updatesubPaths()
        updateLengths();  // must follow updateSupport()
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
     * Update the total and average length of this frequented region's subpath sequences.
     */
    void updateLengths() {
        int totalLength = 0;
        for (Path subpath : subpaths) {
            for (Node node : subpath.nodes) {
                if (node.sequence==null) {
                    // bail, this is an error
                    System.err.println("ERROR: sequence is null for node "+node.id);
                    System.exit(1);
                } else {
                    totalLength += node.sequence.length();
                }
            }
        }
        avgLength = (int)((double)totalLength/subpaths.size());
    }

    /**
     * Update the subpaths from the full genome paths for the current alpha and kappa values.
     */
    void updateSubpaths() {

        // we'll replace subpaths with this set
        List<Path> newSubpaths = new LinkedList<>();
        
        // loop through each genome path
        // TODO: handle multiple subpaths of a path
        for (Path path : graph.paths) {

            // find the left/right endpoints of this path's subpath(s)
            Node left = null;
            Node right = null;
            for (Node node : path.nodes) {
                if (nodes.contains(node)) {
                    if (left==null) {
                        left = node;
                    } else {
                        right = node;
                    }
                }
            }
            
            // build the subpath
            Path subpath = new Path(path.name, path.label);

            if (left!=null && right==null) {
                // single-node subpath
                subpath.addNode(left);

            } else if (left!=null && right!=null) {
                // load the insertions into a set for the kappa filter
                List<List<Node>> insertions = new LinkedList<>();
                // scan across the full path from left to right to create the subpath
                boolean started = false;
                boolean ended = false;

                List<Node> insertion = new LinkedList<>();
                for (Node node : path.nodes) {
                    if (node.equals(left)) {
                        // add the leftmost path node
                        subpath.addNode(node);
                        started = true;
                    } else if (node.equals(right)) {
                        // add the rightmost path node
                        subpath.addNode(node);
                        ended = true;
                        break; // we're done with this loop
                    } else if (started && !ended) {
                        // add all path nodes between left and right
                        subpath.addNode(node);
                        // deal with insertion
                        if (nodes.contains(node)) {
                            // NOT an insertion
                            if (insertion.size()>0) {
                                // finish off the previous insertion and start another
                                insertions.add(insertion);
                                insertion = new LinkedList<>();
                            }
                        } else {
                            // IS an insertion
                            insertion.add(node);
                        }
                    }
                }
                // add the last insertion if nonzero
                if (insertion.size()>0) {
                    insertions.add(insertion);
                }
                
                // kappa filter, bail if we have an insertion exceeding kappa in length
                boolean kappaOK = true;
                for (List<Node> insn : insertions) {
                    String insnSequence = "";
                    for (Node node : insn) {
                        insnSequence += node.sequence;
                    }
                    if (insnSequence.length()>kappa) {
                        kappaOK = false;
                        break;
                    }
                }

                // bail on this path if it fails kappa test
                if (!kappaOK) continue;
            }

            
            // alpha filter
            int in = 0;
            for (Node node : subpath.nodes) {
                if (nodes.contains(node)) {
                    in++;
                }
            }
            double frac = (double)in/(double)nodes.size();
            if (frac<alpha) continue;

            // filters passed, add this subpath
            newSubpaths.add(subpath);
        }

        // replace the instance subpaths with the new set
        subpaths = newSubpaths;

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
     * Return a string summary of this frequented region.
     */
    public String toString() {
        String s = nodes.toString();
        Map<String,Integer> labelCount = new TreeMap<>();
        for (Path subpath : subpaths) {
            if (subpath.label!=null) {
                if (labelCount.containsKey(subpath.label)) {
                    int count = labelCount.get(subpath.label);
                    labelCount.put(subpath.label, count+1);
                } else {
                    labelCount.put(subpath.label, 1);
                }
            }
        }
        int len = s.length();
        for (int i=8; i<=80; i+=8) if (len<i) s += "\t";
        if (labelCount.size()>0) {
            s += avgLength;
            // special marking for special labels: case/ctrl
            if (labelCount.containsKey("case") && !labelCount.containsKey("ctrl")) {
                s += "\tcase:"+labelCount.get("case")+"\t######";
            } else if (labelCount.containsKey("ctrl") && !labelCount.containsKey("case")) {
                s += "\t######\tctrl:"+labelCount.get("ctrl");
            } else {
                for (String label : labelCount.keySet()) {
                    s += "\t"+label+":"+labelCount.get(label);
                }
            }
        } else {
            s += avgLength+"\t"+support;
        }
        return s;
    }

    /**
     * Merge two FrequentedRegions and return the result.
     */
    static FrequentedRegion merge(FrequentedRegion fr1, FrequentedRegion fr2) {
        // validation
        if (!fr1.graph.equals(fr2.graph)) {
            System.err.println("ERROR: attempt to merge FRs in different graphs.");
            System.exit(1);
            return null;
        } else if (fr1.alpha!=fr2.alpha) {
            System.err.println("ERROR: attempt to merge FRs with different alpha values.");
            System.exit(1);
            return null;
        } else if (fr1.kappa!=fr2.kappa) {
            System.err.println("ERROR: attempt to merge FRs with different kappa values.");
            System.exit(1);
            return null;
        } else {
            NodeSet nodes = new NodeSet();
            nodes.addAll(fr1.nodes);
            nodes.addAll(fr2.nodes);
            return new FrequentedRegion(fr1.graph, nodes, fr1.alpha, fr1.kappa);
        }
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
