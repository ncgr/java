/*
 * XML Type:  ISSNType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.ISSNType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML ISSNType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.ISSNType.
 */
public interface ISSNType extends org.apache.xmlbeans.XmlString {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.ISSNType> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "issntypea5aetype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "IssnType" attribute
     */
    gov.nih.nlm.ncbi.eutils.ISSNType.IssnType.Enum getIssnType();

    /**
     * Gets (as xml) the "IssnType" attribute
     */
    gov.nih.nlm.ncbi.eutils.ISSNType.IssnType xgetIssnType();

    /**
     * Sets the "IssnType" attribute
     */
    void setIssnType(gov.nih.nlm.ncbi.eutils.ISSNType.IssnType.Enum issnType);

    /**
     * Sets (as xml) the "IssnType" attribute
     */
    void xsetIssnType(gov.nih.nlm.ncbi.eutils.ISSNType.IssnType issnType);

    /**
     * An XML IssnType(@).
     *
     * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.ISSNType$IssnType.
     */
    public interface IssnType extends org.apache.xmlbeans.XmlNMTOKEN {
        ElementFactory<gov.nih.nlm.ncbi.eutils.ISSNType.IssnType> Factory = new ElementFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "issntypec98dattrtype");
        org.apache.xmlbeans.SchemaType type = Factory.getType();


        org.apache.xmlbeans.StringEnumAbstractBase getEnumValue();
        void setEnumValue(org.apache.xmlbeans.StringEnumAbstractBase e);

        Enum ELECTRONIC = Enum.forString("Electronic");
        Enum PRINT = Enum.forString("Print");

        int INT_ELECTRONIC = Enum.INT_ELECTRONIC;
        int INT_PRINT = Enum.INT_PRINT;

        /**
         * Enumeration value class for gov.nih.nlm.ncbi.eutils.ISSNType$IssnType.
         * These enum values can be used as follows:
         * <pre>
         * enum.toString(); // returns the string value of the enum
         * enum.intValue(); // returns an int value, useful for switches
         * // e.g., case Enum.INT_ELECTRONIC
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

            static final int INT_ELECTRONIC = 1;
            static final int INT_PRINT = 2;

            public static final org.apache.xmlbeans.StringEnumAbstractBase.Table table =
                new org.apache.xmlbeans.StringEnumAbstractBase.Table(new Enum[] {
                new Enum("Electronic", INT_ELECTRONIC),
                new Enum("Print", INT_PRINT),
            });
            private static final long serialVersionUID = 1L;
            private java.lang.Object readResolve() {
                return forInt(intValue());
            }
        }
    }
}
