package org.ncgr.zip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import java.util.zip.GZIPInputStream;

/**
 * Static method returns a BufferedReader for an input file which may be gzipped.
 * NOTE: Assumes that underlying encoding of file is UTF-8.
 */
public class GZIPBufferedReader {

    final static String ENCODING = "UTF-8";
    
    /**
     * Return a Reader for ENCODING, either a ".gz" file or otherwise.
     *
     * @param filename the file name
     * @return a Reader
     */
    public static BufferedReader getReader(String filename) throws FileNotFoundException, IOException, UnsupportedEncodingException {
        if (filename.endsWith(".gz")) {
            InputStream fileStream = new FileInputStream(filename);
            InputStream gzipStream = new GZIPInputStream(fileStream);
            return new BufferedReader(new InputStreamReader(gzipStream, ENCODING));
        } else {
            InputStream fileStream = new FileInputStream(filename);
            return new BufferedReader(new InputStreamReader(fileStream, ENCODING));
        }
    }

    /**
     * Return a Reader for ENCODING, either a ".gz" file or otherwise.
     *
     * @param file the file
     * @return a Reader
     */
    public static BufferedReader getReader(File file) throws FileNotFoundException, IOException, UnsupportedEncodingException {
        String filename = file.getPath();
        return getReader(filename);
    }

    /**
     * Main method for testing this out.
     */
    public static void main(String[] args) {
        if (args.length==0) {
            System.err.println("Usage: GZIPBufferedReader <filename>");
            System.exit(1);
        }
        try {
            BufferedReader reader = getReader(args[0]);
            String line = null;
            while ( (line=reader.readLine()) != null ) {
                System.out.println(line);
            }
        } catch (Exception ex) {
            System.err.println(ex.toString());
            System.exit(1);
        }
    }
    
}
