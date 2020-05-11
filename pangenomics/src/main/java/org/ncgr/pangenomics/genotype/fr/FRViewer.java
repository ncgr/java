package org.ncgr.pangenomics.genotype.fr;

import org.ncgr.pangenomics.genotype.*;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.TreeSet;
import java.util.Properties;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
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
    private static final int TOOLTIP_DISMISS_DELAY = 60000;
    private static final double DEFAULT_MINOR_NODE_FRAC = 0.0;
    
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
        Option decorateEdgesOption = new Option("d", "decorateedges", false, "decorate edges according to the number of paths [false]");
        decorateEdgesOption.setRequired(false);
        options.addOption(decorateEdgesOption);
        //
        Option minorNodeFracOption = new Option("m", "minornodefrac", true, "fraction of paths defining minor (uninteresting) nodes ("+DEFAULT_MINOR_NODE_FRAC+")");
        minorNodeFracOption.setRequired(false);
        options.addOption(minorNodeFracOption);

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp("FRViewer", options);
            System.exit(1);
            return;
        }

        final String prefix = cmd.getOptionValue("p");
        final boolean decorateEdges = cmd.hasOption("d");
        double mOptionValue = DEFAULT_MINOR_NODE_FRAC;
        if (cmd.hasOption("m")) mOptionValue = Double.parseDouble(cmd.getOptionValue("m"));
        final double minorNodeFrac = mOptionValue;
        
        // schedule a job for the event dispatch thread: creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    // turn off metal's use of bold fonts
                    UIManager.put("swing.boldMetal", Boolean.FALSE);
                    try {
                        // these parameters must be final or effectively final
                        createAndShowGUI(prefix, decorateEdges, minorNodeFrac);
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
    public static void createAndShowGUI(String prefix, boolean decorateEdges, double minorNodeFrac) throws IOException {
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
        graph.verbose = true;
        graph.name = graphName;
        graph.nodesFile = nodesFile;
        graph.pathsFile = pathsFile;
        graph.loadTXT();
        graph.buildNodePaths();
        graph.tallyLabelCounts();

        // load the FRs into an array, first is best
        TreeSet<FrequentedRegion> sortedFRs = FRUtils.readFrequentedRegions(prefix);
        FrequentedRegion[] frequentedRegions = new FrequentedRegion[sortedFRs.size()];
        int i = 0;
        for (FrequentedRegion fr : sortedFRs.descendingSet()) {
            fr.graph = graph;
            frequentedRegions[i++] = fr;
        }

        // create the JFrame
        JFrame frame = new JFrame(prefix);
        frame.setPreferredSize(DEFAULT_SIZE);

        // the FGraphXAdapter is an mxGraph; instantiate with the last FR in the list
        FGraphXAdapter fgxAdapter = new FGraphXAdapter(graph, frequentedRegions[0], null, decorateEdges, minorNodeFrac);

        // frGraphComponent extends mxGraphComponent which extends JScrollPane (implements Printable)
        frGraphComponent component = new frGraphComponent(graph, fgxAdapter, frequentedRegions, parameters);
        component.setBackground(Color.LIGHT_GRAY);

        // add the component to the frame, clean up frame and show
        frame.add(component);
        frame.setSize(DEFAULT_SIZE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        // set a longer delay for tooltips
        ToolTipManager.sharedInstance().setDismissDelay(TOOLTIP_DISMISS_DELAY);

        // execute the layout
        component.executeLayout();
    }
}
