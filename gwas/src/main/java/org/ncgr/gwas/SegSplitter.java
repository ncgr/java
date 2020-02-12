package org.ncgr.gwas;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Simply splits a segregation file into separate files per chromosome, to ease loading into R.
 */
public class SegSplitter {
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        if (args.length!=1) {
            System.out.println("Usage: SegSplitter file.seg.txt");
            System.exit(0);
        }

        String segFilename = args[0];
        String prefix = segFilename.replace(".txt", "");
        
        BufferedReader segReader = new BufferedReader(new FileReader(segFilename));
        String line = null;
        String chr = "";
        BufferedWriter writer = null;
        while ((line=segReader.readLine())!=null) {
            String[] fields = line.split("\\t");
            if (fields[0].equals(chr)) {
                writer.write(line);
                writer.newLine();
            } else {
                if (writer!=null) writer.close();
                chr = fields[0];
                writer = new BufferedWriter​(new FileWriter​(prefix+"."+chr+".txt"));
                writer.write(line);
                writer.newLine();
            }
        }
        writer.close();
        segReader.close();
    }
}
