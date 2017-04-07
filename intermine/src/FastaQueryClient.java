import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

import org.intermine.metadata.Model;
import org.intermine.pathquery.Constraints;
import org.intermine.pathquery.OrderDirection;
import org.intermine.pathquery.PathQuery;
import org.intermine.webservice.client.core.ServiceFactory;
import org.intermine.webservice.client.services.QueryService;

/**
 * Print out a multi-FASTA corresponding to all genes.
 * Enter the Intermine service URL as a parameter.
 *
 * @author Sam Hokin
 */
public class FastaQueryClient {

    /**
     * Perform the query and print the rows of results.
     * @param args command line arguments
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        if (args.length!=1) {
            System.out.println("Usage: FastaQueryClient <intermine service URL>");
            System.exit(0);
        }
        
        String intermineServiceUrl = args[0];
    
        ServiceFactory factory = new ServiceFactory(intermineServiceUrl);
        Model model = factory.getModel();
        PathQuery query = new PathQuery(model);

        // Select the output columns:
        query.addViews("Gene.primaryIdentifier", "Gene.secondaryIdentifier");

        // Add orderby
        query.addOrderBy("Gene.primaryIdentifier", OrderDirection.ASC);

        // Filter the results with the following constraints:
        query.addConstraint(Constraints.eq("Gene.symbol", args[0]));

        QueryService service = factory.getQueryService();
        Iterator<List<Object>> rows = service.getRowListIterator(query);
        while (rows.hasNext()) {
            Object[] row = rows.next().toArray();
            System.out.println(">"+row[0].toString());
            System.out.println(row[1].toString());
        }

    }

}

