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
 * Extend JGraphXAdapter to customize tooltips, etc.
 */
class PGraphXAdapter extends JGraphXAdapter<Node,Edge> {
    static DecimalFormat pf = new DecimalFormat("0.0E0");
    static DecimalFormat orf = new DecimalFormat("0.000");

    PangenomicGraph graph;

    public PGraphXAdapter(PangenomicGraph graph) {
        super(new DefaultListenableGraph<Node,Edge>(graph));
        this.graph = graph;
        
        mxStylesheet defaultStylesheet = getStylesheet();

        // set default edge style
        Map<String,Object> defaultEdgeStyle = defaultStylesheet.getDefaultEdgeStyle();
        defaultEdgeStyle.put("strokeColor", "gray");
        defaultEdgeStyle.put("fontColor", "gray");
        defaultEdgeStyle.put(mxConstants.STYLE_NOLABEL, "1");
        defaultStylesheet.setDefaultEdgeStyle(defaultEdgeStyle);
        // set default vertex (Node) style
        Map<String,Object> defaultVertexStyle = defaultStylesheet.getDefaultVertexStyle();
        defaultVertexStyle.put("fontColor", "black");
        defaultVertexStyle.put("fillColor", "gray");
        defaultVertexStyle.put("shape", mxConstants.SHAPE_ELLIPSE);
        defaultVertexStyle.put("verticalAlign", mxConstants.ALIGN_BOTTOM);
        defaultStylesheet.setDefaultVertexStyle(defaultVertexStyle);
        // apply the default stylesheet
        setStylesheet(defaultStylesheet);

        // color the nodes
        selectAll();
        Object[] allCells = getSelectionCells();
        for (Object o : allCells) {
            Object[] cells = {o};
            mxCell c = (mxCell) o;
            if (c.isVertex()) {
                Node n = (Node) c.getValue();
                if (graph.getPaths(n).size()>0) {
                    double or = graph.oddsRatio(n);
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
                        setCellStyles("fontColor", "white", cells);
                    } else if (or<1.0) {
                        // ctrl node
                        double log10or = -Math.log10(or);
                        int gInt = Math.min((int)(127.0*log10or), 127) + 128;
                        String gHex = Integer.toHexString(gInt);
                        String fillColor = "#80"+gHex+"80";
                        setCellStyles("fillColor", fillColor, cells);
                        setCellStyles("fontColor", "white", cells);
                    }
                    // update the cell shape since we've added a big label
                    cellSizeUpdated(c, true);
                }
            } else if (c.isEdge()) {
                // do something with the edges?
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
            double p = graph.fisherExactP(n);
            double or = graph.oddsRatio(n);
            Map<String,Integer> labelCounts = graph.getLabelCounts(n);
            int caseCounts = 0;
            int ctrlCounts = 0;
            if (labelCounts.containsKey("case")) caseCounts = labelCounts.get("case");
            if (labelCounts.containsKey("ctrl")) ctrlCounts = labelCounts.get("ctrl");
            String seq = n.getSequence();
            String tip = "<html>" +
                seq+"<br/>" +
                seq.length()+" bp<br/>" +
                caseCounts+"/"+ctrlCounts+"<br/>" +
                "OR="+orf.format(or)+"<br/>" +
                "p="+pf.format(p) +
                "</html>";
            return tip;
        } else if (c.isEdge()) {
            Edge e = (Edge) c.getValue();
            Map<String,Integer> labelCounts = graph.getLabelCounts(e);
            int caseCounts = 0;
            int ctrlCounts = 0;
            if (labelCounts.containsKey("case")) caseCounts = labelCounts.get("case");
            if (labelCounts.containsKey("ctrl")) ctrlCounts = labelCounts.get("ctrl");
            String tip = caseCounts+"/"+ctrlCounts;
            return tip;
        } else {
            // shouldn't be reached
            return "";
        }
    }
}
