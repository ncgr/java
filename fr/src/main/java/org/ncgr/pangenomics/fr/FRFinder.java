package org.ncgr.pangenomics.fr;

import org.ncgr.jgraph.PangenomicGraph;
import org.ncgr.jgraph.PathWalk;
import org.ncgr.pangenomics.Node;
import org.ncgr.pangenomics.NodeSet;

import java.io.*;
import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;

import org.apache.commons.cli.*;

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

    // optional parameter defaults
    static int MINSUP = 1;
    static int MINSIZE = 1;
    static double MINLEN = 1.0;
    static boolean CASE_CTRL = false;
    static boolean VERBOSE = false;
    static boolean DEBUG = false;
    static boolean BRUTE_FORCE = false;
    static boolean SERIAL = false;
    static boolean RESUME = false;
    
    // required parameters, no defaults; set in constructor
    PangenomicGraph graph;  // the Graph we're analyzing
    double alpha; // penetrance: the fraction of a supporting strain's sequence that actually supports the FR; alternatively, `1-alpha` is the fraction of inserted sequence
    int kappa;    // maximum insertion: the maximum insertion length (measured in bp) that any supporting path may have
 
    // optional parameters, set with setters
    boolean verbose = VERBOSE;
    boolean debug = DEBUG;
    int minSup = MINSUP;   // minimum support: minimum number of genome paths (fr.support) for an FR to be considered interesting
    int minSize = MINSIZE; // minimum size: minimum number of de Bruijn nodes (fr.nodes.size()) that an FR must contain to be considered interesting
    double minLen = MINLEN;   // minimum average length of a frequented region's subpath sequences (fr.avgLength) to be considered interesting
    boolean caseCtrl = CASE_CTRL; // emphasize FRs that have large case/control support
    boolean bruteForce = BRUTE_FORCE; // find FRs comprehensively with brute force, not using heuristic approach from paper; for testing only!
    boolean serial = SERIAL; // serial processing for demos or experiments
    boolean resume = RESUME; // resume from a previous run

    // save files
    String SYNC_FREQUENTED_REGIONS_SAVE = "syncFrequentedRegions.save.txt";
    String USED_FRS_SAVE = "usedFRs.save.txt";
    String FREQUENTED_REGIONS_SAVE = "frequentedRegions.save.txt";

    // I/O
    String outputPrefix; // output file prefix

    // post-processing
    String inputPrefix;

    // the FRs, sorted for convenience
    TreeSet<FrequentedRegion> frequentedRegions;

    long clockTime;

    /**
     * Construct with a populated Graph and required parameters
     */
    public FRFinder(PangenomicGraph graph, double alpha, int kappa) {
        this.graph = graph;
        this.alpha = alpha;
        this.kappa = kappa;
    }

    /**
     * Construct with the output from a previous run. Be sure to set minSup, minSize, minLen filters as needed before running postprocess().
     */
    public FRFinder(String inputPrefix) throws FileNotFoundException, IOException {
        this.inputPrefix = inputPrefix;
        readParameters();
        readFrequentedRegions();
    }

    /**
     * Find the frequented regions in this Graph.
     */
    public void findFRs() throws IOException {
	System.out.println("# graph has "+graph.vertexSet().size()+" nodes and "+graph.getPaths().size()+" paths");
        if (graph.getLabelCounts().get("case")!=null && graph.getLabelCounts().get("ctrl")!=null) {
            System.out.println("# graph has "+graph.getLabelCounts().get("case")+" case paths and "+graph.getLabelCounts().get("ctrl")+" ctrl paths");
        }
        if (verbose) {
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

        // just a counter
        int round = 0;

        if (resume) {
            System.out.println("# resuming from previous run");
            String line = null;
            // frequentedRegions
            BufferedReader frReader = new BufferedReader(new FileReader(FREQUENTED_REGIONS_SAVE));
            while ((line=frReader.readLine())!=null) {
                String[] parts = line.split("\t");
                NodeSet nodes = new NodeSet(parts[0]);
                frequentedRegions.add(new FrequentedRegion(graph, nodes, alpha, kappa));
                round++;
            }
            // syncFrequentedRegions
            BufferedReader sfrReader = new BufferedReader(new FileReader(SYNC_FREQUENTED_REGIONS_SAVE));
            while ((line=sfrReader.readLine())!=null) {
                String[] parts = line.split("\t");
                NodeSet nodes = new NodeSet(parts[0]);
                syncFrequentedRegions.add(new FrequentedRegion(graph, nodes, alpha, kappa));
            }
            // usedFRs
            BufferedReader usedFRsReader = new BufferedReader(new FileReader(USED_FRS_SAVE));
            while ((line=usedFRsReader.readLine())!=null) {
                String[] parts = line.split("\t");
                NodeSet nodes = new NodeSet(parts[0]);
                usedFRs.add(new FrequentedRegion(graph, nodes, alpha, kappa));
            }
        } else {
            // initialize syncFrequentedRegions with single-node FRs
            for (Node node : graph.getNodes()) {
                NodeSet c = new NodeSet();
                c.add(node);
                Set<PathWalk> s = new HashSet<>();
                for (PathWalk p : graph.getPaths()) {
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
        while (added) {
            round++;
            added = false;
            // gently suggest garbage collection
            System.gc();
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
                                    FRPair frpair = new FRPair(fr1, fr2, graph, alpha, kappa, caseCtrl);
                                    String nodesKey = frpair.nodes.toString();
                                    if (!usedNodeSets.contains(nodesKey)) {
                                        usedNodeSets.add(nodesKey);
                                        frpair.merge();
                                        loopFRs.add(frpair.merged);
                                        if (frpair.merged.support>=minSup && frpair.merged.avgLength>=minLen && frpair.merged.nodes.size()>=minSize) frequentedRegions.add(frpair.merged);
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
                            FRPair frpair = new FRPair(fr1, fr2, graph, alpha, kappa, caseCtrl);
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
                        System.out.println(round+":"+frpair.fr1.nodes.toString()+frpair.fr2.nodes.toString()+"\t"+frpair.merged.toString());
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
                                    FRPair frpair = new FRPair(fr1, fr2, graph, alpha, kappa, caseCtrl);
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
                        System.out.println(round+":"+frpair.fr1.nodes+frpair.fr2.nodes+"\t"+frpair.merged.toString());
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
    public void postprocess() throws FileNotFoundException, IOException {
        TreeSet<FrequentedRegion> filteredFRs = new TreeSet<>();
        for (FrequentedRegion fr : frequentedRegions) {
            boolean passes = true;
            String reason = "";
            if (fr.support<minSup) {
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
            if (verbose) System.out.println(fr.toString()+reason);
        }
        if (verbose) System.out.println(filteredFRs.size()+" FRs passed minSup="+minSup+", minSize="+minSize+", minLen="+minLen);
	// output the filtered FRs and SVM data
        frequentedRegions = filteredFRs;
	if (frequentedRegions.size()>0) {
	    printFrequentedRegions();
	    printPathFRsSVM();
            printPathFRsARFF();
	}
    }

    public double getAlpha() {
        return alpha;
    }
    public int getKappa() {
        return kappa;
    }
    public int getMinSup() {
        return minSup;
    }
    public int getMinSize() {
        return minSize;
    }
    public double getMinLen() {
        return minLen;
    }

    // setters for optional parameters
    public void setVerbose() {
        this.verbose = true;
    }
    public void setDebug() {
        this.debug = true;
    }
    public void setCaseCtrl() {
        this.caseCtrl = true;
    }
    public void setBruteForce() {
        this.bruteForce = true;
    }
    public void setSerial() {
        this.serial = true;
    }
    public void setResume() {
        this.resume = true;
    }
    public void setMinSup(int minSup) {
        this.minSup = minSup;
    }
    public void setMinSize(int minSize) {
        this.minSize = minSize;
    }
    public void setMinLen(double minLen) {
        this.minLen = minLen;
    }
    public void setOutputPrefix(String outputPrefix) {
        this.outputPrefix = outputPrefix;
    }

    /**
     * Command-line utility
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {

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
        Option kappaOption = new Option("k", "kappa", true, "kappa=maximum insertion length that any supporting path may have (required)");
        kappaOption.setRequired(false);
        options.addOption(kappaOption);
        //
        Option minLenOption = new Option("l", "minlen", true, "minlen=minimum allowed average length (bp) of an FR's subpaths ("+MINLEN+")");
        minLenOption.setRequired(false);
        options.addOption(minLenOption);
        //
        Option minSupOption = new Option("m", "minsup", true, "minsup=minimum number of supporting paths for a region to be considered interesting ("+MINSUP+")");
        minSupOption.setRequired(false);
        options.addOption(minSupOption);
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
        Option minSizeOption = new Option("s", "minsize", true, "minsize=minimum number of nodes that a FR must contain to be considered interesting ("+MINSIZE+")");
        minSizeOption.setRequired(false);
        options.addOption(minSizeOption);
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
            System.err.println("You must specify a splitMEM-style DOT file plus FASTA (-d/--dot and -f/--fasta ), a vg JSON file (-j, --json) or a vg GFA file (--gfa)");
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
        // String gfaFile = cmd.getOptionValue("gfa");
        // String pathLabelsFile = cmd.getOptionValue("pathlabels");

        // assume GFA file
        File gfaFile = new File(cmd.getOptionValue("gfa"));

        // assume path labels file
        File labelsFile = null;
        if (cmd.hasOption("pathlabels")) labelsFile = new File(cmd.getOptionValue("pathlabels"));

        // run parameters
        double alpha = 0.0;
        int kappa = 0;
        if (cmd.hasOption("alpha")) alpha = Double.parseDouble(cmd.getOptionValue("alpha"));
        if (cmd.hasOption("kappa")) kappa = Integer.parseInt(cmd.getOptionValue("kappa"));

        // create a PangenomicGraph from a GFA file
        PangenomicGraph pg = new PangenomicGraph();
        if (cmd.hasOption("verbose")) pg.setVerbose();
        if (cmd.hasOption("genotype")) pg.setGenotype(Integer.parseInt(cmd.getOptionValue("genotype")));
        pg.importGFA(gfaFile);

        // if a labels file is given, add them to the paths
        if (labelsFile!=null) {
            pg.readPathLabels(labelsFile);
        }

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
        
        FRFinder frf;
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
        } else { 
            // instantiate the FRFinder with this Graph, alpha and kappa
            postProcess = false;
            frf = new FRFinder(pg, alpha, kappa);
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
        if (cmd.hasOption("outputprefix")) {
            frf.setOutputPrefix(cmd.getOptionValue("outputprefix"));
        }

        // tell findFRs to load saved data if resume requested
        boolean resume = false;
        if (cmd.hasOption("resume")) frf.setResume();

        // run the requested job
        if (postProcess) {
            frf.postprocess();
        } else {
            frf.findFRs();
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
    void readParameters() throws FileNotFoundException, IOException {
        // String paramsFile = getParamsFilename(inputPrefix);
        // BufferedReader reader = new BufferedReader(new FileReader(paramsFile));
        // String line = null;
        // String jsonFile = null;
        // String gfaFile = null;
        // String dotFile = null;
        // String fastaFile = null;
        // String labelsFile = null;
        // int genotype = PangenomicGraph.BOTH_GENOTYPES;
        // while ((line=reader.readLine())!=null) {
        //     String[] parts = line.split("\t");
        //     if (parts[0].equals("jsonfile")) {
        //         jsonFile = parts[1];
        //     } else if (parts[0].equals("gfafile")) {
        //         gfaFile = parts[1];
        //     } else if (parts[0].equals("dotFile")) {
        //         dotFile = parts[1];
        //     } else if (parts[0].equals("fastafile")) {
        //         fastaFile = parts[1];
        //     } else if (parts[0].equals("pathlabels")) {
        //         labelsFile = parts[1];
        //     } else if (parts[0].equals("genotype")) {
        //         genotype = Integer.parseInt(parts[1]);
        //     } else if (parts[0].equals("alpha")) {
        //         alpha = Double.parseDouble(parts[1]);
        //     } else if (parts[0].equals("kappa")) {
        //         kappa = Integer.parseInt(parts[1]);
        //     } else if (parts[0].equals("casectrl")) {
        //         caseCtrl = Boolean.parseBoolean(parts[1]);
        //     } else if (parts[0].equals("minsup")) {
        //         minSup = Integer.parseInt(parts[1]);
        //     } else if (parts[0].equals("minsize")) {
        //         minSize = Integer.parseInt(parts[1]);
        //     } else if (parts[0].equals("minlen")) {
        //         minLen = Double.parseDouble(parts[1]);
        //     }
        //     // load the Graph if we've got the files
        //     if (jsonFile!=null) {
        //         graph = new Graph();
        //         graph.genotype = genotype;
        //         graph.readVgJsonFile(jsonFile);
        //         if (labelsFile!=null) graph.readPathLabels(labelsFile);
        //     } else if (gfaFile!=null) {
        //         graph = new Graph();
        //         graph.genotype = genotype;
        //         graph.readVgGfaFile(gfaFile);
        //         if (labelsFile!=null) graph.readPathLabels(labelsFile);
        //     } else if (dotFile!=null && fastaFile!=null) {
        //         graph = new Graph();
        //         graph.genotype = genotype;
        //         graph.readSplitMEMDotFile(dotFile, fastaFile);
        //         if (labelsFile!=null) graph.readPathLabels(labelsFile);
        //     }
        // }
    }

    /**
     * Read FRs from the output from a previous run.
     * Assumes that alpha, kappa and graph are already initialized.
     * [18,34]	70	299	54	16
     * 509678.0.ctrl:[18,20,21,23,24,26,27,29,30,33,34]
     * 628863.1.case:[18,20,21,23,24,26,27,29,30,33,34]
     * etc.
     */
    void readFrequentedRegions() throws FileNotFoundException, IOException {
        // // do we have a Graph?
        // if (graph.getNodes().size()==0) {
        //     System.err.println("ERROR in readFrequentedRegions: graph has not been initialized.");
        //     System.exit(1);
        // }
        // frequentedRegions = new TreeSet<>();
        // String frFilename = getFRSubpathsFilename(inputPrefix);
        // BufferedReader reader = new BufferedReader(new FileReader(frFilename));
        // String line = null;
        // while ((line=reader.readLine())!=null) {
        //     String[] fields = line.split("\t");
        //     NodeSet nodes = new NodeSet(fields[0]);
        //     int support = Integer.parseInt(fields[1]);
        //     double avgLength = Double.parseDouble(fields[2]);
        //     Set<PathWalk> subpaths = new HashSet<>();
        //     for (int i=0; i<support; i++) {
        //         line = reader.readLine();
        //         String[] parts = line.split(":");
        //         String pathFull = parts[0];
        //         String nodeString = parts[1];
        //         // split out the name, genotype, label
        //         String[] nameParts = pathFull.split("\\.");
        //         String name = nameParts[0];
        //         int genotype = -1;
        //         if (nameParts.length>1) genotype = Integer.parseInt(nameParts[1]);
        //         String label = null;
        //         if (nameParts.length>2) label = nameParts[2];
        //         // add to the subpaths
        //         subpaths.add(new PathWalk(name, genotype, label, nodeString));
        //     }
        //     FrequentedRegion fr = new FrequentedRegion(graph, nodes, subpaths, alpha, kappa, support, avgLength);
        //     frequentedRegions.add(fr);
        // }
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
