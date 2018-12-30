package org.ncgr.pangenomics.fr;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Set;
import java.util.TreeSet;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * @author bmumey
 * @author Sam Hokin
 */
public class FRFinder {

    private boolean verbose = false;

    Graph g;
    FastaFile f;
    
    List<ClusterEdge> edgeL;
    Map<Integer,ClusterNode> nodeCluster;

    PriorityBlockingQueue<ClusterNode> iFRQ;

    int numClusterNodes = 0;

    // required parameters, set in constructor
    double alpha = 0.7;    // penetrance: the fraction of a supporting strain's sequence that actually supports the FR; alternatively, `1-alpha` is the fraction of inserted sequence
    int kappa = 0;         // maximum insertion: the maximum insertion length (measured in bp) that any supporting path may have
    boolean useRC = false; // indicates if the sequence (e.g. FASTA file) had its reverse complement appended

    // optional parameters, set with set methods
    int minSup = 1;        // minimum support: minimum number of genome paths in order for a region to be considered frequent
    int minSize = 1;       // minimum size: minimum number of de Bruijn nodes that an FR must contain to be considered frequent

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
    
    /**
     * Construct with a given Graph, FastaFile and parameters
     */
    public FRFinder(Graph g, FastaFile f, double alpha, int kappa, boolean useRC) {
        // set the instance vars
        this.g = g;
        this.f = f;
        this.alpha = alpha;
        this.kappa = kappa;
        this.useRC = useRC;
    }

    /**
     * Compute the support for each frequented region.
     */
    public List<PathSegment> computeSupport(ClusterNode clust, boolean createPSList, boolean findAvgLen) {
        if (clust.pathLocs == null) {
            clust.findPathLocs();
        }
        List<PathSegment> segList = new ArrayList<PathSegment>();
        int fSup = 0;
        int rSup = 0;
        int supLen = 0;
        for (Integer P : clust.pathLocs.keySet()) {
            int[] locs = clust.pathLocs.get(P);
            int start = 0;
            while (start < locs.length) {
                int last = start;
                while (last + 1 < locs.length
                       && ((locs[last + 1] == locs[last] + 1)
                           || (kappa > 0 && g.findGap(f.paths[P], locs[last], locs[last + 1]) <= kappa))) {
                    last++;
                }
                if (last - start + 1 >= alpha * clust.size) {
                    if (!useRC || 2*P < f.paths.length) {
                        fSup++;
                    }
                    if (useRC && 2*P >= f.paths.length) {
                        rSup++;
                    }
                    if (createPSList) {
                        segList.add(new PathSegment(P, locs[start], locs[last]));
                    }
                    if (findAvgLen) {
                        long[] startStop = f.findLoc(P, locs[start], locs[last]);
                        int len = (int) (startStop[1] - startStop[0]); // last pos is exclusive
                        supLen += len;
                    }
                }
                start = last + 1;
            }
        }
        clust.fwdSup = fSup;
        clust.rcSup = rSup;
        if (findAvgLen && clust.fwdSup + clust.rcSup > 0) {
            clust.avgLen = supLen / (clust.fwdSup + clust.rcSup);
        }
        if (createPSList) {
            return segList;
        }
        return null;
    }

