/*
 * XML Type:  PubMedPubDateType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.PubMedPubDateType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML PubMedPubDateType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public interface PubMedPubDateType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.PubMedPubDateType> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "pubmedpubdatetype8929type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "Year" element
     */
    java.lang.String getYear();

    /**
     * Gets (as xml) the "Year" element
     */
    org.apache.xmlbeans.XmlString xgetYear();

    /**
     * Sets the "Year" element
     */
    void setYear(java.lang.String year);

    /**
     * Sets (as xml) the "Year" element
     */
    void xsetYear(org.apache.xmlbeans.XmlString year);

    /**
     * Gets the "Month" element
     */
    java.lang.String getMonth();

    /**
     * Gets (as xml) the "Month" element
     */
    org.apache.xmlbeans.XmlString xgetMonth();

    /**
     * Sets the "Month" element
     */
    void setMonth(java.lang.String month);

    /**
     * Sets (as xml) the "Month" element
     */
    void xsetMonth(org.apache.xmlbeans.XmlString month);

    /**
     * Gets the "Day" element
     */
    java.lang.String getDay();

    /**
     * Gets (as xml) the "Day" element
     */
    org.apache.xmlbeans.XmlString xgetDay();

    /**
     * Sets the "Day" element
     */
    void setDay(java.lang.String day);

    /**
     * Sets (as xml) the "Day" element
     */
    void xsetDay(org.apache.xmlbeans.XmlString day);

    /**
     * Gets the "Hour" element
     */
    java.lang.String getHour();

    /**
     * Gets (as xml) the "Hour" element
     */
    org.apache.xmlbeans.XmlString xgetHour();

    /**
     * True if has "Hour" element
     */
    boolean isSetHour();

    /**
     * Sets the "Hour" element
     */
    void setHour(java.lang.String hour);

    /**
     * Sets (as xml) the "Hour" element
     */
    void xsetHour(org.apache.xmlbeans.XmlString hour);

    /**
     * Unsets the "Hour" element
     */
    void unsetHour();

    /**
     * Gets the "Minute" element
     */
    java.lang.String getMinute();

    /**
     * Gets (as xml) the "Minute" element
     */
    org.apache.xmlbeans.XmlString xgetMinute();

    /**
     * True if has "Minute" element
     */
    boolean isSetMinute();

    /**
     * Sets the "Minute" element
     */
    void setMinute(java.lang.String minute);

    /**
     * Sets (as xml) the "Minute" element
     */
    void xsetMinute(org.apache.xmlbeans.XmlString minute);

    /**
     * Unsets the "Minute" element
     */
    void unsetMinute();

    /**
     * Gets the "Second" element
     */
    java.lang.String getSecond();

    /**
     * Gets (as xml) the "Second" element
     */
    org.apache.xmlbeans.XmlString xgetSecond();

    /**
     * True if has "Second" element
     */
    boolean isSetSecond();

    /**
     * Sets the "Second" element
     */
    void setSecond(java.lang.String second);

    /**
     * Sets (as xml) the "Second" element
     */
    void xsetSecond(org.apache.xmlbeans.XmlString second);

    /**
     * Unsets the "Second" element
     */
    void unsetSecond();

    /**
     * Gets the "PubStatus" attribute
     */
    gov.nih.nlm.ncbi.eutils.PubMedPubDateType.PubStatus.Enum getPubStatus();

    /**
     * Gets (as xml) the "PubStatus" attribute
     */
    gov.nih.nlm.ncbi.eutils.PubMedPubDateType.PubStatus xgetPubStatus();

    /**
     * Sets the "PubStatus" attribute
     */
    void setPubStatus(gov.nih.nlm.ncbi.eutils.PubMedPubDateType.PubStatus.Enum pubStatus);

    /**
     * Sets (as xml) the "PubStatus" attribute
     */
    void xsetPubStatus(gov.nih.nlm.ncbi.eutils.PubMedPubDateType.PubStatus pubStatus);

    /**
     * An XML PubStatus(@).
     *
     * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.PubMedPubDateType$PubStatus.
     */
    public interface PubStatus extends org.apache.xmlbeans.XmlNMTOKEN {
        ElementFactory<gov.nih.nlm.ncbi.eutils.PubMedPubDateType.PubStatus> Factory = new ElementFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "pubstatusf6b2attrtype");
        org.apache.xmlbeans.SchemaType type = Factory.getType();


        org.apache.xmlbeans.StringEnumAbstractBase getEnumValue();
        void setEnumValue(org.apache.xmlbeans.StringEnumAbstractBase e);

        Enum RECEIVED = Enum.forString("received");
        Enum ACCEPTED = Enum.forString("accepted");
        Enum EPUBLISH = Enum.forString("epublish");
        Enum PPUBLISH = Enum.forString("ppublish");
        Enum REVISED = Enum.forString("revised");
        Enum AHEADOFPRINT = Enum.forString("aheadofprint");
        Enum RETRACTED = Enum.forString("retracted");
        Enum PMC = Enum.forString("pmc");
        Enum PMCR = Enum.forString("pmcr");
        Enum PUBMED = Enum.forString("pubmed");
        Enum PUBMEDR = Enum.forString("pubmedr");
        Enum PREMEDLINE = Enum.forString("premedline");
        Enum MEDLINE = Enum.forString("medline");
        Enum MEDLINER = Enum.forString("medliner");
        Enum ENTREZ = Enum.forString("entrez");
        Enum PMC_RELEASE = Enum.forString("pmc-release");

        int INT_RECEIVED = Enum.INT_RECEIVED;
        int INT_ACCEPTED = Enum.INT_ACCEPTED;
        int INT_EPUBLISH = Enum.INT_EPUBLISH;
        int INT_PPUBLISH = Enum.INT_PPUBLISH;
        int INT_REVISED = Enum.INT_REVISED;
        int INT_AHEADOFPRINT = Enum.INT_AHEADOFPRINT;
        int INT_RETRACTED = Enum.INT_RETRACTED;
        int INT_PMC = Enum.INT_PMC;
        int INT_PMCR = Enum.INT_PMCR;
        int INT_PUBMED = Enum.INT_PUBMED;
        int INT_PUBMEDR = Enum.INT_PUBMEDR;
        int INT_PREMEDLINE = Enum.INT_PREMEDLINE;
        int INT_MEDLINE = Enum.INT_MEDLINE;
        int INT_MEDLINER = Enum.INT_MEDLINER;
        int INT_ENTREZ = Enum.INT_ENTREZ;
        int INT_PMC_RELEASE = Enum.INT_PMC_RELEASE;

        /**
         * Enumeration value class for gov.nih.nlm.ncbi.eutils.PubMedPubDateType$PubStatus.
         * These enum values can be used as follows:
         * <pre>
         * enum.toString(); // returns the string value of the enum
         * enum.intValue(); // returns an int value, useful for switches
         * // e.g., case Enum.INT_RECEIVED
         * Enum.forString(s); // returns the enum value for a string
         * Enum.forInt(i); // returns the enum value for an int
         * </pre>
         * Enumeration objects are immutable singleton objects that
         * can be compared using == object equality. They have no
         * public constructor. See the constants defined within this
         * class for all the valid values.
         */
        final class Enum extends org.apache.xmlbeans.StringEnumAbstractBase {
            /**
             * Returns the enum value for a string, or null if none.
             */
            public static Enum forString(java.lang.String s) {
                return (Enum)table.forString(s);
            }

            /**
             * Returns the enum value corresponding to an int, or null if none.
             */
            public static Enum forInt(int i) {
                return (Enum)table.forInt(i);
            }

            private Enum(java.lang.String s, int i) {
                super(s, i);
            }

            static final int INT_RECEIVED = 1;
            static final int INT_ACCEPTED = 2;
            static final int INT_EPUBLISH = 3;
            static final int INT_PPUBLISH = 4;
            static final int INT_REVISED = 5;
            static final int INT_AHEADOFPRINT = 6;
            static final int INT_RETRACTED = 7;
            static final int INT_PMC = 8;
            static final int INT_PMCR = 9;
            static final int INT_PUBMED = 10;
            static final int INT_PUBMEDR = 11;
            static final int INT_PREMEDLINE = 12;
            static final int INT_MEDLINE = 13;
            static final int INT_MEDLINER = 14;
            static final int INT_ENTREZ = 15;
            static final int INT_PMC_RELEASE = 16;

            public static final org.apache.xmlbeans.StringEnumAbstractBase.Table table =
                new org.apache.xmlbeans.StringEnumAbstractBase.Table(new Enum[] {
                new Enum("received", INT_RECEIVED),
                new Enum("accepted", INT_ACCEPTED),
                new Enum("epublish", INT_EPUBLISH),
                new Enum("ppublish", INT_PPUBLISH),
                new Enum("revised", INT_REVISED),
                new Enum("aheadofprint", INT_AHEADOFPRINT),
                new Enum("retracted", INT_RETRACTED),
                new Enum("pmc", INT_PMC),
                new Enum("pmcr", INT_PMCR),
                new Enum("pubmed", INT_PUBMED),
                new Enum("pubmedr", INT_PUBMEDR),
                new Enum("premedline", INT_PREMEDLINE),
                new Enum("medline", INT_MEDLINE),
                new Enum("medliner", INT_MEDLINER),
                new Enum("entrez", INT_ENTREZ),
                new Enum("pmc-release", INT_PMC_RELEASE),
            });
            private static final long serialVersionUID = 1L;
            private java.lang.Object readResolve() {
                return forInt(intValue());
            }
        }
    }
}
