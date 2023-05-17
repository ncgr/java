/*
 * XML Type:  OtherAbstractType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.OtherAbstractType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML OtherAbstractType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public interface OtherAbstractType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.OtherAbstractType> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "otherabstracttype24d3type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "AbstractText" element
     */
    java.lang.String getAbstractText();

    /**
     * Gets (as xml) the "AbstractText" element
     */
    org.apache.xmlbeans.XmlString xgetAbstractText();

    /**
     * Sets the "AbstractText" element
     */
    void setAbstractText(java.lang.String abstractText);

    /**
     * Sets (as xml) the "AbstractText" element
     */
    void xsetAbstractText(org.apache.xmlbeans.XmlString abstractText);

    /**
     * Gets the "CopyrightInformation" element
     */
    java.lang.String getCopyrightInformation();

    /**
     * Gets (as xml) the "CopyrightInformation" element
     */
    org.apache.xmlbeans.XmlString xgetCopyrightInformation();

    /**
     * True if has "CopyrightInformation" element
     */
    boolean isSetCopyrightInformation();

    /**
     * Sets the "CopyrightInformation" element
     */
    void setCopyrightInformation(java.lang.String copyrightInformation);

    /**
     * Sets (as xml) the "CopyrightInformation" element
     */
    void xsetCopyrightInformation(org.apache.xmlbeans.XmlString copyrightInformation);

    /**
     * Unsets the "CopyrightInformation" element
     */
    void unsetCopyrightInformation();

    /**
     * Gets the "Type" attribute
     */
    gov.nih.nlm.ncbi.eutils.OtherAbstractType.Type.Enum getType();

    /**
     * Gets (as xml) the "Type" attribute
     */
    gov.nih.nlm.ncbi.eutils.OtherAbstractType.Type xgetType();

    /**
     * Sets the "Type" attribute
     */
    void setType(gov.nih.nlm.ncbi.eutils.OtherAbstractType.Type.Enum type);

    /**
     * Sets (as xml) the "Type" attribute
     */
    void xsetType(gov.nih.nlm.ncbi.eutils.OtherAbstractType.Type type);

    /**
     * An XML Type(@).
     *
     * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.OtherAbstractType$Type.
     */
    public interface Type extends org.apache.xmlbeans.XmlNMTOKEN {
        ElementFactory<gov.nih.nlm.ncbi.eutils.OtherAbstractType.Type> Factory = new ElementFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "type6619attrtype");
        org.apache.xmlbeans.SchemaType type = Factory.getType();


        org.apache.xmlbeans.StringEnumAbstractBase getEnumValue();
        void setEnumValue(org.apache.xmlbeans.StringEnumAbstractBase e);

        Enum AAMC = Enum.forString("AAMC");
        Enum AIDS = Enum.forString("AIDS");
        Enum KIE = Enum.forString("KIE");
        Enum PIP = Enum.forString("PIP");
        Enum NASA = Enum.forString("NASA");
        Enum PUBLISHER = Enum.forString("Publisher");

        int INT_AAMC = Enum.INT_AAMC;
        int INT_AIDS = Enum.INT_AIDS;
        int INT_KIE = Enum.INT_KIE;
        int INT_PIP = Enum.INT_PIP;
        int INT_NASA = Enum.INT_NASA;
        int INT_PUBLISHER = Enum.INT_PUBLISHER;

        /**
         * Enumeration value class for gov.nih.nlm.ncbi.eutils.OtherAbstractType$Type.
         * These enum values can be used as follows:
         * <pre>
         * enum.toString(); // returns the string value of the enum
         * enum.intValue(); // returns an int value, useful for switches
         * // e.g., case Enum.INT_AAMC
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

            static final int INT_AAMC = 1;
            static final int INT_AIDS = 2;
            static final int INT_KIE = 3;
            static final int INT_PIP = 4;
            static final int INT_NASA = 5;
            static final int INT_PUBLISHER = 6;

            public static final org.apache.xmlbeans.StringEnumAbstractBase.Table table =
                new org.apache.xmlbeans.StringEnumAbstractBase.Table(new Enum[] {
                new Enum("AAMC", INT_AAMC),
                new Enum("AIDS", INT_AIDS),
                new Enum("KIE", INT_KIE),
                new Enum("PIP", INT_PIP),
                new Enum("NASA", INT_NASA),
                new Enum("Publisher", INT_PUBLISHER),
            });
            private static final long serialVersionUID = 1L;
            private java.lang.Object readResolve() {
                return forInt(intValue());
            }
        }
    }
}
