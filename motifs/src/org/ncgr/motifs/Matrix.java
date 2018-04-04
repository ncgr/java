package org.ncgr.motifs;

import org.ncgr.db.DB;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Encapsulates a single MATRIX record from the Jaspar2018 database.
 */
public class Matrix {

    // retrieve these with the getters
    int id;
    String collection;
    String baseId;
    int version;
    String name;

    /**
     * Construct from a loaded ResultSet.
     */
    public Matrix(ResultSet rs) throws SQLException {
        populate(rs);
    }

    /**
     * Construct from a connected DB instance, given an id.
     */
    public Matrix(DB db, int id) throws SQLException {
        db.executeQuery("SELECT * FROM matrix WHERE id="+id);
        if (db.rs.next()) {
            populate(db.rs);
        }
    }

    /**
     * Instantiate local vars from a loaded ResultSet.
     */
    void populate(ResultSet rs) throws SQLException {
        id = rs.getInt("id");
        collection = rs.getString("collection");
        baseId = rs.getString("base_id");
        version = rs.getInt("version");
        name = rs.getString("name");
    }

    ///////////
    // getters
    ///////////
    
    public int getId() {
        return id;
    }
    public String getCollection() {
        return collection;
    }
    public String getBaseId() {
        return baseId;
    }
    public int getVersion() {
        return version;
    }
    public String getName() {
        return name;
    }

    //////////////////////
    // instance methods //
    //////////////////////

    /**
     * Return the length of the motif(s) associated with this instance, by making a DB connection.
     */
    public int getMotifLength() throws ClassNotFoundException, FileNotFoundException, IOException, SQLException {
        DB db = new DB();
        int len = getMotifLength(db);
        db.close();
        return len;
    }
    /**
     * Return the length of the motif(s) associated with this instance, given an instantiated DB object.
     */
    public int getMotifLength(DB db) throws SQLException {
        db.executeQuery("SELECT max(col) FROM matrix_data WHERE id="+id);
        db.rs.next();
        return db.rs.getInt("max");
    }

    /**
     * Return an array of matrix data associated with this instance, by making a DB connection.
     */
    public int[][] getData() throws ClassNotFoundException, FileNotFoundException, IOException, SQLException {
        DB db = new DB();
        int[][] matrixData = getData(db);
        db.close();
        return matrixData;
    }
    /**
     * Return an array of matrix data associated with this instance, given an instantiated DB object.
     */
    public int[][] getData(DB db) throws SQLException {
        int len = getMotifLength(db);
        // loop over columns
        int[][] vals = new int[len][4];
        for (int i=0; i<len; i++) {
            int col = i+1;
            // query over rows A, C, G, T
            int rowId = 0;
            db.executeQuery("SELECT * FROM matrix_data WHERE id="+id+" AND col="+col+" ORDER BY row");
            while (db.rs.next()) {
                int val = db.rs.getInt("val");
                vals[i][rowId++] = val;
            }
        }
        return vals;
    }

    /**
     * Return a map of annotation associated with this instance, by making a DB connection.
     */
    public Map<String,String> getAnnotation() throws ClassNotFoundException, FileNotFoundException, IOException, SQLException {
        DB db = new DB();
        Map<String,String> annotation = getAnnotation(db);
        db.close();
        return annotation;
    }
    /**
     * Return a map of annotation associated with this instance, given an instantiated DB object.
     */
    public Map<String,String> getAnnotation(DB db) throws SQLException {
        Map<String,String> annotation = new HashMap<String,String>();
        db.executeQuery("SELECT * FROM matrix_annotation WHERE id="+id+" ORDER BY tag");
        while (db.rs.next()) {
            annotation.put(db.rs.getString("tag"), db.rs.getString("val"));
        }
        return annotation;
    }

    /**
     * Return the protein accession IDs associated with this instance, by making a DB connection. Returns null if none found.
     */
    public List<String> getProteins() throws ClassNotFoundException, FileNotFoundException, IOException, SQLException {
        DB db = new DB();
        List<String> acc = getProteins(db);
        db.close();
        return acc;
    }
    /**
     * Return the protein accession IDs associated with this instance, given an instantiated DB object. Returns null if none found.
     */
    public List<String> getProteins(DB db) throws SQLException {
        List<String> acc = new ArrayList<String>();
        db.executeQuery("SELECT * FROM matrix_protein WHERE id="+id);
        while (db.rs.next()) {
            acc.add(db.rs.getString("acc"));
        }
        return acc;
    }

    ////////////////////
    // static methods //
    ////////////////////

    /**
     * Return a list of ALL matrix records by querying the database.
     */
    public static List<Matrix> getAll() throws ClassNotFoundException, FileNotFoundException, IOException, SQLException {
        List<Matrix> matrices = new ArrayList<Matrix>();
        DB db = new DB();
        db.executeQuery("SELECT * FROM matrix ORDER BY id");
        while (db.rs.next()) {
            matrices.add(new Matrix(db.rs));
        }
        db.close();
        return matrices;
    }

}
