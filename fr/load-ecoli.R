## load an FRFinder ecoli file

frs = read.table("ecoli.frs.txt", header=TRUE, stringsAsFactors=FALSE, row.names=1)

for (i in 1:length(rownames(frs))) {
    frs$size[i] = length(strsplit(rownames(frs)[i],",")[[1]])
}

nodes = read.table("ecoli.nodes.txt", header=FALSE, stringsAsFactors=FALSE, row.names=1, check.names=FALSE)
colnames(nodes) = c("sequence")
for (i in 1:length(rownames(nodes))) {
    nodes$seqlen[i] = nchar(nodes$sequence[i])
}

params = read.delim(file="ecoli.params.txt", header=FALSE, stringsAsFactors=FALSE)
for (i in 1:length(rownames(params))) {
    if (params$V1[i]=="genotype") genotype = params$V2[i]
    if (params$V1[i]=="alpha") alpha = params$V2[i]
    if (params$V1[i]=="kappa") kappa = params$V2[i]
    if (params$V1[i]=="minsup") minsup = params$V2[i]
    if (params$V1[i]=="maxsup") maxsup = params$V2[i]
    if (params$V1[i]=="minsize") minsize = params$V2[i]
    if (params$V1[i]=="minlen") minlen = params$V2[i]
    if (params$V1[i]=="jsonfile") jsonfile = params$V2[i]
    if (params$V1[i]=="gfafile") gfafile = params$V2[i]
    if (params$V1[i]=="dotfile") dotfile = params$V2[i]
    if (params$V1[i]=="fastafile") fastafile = params$V2[i]
}
rm(params)
    
