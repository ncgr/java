/*
 * XML Type:  DescriptorNameType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.DescriptorNameType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML DescriptorNameType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.DescriptorNameType.
 */
public interface DescriptorNameType extends org.apache.xmlbeans.XmlString {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.DescriptorNameType> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "descriptornametypebd99type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "MajorTopicYN" attribute
     */
    gov.nih.nlm.ncbi.eutils.DescriptorNameType.MajorTopicYN.Enum getMajorTopicYN();

    /**
     * Gets (as xml) the "MajorTopicYN" attribute
     */
    gov.nih.nlm.ncbi.eutils.DescriptorNameType.MajorTopicYN xgetMajorTopicYN();

    /**
     * True if has "MajorTopicYN" attribute
     */
    boolean isSetMajorTopicYN();

    /**
     * Sets the "MajorTopicYN" attribute
     */
    void setMajorTopicYN(gov.nih.nlm.ncbi.eutils.DescriptorNameType.MajorTopicYN.Enum majorTopicYN);

    /**
     * Sets (as xml) the "MajorTopicYN" attribute
     */
    void xsetMajorTopicYN(gov.nih.nlm.ncbi.eutils.DescriptorNameType.MajorTopicYN majorTopicYN);

    /**
     * Unsets the "MajorTopicYN" attribute
     */
    void unsetMajorTopicYN();

    /**
     * An XML MajorTopicYN(@).
     *
     * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.DescriptorNameType$MajorTopicYN.
     */
    public interface MajorTopicYN extends org.apache.xmlbeans.XmlNMTOKEN {
        ElementFactory<gov.nih.nlm.ncbi.eutils.DescriptorNameType.MajorTopicYN> Factory = new ElementFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "majortopicyneb64attrtype");
        org.apache.xmlbeans.SchemaType type = Factory.getType();


        org.apache.xmlbeans.StringEnumAbstractBase getEnumValue();
        void setEnumValue(org.apache.xmlbeans.StringEnumAbstractBase e);

        Enum Y = Enum.forString("Y");
        Enum N = Enum.forString("N");

        int INT_Y = Enum.INT_Y;
        int INT_N = Enum.INT_N;

        /**
         * Enumeration value class for gov.nih.nlm.ncbi.eutils.DescriptorNameType$MajorTopicYN.
         * These enum values can be used as follows:
         * <pre>
         * enum.toString(); // returns the string value of the enum
         * enum.intValue(); // returns an int value, useful for switches
         * // e.g., case Enum.INT_Y
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

            static final int INT_Y = 1;
            static final int INT_N = 2;

            public static final org.apache.xmlbeans.StringEnumAbstractBase.Table table =
                new org.apache.xmlbeans.StringEnumAbstractBase.Table(new Enum[] {
                new Enum("Y", INT_Y),
                new Enum("N", INT_N),
            });
            private static final long serialVersionUID = 1L;
            private java.lang.Object readResolve() {
                return forInt(intValue());
            }
        }
    }
}
