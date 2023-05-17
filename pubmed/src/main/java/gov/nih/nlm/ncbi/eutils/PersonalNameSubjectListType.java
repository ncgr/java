/*
 * XML Type:  PersonalNameSubjectListType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.PersonalNameSubjectListType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML PersonalNameSubjectListType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public interface PersonalNameSubjectListType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.PersonalNameSubjectListType> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "personalnamesubjectlisttypeb246type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets a List of "PersonalNameSubject" elements
     */
    java.util.List<gov.nih.nlm.ncbi.eutils.PersonalNameSubjectType> getPersonalNameSubjectList();

    /**
     * Gets array of all "PersonalNameSubject" elements
     */
    gov.nih.nlm.ncbi.eutils.PersonalNameSubjectType[] getPersonalNameSubjectArray();

    /**
     * Gets ith "PersonalNameSubject" element
     */
    gov.nih.nlm.ncbi.eutils.PersonalNameSubjectType getPersonalNameSubjectArray(int i);

    /**
     * Returns number of "PersonalNameSubject" element
     */
    int sizeOfPersonalNameSubjectArray();

    /**
     * Sets array of all "PersonalNameSubject" element
     */
    void setPersonalNameSubjectArray(gov.nih.nlm.ncbi.eutils.PersonalNameSubjectType[] personalNameSubjectArray);

    /**
     * Sets ith "PersonalNameSubject" element
     */
    void setPersonalNameSubjectArray(int i, gov.nih.nlm.ncbi.eutils.PersonalNameSubjectType personalNameSubject);

    /**
     * Inserts and returns a new empty value (as xml) as the ith "PersonalNameSubject" element
     */
    gov.nih.nlm.ncbi.eutils.PersonalNameSubjectType insertNewPersonalNameSubject(int i);

    /**
     * Appends and returns a new empty value (as xml) as the last "PersonalNameSubject" element
     */
    gov.nih.nlm.ncbi.eutils.PersonalNameSubjectType addNewPersonalNameSubject();

    /**
     * Removes the ith "PersonalNameSubject" element
     */
    void removePersonalNameSubject(int i);
}
