import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceFactory;
import org.graphstream.stream.file.FileSourceDOT;

import java.io.IOException;

public class GraphStreamFileSource {

    public static void main(String[] args) {
        String filePath = args[0];
        Graph g = new DefaultGraph("g");
        FileSource fs = null;
        try {
            System.out.println("ATTEMPTING TO LOAD:"+filePath);
            fs = FileSourceFactory.sourceFor(filePath);
            fs.addSink(g);
            fs.begin(filePath);
            while (fs.nextEvents()) {
                System.out.println("READ EVENT.");
            }
            fs.end();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            if (fs!=null) fs.removeSink(g);
        }

        System.out.println("DONE READING GRAPH FROM DOT FILE.");
    }
    
}
