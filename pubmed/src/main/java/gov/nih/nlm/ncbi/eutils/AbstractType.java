/*
 * XML Type:  AbstractType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.AbstractType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML AbstractType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public interface AbstractType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.AbstractType> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "abstracttypef451type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "AbstractText" element
     */
    java.lang.String getAbstractText();

    /**
     * Gets (as xml) the "AbstractText" element
     */
    org.apache.xmlbeans.XmlString xgetAbstractText();

    /**
     * Sets the "AbstractText" element
     */
    void setAbstractText(java.lang.String abstractText);

    /**
     * Sets (as xml) the "AbstractText" element
     */
    void xsetAbstractText(org.apache.xmlbeans.XmlString abstractText);

    /**
     * Gets the "CopyrightInformation" element
     */
    java.lang.String getCopyrightInformation();

    /**
     * Gets (as xml) the "CopyrightInformation" element
     */
    org.apache.xmlbeans.XmlString xgetCopyrightInformation();

    /**
     * True if has "CopyrightInformation" element
     */
    boolean isSetCopyrightInformation();

    /**
     * Sets the "CopyrightInformation" element
     */
    void setCopyrightInformation(java.lang.String copyrightInformation);

    /**
     * Sets (as xml) the "CopyrightInformation" element
     */
    void xsetCopyrightInformation(org.apache.xmlbeans.XmlString copyrightInformation);

    /**
     * Unsets the "CopyrightInformation" element
     */
    void unsetCopyrightInformation();
}
