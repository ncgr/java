package org.ncgr.motifs;

import org.ncgr.db.DB;

import org.json.JSONObject;

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

    // MATRIX table values
    int id;
    String collection;
    String baseId;
    int version;
    String name;

    // utility values
    int motifLength;

    /**
     * Construct from a loaded ResultSet. This DOES NOT SET motifLength.
     */
    public Matrix(ResultSet rs) throws SQLException {
        populate(rs);
    }

    /**
     * Construct from a connected DB instance, given an id. This DOES SET motifLength.
     */
    public Matrix(DB db, int id) throws SQLException {
        db.executeQuery("SELECT * FROM matrix WHERE id="+id);
        if (db.rs.next()) {
            populate(db.rs);
        }
        db.executeQuery("SELECT max(col) FROM matrix_data WHERE id="+id);
        if (db.rs.next()) {
            setMotifLength(db.rs.getInt("max"));
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

    /////////////
    // getters //
    /////////////
    
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

    public void setMotifLength(int len) {
        motifLength = len;
    }
    public int getMotifLength() {
        return motifLength;
    }

    //////////////////////
    // instance methods //
    //////////////////////

    /**
     * Return an array of matrix data associated with this instance, given an instantiated DB object.
     */
    public int[][] getData(DB db) throws SQLException {
        // loop over columns
        int[][] vals = new int[motifLength][4];
        for (int i=0; i<motifLength; i++) {
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

    /**
     * Return this instance as a JSON object.
     */
    public JSONObject getJSON() {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("collection", collection);
        json.put("baseId", baseId);
        json.put("version", version);
        json.put("name", name);
        json.put("motifLength", motifLength);
        return json;
    }

    ////////////////////
    // static methods //
    ////////////////////

    /**
     * Return a list of ALL matrix records by querying the database, given an instantiated DB object.
     */
    public static List<Matrix> getAll(DB db) throws SQLException {
        List<Matrix> matrices = new ArrayList<Matrix>();
        db.executeQuery("SELECT * FROM matrix ORDER BY id");
        while (db.rs.next()) {
            matrices.add(new Matrix(db.rs));
        }
        // query and set motifLength for each matrix
        for (Matrix m : matrices) {
            db.executeQuery("SELECT max(col) FROM matrix_data WHERE id="+m.getId());
            if (db.rs.next()) {
                m.setMotifLength(db.rs.getInt("max"));
            }
        }
        return matrices;
    }

}
