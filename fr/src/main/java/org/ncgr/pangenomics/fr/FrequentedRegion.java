package org.ncgr.pangenomics.fr;

import org.ncgr.jgraph.Node;
import org.ncgr.jgraph.NodeSet;
import org.ncgr.jgraph.NullNodeException;
import org.ncgr.jgraph.NullSequenceException;
import org.ncgr.jgraph.PangenomicGraph;
import org.ncgr.jgraph.PathWalk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.text.DecimalFormat;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.mskcc.cbio.portal.stats.FisherExact;

/**
 * Represents a cluster of nodes along with the supporting subpaths of the full set of strain/subject/subspecies paths.
 *
 * @author Sam Hokin
 */
public class FrequentedRegion implements Comparable {

    static String PRIORITY_OPTIONS = "0=total support, 1=case support-control support, 2=|case support-control support|, 3=Fisher's exact test double-sided p value, 4=control support (descending)";

    // static utility stuff
    static DecimalFormat df = new DecimalFormat("0.00");
    static DecimalFormat pf = new DecimalFormat("0.00E0");
    static DecimalFormat orf = new DecimalFormat("0.000");
    
    // the PangenomicGraph that this FrequentedRegion belongs to
    PangenomicGraph graph;

    // the set of Nodes that encompass this FR
    NodeSet nodes;
    
    // the subpaths, identified by their originating path name and label, that start and end on this FR's nodes
    Set<PathWalk> subpaths;
    
    // the subpath support of this FR
    int support = 0;

    // the case and control subpath support of this FR
    int caseSupport = 0;
    int ctrlSupport = 0;

    // the average length of the subpath sequences
    double avgLength;

    // a subpath must satisfy the requirement that it traverses at least a fraction alpha of this.nodes
    double alpha;

    // a subpath must satisfy the requirement that its contiguous nodes that do NOT belong in this.nodes have number no larger than kappa
    int kappa;

    // the PangenomicGraph's case and control path counts
    int casePaths;
    int ctrlPaths;

    // the priority option for comparison
    int priorityOption;

