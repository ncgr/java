package org.ncgr.pangenomics.fr;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

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
    private Graph g;      // the Graph we're analyzing
    private double alpha; // penetrance: the fraction of a supporting strain's sequence that actually supports the FR; alternatively, `1-alpha` is the fraction of inserted sequence
    private int kappa;    // maximum insertion: the maximum insertion length (measured in bp) that any supporting path may have
 
    // optional parameters, set with setters
    private boolean verbose = VERBOSE;
    private int minSup = MINSUP;   // minimum support: minimum number of genome paths in order for a region to be considered frequent
    private int minSize = MINSIZE; // minimum size: minimum number of de Bruijn nodes that an FR must contain to be considered frequent
    private boolean useRC = USERC; // indicates if the sequence (e.g. FASTA file) had its reverse complement appended

    // the ordered collection of all FRs
    private TreeSet<FrequentedRegion> frequentedRegions;

    // maps a nodeId to the set of paths that contain it
    TreeMap<Long,Set<Path>> nodePaths; // keyed and ordered by nodeId

    /**
     * Construct with a populated Graph and required parameters
     */
    public FRFinder(Graph g, double alpha, int kappa) {
        this.g = g;
        this.alpha = alpha;
        this.kappa = kappa;
    }

    /**
     * Find the frequented regions in this Graph.
     */
    public void findFRs() {
        if (verbose) {
            printNodes();
            printPaths();
        }
            
        // initialize nodePaths with those in g
        nodePaths = g.nodePaths;
        if (verbose) printNodePaths();

        // create initial FRs, each containing only one node and all of its paths
        frequentedRegions = new TreeSet<>();
        for (long nodeId : nodePaths.keySet()) {
            TreeSet<Long> nodes = new TreeSet<>();
            nodes.add(nodeId);
            FrequentedRegion cluster = new FrequentedRegion(nodes, g.paths, g.nodeSequences, alpha, kappa);
            frequentedRegions.add(cluster);
        }

        // spin through the FRs, adding merged pairs if they have non-zero support, until we reach equilibrium
        int round = 0;
        FrequentedRegion newLowest = frequentedRegions.first();
        FrequentedRegion oldLowest = frequentedRegions.higher(newLowest);
        while (!oldLowest.equals(newLowest)) {
            round++;
            oldLowest = frequentedRegions.first();
            Set<FrequentedRegion> newFRs = new TreeSet<>();
            for (FrequentedRegion fr1 : frequentedRegions) {
                FrequentedRegion fr2 = frequentedRegions.higher(fr1);
                if (fr2!=null) {
                    FrequentedRegion merged = FrequentedRegion.merge(fr1, fr2, alpha, kappa);
                    if (merged.fwdSupport>0) newFRs.add(merged);
                }
            }
            frequentedRegions.addAll(newFRs);
            // drop the lowest one
            frequentedRegions.remove(frequentedRegions.first());
            // get the new lowest one
            newLowest = frequentedRegions.first();
            // DEBUG
            System.out.println("Round "+round+" num="+frequentedRegions.size()+" oldLowest="+oldLowest.nodes+" newLowest="+newLowest.nodes);
        }

        // cull out the FRs that have support = # paths (they're very not interesting)
        Set<FrequentedRegion> removeThese = new TreeSet<>();
        for (FrequentedRegion fr : frequentedRegions) {
            if (fr.fwdSupport==g.paths.size()) removeThese.add(fr);
        }
        frequentedRegions.removeAll(removeThese);

        if (verbose) printFrequentedRegions();
        printFrequentedRegions();

        printPathFRs();
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
        Option categoriesOption = new Option("c", "categories", true, "tab-delimited file with path->categories");
        categoriesOption.setRequired(false);
        options.addOption(categoriesOption);
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
        String categoriesFile = cmd.getOptionValue("categories");

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

        // if a categories file is given, append the categories to the path names
        if (categoriesFile!=null) {
            g.setPathCategories(categoriesFile);
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
     * Print out the nodes along with a k histogram.
     */
    void printNodes() {
        Map<Integer,Integer> countMap = new TreeMap<>();
        printHeading("NODES");
        for (long nodeId : g.nodeSequences.keySet()) {
            String sequence = g.nodeSequences.get(nodeId);
            int length = sequence.length();
            if (countMap.containsKey(length)) {
                countMap.put(length, ((int)countMap.get(length))+1);
            } else {
                countMap.put(length, 1);
            }
            System.out.println(nodeId+"("+length+"):"+sequence);
        }
        printHeading("k HISTOGRAM");
        for (int len : countMap.keySet()) {
            int counts = countMap.get(len);
            System.out.print("length="+len+"\t("+counts+")\t");
            for (int i=1; i<=counts; i++) System.out.print("X");
            System.out.println("");
        }
    }

    /**
     * Print the paths, labeled by pathName.
     */
    void printPaths() {
        printHeading("PATHS");
        for (Path path : g.paths) {
            System.out.print(path.getLabel()+":");
            for (long nodeId : path.nodes) {
                System.out.print(" "+nodeId);
            }
            System.out.println("");
        }
    }

    /**
     * Print out the node paths along with counts.
     */
    void printNodePaths() {
        printHeading("NODE PATHS");
        for (long nodeId : nodePaths.keySet()) {
            Set<Path> paths = nodePaths.get(nodeId);
            String asterisk = " ";
            if (paths.size()==g.paths.size()) asterisk="*";
            System.out.print(asterisk+nodeId+"("+paths.size()+"):");
            for (Path path : paths) {
                System.out.print(" "+path.name);
            }
            System.out.println("");
        }
    }

    /**
     * Print out the FRs.
     */
    void printFrequentedRegions() {
        printHeading("FREQUENTED REGIONS");
        int c = 1;
        for (FrequentedRegion fr : frequentedRegions) {
            System.out.print(c+"\t");
            System.out.println(fr.toString());
            c++;
        }
    }

    /**
     * Print the sequences for each path, labeled by pathName.
     */
    void printPathSequences() {
        printHeading("PATH SEQUENCES");
        for (String pathName : g.pathSequences.keySet()) {
            String sequence = g.pathSequences.get(pathName);
            int length = sequence.length();
            String heading = ">"+pathName+" ("+length+")";
            System.out.print(heading);
            for (int i=heading.length(); i<19; i++) System.out.print(" "); System.out.print(".");
            for (int n=0; n<19; n++) {
                for (int i=0; i<9; i++) System.out.print(" "); System.out.print(".");
            }
            System.out.println("");
            // // entire sequence
            // System.out.println(sequence);
            // trimmed sequence beginning and end
            System.out.println(sequence.substring(0,100)+"........."+sequence.substring(length-101,length));
        }
    }

    /**
     * Print the path names and the FRs that have subpaths belonging to those paths.
     * This can be used as input to a classification routine.
     */
    void printPathFRs() {
        printHeading("PATH FREQUENTED REGIONS");
        // columns
        System.out.print("Path\tCat");
        int c = 1;
        for (FrequentedRegion fr : frequentedRegions) {
            System.out.print("\t"+c);
            c++;
        }
        System.out.println("");
        // rows
        for (Path path : g.paths) {
            System.out.print(path.name+"\t"+path.category);
            for (FrequentedRegion fr : frequentedRegions) {
                System.out.print("\t"+fr.countSubpathsOf(path));
            }
            System.out.println("");
        }
    }
}
