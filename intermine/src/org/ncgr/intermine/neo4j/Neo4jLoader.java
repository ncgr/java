package org.ncgr.intermine.neo4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Properties;

import org.intermine.metadata.AttributeDescriptor;
import org.intermine.metadata.ClassDescriptor;
import org.intermine.metadata.CollectionDescriptor;
import org.intermine.metadata.ConstraintOp;
import org.intermine.metadata.ReferenceDescriptor;
import org.intermine.metadata.Model;
import org.intermine.pathquery.Constraints;
import org.intermine.pathquery.OrderDirection;
import org.intermine.pathquery.PathConstraintAttribute;
import org.intermine.pathquery.PathQuery;
import org.intermine.webservice.client.core.ServiceFactory;
import org.intermine.webservice.client.services.QueryService;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.util.Pair;

import static org.neo4j.driver.v1.Values.parameters;

/**
 * Query an InterMine model, and load it and its data into a Neo4j database, along with the relationships derived from relations and collections.
 * Connection and other properties are given in neo4jloader.properties.
 * 
 * @author Sam Hokin
 */
public class Neo4jLoader {

    static final String PROPERTIES_FILE = "neo4jloader.properties";

    /**
     * @param args command line arguments
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        // Load parameters from neo4jloader.properties
        Properties props = new Properties();
        props.load(new FileInputStream(PROPERTIES_FILE));
        String intermineServiceUrl = props.getProperty("intermine.service.url");
        String neo4jUrl = props.getProperty("neo4j.url");
        String neo4jUser = props.getProperty("neo4j.user");
        String neo4jPassword = props.getProperty("neo4j.password");
        boolean verbose = Boolean.parseBoolean(props.getProperty("verbose"));
        int maxRows = Integer.parseInt(props.getProperty("max.rows"));

        // classes to ignore, usually superclasses or maybe just classes you don't want
        List<String> ignoredClasses = new ArrayList<String>();
        if (props.getProperty("ignored.classes")!=null) ignoredClasses = Arrays.asList(props.getProperty("ignored.classes").trim().split(","));

        // classes to load, overrides loading all classes other than those ignored
        List<String> loadedClasses = new ArrayList<String>();
        if (props.getProperty("loaded.classes")!=null && props.getProperty("loaded.classes").trim().length()>0) loadedClasses = Arrays.asList(props.getProperty("loaded.classes").trim().split(","));
        
        // references to ignore, typically reverse-reference
        List<String> ignoredReferences = new ArrayList<String>();
        if (props.getProperty("ignored.references")!=null) ignoredReferences = Arrays.asList(props.getProperty("ignored.references").trim().split(","));

        // collections to ignore, typically reverse-reference
        List<String> ignoredCollections = new ArrayList<String>();
        if (props.getProperty("ignored.collections")!=null) ignoredCollections = Arrays.asList(props.getProperty("ignored.collections").trim().split(","));

        // list of IM IDs of nodes that have had their attributes stored HERE
        List<Integer> nodesWithAttributesStored = new ArrayList<Integer>();

        // InterMine setup
        ServiceFactory factory = new ServiceFactory(intermineServiceUrl);
        Model model = factory.getModel();
        QueryService service = factory.getQueryService();
        PathQuery nodeQuery = new PathQuery(model);
        PathQuery refQuery = new PathQuery(model);
        PathQuery collQuery = new PathQuery(model);
        PathQuery attrQuery = new PathQuery(model);
        
        // Neo4j setup
        Driver driver = GraphDatabase.driver(neo4jUrl, AuthTokens.basic(neo4jUser, neo4jPassword));

        // Put the desired class descriptors into a map so we can grab them by class name if we want; alphabetical by class simple name
        Map<String,ClassDescriptor> nodeDescriptors = new TreeMap<String,ClassDescriptor>();
        for (ClassDescriptor cd : model.getClassDescriptors()) {
            String nodeClass = cd.getSimpleName();
            if (loadedClasses.size()>0 && loadedClasses.contains(nodeClass)) {
                nodeDescriptors.put(nodeClass, cd);
            } else if (loadedClasses.size()==0 && !ignoredClasses.contains(nodeClass)) {
                nodeDescriptors.put(nodeClass, cd);
            }
        }
        
        // Retreive the IM IDs of nodes that have already been fully stored
        List<Integer> nodesAlreadyStored = new ArrayList<Integer>();
        try (Session session = driver.session()) {
            try (Transaction tx = session.beginTransaction()) {
                StatementResult result = tx.run("MATCH (n:InterMineID) RETURN n.id");
                while (result.hasNext()) {
                    Record record = result.next();
                    nodesAlreadyStored.add(record.get("n.id").asInt());
                }
                tx.success();
                tx.close();
            }
        }

        // Loop over IM model and load the node, properties and relations with their corresponding reference and collections nodes (with only attributes)
        for (String nodeClass : nodeDescriptors.keySet()) {

                ClassDescriptor nodeDescriptor = nodeDescriptors.get(nodeClass);

                // display the node with labels
                System.out.println("--------------------------------------------------------");
                System.out.println(getFullNodeLabel(nodeDescriptor));
                
                // display the attributes
                Set<AttributeDescriptor> attrDescriptors = nodeDescriptor.getAllAttributeDescriptors();
                if (attrDescriptors.size()>1) {
                    Set<String> attrNames = new HashSet<String>(); // just for output
                    for (AttributeDescriptor ad : attrDescriptors) {
                        attrNames.add(ad.getName());
                    }
                    System.out.println("Attributes:"+attrNames);
                }

                // load the references, except ignored classes and ignored references, into a map, and display
                HashMap<String,ReferenceDescriptor> refDescriptors = new HashMap<String,ReferenceDescriptor>();
                for (ReferenceDescriptor rd : nodeDescriptor.getAllReferenceDescriptors()) {
                    String refName = rd.getName();
                    ClassDescriptor refClassDescriptor = rd.getReferencedClassDescriptor();
                    String refClass = refClassDescriptor.getSimpleName();
                    boolean keep = true;
                    keep = keep && !ignoredClasses.contains(refClass);
                    keep = keep && !ignoredReferences.contains(nodeClass+"."+refName);
                    // eliminate ignored superclasses as well
                    for (ClassDescriptor superclassDescriptor : refClassDescriptor.getAllSuperDescriptors()) {
                        String superclassName = superclassDescriptor.getSimpleName();
                        keep = keep && !ignoredReferences.contains(superclassName+"."+refName);
                    }
                    if (keep) refDescriptors.put(refName, rd);
                }
                if (refDescriptors.size()>0) System.out.println("References:"+refDescriptors.keySet());

                // get the collections, except ignored classes, into a map, and display
                HashMap<String,CollectionDescriptor> collDescriptors = new HashMap<String,CollectionDescriptor>();
                for (CollectionDescriptor cd : nodeDescriptor.getAllCollectionDescriptors()) {
                    String collName = cd.getName();
                    ClassDescriptor collClassDescriptor = cd.getReferencedClassDescriptor();
                    String collClass = collClassDescriptor.getSimpleName();
                    boolean keep = true;
                    keep = keep && !ignoredClasses.contains(collClass);
                    keep = keep && !ignoredCollections.contains(nodeClass+"."+collName);
                    // eliminate ignored superclasses as well
                    for (ClassDescriptor superclassDescriptor : collClassDescriptor.getAllSuperDescriptors()) {
                        String superclassName = superclassDescriptor.getSimpleName();
                        keep = keep && !ignoredCollections.contains(superclassName+"."+collName);
                    }
                    if (keep) collDescriptors.put(collName, cd);
                }
                if (collDescriptors.size()>0) System.out.println("Collections:"+collDescriptors.keySet());
            
                // query nodes of this class
                nodeQuery.clearView();
                nodeQuery.addView(nodeClass+".id"); // every object has an IM id
                int nodeCount = 0;
                Iterator<List<Object>> rows = service.getRowListIterator(nodeQuery);
                while (rows.hasNext() && (maxRows==0 || nodeCount<maxRows)) {

                    Object[] row = rows.next().toArray();
                    int id = Integer.parseInt(row[0].toString());

                    // abort this node if it's already stored
                    if (nodesAlreadyStored.contains(id)) continue;

                    if (verbose) System.out.print(nodeClass+":"+id+":");
                    nodeCount++;
			
                    // MERGE this node by its id
                    String nodeLabel = getFullNodeLabel(nodeDescriptor);
                    String merge = "MERGE (n:"+nodeLabel+" {id:"+id+"})";
                    try (Session session = driver.session()) {
                        try (Transaction tx = session.beginTransaction()) {
                            tx.run(merge);
                            tx.success();
                            tx.close();
                        }
                    }

                    // SET this nodes attributes if not already stored
                    if (!nodesWithAttributesStored.contains(id)) {
                        populateIdClassAttributes(service, driver, attrQuery, id, nodeLabel, nodeDescriptor);
                        nodesWithAttributesStored.add(id);
                    }

                    // CREATE INDEX on these individual node types
                    if (nodeCount==1) {
                        try (Session session = driver.session()) {
                            List<String> labels = Arrays.asList(nodeLabel.split(":"));
                            for (String label : labels) {
                                try (Transaction tx = session.beginTransaction()) {
                                    tx.run("CREATE INDEX ON :"+label+"(id)");
                                    tx.success();
                                    tx.close();
                                }
                            }
                        }
                    }

                    // MERGE this node's references by id, class by class
                    for (String refName : refDescriptors.keySet()) {
                        ReferenceDescriptor rd = refDescriptors.get(refName);
                        ClassDescriptor rcd = rd.getReferencedClassDescriptor();
                        String refLabel = getFullNodeLabel(rcd);
                        refQuery.clearView();
                        refQuery.clearConstraints();
                        refQuery.addView(nodeClass+".id");
                        refQuery.addView(nodeClass+"."+refName+".id");
                        refQuery.addConstraint(new PathConstraintAttribute(nodeClass+".id", ConstraintOp.EQUALS, String.valueOf(id)));
                        Iterator<List<Object>> rs = service.getRowListIterator(refQuery);
                        while (rs.hasNext()) {
                            Object[] r = rs.next().toArray();
                            int idn = Integer.parseInt(r[0].toString());      // node id
                            if (r[1]!=null) {                                 // refs can be null sometimes
                                int idr = Integer.parseInt(r[1].toString());  // ref id
                                if (idr!=idn) {                               // no loops
                                    // merge this reference node
                                    merge = "MERGE (n:"+refLabel+" {id:"+idr+"})";
                                    try (Session session = driver.session()) {
                                        try (Transaction tx = session.beginTransaction()) {
                                            tx.run(merge);
                                            tx.success();
                                            tx.close();
                                        }
                                    }
                                    // set this reference node's attributes
                                    if (!nodesWithAttributesStored.contains(idr)) {
                                        populateIdClassAttributes(service, driver, attrQuery, idr, refLabel, rcd);
                                        nodesWithAttributesStored.add(idr);
                                    }
                                    // merge this node-->ref relationship
                                    String match = "MATCH (n:"+nodeLabel+" {id:"+idn+"}),(r:"+refLabel+" {id:"+idr+"}) MERGE (n)-[:"+refName+"]->(r)";
                                    try (Session session = driver.session()) {
                                        try (Transaction tx = session.beginTransaction()) {
                                            tx.run(match);
                                            tx.success();
                                            tx.close();
                                        }
                                    }
                                    if (verbose) System.out.print("r");
                                }
                            }
                        }
                    }
			
                    // MERGE this node's collections by id, one at a time
                    for (String collName : collDescriptors.keySet()) {
                        CollectionDescriptor cd = collDescriptors.get(collName);
                        ClassDescriptor ccd = cd.getReferencedClassDescriptor();
                        String collLabel = getFullNodeLabel(ccd);
                        collQuery.clearView();
                        collQuery.clearConstraints();
                        collQuery.addView(nodeClass+".id");
                        collQuery.addView(nodeClass+"."+collName+".id");
                        collQuery.addConstraint(new PathConstraintAttribute(nodeClass+".id", ConstraintOp.EQUALS, String.valueOf(id)));
                        Iterator<List<Object>> rs = service.getRowListIterator(collQuery);
                        int collCount = 0;
                        while (rs.hasNext() && (maxRows==0 || collCount<maxRows)) {
                            collCount++;
                            Object[] r = rs.next().toArray();
                            int idn = Integer.parseInt(r[0].toString());      // node id
                            int idc = Integer.parseInt(r[1].toString());      // collection id
                            if (idc!=idn) {                                   // no loops
                                // merge this collections node
                                merge = "MERGE (n:"+collLabel+" {id:"+idc+"})";
                                try (Session session = driver.session()) {
                                    try (Transaction tx = session.beginTransaction()) {
                                        tx.run(merge);
                                        tx.success();
                                        tx.close();
                                    }
                                }
                                // set this collection node's attributes
                                if (!nodesWithAttributesStored.contains(idc)) {
                                    populateIdClassAttributes(service, driver, attrQuery, idc, collLabel, ccd);
                                    nodesWithAttributesStored.add(idc);
                                }
                                // merge this node-->coll relationship
                                String match = "MATCH (n:"+nodeLabel+" {id:"+idn+"}),(c:"+collLabel+" {id:"+idc+"}) MERGE (n)-[:"+collName+"]->(c)";
                                try (Session session = driver.session()) {
                                    try (Transaction tx = session.beginTransaction()) {
                                        tx.run(match);
                                        tx.success();
                                        tx.close();
                                    }
                                }
                            }
                            if (verbose) System.out.print("c");
                        }
                    }

                    // MERGE this node's InterMine ID into the InterMine ID nodes for record-keeping that it's stored
                    try (Session session = driver.session()) {
                        try (Transaction tx = session.beginTransaction()) {
                            tx.run("MERGE (:InterMineID {id:"+id+"})");
                            tx.success();
                            tx.close();
                        }
                    }
                    
                    if (verbose) System.out.println("");
                    
                }
            }
            
        // Close connections
        driver.close();

    }

    /**
     * Populate the node attributes for a given IM class and ID
     * @param service the InterMine QueryService
     * @param driver the Neo4j driver
     * @param attrQuery a PathQuery for querying attributes
     * @param id the InterMine ID of the specific node
     * @param nodeLabel the node label to use in the MATCH (may differ from className because of replacement)
     * @param cd the ClassDescriptor for this InterMine object
     */
    static void populateIdClassAttributes(QueryService service, Driver driver, PathQuery attrQuery, int id, String nodeLabel, ClassDescriptor cd) {
        String className = cd.getSimpleName();
        Set<AttributeDescriptor> attrDescriptors = cd.getAllAttributeDescriptors();
        if (attrDescriptors.size()>1) {
            attrQuery.clearView();
            attrQuery.clearConstraints();
            for (AttributeDescriptor ad : attrDescriptors) {
                String attrName = ad.getName();
                attrQuery.addView(className+"."+attrName);
            }
            attrQuery.addConstraint(new PathConstraintAttribute(className+".id", ConstraintOp.EQUALS, String.valueOf(id)));
            Iterator<List<Object>> rows = service.getRowListIterator(attrQuery);
            while (rows.hasNext()) {
                Object[] row = rows.next().toArray();
                // SET this nodes attributes
                String match = "MATCH (n:"+nodeLabel+" {id:"+id+"}) SET ";
                int i = 0;
                int terms = 0;
                for (AttributeDescriptor ad : attrDescriptors) {
                    String attrName = ad.getName();
                    String attrType = ad.getType();
                    String val = escapeForNeo4j(row[i++].toString());
                    terms++;
                    if (terms>1) match += ",";
                    if (attrType.equals("java.lang.String") || attrType.equals("org.intermine.objectstore.query.ClobAccess")) {
                        match += "n."+attrName+"=\""+val+"\"";
                    } else {
                        match += "n."+attrName+"="+val;
                    }
                }
                if (terms>0) {
                    try (Session session = driver.session()) {
                        try (Transaction tx = session.beginTransaction()) {
                            tx.run(match);
                            tx.success();
                            tx.close();
                        }
                    }
                }
            }
        }
    }

