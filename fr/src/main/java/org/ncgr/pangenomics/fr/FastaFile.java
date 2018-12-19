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

    List<Sequence> sequences;
    int[][] paths;
    String filename;
    Graph g;

    boolean verbose = false;

    public FastaFile(String filename, Graph g) {
        this.filename = filename;
        this.g = g;
        readFastaFile(filename, g);
    }
    
    // read a FASTA file into local instance objects
    public void readFastaFile(String filename, Graph g) {

        if (verbose) System.out.println("Reading FASTA file: "+filename);
        
        sequences = new ArrayList<Sequence>();

        Map<Integer,Long> seqStart = new TreeMap<Integer,Long>();
        List<List<Integer>> pathsAL = new ArrayList<List<Integer>>();
        TreeSet<Long> Nlocs = new TreeSet<Long>(); // has to be TreeSet to contain the ceiling() method
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
                    Sequence s = new Sequence();
                    s.label = desc.replace(',', ';');
                    s.length = seq.length();
                    s.startPos = seqStart.get(index);
                    sequences.add(s);
                    for (int k = 0; k < seq.length(); k++) {
                        if (seq.charAt(k) == 'N') {
                            Nlocs.add(s.startPos + k);
                        }
                    }

                    List<Integer> path = new ArrayList<>();
                    seqEnd = seqStart.get(index) + s.length - 1;
                    curStart = seqStart.get(index);
                    while (curStart > 0 && !g.startToNode.containsKey(curStart)) {
                        curStart--;
                    }
                    path.add(g.startToNode.get(curStart));
                    do {
                        curStart += g.length[g.startToNode.get(curStart)] - (g.K - 1);
                        if (g.startToNode.containsKey(curStart)) {
                            path.add(g.startToNode.get(curStart));
                        }
                    } while (g.startToNode.containsKey(curStart) && curStart
                             + g.length[g.startToNode.get(curStart)] - 1 < seqEnd);

                    pathsAL.add(path);
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
                Sequence s = new Sequence();
                s.label = desc.replace(',', ';');
                s.length = seq.length();
                s.startPos = seqStart.get(index);
                sequences.add(s);
                for (int k = 0; k < seq.length(); k++) {
                    if (seq.charAt(k) == 'N') {
                        Nlocs.add(s.startPos + k);
                    }
                }

                List<Integer> path = new ArrayList<>();
                seqEnd = seqStart.get(index) + s.length - 1;
                curStart = seqStart.get(index);
                while (curStart > 0 && !g.startToNode.containsKey(curStart)) {
                    curStart--;
                }
                path.add(g.startToNode.get(curStart));
                do {
                    curStart += g.length[g.startToNode.get(curStart)] - (g.K - 1);
                    if (g.startToNode.containsKey(curStart)) {
                        path.add(g.startToNode.get(curStart));
                    }
                } while (g.startToNode.containsKey(curStart) && curStart
                         + g.length[g.startToNode.get(curStart)] - 1 < seqEnd);

                pathsAL.add(path);
                seqStart.put(++index, seqEnd + 2);

            }
        } catch (IOException e) {
            if (verbose) System.out.println("Error when reading " + filename);
            e.printStackTrace();
        }

        if (verbose) System.out.println("number of paths: " + pathsAL.size());

        paths = new int[pathsAL.size()][];
        for (int i = 0; i < pathsAL.size(); i++) {
            List<Integer> path = pathsAL.get(i);
            paths[i] = new int[path.size()];
            for (int j = 0; j < path.size(); j++) {
                paths[i][j] = path.get(j);
            }
        }

        pathsAL.clear();
        pathsAL = null; // can be gc'ed

        if (verbose) System.out.println("finding node paths");
        g.findNodePaths(paths, Nlocs);

    }

    /**
     * Find a location in this FASTA
     */
    public long[] findLoc(int path, int start, int stop) {
        long[] startStop = new long[2];
        long curStart = sequences.get(path).startPos;

        while (curStart > 0 && !g.startToNode.containsKey(curStart)) {
            curStart--;
        }

        int curIndex = 0;
        while (curIndex != start) {
            curStart += g.length[g.startToNode.get(curStart)] - (g.K - 1);
            curIndex++;
        }
        long offset = Math.max(0, sequences.get(path).startPos - curStart);
        startStop[0] = curStart - sequences.get(path).startPos + offset; // assume fasta seq indices start at 0
        while (curIndex != stop) {
            curStart += g.length[g.startToNode.get(curStart)] - (g.K - 1);
            curIndex++;
        }
        long seqLastPos = sequences.get(path).startPos + sequences.get(path).length - 1;
        startStop[1] = Math.min(seqLastPos, curStart + g.length[g.startToNode.get(curStart)] - 1)
            - sequences.get(path).startPos + 1; // last position is excluded in BED format
        return startStop;
    }

    // getters
    public List<Sequence> getSequences() {
        return sequences;
    }
    public int[][] getPaths() {
        return paths;
    }
    public String getFilename() {
        return filename;
    }

}
