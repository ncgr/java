package org.ncgr.pangenomics.vg;

import vg.Vg;

import java.io.Reader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.google.protobuf.util.JsonFormat;

public class VGLoader {

    String vgFile;
    String jsonFile;
    Vg.Graph graph;

    /**
     * Command line version.
     */
    public static void main(String[] args) {
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        // VGLoader input
        Option vgFileOption = new Option("v", "vg", true, "vg file");
        vgFileOption.setRequired(false);
        options.addOption(vgFileOption);
        Option jsonFileOption = new Option("j", "json", true, "JSON file");
        jsonFileOption.setRequired(false);
        options.addOption(jsonFileOption);
        
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("VGLoader", options);
            System.exit(1);
            return;
        }

        VGLoader vgl = new VGLoader();

        if (cmd.hasOption("v")) {
            String vgFilename = cmd.getOptionValue("v");
            try {
                vgl.loadVGFile(vgFilename);
                vgl.print();
            } catch (FileNotFoundException e) {
                System.out.println("File not found: "+vgFilename);
                System.exit(1);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        if (cmd.hasOption("j")) {
            String jsonFilename = cmd.getOptionValue("j");
            try {
                vgl.loadJSONFile(jsonFilename);
                vgl.print();
            } catch (FileNotFoundException e) {
                System.out.println("File not found: "+jsonFilename);
                System.exit(1);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    /**
     * Load the graph from a VG file.
     */
    public void loadVGFile(String vgFilename) throws FileNotFoundException, IOException {
        graph = Vg.Graph.parseFrom(new FileInputStream(vgFilename));
    }
            
    /**
     * Load the graph from a JSON file.
     */
    public void loadJSONFile(String jsonFilename) throws FileNotFoundException, IOException {
        FileInputStream input = null;
        Reader reader = null;
        try {
            input = new FileInputStream(jsonFilename);
            reader = new InputStreamReader(input);
            Vg.Graph.Builder graphBuilder = Vg.Graph.newBuilder();
            JsonFormat.parser().merge(reader, graphBuilder);
            graph = graphBuilder.build();
        } finally {
            if (reader!=null) reader.close();
            if (input!=null) input.close();
        }
    }

    /**
     * Output the nodes, edges and paths to stdout.
     */
    public void print() {
        List<Vg.Node> nodes = graph.getNodeList();
        List<Vg.Path> paths = graph.getPathList();
        List<Vg.Edge> edges = graph.getEdgeList();
        System.out.println("Vg.Graph: "+nodes.size()+" nodes, "+paths.size()+" paths, "+edges.size()+" edges.");
        for (Vg.Node node : nodes) {
            System.out.println(">Node "+node.getId());
            System.out.println(node.getSequence());
        }
        for (Vg.Path path : paths) {
            System.out.println(path.toString());
        }
        for (Vg.Edge edge : edges) {
            System.out.println("Edge: from "+edge.getFrom()+" to "+edge.getTo()+" overlap "+edge.getOverlap());
        }
    }

}
