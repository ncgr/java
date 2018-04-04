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

    static DecimalFormat df = new DecimalFormat("0.0000");

    public static void main(String[] args) throws ClassNotFoundException, FileNotFoundException, IOException, SQLException {

        // validate arguments
        if (args.length!=1) {
            System.err.println("Usage: MotifScanner CGGTCTAGAT");
            System.exit(1);
        }
        String query = args[0];

        // the matrix row values for DNA sequences
        Map<Character,Integer> rows = new TreeMap<Character,Integer>();
        rows.put('A', 0);
        rows.put('C', 1);
        rows.put('G', 2);
        rows.put('T', 3);

        // the hit list
        TreeMap<Double,Matrix> hitMap = new TreeMap<Double,Matrix>();

        // query all motifs
        List<Matrix> matrices = Matrix.getAll();
        for (Matrix matrix : matrices) {
            int id = matrix.getId();
            String name = matrix.getName();
            int len = matrix.getMotifLength();
            // only look at motifs with exact same length as query
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
                // output
                System.out.println(String.valueOf(id)+"\t"+name+"\t"+querySum);
                for (int i=0; i<len; i++) System.out.print("\t"+query.charAt(i));
                System.out.println("");
                for (char base : rows.keySet()) {
                    int row = rows.get(base);
                    System.out.print(base);
                    for (int i=0; i<len; i++) {
                        System.out.print("\t"+vals[i][row]);
                    }
                    System.out.println("");
                }
                System.out.println("");
            }
        }

        // output the top three winners
        int count = 0;
        for (Double querySum : hitMap.descendingKeySet()) {
            count++;
            Matrix m = hitMap.get(querySum);
            System.out.println(m.getId()+"\t"+m.getName()+"\t"+m.getProteins()+"\t"+df.format(querySum)+"/"+query.length());
            if (count>3) break;
        }

    }


}
