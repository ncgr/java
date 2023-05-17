/*
 * An XML document type.
 * Localname: Season
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.SeasonDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one Season(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface SeasonDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.SeasonDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "seasonabbadoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "Season" element
     */
    java.lang.String getSeason();

    /**
     * Gets (as xml) the "Season" element
     */
    org.apache.xmlbeans.XmlString xgetSeason();

    /**
     * Sets the "Season" element
     */
    void setSeason(java.lang.String season);

    /**
     * Sets (as xml) the "Season" element
     */
    void xsetSeason(org.apache.xmlbeans.XmlString season);
}