    /**
     * Find the frequented regions.
     */
    public void findFRs() {
        if (verbose) System.out.println("Creating node clusters...");
        nodeCluster = new ConcurrentHashMap<Integer,ClusterNode>(g.getNumNodes());

        // create initial node clusters
        f.nodePaths.keySet().parallelStream().forEach((N) -> {
                if (!f.nodePaths.get(N).isEmpty()
                    && (!useRC || 2*f.nodePaths.get(N).first() < f.paths.length)) { // only start with nodes from non-rc'ed paths
                    ClusterNode nodeClst = new ClusterNode();
                    nodeClst.parent = nodeClst.left = nodeClst.right = null;
                    nodeClst.node = N;
                    nodeClst.size = 1;
                    nodeClst.findPathLocs();
                    nodeClst.neighbors = new ConcurrentHashMap<ClusterNode,ClusterEdge>();
                    nodeCluster.put(N, nodeClst);
                }
            });
        Map<Integer,Map<Integer,List<Integer>>> nodePathLocs = new TreeMap<Integer,Map<Integer,List<Integer>>>();
        for (int p = 0; p < f.paths.length; p++) {
            for (int i = 0; i < f.paths[p].length; i++) {
                int n = f.paths[p][i];
                if (!nodePathLocs.containsKey(n)) {
                    nodePathLocs.put(n, new TreeMap<Integer,List<Integer>>());
                }
                if (!nodePathLocs.get(n).containsKey(p)) {
                    nodePathLocs.get(n).put(p, new ArrayList<Integer>());
                }
                nodePathLocs.get(n).get(p).add(i);
            }
        }
        for (Integer node : nodePathLocs.keySet()) {
            for (Integer P : nodePathLocs.get(node).keySet()) {
                List<Integer> al = nodePathLocs.get(node).get(P);
                int[] ar = new int[al.size()];
                int i = 0;
                for (Integer x : al) {
                    ar[i++] = x;
                }
                if (nodeCluster.containsKey(node)) {
                    nodeCluster.get(node).pathLocs.put(P, ar);
                }
            }
        }
        nodePathLocs.clear();

        if (verbose) System.out.println("Computing node support...");
        for (ClusterNode c : nodeCluster.values()) {
            computeSupport(c, false, false);
        }
        // create initial edges
        edgeL = new ArrayList<ClusterEdge>();
        Set<ClusterNode> checkNodes = ConcurrentHashMap.newKeySet();
        for (Integer N : nodeCluster.keySet()) {
            for (int i = 0; i < g.getNeighbor()[N].length; i++) {
                if (nodeCluster.containsKey(g.getNeighbor()[N][i])) {
                    ClusterNode u = nodeCluster.get(N);
                    ClusterNode v = nodeCluster.get(g.getNeighbor()[N][i]);
                    if (!u.neighbors.containsKey(v)) {
                        ClusterEdge e = new ClusterEdge(u, v, -1, 0);
                        edgeL.add(e);
                        u.neighbors.put(v, e);
                        v.neighbors.put(u, e);
                    }
                }
            }
        }
        List<ClusterEdge> edgeM = new ArrayList<ClusterEdge>();

        do {
            if (verbose) System.out.println("# of edges: " + edgeL.size());
            edgeL.parallelStream().forEach((E) -> {
                    if (E.fwdSup < 0) {
                        ClusterNode tmpClst = new ClusterNode();
                        tmpClst.left = E.u;
                        tmpClst.right = E.v;
                        tmpClst.parent = null;
                        tmpClst.size = E.u.size + E.v.size;
                        computeSupport(tmpClst, false, false);
                        tmpClst.pathLocs.clear();
                        E.fwdSup = tmpClst.fwdSup;
                        E.rcSup = tmpClst.rcSup;
                        checkNodes.add(E.u);
                        checkNodes.add(E.v);
                    }
                });
            checkNodes.parallelStream().forEach((u) -> {
                    u.bestNsup = -1;
                    for (ClusterNode v : u.neighbors.keySet()) {
                        ClusterEdge E = u.neighbors.get(v);
                        if (E.sup() > u.bestNsup) {
                            u.bestNsup = E.sup();
                        }
                    }
                });
            checkNodes.clear();
            edgeM.clear();
            for (ClusterEdge E : edgeL) {
                if (E.sup() > 0 && E.sup() == E.u.bestNsup && E.sup() == E.v.bestNsup) {
                    edgeM.add(E);
                    E.u.bestNsup = -1;
                    E.v.bestNsup = -1;
                }
            }
            if (verbose) System.out.println("matching size: " + edgeM.size());
            edgeM.parallelStream().forEach((E) -> {
                    ClusterNode newRoot = new ClusterNode();
                    newRoot.node = -(numClusterNodes++);
                    newRoot.left = E.u;
                    newRoot.right = E.v;
                    newRoot.parent = null;
                    newRoot.size = newRoot.left.size + newRoot.right.size;
                    newRoot.left.parent = newRoot;
                    newRoot.right.parent = newRoot;
                    newRoot.fwdSup = E.fwdSup;
                    newRoot.rcSup = E.rcSup;
                    newRoot.neighbors = new ConcurrentHashMap<ClusterNode, ClusterEdge>();
                    newRoot.findPathLocs();
                });
            for (ClusterEdge E : edgeM) {
                ClusterNode newClst = E.u.parent;
                for (ClusterNode n : newClst.left.neighbors.keySet()) {
                    if (n != newClst.right) {
                        ClusterEdge e = newClst.left.neighbors.get(n);
                        e.u = newClst;
                        e.fwdSup = -1;
                        e.v = n;
                        newClst.neighbors.put(n, e);
                    }
                }
                for (ClusterNode n : newClst.right.neighbors.keySet()) {
                    if (n != newClst.left) {
                        ClusterEdge e = newClst.right.neighbors.get(n);
                        if (newClst.neighbors.containsKey(n)) {
                            edgeL.remove(e);
                        } else {
                            e.u = newClst;
                            e.fwdSup = -1;
                            e.v = n;
                            newClst.neighbors.put(n, e);
                        }
                    }
                }
                for (ClusterNode n : newClst.neighbors.keySet()) {
                    ClusterEdge e = newClst.neighbors.get(n);
                    n.neighbors.put(newClst, e);
                    n.neighbors.remove(newClst.left);
                    n.neighbors.remove(newClst.right);
                }
                newClst.left.neighbors.clear();
                newClst.right.neighbors.clear();
                edgeL.remove(E);
            }
        } while (!edgeM.isEmpty());

        Set<ClusterNode> roots = ConcurrentHashMap.newKeySet();
        nodeCluster.values().parallelStream().forEach((leaf) -> {
                roots.add(leaf.findRoot());
            });

        iFRQ = new PriorityBlockingQueue<ClusterNode>();

        if (verbose) System.out.println("number of root FRs: " + roots.size());
        roots.parallelStream().forEach((root) -> {
		System.out.println("root.node="+root.getNode());
                reportIFRs(root, 0);
            });

        if (verbose) System.out.println("number of iFRs: " + iFRQ.size());
    }

