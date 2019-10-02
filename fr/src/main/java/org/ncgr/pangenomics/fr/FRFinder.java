package org.ncgr.pangenomics.fr;

import org.ncgr.jgraph.Node;
import org.ncgr.jgraph.NodeSet;
import org.ncgr.jgraph.PangenomicGraph;
import org.ncgr.jgraph.PathWalk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import java.util.Collections;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;

import java.util.concurrent.PriorityBlockingQueue;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Finds frequented regions in a pan-genomic graph.
 *
 * See Cleary, et al., "Exploring Frequented Regions in Pan-Genomic Graphs",
 * IEEE/ACM Trans Comput Biol Bioinform. 2018 Aug 9.
 * PMID:30106690 DOI:10.1109/TCBB.2018.2864564
 *
 * @author Sam Hokin
 */
public class FRFinder {

    // a PangenomicGraph must be supplied to the constructor
    PangenomicGraph graph;  // the Graph we're analyzing

    // parameters are stored in a Properties object
    Properties parameters = new Properties();

    // double alpha; // penetrance: the fraction of a supporting strain's sequence that actually supports the FR;
    //               // alternatively, `1-alpha` is the fraction of inserted sequence
    // int kappa;    // maximum insertion: the maximum insertion length (measured in bp) that any supporting path may have;
    //               // or, if kappaByNodes, the maximum number of inserted nodes that a supporting path may have.
 
    // save files
    String SYNC_FREQUENTED_REGIONS_SAVE = "syncFrequentedRegions.save.txt";
    String USED_FRS_SAVE = "usedFRs.save.txt";
    String FREQUENTED_REGIONS_SAVE = "frequentedRegions.save.txt";

    // I/O
    String outputPrefix; // output file prefix

    // the FRs, sorted for convenience
    TreeSet<FrequentedRegion> frequentedRegions;

    // diagnostic
    long clockTime;

    /**
     * Construct with a populated Graph
     */
    public FRFinder(PangenomicGraph graph) {
        initializeParameters();
        this.graph = graph;
    }

    /**
     * Construct with the output from a previous run. Be sure to set minSup, minSize, minLen filters as needed before running postprocess().
     */
    public FRFinder(String inputPrefix) throws Exception {
        readParameters(inputPrefix); // sets properties from file
        readFrequentedRegions(getAlpha(), getKappa());
    }

    /**
     * Initialize the default parameters.
     */
    void initializeParameters() {
        parameters.setProperty("verbose", "false");
        parameters.setProperty("debug", "false");
        parameters.setProperty("minSup", "1");
        parameters.setProperty("minSize", "1");
        parameters.setProperty("minLen", "1.0");
        parameters.setProperty("maxRound", "0");
        parameters.setProperty("caseCtrl", "false");
        parameters.setProperty("bruteForce", "false");
        parameters.setProperty("serial", "false");
        parameters.setProperty("resume", "false");
        parameters.setProperty("kappaByNodes", "false");
        parameters.setProperty("prunedGraph", "false");
    }

