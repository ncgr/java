/*
 * XML Type:  GeneralNoteType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.GeneralNoteType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML GeneralNoteType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.GeneralNoteType.
 */
public interface GeneralNoteType extends org.apache.xmlbeans.XmlString {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.GeneralNoteType> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "generalnotetype506btype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "Owner" attribute
     */
    gov.nih.nlm.ncbi.eutils.GeneralNoteType.Owner.Enum getOwner();

    /**
     * Gets (as xml) the "Owner" attribute
     */
    gov.nih.nlm.ncbi.eutils.GeneralNoteType.Owner xgetOwner();

    /**
     * True if has "Owner" attribute
     */
    boolean isSetOwner();

    /**
     * Sets the "Owner" attribute
     */
    void setOwner(gov.nih.nlm.ncbi.eutils.GeneralNoteType.Owner.Enum owner);

    /**
     * Sets (as xml) the "Owner" attribute
     */
    void xsetOwner(gov.nih.nlm.ncbi.eutils.GeneralNoteType.Owner owner);

    /**
     * Unsets the "Owner" attribute
     */
    void unsetOwner();

    /**
     * An XML Owner(@).
     *
     * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.GeneralNoteType$Owner.
     */
    public interface Owner extends org.apache.xmlbeans.XmlNMTOKEN {
        ElementFactory<gov.nih.nlm.ncbi.eutils.GeneralNoteType.Owner> Factory = new ElementFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "ownerab50attrtype");
        org.apache.xmlbeans.SchemaType type = Factory.getType();


        org.apache.xmlbeans.StringEnumAbstractBase getEnumValue();
        void setEnumValue(org.apache.xmlbeans.StringEnumAbstractBase e);

        Enum NLM = Enum.forString("NLM");
        Enum NASA = Enum.forString("NASA");
        Enum PIP = Enum.forString("PIP");
        Enum KIE = Enum.forString("KIE");
        Enum HSR = Enum.forString("HSR");
        Enum HMD = Enum.forString("HMD");

        int INT_NLM = Enum.INT_NLM;
        int INT_NASA = Enum.INT_NASA;
        int INT_PIP = Enum.INT_PIP;
        int INT_KIE = Enum.INT_KIE;
        int INT_HSR = Enum.INT_HSR;
        int INT_HMD = Enum.INT_HMD;

        /**
         * Enumeration value class for gov.nih.nlm.ncbi.eutils.GeneralNoteType$Owner.
         * These enum values can be used as follows:
         * <pre>
         * enum.toString(); // returns the string value of the enum
         * enum.intValue(); // returns an int value, useful for switches
         * // e.g., case Enum.INT_NLM
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

            static final int INT_NLM = 1;
            static final int INT_NASA = 2;
            static final int INT_PIP = 3;
            static final int INT_KIE = 4;
            static final int INT_HSR = 5;
            static final int INT_HMD = 6;

            public static final org.apache.xmlbeans.StringEnumAbstractBase.Table table =
                new org.apache.xmlbeans.StringEnumAbstractBase.Table(new Enum[] {
                new Enum("NLM", INT_NLM),
                new Enum("NASA", INT_NASA),
                new Enum("PIP", INT_PIP),
                new Enum("KIE", INT_KIE),
                new Enum("HSR", INT_HSR),
                new Enum("HMD", INT_HMD),
            });
            private static final long serialVersionUID = 1L;
            private java.lang.Object readResolve() {
                return forInt(intValue());
            }
        }
    }
}
