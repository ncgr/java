/*
 * XML Type:  JournalIssueType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.JournalIssueType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML JournalIssueType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public interface JournalIssueType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.JournalIssueType> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "journalissuetype3511type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "Volume" element
     */
    java.lang.String getVolume();

    /**
     * Gets (as xml) the "Volume" element
     */
    org.apache.xmlbeans.XmlString xgetVolume();

    /**
     * True if has "Volume" element
     */
    boolean isSetVolume();

    /**
     * Sets the "Volume" element
     */
    void setVolume(java.lang.String volume);

    /**
     * Sets (as xml) the "Volume" element
     */
    void xsetVolume(org.apache.xmlbeans.XmlString volume);

    /**
     * Unsets the "Volume" element
     */
    void unsetVolume();

    /**
     * Gets the "Issue" element
     */
    java.lang.String getIssue();

    /**
     * Gets (as xml) the "Issue" element
     */
    org.apache.xmlbeans.XmlString xgetIssue();

    /**
     * True if has "Issue" element
     */
    boolean isSetIssue();

    /**
     * Sets the "Issue" element
     */
    void setIssue(java.lang.String issue);

    /**
     * Sets (as xml) the "Issue" element
     */
    void xsetIssue(org.apache.xmlbeans.XmlString issue);

    /**
     * Unsets the "Issue" element
     */
    void unsetIssue();

    /**
     * Gets the "PubDate" element
     */
    gov.nih.nlm.ncbi.eutils.PubDateType getPubDate();

    /**
     * Sets the "PubDate" element
     */
    void setPubDate(gov.nih.nlm.ncbi.eutils.PubDateType pubDate);

    /**
     * Appends and returns a new empty "PubDate" element
     */
    gov.nih.nlm.ncbi.eutils.PubDateType addNewPubDate();

    /**
     * Gets the "CitedMedium" attribute
     */
    gov.nih.nlm.ncbi.eutils.JournalIssueType.CitedMedium.Enum getCitedMedium();

    /**
     * Gets (as xml) the "CitedMedium" attribute
     */
    gov.nih.nlm.ncbi.eutils.JournalIssueType.CitedMedium xgetCitedMedium();

    /**
     * Sets the "CitedMedium" attribute
     */
    void setCitedMedium(gov.nih.nlm.ncbi.eutils.JournalIssueType.CitedMedium.Enum citedMedium);

    /**
     * Sets (as xml) the "CitedMedium" attribute
     */
    void xsetCitedMedium(gov.nih.nlm.ncbi.eutils.JournalIssueType.CitedMedium citedMedium);

    /**
     * An XML CitedMedium(@).
     *
     * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.JournalIssueType$CitedMedium.
     */
    public interface CitedMedium extends org.apache.xmlbeans.XmlNMTOKEN {
        ElementFactory<gov.nih.nlm.ncbi.eutils.JournalIssueType.CitedMedium> Factory = new ElementFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "citedmediume79battrtype");
        org.apache.xmlbeans.SchemaType type = Factory.getType();


        org.apache.xmlbeans.StringEnumAbstractBase getEnumValue();
        void setEnumValue(org.apache.xmlbeans.StringEnumAbstractBase e);

        Enum INTERNET = Enum.forString("Internet");
        Enum PRINT = Enum.forString("Print");

        int INT_INTERNET = Enum.INT_INTERNET;
        int INT_PRINT = Enum.INT_PRINT;

        /**
         * Enumeration value class for gov.nih.nlm.ncbi.eutils.JournalIssueType$CitedMedium.
         * These enum values can be used as follows:
         * <pre>
         * enum.toString(); // returns the string value of the enum
         * enum.intValue(); // returns an int value, useful for switches
         * // e.g., case Enum.INT_INTERNET
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

            static final int INT_INTERNET = 1;
            static final int INT_PRINT = 2;

            public static final org.apache.xmlbeans.StringEnumAbstractBase.Table table =
                new org.apache.xmlbeans.StringEnumAbstractBase.Table(new Enum[] {
                new Enum("Internet", INT_INTERNET),
                new Enum("Print", INT_PRINT),
            });
            private static final long serialVersionUID = 1L;
            private java.lang.Object readResolve() {
                return forInt(intValue());
            }
        }
    }
}
