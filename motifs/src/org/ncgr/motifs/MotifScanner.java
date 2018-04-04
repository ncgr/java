package org.ncgr.motifs;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.sql.SQLException;

import java.text.DecimalFormat;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Scan a sequence for likely motifs using MEME-format data stored in a Postgres database, imported from JASPAR.
 */
public class MotifScanner {

    // store all the motif matrices
    List<Matrix> matrices;

    // a little convenience object
    Map<Character,Integer> rows = new TreeMap<Character,Integer>();

    public MotifScanner() throws ClassNotFoundException, FileNotFoundException, IOException, SQLException {
        
        // initialize the matrix row values for DNA sequences (a convenience)
        rows.put('A', 0);
        rows.put('C', 1);
        rows.put('G', 2);
        rows.put('T', 3);

        // retrieve all of the matrices in the database
        matrices = Matrix.getAll();
    }

    /**
     * Scan the matrices for hits against the given query string, return results in a TreeMap keyed by score.
     */
    public TreeMap<Double,Matrix> scan(String query) throws ClassNotFoundException, FileNotFoundException, IOException, SQLException {
        TreeMap<Double,Matrix> hitMap = new TreeMap<Double,Matrix>();
        for (Matrix matrix : matrices) {
            int id = matrix.getId();
            String name = matrix.getName();
            int len = matrix.getMotifLength();
            // BIG RESTRICTION: only look at motifs with same length as query!
            if (len==query.length()) {
                // loop over columns
                int[][] vals = matrix.getData();
                int[] colSum = new int[len];
                double querySum = 0.0;
                for (int i=0; i<len; i++) {
                    char queryChar = query.charAt(i);
                    int row = rows.get(queryChar);
                    int queryVal = vals[i][row];
                    for (int j=0; j<rows.size(); j++) colSum[i] += vals[i][j];
                    if (colSum[i]>0) querySum += (double)queryVal/(double)colSum[i];
                }
                hitMap.put(querySum, matrix);
            }
        }
        return hitMap;
    }

    /**
     * Command-line utility.
     */
    public static void main(String[] args) throws ClassNotFoundException, FileNotFoundException, IOException, SQLException {
        DecimalFormat df = new DecimalFormat("0.0000");
        // validate arguments
        if (args.length!=1) {
            System.err.println("Usage: MotifScanner CGGTCTAGAT");
            System.exit(1);
        }
        String query = args[0];
        MotifScanner ms = new MotifScanner();
        TreeMap<Double,Matrix> hitMap = ms.scan(query);
        for (Double score : hitMap.keySet()) {
            Matrix m = hitMap.get(score);
            System.out.println(m.getId()+"\t"+m.getName()+"\t"+df.format(score)+"/"+query.length());
        }
    }

}
