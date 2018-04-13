# ncgr/pubmed_morphia_example

An example of using the Morphia/MongoDB framework for storing PubMed summaries in a MongoDB database. Morphia allows one to convert a class to a MongoDB entity fairly painlessly. The PubMedSummaryObject is a thin wrapper around org.ncgr.pubmed.PubMedSummary that gets stored.

Authors are currently stored as a simple array within each pub entry, but those could be spit out into referenced entities of their own. 