    /**
     * Find the frequented regions in this Graph for the given alpha and kappa values.
     */
    public void findFRs(double alpha, double kappa) throws Exception {
        if (getPrunedGraph()) {
	    int nRemoved = graph.prune();
	    System.out.println("# graph has been pruned ("+nRemoved+" fully common nodes removed).");
        }

	System.out.println("# graph has "+graph.vertexSet().size()+" nodes and "+graph.getPaths().size()+" paths.");
        if (graph.getLabelCounts().get("case")!=null && graph.getLabelCounts().get("ctrl")!=null) {
            System.out.println("# graph has "+graph.getLabelCounts().get("case")+" case paths and "+
                               graph.getLabelCounts().get("ctrl")+" ctrl paths.");
        }
        if (getVerbose()) {
            graph.printNodes(System.out);
            graph.printPaths(System.out);
            graph.printNodePaths(System.out);
        }

        // store the saved FRs in a TreeSet
        frequentedRegions = new TreeSet<>();

        // keep track of FRs we've already looked at
        Set<FrequentedRegion> usedFRs = new HashSet<>();
        
        // store the studied FRs in a synchronized TreeSet
        Set<FrequentedRegion> syncFrequentedRegions = Collections.synchronizedSet(new TreeSet<>());

        // rejected NodeSets, so we don't bother scanning them more than once
        Set<String> rejectedNodeSets = Collections.synchronizedSet(new HashSet<>());

        // accepted FRPairs so we don't merge them more than once
        Map<String,FRPair> acceptedFRPairs = Collections.synchronizedMap(new HashMap<>());

        // used NodeSets for brute force
        Set<String> usedNodeSets = Collections.synchronizedSet(new HashSet<>());

        // FR-finding round counter
        int round = 0;

        if (getResume()) {
            // resume from a previous run
            System.out.println("# resuming from previous run");
            String line = null;
            // frequentedRegions
            System.out.print("#   frequentedRegions...");
            BufferedReader frReader = new BufferedReader(new FileReader(FREQUENTED_REGIONS_SAVE));
            while ((line=frReader.readLine())!=null) {
                String[] parts = line.split("\t");
                NodeSet nodes = new NodeSet(graph, parts[0]);
                frequentedRegions.add(new FrequentedRegion(graph, nodes, alpha, kappa, kappaByNodes));
                round++;
            }
            System.out.println(frequentedRegions.size());
            // syncFrequentedRegions
            System.out.print("#   syncFrequentedRegions...");
            BufferedReader sfrReader = new BufferedReader(new FileReader(SYNC_FREQUENTED_REGIONS_SAVE));
            while ((line=sfrReader.readLine())!=null) {
                String[] parts = line.split("\t");
                NodeSet nodes = new NodeSet(graph, parts[0]);
                syncFrequentedRegions.add(new FrequentedRegion(graph, nodes, alpha, kappa, kappaByNodes));
            }
            System.out.println(syncFrequentedRegions.size());
            // usedFRs
            System.out.print("#   usedFRs...");
            BufferedReader usedFRsReader = new BufferedReader(new FileReader(USED_FRS_SAVE));
            while ((line=usedFRsReader.readLine())!=null) {
                String[] parts = line.split("\t");
                NodeSet nodes = new NodeSet(graph, parts[0]);
                usedFRs.add(new FrequentedRegion(graph, nodes, alpha, kappa, kappaByNodes));
            }
            System.out.println(usedFRs.size());
            System.out.println("# now continuing...");
        } else {
            // initialize syncFrequentedRegions with single-node FRs that have alpha/kappa support
            for (Node node : graph.getNodes()) {
                NodeSet c = new NodeSet();
                c.add(node);
                Set<PathWalk> s = new HashSet<>();
                for (PathWalk p : graph.getPaths()) {
                    Set<PathWalk> support = p.computeSupport(c, alpha, kappa, kappaByNodes);
                    s.addAll(support);
                }
                if (s.size()>0) {
                    syncFrequentedRegions.add(new FrequentedRegion(graph, c, s, alpha, kappa, kappaByNodes));
                }
            }
        }

        // build the FRs round by round
	long startTime = System.currentTimeMillis();
        boolean added = true;
        while (added && (round<maxRound || maxRound==0)) {
            round++;
            added = false;
            if (bruteForce) {
                // no heurism, for demo purposes
                Set<FrequentedRegion> loopFRs = Collections.synchronizedSet(new HashSet<>());
                int oldSyncSize = syncFrequentedRegions.size();
                int oldFRSize = frequentedRegions.size();
                ////////////////////////////////////////
                // spin through FRs in a parallel manner
                syncFrequentedRegions.parallelStream().forEach((fr1) -> {
                        syncFrequentedRegions.parallelStream().forEach((fr2) -> {
                                if (fr1.compareTo(fr2)>0) {
                                    FRPair frpair = new FRPair(fr1, fr2, graph, alpha, kappa, kappaByNodes, caseCtrl);
                                    String nodesKey = frpair.nodes.toString();
                                    if (!usedNodeSets.contains(nodesKey)) {
                                        usedNodeSets.add(nodesKey);
					try {
					    frpair.merge();
					} catch (Exception e) {
					    System.err.println(e.toString());
					}
                                        loopFRs.add(frpair.merged);
                                        if (frpair.merged.support>=getMinSup() && frpair.merged.avgLength>=minLen && frpair.merged.nodes.size()>=minSize) frequentedRegions.add(frpair.merged);
                                    }
                                }
                            });
                    });
                ////////////////////////////////////////
                syncFrequentedRegions.addAll(loopFRs);
                added = syncFrequentedRegions.size()>oldSyncSize;
                System.out.println(round+":"+(syncFrequentedRegions.size()-oldSyncSize)+" sync FRs added; "+(frequentedRegions.size()-oldFRSize)+" supported FRs added.");
            } else if (serial) {
                // serial processing with extra output for demo purposes or other experiments
                // put FR pairs into a PriorityQueue which sorts them by decreasing interest (defined by the FRPair comparator)
                PriorityQueue<FRPair> pq = new PriorityQueue<>();
                // spin through FRs in a serial manner
                for (FrequentedRegion fr1 : syncFrequentedRegions) {
                    for (FrequentedRegion fr2 : syncFrequentedRegions) {
                        if (fr2.compareTo(fr1)>=0 && !usedFRs.contains(fr1) && !usedFRs.contains(fr2)) {
                            // no merge or rejection test here
                            FRPair frpair = new FRPair(fr1, fr2, graph, alpha, kappa, kappaByNodes, caseCtrl);
                            String nodesKey = frpair.nodes.toString();
                            if (rejectedNodeSets.contains(nodesKey)) {
                                // do nothing
                            } else if (acceptedFRPairs.containsKey(nodesKey)) {
                                // use stored FRPair
                                frpair = acceptedFRPairs.get(nodesKey);
                                if (!frequentedRegions.contains(frpair.merged)) {
                                    pq.add(frpair);
                                }
                            } else {
                                // see if this pair is rejected
                                frpair.computeRejection();
                                if (frpair.alphaReject) {
                                    // add to rejected set
                                    rejectedNodeSets.add(nodesKey);
                                } else {
                                    // merge and add to accepted set
                                    frpair.merge();
                                    acceptedFRPairs.put(nodesKey, frpair);
                                    pq.add(frpair);
                                }
                            }
                        }
                    }
                }
                // add our new FR
                if (pq.size()>0) {
                    FRPair frpair = pq.peek();
                    if (caseCtrl) {
                        added = frpair.merged.caseControlDifference()>0;
                    } else {
                        added = frpair.merged.support>0;
                    }
                    if (added) {
                        usedFRs.add(frpair.fr1);
                        usedFRs.add(frpair.fr2);
                        syncFrequentedRegions.add(frpair.merged);
                        frequentedRegions.add(frpair.merged);
                        System.out.println(round+":"+frpair.merged.toString());
                    }
                }
            } else {
                // default: parallel processing
                // put FR pairs into a PriorityBlockingQueue which sorts them by decreasing interest (defined by the FRPair comparator)
                PriorityBlockingQueue<FRPair> pq = new PriorityBlockingQueue<>();
                ////////////////////////////////////////
                // spin through FRs in a parallel manner
                syncFrequentedRegions.parallelStream().forEach((fr1) -> {
                        syncFrequentedRegions.parallelStream().forEach((fr2) -> {
                                if (fr2.compareTo(fr1)>=0 && !usedFRs.contains(fr1) && !usedFRs.contains(fr2)) {
                                    // no merge or rejection test here
                                    FRPair frpair = new FRPair(fr1, fr2, graph, alpha, kappa, kappaByNodes, caseCtrl);
                                    String nodesKey = frpair.nodes.toString();
                                    if (rejectedNodeSets.contains(nodesKey)) {
                                        // do nothing
                                    } else if (acceptedFRPairs.containsKey(nodesKey)) {
                                        // use stored FRPair
                                        frpair = acceptedFRPairs.get(nodesKey);
                                        if (!frequentedRegions.contains(frpair.merged)) {
                                            pq.add(frpair);
                                        }
                                    } else {
                                        // see if this pair is rejected
                                        frpair.computeRejection();
                                        if (frpair.alphaReject) {
                                            // add to rejected set
                                            rejectedNodeSets.add(nodesKey);
                                        } else {
                                            // merge and add to accepted set
					    try {
						frpair.merge();
					    } catch (Exception e) {
						System.err.println(e.toString());
					    }
                                            acceptedFRPairs.put(nodesKey, frpair);
                                            pq.add(frpair);
                                        }
                                    }
                                    
                                }
                            });
                    });
                ////////////////////////////////////////
                // add our new FR
                if (pq.size()>0) {
                    FRPair frpair = pq.peek();
                    if (caseCtrl) {
                        added = frpair.merged.caseControlDifference()>0;
                    } else {
                        added = frpair.merged.support>0;
                    }
                    if (added) {
                        usedFRs.add(frpair.fr1);
                        usedFRs.add(frpair.fr2);
                        syncFrequentedRegions.add(frpair.merged);
                        frequentedRegions.add(frpair.merged);
                        System.out.println(round+":"+frpair.merged.toString());
                    }
                }
            }

            // output current state for continuation if aborted
            // [8,72]	219	1.00	136	83
            // syncFrequentedRegions
            PrintStream sfrOut = new PrintStream(SYNC_FREQUENTED_REGIONS_SAVE);
            for (FrequentedRegion fr : syncFrequentedRegions) {
                sfrOut.println(fr.toString());
            }
            sfrOut.close();
            // usedFRs
            PrintStream usedFRsOut = new PrintStream(USED_FRS_SAVE);
            for (FrequentedRegion fr : usedFRs) {
                usedFRsOut.println(fr.toString());
            }
            usedFRsOut.close();
            // frequentedRegions
            PrintStream frOut = new PrintStream(FREQUENTED_REGIONS_SAVE);
            for (FrequentedRegion fr : frequentedRegions) {
                frOut.println(fr.toString());
            }
            frOut.close();
        }

	clockTime = System.currentTimeMillis() - startTime;
        System.out.println("Found "+frequentedRegions.size()+" FRs.");
	System.out.println("Clock time: "+formatTime(clockTime));
        
	// final output
	if (frequentedRegions.size()>0) {
            printParameters();
            printFrequentedRegions();
            printFRSubpaths();
            printPathFRs();
	    if (outputPrefix!=null) graph.printAll(outputPrefix);
	}
    }

