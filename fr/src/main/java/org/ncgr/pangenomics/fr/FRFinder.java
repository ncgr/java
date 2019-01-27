package org.ncgr.pangenomics.fr;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.Collections;
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

/**
 * Finds frequented regions in a Graph.
 *
 * See Cleary, et al., "Exploring Frequented Regions in Pan-Genomic Graphs", IEEE/ACM Trans Comput Biol Bioinform. 2018 Aug 9. PMID:30106690 DOI:10.1109/TCBB.2018.2864564
 *
 * @author Sam Hokin
 */
public class FRFinder {

    // optional parameter defaults
    static int MINSUP = 1;
    static int MAXSUP = Integer.MAX_VALUE;
    static int MINSIZE = 1;
    static int MINLEN = 1;
    static int NROUNDS = 2;
    static boolean USERC = false;
    static boolean VERBOSE = false;
    static boolean REMOVE_CHILDREN = false;
    static boolean PARALLEL = false;
    
    // required parameters, no defaults; set in constructor
    Graph graph;  // the Graph we're analyzing
    double alpha; // penetrance: the fraction of a supporting strain's sequence that actually supports the FR; alternatively, `1-alpha` is the fraction of inserted sequence
    int kappa;    // maximum insertion: the maximum insertion length (measured in bp) that any supporting path may have
 
    // optional parameters, set with setters
    boolean verbose = VERBOSE;
    boolean useRC = USERC; // indicates if the sequence (e.g. FASTA file) had its reverse complement appended
    boolean removeChildren = REMOVE_CHILDREN; // option to remove child FRs with lower support from the FR heirarchy
    boolean parallel = PARALLEL; // option to run FR building loop in parallel
    int minSup = MINSUP;   // minimum support: minimum number of genome paths (fr.support) for an FR to be considered interesting
    int maxSup = MAXSUP;   // maximum support: maximum number of genome paths (fr.support) for an FR to be considered interesting
    int minSize = MINSIZE; // minimum size: minimum number of de Bruijn nodes (fr.nodes.size()) that an FR must contain to be considered interesting
    int minLen = MINLEN;   // minimum average length of a frequented region's subpath sequences (fr.avgLength) to be considered interesting
    int nRounds = NROUNDS; // number of FR-building rounds to run

    // the FRs, sorted for convenience
    TreeSet<FrequentedRegion> frequentedRegions;

    /**
     * Construct with a populated Graph and required parameters
     */
    public FRFinder(Graph graph, double alpha, int kappa) {
        this.graph = graph;
        this.alpha = alpha;
        this.kappa = kappa;
    }

