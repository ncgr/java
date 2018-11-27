import vg.Vg;

import org.ncgr.pangenomics.fr.ClusterNode;
import org.ncgr.pangenomics.fr.FRFinder;
import org.ncgr.pangenomics.fr.PathSegment;

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

/**
 * A non-package class providing a main method for running the various apps.
 */
public class Main {

    static String[] colors = {"122,39,25", "92,227,60", "225,70,233", "100,198,222", "232,176,49", "50,39,85",
                              "67,101,33", "222,142,186", "92,119,227", "206,225,151", "227,44,118", "229,66,41",
                              "47,36,24", "225,167,130", "120,132,131", "104,232,178", "158,43,133", "228,228,42", "213,217,213",
                              "118,64,79", "88,155,219", "226,118,222", "146,197,53", "222,100,89", "224,117,41", "160,96,228",
                              "137,89,151", "126,209,119", "145,109,70", "91,176,164", "54,81,103", "164,174,137", "172,166,48",
                              "56,86,143", "210,184,226", "175,123,35", "129,161,88", "158,47,85", "87,231,225", "216,189,112", "49,111,75",
                              "89,137,168", "209,118,134", "33,63,44", "166,128,142", "53,137,55", "80,76,161", "170,124,221", "57,62,13",
                              "176,40,40", "94,179,129", "71,176,51", "223,62,170", "78,25,30", "148,69,172", "122,105,31", "56,33,53",
                              "112,150,40", "239,111,176", "96,55,25", "107,90,87", "164,74,28", "171,198,226", "152,131,176", "166,225,211",
                              "53,121,117", "220,58,86", "86,18,56", "225,197,171", "139,142,217", "216,151,223", "97,229,117", "225,155,85",
                              "31,48,58", "160,146,88", "185,71,129", "164,233,55", "234,171,187", "110,97,125", "177,169,175", "177,104,68",
                              "97,48,122", "237,139,128", "187,96,166", "225,90,127", "97,92,55", "124,35,99", "210,64,194", "154,88,84",
                              "100,63,100", "140,42,54", "105,132,99", "186,227,103", "224,222,81", "191,140,126", "200,230,182", "166,87,123",
                              "72,74,58", "212,222,124", "205,52,136"};
        
    static String[] svgcolors = {"aliceblue", "antiquewhite", "aqua", "aquamarine",
                                 "azure", "beige", "bisque", "black", "blanchedalmond", "blue",
                                 "blueviolet", "brown", "burlywood", "cadetblue", "chartreuse",
                                 "chocolate", "coral", "cornflowerblue", "cornsilk", "crimson",
                                 "cyan", "darkblue", "darkcyan", "darkgoldenrod", "darkgray",
                                 "darkgreen", "darkgrey", "darkkhaki", "darkmagenta", "darkolivegreen",
                                 "darkorange", "darkorchid", "darkred", "darksalmon", "darkseagreen",
                                 "darkslateblue", "darkslategray", "darkslategrey", "darkturquoise", "darkviolet",
                                 "deeppink", "deepskyblue", "dimgray", "dimgrey", "dodgerblue",
                                 "firebrick", "floralwhite", "forestgreen", "fuchsia", "gainsboro",
                                 "ghostwhite", "gold", "goldenrod", "gray", "grey",
                                 "green", "greenyellow", "honeydew", "hotpink", "indianred",
                                 "indigo", "ivory", "khaki", "lavender", "lavenderblush",
                                 "lawngreen", "lemonchiffon", "lightblue", "lightcoral", "lightcyan",
                                 "lightgoldenrodyellow", "lightgray", "lightgreen", "lightgrey", "lightpink",
                                 "lightsalmon", "lightseagreen", "lightskyblue", "lightslategray", "lightslategrey",
                                 "lightsteelblue", "lightyellow", "lime", "limegreen", "linen",
                                 "magenta", "maroon", "mediumaquamarine", "mediumblue", "mediumorchid",
                                 "mediumpurple", "mediumseagreen", "mediumslateblue", "mediumspringgreen", "mediumturquoise",
                                 "mediumvioletred", "midnightblue", "mintcream", "mistyrose", "moccasin",
                                 "navajowhite", "navy", "oldlace", "olive", "olivedrab",
                                 "orange", "orangered", "orchid", "palegoldenrod", "palegreen",
                                 "paleturquoise", "palevioletred", "papayawhip", "peachpuff", "peru",
                                 "pink", "plum", "powderblue", "purple", "red",
                                 "rosybrown", "royalblue", "saddlebrown", "salmon", "sandybrown",
                                 "seagreen", "seashell", "sienna", "silver", "skyblue",
                                 "slateblue", "slategray", "slategrey", "snow", "springgreen",
                                 "steelblue", "tan", "teal", "thistle", "tomato",
                                 "turquoise", "violet", "wheat", "white", "whitesmoke",
                                 "yellow", "yellowgreen"};

