package org.ncgr.pangenomics.fr;

import org.ncgr.pangenomics.Edge;
import org.ncgr.pangenomics.Node;
import org.ncgr.pangenomics.NullNodeException;
import org.ncgr.pangenomics.NullSequenceException;
import org.ncgr.pangenomics.PangenomicGraph;
import org.ncgr.pangenomics.TXTImporter;

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
import java.util.*;
import java.text.*;

/**
 * A static class that displays a PangenomicGraph along with FrequentedRegions in a JFrame.
 */
public class FRViewer {
    private static final long serialVersionUID = 2202072534703043194L;
    private static final Dimension DEFAULT_SIZE = new Dimension(1200, 780);

    /**
     * Main application.
     *
     * @param args command line arguments: graphName
     */
    public static void main(String[] args) {
        if (args.length!=1) {
            System.out.println("Usage: FRViewer <prefix>");
            System.exit(0);
        }
        String prefix = args[0];
        // schedule a job for the event dispatch thread: creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    // turn off metal's use of bold fonts
                    UIManager.put("swing.boldMetal", Boolean.FALSE);
                    try {
                        createAndShowGUI(prefix);
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
    public static void createAndShowGUI(String prefix) throws IOException, NullNodeException, NullSequenceException {
        String[] pieces = prefix.split("-");
        String graphName = pieces[0];
        double alpha = Double.parseDouble(pieces[1]);
        int kappa = Integer.parseInt(pieces[2]);

        String nodesFilename = graphName+".nodes.txt";
        String pathsFilename = graphName+".paths.txt";
        File nodesFile = new File(nodesFilename);
        File pathsFile = new File(pathsFilename);

        Properties parameters = FRUtils.readParameters(prefix); // reads run properties from params file
        
        PangenomicGraph graph = new PangenomicGraph();
        graph.setVerbose();
        graph.setName(graphName);
        graph.importTXT(nodesFile, pathsFile);
        graph.tallyLabelCounts();

        // load the FRs into a sorted map so we see the juicy ones first
        Map<String,FrequentedRegion> unsortedFRs = FRUtils.readFrequentedRegions(prefix, graph);
        FRPComparator frComparator = new FRPComparator(unsortedFRs);
        TreeMap<String,FrequentedRegion> frequentedRegions = new TreeMap<>(frComparator);
        frequentedRegions.putAll(unsortedFRs);
        
        // create the JFrame
        JFrame frame = new JFrame(prefix);
        frame.setPreferredSize(DEFAULT_SIZE);

        // the FGraphXAdapter is an mxGraph; instantiate with the first FR in the list
        Object[] frKeys = frequentedRegions.keySet().toArray();
        FGraphXAdapter fgxAdapter = new FGraphXAdapter(graph, frequentedRegions.get((String)frKeys[0]));

        // frGraphComponent extends mxGraphComponent which extends JScrollPane (implements Printable)
        frGraphComponent component = new frGraphComponent(graph, frequentedRegions, fgxAdapter, parameters);
            
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
