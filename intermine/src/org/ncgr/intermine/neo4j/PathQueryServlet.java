package org.ncgr.intermine.neo4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.intermine.metadata.Model;
import org.intermine.metadata.ClassDescriptor;
import org.intermine.metadata.CollectionDescriptor;
import org.intermine.metadata.ReferenceDescriptor;
import org.intermine.pathquery.PathConstraint;
import org.intermine.pathquery.PathException;
import org.intermine.pathquery.PathQuery;
import org.intermine.pathquery.OrderElement;
import org.intermine.webservice.client.core.ServiceFactory;
import org.intermine.webservice.client.services.QueryService;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;

import static org.neo4j.driver.v1.Values.parameters;

/**
 * Handle PathQuery requests by querying Neo4j (when applicable) or InterMine (otherwise).
 *
 * @author Sam Hokin
 */
public class PathQueryServlet extends HttpServlet {

    static final String CHARSET = "UTF-8";

    // support up to 26 nodes in query
    static List<String> nodeLetters = Arrays.asList("a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z");

    // context parameters
    String intermineRootUrl;
    String neo4jBoltUrl;
    String neo4jBoltUser;
    String neo4jBoltPassword;

    // IM PathQuery API stuff
    ServiceFactory factory;
    Model model;
    QueryService service;

    // Neo4j Bolt API stuff
    Driver driver;

    // output
    List<String> output;

