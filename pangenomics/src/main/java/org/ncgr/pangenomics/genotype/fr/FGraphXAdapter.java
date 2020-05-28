package org.ncgr.pangenomics.genotype.fr;

import org.ncgr.pangenomics.genotype.*;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultListenableGraph;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxStylesheet;

/**
 * Extend JGraphXAdapter to support overridden methods for tooltips and such.
 * JGraphXAdapter in turn extends mxGraph.
 */
public class FGraphXAdapter extends JGraphXAdapter<Node,Edge> {

    static final double P_THRESHOLD = 5e-8; // GWAS standard
    static final int COLOR_FACTOR = 16;
    static final double FR_CELL_RESIZE = 1.5;

    static String LABEL_COLOR_SIG = "white";
    static String LABEL_COLOR_NONSIG = "black";
    static String STROKE_COLOR_HOM = "black";
    static String STROKE_COLOR_HET = "green";
    static String CASE_COLOR = "red";
    static String CTRL_COLOR = "blue";
    
    static DecimalFormat pf = new DecimalFormat("0.0E0");
    static DecimalFormat orf = new DecimalFormat("0.000");
    static DecimalFormat percf = new DecimalFormat("0.0%");

    PangenomicGraph graph;
    FrequentedRegion fr;
    Path highlightedPath;
    boolean decorateEdges;
    double minorNodeFrac;

    int numPaths;
    List<Edge> highlightedPathEdges;

    public FGraphXAdapter(PangenomicGraph graph, FrequentedRegion fr, Path highlightedPath, boolean decorateEdges, double minorNodeFrac) {
        super(new DefaultListenableGraph<Node,Edge>(graph));
        this.graph = graph;
        this.fr = fr;
        this.highlightedPath = highlightedPath;
        this.decorateEdges = decorateEdges;
        this.minorNodeFrac = minorNodeFrac;

        // design tweaks

        
        numPaths = graph.getPathCount();

        // get the highlighted path edges
        if (highlightedPath!=null) {
            highlightedPathEdges = highlightedPath.getEdges();
        }

        // default edge style
        mxStylesheet defaultStylesheet = getStylesheet();
        Map<String,Object> defaultEdgeStyle = defaultStylesheet.getDefaultEdgeStyle();
        defaultEdgeStyle.put("strokeColor", "gray");
        defaultEdgeStyle.put("fontColor", "gray");
        defaultEdgeStyle.put(mxConstants.STYLE_NOLABEL, "1");

        // default vertex style
        Map<String,Object> defaultVertexStyle = defaultStylesheet.getDefaultVertexStyle();
        defaultVertexStyle.put(mxConstants.STYLE_RESIZABLE, "0");
        defaultVertexStyle.put(mxConstants.STYLE_SHADOW, "0");
        defaultVertexStyle.put("fillColor", "white");
        defaultVertexStyle.put("fontColor", "black");
        defaultVertexStyle.put("shape", mxConstants.SHAPE_ELLIPSE);
        defaultVertexStyle.put("spacingTop", "2");
        

        defaultStylesheet.setDefaultEdgeStyle(defaultEdgeStyle);
        defaultStylesheet.setDefaultVertexStyle(defaultVertexStyle);
        setStylesheet(defaultStylesheet);
        setAutoSizeCells(true);
        
        // base style for FR nodes
        String baseFRStyle = "shape=rectangle;fontColor=black;fillColor=#808080;strokeColor=black;strokeWidth=2.0;gradientColor=none;spacingTop=2";
        // style for minor nodes
        String minorStyle = "shape=ellipse;fontColor=#A0A0A0;fillColor=white;strokeColor=gray;strokeWidth=1.0;gradientColor=none;spacingTop=2";
        // style for no-call nodes
        String noCallStyle = "shape=hexagon;fontColor=#A0A0A0;fillColor=white;strokeColor=gray;strokeWidth=1.0;gradientColor=none;spacingTop=2";
        
        // color the nodes and edges
        selectAll();
        Object[] allCells = getSelectionCells();
        for (Object o : allCells) {
            Object[] cells = {o};
            mxCell c = (mxCell) o;
            if (c.isVertex()) {
                Node n = (Node) c.getValue();
                int pathCount = graph.getPathCount(n);
                double pathFrac = (double)pathCount / (double)numPaths;
                if (pathCount>0 && !n.isCalled) {
                    setCellStyle(noCallStyle, cells);
                } else if (pathCount>0 && pathFrac<minorNodeFrac) {
                    setCellStyle(minorStyle, cells);
                } else if (pathCount>0) {
                    double or = graph.oddsRatio(n);
                    double p = graph.fisherExactP(n);
                    // special base style for FR nodes
                    if (fr.containsNode(n)) {
                        setCellStyle(baseFRStyle, cells);
                        mxGeometry geom = c.getGeometry();
                        double height = geom.getHeight();
                        double width = geom.getWidth();
                        geom.setHeight(height*FR_CELL_RESIZE);
                        geom.setWidth(width*FR_CELL_RESIZE);
                    }
                    // color cell based on O.R. and p-value
                    if (!n.isCalled) {
                        // no call
                        setCellStyles("fillColor", "white", cells);
                    } else if (Double.isInfinite(or)) {
                        // case-only node
                        String fillColor = "#FF8080";
                        setCellStyles("fillColor", fillColor, cells); 
                    } else if (or>1.0) {
                        // case-heavy node
                        double mlog10p = -Math.log10(p);
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
                        double mlog10p = -Math.log10(p);
                        int bInt = Math.min((int)(COLOR_FACTOR*mlog10p), 127) + 128;
                        String bHex = Integer.toHexString(bInt);
                        String fillColor = "#8080"+bHex;
                        setCellStyles("fillColor", fillColor, cells);
                    }
                    // use bold white letters if significant
                    if (n.isCalled && p<P_THRESHOLD) {
                        setCellStyles("fontColor", LABEL_COLOR_SIG, cells);
                        setCellStyles("fontStyle", String.valueOf(mxConstants.FONT_BOLD), cells);
                    } else {
                        setCellStyles("fontColor", LABEL_COLOR_NONSIG, cells);
                    }
                    // set border color based on HOM/HET genotype
                    if (n.isCalled) {
                        String[] genotypes = n.genotype.split("/");
                        if (genotypes[0].equals(genotypes[1])) {
                            setCellStyles("strokeColor", STROKE_COLOR_HOM, cells);
                        } else {
                            setCellStyles("strokeColor", STROKE_COLOR_HET, cells);
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
                            setCellStyles("strokeColor", CASE_COLOR, cells);
                        } else if (highlightedPath.isControl()) {
                            setCellStyles("strokeColor", CTRL_COLOR, cells);
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
            tip += percf.format(frac)+"<br/>";
            Map<String,Integer> labelCounts = graph.getLabelCounts(n);
            if (labelCounts.containsKey("case") || labelCounts.containsKey("ctrl")) {
                int caseCounts = 0;
                int ctrlCounts = 0;
                double p = graph.fisherExactP(n);
                double or = graph.oddsRatio(n);
                if (labelCounts.containsKey("case")) caseCounts = labelCounts.get("case");
                if (labelCounts.containsKey("ctrl")) ctrlCounts = labelCounts.get("ctrl");
                tip += caseCounts+"/"+ctrlCounts+"<br/>";
                tip += "OR="+orf.format(or)+"("+orf.format(Math.log10(or))+")<br/>";
                tip += "p="+pf.format(p)+"<br/>";
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
