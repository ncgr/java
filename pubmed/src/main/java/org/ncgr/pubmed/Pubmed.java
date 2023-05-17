package org.ncgr.pubmed;

import org.ncgr.pubmed.xml.esearch.Count;
import org.ncgr.pubmed.xml.esearch.ESearchResult;
import org.ncgr.pubmed.xml.esearch.Id;
import org.ncgr.pubmed.xml.esearch.IdList;

import org.ncgr.pubmed.xml.esummary.DocSum;
import org.ncgr.pubmed.xml.esummary.ESummaryResult;

import org.ncgr.pubmed.xml.nlmarticleset.PmcArticleset;

import gov.nih.nlm.ncbi.eutils.PubmedArticleSetDocument;
import gov.nih.nlm.ncbi.eutils.PubmedArticleType;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;

import javax.xml.bind.JAXBElement;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import javax.xml.stream.XMLStreamException;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;

import org.xml.sax.SAXException;

/**
 * Static methods and command-line utility for populating Abstract and Summary objects.
 */
public class Pubmed {

    /**
     * Unmarshal an ESearchResult from a given esearch URI.
     *
     * @param uri full PubMed esearch URI
     * @return an ESearchResult
     */
    public static ESearchResult getESearchResult(String uri) throws JAXBException, XMLStreamException {
        JAXBContext context = JAXBContext.newInstance(ESearchResult.class);
        return (ESearchResult) context.createUnmarshaller().unmarshal(new StreamSource(uri));
    }

