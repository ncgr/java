package org.ncgr.pangenomics.genotype.fr;

import org.ncgr.pangenomics.genotype.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.text.DecimalFormat;

import java.util.List;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.ThermometerPlot;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;

import javax.swing.border.*;

/**
 * Extend mxGraphComponent to implement ActionListener for button events and such.
 * mxGraphComponent in turn extends JScrollPane.
 */
public class frGraphComponent extends mxGraphComponent implements ActionListener, ListSelectionListener {
    static DecimalFormat df = new DecimalFormat("0.0");
    static DecimalFormat pf = new DecimalFormat("0.0E0");
    static DecimalFormat orf = new DecimalFormat("0.000");
    static DecimalFormat prif = new DecimalFormat("000");
    static String INFINITY = "\u221e";
    static String MATH_MINUS = "\u2212";
    static String CHECKMARK = "\u2713";

    static boolean DEBUG = false;
    
    // constructor parameters
    PangenomicGraph graph;
    FGraphXAdapter fgxAdapter;
    TreeMap<String,FrequentedRegion> frequentedRegions;
    Properties parameters;

    // FGraphXAdapter parameters stored here
    Path highlightedPath;
    boolean decorateEdges;
    double minorNodeFrac;

    // the JList of FRs
    JList<String> frList;
    Object[] frKeys;            // the FR map keys for navigating through the FRs
    String[] frLabels;
    int currentFRIndex;
    FrequentedRegion currentFR; // the current FR being shown

    // the JList of sample names and whatnot
    JList<String> sampleList;
    String[] sampleNames;
    int currentSampleIndex;
    JScrollPane sampleScrollPane;
    
    JButton zoomInButton, zoomOutButton;
    JLabel currentLabel;
    JLabel infoLabel;
    ThermometerPlot thermPlot;

    double scale = 1.0;         // starting zoom scale
    
