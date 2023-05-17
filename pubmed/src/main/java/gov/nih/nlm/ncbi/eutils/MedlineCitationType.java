/*
 * XML Type:  MedlineCitationType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.MedlineCitationType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML MedlineCitationType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public interface MedlineCitationType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.MedlineCitationType> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "medlinecitationtype817etype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "PMID" element
     */
    java.lang.String getPMID();

    /**
     * Gets (as xml) the "PMID" element
     */
    org.apache.xmlbeans.XmlString xgetPMID();

    /**
     * Sets the "PMID" element
     */
    void setPMID(java.lang.String pmid);

    /**
     * Sets (as xml) the "PMID" element
     */
    void xsetPMID(org.apache.xmlbeans.XmlString pmid);

    /**
     * Gets the "DateCreated" element
     */
    gov.nih.nlm.ncbi.eutils.DateCreatedType getDateCreated();

    /**
     * Sets the "DateCreated" element
     */
    void setDateCreated(gov.nih.nlm.ncbi.eutils.DateCreatedType dateCreated);

    /**
     * Appends and returns a new empty "DateCreated" element
     */
    gov.nih.nlm.ncbi.eutils.DateCreatedType addNewDateCreated();

    /**
     * Gets the "DateCompleted" element
     */
    gov.nih.nlm.ncbi.eutils.DateCompletedType getDateCompleted();

    /**
     * True if has "DateCompleted" element
     */
    boolean isSetDateCompleted();

    /**
     * Sets the "DateCompleted" element
     */
    void setDateCompleted(gov.nih.nlm.ncbi.eutils.DateCompletedType dateCompleted);

    /**
     * Appends and returns a new empty "DateCompleted" element
     */
    gov.nih.nlm.ncbi.eutils.DateCompletedType addNewDateCompleted();

    /**
     * Unsets the "DateCompleted" element
     */
    void unsetDateCompleted();

    /**
     * Gets the "DateRevised" element
     */
    gov.nih.nlm.ncbi.eutils.DateRevisedType getDateRevised();

    /**
     * True if has "DateRevised" element
     */
    boolean isSetDateRevised();

    /**
     * Sets the "DateRevised" element
     */
    void setDateRevised(gov.nih.nlm.ncbi.eutils.DateRevisedType dateRevised);

    /**
     * Appends and returns a new empty "DateRevised" element
     */
    gov.nih.nlm.ncbi.eutils.DateRevisedType addNewDateRevised();

    /**
     * Unsets the "DateRevised" element
     */
    void unsetDateRevised();

    /**
     * Gets the "Article" element
     */
    gov.nih.nlm.ncbi.eutils.ArticleType getArticle();

    /**
     * Sets the "Article" element
     */
    void setArticle(gov.nih.nlm.ncbi.eutils.ArticleType article);

    /**
     * Appends and returns a new empty "Article" element
     */
    gov.nih.nlm.ncbi.eutils.ArticleType addNewArticle();

    /**
     * Gets the "MedlineJournalInfo" element
     */
    gov.nih.nlm.ncbi.eutils.MedlineJournalInfoType getMedlineJournalInfo();

    /**
     * Sets the "MedlineJournalInfo" element
     */
    void setMedlineJournalInfo(gov.nih.nlm.ncbi.eutils.MedlineJournalInfoType medlineJournalInfo);

    /**
     * Appends and returns a new empty "MedlineJournalInfo" element
     */
    gov.nih.nlm.ncbi.eutils.MedlineJournalInfoType addNewMedlineJournalInfo();

    /**
     * Gets the "ChemicalList" element
     */
    gov.nih.nlm.ncbi.eutils.ChemicalListType getChemicalList();

    /**
     * True if has "ChemicalList" element
     */
    boolean isSetChemicalList();

    /**
     * Sets the "ChemicalList" element
     */
    void setChemicalList(gov.nih.nlm.ncbi.eutils.ChemicalListType chemicalList);

    /**
     * Appends and returns a new empty "ChemicalList" element
     */
    gov.nih.nlm.ncbi.eutils.ChemicalListType addNewChemicalList();

    /**
     * Unsets the "ChemicalList" element
     */
    void unsetChemicalList();

    /**
     * Gets a List of "CitationSubset" elements
     */
    java.util.List<java.lang.String> getCitationSubsetList();

    /**
     * Gets array of all "CitationSubset" elements
     */
    java.lang.String[] getCitationSubsetArray();

    /**
     * Gets ith "CitationSubset" element
     */
    java.lang.String getCitationSubsetArray(int i);

    /**
     * Gets (as xml) a List of "CitationSubset" elements
     */
    java.util.List<org.apache.xmlbeans.XmlString> xgetCitationSubsetList();

    /**
     * Gets (as xml) array of all "CitationSubset" elements
     */
    org.apache.xmlbeans.XmlString[] xgetCitationSubsetArray();

    /**
     * Gets (as xml) ith "CitationSubset" element
     */
    org.apache.xmlbeans.XmlString xgetCitationSubsetArray(int i);

    /**
     * Returns number of "CitationSubset" element
     */
    int sizeOfCitationSubsetArray();

    /**
     * Sets array of all "CitationSubset" element
     */
    void setCitationSubsetArray(java.lang.String[] citationSubsetArray);

    /**
     * Sets ith "CitationSubset" element
     */
    void setCitationSubsetArray(int i, java.lang.String citationSubset);

    /**
     * Sets (as xml) array of all "CitationSubset" element
     */
    void xsetCitationSubsetArray(org.apache.xmlbeans.XmlString[] citationSubsetArray);

    /**
     * Sets (as xml) ith "CitationSubset" element
     */
    void xsetCitationSubsetArray(int i, org.apache.xmlbeans.XmlString citationSubset);

    /**
     * Inserts the value as the ith "CitationSubset" element
     */
    void insertCitationSubset(int i, java.lang.String citationSubset);

    /**
     * Appends the value as the last "CitationSubset" element
     */
    void addCitationSubset(java.lang.String citationSubset);

    /**
     * Inserts and returns a new empty value (as xml) as the ith "CitationSubset" element
     */
    org.apache.xmlbeans.XmlString insertNewCitationSubset(int i);

    /**
     * Appends and returns a new empty value (as xml) as the last "CitationSubset" element
     */
    org.apache.xmlbeans.XmlString addNewCitationSubset();

    /**
     * Removes the ith "CitationSubset" element
     */
    void removeCitationSubset(int i);

    /**
     * Gets the "CommentsCorrectionsList" element
     */
    gov.nih.nlm.ncbi.eutils.CommentsCorrectionsListType getCommentsCorrectionsList();

    /**
     * True if has "CommentsCorrectionsList" element
     */
    boolean isSetCommentsCorrectionsList();

    /**
     * Sets the "CommentsCorrectionsList" element
     */
    void setCommentsCorrectionsList(gov.nih.nlm.ncbi.eutils.CommentsCorrectionsListType commentsCorrectionsList);

    /**
     * Appends and returns a new empty "CommentsCorrectionsList" element
     */
    gov.nih.nlm.ncbi.eutils.CommentsCorrectionsListType addNewCommentsCorrectionsList();

    /**
     * Unsets the "CommentsCorrectionsList" element
     */
    void unsetCommentsCorrectionsList();

    /**
     * Gets the "GeneSymbolList" element
     */
    gov.nih.nlm.ncbi.eutils.GeneSymbolListType getGeneSymbolList();

    /**
     * True if has "GeneSymbolList" element
     */
    boolean isSetGeneSymbolList();

    /**
     * Sets the "GeneSymbolList" element
     */
    void setGeneSymbolList(gov.nih.nlm.ncbi.eutils.GeneSymbolListType geneSymbolList);

    /**
     * Appends and returns a new empty "GeneSymbolList" element
     */
    gov.nih.nlm.ncbi.eutils.GeneSymbolListType addNewGeneSymbolList();

    /**
     * Unsets the "GeneSymbolList" element
     */
    void unsetGeneSymbolList();

    /**
     * Gets the "MeshHeadingList" element
     */
    gov.nih.nlm.ncbi.eutils.MeshHeadingListType getMeshHeadingList();

    /**
     * True if has "MeshHeadingList" element
     */
    boolean isSetMeshHeadingList();

    /**
     * Sets the "MeshHeadingList" element
     */
    void setMeshHeadingList(gov.nih.nlm.ncbi.eutils.MeshHeadingListType meshHeadingList);

    /**
     * Appends and returns a new empty "MeshHeadingList" element
     */
    gov.nih.nlm.ncbi.eutils.MeshHeadingListType addNewMeshHeadingList();

    /**
     * Unsets the "MeshHeadingList" element
     */
    void unsetMeshHeadingList();

    /**
     * Gets the "NumberOfReferences" element
     */
    java.lang.String getNumberOfReferences();

    /**
     * Gets (as xml) the "NumberOfReferences" element
     */
    org.apache.xmlbeans.XmlString xgetNumberOfReferences();

    /**
     * True if has "NumberOfReferences" element
     */
    boolean isSetNumberOfReferences();

    /**
     * Sets the "NumberOfReferences" element
     */
    void setNumberOfReferences(java.lang.String numberOfReferences);

    /**
     * Sets (as xml) the "NumberOfReferences" element
     */
    void xsetNumberOfReferences(org.apache.xmlbeans.XmlString numberOfReferences);

    /**
     * Unsets the "NumberOfReferences" element
     */
    void unsetNumberOfReferences();

    /**
     * Gets the "PersonalNameSubjectList" element
     */
    gov.nih.nlm.ncbi.eutils.PersonalNameSubjectListType getPersonalNameSubjectList();

    /**
     * True if has "PersonalNameSubjectList" element
     */
    boolean isSetPersonalNameSubjectList();

    /**
     * Sets the "PersonalNameSubjectList" element
     */
    void setPersonalNameSubjectList(gov.nih.nlm.ncbi.eutils.PersonalNameSubjectListType personalNameSubjectList);

    /**
     * Appends and returns a new empty "PersonalNameSubjectList" element
     */
    gov.nih.nlm.ncbi.eutils.PersonalNameSubjectListType addNewPersonalNameSubjectList();

    /**
     * Unsets the "PersonalNameSubjectList" element
     */
    void unsetPersonalNameSubjectList();

    /**
     * Gets a List of "OtherID" elements
     */
    java.util.List<gov.nih.nlm.ncbi.eutils.OtherIDType> getOtherIDList();

    /**
     * Gets array of all "OtherID" elements
     */
    gov.nih.nlm.ncbi.eutils.OtherIDType[] getOtherIDArray();

    /**
     * Gets ith "OtherID" element
     */
    gov.nih.nlm.ncbi.eutils.OtherIDType getOtherIDArray(int i);

    /**
     * Returns number of "OtherID" element
     */
    int sizeOfOtherIDArray();

    /**
     * Sets array of all "OtherID" element
     */
    void setOtherIDArray(gov.nih.nlm.ncbi.eutils.OtherIDType[] otherIDArray);

    /**
     * Sets ith "OtherID" element
     */
    void setOtherIDArray(int i, gov.nih.nlm.ncbi.eutils.OtherIDType otherID);

    /**
     * Inserts and returns a new empty value (as xml) as the ith "OtherID" element
     */
    gov.nih.nlm.ncbi.eutils.OtherIDType insertNewOtherID(int i);

    /**
     * Appends and returns a new empty value (as xml) as the last "OtherID" element
     */
    gov.nih.nlm.ncbi.eutils.OtherIDType addNewOtherID();

    /**
     * Removes the ith "OtherID" element
     */
    void removeOtherID(int i);

    /**
     * Gets a List of "OtherAbstract" elements
     */
    java.util.List<gov.nih.nlm.ncbi.eutils.OtherAbstractType> getOtherAbstractList();

    /**
     * Gets array of all "OtherAbstract" elements
     */
    gov.nih.nlm.ncbi.eutils.OtherAbstractType[] getOtherAbstractArray();

    /**
     * Gets ith "OtherAbstract" element
     */
    gov.nih.nlm.ncbi.eutils.OtherAbstractType getOtherAbstractArray(int i);

    /**
     * Returns number of "OtherAbstract" element
     */
    int sizeOfOtherAbstractArray();

    /**
     * Sets array of all "OtherAbstract" element
     */
    void setOtherAbstractArray(gov.nih.nlm.ncbi.eutils.OtherAbstractType[] otherAbstractArray);

    /**
     * Sets ith "OtherAbstract" element
     */
    void setOtherAbstractArray(int i, gov.nih.nlm.ncbi.eutils.OtherAbstractType otherAbstract);

    /**
     * Inserts and returns a new empty value (as xml) as the ith "OtherAbstract" element
     */
    gov.nih.nlm.ncbi.eutils.OtherAbstractType insertNewOtherAbstract(int i);

    /**
     * Appends and returns a new empty value (as xml) as the last "OtherAbstract" element
     */
    gov.nih.nlm.ncbi.eutils.OtherAbstractType addNewOtherAbstract();

    /**
     * Removes the ith "OtherAbstract" element
     */
    void removeOtherAbstract(int i);

    /**
     * Gets a List of "KeywordList" elements
     */
    java.util.List<gov.nih.nlm.ncbi.eutils.KeywordListType> getKeywordListList();

    /**
     * Gets array of all "KeywordList" elements
     */
    gov.nih.nlm.ncbi.eutils.KeywordListType[] getKeywordListArray();

    /**
     * Gets ith "KeywordList" element
     */
    gov.nih.nlm.ncbi.eutils.KeywordListType getKeywordListArray(int i);

    /**
     * Returns number of "KeywordList" element
     */
    int sizeOfKeywordListArray();

    /**
     * Sets array of all "KeywordList" element
     */
    void setKeywordListArray(gov.nih.nlm.ncbi.eutils.KeywordListType[] keywordListArray);

    /**
     * Sets ith "KeywordList" element
     */
    void setKeywordListArray(int i, gov.nih.nlm.ncbi.eutils.KeywordListType keywordList);

    /**
     * Inserts and returns a new empty value (as xml) as the ith "KeywordList" element
     */
    gov.nih.nlm.ncbi.eutils.KeywordListType insertNewKeywordList(int i);

    /**
     * Appends and returns a new empty value (as xml) as the last "KeywordList" element
     */
    gov.nih.nlm.ncbi.eutils.KeywordListType addNewKeywordList();

    /**
     * Removes the ith "KeywordList" element
     */
    void removeKeywordList(int i);

    /**
     * Gets a List of "SpaceFlightMission" elements
     */
    java.util.List<java.lang.String> getSpaceFlightMissionList();

    /**
     * Gets array of all "SpaceFlightMission" elements
     */
    java.lang.String[] getSpaceFlightMissionArray();

    /**
     * Gets ith "SpaceFlightMission" element
     */
    java.lang.String getSpaceFlightMissionArray(int i);

    /**
     * Gets (as xml) a List of "SpaceFlightMission" elements
     */
    java.util.List<org.apache.xmlbeans.XmlString> xgetSpaceFlightMissionList();

    /**
     * Gets (as xml) array of all "SpaceFlightMission" elements
     */
    org.apache.xmlbeans.XmlString[] xgetSpaceFlightMissionArray();

    /**
     * Gets (as xml) ith "SpaceFlightMission" element
     */
    org.apache.xmlbeans.XmlString xgetSpaceFlightMissionArray(int i);

    /**
     * Returns number of "SpaceFlightMission" element
     */
    int sizeOfSpaceFlightMissionArray();

    /**
     * Sets array of all "SpaceFlightMission" element
     */
    void setSpaceFlightMissionArray(java.lang.String[] spaceFlightMissionArray);

    /**
     * Sets ith "SpaceFlightMission" element
     */
    void setSpaceFlightMissionArray(int i, java.lang.String spaceFlightMission);

    /**
     * Sets (as xml) array of all "SpaceFlightMission" element
     */
    void xsetSpaceFlightMissionArray(org.apache.xmlbeans.XmlString[] spaceFlightMissionArray);

    /**
     * Sets (as xml) ith "SpaceFlightMission" element
     */
    void xsetSpaceFlightMissionArray(int i, org.apache.xmlbeans.XmlString spaceFlightMission);

    /**
     * Inserts the value as the ith "SpaceFlightMission" element
     */
    void insertSpaceFlightMission(int i, java.lang.String spaceFlightMission);

    /**
     * Appends the value as the last "SpaceFlightMission" element
     */
    void addSpaceFlightMission(java.lang.String spaceFlightMission);

    /**
     * Inserts and returns a new empty value (as xml) as the ith "SpaceFlightMission" element
     */
    org.apache.xmlbeans.XmlString insertNewSpaceFlightMission(int i);

    /**
     * Appends and returns a new empty value (as xml) as the last "SpaceFlightMission" element
     */
    org.apache.xmlbeans.XmlString addNewSpaceFlightMission();

    /**
     * Removes the ith "SpaceFlightMission" element
     */
    void removeSpaceFlightMission(int i);

    /**
     * Gets the "InvestigatorList" element
     */
    gov.nih.nlm.ncbi.eutils.InvestigatorListType getInvestigatorList();

    /**
     * True if has "InvestigatorList" element
     */
    boolean isSetInvestigatorList();

    /**
     * Sets the "InvestigatorList" element
     */
    void setInvestigatorList(gov.nih.nlm.ncbi.eutils.InvestigatorListType investigatorList);

    /**
     * Appends and returns a new empty "InvestigatorList" element
     */
    gov.nih.nlm.ncbi.eutils.InvestigatorListType addNewInvestigatorList();

    /**
     * Unsets the "InvestigatorList" element
     */
    void unsetInvestigatorList();

    /**
     * Gets a List of "GeneralNote" elements
     */
    java.util.List<gov.nih.nlm.ncbi.eutils.GeneralNoteType> getGeneralNoteList();

    /**
     * Gets array of all "GeneralNote" elements
     */
    gov.nih.nlm.ncbi.eutils.GeneralNoteType[] getGeneralNoteArray();

    /**
     * Gets ith "GeneralNote" element
     */
    gov.nih.nlm.ncbi.eutils.GeneralNoteType getGeneralNoteArray(int i);

    /**
     * Returns number of "GeneralNote" element
     */
    int sizeOfGeneralNoteArray();

    /**
     * Sets array of all "GeneralNote" element
     */
    void setGeneralNoteArray(gov.nih.nlm.ncbi.eutils.GeneralNoteType[] generalNoteArray);

    /**
     * Sets ith "GeneralNote" element
     */
    void setGeneralNoteArray(int i, gov.nih.nlm.ncbi.eutils.GeneralNoteType generalNote);

    /**
     * Inserts and returns a new empty value (as xml) as the ith "GeneralNote" element
     */
    gov.nih.nlm.ncbi.eutils.GeneralNoteType insertNewGeneralNote(int i);

    /**
     * Appends and returns a new empty value (as xml) as the last "GeneralNote" element
     */
    gov.nih.nlm.ncbi.eutils.GeneralNoteType addNewGeneralNote();

    /**
     * Removes the ith "GeneralNote" element
     */
    void removeGeneralNote(int i);

    /**
     * Gets the "Owner" attribute
     */
    gov.nih.nlm.ncbi.eutils.MedlineCitationType.Owner.Enum getOwner();

    /**
     * Gets (as xml) the "Owner" attribute
     */
    gov.nih.nlm.ncbi.eutils.MedlineCitationType.Owner xgetOwner();

    /**
     * True if has "Owner" attribute
     */
    boolean isSetOwner();

    /**
     * Sets the "Owner" attribute
     */
    void setOwner(gov.nih.nlm.ncbi.eutils.MedlineCitationType.Owner.Enum owner);

    /**
     * Sets (as xml) the "Owner" attribute
     */
    void xsetOwner(gov.nih.nlm.ncbi.eutils.MedlineCitationType.Owner owner);

    /**
     * Unsets the "Owner" attribute
     */
    void unsetOwner();

    /**
     * Gets the "Status" attribute
     */
    gov.nih.nlm.ncbi.eutils.MedlineCitationType.Status.Enum getStatus();

    /**
     * Gets (as xml) the "Status" attribute
     */
    gov.nih.nlm.ncbi.eutils.MedlineCitationType.Status xgetStatus();

    /**
     * Sets the "Status" attribute
     */
    void setStatus(gov.nih.nlm.ncbi.eutils.MedlineCitationType.Status.Enum status);

    /**
     * Sets (as xml) the "Status" attribute
     */
    void xsetStatus(gov.nih.nlm.ncbi.eutils.MedlineCitationType.Status status);

    /**
     * An XML Owner(@).
     *
     * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.MedlineCitationType$Owner.
     */
    public interface Owner extends org.apache.xmlbeans.XmlNMTOKEN {
        ElementFactory<gov.nih.nlm.ncbi.eutils.MedlineCitationType.Owner> Factory = new ElementFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "ownere1e3attrtype");
        org.apache.xmlbeans.SchemaType type = Factory.getType();


        org.apache.xmlbeans.StringEnumAbstractBase getEnumValue();
        void setEnumValue(org.apache.xmlbeans.StringEnumAbstractBase e);

        Enum NLM = Enum.forString("NLM");
        Enum NASA = Enum.forString("NASA");
        Enum PIP = Enum.forString("PIP");
        Enum KIE = Enum.forString("KIE");
        Enum HSR = Enum.forString("HSR");
        Enum HMD = Enum.forString("HMD");
        Enum NOTNLM = Enum.forString("NOTNLM");

        int INT_NLM = Enum.INT_NLM;
        int INT_NASA = Enum.INT_NASA;
        int INT_PIP = Enum.INT_PIP;
        int INT_KIE = Enum.INT_KIE;
        int INT_HSR = Enum.INT_HSR;
        int INT_HMD = Enum.INT_HMD;
        int INT_NOTNLM = Enum.INT_NOTNLM;

        /**
         * Enumeration value class for gov.nih.nlm.ncbi.eutils.MedlineCitationType$Owner.
         * These enum values can be used as follows:
         * <pre>
         * enum.toString(); // returns the string value of the enum
         * enum.intValue(); // returns an int value, useful for switches
         * // e.g., case Enum.INT_NLM
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

            static final int INT_NLM = 1;
            static final int INT_NASA = 2;
            static final int INT_PIP = 3;
            static final int INT_KIE = 4;
            static final int INT_HSR = 5;
            static final int INT_HMD = 6;
            static final int INT_NOTNLM = 7;

            public static final org.apache.xmlbeans.StringEnumAbstractBase.Table table =
                new org.apache.xmlbeans.StringEnumAbstractBase.Table(new Enum[] {
                new Enum("NLM", INT_NLM),
                new Enum("NASA", INT_NASA),
                new Enum("PIP", INT_PIP),
                new Enum("KIE", INT_KIE),
                new Enum("HSR", INT_HSR),
                new Enum("HMD", INT_HMD),
                new Enum("NOTNLM", INT_NOTNLM),
            });
            private static final long serialVersionUID = 1L;
            private java.lang.Object readResolve() {
                return forInt(intValue());
            }
        }
    }

    /**
     * An XML Status(@).
     *
     * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.MedlineCitationType$Status.
     */
    public interface Status extends org.apache.xmlbeans.XmlNMTOKEN {
        ElementFactory<gov.nih.nlm.ncbi.eutils.MedlineCitationType.Status> Factory = new ElementFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "statusa68cattrtype");
        org.apache.xmlbeans.SchemaType type = Factory.getType();


        org.apache.xmlbeans.StringEnumAbstractBase getEnumValue();
        void setEnumValue(org.apache.xmlbeans.StringEnumAbstractBase e);

        Enum COMPLETED = Enum.forString("Completed");
        Enum IN_PROCESS = Enum.forString("In-Process");
        Enum PUB_MED_NOT_MEDLINE = Enum.forString("PubMed-not-MEDLINE");
        Enum IN_DATA_REVIEW = Enum.forString("In-Data-Review");
        Enum PUBLISHER = Enum.forString("Publisher");
        Enum MEDLINE = Enum.forString("MEDLINE");
        Enum OLDMEDLINE = Enum.forString("OLDMEDLINE");

        int INT_COMPLETED = Enum.INT_COMPLETED;
        int INT_IN_PROCESS = Enum.INT_IN_PROCESS;
        int INT_PUB_MED_NOT_MEDLINE = Enum.INT_PUB_MED_NOT_MEDLINE;
        int INT_IN_DATA_REVIEW = Enum.INT_IN_DATA_REVIEW;
        int INT_PUBLISHER = Enum.INT_PUBLISHER;
        int INT_MEDLINE = Enum.INT_MEDLINE;
        int INT_OLDMEDLINE = Enum.INT_OLDMEDLINE;

        /**
         * Enumeration value class for gov.nih.nlm.ncbi.eutils.MedlineCitationType$Status.
         * These enum values can be used as follows:
         * <pre>
         * enum.toString(); // returns the string value of the enum
         * enum.intValue(); // returns an int value, useful for switches
         * // e.g., case Enum.INT_COMPLETED
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

            static final int INT_COMPLETED = 1;
            static final int INT_IN_PROCESS = 2;
            static final int INT_PUB_MED_NOT_MEDLINE = 3;
            static final int INT_IN_DATA_REVIEW = 4;
            static final int INT_PUBLISHER = 5;
            static final int INT_MEDLINE = 6;
            static final int INT_OLDMEDLINE = 7;

            public static final org.apache.xmlbeans.StringEnumAbstractBase.Table table =
                new org.apache.xmlbeans.StringEnumAbstractBase.Table(new Enum[] {
                new Enum("Completed", INT_COMPLETED),
                new Enum("In-Process", INT_IN_PROCESS),
                new Enum("PubMed-not-MEDLINE", INT_PUB_MED_NOT_MEDLINE),
                new Enum("In-Data-Review", INT_IN_DATA_REVIEW),
                new Enum("Publisher", INT_PUBLISHER),
                new Enum("MEDLINE", INT_MEDLINE),
                new Enum("OLDMEDLINE", INT_OLDMEDLINE),
            });
            private static final long serialVersionUID = 1L;
            private java.lang.Object readResolve() {
                return forInt(intValue());
            }
        }
    }
}
