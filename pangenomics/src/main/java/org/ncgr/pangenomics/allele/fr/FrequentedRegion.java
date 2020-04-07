package org.ncgr.pangenomics.allele.fr;

import org.ncgr.pangenomics.allele.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.text.DecimalFormat;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;

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

    static String PRIORITY_OPTIONS =
        "0=total support, " +
        "1:label=label support-other support [case], " +
        "2=|case support-control support|, " +
        "3:label=odds ratio in label's favor [case], " +
        "4=Fisher's exact test double-sided p value";

    // static utility stuff
    static DecimalFormat df = new DecimalFormat("0.00");
    static DecimalFormat pf = new DecimalFormat("0.00E0");
    static DecimalFormat orf = new DecimalFormat("0.000");
    
    // the PangenomicGraph that this FrequentedRegion belongs to
    PangenomicGraph graph;

    // the set of Nodes that encompass this FR
    NodeSet nodes;
    
    // the subpaths, identified by their originating path name and label, that start and end on this FR's nodes
    List<Path> subpaths;
    
    // the subpath support of this FR
    int support = 0;

    // the case and control subpath support of this FR
    int caseSubpathSupport = 0;
    int ctrlSubpathSupport = 0;

    // the average length of the subpath sequences
    double avgLength;

    // a subpath must satisfy the requirement that it traverses at least a fraction alpha of this.nodes
    double alpha;

    // a subpath must satisfy the requirement that its contiguous nodes that do NOT belong in this.nodes have number no larger than kappa
    int kappa;

    // the priority and priority option for comparison
    int priority;
    String priorityOption;
    String priorityLabel;

    /**
     * Construct given a PangenomicGraph, NodeSet and alpha and kappa filter parameters.
     */
    FrequentedRegion(PangenomicGraph graph, NodeSet nodes, double alpha, int kappa, String priorityOption) { 
        this.graph = graph;
        this.nodes = nodes;
        this.alpha = alpha;
        this.kappa = kappa;
        this.priorityOption = priorityOption;
        update();
    }

    /**
     * Construct given a PangenomicGraph, string representation of nodes, and alpha and kappa and priorityOption parameters.
     * 0                               1       2               3       4       5       6       7
     * [7,9,14,19,103,132,174] 7       3030    21105.00        1582    1448    1554    1.373   2.89E-16
     */
    FrequentedRegion(PangenomicGraph graph, String frString, double alpha, int kappa, String priorityOption) {
        String[] parts = frString.split("\t");
        String nodeString = parts[0];
        support = Integer.parseInt(parts[1]);
        avgLength = Double.parseDouble(parts[2]);
        if (parts.length>3) {
            caseSubpathSupport = Integer.parseInt(parts[3]);
            ctrlSubpathSupport = Integer.parseInt(parts[4]);
        }
        this.graph = graph;
        this.nodes = new NodeSet(nodeString);
        this.alpha = alpha;
        this.kappa = kappa;
        this.priorityOption = priorityOption;
        update();
    }

    /**
     * Construct given a PangenomicGraph, NodeSet and Subpaths
     */
    FrequentedRegion(PangenomicGraph graph, NodeSet nodes, List<Path> subpaths, double alpha, int kappa, String priorityOption) {
        this.graph = graph;
        this.nodes = nodes;
        this.subpaths = subpaths;
        this.alpha = alpha;
        this.kappa = kappa;
        this.priorityOption = priorityOption;
        update();
    }

    /**
     * Construct given a PangenomicGraph, NodeSet and Subpaths and already known support and avgLength
     */
    FrequentedRegion(PangenomicGraph graph, NodeSet nodes, List<Path> subpaths, double alpha, int kappa, String priorityOption, int support, double avgLength) {
        this.graph = graph;
        this.nodes = nodes;
        this.subpaths = subpaths;
        this.alpha = alpha;
        this.kappa = kappa;
        this.support = support;
        this.avgLength = avgLength;
        this.priorityOption = priorityOption;
        update();
    }

    /**
     * Construct given only basic information, used for post-processing. NO GRAPH.
     */
    FrequentedRegion(NodeSet nodes, List<Path> subpaths, double alpha, int kappa, String priorityOption, int support, double avgLength) {
        this.nodes = nodes;
        this.subpaths = subpaths;
        this.alpha = alpha;
        this.kappa = kappa;
        this.support = support;
        this.avgLength = avgLength;
        this.priorityOption = priorityOption;
        update();
    }

    /**
     * Update the subpaths, average length, priority, etc.
     */
    void update() {
        updateSupport();
        updateAvgLength();
        updatePriorityLabel();
        updatePriority();
    }

    /**
     * Equality is based on nodes.
     */
    public boolean equals(Object o) {
	FrequentedRegion that = (FrequentedRegion) o;
        return this.nodes.equals(that.nodes);
    }

    /**
     * Comparison is based on priority, then node size (favoring smaller) and then nodes.
     */
    @Override
    public int compareTo(Object o) {
	FrequentedRegion that = (FrequentedRegion) o;
        if (this.priority!=that.priority) {
            return this.priority - that.priority;
        } else if (this.nodes.size()!=that.nodes.size()) {
            return -(this.nodes.size() - that.nodes.size());
        } else {
            return this.nodes.compareTo(that.nodes);
        }
    }

    /**
     * Update the average length of this frequented region's subpath sequences.
     */
    void updateAvgLength() {
        try {
            int totalLength = 0;
            for (Path subpath : subpaths) {
                for (Node node : subpath.getNodes()) {
                    totalLength += node.sequence.length();
                }
            }
            avgLength = (double)totalLength/(double)subpaths.size();
        } catch (Exception e) {
            System.err.println("Exception thrown in FrequentedRegion.updateAvgLength():");
            System.err.println(e);
            System.exit(1);
        }
    }

    /**
     * Update the subpaths and support from the graph paths for the current alpha and kappa values.
     */
    void updateSupport() {
        try {
            subpaths = new ArrayList<>();
            for (Path p : graph.getPaths()) {
                List<Path> supportPaths = p.computeSupport(nodes, alpha, kappa);
                subpaths.addAll(supportPaths);
            }
            support = subpaths.size();
            caseSubpathSupport = getLabelSupport("case");
            ctrlSubpathSupport = getLabelSupport("ctrl");
        } catch (Exception e) {
            System.err.println("Exception thrown in FrequentedRegion.updateSupport():");
            System.err.println(e);
            System.exit(1);
        }
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
     * nodes size support avgLen caseCounts ctrlCounts pri OR p
     */
    public String columnHeading() {
        String s = "nodes\tsize\tsupport\tavgLen";
        if (graph!=null && graph.getLabelCounts().size()>0) {
            for (String label : graph.getLabelCounts().keySet()) {
                s += "\t"+label;
            }
            // odds ratio and p value
            s += "\tOR"+"\tp"+"\tpri";
        }
        return s;
    }

    /**
     * Return the support associated with paths with the given label (not subpaths).
     */
    public int getPathSupport(String label) {
        int count = 0;
        List<String> countedPaths = new ArrayList<>();
        for (Path subpath : subpaths) {
            if (!countedPaths.contains(subpath.name) && subpath.label!=null && subpath.label.equals(label)) {
                countedPaths.add(subpath.name);
                count++;
            }
        }
        return count;
    }

    /**
     * Return the count of subpaths labeled with the given label. Here multiple subpaths of a path count individually.
     */
    public int getSubpathSupport(String label) {
        int count = 0;
        for (Path subpath : subpaths) {
            if (subpath.label!=null && subpath.label.equals(label)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Return the support associated with the label.
     */
    public int getLabelSupport(String label) {
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
     * Set the label to base label-based priority on.
     */
    void updatePriorityLabel() {
        String[] parts = priorityOption.split(":");
        if (parts.length>1) {
            priorityLabel = parts[1];
        } else {
            priorityLabel = "case"; // default
        }
    }

    /**
     * Get the integer metric used for case vs. control comparisons.
     * priorityOption:
     *   0 = total support
     *   1:label = label support - other label support
     *   2 = |case support - control support|
     *   3:label = odds ratio in label's favor
     *   4 = -log10(p) where p = Fisher's exact test double-sided p value
     */
    void updatePriority() {
        try {
            priority = 0;
            if (priorityOption.startsWith("0")) {
                priority = support;
            } else if (priorityOption.startsWith("1")) {
                if (priorityLabel.equals("case")) {
                    priority = caseSubpathSupport - ctrlSubpathSupport;
                } else if (priorityLabel.equals("ctrl")) {
                    priority = ctrlSubpathSupport - caseSubpathSupport;
                } else {
                    System.err.println("ERROR: priority label "+priorityLabel+" is not supported by FrequentedRegion.updatePriority().");
                    System.exit(1);
                }
            } else if (priorityOption.startsWith("2")) {
                priority = Math.abs(caseSubpathSupport - ctrlSubpathSupport);
            } else if (priorityOption.startsWith("3")) {
                if (priorityLabel.equals("case")) {
                    priority = (int)(Math.round(Math.log10(oddsRatio())*1000));
                } else if (priorityLabel.equals("ctrl")) {
                    priority = -(int)(Math.round(Math.log10(oddsRatio())*1000));
                } else {
                    System.err.println("ERROR: priority label "+priorityLabel+" is not supported by FrequentedRegion.updatePriority().");
                    System.exit(1);
                }
            } else if (priorityOption.startsWith("4")) {
                priority = -(int)Math.round(Math.log10(fisherExactP())*100);
            } else {
                // we've got an unallowed priority key for case/control comparison
                System.err.println("ERROR: priority option "+priorityOption+" is not supported by FrequentedRegion.updatePriority().");
                System.exit(1);
            }
        } catch (Exception e) {
            System.err.println("Exception thrown in FrequentedRegion.updatePriority():");
            System.err.println(e);
            System.exit(1);
        }
    }

    /**
     * Return the Fisher's exact test p value for case graph paths vs control graph paths.
     * NOTE1: this does NOT depend on subpaths support!
     * NOTE2: uses graph.fisherExact, which is computed once, since sum(cells)=sum(paths).
     *      | support         | non-support        |
     *      |--------------------------------------|
     * case | casePathSupport | casePathNonsupport |
     * ctrl | ctrlPathSupport | ctrlPathNonsupport |
     */
    public double fisherExactP() {
	int casePaths = graph.getPathCount("case");
	int ctrlPaths = graph.getPathCount("ctrl");
	int casePathSupport = getPathSupport("case");
	int ctrlPathSupport = getPathSupport("ctrl");
	int casePathNonsupport = casePaths - casePathSupport;
	int ctrlPathNonsupport = ctrlPaths - ctrlPathSupport;
        return graph.fisherExact.getTwoTailedP(casePathSupport, casePathNonsupport, ctrlPathSupport, ctrlPathNonsupport);
    }

    /**
     * Return the odds ratio for cases vs controls in terms of supporting subpaths vs. graph paths.
     * 0 = zero case subpath support, POSITIVE_INFINITY = zero control subpath support
     */
    public double oddsRatio() {
	int casePaths = graph.getPathCount("case");
	int ctrlPaths = graph.getPathCount("ctrl");
        if (ctrlSubpathSupport>0) {
            return (double)caseSubpathSupport * (double)ctrlPaths / ( (double)ctrlSubpathSupport * (double)casePaths );
        } else {
            return Double.POSITIVE_INFINITY;
        }
    }

    /**
     * Return a string summary of this frequented region.
     */
    public String toString() {
        String s = nodes.toString()+"\t"+nodes.size()+"\t"+support;
        if (support>0) {
            s += "\t"+df.format(avgLength);
            // show label support if available
            if (graph!=null && graph.getLabelCounts().size()>0) {
                // count the support per label
                Map<String,Integer> labelCounts = new TreeMap<>();
                for (Path subpath : subpaths) {
                    if (subpath.label!=null) {
                        if (!labelCounts.containsKey(subpath.label)) {
                            labelCounts.put(subpath.label, getLabelSupport(subpath.label));
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
                // add the odds ratio
                s += "\t"+orf.format(oddsRatio());
                // add the Fisher's exact test p value
                s += "\t"+pf.format(fisherExactP());
                // add the priority
                s += "\t"+priority;
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

    /**
     * Return the NodeSet associated with this FR.
     */
    public NodeSet getNodes() {
        return nodes;
    }

    /**
     * Return true if this FR contains the given node.
     */
    public boolean containsNode(Node n) {
        return nodes.contains(n);
    }

    /**
     * Return the count of case subpaths traversing the given node.
     */
    public int getCaseCount(Node n) {
        int count = 0;
        for (Path p : subpaths) {
            if (p.isCase()) {
                for (Node node : p.getNodes()) {
                    if (node.equals(n)) {
                        count++;
                        break;
                    }
                }
            }
        }
        return count;
    }

    /**
     * Return the count of control subpaths traversing the given node.
     */
    public int getControlCount(Node n) {
        int count = 0;
        for (Path p : subpaths) {
            if (p.isControl()) {
                for (Node node : p.getNodes()) {
                    if (node.equals(n)) {
                        count++;
                        break;
                    }
                }
            }
        }
        return count;
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
    public static void main(String[] args) throws FileNotFoundException, IOException {
        Options options = new Options();
 
        Option alphaOption = new Option("a", "alpha", true, "starting value of alpha for a scan (can equal alphaend)");
        alphaOption.setRequired(true);
        options.addOption(alphaOption);
        //
        Option kappaOption = new Option("k", "kappa", true, "starting value of kappa for a scan (can equal kappaend)");
        kappaOption.setRequired(true);
        options.addOption(kappaOption);
        //
        Option priorityOptionOption = new Option("pri", "priorityoption", true, "option for priority weighting of FRs: "+FrequentedRegion.PRIORITY_OPTIONS);
        priorityOptionOption.setRequired(true);
        options.addOption(priorityOptionOption);
        //
        Option genotypeOption = new Option("g", "genotype", true, "which genotype to include (0,1) from the input file; "+
                                           PangenomicGraph.BOTH_GENOTYPES+" to include all ["+PangenomicGraph.BOTH_GENOTYPES+"]");
        genotypeOption.setRequired(false);
        options.addOption(genotypeOption);
        //
        Option graphOption = new Option("graph", "graph", true, "graph name");
        graphOption.setRequired(true);
        options.addOption(graphOption);
        //
        Option gfaOption = new Option("gfa", "gfa", false, "load from [graph].paths.gfa");
        gfaOption.setRequired(false);
        options.addOption(gfaOption);
        //
        Option txtOption = new Option("txt", "txt", false, "load from [graph].nodes.txt and [graph].paths.txt");
        txtOption.setRequired(false);
        options.addOption(txtOption);
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
        
        // path labels file
        File labelsFile = null;
        if (cmd.hasOption("pathlabels")) labelsFile = new File(cmd.getOptionValue("pathlabels"));

        // alpha, kappa, priorityOption
        double alpha = Double.parseDouble(cmd.getOptionValue("alpha"));
        int kappa = Integer.parseInt(cmd.getOptionValue("kappa"));
        String priorityOption = cmd.getOptionValue("priorityoption");
        
        // import a PangenomicGraph from the GFA file
        PangenomicGraph pg = new PangenomicGraph();
	// apply options
        if (cmd.hasOption("genotype")) pg.setGenotype(Integer.parseInt(cmd.getOptionValue("genotype")));
	// graph name
	String graphName = cmd.getOptionValue("graph");
	if (cmd.hasOption("gfa")) {
            // GFA file
	    File gfaFile = new File(graphName+".paths.gfa");
	    pg.importGFA(gfaFile);
	    // if a labels file is given, add them to the paths
	    if (labelsFile!=null) {
		pg.readPathLabels(labelsFile);
                pg.tallyLabelCounts();
	    }
	} else if (cmd.hasOption("txt")) {
            // TXT file
	    File nodesFile = new File(graphName+".nodes.txt");
	    File pathsFile = new File(graphName+".paths.txt");
            pg.importTXT(nodesFile, pathsFile);
            pg.tallyLabelCounts();
        }
        System.out.println("# Graph has "+pg.getNodes().size()+" nodes and "+pg.getPaths().size()+" paths.");
        System.out.println("# Graph has "+pg.getLabelCounts().get("case")+" case paths and "+pg.getLabelCounts().get("ctrl")+" ctrl paths.");

        // create the FrequentedRegion with this PangenomicGraph
        String nodeString = cmd.getOptionValue("nodes");
        NodeSet nodes = pg.getNodeSet(nodeString);
        FrequentedRegion fr = new FrequentedRegion(pg, nodes, alpha, kappa, priorityOption);

        // print it out
        System.out.println(fr.columnHeading());
        System.out.println(fr.toString());
    }
}
