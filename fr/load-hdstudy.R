## load a pair of FRFinder hdstudy files

frs = read.table("hdstudy.frs.txt", header=TRUE)
rownames(frs) = frs$FR
frs$FR = NULL

params = read.delim(file="hdstudy.params.txt", header=F)
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
