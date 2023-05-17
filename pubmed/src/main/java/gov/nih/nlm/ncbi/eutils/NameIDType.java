/*
 * XML Type:  NameIDType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.NameIDType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML NameIDType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.NameIDType.
 */
public interface NameIDType extends org.apache.xmlbeans.XmlString {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.NameIDType> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "nameidtype6ccdtype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "Source" attribute
     */
    gov.nih.nlm.ncbi.eutils.NameIDType.Source.Enum getSource();

    /**
     * Gets (as xml) the "Source" attribute
     */
    gov.nih.nlm.ncbi.eutils.NameIDType.Source xgetSource();

    /**
     * Sets the "Source" attribute
     */
    void setSource(gov.nih.nlm.ncbi.eutils.NameIDType.Source.Enum source);

    /**
     * Sets (as xml) the "Source" attribute
     */
    void xsetSource(gov.nih.nlm.ncbi.eutils.NameIDType.Source source);

    /**
     * An XML Source(@).
     *
     * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.NameIDType$Source.
     */
    public interface Source extends org.apache.xmlbeans.XmlNMTOKEN {
        ElementFactory<gov.nih.nlm.ncbi.eutils.NameIDType.Source> Factory = new ElementFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "source0228attrtype");
        org.apache.xmlbeans.SchemaType type = Factory.getType();


        org.apache.xmlbeans.StringEnumAbstractBase getEnumValue();
        void setEnumValue(org.apache.xmlbeans.StringEnumAbstractBase e);

        Enum NCBI = Enum.forString("NCBI");
        Enum PUBLISHER = Enum.forString("Publisher");
        Enum NISO = Enum.forString("NISO");
        Enum ISO = Enum.forString("ISO");

        int INT_NCBI = Enum.INT_NCBI;
        int INT_PUBLISHER = Enum.INT_PUBLISHER;
        int INT_NISO = Enum.INT_NISO;
        int INT_ISO = Enum.INT_ISO;

        /**
         * Enumeration value class for gov.nih.nlm.ncbi.eutils.NameIDType$Source.
         * These enum values can be used as follows:
         * <pre>
         * enum.toString(); // returns the string value of the enum
         * enum.intValue(); // returns an int value, useful for switches
         * // e.g., case Enum.INT_NCBI
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

            static final int INT_NCBI = 1;
            static final int INT_PUBLISHER = 2;
            static final int INT_NISO = 3;
            static final int INT_ISO = 4;

            public static final org.apache.xmlbeans.StringEnumAbstractBase.Table table =
                new org.apache.xmlbeans.StringEnumAbstractBase.Table(new Enum[] {
                new Enum("NCBI", INT_NCBI),
                new Enum("Publisher", INT_PUBLISHER),
                new Enum("NISO", INT_NISO),
                new Enum("ISO", INT_ISO),
            });
            private static final long serialVersionUID = 1L;
            private java.lang.Object readResolve() {
                return forInt(intValue());
            }
        }
    }
}
