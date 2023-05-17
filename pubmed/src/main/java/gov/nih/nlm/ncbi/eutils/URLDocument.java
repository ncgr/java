/*
 * An XML document type.
 * Localname: URL
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.URLDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * A document containing one URL(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public interface URLDocument extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.URLDocument> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "url0800doctype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "URL" element
     */
    gov.nih.nlm.ncbi.eutils.URLDocument.URL getURL();

    /**
     * Sets the "URL" element
     */
    void setURL(gov.nih.nlm.ncbi.eutils.URLDocument.URL url);

    /**
     * Appends and returns a new empty "URL" element
     */
    gov.nih.nlm.ncbi.eutils.URLDocument.URL addNewURL();

    /**
     * An XML URL(@http://www.ncbi.nlm.nih.gov/eutils).
     *
     * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.URLDocument$URL.
     */
    public interface URL extends org.apache.xmlbeans.XmlString {
        ElementFactory<gov.nih.nlm.ncbi.eutils.URLDocument.URL> Factory = new ElementFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "url9ca5elemtype");
        org.apache.xmlbeans.SchemaType type = Factory.getType();


        /**
         * Gets the "lang" attribute
         */
        gov.nih.nlm.ncbi.eutils.URLDocument.URL.Lang.Enum getLang();

        /**
         * Gets (as xml) the "lang" attribute
         */
        gov.nih.nlm.ncbi.eutils.URLDocument.URL.Lang xgetLang();

        /**
         * True if has "lang" attribute
         */
        boolean isSetLang();

        /**
         * Sets the "lang" attribute
         */
        void setLang(gov.nih.nlm.ncbi.eutils.URLDocument.URL.Lang.Enum lang);

        /**
         * Sets (as xml) the "lang" attribute
         */
        void xsetLang(gov.nih.nlm.ncbi.eutils.URLDocument.URL.Lang lang);

        /**
         * Unsets the "lang" attribute
         */
        void unsetLang();

        /**
         * Gets the "Type" attribute
         */
        gov.nih.nlm.ncbi.eutils.URLDocument.URL.Type.Enum getType();

        /**
         * Gets (as xml) the "Type" attribute
         */
        gov.nih.nlm.ncbi.eutils.URLDocument.URL.Type xgetType();

        /**
         * True if has "Type" attribute
         */
        boolean isSetType();

        /**
         * Sets the "Type" attribute
         */
        void setType(gov.nih.nlm.ncbi.eutils.URLDocument.URL.Type.Enum type);

        /**
         * Sets (as xml) the "Type" attribute
         */
        void xsetType(gov.nih.nlm.ncbi.eutils.URLDocument.URL.Type type);

        /**
         * Unsets the "Type" attribute
         */
        void unsetType();

        /**
         * An XML lang(@).
         *
         * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.URLDocument$URL$Lang.
         */
        public interface Lang extends org.apache.xmlbeans.XmlNMTOKEN {
            ElementFactory<gov.nih.nlm.ncbi.eutils.URLDocument.URL.Lang> Factory = new ElementFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "langcad7attrtype");
            org.apache.xmlbeans.SchemaType type = Factory.getType();


            org.apache.xmlbeans.StringEnumAbstractBase getEnumValue();
            void setEnumValue(org.apache.xmlbeans.StringEnumAbstractBase e);

            Enum AF = Enum.forString("AF");
            Enum AR = Enum.forString("AR");
            Enum AZ = Enum.forString("AZ");
            Enum BG = Enum.forString("BG");
            Enum CS = Enum.forString("CS");
            Enum DA = Enum.forString("DA");
            Enum DE = Enum.forString("DE");
            Enum EN = Enum.forString("EN");
            Enum EL = Enum.forString("EL");
            Enum ES = Enum.forString("ES");
            Enum FA = Enum.forString("FA");
            Enum FI = Enum.forString("FI");
            Enum FR = Enum.forString("FR");
            Enum HE = Enum.forString("HE");
            Enum HU = Enum.forString("HU");
            Enum HY = Enum.forString("HY");
            Enum IN = Enum.forString("IN");
            Enum IS = Enum.forString("IS");
            Enum IT = Enum.forString("IT");
            Enum IW = Enum.forString("IW");
            Enum JA = Enum.forString("JA");
            Enum KA = Enum.forString("KA");
            Enum KO = Enum.forString("KO");
            Enum LT = Enum.forString("LT");
            Enum MK = Enum.forString("MK");
            Enum ML = Enum.forString("ML");
            Enum NL = Enum.forString("NL");
            Enum NO = Enum.forString("NO");
            Enum PL = Enum.forString("PL");
            Enum PT = Enum.forString("PT");
            Enum PS = Enum.forString("PS");
            Enum RO = Enum.forString("RO");
            Enum RU = Enum.forString("RU");
            Enum SL = Enum.forString("SL");
            Enum SK = Enum.forString("SK");
            Enum SQ = Enum.forString("SQ");
            Enum SR = Enum.forString("SR");
            Enum SV = Enum.forString("SV");
            Enum SW = Enum.forString("SW");
            Enum TH = Enum.forString("TH");
            Enum TR = Enum.forString("TR");
            Enum UK = Enum.forString("UK");
            Enum VI = Enum.forString("VI");
            Enum ZH = Enum.forString("ZH");

            int INT_AF = Enum.INT_AF;
            int INT_AR = Enum.INT_AR;
            int INT_AZ = Enum.INT_AZ;
            int INT_BG = Enum.INT_BG;
            int INT_CS = Enum.INT_CS;
            int INT_DA = Enum.INT_DA;
            int INT_DE = Enum.INT_DE;
            int INT_EN = Enum.INT_EN;
            int INT_EL = Enum.INT_EL;
            int INT_ES = Enum.INT_ES;
            int INT_FA = Enum.INT_FA;
            int INT_FI = Enum.INT_FI;
            int INT_FR = Enum.INT_FR;
            int INT_HE = Enum.INT_HE;
            int INT_HU = Enum.INT_HU;
            int INT_HY = Enum.INT_HY;
            int INT_IN = Enum.INT_IN;
            int INT_IS = Enum.INT_IS;
            int INT_IT = Enum.INT_IT;
            int INT_IW = Enum.INT_IW;
            int INT_JA = Enum.INT_JA;
            int INT_KA = Enum.INT_KA;
            int INT_KO = Enum.INT_KO;
            int INT_LT = Enum.INT_LT;
            int INT_MK = Enum.INT_MK;
            int INT_ML = Enum.INT_ML;
            int INT_NL = Enum.INT_NL;
            int INT_NO = Enum.INT_NO;
            int INT_PL = Enum.INT_PL;
            int INT_PT = Enum.INT_PT;
            int INT_PS = Enum.INT_PS;
            int INT_RO = Enum.INT_RO;
            int INT_RU = Enum.INT_RU;
            int INT_SL = Enum.INT_SL;
            int INT_SK = Enum.INT_SK;
            int INT_SQ = Enum.INT_SQ;
            int INT_SR = Enum.INT_SR;
            int INT_SV = Enum.INT_SV;
            int INT_SW = Enum.INT_SW;
            int INT_TH = Enum.INT_TH;
            int INT_TR = Enum.INT_TR;
            int INT_UK = Enum.INT_UK;
            int INT_VI = Enum.INT_VI;
            int INT_ZH = Enum.INT_ZH;

            /**
             * Enumeration value class for gov.nih.nlm.ncbi.eutils.URLDocument$URL$Lang.
             * These enum values can be used as follows:
             * <pre>
             * enum.toString(); // returns the string value of the enum
             * enum.intValue(); // returns an int value, useful for switches
             * // e.g., case Enum.INT_AF
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

                static final int INT_AF = 1;
                static final int INT_AR = 2;
                static final int INT_AZ = 3;
                static final int INT_BG = 4;
                static final int INT_CS = 5;
                static final int INT_DA = 6;
                static final int INT_DE = 7;
                static final int INT_EN = 8;
                static final int INT_EL = 9;
                static final int INT_ES = 10;
                static final int INT_FA = 11;
                static final int INT_FI = 12;
                static final int INT_FR = 13;
                static final int INT_HE = 14;
                static final int INT_HU = 15;
                static final int INT_HY = 16;
                static final int INT_IN = 17;
                static final int INT_IS = 18;
                static final int INT_IT = 19;
                static final int INT_IW = 20;
                static final int INT_JA = 21;
                static final int INT_KA = 22;
                static final int INT_KO = 23;
                static final int INT_LT = 24;
                static final int INT_MK = 25;
                static final int INT_ML = 26;
                static final int INT_NL = 27;
                static final int INT_NO = 28;
                static final int INT_PL = 29;
                static final int INT_PT = 30;
                static final int INT_PS = 31;
                static final int INT_RO = 32;
                static final int INT_RU = 33;
                static final int INT_SL = 34;
                static final int INT_SK = 35;
                static final int INT_SQ = 36;
                static final int INT_SR = 37;
                static final int INT_SV = 38;
                static final int INT_SW = 39;
                static final int INT_TH = 40;
                static final int INT_TR = 41;
                static final int INT_UK = 42;
                static final int INT_VI = 43;
                static final int INT_ZH = 44;

                public static final org.apache.xmlbeans.StringEnumAbstractBase.Table table =
                    new org.apache.xmlbeans.StringEnumAbstractBase.Table(new Enum[] {
                    new Enum("AF", INT_AF),
                    new Enum("AR", INT_AR),
                    new Enum("AZ", INT_AZ),
                    new Enum("BG", INT_BG),
                    new Enum("CS", INT_CS),
                    new Enum("DA", INT_DA),
                    new Enum("DE", INT_DE),
                    new Enum("EN", INT_EN),
                    new Enum("EL", INT_EL),
                    new Enum("ES", INT_ES),
                    new Enum("FA", INT_FA),
                    new Enum("FI", INT_FI),
                    new Enum("FR", INT_FR),
                    new Enum("HE", INT_HE),
                    new Enum("HU", INT_HU),
                    new Enum("HY", INT_HY),
                    new Enum("IN", INT_IN),
                    new Enum("IS", INT_IS),
                    new Enum("IT", INT_IT),
                    new Enum("IW", INT_IW),
                    new Enum("JA", INT_JA),
                    new Enum("KA", INT_KA),
                    new Enum("KO", INT_KO),
                    new Enum("LT", INT_LT),
                    new Enum("MK", INT_MK),
                    new Enum("ML", INT_ML),
                    new Enum("NL", INT_NL),
                    new Enum("NO", INT_NO),
                    new Enum("PL", INT_PL),
                    new Enum("PT", INT_PT),
                    new Enum("PS", INT_PS),
                    new Enum("RO", INT_RO),
                    new Enum("RU", INT_RU),
                    new Enum("SL", INT_SL),
                    new Enum("SK", INT_SK),
                    new Enum("SQ", INT_SQ),
                    new Enum("SR", INT_SR),
                    new Enum("SV", INT_SV),
                    new Enum("SW", INT_SW),
                    new Enum("TH", INT_TH),
                    new Enum("TR", INT_TR),
                    new Enum("UK", INT_UK),
                    new Enum("VI", INT_VI),
                    new Enum("ZH", INT_ZH),
                });
                private static final long serialVersionUID = 1L;
                private java.lang.Object readResolve() {
                    return forInt(intValue());
                }
            }
        }

        /**
         * An XML Type(@).
         *
         * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.URLDocument$URL$Type.
         */
        public interface Type extends org.apache.xmlbeans.XmlNMTOKEN {
            ElementFactory<gov.nih.nlm.ncbi.eutils.URLDocument.URL.Type> Factory = new ElementFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "type83ebattrtype");
            org.apache.xmlbeans.SchemaType type = Factory.getType();


            org.apache.xmlbeans.StringEnumAbstractBase getEnumValue();
            void setEnumValue(org.apache.xmlbeans.StringEnumAbstractBase e);

            Enum FULL_TEXT = Enum.forString("FullText");
            Enum SUMMARY = Enum.forString("Summary");
            Enum FULLTEXT = Enum.forString("fulltext");
            Enum SUMMARY_2 = Enum.forString("summary");

            int INT_FULL_TEXT = Enum.INT_FULL_TEXT;
            int INT_SUMMARY = Enum.INT_SUMMARY;
            int INT_FULLTEXT = Enum.INT_FULLTEXT;
            int INT_SUMMARY_2 = Enum.INT_SUMMARY_2;

            /**
             * Enumeration value class for gov.nih.nlm.ncbi.eutils.URLDocument$URL$Type.
             * These enum values can be used as follows:
             * <pre>
             * enum.toString(); // returns the string value of the enum
             * enum.intValue(); // returns an int value, useful for switches
             * // e.g., case Enum.INT_FULL_TEXT
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

                static final int INT_FULL_TEXT = 1;
                static final int INT_SUMMARY = 2;
                static final int INT_FULLTEXT = 3;
                static final int INT_SUMMARY_2 = 4;

                public static final org.apache.xmlbeans.StringEnumAbstractBase.Table table =
                    new org.apache.xmlbeans.StringEnumAbstractBase.Table(new Enum[] {
                    new Enum("FullText", INT_FULL_TEXT),
                    new Enum("Summary", INT_SUMMARY),
                    new Enum("fulltext", INT_FULLTEXT),
                    new Enum("summary", INT_SUMMARY_2),
                });
                private static final long serialVersionUID = 1L;
                private java.lang.Object readResolve() {
                    return forInt(intValue());
                }
            }
        }
    }
}
