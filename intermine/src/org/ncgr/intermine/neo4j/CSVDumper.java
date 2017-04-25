package org.ncgr.intermine.neo4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

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

/**
 * Query an InterMine model, and dump its nodes into CSV files for loading into a bare Neo4j database.
 * Connection and other properties are given in neo4jloader.properties.
 * 
 * @author Sam Hokin
 */
public class CSVDumper {

    static final String PROPERTIES_FILE = "neo4jloader.properties";

    /**
     * @param args command line arguments
     */
    public static void main(String[] args) throws IOException, FileNotFoundException {

        // Load parameters from neo4jloader.properties
        Properties props = new Properties();
        props.load(new FileInputStream(PROPERTIES_FILE));
        String intermineServiceUrl = props.getProperty("intermine.service.url");
        int maxRows = Integer.parseInt(props.getProperty("max.rows"));
        List<String> ignoredClasses = new ArrayList<String>();
        if (props.getProperty("ignored.classes")!=null) ignoredClasses = Arrays.asList(props.getProperty("ignored.classes").split(","));

        // InterMine setup
        ServiceFactory factory = new ServiceFactory(intermineServiceUrl);
        Model model = factory.getModel();
        QueryService service = factory.getQueryService();
        PathQuery nodeQuery = new PathQuery(model);
        PathQuery attrQuery = new PathQuery(model);
        
        // Get the entire model's class descriptors and store them in a map so we can grab them by class name if we want
        Set<ClassDescriptor> classDescriptors = model.getClassDescriptors();
        Map<String,ClassDescriptor> classDescriptorMap = new HashMap<String,ClassDescriptor>();
        for (ClassDescriptor cd : classDescriptors) {
            classDescriptorMap.put(cd.getSimpleName(), cd);
        }
        
        // Loop over IM model and dump the nodes
        for (ClassDescriptor thisDescriptor : classDescriptors) {
            String nodeClass = thisDescriptor.getSimpleName();
            if (!ignoredClasses.contains(nodeClass)) {
                String nodeName = nodeClass;

                // get this node's attribute descriptors
                Set<AttributeDescriptor> attrDescriptors = thisDescriptor.getAllAttributeDescriptors();
                
                // open this node's file for writing
                String filename = "/tmp/"+nodeClass+".csv";
                PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
                System.out.print(filename);

                // write the header
                String header = "id:ID";
                for (AttributeDescriptor ad : attrDescriptors) {
                    String attrName = ad.getName();
                    String attrType = ad.getType();
                    if (!attrName.equals("id")) {
                        if (attrType.equals("java.lang.String")) {
                            header += ","+attrName;
                        } else if (attrType.equals("java.lang.Integer")) {
                            header += ","+attrName+":int";
                        } else if (attrType.equals("java.lang.Double")) {
                            header += ","+attrName+":double";
                        } else if (attrType.equals("java.lang.Boolean")) {
                            header += ","+attrName+":boolean";
                        } else {
                            header += ","+attrName;
                        }
                    }
                }
                header += ",:LABEL";
                writer.println(header);

                // build the PathQuery for this node
                nodeQuery.clearView();
                nodeQuery.addView(nodeClass+".id");
                for (AttributeDescriptor ad : attrDescriptors) {
                    String attrName = ad.getName();
                    if (!attrName.equals("id")) nodeQuery.addView(nodeClass+"."+attrName);
                }
                
                int rowCount = 0;
                Iterator<List<Object>> rows = service.getRowListIterator(nodeQuery);
                while (rows.hasNext() && (maxRows==0 || rowCount<maxRows)) {
                    rowCount++;
                    
                    Object[] row = rows.next().toArray();
                    int i = 0;
                    String id = row[i++].toString();
                    String line = id;
                    for (AttributeDescriptor ad : attrDescriptors) {
                        String attrName = ad.getName();
                        String attrType = ad.getType();
                        if (!attrName.equals("id")) {
                            line += ",";
                            String val = row[i++].toString();
                            if (!val.equals("null")) {
                                if (attrType.equals("java.lang.String")) {
                                    line += "\""+Neo4jLoader.escapeForNeo4j(val)+"\"";
                                } else {
                                    line += val;
                                }
                            }
                        }
                    }
                    line += "," + nodeName;
                    writer.println(line);
                }
                writer.close();
                if (rowCount>0) {
                    System.out.println("\t"+rowCount);
                } else {
                    File file = new File(filename);
                    if (file.delete()) {
                        System.out.println("\tNO RECORDS");
                    } else {
                        System.out.println("\t0 - error deleting file.");
                    }
                }

            }
        }

    }

}
