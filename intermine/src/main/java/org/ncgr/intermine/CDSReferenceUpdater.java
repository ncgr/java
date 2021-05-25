package org.ncgr.intermine;

import org.intermine.sql.Database;
import org.intermine.sql.DatabaseFactory;

import java.util.Map;
import java.util.HashMap;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Update gene, transcript, and protein references for CDSes.
 *
 * @author Sam Hokin
 */
public class CDSReferenceUpdater {

    /**
     * Do the work. No args.
     */
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Database database = DatabaseFactory.getDatabase("db.production");
        Connection conn = database.getConnection();
        Statement stmt = conn.createStatement();
        // gene refs
        ResultSet rs = stmt.executeQuery("SELECT cds.id AS cdsid, gene.id AS geneid FROM cds,gene WHERE cds.geneid IS NULL AND cds.primaryidentifier LIKE gene.primaryidentifier||'.%'");
        Map<Integer,Integer> cdsGenes = new HashMap<>();
        while (rs.next()) {
            int cdsid = rs.getInt("cdsid");
            int geneid = rs.getInt("geneid");
            cdsGenes.put(cdsid, geneid);
        }
        rs.close();
        // update CDS.gene
        int count = 0;
        System.err.println("Updating CDS.gene references...");
        for (int cdsid : cdsGenes.keySet()) {
            int geneid = cdsGenes.get(cdsid);
            String update = "UPDATE cds SET geneid="+geneid+" WHERE id="+cdsid;
            stmt.executeUpdate(update);
            count++;
        }
        System.err.println("Updated "+count+" CDS records.");
        // transcript refs
        rs = stmt.executeQuery("SELECT cds.id AS cdsid, transcript.id AS transcriptid FROM cds,transcript WHERE cds.transcriptid IS NULL AND cds.primaryidentifier=transcript.primaryidentifier");
        Map<Integer,Integer> cdsTranscripts = new HashMap<>();
        while (rs.next()) {
            int cdsid = rs.getInt("cdsid");
            int transcriptid = rs.getInt("transcriptid");
            cdsTranscripts.put(cdsid, transcriptid);
        }
        rs.close();
        // update CDS.transcript
        count = 0;
        System.err.println("Updating CDS.transcript references...");
        for (int cdsid : cdsTranscripts.keySet()) {
            int transcriptid = cdsTranscripts.get(cdsid);
            String update = "UPDATE cds SET transcriptid="+transcriptid+" WHERE id="+cdsid;
            stmt.executeUpdate(update);
            count++;
        }
        System.err.println("Updated "+count+" CDS records.");
        // protein refs
        rs = stmt.executeQuery("SELECT cds.id AS cdsid, protein.id AS proteinid FROM cds,protein WHERE cds.proteinid IS NULL AND cds.primaryidentifier=protein.primaryidentifier");
        Map<Integer,Integer> cdsProteins = new HashMap<>();
        while (rs.next()) {
            int cdsid = rs.getInt("cdsid");
            int proteinid = rs.getInt("proteinid");
            cdsProteins.put(cdsid, proteinid);
        }
        rs.close();
        // update CDS.protein
        count = 0;
        System.err.println("Updating CDS.protein references...");
        for (int cdsid : cdsProteins.keySet()) {
            int proteinid = cdsProteins.get(cdsid);
            String update = "UPDATE cds SET proteinid="+proteinid+" WHERE id="+cdsid;
            stmt.executeUpdate(update);
            count++;
        }
        System.err.println("Updated "+count+" CDS records.");
        // close out
        stmt.close();
        conn.close();
    }
}

