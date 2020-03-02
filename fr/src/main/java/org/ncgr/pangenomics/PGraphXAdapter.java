package org.ncgr.pangenomics;

import java.text.DecimalFormat;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

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

    // in line with standard GWAS value
    static double P_THRESHOLD = 5.0e-8;
    static int COLOR_FACTOR = 16;

    PangenomicGraph graph;
    boolean hasCaseControlLabels;
    Map<Path,List<Edge>> highlightPathEdges;

    public PGraphXAdapter(PangenomicGraph graph, List<Path> highlightPaths, boolean decorateEdges) {
        super(new DefaultListenableGraph<Node,Edge>(graph));
        this.graph = graph;
        
        // need to store highlighted paths up here so we don't recalculate on every node
        highlightPathEdges = new HashMap<>();
        if (highlightPaths!=null) {
            for (Path p : highlightPaths) {
                highlightPathEdges.put(p, p.getEdges());
            }
        }

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
                        if (Double.isInfinite(or) && graph.getPathCount(n)==graph.getPathCount()) {
                            // all paths go through node
                            setCellStyles("fillColor", "white", cells);
                            setCellStyles("fontColor", "black", cells);
                        } else if (Double.isInfinite(or) || or>1.0) {
                            // case-heavy node
                            double mlog10p = -Math.log10(p);
                            int rInt = Math.min((int)(COLOR_FACTOR*mlog10p), 127) + 128; // full color at COLOR_FACTOR*mlog10p=127
                            String rHex = Integer.toHexString(rInt);
                            String fillColor = "#"+rHex+"8080";
                            setCellStyles("fillColor", fillColor, cells);
                            if (p<P_THRESHOLD) {
                                setCellStyles("fontColor", "white", cells);
                                setCellStyles("fontStyle", String.valueOf(mxConstants.FONT_BOLD), cells);
                            } else {
                                setCellStyles("fontColor", "black", cells);
                            }
                        } else if (or==0.00 || or<1.0) {
                            // control-heavy node
                            double mlog10p = -Math.log10(p);
                            int gInt = Math.min((int)(COLOR_FACTOR*mlog10p), 127) + 128; // full color at COLOR_FACTOR*mlog10p=127
                            String gHex = Integer.toHexString(gInt);
                            String fillColor = "#80"+gHex+"80";
                            setCellStyles("fillColor", fillColor, cells);
                            if (p<P_THRESHOLD) {
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
                // highlighted paths
                for (Path p : highlightPathEdges.keySet()) {
                    List<Edge> edges = highlightPathEdges.get(p);
                    if (edges.contains(e)) {
                        if (p.isCase()) {
                            setCellStyles("strokeColor", "red", cells);
                        } else if (p.isControl()) {
                            setCellStyles("strokeColor", "green", cells);
                        } else {
                            setCellStyles("strokeColor", "blue", cells);
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
            int graphPathCount = graph.getPathCount();
            int nodePathCount = graph.getPathCount(n);
            double nodeFrac = (double)nodePathCount/(double)graphPathCount; // MAF
            String tip = "<html>" +
                seq+"<br/>" +
                seq.length()+" bp<br/>" +
                nodePathCount+" paths<br/>" +
                percf.format(nodeFrac);
            if (hasCaseControlLabels) {
                double or = graph.oddsRatio(n);
                double p = graph.fisherExactP(n);
                Map<String,Integer> graphLabelCounts = graph.getLabelCounts();
                Map<String,Integer> nodeLabelCounts = graph.getLabelCounts(n);
                int nodeCaseCounts = 0;
                int nodeCtrlCounts = 0;
                if (nodeLabelCounts.containsKey("case")) nodeCaseCounts = nodeLabelCounts.get("case");
                if (nodeLabelCounts.containsKey("ctrl")) nodeCtrlCounts = nodeLabelCounts.get("ctrl");
                double nodeCaseFrac = (double)nodeCaseCounts/(double)graphLabelCounts.get("case");
                double nodeCtrlFrac = (double)nodeCtrlCounts/(double)graphLabelCounts.get("ctrl");
                tip += "<br/>"+nodeCaseCounts+"/"+nodeCtrlCounts+"<br/>" +
                    percf.format(nodeCaseFrac)+"/"+percf.format(nodeCtrlFrac)+"<br/>" +
                    "OR="+orf.format(or)+"<br/>" +
                    "p="+pf.format(p);
            }
            tip += "</html>";
            return tip;
        } else if (c.isEdge()) {
            Edge e = (Edge) c.getValue();
            int graphPathCount = graph.getPathCount();
            int edgePathCount = graph.getPathCount(e);
            String tip = percf.format((double)edgePathCount/(double)graphPathCount);
            if (hasCaseControlLabels) {
                Map<String,Integer> graphLabelCounts = graph.getLabelCounts();
                Map<String,Integer> edgeLabelCounts = graph.getLabelCounts(e);
                int edgeCaseCounts = 0;
                int edgeCtrlCounts = 0;
                if (edgeLabelCounts.containsKey("case")) edgeCaseCounts = edgeLabelCounts.get("case");
                if (edgeLabelCounts.containsKey("ctrl")) edgeCtrlCounts = edgeLabelCounts.get("ctrl");
                double edgeCaseFrac = (double)edgeCaseCounts/(double)graphLabelCounts.get("case");
                double edgeCtrlFrac = (double)edgeCtrlCounts/(double)graphLabelCounts.get("ctrl");
                tip += " ("+percf.format(edgeCaseFrac)+"/"+percf.format(edgeCtrlFrac)+")";
            } else {
                double edgeFrac = (double)edgePathCount/(double)graphPathCount;
                tip += " ("+percf.format(edgeFrac)+")";
            }
            return tip;
        } else {
            // shouldn't be reached
            return "";
        }
    }
}
