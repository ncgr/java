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
 * Finds frequented regions in a Graph. Based loosely on bmumey's static C-like implementation.
 *
 * See Cleary, et al., "Exploring Frequented Regions in Pan-Genomic Graphs", IEEE/ACM Trans Comput Biol Bioinform. 2018 Aug 9. PMID:30106690 DOI:10.1109/TCBB.2018.2864564
 *
 * @author Sam Hokin
 */
public class FRFinder {

    // optional parameter defaults
    static int MINSUP = 1;
    static int MINSIZE = 1;
    static boolean USERC = false;
    static boolean VERBOSE = false;

    // required parameters, no defaults; set in constructor
    Graph graph;  // the Graph we're analyzing
    double alpha; // penetrance: the fraction of a supporting strain's sequence that actually supports the FR; alternatively, `1-alpha` is the fraction of inserted sequence
    int kappa;    // maximum insertion: the maximum insertion length (measured in bp) that any supporting path may have
 
    // optional parameters, set with setters
    boolean verbose = VERBOSE;
    int minSup = MINSUP;   // minimum support: minimum number of genome paths (fr.support) in order for a region to be considered frequent
    int minSize = MINSIZE; // minimum size: minimum number of de Bruijn nodes (fr.nodes.size())that an FR must contain to be considered frequent
    boolean useRC = USERC; // indicates if the sequence (e.g. FASTA file) had its reverse complement appended

    // the FRs
    Set<FrequentedRegion> frequentedRegions;

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

        // store the FRs in a synchronized set for parallel processing
        frequentedRegions = Collections.synchronizedSet(new TreeSet<>());
        
        // store the analyzed NodeSets in a synchronized list for parallel processing
        List<NodeSet> syncNodeSets = Collections.synchronizedList(new LinkedList<>());

        // create initial NodeSets each containing only one node; add associated FRs if they pass muster
        for (Node node : graph.nodes) {
            NodeSet nodeSet = new NodeSet();
            nodeSet.add(node);
            syncNodeSets.add(nodeSet);
            FrequentedRegion fr = new FrequentedRegion(graph, nodeSet, alpha, kappa);
            if (passesFilters(fr)) frequentedRegions.add(fr);
        }

        // build the FRs round by round
        int round = 0;
        while (round<2) {
            round++;
            // use a frozen copy of the current NodeSets
            Set<NodeSet> nsSet = new TreeSet<>();
            nsSet.addAll(syncNodeSets);
            for (NodeSet ns1 : nsSet) {
                // DEBUG
                System.out.println("ns1="+ns1);
                nsSet.parallelStream().forEach((ns2) -> {
                        //////////
                        if (!ns2.equals(ns1)) {
                            NodeSet merged = NodeSet.merge(ns1, ns2);
                            if (!syncNodeSets.contains(merged)) {
                                syncNodeSets.add(merged);
                                FrequentedRegion fr = new FrequentedRegion(graph, merged, alpha, kappa);
                                if (passesFilters(fr)) frequentedRegions.add(fr);
                            }
                        }
                        //////////
                    });
            }

            // DEBUG
            TreeSet<FrequentedRegion> sortedFRs = new TreeSet<>();
            sortedFRs.addAll(frequentedRegions);
            FrequentedRegion highest = sortedFRs.last();
            System.out.println("Round "+round+" num="+frequentedRegions.size()+
                               " highest="+highest.nodes+" totalLength="+highest.totalLength+" support="+highest.support+" avgLength="+highest.avgLength);

            // print the histogram of FR sizes
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
            System.out.println("-------------------------------------------------");
        }

        // // purge FRs that don't pass filters or support = graph.nodes.size()   || fr.support==graph.paths.size()
        // Set<FrequentedRegion> removes = new TreeSet<>();
        // for (FrequentedRegion fr : frequentedRegions) {
        //     if (fr.nodes.size()<minSize || fr.support<minSup) removes.add(fr);
        // }
        // frequentedRegions.removeAll(removes);
            
        // while (round<2) {
        //     round++;
        //     List<TreeSet<Long>> newDoneList = new LinkedList<>(); // has to be added to doneList at the end to avoid concurrent modification exception
        //     Set<FrequentedRegion> newFRs = new TreeSet<>(); // the FRs we're adding in this round
        //     for (TreeSet<Long> leftNodes : doneList) {
        //         for (TreeSet<Long> rightNodes : doneList) {
        //             if (!leftNodes.equals(rightNodes)) {
        //                 TreeSet<Long> newNodes = new TreeSet<>();
        //                 newNodes.addAll(leftNodes);
        //                 newNodes.addAll(rightNodes);
        //                 if (!doneList.contains(newNodes)) {
        //                     newDoneList.add(newNodes);
        //                     if (newNodes.size()>=minSize) {
        //                         FrequentedRegion newFR = new FrequentedRegion(newNodes, graph.nodeSequences, graph.paths, alpha, kappa);
        //                         // add this merged FR if it meets requirements
        //                         if (newFR.support>=minSup) {
        //                             newFRs.add(newFR);
        //                             if (newFR.totalLength>maxTotalLength) {
        //                                 maxTotalLength = newFR.totalLength;
        //                                 System.out.println(newFR);
        //                             }
        //                         }
        //                     }
        //                 }
        //             }
        //         }
        //     }
        //     doneList = newDoneList;
        //     frequentedRegions.addAll(newFRs);
            
