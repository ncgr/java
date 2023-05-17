/*
 * XML Type:  PaginationType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.PaginationType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML PaginationType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public interface PaginationType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.PaginationType> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "paginationtype7c19type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "StartPage" element
     */
    java.lang.String getStartPage();

    /**
     * Gets (as xml) the "StartPage" element
     */
    org.apache.xmlbeans.XmlString xgetStartPage();

    /**
     * True if has "StartPage" element
     */
    boolean isSetStartPage();

    /**
     * Sets the "StartPage" element
     */
    void setStartPage(java.lang.String startPage);

    /**
     * Sets (as xml) the "StartPage" element
     */
    void xsetStartPage(org.apache.xmlbeans.XmlString startPage);

    /**
     * Unsets the "StartPage" element
     */
    void unsetStartPage();

    /**
     * Gets the "EndPage" element
     */
    java.lang.String getEndPage();

    /**
     * Gets (as xml) the "EndPage" element
     */
    org.apache.xmlbeans.XmlString xgetEndPage();

    /**
     * True if has "EndPage" element
     */
    boolean isSetEndPage();

    /**
     * Sets the "EndPage" element
     */
    void setEndPage(java.lang.String endPage);

    /**
     * Sets (as xml) the "EndPage" element
     */
    void xsetEndPage(org.apache.xmlbeans.XmlString endPage);

    /**
     * Unsets the "EndPage" element
     */
    void unsetEndPage();

    /**
     * Gets the "MedlinePgn" element
     */
    java.lang.String getMedlinePgn();

    /**
     * Gets (as xml) the "MedlinePgn" element
     */
    org.apache.xmlbeans.XmlString xgetMedlinePgn();

    /**
     * True if has "MedlinePgn" element
     */
    boolean isSetMedlinePgn();

    /**
     * Sets the "MedlinePgn" element
     */
    void setMedlinePgn(java.lang.String medlinePgn);

    /**
     * Sets (as xml) the "MedlinePgn" element
     */
    void xsetMedlinePgn(org.apache.xmlbeans.XmlString medlinePgn);

    /**
     * Unsets the "MedlinePgn" element
     */
    void unsetMedlinePgn();
}
