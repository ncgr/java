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
 * Query an InterMine model, and load it and its data into a Neo4j database, along with all the relations and collections.
 * Connection and other properties are given in neo4jloader.properties.
 * 
 * @author Sam Hokin
 */
public class Neo4jLoader {

    /**
     * @param args command line arguments
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        // Load parameters from neo4jloader.properties
        Properties props = new Properties();
        props.load(new FileInputStream("neo4jloader.properties"));
        String intermineServiceUrl = props.getProperty("intermine.service.url");
        String neo4jUrl = props.getProperty("neo4j.url");
        String neo4jUser = props.getProperty("neo4j.user");
        String neo4jPassword = props.getProperty("neo4j.password");
        boolean verbose = Boolean.parseBoolean(props.getProperty("verbose"));
        int maxRows = Integer.parseInt(props.getProperty("max.rows"));
        List<String> ignoredClasses = Arrays.asList(props.getProperty("ignored.classes").split(","));
        
        // InterMine setup
        ServiceFactory factory = new ServiceFactory(intermineServiceUrl);
        Model model = factory.getModel();
        QueryService service = factory.getQueryService();
        PathQuery nodeQuery = new PathQuery(model);
        PathQuery refQuery = new PathQuery(model);
        PathQuery collQuery = new PathQuery(model);
        
        // Neo4j setup
        Driver driver = GraphDatabase.driver(neo4jUrl, AuthTokens.basic(neo4jUser, neo4jPassword));

        // Get the entire model's class descriptors
        Set<ClassDescriptor> classDescriptors = model.getClassDescriptors();
        
        // Loop over IM model to create Neo4j indexes on mine ID to hopefully speed up matches and merges as we load.
        // NOTE: this could end up being counter-productive, but an index on a simple ID seems worth trying!
        // NOTE: this will wind up creating types that may not have any data in the particular mine. That seems OK, and it lets you know they're in the model but not populated.
        for (ClassDescriptor cd : classDescriptors) {
            String simpleName = cd.getSimpleName();
            if (!ignoredClasses.contains(simpleName)) {
                try (Session session = driver.session()) {
                    try (Transaction tx = session.beginTransaction()) {
                        tx.run("CREATE INDEX ON :"+simpleName+"(id)");
                        tx.success();
                    }
                }
            }
        }

        // Loop over IM model and load the node, properties and relations with their corresponding shell nodes (containing only id)
        for (ClassDescriptor cd : classDescriptors) {
            String simpleName = cd.getSimpleName();
            if (!ignoredClasses.contains(simpleName)) {

                Set<String> superclassNames = new HashSet<String>();
                for (ClassDescriptor superclassDescriptor : cd.getAllSuperDescriptors()) {
                    String superclassName = superclassDescriptor.getSimpleName();
                    if (!superclassName.equals(simpleName)) superclassNames.add(superclassName);
                }
                System.out.println("--------------------------------------------------------");
                System.out.println(simpleName+":"+superclassNames);
                
                Set<AttributeDescriptor> attrDescriptors = cd.getAllAttributeDescriptors();
                Set<String> attrNames = new HashSet<String>();
                for (AttributeDescriptor attrDescriptor : attrDescriptors) {
                    String attr = attrDescriptor.getName();
                    if (!attr.equals("id")) attrNames.add(attr);
                }
                if (attrNames.size()>0) System.out.println("Attributes:"+attrNames);

                Set<ReferenceDescriptor> refDescriptors = cd.getAllReferenceDescriptors();
                HashMap<String,String> refMap = new HashMap<String,String>();
                for (ReferenceDescriptor refDescriptor : refDescriptors) {
                    String refName = refDescriptor.getName();
                    String refClass = refDescriptor.getReferencedClassDescriptor().getSimpleName();
                    if (!ignoredClasses.contains(refClass)) {
                        refMap.put(refName, refClass);
                    }
                }
                if (refMap.size()>0) System.out.println("References:"+refMap.keySet());

                Set<CollectionDescriptor> collDescriptors = cd.getAllCollectionDescriptors();
                HashMap<String,String> collMap = new HashMap<String,String>();
                for (CollectionDescriptor collDescriptor : collDescriptors) {
                    String collName = collDescriptor.getName();
                    String collClass = collDescriptor.getReferencedClassDescriptor().getSimpleName();
                    if (!ignoredClasses.contains(collClass)) {
                        collMap.put(collName, collClass);
                    }
                }
                if (collMap.size()>0) System.out.println("Collections:"+collMap.keySet());

            
                // Query nodes of this type
                nodeQuery.clearView();
                nodeQuery.addView(simpleName+".id"); // every object has an IM id
                for (String attr : attrNames) {
                    nodeQuery.addView(simpleName+"."+attr);
                }
                int nodeCount = 0;
                Iterator<List<Object>> rows = service.getRowListIterator(nodeQuery);
                while (rows.hasNext() && (maxRows==0 || nodeCount<maxRows)) {
			
                    nodeCount++;

                    // wrap this puppy in a try block since IM can crash on bad JSON at times
                    try {
			
			Object[] row = rows.next().toArray();
			int i = 0;
			String id = row[i++].toString(); // all IM objects have an id, which we'll use to key nodes
			if (verbose) System.out.print(simpleName+":"+id);
			
			// MERGE this node
			HashMap<String,String> attrMap = new HashMap<String,String>();
			for (String attr : attrNames) {
			    attrMap.put(attr, escapeForNeo4j(row[i++].toString()));
			}
			String merge = "MERGE (n:"+simpleName+" {id:"+id+"})";
			try (Session session = driver.session()) {  // low cost
			    try (Transaction tx = session.beginTransaction()) {
				tx.run(merge);
				tx.success();
			    }
			}
			
			// SET this nodes attributes
			if (attrMap.size()>0) {
			    String match = "MATCH (n:"+simpleName+" {id:"+id+"}) SET ";
			    int terms = 0;
			    for (String attr : attrMap.keySet()) {
				String val = attrMap.get(attr);
				if (!val.equals("null")) {
				    terms++;
				    if (terms>1) match += ",";
				    match += "n."+attr+"=\""+val+"\"";
				}
			    }
			    if (terms>0) {
				try (Session session = driver.session()) {  // low cost
				    try (Transaction tx = session.beginTransaction()) {
					tx.run(match);
					tx.success();
				    }
				}
			    }
			}
			
			// MERGE this node's references only with id
			if (refMap.size()>0) {
			    refQuery.clearView();
			    refQuery.clearConstraints();
			    refQuery.clearOuterJoinStatus();
			    refQuery.addView(simpleName+".id");
			    for (String refName : refMap.keySet()) {
				String refClass = refMap.get(refName);
				refQuery.addView(simpleName+"."+refName+".id");
				refQuery.setOuterJoinStatus(simpleName+"."+refName, OuterJoinStatus.OUTER);
			    }
			    refQuery.addConstraint(new PathConstraintAttribute(simpleName+".id", ConstraintOp.EQUALS, id));
			    Iterator<List<Object>> rs = service.getRowListIterator(refQuery);
			    while (rs.hasNext()) {
				Object[] r = rs.next().toArray();
				int j = 0;
				String idn = r[j++].toString(); // node id
				for (String refName : refMap.keySet()) {
				    String refClass = refMap.get(refName);
				    String idr = r[j++].toString(); // ref id
				    if (!idr.equals("null")) {
					merge = "MERGE (n:"+refClass+" {id:"+idr+"})";
					try (Session session = driver.session()) {
					    try (Transaction tx = session.beginTransaction()) {
						tx.run(merge);
						tx.success();
					    }
					}
					String match = "MATCH (n:"+simpleName+" {id:"+idn+"}),(r:"+refClass+" {id:"+idr+"}) MERGE (n)-[:"+refName+"]->(r)";
					try (Session session = driver.session()) {
					    try (Transaction tx = session.beginTransaction()) {
						tx.run(match);
						tx.success();
					    }
					}
					if (verbose) System.out.print(".");
				    }
				}
			    }
			}
			
			// MERGE this node's collections only with id, one at a time
			if (collMap.size()>0) {
			    for (String collName : collMap.keySet()) {
				String collClass = collMap.get(collName);
				collQuery.clearView();
				collQuery.clearConstraints();
				collQuery.addView(simpleName+".id");
				collQuery.addView(simpleName+"."+collName+".id");
				collQuery.addConstraint(new PathConstraintAttribute(simpleName+".id", ConstraintOp.EQUALS, id));
				Iterator<List<Object>> rs = service.getRowListIterator(collQuery);
				int collCount = 0;
				while (rs.hasNext() && (maxRows==0 || collCount<maxRows)) {
				    collCount++;
				    Object[] r = rs.next().toArray();
				    String idn = r[0].toString(); // node id
				    String idc = r[1].toString(); // collection id
				    merge = "MERGE (n:"+collClass+" {id:"+idc+"})";
				    try (Session session = driver.session()) {
					try (Transaction tx = session.beginTransaction()) {
					    tx.run(merge);
					    tx.success();
					}
				    }
				    String match = "MATCH (n:"+simpleName+" {id:"+idn+"}),(c:"+collClass+" {id:"+idc+"}) MERGE (n)-[:"+collName+"]->(c)";
				    try (Session session = driver.session()) {
					try (Transaction tx = session.beginTransaction()) {
					    tx.run(match);
					    tx.success();
					}
				    }
				    if (verbose) System.out.print(".");
				}
			    }
			}
			if (verbose) System.out.println("");
		    } catch (Exception ex) {
                        // don't stop, just inform
                        System.err.println(ex);
                    }
                }

            }
        }
        
        // Close connections
        driver.close();

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
