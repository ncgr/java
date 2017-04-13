# intermine
These are Java apps written to perform various tasks using the InterMine PathQuery API.

Compile: ```ant classes```

```./run org.ncgr.intermine.FastaQueryClient https://apps.araport.org/thalemine/service REV```
```./run org.ncgr.intermine.ModelViewer https://apps.araport.org/thalemine/service```
```./run org.ncgr.intermine.neo4j.Neo4jLoader```
```./run org.ncgr.intermine.neo4j.Neo4jNodeLoader Gene 4295368``` 
```./run org.ncgr.intermine.neo4j.Neo4jEdgeLoader Gene homologues homologue```

FastaQueryClient: prints the FASTA for the gene with the given symbol. Parameters: IM service URL and gene symbol.

ModelViewer: prints out the full data model from a mine. Parameter: IM service URL.

Neo4jLoader: loads a mine into Neo4j. Parameters in neo4jloader.properties.

Neo4jNodeLoader: load a single node into Neo4j referenced by class and IM id. Parameters in neo4jloader.properties.

Neo4jEdgeLoader: load edges into Neo4j given by source class, edge reference or collection, and target class name as referenced in the edge class. This converts IM objects
which store relations into Neo4j edges with properties. 
