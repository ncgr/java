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
    
    // constructor parameters
    PangenomicGraph graph;
    FGraphXAdapter fgxAdapter;
    TreeMap<String,FrequentedRegion> frequentedRegions;
    Properties parameters;

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

    Path highlightedPath;
    boolean decorateEdges;
    
    JButton zoomInButton, zoomOutButton;
    JLabel currentLabel;
    JLabel infoLabel;
    ThermometerPlot thermPlot;

    double scale = 1.0;         // starting zoom scale
    
    /**
     * Constructor takes a FGraphXAdapter
     */
    frGraphComponent(PangenomicGraph graph, FGraphXAdapter fgxAdapter, boolean decorateEdges,
                     TreeMap<String,FrequentedRegion> frequentedRegions, Properties parameters) {
        super(fgxAdapter);
        this.fgxAdapter = fgxAdapter;
        this.graph = graph;
        this.decorateEdges = decorateEdges;
        this.frequentedRegions = frequentedRegions;
        this.parameters = parameters;
        
        // housekeeping
        setConnectable(false);
        getGraph().setAllowDanglingEdges(false);
        setToolTips(true);
        setViewportBorder(new LineBorder(Color.BLACK));

        // load the FR keys into an array to select the chosen FR with an int on action events
        frKeys = frequentedRegions.keySet().toArray();

        // set the current FR to the first one
        currentFR = frequentedRegions.get((String)frKeys[0]);
        currentFR.updateSupport();

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

        // put the top panel on the graph
        setColumnHeaderView(topPanel);

        // the side panel for information
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new GridLayout(2,1));
        sidePanel.setBackground(Color.LIGHT_GRAY);
        infoLabel = new JLabel();
        infoLabel.setVerticalAlignment(SwingConstants.TOP);
        sidePanel.add(infoLabel);

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
        thermPlot.setSubrange(ThermometerPlot.WARNING, 200.0, 400.0);
        thermPlot.setSubrange(ThermometerPlot.CRITICAL, 400.0, currentFR.priority);
        // thermPlot.setValueFormat(df);
        ChartPanel thermPanel = new ChartPanel(new JFreeChart(thermPlot));
        thermPanel.setPreferredSize(new Dimension(100,200));
        thermPanel.setMaximumSize(new Dimension(1000,200));
        sidePanel.add(thermPanel);

        // update for the current FR
        updateSidePanel();
        
        // put the side panel on the graph
        setRowHeaderView(sidePanel);
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
     */
    public void updateSidePanel() {
        double p = currentFR.fisherExactP();
        double or = currentFR.oddsRatio();
        String kappaString = String.valueOf(currentFR.kappa);
        if (currentFR.kappa==Integer.MAX_VALUE) kappaString = INFINITY;
        // info text
        String infoLabelString = "<html>"+
            "<b>"+graph.name+"</b><br/>" +
            graph.getNodes().size()+" nodes<br/>" +
            graph.paths.size()+" paths<br/>" +
            graph.labelCounts.get("case")+"/"+graph.labelCounts.get("ctrl") +
            "<hr/>" +
            "alpha="+currentFR.alpha+"<br/>" +
            "kappa="+kappaString+"<br/>" +
            "minSup="+parameters.getProperty("minSup")+"<br/>" +
            "minLen="+parameters.getProperty("minLen")+"<br/>" +
            "minSize="+parameters.getProperty("minSize")+"<br/>" +
            "minPriority="+parameters.getProperty("minPriority")+"<br/>" +
            "maxRound="+parameters.getProperty("maxRound")+"<br/>" +
            "priorityOption="+parameters.getProperty("priorityOption")+"<br/>" +
            "keepOption="+parameters.getProperty("keepOption")+"<br/>";
        if (parameters.getProperty("requiredNode")!=null) {
            infoLabelString += "requiredNode="+parameters.getProperty("requiredNode")+"<br/>";
        }
        infoLabelString +=
            "<hr/>" +
            "FR "+(currentFRIndex+1)+":<br/>" +
            "size="+currentFR.nodes.size()+"<br/>" +
            "support="+currentFR.caseSubpathSupport+"/"+currentFR.ctrlSubpathSupport+"<br/>" +
            "p="+pf.format(p)+"<br/>" +
            "log10(OR)="+orf.format(Math.log10(or))+"<br/>" +
            "priority="+currentFR.priority +
            "<hr/>"+
            "</html>";
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
                fgxAdapter = new FGraphXAdapter(graph, currentFR, highlightedPath, decorateEdges);
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
                fgxAdapter = new FGraphXAdapter(graph, currentFR, highlightedPath, decorateEdges);
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

