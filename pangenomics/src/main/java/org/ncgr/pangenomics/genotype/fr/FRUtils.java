package org.ncgr.pangenomics.genotype.fr;

import org.ncgr.pangenomics.genotype.*;

import org.jgrapht.GraphPath;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import java.text.DecimalFormat;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Static methods to read FrequentedRegions and do the other things.
 *
 * @author Sam Hokin
 */
public class FRUtils {    

    /**
     * Read FRs from the text files written by a FRFinder run, given the graph as well.
     * This constructor populates the FRs fully, with subpaths and the whole bit.
     *
     * nodes   size    support case    ctrl    OR      p       pri
     * [1341]  1       21      19      2       9.500   9.48E-3 202
     * 509678.ctrl:[18,20,21,23,24,26,27,29,30,33,34]
     * 628863.case:[18,20,21,23,24,26,27,29,30,33,34]
     * etc.
     */
    public static TreeSet<FrequentedRegion> readFrequentedRegions(String inputPrefix, PangenomicGraph graph) throws FileNotFoundException, IOException {
        // get alpha, kappa from the input prefix
        double alpha = readAlpha(inputPrefix);
        int kappa = readKappa(inputPrefix);
        // parse the priorityOption and impose defaults
        String priorityOption = readPriorityOption(inputPrefix);
        String[] priorityParts = priorityOption.split(":");
        int priorityOptionKey = Integer.parseInt(priorityParts[0]);
        String priorityOptionLabel = null;
        if (priorityParts.length>1) {
            String priorityOptionParameter = priorityParts[1];
            if (priorityParts[1].equals("case") || priorityParts[1].equals("ctrl")) {
                priorityOptionLabel = priorityParts[1];
            }
        }
        if (priorityOptionKey==1 && priorityOptionLabel==null) priorityOptionLabel = "case";
        if (priorityOptionKey==3 && priorityOptionLabel==null) priorityOptionLabel = "case";
        // get the graph from the nodes and paths files
        File nodesFile = new File(getNodesFilename(inputPrefix));
        File pathsFile = new File(getPathsFilename(inputPrefix));
        // create a node map for building subpaths
        Map<Long,Node> nodeMap = new HashMap<>();
        for (Node n : graph.getNodes()) {
            nodeMap.put(n.id, n);
        }
        // build the FRs
        TreeSet<FrequentedRegion> sortedFRs = new TreeSet<>();
        String subpathsFilename = getFRSubpathsFilename(inputPrefix);
        BufferedReader reader = new BufferedReader(new FileReader(subpathsFilename));
        String line = null;
        while ((line=reader.readLine())!=null) {
            // 0                1       2       3           4           5     6       7
            // nodes            size    support caseSupport ctrlSupport OR    p       priority
            // [1341]           1       21      19          2           9.500 9.48E-3 202
            // 429266.case:1341]
            // 158005.case:1341]
            // ...
            String[] fields = line.split("\t");
            NodeSet nodes = graph.getNodeSet(fields[0]);
            int size = Integer.parseInt(fields[1]);
            int support = Integer.parseInt(fields[2]);
            int caseSupport = Integer.parseInt(fields[3]);
            int ctrlSupport = Integer.parseInt(fields[4]);
            double or = Double.POSITIVE_INFINITY;
            try {
                or = Double.parseDouble(fields[5]);
            } catch (NumberFormatException e) {
                // do nothing, it's an infinity symbol
            }
            double p = Double.parseDouble(fields[6]);
            int priority = Integer.parseInt(fields[7]);
            List<Path> subpaths = new ArrayList<>();
            for (int i=0; i<support; i++) {
                line = reader.readLine();
                String[] parts = line.split(":");
                String pathFull = parts[0];
                String nodeString = parts[1];
                // split out the name, label, nodes
                String[] nameParts = pathFull.split("\\.");
                String name = nameParts[0];
                String label = null;
                if (nameParts.length>1) label = nameParts[1];
                List<Node> subNodes = new ArrayList<>();
                String[] nodesAsStrings = nodeString.replace("[","").replace("]","").split(",");
                for (String nodeAsString : nodesAsStrings) {
		    long nodeId = Long.parseLong(nodeAsString);
                    subNodes.add(nodeMap.get(nodeId));
                }
                // add to the subpaths
                subpaths.add(new Path(graph, subNodes, name, label));
            }
            FrequentedRegion fr = new FrequentedRegion(graph, nodes, subpaths, alpha, kappa, priorityOptionKey, priorityOptionLabel, support);
            sortedFRs.add(fr);
        }
        return sortedFRs;
    }

