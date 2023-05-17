package org.ncgr.pubmed;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Encapsulates a PubMed Central Article with a lot less data.
 * This uses old-school Node tree handling since there don't seem to be methods to get 
 * the various objects!
 */
public class Article {

    String pmid;
    String pmcid;
    String doi;
    String publisherId;
    String title;
    String abstr;
    List<String> paragraphs = new ArrayList<>();
    
    /*
     * Construct from an org.ncgr.pubmed.xml.nlmarticleset.Article
     */
    public Article(org.ncgr.pubmed.xml.nlmarticleset.Article article) {
        for (Object o : article.getContent()) {
            try {
                Element e = (Element) o;
                handleElement(e);
            } catch (Exception ex) {
                // do nothing
            }
        }
    }

    /**
     * Handle a top-level Element, drilling down through three levels.
     */
    void handleElement(Element e) {
        NodeList nl1 = e.getChildNodes();
        for (int i=0; i<nl1.getLength(); i++) {
            Node n1 = nl1.item(i);
            handleNode(e, 1, n1);
            if (n1.hasChildNodes()) {
                NodeList nl2 = n1.getChildNodes();
                for (int j=0; j<nl2.getLength(); j++) {
                    Node n2 = nl2.item(j);
                    handleNode(e, 2, n2);
                    if (n2.hasChildNodes()) {
                        NodeList nl3 = n2.getChildNodes();
                        for (int k=0; k<nl3.getLength(); k++) {
                            Node n3 = nl3.item(k);
                            handleNode(e, 3, n3);
                        }
                    }
                }
            }
        }
    }

    /**
     * Handle a node depending on its name. The top-level Element and depth are passed in, just in case it matters.
     *
     * @param e the top-level Element
     * @param depth the depth into the element
     * @param n the Node
     */
    void handleNode(Element e, int depth, Node n) {
        switch (n.getNodeName()) {
        case "article-id":
            handleArticleId(n);
            break;
        case "article-title":
            this.title = n.getTextContent();
            break;
        case "abstract":
            this.abstr = n.getTextContent();
            break;
        case "title":
            // likely a section title
            break;
        case "sec":
            break;
        case "p": 
            // only store paragraphs from the body
            if (e.getTagName().equals("body")) {
                paragraphs.add(n.getTextContent());
            }
            break;
        case "italic":
            break;
        case "xref":
            break;
        case "sup":
            break;
        case "supplementary-material":
            break;
        case "label":
            break;
        case "caption":
            break;
        case "media":
            break;
        case "fig":
            break;
        case "ref":
            break;
        case "element-citation":
            break;
        case "mixed-citation":
            break;
        case "contrib":
            break;
        case "aff":
            break;
        case "fn":
            break;
        case "fn-group":
            break;
        case "table-wrap":
            break;
        case "sub":
            break;
        case "bold":
            break;
        case "#text":
            break;
        default:
            // signal that we've got a new type of node
            System.err.println(depth+": " + n.getNodeName());
        }
    }

    // 1: journal-meta
    // 2: journal-id
    // 2: journal-title-group
    // 3: journal-title
    // 2: issn
    // 2: publisher
    // 3: publisher-name
    // 3: publisher-loc
    //
    // 1: article-meta
    // 2: article-categories
    // 3: subj-group
    // 2: title-group
    // 3: alt-title
    // 2: contrib-group
    // 3: addr-line
    // 2: contrib-group
    // 2: author-notes
    // 3: corresp
    // 2: pub-date
    // 3: day
    // 3: month
    // 3: year
    // 2: volume
    // 2: issue
    // 2: elocation-id
    // 2: history
    // 3: date
    // 2: permissions
    // 3: license
    // 2: self-uri
    // 2: funding-group
    // 3: award-group
    // 3: funding-statement
    // 2: counts
    // 3: fig-count
    // 3: table-count
    // 3: page-count
    // 2: custom-meta-group
    // 3: custom-meta
    //
    // 1: notes
    // 3: ext-link
    //
    // 1: ack
    //
    // 1: ref-list

    /**
     * Handle an article-id node.
     */
    void handleArticleId(Node n) {
        switch (n.getAttributes().getNamedItem("pub-id-type").getTextContent()) {
        case "pmid":
            pmid = n.getTextContent();
            break;
        case "pmc":
            pmcid = n.getTextContent();
            break;
        case "doi":
            doi = n.getTextContent();
            break;
        case "publisher-id":
            publisherId = n.getTextContent();
            break;
        default:
        }
    }
        
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("TITLE: " + title + "\n");
        sb.append("ABSTRACT: " + abstr + "\n");
        for (String paragraph : paragraphs) {
            sb.append("-----\n");
            sb.append(paragraph + "\n");
        }
        return sb.toString();
    }
}