    /**
     * Find the frequented regions in this Graph.
     */
    public void findFRs() {

        if (verbose) {
            graph.printNodes();
            graph.printPaths();
            graph.printNodePaths();
        }

        // store the FRs in a TreeSet, backed with a synchronizedSet for parallel processing
        frequentedRegions = new TreeSet<>();
        
        // store the analyzed NodeSets in a TreeSet, backed with a synchronizedSet for parallel processing
        TreeSet<NodeSet> nodeSets = new TreeSet<>();
        Set<NodeSet> syncNodeSets = Collections.synchronizedSet(nodeSets);

        // create initial NodeSets each containing only one node; add associated FRs if they pass filter
        for (Node node : graph.nodes) {
            NodeSet nodeSet = new NodeSet();
            nodeSet.add(node);
            syncNodeSets.add(nodeSet);
            FrequentedRegion fr = new FrequentedRegion(graph, nodeSet, alpha, kappa);
            if (passesFilters(fr)) frequentedRegions.add(fr);
        }

        // build the FRs round by round
        int round = 0;
        while (round<nRounds) {
            round++;
            printHeading("ROUND "+round);

            // use a frozen copy of the current NodeSets for iterating
            final Set<NodeSet> currentNodeSets = new TreeSet<>();
            currentNodeSets.addAll(syncNodeSets);

            if (parallel) {
                // run non-parallel outer loop
                for (NodeSet ns1 : currentNodeSets) {
                    int done = 0;
                    int alreadyDone = 0;
                    long start = System.currentTimeMillis();
                    // run parallel inner loop over the current NodeSets
                    currentNodeSets.parallelStream().forEach((ns2) -> {
                            ////////
                            NodeSet merged = NodeSet.merge(ns1, ns2);
                            if (!syncNodeSets.contains(merged)) {
                                syncNodeSets.add(merged);
                                FrequentedRegion fr = new FrequentedRegion(graph, merged, alpha, kappa);
                                if (passesFilters(fr)) frequentedRegions.add(fr);
                            }
                            ////////
                        });
                    System.out.println(ns1.toString()+": elapsed="+(System.currentTimeMillis()-start));
                }
            } else {
                // run non-parallel loops over the current NodeSets, for debuggery
                for (NodeSet ns1 : currentNodeSets) {
                    int done = 0;
                    int alreadyDone = 0;
                    long start = System.currentTimeMillis();
                    for (NodeSet ns2 : currentNodeSets) {
                        NodeSet merged = NodeSet.merge(ns1, ns2);
                        if (syncNodeSets.contains(merged)) {
                            alreadyDone++;
                        } else {
                            done++;
                            syncNodeSets.add(merged);
                            FrequentedRegion fr = new FrequentedRegion(graph, merged, alpha, kappa);
                            if (passesFilters(fr)) frequentedRegions.add(fr);
                        }
                    }
                    System.out.println(ns1.toString()+":"+done+"|"+alreadyDone+" elapsed:"+(System.currentTimeMillis()-start));
                }
            }

            // print a summary of this round
            if (frequentedRegions.size()>0) {
                FrequentedRegion highestSupportFR = frequentedRegions.first();
                FrequentedRegion highestAvgLengthFR = frequentedRegions.first();
                FrequentedRegion highestTotalLengthFR = frequentedRegions.first();
                for (FrequentedRegion fr : frequentedRegions) {
                    if (fr.support>highestSupportFR.support) highestSupportFR = fr;
                    if (fr.avgLength>highestAvgLengthFR.avgLength) highestAvgLengthFR = fr;
                    if (fr.support*fr.avgLength>highestTotalLengthFR.support*highestTotalLengthFR.avgLength) highestTotalLengthFR = fr;
                }
                System.out.println("Highest support:");
                System.out.println(highestSupportFR.columnHeading());
                System.out.println(highestSupportFR.toString());
                System.out.println("Highest avg length:");
                System.out.println(highestAvgLengthFR.columnHeading());
                System.out.println(highestAvgLengthFR.toString());
                System.out.println("Highest total length:");
                System.out.println(highestTotalLengthFR.columnHeading());
                System.out.println(highestTotalLengthFR.toString());
            }

            // print the histogram of FR sizes from this round
            Map<Integer,Integer> countMap = new TreeMap<>();
            int maxSize = 0;
            for (FrequentedRegion fr : frequentedRegions) {
                if (fr.nodes.size()>maxSize) maxSize = fr.nodes.size();
                if (countMap.containsKey(fr.nodes.size())) {
                    int count = countMap.get(fr.nodes.size());
                    count++;
                    countMap.put(fr.nodes.size(), count);
                } else {
                    countMap.put(fr.nodes.size(), 1);
                }
            }
            for (int num : countMap.keySet()) {
                System.out.println("FR node size (#):"+num+" ("+countMap.get(num)+")");
            }
        }
        
        // remove child FRs that have lower support than their parent
        // NOTE: this presumes that the FR comparator orders by parent-->child!
        if (removeChildren) {
            List<FrequentedRegion> uninterestingFRs = new LinkedList<>();
            FrequentedRegion parentFR = frequentedRegions.first();
            for (FrequentedRegion thisFR : frequentedRegions) {
                if (parentFR.nodes.parentOf(thisFR.nodes)) {
                    if (parentFR.support>=thisFR.support) {
                        uninterestingFRs.add(thisFR);
                    }
                } else {
                    parentFR = thisFR;
                }
            }
            frequentedRegions.removeAll(uninterestingFRs);
            printHeading("REMOVED "+uninterestingFRs.size()+" CHILD FRs");
        }

	// verbosity
	if (verbose) {
	    printPathFRs();
	}

	// the end result
        // DEBUG
        // printFrequentedRegions();
    }

    /**
     * Return true if the given FR passes support and size filters. Other filters could be added here.
     */
    boolean passesFilters(FrequentedRegion fr) {
        return fr.nodes.size()>=minSize && fr.support>=minSup && fr.support<=maxSup && fr.avgLength>=minLen;
    }

    public double getAlpha() {
        return alpha;
    }
    public int getKappa() {
        return kappa;
    }
    public boolean getUseRC() {
        return useRC;
    }
    public int getMinSup() {
        return minSup;
    }
    public int getMaxSup() {
        return maxSup;
    }
    public int getMinSize() {
        return minSize;
    }
    public int getMinLen() {
        return minLen;
    }

