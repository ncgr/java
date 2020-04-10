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
import java.util.TreeMap;
import java.util.Properties;

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
    public static TreeMap<String,FrequentedRegion> readFrequentedRegions(String inputPrefix, PangenomicGraph graph) throws FileNotFoundException, IOException {
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
        Map<String,FrequentedRegion> unsortedFRs = new HashMap<>();
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
            FrequentedRegion fr = new FrequentedRegion(graph, nodes, subpaths, alpha, kappa, priorityOption, support);
            unsortedFRs.put(fr.nodes.toString(), fr);
        }
        // now sort them in a TreeMap
        FRpriorityComparator frComparator = new FRpriorityComparator(unsortedFRs);
        TreeMap<String,FrequentedRegion> frequentedRegions = new TreeMap<>(frComparator);
        frequentedRegions.putAll(unsortedFRs);
        return frequentedRegions;
    }

    /**
     * Read FRs with only parameters from the text file written by a FRFinder run.
     * This constructor populates the FRs with only their main class variables; no subpaths, for example.
     * 0       1       2       3       4       5       6       7
     * nodes   size    support case    ctrl    OR      p       pri
     * [1341]  1       21      19      2       9.500   9.48E-3 202
     */
    public static TreeMap<String,FrequentedRegion> readFrequentedRegions(String inputPrefix) throws FileNotFoundException, IOException {
        // get alpha, kappa from the input prefix
        double alpha = readAlpha(inputPrefix);
        int kappa = readKappa(inputPrefix);
        // read the FRs
        Map<String,FrequentedRegion> unsortedFRs = new HashMap<>();
        String frFilename = getFRsFilename(inputPrefix);
        BufferedReader reader = new BufferedReader(new FileReader(frFilename));
        String line = null;
        while ((line=reader.readLine())!=null) {
            if (line.startsWith("nodes")) continue; // heading
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
            unsortedFRs.put(fr.nodes.toString(), fr);
        }
        // now sort them in a TreeMap
        FRpriorityComparator frComparator = new FRpriorityComparator(unsortedFRs);
        TreeMap<String,FrequentedRegion> frequentedRegions = new TreeMap<>(frComparator);
        frequentedRegions.putAll(unsortedFRs);
        return frequentedRegions;
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
