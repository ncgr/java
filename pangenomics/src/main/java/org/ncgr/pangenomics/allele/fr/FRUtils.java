package org.ncgr.pangenomics.allele.fr;

import org.ncgr.pangenomics.allele.*;

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
import java.util.TreeSet;
import java.util.Properties;

/**
 * Static methods to read FrequentedRegions and do the other things.
 *
 * @author Sam Hokin
 */
public class FRUtils {    
    /**
     * Read FRs from the text files written by a FRFinder run, given the graph as well.
     * [18,34]	70	299	54	16
     * 509678.0.ctrl:[18,20,21,23,24,26,27,29,30,33,34]
     * 628863.1.case:[18,20,21,23,24,26,27,29,30,33,34]
     * etc.
     */
    public static TreeSet<FrequentedRegion> readFrequentedRegions(String inputPrefix, PangenomicGraph graph) throws FileNotFoundException, IOException {
        // get alpha, kappa from the input prefix
        double alpha = readAlpha(inputPrefix);
        int kappa = readKappa(inputPrefix);
        String priorityOption = readPriorityOption(inputPrefix);
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
            // 0                1       2       3       4           5           6   7       8
            // nodes            size    support avgLen  caseSupport ctrlSupport OR  p       priority
            // [2,7,9,10,14,15]	6	24	52.79	24	    0	        ∞   2.66E-3 257
            // 371985.1.case:[2]
            // 131922.1.case:[2]
            // ...
            String[] fields = line.split("\t");
            NodeSet nodes = graph.getNodeSet(fields[0]);
            int size = Integer.parseInt(fields[1]);
            int support = Integer.parseInt(fields[2]);
            double avgLength = Double.parseDouble(fields[3]);
            int caseSupport = Integer.parseInt(fields[4]);
            int ctrlSupport = Integer.parseInt(fields[5]);
            double or = Double.POSITIVE_INFINITY;
            try {
                or = Double.parseDouble(fields[6]);
            } catch (NumberFormatException e) {
                // do nothing, it's an infinity symbol
            }
            double p = Double.parseDouble(fields[7]);
            int priority = Integer.parseInt(fields[8]);
            List<Path> subpaths = new ArrayList<>();
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
                subpaths.add(new Path(graph, subNodes, name, genotype, label, graph.getSkipSequences()));
            }
            FrequentedRegion fr = new FrequentedRegion(graph, nodes, subpaths, alpha, kappa, priorityOption, support, avgLength);
            sortedFRs.add(fr);
        }
        return sortedFRs;
    }

    /**
     * Read FRs with only parameters from the text file written by a FRFinder run.
     * This constructor populates the FRs with only their main class variables; no subpaths, for example.
     * 0                1       2       3       4           5           6   7       8
     * nodes            size    support avgLen  caseSupport ctrlSupport OR  p       priority
     * [2,7,9,10,14,15]	6	24	52.79	24	    0	        ∞   2.66E-3 257
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
            if (line.startsWith("nodes")) continue; // heading
            String[] fields = line.split("\t");
            NodeSet nodes = new NodeSet(fields[0]);
            int size = Integer.parseInt(fields[1]);
            int support = Integer.parseInt(fields[2]);
            double avgLen = Double.parseDouble(fields[3]);
            int caseSupport = Integer.parseInt(fields[4]);
            int ctrlSupport = Integer.parseInt(fields[5]);
            double orValue = Double.POSITIVE_INFINITY;
            try {
                orValue = Double.parseDouble(fields[6]);
            } catch (NumberFormatException e) {
                // do nothing, it's an infinity symbol
            }
            double pValue = Double.parseDouble(fields[7]);
            int priority = Integer.parseInt(fields[8]);
            FrequentedRegion fr = new FrequentedRegion(nodes, alpha, kappa, support, avgLen, caseSupport, ctrlSupport, orValue, pValue, priority);
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
}
