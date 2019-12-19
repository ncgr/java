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
import org.ncgr.jgraph.TXTImporter;

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
    List<PathWalk> paths;

    // parameters are stored in a Properties object
    Properties parameters = new Properties();

    // save filename suffixes
    String FREQUENTED_REGIONS_SAVE = "save.frs.txt";
    String ALL_FREQUENTED_REGIONS_SAVE = "save.allFrequentedRegions.txt";
    String REJECTED_NODESETS_SAVE = "save.rejectedNodeSets.txt";

    // the output FRs
    ConcurrentHashMap<String,FrequentedRegion> frequentedRegions;

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
    public FRFinder(String inputPrefix) throws FileNotFoundException, IOException,
                                               NullNodeException, NullSequenceException, NoNodesException {
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
        parameters.setProperty("minPriority", "0");
        parameters.setProperty("requiredNode", "0");
        parameters.setProperty("priorityOption", "0");
        parameters.setProperty("frKeepOption", "");
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

	System.out.println("# Starting findFRs: alpha="+alpha+" kappa="+kappa+
                           " priorityOption="+getPriorityOption()+" minPriority="+getMinPriority()+
                           " minSup="+getMinSup()+" minSize="+getMinSize()+" minLen="+getMinLen()+
                           " requiredNode="+getRequiredNodeId()+" frKeepOption="+getFRKeepOption());

        // output the graph files if graph loaded from GFA
        if (getGfaFilename()!=null) graph.printAll(getGraphName());

        // store the saved FRs in a map
        frequentedRegions = new ConcurrentHashMap<>();

        // store the studied FRs in a ConcurrentHashMap for thread-safe ops
        ConcurrentHashMap<String,FrequentedRegion> allFrequentedRegions = new ConcurrentHashMap<>();

        // rejected NodeSets (strings), so we don't bother scanning them more than once
        ConcurrentSkipListSet<String> rejectedNodeSets = new ConcurrentSkipListSet<>();

        // accepted FRPairs so we don't merge them more than once
        ConcurrentHashMap<String,FRPair> acceptedFRPairs = new ConcurrentHashMap<>();

        // optional required node, null if not set; must be final since used in the parallel stream loop
        final Node requiredNode = graph.getNode(getRequiredNodeId());

        // FR-finding round counter
        int round = 0;

        if (getResume()) {
            // resume from a previous run
            System.out.println("# Resuming from previous run");
            String line = null;
            // allFrequentedRegions
            BufferedReader sfrReader = new BufferedReader(new FileReader(getGraphName()+"."+ALL_FREQUENTED_REGIONS_SAVE));
            while ((line=sfrReader.readLine())!=null) {
                String[] parts = line.split("\t");
                NodeSet nodes = new NodeSet(graph, parts[0]);
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
                NodeSet nodes = new NodeSet(graph, parts[0]);
                frequentedRegions.put(nodes.toString(), new FrequentedRegion(graph, nodes, alpha, kappa, getPriorityOption()));
                round++;
            }
	    // informativationalism
            System.out.println("# Loaded "+allFrequentedRegions.size()+" allFrequentedRegions.");
	    System.out.println("# Loaded "+rejectedNodeSets.size()+" rejectedNodeSets.");
            System.out.println("# Loaded "+frequentedRegions.size()+" frequentedRegions.");
            System.out.println("# Now continuing with FR finding...");
        } else {
            // load the single-node FRs
            for (Node node : graph.getNodes()) {
                NodeSet c = new NodeSet();
                c.add(node);
                FrequentedRegion fr = new FrequentedRegion(graph, c, alpha, kappa, getPriorityOption());
                allFrequentedRegions.put(c.toString(), fr);
            }
        }

        // build the FRs round by round
	long startTime = System.currentTimeMillis();
        boolean added = true;
        while (added && (round<getMaxRound() || getMaxRound()==0)) {
            round++;
            added = false;
            // store FRPairs in a map keyed by merged nodes in THIS round for parallel operation and sorting
            ConcurrentHashMap<String,FRPair> frpairMap = new ConcurrentHashMap<>();
            ////////////////////////////////////////////////////////////////////////////////////////////////
            // start parallel streams
            allFrequentedRegions.entrySet().parallelStream().forEach(entry1 -> {
                    FrequentedRegion fr1 = entry1.getValue();
                    allFrequentedRegions.entrySet().parallelStream().forEach(entry2 -> {
                            FrequentedRegion fr2 = entry2.getValue();
                            if (fr2.nodes.compareTo(fr1.nodes)>0) {
                                FRPair frpair = new FRPair(fr1, fr2);
                                String nodesKey = frpair.nodes.toString();
                                if (rejectedNodeSets.contains(nodesKey)) {
                                    // do nothing, rejected
                                } else if (frequentedRegions.containsKey(nodesKey)) {
                                    // do nothing, already found
                                } else if (requiredNode!=null && !frpair.nodes.contains(requiredNode)) {
                                    // add to rejected set
                                    rejectedNodeSets.add(nodesKey);
                                } else {
                                    if (acceptedFRPairs.containsKey(nodesKey)) {
                                        // get stored FRPair since already merged in a previous round
                                        frpair = acceptedFRPairs.get(nodesKey);
                                    } else {
                                        // compute alpha-rejection before merging
                                        frpair.computeAlphaRejection();
                                        if (frpair.alphaReject) {
                                            // add to rejected set
                                            rejectedNodeSets.add(nodesKey);                                
                                        } else {
                                            // merge this pair
                                            try {
                                                // have to catch Exceptions here since in a parallel stream
                                                if (frpair.merged==null) frpair.merge();
                                            } catch (Exception e) {
                                                System.err.println(e);
                                                System.exit(1);
                                            }
                                        }
                                    }
                                    if (frpair.merged!=null) {
                                        // should we keep this merged FR?
                                        boolean keep = true;
                                        if (getFRKeepOption().equals("superset")) {
                                            for (FrequentedRegion frOld : frequentedRegions.values()) {
                                                if (frpair.merged.nodes.isSupersetOf(frOld.nodes) && frpair.merged.priority<=frOld.priority) {
                                                    keep = false;
                                                    rejectedNodeSets.add(nodesKey);
                                                    break;
                                                }
                                            }
                                        } else if (getFRKeepOption().equals("distance")) {
                                            for (FrequentedRegion frOld : frequentedRegions.values()) {
                                                if (frpair.merged.nodes.distanceFrom(frOld.nodes)<2 && frpair.merged.priority<=frOld.priority) {
                                                    keep = false;
                                                    rejectedNodeSets.add(nodesKey);
                                                    break;
                                                }
                                            }
                                        }
                                        if (keep) {
                                            // add this candidate merged pair
                                            acceptedFRPairs.put(nodesKey, frpair);
                                            frpairMap.put(nodesKey, frpair);
                                        }
                                    }
                                }
                            }
                        });
                });
            // end parallelStreams
            ////////////////////////////////////////////////////////////////////////////////////////////////
            // add our new best merged FR
            if (frpairMap.size()>0) {
                TreeSet<FRPair> frpairSet = new TreeSet<>(frpairMap.values());
                FRPair frpair = frpairSet.last();
                FrequentedRegion fr = frpair.merged;
                // we need to have the minimum support and priority, etc.
                added = fr.support>=getMinSup() && fr.avgLength>=getMinLen() && fr.nodes.size()>=getMinSize() && fr.priority>=getMinPriority();
                if (added) {
                    // add this FR to the mergeable FRs map
                    allFrequentedRegions.put(fr.nodes.toString(), fr);
                    frequentedRegions.put(fr.nodes.toString(), fr);
                    System.out.println(round+":"+fr);
                }
            }
            // output current state for continuation if aborted
            if (frequentedRegions.size()>0 && !getSkipSaveFiles()) {
                // params with current clock time
                clockTime = System.currentTimeMillis() - startTime;
                printParameters(getGraphName()+".save", alpha, kappa);
                // allFrequentedRegions
                PrintStream sfrOut = new PrintStream(getGraphName()+"."+ALL_FREQUENTED_REGIONS_SAVE);
                for (FrequentedRegion fr : allFrequentedRegions.values()) {
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
	System.out.println("Clock time: "+formatTime(clockTime));
        
	// final output
	if (frequentedRegions.size()>0) {
            printParameters(formOutputPrefix(alpha, kappa), alpha, kappa);
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
            if (fr.avgLength<getMinLen()) {
                passes = false;
                reason += " avgLength";
            } else {
                reason += " AVGLENGTH";
            }
            if (passes) filteredFRs.put(fr.nodes.toString(), fr);
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
    public long getRequiredNodeId() {
        return Long.parseLong(parameters.getProperty("requiredNode"));
    }
    public String getFRKeepOption() {
        return parameters.getProperty("frKeepOption");
    }
    public String getGfaFilename() {
        return parameters.getProperty("gfaFile");
    }
    public String getTxtFilename() {
        return parameters.getProperty("txtFile");
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
    public void setMinPriority(int minPriority) {
        parameters.setProperty("minPriority", String.valueOf(minPriority));
    }
    public void setRequiredNodeId(long requiredNode) {
        parameters.setProperty("requiredNode", String.valueOf(requiredNode));
    }
    public void setFRKeepOption(String frKeepOption) {
        parameters.setProperty("frKeepOption", frKeepOption);
    }
    public void setGraphName(String graphName) {
        parameters.setProperty("graphName", graphName);
    }
    public void setGfaFilename(String gfaFilename) {
        parameters.setProperty("gfaFile", gfaFilename);
    }
    public void setTxtFilename(String txtFilename) {
        parameters.setProperty("txtFile", txtFilename);
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
        Option minPriorityOption = new Option("mp", "minpriority", true, "minimum priority for saving an FR [0]");
        minPriorityOption.setRequired(false);
        options.addOption(minPriorityOption);
        //
        Option prunedGraphOption = new Option("pr", "prunedgraph", false, "prune graph -- remove all common nodes [false]");
        prunedGraphOption.setRequired(false);
        options.addOption(prunedGraphOption);
        //
        Option skipNodePathsOption = new Option("snp", "skipnodepaths", false, "skip building list of paths per node [false]");
        skipNodePathsOption.setRequired(false);
        options.addOption(skipNodePathsOption);
        //
        Option skipSaveFilesOption = new Option("ssf", "skipsavefiles", false, "skip saving files after each FR is found, to save time [false]");
        skipSaveFilesOption.setRequired(false);
        options.addOption(skipSaveFilesOption);
        //
        Option requiredNodeOption = new Option("rn", "requirednode", true, "require that found FRs contain the given node [0]");
        requiredNodeOption.setRequired(false);
        options.addOption(requiredNodeOption);
        //
        Option frKeepOptionOption = new Option("keep", "frkeepoption", true, "option for keeping FRs in finder loop: superset|distance [neither]");
        frKeepOptionOption.setRequired(false);
        options.addOption(frKeepOptionOption);

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
        if (!cmd.hasOption("inputprefix") && !cmd.hasOption("gfa") && !cmd.hasOption("txt")) {
            System.err.println("You must specify loading from a vg GFA file (--gfa) or TXT files (--txt), or provide input prefix for post-processing (--inputPrefix)");
            System.exit(1);
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
            // import a PangenomicGraph from a GFA file or pair of TXT files
            PangenomicGraph pg = new PangenomicGraph();
            if (cmd.hasOption("verbose")) pg.setVerbose();
            // graph name
            String graphName = cmd.getOptionValue("graph");
            if (cmd.hasOption("gfa")) {
                // GFA file
                File gfaFile = new File(graphName+".paths.gfa");
                pg.importGFA(gfaFile);
                System.out.println("# Graph has "+pg.vertexSet().size()+" nodes and "+pg.edgeSet().size()+" edges with "+pg.getPaths().size()+" paths.");
                // if a labels file is given, add them to the paths
                if (labelsFile!=null) {
                    pg.readPathLabels(labelsFile);
                    pg.tallyLabelCounts();
                    System.out.println("# Graph has "+pg.getLabelCounts().get("case")+" case paths and "+pg.getLabelCounts().get("ctrl")+" ctrl paths.");
                }
            } else if (cmd.hasOption("txt")) {
                // TXT file
                File nodesFile = new File(graphName+".nodes.txt");
                File pathsFile = new File(graphName+".paths.txt");
                pg.importTXT(nodesFile, pathsFile);
                System.out.println("# Graph has "+pg.vertexSet().size()+" nodes and "+pg.edgeSet().size()+" edges with "+pg.getPaths().size()+" paths.");
                pg.tallyLabelCounts();
                System.out.println("# Graph has "+pg.getLabelCounts().get("case")+" case paths and "+pg.getLabelCounts().get("ctrl")+" ctrl paths.");
            }
            // set graph options
            if (cmd.hasOption("verbose")) pg.setVerbose();
            if (cmd.hasOption("genotype")) pg.setGenotype(Integer.parseInt(cmd.getOptionValue("genotype")));
            if (cmd.hasOption("skipnodepaths")) pg.setSkipNodePaths();
	    // prune the graph if so commanded
	    if (cmd.hasOption("prunedgraph")) {
		int nRemoved = pg.prune();
		System.out.println("# Graph has been pruned ("+nRemoved+" fully common nodes removed).");
	    }
            // instantiate the FRFinder with this PangenomicGraph
            FRFinder frf = new FRFinder(pg);
            frf.setGraphName(graphName);
            if (cmd.hasOption("gfa")) frf.setGfaFilename(graphName+".paths.gfa");
            if (cmd.hasOption("txt")) frf.setTxtFilename(graphName+".nodes.txt");
            if (cmd.hasOption("priorityoption")) {
                frf.setPriorityOption(cmd.getOptionValue("priorityoption"));
            }
            // set optional FRFinder parameters
            if (cmd.hasOption("minsup")) frf.setMinSup(Integer.parseInt(cmd.getOptionValue("minsup")));
            if (cmd.hasOption("minsize")) frf.setMinSize(Integer.parseInt(cmd.getOptionValue("minsize")));
            if (cmd.hasOption("minlen")) frf.setMinLen(Double.parseDouble(cmd.getOptionValue("minlen")));
            if (cmd.hasOption("maxround")) frf.setMaxRound(Integer.parseInt(cmd.getOptionValue("maxround")));
            if (cmd.hasOption("minpriority")) frf.setMinPriority(Integer.parseInt(cmd.getOptionValue("minpriority")));
            if (cmd.hasOption("requirednode")) frf.setRequiredNodeId(Long.parseLong(cmd.getOptionValue("requirednode")));
            if (cmd.hasOption("frkeepoption")) frf.setFRKeepOption(cmd.getOptionValue("frkeepoption"));
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
        for (FrequentedRegion fr : frequentedRegions.values()) {
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
        PrintStream out = new PrintStream(getPathFRsARFFFilename(outputPrefix));
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
        for (PathWalk path : paths) {
            out.print(path.getNameGenotype()+",");
            c = 0;
            for (FrequentedRegion fr : frequentedRegions.values()) {
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
        boolean first = true;
        for (FrequentedRegion fr : frequentedRegions.values()) {
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
        PrintStream out = new PrintStream(getFRSubpathsFilename(outputPrefix));
        for (FrequentedRegion fr : frequentedRegions.values()) {
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
     * Print out the parameters.
     */
    public void printParameters(String outputPrefix, double alpha, int kappa) throws IOException {
        PrintStream out = new PrintStream(getParamsFilename(outputPrefix));
        String comments = "alpha="+alpha+"\n"+"kappa="+kappa+"\n"+"clocktime="+formatTime(clockTime);
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
        // get the graph from the nodes and paths files
        File nodesFile = new File(getNodesFilename(getInputPrefix()));
        File pathsFile = new File(getPathsFilename(getInputPrefix()));
        graph = new PangenomicGraph();
        graph.importTXT(nodesFile, pathsFile);
        // create a node map for building subpaths
        Map<Long,Node> nodeMap = new HashMap<>();
        for (Node n : graph.getNodes()) {
            nodeMap.put(n.getId(), n);
        }
	// build the FRs
        frequentedRegions = new ConcurrentHashMap<>();
        String frFilename = getFRSubpathsFilename(getInputPrefix());
        BufferedReader reader = new BufferedReader(new FileReader(frFilename));
        String line = null;
        while ((line=reader.readLine())!=null) {
            String[] fields = line.split("\t");
            NodeSet nodes = new NodeSet(graph, fields[0]);
            int support = Integer.parseInt(fields[1]);
            double avgLength = Double.parseDouble(fields[2]);
            List<PathWalk> subpaths = new ArrayList<>();
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
                List<Node> subNodes = new ArrayList<>();
                String[] nodesAsStrings = nodeString.replace("[","").replace("]","").split(",");
                for (String nodeAsString : nodesAsStrings) {
		    long nodeId = Long.parseLong(nodeAsString);
                    subNodes.add(nodeMap.get(nodeId));
                }
                // add to the subpaths
                subpaths.add(new PathWalk(subNodes, name, genotype, label));
            }
            FrequentedRegion fr = new FrequentedRegion(nodes, subpaths, alpha, kappa, getPriorityOption(), support, avgLength);
            frequentedRegions.put(fr.nodes.toString(), fr);
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
