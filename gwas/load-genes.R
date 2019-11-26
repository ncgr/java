##
## load B73 gene data from a GFF3 file
##

library(ape)

gffFile = "/home/shokin/genomes/Human/Homo_sapiens.GRCh37.87.chr.gff3"

## load GFF files
print("Loading GFF...", quote=F)
genes = read.gff(gffFile, GFF3=TRUE)

## extract genes
print("Extracting genes...", quote=F)
genes = genes[genes$type=="gene",]

## get lengths
genes$length = genes$end - genes$start

## parse the gene ID and name out of the attributes
## ID=gene:ENSG00000235249;Name=OR4F29;biotype=protein_coding;description=olfactory receptor%2C family 4%2C subfamily F%2C member 29 [Source:HGNC Symbol%3BAcc:31275];gene_id=ENSG00000235249;logic_name=ensembl_havana_gene;version=1
## strsplit(genes$attributes,";")[[20]][1]
## [1] "ID=gene:ENSG00000237330"
## strsplit(genes$attributes,";")[[20]][2]
## "Name=RNF223"
for (i in 1:nrow(genes)) {
    parts = strsplit(genes$attributes[i],";")[[1]]
    idParts = strsplit(parts[1],"=")[[1]]
    idMoreParts = strsplit(idParts[2],":")[[1]]
    nameParts = strsplit(parts[2],"=")[[1]]
    genes$ID[i] = idMoreParts[2]
    genes$name[i] = nameParts[2]
}
rownames(genes) = genes$ID
genes$ID = NULL
genes$source = NULL
genes$type = NULL
genes$score = NULL
genes$strand = NULL
genes$phase = NULL
genes$attributes = NULL

