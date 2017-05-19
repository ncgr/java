# intermine
These are Java apps written to perform various tasks using the InterMine PathQuery API.

Compile: ```ant jar```

### org.ncgr.intermine.FastaQueryClient
Prints the FASTA for the gene with the given symbol. Parameters: IM service URL and gene symbol.

```./run org.ncgr.intermine.FastaQueryClient https://apps.araport.org/thalemine/service REV```

### org.ncgr.intermine.ModelViewer
Prints out the full data model from a mine. Parameter: IM service URL.

```./run org.ncgr.intermine.ModelViewer https://apps.araport.org/thalemine/service```
