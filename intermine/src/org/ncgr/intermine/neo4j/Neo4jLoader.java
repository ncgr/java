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
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;

import org.intermine.metadata.AttributeDescriptor;
import org.intermine.metadata.ClassDescriptor;
import org.intermine.metadata.CollectionDescriptor;
import org.intermine.metadata.ConstraintOp;
import org.intermine.metadata.ReferenceDescriptor;
import org.intermine.metadata.Model;
import org.intermine.pathquery.Constraints;
import org.intermine.pathquery.OrderDirection;
import org.intermine.pathquery.OuterJoinStatus;
import org.intermine.pathquery.PathConstraintAttribute;
import org.intermine.pathquery.PathQuery;
import org.intermine.webservice.client.core.ServiceFactory;
import org.intermine.webservice.client.services.QueryService;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Transaction;

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
        List<String> ignoredClasses = new ArrayList<String>();
        if (props.getProperty("ignored.classes")!=null) ignoredClasses = Arrays.asList(props.getProperty("ignored.classes").split(","));
        List<String> replacedClassPairs = new ArrayList<String>();
        if (props.getProperty("replaced.classes")!=null) replacedClassPairs = Arrays.asList(props.getProperty("replaced.classes").split(","));
        Map<String,String> replacedClasses = new HashMap<String,String>();
        for (String rcp : replacedClassPairs) {
            String[] pair = rcp.split("\\.");
            replacedClasses.put(pair[0], pair[1]);
            System.out.println("Will replace "+pair[0]+" with "+pair[1]+" during node MERGEs.");
        }
        List<String> ignoredReferences = new ArrayList<String>();
        if (props.getProperty("ignored.references")!=null) ignoredReferences = Arrays.asList(props.getProperty("ignored.references").split(","));
        List<String> ignoredCollections = new ArrayList<String>();
        if (props.getProperty("ignored.collections")!=null) ignoredCollections = Arrays.asList(props.getProperty("ignored.collections").split(","));

        List<String> specialPluralPairs = new ArrayList<String>();
        if (props.getProperty("special.plurals")!=null) specialPluralPairs = Arrays.asList(props.getProperty("special.plurals").split(","));
        Map<String,String> pluralSingulars = new HashMap<String,String>();
        for (String spp : specialPluralPairs) {
            String[] pair = spp.split("\\.");
            pluralSingulars.put(pair[0], pair[1]);
        }

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

        // Get the entire model's class descriptors and store them in a map so we can grab them by class name if we want
        Set<ClassDescriptor> classDescriptors = model.getClassDescriptors();
        Map<String,ClassDescriptor> classDescriptorMap = new HashMap<String,ClassDescriptor>();
        for (ClassDescriptor cd : classDescriptors) {
            classDescriptorMap.put(cd.getSimpleName(), cd);
        }
        
        // Store the IM IDs of nodes that have had their attributes stored
        List<Integer> nodesWithAttributesStored = new ArrayList<Integer>();

        // Loop over IM model and load the node, properties and relations with their corresponding reference and collections nodes (containing only id so far)
        for (ClassDescriptor thisDescriptor : classDescriptors) {
            String nodeClass = thisDescriptor.getSimpleName();
            if (!ignoredClasses.contains(nodeClass)) {

                // superclass is simply displayed for info purposes
                Set<String> superclassNames = new HashSet<String>();
                for (ClassDescriptor superclassDescriptor : thisDescriptor.getAllSuperDescriptors()) {
                    String superclassName = superclassDescriptor.getSimpleName();
                    if (!superclassName.equals(nodeClass)) superclassNames.add(superclassName);
                }
                System.out.println("--------------------------------------------------------");
                System.out.println(nodeClass+":"+superclassNames);
                
                // display the attributes
                Set<AttributeDescriptor> attrDescriptors = thisDescriptor.getAllAttributeDescriptors();
                if (attrDescriptors.size()>1) {
                    Set<String> attrNames = new HashSet<String>(); // just for output
                    for (AttributeDescriptor ad : attrDescriptors) {
                        attrNames.add(ad.getName());
                    }
                    System.out.println("Attributes:"+attrNames);
                }

                // load the references, except ignored classes and ignored references, into a map, and display
                HashMap<String,ReferenceDescriptor> refDescriptors = new HashMap<String,ReferenceDescriptor>();
                for (ReferenceDescriptor rd : thisDescriptor.getAllReferenceDescriptors()) {
                    String refName = rd.getName();
                    String refClass = rd.getReferencedClassDescriptor().getSimpleName();
                    String nodeDotRefName = nodeClass+"."+refName;
                    if (!ignoredClasses.contains(refClass) && !ignoredReferences.contains(nodeDotRefName)) refDescriptors.put(refName, rd);
                }
                if (refDescriptors.size()>0) System.out.println("References:"+refDescriptors.keySet());

                // get the collections, except ignored classes, into a map, and display
                HashMap<String,CollectionDescriptor> collDescriptors = new HashMap<String,CollectionDescriptor>();
                for (CollectionDescriptor cd : thisDescriptor.getAllCollectionDescriptors()) {
                    String collName = cd.getName();
                    String collClass = cd.getReferencedClassDescriptor().getSimpleName();
                    String nodeDotCollName = nodeClass+"."+collName;
                    if (!ignoredClasses.contains(collClass) && !ignoredCollections.contains(nodeDotCollName)) collDescriptors.put(collName, cd);
                }
                if (collDescriptors.size()>0) System.out.println("Collections:"+collDescriptors.keySet());
            
                // query nodes of this class
                nodeQuery.clearView();
                nodeQuery.addView(nodeClass+".id"); // every object has an IM id
                int nodeCount = 0;
                Iterator<List<Object>> rows = service.getRowListIterator(nodeQuery);
                while (rows.hasNext() && (maxRows==0 || nodeCount<maxRows)) {
                    nodeCount++;

                    Object[] row = rows.next().toArray();
                    int id = Integer.parseInt(row[0].toString());
                    if (verbose) System.out.print(nodeClass+":"+id+":");
			
                    // MERGE this node by its id
                    String nodeLabel = nodeClass;
                    if (replacedClasses.containsKey(nodeClass)) nodeLabel = replacedClasses.get(nodeClass);
                    String merge = "MERGE (n:"+nodeLabel+" {id:"+id+"})";
                    try (Session session = driver.session()) {
                        try (Transaction tx = session.beginTransaction()) {
                            tx.run(merge);
                            tx.success();
                        }
                    }

                    // SET this nodes attributes if not already stored
                    if (!nodesWithAttributesStored.contains(id)) {
                        populateIdClassAttributes(service, driver, attrQuery, id, nodeLabel, thisDescriptor);
                        nodesWithAttributesStored.add(id);
                    }

                    // CREATE INDEX on this node type
                    if (nodeCount==1) {
                        try (Session session = driver.session()) {
                            try (Transaction tx = session.beginTransaction()) {
                                tx.run("CREATE INDEX ON :"+nodeLabel+"(id)");
                                tx.success();
                            }
                        }
                    }

                    // MERGE this node's references by id
                    if (refDescriptors.size()>0) {
                        // store id, descriptor in a map for further use
                        HashMap<Integer,ReferenceDescriptor> refIdDescriptorMap = new HashMap<Integer,ReferenceDescriptor>();
                        refQuery.clearView();
                        refQuery.clearConstraints();
                        refQuery.clearOuterJoinStatus();
                        refQuery.addView(nodeClass+".id");
                        for (String refName : refDescriptors.keySet()) {
                            refQuery.addView(nodeClass+"."+refName+".id");
                            refQuery.setOuterJoinStatus(nodeClass+"."+refName, OuterJoinStatus.OUTER);
                        }
                        refQuery.addConstraint(new PathConstraintAttribute(nodeClass+".id", ConstraintOp.EQUALS, String.valueOf(id)));
                        Iterator<List<Object>> rs = service.getRowListIterator(refQuery);
                        while (rs.hasNext()) {
                            Object[] r = rs.next().toArray();
                            int j = 0;
                            int idn = Integer.parseInt(r[j++].toString()); // node id
                            for (String refName : refDescriptors.keySet()) {
                                ReferenceDescriptor rd = refDescriptors.get(refName);
                                String refClass = rd.getReferencedClassDescriptor().getSimpleName();
                                String refLabel = refClass;
                                if (replacedClasses.containsKey(refClass)) refLabel = replacedClasses.get(refClass);
                                String idrString = r[j++].toString(); // ref id
                                if (!idrString.equals("null")) {
                                    int idr = Integer.parseInt(idrString);
                                    refIdDescriptorMap.put(idr, rd);
                                    // merge this reference node
                                    merge = "MERGE (n:"+refLabel+" {id:"+idr+"})";
                                    try (Session session = driver.session()) {
                                        try (Transaction tx = session.beginTransaction()) {
                                            tx.run(merge);
                                            tx.success();
                                        }
                                    }
                                    // set this reference node's attributes
                                    if (!nodesWithAttributesStored.contains(idr)) {
                                        populateIdClassAttributes(service, driver, attrQuery, idr, refLabel, rd.getReferencedClassDescriptor());
                                        nodesWithAttributesStored.add(idr);
                                    }
                                    // merge this node-->ref relationship
                                    String match = "MATCH (n:"+nodeLabel+" {id:"+idn+"}),(r:"+refLabel+" {id:"+idr+"}) MERGE (n)-[:"+refName+"]->(r)";
                                    try (Session session = driver.session()) {
                                        try (Transaction tx = session.beginTransaction()) {
                                            tx.run(match);
                                            tx.success();
                                        }
                                    }
                                    if (verbose) System.out.print("r");
                                }
                            }
                        }
                    }
			
                    // MERGE this node's collections only with id, one at a time
                    if (collDescriptors.size()>0) {
                        // store id, class in a map for further use
                        for (String collName : collDescriptors.keySet()) {
                            CollectionDescriptor cd = collDescriptors.get(collName);
                            String collClass = cd.getReferencedClassDescriptor().getSimpleName();
                            String collLabel = collClass;
                            if (replacedClasses.containsKey(collClass)) collLabel = replacedClasses.get(collClass);
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
                                // merge this collections node
                                merge = "MERGE (n:"+collLabel+" {id:"+idc+"})";
                                try (Session session = driver.session()) {
                                    try (Transaction tx = session.beginTransaction()) {
                                        tx.run(merge);
                                        tx.success();
                                    }
                                }
                                // set this collection node's attributes
                                if (!nodesWithAttributesStored.contains(idc)) {
                                    populateIdClassAttributes(service, driver, attrQuery, idc, collLabel, cd.getReferencedClassDescriptor());
                                    nodesWithAttributesStored.add(idc);
                                }
                                // merge this node-->collections relationship, making collName have singular rather than plural form (so they match with refs)
                                String collNameSingular = collName;
                                if (pluralSingulars.containsKey(collName)) {
                                    collNameSingular = pluralSingulars.get(collName);
                                } else if (collName.endsWith("s")) {
                                    collNameSingular = collName.substring(0,collName.length()-1);
                                }
                                String match = "MATCH (n:"+nodeLabel+" {id:"+idn+"}),(c:"+collLabel+" {id:"+idc+"}) MERGE (n)-[:"+collNameSingular+"]->(c)";
                                try (Session session = driver.session()) {
                                    try (Transaction tx = session.beginTransaction()) {
                                        tx.run(match);
                                        tx.success();
                                    }
                                }
                                if (verbose) System.out.print("c");
                            }
                        }
                    }
                    
                    if (verbose) System.out.println("");
                    
                }
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
            attrQuery.clearOuterJoinStatus();
            for (AttributeDescriptor ad : attrDescriptors) {
                String attrName = ad.getName();
                if (!attrName.equals("id")) attrQuery.addView(className+"."+attrName);
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
                    if (!attrName.equals("id")) {
                        String val = escapeForNeo4j(row[i++].toString());
                        if (!val.equals("null")) {
                            terms++;
                            if (terms>1) match += ",";
                            if (attrType.equals("java.lang.String") || attrType.equals("org.intermine.objectstore.query.ClobAccess")) {
                                match += "n."+attrName+"=\""+val+"\"";
                            } else {
                                match += "n."+attrName+"="+val;
                            }
                        }
                    }
                }
                if (terms>0) {
                    try (Session session = driver.session()) {
                        try (Transaction tx = session.beginTransaction()) {
                            tx.run(match);
                            tx.success();
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
            else if (c < 32 || c >= 127)
                builder.append(String.format("\\u%04x", (int)c));
            else
                builder.append(c);
        }
        return builder.toString();
    }

}
