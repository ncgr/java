/*
 * XML Type:  CommentsCorrectionsType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML CommentsCorrectionsType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public interface CommentsCorrectionsType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "commentscorrectionstype8844type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "RefSource" element
     */
    java.lang.String getRefSource();

    /**
     * Gets (as xml) the "RefSource" element
     */
    org.apache.xmlbeans.XmlString xgetRefSource();

    /**
     * Sets the "RefSource" element
     */
    void setRefSource(java.lang.String refSource);

    /**
     * Sets (as xml) the "RefSource" element
     */
    void xsetRefSource(org.apache.xmlbeans.XmlString refSource);

    /**
     * Gets the "PMID" element
     */
    java.lang.String getPMID();

    /**
     * Gets (as xml) the "PMID" element
     */
    org.apache.xmlbeans.XmlString xgetPMID();

    /**
     * True if has "PMID" element
     */
    boolean isSetPMID();

    /**
     * Sets the "PMID" element
     */
    void setPMID(java.lang.String pmid);

    /**
     * Sets (as xml) the "PMID" element
     */
    void xsetPMID(org.apache.xmlbeans.XmlString pmid);

    /**
     * Unsets the "PMID" element
     */
    void unsetPMID();

    /**
     * Gets the "Note" element
     */
    java.lang.String getNote();

    /**
     * Gets (as xml) the "Note" element
     */
    org.apache.xmlbeans.XmlString xgetNote();

    /**
     * True if has "Note" element
     */
    boolean isSetNote();

    /**
     * Sets the "Note" element
     */
    void setNote(java.lang.String note);

    /**
     * Sets (as xml) the "Note" element
     */
    void xsetNote(org.apache.xmlbeans.XmlString note);

    /**
     * Unsets the "Note" element
     */
    void unsetNote();

    /**
     * Gets the "RefType" attribute
     */
    gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType.RefType.Enum getRefType();

    /**
     * Gets (as xml) the "RefType" attribute
     */
    gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType.RefType xgetRefType();

    /**
     * Sets the "RefType" attribute
     */
    void setRefType(gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType.RefType.Enum refType);

    /**
     * Sets (as xml) the "RefType" attribute
     */
    void xsetRefType(gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType.RefType refType);

    /**
     * An XML RefType(@).
     *
     * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType$RefType.
     */
    public interface RefType extends org.apache.xmlbeans.XmlNMTOKEN {
        ElementFactory<gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType.RefType> Factory = new ElementFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "reftype01afattrtype");
        org.apache.xmlbeans.SchemaType type = Factory.getType();


        org.apache.xmlbeans.StringEnumAbstractBase getEnumValue();
        void setEnumValue(org.apache.xmlbeans.StringEnumAbstractBase e);

        Enum COMMENT_ON = Enum.forString("CommentOn");
        Enum COMMENT_IN = Enum.forString("CommentIn");
        Enum ERRATUM_IN = Enum.forString("ErratumIn");
        Enum ERRATUM_FOR = Enum.forString("ErratumFor");
        Enum PARTIAL_RETRACTION_IN = Enum.forString("PartialRetractionIn");
        Enum PARTIAL_RETRACTION_OF = Enum.forString("PartialRetractionOf");
        Enum REPUBLISHED_FROM = Enum.forString("RepublishedFrom");
        Enum REPUBLISHED_IN = Enum.forString("RepublishedIn");
        Enum RETRACTION_OF = Enum.forString("RetractionOf");
        Enum RETRACTION_IN = Enum.forString("RetractionIn");
        Enum UPDATE_IN = Enum.forString("UpdateIn");
        Enum UPDATE_OF = Enum.forString("UpdateOf");
        Enum SUMMARY_FOR_PATIENTS_IN = Enum.forString("SummaryForPatientsIn");
        Enum ORIGINAL_REPORT_IN = Enum.forString("OriginalReportIn");
        Enum REPRINT_OF = Enum.forString("ReprintOf");
        Enum REPRINT_IN = Enum.forString("ReprintIn");
        Enum CITES = Enum.forString("Cites");

        int INT_COMMENT_ON = Enum.INT_COMMENT_ON;
        int INT_COMMENT_IN = Enum.INT_COMMENT_IN;
        int INT_ERRATUM_IN = Enum.INT_ERRATUM_IN;
        int INT_ERRATUM_FOR = Enum.INT_ERRATUM_FOR;
        int INT_PARTIAL_RETRACTION_IN = Enum.INT_PARTIAL_RETRACTION_IN;
        int INT_PARTIAL_RETRACTION_OF = Enum.INT_PARTIAL_RETRACTION_OF;
        int INT_REPUBLISHED_FROM = Enum.INT_REPUBLISHED_FROM;
        int INT_REPUBLISHED_IN = Enum.INT_REPUBLISHED_IN;
        int INT_RETRACTION_OF = Enum.INT_RETRACTION_OF;
        int INT_RETRACTION_IN = Enum.INT_RETRACTION_IN;
        int INT_UPDATE_IN = Enum.INT_UPDATE_IN;
        int INT_UPDATE_OF = Enum.INT_UPDATE_OF;
        int INT_SUMMARY_FOR_PATIENTS_IN = Enum.INT_SUMMARY_FOR_PATIENTS_IN;
        int INT_ORIGINAL_REPORT_IN = Enum.INT_ORIGINAL_REPORT_IN;
        int INT_REPRINT_OF = Enum.INT_REPRINT_OF;
        int INT_REPRINT_IN = Enum.INT_REPRINT_IN;
        int INT_CITES = Enum.INT_CITES;

        /**
         * Enumeration value class for gov.nih.nlm.ncbi.eutils.CommentsCorrectionsType$RefType.
         * These enum values can be used as follows:
         * <pre>
         * enum.toString(); // returns the string value of the enum
         * enum.intValue(); // returns an int value, useful for switches
         * // e.g., case Enum.INT_COMMENT_ON
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

            static final int INT_COMMENT_ON = 1;
            static final int INT_COMMENT_IN = 2;
            static final int INT_ERRATUM_IN = 3;
            static final int INT_ERRATUM_FOR = 4;
            static final int INT_PARTIAL_RETRACTION_IN = 5;
            static final int INT_PARTIAL_RETRACTION_OF = 6;
            static final int INT_REPUBLISHED_FROM = 7;
            static final int INT_REPUBLISHED_IN = 8;
            static final int INT_RETRACTION_OF = 9;
            static final int INT_RETRACTION_IN = 10;
            static final int INT_UPDATE_IN = 11;
            static final int INT_UPDATE_OF = 12;
            static final int INT_SUMMARY_FOR_PATIENTS_IN = 13;
            static final int INT_ORIGINAL_REPORT_IN = 14;
            static final int INT_REPRINT_OF = 15;
            static final int INT_REPRINT_IN = 16;
            static final int INT_CITES = 17;

            public static final org.apache.xmlbeans.StringEnumAbstractBase.Table table =
                new org.apache.xmlbeans.StringEnumAbstractBase.Table(new Enum[] {
                new Enum("CommentOn", INT_COMMENT_ON),
                new Enum("CommentIn", INT_COMMENT_IN),
                new Enum("ErratumIn", INT_ERRATUM_IN),
                new Enum("ErratumFor", INT_ERRATUM_FOR),
                new Enum("PartialRetractionIn", INT_PARTIAL_RETRACTION_IN),
                new Enum("PartialRetractionOf", INT_PARTIAL_RETRACTION_OF),
                new Enum("RepublishedFrom", INT_REPUBLISHED_FROM),
                new Enum("RepublishedIn", INT_REPUBLISHED_IN),
                new Enum("RetractionOf", INT_RETRACTION_OF),
                new Enum("RetractionIn", INT_RETRACTION_IN),
                new Enum("UpdateIn", INT_UPDATE_IN),
                new Enum("UpdateOf", INT_UPDATE_OF),
                new Enum("SummaryForPatientsIn", INT_SUMMARY_FOR_PATIENTS_IN),
                new Enum("OriginalReportIn", INT_ORIGINAL_REPORT_IN),
                new Enum("ReprintOf", INT_REPRINT_OF),
                new Enum("ReprintIn", INT_REPRINT_IN),
                new Enum("Cites", INT_CITES),
            });
            private static final long serialVersionUID = 1L;
            private java.lang.Object readResolve() {
                return forInt(intValue());
            }
        }
    }
}