        //     // DEBUG
        //     FrequentedRegion highest = frequentedRegions.last();
        //     System.out.println("Round "+round+" num="+frequentedRegions.size()+
        //                        " highest="+highest.nodes+" totalLength="+highest.totalLength+" support="+highest.support+" avgLength="+highest.avgLength);
        //     // print the histogram of FR sizes
        //     Map<Integer,Integer> countMap = new TreeMap<>();
        //     maxSize = 0;
        //     for (FrequentedRegion fr : frequentedRegions) {
        //         if (fr.nodes.size()>maxSize) maxSize = fr.nodes.size();
        //         if (countMap.containsKey(fr.nodes.size())) {
        //             int count = countMap.get(fr.nodes.size());
        //             count++;
        //             countMap.put(fr.nodes.size(), count);
        //         } else {
        //             countMap.put(fr.nodes.size(), 1);
        //         }
        //     }
        //     for (int num : countMap.keySet()) {
        //         System.out.println("FR node size (#):"+num+" ("+countMap.get(num)+")");
        //     }
        //     System.out.println("-------------------------------------------------");
            
        // }

        if (verbose) printFrequentedRegions();

        // // DEBUG
        printFrequentedRegions();
        // printPathFRs();
    }

    /**
     * Return true if the given FR passes support and size filters. Other filters could be added here.
     */
    boolean passesFilters(FrequentedRegion fr) {
        return fr.nodes.size()>=minSize && fr.support>=minSup;
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
    public int getMinSize() {
        return minSize;
    }

    // setters for optional parameters
    public void setVerbose() {
        verbose = true;
    }
    public void setMinSup(int minSup) {
        this.minSup = minSup;
    }
    public void setMinSize(int minSize) {
        this.minSize = minSize;
    }
    public void setUseRC() {
        this.useRC = true;
    }

    /**
     * Command-line utility
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {

        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        if (args.length==0) {
            System.out.println("Usage:");
            System.out.println("FRFinder [options]");
            System.exit(1);
        }
    
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
        Option labelsOption = new Option("l", "labels", true, "tab-delimited file with pathname<tab>label");
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
        Option minSupOption = new Option("m", "minsup", true, "minsup=minimum number of supporting paths for a region to be considered frequent ("+MINSUP+")");
        minSupOption.setRequired(false);
        options.addOption(minSupOption);
        //
        Option minSizeOption = new Option("z", "minsize", true, "minsize=minimum number of nodes that a FR must contain to be considered frequent ("+MINSIZE+")");
        minSizeOption.setRequired(false);
        options.addOption(minSizeOption);
        //
        Option rcOption = new Option("r", "userc", false, "useRC=flag to indicate if the sequence (e.g. FASTA) had its reverse complement appended ("+USERC+")");
        rcOption.setRequired(false);
        options.addOption(rcOption);
        //
        Option verboseOption = new Option("v", "verbose", false, "verbose output ("+VERBOSE+")");
        verboseOption.setRequired(false);
        options.addOption(verboseOption);

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
        String labelsFile = cmd.getOptionValue("labels");

        // required parameters
        double alpha = Double.parseDouble(cmd.getOptionValue("alpha"));
        int kappa = Integer.parseInt(cmd.getOptionValue("kappa"));

        // create a Graph from the dot+FASTA or JSON file
        Graph g = new Graph();
        if (cmd.hasOption("verbose")) g.setVerbose();
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
        if (labelsFile!=null) {
            g.readPathLabels(labelsFile);
        }
        
        // instantiate the FRFinder with this Graph and required parameters
        FRFinder frf = new FRFinder(g, alpha, kappa);
        
        // set optional FRFinder parameters
        boolean useRC = cmd.hasOption("rc");
        if (cmd.hasOption("verbose")) frf.setVerbose();
        if (cmd.hasOption("userc")) frf.setUseRC();
        if (cmd.hasOption("minsup")) {
            int minSup = Integer.parseInt(cmd.getOptionValue("minsup"));
            frf.setMinSup(minSup);
        }
        if (cmd.hasOption("minsize")) {
            int minSize = Integer.parseInt(cmd.getOptionValue("minsize"));
            frf.setMinSize(minSize);
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