    /**
     * Post-process a set of FRs for given minSup, minLen and minSize.
     */
    public void postprocess() throws Exception {
        TreeSet<FrequentedRegion> filteredFRs = new TreeSet<>();
        for (FrequentedRegion fr : frequentedRegions) {
            boolean passes = true;
            String reason = "";
            if (fr.support<getMinSup()) {
                passes = false;
                reason += " support";
            } else {
                reason += " SUPPORT";
            }
            if (fr.nodes.size()<minSize) {
                passes = false;
                reason += " size";
            } else {
                reason += " SIZE";
            }
            if (fr.avgLength<minLen) {
                passes = false;
                reason += " avgLength";
            } else {
                reason += " AVGLENGTH";
            }
            if (passes) filteredFRs.add(fr);
            if (getVerbose()) System.out.println(fr.toString()+reason);
        }
        if (getVerbose()) System.out.println(filteredFRs.size()+" FRs passed minSup="+getMinSup()+", minSize="+minSize+", minLen="+minLen);
	// output the filtered FRs and SVM data
        frequentedRegions = filteredFRs;
	if (frequentedRegions.size()>0) {
	    printFrequentedRegions();
	    printPathFRsSVM();
            printPathFRsARFF();
	}
    }

    // parameter getters
    public double getAlpha() {
        return Double.parseDouble(parameters.getProperty("alpha"));
    }
    public double getKappa() {
        return Double.parseDouble(parameters.getProperty("kappa"));
    }
    public boolean getVerbose() {
        return Boolean.parseBoolean(parameters.getProperty("verbose"));
    }
    public boolean getPrunedGraph() {
        return Boolean.parseBoolean(parameters.getProperty("prunedGraph"));
    }
    public int getMinSup() {
        return Integer.parseInt(parameters.getProperty("minSup"));
    }
    public int getMinSize() {
        return Integer.parseInt(parameters.getProperty("minSize"));
    }
    public double getMinLen() {
        return Double.parseDouble(parameters.getProperty("minLen"));
    }

