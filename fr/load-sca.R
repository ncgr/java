## load an FRFinder sca file

frs = read.table("sca.frs.txt", header=TRUE, stringsAsFactors=FALSE)
rownames(frs) = frs$FR
frs$FR = NULL

for (i in 1:length(rownames(frs))) {
    frs$size[i] = length(strsplit(frs$nodes[i],",")[[1]])
}

params = read.delim(file="sca.params.txt", header=FALSE, stringsAsFactors=FALSE)
for (i in 1:length(rownames(params))) {
    if (params$V1[i]=="genotype") genotype = params$V2[i]
    if (params$V1[i]=="alpha") alpha = params$V2[i]
    if (params$V1[i]=="kappa") kappa = params$V2[i]
    if (params$V1[i]=="minsup") minsup = params$V2[i]
    if (params$V1[i]=="maxsup") maxsup = params$V2[i]
    if (params$V1[i]=="minsize") minsize = params$V2[i]
    if (params$V1[i]=="minlen") minlen = params$V2[i]
    if (params$V1[i]=="casectrl") casectrl = params$V2[i]
    if (params$V1[i]=="jsonfile") jsonfile = params$V2[i]
    if (params$V1[i]=="gfafile") gfafile = params$V2[i]
    if (params$V1[i]=="dotfile") dotfile = params$V2[i]
    if (params$V1[i]=="fastafile") fastafile = params$V2[i]
    if (params$V1[i]=="pathlabels") pathlabels = params$V2[i]
}
rm(params)

labelcounts = read.delim(file="sca.labelcounts.txt", header=FALSE, stringsAsFactors=FALSE, row.names=1)
colnames(labelcounts) = c("count")
    
