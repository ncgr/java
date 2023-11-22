package org.ncgr.intermine;

import java.util.Arrays;
import java.util.List;
import java.util.Iterator;

import org.intermine.pathquery.Constraints;
import org.intermine.pathquery.PathQuery;

import org.intermine.client.services.QueryService;
import org.intermine.client.core.ServiceFactory;
    
/**
 * Just a simple static main to spit out the XML version of a Java PathQuery.
 *
 * @author Sam Hokin
 */
public class PathQueryToXML {
    
    private static final String serviceRootUrl = "http://localhost:8080/minimine/service";
    private static final ServiceFactory factory = new ServiceFactory(serviceRootUrl);
    
    /**
     * Do the work. No parameters.
     */
    public static void main(String[] args) {

        // Create a query
        PathQuery query = new PathQuery(factory.getModel());

        // Let's get Gene IDs
        query.addViews("Gene.id","Gene.name", "Gene.length");

        // Here's a list of Gene.primaryIdentifier
        List<String> primaryIdentifiers = Arrays.asList("phavu.G19833.gnm1.ann1.Phvul.011G091200",
                                                        "phavu.G19833.gnm1.ann1.Phvul.005G002800",
                                                        "phavu.G19833.gnm1.ann1.Phvul.005G149000",
                                                        "phavu.G19833.gnm1.ann1.Phvul.008G102000");

        // Constrain to primaryIdentifier in a List
        query.addConstraint(Constraints.oneOfValues("Gene.primaryIdentifier", primaryIdentifiers));

        // output the query
        System.out.println(query);
        
        // output the query as XML
        System.out.println(query.toXml());

        // run the query
        QueryService service = factory.getQueryService();
        Iterator<List<Object>> rows = service.getRowListIterator(query);
        while (rows.hasNext()) {
            Object[] row = rows.next().toArray();
            System.out.println(row[0].toString() + "\t" + row[1].toString() + "\t" + row[2].toString());
        }
    }
}
