/*
 * XML Type:  ELocationIDType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.ELocationIDType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML ELocationIDType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.ELocationIDType.
 */
public interface ELocationIDType extends org.apache.xmlbeans.XmlString {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.ELocationIDType> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "elocationidtypee730type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "EIdType" attribute
     */
    gov.nih.nlm.ncbi.eutils.ELocationIDType.EIdType.Enum getEIdType();

    /**
     * Gets (as xml) the "EIdType" attribute
     */
    gov.nih.nlm.ncbi.eutils.ELocationIDType.EIdType xgetEIdType();

    /**
     * Sets the "EIdType" attribute
     */
    void setEIdType(gov.nih.nlm.ncbi.eutils.ELocationIDType.EIdType.Enum eIdType);

    /**
     * Sets (as xml) the "EIdType" attribute
     */
    void xsetEIdType(gov.nih.nlm.ncbi.eutils.ELocationIDType.EIdType eIdType);

    /**
     * Gets the "ValidYN" attribute
     */
    gov.nih.nlm.ncbi.eutils.ELocationIDType.ValidYN.Enum getValidYN();

    /**
     * Gets (as xml) the "ValidYN" attribute
     */
    gov.nih.nlm.ncbi.eutils.ELocationIDType.ValidYN xgetValidYN();

    /**
     * True if has "ValidYN" attribute
     */
    boolean isSetValidYN();

    /**
     * Sets the "ValidYN" attribute
     */
    void setValidYN(gov.nih.nlm.ncbi.eutils.ELocationIDType.ValidYN.Enum validYN);

    /**
     * Sets (as xml) the "ValidYN" attribute
     */
    void xsetValidYN(gov.nih.nlm.ncbi.eutils.ELocationIDType.ValidYN validYN);

    /**
     * Unsets the "ValidYN" attribute
     */
    void unsetValidYN();

    /**
     * An XML EIdType(@).
     *
     * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.ELocationIDType$EIdType.
     */
    public interface EIdType extends org.apache.xmlbeans.XmlNMTOKEN {
        ElementFactory<gov.nih.nlm.ncbi.eutils.ELocationIDType.EIdType> Factory = new ElementFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "eidtype146eattrtype");
        org.apache.xmlbeans.SchemaType type = Factory.getType();


        org.apache.xmlbeans.StringEnumAbstractBase getEnumValue();
        void setEnumValue(org.apache.xmlbeans.StringEnumAbstractBase e);

        Enum DOI = Enum.forString("doi");
        Enum PII = Enum.forString("pii");

        int INT_DOI = Enum.INT_DOI;
        int INT_PII = Enum.INT_PII;

        /**
         * Enumeration value class for gov.nih.nlm.ncbi.eutils.ELocationIDType$EIdType.
         * These enum values can be used as follows:
         * <pre>
         * enum.toString(); // returns the string value of the enum
         * enum.intValue(); // returns an int value, useful for switches
         * // e.g., case Enum.INT_DOI
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

            static final int INT_DOI = 1;
            static final int INT_PII = 2;

            public static final org.apache.xmlbeans.StringEnumAbstractBase.Table table =
                new org.apache.xmlbeans.StringEnumAbstractBase.Table(new Enum[] {
                new Enum("doi", INT_DOI),
                new Enum("pii", INT_PII),
            });
            private static final long serialVersionUID = 1L;
            private java.lang.Object readResolve() {
                return forInt(intValue());
            }
        }
    }

    /**
     * An XML ValidYN(@).
     *
     * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.ELocationIDType$ValidYN.
     */
    public interface ValidYN extends org.apache.xmlbeans.XmlNMTOKEN {
        ElementFactory<gov.nih.nlm.ncbi.eutils.ELocationIDType.ValidYN> Factory = new ElementFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "validyn6877attrtype");
        org.apache.xmlbeans.SchemaType type = Factory.getType();


        org.apache.xmlbeans.StringEnumAbstractBase getEnumValue();
        void setEnumValue(org.apache.xmlbeans.StringEnumAbstractBase e);

        Enum Y = Enum.forString("Y");
        Enum N = Enum.forString("N");

        int INT_Y = Enum.INT_Y;
        int INT_N = Enum.INT_N;

        /**
         * Enumeration value class for gov.nih.nlm.ncbi.eutils.ELocationIDType$ValidYN.
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