    /**
     * Read FRs with only parameters from the text file written by a FRFinder run.
     * This constructor populates the FRs with only their main class variables; no subpaths, for example.
     * 0       1       2       3       4       5       6       7
     * nodes   size    support case    ctrl    OR      p       pri
     * [1341]  1       21      19      2       9.500   9.48E-3 202
     */
    public static TreeSet<FrequentedRegion> readFrequentedRegions(String inputPrefix) throws FileNotFoundException, IOException {
        // get alpha, kappa from the input prefix
        double alpha = readAlpha(inputPrefix);
        int kappa = readKappa(inputPrefix);
        // read the FRs
        TreeSet<FrequentedRegion> sortedFRs = new TreeSet<>();
        String frFilename = getFRsFilename(inputPrefix);
        BufferedReader reader = new BufferedReader(new FileReader(frFilename));
        String line = null;
        while ((line=reader.readLine())!=null) {
            if (!line.startsWith("[")) continue; // heading
            String[] fields = line.split("\t");
            NodeSet nodes = new NodeSet(fields[0]);
            int size = Integer.parseInt(fields[1]);
            int support = Integer.parseInt(fields[2]);
            int caseSupport = Integer.parseInt(fields[3]);
            int ctrlSupport = Integer.parseInt(fields[4]);
            double orValue = Double.POSITIVE_INFINITY;
            try {
                orValue = Double.parseDouble(fields[5]);
            } catch (NumberFormatException e) {
                // do nothing, it's an infinity symbol
            }
            double pValue = Double.parseDouble(fields[6]);
            int priority = Integer.parseInt(fields[7]);
            FrequentedRegion fr = new FrequentedRegion(nodes, alpha, kappa, support, caseSupport, ctrlSupport, orValue, pValue, priority);
            sortedFRs.add(fr);
        }
        return sortedFRs;
    }

