/*
 * An XML document type.
 * Localname: DataBankName
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.DataBankNameDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one DataBankName(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface DataBankNameDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.DataBankNameDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "databanknamed1ecdoctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "DataBankName" element
     */
    java.lang.String getDataBankName();

    /**
     * Gets (as xml) the "DataBankName" element
     */
    org.apache.xmlbeans.XmlString xgetDataBankName();

    /**
     * Sets the "DataBankName" element
     */
    void setDataBankName(java.lang.String dataBankName);

    /**
     * Sets (as xml) the "DataBankName" element
     */
    void xsetDataBankName(org.apache.xmlbeans.XmlString dataBankName);
}