    /**
     * Constructor takes a FGraphXAdapter
     */
    frGraphComponent(PangenomicGraph graph, FGraphXAdapter fgxAdapter, TreeMap<String,FrequentedRegion> frequentedRegions, Properties parameters) {
        super(fgxAdapter);
        this.graph = graph;
        this.fgxAdapter = fgxAdapter;
        this.frequentedRegions = frequentedRegions;
        this.parameters = parameters;

        highlightedPath = fgxAdapter.highlightedPath;
        decorateEdges = fgxAdapter.decorateEdges;
        minorNodeFrac = fgxAdapter.minorNodeFrac;
        
        // housekeeping
        setConnectable(false);
        getGraph().setAllowDanglingEdges(false);
        setToolTips(true);
        setViewportBorder(new LineBorder(Color.BLACK));
        if (DEBUG) System.err.println("setViewPortBorder done.");

        // load the FR keys into an array to select the chosen FR with an int on action events
        frKeys = frequentedRegions.keySet().toArray();

        // set the current FR to the first one
        currentFR = frequentedRegions.get((String)frKeys[0]);
        currentFR.updateSupport();
        if (DEBUG) System.err.println("currentFR.updateSupport() done.");

        // zoom in button -- plus is equals plus shift
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS,KeyEvent.SHIFT_DOWN_MASK), "zoomIn");
        getActionMap().put("zoomIn", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    zoomInButton.doClick();
                }
            });
        // zoom in button -- equals key is equivalent to plus without shift
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS,0), "zoomIn");
        getActionMap().put("zoomIn", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    zoomInButton.doClick();
                }
            });
        // zoom out button
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS,0), "zoomOut");
        getActionMap().put("zoomOut", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    zoomOutButton.doClick();
                }
            });
        // q quits
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Q,0), "quit");
        getActionMap().put("quit", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
        if (DEBUG) System.err.println("button actions created.");

        // add a column header with navigation/zoom buttons
        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.LIGHT_GRAY);

        // use a GridBagLayout
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        topPanel.setLayout(gridbag);
        
        // FR selector
        int maxFRLabelLength = 0;
        frLabels = new String[frKeys.length];
        for (int i=0; i<frKeys.length; i++) {
            FrequentedRegion fr = frequentedRegions.get((String)frKeys[i]);
            frLabels[i] = (i+1)+":"+fr.nodes.toString()+" "+fr.support+"  "+orf.format(Math.log10(fr.oddsRatio()))+"  "+fr.priority;
            if (frLabels[i].length()>maxFRLabelLength) maxFRLabelLength = frLabels[i].length();
        }
        int preferredFRXsize = maxFRLabelLength*9;
        frList = new JList<String>(frLabels);
        frList.setLayoutOrientation(JList.VERTICAL);
        frList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        frList.addListSelectionListener(this);
        JScrollPane frScrollPane = new JScrollPane(frList);
        frScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        frScrollPane.setPreferredSize(new Dimension(preferredFRXsize, 18));
        c.insets = new Insets(1, 4, 1, 4); // top, left, bottom, right
        gridbag.setConstraints(frScrollPane, c);
        topPanel.add(frScrollPane);
        if (DEBUG) System.err.println("FR selector built.");

        // sample/path selector
        sampleNames = graph.getPathNames();
        sampleList = new JList<String>();
        sampleList.setLayoutOrientation(JList.VERTICAL);
        sampleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sampleList.addListSelectionListener​(this);
        sampleScrollPane = new JScrollPane(sampleList);
        sampleScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        updateSampleScrollPane();
        c.insets = new Insets(1, 4, 1, 4); // top, left, bottom, right
        gridbag.setConstraints(sampleScrollPane, c);
        topPanel.add(sampleScrollPane);
        if (DEBUG) System.err.println("sample/path selector built.");
        
        // zoom out button
        zoomOutButton = new JButton(MATH_MINUS);
        zoomOutButton.setActionCommand("zoomOut");
        zoomOutButton.setFont(zoomOutButton.getFont().deriveFont(Font.BOLD));
        zoomOutButton.addActionListener(this);
        c.insets = new Insets(1, 4, 1, 4); // top, left, bottom, right
        gridbag.setConstraints(zoomOutButton, c);
        topPanel.add(zoomOutButton);
        
        // zoom in button
        zoomInButton = new JButton("+");
        zoomInButton.setActionCommand("zoomIn");
        zoomInButton.setFont(zoomInButton.getFont().deriveFont(Font.BOLD));
        zoomInButton.addActionListener(this);
        gridbag.setConstraints(zoomInButton, c);
        topPanel.add(zoomInButton);
        if (DEBUG) System.err.println("zoom buttons built.");

        // put the top panel on the graph
        setColumnHeaderView(topPanel);

        // the side panel for information
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new GridLayout(2,1));
        sidePanel.setBackground(Color.LIGHT_GRAY);
        infoLabel = new JLabel();
        infoLabel.setVerticalAlignment(SwingConstants.TOP);
        sidePanel.add(infoLabel);
        if (DEBUG) System.err.println("side panel info label added.");

        // priority-value thermometer
        thermPlot = new ThermometerPlot();
        thermPlot.setUnits(ThermometerPlot.UNITS_NONE);
        thermPlot.setColumnRadius(10);
        thermPlot.setBulbRadius(30);
        thermPlot.setGap(0);
        thermPlot.setBackgroundPaint(Color.LIGHT_GRAY);
        thermPlot.setOutlineVisible(false);
        thermPlot.setLowerBound(0.0);
        thermPlot.setUpperBound(currentFR.priority);
        thermPlot.setSubrange(ThermometerPlot.NORMAL, 0.0, 200.0);
        thermPlot.setSubrange(ThermometerPlot.WARNING, 200.0, 730.0);
        thermPlot.setSubrange(ThermometerPlot.CRITICAL, 730.0, currentFR.priority);
        // thermPlot.setValueFormat(df);
        ChartPanel thermPanel = new ChartPanel(new JFreeChart(thermPlot));
        thermPanel.setPreferredSize(new Dimension(100,200));
        thermPanel.setMaximumSize(new Dimension(1000,200));
        sidePanel.add(thermPanel);
        if (DEBUG) System.err.println("side panel therm panel added.");
        
        // update for the current FR
        updateSidePanel();
        if (DEBUG) System.err.println("side panel updated.");
        
        // put the side panel on the graph
        setRowHeaderView(sidePanel);
        if (DEBUG) System.err.println("side panel added to graph.");
    }

    /**
     * Update the sample scroll pane, called when the FR is changed to update the checkmarks.
     */
    void updateSampleScrollPane() {
        int maxSampleLabelLength = 0;
        String[] sampleLabels = new String[sampleNames.length];
        for (int i=0; i<sampleNames.length; i++) {
            Path p = graph.getPath(sampleNames[i]);
            sampleLabels[i] = sampleNames[i]+" ("+p.label+")";
            if (currentFR.containsSubpathOf(p)) {
                sampleLabels[i] += CHECKMARK;
            }
            if (sampleLabels[i].length()>maxSampleLabelLength) maxSampleLabelLength = sampleLabels[i].length();
        }
        int preferredSampleNameXsize = maxSampleLabelLength*9;
        sampleList.setListData(sampleLabels);
        sampleScrollPane.setPreferredSize(new Dimension(preferredSampleNameXsize, 18));
    }
    
    /**
     * Handle button actions.
     */
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("zoomIn") || command.equals("zoomOut")) {
            if (command.equals("zoomIn")) {
                scale = scale*Math.sqrt(2.0);
            } else if (command.equals("zoomOut")) {
                scale = scale/Math.sqrt(2.0);
            }
            fgxAdapter.getView().setScale(scale);
            refresh();
        }
    }

    /**
     * Update the info label on the sidePanel with currentFR run parameters and graph info.
     * #alpha=1.0
     * #kappa=2147483647
     * #clocktime=02:06:49
     * #Mon Apr 27 11:47:37 MDT 2020
     * graphName=SchizophreniaSwedish_Sklar/HLAA
     * minSize=0
     * minLength=0.0
     * minSupport=2500
     * minPriority=700
     * minMAF=0.01
     * maxRound=50
     * priorityOption=4
     * requiredNodeString=[757]
     * excludedNodeString=[]
     * keepOption=subset
     * requireBestNodeSet=true
     */
    public void updateSidePanel() {
        double p = currentFR.fisherExactP();
        double or = currentFR.oddsRatio();
        String kappaString = String.valueOf(currentFR.kappa);
        if (currentFR.kappa==Integer.MAX_VALUE) kappaString = INFINITY;
        // info text
        String infoLabelString = "<html>";
        infoLabelString += "<b>"+graph.name+"</b>";
        infoLabelString += "<br/>"+graph.getNodes().size()+" nodes, "+graph.paths.size()+" paths";
        infoLabelString += "<br/>"+graph.labelCounts.get("case")+" cases / "+graph.labelCounts.get("ctrl")+" controls";
        infoLabelString += "<hr/>";
        infoLabelString += "alpha="+currentFR.alpha;
        infoLabelString += "<br/>kappa="+kappaString;
        infoLabelString += "<br/>minSupport="+parameters.getProperty("minSupport");
        infoLabelString += "<br/>minLength="+parameters.getProperty("minLength");
        infoLabelString += "<br/>minSize="+parameters.getProperty("minSize");
        infoLabelString += "<br/>minPriority="+parameters.getProperty("minPriority");
        infoLabelString += "<br/>minMAF="+parameters.getProperty("minMAF");
        infoLabelString += "<br/>maxRound="+parameters.getProperty("maxRound");
        infoLabelString += "<br/>priorityOption="+parameters.getProperty("priorityOption");
        infoLabelString += "<br/>keepOption="+parameters.getProperty("keepOption");
        infoLabelString += "<br/>requireBestNodeSet="+parameters.getProperty("requireBestNodeSet");
        if (!parameters.getProperty("requiredNodeString").equals("[]")) {
            infoLabelString += "<br/>requiredNodes="+parameters.getProperty("requiredNodeString");
        }
        if (!parameters.getProperty("excludedNodeString").equals("[]")) {
            infoLabelString += "<br/>excludedNodes="+parameters.getProperty("excludedNodeString");
        }
        infoLabelString += "<hr/>";
        infoLabelString += "FR "+(currentFRIndex+1)+":";
        infoLabelString += "<br/>size="+currentFR.nodes.size();
        infoLabelString += "<br/>support="+currentFR.caseSubpathSupport+"/"+currentFR.ctrlSubpathSupport;
        infoLabelString += "<br/>p="+pf.format(p);
        infoLabelString += "<br/>log10(OR)="+orf.format(Math.log10(or));
        infoLabelString += "<br/>priority="+currentFR.priority;
        infoLabelString += "<hr/>";
        infoLabelString += "</html>";
        infoLabel.setText(infoLabelString);
        // thermometer
        if (or>1.0) {
            thermPlot.setSubrangePaint(ThermometerPlot.NORMAL, Color.GRAY);
            thermPlot.setSubrangePaint(ThermometerPlot.WARNING, Color.RED);
            thermPlot.setSubrangePaint(ThermometerPlot.CRITICAL, Color.RED);
        } else {
            thermPlot.setSubrangePaint(ThermometerPlot.NORMAL, Color.GRAY);
            thermPlot.setSubrangePaint(ThermometerPlot.WARNING, Color.BLUE);
            thermPlot.setSubrangePaint(ThermometerPlot.CRITICAL, Color.BLUE);
        }
        thermPlot.setDataset(new DefaultValueDataset(currentFR.priority));
    }

    /**
     * Handle FR list and sample list selection changes.
     */
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int firstIndex = e.getFirstIndex();
            int lastIndex = e.getLastIndex();
            if (e.getSource().equals(frList)) {
                if (firstIndex==currentFRIndex) {
                    currentFRIndex = lastIndex;
                } else  {
                    currentFRIndex = firstIndex;
                }
                currentFR = frequentedRegions.get((String)frKeys[currentFRIndex]);
                if (currentFR.subpaths==null) currentFR.updateSupport();
                fgxAdapter = new FGraphXAdapter(graph, currentFR, highlightedPath, decorateEdges, minorNodeFrac);
                setGraph(fgxAdapter);
                updateSidePanel();
                updateSampleScrollPane();
                executeLayout();
            } else if (e.getSource().equals(sampleList)) {
                if (firstIndex==currentSampleIndex) {
                    currentSampleIndex = lastIndex;
                } else  {
                    currentSampleIndex = firstIndex;
                }
                sampleList.ensureIndexIsVisible(currentSampleIndex);
                String sampleName = sampleNames[currentSampleIndex];
                highlightedPath = graph.getPath(sampleName);
                fgxAdapter = new FGraphXAdapter(graph, currentFR, highlightedPath, decorateEdges, minorNodeFrac);
                setGraph(fgxAdapter);
                executeLayout();
            }
        }
    }

    /**
     * Execute the layout.
     */
    public void executeLayout() {
        fgxAdapter.getView().setScale(scale);
        mxHierarchicalLayout layout = new mxHierarchicalLayout(fgxAdapter, SwingConstants.WEST);
        layout.setFineTuning(true);
        layout.execute(fgxAdapter.getDefaultParent());
    }
}

