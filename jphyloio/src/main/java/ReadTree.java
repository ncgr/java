import java.io.File;

import org.ncgr.jphyloio.Edge;
import org.ncgr.jphyloio.Node;

import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.ReadWriteParameterNames;
import info.bioinfweb.jphyloio.events.EdgeEvent;
import info.bioinfweb.jphyloio.events.JPhyloIOEvent;
import info.bioinfweb.jphyloio.events.LinkedLabeledIDEvent;
import info.bioinfweb.jphyloio.events.NodeEvent;
import info.bioinfweb.jphyloio.events.type.EventTopologyType;
import info.bioinfweb.jphyloio.factory.JPhyloIOReaderWriterFactory;
import info.bioinfweb.jphyloio.formats.newick.NewickEventReader;

/**
 * Loads a tree from a file given on the command line for testing purposes.
 */
public class ReadTree {
    
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        // validate
        if (args.length==0) {
            System.err.println("Usage: ReadTree <Newick file>");
            System.exit(1);
        }
        File inputFile = new File(args[0]);
        // Guess the format, since the user did not explicitly specify one.
        JPhyloIOReaderWriterFactory factory = new JPhyloIOReaderWriterFactory();
        try {
            String formatID = factory.guessFormat(inputFile);
            if (formatID==null) {
                System.err.println("File format could not be determined from file "+inputFile.getName());
                return;
            } else if (!formatID.equals("info.bioinfweb.jphyloio.newick")) {
                System.err.println("File format is not a Newick file: "+formatID);
                return;
            }
            System.out.println("Format: "+formatID);
            // read the tree
            readTree(factory, inputFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
    /**
     * Read the tree described by a Newick file.
     */
    static void readTree(JPhyloIOReaderWriterFactory factory, File file) throws Exception {
        ReadWriteParameterMap parameters = new ReadWriteParameterMap();

        // Use OTU labels as node labels if no node label is present.
        parameters.put(ReadWriteParameterNames.KEY_USE_OTU_LABEL, true);  

        // This parameter defines if cross links between nodes (defined by the clade_relation tag of PhyloXML) should be
        // modeled as metadata attached to a node or if the whole phylogeny shall be interpreted as a phylogenetic network.
        // Since the network interpretation is the default, we need to set this parameter in order to receive tree events
        // and not network events.
        
        // commenting out doesn't help
        // parameters.put(ReadWriteParameterNames.KEY_PHYLOXML_CONSIDER_PHYLOGENY_AS_TREE, true);

        // TEST - doesn't help
        // parameters.put(ReadWriteParameterNames.KEY_EXPECT_E_NEWICK, true);

        //NewickEventReader reader = new NewickEventReader​(file, parameters);
        NewickEventReader reader = new NewickEventReader(file, parameters);

        // This loop will run until all events of the JPhyloIO reader are consumed (and the end of the document is reached). 
        while (reader.hasNextEvent()) {
            JPhyloIOEvent event = reader.next();
            // DEBUG
            System.out.println("## "+event.getType().toString());
            //
            if (event.getType().getTopologyType().equals(EventTopologyType.START)) {
                switch (event.getType().getContentType()) {
                case ALIGNMENT:
                    // Indicates the start or the end of the contents of a matrix.
                    break;
                case CHARACTER_DEFINITION:
                    // Indicates a defined character (alignment column).
                    break;
                case CHARACTER_SET:
                    // Indicates the start or end of a sequence of CHARACTER_SET_INTERVAL events.
                    break;
                case CHARACTER_SET_INTERVAL:
                    // Indicates a single interval of a character set.
                    break;
                case COMMENT:
                    // Indicates a comment found in the underlying data source.
                    System.out.println("COMMENT:"+event.toString());
                    break;
                case DOCUMENT:
                    // Indicates the start or the end of the read document.
                    System.out.println("DOCUMENT:"+event.toString());
                    break;
                case EDGE:
                    // Indicates an edge in a phylogenetic tree or network.
                    Edge e = new Edge(event.asEdgeEvent());
                    System.out.println("EDGE:"+e.toString());
                    break;
                case LITERAL_META:
                    // Indicates the start or the end of a literal meta information.
                    break;
                case LITERAL_META_CONTENT:
                    // Indicates literal metadata content was found in the underlying data source.
                    break;
                case NETWORK:
                    // Indicates the start or the end of the contents of a phylogenetic network.
                    System.out.println("NETWORK:"+event.toString());
                    break;
                case NODE:
                    // Indicates a node in a phylogenetic tree or network.
                    Node n = new Node(event.asNodeEvent());
                    System.out.println("NODE:"+n.toString());
                    break;
                case NODE_EDGE_SET:
                    // Indicates the start or end of a sequence of SET_ELEMENT events that define a set of node and edges (including root edges).
                    break;
                case OTU:
                    // Indicates the start or the end of an OTU/taxon definition.
                    break;
                case OTU_LIST:
                    // Indicates the start or the end of a list of OTU/taxon definitions.
                    break;
                case OTU_SET:
                    // Indicates the start or end of a sequence of SET_ELEMENT events that define a set of OTUs.
                    break;
                case RESOURCE_META:
                    // Indicates the start or the end of a resource meta information.
                    break;
                case ROOT_EDGE:
                    // Indicates a root edge in a phylogenetic tree.
                    break;
                case SEQUENCE:
                    // Indicates the start or the end of the contents of a sequence in a matrix.
                    break;
                case SEQUENCE_SET:
                    // Indicates the start or end of a sequence of SET_ELEMENT events that define a set of sequences.
                    break;
                case SEQUENCE_TOKENS:
                    // Indicates a number of sequence tokens.
                    break;
                case SET_ELEMENT:
                    // Indicates a member of a set that is linked by its ID.
                    break;
                case SINGLE_SEQUENCE_TOKEN:
                    // Indicates a number a single sequence token.
                    break;
                case SINGLE_TOKEN_DEFINITION: 	
                    // Indicates the definition of a single sequence token symbol.
                    break;
                case TOKEN_SET_DEFINITION:
                    // Indicates the start or end of a token set definition.
                    break;
                case TREE:
                    // Indicates the start or the end of the contents of a phylogenetic tree.
                    break;
                case TREE_NETWORK_GROUP:
                    // Indicates the start or the end of a sequence of phylogenetic trees and network.
                    System.out.println("TREE_NETWORK_GROUP:"+stringify(event.asLinkedLabeledIDEvent()));
                    break;
                case TREE_NETWORK_SET:
                    // Indicates the start or end of a sequence of SET_ELEMENT events that define a set of trees and networks.
                    System.out.println("TREE_NETWORK_SET:"+event.toString());
                    break;
                case UNKNOWN_COMMAND:
                    // Events of this type are used by some readers to provide the application with contents of unknown commands in a format.
                    break;
	        default:
                    // probably shouldn't reach this
                    System.out.println("default:"+event.getType().getContentType()+" "+event.toString());
                    break;
                }
            } else {
                // do nothing if event topology type is not START
                // System.err.println(event.getType().getTopologyType()+":"+event.getType().getContentType()+":"+event.toString());
            }
        }
    }

    /**
     * Return a string summarizing a LinkedLabeledIDEvent.
     */
    public static String stringify(LinkedLabeledIDEvent e) {
        String s = e.getID();
        if (e.hasLabel()) {
            s += ":"+e.getLabel();
        }
        if (e.hasLink()) {
            s += ":"+e.getLinkedID();
        }
        return s;
    }

}
