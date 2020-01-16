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
frs = read.table(frFilename, header=TRUE, stringsAsFactors=FALSE)
rownames(frs) = frs$FR
frs$FR = NULL

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

## paths for PCA
if (!isSaveSet) {
    pathfrs = read.table(file=paste(prefix,".pathfrs.txt",sep=""), stringsAsFactors=FALSE, check.names=FALSE)
    ## PCA on path FR vectors
    paths = as.data.frame(t(pathfrs))
    pca = prcomp(paths, center=TRUE)
    ## get the results for variables (nodes)
    ## res.var$coord          # Coordinates
    ## res.var$contrib        # Contributions to the PCs
    ## res.var$cos2           # Quality of representation 
    pca.var = get_pca_var(pca)
    ## get the results for individuals
    ## res.ind$coord          # Coordinates
    ## res.ind$contrib        # Contributions to the PCs
    ## res.ind$cos2           # Quality of representation
    pca.ind = get_pca_ind(pca)
    
    ## determine which are cases and which are controls
    cases = endsWith(rownames(paths), "case")
    cases.0 = endsWith(rownames(paths), "0.case")
    cases.1 = endsWith(rownames(paths), "1.case")
    controls = endsWith(rownames(paths), "ctrl")
    controls.0 = endsWith(rownames(paths), "0.ctrl")
    controls.1 = endsWith(rownames(paths), "1.ctrl")
    
    ## append case/control label to paths
    paths$Label[cases] = "case"
    paths$Label[controls] = "ctrl"
}

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
nodes = read.table(file=paste(graphName,"nodes","txt",sep="."), row.names=1, col.names=c("node","sequence"))
