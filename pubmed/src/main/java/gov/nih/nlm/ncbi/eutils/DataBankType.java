/*
 * XML Type:  DataBankType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.DataBankType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML DataBankType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public interface DataBankType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.DataBankType> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "databanktype66edtype");
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

    /**
     * Gets the "AccessionNumberList" element
     */
    gov.nih.nlm.ncbi.eutils.AccessionNumberListType getAccessionNumberList();

    /**
     * True if has "AccessionNumberList" element
     */
    boolean isSetAccessionNumberList();

    /**
     * Sets the "AccessionNumberList" element
     */
    void setAccessionNumberList(gov.nih.nlm.ncbi.eutils.AccessionNumberListType accessionNumberList);

    /**
     * Appends and returns a new empty "AccessionNumberList" element
     */
    gov.nih.nlm.ncbi.eutils.AccessionNumberListType addNewAccessionNumberList();

    /**
     * Unsets the "AccessionNumberList" element
     */
    void unsetAccessionNumberList();
}