    void reportIFRs(ClusterNode clust, int parentSup) {
        if ((clust.fwdSup + clust.rcSup) > parentSup
            && (clust.fwdSup + clust.rcSup) >= minSup
            && clust.fwdSup >= clust.rcSup) {
            computeSupport(clust, false, true);
            iFRQ.add(clust);
        }
        if (clust.left != null) {
            reportIFRs(clust.left, Math.max(parentSup, clust.fwdSup + clust.rcSup));
        }
        if (clust.right != null) {
            reportIFRs(clust.right, Math.max(parentSup, clust.fwdSup + clust.rcSup));
        }
    }

    // getters for required parameters
    public double getAlpha() {
        return alpha;
    }
    public int getKappa() {
        return kappa;
    }
    public boolean getUseRC() {
        return useRC;
    }

    // setters/getters for optional parameters
    public void setMinSup(int minSup) {
        this.minSup = minSup;
    }
    public void setMinSize(int minSize) {
        this.minSize = minSize;
    }
    public int getMinSup() {
        return minSup;
    }
    public int getMinSize() {
        return minSize;
    }

    // getters for instance objects
    public Graph getGraph() {
        return g;
    }
    public FastaFile getFastaFile() {
        return f;
    }
    public PriorityBlockingQueue<ClusterNode> getIFRQ() {
        return iFRQ;
    }

