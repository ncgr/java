package org.ncgr.pangenomics.genotype.fr;

import org.ncgr.pangenomics.genotype.*;

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

/**
 * Represents a cluster of nodes along with the supporting subpaths of the full set of strain/subject/subspecies paths.
 *
 * @author Sam Hokin
 */
class FrequentedRegion implements Comparable {

    static String PRIORITY_OPTIONS =
        "0=total support, " +
        "1:label=label support-other support [case,ctrl,alt], " +
        "2=|case support-control support|, " +
        "3:label=odds ratio in label's favor [case,ctrl,alt], " +
        "4:label=Fisher's exact test double-sided p value [null,case,ctrl,alt]";

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

    // a subpath must satisfy the requirement that it traverses at least a fraction alpha of this.nodes
    double alpha;

    // a subpath must satisfy the requirement that its contiguous nodes that do NOT belong in this.nodes have number no larger than kappa
    int kappa;

    // the priority and priority option for comparison
    int priority;                // the priority metric
    int priorityOptionKey;       // 0, 1, 2, etc.
    String priorityOptionLabel;  // label for priority update emphasis, can be null

    // the calculated p value and odds ratio (saved when methods called first time)
    double pValue = Double.NEGATIVE_INFINITY;
    double orValue = Double.NEGATIVE_INFINITY;

    /**
     * Construct given a PangenomicGraph, NodeSet and alpha and kappa filter parameters.
     */
    FrequentedRegion(PangenomicGraph graph, NodeSet nodes, double alpha, int kappa, int priorityOptionKey, String priorityOptionLabel) {
        this.graph = graph;
        this.nodes = nodes;
        this.alpha = alpha;
        this.kappa = kappa;
        this.priorityOptionKey = priorityOptionKey;
        this.priorityOptionLabel = priorityOptionLabel;
        update();
    }

    /**
     * Construct given a PangenomicGraph, string representation of nodes, and alpha and kappa and priorityOption parameters.
     * 0                               1       2               3       4       5       6       7
     * [7,9,14,19,103,132,174] 7       3030    21105.00        1582    1448    1554    1.373   2.89E-16
     */
    FrequentedRegion(PangenomicGraph graph, String frString, double alpha, int kappa, int priorityOptionKey, String priorityOptionLabel) {
        String[] parts = frString.split("\t");
        String nodeString = parts[0];
        support = Integer.parseInt(parts[1]);
        if (parts.length>3) {
            caseSubpathSupport = Integer.parseInt(parts[3]);
            ctrlSubpathSupport = Integer.parseInt(parts[4]);
        }
        this.graph = graph;
        this.nodes = new NodeSet(nodeString);
        this.alpha = alpha;
        this.kappa = kappa;
        this.priorityOptionKey = priorityOptionKey;
        this.priorityOptionLabel = priorityOptionLabel;
        update();
    }

    /**
     * Construct given a PangenomicGraph, NodeSet and Subpaths
     */
    FrequentedRegion(PangenomicGraph graph, NodeSet nodes, List<Path> subpaths, double alpha, int kappa, int priorityOptionKey, String priorityOptionLabel) {
        this.graph = graph;
        this.nodes = nodes;
        this.subpaths = subpaths;
        this.alpha = alpha;
        this.kappa = kappa;
        this.priorityOptionKey = priorityOptionKey;
        this.priorityOptionLabel = priorityOptionLabel;
        update();
    }

    /**
     * Construct given a PangenomicGraph, NodeSet and Subpaths and already known support 
     */
    FrequentedRegion(PangenomicGraph graph, NodeSet nodes, List<Path> subpaths, double alpha, int kappa, int priorityOptionKey, String priorityOptionLabel, int support) {
        this.graph = graph;
        this.nodes = nodes;
        this.subpaths = subpaths;
        this.alpha = alpha;
        this.kappa = kappa;
        this.support = support;
        this.priorityOptionKey = priorityOptionKey;
        this.priorityOptionLabel = priorityOptionLabel;
        update();
    }

