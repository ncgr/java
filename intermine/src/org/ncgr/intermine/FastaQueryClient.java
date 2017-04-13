package org.ncgr.intermine;

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
 * Print out the FASTA for a gene with the given symbol.
 * Usage: FastaQueryClient serviceURL geneSymbol
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

        if (args.length!=2) {
            System.out.println("Usage: FastaQueryClient <intermine service URL> <gene symbol>");
            System.exit(0);
        }
        String intermineServiceUrl = args[0];
        String geneSymbol = args[1];
    
        ServiceFactory factory = new ServiceFactory(intermineServiceUrl);
        Model model = factory.getModel();
        PathQuery query = new PathQuery(model);

        // Select the output columns:
        query.addViews("Gene.primaryIdentifier", "Gene.secondaryIdentifier", "Gene.symbol", "Gene.sequence.residues");

        // Constrain to our chosen symbol
        query.addConstraint(Constraints.eq("Gene.symbol", geneSymbol));

        QueryService service = factory.getQueryService();
        Iterator<List<Object>> rows = service.getRowListIterator(query);
        while (rows.hasNext()) {
            Object[] row = rows.next().toArray();
            String primaryIdentifier = row[0].toString();
            String secondaryIdentifier = row[1].toString();
            String symbol = row[2].toString();
            String residues = row[3].toString();
            System.out.println(">"+primaryIdentifier+"|"+secondaryIdentifier+"|"+symbol);
            System.out.println(residues);
        }

    }

}

