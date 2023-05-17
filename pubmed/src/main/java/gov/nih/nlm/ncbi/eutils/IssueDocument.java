/*
 * An XML document type.
 * Localname: Issue
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.IssueDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one Issue(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface IssueDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.IssueDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "issue0616doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "Issue" element
     */
    java.lang.String getIssue();

    /**
     * Gets (as xml) the "Issue" element
     */
    org.apache.xmlbeans.XmlString xgetIssue();

    /**
     * Sets the "Issue" element
     */
    void setIssue(java.lang.String issue);

    /**
     * Sets (as xml) the "Issue" element
     */
    void xsetIssue(org.apache.xmlbeans.XmlString issue);
}