    /**
     * Command-line utility
     */
    public static void main(String[] args) throws IOException {
                
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        if (args.length==0) {
            System.out.println("Usage:");
            System.out.println("FRFinder [options]");
            System.exit(1);
        }
    
        // FRFinder options
        Option dotO = new Option("d", "dot", true, "dot file");
        dotO.setRequired(true);
        options.addOption(dotO);
        //
        Option faO = new Option("f", "fasta", true, "fasta file");
        faO.setRequired(true);
        options.addOption(faO);
        //
        Option aO = new Option("a", "alpha", true, "alpha parameter");
        aO.setRequired(true);
        options.addOption(aO);
        //
        Option kO = new Option("k", "kappa", true, "kappa parameter");
        kO.setRequired(true);
        options.addOption(kO);
        //
        Option minSO = new Option("m", "minsup", true, "minsup parameter");
        minSO.setRequired(false);
        options.addOption(minSO);
        //
        Option minZO = new Option("z", "minsize", true, "minsize parameter");
        minZO.setRequired(false);
        options.addOption(minZO);
        //
        Option rcO = new Option("r", "rc", false, "rc flag");
        rcO.setRequired(false);
        options.addOption(rcO);
        //
        Option oneBO = new Option("1", "onebased", false, "start FR IDs at 1 rather than 0");
        oneBO.setRequired(false);
        options.addOption(oneBO);
        //
        Option vO = new Option("v", "verbose", false, "verbose output");
        vO.setRequired(false);
        options.addOption(vO);

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("FRFinder", options);
            System.exit(1);
            return;
        }
        
        // FRFinder values
        String dotFile = cmd.getOptionValue("dot");
        String fastaFile = cmd.getOptionValue("fasta");
        double alpha = Double.parseDouble(cmd.getOptionValue("alpha"));
        int kappa = Integer.parseInt(cmd.getOptionValue("kappa"));
        boolean useRC = cmd.hasOption("rc");
        boolean oneBased = cmd.hasOption("onebased");
        
        boolean verbose = cmd.hasOption("v");

        // create a Graph from the dot file
        Graph g = new Graph();
        if (verbose) g.setVerbose();
        g.readSplitMEMDotFile(dotFile);
        
        // create a FastaFile from the fasta file and the Graph
        FastaFile f = new FastaFile(fastaFile, g);
        
        // create the FRFinder and find FRs
        FRFinder frf = new FRFinder(g, f, alpha, kappa, useRC);
        if (verbose) frf.setVerbose();
        if (cmd.hasOption("minsup")) {
            frf.setMinSup(Integer.parseInt(cmd.getOptionValue("minsup")));
        }
        if (cmd.hasOption("minsize")) {
            frf.setMinSize(Integer.parseInt(cmd.getOptionValue("minsize")));
        }

        
        // find the FRs
        frf.findFRs();

        // output results
        if (verbose) System.out.println("Outputting results...");

