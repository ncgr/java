# org.ncgr.crossref
A package containing one class, **WorksQuery**, which handles querying CrossRef with two constructors:
- **WorksQuery**(String queryAuthor, String queryTitle) attemps to find a publication given author and/or title
- **WorksQuery**(String queryDOI) attempts to find a publication given a DOI

**WorksQuery** provides a bunch of getters to get all the CrossRef fields for a publication.
