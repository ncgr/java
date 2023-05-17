/*
 * XML Type:  MedlineCitationType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.MedlineCitationType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;

/**
 * An XML MedlineCitationType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public class MedlineCitationTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements gov.nih.nlm.ncbi.eutils.MedlineCitationType {
    private static final long serialVersionUID = 1L;

    public MedlineCitationTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "PMID"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "DateCreated"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "DateCompleted"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "DateRevised"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "Article"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "MedlineJournalInfo"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "ChemicalList"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "CitationSubset"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "CommentsCorrectionsList"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "GeneSymbolList"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "MeshHeadingList"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "NumberOfReferences"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "PersonalNameSubjectList"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "OtherID"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "OtherAbstract"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "KeywordList"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "SpaceFlightMission"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "InvestigatorList"),
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "GeneralNote"),
        new QName("", "Owner"),
        new QName("", "Status"),
    };


    /**
     * Gets the "PMID" element
     */
    @Override
    public java.lang.String getPMID() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "PMID" element
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetPMID() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return target;
        }
    }

    /**
     * Sets the "PMID" element
     */
    @Override
    public void setPMID(java.lang.String pmid) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[0]);
            }
            target.setStringValue(pmid);
        }
    }

    /**
     * Sets (as xml) the "PMID" element
     */
    @Override
    public void xsetPMID(org.apache.xmlbeans.XmlString pmid) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PROPERTY_QNAME[0]);
            }
            target.set(pmid);
        }
    }

    /**
     * Gets the "DateCreated" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.DateCreatedType getDateCreated() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.DateCreatedType target = null;
            target = (gov.nih.nlm.ncbi.eutils.DateCreatedType)get_store().find_element_user(PROPERTY_QNAME[1], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "DateCreated" element
     */
    @Override
    public void setDateCreated(gov.nih.nlm.ncbi.eutils.DateCreatedType dateCreated) {
        generatedSetterHelperImpl(dateCreated, PROPERTY_QNAME[1], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "DateCreated" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.DateCreatedType addNewDateCreated() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.DateCreatedType target = null;
            target = (gov.nih.nlm.ncbi.eutils.DateCreatedType)get_store().add_element_user(PROPERTY_QNAME[1]);
            return target;
        }
    }

    /**
     * Gets the "DateCompleted" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.DateCompletedType getDateCompleted() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.DateCompletedType target = null;
            target = (gov.nih.nlm.ncbi.eutils.DateCompletedType)get_store().find_element_user(PROPERTY_QNAME[2], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * True if has "DateCompleted" element
     */
    @Override
    public boolean isSetDateCompleted() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[2]) != 0;
        }
    }

    /**
     * Sets the "DateCompleted" element
     */
    @Override
    public void setDateCompleted(gov.nih.nlm.ncbi.eutils.DateCompletedType dateCompleted) {
        generatedSetterHelperImpl(dateCompleted, PROPERTY_QNAME[2], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "DateCompleted" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.DateCompletedType addNewDateCompleted() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.DateCompletedType target = null;
            target = (gov.nih.nlm.ncbi.eutils.DateCompletedType)get_store().add_element_user(PROPERTY_QNAME[2]);
            return target;
        }
    }

    /**
     * Unsets the "DateCompleted" element
     */
    @Override
    public void unsetDateCompleted() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[2], 0);
        }
    }

    /**
     * Gets the "DateRevised" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.DateRevisedType getDateRevised() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.DateRevisedType target = null;
            target = (gov.nih.nlm.ncbi.eutils.DateRevisedType)get_store().find_element_user(PROPERTY_QNAME[3], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * True if has "DateRevised" element
     */
    @Override
    public boolean isSetDateRevised() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[3]) != 0;
        }
    }

    /**
     * Sets the "DateRevised" element
     */
    @Override
    public void setDateRevised(gov.nih.nlm.ncbi.eutils.DateRevisedType dateRevised) {
        generatedSetterHelperImpl(dateRevised, PROPERTY_QNAME[3], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "DateRevised" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.DateRevisedType addNewDateRevised() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.DateRevisedType target = null;
            target = (gov.nih.nlm.ncbi.eutils.DateRevisedType)get_store().add_element_user(PROPERTY_QNAME[3]);
            return target;
        }
    }

    /**
     * Unsets the "DateRevised" element
     */
    @Override
    public void unsetDateRevised() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[3], 0);
        }
    }

    /**
     * Gets the "Article" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ArticleType getArticle() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.ArticleType target = null;
            target = (gov.nih.nlm.ncbi.eutils.ArticleType)get_store().find_element_user(PROPERTY_QNAME[4], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "Article" element
     */
    @Override
    public void setArticle(gov.nih.nlm.ncbi.eutils.ArticleType article) {
        generatedSetterHelperImpl(article, PROPERTY_QNAME[4], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "Article" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ArticleType addNewArticle() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.ArticleType target = null;
            target = (gov.nih.nlm.ncbi.eutils.ArticleType)get_store().add_element_user(PROPERTY_QNAME[4]);
            return target;
        }
    }

    /**
     * Gets the "MedlineJournalInfo" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.MedlineJournalInfoType getMedlineJournalInfo() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.MedlineJournalInfoType target = null;
            target = (gov.nih.nlm.ncbi.eutils.MedlineJournalInfoType)get_store().find_element_user(PROPERTY_QNAME[5], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "MedlineJournalInfo" element
     */
    @Override
    public void setMedlineJournalInfo(gov.nih.nlm.ncbi.eutils.MedlineJournalInfoType medlineJournalInfo) {
        generatedSetterHelperImpl(medlineJournalInfo, PROPERTY_QNAME[5], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "MedlineJournalInfo" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.MedlineJournalInfoType addNewMedlineJournalInfo() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.MedlineJournalInfoType target = null;
            target = (gov.nih.nlm.ncbi.eutils.MedlineJournalInfoType)get_store().add_element_user(PROPERTY_QNAME[5]);
            return target;
        }
    }

    /**
     * Gets the "ChemicalList" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ChemicalListType getChemicalList() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.ChemicalListType target = null;
            target = (gov.nih.nlm.ncbi.eutils.ChemicalListType)get_store().find_element_user(PROPERTY_QNAME[6], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * True if has "ChemicalList" element
     */
    @Override
    public boolean isSetChemicalList() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[6]) != 0;
        }
    }

    /**
     * Sets the "ChemicalList" element
     */
    @Override
    public void setChemicalList(gov.nih.nlm.ncbi.eutils.ChemicalListType chemicalList) {
        generatedSetterHelperImpl(chemicalList, PROPERTY_QNAME[6], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "ChemicalList" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.ChemicalListType addNewChemicalList() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.ChemicalListType target = null;
            target = (gov.nih.nlm.ncbi.eutils.ChemicalListType)get_store().add_element_user(PROPERTY_QNAME[6]);
            return target;
        }
    }

    /**
     * Unsets the "ChemicalList" element
     */
    @Override
    public void unsetChemicalList() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[6], 0);
        }
    }

    /**
     * Gets a List of "CitationSubset" elements
     */
    @Override
    public java.util.List<java.lang.String> getCitationSubsetList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListObject<>(
                this::getCitationSubsetArray,
                this::setCitationSubsetArray,
                this::insertCitationSubset,
                this::removeCitationSubset,
                this::sizeOfCitationSubsetArray
            );
        }
    }

    /**
     * Gets array of all "CitationSubset" elements
     */
    @Override
    public java.lang.String[] getCitationSubsetArray() {
        return getObjectArray(PROPERTY_QNAME[7], org.apache.xmlbeans.SimpleValue::getStringValue, String[]::new);
    }

    /**
     * Gets ith "CitationSubset" element
     */
    @Override
    public java.lang.String getCitationSubsetArray(int i) {
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
     * Gets (as xml) a List of "CitationSubset" elements
     */
    @Override
    public java.util.List<org.apache.xmlbeans.XmlString> xgetCitationSubsetList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                this::xgetCitationSubsetArray,
                this::xsetCitationSubsetArray,
                this::insertNewCitationSubset,
                this::removeCitationSubset,
                this::sizeOfCitationSubsetArray
            );
        }
    }

    /**
     * Gets (as xml) array of all "CitationSubset" elements
     */
    @Override
    public org.apache.xmlbeans.XmlString[] xgetCitationSubsetArray() {
        return xgetArray(PROPERTY_QNAME[7], org.apache.xmlbeans.XmlString[]::new);
    }

    /**
     * Gets (as xml) ith "CitationSubset" element
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetCitationSubsetArray(int i) {
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
     * Returns number of "CitationSubset" element
     */
    @Override
    public int sizeOfCitationSubsetArray() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[7]);
        }
    }

    /**
     * Sets array of all "CitationSubset" element
     */
    @Override
    public void setCitationSubsetArray(java.lang.String[] citationSubsetArray) {
        synchronized (monitor()) {
            check_orphaned();
            arraySetterHelper(citationSubsetArray, PROPERTY_QNAME[7]);
        }
    }

    /**
     * Sets ith "CitationSubset" element
     */
    @Override
    public void setCitationSubsetArray(int i, java.lang.String citationSubset) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[7], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setStringValue(citationSubset);
        }
    }

    /**
     * Sets (as xml) array of all "CitationSubset" element
     */
    @Override
    public void xsetCitationSubsetArray(org.apache.xmlbeans.XmlString[]citationSubsetArray) {
        synchronized (monitor()) {
            check_orphaned();
            arraySetterHelper(citationSubsetArray, PROPERTY_QNAME[7]);
        }
    }

    /**
     * Sets (as xml) ith "CitationSubset" element
     */
    @Override
    public void xsetCitationSubsetArray(int i, org.apache.xmlbeans.XmlString citationSubset) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[7], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(citationSubset);
        }
    }

    /**
     * Inserts the value as the ith "CitationSubset" element
     */
    @Override
    public void insertCitationSubset(int i, java.lang.String citationSubset) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target =
                (org.apache.xmlbeans.SimpleValue)get_store().insert_element_user(PROPERTY_QNAME[7], i);
            target.setStringValue(citationSubset);
        }
    }

    /**
     * Appends the value as the last "CitationSubset" element
     */
    @Override
    public void addCitationSubset(java.lang.String citationSubset) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[7]);
            target.setStringValue(citationSubset);
        }
    }

    /**
     * Inserts and returns a new empty value (as xml) as the ith "CitationSubset" element
     */
    @Override
    public org.apache.xmlbeans.XmlString insertNewCitationSubset(int i) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().insert_element_user(PROPERTY_QNAME[7], i);
            return target;
        }
    }

    /**
     * Appends and returns a new empty value (as xml) as the last "CitationSubset" element
     */
    @Override
    public org.apache.xmlbeans.XmlString addNewCitationSubset() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PROPERTY_QNAME[7]);
            return target;
        }
    }

    /**
     * Removes the ith "CitationSubset" element
     */
    @Override
    public void removeCitationSubset(int i) {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[7], i);
        }
    }

    /**
     * Gets the "CommentsCorrectionsList" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.CommentsCorrectionsListType getCommentsCorrectionsList() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.CommentsCorrectionsListType target = null;
            target = (gov.nih.nlm.ncbi.eutils.CommentsCorrectionsListType)get_store().find_element_user(PROPERTY_QNAME[8], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * True if has "CommentsCorrectionsList" element
     */
    @Override
    public boolean isSetCommentsCorrectionsList() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[8]) != 0;
        }
    }

    /**
     * Sets the "CommentsCorrectionsList" element
     */
    @Override
    public void setCommentsCorrectionsList(gov.nih.nlm.ncbi.eutils.CommentsCorrectionsListType commentsCorrectionsList) {
        generatedSetterHelperImpl(commentsCorrectionsList, PROPERTY_QNAME[8], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "CommentsCorrectionsList" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.CommentsCorrectionsListType addNewCommentsCorrectionsList() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.CommentsCorrectionsListType target = null;
            target = (gov.nih.nlm.ncbi.eutils.CommentsCorrectionsListType)get_store().add_element_user(PROPERTY_QNAME[8]);
            return target;
        }
    }

    /**
     * Unsets the "CommentsCorrectionsList" element
     */
    @Override
    public void unsetCommentsCorrectionsList() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[8], 0);
        }
    }

    /**
     * Gets the "GeneSymbolList" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.GeneSymbolListType getGeneSymbolList() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.GeneSymbolListType target = null;
            target = (gov.nih.nlm.ncbi.eutils.GeneSymbolListType)get_store().find_element_user(PROPERTY_QNAME[9], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * True if has "GeneSymbolList" element
     */
    @Override
    public boolean isSetGeneSymbolList() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[9]) != 0;
        }
    }

    /**
     * Sets the "GeneSymbolList" element
     */
    @Override
    public void setGeneSymbolList(gov.nih.nlm.ncbi.eutils.GeneSymbolListType geneSymbolList) {
        generatedSetterHelperImpl(geneSymbolList, PROPERTY_QNAME[9], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "GeneSymbolList" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.GeneSymbolListType addNewGeneSymbolList() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.GeneSymbolListType target = null;
            target = (gov.nih.nlm.ncbi.eutils.GeneSymbolListType)get_store().add_element_user(PROPERTY_QNAME[9]);
            return target;
        }
    }

    /**
     * Unsets the "GeneSymbolList" element
     */
    @Override
    public void unsetGeneSymbolList() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[9], 0);
        }
    }

    /**
     * Gets the "MeshHeadingList" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.MeshHeadingListType getMeshHeadingList() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.MeshHeadingListType target = null;
            target = (gov.nih.nlm.ncbi.eutils.MeshHeadingListType)get_store().find_element_user(PROPERTY_QNAME[10], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * True if has "MeshHeadingList" element
     */
    @Override
    public boolean isSetMeshHeadingList() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[10]) != 0;
        }
    }

    /**
     * Sets the "MeshHeadingList" element
     */
    @Override
    public void setMeshHeadingList(gov.nih.nlm.ncbi.eutils.MeshHeadingListType meshHeadingList) {
        generatedSetterHelperImpl(meshHeadingList, PROPERTY_QNAME[10], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "MeshHeadingList" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.MeshHeadingListType addNewMeshHeadingList() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.MeshHeadingListType target = null;
            target = (gov.nih.nlm.ncbi.eutils.MeshHeadingListType)get_store().add_element_user(PROPERTY_QNAME[10]);
            return target;
        }
    }

    /**
     * Unsets the "MeshHeadingList" element
     */
    @Override
    public void unsetMeshHeadingList() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[10], 0);
        }
    }

    /**
     * Gets the "NumberOfReferences" element
     */
    @Override
    public java.lang.String getNumberOfReferences() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[11], 0);
            return (target == null) ? null : target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "NumberOfReferences" element
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetNumberOfReferences() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[11], 0);
            return target;
        }
    }

    /**
     * True if has "NumberOfReferences" element
     */
    @Override
    public boolean isSetNumberOfReferences() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[11]) != 0;
        }
    }

    /**
     * Sets the "NumberOfReferences" element
     */
    @Override
    public void setNumberOfReferences(java.lang.String numberOfReferences) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[11], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[11]);
            }
            target.setStringValue(numberOfReferences);
        }
    }

    /**
     * Sets (as xml) the "NumberOfReferences" element
     */
    @Override
    public void xsetNumberOfReferences(org.apache.xmlbeans.XmlString numberOfReferences) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[11], 0);
            if (target == null) {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PROPERTY_QNAME[11]);
            }
            target.set(numberOfReferences);
        }
    }

    /**
     * Unsets the "NumberOfReferences" element
     */
    @Override
    public void unsetNumberOfReferences() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[11], 0);
        }
    }

    /**
     * Gets the "PersonalNameSubjectList" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.PersonalNameSubjectListType getPersonalNameSubjectList() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.PersonalNameSubjectListType target = null;
            target = (gov.nih.nlm.ncbi.eutils.PersonalNameSubjectListType)get_store().find_element_user(PROPERTY_QNAME[12], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * True if has "PersonalNameSubjectList" element
     */
    @Override
    public boolean isSetPersonalNameSubjectList() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[12]) != 0;
        }
    }

    /**
     * Sets the "PersonalNameSubjectList" element
     */
    @Override
    public void setPersonalNameSubjectList(gov.nih.nlm.ncbi.eutils.PersonalNameSubjectListType personalNameSubjectList) {
        generatedSetterHelperImpl(personalNameSubjectList, PROPERTY_QNAME[12], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "PersonalNameSubjectList" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.PersonalNameSubjectListType addNewPersonalNameSubjectList() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.PersonalNameSubjectListType target = null;
            target = (gov.nih.nlm.ncbi.eutils.PersonalNameSubjectListType)get_store().add_element_user(PROPERTY_QNAME[12]);
            return target;
        }
    }

    /**
     * Unsets the "PersonalNameSubjectList" element
     */
    @Override
    public void unsetPersonalNameSubjectList() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[12], 0);
        }
    }

    /**
     * Gets a List of "OtherID" elements
     */
    @Override
    public java.util.List<gov.nih.nlm.ncbi.eutils.OtherIDType> getOtherIDList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                this::getOtherIDArray,
                this::setOtherIDArray,
                this::insertNewOtherID,
                this::removeOtherID,
                this::sizeOfOtherIDArray
            );
        }
    }

    /**
     * Gets array of all "OtherID" elements
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.OtherIDType[] getOtherIDArray() {
        return getXmlObjectArray(PROPERTY_QNAME[13], new gov.nih.nlm.ncbi.eutils.OtherIDType[0]);
    }

    /**
     * Gets ith "OtherID" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.OtherIDType getOtherIDArray(int i) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.OtherIDType target = null;
            target = (gov.nih.nlm.ncbi.eutils.OtherIDType)get_store().find_element_user(PROPERTY_QNAME[13], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /**
     * Returns number of "OtherID" element
     */
    @Override
    public int sizeOfOtherIDArray() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[13]);
        }
    }

    /**
     * Sets array of all "OtherID" element  WARNING: This method is not atomicaly synchronized.
     */
    @Override
    public void setOtherIDArray(gov.nih.nlm.ncbi.eutils.OtherIDType[] otherIDArray) {
        check_orphaned();
        arraySetterHelper(otherIDArray, PROPERTY_QNAME[13]);
    }

    /**
     * Sets ith "OtherID" element
     */
    @Override
    public void setOtherIDArray(int i, gov.nih.nlm.ncbi.eutils.OtherIDType otherID) {
        generatedSetterHelperImpl(otherID, PROPERTY_QNAME[13], i, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
    }

    /**
     * Inserts and returns a new empty value (as xml) as the ith "OtherID" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.OtherIDType insertNewOtherID(int i) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.OtherIDType target = null;
            target = (gov.nih.nlm.ncbi.eutils.OtherIDType)get_store().insert_element_user(PROPERTY_QNAME[13], i);
            return target;
        }
    }

    /**
     * Appends and returns a new empty value (as xml) as the last "OtherID" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.OtherIDType addNewOtherID() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.OtherIDType target = null;
            target = (gov.nih.nlm.ncbi.eutils.OtherIDType)get_store().add_element_user(PROPERTY_QNAME[13]);
            return target;
        }
    }

    /**
     * Removes the ith "OtherID" element
     */
    @Override
    public void removeOtherID(int i) {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[13], i);
        }
    }

    /**
     * Gets a List of "OtherAbstract" elements
     */
    @Override
    public java.util.List<gov.nih.nlm.ncbi.eutils.OtherAbstractType> getOtherAbstractList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                this::getOtherAbstractArray,
                this::setOtherAbstractArray,
                this::insertNewOtherAbstract,
                this::removeOtherAbstract,
                this::sizeOfOtherAbstractArray
            );
        }
    }

    /**
     * Gets array of all "OtherAbstract" elements
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.OtherAbstractType[] getOtherAbstractArray() {
        return getXmlObjectArray(PROPERTY_QNAME[14], new gov.nih.nlm.ncbi.eutils.OtherAbstractType[0]);
    }

    /**
     * Gets ith "OtherAbstract" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.OtherAbstractType getOtherAbstractArray(int i) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.OtherAbstractType target = null;
            target = (gov.nih.nlm.ncbi.eutils.OtherAbstractType)get_store().find_element_user(PROPERTY_QNAME[14], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /**
     * Returns number of "OtherAbstract" element
     */
    @Override
    public int sizeOfOtherAbstractArray() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[14]);
        }
    }

    /**
     * Sets array of all "OtherAbstract" element  WARNING: This method is not atomicaly synchronized.
     */
    @Override
    public void setOtherAbstractArray(gov.nih.nlm.ncbi.eutils.OtherAbstractType[] otherAbstractArray) {
        check_orphaned();
        arraySetterHelper(otherAbstractArray, PROPERTY_QNAME[14]);
    }

    /**
     * Sets ith "OtherAbstract" element
     */
    @Override
    public void setOtherAbstractArray(int i, gov.nih.nlm.ncbi.eutils.OtherAbstractType otherAbstract) {
        generatedSetterHelperImpl(otherAbstract, PROPERTY_QNAME[14], i, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
    }

    /**
     * Inserts and returns a new empty value (as xml) as the ith "OtherAbstract" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.OtherAbstractType insertNewOtherAbstract(int i) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.OtherAbstractType target = null;
            target = (gov.nih.nlm.ncbi.eutils.OtherAbstractType)get_store().insert_element_user(PROPERTY_QNAME[14], i);
            return target;
        }
    }

    /**
     * Appends and returns a new empty value (as xml) as the last "OtherAbstract" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.OtherAbstractType addNewOtherAbstract() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.OtherAbstractType target = null;
            target = (gov.nih.nlm.ncbi.eutils.OtherAbstractType)get_store().add_element_user(PROPERTY_QNAME[14]);
            return target;
        }
    }

    /**
     * Removes the ith "OtherAbstract" element
     */
    @Override
    public void removeOtherAbstract(int i) {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[14], i);
        }
    }

    /**
     * Gets a List of "KeywordList" elements
     */
    @Override
    public java.util.List<gov.nih.nlm.ncbi.eutils.KeywordListType> getKeywordListList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                this::getKeywordListArray,
                this::setKeywordListArray,
                this::insertNewKeywordList,
                this::removeKeywordList,
                this::sizeOfKeywordListArray
            );
        }
    }

    /**
     * Gets array of all "KeywordList" elements
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.KeywordListType[] getKeywordListArray() {
        return getXmlObjectArray(PROPERTY_QNAME[15], new gov.nih.nlm.ncbi.eutils.KeywordListType[0]);
    }

    /**
     * Gets ith "KeywordList" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.KeywordListType getKeywordListArray(int i) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.KeywordListType target = null;
            target = (gov.nih.nlm.ncbi.eutils.KeywordListType)get_store().find_element_user(PROPERTY_QNAME[15], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /**
     * Returns number of "KeywordList" element
     */
    @Override
    public int sizeOfKeywordListArray() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[15]);
        }
    }

    /**
     * Sets array of all "KeywordList" element  WARNING: This method is not atomicaly synchronized.
     */
    @Override
    public void setKeywordListArray(gov.nih.nlm.ncbi.eutils.KeywordListType[] keywordListArray) {
        check_orphaned();
        arraySetterHelper(keywordListArray, PROPERTY_QNAME[15]);
    }

    /**
     * Sets ith "KeywordList" element
     */
    @Override
    public void setKeywordListArray(int i, gov.nih.nlm.ncbi.eutils.KeywordListType keywordList) {
        generatedSetterHelperImpl(keywordList, PROPERTY_QNAME[15], i, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
    }

    /**
     * Inserts and returns a new empty value (as xml) as the ith "KeywordList" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.KeywordListType insertNewKeywordList(int i) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.KeywordListType target = null;
            target = (gov.nih.nlm.ncbi.eutils.KeywordListType)get_store().insert_element_user(PROPERTY_QNAME[15], i);
            return target;
        }
    }

    /**
     * Appends and returns a new empty value (as xml) as the last "KeywordList" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.KeywordListType addNewKeywordList() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.KeywordListType target = null;
            target = (gov.nih.nlm.ncbi.eutils.KeywordListType)get_store().add_element_user(PROPERTY_QNAME[15]);
            return target;
        }
    }

    /**
     * Removes the ith "KeywordList" element
     */
    @Override
    public void removeKeywordList(int i) {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[15], i);
        }
    }

    /**
     * Gets a List of "SpaceFlightMission" elements
     */
    @Override
    public java.util.List<java.lang.String> getSpaceFlightMissionList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListObject<>(
                this::getSpaceFlightMissionArray,
                this::setSpaceFlightMissionArray,
                this::insertSpaceFlightMission,
                this::removeSpaceFlightMission,
                this::sizeOfSpaceFlightMissionArray
            );
        }
    }

    /**
     * Gets array of all "SpaceFlightMission" elements
     */
    @Override
    public java.lang.String[] getSpaceFlightMissionArray() {
        return getObjectArray(PROPERTY_QNAME[16], org.apache.xmlbeans.SimpleValue::getStringValue, String[]::new);
    }

    /**
     * Gets ith "SpaceFlightMission" element
     */
    @Override
    public java.lang.String getSpaceFlightMissionArray(int i) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[16], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target.getStringValue();
        }
    }

    /**
     * Gets (as xml) a List of "SpaceFlightMission" elements
     */
    @Override
    public java.util.List<org.apache.xmlbeans.XmlString> xgetSpaceFlightMissionList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                this::xgetSpaceFlightMissionArray,
                this::xsetSpaceFlightMissionArray,
                this::insertNewSpaceFlightMission,
                this::removeSpaceFlightMission,
                this::sizeOfSpaceFlightMissionArray
            );
        }
    }

    /**
     * Gets (as xml) array of all "SpaceFlightMission" elements
     */
    @Override
    public org.apache.xmlbeans.XmlString[] xgetSpaceFlightMissionArray() {
        return xgetArray(PROPERTY_QNAME[16], org.apache.xmlbeans.XmlString[]::new);
    }

    /**
     * Gets (as xml) ith "SpaceFlightMission" element
     */
    @Override
    public org.apache.xmlbeans.XmlString xgetSpaceFlightMissionArray(int i) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[16], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /**
     * Returns number of "SpaceFlightMission" element
     */
    @Override
    public int sizeOfSpaceFlightMissionArray() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[16]);
        }
    }

    /**
     * Sets array of all "SpaceFlightMission" element
     */
    @Override
    public void setSpaceFlightMissionArray(java.lang.String[] spaceFlightMissionArray) {
        synchronized (monitor()) {
            check_orphaned();
            arraySetterHelper(spaceFlightMissionArray, PROPERTY_QNAME[16]);
        }
    }

    /**
     * Sets ith "SpaceFlightMission" element
     */
    @Override
    public void setSpaceFlightMissionArray(int i, java.lang.String spaceFlightMission) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PROPERTY_QNAME[16], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.setStringValue(spaceFlightMission);
        }
    }

    /**
     * Sets (as xml) array of all "SpaceFlightMission" element
     */
    @Override
    public void xsetSpaceFlightMissionArray(org.apache.xmlbeans.XmlString[]spaceFlightMissionArray) {
        synchronized (monitor()) {
            check_orphaned();
            arraySetterHelper(spaceFlightMissionArray, PROPERTY_QNAME[16]);
        }
    }

    /**
     * Sets (as xml) ith "SpaceFlightMission" element
     */
    @Override
    public void xsetSpaceFlightMissionArray(int i, org.apache.xmlbeans.XmlString spaceFlightMission) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PROPERTY_QNAME[16], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            target.set(spaceFlightMission);
        }
    }

    /**
     * Inserts the value as the ith "SpaceFlightMission" element
     */
    @Override
    public void insertSpaceFlightMission(int i, java.lang.String spaceFlightMission) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target =
                (org.apache.xmlbeans.SimpleValue)get_store().insert_element_user(PROPERTY_QNAME[16], i);
            target.setStringValue(spaceFlightMission);
        }
    }

    /**
     * Appends the value as the last "SpaceFlightMission" element
     */
    @Override
    public void addSpaceFlightMission(java.lang.String spaceFlightMission) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PROPERTY_QNAME[16]);
            target.setStringValue(spaceFlightMission);
        }
    }

    /**
     * Inserts and returns a new empty value (as xml) as the ith "SpaceFlightMission" element
     */
    @Override
    public org.apache.xmlbeans.XmlString insertNewSpaceFlightMission(int i) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().insert_element_user(PROPERTY_QNAME[16], i);
            return target;
        }
    }

    /**
     * Appends and returns a new empty value (as xml) as the last "SpaceFlightMission" element
     */
    @Override
    public org.apache.xmlbeans.XmlString addNewSpaceFlightMission() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PROPERTY_QNAME[16]);
            return target;
        }
    }

    /**
     * Removes the ith "SpaceFlightMission" element
     */
    @Override
    public void removeSpaceFlightMission(int i) {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[16], i);
        }
    }

    /**
     * Gets the "InvestigatorList" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.InvestigatorListType getInvestigatorList() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.InvestigatorListType target = null;
            target = (gov.nih.nlm.ncbi.eutils.InvestigatorListType)get_store().find_element_user(PROPERTY_QNAME[17], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * True if has "InvestigatorList" element
     */
    @Override
    public boolean isSetInvestigatorList() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[17]) != 0;
        }
    }

    /**
     * Sets the "InvestigatorList" element
     */
    @Override
    public void setInvestigatorList(gov.nih.nlm.ncbi.eutils.InvestigatorListType investigatorList) {
        generatedSetterHelperImpl(investigatorList, PROPERTY_QNAME[17], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "InvestigatorList" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.InvestigatorListType addNewInvestigatorList() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.InvestigatorListType target = null;
            target = (gov.nih.nlm.ncbi.eutils.InvestigatorListType)get_store().add_element_user(PROPERTY_QNAME[17]);
            return target;
        }
    }

    /**
     * Unsets the "InvestigatorList" element
     */
    @Override
    public void unsetInvestigatorList() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[17], 0);
        }
    }

    /**
     * Gets a List of "GeneralNote" elements
     */
    @Override
    public java.util.List<gov.nih.nlm.ncbi.eutils.GeneralNoteType> getGeneralNoteList() {
        synchronized (monitor()) {
            check_orphaned();
            return new org.apache.xmlbeans.impl.values.JavaListXmlObject<>(
                this::getGeneralNoteArray,
                this::setGeneralNoteArray,
                this::insertNewGeneralNote,
                this::removeGeneralNote,
                this::sizeOfGeneralNoteArray
            );
        }
    }

    /**
     * Gets array of all "GeneralNote" elements
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.GeneralNoteType[] getGeneralNoteArray() {
        return getXmlObjectArray(PROPERTY_QNAME[18], new gov.nih.nlm.ncbi.eutils.GeneralNoteType[0]);
    }

    /**
     * Gets ith "GeneralNote" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.GeneralNoteType getGeneralNoteArray(int i) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.GeneralNoteType target = null;
            target = (gov.nih.nlm.ncbi.eutils.GeneralNoteType)get_store().find_element_user(PROPERTY_QNAME[18], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /**
     * Returns number of "GeneralNote" element
     */
    @Override
    public int sizeOfGeneralNoteArray() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().count_elements(PROPERTY_QNAME[18]);
        }
    }

    /**
     * Sets array of all "GeneralNote" element  WARNING: This method is not atomicaly synchronized.
     */
    @Override
    public void setGeneralNoteArray(gov.nih.nlm.ncbi.eutils.GeneralNoteType[] generalNoteArray) {
        check_orphaned();
        arraySetterHelper(generalNoteArray, PROPERTY_QNAME[18]);
    }

    /**
     * Sets ith "GeneralNote" element
     */
    @Override
    public void setGeneralNoteArray(int i, gov.nih.nlm.ncbi.eutils.GeneralNoteType generalNote) {
        generatedSetterHelperImpl(generalNote, PROPERTY_QNAME[18], i, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);
    }

    /**
     * Inserts and returns a new empty value (as xml) as the ith "GeneralNote" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.GeneralNoteType insertNewGeneralNote(int i) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.GeneralNoteType target = null;
            target = (gov.nih.nlm.ncbi.eutils.GeneralNoteType)get_store().insert_element_user(PROPERTY_QNAME[18], i);
            return target;
        }
    }

    /**
     * Appends and returns a new empty value (as xml) as the last "GeneralNote" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.GeneralNoteType addNewGeneralNote() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.GeneralNoteType target = null;
            target = (gov.nih.nlm.ncbi.eutils.GeneralNoteType)get_store().add_element_user(PROPERTY_QNAME[18]);
            return target;
        }
    }

    /**
     * Removes the ith "GeneralNote" element
     */
    @Override
    public void removeGeneralNote(int i) {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_element(PROPERTY_QNAME[18], i);
        }
    }

    /**
     * Gets the "Owner" attribute
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.MedlineCitationType.Owner.Enum getOwner() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[19]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_default_attribute_value(PROPERTY_QNAME[19]);
            }
            return (target == null) ? null : (gov.nih.nlm.ncbi.eutils.MedlineCitationType.Owner.Enum)target.getEnumValue();
        }
    }

    /**
     * Gets (as xml) the "Owner" attribute
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.MedlineCitationType.Owner xgetOwner() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.MedlineCitationType.Owner target = null;
            target = (gov.nih.nlm.ncbi.eutils.MedlineCitationType.Owner)get_store().find_attribute_user(PROPERTY_QNAME[19]);
            if (target == null) {
                target = (gov.nih.nlm.ncbi.eutils.MedlineCitationType.Owner)get_default_attribute_value(PROPERTY_QNAME[19]);
            }
            return target;
        }
    }

    /**
     * True if has "Owner" attribute
     */
    @Override
    public boolean isSetOwner() {
        synchronized (monitor()) {
            check_orphaned();
            return get_store().find_attribute_user(PROPERTY_QNAME[19]) != null;
        }
    }

    /**
     * Sets the "Owner" attribute
     */
    @Override
    public void setOwner(gov.nih.nlm.ncbi.eutils.MedlineCitationType.Owner.Enum owner) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[19]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[19]);
            }
            target.setEnumValue(owner);
        }
    }

    /**
     * Sets (as xml) the "Owner" attribute
     */
    @Override
    public void xsetOwner(gov.nih.nlm.ncbi.eutils.MedlineCitationType.Owner owner) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.MedlineCitationType.Owner target = null;
            target = (gov.nih.nlm.ncbi.eutils.MedlineCitationType.Owner)get_store().find_attribute_user(PROPERTY_QNAME[19]);
            if (target == null) {
                target = (gov.nih.nlm.ncbi.eutils.MedlineCitationType.Owner)get_store().add_attribute_user(PROPERTY_QNAME[19]);
            }
            target.set(owner);
        }
    }

    /**
     * Unsets the "Owner" attribute
     */
    @Override
    public void unsetOwner() {
        synchronized (monitor()) {
            check_orphaned();
            get_store().remove_attribute(PROPERTY_QNAME[19]);
        }
    }

    /**
     * Gets the "Status" attribute
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.MedlineCitationType.Status.Enum getStatus() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[20]);
            return (target == null) ? null : (gov.nih.nlm.ncbi.eutils.MedlineCitationType.Status.Enum)target.getEnumValue();
        }
    }

    /**
     * Gets (as xml) the "Status" attribute
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.MedlineCitationType.Status xgetStatus() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.MedlineCitationType.Status target = null;
            target = (gov.nih.nlm.ncbi.eutils.MedlineCitationType.Status)get_store().find_attribute_user(PROPERTY_QNAME[20]);
            return target;
        }
    }

    /**
     * Sets the "Status" attribute
     */
    @Override
    public void setStatus(gov.nih.nlm.ncbi.eutils.MedlineCitationType.Status.Enum status) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[20]);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[20]);
            }
            target.setEnumValue(status);
        }
    }

    /**
     * Sets (as xml) the "Status" attribute
     */
    @Override
    public void xsetStatus(gov.nih.nlm.ncbi.eutils.MedlineCitationType.Status status) {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.MedlineCitationType.Status target = null;
            target = (gov.nih.nlm.ncbi.eutils.MedlineCitationType.Status)get_store().find_attribute_user(PROPERTY_QNAME[20]);
            if (target == null) {
                target = (gov.nih.nlm.ncbi.eutils.MedlineCitationType.Status)get_store().add_attribute_user(PROPERTY_QNAME[20]);
            }
            target.set(status);
        }
    }
    /**
     * An XML Owner(@).
     *
     * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.MedlineCitationType$Owner.
     */
    public static class OwnerImpl extends org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx implements gov.nih.nlm.ncbi.eutils.MedlineCitationType.Owner {
        private static final long serialVersionUID = 1L;

        public OwnerImpl(org.apache.xmlbeans.SchemaType sType) {
            super(sType, false);
        }

        protected OwnerImpl(org.apache.xmlbeans.SchemaType sType, boolean b) {
            super(sType, b);
        }
    }
    /**
     * An XML Status(@).
     *
     * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.MedlineCitationType$Status.
     */
    public static class StatusImpl extends org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx implements gov.nih.nlm.ncbi.eutils.MedlineCitationType.Status {
        private static final long serialVersionUID = 1L;

        public StatusImpl(org.apache.xmlbeans.SchemaType sType) {
            super(sType, false);
        }

        protected StatusImpl(org.apache.xmlbeans.SchemaType sType, boolean b) {
            super(sType, b);
        }
    }
}
