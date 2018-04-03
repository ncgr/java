package org.ncgr.meme;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Scan a sequence for likely motifs using MEME-format data stored in a Postgres database, imported from JASPAR.
 */
public class MotifScanner {

    // the matrix row values for DNA sequences
    static char[] rows = {'A', 'C', 'G', 'T'};
    
    public static void main(String[] args) throws ClassNotFoundException, FileNotFoundException, IOException, SQLException {

        // validate arguments
        if (args.length!=1) {
            System.err.println("Usage: MotifScanner CGGTCTAGAT");
            System.exit(1);
        }
        String query = args[0];

        // open the db connections
        DB db1 = new DB();
        DB db2 = new DB();

        // the overall maximum hit
        double maxSum = 0.0;
        String maxName = "";
        int maxId = 0;

        // query all motifs
        db1.executeQuery("SELECT * FROM matrix ORDER BY name");
        while (db1.rs.next()) {
            int id = db1.rs.getInt("id");
            String name = db1.rs.getString("name");
            // get motif length
            db2.executeQuery("SELECT max(col) FROM matrix_data WHERE id="+id);
            db2.rs.next();
            int len = db2.rs.getInt("max");
            // only look at motifs with exact same length as query
            if (len==query.length()) {
                // loop over columns
                int[][] vals = new int[len][4];
                int[] colSum = new int[len];
                double querySum = 0.0;
                for (int i=0; i<len; i++) {
                    int col = i+1;
                    char queryChar = query.charAt(i);
                    int queryVal = 0;
                    // query over rows A, C, G, T
                    int rowId = 0;
                    db2.executeQuery("SELECT * FROM matrix_data WHERE id="+id+" AND col="+col+" ORDER BY row");
                    while (db2.rs.next()) {
                        char row = db2.rs.getString("row").charAt(0);
                        int val = db2.rs.getInt("val");
                        vals[col-1][rowId++] = val;
                        colSum[i] += val;
                        if (row==queryChar) queryVal = val;
                    }
                    if (colSum[i]>0) querySum += (double)queryVal/(double)colSum[i];
                }
                // output
                System.out.println(String.valueOf(id)+"\t"+name+"\t"+querySum);
                for (int i=0; i<len; i++) System.out.print("\t"+query.charAt(i));
                System.out.println("");
                for (int j=0; j<4; j++) {
                    System.out.print(rows[j]);
                    for (int i=0; i<len; i++) {
                        System.out.print("\t"+vals[i][j]);
                    }
                    System.out.println("");
                }
                for (int i=0; i<len; i++) {
                    System.out.print("\t"+colSum[i]);
                }
                System.out.println("");
                // store overall best hit
                if (querySum>maxSum) {
                    maxSum = querySum;
                    maxName = name;
                    maxId = id;
                }
            }
        }
        System.out.println("");
        System.out.println("BEST HIT: ["+maxId+"] "+maxName+" = "+maxSum);


        // close the connections
        db1.close();
        db2.close();

    }


}
