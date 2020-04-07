package org.ncgr.pangenomics.genotype.fr;

import org.ncgr.pangenomics.genotype.*;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultListenableGraph;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxStylesheet;

/**
 * Extend JGraphXAdapter to support overriden methods for tooltips and such.
 * JGraphXAdapter in turn extends mxGraph.
 */
public class FGraphXAdapter extends JGraphXAdapter<Node,Edge> {

    static final double P_THRESHOLD = 5e-8; // GWAS standard
    static int COLOR_FACTOR = 16;
    
    static DecimalFormat pf = new DecimalFormat("0.0E0");
    static DecimalFormat orf = new DecimalFormat("0.000");
    static DecimalFormat percf = new DecimalFormat("0.0%");

    PangenomicGraph graph;
    FrequentedRegion fr;

    Path highlightedPath = null;
    List<Edge> highlightedPathEdges;

    public FGraphXAdapter(PangenomicGraph graph, FrequentedRegion fr, Path highlightedPath, boolean decorateEdges) {
        super(new DefaultListenableGraph<Node,Edge>(graph));
        this.graph = graph;
        this.fr = fr;

        // get the highlighted path edges
        if (highlightedPath!=null) {
            highlightedPathEdges = highlightedPath.getEdges();
        }

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
        setAutoSizeCells(true);
        
        // default case/control styles
        String baseFRStyle = "shape="+mxConstants.SHAPE_RECTANGLE+";fontColor=black;fillColor=#808080;strokeColor=black;gradientColor=none";

        // FR stats
        double frP = fr.fisherExactP();

        // color the nodes
        selectAll();
        Object[] allCells = getSelectionCells();
        for (Object o : allCells) {
            Object[] cells = {o};
            mxCell c = (mxCell) o;
            if (c.isVertex()) {
                Node n = (Node) c.getValue();
                if (c.getEdgeCount()>0) {
                    double or = graph.oddsRatio(n);
                    double p = graph.fisherExactP(n);
                    double mlog10p = -Math.log10(p);
                    if (fr.containsNode(n)) {
                        // special cell style for FR nodes
                        setCellStyle(baseFRStyle, cells);
                        mlog10p = -Math.log10(frP);
                    }
                    if (Double.isInfinite(or)) {
                        // case-only node
                        String fillColor = "#FF8080";
                        setCellStyles("fillColor", fillColor, cells); 
                    } else if (or>1.0) {
                        // case-heavy node
                        int rInt = Math.min((int)(COLOR_FACTOR*mlog10p), 127) + 128;
                        String rHex = Integer.toHexString(rInt);
                        String fillColor = "#"+rHex+"8080";
                        setCellStyles("fillColor", fillColor, cells); 
                    } else if (or==0.0) {
                        // control-only node
                        String fillColor = "#8080FF";
                        setCellStyles("fillColor", fillColor, cells); 
                    } else if (or<1.0) {
                        // control-heavy node
                        int bInt = Math.min((int)(COLOR_FACTOR*mlog10p), 127) + 128;
                        String bHex = Integer.toHexString(bInt);
                        String fillColor = "#8080"+bHex;
                        setCellStyles("fillColor", fillColor, cells);
                    }
                    // bold white letters if significant
                    if (p<P_THRESHOLD) {
                        setCellStyles("fontColor", "white", cells);
                        setCellStyles("fontStyle", String.valueOf(mxConstants.FONT_BOLD), cells);
                    } else {
                        setCellStyles("fontColor", "black", cells);
                    }
                }
            } else if (c.isEdge()) {
                Edge e = (Edge) c.getValue();
                // this takes a long time
                if (decorateEdges) {
                    double strokeWidth = Math.max(1.0, 5.0*(double)graph.getPathCount(e)/(double)graph.getPathCount());
                    setCellStyles("strokeWidth", String.valueOf(strokeWidth), cells);
                }
                // highlighted path
                if (highlightedPath!=null) {
                    if (highlightedPathEdges.contains(e)) {
                        if (highlightedPath.isCase()) {
                            setCellStyles("strokeColor", "red", cells);
                        } else if (highlightedPath.isControl()) {
                            setCellStyles("strokeColor", "green", cells);
                        } else {
                            setCellStyles("strokeColor", "blue", cells);
                        }
                        setCellStyles("strokeWidth", "2.0", cells);
                    }
                }
            }
        }
        clearSelection();
    }

    /**
     * Override to show stats and node genotype as tooltip.
     */
    @Override
    public String getToolTipForCell(Object o) {
        mxCell c = (mxCell) o;
        if (c.isVertex()) {
            Node n = (Node) c.getValue();
            int pathCount = graph.getPathCount(n);
            double frac = (double)pathCount/(double)graph.getPathCount();
            String tip = "<html>";
            if (n.rs!=null) tip += n.rs+"<br/>";
            tip += n.contig+":"+n.start+"-"+n.end+"<br/>";
            tip += n.genotype+"<br/>";
            tip += pathCount+" paths<br/>";
            tip += percf.format(frac);
            Map<String,Integer> labelCounts = graph.getLabelCounts(n);
            if (labelCounts.containsKey("case") || labelCounts.containsKey("ctrl")) {
                int caseCounts = 0;
                int ctrlCounts = 0;
                double p = graph.fisherExactP(n);
                double or = graph.oddsRatio(n);
                if (labelCounts.containsKey("case")) caseCounts = labelCounts.get("case");
                if (labelCounts.containsKey("ctrl")) ctrlCounts = labelCounts.get("ctrl");
                tip += "<br/>"+caseCounts+"/"+ctrlCounts+"<br/>" +
                    "lOR="+orf.format(Math.log10(or))+"<br/>" +
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
            String tip = percf.format((double)graph.getPathCount(e)/(double)graph.getPathCount());
            Map<String,Integer> labelCounts = graph.getLabelCounts(e);
            if (labelCounts.containsKey("case") || labelCounts.containsKey("ctrl")) {
                int caseCounts = 0;
                int ctrlCounts = 0;
                if (labelCounts.containsKey("case")) caseCounts = labelCounts.get("case");
                if (labelCounts.containsKey("ctrl")) ctrlCounts = labelCounts.get("ctrl");
                tip += " ("+caseCounts+"/"+ctrlCounts+")";
            }
            return tip;
        } else {
            // don't think this is ever reached
            return "";
        }
    }
}