    /**
     * Construct given only basic information, used for post-processing. NO GRAPH.
     */
    FrequentedRegion(NodeSet nodes, List<Path> subpaths, double alpha, int kappa, int priorityOptionKey, String priorityOptionLabel, int support) {
        this.nodes = nodes;
        this.subpaths = subpaths;
        this.alpha = alpha;
        this.kappa = kappa;
        this.support = support;
        this.priorityOptionKey = priorityOptionKey;
        this.priorityOptionLabel = priorityOptionLabel;
        update();
    }

    /**
     * Construct given only the pieces in the FR.toString() output (and alpha, kappa).
     */
    FrequentedRegion(NodeSet nodes, double alpha, int kappa, int support, int caseSubpathSupport, int ctrlSubpathSupport, double orValue, double pValue, int priority) {
        this.nodes = nodes;
        this.alpha = alpha;
        this.kappa = kappa;
        this.support = support;
        this.caseSubpathSupport = caseSubpathSupport;
        this.ctrlSubpathSupport = ctrlSubpathSupport;
        this.orValue = orValue;
        this.pValue = pValue;
        this.priority = priority;
    }

    /**
     * Update the subpaths, priority, etc.
     */
    void update() {
        updateSupport();
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
     * Comparison is based on higher priority, then higher support, then smaller size, then the nodes themselves.
     */
    @Override
    public int compareTo(Object o) {
	FrequentedRegion that = (FrequentedRegion) o;
        if (this.priority!=that.priority) {
            return this.priority - that.priority;
        } else if (this.support!=that.support) {
            return this.support - that.support;
        } else if (this.nodes.size()!=that.nodes.size()) {
            return -(this.nodes.size() - that.nodes.size());
        } else {
            return this.nodes.compareTo(that.nodes);
        }
    }

    /**
     * Update the subpaths and support from the graph paths for the current alpha and kappa values.
     */
    void updateSupport() {
        subpaths = new ArrayList<>();
        for (Path p : graph.paths) {
            List<Path> supportPaths = computeSupport(p);
            subpaths.addAll(supportPaths);
        }
        support = subpaths.size();
        caseSubpathSupport = getSubpathSupport("case");
        ctrlSubpathSupport = getSubpathSupport("ctrl");
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
     * nodes size support case ctrl OR p pri
     */
    public String columnHeading() {
        String s = "nodes\tsize\tsupport";
        if (graph!=null && graph.labelCounts.size()>0) {
            for (String label : graph.labelCounts.keySet()) {
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
     * Update the integer priority metric used for ordering FRs.
     */
    void updatePriority() {
        priority = 0;
        if (priorityOptionKey==0) {
            priority = support;
        } else if (priorityOptionKey==1) {
            if (priorityOptionLabel.equals("case")) {
                // favor case support
                priority = caseSubpathSupport - ctrlSubpathSupport;
            } else if (priorityOptionLabel.equals("ctrl")) {
                // favor control support
                priority = ctrlSubpathSupport - caseSubpathSupport;
            } else {
                System.err.println("ERROR: priorityOptionLabel="+priorityOptionLabel+" is not supported by FrequentedRegion.updatePriority() with priorityOptionKey="+priorityOptionKey+".");
                System.exit(1);
            }
        } else if (priorityOptionKey==2) {
            // case and control are the same
            priority = Math.abs(caseSubpathSupport - ctrlSubpathSupport);
        } else if (priorityOptionKey==3) {
            double mlog10OR = 0.0;
            if (ctrlSubpathSupport==0) {
                // zero ctrl support, treat like OR=1000
                mlog10OR = 3.0;
            } else if (caseSubpathSupport==0) {
                // zero case support, treat as if OR=1/1000
                mlog10OR = 3.0;
            } else {
                mlog10OR = Math.log10(oddsRatio());
            }
            priority = (int)(support*mlog10OR*10);
        } else if (priorityOptionKey==4) {
            priority = -(int)(Math.log10(fisherExactP())*100);
        } else {
            // we've got an unallowed priority key
            System.err.println("ERROR: priorityOptionKey="+priorityOptionKey+" is not supported by FrequentedRegion.updatePriority().");
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
     * NOTE3: this is calculated only ONCE and stored in pValue.
     */
    public double fisherExactP() {
        if (pValue==Double.NEGATIVE_INFINITY) {
            int casePaths = graph.getPathCount("case");
            int ctrlPaths = graph.getPathCount("ctrl");
            int casePathSupport = getPathSupport("case");
            int ctrlPathSupport = getPathSupport("ctrl");
            int casePathNonsupport = casePaths - casePathSupport;
            int ctrlPathNonsupport = ctrlPaths - ctrlPathSupport;
            pValue = graph.fisherExact.getTwoTailedP(casePathSupport, casePathNonsupport, ctrlPathSupport, ctrlPathNonsupport);
        }
        return pValue;
    }

    /**
     * Return the odds ratio for cases vs controls in terms of supporting subpaths vs. graph paths.
     * 0 = zero case subpath support, POSITIVE_INFINITY = zero control subpath support.
     * NOTE: this is calculated ONCE and stored in orValue.
     */
    public double oddsRatio() {
        if (orValue==Double.NEGATIVE_INFINITY) {
            int casePaths = graph.getPathCount("case");
            int ctrlPaths = graph.getPathCount("ctrl");
            if (ctrlSubpathSupport>0) {
                orValue = (double)caseSubpathSupport * (double)ctrlPaths / ( (double)ctrlSubpathSupport * (double)casePaths );
            } else {
                orValue = Double.POSITIVE_INFINITY;
            }
        }
        return orValue;
    }

    /**
     * Return a string summary of this frequented region.
     */
    public String toString() {
        String s = nodes.toString()+"\t"+nodes.size()+"\t"+support;
        if (support>0) {
            // show label support if available
            if (graph!=null && graph.labelCounts.size()>0) {
                // count the support per label
                Map<String,Integer> labelCounts = new TreeMap<>();
                for (Path subpath : subpaths) {
                    if (subpath.label!=null) {
                        if (!labelCounts.containsKey(subpath.label)) {
                            labelCounts.put(subpath.label, getSubpathSupport(subpath.label));
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
        StringBuilder sb = new StringBuilder();
        for (Path sp : subpaths) {
            sb.append(sp.toString());
	    sb.append("\n");
        }
	return sb.toString();
    }

    /**
     * Return true if this FR contains a subpath which belongs to the given Path.
     */
    public boolean containsSubpathOf(Path path) {
        if (subpaths!=null) {
            for (Path sp : subpaths) {
                if (sp.equals(path)) return true;
            }
        }
        return false;
    }

    /**
     * Return a count of subpaths of FR that belong to the given Path.
     */
    public int countSubpathsOf(Path path) {
        int count = 0;
        if (subpaths!=null) {
            for (Path sp : subpaths) {
                if (sp.name.equals(path.name)) count++;
            }
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
        //
        Option excludedNodesOption = new Option("en", "excludednodes", true, "disallow paths that include any of the given nodes []");
        excludedNodesOption.setRequired(false);
        options.addOption(excludedNodesOption);
        
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

        // alpha, kappa
        double alpha = Double.parseDouble(cmd.getOptionValue("alpha"));
        int kappa = Integer.parseInt(cmd.getOptionValue("kappa"));
        if (kappa==-1) kappa = Integer.MAX_VALUE; // effectively infinity

        // parse the priorityOption and impose defaults
        String[] parts = cmd.getOptionValue("priorityoption").split(":");
        int priorityOptionKey = Integer.parseInt(parts[0]);
        String priorityOptionLabel = null;
        if (parts.length>1) {
            String priorityOptionParameter = parts[1];
            if (priorityOptionParameter.equals("case") || priorityOptionParameter.equals("ctrl")) {
                priorityOptionLabel = priorityOptionParameter;
            }
        }
        // impose defaults
        if (priorityOptionKey==1 && priorityOptionLabel==null) priorityOptionLabel = "case";
        if (priorityOptionKey==3 && priorityOptionLabel==null) priorityOptionLabel = "case";

	// excluded nodes
	String excludedNodeString = "[]";
	if (cmd.hasOption("excludednodes")) {
	    excludedNodeString = cmd.getOptionValue("excludednodes");
	}
        
        // import the PangenomicGraph from a pair of TXT files
	String graphName = cmd.getOptionValue("graph");
        PangenomicGraph pg = new PangenomicGraph();
        pg.nodesFile = new File(graphName+".nodes.txt");
        pg.pathsFile = new File(graphName+".paths.txt");
        pg.loadTXT();
	// remove paths that contain an excluded node, if any
	NodeSet excludedNodes = pg.getNodeSet(excludedNodeString);
	if (excludedNodes.size()>0) {
	    List<Path> pathsToRemove = new ArrayList<>();
	    for (Path path : pg.paths) {
		for (Node node : excludedNodes) {
		    if (path.getNodes().contains(node)) {
			pathsToRemove.add(path);
			continue;
		    }
		}
	    }
	    pg.paths.removeAll(pathsToRemove);
	} 
        pg.tallyLabelCounts();
	System.out.println("# Graph has "+pg.vertexSet().size()+" nodes and "+pg.edgeSet().size()+" edges with "+pg.paths.size()+" paths.");
        System.out.println("# Graph has "+pg.labelCounts.get("case")+" case paths and "+pg.labelCounts.get("ctrl")+" ctrl paths.");

        // create the FrequentedRegion with this PangenomicGraph
        NodeSet nodes = pg.getNodeSet(cmd.getOptionValue("nodes"));
        FrequentedRegion fr = new FrequentedRegion(pg, nodes, alpha, kappa, priorityOptionKey, priorityOptionLabel);

        // print it out
        System.out.println(fr.columnHeading());
        System.out.println(fr.toString());
    }

    /**
     * Algorithm 1 from Cleary, et al. generates the supporting path segments of this path for the given NodeSet and alpha and kappa parameters.
     *
     * @param nodes the NodeSet, or cluster C as it's called in Algorithm 1
     * @param alpha the penetrance parameter = minimum fraction of nodes in C that are in subpath
     * @param kappa the insertion parameter = maximum inserted number of nodes
     * @return the set of supporting path segments
     */
    public List<Path> computeSupport(Path p) {
        // s = the supporting subpaths
        List<Path> s = new ArrayList<>();
        // m = the list of the path's nodes that are in C=nodes
        List<Node> m = new ArrayList<>();
        for (Node n : p.getNodes()) {
            if (nodes.contains(n)) m.add(n);
        }
        // find maximal subpaths
        for (int i=0; i<m.size(); i++) {
            Node nl = m.get(i);
            Node nr = null;
            int num = 0;
            for (int j=i; j<m.size(); j++) {
                // kappa test
                Path subpath = p.subpath(nl, m.get(j));
                int maxInsertion = 0; // max insertion
                int insertion = 0; // continguous insertion
                for (Node n : subpath.getNodes()) {
                    if (nodes.contains(n)) {
                        // reset and save previous insertion if large
                        if (insertion>maxInsertion) maxInsertion = insertion;
                        insertion = 0;
                    } else {
                        insertion += 1;
                    }
                }
                if (maxInsertion>kappa) break;
                // we're good, set nr from this cycle
                nr = m.get(j);
                num = j - i + 1; // number of this path's nodes in nodes collection
            }
            // is this a subpath of an already counted subpath? (maximality test)
            Path subpath = p.subpath(nl,nr);
            boolean ignore = false;
            for (Path checkpath : s) {
                if (checkpath.contains(subpath)) {
                    ignore = true;
                    break;
                }
            }
            // sanity check
            if (subpath.getNodes().size()==0) {
                System.err.println("ERROR: subpath.getNodes().size()=0; path="+this.toString()+" nl="+nl+" nr="+nr);
                ignore = true;
            }
            // alpha test on maximal subpath
            if (!ignore && num>=alpha*nodes.size()) s.add(subpath);
        }
        return s;
    }
}
