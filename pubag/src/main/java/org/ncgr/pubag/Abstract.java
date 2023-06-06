package org.ncgr.pubag;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.ncgr.pubag.xml.Arr;
import org.ncgr.pubag.xml.Doc;
import org.ncgr.pubag.xml.Str;

/**
 * Contains an Abstract from a PubAg Doc.
 */
public class Abstract {

    String id;
    String title;
    String text;
    String pmid;
    String doi;
    List<String> keywords = new ArrayList<>();

    /**
     * Construct an empty Abstract.
     */
    public Abstract() {
    }
    
    /**
     * Construct from a Doc. There may be null fields.
     */
    public Abstract(Doc doc) {
        setId(doc);
        setTitle(doc);
        setText(doc);
        setPmid(doc);
        setDoi(doc);
        setKeywords(doc);
    }

    /**
     * String representation of this Abstract object.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("---\n");
        if (title!=null && title.startsWith("Title: ")) {
            sb.append(title + "\n");
        } else if (title!=null) {
            sb.append("Title: " + title + "\n");
        }
        if (text!=null && text.startsWith("Abstract: ")) {
            sb.append(text + "\n");
        } else if (text!=null) {
            sb.append("Abstract: " + text + "\n");
        }
        if (keywords.size()>0) {
            String keywordsStr = keywords.toString().replace("[", "").replace("]", "");
            sb.append("Keywords: " + keywordsStr + "\n");
        }
        if (doi!=null) sb.append("DOI: " + doi + "\n");
        if (pmid!=null) sb.append("PMID: " + pmid + "\n");
        // all have an id
        sb.append("ID: " + id);
        return sb.toString();
    }

    /**
     * Two Abstracts are equal if they have the same id.
     */
    @Override
    public boolean equals(Object o) {
        Abstract that = (Abstract) o;
        return this.getId().equals(that.getId());
    }

    /**
     * Return true if this Abstract is complete, i.e. has required fields populated.
     */
    public boolean isComplete() {
        return (id!=null && title!=null && text!=null);
    }

    public void setId(String s) {
        id = s;
    }

    public String getId() {
        return id;
    }
    
    public void setPmid(String s) {
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

    public void setDoi(String s) {
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
     * Set the title from a Doc.
     *
     * @param doc the Doc
     */
    void setTitle(Doc doc) {
        for (Object o : doc.getArrOrBoolOrStr()) {
            if (o instanceof Str) {
                Str str = (Str) o;
                if (str.getName().equals("title")) {
                    this.title = str.getContent();
                }
            }
        }
    }

    /**
     * Set the abstract text from a Doc.
     *
     * @param doc the Doc
     */
    void setText(Doc doc) {
        for (Object o : doc.getArrOrBoolOrStr()) {
            if (o instanceof Str) {
                Str str = (Str) o;
                if (str.getName().equals("abstract")) {
                    this.text = str.getContent();
                }
            }
        }
    }

    /**
     * Set the PMID from a Doc.
     * The pmid_url can have two values, the first containing an actual PMID.
     *
     * https://www.ncbi.nlm.nih.gov/pubmed/?term=23558204 
     * https://www.ncbi.nlm.nih.gov/pubmed/?term= 
     *
     * @param doc the Doc
     * @return the PMID as a String
     */
    void setPmid(Doc doc) {
        for (Object o : doc.getArrOrBoolOrStr()) {
            if (o instanceof Str) {
                Str str = (Str) o;
                if (str.getName().equals("pmid_url")) {
                    String pmidUrl = str.getContent();
                    if (!pmidUrl.endsWith("term=")) {
                        String[] pieces = pmidUrl.split("term=");
                        this.pmid = pieces[1];
                    }
                }
            }
        }
    }
    
    /**
     * Set the PubAg ID from a Doc (as a String).
     */
    void setId(Doc doc) {
        for (Object o : doc.getArrOrBoolOrStr()) {
            if (o instanceof Str) {
                Str str = (Str) o;
                if (str.getName().equals("id")) {
                    this.id = str.getContent();
                }
            }
        }
    }

    /**
     * Set the DOI from a Doc (as a String).
     */
    void setDoi(Doc doc) {
        for (Object o : doc.getArrOrBoolOrStr()) {
            if (o instanceof Str) {
                Str str = (Str) o;
                if (str.getName().equals("doi_url")) {
                    String doiUrl = str.getContent();
                    String[] pieces = doiUrl.split("doi.org/");
                    this.doi = pieces[1];
                }
            }
        }
    }

    /**
     * Set the List of keywords from a Doc.
     */
    void setKeywords(Doc doc) {
        for (Object o : doc.getArrOrBoolOrStr()) {
            if (o instanceof Arr) {
                Arr arr = (Arr) o;
                if (arr.getName().equals("subject")) {
                    List<Str> strs = arr.getStr();
                    for (Str s : strs) {
                        this.keywords.add(s.getContent());
                    }
                }
            }
        }
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
                currentAbstract.setDoi(parts[1]);
            }
            if (line.startsWith("PMID: ")) {
                String[] parts = line.split("PMID: ");
                currentAbstract.setPmid(parts[1]);
            }
            if (line.startsWith("ID: ")) {
                String[] parts = line.split("ID: ");
                currentAbstract.setId(parts[1]);
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