    /**
     * Unmarshal a PubmedArticleSet from a given efetch URI.
     *
     * @param uri full PubMed efetch URI
     * @return a PubmedArticleSet
     */
    public static PubmedArticleSetDocument getPubmedArticleSet(String uri) throws JAXBException, XMLStreamException, MalformedURLException, XmlException, IOException {
        // do this because of namespace mismatch between "" and "http://www.ncbi.nlm.nih.gov/eutils"
        Map<String,String> nses = new HashMap<>();
        nses.put("", "http://www.ncbi.nlm.nih.gov/eutils");
        XmlOptions options = new XmlOptions().setLoadSubstituteNamespaces​(nses);
        // this crashes sometimes
        PubmedArticleSetDocument pasd = null;
        while (pasd == null) {
            try {
                pasd = PubmedArticleSetDocument.Factory.parse(new URL(uri), options);
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        }
        return pasd;
    }

    /**
     * Unmarshal an ESummaryResult from a given esummary URI.
     *
     * @param uri full PubMed esummary URI
     * @return an ESummaryResult
     */
    public static ESummaryResult getESummaryResult(String uri) throws JAXBException, XMLStreamException {
        JAXBContext context = JAXBContext.newInstance(ESummaryResult.class);
        return (ESummaryResult) context.createUnmarshaller().unmarshal(new StreamSource(uri));
    }

    /**
     * Unmarshal an PmcArticleset from a given efetch URI.
     *
     * @param uri full PubMed efetch URI
     * @return a PmcArticlesetType
     */
    public static PmcArticleset getPmcArticleset(String uri) throws JAXBException, XMLStreamException {
        JAXBContext context = JAXBContext.newInstance(PmcArticleset.class);
        // return (PmcArticleset) context.createUnmarshaller().unmarshal(new StreamSource(uri));
        PmcArticleset articlesetType = (PmcArticleset) context.createUnmarshaller().unmarshal(new StreamSource(uri));
        return articlesetType;
    }

    /**
     * Return a List of Abstracts extracted from a PubmedArticleSet. Don't include those with null title or PMID.
     *
     * @param articleSet a PubmedArticleSet
     * @return a list of Abstract objects
     */
    static List<Abstract> getAbstracts(PubmedArticleSetDocument articleSetDocument) {
        List<Abstract> abstractList = new ArrayList<>();
        for (PubmedArticleType pubmedArticleType : articleSetDocument.getPubmedArticleSet().getPubmedArticleArray()) {
            Abstract a = new Abstract(pubmedArticleType);
            if (a.getTitle()!=null && a.getPMID()!=null) {
                abstractList.add(a);
            }
        }
        return abstractList;
    }
    
    /**
     * Retrieve a single Abstract for a single PMID, null if not found.
     *
     * @param pmid a PMID
     * @param apikey an optional PubMed API key
     * @return an Abstract, null if not found.
     */
    public static Abstract getAbstract(String pmid, String apikey) throws JAXBException, XMLStreamException, MalformedURLException, XmlException, IOException {
        String uri = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=pubmed&rettype=abstract&id="+pmid;
        if (apikey!=null) uri += "&api_key=" + apikey;
        PubmedArticleSetDocument articleSet = getPubmedArticleSet(uri);
        List<Abstract> abstracts = getAbstracts(articleSet);
        if (abstracts.size()>0) {
            return abstracts.get(0);
        } else {
            return null;
        }
    }

    /**
     * Retrieve a List of Abstracts for a List of article PMIDs.
     * There is a limit at PubMed on the number that can be fetched at once, so we split into groups of 100.
     *
     * @param idList a List of PMIDs
     * @param apikey an optional PubMed API key
     * @return a list of Abstract objects
     */
    public static List<Abstract> getAbstracts(List<String> idList, String apikey) throws JAXBException, XMLStreamException, MalformedURLException, XmlException, IOException {
        List<Abstract> abstracts = new ArrayList<>();
        String uri = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=pubmed&rettype=abstract";
        if (apikey!=null) uri += "&api_key=" + apikey;
        int start = 0;
        int end = Math.min(100, idList.size());
        boolean hasMore = true;
        while (hasMore) {
            List<String> idSubList = idList.subList(start, end);
            String subUri = uri + "&id=" + getCommaSeparatedString(idSubList);
            PubmedArticleSetDocument articleSet = getPubmedArticleSet(subUri);
            abstracts.addAll(getAbstracts(articleSet));
            start = Math.min(start + 100, idList.size());
            end = Math.min(end + 100, idList.size());
            hasMore = start < idList.size();
        }
        return abstracts;
    }
    
    /**
     * Return a single Summary given a PMID.
     */
    public static Summary getSummary(String pmid, String apikey) throws SAXException, JAXBException, XMLStreamException {
        List<Summary> summaries = new ArrayList<>();
        String uri = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=pubmed&id="+pmid;
        if (apikey!=null) uri += "&api_key="+apikey;
        ESummaryResult result = getESummaryResult(uri);
        for (Object o : result.getDocSumOrERROR()) {
            if (o instanceof DocSum) {
                return new Summary((DocSum) o);
            } else if (o instanceof org.ncgr.pubmed.xml.esummary.ERROR) {
                return new Summary((org.ncgr.pubmed.xml.esummary.ERROR) o);
            }
        }
        return null;
    }
    
    /**
     * Return a List of Summaries given a List of PMIDs, empty if none found.
     *
     * @param idList List of PMIDs
     * @param apikey optional PubMed API key
     * @return a List of Summary objects
     */
    public static List<Summary> getSummaries(List<String> idList, String apikey) throws SAXException, JAXBException, XMLStreamException {
        List<Summary> summaries = new ArrayList<>();
        String uri = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=pubmed&id="+getCommaSeparatedString(idList);
        if (apikey!=null) uri += "&api_key="+apikey;
        ESummaryResult result = getESummaryResult(uri);
        for (Object o : result.getDocSumOrERROR()) {
            if (o instanceof DocSum) {
                summaries.add(new Summary((DocSum) o));
            } else if (o instanceof org.ncgr.pubmed.xml.esummary.ERROR) {
                summaries.add(new Summary((org.ncgr.pubmed.xml.esummary.ERROR) o));
            }
        }
        return summaries;
    }

    /**
     * Return a single Article given a PMCID, null if not found.
     *
     * @param pmcid the PMCID
     * @param apikey an optional PubMed API key
     * @return an Article, or null if not found
     */
    public static Article getArticle(String pmcid, String apikey) throws SAXException, JAXBException, XMLStreamException {
        String uri = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=pmc&id="+pmcid;
        if (apikey!=null) uri += "&api_key="+apikey;
        PmcArticleset articleSet = getPmcArticleset(uri);
        for (org.ncgr.pubmed.xml.nlmarticleset.Article a : articleSet.getArticle()) {
            return new Article(a);
        }
        return null;
    }

    /**
     * Return a List of Articles given a List of PMCIDs, empty if none found.
     *
     * @param pmcidList a List of PMCIDs
     * @param apikey an optional PubMed API key
     * @return a List of Articles, empty if none found
     */
    public static List<Article> getArticles(List<String> pmcidList, String apikey) throws SAXException, JAXBException, XMLStreamException {
        List<Article> articles = new ArrayList<>();
        String uri = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=pmc&id="+getCommaSeparatedString(pmcidList);
        if (apikey!=null) uri += "&api_key="+apikey;
        PmcArticleset articleSet = getPmcArticleset(uri);
        for (org.ncgr.pubmed.xml.nlmarticleset.Article a : articleSet.getArticle()) {
            articles.add(new Article(a));
        }
        return articles;
    }
    
    /**
     * Return the count from an ESearchResult, 0 if not found.
     */
    static int getESearchResultCount(ESearchResult result) {
        for (Object o : result.getCountOrRetMaxOrRetStartOrQueryKeyOrWebEnvOrIdListOrTranslationSetOrTranslationStackOrQueryTranslationOrERROR()) {
            if (o instanceof Count) {
                return Integer.parseInt(((Count)o).getvalue());
            }
        }
        return 0;
    }

    /**
     * Return the error from an ESearchResult, null if not found.
     */
    static org.ncgr.pubmed.xml.esearch.ERROR getESearchResultERROR(ESearchResult result) {
        for (Object o : result.getCountOrRetMaxOrRetStartOrQueryKeyOrWebEnvOrIdListOrTranslationSetOrTranslationStackOrQueryTranslationOrERROR()) {
            if (o instanceof org.ncgr.pubmed.xml.esearch.ERROR) {
                return (org.ncgr.pubmed.xml.esearch.ERROR) o;
            }
        }
        return null;
    }

    /**
     * Return the List of PMIDs from an ESearchResult, empty if none given.
     */
    static List<String> getESearchResultIdList(ESearchResult result) {
        List<String> idList = new ArrayList<>();
        for (Object o : result.getCountOrRetMaxOrRetStartOrQueryKeyOrWebEnvOrIdListOrTranslationSetOrTranslationStackOrQueryTranslationOrERROR()) {
            if (o instanceof IdList) {
                for (Id id : ((IdList)o).getId()) {
                    idList.add(id.getvalue());
                }
            }
        }
        return idList;
    }
    
    /**
     * Return a Abstract searching on DOI with an optional API key; null if not found.
     *
     * @param doi the DOI
     * @param apikey an optional PubMed API key
     * @return an Abstract, null if not found
     */
    public static Abstract searchAbstractDOI(String doi, String apikey)
        throws UnsupportedEncodingException, SAXException, JAXBException, XMLStreamException, MalformedURLException, XmlException, IOException {
        String uri = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&term="+URLEncoder.encode(doi,"UTF-8")+"[DOI]";
        if (apikey!=null) uri += "&api_key="+apikey;
        ESearchResult result = getESearchResult(uri);
        org.ncgr.pubmed.xml.esearch.ERROR error = getESearchResultERROR(result);
        if (error!=null) return new Abstract(error);
        List<String> idList = getESearchResultIdList(result);
        // get single Abstract and return it
        if (idList.size()>0) {
            return getAbstract(idList.get(0), apikey);
        } else {
            return null;
        }
    }

    /**
     * Return a List of Abstracts that match a given search term in the abstract text. Limit the number with retmax.
     *
     * @param term the search term
     * @param retmax the maximum number of articles to be returned
     * @param apikey optional PubMed API key
     * @return a List of Abstracts, empty if none found
     */
    public static List<Abstract> searchAbstractText(String term, int retmax, String apikey)
        throws UnsupportedEncodingException, SAXException, JAXBException, XMLStreamException, MalformedURLException, XmlException, IOException {
        String uri = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&retmax="+retmax+"&term="+URLEncoder.encode(term,"UTF-8")+"[Abstract]";
        if (apikey!=null) uri += "&api_key=" + apikey;
        ESearchResult result = getESearchResult(uri);
        org.ncgr.pubmed.xml.esearch.ERROR error = getESearchResultERROR(result);
        if (error!=null) {
            List<Abstract> abstracts = new ArrayList<>();
            abstracts.add(new Abstract(error));
            return abstracts;
        }
        List<String> idList = getESearchResultIdList(result);
        if (idList.size()>0) {
            return getAbstracts(idList, apikey);
        } else {
            return new ArrayList<Abstract>();
        }
    }

    /**
     * Return a List of Abstracts that match a given search term in the abstract title. Limit the number with retmax.
     *
     * @param term the search term
     * @param retmax the maximum number of articles to be returned
     * @param apikey optional PubMed API key
     * @return a List of Abstracts, empty if none found
     */
    public static List<Abstract> searchAbstractTitle(String term, int retmax, String apikey)
        throws UnsupportedEncodingException, SAXException, JAXBException, XMLStreamException, MalformedURLException, XmlException, IOException {
        String uri = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&retmax="+retmax+"&term="+URLEncoder.encode(term,"UTF-8")+"[Title]";
        if (apikey!=null) uri += "&api_key=" + apikey;
        ESearchResult result = getESearchResult(uri);
        org.ncgr.pubmed.xml.esearch.ERROR error = getESearchResultERROR(result);
        if (error!=null) {
            List<Abstract> abstracts = new ArrayList<>();
            abstracts.add(new Abstract(error));
            return abstracts;
        }
        List<String> idList = getESearchResultIdList(result);
        if (idList.size()>0) {
            return getAbstracts(idList, apikey);
        } else {
            return new ArrayList<Abstract>();
        }
    }

    /**
     * Return a List of Abstracts that match a given search term in the abstract title or text. Limit the number with retmax.
     *
     * @param term the search term
     * @param retmax the maximum number of articles to be returned
     * @param apikey optional PubMed API key
     * @return a List of Abstracts, empty if none found
     */
    public static List<Abstract> searchAbstractTitleAndText(String term, int retmax, String apikey)
        throws UnsupportedEncodingException, SAXException, JAXBException, XMLStreamException, MalformedURLException, XmlException, IOException {
        String uri = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&retmax="+retmax+"&term="+URLEncoder.encode(term,"UTF-8")+"[Title/Abstract]";
        if (apikey!=null) uri += "&api_key=" + apikey;
        ESearchResult result = getESearchResult(uri);
        org.ncgr.pubmed.xml.esearch.ERROR error = getESearchResultERROR(result);
        if (error!=null) {
            List<Abstract> abstracts = new ArrayList<>();
            abstracts.add(new Abstract(error));
            return abstracts;
        }
        List<String> idList = getESearchResultIdList(result);
        if (idList.size()>0) {
            return getAbstracts(idList, apikey);
        } else {
            return new ArrayList<Abstract>();
        }
    }
    
    /**
     * Return a Summary searching on DOI with an optional API key. Returns null if none found.
     *
     * @param doi the DOI
     * @param apikey an optional PubMed API key
     * @return a Summary
     */
    public static Summary searchSummaryDOI(String doi, String apikey) throws UnsupportedEncodingException, SAXException, JAXBException, XMLStreamException {
        String uri = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&term="+URLEncoder.encode(doi,"UTF-8")+"[DOI]";
        if (apikey!=null) uri += "&api_key="+apikey;
        ESearchResult result = getESearchResult(uri);
        org.ncgr.pubmed.xml.esearch.ERROR error = getESearchResultERROR(result);
        if (error!=null) {
            return new Summary(error);
        }
        List<String> idList = getESearchResultIdList(result);
        if (idList.size()>0) {
            return getSummary(idList.get(0), apikey);
        } else {
            return null;
        }        
    }

    /**
     * Return a List of Summary by searching on title. Set apikey to null if not supplied. Return empty list if none found.
     *
     * @param title the title to search
     * @param retmax the maximum number of returned articles
     * @param apikey optional PubMed API key
     * @return a single Summary resulting from the search
     */
    public static List<Summary> searchSummaryTitle(String title, int retmax, String apikey) throws UnsupportedEncodingException, SAXException, JAXBException, XMLStreamException {
        String uri = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&retmax="+retmax+"&term="+URLEncoder.encode(title,"UTF-8")+"[Title]";
        if (apikey!=null) uri += "&api_key="+apikey;
        ESearchResult result = getESearchResult(uri);
        org.ncgr.pubmed.xml.esearch.ERROR error = getESearchResultERROR(result);
        if (error!=null) {
            List<Summary> list = new ArrayList<>();
            list.add(new Summary(error));
            return list;
        }
        List<String> idList = getESearchResultIdList(result);
        if (idList.size()>0) {
            return getSummaries(idList, apikey);
        } else {
            return new ArrayList<Summary>();
        }
    }
    
    /**
     * Return a comma-separated String formed from a List of Strings.
     */
    static String getCommaSeparatedString(List<String> list) {
        return list.toString().replace("[","").replace("]","").replace(" ","");
    }
    
    /**
     * Command-line utility.
     */
    public static void main(String[] args) throws SAXException, JAXBException, XMLStreamException, UnsupportedEncodingException, MalformedURLException, XmlException, IOException {
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        Option abstractOption = new Option("abs", "abstract", false, "Retrieve PubMed abstracts");
        abstractOption.setRequired(false);
        options.addOption(abstractOption);

        Option articleOption = new Option("art", "article", false, "Retrieve PMC articles");
        articleOption.setRequired(false);
        options.addOption(articleOption);

        Option summaryOption = new Option("sum", "summary", false, "Retrieve PubMed summaries");
        summaryOption.setRequired(false);
        options.addOption(summaryOption);
        
        Option apikeyOption = new Option("key", "apikey", true, "PubMed API key [optional]");
        apikeyOption.setRequired(false);
        options.addOption(apikeyOption);
        
        Option idsOption = new Option("ids", "ids", true, "comma-separated list of PMIDs or PMCIDs for efetch/esummary request");
        idsOption.setRequired(false);
        options.addOption(idsOption);

        Option textOption = new Option("t", "text", true, "value of text for esearch request");
        textOption.setRequired(false);
        options.addOption(textOption);

        Option doiOption = new Option("d", "doi", true, "value of DOI for esearch request");
        doiOption.setRequired(false);
        options.addOption(doiOption);

        Option retmaxOption = new Option("m", "retmax", true, "maximum number of returned objects [20]");
        retmaxOption.setRequired(false);
        options.addOption(retmaxOption);
        
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp("Pubmed", options);
            System.exit(1);
            return;
        }
        if (cmd.getOptions().length==0) {
            formatter.printHelp("Pubmed", options);
            System.exit(1);
            return;
        }

        if (!cmd.hasOption("abstract") && !cmd.hasOption("article") && !cmd.hasOption("summary")) {
            System.err.println("You must choose either --abstract --article or --summary.");
            System.exit(1);
        }

        String apikey = null;
        if (cmd.hasOption("apikey")) apikey = cmd.getOptionValue("apikey");

        int retmax = 20;
        if (cmd.hasOption("retmax")) retmax = Integer.parseInt(cmd.getOptionValue("retmax"));
        
        if (cmd.hasOption("ids")) {
            String ids = cmd.getOptionValue("ids");
            List<String> idList = Arrays.asList(ids.split(","));
            if (cmd.hasOption("abstract")) {
                List<Abstract> abstracts = getAbstracts(idList, apikey);
                for (Abstract a : abstracts) {
                    System.out.println(a.toString());
                }
            }
            if (cmd.hasOption("summary")) {
                List<Summary> summaries = getSummaries(idList, apikey);
                for (Summary s : summaries) {
                    System.out.println(s.toString());
                }
            }
            if (cmd.hasOption("article")) {
                List<Article> articles = getArticles(idList, apikey);
                for (Article a : articles) {
                    System.out.println(a.toString());
                }
            }
        }
            
        if (cmd.hasOption("text")) {
            String text = cmd.getOptionValue("text");
            if (cmd.hasOption("abstract")) {
                // search abstract title and text
                List<Abstract> abstracts = searchAbstractTitleAndText(text, retmax, apikey);
                // output
                for (Abstract a : abstracts) {
                    System.out.println(a.toString());
                }
            }
            if (cmd.hasOption("summary")) {
                // search summary titles
                List<Summary> summaries = searchSummaryTitle(text, retmax, apikey);
                // output
                for (Summary s : summaries) {
                    System.out.println(s.toString());
                }
            }
        }
        
        if (cmd.hasOption("doi")) {
            String doi = cmd.getOptionValue("doi");
            if (cmd.hasOption("abstract")) {
                Abstract a = searchAbstractDOI(doi, apikey);
                if (a!=null) {
                    System.out.println(a.toString());
                }
            }
            if (cmd.hasOption("summary")) {
                Summary summary = searchSummaryDOI(doi, apikey);
                if (summary!=null) {
                    System.out.println(summary.toString());
                }
            }
        }
    }
}
