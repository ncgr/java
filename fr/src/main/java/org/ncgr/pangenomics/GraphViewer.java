package org.ncgr.pangenomics;

import com.mxgraph.layout.*;
import com.mxgraph.layout.orthogonal.*;
import com.mxgraph.layout.hierarchical.*;
import com.mxgraph.model.*;
import com.mxgraph.swing.*;
import com.mxgraph.util.*;
import com.mxgraph.view.*;

import org.jgrapht.*;
import org.jgrapht.ext.*;
import org.jgrapht.graph.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.*;
import java.util.*;

/**
 * Static class which shows a PangenomicGraph with nodes colored according to case/control degree in a JFrame.
 */
public class GraphViewer {
    private static final long serialVersionUID = 2202072534703043194L;
    private static final Dimension DEFAULT_SIZE = new Dimension(1000, 780);

    /**
     * Main application.
     *
     * @param args command line arguments: graphName [highlightPathName]
     */
    public static void main(String[] args) {
        if (args.length==0 || args.length>2) {
            System.out.println("Usage: GraphViewer <graph> [highlight path name]");
            System.exit(0);
        }
        
        // schedule a job for the event dispatch thread: creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    // turn off metal's use of bold fonts
                    UIManager.put("swing.boldMetal", Boolean.FALSE);
                    try {
                        if (args.length==2) {
                            createAndShowGUI(args[0], args[1]);
                        } else {
                            createAndShowGUI(args[0], null);
                        }
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
     * @param highlightPathName the name of a path to be highlighted, if not null
     */
    private static void createAndShowGUI(String graphName, String highlightPathName) throws IOException, NullNodeException, NullSequenceException {
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

        // get the highlight path if provided
        Path highlightPath = null;
        if (highlightPathName!=null) {
            highlightPath = graph.getPath(highlightPathName);
            if (highlightPath==null) {
                System.err.println("ERROR: requested highlight path "+highlightPathName+" is not in graph.");
                System.exit(1);
            }
        }

        // PGraphXAdapter extends JGraphXAdapter which extends mxGraph
        PGraphXAdapter pgxAdapter = new PGraphXAdapter(graph, highlightPath);

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


