package org.ncgr.datastore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Provides methods for extracting useful chunks of info from an LIS README.md file.
 */
public class Readme {

    File file;

    /**
     * Construct from a file.
     */
    public Readme(File file) throws FileNotFoundException {
        this.file = file;
    }

    /**
     * Get the content of a given section, e.g. Genotype
     */
    public String getContent(String section) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        while ((line=reader.readLine())!=null) {
            if (line.startsWith("#### "+section)) {
                String next = reader.readLine();
                if (next.startsWith("<!---")) {
                    return reader.readLine();
                } else {
                    return next;
                }
            }
        }
        return null;
    }

}
