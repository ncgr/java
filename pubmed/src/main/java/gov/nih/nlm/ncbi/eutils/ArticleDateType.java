/*
 * XML Type:  ArticleDateType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.ArticleDateType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML ArticleDateType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public interface ArticleDateType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.ArticleDateType> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "articledatetype9461type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "Year" element
     */
    java.lang.String getYear();

    /**
     * Gets (as xml) the "Year" element
     */
    org.apache.xmlbeans.XmlString xgetYear();

    /**
     * Sets the "Year" element
     */
    void setYear(java.lang.String year);

    /**
     * Sets (as xml) the "Year" element
     */
    void xsetYear(org.apache.xmlbeans.XmlString year);

    /**
     * Gets the "Month" element
     */
    java.lang.String getMonth();

    /**
     * Gets (as xml) the "Month" element
     */
    org.apache.xmlbeans.XmlString xgetMonth();

    /**
     * Sets the "Month" element
     */
    void setMonth(java.lang.String month);

    /**
     * Sets (as xml) the "Month" element
     */
    void xsetMonth(org.apache.xmlbeans.XmlString month);

    /**
     * Gets the "Day" element
     */
    java.lang.String getDay();

    /**
     * Gets (as xml) the "Day" element
     */
    org.apache.xmlbeans.XmlString xgetDay();

    /**
     * Sets the "Day" element
     */
    void setDay(java.lang.String day);

    /**
     * Sets (as xml) the "Day" element
     */
    void xsetDay(org.apache.xmlbeans.XmlString day);

    /**
     * Gets the "DateType" attribute
     */
    java.lang.String getDateType();

    /**
     * Gets (as xml) the "DateType" attribute
     */
    org.apache.xmlbeans.XmlString xgetDateType();

    /**
     * True if has "DateType" attribute
     */
    boolean isSetDateType();

    /**
     * Sets the "DateType" attribute
     */
    void setDateType(java.lang.String dateType);

    /**
     * Sets (as xml) the "DateType" attribute
     */
    void xsetDateType(org.apache.xmlbeans.XmlString dateType);

    /**
     * Unsets the "DateType" attribute
     */
    void unsetDateType();
}