    /**
     * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        // load the context parameters
        intermineRootUrl = getServletContext().getInitParameter("intermine.root.url");
        neo4jBoltUrl = getServletContext().getInitParameter("neo4j.bolt.url");
        neo4jBoltUser = getServletContext().getInitParameter("neo4j.bolt.user");
        neo4jBoltPassword = getServletContext().getInitParameter("neo4j.bolt.password");

        // InterMine setup
        factory = new ServiceFactory(intermineRootUrl+"/service");
        model = factory.getModel();
        service = factory.getQueryService();

        // Neo4j setup
        driver = GraphDatabase.driver(neo4jBoltUrl, AuthTokens.basic(neo4jBoltUser, neo4jBoltPassword));

        // data output storage so we're not affected by I/O in timing
        output = new ArrayList<String>();

    }

    /**
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
  
    /**
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // process the request
        String queryXml = request.getParameter("query");
        String format = request.getParameter("format");
        
        // bail if there is no query
        if (queryXml==null) return;
 
        PrintWriter writer = response.getWriter();

        // ----------------------------------------
        // ---------- INTERMINE ENDPOINT ----------
        // ----------------------------------------

        // URLEncode the query and form the request URL
        String encodedQuery = URLEncoder.encode(queryXml, CHARSET);

        // form the request URL
        String intermineEndpoint = intermineRootUrl+request.getServletPath();
        String queryUrl = intermineEndpoint+"?query="+encodedQuery;
        if (format!=null) queryUrl += "&format="+format;
        
        writer.println("");
        writer.println("========== IM web services endpoint ==========");
        writer.println("");
        writer.println(queryUrl);
        writer.println("");
        output.clear();
        
        // timing
        long startTime = System.currentTimeMillis();

        // make the IM GET request
        URLConnection connection = new URL(queryUrl).openConnection();
        connection.setRequestProperty("Accept-Charset", CHARSET);
        BufferedReader imReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), CHARSET));

        // set the content type corresponding to what IM returns
        response.setContentType("text/plain");
        // spit out the results
        String line = null;
        while ((line=imReader.readLine())!=null) {
            output.add(line);
        }
        
        // timing
        long endTime = System.currentTimeMillis();

        // output
        for (String s : output) writer.println(s);
        writer.println("");
        writer.println("Query time: "+(endTime-startTime)+" ms");

        // ------------------------------------
        // ---------- PATH QUERY API ----------
        // ------------------------------------
        
        writer.println("");
        writer.println("========== PathQuery API ==========");
        writer.println("");
        output.clear();
        
        // create the PathQuery for Java API request
        PathQuery pathQuery = service.createPathQuery(queryXml);
        writer.println(pathQuery.toString());
        writer.println("");

        // timing
        startTime = System.currentTimeMillis();
        
        Iterator<List<Object>> rows = service.getRowListIterator(pathQuery);
        while (rows.hasNext()) {
            Object[] row = rows.next().toArray();
            String s = "";
            for (int i=0; i<row.length; i++) {
                s += row[i].toString()+"\t";
            }
            output.add(s);
        }
        endTime = System.currentTimeMillis();
        for (String s : output) writer.println(s);
        writer.println("");
        writer.println("Query time: "+(endTime-startTime)+" ms");

        // ----------------------------------------
        // ---------- NEO4J CYPHER QUERY ----------
        // ----------------------------------------

        writer.println("");
        writer.println("========== Neo4j Cypher Query ==========");
        writer.println("");
        output.clear();
        
        Map<String,String> nodes = new TreeMap<String,String>(); // Cypher nodes keyed by letter (a:Gene)
        Map<String,String> properties = new TreeMap<String,String>(); // Cypher properties (c.name) keyed by full IM path (Gene.goAnnotation.ontologyTerm.name)
        List<String> orderedProperties = new ArrayList<String>(); // ordered as given in the PathQuery
        List<String> columnHeaders = pathQuery.getColumnHeaders();
        for (String columnHeader : columnHeaders) {
            String[] parts = columnHeader.split(" > ");
            String currentLetter = null;
            String currentNode = null;
            ClassDescriptor currentNodeDescriptor = null;
            for (int i=0; i<parts.length; i++) {
                if (i==0) {
                    // root node at start
                    currentNode = parts[i];
                    currentNodeDescriptor = model.getClassDescriptorByName(currentNode);
                    currentLetter = nodeLetters.get(0);
                    nodes.put(currentLetter, currentNode);
                } else if (i<(parts.length-1)) {
                    // subnodes in middle; can't get ref or coll descriptor by name since may be from a superclass
                    String className = null;
                    Set<ReferenceDescriptor> refDescriptors = currentNodeDescriptor.getAllReferenceDescriptors();
                    for (ReferenceDescriptor rd : refDescriptors) {
                        if (rd.getName().equals(parts[i])) {
                            className = rd.getReferencedClassDescriptor().getSimpleName();
                        }
                    }
                    Set<CollectionDescriptor> collDescriptors = currentNodeDescriptor.getAllCollectionDescriptors();
                    for (CollectionDescriptor cd : collDescriptors) {
                        if (cd.getName().equals(parts[i])) {
                            className = cd.getReferencedClassDescriptor().getSimpleName();
                        }
                    }
                    if (className==null) {
                        writer.println("********** Could not find class for "+currentNode+"."+parts[i]);
                    } else {
                        currentNode = className;
                        currentNodeDescriptor = model.getClassDescriptorByName(currentNode);
                        if (nodes.containsValue(currentNode)) {
                            // spin through it, don't know a quicker way
                            for (String letter : nodes.keySet()) {
                                if (nodes.get(letter).equals(currentNode)) currentLetter = letter;
                            }
                        } else {
                            // find the next available letter
                            int j = 1;
                            while (nodes.containsKey(nodeLetters.get(j))) {
                                j++;
                            }
                            currentLetter = nodeLetters.get(j);
                            nodes.put(currentLetter, currentNode);
                        }
                    }
                }
            }
            // property at end
            String path = parts[0];
            for (int i=1; i<parts.length; i++) path += "."+parts[i];
            String property = currentLetter+"."+parts[parts.length-1];
            properties.put(path, property);
            orderedProperties.add(property);
        }

        // Cypher query: MATCH section
        String cypherQuery = "MATCH ";
        boolean first = true;
        for (String letter : nodes.keySet()) {
            String node = nodes.get(letter);
            if (first) {
                first = false;
            } else {
                cypherQuery += "-[]-";
            }
            cypherQuery += "("+letter+":"+node+")";
        }

        // Cypher query: WHERE section
        cypherQuery += " WHERE ";
        Map<PathConstraint,String> constraints = pathQuery.getConstraints();
        for (PathConstraint pc : constraints.keySet()) {
            String pcString = pc.toString();
            String[] parts = pcString.split(" = ");
            cypherQuery += properties.get(parts[0])+"=\""+parts[1]+"\"";
        }

        // Cypher query: RETURN section
        cypherQuery += " RETURN ";
        first = true;
        for (String property : orderedProperties) {
            if (first) {
                first = false;
            } else {
                cypherQuery += ",";
            }
            cypherQuery += property;
        }

        // Cypher query: ORDER BY section
        cypherQuery += " ORDER BY ";
        List<OrderElement> orderElements = pathQuery.getOrderBy();
        for (OrderElement orderElement : orderElements) {
            String orderElementString = orderElement.toString();
            String[] parts = orderElementString.split(" ");
            cypherQuery += properties.get(parts[0])+" "+parts[1];
        }

        // display our Cypher query!
        writer.println(cypherQuery);
        writer.println("");

        // execute the Cypher query
        startTime = System.currentTimeMillis();
        try (Session session = driver.session()) {
            try (Transaction tx = session.beginTransaction()) {
                StatementResult result = tx.run(cypherQuery);
                while (result.hasNext()) {
                    Record record = result.next();
                    String row = "";
                    for (String property : orderedProperties) {
                        row += record.get(property).asString()+"\t";
                    }
                    output.add(row);
                }
            }
        }
        endTime = System.currentTimeMillis();
        for (String s : output) writer.println(s);
        writer.println("");
        writer.println("Query time: "+(endTime-startTime)+" ms");

        // close out the response writer
        writer.flush();
        writer.close();

    }

    /**
     * @see javax.servlet.GenericServlet#destroy()
     */
    public void destroy() {
        // close the Neo4j Bolt driver
        driver.close();
    }

}
