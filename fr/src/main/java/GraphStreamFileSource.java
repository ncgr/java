import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.GraphParseException;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceFactory;
import org.graphstream.stream.file.FileSinkDOT;
import org.graphstream.stream.file.FileSinkDGS;

import java.io.IOException;

/**
 * Experiment with some GraphStream classes and methods.
 */
public class GraphStreamFileSource {

    public static void main(String[] args) throws IOException, GraphParseException {

        boolean readGraph = args[0].equals("read");
        boolean writeGraph = args[0].equals("write");
        String filePath = args[1];

        if (readGraph) {
            Graph g = new MultiGraph("G");
            g.read(filePath);
            System.out.println("Done reading "+filePath+".");
        }

        if (writeGraph) {
            Graph g = new MultiGraph("G");
            // nodes
            g.addNode("N1");
            g.addNode("N2");
            g.addNode("N3");
            // edges
            g.addEdge("E12a", "N1", "N2", true);
            g.addEdge("E12b", "N1", "N2", true);
            g.addEdge("E13", "N1", "N3", true);
            g.addEdge("E23", "N2", "N3", true);
            // edge attributes
            g.getEdge("E12a").addAttribute("isCool", false);
            g.getEdge("E12a").addAttribute("length", 12345);
            g.getEdge("E12b").addAttribute("isCool", true);
            g.getEdge("E12b").addAttribute("length", 54321);
            // output
            if (filePath.endsWith(".dot")) {
                FileSinkDOT sink = new FileSinkDOT();
                g.write(sink, filePath);
                System.out.println("Done writing "+filePath+".");
            } else if (filePath.endsWith(".dgs")) {
                FileSinkDGS sink = new FileSinkDGS();
                g.write(sink, filePath);
                System.out.println("Done writing "+filePath+".");
            }
            
        }
        
    }
    
}