    // parameter setters
    public void setVerbose() {
        parameters.setProperty("verbose", "true");
    }
    public void setDebug() {
        parameters.setProperty("debug", "true");
    }
    public void setCaseCtrl() {
        parameters.setProperty("caseCtrl", "true");
    }
    public void setBruteForce() {
        parameters.setProperty("bruteForce", "true");
    }
    public void setSerial() {
        parameters.setProperty("serial", "true");
    }
    public void setResume() {
        parameters.setProperty("resume", "true");
    }
    public void setKappaByNodes() {
        parameters.setProperty("kappaByNodes", "true");
    }
    public void setPrunedGraph() {
        parameters.setProperty("prunedGraph", "true");
    }
    public void setMinSup(int minSup) {
        parameters.setProperty("minSup", String.valueOf(minSup));
    }
    public void setMinSize(int minSize) {
        parameters.setProperty("minSize", String.valueOf(minSize));
    }
    public void setMinLen(double minLen) {
        parameters.setProperty("minLen", String.valueOf(minLen));
    }
    public void setMaxRound(int maxRound) {
        parameters.setProperty("maxRound", String.valueOf(maxRound));
    }
    public void setOutputPrefix(String outputPrefix) {
        parameters.setProperty("outputPrefix", outputPrefix);
    }

    /**
     * Command-line utility
     */
    public static void main(String[] args) throws Exception {

        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        //
        Option alphaOption = new Option("a", "alpha", true, "alpha=penetrance, fraction of a supporting path's sequence that supports the FR (required)");
        alphaOption.setRequired(false);
        options.addOption(alphaOption);
	// 
        Option dotOption = new Option("d", "dot", true, "splitMEM DOT file (requires FASTA file)");
        dotOption.setRequired(false);
        options.addOption(dotOption);
        //
        Option fastaOption = new Option("f", "fasta", true, "FASTA file (requires DOT file)");
        fastaOption.setRequired(false);
        options.addOption(fastaOption);
        //
        Option genotypeOption = new Option("g", "genotype", true, "which genotype to include (0,1) from the input file; "+PangenomicGraph.BOTH_GENOTYPES+" to include all ("+PangenomicGraph.BOTH_GENOTYPES+")");
        genotypeOption.setRequired(false);
        options.addOption(genotypeOption);
        //
        Option jsonOption = new Option("j", "json", true, "vg JSON file");
        jsonOption.setRequired(false);
        options.addOption(jsonOption);
        //
        Option gfaOption = new Option("gfa", "gfa", true, "GFA file");
        gfaOption.setRequired(false);
        options.addOption(gfaOption);
        //
        Option kappaOption = new Option("k", "kappa", true, "maximum insertion length that any supporting path may have (required)");
        kappaOption.setRequired(false);
        options.addOption(kappaOption);
        //
        Option kappaByNodesOption = new Option("kn", "kappabynodes", false, "use number of inserted nodes rather than length of inserted sequence in kappa restriction (false)");
        kappaByNodesOption.setRequired(false);
        options.addOption(kappaByNodesOption);
        //
        Option minLenOption = new Option("l", "minlen", true, "minimum allowed average length (bp) of an FR's subpaths ("+MINLEN+")");
        minLenOption.setRequired(false);
        options.addOption(minLenOption);
        //
        Option minSupOption = new Option("m", "minsup", true, "minimum number of supporting paths for a region to be considered interesting ("+MINSUP+")");
        minSupOption.setRequired(false);
        options.addOption(minSupOption);
        //
        Option minSizeOption = new Option("s", "minsize", true, "minimum number of nodes that a FR must contain to be considered interesting ("+MINSIZE+")");
        minSizeOption.setRequired(false);
        options.addOption(minSizeOption);
        //
        Option outputprefixOption = new Option("o", "outputprefix", true, "output file prefix (stdout)");
        outputprefixOption.setRequired(false);
        options.addOption(outputprefixOption);
        //
        Option inputprefixOption = new Option("i", "inputprefix", true, "input file prefix for further processing");
        inputprefixOption.setRequired(false);
        options.addOption(inputprefixOption);
        //
        Option labelsOption = new Option("p", "pathlabels", true, "tab-delimited file with pathname<tab>label");
        labelsOption.setRequired(false);
        options.addOption(labelsOption);
        //
        Option verboseOption = new Option("v", "verbose", false, "verbose output ("+VERBOSE+")");
        verboseOption.setRequired(false);
        options.addOption(verboseOption);
        //
        Option debugOption = new Option("do", "debug", false, "debug output ("+DEBUG+")");
        debugOption.setRequired(false);
        options.addOption(debugOption);
        //
        Option caseCtrlOption = new Option("cc", "casectrl", false, "emphasize FRs that have large case vs. control support ("+CASE_CTRL+")");
        caseCtrlOption.setRequired(false);
        options.addOption(caseCtrlOption);
        //
        Option bruteForceOption = new Option("bf", "bruteforce", false, "find FRs comprehensively via brute force - testing only! ("+BRUTE_FORCE+")");
        bruteForceOption.setRequired(false);
        options.addOption(bruteForceOption);
        //
        Option serialOption = new Option("sr", "serial", false, "find FRs serially for testing/experiments ("+SERIAL+")");
        serialOption.setRequired(false);
        options.addOption(serialOption);
        //
        Option resumeOption = new Option("r", "resume", false, "resume from a previous run ("+RESUME+")");
        resumeOption.setRequired(false);
        options.addOption(resumeOption);
        //
        Option maxRoundOption = new Option("mr", "maxround", true, "maximum FR-finding round to run (0=unlimited)");
        maxRoundOption.setRequired(false);
        options.addOption(maxRoundOption);
        //
        Option prunedGraphOption = new Option("pr", "prunedgraph", false, "prune graph -- remove all common nodes (false)");
        prunedGraphOption.setRequired(false);
        options.addOption(prunedGraphOption);

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp("FRFinder", options);
            System.exit(1);
            return;
        }

