package org.ncgr.pangenomics.fr;

import org.ncgr.pangenomics.Graph;
import org.ncgr.pangenomics.Node;
import org.ncgr.pangenomics.NodeSet;
import org.ncgr.pangenomics.Path;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import java.util.Collections;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;

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
    static double MIN_CASE_CTRL_RATIO = 0.0;
    static boolean USERC = false;
    static boolean VERBOSE = false;
    static boolean DEBUG = false;
    
    // required parameters, no defaults; set in constructor
    Graph graph;  // the Graph we're analyzing
    double alpha; // penetrance: the fraction of a supporting strain's sequence that actually supports the FR; alternatively, `1-alpha` is the fraction of inserted sequence
    int kappa;    // maximum insertion: the maximum insertion length (measured in bp) that any supporting path may have
 
    // optional parameters, set with setters
    boolean verbose = VERBOSE;
    boolean debug = DEBUG;
    boolean useRC = USERC; // indicates if the sequence (e.g. FASTA file) had its reverse complement appended
    int minSup = MINSUP;   // minimum support: minimum number of genome paths (fr.support) for an FR to be considered interesting
    int maxSup = MAXSUP;   // maximum support: maximum number of genome paths (fr.support) for an FR to be considered interesting
    int minSize = MINSIZE; // minimum size: minimum number of de Bruijn nodes (fr.nodes.size()) that an FR must contain to be considered interesting
    int minLen = MINLEN;   // minimum average length of a frequented region's subpath sequences (fr.avgLength) to be considered interesting
    int nRounds = NROUNDS; // number of FR-building rounds to run
    double minCaseCtrlRatio = MIN_CASE_CTRL_RATIO; // if >0 then remove FRs that do not have at least this ratio of case/ctrl or ctrl/case labeled paths
    String outputFile = null; // output file for FRs (stdout if null)

    // the FRs, sorted for convenience
    TreeSet<FrequentedRegion> frequentedRegions;

    // utility items for post-processing
    String inputFile;
    String outputHeading;
    Map<NodeSet,String> outputLines;

    /**
     * Construct with a populated Graph and required parameters
     */
    public FRFinder(Graph graph, double alpha, int kappa) {
        this.graph = graph;
        this.alpha = alpha;
        this.kappa = kappa;
    }

    /**
     * Construct with the output from a previous run
     */
    public FRFinder(String inputFile) throws FileNotFoundException, IOException {
        this.inputFile = inputFile;
        readFrequentedRegions(inputFile);
    }

    /**
     * Find the frequented regions in this Graph.
     */
    public void findFRs() throws IOException {

        if (verbose) {
            graph.printNodes();
            graph.printPaths();
            graph.printNodePaths();
        }

        // store the FRs in a TreeSet, backed with a synchronized Set for parallel processing
        frequentedRegions = new TreeSet<>();
        Set<FrequentedRegion> syncFrequentedRegions = Collections.synchronizedSet(frequentedRegions);
        
        // store the analyzed NodeSets in a TreeSet, backed with a synchronizedSet for parallel processing
        TreeSet<NodeSet> nodeSets = new TreeSet<>();
        Set<NodeSet> syncNodeSets = Collections.synchronizedSet(nodeSets);

        // create initial NodeSets each containing only one node; add associated FRs if they pass filter
        for (Node node : graph.nodes.values()) {
            NodeSet nodeSet = new NodeSet();
            nodeSet.add(node);
            nodeSets.add(nodeSet);
            FrequentedRegion fr = new FrequentedRegion(graph, nodeSet, alpha, kappa);
            if (passesFilters(fr)) frequentedRegions.add(fr);
        }

        // build the FRs round by round
        int round = 0;
        while (round<nRounds) {
            round++;
            printHeading("ROUND "+round);
            if (debug) System.out.println("ns1\telapsed\tadded\tremoved\tcurrent\ttotal");

            // gently suggest garbage collection
            System.gc();

            // use a frozen copy of the current NodeSets for iterating
            final Set<NodeSet> currentNodeSets = new TreeSet<>(nodeSets);

            // non-parallel outer loop through this round's NodeSets
            for (NodeSet ns1 : currentNodeSets) {
                long start = System.currentTimeMillis();
                // we'll add the new FRs to a synchronized set
                Set<FrequentedRegion> newFRs = Collections.synchronizedSet(new HashSet<>());
                // run parallel inner loop over the current NodeSets
                currentNodeSets.parallelStream().forEach((ns2) -> {
                        //////// START PARALLEL CODE ////////
                        if (ns2.first().compareTo(ns1.last())>0) {
                            NodeSet merged = NodeSet.merge(ns1, ns2);
                            if (!syncNodeSets.contains(merged)) {
                                syncNodeSets.add(merged);
                                if (merged.size()>=minSize) {
                                    FrequentedRegion fr = new FrequentedRegion(graph, merged, alpha, kappa);
                                    if (passesFilters(fr)) {
                                        newFRs.add(fr);
                                    }
                                }
                            }
                        }
                        //////// END PARALLEL CODE ////////
                    });
                // spin through the new FRs to see if they are "maximal" (so far) and remove the parents if so
                Set<FrequentedRegion> toAdd = Collections.synchronizedSet(new HashSet<>(newFRs));
                Set<FrequentedRegion> toRemove = Collections.synchronizedSet(new HashSet<>());
                syncFrequentedRegions.parallelStream().forEach((oldFR) -> {
                        //////// START PARALLEL CODE ////////
                        newFRs.parallelStream().forEach((newFR) -> {
                                if (newFR.nodes.parentOf(oldFR.nodes)) {
                                    toAdd.remove(newFR); // yank newFR, oldFR is more maximal
                                } else if (newFR.nodes.childOf(oldFR.nodes)) {
                                    toRemove.add(oldFR); // yank oldFR, newFR is more maximal
                                }
                            });
                        //////// END PARALLEL CODE ////////
                    });
                // update frequentedRegions, display some debug stats
                frequentedRegions.addAll(toAdd);
                frequentedRegions.removeAll(toRemove);
                int added = toAdd.size();
                int removed = toRemove.size();
                int current = frequentedRegions.size();
                int total = syncNodeSets.size();
                if (debug) {
                    long elapsed = System.currentTimeMillis()-start;
                    System.out.println(ns1.toString()+"\t"+elapsed+"ms\t"+added+"\t"+removed+"\t"+current+"\t"+total);
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
                System.out.println("---------------");
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
            printFRHistogram();
        }
        
	// verbosity
	if (verbose) {
	    printPathFRs();
	}

	// the end result
        printFrequentedRegions();
    }

    /**
     * Return true if the given FR passes support and size filters. Other filters could be added here.
     */
    boolean passesFilters(FrequentedRegion fr) {
        // basic filters
        boolean passes = fr.nodes.size()>=minSize && fr.support>=minSup && fr.support<=maxSup && fr.avgLength>=minLen;
        // min case/ctrl or ctrl/case ratio
        if (minCaseCtrlRatio>1.0) {
            int caseCounts = fr.getLabelCount("case");
            int ctrlCounts = fr.getLabelCount("ctrl");
            if (caseCounts>0 || ctrlCounts>0) {
                double ratio = 1.0;
                if (caseCounts>=ctrlCounts && ctrlCounts>0) {
                    ratio = (double)caseCounts/(double)ctrlCounts;
                    passes = passes && ratio>minCaseCtrlRatio;
                } else if (ctrlCounts>=caseCounts && caseCounts>0) {
                    ratio = (double)ctrlCounts/(double)caseCounts;
                    passes = passes && ratio>minCaseCtrlRatio;
                }
            }
        }
        return passes;
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
    public void setDebug() {
        this.debug = true;
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
    public void setMinCaseCtrlRatio(double minCaseCtrlRatio) {
        this.minCaseCtrlRatio = minCaseCtrlRatio;
    }
    public void setUseRC() {
        this.useRC = true;
    }
    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
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
        alphaOption.setRequired(true);
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
        Option genotypeOption = new Option("g", "genotype", true, "which genotype to include (0,1) from the input file; -1 to include all ("+Graph.GENOTYPE+")");
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
        kappaOption.setRequired(true);
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
        Option maxSupOption = new Option("n", "maxsup", true, "maxsup=maximum number of supporting paths for a region to be considered interesting ("+MAXSUP+")");
        maxSupOption.setRequired(false);
        options.addOption(maxSupOption);
        //
        Option outputfileOption = new Option("o", "outputfile", true, "output file (stdout)");
        outputfileOption.setRequired(false);
        options.addOption(outputfileOption);
        //
        Option labelsOption = new Option("p", "pathlabels", true, "tab-delimited file with pathname<tab>label");
        labelsOption.setRequired(false);
        options.addOption(labelsOption);
        //
        Option rcOption = new Option("r", "userc", false, "useRC=flag to indicate if the sequence (e.g. FASTA) had its reverse complement appended ("+USERC+")");
        rcOption.setRequired(false);
        options.addOption(rcOption);
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
        Option graphOnlyOption = new Option("go", "graphonly", false, "just read the graph and output, do not find FRs; for debuggery (false)");
        graphOnlyOption.setRequired(false);
        options.addOption(graphOnlyOption);
        //
        Option nRoundsOption = new Option("nr", "nrounds", true, "number of FR-building rounds to run ("+NROUNDS+")");
        nRoundsOption.setRequired(false);
        options.addOption(nRoundsOption);
        //
        Option minCaseCtrlRatioOption = new Option("mccr", "mincasectrlratio", true,
                                                   "minimum ratio of case/ctrl or ctrl/case labeled paths for FR to qualify ("+MIN_CASE_CTRL_RATIO+")");
        minCaseCtrlRatioOption.setRequired(false);
        options.addOption(minCaseCtrlRatioOption);

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp("FRFinder", options);
            System.exit(1);
            return;
        }

        // parameter validation
        if (!cmd.hasOption("dot") && !cmd.hasOption("json") && !cmd.hasOption("gfa")) {
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
        String gfaFile = cmd.getOptionValue("gfa");
        String pathLabelsFile = cmd.getOptionValue("pathlabels");

        // required parameters
        double alpha = Double.parseDouble(cmd.getOptionValue("alpha"));
        int kappa = Integer.parseInt(cmd.getOptionValue("kappa"));

        // create a Graph from the dot+FASTA or JSON or GFA file
        Graph g = new Graph();
        if (cmd.hasOption("verbose")) g.setVerbose();
        if (cmd.hasOption("debug")) g.setDebug();
        if (cmd.hasOption("genotype")) g.genotype = Integer.parseInt(cmd.getOptionValue("genotype"));
        if (dotFile!=null && fastaFile!=null) {
            g.readSplitMEMDotFile(dotFile, fastaFile);
        } else if (jsonFile!=null) {
            g.readVgJsonFile(jsonFile);
        } else if (gfaFile!=null) {
            g.readVgGfaFile(gfaFile);
        } else {
            System.err.println("ERROR: no DOT+FASTA or JSON or GFA provided.");
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
        if (cmd.hasOption("debug")) frf.setDebug();
        if (cmd.hasOption("userc")) frf.setUseRC();
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
        if (cmd.hasOption("mincasectrlratio")) {
            double mccr = Double.parseDouble(cmd.getOptionValue("mincasectrlratio"));
            if (mccr<1.0) {
                System.err.println("ERROR: parameter mccr/mincasectrlratio must be greater than 1");
                System.exit(1);
            }
            frf.setMinCaseCtrlRatio(mccr);
        }
        if (cmd.hasOption("outputfile")) {
            frf.setOutputFile(cmd.getOptionValue("outputfile"));
        }

        // print out the parameters to stdout or outputFile+".params" if exists
        frf.printParameters();
        
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
     * Print the path names and the FRs that have subpaths belonging to those paths.
     * This can be used as input to a classification routine.
     */
    void printPathFRs() {
        printHeading("PATH FREQUENTED REGIONS");
        // columns
        System.out.print("Path\\FR");
        if (graph.paths.first().label!=null) System.out.print("\tLabel");
        int c = 1;
        for (FrequentedRegion fr : frequentedRegions) {
            System.out.print("\t"+c);
            c++;
        }
        System.out.println("");
        // rows
        for (Path path : graph.paths) {
            System.out.print(path.name);
            if (path.label!=null) System.out.print("\t"+path.label);
            for (FrequentedRegion fr : frequentedRegions) {
                System.out.print("\t"+fr.countSubpathsOf(path));
            }
            System.out.println("");
        }
    }

    /**
     * Print out the FRs, either to stdout or outputFile if not null.
     */
    void printFrequentedRegions() throws IOException {
        PrintStream out = null;
        if (inputFile==null) {
            // output from a findFRs run
            if (outputFile==null) {
                out = System.out;
                printHeading("FREQUENTED REGIONS");
            } else {
                out = new PrintStream(outputFile);
            }
            out.println("FR\t"+frequentedRegions.first().columnHeading());
            int c = 1;
            for (FrequentedRegion fr : frequentedRegions) {
                out.println(c+"\t"+fr.toString());
                c++;
            }
        } else {
            // output from post-processing
            if (outputFile==null) {
                out = System.out;
                printHeading("FREQUENTED REGIONS");
            } else {
                out = new PrintStream(outputFile);
            }
            out.println(outputHeading);
            for (FrequentedRegion fr : frequentedRegions) {
                out.println(outputLines.get(fr.nodes));
            }
        }
    }

    /**
     * Read FR NodeSets from the output from a previous run.
     * 0   1           2       3      4        5        6        7        ...
     * FR  nodes       support avgLen label1.n label1.f label2.n label2.f ...
     * 1   [5,7,15,33] 28      282    18       0.667    10       1.000    ...
     */
    void readFrequentedRegions(String inputFile) throws FileNotFoundException, IOException {
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        frequentedRegions = new TreeSet<>();
        outputLines = new TreeMap<>();
        outputHeading = reader.readLine();
        String line = null;
        while ((line=reader.readLine())!=null) {
            String[] fields = line.split("\t");
            String nsString = fields[1];
            NodeSet ns = new NodeSet(nsString);
            outputLines.put(ns,line);
            FrequentedRegion fr = new FrequentedRegion(ns);
            frequentedRegions.add(fr);
        }
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
     * Print out the parameters, either to stdout or {outputFile}.params
     */
    public void printParameters() throws IOException {
        PrintStream out = null;
        if (outputFile==null) {
            out = System.out;
            printHeading("PARAMETERS");
        } else {
            // no heading for file output; append to outputFile
            String paramFile = outputFile+".params";
            out = new PrintStream(paramFile);
        }
        // Graph
        if (graph!=null) {
            if (graph.jsonFile!=null) out.println("jsonfile"+"\t"+graph.jsonFile);
            if (graph.dotFile!=null) out.println("dotfile"+"\t"+graph.dotFile);
            if (graph.fastaFile!=null) out.println("fastafile"+"\t"+graph.fastaFile);
            out.println("genotype"+"\t"+graph.genotype);
        }
        // FRFinder
        out.println("alpha"+"\t"+alpha);
        out.println("kappa"+"\t"+kappa);
        out.println("minsup"+"\t"+minSup);
        out.println("maxsup"+"\t"+maxSup);
        out.println("minsize"+"\t"+minSize);
        out.println("minlen"+"\t"+minLen);
        out.println("nrounds"+"\t"+nRounds);
        out.println("mincasectrlratio"+"\t"+minCaseCtrlRatio);
        out.println("userc"+"\t"+useRC);
        if (inputFile!=null) out.println("inputfile"+"\t"+inputFile);
        if (outputFile!=null) out.println("outputfile"+"\t"+outputFile);
    }

    /**
     * Read in the parameters from a previous run, presuming inputFile is not null.
     */
    void readParameters() throws FileNotFoundException, IOException {
        if (inputFile==null) return;
        String paramFile = inputFile+".params";
        BufferedReader reader = new BufferedReader(new FileReader(paramFile));
        String line = null;
        String jsonFile = null;
        String dotFile = null;
        String fastaFile = null;
        int genotype = Graph.GENOTYPE;
        while ((line=reader.readLine())!=null) {
            String[] parts = line.split("\t");
            if (parts[0].equals("jsonfile")) {
                jsonFile = parts[1];
            } else if (parts[0].equals("dotFile")) {
                dotFile = parts[1];
            } else if (parts[0].equals("fastafile")) {
                fastaFile = parts[1];
            } else if (parts[0].equals("genotype")) {
                genotype = Integer.parseInt(parts[1]);
            } else if (parts[0].equals("alpha")) {
                alpha = Double.parseDouble(parts[1]);
            } else if (parts[0].equals("kappa")) {
                kappa = Integer.parseInt(parts[1]);
            } else if (parts[0].equals("minsup")) {
                minSup = Integer.parseInt(parts[1]);
            } else if (parts[0].equals("maxsup")) {
                maxSup = Integer.parseInt(parts[1]);
            } else if (parts[0].equals("minsize")) {
                minSize = Integer.parseInt(parts[1]);
            } else if (parts[0].equals("minlen")) {
                minLen = Integer.parseInt(parts[1]);
            } else if (parts[0].equals("nrounds")) {
                nRounds = Integer.parseInt(parts[1]);
            } else if (parts[0].equals("userc")) {
                useRC = Boolean.parseBoolean(parts[1]);
            }
            // load the Graph if we've got the files
            if (jsonFile!=null) {
                graph = new Graph();
                graph.genotype = genotype;
                graph.readVgJsonFile(jsonFile);
            } else if (dotFile!=null && fastaFile!=null) {
                graph = new Graph();
                graph.genotype = genotype;
                graph.readSplitMEMDotFile(dotFile, fastaFile);
            }
        }
    }
}
