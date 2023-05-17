/*
 * XML Type:  ObjectType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.ObjectType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML ObjectType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public interface ObjectType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.ObjectType> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "objecttyped454type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets a List of "Param" elements
     */
    java.util.List<gov.nih.nlm.ncbi.eutils.ParamType> getParamList();

    /**
     * Gets array of all "Param" elements
     */
    gov.nih.nlm.ncbi.eutils.ParamType[] getParamArray();

    /**
     * Gets ith "Param" element
     */
    gov.nih.nlm.ncbi.eutils.ParamType getParamArray(int i);

    /**
     * Returns number of "Param" element
     */
    int sizeOfParamArray();

    /**
     * Sets array of all "Param" element
     */
    void setParamArray(gov.nih.nlm.ncbi.eutils.ParamType[] paramArray);

    /**
     * Sets ith "Param" element
     */
    void setParamArray(int i, gov.nih.nlm.ncbi.eutils.ParamType param);

    /**
     * Inserts and returns a new empty value (as xml) as the ith "Param" element
     */
    gov.nih.nlm.ncbi.eutils.ParamType insertNewParam(int i);

    /**
     * Appends and returns a new empty value (as xml) as the last "Param" element
     */
    gov.nih.nlm.ncbi.eutils.ParamType addNewParam();

    /**
     * Removes the ith "Param" element
     */
    void removeParam(int i);

    /**
     * Gets the "Type" attribute
     */
    java.lang.String getType();

    /**
     * Gets (as xml) the "Type" attribute
     */
    org.apache.xmlbeans.XmlString xgetType();

    /**
     * Sets the "Type" attribute
     */
    void setType(java.lang.String type);

    /**
     * Sets (as xml) the "Type" attribute
     */
    void xsetType(org.apache.xmlbeans.XmlString type);
}