        if (cmd.getOptions().length==0) {
            formatter.printHelp("FRFinder", options);
            System.exit(1);
            return;
        }
        
        // parameter validation
        if (!cmd.hasOption("inputprefix") && !cmd.hasOption("dot") && !cmd.hasOption("json") && !cmd.hasOption("gfa")) {
            throw new Exception("You must specify a splitMEM-style DOT file plus FASTA (-d/--dot and -f/--fasta ), a vg JSON file (-j, --json) or a vg GFA file (--gfa)");
        }
        if (cmd.hasOption("dot") && !cmd.hasOption("fasta")) {
            throw new Exception("If you specify a splitMEM dot file (-d/--dot) you MUST ALSO specify a FASTA file (-f/--fasta)");
        }
        
        // files
        String dotFile = cmd.getOptionValue("dot");
        String fastaFile = cmd.getOptionValue("fasta");
        String jsonFile = cmd.getOptionValue("json");
        // String gfaFilename = cmd.getOptionValue("gfa");
        // String pathLabelsFile = cmd.getOptionValue("pathlabels");

        // GFA file
        File gfaFile = null;
        if (cmd.hasOption("gfa")) gfaFile = new File(cmd.getOptionValue("gfa"));

        // path labels file
        File labelsFile = null;
        if (cmd.hasOption("pathlabels")) labelsFile = new File(cmd.getOptionValue("pathlabels"));

        // run parameters
        double alpha = 0.0;
        int kappa = 0;
        if (cmd.hasOption("alpha")) alpha = Double.parseDouble(cmd.getOptionValue("alpha"));
        if (cmd.hasOption("kappa")) kappa = Integer.parseInt(cmd.getOptionValue("kappa"));

        // filters
        int minSup = MINSUP;
        int minSize = MINSIZE;
        double minLen = MINLEN;
        if (cmd.hasOption("minsup")) {
            minSup = Integer.parseInt(cmd.getOptionValue("minsup"));
        }
        if (cmd.hasOption("minsize")) {
            minSize = Integer.parseInt(cmd.getOptionValue("minsize"));
        }
        if (cmd.hasOption("minlen")) {
            minLen = Double.parseDouble(cmd.getOptionValue("minlen"));
        }

        // run limits
        int maxRound = MAXROUND;
        if (cmd.hasOption("maxround")) {
            maxRound = Integer.parseInt(cmd.getOptionValue("maxround"));
        }

        FRFinder frf = null;
        boolean postProcess = false;
        if (cmd.hasOption("inputprefix")) {
            postProcess = true;
            String inputPrefix = cmd.getOptionValue("inputprefix");
            // instantiate the FRFinder from the saved files
            frf = new FRFinder(inputPrefix);
            // apply the desired filters
            frf.setMinSup(minSup);
            frf.setMinSize(minSize);
            frf.setMinLen(minLen);
	    // set the outputPrefix, which depends on the above being set
	    frf.setOutputPrefix(frf.getOutputPrefix(inputPrefix));
        } else if (gfaFile!=null) { 
            // create a PangenomicGraph from a GFA file
            PangenomicGraph pg = new PangenomicGraph();
            if (cmd.hasOption("verbose")) pg.setVerbose();
            if (cmd.hasOption("genotype")) pg.setGenotype(Integer.parseInt(cmd.getOptionValue("genotype")));
            pg.importGFA(gfaFile);
            // if a labels file is given, add them to the paths
            if (labelsFile!=null) {
                pg.readPathLabels(labelsFile);
            }
            // instantiate the FRFinder with this Graph, alpha and kappa
            postProcess = false;
            frf = new FRFinder(pg);
            if (cmd.hasOption("casectrl")) {
                frf.setCaseCtrl();
            }
            if (cmd.hasOption("bruteforce")) {
                frf.setBruteForce();
            } else if (cmd.hasOption("serial")) {
                frf.setSerial();
            }
            // these are not used by normal method, which saves all FRs
            frf.setMinSup(minSup);
            frf.setMinSize(minSize);
            frf.setMinLen(minLen);
        }
        
