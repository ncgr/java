package org.ncgr.pangenomics.allele;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;

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
 * Static class which shows a PangenomicGraph with nodes colored according to case/control degree in a JFrame.
 */
public class GraphViewer {
    private static final long serialVersionUID = 2202072534703043194L;
    private static final Dimension DEFAULT_SIZE = new Dimension(1000, 780);

    /**
     * Main application.
     */
    public static void main(String[] args) {

        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        Option graphOption = new Option("g", "graph", true, "name of graph");
        graphOption.setRequired(true);
        options.addOption(graphOption);
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
            formatter.printHelp("GraphViewer", options);
            System.exit(1);
            return;
        }

        String graphName = cmd.getOptionValue("g");
        String highlightPathName = cmd.getOptionValue("h");
        boolean decorateEdges = cmd.hasOption("d");

        // schedule a job for the event dispatch thread: creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    // turn off metal's use of bold fonts
                    UIManager.put("swing.boldMetal", Boolean.FALSE);
                    try {
                        createAndShowGUI(graphName, highlightPathName, decorateEdges);
                    } catch (Exception e) {
                        System.err.println(e);
                        System.exit(1);
                    }
                }
            });
    }

    /**
     * Do the GUI work.
     * @param graphName the name of the graph, from which the nodes and paths files will be formed
     * @param highlightPathName the name of a path or paths to be highlighted, if not null
     */
    private static void createAndShowGUI(String graphName, String highlightPathName, boolean decorateEdges) throws IOException, NullNodeException, NullSequenceException {
        String nodesFilename = graphName+".nodes.txt";
        String pathsFilename = graphName+".paths.txt";
        File nodesFile = new File(nodesFilename);
        File pathsFile = new File(pathsFilename);
        
        // get the graph
        PangenomicGraph graph = new PangenomicGraph();
        graph.setVerbose();
        graph.setName(graphName);
        graph.importTXT(nodesFile, pathsFile);
        graph.tallyLabelCounts();

        // get the highlight path(s) if provided
        List<Path> highlightPaths = new ArrayList<>();
        if (highlightPathName!=null) {
            if (graph.hasPath(highlightPathName)) {
                highlightPaths.add(graph.getPath(highlightPathName));
            } else {
                // try two genotypes
                if (graph.hasPath(highlightPathName+".0")) {
                    highlightPaths.add(graph.getPath(highlightPathName+".0"));
                }
                if (graph.hasPath(highlightPathName+".1")) {
                    highlightPaths.add(graph.getPath(highlightPathName+".1"));
                }
            }
            if (highlightPaths.size()==0) {
                System.err.println("ERROR: requested highlight path "+highlightPathName+" is not in graph.");
                System.exit(1);
            }
        }

        // PGraphXAdapter extends JGraphXAdapter which extends mxGraph
        PGraphXAdapter pgxAdapter = new PGraphXAdapter(graph, highlightPaths, decorateEdges);

        // pgGraphComponent extends mxGraphComponent
        pgGraphComponent component = new pgGraphComponent(graph, pgxAdapter);

        // Create and populate the JFrame
        JFrame frame = new JFrame(graphName);
        frame.setPreferredSize(DEFAULT_SIZE);
        frame.getContentPane().add(component);
        frame.setSize(DEFAULT_SIZE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        // mxHierarchicalLayout -- good! WEST orientation is best.
        mxHierarchicalLayout layout = new mxHierarchicalLayout(pgxAdapter, SwingConstants.WEST);
        layout.setFineTuning(true);

        // lay out the layout
        layout.execute(pgxAdapter.getDefaultParent());
    }
}


