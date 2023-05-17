/*
 * XML Type:  PersonalNameSubjectType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.PersonalNameSubjectType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML PersonalNameSubjectType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public interface PersonalNameSubjectType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.PersonalNameSubjectType> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "personalnamesubjecttype04c4type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "LastName" element
     */
    java.lang.String getLastName();

    /**
     * Gets (as xml) the "LastName" element
     */
    org.apache.xmlbeans.XmlString xgetLastName();

    /**
     * Sets the "LastName" element
     */
    void setLastName(java.lang.String lastName);

    /**
     * Sets (as xml) the "LastName" element
     */
    void xsetLastName(org.apache.xmlbeans.XmlString lastName);

    /**
     * Gets the "ForeName" element
     */
    java.lang.String getForeName();

    /**
     * Gets (as xml) the "ForeName" element
     */
    org.apache.xmlbeans.XmlString xgetForeName();

    /**
     * True if has "ForeName" element
     */
    boolean isSetForeName();

    /**
     * Sets the "ForeName" element
     */
    void setForeName(java.lang.String foreName);

    /**
     * Sets (as xml) the "ForeName" element
     */
    void xsetForeName(org.apache.xmlbeans.XmlString foreName);

    /**
     * Unsets the "ForeName" element
     */
    void unsetForeName();

    /**
     * Gets the "Initials" element
     */
    java.lang.String getInitials();

    /**
     * Gets (as xml) the "Initials" element
     */
    org.apache.xmlbeans.XmlString xgetInitials();

    /**
     * True if has "Initials" element
     */
    boolean isSetInitials();

    /**
     * Sets the "Initials" element
     */
    void setInitials(java.lang.String initials);

    /**
     * Sets (as xml) the "Initials" element
     */
    void xsetInitials(org.apache.xmlbeans.XmlString initials);

    /**
     * Unsets the "Initials" element
     */
    void unsetInitials();

    /**
     * Gets the "Suffix" element
     */
    java.lang.String getSuffix();

    /**
     * Gets (as xml) the "Suffix" element
     */
    org.apache.xmlbeans.XmlString xgetSuffix();

    /**
     * True if has "Suffix" element
     */
    boolean isSetSuffix();

    /**
     * Sets the "Suffix" element
     */
    void setSuffix(java.lang.String suffix);

    /**
     * Sets (as xml) the "Suffix" element
     */
    void xsetSuffix(org.apache.xmlbeans.XmlString suffix);

    /**
     * Unsets the "Suffix" element
     */
    void unsetSuffix();
}
