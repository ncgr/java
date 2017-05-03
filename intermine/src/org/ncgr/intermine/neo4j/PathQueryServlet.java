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
import java.util.HashMap;
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
import org.intermine.metadata.AttributeDescriptor;
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
    static final int MAX_ROWS_SHOWN = 10;

    // IM classes which should be treated as edges: key=IM class, value=Neo4j edge type
    static Map<String,String> edgeClassTypes = new HashMap<String,String>();

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

        // load the special classes where an IM class becomes a Neo4j relationship
        String edgeClassList = getServletContext().getInitParameter("intermine.edge.classes");
        String edgeTypeList = getServletContext().getInitParameter("neo4j.edge.types");
        if (edgeClassList!=null) {
            String[] classes = edgeClassList.split(",");
            String[] types = edgeTypeList.split(",");
            for (int i=0; i<classes.length; i++) {
                edgeClassTypes.put(classes[i], types[i]);
            }
        }

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
        
        writer.println("========== IM web services endpoint ==========");
        writer.println("");
        writer.println(queryUrl);
        writer.println("");
        output.clear();
        
        // timing
        long startTime = System.currentTimeMillis();

        try {
            
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

        } catch (Exception e) {
            output.add(e.toString());
        }
        
        // timing
        long endTime = System.currentTimeMillis();

        // output
        int count = 0;
        for (String s : output) {
            count++;
            if (count<=MAX_ROWS_SHOWN) writer.println(s);
        }
        if (count>MAX_ROWS_SHOWN) {
            writer.println("");
            writer.println("+ "+(output.size()-MAX_ROWS_SHOWN)+" more records.");
        }
        output.clear();
        writer.println("");
        writer.println("Query time: "+(endTime-startTime)+" ms");
        writer.println("");

        // ------------------------------------
        // ---------- PATH QUERY API ----------
        // ------------------------------------
        
        // create the PathQuery for Java API request
        PathQuery pathQuery = service.createPathQuery(queryXml);
        
        writer.println("========== PathQuery API ==========");
        try {
            // some diagnostics
            writer.println("PathQuery:"+pathQuery);
            writer.println("ConstraintLogic:"+pathQuery.getConstraintLogic());
            writer.println("Description:"+pathQuery.getDescription());
            writer.println("JSON:"+pathQuery.getJson());
            writer.println("RootClass:"+pathQuery.getRootClass());
            writer.println("SubClasses:"+pathQuery.getSubclasses());
            writer.println("Title:"+pathQuery.getTitle());
            writer.println("Valid:"+pathQuery.isValid());
            writer.println("FixUpForJoinStyle:"+pathQuery.fixUpForJoinStyle());
            writer.println("ColumnHeaders:"+pathQuery.getColumnHeaders());
            writer.println("GroupedConstraintLogic:"+pathQuery.getGroupedConstraintLogic());
            writer.println("View:"+pathQuery.getView());
            writer.println("VerifyList:"+pathQuery.verifyQuery());
            writer.println("BagNames:"+pathQuery.getBagNames());
            writer.println("ConstraintCodes:"+pathQuery.getConstraintCodes());
            writer.println("ExistingLoops:"+pathQuery.getExistingLoops());
            writer.println("ConstraintGroups:"+pathQuery.getConstraintGroups());
            writer.println("OuterJoinGroups:"+pathQuery.getOuterJoinGroups());
            writer.println("OuterMap:"+pathQuery.getOuterMap());
        } catch (PathException e) {
            writer.println(e.toString());
        }
        writer.println("");

        // timing
        startTime = System.currentTimeMillis();
        
        try {
            
            Iterator<List<Object>> rows = service.getRowListIterator(pathQuery);
            while (rows.hasNext()) {
                Object[] row = rows.next().toArray();
                String s = "";
                for (int i=0; i<row.length; i++) {
                    s += row[i].toString()+"\t";
                }
                output.add(s);
            }

        } catch (Exception e) {
            output.add(e.toString());
        }

        endTime = System.currentTimeMillis();
        count = 0;
        for (String s : output) {
            count++;
            if (count<=MAX_ROWS_SHOWN) {
                writer.println(s);
            }
        }
        if (count>MAX_ROWS_SHOWN) {
            writer.println("");
            writer.println("+ "+(output.size()-MAX_ROWS_SHOWN)+" more records.");
        }
        output.clear();
        writer.println("");
        writer.println("Query time: "+(endTime-startTime)+" ms");
        writer.println("");

        // ----------------------------------------
        // ---------- NEO4J CYPHER QUERY ----------
        // ----------------------------------------

        writer.println("========== Neo4j Cypher Query ==========");
        writer.println("");
        
        Map<String,String> nodes = new TreeMap<String,String>();          // Cypher nodes keyed by letter (a:Gene)
        Map<String,String> properties = new TreeMap<String,String>();     // Cypher properties (c.name) keyed by full IM path (Gene.goAnnotation.ontologyTerm.name)
        List<String> orderedProperties = new ArrayList<String>();         // properties ordered as given in the PathQuery
        Map<String,String> orderedTypes = new HashMap<String,String>();   // stores field types (java.lang.String, etc.) for each property
        List<String> columnHeaders = pathQuery.getColumnHeaders();
        for (String columnHeader : columnHeaders) {
            String path = "";
            String[] parts = columnHeader.split(" > ");
            String currentLetter = null;
            String currentNode = null;
            ClassDescriptor currentNodeDescriptor = null;
            for (int i=0; i<parts.length; i++) {
                if (i==0) {
                    // root node at start
                    path = parts[i];
                    currentNode = parts[i];
                    currentNodeDescriptor = model.getClassDescriptorByName(currentNode);
                    currentLetter = nodeLetters.get(0);
                    nodes.put(currentLetter, currentNode);
                } else if (i<(parts.length-1)) {
                    path += "."+parts[i];
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
                } else {
                    // property at end
                    path += "."+parts[i];
                    String property = currentLetter+"."+parts[i];
                    properties.put(path, property);
                    orderedProperties.add(property);
                    AttributeDescriptor currentAttributeDescriptor = currentNodeDescriptor.getAttributeDescriptorByName(parts[i], true);
                    orderedTypes.put(property, currentAttributeDescriptor.getType());
                }
            }
        }

        // Cypher query: MATCH section
        String cypherQuery = "MATCH ";
        boolean first = true;
        boolean edge = false;
        for (String letter : nodes.keySet()) {
            String node = nodes.get(letter);
            if (first) {
                // one always starts with a node
                first = false;
                cypherQuery += "("+letter+":"+node+")";
                edge = false;
            } else if (edgeClassTypes.containsKey(node)) {
                // force this one to be an edge, not a node
                cypherQuery += "-["+letter+":"+edgeClassTypes.get(node)+"]-";
                edge = true;
            } else if (edge) {
                // have an edge already so follow with a plain node
                cypherQuery += "("+letter+":"+node+")";
                edge = false;
            } else {
                // need a generic edge in front of this node
                cypherQuery += "-[]-";
                cypherQuery += "("+letter+":"+node+")";
                edge = false;
            }
        }

        // Cypher query: WHERE section OPTIONAL
        Map<PathConstraint,String> constraints = pathQuery.getConstraints();
        if (constraints.size()>0) {
            cypherQuery += " WHERE ";
            for (PathConstraint pc : constraints.keySet()) {
                String pcString = pc.toString();
                String[] parts = pcString.split(" = ");
                cypherQuery += properties.get(parts[0])+"=\""+parts[1]+"\"";
            }
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
                        String type = orderedTypes.get(property);
                        try {
                            if (type.endsWith("String")) {
                                row += record.get(property).asString()+"\t";
                            } else {
                                row += record.get(property)+"\t";
                            }
                        } catch (Exception e) {
                            row += e.toString();
                        }
                    }
                    output.add(row);
                }
            }
        }
        endTime = System.currentTimeMillis();

        count = 0;
        for (String s : output) {
            count++;
            if (count<=MAX_ROWS_SHOWN) writer.println(s);
        }
        if (count>MAX_ROWS_SHOWN) {
            writer.println("");
            writer.println("+ "+(output.size()-MAX_ROWS_SHOWN)+" more records.");
        }
        output.clear();
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
