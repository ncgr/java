package org.ncgr.pangenomics.allele.fr;

import org.ncgr.pangenomics.allele.*;

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
import java.util.Map;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

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
public class frGraphComponent extends mxGraphComponent implements ActionListener {
    static DecimalFormat df = new DecimalFormat("0.0");
    static DecimalFormat pf = new DecimalFormat("0.0E0");
    static DecimalFormat orf = new DecimalFormat("0.000");
    static DecimalFormat prif = new DecimalFormat("000");
    static String INFINITY = "\u221e";
    static String MATH_MINUS = "\u2212";
    
    // constructor parameters
    PangenomicGraph graph;
    Map<String,FrequentedRegion> frequentedRegions;
    FGraphXAdapter fgxAdapter;
    Properties parameters;
    Path highlightPath;
    boolean decorateEdges;
    
    JButton prevButton, nextButton;
    JButton zoomInButton, zoomOutButton;
    JLabel currentLabel;
    JLabel infoLabel;
    JLabel nodesLabel;
    ThermometerPlot thermPlot;
    
    Object[] frKeys;            // the FR map keys for navigating through the FRs

    double scale = 1.0;         // starting zoom scale
    int current = 0;            // key index of current FR being shown
    FrequentedRegion currentFR; // the current FR being shown
    
