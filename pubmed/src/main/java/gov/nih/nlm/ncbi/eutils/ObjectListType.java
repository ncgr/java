/*
 * XML Type:  ObjectListType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.ObjectListType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML ObjectListType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public interface ObjectListType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.ObjectListType> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "objectlisttype39d6type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets a List of "Object" elements
     */
    java.util.List<gov.nih.nlm.ncbi.eutils.ObjectType> getObjectList();

    /**
     * Gets array of all "Object" elements
     */
    gov.nih.nlm.ncbi.eutils.ObjectType[] getObjectArray();

    /**
     * Gets ith "Object" element
     */
    gov.nih.nlm.ncbi.eutils.ObjectType getObjectArray(int i);

    /**
     * Returns number of "Object" element
     */
    int sizeOfObjectArray();

    /**
     * Sets array of all "Object" element
     */
    void setObjectArray(gov.nih.nlm.ncbi.eutils.ObjectType[] objectArray);

    /**
     * Sets ith "Object" element
     */
    void setObjectArray(int i, gov.nih.nlm.ncbi.eutils.ObjectType object);

    /**
     * Inserts and returns a new empty value (as xml) as the ith "Object" element
     */
    gov.nih.nlm.ncbi.eutils.ObjectType insertNewObject(int i);

    /**
     * Appends and returns a new empty value (as xml) as the last "Object" element
     */
    gov.nih.nlm.ncbi.eutils.ObjectType addNewObject();

    /**
     * Removes the ith "Object" element
     */
    void removeObject(int i);
}
