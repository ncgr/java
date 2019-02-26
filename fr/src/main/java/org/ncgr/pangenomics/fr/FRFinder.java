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
import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.concurrent.PriorityBlockingQueue;

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
    static int MINSIZE = 1;
    static double MINLEN = 1.0;
    static boolean CASE_CTRL = false;
    static boolean VERBOSE = false;
    static boolean DEBUG = false;
    static boolean BRUTE_FORCE = false;
    static boolean SERIAL = false;
    
    // required parameters, no defaults; set in constructor
    Graph graph;  // the Graph we're analyzing
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
    public FRFinder(Graph graph, double alpha, int kappa) {
        this.graph = graph;
        this.alpha = alpha;
        this.kappa = kappa;

        if (verbose) {
            graph.printNodes(System.out);
            graph.printPaths(System.out);
            graph.printNodePaths(System.out);
        }
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

        // store the saved FRs in a TreeSet
        frequentedRegions = new TreeSet<>();
        
        // store the studied FRs in a synchronized TreeSet initialized with single-node FRs
        Set<FrequentedRegion> syncFrequentedRegions = Collections.synchronizedSet(new TreeSet<>());
        for (Node node : graph.nodes.values()) {
            NodeSet c = new NodeSet();
            c.add(node);
            Set<Path> s = new HashSet<>();
            for (Path p : graph.paths) {
                Set<Path> support = FrequentedRegion.computeSupport(c, p, alpha, kappa);
                s.addAll(support);
            }
            if (s.size()>0) {
                syncFrequentedRegions.add(new FrequentedRegion(graph, c, s, alpha, kappa));
            }
        }

        // keep track of FRs we've already looked at
        Set<FrequentedRegion> usedFRs = new HashSet<>();
        
        // build the FRs round by round
	long startTime = System.currentTimeMillis();
        boolean added = true;
        int round = 0;
        while (added) {
            round++;
            added = false;
            // gently suggest garbage collection
            System.gc();
            if (bruteForce) {
                // no heurism, for demo purposes
                Set<FrequentedRegion> loopFRs = new HashSet<>();
                int oldSize = frequentedRegions.size();
                for (FrequentedRegion fr1 : syncFrequentedRegions) {
                    for (FrequentedRegion fr2 : syncFrequentedRegions) {
                        if (fr1.compareTo(fr2)>0) {
                            FRPair frpair = new FRPair(fr1, fr2, graph, alpha, kappa, caseCtrl);
                            loopFRs.add(frpair.merged);
                            if (!frequentedRegions.contains(frpair.merged) &&
                                frpair.merged.support>0 &&
                                frpair.merged.support>=minSup &&
                                frpair.merged.nodes.size()>=minSize &&
                                frpair.merged.avgLength>=minLen) {
                                frequentedRegions.add(frpair.merged);
                                System.out.println(fr1.nodes.toString()+fr2.nodes.toString()+":"+frpair.merged.summaryString());
                            }
                        }
                    }
                }
                added = loopFRs.size()>0;
                syncFrequentedRegions.addAll(loopFRs);
                System.out.println(round+":"+(frequentedRegions.size()-oldSize)+" FRs added.");
            } else if (serial) {
                // serial processing with extra output for demo purposes or other experiments
                // put FR pairs into a PriorityQueue which sorts them by decreasing interest (defined by the FRPair comparator)
                PriorityQueue<FRPair> pq = new PriorityQueue<>();
                // spin through FRs in a serial manner
                for (FrequentedRegion fr1 : syncFrequentedRegions) {
                    for (FrequentedRegion fr2 : syncFrequentedRegions) {
                        if (fr2.compareTo(fr1)>=0) {
                            FRPair frpair = new FRPair(fr1, fr2, graph, alpha, kappa, caseCtrl);
                            if (!usedFRs.contains(fr1) && !usedFRs.contains(fr2) && !frequentedRegions.contains(frpair.merged)) {
                                System.out.println(fr1.nodes.toString()+fr2.nodes.toString()+":"+frpair.merged.summaryString());
                                pq.add(frpair);
                            }
                        }
                    }
                }
                // add our new FR
                if (pq.size()>0) {
                    FRPair frpair = pq.peek();
                    if (frpair.merged.support>0) {
                        added = true;
                        usedFRs.add(frpair.fr1);
                        usedFRs.add(frpair.fr2);
                        syncFrequentedRegions.add(frpair.merged);
                        frequentedRegions.add(frpair.merged);
                        System.out.println(round+":"+frpair.fr1.nodes+frpair.fr2.nodes+"\t"+frpair.merged.summaryString());
                    }
                }
            } else {
                // default: parallel processing
                // put FR pairs into a PriorityBlockingQueue which sorts them by decreasing interest (defined by the FRPair comparator)
                PriorityBlockingQueue<FRPair> pbq = new PriorityBlockingQueue<>();
                ////////////////////////////////////////
                // spin through FRs in a parallel manner
                // NOTE: fr1>=fr2 compare to stay above diagonal costs same time as running both sides!
                syncFrequentedRegions.parallelStream().forEach((fr1) -> {
                        syncFrequentedRegions.parallelStream().forEach((fr2) -> {
                                if (fr2.compareTo(fr1)>=0) {
                                    FRPair frpair = new FRPair(fr1, fr2, graph, alpha, kappa, caseCtrl);
                                    if (!usedFRs.contains(fr1) && !usedFRs.contains(fr2) && !frequentedRegions.contains(frpair.merged)) {
                                        pbq.add(frpair);
                                    }
                                }
                            });
                    });
                ////////////////////////////////////////
                // add our new FR
                if (pbq.size()>0) {
                    FRPair frpair = pbq.peek();
                    if (frpair.merged.support>0) {
                        added = true;
                        usedFRs.add(frpair.fr1);
                        usedFRs.add(frpair.fr2);
                        syncFrequentedRegions.add(frpair.merged);
                        frequentedRegions.add(frpair.merged);
                        System.out.println(round+":"+frpair.fr1.nodes+frpair.fr2.nodes+"\t"+frpair.merged.summaryString());
                    }
                }
            }
        }

	clockTime = System.currentTimeMillis() - startTime;
        System.out.println("Found "+frequentedRegions.size()+" FRs.");
	System.out.println("Clock time: "+formatTime(clockTime));
        
	// final output
	if (frequentedRegions.size()>0) {
	    printAll();
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
            if (verbose) System.out.println(fr.summaryString()+reason);
        }
        System.out.println(filteredFRs.size()+" FRs passed minSup="+minSup+", minSize="+minSize+", minLen="+minLen);
	// output the filtered FRs and SVM data
        frequentedRegions = filteredFRs;
	if (frequentedRegions.size()>0) {
	    printFrequentedRegions();
	    printPathFRsSVM();
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
        Option genotypeOption = new Option("g", "genotype", true, "which genotype to include (0,1) from the input file; "+Graph.BOTH_GENOTYPES+" to include all ("+Graph.BOTH_GENOTYPES+")");
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
        Option serialOption = new Option("bf", "serial", false, "find FRs serially for testing/experiments ("+SERIAL+")");
        serialOption.setRequired(false);
        options.addOption(serialOption);

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
        String gfaFile = cmd.getOptionValue("gfa");
        String pathLabelsFile = cmd.getOptionValue("pathlabels");

        // original run parameters
        double alpha = 0.0;
        int kappa = 0;
        if (cmd.hasOption("alpha")) alpha = Double.parseDouble(cmd.getOptionValue("alpha"));
        if (cmd.hasOption("kappa")) kappa = Integer.parseInt(cmd.getOptionValue("kappa"));

        // create a Graph from the dot+FASTA or JSON or GFA file
        Graph g = new Graph();
        if (cmd.hasOption("verbose")) g.setVerbose();
        if (cmd.hasOption("genotype")) g.genotype = Integer.parseInt(cmd.getOptionValue("genotype"));
        if (dotFile!=null && fastaFile!=null) {
            g.readSplitMEMDotFile(dotFile, fastaFile);
        } else if (jsonFile!=null) {
            g.readVgJsonFile(jsonFile);
        } else if (gfaFile!=null) {
            g.readVgGfaFile(gfaFile);
        }

        // if a labels file is given, append the labels to the path names
        if (pathLabelsFile!=null) {
            g.readPathLabels(pathLabelsFile);
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
            String inputPrefix = cmd.getOptionValue("inputprefix");
            postProcess = true;
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
            frf = new FRFinder(g, alpha, kappa);
            postProcess = false;
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
        for (Path path : graph.paths) {
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
            for (Path path : graph.paths) {
                out.print("\t"+fr.countSubpathsOf(path));
            }
            out.println("");
        }
        if (outputPrefix!=null) out.close();
    }

    /**
     * Print the path FR support for libsvm. Lines are like:
     * +1 1:1 2:1 3:1 4:0 ...
     * -1 1:0 2:0 3:0 4:1 ...
     * +1 1:0 2:1 3:0 4:2 ...
     * Where +1 corresponds to "case" and -1 corresponds to "ctrl" (0 otherwise).
     */
    void printPathFRsSVM() throws IOException {
        PrintStream out = System.out;
        if (outputPrefix==null) {
            printHeading("PATH SVM RECORDS");
        } else {
            out = new PrintStream(getPathFRsSVMFilename(outputPrefix));
        }
        // only rows, one per path
        for (Path path : graph.paths) {
            String group = "0";
            if (path.label!=null && path.label.equals("case")) {
                group = "+1";
            } else if (path.label!=null && path.label.equals("ctrl")) {
                group = "-1";
            }
            out.print(group);
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
            out.println(fr.summaryString());
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
            out.println(fr.summaryString());
            out.println(fr.subpathsString());
        }
        if (outputPrefix!=null) out.close();
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
        // do we have a Graph?
        if (graph.nodes.size()==0) {
            System.err.println("ERROR in readFrequentedRegions: graph has not been initialized.");
            System.exit(1);
        }
        frequentedRegions = new TreeSet<>();
        String frFilename = getFRSubpathsFilename(inputPrefix);
        BufferedReader reader = new BufferedReader(new FileReader(frFilename));
        String line = null;
        while ((line=reader.readLine())!=null) {
            String[] fields = line.split("\t");
            NodeSet nodes = new NodeSet(fields[0]);
            int support = Integer.parseInt(fields[1]);
            double avgLength = Double.parseDouble(fields[2]);
            Set<Path> subpaths = new HashSet<>();
            for (int i=0; i<support; i++) {
                line = reader.readLine();
                String[] parts = line.split(":");
                String pathFull = parts[0];
                String nodeString = parts[1];
                // split out the name, genotype, label
                String[] nameParts = pathFull.split("\\.");
                String name = nameParts[0];
                int genotype = -1;
                if (nameParts.length>1) genotype = Integer.parseInt(nameParts[1]);
                String label = null;
                if (nameParts.length>2) label = nameParts[2];
                // add to the subpaths
                subpaths.add(new Path(name, genotype, label, nodeString));
            }
            FrequentedRegion fr = new FrequentedRegion(graph, nodes, subpaths, alpha, kappa, support, avgLength);
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
            out.println("genotype"+"\t"+graph.genotype);
            if (graph.jsonFile!=null) out.println("jsonfile"+"\t"+graph.jsonFile);
            if (graph.gfaFile!=null) out.println("gfafile"+"\t"+graph.gfaFile);
            if (graph.dotFile!=null) out.println("dotfile"+"\t"+graph.dotFile);
            if (graph.fastaFile!=null) out.println("fastafile"+"\t"+graph.fastaFile);
            if (graph.labelsFile!=null) out.println("pathlabels"+"\t"+graph.labelsFile);
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
     * Print all output.
     */
    void printAll() throws FileNotFoundException, IOException {
        printParameters();
        printFrequentedRegions();
        printFRSubpaths();
        printPathFRs();
        printPathFRsSVM();
    }

    /**
     * Read in the parameters from a previous run.
     */
    void readParameters() throws FileNotFoundException, IOException {
        String paramsFile = getParamsFilename(inputPrefix);
        BufferedReader reader = new BufferedReader(new FileReader(paramsFile));
        String line = null;
        String jsonFile = null;
        String gfaFile = null;
        String dotFile = null;
        String fastaFile = null;
        String labelsFile = null;
        int genotype = Graph.BOTH_GENOTYPES;
        while ((line=reader.readLine())!=null) {
            String[] parts = line.split("\t");
            if (parts[0].equals("jsonfile")) {
                jsonFile = parts[1];
            } else if (parts[0].equals("gfafile")) {
                gfaFile = parts[1];
            } else if (parts[0].equals("dotFile")) {
                dotFile = parts[1];
            } else if (parts[0].equals("fastafile")) {
                fastaFile = parts[1];
            } else if (parts[0].equals("pathlabels")) {
                labelsFile = parts[1];
            } else if (parts[0].equals("genotype")) {
                genotype = Integer.parseInt(parts[1]);
            } else if (parts[0].equals("alpha")) {
                alpha = Double.parseDouble(parts[1]);
            } else if (parts[0].equals("kappa")) {
                kappa = Integer.parseInt(parts[1]);
            } else if (parts[0].equals("casectrl")) {
                caseCtrl = Boolean.parseBoolean(parts[1]);
            } else if (parts[0].equals("minsup")) {
                minSup = Integer.parseInt(parts[1]);
            } else if (parts[0].equals("minsize")) {
                minSize = Integer.parseInt(parts[1]);
            } else if (parts[0].equals("minlen")) {
                minLen = Double.parseDouble(parts[1]);
            }
            // load the Graph if we've got the files
            if (jsonFile!=null) {
                graph = new Graph();
                graph.genotype = genotype;
                graph.readVgJsonFile(jsonFile);
                if (labelsFile!=null) graph.readPathLabels(labelsFile);
            } else if (gfaFile!=null) {
                graph = new Graph();
                graph.genotype = genotype;
                graph.readVgGfaFile(gfaFile);
                if (labelsFile!=null) graph.readPathLabels(labelsFile);
            } else if (dotFile!=null && fastaFile!=null) {
                graph = new Graph();
                graph.genotype = genotype;
                graph.readSplitMEMDotFile(dotFile, fastaFile);
                if (labelsFile!=null) graph.readPathLabels(labelsFile);
            }
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
     * Form the parameters output filename
     */
    String getParamsFilename(String prefix) {
        return prefix+".params.txt";
    }

    /**
     * Form the new output prefix from the input prefix and post-processing parameters
     */
    String getOutputPrefix(String inputPrefix) {
        return inputPrefix+"."+minSup+"."+minSize+"."+(int)minLen;
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
