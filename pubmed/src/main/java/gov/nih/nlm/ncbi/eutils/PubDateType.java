/*
 * XML Type:  PubDateType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.PubDateType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML PubDateType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public interface PubDateType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.PubDateType> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "pubdatetype5c7atype");
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
     * True if has "Year" element
     */
    boolean isSetYear();

    /**
     * Sets the "Year" element
     */
    void setYear(java.lang.String year);

    /**
     * Sets (as xml) the "Year" element
     */
    void xsetYear(org.apache.xmlbeans.XmlString year);

    /**
     * Unsets the "Year" element
     */
    void unsetYear();

    /**
     * Gets the "Month" element
     */
    java.lang.String getMonth();

    /**
     * Gets (as xml) the "Month" element
     */
    org.apache.xmlbeans.XmlString xgetMonth();

    /**
     * True if has "Month" element
     */
    boolean isSetMonth();

    /**
     * Sets the "Month" element
     */
    void setMonth(java.lang.String month);

    /**
     * Sets (as xml) the "Month" element
     */
    void xsetMonth(org.apache.xmlbeans.XmlString month);

    /**
     * Unsets the "Month" element
     */
    void unsetMonth();

    /**
     * Gets the "Day" element
     */
    java.lang.String getDay();

    /**
     * Gets (as xml) the "Day" element
     */
    org.apache.xmlbeans.XmlString xgetDay();

    /**
     * True if has "Day" element
     */
    boolean isSetDay();

    /**
     * Sets the "Day" element
     */
    void setDay(java.lang.String day);

    /**
     * Sets (as xml) the "Day" element
     */
    void xsetDay(org.apache.xmlbeans.XmlString day);

    /**
     * Unsets the "Day" element
     */
    void unsetDay();

    /**
     * Gets the "Season" element
     */
    java.lang.String getSeason();

    /**
     * Gets (as xml) the "Season" element
     */
    org.apache.xmlbeans.XmlString xgetSeason();

    /**
     * True if has "Season" element
     */
    boolean isSetSeason();

    /**
     * Sets the "Season" element
     */
    void setSeason(java.lang.String season);

    /**
     * Sets (as xml) the "Season" element
     */
    void xsetSeason(org.apache.xmlbeans.XmlString season);

    /**
     * Unsets the "Season" element
     */
    void unsetSeason();

    /**
     * Gets the "MedlineDate" element
     */
    java.lang.String getMedlineDate();

    /**
     * Gets (as xml) the "MedlineDate" element
     */
    org.apache.xmlbeans.XmlString xgetMedlineDate();

    /**
     * True if has "MedlineDate" element
     */
    boolean isSetMedlineDate();

    /**
     * Sets the "MedlineDate" element
     */
    void setMedlineDate(java.lang.String medlineDate);

    /**
     * Sets (as xml) the "MedlineDate" element
     */
    void xsetMedlineDate(org.apache.xmlbeans.XmlString medlineDate);

    /**
     * Unsets the "MedlineDate" element
     */
    void unsetMedlineDate();
}