    // setters for optional parameters
    public void setVerbose() {
        this.verbose = true;
    }
    public void setMinSup(int minSup) {
        this.minSup = minSup;
    }
    public void setMaxSup(int maxSup) {
        this.maxSup = maxSup;
    }
    public void setMinSize(int minSize) {
        this.minSize = minSize;
    }
    public void setMinLen(int minLen) {
        this.minLen = minLen;
    }
    public void setNRounds(int nRounds) {
        this.nRounds = nRounds;
    }
    public void setUseRC() {
        this.useRC = true;
    }
    public void setRemoveChildren() {
        this.removeChildren = true;
    }
    public void setParallel() {
        this.parallel = true;
    }

    /**
     * Command-line utility
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {

        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        // FRFinder options
        Option dotOption = new Option("d", "dot", true, "splitMEM DOT file (requires FASTA file)");
        dotOption.setRequired(false);
        options.addOption(dotOption);
        //
        Option fastaOption = new Option("f", "fasta", true, "FASTA file (requires DOT file)");
        fastaOption.setRequired(false);
        options.addOption(fastaOption);
        //
        Option jsonOption = new Option("j", "json", true, "vg JSON file");
        jsonOption.setRequired(false);
        options.addOption(jsonOption);
        //
        Option labelsOption = new Option("p", "pathlabels", true, "tab-delimited file with pathname<tab>label");
        labelsOption.setRequired(false);
        options.addOption(labelsOption);
        //
        Option alphaOption = new Option("a", "alpha", true, "alpha=penetrance, fraction of a supporting path's sequence that supports the FR (required)");
        alphaOption.setRequired(true);
        options.addOption(alphaOption);
        //
        Option kappaOption = new Option("k", "kappa", true, "kappa=maximum insertion length that any supporting path may have (required)");
        kappaOption.setRequired(true);
        options.addOption(kappaOption);
        //
        Option minSupOption = new Option("m", "minsup", true, "minsup=minimum number of supporting paths for a region to be considered interesting ("+MINSUP+")");
        minSupOption.setRequired(false);
        options.addOption(minSupOption);
        //
        Option maxSupOption = new Option("n", "maxsup", true, "maxsup=maximum number of supporting paths for a region to be considered interesting ("+MAXSUP+")");
        maxSupOption.setRequired(false);
        options.addOption(maxSupOption);
        //
        Option minSizeOption = new Option("s", "minsize", true, "minsize=minimum number of nodes that a FR must contain to be considered interesting ("+MINSIZE+")");
        minSizeOption.setRequired(false);
        options.addOption(minSizeOption);
        //
        Option minLenOption = new Option("l", "minlen", true, "minlen=minimum allowed average length (bp) of an FR's subpaths ("+MINLEN+")");
        minLenOption.setRequired(false);
        options.addOption(minLenOption);
        //
        Option rcOption = new Option("r", "userc", false, "useRC=flag to indicate if the sequence (e.g. FASTA) had its reverse complement appended ("+USERC+")");
        rcOption.setRequired(false);
        options.addOption(rcOption);
        //
        Option verboseOption = new Option("v", "verbose", false, "verbose output ("+VERBOSE+")");
        verboseOption.setRequired(false);
        options.addOption(verboseOption);
        //
        Option genotypeOption = new Option("g", "genotype", true, "which genotype to include (0,1) from the input file; -1 to include all ("+Graph.GENOTYPE+")");
        genotypeOption.setRequired(false);
        options.addOption(genotypeOption);
        //
        Option graphOnlyOption = new Option("go", "graphonly", false, "just read the graph and output, do not find FRs; for debuggery (false)");
        graphOnlyOption.setRequired(false);
        options.addOption(graphOnlyOption);
        //
        Option removeChildrenOption = new Option("rc", "removechildren", false, "remove child FRs that have equal or lower support ("+REMOVE_CHILDREN+")");
        removeChildrenOption.setRequired(false);
        options.addOption(removeChildrenOption);
        //
        Option parallelOption = new Option("pa", "parallel", false, "run FR-building loop in parallel ("+PARALLEL+")");
        parallelOption.setRequired(false);
        options.addOption(parallelOption);
        //
        Option nRoundsOption = new Option("nr", "nrounds", true, "number of FR-building rounds to run ("+NROUNDS+")");
        nRoundsOption.setRequired(false);
        options.addOption(nRoundsOption);

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp("FRFinder", options);
            System.exit(1);
            return;
        }

        // parameter validation
        if (!cmd.hasOption("dot") && !cmd.hasOption("json")) {
            System.err.println("You must specify either a splitMEM dot file plus FASTA (-d/--dot and -f/--fasta ) or a vg JSON file (-j, --json)");
            System.exit(1);
            return;
        }
        if (cmd.hasOption("dot") && !cmd.hasOption("fasta")) {
            System.err.println("If you specify a splitMEM dot file (-d/--dot) you MUST ALSO specify a FASTA file (-f/--fasta)");
            System.exit(1);
            return;
        }
        
        // files
        String dotFile = cmd.getOptionValue("dot");
        String fastaFile = cmd.getOptionValue("fasta");
        String jsonFile = cmd.getOptionValue("json");
        String pathLabelsFile = cmd.getOptionValue("pathlabels");

        // required parameters
        double alpha = Double.parseDouble(cmd.getOptionValue("alpha"));
        int kappa = Integer.parseInt(cmd.getOptionValue("kappa"));

        // create a Graph from the dot+FASTA or JSON file
        Graph g = new Graph();
        if (cmd.hasOption("verbose") && !cmd.hasOption("graphonly")) g.setVerbose();
        if (cmd.hasOption("genotype")) g.setGenotype(Integer.parseInt(cmd.getOptionValue("genotype")));
        if (dotFile!=null && fastaFile!=null) {
            System.out.println("DOT+FASTA input is not yet enabled.");
            System.exit(0);
            // g.readSplitMEMDotFile(dotFile, fastaFile);
        } else if (jsonFile!=null) {
            g.readVgJsonFile(jsonFile);
        } else {
            System.err.println("ERROR: no DOT+FASTA or JSON provided.");
            System.exit(1);
        }

        // if a labels file is given, append the labels to the path names
        if (pathLabelsFile!=null) {
            g.readPathLabels(pathLabelsFile);
        }

        // bail if we're just looking at the graph
        if (cmd.hasOption("graphonly")) {
            if (pathLabelsFile!=null) g.printLabelCounts();
            g.printNodes();
            g.printPaths();
            g.printNodePaths();
            g.printPathSequences();
            return;
        }
        
        // instantiate the FRFinder with this Graph and required parameters
        FRFinder frf = new FRFinder(g, alpha, kappa);
        
        // set optional FRFinder parameters
        if (cmd.hasOption("verbose")) frf.setVerbose();
        if (cmd.hasOption("userc")) frf.setUseRC();
        if (cmd.hasOption("removechildren")) frf.setRemoveChildren();
        if (cmd.hasOption("parallel")) frf.setParallel();
        if (cmd.hasOption("minsup")) {
            frf.setMinSup(Integer.parseInt(cmd.getOptionValue("minsup")));
        }
        if (cmd.hasOption("maxsup")) {
            frf.setMaxSup(Integer.parseInt(cmd.getOptionValue("maxsup")));
        }
        if (cmd.hasOption("minsize")) {
            frf.setMinSize(Integer.parseInt(cmd.getOptionValue("minsize")));
        }
        if (cmd.hasOption("minlen")) {
            frf.setMinLen(Integer.parseInt(cmd.getOptionValue("minlen")));
        }
        if (cmd.hasOption("nrounds")) {
            frf.setNRounds(Integer.parseInt(cmd.getOptionValue("nrounds")));
        }
        
        //////////////////
        // Find the FRs //
        //////////////////
        frf.findFRs();
    }

    /**
     * Print a delineating heading, for general use.
     */
    static void printHeading(String heading) {
        for (int i=0; i<heading.length(); i++) System.out.print("="); System.out.println("");
        System.out.println(heading);
        for (int i=0; i<heading.length(); i++) System.out.print("="); System.out.println("");
    }

    /**
     * Print out the FRs.
     */
    void printFrequentedRegions() {
        printHeading("FREQUENTED REGIONS");
        System.out.println(frequentedRegions.first().columnHeading());
        for (FrequentedRegion fr : frequentedRegions) {
            System.out.println(fr.toString());
        }
    }

    /**
     * Print the path names and the FRs that have subpaths belonging to those paths.
     * This can be used as input to a classification routine.
     */
    void printPathFRs() {
        printHeading("PATH FREQUENTED REGIONS");
        // columns
        System.out.print("Path\tLabel");
        int c = 1;
        for (FrequentedRegion fr : frequentedRegions) {
            System.out.print("\t"+c);
            c++;
        }
        System.out.println("");
        // rows
        for (Path path : graph.paths) {
            System.out.print(path.name+"\t"+path.label);
            for (FrequentedRegion fr : frequentedRegions) {
                System.out.print("\t"+fr.countSubpathsOf(path));
            }
            System.out.println("");
        }
    }
}