    public static void main(String[] args) {
        
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;
        
        if (args[0].equals("FRFinder")) {
    
            // FRFinder input
            Option dotO = new Option("d", "dot", true, "dot file");
            dotO.setRequired(true);
            options.addOption(dotO);
            Option faO = new Option("f", "fasta", true, "fasta file");
            faO.setRequired(true);
            options.addOption(faO);
            Option aO = new Option("a", "alpha", true, "alpha parameter");
            aO.setRequired(true);
            options.addOption(aO);
            Option kO = new Option("k", "kappa", true, "kappa parameter");
            kO.setRequired(true);
            options.addOption(kO);
            Option minSO = new Option("m", "minsup", true, "minsup parameter");
            minSO.setRequired(false);
            options.addOption(minSO);
            Option minZO = new Option("z", "minsize", true, "minsize parameter");
            minZO.setRequired(false);
            options.addOption(minZO);
            Option rcO = new Option("r", "rc", false, "rc flag");
            rcO.setRequired(false);
            options.addOption(rcO);
            try {
                cmd = parser.parse(options, args);
            } catch (ParseException e) {
                System.out.println(e.getMessage());
                formatter.printHelp("Main", options);
                System.exit(1);
                return;
            }
            // FRFinder values
            String dotFile = cmd.getOptionValue("dot");
            String fastaFile = cmd.getOptionValue("fasta");
            double alpha = Double.parseDouble(cmd.getOptionValue("alpha"));
            int kappa = Integer.parseInt(cmd.getOptionValue("kappa"));
            boolean useRC = cmd.hasOption("rc");
            FRFinder frf = new FRFinder(dotFile, fastaFile, alpha, kappa, useRC);
            if (cmd.hasOption("minsup")) {
                frf.setMinSup(Integer.parseInt(cmd.getOptionValue("minsup")));
            }
            if (cmd.hasOption("minsize")) {
                frf.setMinSize(Integer.parseInt(cmd.getOptionValue("minsize")));
            }
            System.out.println("Finding FRs...");
            frf.findFRs();

            // output results
            System.out.println("Outputting results...");

            ClusterNode top;
            ArrayList<ClusterNode> iFRs = new ArrayList<ClusterNode>();
            while ((top = frf.getIFRQ().poll()) != null) {
                if (top.getAvgLen() >= frf.getMinLen() && top.getSize() >= frf.getMinSize()) {
                    iFRs.add(top);
                }
            }

            try {
                String paramString = "-a" + frf.getAlpha() + "-kp" + frf.getKappa() + "-sup" + frf.getMinSup() + "-sz" + frf.getMinSize();
                if (frf.getUseRC()) {
                    paramString += "-rc";
                }
                String[] tmp = frf.getGraph().getDotFile().split("/");
                String dotName = tmp[tmp.length - 1];
                tmp = frf.getFastaFile().getFilename().split("/");
                String fastaName = tmp[tmp.length - 1];
                String filePrefix = dotName + "-" + fastaName;
                String rd = "FR-" + filePrefix + "/";
                File resultsDir = new File(rd);
                resultsDir.mkdir();
            
                HashMap<Integer, TreeSet<Integer>> nodeFRset = new HashMap<Integer,TreeSet<Integer>>();
                BufferedWriter frOut = new BufferedWriter(new FileWriter(rd + "FR" + paramString + ".frs.txt"));
                for (int fr = 0; fr < iFRs.size(); fr++) {
                    ClusterNode iFR = iFRs.get(fr);
                    String frName = "fr-" + fr;
                    TreeSet<Integer> clustNodes = iFR.getNodeSet();
                    frOut.write(frName);
                    for (Integer n : clustNodes) {
                        frOut.write("," + n);
                        if (!nodeFRset.containsKey(n)) {
                            nodeFRset.put(n, new TreeSet<Integer>());
                        }
                        nodeFRset.get(n).add(fr);
                    }
                    frOut.write("\n");
                }
                frOut.close();
            
                System.out.println("writing bed file");
                BufferedWriter bedOut = new BufferedWriter(new FileWriter(rd + "FR" + paramString + ".bed"));
                TreeMap<String,TreeMap<Integer,Integer>> seqFRcount = new TreeMap<String,TreeMap<Integer,Integer>>();
                TreeMap<String,TreeMap<Integer,LinkedList<String>>> seqIndxFRstr = new TreeMap<String,TreeMap<Integer,LinkedList<String>>>();
                int[] pathTotalSupLen = new int[frf.getFastaFile().getPaths().length];

                for (int fr = 0; fr < iFRs.size(); fr++) {
                    ClusterNode iFR = iFRs.get(fr);
                    if ((fr % 100) == 0) {
                        System.out.println("writing fr-" + fr);
                    }
                    List<PathSegment> supportingSegments = frf.computeSupport(iFR, true, false);
                    for (PathSegment ps : supportingSegments) {
                        String name = frf.getFastaFile().getSequences().get(ps.getPath()).getLabel();
                        if (!seqFRcount.containsKey(name)) {
                            seqFRcount.put(name, new TreeMap<Integer,Integer>());
                        }
                        if (!seqFRcount.get(name).containsKey(fr)) {
                            seqFRcount.get(name).put(fr, 0);
                        }
                        seqFRcount.get(name).put(fr, seqFRcount.get(name).get(fr) + 1);
                        long[] startStop = frf.getFastaFile().findLoc(ps.getPath(), ps.getStart(), ps.getStop());
                        int frLen = (int) (startStop[1] - startStop[0]); // last position is excluded                                     
                        pathTotalSupLen[ps.getPath()] += frLen;
                        bedOut.write(name // chrom
                                     + "\t" + startStop[0] // chromStart (starts with 0)
                                     + "\t" + startStop[1] // chromEnd
                                     + "\t" + "fr-" + fr// name
                                     + "\t" + Math.round(iFR.getFwdSup() + iFR.getRcSup()) // score
                                     + "\t+" // strand
                                     + "\t" + 0 // thickstart
                                     + "\t" + 0 // thickend
                                     + "\t" + colors[fr % colors.length] // itemRGB
                                     + "\t" + frLen // FR length
                                     + "\n");
                        if (!seqIndxFRstr.containsKey(name)) {
                            seqIndxFRstr.put(name, new TreeMap<Integer,LinkedList<String>>());
                        }
                        if (!seqIndxFRstr.get(name).containsKey(ps.getStart())) {
                            seqIndxFRstr.get(name).put(ps.getStart(), new LinkedList<String>());
                        }
                        if (!seqIndxFRstr.get(name).containsKey(ps.getStop())) {
                            seqIndxFRstr.get(name).put(ps.getStop(), new LinkedList<String>());
                        }
                        seqIndxFRstr.get(name).get(ps.getStart()).addFirst(" [fr-" + fr + ":" + startStop[0]);
                        seqIndxFRstr.get(name).get(ps.getStop()).addLast(" fr-" + fr + ":" + startStop[1] + "] ");
                    }
                }
                bedOut.close();

                System.out.println("writing dist file");
                BufferedWriter distOut = new BufferedWriter(new FileWriter(rd + "FR" + paramString + ".dist.txt"));
                distOut.write("FR,size,support,avg length\n");
                for (int fr = 0; fr < iFRs.size(); fr++) {
                    ClusterNode iFR = iFRs.get(fr);
                    distOut.write("fr-" + fr + "," + iFR.getSize() + "," + (iFR.getFwdSup() + iFR.getRcSup()) + "," + iFR.getAvgLen() + "\n");
                }
                distOut.close();

                if (frf.getUseRC()) {
                    System.out.println("writing rc file");
                    BufferedWriter rcOut = new BufferedWriter(new FileWriter(rd + "FR" + paramString + ".rc.txt"));
                    for (int i = 0; i < frf.getFastaFile().getPaths().length / 2; i++) {
                        if (pathTotalSupLen[i + frf.getFastaFile().getPaths().length / 2] > pathTotalSupLen[i]) {
                            rcOut.write(frf.getFastaFile().getSequences().get(i).getLabel() + "\n");
                        }
                    }
                    rcOut.close();
                }
                System.out.println("writing frpaths file");
                BufferedWriter frPathsOut = new BufferedWriter(new FileWriter(rd + "FR" + paramString + ".frpaths.txt"));
                for (int i = 0; i < frf.getFastaFile().getPaths().length; i++) {
                    String name = frf.getFastaFile().getSequences().get(i).getLabel();
                    if (seqIndxFRstr.containsKey(name)) {
                        frPathsOut.write(name + ",");
                        for (int pos : seqIndxFRstr.get(name).keySet()) {
                            LinkedList<String> ll = seqIndxFRstr.get(name).get(pos);
                            for (String s : ll) {
                                frPathsOut.write(s);
                            }
                        }
                        frPathsOut.write("\n");
                    }
                }
                frPathsOut.close();

                System.out.println("writing csfr file");
                BufferedWriter seqFROut = new BufferedWriter(new FileWriter(rd + "FR" + paramString + ".csfr.txt"));
                for (String seq : seqFRcount.keySet()) {
                    seqFROut.write(seq);
                    for (Integer F : seqFRcount.get(seq).keySet()) {
                        seqFROut.write("," + F + ":" + seqFRcount.get(seq).get(F));
                    }
                    seqFROut.write("\n");
                }
                seqFROut.close();

                System.out.println("done");
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(-1);
            }

        } else if (args[0].equals("VGLoader")) {

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
                formatter.printHelp("VgGraphLoader", options);
                System.exit(1);
                return;
            }
            
            // load parameters
            String vgFile = cmd.getOptionValue("vg");
            String jsonFile = cmd.getOptionValue("json");

            if (vgFile!=null) {
                
                try {
                    Vg.Graph graph = Vg.Graph.parseFrom(new FileInputStream(vgFile));
                    System.out.println(vgFile + ": Successfully read into a Vg.Graph object.");
                } catch (FileNotFoundException e) {
                    System.out.println(vgFile + ": File not found.");
                    System.exit(1);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
                System.out.println("graph loaded.");

            } else if (jsonFile!=null) {

                try {
                    FileInputStream input = new FileInputStream(jsonFile);
                    Reader reader = new InputStreamReader(input);
                    try {
                        Vg.Graph.Builder graph = Vg.Graph.newBuilder();
                        JsonFormat.parser().merge(reader, graph);
                        List<Vg.Node> nodes = graph.getNodeList();
                        List<Vg.Path> paths = graph.getPathList();
                        List<Vg.Edge> edges = graph.getEdgeList();
                        System.out.println("graph loaded: "+nodes.size()+" nodes, "+paths.size()+" paths, "+edges.size()+" edges.");
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
                    } finally {
                        reader.close();
                        input.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
                
            }

        }

    }

}
