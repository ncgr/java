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
 * Update gene and protein references for Transcripts.
 *
 * @author Sam Hokin
 */
public class TranscriptReferenceUpdater {

    /**
     * Do the work. No args.
     */
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Database database = DatabaseFactory.getDatabase("db.production");
        Connection conn = database.getConnection();
        Statement stmt = conn.createStatement();
        // gene refs
        ResultSet rs = stmt.executeQuery("SELECT transcript.id AS transcriptid, gene.id AS geneid FROM transcript,gene WHERE transcript.geneid IS NULL AND transcript.primaryidentifier LIKE gene.primaryidentifier||'.%'");
        Map<Integer,Integer> transcriptGenes = new HashMap<>();
        while (rs.next()) {
            int transcriptid = rs.getInt("transcriptid");
            int geneid = rs.getInt("geneid");
            transcriptGenes.put(transcriptid, geneid);
        }
        rs.close();
        // update Transcript.gene
        int count = 0;
        System.err.println("Updating Transcript.gene references...");
        for (int transcriptid : transcriptGenes.keySet()) {
            int geneid = transcriptGenes.get(transcriptid);
            String update = "UPDATE transcript SET geneid="+geneid+" WHERE id="+transcriptid;
            stmt.executeUpdate(update);
            count++;
        }
        System.err.println("Updated "+count+" Transcript records.");
        // protein refs
        rs = stmt.executeQuery("SELECT transcript.id AS transcriptid, protein.id AS proteinid FROM transcript,protein WHERE transcript.proteinid IS NULL AND transcript.primaryidentifier=protein.primaryidentifier");
        Map<Integer,Integer> transcriptProteins = new HashMap<>();
        while (rs.next()) {
            int transcriptid = rs.getInt("transcriptid");
            int proteinid = rs.getInt("proteinid");
            transcriptProteins.put(transcriptid, proteinid);
        }
        rs.close();
        // update Transcript.protein
        count = 0;
        System.err.println("Updating Transcript.protein references...");
        for (int transcriptid : transcriptProteins.keySet()) {
            int proteinid = transcriptProteins.get(transcriptid);
            String update = "UPDATE transcript SET proteinid="+proteinid+" WHERE id="+transcriptid;
            stmt.executeUpdate(update);
            count++;
        }
        System.err.println("Updated "+count+" Transcript records.");
        // close out
        stmt.close();
        conn.close();
    }
}

