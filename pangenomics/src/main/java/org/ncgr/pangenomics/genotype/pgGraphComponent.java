package org.ncgr.pangenomics.genotype;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

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

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;

/**
 * Extend mxGraphComponent to implement ActionListener for button and key presses
 */
class pgGraphComponent extends mxGraphComponent implements ActionListener, ListSelectionListener {
    PangenomicGraph graph;
    PGraphXAdapter pgxAdapter;
    
    // zoom buttons
    JButton zoomInButton;
    JButton zoomOutButton;

    // the JList of sample names and whatnot
    JList sampleList;
    String[] sampleNames;
    int currentIndex;
    Path highlightedPath;

    boolean decorateEdges;
    
    // starting scale
    double scale = 1.0;
    
    /**
     * Constructor takes a graph and JGraphXAdapter
     */
    pgGraphComponent(PangenomicGraph graph, PGraphXAdapter pgxAdapter, boolean decorateEdges) {
        super(pgxAdapter);
        this.pgxAdapter = pgxAdapter;
        this.graph = graph;
        this.decorateEdges = decorateEdges;

        // housekeeping
        setConnectable(false);
        getGraph().setAllowDanglingEdges(false);
        setToolTips(true);

        ////////////////
        // KEYSTROKES //
        ////////////////
        
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

        //////////////////////
        // BUTTONS AND SUCH //
        //////////////////////
        
        // add a column header with static info and navigation buttons
        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.LIGHT_GRAY);
        // graph label in bold
        JLabel graphLabel = new JLabel(graph.name);
        graphLabel.setFont(graphLabel.getFont().deriveFont(Font.BOLD));
        topPanel.add(graphLabel);
        // zoom buttons
        zoomOutButton = new JButton("-");
        zoomOutButton.setActionCommand("zoomOut");
        zoomOutButton.addActionListener(this);
        topPanel.add(zoomOutButton);
        zoomInButton = new JButton("+");
        zoomInButton.setActionCommand("zoomIn");
        zoomInButton.addActionListener(this);
        topPanel.add(zoomInButton);
        // path selector
        sampleNames = graph.getPathNames();
        String[] sampleLabels = new String[sampleNames.length];
        for (int i=0; i<sampleNames.length; i++) {
            Path p = graph.getPath(sampleNames[i]);
            sampleLabels[i] = sampleNames[i]+" ("+p.label+")";
        }
        sampleList = new JList<String>(sampleLabels);
        sampleList.setLayoutOrientation(JList.VERTICAL);
        // sampleList.setVisibleRowCount​(1);
        sampleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sampleList.addListSelectionListener​(this);
        JScrollPane scrollPane = new JScrollPane(sampleList);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(128, 18));
        topPanel.add(scrollPane);
 
        // graph info
        String infoLabelString = graph.getNodes().size()+" nodes "+graph.paths.size()+" paths";
        if (graph.getLabelCounts().get("case")!=null && graph.getLabelCounts().get("ctrl")!=null) {
            infoLabelString += " ("+graph.getLabelCounts().get("case")+" cases / "+graph.getLabelCounts().get("ctrl")+" controls)";
        }
        JLabel infoLabel = new JLabel(infoLabelString);
        topPanel.add(infoLabel);
        // put it on the top
        setColumnHeaderView(topPanel);
    }
    
    /**
     * Handle button (and key) actions
     */
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("zoomIn") || command.equals("zoomOut")) {
            if (command.equals("zoomIn")) {
                scale = scale*Math.sqrt(2.0);
            } else if (command.equals("zoomOut")) {
                scale = scale/Math.sqrt(2.0);
            }
            pgxAdapter.getView().setScale(scale);
            refresh();
        }
    }

    /**
     * Handle sample list selection changes
     */
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int firstIndex = e.getFirstIndex();
            int lastIndex = e.getLastIndex();
            if (firstIndex==currentIndex) {
                currentIndex = lastIndex;
            } else  {
                currentIndex = firstIndex;
            }
            sampleList.ensureIndexIsVisible(currentIndex);
            String sampleName = sampleNames[currentIndex];
            highlightedPath = graph.getPath(sampleName);
            pgxAdapter = new PGraphXAdapter(graph, decorateEdges, highlightedPath);
            setGraph(pgxAdapter);
            executeLayout();
        }
    }

    /**
     * Execute the layout.
     */
    public void executeLayout() {
        pgxAdapter.getView().setScale(scale);
        mxHierarchicalLayout layout = new mxHierarchicalLayout(pgxAdapter, SwingConstants.WEST);
        layout.setFineTuning(true);
        layout.execute(pgxAdapter.getDefaultParent());
    }
}
