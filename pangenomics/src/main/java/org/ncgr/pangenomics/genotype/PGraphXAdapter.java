package org.ncgr.pangenomics.genotype;

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

    static String LABEL_COLOR_SIG = "white";
    static String LABEL_COLOR_NONSIG = "black";
    static String STROKE_COLOR_HOM = "black";
    static String STROKE_COLOR_HET = "green";
    
    static double P_THRESHOLD = 5.0e-8; // standard GWAS value
    static int COLOR_FACTOR = 16;

    PangenomicGraph graph;
    boolean hasCaseControlLabels;

    int numPaths;
    double minorNodeFrac;

    Path highlightedPath = null;
    List<Edge> highlightedPathEdges;

    public PGraphXAdapter(PangenomicGraph graph, boolean decorateEdges, Path highlightedPath, double minorNodeFrac) {
        super(new DefaultListenableGraph<Node,Edge>(graph));
        this.graph = graph;
        this.minorNodeFrac = minorNodeFrac;

        numPaths = graph.getPathCount();

        // get the highlighted path edges
        if (highlightedPath!=null) {
            highlightedPathEdges = highlightedPath.getEdges();
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
        defaultVertexStyle.put("spacingTop", "2");
        defaultStylesheet.setDefaultVertexStyle(defaultVertexStyle);
        // apply the default stylesheet
        setStylesheet(defaultStylesheet);
        setAutoSizeCells(true);

        // style for minor nodes
        String minorStyle = "shape=ellipse;fontColor=#A0A0A0;fillColor=white;strokeColor=gray;strokeWidth=1.0;gradientColor=none;spacingTop=2";
        // style for no-call nodes
        String noCallStyle = "shape=hexagon;fontColor=#A0A0A0;fillColor=white;strokeColor=gray;strokeWidth=1.0;gradientColor=none;spacingTop=2";

        // logical to do case/control ops
        hasCaseControlLabels = graph.labelCounts.containsKey("case") && graph.labelCounts.containsKey("ctrl");

        // color the nodes, plus other decoration
        selectAll();
        Object[] allCells = getSelectionCells();
        for (Object o : allCells) {
            Object[] cells = {o};
            mxCell c = (mxCell) o;
            if (c.isVertex()) {
                Node n = (Node) c.getValue();
                int pathCount = graph.getPathCount(n);
                double pathFrac = (double)pathCount / (double)numPaths;
                if (hasCaseControlLabels) {
                    if (pathCount>0 && n.genotype.equals("./.")) {
                        setCellStyle(noCallStyle, cells);
                    } else if (pathCount>0 && pathFrac<minorNodeFrac) {
                        setCellStyle(minorStyle, cells);
                    } else if (pathCount>0) {
                        double or = graph.oddsRatio(n);
                        double p = graph.fisherExactP(n);
                        boolean genotypeCalled = !n.genotype.equals("./.");
                        // styling based on segregation and genotype
                        if (graph.getPathCount(n)==graph.getPathCount()) {
                            // all paths go through node, uninteresting
                            setCellStyles("fillColor", "white", cells);
                            setCellStyles("fontColor", "black", cells);
                        } else if (!genotypeCalled) {
                            // no call
                            setCellStyles("fillColor", "white", cells);
                            setCellStyles("fontColor", "black", cells);
                        } else if (Double.isInfinite(or)) {
                            // case-only node
                            String fillColor = "#FF8080";
                            setCellStyles("fillColor", fillColor, cells);
                        } else if (or>1.0) {
                            // case-heavy node
                            double mlog10p = -Math.log10(p);
                            int rInt = Math.min((int)(COLOR_FACTOR*mlog10p), 127) + 128; // full color at COLOR_FACTOR*mlog10p=127
                            String rHex = Integer.toHexString(rInt);
                            String fillColor = "#"+rHex+"8080";
                            setCellStyles("fillColor", fillColor, cells);
                        } else if (or==0.00) {
                            // control-only node
                            String fillColor = "#8080FF";
                            setCellStyles("fillColor", fillColor, cells);
                        } else if (or<1.0) {
                            // control-heavy node
                            double mlog10p = -Math.log10(p);
                            int bInt = Math.min((int)(COLOR_FACTOR*mlog10p), 127) + 128; // full color at COLOR_FACTOR*mlog10p=127
                            String bHex = Integer.toHexString(bInt);
                            String fillColor = "#8080"+bHex;
                            setCellStyles("fillColor", fillColor, cells);
                        }
                        // bold white letters if significant
                        if (genotypeCalled && p<P_THRESHOLD) {
                            setCellStyles("fontColor", LABEL_COLOR_SIG, cells);
                            setCellStyles("fontStyle", String.valueOf(mxConstants.FONT_BOLD), cells);
                        } else {
                            setCellStyles("fontColor", LABEL_COLOR_NONSIG, cells);
                        }
                        // set border color based on HOM/HET genotype
                        if (genotypeCalled) {
                            String[] alleles = new String[0];
                            if (n.genotype.contains("/")) alleles = n.genotype.split("/"); // unphased
                            if (n.genotype.contains("|")) alleles = n.genotype.split("/"); // phased
                            if (alleles.length==2) {
                                if (alleles[0].equals(alleles[1])) {
                                    setCellStyles("strokeColor", STROKE_COLOR_HOM, cells);
                                } else {
                                    setCellStyles("strokeColor", STROKE_COLOR_HET, cells);
                                }
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
                // highlighted path
                if (highlightedPath!=null) {
                    if (highlightedPathEdges.contains(e)) {
                        if (highlightedPath.isCase()) {
                            setCellStyles("strokeColor", "red", cells);
                        } else if (highlightedPath.isControl()) {
                            setCellStyles("strokeColor", "blue", cells);
                        } else {
                            setCellStyles("strokeColor", "black", cells);
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
            int graphPathCount = graph.getPathCount();
            int nodePathCount = graph.getPathCount(n);
            double nodeFrac = (double)nodePathCount/(double)graphPathCount; // MAF
            String tip = "<html>";
            if (n.rs!=null) tip += n.rs+"<br/>";
            tip += n.contig+":"+n.start+"-"+n.end+"<br/>";
            tip += n.genotype+"<br/>";
            tip += nodePathCount+" paths<br/>";
            tip += percf.format(nodeFrac);
            if (hasCaseControlLabels) {
                double lOR = Math.log10(graph.oddsRatio(n));
                double p = graph.fisherExactP(n);
                Map<String,Integer> graphLabelCounts = graph.labelCounts;
                Map<String,Integer> nodeLabelCounts = graph.getLabelCounts(n);
                int nodeCaseCounts = 0;
                int nodeCtrlCounts = 0;
                if (nodeLabelCounts.containsKey("case")) nodeCaseCounts = nodeLabelCounts.get("case");
                if (nodeLabelCounts.containsKey("ctrl")) nodeCtrlCounts = nodeLabelCounts.get("ctrl");
                double nodeCaseFrac = (double)nodeCaseCounts/(double)graphLabelCounts.get("case");
                double nodeCtrlFrac = (double)nodeCtrlCounts/(double)graphLabelCounts.get("ctrl");
                tip += "<br/>"+
                    nodeCaseCounts+"/"+nodeCtrlCounts+"<br/>" +
                    percf.format(nodeCaseFrac)+"/"+percf.format(nodeCtrlFrac)+"<br/>" +
                    "lOR="+orf.format(lOR)+"<br/>" +
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
                Map<String,Integer> graphLabelCounts = graph.labelCounts;
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
