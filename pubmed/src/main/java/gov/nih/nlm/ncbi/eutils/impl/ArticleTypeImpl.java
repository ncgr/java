/*
 * XML Type:  ArticleType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.ArticleType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;

/**
 * An XML ArticleType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public class ArticleTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements gov.nih.nlm.ncbi.eutils.ArticleType {
    private static final long serialVersionUID = 1L;

    public ArticleTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "Journal"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "ArticleTitle"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "Pagination"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "ELocationID"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "Abstract"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "Affiliation"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "AuthorList"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "Language"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "DataBankList"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "GrantList"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "PublicationTypeList"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "VernacularTitle"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "ArticleDate"),
        new QName("", "PubModel"),
    };


    /**
     * Gets the "Journal" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.JournalType getJournal() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.JournalType target = null;
            target = (gov.nih.nlm.ncbi.eutils.JournalType)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "Journal" element
     */
    @Override
    public void setJournal(gov.nih.nlm.ncbi.eutils.JournalType journal) {
        generatedSetterHelperImpl(journal, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "Journal" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.JournalType addNewJournal() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.JournalType target = null;
            target = (gov.nih.nlm.ncbi.eutils.JournalType)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }

    /**
     * Gets the "ArticleTitle" element
     */
    @Override
    public java.lang.String getArticleTitle() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[1], 0);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "ArticleTitle" element
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetArticleTitle() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[1], 0);
            return target;
        }
    }

    /**
     * Sets the "ArticleTitle" element
     */
    @Override
    public void setArticleTitle(java.lang.String articleTitle) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[1], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[1]);
            }
            target.setStringValue(articleTitle);
        }
    }

    /**
     * Sets (as xml) the "ArticleTitle" element
     */
    @Override
    public void xsetArticleTitle(org.apache.xmlbeans.XmlString articleTitle) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[1], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PROPERTY_QNAME[1]);
            }
            target.set(articleTitle);
        }
    }

    /**
     * Gets the "Pagination" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.PaginationType getPagination() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.PaginationType target = null;
            target = (gov.nih.nlm.ncbi.eutils.PaginationType)get_store().find_element_user(PROPERTY_QNAME[2], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "Pagination" element
     */
    @Override
    public void setPagination(gov.nih.nlm.ncbi.eutils.PaginationType pagination) {
        generatedSetterHelperImpl(pagination, PROPERTY_QNAME[2], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "Pagination" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.PaginationType addNewPagination() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.PaginationType target = null;
            target = (gov.nih.nlm.ncbi.eutils.PaginationType)get_store().add_element_user(PROPERTY_QNAME[2]);
            return target;
        }
    }

    /**
     * Gets a List of "ELocationID" elements
     */
    @Override
    public java.util.List<gov.nih.nlm.ncbi.eutils.ELocationIDType> getELocationIDList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                this::getELocationIDArray,
                this::setELocationIDArray,
                this::insertNewELocationID,
                this::removeELocationID,
                this::sizeOfELocationIDArray
            );
        }
    }

    /**
     * Gets array of all "ELocationID" elements
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ELocationIDType[] getELocationIDArray() {
        return getXmlObjectArray(PROPERTY_QNAME[3], new gov.nih.nlm.ncbi.eutils.ELocationIDType[0]);
    }

    /**
     * Gets ith "ELocationID" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ELocationIDType getELocationIDArray(int i) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.ELocationIDType target = null;
            target = (gov.nih.nlm.ncbi.eutils.ELocationIDType)get_store().find_element_user(PROPERTY_QNAME[3], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /**
     * Returns number of "ELocationID" element
     */
    @Override
    public int sizeOfELocationIDArray() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[3]);
        }
    }

    /**
     * Sets array of all "ELocationID" element  WARNING: This method is not atomicaly synchronized.
     */
    @Override
    public void setELocationIDArray(gov.nih.nlm.ncbi.eutils.ELocationIDType[] eLocationIDArray) {
        check_orphaned();
        arraySetterHelper(eLocationIDArray, PROPERTY_QNAME[3]);
    }

    /**
     * Sets ith "ELocationID" element
     */
    @Override
    public void setELocationIDArray(int i, gov.nih.nlm.ncbi.eutils.ELocationIDType eLocationID) {
        generatedSetterHelperImpl(eLocationID, PROPERTY_QNAME[3], i, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
    }

    /**
     * Inserts and returns a new empty value (as xml) as the ith "ELocationID" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ELocationIDType insertNewELocationID(int i) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.ELocationIDType target = null;
            target = (gov.nih.nlm.ncbi.eutils.ELocationIDType)get_store().insert_element_user(PROPERTY_QNAME[3], i);
            return target;
        }
    }

    /**
     * Appends and returns a new empty value (as xml) as the last "ELocationID" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ELocationIDType addNewELocationID() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.ELocationIDType target = null;
            target = (gov.nih.nlm.ncbi.eutils.ELocationIDType)get_store().add_element_user(PROPERTY_QNAME[3]);
            return target;
        }
    }

    /**
     * Removes the ith "ELocationID" element
     */
    @Override
    public void removeELocationID(int i) {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[3], i);
        }
    }

    /**
     * Gets the "Abstract" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.AbstractType getAbstract() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.AbstractType target = null;
            target = (gov.nih.nlm.ncbi.eutils.AbstractType)get_store().find_element_user(PROPERTY_QNAME[4], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * True if has "Abstract" element
     */
    @Override
    public boolean isSetAbstract() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[4]) != 0;
        }
    }

    /**
     * Sets the "Abstract" element
     */
    @Override
    public void setAbstract(gov.nih.nlm.ncbi.eutils.AbstractType xabstract) {
        generatedSetterHelperImpl(xabstract, PROPERTY_QNAME[4], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "Abstract" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.AbstractType addNewAbstract() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.AbstractType target = null;
            target = (gov.nih.nlm.ncbi.eutils.AbstractType)get_store().add_element_user(PROPERTY_QNAME[4]);
            return target;
        }
    }

    /**
     * Unsets the "Abstract" element
     */
    @Override
    public void unsetAbstract() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[4], 0);
        }
    }

    /**
     * Gets the "Affiliation" element
     */
    @Override
    public java.lang.String getAffiliation() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[5], 0);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "Affiliation" element
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetAffiliation() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[5], 0);
            return target;
        }
    }

    /**
     * True if has "Affiliation" element
     */
    @Override
    public boolean isSetAffiliation() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[5]) != 0;
        }
    }

    /**
     * Sets the "Affiliation" element
     */
    @Override
    public void setAffiliation(java.lang.String affiliation) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[5], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[5]);
            }
            target.setStringValue(affiliation);
        }
    }

    /**
     * Sets (as xml) the "Affiliation" element
     */
    @Override
    public void xsetAffiliation(org.apache.xmlbeans.XmlString affiliation) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[5], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PROPERTY_QNAME[5]);
            }
            target.set(affiliation);
        }
    }

    /**
     * Unsets the "Affiliation" element
     */
    @Override
    public void unsetAffiliation() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[5], 0);
        }
    }

    /**
     * Gets the "AuthorList" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.AuthorListType getAuthorList() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.AuthorListType target = null;
            target = (gov.nih.nlm.ncbi.eutils.AuthorListType)get_store().find_element_user(PROPERTY_QNAME[6], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * True if has "AuthorList" element
     */
    @Override
    public boolean isSetAuthorList() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[6]) != 0;
        }
    }

    /**
     * Sets the "AuthorList" element
     */
    @Override
    public void setAuthorList(gov.nih.nlm.ncbi.eutils.AuthorListType authorList) {
        generatedSetterHelperImpl(authorList, PROPERTY_QNAME[6], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "AuthorList" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.AuthorListType addNewAuthorList() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.AuthorListType target = null;
            target = (gov.nih.nlm.ncbi.eutils.AuthorListType)get_store().add_element_user(PROPERTY_QNAME[6]);
            return target;
        }
    }

    /**
     * Unsets the "AuthorList" element
     */
    @Override
    public void unsetAuthorList() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[6], 0);
        }
    }

    /**
     * Gets a List of "Language" elements
     */
    @Override
    public java.util.List<java.lang.String> getLanguageList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListObject<>(
                this::getLanguageArray,
                this::setLanguageArray,
                this::insertLanguage,
                this::removeLanguage,
                this::sizeOfLanguageArray
            );
        }
    }

    /**
     * Gets array of all "Language" elements
     */
    @Override
    public java.lang.String[] getLanguageArray() {
        return getObjectArray(PROPERTY_QNAME[7], org.apache.xmlbeans.SimpleValue::getStringValue, String[]::new);
    }

    /**
     * Gets ith "Language" element
     */
    @Override
    public java.lang.String getLanguageArray(int i) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[7], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target.getStringValue();
        }
    }

    /**
     * Gets (as xml) a List of "Language" elements
     */
    @Override
    public java.util.List<org.apache.xmlbeans.XmlString> xgetLanguageList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                this::xgetLanguageArray,
                this::xsetLanguageArray,
                this::insertNewLanguage,
                this::removeLanguage,
                this::sizeOfLanguageArray
            );
        }
    }

    /**
     * Gets (as xml) array of all "Language" elements
     */
    @Override
    public org.apache.xmlbeans.XmlString[] xgetLanguageArray() {
        return xgetArray(PROPERTY_QNAME[7], org.apache.xmlbeans.XmlString[]::new);
    }

    /**
     * Gets (as xml) ith "Language" element
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetLanguageArray(int i) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[7], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /**
     * Returns number of "Language" element
     */
    @Override
    public int sizeOfLanguageArray() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[7]);
        }
    }

    /**
     * Sets array of all "Language" element
     */
    @Override
    public void setLanguageArray(java.lang.String[] languageArray) {
        synchronized (monitor()) {
            check_orphaned();
            arraySetterHelper(languageArray, PROPERTY_QNAME[7]);
        }
    }

    /**
     * Sets ith "Language" element
     */
    @Override
    public void setLanguageArray(int i, java.lang.String language) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[7], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setStringValue(language);
        }
    }

    /**
     * Sets (as xml) array of all "Language" element
     */
    @Override
    public void xsetLanguageArray(org.apache.xmlbeans.XmlString[]languageArray) {
        synchronized (monitor()) {
            check_orphaned();
            arraySetterHelper(languageArray, PROPERTY_QNAME[7]);
        }
    }

    /**
     * Sets (as xml) ith "Language" element
     */
    @Override
    public void xsetLanguageArray(int i, org.apache.xmlbeans.XmlString language) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[7], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(language);
        }
    }

    /**
     * Inserts the value as the ith "Language" element
     */
    @Override
    public void insertLanguage(int i, java.lang.String language) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target =
                (org.apache.xmlbeans.SimpleValue)get_store().insert_element_user(PROPERTY_QNAME[7], i);
            target.setStringValue(language);
        }
    }

    /**
     * Appends the value as the last "Language" element
     */
    @Override
    public void addLanguage(java.lang.String language) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[7]);
            target.setStringValue(language);
        }
    }

    /**
     * Inserts and returns a new empty value (as xml) as the ith "Language" element
     */
    @Override
    public org.apache.xmlbeans.XmlString insertNewLanguage(int i) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().insert_element_user(PROPERTY_QNAME[7], i);
            return target;
        }
    }

    /**
     * Appends and returns a new empty value (as xml) as the last "Language" element
     */
    @Override
    public org.apache.xmlbeans.XmlString addNewLanguage() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PROPERTY_QNAME[7]);
            return target;
        }
    }

    /**
     * Removes the ith "Language" element
     */
    @Override
    public void removeLanguage(int i) {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[7], i);
        }
    }

    /**
     * Gets the "DataBankList" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.DataBankListType getDataBankList() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.DataBankListType target = null;
            target = (gov.nih.nlm.ncbi.eutils.DataBankListType)get_store().find_element_user(PROPERTY_QNAME[8], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * True if has "DataBankList" element
     */
    @Override
    public boolean isSetDataBankList() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[8]) != 0;
        }
    }

    /**
     * Sets the "DataBankList" element
     */
    @Override
    public void setDataBankList(gov.nih.nlm.ncbi.eutils.DataBankListType dataBankList) {
        generatedSetterHelperImpl(dataBankList, PROPERTY_QNAME[8], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "DataBankList" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.DataBankListType addNewDataBankList() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.DataBankListType target = null;
            target = (gov.nih.nlm.ncbi.eutils.DataBankListType)get_store().add_element_user(PROPERTY_QNAME[8]);
            return target;
        }
    }

    /**
     * Unsets the "DataBankList" element
     */
    @Override
    public void unsetDataBankList() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[8], 0);
        }
    }

    /**
     * Gets the "GrantList" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.GrantListType getGrantList() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.GrantListType target = null;
            target = (gov.nih.nlm.ncbi.eutils.GrantListType)get_store().find_element_user(PROPERTY_QNAME[9], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * True if has "GrantList" element
     */
    @Override
    public boolean isSetGrantList() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[9]) != 0;
        }
    }

    /**
     * Sets the "GrantList" element
     */
    @Override
    public void setGrantList(gov.nih.nlm.ncbi.eutils.GrantListType grantList) {
        generatedSetterHelperImpl(grantList, PROPERTY_QNAME[9], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "GrantList" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.GrantListType addNewGrantList() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.GrantListType target = null;
            target = (gov.nih.nlm.ncbi.eutils.GrantListType)get_store().add_element_user(PROPERTY_QNAME[9]);
            return target;
        }
    }

    /**
     * Unsets the "GrantList" element
     */
    @Override
    public void unsetGrantList() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[9], 0);
        }
    }

    /**
     * Gets the "PublicationTypeList" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.PublicationTypeListType getPublicationTypeList() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.PublicationTypeListType target = null;
            target = (gov.nih.nlm.ncbi.eutils.PublicationTypeListType)get_store().find_element_user(PROPERTY_QNAME[10], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "PublicationTypeList" element
     */
    @Override
    public void setPublicationTypeList(gov.nih.nlm.ncbi.eutils.PublicationTypeListType publicationTypeList) {
        generatedSetterHelperImpl(publicationTypeList, PROPERTY_QNAME[10], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "PublicationTypeList" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.PublicationTypeListType addNewPublicationTypeList() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.PublicationTypeListType target = null;
            target = (gov.nih.nlm.ncbi.eutils.PublicationTypeListType)get_store().add_element_user(PROPERTY_QNAME[10]);
            return target;
        }
    }

    /**
     * Gets the "VernacularTitle" element
     */
    @Override
    public java.lang.String getVernacularTitle() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[11], 0);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "VernacularTitle" element
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetVernacularTitle() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[11], 0);
            return target;
        }
    }

    /**
     * True if has "VernacularTitle" element
     */
    @Override
    public boolean isSetVernacularTitle() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[11]) != 0;
        }
    }

    /**
     * Sets the "VernacularTitle" element
     */
    @Override
    public void setVernacularTitle(java.lang.String vernacularTitle) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[11], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[11]);
            }
            target.setStringValue(vernacularTitle);
        }
    }

    /**
     * Sets (as xml) the "VernacularTitle" element
     */
    @Override
    public void xsetVernacularTitle(org.apache.xmlbeans.XmlString vernacularTitle) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[11], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PROPERTY_QNAME[11]);
            }
            target.set(vernacularTitle);
        }
    }

    /**
     * Unsets the "VernacularTitle" element
     */
    @Override
    public void unsetVernacularTitle() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[11], 0);
        }
    }

    /**
     * Gets a List of "ArticleDate" elements
     */
    @Override
    public java.util.List<gov.nih.nlm.ncbi.eutils.ArticleDateType> getArticleDateList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                this::getArticleDateArray,
                this::setArticleDateArray,
                this::insertNewArticleDate,
                this::removeArticleDate,
                this::sizeOfArticleDateArray
            );
        }
    }

    /**
     * Gets array of all "ArticleDate" elements
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ArticleDateType[] getArticleDateArray() {
        return getXmlObjectArray(PROPERTY_QNAME[12], new gov.nih.nlm.ncbi.eutils.ArticleDateType[0]);
    }

    /**
     * Gets ith "ArticleDate" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ArticleDateType getArticleDateArray(int i) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.ArticleDateType target = null;
            target = (gov.nih.nlm.ncbi.eutils.ArticleDateType)get_store().find_element_user(PROPERTY_QNAME[12], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /**
     * Returns number of "ArticleDate" element
     */
    @Override
    public int sizeOfArticleDateArray() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[12]);
        }
    }

    /**
     * Sets array of all "ArticleDate" element  WARNING: This method is not atomicaly synchronized.
     */
    @Override
    public void setArticleDateArray(gov.nih.nlm.ncbi.eutils.ArticleDateType[] articleDateArray) {
        check_orphaned();
        arraySetterHelper(articleDateArray, PROPERTY_QNAME[12]);
    }

    /**
     * Sets ith "ArticleDate" element
     */
    @Override
    public void setArticleDateArray(int i, gov.nih.nlm.ncbi.eutils.ArticleDateType articleDate) {
        generatedSetterHelperImpl(articleDate, PROPERTY_QNAME[12], i, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
    }

    /**
     * Inserts and returns a new empty value (as xml) as the ith "ArticleDate" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ArticleDateType insertNewArticleDate(int i) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.ArticleDateType target = null;
            target = (gov.nih.nlm.ncbi.eutils.ArticleDateType)get_store().insert_element_user(PROPERTY_QNAME[12], i);
            return target;
        }
    }

    /**
     * Appends and returns a new empty value (as xml) as the last "ArticleDate" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ArticleDateType addNewArticleDate() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.ArticleDateType target = null;
            target = (gov.nih.nlm.ncbi.eutils.ArticleDateType)get_store().add_element_user(PROPERTY_QNAME[12]);
            return target;
        }
    }

    /**
     * Removes the ith "ArticleDate" element
     */
    @Override
    public void removeArticleDate(int i) {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[12], i);
        }
    }

    /**
     * Gets the "PubModel" attribute
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ArticleType.PubModel.Enum getPubModel() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[13]);
            return (target == null) ? null : (gov.nih.nlm.ncbi.eutils.ArticleType.PubModel.Enum)target.getEnumValue();
        }
    }

    /**
     * Gets (as xml) the "PubModel" attribute
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ArticleType.PubModel xgetPubModel() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.ArticleType.PubModel target = null;
            target = (gov.nih.nlm.ncbi.eutils.ArticleType.PubModel)get_store().find_attribute_user(PROPERTY_QNAME[13]);
            return target;
        }
    }

    /**
     * Sets the "PubModel" attribute
     */
    @Override
    public void setPubModel(gov.nih.nlm.ncbi.eutils.ArticleType.PubModel.Enum pubModel) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[13]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[13]);
            }
            target.setEnumValue(pubModel);
        }
    }

    /**
     * Sets (as xml) the "PubModel" attribute
     */
    @Override
    public void xsetPubModel(gov.nih.nlm.ncbi.eutils.ArticleType.PubModel pubModel) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.ArticleType.PubModel target = null;
            target = (gov.nih.nlm.ncbi.eutils.ArticleType.PubModel)get_store().find_attribute_user(PROPERTY_QNAME[13]);
            if (target == null) {
                target = (gov.nih.nlm.ncbi.eutils.ArticleType.PubModel)get_store().add_attribute_user(PROPERTY_QNAME[13]);
            }
            target.set(pubModel);
        }
    }
    /**
     * An XML PubModel(@).
     *
     * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.ArticleType$PubModel.
     */
    public static class PubModelImpl extends org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx implements gov.nih.nlm.ncbi.eutils.ArticleType.PubModel {
        private static final long serialVersionUID = 1L;

        public PubModelImpl(org.apache.xmlbeans.SchemaType sType) {
            super(sType, false);
        }

        protected PubModelImpl(org.apache.xmlbeans.SchemaType sType, boolean b) {
            super(sType, b);
        }
    }
}
