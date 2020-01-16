package org.ncgr.pangenomics.fr;

import org.ncgr.pangenomics.*;

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
 * Extend JGraphXAdapter to support overriden methods for tooltips and such.
 * JGraphXAdapter in turn extends mxGraph.
 */
public class FGraphXAdapter extends JGraphXAdapter<Node,Edge> {

    static final double P_THRESHOLD = 5e-2;
    static DecimalFormat pf = new DecimalFormat("0.0E0");
    static DecimalFormat orf = new DecimalFormat("0.000");
    static DecimalFormat percf = new DecimalFormat("0.0%");

    PangenomicGraph graph;
    FrequentedRegion fr;
    java.util.List<Edge> highlightPathEdges;

    public FGraphXAdapter(PangenomicGraph graph, FrequentedRegion fr, Path highlightPath) {
        super(new DefaultListenableGraph<Node,Edge>(graph));
        this.graph = graph;
        this.fr = fr;
        if (highlightPath!=null) highlightPathEdges = highlightPath.getEdges();

        // set default styles
        mxStylesheet defaultStylesheet = getStylesheet();
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
        setStylesheet(defaultStylesheet);

        // default case/control styles
        String baseFRStyle = "shape="+mxConstants.SHAPE_RECTANGLE+";fontColor=black;fillColor=#808080;strokeColor=black;gradientColor=none;verticalAlign=bottom";

        // FR stats
        double frOR = fr.oddsRatio();
        double frP = fr.fisherExactP();
        int frPlevel = (int)(-Math.log10(frP)*10);
        
        // color the nodes
        selectAll();
        Object[] allCells = getSelectionCells();
        for (Object o : allCells) {
            Object[] cells = {o};
            mxCell c = (mxCell) o;
            if (c.isVertex()) {
                Node n = (Node) c.getValue();
                if (c.getEdgeCount()>0) {
                    if (fr.containsNode(n)) {
                        // significance decoration
                        setCellStyle(baseFRStyle, cells);
                        if (frP<P_THRESHOLD) {
                            if (Double.isInfinite(frOR)) {
                                // 100% case node
                                setCellStyles("fillColor", "#ff8080", cells);
                                setCellStyles("fontColor", "black", cells);
                            } else if (frOR==0.00) {
                                // 100% ctrl node
                                setCellStyles("fillColor", "#80ff80", cells);
                                setCellStyles("fontColor", "black", cells);
                            } else if (frOR>1.0) {
                                // case node
                                int rInt = Math.min(frPlevel, 127) + 128;
                                String rHex = Integer.toHexString(rInt);
                                String fillColor = "#"+rHex+"8080";
                                setCellStyles("fillColor", fillColor, cells); 
                                setCellStyles("fontColor", "white", cells);
                            } else if (frOR<1.0) {
                                // ctrl node
                                int gInt = Math.min(frPlevel, 127) + 128;
                                String gHex = Integer.toHexString(gInt);
                                String fillColor = "#80"+gHex+"80";
                                setCellStyles("fillColor", fillColor, cells);
                                setCellStyles("fontColor", "white", cells);
                            }
                        }
                    } else {
                        double or = graph.oddsRatio(n);
                        double p = graph.fisherExactP(n);
                        // color based on segregation
                        if (Double.isInfinite(or)) {
                            // 100% case node
                            setCellStyles("fillColor", "#ff8080", cells);
                            setCellStyles("fontColor", "black", cells);
                        } else if (or==0.00) {
                            // 100% ctrl node
                            setCellStyles("fillColor", "#80ff80", cells);
                            setCellStyles("fontColor", "black", cells);
                        } else if (or>1.0) {
                            // case node
                            double log10or = Math.log10(or);
                            int rInt = Math.min((int)(127.0*log10or), 127) + 128;
                            String rHex = Integer.toHexString(rInt);
                            String fillColor = "#"+rHex+"8080";
                            setCellStyles("fillColor", fillColor, cells);
                            if (p<1e-2) {
                                setCellStyles("fontColor", "white", cells);
                            } else {
                                setCellStyles("fontColor", "black", cells);
                            }
                        } else if (or<1.0) {
                            // ctrl node
                            double log10or = -Math.log10(or);
                            int gInt = Math.min((int)(127.0*log10or), 127) + 128;
                            String gHex = Integer.toHexString(gInt);
                            String fillColor = "#80"+gHex+"80";
                            setCellStyles("fillColor", fillColor, cells);
                            if (p<1e-2) {
                                setCellStyles("fontColor", "white", cells);
                            } else {
                                setCellStyles("fontColor", "black", cells);
                            }
                        }
                    }
                }
            } else if (c.isEdge()) {
                if (highlightPath!=null) {
                    // highlight path's edges
                    Edge e = (Edge) c.getValue();
                    if (highlightPathEdges.contains(e)) {
                        setCellStyles("strokeWidth", "2.0", cells);
                        if (highlightPath.isCase()) {
                            setCellStyles("strokeColor", "red", cells);
                        } else if (highlightPath.isControl()) {
                            setCellStyles("strokeColor", "green", cells);
                        } else {
                            setCellStyles("strokeColor", "black", cells);
                        }
                    }
                }
            }
        }
        clearSelection();
    }

    /**
     * Override to show stats and node sequence as tooltip.
     */
    @Override
    public String getToolTipForCell(Object o) {
        mxCell c = (mxCell) o;
        if (c.isVertex()) {
            Node n = (Node) c.getValue();
            String seq = n.getSequence();
            int pathCount = graph.getPathCount(n);
            double frac = (double)pathCount/(double)graph.getPathCount();
            String tip = "<html>" +
                seq+"<br/>" +
                seq.length()+" bp<br/>" +
                pathCount+" paths<br/>" +
                percf.format(frac);
            Map<String,Integer> labelCounts = graph.getLabelCounts(n);
            if (labelCounts.containsKey("case") && labelCounts.containsKey("ctrl")) {
                int caseCounts = 0;
                int ctrlCounts = 0;
                double p = graph.fisherExactP(n);
                double or = graph.oddsRatio(n);
                if (labelCounts.containsKey("case")) caseCounts = labelCounts.get("case");
                if (labelCounts.containsKey("ctrl")) ctrlCounts = labelCounts.get("ctrl");
                tip += "<br/>"+caseCounts+"/"+ctrlCounts+"<br/>" +
                    "OR="+orf.format(or)+"<br/>" +
                    "p="+pf.format(p);
                if (fr.containsNode(n)) {
                    // show case|control subpath counts for this node since it's in the FR
                    tip += "<br/>support="+fr.getCaseCount(n)+"/"+fr.getControlCount(n);
                }
            }
            tip += "</html>";
            return tip;
        } else if (c.isEdge()) {
            Edge e = (Edge) c.getValue();
            Map<String,Integer> labelCounts = graph.getLabelCounts(e);
            int caseCounts = 0;
            int ctrlCounts = 0;
            if (labelCounts.containsKey("case")) caseCounts = labelCounts.get("case");
            if (labelCounts.containsKey("ctrl")) ctrlCounts = labelCounts.get("ctrl");
            String tip = "paths="+caseCounts+"/"+ctrlCounts;
            return tip;
        } else {
            // don't think this is ever reached
            return "";
        }
    }
}
