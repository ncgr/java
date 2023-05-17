/*
 * An XML document type.
 * Localname: StartPage
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.StartPageDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one StartPage(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface StartPageDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.StartPageDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "startpage555edoctype");
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
     * Sets the "StartPage" element
     */
    void setStartPage(java.lang.String startPage);

    /**
     * Sets (as xml) the "StartPage" element
     */
    void xsetStartPage(org.apache.xmlbeans.XmlString startPage);
}
