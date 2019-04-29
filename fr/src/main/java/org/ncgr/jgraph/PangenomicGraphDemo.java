package org.ncgr.jgraph;

import org.ncgr.pangenomics.Path;
import org.ncgr.pangenomics.Node;

import java.awt.*;
import java.io.*;
import javax.swing.*;

import com.mxgraph.layout.*;
import com.mxgraph.swing.*;

import org.apache.commons.cli.*;

import org.jgrapht.*;
import org.jgrapht.ext.*;
import org.jgrapht.graph.*;

/**
 * A demo applet that displays a PangenomicGraph.
 */
public class PangenomicGraphDemo extends JApplet {

    // defaults
    public static int BOTH_GENOTYPES = -1;
    public static boolean VERBOSE = false;
    private static final long serialVersionUID = 2202072534703043194L;
    private static final Dimension DEFAULT_SIZE = new Dimension(800,800);

    // output verbosity
    boolean verbose = VERBOSE;

    // genotype preference (default: load all genotypes)
    public int genotype = BOTH_GENOTYPES;

    private JGraphXAdapter<Node,Edge> jgxAdapter;
    private PangenomicGraph pg;

    // setter
    public void setGraph(PangenomicGraph pg) {
        this.pg = pg;
    }

    /**
     * An alternative starting point for this demo, to also allow running this applet as an
     * application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) throws IOException, FileNotFoundException {

        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        Option gfaOption = new Option("g", "gfa", true, "vg GFA file");
        gfaOption.setRequired(false);
        options.addOption(gfaOption);
        //
        Option genotypeOption = new Option("gt", "genotype", true, "which genotype to include (0,1) from the GFA file; "+BOTH_GENOTYPES+" to include both ("+BOTH_GENOTYPES+")");
        genotypeOption.setRequired(false);
        options.addOption(genotypeOption);
        //
        Option labelsOption = new Option("p", "pathlabels", true, "tab-delimited file containing one pathname<tab>label per line");
        labelsOption.setRequired(false);
        options.addOption(labelsOption);
        //
        Option outputprefixOption = new Option("o", "outputprefix", true, "output file prefix (stdout)");
        outputprefixOption.setRequired(false);
        options.addOption(outputprefixOption);
        //
        Option verboseOption = new Option("v", "verbose", false, "verbose output ("+VERBOSE+")");
        verboseOption.setRequired(false);
        options.addOption(verboseOption);

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp("PangenomicGraph", options);
            System.exit(1);
            return;
        }

        // none required, so spit out help if nothing supplied
        if (cmd.getOptions().length==0) {
            formatter.printHelp("PangenomicGraph", options);
            System.exit(1);
            return;
        }
        
        // parameter validation
        if (!cmd.hasOption("gfa")) {
            System.err.println("You must specify a vg GFA file (--gfa)");
            System.exit(1);
            return;
        }
        
        // files
        String gfaFile = cmd.getOptionValue("gfa");
        String pathLabelsFile = cmd.getOptionValue("pathlabels");

        // create a PangenomicGraph from a GFA file
        PangenomicGraphDemo pgd = new PangenomicGraphDemo();
        PangenomicGraph pg = new PangenomicGraph();
        pgd.setGraph(pg);
        if (cmd.hasOption("verbose")) pg.setVerbose();
        if (cmd.hasOption("genotype")) pg.genotype = Integer.parseInt(cmd.getOptionValue("genotype"));
        if (gfaFile!=null) {
            // pg.readVgGfaFile(gfaFile);
        } else {
            System.err.println("ERROR: no GFA provided.");
            System.exit(1);
        }

        // if a labels file is given, add them to the paths
        if (pathLabelsFile!=null) {
            pg.readPathLabels(pathLabelsFile);
        }
        
        pgd.init();

        JFrame frame = new JFrame();
        frame.getContentPane().add(pgd);
        frame.setTitle("JGraphT Adapter to JGraphX Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void init() {
        // create a visualization using JGraph, via an adapter
        jgxAdapter = new JGraphXAdapter<>(pg);

        setPreferredSize(DEFAULT_SIZE);
        mxGraphComponent component = new mxGraphComponent(jgxAdapter);
        component.setConnectable(false);
        component.getGraph().setAllowDanglingEdges(false);
        getContentPane().add(component);
        resize(DEFAULT_SIZE);

        // positioning via jgraphx layouts

        mxFastOrganicLayout layout = new mxFastOrganicLayout(jgxAdapter);
        layout.setUseBoundingBox(true);
        
        // mxCompactTreeLayout layout = new mxCompactTreeLayout(jgxAdapter);
        // mxEdgeLabelLayout layout = new mxEdgeLabelLayout(jgxAdapter);
        // mxOrganicLayout layout = new mxOrganicLayout(jgxAdapter);
        // mxParallelEdgeLayout layout = new mxParallelEdgeLayout(jgxAdapter);
        // mxPartitionLayout layout = new mxPartitionLayout(jgxAdapter);
        // mxStackLayout layout = new mxStackLayout(jgxAdapter);
        
        // mxCircleLayout layout = new mxCircleLayout(jgxAdapter);
        // // center the circle
        // int radius = 100;
        // layout.setX0((DEFAULT_SIZE.width / 2.0) - radius);
        // layout.setY0((DEFAULT_SIZE.height / 2.0) - radius);
        // layout.setRadius(radius);
        // layout.setMoveCircle(true);

        layout.execute(jgxAdapter.getDefaultParent());
    }
}
