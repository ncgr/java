package org.ncgr.pangenomics.fr;

import org.ncgr.jgraph.Edge;
import org.ncgr.jgraph.Node;
import org.ncgr.jgraph.NullSequenceException;
import org.ncgr.jgraph.PangenomicGraph;
import org.ncgr.jgraph.TXTImporter;

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
    private static final Dimension DEFAULT_SIZE = new Dimension(1200, 700);
    private static final double P_THRESHOLD = 5e-2;

    /**
     * Run as an application.
     *
     * @param args command line arguments: FRFinder run prefix, e.g. HTT-0.1-100
     */
    public static void main(String[] args) throws IOException, NullSequenceException {
        if (args.length!=1) {
            System.out.println("Usage: FRViewer <prefix>");
            System.exit(0);
        }
        String prefix = args[0];
        String[] pieces = prefix.split("-");
        String graphName = pieces[0];
        double alpha = Double.parseDouble(pieces[1]);
        int kappa = Integer.parseInt(pieces[2]);

        String nodesFilename = graphName+".nodes.txt";
        String pathsFilename = graphName+".paths.txt";
        File nodesFile = new File(nodesFilename);
        File pathsFile = new File(pathsFilename);
        
        PangenomicGraph graph = new PangenomicGraph();
        graph.setVerbose();
        graph.setName(graphName);
        graph.importTXT(nodesFile, pathsFile);
        graph.tallyLabelCounts();

        // load the FRs into a sorted map so we see the juicy ones first
        Map<String,FrequentedRegion> unsortedFRs = FRUtils.readFrequentedRegions(prefix, graph);
        FRSorter frSorter = new FRSorter(unsortedFRs);
        TreeMap<String,FrequentedRegion> frequentedRegions = new TreeMap<>(frSorter);
        frequentedRegions.putAll(unsortedFRs);
        
        Object[] frKeys = frequentedRegions.keySet().toArray();
        FrequentedRegion firstFR = frequentedRegions.get((String)frKeys[0]);

        // create the JFrame
        JFrame frame = new JFrame(prefix);
        frame.setPreferredSize(DEFAULT_SIZE);

        // the JGraphXAdapter is an mxGraph
        JGraphXAdapter jgxAdapter = getAdapter(graph, firstFR);

        // the mxGraphComponent is a JScrollPane (implements Printable)
        pgGraphComponent component = new pgGraphComponent(graph, frequentedRegions, jgxAdapter);
            
        // add the component to the frame, clean up frame and show
        frame.add(component);
        frame.resize(DEFAULT_SIZE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        // refresh the layout
        component.refresh();
    }

    /**
     * Create and return the JGraphXAdapter which displays the graph highlighted for a given FrequentedRegion.
     */
    static JGraphXAdapter getAdapter(PangenomicGraph graph, FrequentedRegion fr) {
        // cast graph to a ListenableGraph
        ListenableGraph<Node,Edge> g = new DefaultListenableGraph<Node,Edge>(graph);

        // create a visualization using JGraph, via an adapter
        // defaultVertex:{perimeter=com.mxgraph.view.mxPerimeter$1@61dc03ce, shape=rectangle, fontColor=#774400, strokeColor=#6482B9, fillColor=#C3D9FF, align=center, verticalAlign=middle}
        // defaultEdge:{endArrow=classic, shape=connector, fontColor=#446299, strokeColor=#6482B9, align=center, verticalAlign=middle}
        // vertex shapes: actor, cloud, cylinder, doubleEllipse, doubleRectangle, ellipse, hexagon, image, label, rectangle, rhombus, swimlane, triangle
        // edge shapes: arrow, connector, curve, line

        // the jgxAdapter is an mxGraph
        JGraphXAdapter jgxAdapter = new JGraphXAdapter<Node,Edge>(g);

        // set default styles
        mxStylesheet defaultStylesheet = jgxAdapter.getStylesheet();
        Map<String,Object> defaultEdgeStyle = defaultStylesheet.getDefaultEdgeStyle();
        defaultEdgeStyle.put("strokeColor", "gray");
        defaultEdgeStyle.put("fontColor", "gray");
        defaultEdgeStyle.put(mxConstants.STYLE_NOLABEL, "1");
        defaultStylesheet.setDefaultEdgeStyle(defaultEdgeStyle);
        Map<String,Object> defaultVertexStyle = defaultStylesheet.getDefaultVertexStyle();
        defaultVertexStyle.put("fillColor", "white");
        defaultVertexStyle.put("fontColor", "black");
        defaultVertexStyle.put("shape", mxConstants.SHAPE_ELLIPSE);
        defaultStylesheet.setDefaultVertexStyle(defaultVertexStyle);
        jgxAdapter.setStylesheet(defaultStylesheet);

        // default case/control styles
        String baseFRStyle = "shape="+mxConstants.SHAPE_RECTANGLE+";fontColor=black;fillColor=#808080;strokeColor=black;gradientColor=none;verticalAlign=bottom";

        // FR stats
        double frOR = fr.oddsRatio();
        double frP = fr.fisherExactP();
        int frPfactor = (int)(-Math.log10(frP)*10);
        
        // color the nodes
        jgxAdapter.selectAll();
        Object[] allCells = jgxAdapter.getSelectionCells();
        for (Object o : allCells) {
            Object[] cells = {o};
            mxCell c = (mxCell) o;
            if (c.isVertex()) {
                Node n = (Node) c.getValue();
                if (c.getEdgeCount()==0) {
                    // remove orphan
                    c.removeFromParent();
                } else {
                    if (fr.containsNode(n)) {
                        // show case|control subpath counts for this node since it's in the FR
                        String label = n.getId()+":"+fr.getCaseCount(n)+"/"+fr.getControlCount(n);
                        c.setValue(label);
                        jgxAdapter.setCellStyle(baseFRStyle, cells);
                        // significance decoration
                        if (frP<P_THRESHOLD) {
                            if (Double.isInfinite(frOR)) {
                                // 100% case node
                                jgxAdapter.setCellStyles("fillColor", "#ff8080", cells);
                                jgxAdapter.setCellStyles("fontColor", "black", cells);
                            } else if (frOR==0.00) {
                                // 100% ctrl node
                                jgxAdapter.setCellStyles("fillColor", "#80ff80", cells);
                                jgxAdapter.setCellStyles("fontColor", "black", cells);
                            } else if (frOR>1.0) {
                                // case node
                                int rInt = Math.min(frPfactor, 127) + 128;
                                String rHex = Integer.toHexString(rInt);
                                String fillColor = "#"+rHex+"8080";
                                jgxAdapter.setCellStyles("fillColor", fillColor, cells); 
                                jgxAdapter.setCellStyles("fontColor", "white", cells);
                            } else if (frOR<1.0) {
                                // ctrl node
                                int gInt = Math.min(frPfactor, 127) + 128;
                                String gHex = Integer.toHexString(gInt);
                                String fillColor = "#80"+gHex+"80";
                                jgxAdapter.setCellStyles("fillColor", fillColor, cells);
                                jgxAdapter.setCellStyles("fontColor", "white", cells);
                            }
                        }
                    }
                    // update the cell shape
                    jgxAdapter.cellSizeUpdated(c, true);
                }
            } else if (c.isEdge()) {
                // do something with the edges?
            }
        }
        jgxAdapter.clearSelection();
        
        return jgxAdapter;
    }
}

