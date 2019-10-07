##
## load an FR file along with its parameters
##

prefix = readline(prompt="FR file prefix (ex. HTT.400-0.5-1): ")

## FRs
frs = read.table(paste(prefix,".frs.txt",sep=""), header=TRUE, stringsAsFactors=FALSE)
rownames(frs) = frs$FR
frs$FR = NULL
for (i in 1:length(rownames(frs))) {
    frs$size[i] = length(strsplit(frs$nodes[i],",")[[1]])
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
## outputPrefix=HTT.400-0.8-3
## bruteForce=false
## verbose=false
## maxRound=0
## serial=false
## prunedGraph=false
## gfaFile=HTT.400.paths.gfa
## minLen=1.0
## minSize=1
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
        if (params$V1[i]=="outputPrefix") outputPrefix = params$V2[i]
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

## path FRs
pathfrs = read.table(file=paste(prefix,".pathfrs.txt",sep=""), stringsAsFactors=FALSE, check.names=FALSE)
pca = prcomp(pathfrs, center=TRUE, scale.=FALSE)

## get the path labels
pathLabels = c()
dotParts = strsplit(colnames(pathfrs), ".", fixed=TRUE)
for (i in 1:length(dotParts)) {
    pathLabels = c(pathLabels, dotParts[[i]][3])
}

## label counts (if exists)
labelFile = paste(prefix,".labelcounts.txt",sep="")
labelsExist = file.exists(labelFile)
if (labelsExist) {
    labelCounts = read.delim(file=labelFile, header=FALSE, stringsAsFactors=FALSE, row.names=1)
    colnames(labelCounts) = c("count")
}