    /**
     * Constructor takes a FGraphXAdapter
     */
    frGraphComponent(PangenomicGraph graph, Map<String,FrequentedRegion> frequentedRegions, FGraphXAdapter fgxAdapter,
                     Properties parameters, Path highlightPath, boolean decorateEdges) {
        super(fgxAdapter);
        
        this.fgxAdapter = fgxAdapter;
        this.graph = graph;
        this.frequentedRegions = frequentedRegions;
        this.parameters = parameters;
        this.highlightPath = highlightPath;
        this.decorateEdges = decorateEdges;
        
        // housekeeping
        setConnectable(false);
        getGraph().setAllowDanglingEdges(false);
        setToolTips(true);
        setViewportBorder(new LineBorder(Color.BLACK));

        // load the FR keys into an array to select the chosen FR with an int on action events
        frKeys = frequentedRegions.keySet().toArray();

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
        // left arrow advances to next FR
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,0), "previous");
        getActionMap().put("previous", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    if (current>0) {
                        prevButton.doClick();
                    }
                }
            });
        // right arrow regresses to previous FR
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,0), "next");
        getActionMap().put("next", new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    if (current<(frKeys.length-1)) {
                        nextButton.doClick();
                    }
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
        GridBagLayout gridbag = new GridBagLayout();
        topPanel.setBackground(Color.LIGHT_GRAY);
        topPanel.setLayout(gridbag);
        
        GridBagConstraints c = new GridBagConstraints();

        // empty last label
        JLabel emptyLabel1 = new JLabel("");
        c.weightx = 1.0;
        gridbag.setConstraints(emptyLabel1, c);
        topPanel.add(emptyLabel1);
        
        // navigation buttons
        prevButton = new JButton("FR");
        prevButton.setActionCommand("previous");
        prevButton.addActionListener(this);
        c.weightx = 0.0;
        c.insets = new Insets(1, 4, 1, 4); // top, left, bottom, right
        gridbag.setConstraints(prevButton, c);
        topPanel.add(prevButton);
        currentLabel = new JLabel("FR 1 / "+frequentedRegions.size());
        currentLabel.setFont(currentLabel.getFont().deriveFont(Font.BOLD));
        topPanel.add(currentLabel);
        nextButton = new JButton("FR 2");
        nextButton.setActionCommand("next");
        nextButton.addActionListener(this);
        c.insets = new Insets(1, 4, 1, 16); // top, left, bottom, right
        gridbag.setConstraints(nextButton, c);
        topPanel.add(nextButton);

        // zoom buttons
        zoomOutButton = new JButton(MATH_MINUS);
        zoomOutButton.setActionCommand("zoomOut");
        zoomOutButton.setFont(zoomOutButton.getFont().deriveFont(Font.BOLD));
        zoomOutButton.addActionListener(this);
        c.insets = new Insets(1, 4, 1, 4); // top, left, bottom, right
        gridbag.setConstraints(zoomOutButton, c);
        topPanel.add(zoomOutButton);
        zoomInButton = new JButton("+");
        zoomInButton.setActionCommand("zoomIn");
        zoomInButton.setFont(zoomInButton.getFont().deriveFont(Font.BOLD));
        zoomInButton.addActionListener(this);
        gridbag.setConstraints(zoomInButton, c);
        topPanel.add(zoomInButton);

        // empty last label
        JLabel emptyLabel2 = new JLabel("");
        c.weightx = 1.0;
        c.gridwidth = GridBagConstraints.REMAINDER; // last
        gridbag.setConstraints(emptyLabel2, c);
        topPanel.add(emptyLabel2);

        // label with current FR's nodes on next line
        nodesLabel = new JLabel("");
        c.weightx = 0.0;
        gridbag.setConstraints(nodesLabel, c);
        topPanel.add(nodesLabel);

        // put the top panel on the graph
        setColumnHeaderView(topPanel);

        // buttons are disabled when we're on the first or last FR
        updateButtonStates();

        // set the current FR to the first one
        currentFR = frequentedRegions.get((String)frKeys[0]);
        
        // the side panel for information
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new GridLayout(2,1));
        sidePanel.setBackground(Color.LIGHT_GRAY);

        infoLabel = new JLabel();
        infoLabel.setVerticalAlignment(SwingConstants.TOP);
        sidePanel.add(infoLabel);

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

        updateSidePanel(currentFR, parameters);
        updateNodesLabel(currentFR);
        
        // put the side panel on the graph
        setRowHeaderView(sidePanel);
    }
    
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("next") || command.equals("previous")) {
            if (command.equals("next")) {
                current++;
            } else if (command.equals("previous")) {
                current--;
            }
            currentFR = frequentedRegions.get((String)frKeys[current]);
            fgxAdapter = new FGraphXAdapter(graph, currentFR, highlightPath, decorateEdges);
            setGraph(fgxAdapter);
            updateSidePanel(currentFR, parameters);
            updateNodesLabel(currentFR);
            executeLayout();
            updateButtonStates();
        } else if (command.equals("zoomIn") || command.equals("zoomOut")) {
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
     * Update the button states. current is zero-based, we'll list FRs as one-based.
     */
    public void updateButtonStates() {
        currentLabel.setText("FR "+(current+1)+" / "+frequentedRegions.size());
        if (current==0) {
            prevButton.setText("FR");
            prevButton.setEnabled(false);
        } else {
            prevButton.setText("FR "+current);
            prevButton.setEnabled(true);
        }
        if (current==(frKeys.length-1)) {
            nextButton.setText("FR");
            nextButton.setEnabled(false);
        } else {
            nextButton.setText("FR "+(current+2));
            nextButton.setEnabled(true);
        }
    }

    /**
     * Update the info label on the sidePanel with FR run parameters and graph info
     * #alpha=1.0
     * #kappa=100
     * #clocktime=05:05:05
     * #Wed Jan 08 21:26:53 MST 2020
     * minSup=100
     * debug=false
     * minSize=1
     * minLen=1.0
     * txtFile=HLAA.nodes.txt
     * resume=false
     * verbose=true
     * graphName=HLAA
     * requiredNode=0
     * maxRound=20
     * priorityOption=4
     * minPriority=0
     * keepOption=subset
     */
    public void updateSidePanel(FrequentedRegion fr, Properties parameters) {
        double p = fr.fisherExactP();
        double or = fr.oddsRatio();
        String kappaString = String.valueOf(fr.kappa);
        if (fr.kappa==Integer.MAX_VALUE) kappaString = INFINITY;
        // info text
        String infoLabelString = "<html>"+
            "<b>"+graph.getName()+"</b><br/>" +
            graph.getNodes().size()+" nodes<br/>" +
            graph.getPaths().size()+" paths<br/>" +
            graph.getLabelCounts().get("case")+"/"+graph.getLabelCounts().get("ctrl") +
            "<hr/>" +
            "alpha="+fr.alpha+"<br/>" +
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
            "FR "+(current+1)+":<br/>" +
            "size="+fr.nodes.size()+"<br/>" +
            "avgLen="+df.format(fr.avgLength)+"<br/>" +
            "support="+fr.caseSupport+"/"+fr.ctrlSupport+"<br/>" +
            "p="+pf.format(p)+"<br/>" +
            "O.R.="+orf.format(or)+"<br/>" +
            "priority="+fr.priority +
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
            thermPlot.setSubrangePaint(ThermometerPlot.WARNING, Color.GREEN);
            thermPlot.setSubrangePaint(ThermometerPlot.CRITICAL, Color.GREEN);
        }
        thermPlot.setDataset(new DefaultValueDataset(fr.priority));
    }

    /**
     * Update the nodes label which shows the current FR's nodes.
     */
    public void updateNodesLabel(FrequentedRegion fr) {
        nodesLabel.setText(fr.nodes.toString());
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