    /**
     * Escape special characters for Neo4j cypher queries.
     */
    static String escapeForNeo4j(String value) {
        StringBuilder builder = new StringBuilder();
        for (char c : value.toCharArray()) {
            if (c=='\'')
                builder.append("\\'");
            else if (c=='\"')
                builder.append("\\\"");
            else if (c=='\r')
                builder.append("\\r");
            else if (c=='\n')
                builder.append("\\n");
            else if (c=='\t')
                builder.append("\\t");
            else if (c=='\\')
                builder.append(""); //  yank leading slashes
            else if (c < 32 || c >= 127)
                builder.append(String.format("\\u%04x", (int)c));
            else
                builder.append(c);
        }
        return builder.toString();
    }

    /**
     * Form the multiple label for a node, appending its superclass names
     *
     * @param nodeDescriptor the ClassDescriptor for the desired node
     * @return a full node label
     */
    static String getFullNodeLabel(ClassDescriptor nodeDescriptor) {
        String nodeName = nodeDescriptor.getSimpleName();
        String fullNodeLabel = nodeName;
        for (ClassDescriptor superclassDescriptor : nodeDescriptor.getAllSuperDescriptors()) {
            String superclassName = superclassDescriptor.getSimpleName();
            if (!superclassName.equals(nodeName)) {
                fullNodeLabel += ":"+superclassName;
            }
        }
        return fullNodeLabel;
    }

}
