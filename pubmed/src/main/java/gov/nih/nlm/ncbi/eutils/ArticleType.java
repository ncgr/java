/*
 * XML Type:  ArticleType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.ArticleType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML ArticleType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public interface ArticleType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.ArticleType> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "articletype456ftype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "Journal" element
     */
    gov.nih.nlm.ncbi.eutils.JournalType getJournal();

    /**
     * Sets the "Journal" element
     */
    void setJournal(gov.nih.nlm.ncbi.eutils.JournalType journal);

    /**
     * Appends and returns a new empty "Journal" element
     */
    gov.nih.nlm.ncbi.eutils.JournalType addNewJournal();

    /**
     * Gets the "ArticleTitle" element
     */
    java.lang.String getArticleTitle();

    /**
     * Gets (as xml) the "ArticleTitle" element
     */
    org.apache.xmlbeans.XmlString xgetArticleTitle();

    /**
     * Sets the "ArticleTitle" element
     */
    void setArticleTitle(java.lang.String articleTitle);

    /**
     * Sets (as xml) the "ArticleTitle" element
     */
    void xsetArticleTitle(org.apache.xmlbeans.XmlString articleTitle);

    /**
     * Gets the "Pagination" element
     */
    gov.nih.nlm.ncbi.eutils.PaginationType getPagination();

    /**
     * Sets the "Pagination" element
     */
    void setPagination(gov.nih.nlm.ncbi.eutils.PaginationType pagination);

    /**
     * Appends and returns a new empty "Pagination" element
     */
    gov.nih.nlm.ncbi.eutils.PaginationType addNewPagination();

    /**
     * Gets a List of "ELocationID" elements
     */
    java.util.List<gov.nih.nlm.ncbi.eutils.ELocationIDType> getELocationIDList();

    /**
     * Gets array of all "ELocationID" elements
     */
    gov.nih.nlm.ncbi.eutils.ELocationIDType[] getELocationIDArray();

    /**
     * Gets ith "ELocationID" element
     */
    gov.nih.nlm.ncbi.eutils.ELocationIDType getELocationIDArray(int i);

    /**
     * Returns number of "ELocationID" element
     */
    int sizeOfELocationIDArray();

    /**
     * Sets array of all "ELocationID" element
     */
    void setELocationIDArray(gov.nih.nlm.ncbi.eutils.ELocationIDType[] eLocationIDArray);

    /**
     * Sets ith "ELocationID" element
     */
    void setELocationIDArray(int i, gov.nih.nlm.ncbi.eutils.ELocationIDType eLocationID);

    /**
     * Inserts and returns a new empty value (as xml) as the ith "ELocationID" element
     */
    gov.nih.nlm.ncbi.eutils.ELocationIDType insertNewELocationID(int i);

    /**
     * Appends and returns a new empty value (as xml) as the last "ELocationID" element
     */
    gov.nih.nlm.ncbi.eutils.ELocationIDType addNewELocationID();

    /**
     * Removes the ith "ELocationID" element
     */
    void removeELocationID(int i);

    /**
     * Gets the "Abstract" element
     */
    gov.nih.nlm.ncbi.eutils.AbstractType getAbstract();

    /**
     * True if has "Abstract" element
     */
    boolean isSetAbstract();

    /**
     * Sets the "Abstract" element
     */
    void setAbstract(gov.nih.nlm.ncbi.eutils.AbstractType xabstract);

    /**
     * Appends and returns a new empty "Abstract" element
     */
    gov.nih.nlm.ncbi.eutils.AbstractType addNewAbstract();

    /**
     * Unsets the "Abstract" element
     */
    void unsetAbstract();

    /**
     * Gets the "Affiliation" element
     */
    java.lang.String getAffiliation();

    /**
     * Gets (as xml) the "Affiliation" element
     */
    org.apache.xmlbeans.XmlString xgetAffiliation();

    /**
     * True if has "Affiliation" element
     */
    boolean isSetAffiliation();

    /**
     * Sets the "Affiliation" element
     */
    void setAffiliation(java.lang.String affiliation);

    /**
     * Sets (as xml) the "Affiliation" element
     */
    void xsetAffiliation(org.apache.xmlbeans.XmlString affiliation);

    /**
     * Unsets the "Affiliation" element
     */
    void unsetAffiliation();

    /**
     * Gets the "AuthorList" element
     */
    gov.nih.nlm.ncbi.eutils.AuthorListType getAuthorList();

    /**
     * True if has "AuthorList" element
     */
    boolean isSetAuthorList();

    /**
     * Sets the "AuthorList" element
     */
    void setAuthorList(gov.nih.nlm.ncbi.eutils.AuthorListType authorList);

    /**
     * Appends and returns a new empty "AuthorList" element
     */
    gov.nih.nlm.ncbi.eutils.AuthorListType addNewAuthorList();

    /**
     * Unsets the "AuthorList" element
     */
    void unsetAuthorList();

    /**
     * Gets a List of "Language" elements
     */
    java.util.List<java.lang.String> getLanguageList();

    /**
     * Gets array of all "Language" elements
     */
    java.lang.String[] getLanguageArray();

    /**
     * Gets ith "Language" element
     */
    java.lang.String getLanguageArray(int i);

    /**
     * Gets (as xml) a List of "Language" elements
     */
    java.util.List<org.apache.xmlbeans.XmlString> xgetLanguageList();

    /**
     * Gets (as xml) array of all "Language" elements
     */
    org.apache.xmlbeans.XmlString[] xgetLanguageArray();

    /**
     * Gets (as xml) ith "Language" element
     */
    org.apache.xmlbeans.XmlString xgetLanguageArray(int i);

    /**
     * Returns number of "Language" element
     */
    int sizeOfLanguageArray();

    /**
     * Sets array of all "Language" element
     */
    void setLanguageArray(java.lang.String[] languageArray);

    /**
     * Sets ith "Language" element
     */
    void setLanguageArray(int i, java.lang.String language);

    /**
     * Sets (as xml) array of all "Language" element
     */
    void xsetLanguageArray(org.apache.xmlbeans.XmlString[] languageArray);

    /**
     * Sets (as xml) ith "Language" element
     */
    void xsetLanguageArray(int i, org.apache.xmlbeans.XmlString language);

    /**
     * Inserts the value as the ith "Language" element
     */
    void insertLanguage(int i, java.lang.String language);

    /**
     * Appends the value as the last "Language" element
     */
    void addLanguage(java.lang.String language);

    /**
     * Inserts and returns a new empty value (as xml) as the ith "Language" element
     */
    org.apache.xmlbeans.XmlString insertNewLanguage(int i);

    /**
     * Appends and returns a new empty value (as xml) as the last "Language" element
     */
    org.apache.xmlbeans.XmlString addNewLanguage();

    /**
     * Removes the ith "Language" element
     */
    void removeLanguage(int i);

    /**
     * Gets the "DataBankList" element
     */
    gov.nih.nlm.ncbi.eutils.DataBankListType getDataBankList();

    /**
     * True if has "DataBankList" element
     */
    boolean isSetDataBankList();

    /**
     * Sets the "DataBankList" element
     */
    void setDataBankList(gov.nih.nlm.ncbi.eutils.DataBankListType dataBankList);

    /**
     * Appends and returns a new empty "DataBankList" element
     */
    gov.nih.nlm.ncbi.eutils.DataBankListType addNewDataBankList();

    /**
     * Unsets the "DataBankList" element
     */
    void unsetDataBankList();

    /**
     * Gets the "GrantList" element
     */
    gov.nih.nlm.ncbi.eutils.GrantListType getGrantList();

    /**
     * True if has "GrantList" element
     */
    boolean isSetGrantList();

    /**
     * Sets the "GrantList" element
     */
    void setGrantList(gov.nih.nlm.ncbi.eutils.GrantListType grantList);

    /**
     * Appends and returns a new empty "GrantList" element
     */
    gov.nih.nlm.ncbi.eutils.GrantListType addNewGrantList();

    /**
     * Unsets the "GrantList" element
     */
    void unsetGrantList();

    /**
     * Gets the "PublicationTypeList" element
     */
    gov.nih.nlm.ncbi.eutils.PublicationTypeListType getPublicationTypeList();

    /**
     * Sets the "PublicationTypeList" element
     */
    void setPublicationTypeList(gov.nih.nlm.ncbi.eutils.PublicationTypeListType publicationTypeList);

    /**
     * Appends and returns a new empty "PublicationTypeList" element
     */
    gov.nih.nlm.ncbi.eutils.PublicationTypeListType addNewPublicationTypeList();

    /**
     * Gets the "VernacularTitle" element
     */
    java.lang.String getVernacularTitle();

    /**
     * Gets (as xml) the "VernacularTitle" element
     */
    org.apache.xmlbeans.XmlString xgetVernacularTitle();

    /**
     * True if has "VernacularTitle" element
     */
    boolean isSetVernacularTitle();

    /**
     * Sets the "VernacularTitle" element
     */
    void setVernacularTitle(java.lang.String vernacularTitle);

    /**
     * Sets (as xml) the "VernacularTitle" element
     */
    void xsetVernacularTitle(org.apache.xmlbeans.XmlString vernacularTitle);

    /**
     * Unsets the "VernacularTitle" element
     */
    void unsetVernacularTitle();

    /**
     * Gets a List of "ArticleDate" elements
     */
    java.util.List<gov.nih.nlm.ncbi.eutils.ArticleDateType> getArticleDateList();

    /**
     * Gets array of all "ArticleDate" elements
     */
    gov.nih.nlm.ncbi.eutils.ArticleDateType[] getArticleDateArray();

    /**
     * Gets ith "ArticleDate" element
     */
    gov.nih.nlm.ncbi.eutils.ArticleDateType getArticleDateArray(int i);

    /**
     * Returns number of "ArticleDate" element
     */
    int sizeOfArticleDateArray();

    /**
     * Sets array of all "ArticleDate" element
     */
    void setArticleDateArray(gov.nih.nlm.ncbi.eutils.ArticleDateType[] articleDateArray);

    /**
     * Sets ith "ArticleDate" element
     */
    void setArticleDateArray(int i, gov.nih.nlm.ncbi.eutils.ArticleDateType articleDate);

    /**
     * Inserts and returns a new empty value (as xml) as the ith "ArticleDate" element
     */
    gov.nih.nlm.ncbi.eutils.ArticleDateType insertNewArticleDate(int i);

    /**
     * Appends and returns a new empty value (as xml) as the last "ArticleDate" element
     */
    gov.nih.nlm.ncbi.eutils.ArticleDateType addNewArticleDate();

    /**
     * Removes the ith "ArticleDate" element
     */
    void removeArticleDate(int i);

    /**
     * Gets the "PubModel" attribute
     */
    gov.nih.nlm.ncbi.eutils.ArticleType.PubModel.Enum getPubModel();

    /**
     * Gets (as xml) the "PubModel" attribute
     */
    gov.nih.nlm.ncbi.eutils.ArticleType.PubModel xgetPubModel();

    /**
     * Sets the "PubModel" attribute
     */
    void setPubModel(gov.nih.nlm.ncbi.eutils.ArticleType.PubModel.Enum pubModel);

    /**
     * Sets (as xml) the "PubModel" attribute
     */
    void xsetPubModel(gov.nih.nlm.ncbi.eutils.ArticleType.PubModel pubModel);

    /**
     * An XML PubModel(@).
     *
     * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.ArticleType$PubModel.
     */
    public interface PubModel extends org.apache.xmlbeans.XmlNMTOKEN {
        ElementFactory<gov.nih.nlm.ncbi.eutils.ArticleType.PubModel> Factory = new ElementFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "pubmodeldc83attrtype");
        org.apache.xmlbeans.SchemaType type = Factory.getType();


        org.apache.xmlbeans.StringEnumAbstractBase getEnumValue();
        void setEnumValue(org.apache.xmlbeans.StringEnumAbstractBase e);

        Enum PRINT = Enum.forString("Print");
        Enum PRINT_ELECTRONIC = Enum.forString("Print-Electronic");
        Enum ELECTRONIC = Enum.forString("Electronic");
        Enum ELECTRONIC_PRINT = Enum.forString("Electronic-Print");

        int INT_PRINT = Enum.INT_PRINT;
        int INT_PRINT_ELECTRONIC = Enum.INT_PRINT_ELECTRONIC;
        int INT_ELECTRONIC = Enum.INT_ELECTRONIC;
        int INT_ELECTRONIC_PRINT = Enum.INT_ELECTRONIC_PRINT;

        /**
         * Enumeration value class for gov.nih.nlm.ncbi.eutils.ArticleType$PubModel.
         * These enum values can be used as follows:
         * <pre>
         * enum.toString(); // returns the string value of the enum
         * enum.intValue(); // returns an int value, useful for switches
         * // e.g., case Enum.INT_PRINT
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

            static final int INT_PRINT = 1;
            static final int INT_PRINT_ELECTRONIC = 2;
            static final int INT_ELECTRONIC = 3;
            static final int INT_ELECTRONIC_PRINT = 4;

            public static final org.apache.xmlbeans.StringEnumAbstractBase.Table table =
                new org.apache.xmlbeans.StringEnumAbstractBase.Table(new Enum[] {
                new Enum("Print", INT_PRINT),
                new Enum("Print-Electronic", INT_PRINT_ELECTRONIC),
                new Enum("Electronic", INT_ELECTRONIC),
                new Enum("Electronic-Print", INT_ELECTRONIC_PRINT),
            });
            private static final long serialVersionUID = 1L;
            private java.lang.Object readResolve() {
                return forInt(intValue());
            }
        }
    }
}
