package org.ncgr.pangenomics.genotype.fr;

import org.ncgr.pangenomics.genotype.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

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
    List<Path> paths;

    // parameters are stored in a Properties object
    Properties parameters = new Properties();

    // save filename suffixes
    static String FREQUENTED_REGIONS_SAVE = "save.frs.txt";
    static String ALL_FREQUENTED_REGIONS_SAVE = "save.allFrequentedRegions.txt";
    static String REJECTED_NODESETS_SAVE = "save.rejectedNodeSets.txt";
    static String ACCEPTED_FRPAIRS_SAVE = "save.acceptedFRPairs.txt";

    // the output FRs
    ConcurrentHashMap<String,FrequentedRegion> frequentedRegions;

    // the keepoption value, if there is one after the colon
    int keepOptionValue;
    
    // diagnostics
    long clockTime;
    PrintStream logOut;
    
    /**
     * Construct with a populated Graph and default parameters.
     */
    public FRFinder(PangenomicGraph graph) {
        initializeParameters();
        this.graph = graph;
        this.paths = graph.paths;
    }

    /**
     * Construct with the output from a previous run. Be sure to set minSup, minSize, minLen filters as needed before running postprocess().
     */
    public FRFinder(String inputPrefix) throws FileNotFoundException, IOException {
        initializeParameters();
        parameters = FRUtils.readParameters(inputPrefix); // sets properties from file
        readFrequentedRegions();
    }

    /**
     * Initialize the default parameters.
     */
    void initializeParameters() {
        parameters.setProperty("verbose", "false");
        parameters.setProperty("debug", "false");
        parameters.setProperty("resume", "false");
        parameters.setProperty("writeSaveFiles", "false");
        parameters.setProperty("minSup", "1");
        parameters.setProperty("minSize", "1");
        parameters.setProperty("minLen", "1.0");
        parameters.setProperty("maxRound", "0");
        parameters.setProperty("minPriority", "1");
        parameters.setProperty("requiredNodes", "[]");
        parameters.setProperty("excludedNodes", "[]");
        parameters.setProperty("priorityOption", "4");
        parameters.setProperty("keepOption", "null");
    }

    /**
     * Find the frequented regions in this Graph for the given alpha and kappa values.
     * double alpha = penetrance: the fraction of a supporting strain's sequence that actually supports the FR;
     *                alternatively, `1-alpha` is the fraction of inserted sequence
     * int kappa = maximum insertion: the maximum number of inserted nodes that a supporting path may have.
      */
    public void findFRs(double alpha, int kappa) throws FileNotFoundException, IOException {
        // initialize log file
        logOut = new PrintStream(formOutputPrefix(alpha, kappa)+".log");
	printToLog("# Starting findFRs: alpha="+alpha+" kappa="+kappa+
                   " priorityOption="+getPriorityOption()+" minPriority="+getMinPriority()+
                   " minSup="+getMinSup()+" minSize="+getMinSize()+" minLen="+getMinLen()+
                   " requiredNodes="+getRequiredNodes()+" excludedNodes="+getExcludedNodes()+" keepOption="+getKeepOption()+" maxRound="+getMaxRound());

        // store the saved FRs in a map
        frequentedRegions = new ConcurrentHashMap<>();

        // store the studied FRs in a ConcurrentHashMap for thread-safe ops
        ConcurrentHashMap<String,FrequentedRegion> allFrequentedRegions = new ConcurrentHashMap<>();

        // rejected NodeSets (strings), so we don't bother scanning them more than once
        ConcurrentSkipListSet<String> rejectedNodeSets = new ConcurrentSkipListSet<>();

        // accepted FRPairs so we don't merge them more than once
        ConcurrentHashMap<String,FRPair> acceptedFRPairs = new ConcurrentHashMap<>();

        // optional required and excluded nodes; must be final since used in the parallel stream loop
        final NodeSet requiredNodes = graph.getNodeSet(getRequiredNodes());
        final NodeSet excludedNodes = graph.getNodeSet(getExcludedNodes());

        // FR-finding round counter
        int round = 0;

        if (resume()) {
            // resume from a previous run
            printToLog("# Resuming from previous run");
            String line = null;
            // allFrequentedRegions
            BufferedReader sfrReader = new BufferedReader(new FileReader(getGraphName()+"."+ALL_FREQUENTED_REGIONS_SAVE));
            while ((line=sfrReader.readLine())!=null) {
                String[] parts = line.split("\t");
                NodeSet nodes = graph.getNodeSet(parts[0]);
                allFrequentedRegions.put(nodes.toString(), new FrequentedRegion(graph, nodes, alpha, kappa, getPriorityOption()));
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
                NodeSet nodes = graph.getNodeSet(parts[0]);
                frequentedRegions.put(nodes.toString(), new FrequentedRegion(graph, nodes, alpha, kappa, getPriorityOption()));
                round++; // increment round so we start where we left off
            }
	    // informativationalism
            printToLog("# Loaded "+allFrequentedRegions.size()+" allFrequentedRegions.");
	    printToLog("# Loaded "+rejectedNodeSets.size()+" rejectedNodeSets.");
            printToLog("# Loaded "+frequentedRegions.size()+" frequentedRegions.");
            printToLog("# Now continuing with FR finding...");
        } else {
            // load the single-node FRs into allFrequentedRegions
            ConcurrentSkipListSet<Node> nodeSet = new ConcurrentSkipListSet<>(graph.getNodes());
            nodeSet.parallelStream().forEach(node -> {
                    NodeSet c = new NodeSet();
                    c.add(node);
                    try {
                        FrequentedRegion fr = new FrequentedRegion(graph, c, alpha, kappa, getPriorityOption());
                        allFrequentedRegions.put(c.toString(), fr);
                    } catch (Exception e) {
                        System.err.println(e);
                        System.exit(1);
                    }
                });
            // add interesting single-node FRs in round 0, since we won't hit them in the loop
            for (FrequentedRegion fr : allFrequentedRegions.values()) {
                if (isInteresting(fr)) {
                    if (requiredNodes.size()==0) {
                        frequentedRegions.put(fr.nodes.toString(), fr);
                    } else {
                        for (Node n : requiredNodes) {
                            if (fr.nodes.contains(n)) {
                                frequentedRegions.put(fr.nodes.toString(), fr);
                            }
                        }
                    }
                }
            }
        }

        // add the required nodes to allFrequentedRegions
        FrequentedRegion requiredFR = new FrequentedRegion(graph, requiredNodes, alpha, kappa, getPriorityOption());
        allFrequentedRegions.put(requiredFR.nodes.toString(), requiredFR);
        if (isInteresting(requiredFR)) {
            frequentedRegions.put(requiredFR.nodes.toString(), requiredFR);
        }

        // dump out the pre-search FRs of interest, sorted by priority
        TreeSet<FrequentedRegion> sortedFRs = new TreeSet<>(frequentedRegions.values());
        for (FrequentedRegion fr : sortedFRs) {
            printToLog("0:"+fr.toString());
        }

        // build the FRs round by round
	long startTime = System.currentTimeMillis();
        int oldPriority = 0;
        boolean added = true;
        while (added && (round<getMaxRound() || getMaxRound()==0)) {
            round++;
            added = false;
            // store FRPairs in a map keyed by merged nodes in THIS round for parallel operation and sorting
            ConcurrentSkipListSet<FRPair> frpairSet = new ConcurrentSkipListSet<>();
            ////////////////////////////////////////////////////////////////////////////////////////////////
            // start parallel streams
            allFrequentedRegions.entrySet().parallelStream().forEach(entry1 -> {
                    FrequentedRegion fr1 = entry1.getValue();
                    if (debug()) System.out.println("fr1:"+fr1);
                    allFrequentedRegions.entrySet().parallelStream().forEach(entry2 -> {
                            FrequentedRegion fr2 = entry2.getValue();
                            if (fr2.nodes.compareTo(fr1.nodes)>0) {
                                FRPair frpair = new FRPair(fr1, fr2);
                                String nodesKey = frpair.nodes.toString();
                                boolean rejected = false;
                                if (frequentedRegions.containsKey(nodesKey)) {
                                    // if already found, bail
                                    rejected = true;
                                } else if (acceptedFRPairs.containsKey(nodesKey)) {
                                    // get stored FRPair since already accepted and merged in a previous round
                                    frpair = acceptedFRPairs.get(nodesKey);
                                } else if (rejectedNodeSets.contains(nodesKey)) {
                                    // already rejected, bail
                                    rejected = true;
                                }
				// reject if not all the required nodes are present
                                if (!rejected && requiredNodes.size()>0) {
                                    for (Node n : requiredNodes) {
                                        if (!frpair.nodes.contains(n)) {
                                            rejected = true; // lacking a required node
                                            break;
                                        }
                                    }
                                }
				// reject if one of the excluded nodes is present
                                if (!rejected && excludedNodes.size()>0) {
                                    for (Node n : excludedNodes) {
                                        if (frpair.nodes.contains(n)) {
                                            rejected = true; // containing an excluded node
                                            break;
                                        }
                                    }
                                }
                                if (rejected) {
                                    // add this rejected NodeSet to the rejected list
                                    rejectedNodeSets.add(nodesKey);
                                } else {
                                    // merge this FRPair if not already merged
                                    if (frpair.merged==null) {
                                        try {
                                            // have to catch Exceptions here since in a parallel stream
                                            if (frpair.merged==null) frpair.merge();
                                        } catch (Exception e) {
                                            System.err.println(e);
                                            System.exit(1);
                                        }
                                    }
                                    // should we keep this merged FR according to keepOption?
                                    boolean keep = true; // default if keepOption not set
                                    if (getKeepOption().startsWith("subset") && frpair.merged.nodes.size()>=keepOptionValue) {
                                        // keep FRs that are subsets of others or have higher priority
                                        for (FrequentedRegion frOld : frequentedRegions.values()) {
                                            if (frpair.merged.nodes.isSupersetOf(frOld.nodes) && frpair.merged.priority<=frOld.priority) {
                                                keep = false;
                                                break;
                                            }
                                        }
                                    } else if (getKeepOption().startsWith("distance")) {
                                        // keep FRs that are at least a distance of keepOptionValue away from others or have higher priority
                                        for (FrequentedRegion frOld : frequentedRegions.values()) {
                                            if (frpair.merged.nodes.distanceFrom(frOld.nodes)<keepOptionValue && frpair.merged.priority<=frOld.priority) {
                                                keep = false;
                                                break;
                                            }
                                        }
                                    }
                                    if (keep) {
                                        // add this candidate merged pair
                                        acceptedFRPairs.put(nodesKey, frpair);
                                        frpairSet.add(frpair);
                                    } else {
                                        // add this to the rejected list
                                        rejectedNodeSets.add(nodesKey);
                                    }
                                }
                            }
                        });
                });
            // end parallelStreams
            ////////////////////////////////////////////////////////////////////////////////////////////////
            // add our new best merged FR
            if (frpairSet.size()>0) {
                FrequentedRegion fr = frpairSet.last().merged;
                // we need to have the minimum support and priority, etc.
                added = isInteresting(fr);
                if (added) {
                    // add this FR to the mergeable FRs map
                    allFrequentedRegions.put(fr.nodes.toString(), fr);
                    frequentedRegions.put(fr.nodes.toString(), fr);
                    oldPriority = fr.priority;
                    // output this FR
                    printToLog(round+":"+fr);
                } else {
                    // show the top remaining FR that wasn't added
                    printToLog("-------------------------------------------------------------------------------------------------------");
                    printToLog("TR:"+fr);
                }
            }
            // output current state for continuation if aborted
            if (frequentedRegions.size()>0 && writeSaveFiles()) {
                // params with current clock time
                clockTime = System.currentTimeMillis() - startTime;
                FRUtils.printParameters(parameters, getGraphName()+".save", alpha, kappa, clockTime);
                // allFrequentedRegions
                PrintStream sfrOut = new PrintStream(getGraphName()+"."+ALL_FREQUENTED_REGIONS_SAVE);
                for (FrequentedRegion fr : allFrequentedRegions.values()) {
                    sfrOut.println(fr.toString());
                }
                sfrOut.close();
                // acceptedFRPairs
                PrintStream afrpOut = new PrintStream(getGraphName()+"."+ACCEPTED_FRPAIRS_SAVE);
                for (FRPair frpair : acceptedFRPairs.values()) {
                    afrpOut.println(frpair);
                }
                // rejectedNodeSets
                PrintStream rnsOut = new PrintStream(getGraphName()+"."+REJECTED_NODESETS_SAVE);
                for (String nodesKey : rejectedNodeSets) {
                    rnsOut.println(nodesKey);
                }
                rnsOut.close();
                // frequentedRegions
                PrintStream frOut = new PrintStream(getGraphName()+"."+FREQUENTED_REGIONS_SAVE);
                boolean first = true;
                for (FrequentedRegion fr : frequentedRegions.values()) {
                    if (first) {
                        frOut.println(fr.columnHeading()); // header
                        first = false;
                    }
                    frOut.println(fr.toString());
                }
                frOut.close();
            }
        }

        // timing
	clockTime = System.currentTimeMillis() - startTime;
        System.out.println("Found "+frequentedRegions.size()+" FRs.");
	System.out.println("Clock time: "+FRUtils.formatTime(clockTime));
        
	// final output
	if (frequentedRegions.size()>0) {
            FRUtils.printParameters(parameters, formOutputPrefix(alpha, kappa), alpha, kappa, clockTime);
            printFrequentedRegions(formOutputPrefix(alpha, kappa));
            printFRSubpaths(formOutputPrefix(alpha, kappa));
            printPathFRs(formOutputPrefix(alpha, kappa));
	}
    }

    /**
     * Post-process a set of FRs for given minSup, minLen and minSize.
     */
    public void postprocess() throws FileNotFoundException, IOException {
        ConcurrentHashMap<String,FrequentedRegion> filteredFRs = new ConcurrentHashMap<>();
        for (FrequentedRegion fr : frequentedRegions.values()) {
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
            if (passes) filteredFRs.put(fr.nodes.toString(), fr);
            if (verbose()) System.out.println(fr.toString()+reason);
        }
        if (verbose()) System.out.println(filteredFRs.size()+" FRs passed minSup="+getMinSup()+", minSize="+getMinSize()+", minLen="+getMinLen());
	// output the filtered FRs and SVM data
        frequentedRegions = filteredFRs;
	if (frequentedRegions.size()>0) {
	    printFrequentedRegions(formOutputPrefix());
	    printPathFRsSVM(formOutputPrefix());
            printPathFRsARFF(formOutputPrefix());
	}
    }

    // parameter getters
    public boolean verbose() {
        return Boolean.parseBoolean(parameters.getProperty("verbose"));
    }
    public boolean debug() {
        return Boolean.parseBoolean(parameters.getProperty("debug"));
    }
    public boolean writeSaveFiles() {
        return Boolean.parseBoolean(parameters.getProperty("writeSaveFiles"));
    }
    public boolean resume() {
        return Boolean.parseBoolean(parameters.getProperty("resume"));
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
    public String getPriorityOption() {
        return parameters.getProperty("priorityOption");
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
    public int getMinPriority() {
        return Integer.parseInt(parameters.getProperty("minPriority"));
    }
    public String getRequiredNodes() {
        return parameters.getProperty("requiredNodes");
    }
    public String getExcludedNodes() {
        return parameters.getProperty("excludedNodes");
    }
    public String getKeepOption() {
        return parameters.getProperty("keepOption");
    }
    
    // parameter setters
    public void setVerbose() {
        parameters.setProperty("verbose", "true");
    }
    public void setDebug() {
        parameters.setProperty("debug", "true");
    }
    public void setPriorityOption(String priorityOption) {
        parameters.setProperty("priorityOption", priorityOption);
    }
    public void setResume() {
        parameters.setProperty("resume", "true");
    }
    public void setWriteSaveFiles() {
        parameters.setProperty("writeSaveFiles", "true");
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
    public void setMinPriority(int minPriority) {
        parameters.setProperty("minPriority", String.valueOf(minPriority));
    }
    public void setRequiredNodes(String requiredNodes) {
        parameters.setProperty("requiredNodes", requiredNodes);
    }
    public void setExcludedNodes(String excludedNodes) {
        parameters.setProperty("excludedNodes", excludedNodes);
    }
    public void setKeepOption(String keepOption) {
        parameters.setProperty("keepOption", keepOption);
        if (keepOption.startsWith("subset") || keepOption.startsWith("distance")) {
            String[] parts = keepOption.split(":");
            if (parts.length==2) {
                keepOptionValue = Integer.parseInt(parts[1]);
            } else if (keepOption.equals("subset")) {
                keepOptionValue = 3; // default
            } else if (keepOption.equals("distance")) {
                keepOptionValue = 2; // default
            }
        } else {
            System.err.println("ERROR: allowed keepoption values are: subset[:N]|distance[:N]");
            System.exit(1);
        }
    }
    public void setGraphName(String graphName) {
        parameters.setProperty("graphName", graphName);
    }

    /**
     * Command-line utility
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
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
        Option kappaStartOption = new Option("ks", "kappastart", true, "starting value of kappa for a scan (can equal kappaend; -1=infinity)");
        kappaStartOption.setRequired(false);
        options.addOption(kappaStartOption);
        //
        Option kappaEndOption = new Option("ke", "kappaend", true, "ending value of kappa for a scan (can equal kappastart; -1=infinity)");
        kappaEndOption.setRequired(false);
        options.addOption(kappaEndOption);
        //
        Option graphOption = new Option("graph", "graph", true, "graph name");
        graphOption.setRequired(true);
        options.addOption(graphOption);
        //
        Option txtOption = new Option("txt", "txt", false, "load from [graph].nodes.txt and [graph].paths.txt");
        txtOption.setRequired(false);
        options.addOption(txtOption);
        //
        Option minSupOption = new Option("m", "minsup", true, "minimum number of supporting paths for a region to be considered interesting [1]");
        minSupOption.setRequired(false);
        options.addOption(minSupOption);
        //
        Option minSizeOption = new Option("s", "minsize", true, "minimum number of nodes that a FR must contain to be considered interesting [1]");
        minSizeOption.setRequired(false);
        options.addOption(minSizeOption);
        //
        Option inputprefixOption = new Option("i", "inputprefix", true, "input file prefix for post-processing");
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
        Option priorityOptionOption = new Option("pri", "priorityoption", true, "option for priority weighting of FRs: "+FrequentedRegion.PRIORITY_OPTIONS);
        priorityOptionOption.setRequired(true);
        options.addOption(priorityOptionOption);
        //
        Option resumeOption = new Option("r", "resume", false, "resume from a previous run [false]");
        resumeOption.setRequired(false);
        options.addOption(resumeOption);
        //
        Option maxRoundOption = new Option("mr", "maxround", true, "maximum FR-finding round to run [0=unlimited]");
        maxRoundOption.setRequired(false);
        options.addOption(maxRoundOption);
        //
        Option minPriorityOption = new Option("mp", "minpriority", true, "minimum priority for saving an FR [1]");
        minPriorityOption.setRequired(false);
        options.addOption(minPriorityOption);
        //
        Option skipNodePathsOption = new Option("snp", "skipnodepaths", false, "skip building list of paths per node [false]");
        skipNodePathsOption.setRequired(false);
        options.addOption(skipNodePathsOption);
        //
        Option writeSaveFilesOption = new Option("wsf", "writesavefiles", false, "write save files after each FR is found [false]");
        writeSaveFilesOption.setRequired(false);
        options.addOption(writeSaveFilesOption);
        //
        Option requiredNodesOption = new Option("rn", "requirednodes", true, "require that found FRs contain the given nodes []");
        requiredNodesOption.setRequired(false);
        options.addOption(requiredNodesOption);
        //
        Option excludedNodesOption = new Option("en", "excludednodes", true, "require that found FRs NOT contain the given nodes []");
        excludedNodesOption.setRequired(false);
        options.addOption(excludedNodesOption);
        //
        Option keepOptionOption = new Option("keep", "keepoption", true, "option for keeping FRs in finder loop: subset[:N]|distance[:N] [keep all]");
        keepOptionOption.setRequired(false);
        options.addOption(keepOptionOption);

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
        if (kappaStart==-1 || kappaEnd==-1) {
            kappaStart = Integer.MAX_VALUE; // effectively infinity
            kappaEnd = Integer.MAX_VALUE;
        }
        
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
            if (cmd.hasOption("writesavefiles")) frf.setWriteSaveFiles();
            frf.postprocess();
        } else {
            String graphName = cmd.getOptionValue("graph");
            // load graph from TXT file
            PangenomicGraph pg = new PangenomicGraph();
            pg.name = graphName;
            pg.nodesFile = new File(graphName+".nodes.txt");
            pg.pathsFile = new File(graphName+".paths.txt");
            pg.loadTXT();
            System.out.println("# Graph has "+pg.vertexSet().size()+" nodes and "+pg.edgeSet().size()+" edges with "+pg.paths.size()+" paths.");
            pg.tallyLabelCounts();
            System.out.println("# Graph has "+pg.getLabelCounts().get("case")+" case paths and "+pg.getLabelCounts().get("ctrl")+" ctrl paths.");
            // set graph options
            if (cmd.hasOption("verbose")) pg.verbose = true;
            // if (cmd.hasOption("skipnodepaths")) pg.setSkipNodePaths();
            // instantiate the FRFinder with this PangenomicGraph
            FRFinder frf = new FRFinder(pg);
            frf.setGraphName(graphName);
            if (cmd.hasOption("priorityoption")) {
                frf.setPriorityOption(cmd.getOptionValue("priorityoption"));
            }
            // set optional FRFinder parameters
            if (cmd.hasOption("minsup")) frf.setMinSup(Integer.parseInt(cmd.getOptionValue("minsup")));
            if (cmd.hasOption("minsize")) frf.setMinSize(Integer.parseInt(cmd.getOptionValue("minsize")));
            if (cmd.hasOption("minlen")) frf.setMinLen(Double.parseDouble(cmd.getOptionValue("minlen")));
            if (cmd.hasOption("maxround")) frf.setMaxRound(Integer.parseInt(cmd.getOptionValue("maxround")));
            if (cmd.hasOption("minpriority")) frf.setMinPriority(Integer.parseInt(cmd.getOptionValue("minpriority")));
            if (cmd.hasOption("requirednodes")) frf.setRequiredNodes(cmd.getOptionValue("requirednodes"));
            if (cmd.hasOption("excludednodes")) frf.setExcludedNodes(cmd.getOptionValue("excludednodes"));
            if (cmd.hasOption("keepoption")) frf.setKeepOption(cmd.getOptionValue("keepoption"));
            if (cmd.hasOption("verbose")) frf.setVerbose();
            if (cmd.hasOption("debug")) frf.setDebug();
            if (cmd.hasOption("resume")) frf.setResume();
            if (cmd.hasOption("writesavefiles")) frf.setWriteSaveFiles();
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
     * Print to both the console and the log file
     */
    void printToLog(String text) {
        System.out.println(text);
        logOut.println(text);
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
        PrintStream out = new PrintStream(FRUtils.getPathFRsFilename(outputPrefix));
        // columns are paths
        boolean first = true;
        for (Path path : paths) {
            if (first) {
                first = false;
            } else {
                out.print("\t");
            }
            out.print(path.name+"."+path.label);
        }
        out.println("");
        // rows are FRs
        int c = 1;
        for (FrequentedRegion fr : frequentedRegions.values()) {
            out.print("FR"+(c++));
            for (Path path : paths) {
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
        PrintStream out = new PrintStream(FRUtils.getPathFRsSVMFilename(outputPrefix));
        // only rows, one per path
        for (Path path : paths) {
            out.print(path.name+"."+path.label);
            // TODO: update these to strings along with fixing the SVM code to handle strings
            String group = "";
            if (path.label!=null) group = path.label;
            out.print("\t"+group);
            int c = 0;
            for (FrequentedRegion fr : frequentedRegions.values()) {
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
        PrintStream out = new PrintStream(FRUtils.getPathFRsARFFFilename(outputPrefix));
        out.println("@RELATION "+outputPrefix);
        out.println("");
        // attributes: path ID
        out.println("@ATTRIBUTE ID STRING");
        // attributes: each FR is a numeric labeled FRn
        int c = 0;
        for (FrequentedRegion fr : frequentedRegions.values()) {
            c++;
            String frLabel = "FR"+c;
            out.println("@ATTRIBUTE "+frLabel+" NUMERIC");
        }
        // add the class attribute
        out.println("@ATTRIBUTE class {case,ctrl}");
        out.println("");
        // data
        out.println("@DATA");
        for (Path path : paths) {
            out.print(path.name+"."+path.label);
            c = 0;
            for (FrequentedRegion fr : frequentedRegions.values()) {
                c++;
                out.print(fr.countSubpathsOf(path)+",");
            }
            out.println(path.label);
        }
        out.close();
    }

    /**
     * Print out the FRs, in order of priority.
     */
    void printFrequentedRegions(String outputPrefix) throws IOException {
        if (frequentedRegions.size()==0) {
            System.err.println("NO FREQUENTED REGIONS!");
            return;
        }
        PrintStream out = new PrintStream(FRUtils.getFRsFilename(outputPrefix));
        boolean first = true;
        TreeSet<FrequentedRegion> sortedFRs = new TreeSet<>(frequentedRegions.values());
        for (FrequentedRegion fr : sortedFRs) {
            if (first) {
                out.println(fr.columnHeading());
                first = false;
            }
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
        PrintStream out = new PrintStream(FRUtils.getFRSubpathsFilename(outputPrefix));
        TreeSet<FrequentedRegion> sortedFRs = new TreeSet<>(frequentedRegions.values());
        for (FrequentedRegion fr : sortedFRs) {
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
        for (FrequentedRegion fr : frequentedRegions.values()) {
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
     * Read FRs from the output from a previous run.
     * [18,34]	70	299	54	16
     * 509678.0.ctrl:[18,20,21,23,24,26,27,29,30,33,34]
     * 628863.1.case:[18,20,21,23,24,26,27,29,30,33,34]
     * etc.
     */
    void readFrequentedRegions() throws FileNotFoundException, IOException {
        // get alpha, kappa from the input prefix
        double alpha = FRUtils.readAlpha(getInputPrefix());
        int kappa = FRUtils.readKappa(getInputPrefix());
        // get the graph from the nodes and paths files
        graph = new PangenomicGraph();
        graph.nodesFile = new File(FRUtils.getNodesFilename(getInputPrefix()));
        graph.pathsFile = new File(FRUtils.getPathsFilename(getInputPrefix()));
        graph.loadTXT();
        // create a node map for building subpaths
        Map<Long,Node> nodeMap = new HashMap<>();
        for (Node n : graph.getNodes()) {
            nodeMap.put(n.id, n);
        }
	// build the FRs
        frequentedRegions = new ConcurrentHashMap<>();
        String frFilename = FRUtils.getFRSubpathsFilename(getInputPrefix());
        BufferedReader reader = new BufferedReader(new FileReader(frFilename));
        String line = null;
        while ((line=reader.readLine())!=null) {
            String[] fields = line.split("\t");
            NodeSet nodes = graph.getNodeSet(fields[0]);
            int support = Integer.parseInt(fields[1]);
            List<Path> subpaths = new ArrayList<>();
            for (int i=0; i<support; i++) {
                line = reader.readLine();
                String[] parts = line.split(":");
                String pathFull = parts[0];
                String nodeString = parts[1];
                // split out the name, label, nodes
                String[] nameParts = pathFull.split("\\.");
                String name = nameParts[0];
                String label = label = nameParts[2];
                List<Node> subNodes = new ArrayList<>();
                String[] nodesAsStrings = nodeString.replace("[","").replace("]","").split(",");
                for (String nodeAsString : nodesAsStrings) {
		    long nodeId = Long.parseLong(nodeAsString);
                    subNodes.add(nodeMap.get(nodeId));
                }
                // add to the subpaths
                subpaths.add(new Path(graph, subNodes, name, label));
            }
            FrequentedRegion fr = new FrequentedRegion(nodes, subpaths, alpha, kappa, getPriorityOption(), support);
            frequentedRegions.put(fr.nodes.toString(), fr);
        }
    }


    /**
     * Form an outputPrefix with given alpha and kappa.
     */
    String formOutputPrefix(double alpha, int kappa) {
        DecimalFormat af = new DecimalFormat("0.0");
        if (kappa==Integer.MAX_VALUE) {
            return getGraphName()+"-"+af.format(alpha)+"-Inf";
        } else {
            return getGraphName()+"-"+af.format(alpha)+"-"+kappa;
        }
    }

    /**
     * Form an outputPrefix from inputPrefix, minSup, minSize, minLen.
     */
    String formOutputPrefix() {
        return getInputPrefix()+"-"+getMinSup()+"."+getMinSize()+"."+(int)getMinLen();
    }

    /**
     * Return true if the given FR is "interesting". (Note that the alpha, kappa requirements are enforced by fr.support>=minSup.)
     */
    boolean isInteresting(FrequentedRegion fr) {
        return fr.support>=getMinSup() && fr.nodes.size()>=getMinSize() && fr.priority>=getMinPriority();
    }
}
