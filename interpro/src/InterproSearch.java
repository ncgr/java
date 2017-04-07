package org.ncgr.interpro;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * A command-line tool to search for PFAM or GENE3D accessions in the Interpro database.
 */
public class InterproSearch {

    public static void main(String[] args) {

        if (args.length!=1) {
            System.err.println("Usage InterproSearch <XML file>");
            System.exit(1);
        }

        File xmlFile = new File(args[0]);

        System.out.print("Instantiating InterproReader...");
        InterproReader reader = new InterproReader();
        System.out.println("done.");

        if (xmlFile.canRead()) {
            System.out.print("Reading "+xmlFile.getName()+"; length="+xmlFile.length()+"...");
            reader.read(xmlFile);
            System.out.println("done.");
        } else {
            System.err.println("Can't read "+xmlFile.getName()+"; exiting.");
            System.exit(1);
        }

        // the maps
        Map<String,DBInfo> dbInfoMap = reader.getDBInfoMap();
        Map<String,Interpro> interproMap = reader.getInterproMap();
        Set<String> delRefSet = reader.getDelRefSet();

        // build the PFAM and Gene3d maps
        System.out.print("Building PFAM and Gene3d maps...");
        Map<String,Interpro> pfamMap = new HashMap<String,Interpro>();
        Map<String,Interpro> gene3dMap = new HashMap<String,Interpro>();
        Map<String,String> pfamNameMap = new HashMap<String,String>();
        for (Interpro interpro : interproMap.values()) {
            List<DbXref> members = interpro.memberList.getEntries();
            for (DbXref member : members) {
                if (member.db.equals("PFAM")) {
                    pfamMap.put(member.dbkey, interpro);
                    pfamNameMap.put(member.dbkey, member.name);
                }
                if (member.db.equals("GENE3D")) {
                    gene3dMap.put(member.dbkey, interpro);
                }
            }
        }
        System.out.println("done.");

        // now loop on queries
        Scanner scanner = new Scanner(System.in);
        boolean terminated = false;
        while (!terminated) {
            System.out.println("=================================================================================================");
            System.out.print("Enter a PFAM or GENE3D ID (^C to quit): ");
            String id = scanner.next();
            terminated = (id.length()==0);
            if (!terminated) {
                if (id.startsWith("PF")) {
                    Interpro interpro = pfamMap.get(id);
                    if (interpro!=null) {
                        System.out.println("PFAM Name="+pfamNameMap.get(id));
                        System.out.println(interpro.toString());
                    }
                } else if (id.startsWith("G3D")) {
                    Interpro interpro = gene3dMap.get(id);
                    if (interpro!=null) System.out.println(interpro.toString());
                }
            }
        }

    }

}
