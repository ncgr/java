/*
 * An XML document type.
 * Localname: NameOfSubstance
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.NameOfSubstanceDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;

/**
 * A document containing one NameOfSubstance(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public class NameOfSubstanceDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements gov.nih.nlm.ncbi.eutils.NameOfSubstanceDocument {
    private static final long serialVersionUID = 1L;

    public NameOfSubstanceDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "NameOfSubstance"),
    };


    /**
     * Gets the "NameOfSubstance" element
     */
    @Override
    public java.lang.String getNameOfSubstance() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "NameOfSubstance" element
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetNameOfSubstance() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return target;
        }
    }

    /**
     * Sets the "NameOfSubstance" element
     */
    @Override
    public void setNameOfSubstance(java.lang.String nameOfSubstance) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[0]);
            }
            target.setStringValue(nameOfSubstance);
        }
    }

    /**
     * Sets (as xml) the "NameOfSubstance" element
     */
    @Override
    public void xsetNameOfSubstance(org.apache.xmlbeans.XmlString nameOfSubstance) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PROPERTY_QNAME[0]);
            }
            target.set(nameOfSubstance);
        }
    }
}
