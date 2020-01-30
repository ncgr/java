package org.ncgr.pangenomics;

import java.text.DecimalFormat;
import java.util.Map;

import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultListenableGraph;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxStylesheet;

/**
 * Extend JGraphXAdapter to customize tooltips, etc.
 */
class PGraphXAdapter extends JGraphXAdapter<Node,Edge> {
    static DecimalFormat pf = new DecimalFormat("0.0E0");
    static DecimalFormat orf = new DecimalFormat("0.000");
    static DecimalFormat percf = new DecimalFormat("0.0%");

    PangenomicGraph graph;
    boolean hasCaseControlLabels;
    java.util.List<Edge> highlightPathEdges;

    public PGraphXAdapter(PangenomicGraph graph, Path highlightPath, boolean decorateEdges) {
        super(new DefaultListenableGraph<Node,Edge>(graph));
        this.graph = graph;
        if (highlightPath!=null) highlightPathEdges = highlightPath.getEdges();
        
        mxStylesheet defaultStylesheet = getStylesheet();

        // set default edge style
        Map<String,Object> defaultEdgeStyle = defaultStylesheet.getDefaultEdgeStyle();
        defaultEdgeStyle.put("strokeColor", "gray");
        defaultEdgeStyle.put("fontColor", "gray");
        defaultEdgeStyle.put(mxConstants.STYLE_NOLABEL, "1");
        defaultStylesheet.setDefaultEdgeStyle(defaultEdgeStyle);
        // set default vertex (Node) style
        Map<String,Object> defaultVertexStyle = defaultStylesheet.getDefaultVertexStyle();
        defaultVertexStyle.put("fillColor", "white");
        defaultVertexStyle.put("fontColor", "black");
        defaultVertexStyle.put("shape", mxConstants.SHAPE_ELLIPSE);
        defaultStylesheet.setDefaultVertexStyle(defaultVertexStyle);
        // apply the default stylesheet
        setStylesheet(defaultStylesheet);
        setAutoSizeCells(true);

        // logical to do case/control ops
        hasCaseControlLabels = graph.getLabelCounts().containsKey("case") && graph.getLabelCounts().containsKey("ctrl");

        // color the nodes, plus other decoration
        selectAll();
        Object[] allCells = getSelectionCells();
        for (Object o : allCells) {
            Object[] cells = {o};
            mxCell c = (mxCell) o;
            if (c.isVertex()) {
                Node n = (Node) c.getValue();
                if (hasCaseControlLabels) {
                    if (c.getEdgeCount()>0) {
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
                                setCellStyles("fontStyle", String.valueOf(mxConstants.FONT_BOLD), cells);
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
                                setCellStyles("fontStyle", String.valueOf(mxConstants.FONT_BOLD), cells);
                            } else {
                                setCellStyles("fontColor", "black", cells);
                            }
                        }
                    }
                }
            } else if (c.isEdge()) {
                Edge e = (Edge) c.getValue();
                // this takes a long time
                if (decorateEdges) {
                    double strokeWidth = Math.max(1.0, 5.0*(double)graph.getPathCount(e)/(double)graph.getPathCount());
                    setCellStyles("strokeWidth", String.valueOf(strokeWidth), cells);
                }
                if (highlightPath!=null && highlightPathEdges.contains(e)) {
                    // highlight path's edges
                    if (highlightPath.isCase()) {
                        setCellStyles("strokeColor", "red", cells);
                    } else if (highlightPath.isControl()) {
                        setCellStyles("strokeColor", "green", cells);
                    } else {
                        setCellStyles("strokeColor", "blue", cells);
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
            if (hasCaseControlLabels) {
                double or = graph.oddsRatio(n);
                double p = graph.fisherExactP(n);
                Map<String,Integer> labelCounts = graph.getLabelCounts(n);
                int caseCounts = 0;
                int ctrlCounts = 0;
                if (labelCounts.containsKey("case")) caseCounts = labelCounts.get("case");
                if (labelCounts.containsKey("ctrl")) ctrlCounts = labelCounts.get("ctrl");
                tip += "<br/>"+caseCounts+"/"+ctrlCounts+"<br/>" +
                    "OR="+orf.format(or)+"<br/>" +
                    "p="+pf.format(p);
            }
            tip += "</html>";
            return tip;
        } else if (c.isEdge()) {
            Edge e = (Edge) c.getValue();
            String tip = percf.format((double)graph.getPathCount(e)/(double)graph.getPathCount());
            if (hasCaseControlLabels) {
                Map<String,Integer> labelCounts = graph.getLabelCounts(e);
                int caseCounts = 0;
                int ctrlCounts = 0;
                if (labelCounts.containsKey("case")) caseCounts = labelCounts.get("case");
                if (labelCounts.containsKey("ctrl")) ctrlCounts = labelCounts.get("ctrl");
                tip += " ("+caseCounts+"/"+ctrlCounts+")";
            } else {
                tip += " ("+graph.getPathCount(e)+")";
            }
            return tip;
        } else {
            // shouldn't be reached
            return "";
        }
    }
}