    /**
     * Return alpha from an FRFinder run.
     */
    public static double readAlpha(String inputPrefix) throws FileNotFoundException, IOException {
        String paramsFilename = getParamsFilename(inputPrefix);
        BufferedReader reader = new BufferedReader(new FileReader(paramsFilename));
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
     * Return kappa from a an FRFinder run.
     */
    public static int readKappa(String inputPrefix) throws FileNotFoundException, IOException {
        String paramsFilename = getParamsFilename(inputPrefix);
        BufferedReader reader = new BufferedReader(new FileReader(paramsFilename));
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
     * Return priorityOption from an FRFinder run.
     */
    public static String readPriorityOption(String inputPrefix) throws FileNotFoundException, IOException {
        String paramsFilename = getParamsFilename(inputPrefix);
        BufferedReader reader = new BufferedReader(new FileReader(paramsFilename));
        Properties parameters = new Properties();
        parameters.load(reader);
        return parameters.getProperty("priorityOption");
    }        

    /**
     * Form the FRs output filename
     */
    public static String getFRsFilename(String prefix) {
        return prefix+".frs.txt";
    }

    /**
     * Form the FRSubpaths output filename
     */
    public static String getFRSubpathsFilename(String prefix) {
        return prefix+".subpaths.txt";
    }

    /**
     * Form the pathFRs output filename
     */
    public static String getPathFRsFilename(String prefix) {
        return prefix+".pathfrs.txt";
    }

    /**
     * Form the SVM version of the pathFRs output filename
     */
    public static String getPathFRsSVMFilename(String prefix) {
        return prefix+".svm.txt";
    }

    /**
     * Form the ARFF version of the pathFRs output filename
     */
    public static String getPathFRsARFFFilename(String prefix) {
        return prefix+".arff";
    }

    /**
     * Form the parameters output filename
     */
    public static String getParamsFilename(String prefix) {
        return prefix+".params.txt";
    }

    /**
     * Form the graph nodes filename
     * if prefix = HTT.1k-1.0-0 then filename = HTT.nodes.txt
     */
    public static String getNodesFilename(String prefix) {
        String[] parts = prefix.split("-");
        return parts[0]+".nodes.txt";
    }

    /**
     * Form the graph paths filename
     * if prefix = HTT.1k-1.0-0 then filename = HTT.paths.txt
     */
    public static String getPathsFilename(String prefix) {
        String[] parts = prefix.split("-");
        return parts[0]+".paths.txt";
    }

    /**
     * Print out the parameters.
     */
    public static void printParameters(Properties parameters, String outputPrefix, double alpha, int kappa, long clockTime) throws IOException {
        PrintStream out = new PrintStream(getParamsFilename(outputPrefix));
        String comments = "alpha="+alpha+"\n"+"kappa="+kappa+"\n"+"clocktime="+formatTime(clockTime);
        parameters.store(out, comments);
        out.close();
    }
    
    /**
     * Read the parameters from a previous run's properties file.
     */
    public static Properties readParameters(String inputPrefix) throws FileNotFoundException, IOException {
        String paramsFilename = getParamsFilename(inputPrefix);
        BufferedReader reader = new BufferedReader(new FileReader(paramsFilename));
        Properties parameters = new Properties();
        parameters.load(reader);
        parameters.setProperty("paramsFile", paramsFilename);
        parameters.setProperty("inputPrefix", inputPrefix);
        return parameters;
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

    // /**
    //  * Read FRs from the output from a previous run.
    //  * [18,34]	70	299	54	16
    //  * 509678.0.ctrl:[18,20,21,23,24,26,27,29,30,33,34]
    //  * 628863.1.case:[18,20,21,23,24,26,27,29,30,33,34]
    //  * etc.
    //  */
    // void readFrequentedRegions() throws FileNotFoundException, IOException {
    //     // get alpha, kappa from the input prefix
    //     double alpha = FRUtils.readAlpha(inputPrefix);
    //     int kappa = FRUtils.readKappa(inputPrefix);
    //     // get the graph from the nodes and paths files
    //     graph = new PangenomicGraph();
    //     graph.nodesFile = new File(FRUtils.getNodesFilename(inputPrefix));
    //     graph.pathsFile = new File(FRUtils.getPathsFilename(inputPrefix));
    //     graph.loadTXT();
    //     // create a node map for building subpaths
    //     Map<Long,Node> nodeMap = new HashMap<>();
    //     for (Node n : graph.getNodes()) {
    //         nodeMap.put(n.id, n);
    //     }
    // 	// build the FRs
    //     frequentedRegions = new ConcurrentHashMap<>();
    //     String frFilename = FRUtils.getFRSubpathsFilename(inputPrefix);
    //     BufferedReader reader = new BufferedReader(new FileReader(frFilename));
    //     String line = null;
    //     while ((line=reader.readLine())!=null) {
    //         String[] fields = line.split("\t");
    //         NodeSet nodes = graph.getNodeSet(fields[0]);
    //         int support = Integer.parseInt(fields[1]);
    //         List<Path> subpaths = new ArrayList<>();
    //         for (int i=0; i<support; i++) {
    //             line = reader.readLine();
    //             String[] parts = line.split(":");
    //             String pathFull = parts[0];
    //             String nodeString = parts[1];
    //             // split out the name, label, nodes
    //             String[] nameParts = pathFull.split("\\.");
    //             String name = nameParts[0];
    //             String label = label = nameParts[2];
    //             List<Node> subNodes = new ArrayList<>();
    //             String[] nodesAsStrings = nodeString.replace("[","").replace("]","").split(",");
    //             for (String nodeAsString : nodesAsStrings) {
    // 		    long nodeId = Long.parseLong(nodeAsString);
    //                 subNodes.add(nodeMap.get(nodeId));
    //             }
    //             // add to the subpaths
    //             subpaths.add(new Path(graph, subNodes, name, label));
    //         }
    //         FrequentedRegion fr = new FrequentedRegion(nodes, subpaths, alpha, kappa, priorityOptionKey, priorityOptionLabel, support);
    //         frequentedRegions.put(fr.nodes.toString(), fr);
    //     }
    // }
    
    /**
     * Form an outputPrefix from inputPrefix, minSupport, and minSize.
     */
    public static String formOutputPrefix(String inputPrefix, int minSupport, int minSize) {
        return inputPrefix+"-"+minSupport+"."+minSize;
    }

    /**
     * Post-process a list of FRs read in from the inputPrefix file for given minSupport and minSize.
     * Outputs a new set of FR files with minSupport and minSize in the outputPrefix.
     */
    public static void postprocess(String inputPrefix, int minSupport, int minSize) throws FileNotFoundException, IOException {
	Properties parameters = readParameters(inputPrefix);
	parameters.setProperty("minSupport", String.valueOf(minSupport));
	parameters.setProperty("minSize", String.valueOf(minSize));
	Set<FrequentedRegion> frequentedRegions = readFrequentedRegions(inputPrefix);
	Set<FrequentedRegion> filteredFRs = new TreeSet<>();
        for (FrequentedRegion fr : frequentedRegions) {
            boolean passes = true;
            String reason = "";
            if (fr.support<minSupport) {
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
            if (passes) filteredFRs.add(fr);
        }
        System.out.println(filteredFRs.size()+" FRs passed minSupport="+minSupport+", minSize="+minSize);
	// output the filtered FRs
	if (filteredFRs.size()>0) {
	    printFrequentedRegions(filteredFRs, formOutputPrefix(inputPrefix, minSupport, minSize));
	}
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
    public static void printPathFRsSVM(String inputPrefix) throws IOException {
	// load the graph
	PangenomicGraph graph = new PangenomicGraph();
	graph.nodesFile = new File(getNodesFilename(inputPrefix));
	graph.pathsFile = new File(getPathsFilename(inputPrefix)); 
	graph.loadTXT();
	graph.tallyLabelCounts();
	// load the frequented regions and update support
	Set<FrequentedRegion> frequentedRegions = readFrequentedRegions(inputPrefix, graph);
	for (FrequentedRegion fr : frequentedRegions) {
	    fr.updateSupport();
	}
	// output path by path
        PrintStream out = new PrintStream(getPathFRsSVMFilename(inputPrefix));
        // no header, one path per row
        for (Path path : graph.paths) {
            out.print(path.name+"."+path.label);
            // TODO: update these to strings along with fixing the SVM code to handle strings
            String group = "";
            if (path.label!=null) group = path.label;
            out.print("\t"+group);
            int c = 0;
            for (FrequentedRegion fr : frequentedRegions) {
                c++;
		int subpathCount = fr.countSubpathsOf(path);
                out.print("\t"+c+":"+subpathCount);
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
    public static void printPathFRsARFF(String inputPrefix) throws IOException {
        // load the graph
	PangenomicGraph graph = new PangenomicGraph();
        graph.nodesFile = new File(getNodesFilename(inputPrefix));
        graph.pathsFile = new File(getPathsFilename(inputPrefix));
        graph.loadTXT();
	graph.tallyLabelCounts();
	// load the frequented regions and update support
	Set<FrequentedRegion> frequentedRegions = readFrequentedRegions(inputPrefix, graph);
	for (FrequentedRegion fr : frequentedRegions) {
	    fr.updateSupport();
	}
	// ARFF output	
        PrintStream out = new PrintStream(FRUtils.getPathFRsARFFFilename(inputPrefix));
        out.println("@RELATION "+inputPrefix);
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
        for (Path path : graph.paths) {
            out.print(path.name+"."+path.label+",");
            c = 0;
            for (FrequentedRegion fr : frequentedRegions) {
                c++;
                out.print(fr.countSubpathsOf(path)+",");
            }
            out.println(path.label);
        }
        out.close();
    }

    /**
     * Main class with methods for post-processing.
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        Option inputPrefixOption = new Option("i", "inputprefix", true, "prefix of input files (e.g. HLAA-0.1-Inf)");
        inputPrefixOption.setRequired(true);
        options.addOption(inputPrefixOption);
	//
	Option postprocessOption = new Option("p", "postprocess", false, "postprocess by applying new minsup, minsize to an FR set given by inputprefix");
	postprocessOption.setRequired(false);
	options.addOption(postprocessOption);
        //
        Option minSupportOption = new Option("m", "minsupport", true, "minimum number of supporting paths for a region to be considered interesting");
        minSupportOption.setRequired(false);
        options.addOption(minSupportOption);
        //
        Option minSizeOption = new Option("s", "minsize", true, "minimum number of nodes that a FR must contain to be considered interesting");
        minSizeOption.setRequired(false);
        options.addOption(minSizeOption);
	//
	Option printPathFRsSVMOption = new Option("svm", "svm", false, "print out an SVM style file from the data given by inputprefix");
	printPathFRsSVMOption.setRequired(false);
	options.addOption(printPathFRsSVMOption);
	//
	Option printPathFRsARFFOption = new Option("arff", "arff", false, "print out an ARFF style file from the data given by inputprefix");
	printPathFRsARFFOption.setRequired(false);
	options.addOption(printPathFRsARFFOption);

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp("FRUtils", options);
            System.exit(1);
            return;
        }

        if (cmd.getOptions().length==0) {
            formatter.printHelp("FRUtils", options);
            System.exit(1);
            return;
        }
	
	String inputPrefix = cmd.getOptionValue("i");

	if (cmd.hasOption("p")) {
	    int minSupport = Integer.parseInt(cmd.getOptionValue("m"));
	    int minSize = Integer.parseInt(cmd.getOptionValue("s"));
	    postprocess(inputPrefix, minSupport, minSize);
	}

	if (cmd.hasOption("svm")) {
	    printPathFRsSVM(inputPrefix);
	}

	if (cmd.hasOption("arff")) {
	    printPathFRsARFF(inputPrefix);
	}
    }

    /**
     * Print out a set of FRs.
     */
    public static void printFrequentedRegions(Set<FrequentedRegion> frequentedRegions, String inputPrefix) throws IOException {
        if (frequentedRegions.size()==0) {
            System.err.println("NO FREQUENTED REGIONS!");
            return;
        }
        PrintStream out = new PrintStream(FRUtils.getFRsFilename(inputPrefix));
        boolean first = true;
        for (FrequentedRegion fr : frequentedRegions) {
            if (first) {
                out.println(fr.columnHeading());
                first = false;
            }
            out.println(fr.toString());
        }
        out.close();
    }
}