        ClusterNode top;
        ArrayList<ClusterNode> iFRs = new ArrayList<ClusterNode>();
        while ((top = frf.getIFRQ().poll()) != null) {
            if (top.getAvgLen() >= g.getK() && top.getSize() >= frf.getMinSize()) {
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
            
            if (verbose) System.out.println("Writing bed file.");
            BufferedWriter bedOut = new BufferedWriter(new FileWriter(rd + "FR" + paramString + ".bed"));
            TreeMap<String,TreeMap<Integer,Integer>> seqFRcount = new TreeMap<String,TreeMap<Integer,Integer>>();
            TreeMap<String,TreeMap<Integer,LinkedList<String>>> seqIndxFRstr = new TreeMap<String,TreeMap<Integer,LinkedList<String>>>();
            int[] pathTotalSupLen = new int[frf.getFastaFile().getPaths().length];

            for (int fr = 0; fr < iFRs.size(); fr++) {
                ClusterNode iFR = iFRs.get(fr);
                if ((fr % 100) == 0) {
                    if (verbose) System.out.println("Writing fr-" + fr);
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

            if (verbose) System.out.println("Writing dist file.");
            BufferedWriter distOut = new BufferedWriter(new FileWriter(rd + "FR" + paramString + ".dist.txt"));
            distOut.write("FR,size,support,avg length\n");
            for (int fr = 0; fr < iFRs.size(); fr++) {
                ClusterNode iFR = iFRs.get(fr);
                distOut.write("fr-" + fr + "," + iFR.getSize() + "," + (iFR.getFwdSup() + iFR.getRcSup()) + "," + iFR.getAvgLen() + "\n");
            }
            distOut.close();

            if (frf.getUseRC()) {
                if (verbose) System.out.println("Writing rc file.");
                BufferedWriter rcOut = new BufferedWriter(new FileWriter(rd + "FR" + paramString + ".rc.txt"));
                for (int i = 0; i < frf.getFastaFile().getPaths().length / 2; i++) {
                    if (pathTotalSupLen[i + frf.getFastaFile().getPaths().length / 2] > pathTotalSupLen[i]) {
                        rcOut.write(frf.getFastaFile().getSequences().get(i).getLabel() + "\n");
                    }
                }
                rcOut.close();
            }
            if (verbose) System.out.println("Writing frpaths file.");
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

            if (verbose) System.out.println("Writing csfr file.");
            BufferedWriter seqFROut = new BufferedWriter(new FileWriter(rd + "FR" + paramString + ".csfr.txt"));
            for (String seq : seqFRcount.keySet()) {
                seqFROut.write(seq);
                for (Integer F : seqFRcount.get(seq).keySet()) {
                    if (oneBased) {
                        // FR IDs start with 1 for apps like libsvm
                        seqFROut.write("," + (F+1) + ":" + seqFRcount.get(seq).get(F));
                    } else {
                        // FR IDs start with 0
                        seqFROut.write("," + F + ":" + seqFRcount.get(seq).get(F));
                    }
                }
                seqFROut.write("\n");
            }
            seqFROut.close();

            if (verbose) System.out.println("Done!");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        }

    }

    /**
     * Toggle on verbosity.
     */
    public void setVerbose() {
        verbose = true;
    }

    // String[] svgcolors = {"aliceblue", "antiquewhite", "aqua", "aquamarine",
    //                       "azure", "beige", "bisque", "black", "blanchedalmond", "blue",
    //                       "blueviolet", "brown", "burlywood", "cadetblue", "chartreuse",
    //                       "chocolate", "coral", "cornflowerblue", "cornsilk", "crimson",
    //                       "cyan", "darkblue", "darkcyan", "darkgoldenrod", "darkgray",
    //                       "darkgreen", "darkgrey", "darkkhaki", "darkmagenta", "darkolivegreen",
    //                       "darkorange", "darkorchid", "darkred", "darksalmon", "darkseagreen",
    //                       "darkslateblue", "darkslategray", "darkslategrey", "darkturquoise", "darkviolet",
    //                       "deeppink", "deepskyblue", "dimgray", "dimgrey", "dodgerblue",
    //                       "firebrick", "floralwhite", "forestgreen", "fuchsia", "gainsboro",
    //                       "ghostwhite", "gold", "goldenrod", "gray", "grey",
    //                       "green", "greenyellow", "honeydew", "hotpink", "indianred",
    //                       "indigo", "ivory", "khaki", "lavender", "lavenderblush",
    //                       "lawngreen", "lemonchiffon", "lightblue", "lightcoral", "lightcyan",
    //                       "lightgoldenrodyellow", "lightgray", "lightgreen", "lightgrey", "lightpink",
    //                       "lightsalmon", "lightseagreen", "lightskyblue", "lightslategray", "lightslategrey",
    //                       "lightsteelblue", "lightyellow", "lime", "limegreen", "linen",
    //                       "magenta", "maroon", "mediumaquamarine", "mediumblue", "mediumorchid",
    //                       "mediumpurple", "mediumseagreen", "mediumslateblue", "mediumspringgreen", "mediumturquoise",
    //                       "mediumvioletred", "midnightblue", "mintcream", "mistyrose", "moccasin",
    //                       "navajowhite", "navy", "oldlace", "olive", "olivedrab",
    //                       "orange", "orangered", "orchid", "palegoldenrod", "palegreen",
    //                       "paleturquoise", "palevioletred", "papayawhip", "peachpuff", "peru",
    //                       "pink", "plum", "powderblue", "purple", "red",
    //                       "rosybrown", "royalblue", "saddlebrown", "salmon", "sandybrown",
    //                       "seagreen", "seashell", "sienna", "silver", "skyblue",
    //                       "slateblue", "slategray", "slategrey", "snow", "springgreen",
    //                       "steelblue", "tan", "teal", "thistle", "tomato",
    //                       "turquoise", "violet", "wheat", "white", "whitesmoke",
    //                       "yellow", "yellowgreen"};

}
