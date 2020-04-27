##
## load an FR file along with its parameters
## nodes support avgLen case ctrl size
##

## for plotting and analysis
library(ggfortify)
library(factoextra)

prefix = readline(prompt="FR file prefix (ex. HTT.400-0.5-1 or HTT.save): ")
isSaveSet = grepl("save", prefix, fixed=TRUE)
frFilename = paste(prefix, ".frs.txt", sep="")

## read the FRs table, either a save file or a proper output file
## nodes	size	support	case	ctrl	OR	p	pri
frs = read.table(frFilename, header=TRUE, stringsAsFactors=FALSE)
rownames(frs) = frs$nodes
frs$nodes = NULL

## divine alpha, kappa from filename like HTT.400-0.8-3
if (isSaveSet) {
    prefix.parts = strsplit(prefix, ".save", fixed=TRUE);
    graphPrefix = prefix.parts[[1]][1]
} else {
    prefix.parts = strsplit(prefix, "-", fixed=TRUE)
    graphPrefix = prefix.parts[[1]][1]
    alpha = as.numeric(prefix.parts[[1]][2])
    kappa = as.numeric(prefix.parts[[1]][3])
}

## load the parameters from the params.txt file
source("load-params.R")

## label counts (if exists)
labelFile = paste(graphPrefix,".labelcounts.txt",sep="")
labelsExist = file.exists(labelFile)
if (labelsExist) {
    labelCounts = read.delim(file=labelFile, header=FALSE, stringsAsFactors=FALSE, row.names=1)
    colnames(labelCounts) = c("count")
}

## odds ratio (if label counts exist)
if (labelsExist) {
    casePaths = labelCounts["case",1]
    ctrlPaths = labelCounts["ctrl",1]
    frs$OR = (frs$case/frs$ctrl) / (casePaths/ctrlPaths)
}

## nodes
## 1	rs114039523	6	29910286	29910286	T/T	0.6643768400392541
## 4	rs114039523	6	29910286	29910286	./.	8.92140244446427E-5
nodes = read.table(file=paste(graphName,"nodes","txt",sep="."), row.names=1, col.names=c("node","rs","chr","start","end","genotype","p"))

