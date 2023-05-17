/*
 * An XML document type.
 * Localname: SpaceFlightMission
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.SpaceFlightMissionDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one SpaceFlightMission(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface SpaceFlightMissionDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.SpaceFlightMissionDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "spaceflightmission9fa7doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "SpaceFlightMission" element
     */
    java.lang.String getSpaceFlightMission();

    /**
     * Gets (as xml) the "SpaceFlightMission" element
     */
    org.apache.xmlbeans.XmlString xgetSpaceFlightMission();

    /**
     * Sets the "SpaceFlightMission" element
     */
    void setSpaceFlightMission(java.lang.String spaceFlightMission);

    /**
     * Sets (as xml) the "SpaceFlightMission" element
     */
    void xsetSpaceFlightMission(org.apache.xmlbeans.XmlString spaceFlightMission);
}
