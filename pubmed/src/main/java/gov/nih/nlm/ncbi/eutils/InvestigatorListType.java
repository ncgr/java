/*
 * XML Type:  InvestigatorListType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.InvestigatorListType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML InvestigatorListType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public interface InvestigatorListType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.InvestigatorListType> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "investigatorlisttypeb5ectype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets a List of "Investigator" elements
     */
    java.util.List<gov.nih.nlm.ncbi.eutils.InvestigatorType> getInvestigatorList();

    /**
     * Gets array of all "Investigator" elements
     */
    gov.nih.nlm.ncbi.eutils.InvestigatorType[] getInvestigatorArray();

    /**
     * Gets ith "Investigator" element
     */
    gov.nih.nlm.ncbi.eutils.InvestigatorType getInvestigatorArray(int i);

    /**
     * Returns number of "Investigator" element
     */
    int sizeOfInvestigatorArray();

    /**
     * Sets array of all "Investigator" element
     */
    void setInvestigatorArray(gov.nih.nlm.ncbi.eutils.InvestigatorType[] investigatorArray);

    /**
     * Sets ith "Investigator" element
     */
    void setInvestigatorArray(int i, gov.nih.nlm.ncbi.eutils.InvestigatorType investigator);

    /**
     * Inserts and returns a new empty value (as xml) as the ith "Investigator" element
     */
    gov.nih.nlm.ncbi.eutils.InvestigatorType insertNewInvestigator(int i);

    /**
     * Appends and returns a new empty value (as xml) as the last "Investigator" element
     */
    gov.nih.nlm.ncbi.eutils.InvestigatorType addNewInvestigator();

    /**
     * Removes the ith "Investigator" element
     */
    void removeInvestigator(int i);
}
