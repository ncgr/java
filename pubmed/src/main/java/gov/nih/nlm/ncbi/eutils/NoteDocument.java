/*
 * An XML document type.
 * Localname: Note
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.NoteDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one Note(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface NoteDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.NoteDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "noteca0bdoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "Note" element
     */
    java.lang.String getNote();

    /**
     * Gets (as xml) the "Note" element
     */
    org.apache.xmlbeans.XmlString xgetNote();

    /**
     * Sets the "Note" element
     */
    void setNote(java.lang.String note);

    /**
     * Sets (as xml) the "Note" element
     */
    void xsetNote(org.apache.xmlbeans.XmlString note);
}
