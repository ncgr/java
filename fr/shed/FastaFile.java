package org.ncgr.pangenomics.fr;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.List;
import java.util.LinkedList;
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
    private String filename;

    private Graph g;

    /**
     * Construct given a Graph associated with the FASTA file.
     */
    public FastaFile(Graph g) {
        this.g = g;
    }
    
    /**
     * Read a FASTA file into local instance objects.
     */
    public void readFastaFile(String filename) throws FileNotFoundException, IOException {
        if (verbose) System.out.println("Reading FASTA file: "+filename);
        this.filename = filename;

        // Graph stuff
        Map<Long,Integer> startToNode = g.getStartToNode();
        int[] length = g.getLength();
        int K = g.getK();
        
        sequences = new LinkedList<Sequence>();
        Nlocs = new TreeSet<Long>(); // uses TreeSet.ceiling()

        Map<Integer,Long> seqStart = new TreeMap<Integer,Long>();
        seqStart.put(0, (long) 0); // was 1

        List<List<Integer>> pathsList = new LinkedList<List<Integer>>();

        long curStart = 0; // was 1
        int index = 0;
        long seqEnd;

        BufferedReader in = new BufferedReader(new FileReader(filename));
        StringBuffer buffer = new StringBuffer();

        String line = in.readLine();
        if (line == null) {
            throw new IOException(filename + " is an empty file");
        }

        String desc = "";
        String seq = "";
        if (line.charAt(0) != '>') {
            throw new IOException("First line of " + filename + " should start with '>'");
        } else {
            desc = line.substring(1);
        }
        while ((line=in.readLine())!=null) {
            if (line.length()>0 && line.charAt(0)=='>') {
                seq = buffer.toString();
                // desc, seq are the next fasta sequence here
                Sequence s = new Sequence(desc.replace(',',';'), seq.length(), seqStart.get(index));
                sequences.add(s);
                for (int k=0; k<seq.length(); k++) {
                    if (seq.charAt(k) == 'N') {
                        Nlocs.add(s.getStartPos() + k);
                    }
                }

                List<Integer> path = new LinkedList<>();
                seqEnd = seqStart.get(index) + s.getLength() - 1; // ??
                curStart = seqStart.get(index);
                while (curStart>0 && !startToNode.containsKey(curStart)) {
                    curStart--;
                }
		// DEBUG
		if (!startToNode.containsKey(curStart)) {
		    System.err.println("ERROR: startToNode("+curStart+") does not exist.");
		    System.err.print("startToNode keys:");
		    for (long key : startToNode.keySet()) System.err.print(" "+key);
		    System.err.println("");
		    System.exit(1);
		}
		// DEBUG
		System.out.println("Adding path="+startToNode.get(curStart));
                path.add(startToNode.get(curStart));
                do {
                    curStart += length[startToNode.get(curStart)] - K; // WAS K-1
                    if (startToNode.containsKey(curStart)) {
			// DEBUG
			System.out.println("Adding path="+startToNode.get(curStart));
                        path.add(startToNode.get(curStart));
                    }
                } while (startToNode.containsKey(curStart) && (curStart+length[startToNode.get(curStart)]-1)<seqEnd);

                pathsList.add(path);
                seqStart.put(++index, seqEnd+1); // was +2

                buffer = new StringBuffer();
                desc = line.substring(1);
            } else {
                buffer.append(line.trim().toUpperCase());
            }
        }
        if (buffer.length()!=0) {
            seq = buffer.toString();
            // desc, seq are the next fasta sequence here
            Sequence s = new Sequence(desc.replace(',', ';'), seq.length(), seqStart.get(index));
            sequences.add(s);
            for (int k = 0; k < seq.length(); k++) {
                if (seq.charAt(k) == 'N') {
                    Nlocs.add(s.getStartPos() + k);
                }
            }

            List<Integer> path = new LinkedList<>();
            seqEnd = seqStart.get(index) + s.getLength() - 1; // ??
            curStart = seqStart.get(index);
            while (curStart > 0 && !startToNode.containsKey(curStart)) {
                curStart--;
            }
	    // DEBUG
	    System.out.println("Adding path="+startToNode.get(curStart));
            path.add(startToNode.get(curStart));
            do {
                curStart += length[startToNode.get(curStart)] - K; // WAS K-1
                if (startToNode.containsKey(curStart)) {
		    // DEBUG
		    System.out.println("Adding path="+startToNode.get(curStart));
                    path.add(startToNode.get(curStart));
                }
            } while (startToNode.containsKey(curStart) && (curStart+length[startToNode.get(curStart)]-1)<seqEnd);

            pathsList.add(path);
            seqStart.put(++index, seqEnd+1); // was +2
        }

        paths = new int[pathsList.size()][];
        for (int i=0; i<pathsList.size(); i++) {
            List<Integer> path = pathsList.get(i);
            paths[i] = new int[path.size()];
            for (int j=0; j<path.size(); j++) {
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
    public TreeSet<Long> getNlocs() {
        return Nlocs;
    }
    public String getFilename() {
        return filename;
    }
    
    public void setVerbose() {
        verbose = true;
    }

}
