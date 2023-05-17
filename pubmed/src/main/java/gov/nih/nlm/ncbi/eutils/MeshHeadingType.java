/*
 * XML Type:  MeshHeadingType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.MeshHeadingType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML MeshHeadingType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public interface MeshHeadingType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.MeshHeadingType> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "meshheadingtype3db0type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "DescriptorName" element
     */
    gov.nih.nlm.ncbi.eutils.DescriptorNameType getDescriptorName();

    /**
     * Sets the "DescriptorName" element
     */
    void setDescriptorName(gov.nih.nlm.ncbi.eutils.DescriptorNameType descriptorName);

    /**
     * Appends and returns a new empty "DescriptorName" element
     */
    gov.nih.nlm.ncbi.eutils.DescriptorNameType addNewDescriptorName();

    /**
     * Gets a List of "QualifierName" elements
     */
    java.util.List<gov.nih.nlm.ncbi.eutils.QualifierNameType> getQualifierNameList();

    /**
     * Gets array of all "QualifierName" elements
     */
    gov.nih.nlm.ncbi.eutils.QualifierNameType[] getQualifierNameArray();

    /**
     * Gets ith "QualifierName" element
     */
    gov.nih.nlm.ncbi.eutils.QualifierNameType getQualifierNameArray(int i);

    /**
     * Returns number of "QualifierName" element
     */
    int sizeOfQualifierNameArray();

    /**
     * Sets array of all "QualifierName" element
     */
    void setQualifierNameArray(gov.nih.nlm.ncbi.eutils.QualifierNameType[] qualifierNameArray);

    /**
     * Sets ith "QualifierName" element
     */
    void setQualifierNameArray(int i, gov.nih.nlm.ncbi.eutils.QualifierNameType qualifierName);

    /**
     * Inserts and returns a new empty value (as xml) as the ith "QualifierName" element
     */
    gov.nih.nlm.ncbi.eutils.QualifierNameType insertNewQualifierName(int i);

    /**
     * Appends and returns a new empty value (as xml) as the last "QualifierName" element
     */
    gov.nih.nlm.ncbi.eutils.QualifierNameType addNewQualifierName();

    /**
     * Removes the ith "QualifierName" element
     */
    void removeQualifierName(int i);
}
