package org.ncgr.pangenomics.fr;

import org.ncgr.jgraph.Node;
import org.ncgr.jgraph.NodeSet;
import org.ncgr.jgraph.PangenomicGraph;
import org.ncgr.jgraph.PathWalk;

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
    
    // the PangenomicGraph that this FrequentedRegion belongs to
    PangenomicGraph graph;

    // the set of Nodes that encompass this FR
    NodeSet nodes;
    
    // the subpaths, identified by their originating path name and label, that start and end on this FR's nodes
    Set<PathWalk> subpaths;
    
    // the subpath support of this FR
    int support = 0;

    // the average length of the subpath sequences
    double avgLength;

    // a subpath must satisfy the requirement that it traverses at least a fraction alpha of this.nodes
    double alpha;

    // a subpath must satisfy the requirement that its contiguous nodes that do NOT belong in this.nodes have total sequence length or number no larger than kappa
    int kappa;
    boolean kappaByNodes = false; // set true to use number of inserted nodes rather than length of inserted sequence in bp for kappa

    // the PangenomicGraph's case and control path counts
    int casePaths;
    int ctrlPaths;

    /**
     * Construct given a PangenomicGraph, NodeSet and alpha and kappa filter parameters.
     */
    FrequentedRegion(PangenomicGraph graph, NodeSet nodes, double alpha, int kappa, boolean kappaByNodes) {
        this.graph = graph;
        this.nodes = nodes;
        this.alpha = alpha;
        this.kappa = kappa;
        this.kappaByNodes = kappaByNodes;
        if (graph.getLabelCounts().get("case")!=null && graph.getLabelCounts().get("ctrl")!=null) {
            this.casePaths = graph.getLabelCounts().get("case");
            this.ctrlPaths = graph.getLabelCounts().get("ctrl");
        }
        // compute the subpaths, average length, support, etc.
        this.nodes.update();
        updateSupport();
        updateAvgLength();
    }

    /**
     * Construct given a PangenomicGraph, NodeSet and Subpaths
     */
    FrequentedRegion(PangenomicGraph graph, NodeSet nodes, Set<PathWalk> subpaths, double alpha, int kappa, boolean kappaByNodes) {
        this.graph = graph;
        this.nodes = nodes;
        this.subpaths = subpaths;
        this.alpha = alpha;
        this.kappa = kappa;
        this.kappaByNodes = kappaByNodes;
        if (graph.getLabelCounts().get("case")!=null && graph.getLabelCounts().get("ctrl")!=null) {
            this.casePaths = graph.getLabelCounts().get("case");
            this.ctrlPaths = graph.getLabelCounts().get("ctrl");
        }
        support = subpaths.size();
        updateAvgLength();
    }

    /**
     * Construct given a PangenomicGraph, NodeSet and Subpaths and already known support and avgLength
     */
    FrequentedRegion(PangenomicGraph graph, NodeSet nodes, Set<PathWalk> subpaths, double alpha, int kappa, boolean kappaByNodes, int support, double avgLength) {
        this.graph = graph;
        this.nodes = nodes;
        this.subpaths = subpaths;
        this.alpha = alpha;
        this.kappa = kappa;
        this.kappaByNodes = kappaByNodes;
        this.support = support;
        this.avgLength = avgLength;
        if (graph.getLabelCounts().get("case")!=null && graph.getLabelCounts().get("ctrl")!=null) {
            this.casePaths = graph.getLabelCounts().get("case");
            this.ctrlPaths = graph.getLabelCounts().get("ctrl");
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
        for (PathWalk subpath : subpaths) {
            for (Node node : subpath.getNodes()) {
                totalLength += node.getSequence().length();
            }
        }
        avgLength = (double)totalLength/(double)subpaths.size();
    }

    /**
     * Update the subpaths and support from the graph paths for the current alpha and kappa values.
     */
    void updateSupport() {
        subpaths = new HashSet<>();
        for (PathWalk p : graph.getPaths()) {
            Set<PathWalk> supportPaths = p.computeSupport(nodes, alpha, kappa, kappaByNodes);
            subpaths.addAll(supportPaths);
        }
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
        if (graph.getLabelCounts().size()>0) {
            for (String label : graph.getLabelCounts().keySet()) {
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
        for (PathWalk subpath : subpaths) {
            if (subpath.getLabel()!=null && subpath.getLabel().equals(label)) count++;
        }
        return count;
    }

    /**
     * Return the count of subpaths labeled with the given label and genotype.
     */
    public int getLabelGenotypeCount(String label, int genotype) {
        int count = 0;
        for (PathWalk subpath : subpaths) {
            if (subpath.getLabel()!=null) {
                if (subpath.getLabel().equals(label) && subpath.getGenotype()==genotype) count++;
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
            if (graph.getLabelCounts().size()>0) {
                // count the support per label
                Map<String,Integer> labelCounts = new TreeMap<>();
                for (PathWalk subpath : subpaths) {
                    if (subpath.getLabel()!=null) {
                        if (!labelCounts.containsKey(subpath.getLabel())) {
                            labelCounts.put(subpath.getLabel(), getLabelCount(subpath.getLabel()));
                        }
                    }
                }
                for (String label : graph.getLabelCounts().keySet()) {
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
        for (PathWalk sp : subpaths) {
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
     * Return true if this FR contains a subpath which belongs to the given PathWalk.
     */
    public boolean containsSubpathOf(PathWalk path) {
        for (PathWalk sp : subpaths) {
            if (sp.equals(path)) return true;
        }
        return false;
    }

    /**
     * Return a count of subpaths of FR that belong to the given PathWalk.
     */
    public int countSubpathsOf(PathWalk path) {
        int count = 0;
        for (PathWalk sp : subpaths) {
            if (sp.getName().equals(path.getName()) && sp.getGenotype()==path.getGenotype()) count++;
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
        for (PathWalk sp : subpaths) {
            if (sp.getLabel().equals(label)) count++;
        }
        return count;
    }
}
