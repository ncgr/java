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
 * Static class which shows a PangenomicGraph with nodes colored according to case/control degree in a JFrame.
 */
public class GraphViewer {
    private static final long serialVersionUID = 2202072534703043194L;
    private static final Dimension DEFAULT_SIZE = new Dimension(1000, 700);
    private static final double P_THRESHOLD = 5e-2;

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

        JGraphXAdapter jgxAdapter = getAdapter(graph);
        mxGraphComponent component = new mxGraphComponent(jgxAdapter);
        component.setConnectable(false);
        component.getGraph().setAllowDanglingEdges(false);

        // Create and populate the JFrame
        JFrame frame = new JFrame(graphName);
        frame.setPreferredSize(DEFAULT_SIZE);
        frame.getContentPane().add(component);
        frame.resize(DEFAULT_SIZE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        // mxHierarchicalLayout -- good! WEST orientation is best.
        mxHierarchicalLayout layout = new mxHierarchicalLayout(jgxAdapter, SwingConstants.WEST);
        layout.setFineTuning(true);

        // lay out the layout
        layout.execute(jgxAdapter.getDefaultParent());
    }

    /**
     * Create and return the JGraphXAdapter which displays the graph.
     */
    static JGraphXAdapter getAdapter(PangenomicGraph graph) {
        // cast graph to a ListenableGraph
        ListenableGraph<Node,Edge> g = new DefaultListenableGraph<Node,Edge>(graph);

        // the jgxAdapter is an mxGraph
        JGraphXAdapter<Node,Edge> jgxAdapter = new JGraphXAdapter<Node,Edge>(g);
        
        mxStylesheet defaultStylesheet = jgxAdapter.getStylesheet();

        // set default edge style
        Map<String,Object> defaultEdgeStyle = defaultStylesheet.getDefaultEdgeStyle();
        defaultEdgeStyle.put("strokeColor", "gray");
        defaultEdgeStyle.put("fontColor", "gray");
        defaultEdgeStyle.put(mxConstants.STYLE_NOLABEL, "1");
        defaultStylesheet.setDefaultEdgeStyle(defaultEdgeStyle);

        // set default vertex (Node) style
        Map<String,Object> defaultVertexStyle = defaultStylesheet.getDefaultVertexStyle();
        defaultVertexStyle.put("fillColor", "gray");
        defaultVertexStyle.put("fontColor", "black");
        defaultStylesheet.setDefaultVertexStyle(defaultVertexStyle);
        jgxAdapter.setStylesheet(defaultStylesheet);

        // default case/control vertex styles
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

        return jgxAdapter;
    }
}