        // set optional FRFinder parameters
        if (cmd.hasOption("verbose")) frf.setVerbose();
        if (cmd.hasOption("debug")) frf.setDebug();
        if (cmd.hasOption("kappabynodes")) frf.setKappaByNodes();
        if (cmd.hasOption("prunedgraph")) frf.setPrunedGraph();
        if (cmd.hasOption("outputprefix")) {
            frf.setOutputPrefix(cmd.getOptionValue("outputprefix"));
        }
        frf.setMaxRound(maxRound);

        // tell findFRs to load saved data if resume requested
        boolean resume = false;
        if (cmd.hasOption("resume")) frf.setResume();

        // run the requested job
        if (postProcess) {
            frf.postprocess();
        } else {
            frf.findFRs(alpha, kappa);
        }
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
     * Print the path names and the count of subpaths for each FR, to stdout or outputPrefix.pathfrs.txt.
     * This can be used as input to a classification routine.
     */
    void printPathFRs() throws IOException {
        PrintStream out = System.out;
        if (outputPrefix==null) {
            printHeading("PATH FREQUENTED REGIONS");
        } else {
            out = new PrintStream(getPathFRsFilename(outputPrefix));
        }
        // columns are paths
        boolean first = true;
        for (PathWalk path : graph.getPaths()) {
            if (first) {
                first = false;
            } else {
                out.print("\t");
            }
            out.print(path.getNameGenotypeLabel());
        }
        out.println("");
        // rows are FRs
        int c = 1;
        for (FrequentedRegion fr : frequentedRegions) {
            out.print("FR"+(c++));
            for (PathWalk path : graph.getPaths()) {
                out.print("\t"+fr.countSubpathsOf(path));
            }
            out.println("");
        }
        if (outputPrefix!=null) out.close();
    }

    /**
     * Print the labeled path FR support for SVM analysis. Lines are like:
     *
     * path1.0 case 1:1 2:1 3:1 4:0 ...
     * path1.1 case 1:0 2:0 3:0 4:1 ...
     * path2.0 ctrl 1:0 2:1 3:0 4:2 ...
     *
     * which is similar, but not identical to, the SVMlight format.
     */
    void printPathFRsSVM() throws IOException {
        PrintStream out = System.out;
        if (outputPrefix==null) {
            printHeading("PATH SVM RECORDS");
        } else {
            out = new PrintStream(getPathFRsSVMFilename(outputPrefix));
        }
        // only rows, one per path
        for (PathWalk path : graph.getPaths()) {
            out.print(path.getNameGenotype());
            // TODO: update these to strings along with fixing the SVM code to handle strings
            String group = "";
            if (path.getLabel()!=null) group = path.getLabel();
            out.print("\t"+group);
            int c = 0;
            for (FrequentedRegion fr : frequentedRegions) {
                c++;
                out.print("\t"+c+":"+fr.countSubpathsOf(path));
            }
            out.println("");
        }
        if (outputPrefix!=null) out.close();
    }

    /**
     * Print the (unlabeled) path FR support in ARFF format.
     *
     * @RELATION iris
     *
     * @ATTRIBUTE ID           STRING
     * @ATTRIBUTE sepallength  NUMERIC
     * @ATTRIBUTE sepalwidth   NUMERIC
     * @ATTRIBUTE petallength  NUMERIC
     * @ATTRIBUTE petalwidth   NUMERIC
     * @ATTRIBUTE class        {Iris-setosa,Iris-versicolor,Iris-virginica}
     
     * @DATA
     * 5.1,3.5,1.4,0.2,Iris-setosa
     * 4.9,3.0,1.4,0.2,Iris-virginica
     * 4.7,3.2,1.3,0.2,Iris-versicolor
     * 4.6,3.1,1.5,0.2,Iris-setosa
     * 5.0,3.6,1.4,0.2,Iris-viginica
     */
    void printPathFRsARFF() throws IOException {
        PrintStream out = System.out;
        if (outputPrefix==null) {
            out.println("@RELATION frs");
        } else {
            out = new PrintStream(getPathFRsARFFFilename(outputPrefix));
            out.println("@RELATION "+outputPrefix);
            out.println("");
        }
        // attributes: path ID
        out.println("@ATTRIBUTE ID STRING");
        // attributes: each FR is a numeric labeled FRn
        int c = 0;
        for (FrequentedRegion fr : frequentedRegions) {
            c++;
            String frLabel = "FR"+c;
            out.println("@ATTRIBUTE "+frLabel+" NUMERIC");
        }
        // add the class attribute
        out.println("@ATTRIBUTE class {case,ctrl}");
        out.println("");
        // data
        out.println("@DATA");
        for (PathWalk path : graph.getPaths()) {
            out.print(path.getNameGenotype()+",");
            c = 0;
            for (FrequentedRegion fr : frequentedRegions) {
                c++;
                out.print(fr.countSubpathsOf(path)+",");
            }
            out.println(path.getLabel());
        }
        if (outputPrefix!=null) out.close();
    }
    
