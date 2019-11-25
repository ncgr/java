package org.ncgr.pangenomics.fr;

import org.ncgr.jgraph.Node;
import org.ncgr.jgraph.NodeSet;
import org.ncgr.jgraph.NoNodesException;
import org.ncgr.jgraph.NoNodePathsException;
import org.ncgr.jgraph.NoPathsException;
import org.ncgr.jgraph.NullNodeException;
import org.ncgr.jgraph.NullSequenceException;
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

    // a PangenomicGraph must be supplied to the constructor unless post-processing
    PangenomicGraph graph;

    // store paths locally since post-processing doesn't build a graph
    Set<PathWalk> paths;

    // parameters are stored in a Properties object
    Properties parameters = new Properties();

    // save filename suffixes
    String FREQUENTED_REGIONS_SAVE = "save.frs.txt";
    String SYNC_FREQUENTED_REGIONS_SAVE = "save.syncFrequentedRegions.txt";
    String USED_FRS_SAVE = "save.usedFRs.txt";
    String REJECTED_NODESETS_SAVE = "save.rejectedNodeSets.txt";

    // the FRs, sorted for convenience
    TreeSet<FrequentedRegion> frequentedRegions;

    // diagnostic
    long clockTime;

    /**
     * Construct with a populated Graph and default parameters.
     */
    public FRFinder(PangenomicGraph graph) {
        initializeParameters();
        this.graph = graph;
        this.paths = graph.getPaths();
    }

    /**
     * Construct with the output from a previous run. Be sure to set minSup, minSize, minLen filters as needed before running postprocess().
     */
    public FRFinder(String inputPrefix)
        throws FileNotFoundException, IOException, NullNodeException, NullSequenceException, NoNodesException {
        initializeParameters();
        readParameters(inputPrefix); // sets properties from file
        readFrequentedRegions();
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
        parameters.setProperty("priority", "0");
        parameters.setProperty("bruteForce", "false");
        parameters.setProperty("serial", "false");
        parameters.setProperty("resume", "false");
        parameters.setProperty("prunedGraph", "false");
    }

    /**
     * Find the frequented regions in this Graph for the given alpha and kappa values.
     * double alpha = penetrance: the fraction of a supporting strain's sequence that actually supports the FR;
     *                alternatively, `1-alpha` is the fraction of inserted sequence
     * int kappa = maximum insertion: the maximum number of inserted nodes that a supporting path may have.
      */
    public void findFRs(double alpha, int kappa) throws FileNotFoundException, IOException,
                                                        NullNodeException, NullSequenceException, NoPathsException, NoNodePathsException {

	System.out.println("# Starting findFRs: alpha="+alpha+" kappa="+kappa);

        // output the graph files
        graph.printAll(getGraphName());

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
            System.out.println("# Resuming from previous run");
            String line = null;
	    // usedFRs
            BufferedReader usedFRsReader = new BufferedReader(new FileReader(getGraphName()+"."+USED_FRS_SAVE));
            while ((line=usedFRsReader.readLine())!=null) {
                String[] parts = line.split("\t");
                NodeSet nodes = new NodeSet(graph, parts[0]);
                usedFRs.add(new FrequentedRegion(graph, nodes, alpha, kappa));
            }
            // syncFrequentedRegions
            BufferedReader sfrReader = new BufferedReader(new FileReader(getGraphName()+"."+SYNC_FREQUENTED_REGIONS_SAVE));
            while ((line=sfrReader.readLine())!=null) {
                String[] parts = line.split("\t");
                NodeSet nodes = new NodeSet(graph, parts[0]);
                syncFrequentedRegions.add(new FrequentedRegion(graph, nodes, alpha, kappa));
            }
	    // rejectedNodeSets
	    BufferedReader rnsReader = new BufferedReader(new FileReader(getGraphName()+"."+REJECTED_NODESETS_SAVE));
	    while ((line=rnsReader.readLine())!=null) {
		rejectedNodeSets.add(line);
	    }
            // frequentedRegions
	    // 0                                                                                                1   2         3  4       
	    // [1353,1355,1356,1357,1359,1360,1361,1363,1364,1366,1367,1368,...,1463,1464,1465,1467,1468,1469]	27  18621.00  1	 26
	    
            BufferedReader frReader = new BufferedReader(new FileReader(getGraphName()+"."+FREQUENTED_REGIONS_SAVE));
	    line = frReader.readLine(); // header
            while ((line=frReader.readLine())!=null) {
                String[] parts = line.split("\t");
                NodeSet nodes = new NodeSet(graph, parts[0]);
                frequentedRegions.add(new FrequentedRegion(graph, nodes, alpha, kappa));
                round++;
            }
	    // informativationalism
	    System.out.println("# Loaded "+usedFRs.size()+" usedFRs.");
            System.out.println("# Loaded "+syncFrequentedRegions.size()+" syncFrequentedRegions.");
	    System.out.println("# Loaded "+rejectedNodeSets.size()+" rejectedNodeSets.");
            System.out.println("# Loaded "+frequentedRegions.size()+" frequentedRegions.");
            System.out.println("# Now continuing with FR finding...");
        } else {
            // initialize syncFrequentedRegions with single-node FRs that have alpha/kappa support
            for (Node node : graph.getNodes()) {
                NodeSet c = new NodeSet();
                c.add(node);
                Set<PathWalk> s = new HashSet<>();
                for (PathWalk p : paths) {
                    Set<PathWalk> support = p.computeSupport(c, alpha, kappa);
                    s.addAll(support);
                }
                if (s.size()>0) {
                    syncFrequentedRegions.add(new FrequentedRegion(graph, c, s, alpha, kappa));
                }
            }
        }

        // build the FRs round by round
	long startTime = System.currentTimeMillis();
        boolean added = true;
        while (added && (round<getMaxRound() || getMaxRound()==0)) {
            round++;
            added = false;
            if (getBruteForce()) {
                // no heurism, for demo purposes
                Set<FrequentedRegion> loopFRs = Collections.synchronizedSet(new HashSet<>());
                int oldSyncSize = syncFrequentedRegions.size();
                int oldFRSize = frequentedRegions.size();
                ////////////////////////////////////////
                // spin through FRs in a parallel manner
                syncFrequentedRegions.parallelStream().forEach(fr1 -> {
                        syncFrequentedRegions.parallelStream().forEach(fr2 -> {
                                if (fr1.compareTo(fr2)>0) {
                                    FRPair frpair = new FRPair(fr1, fr2, graph, alpha, kappa, getPriority());
                                    String nodesKey = frpair.nodes.toString();
                                    if (!usedNodeSets.contains(nodesKey)) {
                                        usedNodeSets.add(nodesKey);
                                        try {
                                            frpair.merge();
                                        } catch (NullNodeException e) {
                                            System.err.println(e);
                                            System.exit(1);
                                        } catch (NullSequenceException e) {
                                            System.err.println(e);
                                            System.exit(1);
                                        }
                                        loopFRs.add(frpair.merged);
                                        if (frpair.merged.support>=getMinSup() &&
                                            frpair.merged.avgLength>=getMinLen() &&
                                            frpair.merged.nodes.size()>=getMinSize()) frequentedRegions.add(frpair.merged);
                                    }
                                }
                            });
                    });
                ////////////////////////////////////////
                syncFrequentedRegions.addAll(loopFRs);
                System.out.println(round+":"+(syncFrequentedRegions.size()-oldSyncSize)+" sync FRs added; "+(frequentedRegions.size()-oldFRSize)+" supported FRs added.");
            } else if (getSerial()) {
                // serial processing with extra output for demo purposes or other experiments
                // put FR pairs into a PriorityQueue which sorts them by decreasing interest (defined by the FRPair comparator)
                PriorityQueue<FRPair> pq = new PriorityQueue<>();
                // spin through FRs in a serial manner
                for (FrequentedRegion fr1 : syncFrequentedRegions) {
                    for (FrequentedRegion fr2 : syncFrequentedRegions) {
                        if (fr2.compareTo(fr1)>=0 && !usedFRs.contains(fr1) && !usedFRs.contains(fr2)) {
                            // no merge or rejection test here
                            FRPair frpair = new FRPair(fr1, fr2, graph, alpha, kappa, getPriority());
                            String nodesKey = frpair.nodes.toString();
                            if (rejectedNodeSets.contains(nodesKey)) {
                                // do nothing
                            } else if (acceptedFRPairs.containsKey(nodesKey)) {
                                // use stored FRPair
                                frpair = acceptedFRPairs.get(nodesKey);
                                if (!frequentedRegions.contains(frpair.merged)) {
                                    pq.add(frpair);
                                    added = true;
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
                                    added = true;
                                }
                            }
                        }
                    }
                }
                // add our new FR
                if (pq.size()>0) {
                    FRPair frpair = pq.peek();
                    usedFRs.add(frpair.fr1);
                    usedFRs.add(frpair.fr2);
                    syncFrequentedRegions.add(frpair.merged);
                    frequentedRegions.add(frpair.merged);
                    System.out.println(round+":"+frpair.merged.toString());
                }
            } else {
                // default: parallel processing
                // put FR pairs into a PriorityBlockingQueue which sorts them by decreasing interest (defined by the FRPair comparator)
                PriorityBlockingQueue<FRPair> pq = new PriorityBlockingQueue<>();
                ////////////////////////////////////////
                // spin through FRs in a parallel manner
                syncFrequentedRegions.parallelStream().forEach(fr1 -> {
                        if (getDebug()) System.out.println("fr1="+fr1.getNodes().toString()); // DEBUG
                        syncFrequentedRegions.parallelStream().forEach(fr2 -> {
                                if (fr2.compareTo(fr1)>=0 && !usedFRs.contains(fr1) && !usedFRs.contains(fr2)) {
                                    // no merge or rejection test here
                                    FRPair frpair = new FRPair(fr1, fr2, graph, alpha, kappa, getPriority());
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
					    if (!frequentedRegions.contains(frpair.merged)) {
						pq.add(frpair);
					    }
                                        }
                                    }
                                    
                                }
                            });
                    });
                ////////////////////////////////////////
                // add our new FR if it has support>0
                if (pq.size()>0) {
                    FRPair frpair = pq.peek();
                    if (frpair.merged.support>0) {
                        added = true;
                        usedFRs.add(frpair.fr1);
                        usedFRs.add(frpair.fr2);
                        syncFrequentedRegions.add(frpair.merged);
                        frequentedRegions.add(frpair.merged);
                        System.out.println(round+":"+frpair.merged.toString());
                    }
                }
            }

            // output current state for continuation if aborted
            if (frequentedRegions.size()>0 && !getSkipSaveFiles()) {
                // usedFRs
                PrintStream usedFRsOut = new PrintStream(getGraphName()+"."+USED_FRS_SAVE);
                for (FrequentedRegion fr : usedFRs) {
                    usedFRsOut.println(fr.toString());
                }
                usedFRsOut.close();
                // syncFrequentedRegions
                PrintStream sfrOut = new PrintStream(getGraphName()+"."+SYNC_FREQUENTED_REGIONS_SAVE);
                for (FrequentedRegion fr : syncFrequentedRegions) {
                    sfrOut.println(fr.toString());
                }
                sfrOut.close();
                // rejectedNodeSets
                PrintStream rnsOut = new PrintStream(getGraphName()+"."+REJECTED_NODESETS_SAVE);
                for (String nodesKey : rejectedNodeSets) {
                    rnsOut.println(nodesKey);
                }
                rnsOut.close();
                // frequentedRegions
                PrintStream frOut = new PrintStream(getGraphName()+"."+FREQUENTED_REGIONS_SAVE);
                frOut.println(frequentedRegions.first().columnHeading()); // header
                for (FrequentedRegion fr : frequentedRegions) {
                    frOut.println(fr.toString());
                }
                frOut.close();
            }
        }

	clockTime = System.currentTimeMillis() - startTime;
        System.out.println("Found "+frequentedRegions.size()+" FRs.");
	System.out.println("Clock time: "+formatTime(clockTime));
        
	// final output
	if (frequentedRegions.size()>0) {
            printParameters(formOutputPrefix(alpha, kappa));
            printFrequentedRegions(formOutputPrefix(alpha, kappa));
            printFRSubpaths(formOutputPrefix(alpha, kappa));
            printPathFRs(formOutputPrefix(alpha, kappa));
	}
    }

    /**
     * Post-process a set of FRs for given minSup, minLen and minSize.
     */
    public void postprocess() throws FileNotFoundException, IOException {
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
            if (fr.nodes.size()<getMinSize()) {
                passes = false;
                reason += " size";
            } else {
                reason += " SIZE";
            }
            if (fr.avgLength<getMinLen()) {
                passes = false;
                reason += " avgLength";
            } else {
                reason += " AVGLENGTH";
            }
            if (passes) filteredFRs.add(fr);
            if (getVerbose()) System.out.println(fr.toString()+reason);
        }
        if (getVerbose()) System.out.println(filteredFRs.size()+" FRs passed minSup="+getMinSup()+", minSize="+getMinSize()+", minLen="+getMinLen());
	// output the filtered FRs and SVM data
        frequentedRegions = filteredFRs;
	if (frequentedRegions.size()>0) {
	    printFrequentedRegions(formOutputPrefix());
	    printPathFRsSVM(formOutputPrefix());
            printPathFRsARFF(formOutputPrefix());
	}
    }

    // parameter getters
    public boolean getVerbose() {
        return Boolean.parseBoolean(parameters.getProperty("verbose"));
    }
    public boolean getDebug() {
        return Boolean.parseBoolean(parameters.getProperty("debug"));
    }
    public boolean getPrunedGraph() {
        return Boolean.parseBoolean(parameters.getProperty("prunedGraph"));
    }
    public boolean getSkipSaveFiles() {
        return Boolean.parseBoolean(parameters.getProperty("skipSaveFiles"));
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
    public boolean getResume() {
        return Boolean.parseBoolean(parameters.getProperty("resume"));
    }
    public boolean getBruteForce() {
        return Boolean.parseBoolean(parameters.getProperty("bruteForce"));
    }
    public boolean getSerial() {
        return Boolean.parseBoolean(parameters.getProperty("serial"));
    }
    public int getPriority() {
        return Integer.parseInt(parameters.getProperty("priority"));
    }
    public String getInputPrefix() {
        return parameters.getProperty("inputPrefix");
    }
    public String getGraphName() {
        return parameters.getProperty("graphName");
    }
    public int getMaxRound() {
        return Integer.parseInt(parameters.getProperty("maxRound"));
    }

    // parameter setters
    public void setVerbose() {
        parameters.setProperty("verbose", "true");
    }
    public void setDebug() {
        parameters.setProperty("debug", "true");
    }
    public void setPriority(int priority) {
        parameters.setProperty("priority", String.valueOf(priority));
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
    public void setPrunedGraph() {
        parameters.setProperty("prunedGraph", "true");
    }
    public void setSkipSaveFiles() {
        parameters.setProperty("skipSaveFiles", "true");
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
    public void setGfaFile(String gfaFilename) {
        parameters.setProperty("gfaFile", gfaFilename);
    }
    public void setGraphName(String graphName) {
        parameters.setProperty("graphName", graphName);
    }

    /**
     * Command-line utility
     */
    public static void main(String[] args) throws FileNotFoundException, IOException,
                                                  NullNodeException, NullSequenceException, NoNodesException, NoPathsException, NoNodePathsException {
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        Option alphaStartOption = new Option("as", "alphastart", true, "starting value of alpha for a scan (can equal alphaend)");
        alphaStartOption.setRequired(false);
        options.addOption(alphaStartOption);
        //
        Option alphaEndOption = new Option("ae", "alphaend", true, "ending value of alpha for a scan (can equal alphastart)");
        alphaEndOption.setRequired(false);
        options.addOption(alphaEndOption);
        //
        Option kappaStartOption = new Option("ks", "kappastart", true, "starting value of kappa for a scan (can equal kappaend)");
        kappaStartOption.setRequired(false);
        options.addOption(kappaStartOption);
        //
        Option kappaEndOption = new Option("ke", "kappaend", true, "ending value of kappa for a scan (can equal kappastart)");
        kappaEndOption.setRequired(false);
        options.addOption(kappaEndOption);
        //
        Option genotypeOption = new Option("g", "genotype", true, "which genotype to include (0,1) from the input file; "+
                                           PangenomicGraph.BOTH_GENOTYPES+" to include all ["+PangenomicGraph.BOTH_GENOTYPES+"]");
        genotypeOption.setRequired(false);
        options.addOption(genotypeOption);
        //
        Option gfaOption = new Option("gfa", "gfa", true, "GFA file");
        gfaOption.setRequired(false);
        options.addOption(gfaOption);
        //
        Option minLenOption = new Option("l", "minlen", true, "minimum allowed average length (bp) of an FR's subpaths [1.0]");
        minLenOption.setRequired(false);
        options.addOption(minLenOption);
        //
        Option minSupOption = new Option("m", "minsup", true, "minimum number of supporting paths for a region to be considered interesting [1]");
        minSupOption.setRequired(false);
        options.addOption(minSupOption);
        //
        Option minSizeOption = new Option("s", "minsize", true, "minimum number of nodes that a FR must contain to be considered interesting [1]");
        minSizeOption.setRequired(false);
        options.addOption(minSizeOption);
        //
        Option inputprefixOption = new Option("i", "inputprefix", true, "input file prefix for further processing");
        inputprefixOption.setRequired(false);
        options.addOption(inputprefixOption);
        //
        Option labelsOption = new Option("p", "pathlabels", true, "tab-delimited file with pathname<tab>label");
        labelsOption.setRequired(false);
        options.addOption(labelsOption);
        //
        Option verboseOption = new Option("v", "verbose", false, "verbose output [false]");
        verboseOption.setRequired(false);
        options.addOption(verboseOption);
        //
        Option debugOption = new Option("do", "debug", false, "debug output [false]");
        debugOption.setRequired(false);
        options.addOption(debugOption);
        //
        Option priorityOption = new Option("pri", "priority", true, "priority of FRs: 0=support; 1=|case-control|; 2=case-control (0)");
        priorityOption.setRequired(true);
        options.addOption(priorityOption);
        //
        Option bruteForceOption = new Option("bf", "bruteforce", false, "find FRs comprehensively via brute force - testing only! [false]");
        bruteForceOption.setRequired(false);
        options.addOption(bruteForceOption);
        //
        Option serialOption = new Option("sr", "serial", false, "find FRs serially for testing/experiments [false]");
        serialOption.setRequired(false);
        options.addOption(serialOption);
        //
        Option resumeOption = new Option("r", "resume", false, "resume from a previous run [false]");
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
        //
        Option skipNodePathsOption = new Option("snp", "skipnodepaths", false, "skip building list of paths per node (false)");
        skipNodePathsOption.setRequired(false);
        options.addOption(skipNodePathsOption);
        //
        Option skipSaveFilesOption = new Option("ssf", "skipsavefiles", false, "skip saving files after each FR is found (to save time; false)");
        skipSaveFilesOption.setRequired(false);
        options.addOption(skipSaveFilesOption);

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
        if (!cmd.hasOption("inputprefix") && !cmd.hasOption("gfa")) {
            System.err.println("You must specify a vg GFA file (--gfa)");
            System.exit(1);
        }
        
        // GFA file
        // CIDR_HD_Modifiers/HTT.400.paths.gfa
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

        // required run parameters
        double alphaStart = 0.0;
        double alphaEnd = 0.0;
        int kappaStart = 0;
        int kappaEnd = 0;
        if (cmd.hasOption("alphastart")) alphaStart = Double.parseDouble(cmd.getOptionValue("alphastart"));
        if (cmd.hasOption("alphaend")) alphaEnd = Double.parseDouble(cmd.getOptionValue("alphaend"));
        if (cmd.hasOption("kappastart")) kappaStart = Integer.parseInt(cmd.getOptionValue("kappastart"));
        if (cmd.hasOption("kappaend")) kappaEnd = Integer.parseInt(cmd.getOptionValue("kappaend"));
        
        if (cmd.hasOption("inputprefix")) {
            // post-process an existing run
            String inputPrefix = cmd.getOptionValue("inputprefix");
            // instantiate the FRFinder from the saved files
            FRFinder frf = new FRFinder(inputPrefix);
            // set optional FRFinder parameters
            if (cmd.hasOption("minsup")) frf.setMinSup(Integer.parseInt(cmd.getOptionValue("minsup")));
            if (cmd.hasOption("minsize")) frf.setMinSize(Integer.parseInt(cmd.getOptionValue("minsize")));
            if (cmd.hasOption("minlen")) frf.setMinLen(Double.parseDouble(cmd.getOptionValue("minlen")));
            if (cmd.hasOption("verbose")) frf.setVerbose();
            if (cmd.hasOption("debug")) frf.setDebug();
            if (cmd.hasOption("skipSaveFiles")) frf.setSkipSaveFiles();
            frf.postprocess();
        } else {
            // import a PangenomicGraph from a GFA file
            PangenomicGraph pg = new PangenomicGraph();
            if (cmd.hasOption("verbose")) pg.setVerbose();
            if (cmd.hasOption("genotype")) pg.setGenotype(Integer.parseInt(cmd.getOptionValue("genotype")));
            if (cmd.hasOption("skipnodepaths")) pg.setSkipNodePaths();
	    System.out.println("# Loading GFA file "+gfaFile.getName());
            pg.importGFA(gfaFile);
	    // prune the graph if so commanded
	    if (cmd.hasOption("prunedgraph")) {
		int nRemoved = pg.prune();
		System.out.println("# Graph has been pruned ("+nRemoved+" fully common nodes removed).");
	    }
	    System.out.println("# Graph has "+pg.vertexSet().size()+" nodes and "+pg.getPaths().size()+" paths.");
            // if a labels file is given, add them to the paths
            if (labelsFile!=null) {
                pg.readPathLabels(labelsFile);
		System.out.println("# Graph has "+pg.getLabelCounts().get("case")+" case paths and "+pg.getLabelCounts().get("ctrl")+" ctrl paths.");
            }
            // instantiate the FRFinder with this PangenomicGraph
            FRFinder frf = new FRFinder(pg);
            frf.setGfaFile(gfaFile.getName());
            frf.setGraphName(graphName);
            if (cmd.hasOption("priority")) {
                frf.setPriority(Integer.parseInt(cmd.getOptionValue("priority")));
            }
            if (cmd.hasOption("bruteforce")) {
                frf.setBruteForce();
            } else if (cmd.hasOption("serial")) {
                frf.setSerial();
            }
            // set optional FRFinder parameters
            if (cmd.hasOption("minsup")) frf.setMinSup(Integer.parseInt(cmd.getOptionValue("minsup")));
            if (cmd.hasOption("minsize")) frf.setMinSize(Integer.parseInt(cmd.getOptionValue("minsize")));
            if (cmd.hasOption("minlen")) frf.setMinLen(Double.parseDouble(cmd.getOptionValue("minlen")));
            if (cmd.hasOption("maxround")) frf.setMaxRound(Integer.parseInt(cmd.getOptionValue("maxround")));
            if (cmd.hasOption("verbose")) frf.setVerbose();
            if (cmd.hasOption("debug")) frf.setDebug();
            if (cmd.hasOption("resume")) frf.setResume();
            if (cmd.hasOption("prunedgraph")) frf.setPrunedGraph();
            if (cmd.hasOption("skipSaveFiles")) frf.setSkipSaveFiles();
            // run the requested job
            if (alphaStart==alphaEnd && kappaStart==kappaEnd) {
                // single run
                double alpha = alphaStart;
                int kappa = kappaStart;
                frf.findFRs(alpha, kappa);
            } else if (alphaStart!=alphaEnd && kappaStart==kappaEnd) {
                // scan alpha at fixed kappa
                int kappa = kappaStart;
                for (double a=alphaStart; a<=alphaEnd; a+=0.1000) {
                    frf.findFRs(a, kappa);
                }
            } else if (alphaStart==alphaEnd && kappaStart!=kappaEnd) {
                // scan kappa at fixed alpha
                double alpha = alphaStart;
                for (int k=kappaStart; k<=kappaEnd; k+=1) {
                    frf.findFRs(alpha, k);
                }
            } else {
                // scan both alpha and kappa
                for (double a=alphaStart; a<=alphaEnd; a+=0.1) {
                    for (int k=kappaStart; k<=kappaEnd; k+=1) {
                        frf.findFRs(a, k);
                    }
                }
            }
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
     * Print the path names and the count of subpaths for each FR.
     * This can be used as input to a classification routine.
     */
    void printPathFRs(String outputPrefix) throws IOException {
        PrintStream out = new PrintStream(getPathFRsFilename(outputPrefix));
        // columns are paths
        boolean first = true;
        for (PathWalk path : paths) {
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
            for (PathWalk path : paths) {
                out.print("\t"+fr.countSubpathsOf(path));
            }
            out.println("");
        }
        out.close();
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
    void printPathFRsSVM(String outputPrefix) throws IOException {
        PrintStream out = new PrintStream(getPathFRsSVMFilename(outputPrefix));
        // only rows, one per path
        for (PathWalk path : paths) {
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
        out.close();
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
    void printPathFRsARFF(String outputPrefix) throws IOException {
        PrintStream out = new PrintStream(getPathFRsARFFFilename(outputPrefix));
        out.println("@RELATION "+outputPrefix);
        out.println("");
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
        for (PathWalk path : paths) {
            out.print(path.getNameGenotype()+",");
            c = 0;
            for (FrequentedRegion fr : frequentedRegions) {
                c++;
                out.print(fr.countSubpathsOf(path)+",");
            }
            out.println(path.getLabel());
        }
        out.close();
    }

    /**
     * Print out the FRs.
     */
    void printFrequentedRegions(String outputPrefix) throws IOException {
        if (frequentedRegions.size()==0) {
            System.err.println("NO FREQUENTED REGIONS!");
            return;
        }
        PrintStream out = new PrintStream(getFRsFilename(outputPrefix));
        out.println(frequentedRegions.first().columnHeading());
        for (FrequentedRegion fr : frequentedRegions) {
            out.println(fr.toString());
        }
        out.close();
    }
    
    /**
     * Print out the FRs along with their subpaths, strictly to an output file.
     */
    void printFRSubpaths(String outputPrefix) throws IOException {
        if (frequentedRegions.size()==0) {
            System.err.println("NO FREQUENTED REGIONS!");
            return;
        }
        PrintStream out = new PrintStream(getFRSubpathsFilename(outputPrefix));
        for (FrequentedRegion fr : frequentedRegions) {
            out.println(fr.toString());
            out.println(fr.subpathsString());
        }
        out.close();
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
     * Print out the parameters.
     */
    public void printParameters(String outputPrefix) throws IOException {
        PrintStream out = new PrintStream(getParamsFilename(outputPrefix));
        String comments = "clocktime="+formatTime(clockTime);
        parameters.store(out, comments);
        out.close();
    }

    /**
     * Return alpha from a previous run.
     */
    static double readAlpha(String inputPrefix) throws FileNotFoundException, IOException {
        String paramsFile = getParamsFilename(inputPrefix);
        BufferedReader reader = new BufferedReader(new FileReader(paramsFile));
        String line = null;
        double alpha = 0;
        while ((line=reader.readLine())!=null) {
            if (line.startsWith("#alpha")) {
                String[] parts = line.split("=");
                alpha = Double.parseDouble(parts[1]);
            }
        }
        return alpha;
    }

    /**
     * Return kappa from a previous run.
     */
    static int readKappa(String inputPrefix) throws FileNotFoundException, IOException {
        String paramsFile = getParamsFilename(inputPrefix);
        BufferedReader reader = new BufferedReader(new FileReader(paramsFile));
        String line = null;
        int kappa = 0;
        while ((line=reader.readLine())!=null) {
            if (line.startsWith("#kappa")) {
                String[] parts = line.split("=");
                kappa = Integer.parseInt(parts[1]);
            }
        }
        return kappa;
    }
    
    /**
     * Read the parameters from a previous run.
     */
    void readParameters(String inputPrefix) throws FileNotFoundException, IOException {
        String paramsFilename = getParamsFilename(inputPrefix);
        BufferedReader reader = new BufferedReader(new FileReader(paramsFilename));
        parameters.load(reader);
        parameters.setProperty("paramsFile", paramsFilename);
        parameters.setProperty("inputPrefix", inputPrefix);
    }

    /**
     * Read FRs from the output from a previous run.
     * [18,34]	70	299	54	16
     * 509678.0.ctrl:[18,20,21,23,24,26,27,29,30,33,34]
     * 628863.1.case:[18,20,21,23,24,26,27,29,30,33,34]
     * etc.
     */
    void readFrequentedRegions() throws FileNotFoundException, IOException, NullNodeException, NullSequenceException, NoNodesException {
        // get alpha, kappa from the input prefix
        double alpha = readAlpha(getInputPrefix());
        int kappa = readKappa(getInputPrefix());
        // get the nodes from the nodes file
        File nodesFile = new File(getNodesFilename(getInputPrefix()));
        Map<Long,Node> nodeMap = PangenomicGraph.readNodes(nodesFile);
        // get the paths from the paths file
        File pathsFile = new File(getPathsFilename(getInputPrefix()));
        paths = PangenomicGraph.readPaths(pathsFile);
	// build the FRs
        frequentedRegions = new TreeSet<>();
        String frFilename = getFRSubpathsFilename(getInputPrefix());
        BufferedReader reader = new BufferedReader(new FileReader(frFilename));
        String line = null;
        while ((line=reader.readLine())!=null) {
            String[] fields = line.split("\t");
            NodeSet nodes = new NodeSet(nodeMap, fields[0]);
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
                    subNodes.add(nodeMap.get(nodeId));
                }
                // add to the subpaths
                subpaths.add(new PathWalk(subNodes, name, genotype, label));
            }
            FrequentedRegion fr = new FrequentedRegion(nodes, subpaths, alpha, kappa, support, avgLength);
            frequentedRegions.add(fr);
        }
    }

    /**
     * Form the FRs output filename
     */
    static String getFRsFilename(String prefix) {
        return prefix+".frs.txt";
    }

    /**
     * Form the FRSubpaths output filename
     */
    static String getFRSubpathsFilename(String prefix) {
        return prefix+".subpaths.txt";
    }

    /**
     * Form the pathFRs output filename
     */
    static String getPathFRsFilename(String prefix) {
        return prefix+".pathfrs.txt";
    }

    /**
     * Form the SVM version of the pathFRs output filename
     */
    static String getPathFRsSVMFilename(String prefix) {
        return prefix+".svm.txt";
    }

    /**
     * Form the ARFF version of the pathFRs output filename
     */
    static String getPathFRsARFFFilename(String prefix) {
        return prefix+".arff";
    }

    /**
     * Form the parameters output filename
     */
    static String getParamsFilename(String prefix) {
        return prefix+".params.txt";
    }

    /**
     * Form the graph nodes filename
     * if prefix = HTT.1k-1.0-0 then filename = HTT.nodes.txt
     */
    static String getNodesFilename(String prefix) {
        String[] parts = prefix.split("-");
        return parts[0]+".nodes.txt";
    }

    /**
     * Form the graph paths filename
     * if prefix = HTT.1k-1.0-0 then filename = HTT.paths.txt
     */
    static String getPathsFilename(String prefix) {
        String[] parts = prefix.split("-");
        return parts[0]+".paths.txt";
    }

    /**
     * Form an outputPrefix with given alpha and kappa.
     */
    String formOutputPrefix(double alpha, int kappa) {
        DecimalFormat af = new DecimalFormat("0.0");
        return getGraphName()+"-"+af.format(alpha)+"-"+kappa;
    }

    /**
     * Form an outputPrefix from inputPrefix, minSup, minSize, minLen.
     */
    String formOutputPrefix() {
        return getInputPrefix()+"-"+getMinSup()+"."+getMinSize()+"."+(int)getMinLen();
    }

    /**
     * Format a time duration given in milliseconds.
     */
    static String formatTime(long millis) {
        DecimalFormat tf = new DecimalFormat("00"); // hours, minutes, seconds
        long hours = (millis / 1000) / 60 / 60;
	long minutes = (millis / 1000 / 60) % 60;
        long seconds = (millis / 1000) % 60;
	return tf.format(hours)+":"+tf.format(minutes)+":"+tf.format(seconds);
    }
}