/**
 * Extend mxGraphComponent to implement ActionListener
 */
class pgGraphComponent extends mxGraphComponent implements ActionListener {
    static DecimalFormat df = new DecimalFormat("0.0");
    static DecimalFormat pf = new DecimalFormat("0.0E0");
    static DecimalFormat orf = new DecimalFormat("0.000");        // initialize

    PangenomicGraph graph;
    Map<String,FrequentedRegion> frequentedRegions;
    JGraphXAdapter jgxAdapter;
    
    Object[] frKeys;
    JButton prevButton;
    JButton nextButton;
    int current = 0; // index of current FR being shown
    
    /**
     * Constructor takes a JGraphXAdapter
     */
    pgGraphComponent(PangenomicGraph graph, Map<String,FrequentedRegion> frequentedRegions, JGraphXAdapter jgxAdapter) {
        super(jgxAdapter);
        this.jgxAdapter = jgxAdapter;
        this.graph = graph;
        this.frequentedRegions = frequentedRegions;

        // load the FR keys into an array to select the chosen FR with an int on action events
        frKeys = frequentedRegions.keySet().toArray();

        // left arrow advances to next FR
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,0), "previous");
        getActionMap().put("previous", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    if (current>0) {
                        prevButton.doClick();
                    }
                }
            });
        // right arrow regresses to previous FR
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,0), "next");
        getActionMap().put("next", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    if (current<(frKeys.length-1)) {
                        nextButton.doClick();
                    }
                }
            });
        // q quits
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Q,0), "quit");
        getActionMap().put("quit", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
        
        // some settings
        setConnectable(false);
        getGraph().setAllowDanglingEdges(false);

        // add a column header with navigation buttons
        JPanel buttonPanel = new JPanel();
        prevButton = new JButton("Prev FR");
        prevButton.setActionCommand("previous");
        prevButton.addActionListener(this);
        buttonPanel.add(prevButton);
        nextButton = new JButton("Next FR");
        nextButton.setActionCommand("next");
        nextButton.addActionListener(this);
        buttonPanel.add(nextButton);
        setColumnHeaderView(buttonPanel);
        updateButtonStates();

        // set the info row for this first FR
        setInfoRow(frequentedRegions.get((String)frKeys[0]));
    }
    
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("next")) {
            current++;
        } else if (command.equals("previous")) {
            current--;
        }
        // load new FR and update graph
        FrequentedRegion fr = frequentedRegions.get((String)frKeys[current]);
        jgxAdapter = FRViewer.getAdapter(graph, fr);
        setGraph(jgxAdapter);
        setInfoRow(fr);
        refresh();
        updateButtonStates();
    }

    /**
     * Update the button states.
     */
    public void updateButtonStates() {
        if (current==0) {
            prevButton.setEnabled(false);
        } else {
            prevButton.setEnabled(true);
        }
        if (current==(frKeys.length-1)) {
            nextButton.setEnabled(false);
        } else {
            nextButton.setEnabled(true);
        }
    }

    /**
     * Create a text row with FR info
     */
    public void setInfoRow(FrequentedRegion fr) {
        JLabel rowLabel = new JLabel("<html>"+
                                     graph.getName()+
                                     "<hr/>"+
                                     "alpha="+fr.alpha+"<br/>"+
                                     "kappa="+fr.kappa+"<br/>"+
                                     "size="+fr.nodes.size()+"<br/>"+
                                     "avgLen="+df.format(fr.avgLength)+"<br/>"+
                                     "case="+fr.caseSupport+"<br/>"+
                                     "ctrl="+fr.ctrlSupport+
                                     "<hr/>"+
                                     "p="+pf.format(fr.fisherExactP())+"<br/>"+
                                     "O.R.="+orf.format(fr.oddsRatio())+
                                     "<hr/>"+
                                     "</html>");
        rowLabel.setVerticalAlignment(SwingConstants.TOP);
        setRowHeaderView(rowLabel);
    }

    /**
     * Refresh the layout.
     */
    public void refresh() {
        mxHierarchicalLayout layout = new mxHierarchicalLayout(jgxAdapter, SwingConstants.WEST);
        layout.setFineTuning(true);
        layout.execute(jgxAdapter.getDefaultParent());
    }
}

/**
 * Comparator to sort FRs by p-value.
 */
class FRSorter implements Comparator<String> {
    Map<String,FrequentedRegion> frequentedRegions;

    FRSorter(Map<String,FrequentedRegion> frequentedRegions) {
        this.frequentedRegions = frequentedRegions;
    }

    // compare by fishing out the p-values
    public int compare(String key1, String key2) {
        if (key1.equals(key2)) return 0;
        FrequentedRegion fr1 = frequentedRegions.get(key1);
        FrequentedRegion fr2 = frequentedRegions.get(key2);
        double p1 = fr1.fisherExactP();
        double p2 = fr2.fisherExactP();
        return Double.compare(p1, p2);
    }
}
