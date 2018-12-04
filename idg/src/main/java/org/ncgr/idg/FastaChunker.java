package org.ncgr.idg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Simple routine that splits a FASTA file into n-mers, where n is given on the command line.
 */
public class FastaChunker {

    public static void main(String[] args) {

        if (args.length!=2) {
            System.out.println("Usage: FastaChunker [n] [FASTA file]");
            System.exit(0);
        }

        int n = 0;
        File fastaFile = null;
        
        try {
            n = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("Error: [N] must be an integer.");
            System.err.println("Usage: FastaChunker [n] [FASTA file]");
            System.exit(1);
        }

        try {
            fastaFile = new File(args[1]);
        } catch (Exception e) {
            System.err.println("Error reading "+args[1]);
            System.err.println("Usage: FastaChunker [n] [FASTA file]");
            System.exit(1);
        }

        try {
            
            BufferedReader reader = new BufferedReader(new FileReader(fastaFile));
            StringBuffer sequence = new StringBuffer();

            String id = "";

            // read it into a StringBuffer
            String line = null;
            while ((line=reader.readLine())!=null) {
                if (line.startsWith(">")) {
                    // get the ID of the full fasta sequence up to first space
                    String[] parts = line.split(" ");
                    id = parts[0].substring(1);
                } else {
                    sequence.append(line);
                }
            }

            // spit out individual n-mers
            for (int i=0; i<sequence.length()-n+1; i++) {
                String seq = sequence.substring(i,i+n);
                System.out.println(">"+id+"."+(i+1));
                System.out.println(seq);
            }
            
        } catch (Exception e) {
            System.err.println("Error reading file "+args[1]);
            System.err.println("Usage: FastaChunker [N] [FASTA-FILE]");
            System.exit(1);
        }

    }

}
