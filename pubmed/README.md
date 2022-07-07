# org.ncgr.pubmed
Java library for searching and reading PubMed XML data.

The main class of interest is **PubMedSummary** which has a default constructor and provides a number of search methods:
- search(int id) find a PubMed summary from a PMID using the anonymous API
- search(int id, String apiKey) same, but higher performance API with an API key
- searchTitle(String title) search for a PubMed summary from a given title using the anonymous API
- searchTitle(String title, String apiKey) same, but higher performance with an API key provided
- searchDOI(String doi) search for a PubMed summary given a DOI using the anonymous API
- searchDOI(String doi, String apiKey) same, using the higher performance API