    /**
     * Construct given a PangenomicGraph, NodeSet and alpha and kappa filter parameters.
     */
    FrequentedRegion(PangenomicGraph graph, NodeSet nodes, double alpha, int kappa, int priorityOption) throws NullNodeException, NullSequenceException {
        this.graph = graph;
        this.nodes = nodes;
        this.alpha = alpha;
        this.kappa = kappa;
        this.priorityOption = priorityOption;
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
    FrequentedRegion(PangenomicGraph graph, NodeSet nodes, Set<PathWalk> subpaths, double alpha, int kappa, int priorityOption) {
        this.graph = graph;
        this.nodes = nodes;
        this.subpaths = subpaths;
        this.alpha = alpha;
        this.kappa = kappa;
        this.priorityOption = priorityOption;
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
    FrequentedRegion(PangenomicGraph graph, NodeSet nodes, Set<PathWalk> subpaths, double alpha, int kappa, int priorityOption, int support, double avgLength) {
        this.graph = graph;
        this.nodes = nodes;
        this.subpaths = subpaths;
        this.alpha = alpha;
        this.kappa = kappa;
        this.priorityOption = priorityOption;
        this.support = support;
        this.avgLength = avgLength;
        if (graph.getLabelCounts().get("case")!=null && graph.getLabelCounts().get("ctrl")!=null) {
            this.casePaths = graph.getLabelCounts().get("case");
            this.ctrlPaths = graph.getLabelCounts().get("ctrl");
        }
    }

    /**
     * Construct given only basic information, used for post-processing.
     */
    FrequentedRegion(NodeSet nodes, Set<PathWalk> subpaths, double alpha, int kappa, int priorityOption, int support, double avgLength) {
        this.nodes = nodes;
        this.subpaths = subpaths;
        this.alpha = alpha;
        this.kappa = kappa;
        this.priorityOption = priorityOption;
        this.support = support;
        this.avgLength = avgLength;
    }        

    /**
     * Equality is based on priority. MANY FRs are equal!
     */
    public boolean equals(Object o) {
	FrequentedRegion that = (FrequentedRegion) o;
        return this.getPriority()==that.getPriority();
    }

    /**
     * Comparison is based on priority; favor size as tie-breaker.
     */
    @Override
    public int compareTo(Object o) {
	FrequentedRegion that = (FrequentedRegion) o;
        int thisPriority = this.getPriority();
        int thatPriority = that.getPriority();
        if (thisPriority!=thatPriority) {
            return thisPriority - thatPriority;
        } else {
            return -(this.nodes.size() - that.nodes.size());
        }
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
    void updateSupport() throws NullNodeException, NullSequenceException {
        subpaths = new HashSet<>();
        for (PathWalk p : graph.getPaths()) {
            Set<PathWalk> supportPaths = p.computeSupport(nodes, alpha, kappa);
            subpaths.addAll(supportPaths);
        }
        support = subpaths.size();
        caseSupport = getLabelSupport("case");
        ctrlSupport = getLabelSupport("ctrl");
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
        if (graph!=null && graph.getLabelCounts().size()>0) {
            for (String label : graph.getLabelCounts().keySet()) {
                s += "\t"+label;
            }
            // odds ratio and p value
            s += "\tpri"+"\tOR"+"\tp";
        }
        return s;
    }

    /**
     * Return the support associated with the label.
     */
    public int getLabelSupport(String label) {
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
     * The integer metric used for case vs. control comparisons.
     * priorityOption:
     *   0 = total support
     *   1 =  case support - control support
     *   2 = |case support - control support|
     *   3 = case support if control support is zero, else log10(odds ratio)
     *   4 = -log10(p) where p = Fisher's exact test double-sided p value
     */
    public int getPriority() {
        int priority = 0;
        switch(priorityOption) {
        case 0 :
            priority = support;
            break;
        case 1 :
            priority = caseSupport - ctrlSupport;
            break;
        case 2 :
            priority = Math.abs(caseSupport - ctrlSupport);
            break;
        case 3 :
            if (ctrlSupport==0) {
                priority = caseSupport;
            } else {
                priority = (int)Math.round(Math.log10(oddsRatio()));
            }
            break;
        case 4 :
            priority = -(int)Math.round(Math.log10(fisherExactP())*100);
            break;
        default :
            // we've got an unallowed priority key for case/control comparison
            System.err.println("ERROR: priority option "+priorityOption+" is not supported by FrequentedRegion.getPriority(priorityOption).");
            System.exit(1);
            break;
        }
        return priority;
    }

    /**
     * Return the Fisher's exact test p value for cases vs controls.
     */
    public double fisherExactP() {
        int maxSize = casePaths + ctrlPaths + caseSupport + ctrlSupport;
        FisherExact fisherExact = new FisherExact(maxSize);
        return fisherExact.getTwoTailedP(caseSupport, ctrlSupport, casePaths, ctrlPaths);
    }

    /**
     * Return the odds ratio for cases vs controls.
     * When cases=0 set odds ratio = 10 rather than Infinity.
     */
    public double oddsRatio() {
        return ((double)caseSupport/casePaths) / ((double)ctrlSupport/ctrlPaths);
    }

    /**
     * Return a string summary of this frequented region.
     */
    public String toString() {
        String s = nodes.toString()+"\t"+support;
        if (support>0) {
            s += "\t"+df.format(avgLength);
            // show label support if available
            if (graph!=null && graph.getLabelCounts().size()>0) {
                // count the support per label
                Map<String,Integer> labelCounts = new TreeMap<>();
                for (PathWalk subpath : subpaths) {
                    if (subpath.getLabel()!=null) {
                        if (!labelCounts.containsKey(subpath.getLabel())) {
                            labelCounts.put(subpath.getLabel(), getLabelSupport(subpath.getLabel()));
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
                // add the priority
                s += "\t"+getPriority();
                // add the odds ratio
                s += "\t"+orf.format(oddsRatio());
                // add the Fisher's exact test p value
                s += "\t"+pf.format(fisherExactP());
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

    /**
     * Return the NodeSet associated with this FR.
     */
    public NodeSet getNodes() {
        return nodes;
    }

    /**
     * Return this FR's average length.
     */
    public double getAvgLength() {
        return avgLength;
    }

    /**
     * Command-line utility gives results for an input cluster of nodes and alpha, kappa and graph.
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, NullSequenceException, NullNodeException {
        Options options = new Options();
 
        Option alphaOption = new Option("a", "alpha", true, "starting value of alpha for a scan (can equal alphaend)");
        alphaOption.setRequired(true);
        options.addOption(alphaOption);
        //
        Option kappaOption = new Option("k", "kappa", true, "starting value of kappa for a scan (can equal kappaend)");
        kappaOption.setRequired(true);
        options.addOption(kappaOption);
        //
        Option priorityOption = new Option("pri", "priorityOption", true, "option for priority weighting of FRs: "+FrequentedRegion.PRIORITY_OPTIONS);
        priorityOption.setRequired(true);
        options.addOption(priorityOption);
        //
        Option genotypeOption = new Option("g", "genotype", true, "which genotype to include (0,1) from the input file; "+
                                           PangenomicGraph.BOTH_GENOTYPES+" to include all ["+PangenomicGraph.BOTH_GENOTYPES+"]");
        genotypeOption.setRequired(false);
        options.addOption(genotypeOption);
        //
        Option gfaOption = new Option("gfa", "gfa", true, "GFA file");
        gfaOption.setRequired(true);
        options.addOption(gfaOption);
        //
        Option labelsOption = new Option("p", "pathlabels", true, "tab-delimited file with pathname<tab>label");
        labelsOption.setRequired(false);
        options.addOption(labelsOption);
        //
        Option nodesOption = new Option("n", "nodes", true, "set of nodes to calculate FR e.g. [1,2,3,4,5]");
        nodesOption.setRequired(true);
        options.addOption(nodesOption);
        
        CommandLine cmd;
        HelpFormatter formatter = new HelpFormatter();
        try {
            CommandLineParser parser = new DefaultParser();
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp("FrequentedRegion", options);
            System.exit(1);
            return;
        }

        if (cmd.getOptions().length==0) {
            formatter.printHelp("FrequentedRegion", options);
            System.exit(1);
            return;
        }
        
        // GFA file
        File gfaFile = null;
        String gfaFilename = null;
        String graphName = null;
        if (cmd.hasOption("gfa")) {
            gfaFilename = cmd.getOptionValue("gfa");
            gfaFile = new File(gfaFilename);
            // get graphName from filename assuming graphName.paths.gfa
            String[] dirParts = gfaFilename.split("/");
            String filename = dirParts[dirParts.length-1];
            String[] dotParts = filename.split("\\.");
            graphName = dotParts[0];
            for (int i=1; i<dotParts.length-2; i++) graphName += "."+dotParts[i];
        }

        // path labels file
        File labelsFile = null;
        if (cmd.hasOption("pathlabels")) labelsFile = new File(cmd.getOptionValue("pathlabels"));

        // alpha, kappa, priorityOption
        double alpha = Double.parseDouble(cmd.getOptionValue("alpha"));
        int kappa = Integer.parseInt(cmd.getOptionValue("kappa"));
        int priority = Integer.parseInt(cmd.getOptionValue("priorityOption"));
        
        // import a PangenomicGraph from the GFA file
        PangenomicGraph pg = new PangenomicGraph();
        if (cmd.hasOption("genotype")) pg.setGenotype(Integer.parseInt(cmd.getOptionValue("genotype")));
        System.out.println("# Loading GFA file "+gfaFile.getName());
        pg.importGFA(gfaFile);
        System.out.println("# Graph has "+pg.vertexSet().size()+" nodes and "+pg.getPaths().size()+" paths.");
        // if a labels file is given, add them to the paths
        if (labelsFile!=null) {
            pg.readPathLabels(labelsFile);
            System.out.println("# Graph has "+pg.getLabelCounts().get("case")+" case paths and "+pg.getLabelCounts().get("ctrl")+" ctrl paths.");
        }

        // create the NodeSet
        String nodeString = cmd.getOptionValue("nodes");
        NodeSet nodes = new NodeSet(pg, nodeString);
        
        // create the FrequentedRegion with this PangenomicGraph
        FrequentedRegion fr = new FrequentedRegion(pg, nodes, alpha, kappa, priority);

        // print it out
        System.out.println(fr.columnHeading());
        System.out.println(fr.toString());
    }
}
