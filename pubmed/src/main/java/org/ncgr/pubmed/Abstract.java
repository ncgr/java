package org.ncgr.pubmed;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gov.nih.nlm.ncbi.eutils.AbstractType;
import gov.nih.nlm.ncbi.eutils.ArticleIdType;
import gov.nih.nlm.ncbi.eutils.ArticleType;
import gov.nih.nlm.ncbi.eutils.KeywordListType;
import gov.nih.nlm.ncbi.eutils.KeywordType;
import gov.nih.nlm.ncbi.eutils.PubmedArticleType;

/**
 * Contains an Abstract from a PubmedArticle.
 */
public class Abstract {

    String error;
    String pmid;
    String title;
    String text;
    String pmcid;
    String doi;
    List<String> keywords = new ArrayList<>();

    /**
     * Construct an empty Abstract.
     */
    public Abstract() {
    }
    
    /**
     * Construct from a PubmedArticleType
     */
    public Abstract(PubmedArticleType pubmedArticleType) {
        ArticleType articleType = pubmedArticleType.getMedlineCitation().getArticle();
        AbstractType abstractType = articleType.getAbstract();
        if (abstractType!=null) {
            setPMID(pubmedArticleType.getMedlineCitation().getPMID());
            setTitle(articleType.getArticleTitle());
            setText(abstractType.getAbstractText());
            for (ArticleIdType id : pubmedArticleType.getPubmedData().getArticleIdList().getArticleIdArray()) {
                if (id.getIdType() == ArticleIdType.IdType.PMC) {
                    setPMCID(id.getStringValue());
                } else if (id.getIdType() == ArticleIdType.IdType.DOI) {
                    setDOI(id.getStringValue());
                }
            }
            for (KeywordListType keywordListType : pubmedArticleType.getMedlineCitation().getKeywordListArray()) {
                for (KeywordType keywordType : keywordListType.getKeywordArray()) {
                    addKeyword(keywordType.getStringValue());
                }
            }
        }
    }

    /**
     * Construct from an org.ncgr.pubmed.xml.esearch.ERROR.
     */
    Abstract(org.ncgr.pubmed.xml.esearch.ERROR esearchError) {
        setError(esearchError.getvalue());
    }

    public void setError(String s) {
        error = s;
    }

    public String getError() {
        return error;
    }

    public void setPMID(String s) {
        pmid = s;
    }

    public String getPMID() {
        return pmid;
    }

    public void setTitle(String s) {
        title = s;
    }

    public String getTitle() {
        return title;
    }

    public void setText(String s) {
        text = s;
    }

    public String getText() {
        return text;
    }

    public void setPMCID(String s) {
        pmcid = s;
    }

    public String getPMCID() {
        return pmcid;
    }

    public void setDOI(String s) {
        doi = s;
    }

    public String getDOI() {
        return doi;
    }

    public void setKeywords(List<String> list) {
        for (String k : list) {
            addKeyword(k);
        }
    }

    public void addKeyword(String s) {
        keywords.add(s.trim().replace("\n", "").replace("\r", ""));
    }

    public List<String> getKeywords() {
        return keywords;
    }

    /**
     * String representation of this Abstract object.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("---\n");
        if (title.startsWith("Title: ")) {
            sb.append(title + "\n");
        } else {
            sb.append("Title: " + title + "\n");
        }
        if (text.startsWith("Abstract: ")) {
            sb.append(text + "\n");
        } else {
            sb.append("Abstract: " + text + "\n");
        }
        if (keywords.size()>0) {
            String keywordsStr = keywords.toString().replace("[", "").replace("]", "");
            sb.append("Keywords: " + keywordsStr + "\n");
        }
        if (doi!=null) sb.append("DOI: " + doi + "\n");
        if (pmcid!=null) sb.append("PMCID: " + pmcid + "\n");
        sb.append("PMID: " + pmid);
        return sb.toString();
    }

    /**
     * Load a List of Abstracts from a text file, given by name, containing data in the format of toString() output.
     * Abstracts start with "---" and end with "PMID:".
     */
    public static List<Abstract> load(String filename) throws FileNotFoundException, IOException {
        List<Abstract> abstracts = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        Abstract currentAbstract = new Abstract();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.equals("---")) {
                // start new Abstract
                currentAbstract = new Abstract();
            }
            if (line.startsWith("Abstract: ")) {
                String[] parts = line.split("Abstract: ");
                currentAbstract.setText(parts[1]);
            }
            if (line.startsWith("Title: ")) {
                String[] parts = line.split("Title: ");
                currentAbstract.setTitle(parts[1]);
            }
            if (line.startsWith("Keywords: ")) {
                String[] parts = line.split("Keywords: ");
                if (parts.length>1) {
                    List<String> keywords = Arrays.asList(parts[1].split(","));
                    currentAbstract.setKeywords(keywords);
                }
            }
            if (line.startsWith("DOI: ")) {
                String[] parts = line.split("DOI: ");
                currentAbstract.setDOI(parts[1]);
            }
            if (line.startsWith("PMCID: ")) {
                String[] parts = line.split("PMCID: ");
                currentAbstract.setPMCID(parts[1]);
            }
            if (line.startsWith("PMID: ")) {
                String[] parts = line.split("PMID: ");
                currentAbstract.setPMID(parts[1]);
                // end of record
                abstracts.add(currentAbstract);
                // just in case
                currentAbstract = null;
            }
        }
        return abstracts;
    }

    /**
     * Command-line utility for testing Abstract loads.
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        if (args.length!=1) {
            System.err.println("Usage: Abstract <filename>");
            System.exit(1);
        }
        String filename = args[0];
        List<Abstract> abstracts = Abstract.load(filename);
        for (Abstract a : abstracts) {
            System.out.println(a.toString());
        }
    }
}
