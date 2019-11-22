##
## load an FR file along with its parameters
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
for (i in 1:length(rownames(frs))) {
    frs$size[i] = length(strsplit(frs$nodes[i],",")[[1]])
}

## divine alpha, kappa from filename like HTT.400-0.8-3
if (isSaveSet) {
    prefix.parts = strsplit(prefix, ".", fixed=TRUE);
    graphPrefix = prefix.parts[[1]][1]
    alpha = "NA"
    kappa = "NA"
} else {
    prefix.parts = strsplit(prefix, "-", fixed=TRUE)
    graphPrefix = prefix.parts[[1]][1]
    alpha = as.numeric(prefix.parts[[1]][2])
    kappa = as.numeric(prefix.parts[[1]][3])
}

## parameters
##
## #alpha=0.8
## #kappa=3
## #clocktime=00:00:02
## #Mon Oct 07 11:43:15 MDT 2019
## resume=false
## debug=false
## caseCtrl=true
## minSup=1
## bruteForce=false
## verbose=false
## maxRound=0
## serial=false
## prunedGraph=false
## gfaFile=HTT.400.paths.gfa
## minLen=1.0
## minSize=1
if (!isSaveSet) {
    params = read.delim(file=paste(prefix,".params.txt",sep=""), header=FALSE, stringsAsFactors=FALSE, sep="=")
    for (i in 1:length(rownames(params))) {
        ## comments section
        if (params$V1[i]=="#alpha") {
            alpha = as.numeric(params$V2[i])
        } else if (params$V1[i]=="#kappa") {
            kappa = as.numeric(params$V2[i])
        } else if (params$V1[i]=="#clocktime") {
            clocktime = params$V2[i]
        } else if (substr(params$V1[i], 1, 1)=="#") {
            date = substr(params$V1[i], 2, 1000)
        } else {
            ## main parameters section
            if (params$V1[i]=="resume") resume = as.logical(params$V2[i])
            if (params$V1[i]=="debug") debug = as.logical(params$V2[i])
            if (params$V1[i]=="caseCtrl") caseCtrl = as.logical(params$V2[i])
            if (params$V1[i]=="minSup") minSup = as.numeric(params$V2[i])
            if (params$V1[i]=="bruteForce") bruteForce = as.logical(params$V2[i])
            if (params$V1[i]=="verbose") verbose = as.logical(params$V2[i])
            if (params$V1[i]=="maxRound") maxRound = as.numeric(params$V2[i])
            if (params$V1[i]=="serial") serial = as.logical(params$V2[i])
            if (params$V1[i]=="prunedGraph") prunedGraph = as.logical(params$V2[i])
            if (params$V1[i]=="gfaFile") gfaFile = params$V2[i]
            if (params$V1[i]=="minLen") minLen = as.numeric(params$V2[i])
            if (params$V1[i]=="minSize") minSize = as.numeric(params$V2[i])
        }
    }
}

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
