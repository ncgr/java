##
## load the FRFinder output data
## prefix must be set
##

## FRs
frs = read.table(paste(prefix,".frs.txt",sep=""), header=TRUE, stringsAsFactors=FALSE)
rownames(frs) = frs$FR
frs$FR = NULL
for (i in 1:length(rownames(frs))) {
    frs$size[i] = length(strsplit(frs$nodes[i],",")[[1]])
}

## parameters
params = read.delim(file=paste(prefix,".params.txt",sep=""), header=FALSE, stringsAsFactors=FALSE)
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
    if (params$V1[i]=="outputprefix") outputprefix = params$V2[i]
    if (params$V1[i]=="date") date = params$V2[i]
    if (params$V1[i]=="clocktime") clocktime = params$V2[i]
}
rm(params)

## path FRs
pathfrs = read.table(file=paste(prefix,".pathfrs.txt",sep=""), stringsAsFactors=FALSE, check.names=FALSE)
pca = prcomp(pathfrs, center=TRUE, scale.=FALSE)

## label counts (if exists)
labelFile = paste(prefix,".labelcounts.txt",sep="")
labelsExist = file.exists(labelFile)
if (labelsExist) {
    labelcounts = read.delim(file=labelFile, header=FALSE, stringsAsFactors=FALSE, row.names=1)
    colnames(labelcounts) = c("count")
}

