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
 * Query an InterMine model, and load a SINGLE object, referenced by its InterMine id, along with its attributes, ALL references and ALL collections into a Neo4j database.
 *
 * Connection properties are given in neo4jloader.properties.
 * 
 * @author Sam Hokin
 */
public class Neo4jNodeLoader {

    static final String PROPERTIES_FILE = "neo4jloader.properties";

    /**
     * @param args command line arguments
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        // args
        if (args.length!=2) {
            System.out.println("Usage: Neo4jNodeLoader <IM class name> <IM id>");
            System.exit(0);
        }
        String nodeClass = args[0];
        int id = Integer.parseInt(args[1]);

        // Load parameters from neo4jloader.properties
        Properties props = new Properties();
        props.load(new FileInputStream(PROPERTIES_FILE));
        String intermineServiceUrl = props.getProperty("intermine.service.url");
        String neo4jUrl = props.getProperty("neo4j.url");
        String neo4jUser = props.getProperty("neo4j.user");
        String neo4jPassword = props.getProperty("neo4j.password");
        List<String> ignoredClasses = new ArrayList<String>();
        if (props.getProperty("ignored.classes")!=null) ignoredClasses = Arrays.asList(props.getProperty("ignored.classes").split(","));
        List<String> replacedClassPairs = new ArrayList<String>();
        if (props.getProperty("replaced.classes")!=null) replacedClassPairs = Arrays.asList(props.getProperty("replaced.classes").split(","));
        Map<String,String> replacedClasses = new HashMap<String,String>();
        for (String rc : replacedClassPairs) {
            String[] pair = rc.split("\\.");
            replacedClasses.put(pair[0], pair[1]);
            System.out.println("Will replace "+pair[0]+" with "+pair[1]+" during node MERGEs.");
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

        // Get the descriptor for this node
        ClassDescriptor nodeDescriptor = model.getClassDescriptorByName(nodeClass);
        
        // display the attributes
        Set<AttributeDescriptor> attrDescriptors = nodeDescriptor.getAllAttributeDescriptors();
        if (attrDescriptors.size()>1) {
            Set<String> attrNames = new HashSet<String>(); // just for output
            for (AttributeDescriptor ad : attrDescriptors) {
                attrNames.add(ad.getName());
            }
            System.out.println("Attributes:"+attrNames);
        }

        // load the references, except ignored classes, into a map, and display
        HashMap<String,ReferenceDescriptor> refDescriptors = new HashMap<String,ReferenceDescriptor>();
        for (ReferenceDescriptor rd : nodeDescriptor.getAllReferenceDescriptors()) {
            String refName = rd.getName();
            String refClass = rd.getReferencedClassDescriptor().getSimpleName();
            if (!ignoredClasses.contains(refClass)) refDescriptors.put(refName, rd);
        }
        if (refDescriptors.size()>0) System.out.println("References:"+refDescriptors.keySet());

        // get the collections, except ignored classes, into a map, and display
        HashMap<String,CollectionDescriptor> collDescriptors = new HashMap<String,CollectionDescriptor>();
        for (CollectionDescriptor cd : nodeDescriptor.getAllCollectionDescriptors()) {
            String collName = cd.getName();
            String collClass = cd.getReferencedClassDescriptor().getSimpleName();
            if (!ignoredClasses.contains(collClass)) collDescriptors.put(collName, cd);
        }
        if (collDescriptors.size()>0) System.out.println("Collections:"+collDescriptors.keySet());

        // query this node (so we're sure it exists in the mine)
        nodeQuery.addView(nodeClass+".id"); // every object has an IM id
        nodeQuery.addOrderBy(nodeClass+".id", OrderDirection.ASC);
        nodeQuery.addConstraint(new PathConstraintAttribute(nodeClass+".id", ConstraintOp.EQUALS, String.valueOf(id)));
        Iterator<List<Object>> rows = service.getRowListIterator(nodeQuery);
        while (rows.hasNext()) {
            Object[] row = rows.next().toArray();
            System.out.print(nodeClass+":"+id+":");

            // MERGE this node by its id
            String nodeLabel = nodeClass;
            String merge = "MERGE (n:"+nodeLabel+" {id:"+id+"})";
            try (Session session = driver.session()) {
                try (Transaction tx = session.beginTransaction()) {
                    tx.run(merge);
                    tx.success();
                }
            }

            // SET this nodes attributes
            Neo4jLoader.populateIdClassAttributes(service, driver, attrQuery, id, nodeLabel, nodeDescriptor);

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
                        if (replacedClasses.containsKey(refClass)) refClass = replacedClasses.get(refClass);
                        String idrString = r[j++].toString(); // ref id
                        if (!idrString.equals("null")) {
                            int idr = Integer.parseInt(idrString);
                            refIdDescriptorMap.put(idr, rd);
                            // merge this reference node
                            merge = "MERGE (n:"+refClass+" {id:"+idr+"})";
                            try (Session session = driver.session()) {
                                try (Transaction tx = session.beginTransaction()) {
                                    tx.run(merge);
                                    tx.success();
                                }
                            }
                            // set this reference node's attributes
                            Neo4jLoader.populateIdClassAttributes(service, driver, attrQuery, idr, refClass, rd.getReferencedClassDescriptor());
                            // merge this node-->ref relationship
                            String match = "MATCH (n:"+nodeClass+" {id:"+idn+"}),(r:"+refClass+" {id:"+idr+"}) MERGE (n)-[:"+refName+"]->(r)";
                            try (Session session = driver.session()) {
                                try (Transaction tx = session.beginTransaction()) {
                                    tx.run(match);
                                    tx.success();
                                }
                            }
                            System.out.print("r");
                        }
                    }
                }
            }

            System.out.println("");

        }
        
        // Close connections
        driver.close();

    }

}
