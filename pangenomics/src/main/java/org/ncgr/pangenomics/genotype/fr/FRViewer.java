package org.ncgr.pangenomics.genotype.fr;

import org.ncgr.pangenomics.genotype.*;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.Properties;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;

/**
 * A static class that displays a PangenomicGraph along with FrequentedRegions in a JFrame.
 */
public class FRViewer {
    private static final long serialVersionUID = 2202072534703043194L;
    private static final Dimension DEFAULT_SIZE = new Dimension(1200, 780);

    /**
     * Main application.
     */
    public static void main(String[] args) {

        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        Option prefixOption = new Option("p", "prefix", true, "prefix of FR files, e.g. HLAB.601-1.0-0");
        prefixOption.setRequired(true);
        options.addOption(prefixOption);
        //
        Option highlightPathOption = new Option("h", "highlightpath", true, "path to highlight");
        highlightPathOption.setRequired(false);
        options.addOption(highlightPathOption);
        //
        Option decorateEdgesOption = new Option("d", "decorateedges", false, "decorate edges according to the number of paths [false]");
        decorateEdgesOption.setRequired(false);
        options.addOption(decorateEdgesOption);

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp("FRViewer", options);
            System.exit(1);
            return;
        }

        String prefix = cmd.getOptionValue("p");
        String highlightPathName = cmd.getOptionValue("h");
        boolean decorateEdges = cmd.hasOption("d");
        
        // schedule a job for the event dispatch thread: creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    // turn off metal's use of bold fonts
                    UIManager.put("swing.boldMetal", Boolean.FALSE);
                    try {
                        createAndShowGUI(prefix, highlightPathName, decorateEdges);
                    } catch (Exception e) {
                        System.err.println(e);
                        System.exit(1);
                    }
                }
            });
    }

    /**
     * Create and show the Swing GUI.
     *
     * @param prefix the FRFinder run prefix, e.g. HTT-0.1-100
     */
    public static void createAndShowGUI(String prefix, String highlightPathName, boolean decorateEdges) throws IOException, NullNodeException, NullSequenceException {
        String[] pieces = prefix.split("-");
        String graphName = pieces[0];
        double alpha = Double.parseDouble(pieces[1]);
        int kappa = Integer.MAX_VALUE;
        if (!pieces[2].equals("Inf")) kappa = Integer.parseInt(pieces[2]);

        String nodesFilename = graphName+".nodes.txt";
        String pathsFilename = graphName+".paths.txt";
        File nodesFile = new File(nodesFilename);
        File pathsFile = new File(pathsFilename);

        // read run properties from params file
        Properties parameters = FRUtils.readParameters(prefix);
        
        // load the graph
        PangenomicGraph graph = new PangenomicGraph();
        graph.setVerbose();
        graph.setName(graphName);
        graph.importTXT(nodesFile, pathsFile);
        graph.tallyLabelCounts();

        // get the highlight path if provided
        Path highlightPath = null;
        if (highlightPathName!=null) {
            highlightPath = graph.getPath(highlightPathName);
            if (highlightPath==null) {
                System.err.println("ERROR: requested highlight path "+highlightPathName+" is not in graph.");
                System.exit(1);
            }
        }

        // load the FRs into a sorted map so we see the juicy ones first
        Map<String,FrequentedRegion> unsortedFRs = FRUtils.readFrequentedRegions(prefix, graph);
        FRpriorityComparator frComparator = new FRpriorityComparator(unsortedFRs);
        TreeMap<String,FrequentedRegion> frequentedRegions = new TreeMap<>(frComparator);
        frequentedRegions.putAll(unsortedFRs);
        
        // create the JFrame
        JFrame frame = new JFrame(prefix);
        frame.setPreferredSize(DEFAULT_SIZE);

        // the FGraphXAdapter is an mxGraph; instantiate with the first FR in the list
        Object[] frKeys = frequentedRegions.keySet().toArray();
        FGraphXAdapter fgxAdapter = new FGraphXAdapter(graph, frequentedRegions.get((String)frKeys[0]), highlightPath, decorateEdges);

        // frGraphComponent extends mxGraphComponent which extends JScrollPane (implements Printable)
        frGraphComponent component = new frGraphComponent(graph, frequentedRegions, fgxAdapter, parameters, highlightPath, decorateEdges);
        component.setBackground(Color.LIGHT_GRAY);

        // add the component to the frame, clean up frame and show
        frame.add(component);
        frame.setSize(DEFAULT_SIZE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        // execute the layout
        component.executeLayout();
    }
}
