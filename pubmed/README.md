# org.ncgr.pubmed
Java library for searching and reading PubMed XML data.

## xmlbeans
This package uses org.apache.xmlbeans to generate the XML schema from the `eutils.xsd` file.
JAXB has a problem in that it doesn't handle HTML within tags (which is common in PubMed abstracts and even titles).

The xmlbeans tool that is used to parse an XSD is `scomp` as follows:
```
scomp -d bin -src src/main/java -out libs/eutils.jar -dl eutils.xsd 
```
Unfortunately, `scomp` does not handle DTD files, which is what NCBI provides. I was able to obtain `eutils.xsd` from
a Github repo.

## JAXB
Conversion of the DTD to XSD has been problematic. So I generated XML schema from other DTDs using the JAXB tool xjc,
and use JAXB methods to parse those cases.

Using both xmlbeans and JAXB is clearly not optimal, but the lack of XSD for the PubMed schema is a real annoyance. But I've
spent a lot of time trying to generate XSD from the DTDs and there always seem to be problems.
