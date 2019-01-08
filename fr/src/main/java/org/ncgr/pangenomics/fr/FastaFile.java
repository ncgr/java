package org.ncgr.pangenomics.fr;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.TreeSet;

/**
 * Storage of data retrieved from a FASTA file.
 *
 * @author bmumsey
 * @author Sam Hokin
 */
public class FastaFile {

    private boolean verbose = false;

    private List<Sequence> sequences;
    private int[][] paths;
    private TreeSet<Long> Nlocs;
    private Graph g;
    private String filename;

    public FastaFile(Graph g) {
        this.g = g;
    }
    
    // read a FASTA file into local instance objects
    public void readFastaFile(String filename) {
        if (verbose) System.out.println("Reading FASTA file: "+filename);
        this.filename = filename;
        
        sequences = new ArrayList<Sequence>();
        Nlocs = new TreeSet<Long>(); // uses TreeSet.ceiling()

        Map<Integer,Long> seqStart = new TreeMap<Integer,Long>();
        List<List<Integer>> pathsList = new ArrayList<List<Integer>>();
        long curStart = 1;
        int index = 0;
        seqStart.put(0, (long) 1);
        long seqEnd;
        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            StringBuffer buffer = new StringBuffer();

            String line = in.readLine();
            if (line == null) {
                throw new IOException(filename + " is an empty file");
            }

            String desc = "", seq = "";
            if (line.charAt(0) != '>') {
                throw new IOException("First line of " + filename + " should start with '>'");
            } else {
                desc = line.substring(1);
            }
            while ((line = in.readLine()) != null) {
                if (line.length() > 0 && line.charAt(0) == '>') {
                    seq = buffer.toString();
                    // desc, seq are the next fasta sequence here
                    Sequence s = new Sequence(desc.replace(',', ';'), seq.length(), seqStart.get(index));
                    sequences.add(s);
                    for (int k = 0; k < seq.length(); k++) {
                        if (seq.charAt(k) == 'N') {
                            Nlocs.add(s.getStartPos() + k);
                        }
                    }

                    List<Integer> path = new ArrayList<>();
                    seqEnd = seqStart.get(index) + s.getLength() - 1;
                    curStart = seqStart.get(index);
                    while (curStart > 0 && !g.getStartToNode().containsKey(curStart)) {
                        curStart--;
                    }
                    path.add(g.getStartToNode().get(curStart));
                    do {
                        curStart += g.getLength()[g.getStartToNode().get(curStart)] - (g.getK() - 1);
                        if (g.getStartToNode().containsKey(curStart)) {
                            path.add(g.getStartToNode().get(curStart));
                        }
                    } while (g.getStartToNode().containsKey(curStart) && curStart
                             + g.getLength()[g.getStartToNode().get(curStart)] - 1 < seqEnd);

                    pathsList.add(path);
                    seqStart.put(++index, seqEnd + 2);

                    buffer = new StringBuffer();
                    desc = line.substring(1);
                } else {
                    buffer.append(line.trim().toUpperCase());
                }
            }
            if (buffer.length() != 0) {
                seq = buffer.toString();
                // desc, seq are the next fasta sequence here
                Sequence s = new Sequence(desc.replace(',', ';'), seq.length(), seqStart.get(index));
                sequences.add(s);
                for (int k = 0; k < seq.length(); k++) {
                    if (seq.charAt(k) == 'N') {
                        Nlocs.add(s.getStartPos() + k);
                    }
                }

                List<Integer> path = new ArrayList<>();
                seqEnd = seqStart.get(index) + s.getLength() - 1;
                curStart = seqStart.get(index);
                while (curStart > 0 && !g.getStartToNode().containsKey(curStart)) {
                    curStart--;
                }
                path.add(g.getStartToNode().get(curStart));
                do {
                    curStart += g.getLength()[g.getStartToNode().get(curStart)] - (g.getK() - 1);
                    if (g.getStartToNode().containsKey(curStart)) {
                        path.add(g.getStartToNode().get(curStart));
                    }
                } while (g.getStartToNode().containsKey(curStart) && curStart
                         + g.getLength()[g.getStartToNode().get(curStart)] - 1 < seqEnd);

                pathsList.add(path);
                seqStart.put(++index, seqEnd + 2);

            }
        } catch (IOException e) {
            if (verbose) System.out.println("Error when reading " + filename);
            e.printStackTrace();
        }

        if (verbose) System.out.println("number of paths: " + pathsList.size());

        paths = new int[pathsList.size()][];
        for (int i=0; i<pathsList.size(); i++) {
            List<Integer> path = pathsList.get(i);
            paths[i] = new int[path.size()];
            for (int j = 0; j < path.size(); j++) {
                paths[i][j] = path.get(j);
            }
        }
    }

    // getters and setters
    public List<Sequence> getSequences() {
        return sequences;
    }
    public int[][] getPaths() {
        return paths;
    }
    public String getFilename() {
        return filename;
    }
    public TreeSet<Long> getNlocs() {
        return Nlocs;
    }
    
    public void setVerbose() {
        verbose = true;
    }

}
