/*
 * XML Type:  ArticleIdType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.ArticleIdType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML ArticleIdType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.ArticleIdType.
 */
public interface ArticleIdType extends org.apache.xmlbeans.XmlString {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.ArticleIdType> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "articleidtypea714type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "IdType" attribute
     */
    gov.nih.nlm.ncbi.eutils.ArticleIdType.IdType.Enum getIdType();

    /**
     * Gets (as xml) the "IdType" attribute
     */
    gov.nih.nlm.ncbi.eutils.ArticleIdType.IdType xgetIdType();

    /**
     * True if has "IdType" attribute
     */
    boolean isSetIdType();

    /**
     * Sets the "IdType" attribute
     */
    void setIdType(gov.nih.nlm.ncbi.eutils.ArticleIdType.IdType.Enum idType);

    /**
     * Sets (as xml) the "IdType" attribute
     */
    void xsetIdType(gov.nih.nlm.ncbi.eutils.ArticleIdType.IdType idType);

    /**
     * Unsets the "IdType" attribute
     */
    void unsetIdType();

    /**
     * An XML IdType(@).
     *
     * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.ArticleIdType$IdType.
     */
    public interface IdType extends org.apache.xmlbeans.XmlNMTOKEN {
        ElementFactory<gov.nih.nlm.ncbi.eutils.ArticleIdType.IdType> Factory = new ElementFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "idtypee2bfattrtype");
        org.apache.xmlbeans.SchemaType type = Factory.getType();


        org.apache.xmlbeans.StringEnumAbstractBase getEnumValue();
        void setEnumValue(org.apache.xmlbeans.StringEnumAbstractBase e);

        Enum DOI = Enum.forString("doi");
        Enum PII = Enum.forString("pii");
        Enum PMCPID = Enum.forString("pmcpid");
        Enum PMPID = Enum.forString("pmpid");
        Enum PMC = Enum.forString("pmc");
        Enum MID = Enum.forString("mid");
        Enum SICI = Enum.forString("sici");
        Enum PUBMED = Enum.forString("pubmed");
        Enum MEDLINE = Enum.forString("medline");
        Enum PMCID = Enum.forString("pmcid");

        int INT_DOI = Enum.INT_DOI;
        int INT_PII = Enum.INT_PII;
        int INT_PMCPID = Enum.INT_PMCPID;
        int INT_PMPID = Enum.INT_PMPID;
        int INT_PMC = Enum.INT_PMC;
        int INT_MID = Enum.INT_MID;
        int INT_SICI = Enum.INT_SICI;
        int INT_PUBMED = Enum.INT_PUBMED;
        int INT_MEDLINE = Enum.INT_MEDLINE;
        int INT_PMCID = Enum.INT_PMCID;

        /**
         * Enumeration value class for gov.nih.nlm.ncbi.eutils.ArticleIdType$IdType.
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
            static final int INT_PMCPID = 3;
            static final int INT_PMPID = 4;
            static final int INT_PMC = 5;
            static final int INT_MID = 6;
            static final int INT_SICI = 7;
            static final int INT_PUBMED = 8;
            static final int INT_MEDLINE = 9;
            static final int INT_PMCID = 10;

            public static final org.apache.xmlbeans.StringEnumAbstractBase.Table table =
                new org.apache.xmlbeans.StringEnumAbstractBase.Table(new Enum[] {
                new Enum("doi", INT_DOI),
                new Enum("pii", INT_PII),
                new Enum("pmcpid", INT_PMCPID),
                new Enum("pmpid", INT_PMPID),
                new Enum("pmc", INT_PMC),
                new Enum("mid", INT_MID),
                new Enum("sici", INT_SICI),
                new Enum("pubmed", INT_PUBMED),
                new Enum("medline", INT_MEDLINE),
                new Enum("pmcid", INT_PMCID),
            });
            private static final long serialVersionUID = 1L;
            private java.lang.Object readResolve() {
                return forInt(intValue());
            }
        }
    }
}
