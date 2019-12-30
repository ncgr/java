/*
 * (C) Copyright 2013-2018, by Barak Naveh and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * See the CONTRIBUTORS.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the
 * GNU Lesser General Public License v2.1 or later
 * which is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1-standalone.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR LGPL-2.1-or-later
 */
package org.ncgr.jgraph;

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
import java.io.*;
import java.util.*;

/**
 * A demo applet that shows how to use JGraphX to visualize JGraphT graphs. Applet based on JGraphAdapterDemo.
 */
public class GraphViewer extends JApplet {
    private static final long serialVersionUID = 2202072534703043194L;
    private static final Dimension DEFAULT_SIZE = new Dimension(1000, 500);
    private static final double P_THRESHOLD = 5e-2;
    
    private JGraphXAdapter<Node,Edge> jgxAdapter;

    PangenomicGraph graph;

    /**
     * An alternative starting point for this demo, to also allow running this applet as an application.
     *
     * @param args command line arguments: nodes.txt file and paths.txt file
     */
    public static void main(String[] args) throws IOException, NullSequenceException {
        if (args.length!=1) {
            System.out.println("Usage: GraphViewer <graph>");
            System.exit(0);
        }
        String graphName = args[0];
        String nodesFilename = graphName+".nodes.txt";
        String pathsFilename = graphName+".paths.txt";
        File nodesFile = new File(nodesFilename);
        File pathsFile = new File(pathsFilename);
        
        PangenomicGraph graph = new PangenomicGraph();
        graph.setVerbose();
        graph.importTXT(nodesFile, pathsFile);
        graph.tallyLabelCounts();

        GraphViewer viewer = new GraphViewer();
        viewer.setGraph(graph);
        
        viewer.init();
        JFrame frame = new JFrame();
        frame.getContentPane().add(viewer);
        frame.setTitle(graphName);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
    
    /**
     * Set the graph.
     */
    void setGraph(PangenomicGraph graph) {
        this.graph = graph;
    }

    @Override
    public void init() {
        
        ListenableGraph<Node,Edge> g = new DefaultListenableGraph<Node,Edge>(graph);

        // create a visualization using JGraph, via an adapter

        // void 	setAllowDanglingEdges(boolean value)        // Sets allowDanglingEdges.
        // void 	setAllowLoops(boolean value)        // Sets allowLoops.
        // void 	setAllowNegativeCoordinates(boolean value) 
        // void 	setAlternateEdgeStyle(java.lang.String value)        // Sets alternateEdgeStyle.
        // void 	setAutoOrigin(boolean value) 
        // void 	setAutoSizeCells(boolean value)        // Specifies if cell sizes should be automatically updated after a label change.
        // void 	setBorder(int value)        // Sets the value of .
        // void 	setCellsBendable(boolean value)        // Sets cellsBendable.
        // void 	setCellsCloneable(boolean value)        // Specifies if the graph should allow cloning of cells by holding down the control key while cells are being moved.
        // void 	setCellsDeletable(boolean value)        // Sets cellsDeletable.
        // void 	setCellsDisconnectable(boolean value)        // Sets cellsDisconnectable.
        // void 	setCellsEditable(boolean value)        // Sets if the graph is editable.
        // void 	setCellsLocked(boolean value)        // Sets cellsLocked, the default return value for isCellLocked and fires a property change event for cellsLocked.
        // void 	setCellsMovable(boolean value)        // Sets cellsMovable.
        // void 	setCellsResizable(boolean value)        // Sets if the graph is resizable.
        // void 	setCellsSelectable(boolean value)        // Sets cellsSelectable.
        // void 	setChangesRepaintThreshold(int value) 
        // void 	setCloneInvalidEdges(boolean value)        // Sets cloneInvalidEdge.
        // void 	setCollapseToPreferredSize(boolean value) 
        // void 	setConnectableEdges(boolean value)        // Sets connetableEdges.
        // void 	setConstrainChildren(boolean value) 
        // void 	setDefaultLoopStyle(mxEdgeStyle.mxEdgeStyleFunction value)        // Sets the default style used for loops.
        // void 	setDefaultOverlap(double value)        // Sets defaultOverlap.
        // void 	setDefaultParent(java.lang.Object value)        // Sets the default parent to be returned by getDefaultParent.
        // void 	setDisconnectOnMove(boolean value)        // Sets disconnectOnMove.
        // void 	setDropEnabled(boolean value)        // Sets dropEnabled.
        // void 	setEdgeLabelsMovable(boolean value)        // Returns edgeLabelsMovable.
        // void 	setEnabled(boolean value)        // Specifies if the graph should allow any interactions.
        // void 	setExtendParents(boolean value)        // Sets extendParents.
        // void 	setExtendParentsOnAdd(boolean value)        // Sets extendParentsOnAdd.
        // void 	setGridEnabled(boolean value)        // Sets if the grid is enabled.
        // void 	setGridSize(int value)        // Sets the grid size and fires a property change event for gridSize.
        // void 	setHtmlLabels(boolean value)
        // void 	setKeepEdgesInBackground(boolean value) 
        // void 	setKeepEdgesInForeground(boolean value)
        // void 	setLabelsClipped(boolean value)        // Sets labelsClipped.
        // void 	setLabelsVisible(boolean value)
        // void 	setMaximumGraphBounds(mxRectangle value) 
        // void 	setMinimumGraphSize(mxRectangle value) 
        // void 	setModel(mxIGraphModel value)        // Sets the graph model that contains the data, and fires an mxEvent.CHANGE followed by an mxEvent.REPAINT event.
        // void 	setMultigraph(boolean value)        // Sets multigraph.
        // void 	setOrigin(mxPoint value) 
        // void 	setPortsEnabled(boolean value)        // Sets if ports are enabled.
        // void 	setResetEdgesOnConnect(boolean value)        // Sets resetEdgesOnConnect.
        // void 	setResetEdgesOnMove(boolean value)        // Sets resetEdgesOnMove.
        // void 	setResetEdgesOnResize(boolean value)        // Sets resetEdgesOnResize.
        // void 	setResetViewOnRootChange(boolean value)        // Sets resetEdgesOnResize.
        // void 	setSplitEnabled(boolean value)        // Sets splitEnabled.
        // void 	setStylesheet(mxStylesheet value)        // Sets the stylesheet that provides the style.
        // void 	setSwimlaneNesting(boolean value)        // Sets swimlaneNesting.
        // void 	setVertexLabelsMovable(boolean value)        // Sets vertexLabelsMovable.
        // void 	setView(mxGraphView value)        // Sets the view that contains the cell states.
        // defaultVertex:{perimeter=com.mxgraph.view.mxPerimeter$1@61dc03ce, shape=rectangle, fontColor=#774400, strokeColor=#6482B9, fillColor=#C3D9FF, align=center, verticalAlign=middle}
        // defaultEdge:{endArrow=classic, shape=connector, fontColor=#446299, strokeColor=#6482B9, align=center, verticalAlign=middle}
        // vertex: shape=actor, cloud, cylinder, doubleEllipse, doubleRectangle, ellipse, hexagon, image, label, rectangle, rhombus, swimlane, triangle
        // edge: shape=arrow, connector, curve, line

        // the jgxAdapter is an mxGraph
        jgxAdapter = new JGraphXAdapter<Node,Edge>(g);

        // set default styles
        mxStylesheet defaultStylesheet = jgxAdapter.getStylesheet();
        Map<String,Object> defaultEdgeStyle = defaultStylesheet.getDefaultEdgeStyle();
        defaultEdgeStyle.put("strokeColor", "gray");
        defaultEdgeStyle.put("fontColor", "gray");
        defaultEdgeStyle.put(mxConstants.STYLE_NOLABEL, "1");
        defaultStylesheet.setDefaultEdgeStyle(defaultEdgeStyle);
        Map<String,Object> defaultVertexStyle = defaultStylesheet.getDefaultVertexStyle();
        defaultVertexStyle.put("fillColor", "gray");
        defaultVertexStyle.put("fontColor", "black");
        defaultStylesheet.setDefaultVertexStyle(defaultVertexStyle);
        jgxAdapter.setStylesheet(defaultStylesheet);

        // default case/control styles
        String baseCaseStyle = "shape="+mxConstants.SHAPE_ELLIPSE+";fontStyle="+mxConstants.FONT_BOLD+";fontColor=black;gradientColor=none;verticalAlign=bottom";
        String baseCtrlStyle = "shape="+mxConstants.SHAPE_ELLIPSE+";fontStyle="+mxConstants.FONT_BOLD+";fontColor=black;gradientColor=none;verticalAlign=bottom";

        // color the nodes
        jgxAdapter.selectAll();
        Object[] allCells = jgxAdapter.getSelectionCells();
        for (Object o : allCells) {
            Object[] cells = {o};
            mxCell c = (mxCell) o;
            if (c.isVertex()) {
                Node n = (Node) c.getValue();
                if (graph.getPaths(n).size()==0) {
                    // remove orphan
                    c.removeFromParent();
                } else {
                    // style
                    double or = graph.oddsRatio(n);
                    double p = graph.fisherExactP(n);
                    if (p<P_THRESHOLD) {
                        if (Double.isInfinite(or)) {
                            // 100% case node
                            jgxAdapter.setCellStyle(baseCaseStyle, cells);
                            jgxAdapter.setCellStyles("fillColor", "#ff8080", cells);
                            jgxAdapter.setCellStyles("fontColor", "black", cells);
                        } else if (or==0.00) {
                            // 100% ctrl node
                            jgxAdapter.setCellStyle(baseCtrlStyle, cells);
                            jgxAdapter.setCellStyles("fillColor", "#80ff80", cells);
                            jgxAdapter.setCellStyles("fontColor", "black", cells);
                        } else if (or>1.0) {
                            // case node
                            jgxAdapter.setCellStyle(baseCaseStyle, cells);
                            double log10or = Math.log10(or);
                            int rInt = Math.min((int)(127.0*log10or), 127) + 128;
                            String rHex = Integer.toHexString(rInt);
                            String fillColor = "#"+rHex+"8080";
                            jgxAdapter.setCellStyles("fillColor", fillColor, cells); 
                            jgxAdapter.setCellStyles("fontColor", "white", cells);
                        } else if (or<1.0) {
                            // ctrl node
                            jgxAdapter.setCellStyle(baseCtrlStyle, cells);
                            double log10or = -Math.log10(or);
                            int rInt = Math.min((int)(127.0*log10or), 127) + 128;
                            String rHex = Integer.toHexString(rInt);
                            String fillColor = "#80"+rHex+"80";
                            jgxAdapter.setCellStyles("fillColor", fillColor, cells);
                            jgxAdapter.setCellStyles("fontColor", "white", cells);
                        }
                    }
                }
            } else if (c.isEdge()) {
                // do something with the edges?
            }
        }
        jgxAdapter.clearSelection();
        
        // set up this JApplet
        setPreferredSize(DEFAULT_SIZE);
        mxGraphComponent component = new mxGraphComponent(jgxAdapter);
        component.setConnectable(false);
        component.getGraph().setAllowDanglingEdges(false);
        getContentPane().add(component);
        resize(DEFAULT_SIZE);

        // mxHierarchicalLayout -- good! WEST orientation is best.
        // void 	setDisableEdgeStyle​(boolean disableEdgeStyle) 	 
        // void 	setFineTuning​(boolean fineTuning) 	 
        // void 	setInterHierarchySpacing​(double interHierarchySpacing) 	 
        // void 	setInterRankCellSpacing​(double interRankCellSpacing) 	 
        // void 	setIntraCellSpacing​(double intraCellSpacing) 	 
        // void 	setMoveParent​(boolean value) 	Sets the moveParent flag.
        // void 	setOrientation​(int orientation) 	 
        // void 	setParallelEdgeSpacing​(double parallelEdgeSpacing) 	 
        // void 	setParentBorder​(int value) 	Sets parentBorder.
        // void 	setResizeParent​(boolean value)
        mxHierarchicalLayout layout = new mxHierarchicalLayout(jgxAdapter, SwingConstants.WEST);
        layout.setFineTuning(true);

        // lay out the layout
        layout.execute(jgxAdapter.getDefaultParent());
    }
}
