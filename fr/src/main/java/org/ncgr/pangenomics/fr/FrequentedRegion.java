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
    static DecimalFormat df = new DecimalFormat("0.00");
    
    // the Graph that this FrequentedRegion belongs to
    Graph graph;

    // the set of Nodes that encompass this FR
    NodeSet nodes;
    
    // the subpaths, identified by their originating path name and label, that start and end on this FR's nodes
    Set<Path> subpaths;
    
    // the subpath support of this FR
    int support = 0;

    // the average length of the subpath sequences
    double avgLength;

    // a subpath must satisfy the requirement that it traverses at least alpha*nodes.size()
    double alpha;

    // a subpath must satisfy the requirement that its contiguous nodes that do NOT belong in this.nodes have total sequence length no larger than kappa
    int kappa;

    // the Graph's case and control path counts
    int casePaths;
    int ctrlPaths;

    /**
     * Construct given a Graph, NodeSet and alpha and kappa filter parameters.
     */
    FrequentedRegion(Graph graph, NodeSet nodes, double alpha, int kappa) {
        this.graph = graph;
        this.nodes = nodes;
        this.alpha = alpha;
        this.kappa = kappa;
        if (graph.labelCounts.get("case")!=null && graph.labelCounts.get("ctrl")!=null) {
            this.casePaths = graph.labelCounts.get("case");
            this.ctrlPaths = graph.labelCounts.get("ctrl");
        }
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
        if (graph.labelCounts.get("case")!=null && graph.labelCounts.get("ctrl")!=null) {
            this.casePaths = graph.labelCounts.get("case");
            this.ctrlPaths = graph.labelCounts.get("ctrl");
        }
        updateSupport();
        updateAvgLength();
    }

    /**
     * Construct given a Graph, NodeSet and Subpaths and already known support and avgLength
     */
    FrequentedRegion(Graph graph, NodeSet nodes, Set<Path> subpaths, double alpha, int kappa, int support, double avgLength) {
        this.graph = graph;
        this.nodes = nodes;
        this.subpaths = subpaths;
        this.alpha = alpha;
        this.kappa = kappa;
        this.support = support;
        this.avgLength = avgLength;
        if (graph.labelCounts.get("case")!=null && graph.labelCounts.get("ctrl")!=null) {
            this.casePaths = graph.labelCounts.get("case");
            this.ctrlPaths = graph.labelCounts.get("ctrl");
        }
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
                totalLength += node.getSequence().length();
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
    }

    /**
     * Update the current support of this frequented region, which right now is just the size of the subpaths map.
     * NOTE: haven't yet implemented rc option
     */
    void updateSupport() {
        support = subpaths.size();
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
        if (graph.labelCounts.size()>0) {
            for (String label : graph.labelCounts.keySet()) {
                s += "\t"+label;
            }
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
     * The metric used for case vs. control comparisons
     * |control paths*case support - case paths*control support|
     */
    public int caseControlDifference() {
        return Math.abs(getLabelCount("case")*ctrlPaths - getLabelCount("ctrl")*casePaths);
    }

    /**
     * Return a string summary of this frequented region.
     */
    public String toString() {
        String s = nodes.toString()+"\t"+support;
        if (support>0) {
            s += "\t"+df.format(avgLength);
            // show label support if available
            if (graph.labelCounts.size()>0) {
                // count the support per label
                Map<String,Integer> labelCounts = new TreeMap<>();
                for (Path subpath : subpaths) {
                    if (subpath.label!=null) {
                        if (!labelCounts.containsKey(subpath.label)) {
                            labelCounts.put(subpath.label, getLabelCount(subpath.label));
                        }
                    }
                }
                for (String label : graph.labelCounts.keySet()) {
                    if (labelCounts.containsKey(label)) {
                        s += "\t"+labelCounts.get(label);
                    } else {
                        s += "\t"+0;
                    }
                }
            }
        }
        return s;
    }

    /**
     * Return a string with the subpaths.
     */
    public String subpathsString() {
        String s = "";
        boolean first = true;
        for (Path sp : subpaths) {
            if (first) {
                first = false;
            } else {
                s += "\n";
            }
            s += sp.toString();
        }
        return s;
    }

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
            if (sp.name.equals(path.name) && sp.genotype==path.genotype) count++;
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
