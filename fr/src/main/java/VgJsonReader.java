import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Set;
import java.util.TreeSet;

import org.ncgr.pangenomics.Graph;

/**
 * Read a Vg JSON into a fr.Graph.
 *
 * @author Sam Hokin
 */
public class VgJsonReader {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        String jsonFile = args[0];
        Graph graph = new Graph();
        graph.setVerbose();
        graph.readVgJsonFile(jsonFile);
    }
        
}
