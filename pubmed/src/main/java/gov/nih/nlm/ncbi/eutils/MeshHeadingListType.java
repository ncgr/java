/*
 * XML Type:  MeshHeadingListType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.MeshHeadingListType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML MeshHeadingListType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public interface MeshHeadingListType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.MeshHeadingListType> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "meshheadinglisttype9532type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets a List of "MeshHeading" elements
     */
    java.util.List<gov.nih.nlm.ncbi.eutils.MeshHeadingType> getMeshHeadingList();

    /**
     * Gets array of all "MeshHeading" elements
     */
    gov.nih.nlm.ncbi.eutils.MeshHeadingType[] getMeshHeadingArray();

    /**
     * Gets ith "MeshHeading" element
     */
    gov.nih.nlm.ncbi.eutils.MeshHeadingType getMeshHeadingArray(int i);

    /**
     * Returns number of "MeshHeading" element
     */
    int sizeOfMeshHeadingArray();

    /**
     * Sets array of all "MeshHeading" element
     */
    void setMeshHeadingArray(gov.nih.nlm.ncbi.eutils.MeshHeadingType[] meshHeadingArray);

    /**
     * Sets ith "MeshHeading" element
     */
    void setMeshHeadingArray(int i, gov.nih.nlm.ncbi.eutils.MeshHeadingType meshHeading);

    /**
     * Inserts and returns a new empty value (as xml) as the ith "MeshHeading" element
     */
    gov.nih.nlm.ncbi.eutils.MeshHeadingType insertNewMeshHeading(int i);

    /**
     * Appends and returns a new empty value (as xml) as the last "MeshHeading" element
     */
    gov.nih.nlm.ncbi.eutils.MeshHeadingType addNewMeshHeading();

    /**
     * Removes the ith "MeshHeading" element
     */
    void removeMeshHeading(int i);
}
