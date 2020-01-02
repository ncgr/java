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
 * Extend mxGraphComponent to implement ActionListener for button and key presses
 */
class pgGraphComponent extends mxGraphComponent implements ActionListener {
    PangenomicGraph graph;
    PGraphXAdapter pgxAdapter;
    
    // zoom buttons
    JButton zoomInButton;
    JButton zoomOutButton;

    // starting scale
    double scale = 1.0;
    
    /**
     * Constructor takes a graph and JGraphXAdapter
     */
    pgGraphComponent(PangenomicGraph graph, PGraphXAdapter pgxAdapter) {
        super(pgxAdapter);
        this.pgxAdapter = pgxAdapter;
        this.graph = graph;

        // housekeeping
        setConnectable(false);
        getGraph().setAllowDanglingEdges(false);
        setToolTips(true);

        // zoom in button -- plus is equals plus shift
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS,KeyEvent.SHIFT_MASK), "zoomIn");
        getActionMap().put("zoomIn", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    zoomInButton.doClick();
                }
            });
        // zoom in button -- equals key is equivalent to plus without shift
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS,0), "zoomIn");
        getActionMap().put("zoomIn", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    zoomInButton.doClick();
                }
            });
        // zoom out button
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS,0), "zoomOut");
        getActionMap().put("zoomOut", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    zoomOutButton.doClick();
                }
            });
        // q quits
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Q,0), "quit");
        getActionMap().put("quit", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
        
        // add a column header with static info and navigation buttons
        JPanel topPanel = new JPanel();
        // graph label in bold
        JLabel graphLabel = new JLabel(graph.getName());
        graphLabel.setFont(graphLabel.getFont().deriveFont(Font.BOLD));
        topPanel.add(graphLabel);
        // zoom buttons
        zoomInButton = new JButton("Zoom IN");
        zoomInButton.setActionCommand("zoomIn");
        zoomInButton.addActionListener(this);
        topPanel.add(zoomInButton);
        zoomOutButton = new JButton("Zoom OUT");
        zoomOutButton.setActionCommand("zoomOut");
        zoomOutButton.addActionListener(this);
        topPanel.add(zoomOutButton);
        // graph info
        String infoLabelString = graph.getNodes().size()+" nodes " +
            graph.getPaths().size()+" paths (" +
            graph.getLabelCounts().get("case")+" cases / " +
            graph.getLabelCounts().get("ctrl")+" controls)";
        JLabel infoLabel = new JLabel(infoLabelString);
        topPanel.add(infoLabel);
        // put it on the top
        setColumnHeaderView(topPanel);
    }
    
    /**
     * Handle button (and key) actions
     */
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("zoomIn") || command.equals("zoomOut")) {
            if (command.equals("zoomIn")) {
                scale = scale*Math.sqrt(2.0);
            } else if (command.equals("zoomOut")) {
                scale = scale/Math.sqrt(2.0);
            }
            pgxAdapter = new PGraphXAdapter(graph);
            pgxAdapter.getView().setScale(scale);
            setGraph(pgxAdapter);
            refresh();
            mxHierarchicalLayout layout = new mxHierarchicalLayout(pgxAdapter, SwingConstants.WEST);
            layout.setFineTuning(true);
            layout.execute(pgxAdapter.getDefaultParent());
        }
    }
}