    /**
     * Print out the FRs, either to stdout or outputPrefix.frs.txt
     */
    void printFrequentedRegions() throws IOException {
        if (frequentedRegions.size()==0) {
            System.err.println("NO FREQUENTED REGIONS!");
            return;
        }
        PrintStream out = System.out;
        if (outputPrefix==null) {
            printHeading("FREQUENTED REGIONS");
        } else {
            out = new PrintStream(getFRsFilename(outputPrefix));
        }
        out.println(frequentedRegions.first().columnHeading());
        for (FrequentedRegion fr : frequentedRegions) {
            out.println(fr.toString());
        }
        if (outputPrefix!=null) out.close();
    }

    /**
     * Print out the FRs along with their subpaths, strictly to an output file.
     */
    void printFRSubpaths() throws IOException {
        if (frequentedRegions.size()==0) {
            System.err.println("NO FREQUENTED REGIONS!");
            return;
        }
        if (outputPrefix==null) return;
        PrintStream out = new PrintStream(getFRSubpathsFilename(outputPrefix));
        for (FrequentedRegion fr : frequentedRegions) {
            out.println(fr.toString());
            out.println(fr.subpathsString());
        }
        if (outputPrefix!=null) out.close();
    }

    /**
     * Print a crude histogram of FR node sizes.
     */
    public void printFRHistogram() {
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

    /**
     * Print out the parameters, either to stdout or outputPrefix.params.txt
     */
    public void printParameters() throws IOException {
        PrintStream out = System.out;
        if (outputPrefix==null) {
            printHeading("PARAMETERS");
        } else {
            out = new PrintStream(getParamsFilename(outputPrefix));
        }
        out.println("outputprefix"+"\t"+outputPrefix);
        // Graph
        if (graph!=null) {
            out.println("genotype"+"\t"+graph.getGenotype());
            if (graph.getGFAFilename()!=null) out.println("gfafile"+"\t"+graph.getGFAFilename());
            if (graph.getPathLabelsFilename()!=null) out.println("pathlabels"+"\t"+graph.getPathLabelsFilename());
            // if (graph.jsonFile!=null) out.println("jsonfile"+"\t"+graph.jsonFile);
            // if (graph.dotFile!=null) out.println("dotfile"+"\t"+graph.dotFile);
            // if (graph.fastaFile!=null) out.println("fastafile"+"\t"+graph.fastaFile);
        }
        // FRFinder
        out.println("alpha"+"\t"+alpha);
        out.println("kappa"+"\t"+kappa);
        out.println("kappabynodes"+"\t"+kappaByNodes);
        out.println("prunedgraph"+"\t"+getPrunedGraph());
        out.println("casectrl"+"\t"+caseCtrl);
        if (inputPrefix!=null) {
            // post-processing parameters
            out.println("minsup"+"\t"+minSup);
            out.println("minsize"+"\t"+minSize);
            out.println("minlen"+"\t"+minLen);
        }
        // runtime stuff
        out.println("date"+"\t"+ZonedDateTime.now().toString());
        out.println("clocktime"+"\t"+formatTime(clockTime));
    }

    /**
     * Read in the parameters from a previous run.
     */
    void readParameters(String inputPrefix) throws Exception {
        String paramsFile = getParamsFilename(inputPrefix);
        parameters.setProperty("paramsFile", paramsFile);
        BufferedReader reader = new BufferedReader(new FileReader(paramsFile));
        String line = null;
        String jsonFile = null;
        String gfaFilename = null;
        String dotFile = null;
        String fastaFile = null;
        String labelsFilename = null;
        int genotype = PangenomicGraph.BOTH_GENOTYPES;
        while ((line=reader.readLine())!=null) {
            String[] parts = line.split("\t");
            if (parts[0].equals("jsonfile")) {
                parameters.setProperty("jsonFile", parts[1]);
            } else if (parts[0].equals("gfafile")) {
                parameters.setProperty("gfaFilename", parts[1]);
            } else if (parts[0].equals("dotFile")) {
                parameters.setProperty("dotFile", parts[1]);
            } else if (parts[0].equals("fastafile")) {
                parameters.setProperty("fastaFile", parts[1]);
            } else if (parts[0].equals("pathlabels")) {
                parameters.setProperty("labelsFilename", parts[1]);
            } else if (parts[0].equals("genotype")) {
                parameters.setProperty("genotype", parts[1]);
            } else if (parts[0].equals("alpha")) {
                parameters.setProperty("alpha", parts[1]);
            } else if (parts[0].equals("kappa")) {
                parameters.setProperty("kappa", parts[1]);
            } else if (parts[0].equals("kappabynodes")) {
                parameters.setProperty("kappaByNodes", parts[1]);
            } else if (parts[0].equals("prunedgraph")) {
                parameters.setProperty("prunedGraph", parts[1]);
            } else if (parts[0].equals("casectrl")) {
                parameters.setProperty("caseCtrl", parts[1]);
            } else if (parts[0].equals("minsup")) {
                parameters.setProperty("minSup", parts[1]);
            } else if (parts[0].equals("minsize")) {
                parameters.setProperty("minSize", parts[1]);
            } else if (parts[0].equals("minlen")) {
                parameters.setProperty("minLen", parts[1]);
            }
        }
        // load the Graph if we've got the files
        if (jsonFile!=null) {
            System.err.println("JSON graph import is not currently enabled.");
            System.exit(1);
        } else if (gfaFilename!=null) {
            if (verbose) System.out.println("Reading graph from "+gfaFilename);
            File gfaFile = new File(gfaFilename);
            graph = new PangenomicGraph();
            graph.importGFA(gfaFile);
            graph.setGenotype(genotype);
        } else if (dotFile!=null && fastaFile!=null) {
            System.err.println("DOT graph import is not currently enabled.");
            System.exit(1);
        }
        // load the path labels if we got 'em
        if (labelsFilename!=null) {
            if (verbose) System.out.println("Reading labels file from "+labelsFilename);
            File labelsFile = new File(labelsFilename);
            graph.readPathLabels(labelsFile);
        }
    }

    /**
     * Read FRs from the output from a previous run.
     * Assumes that graph is already initialized.
     * [18,34]	70	299	54	16
     * 509678.0.ctrl:[18,20,21,23,24,26,27,29,30,33,34]
     * 628863.1.case:[18,20,21,23,24,26,27,29,30,33,34]
     * etc.
     */
    void readFrequentedRegions(double alpha, double kappa) throws Exception {
        // do we have a Graph?
        if (graph.getNodes().size()==0) {
            throw new Exception("ERROR in readFrequentedRegions: graph has not been initialized.");
        }
	// build an id-keyed map of nodes for further down
	Map<Long,Node> nodesById = new HashMap<>();
	for (Node n : graph.getNodes()) {
	    if (n.getSequence()==null) {
		throw new Exception("ERROR in readFrequentedRegions: graph node "+n.getId()+" has no sequence.");
	    }
	    nodesById.put(n.getId(), n);
	}
	// build the FRs
        frequentedRegions = new TreeSet<>();
        String frFilename = getFRSubpathsFilename(inputPrefix);
        BufferedReader reader = new BufferedReader(new FileReader(frFilename));
        String line = null;
        while ((line=reader.readLine())!=null) {
            String[] fields = line.split("\t");
            NodeSet nodes = new NodeSet(graph, fields[0]);
            int support = Integer.parseInt(fields[1]);
            double avgLength = Double.parseDouble(fields[2]);
            Set<PathWalk> subpaths = new HashSet<>();
            for (int i=0; i<support; i++) {
                line = reader.readLine();
                String[] parts = line.split(":");
                String pathFull = parts[0];
                String nodeString = parts[1];
                // split out the name, genotype, label, nodes
                String[] nameParts = pathFull.split("\\.");
                String name = nameParts[0];
                int genotype = -1;
                if (nameParts.length>1) genotype = Integer.parseInt(nameParts[1]);
                String label = null;
                if (nameParts.length>2) label = nameParts[2];
                List<Node> subNodes = new LinkedList<>();
                String[] nodesAsStrings = nodeString.replace("[","").replace("]","").split(",");
                for (String nodeAsString : nodesAsStrings) {
		    long nodeId = Long.parseLong(nodeAsString);
                    subNodes.add(nodesById.get(nodeId));
                }
                // add to the subpaths
                subpaths.add(new PathWalk(graph, subNodes, name, genotype, label, false));
            }
            FrequentedRegion fr = new FrequentedRegion(graph, nodes, subpaths, alpha, kappa, kappaByNodes, support, avgLength);
            frequentedRegions.add(fr);
        }
    }

    /**
     * Form the FRs output filename
     */
    String getFRsFilename(String prefix) {
        return prefix+".frs.txt";
    }

    /**
     * Form the FRSubpaths output filename
     */
    String getFRSubpathsFilename(String prefix) {
        return prefix+".subpaths.txt";
    }

    /**
     * Form the pathFRs output filename
     */
    String getPathFRsFilename(String prefix) {
        return prefix+".pathfrs.txt";
    }

    /**
     * Form the SVM version of the pathFRs output filename
     */
    String getPathFRsSVMFilename(String prefix) {
        return prefix+".svm.txt";
    }

    /**
     * Form the ARFF version of the pathFRs output filename
     */
    String getPathFRsARFFFilename(String prefix) {
        return prefix+".arff";
    }

    /**
     * Form the parameters output filename
     */
    String getParamsFilename(String prefix) {
        return prefix+".params.txt";
    }

    /**
     * Form the new output prefix from the input prefix and post-processing parameters
     */
    String getOutputPrefix(String inputPrefix) {
        return inputPrefix+"-"+minSup+"."+minSize+"."+(int)minLen;
    }

    /**
     * Format a time duration given in milliseconds.
     */
    public static String formatTime(long millis) {
        DecimalFormat tf = new DecimalFormat("00"); // hours, minutes, seconds
        long hours = (millis / 1000) / 60 / 60;
	long minutes = (millis / 1000 / 60) % 60;
        long seconds = (millis / 1000) % 60;
	return tf.format(hours)+":"+tf.format(minutes)+":"+tf.format(seconds);
    }
}
