package org.ncgr.pubmed_morphia_example;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;
import org.mongodb.morphia.annotations.Property;
import org.mongodb.morphia.annotations.Reference;

import org.bson.types.ObjectId;
    
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Encapsulates a PubMed author in an object that gets stored in MongoDB using Morphia.
 */
@Entity("Authors")
@Indexes(@Index(value = "name", fields = @Field("name")))
public class AuthorObject {

    // eSummaryResult/DocSum fields
    @Id
    private ObjectId id;
    private String name; 

    /**
     * Default constructor, used by Morphia to instantiate. Somehow. Since there are no setters. Magic.
     */
    public AuthorObject() {
    }

    /**
     * Instantiate from a PubMed author full name string.
     */
    public AuthorObject(String name) {
        this.id = new ObjectId();
        this.name = name;
    }

    // getters and setters
    public ObjectId getId() {
        return id;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    

    // parse the last name out of the full name, e.g. "St Martin ADF"
    public String getLastName() {
        String[] parts = name.split(" ");
        String lastName = "";
        for (int i=0; i<parts.length-1; i++) {
            lastName += parts[i];
        }
        return lastName;
    }

    // parse the initials out of the full name e.g. "St Martin ADF"
    public String getInitials() {
        String[] parts = name.split(" ");
        return parts[parts.length-1];
    }

        
    

}
