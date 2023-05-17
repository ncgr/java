/*
 * XML Type:  OtherIDType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.OtherIDType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML OtherIDType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.OtherIDType.
 */
public interface OtherIDType extends org.apache.xmlbeans.XmlString {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.OtherIDType> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "otheridtype0c9atype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "Source" attribute
     */
    gov.nih.nlm.ncbi.eutils.OtherIDType.Source.Enum getSource();

    /**
     * Gets (as xml) the "Source" attribute
     */
    gov.nih.nlm.ncbi.eutils.OtherIDType.Source xgetSource();

    /**
     * Sets the "Source" attribute
     */
    void setSource(gov.nih.nlm.ncbi.eutils.OtherIDType.Source.Enum source);

    /**
     * Sets (as xml) the "Source" attribute
     */
    void xsetSource(gov.nih.nlm.ncbi.eutils.OtherIDType.Source source);

    /**
     * An XML Source(@).
     *
     * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.OtherIDType$Source.
     */
    public interface Source extends org.apache.xmlbeans.XmlNMTOKEN {
        ElementFactory<gov.nih.nlm.ncbi.eutils.OtherIDType.Source> Factory = new ElementFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "source229fattrtype");
        org.apache.xmlbeans.SchemaType type = Factory.getType();


        org.apache.xmlbeans.StringEnumAbstractBase getEnumValue();
        void setEnumValue(org.apache.xmlbeans.StringEnumAbstractBase e);

        Enum NASA = Enum.forString("NASA");
        Enum KIE = Enum.forString("KIE");
        Enum PIP = Enum.forString("PIP");
        Enum POP = Enum.forString("POP");
        Enum ARPL = Enum.forString("ARPL");
        Enum CPC = Enum.forString("CPC");
        Enum IND = Enum.forString("IND");
        Enum CPFH = Enum.forString("CPFH");
        Enum CLML = Enum.forString("CLML");
        Enum NRCBL = Enum.forString("NRCBL");
        Enum NLM = Enum.forString("NLM");

        int INT_NASA = Enum.INT_NASA;
        int INT_KIE = Enum.INT_KIE;
        int INT_PIP = Enum.INT_PIP;
        int INT_POP = Enum.INT_POP;
        int INT_ARPL = Enum.INT_ARPL;
        int INT_CPC = Enum.INT_CPC;
        int INT_IND = Enum.INT_IND;
        int INT_CPFH = Enum.INT_CPFH;
        int INT_CLML = Enum.INT_CLML;
        int INT_NRCBL = Enum.INT_NRCBL;
        int INT_NLM = Enum.INT_NLM;

        /**
         * Enumeration value class for gov.nih.nlm.ncbi.eutils.OtherIDType$Source.
         * These enum values can be used as follows:
         * <pre>
         * enum.toString(); // returns the string value of the enum
         * enum.intValue(); // returns an int value, useful for switches
         * // e.g., case Enum.INT_NASA
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

            static final int INT_NASA = 1;
            static final int INT_KIE = 2;
            static final int INT_PIP = 3;
            static final int INT_POP = 4;
            static final int INT_ARPL = 5;
            static final int INT_CPC = 6;
            static final int INT_IND = 7;
            static final int INT_CPFH = 8;
            static final int INT_CLML = 9;
            static final int INT_NRCBL = 10;
            static final int INT_NLM = 11;

            public static final org.apache.xmlbeans.StringEnumAbstractBase.Table table =
                new org.apache.xmlbeans.StringEnumAbstractBase.Table(new Enum[] {
                new Enum("NASA", INT_NASA),
                new Enum("KIE", INT_KIE),
                new Enum("PIP", INT_PIP),
                new Enum("POP", INT_POP),
                new Enum("ARPL", INT_ARPL),
                new Enum("CPC", INT_CPC),
                new Enum("IND", INT_IND),
                new Enum("CPFH", INT_CPFH),
                new Enum("CLML", INT_CLML),
                new Enum("NRCBL", INT_NRCBL),
                new Enum("NLM", INT_NLM),
            });
            private static final long serialVersionUID = 1L;
            private java.lang.Object readResolve() {
                return forInt(intValue());
            }
        }
    }
}
