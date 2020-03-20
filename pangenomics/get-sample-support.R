##
## get the case/control support per sample, based on HET or HOM requirement
##

source("pathName.R")
source("pathLabel.R")
source("pathGenotype.R")

## query whether to count support as homozygous (both paths) or heterozygous (one or both paths)
requireHomozygous = as.logical(readline(prompt="Require homozygous calls (TRUE/FALSE): "))
if (requireHomozygous) {
    mergeMode = "HOM mode: sample is called CASE iff both paths are called CASE"
} else {
    mergeMode = "HET mode: sample is called CASE if at least ONE path is called CASE"
}   

## run through the paths and FRs, determining whether they are HET/HOM supporters, and counting
##     128686.1.case 974679.0.case 412119.0.ctrl 434159.1.case 391824.0.ctrl
## FR1             0             0             0             0             0
## FR2             0             0             0             0             0

## initialize sample support with zeros per sample
numFRs = length(rownames(pathfrs))
numSamples = length(colnames(pathfrs))/2
sampleSupport = setNames(data.frame(matrix(ncol=(numFRs+1), nrow=0)), c("label",rownames(pathfrs)))

## build support per sample
for (path in colnames(pathfrs)) {
    sampleName = pathName(path)
    sampleSupport[sampleName,"label"] = pathLabel(path)
    for (fr in rownames(pathfrs)) {
        if (is.na(sampleSupport[sampleName,fr])) {
            sampleSupport[sampleName,fr] = pathfrs[fr,path]
        } else if (requireHomozygous) {
            ## assume that the minimum support of the two genotypes is homozygous support
            sampleSupport[sampleName,fr] = min(sampleSupport[sampleName,fr], pathfrs[fr,path])
        } else {
            ## support from either path is heterozygous support
            sampleSupport[sampleName,fr] = max(sampleSupport[sampleName,fr], pathfrs[fr,path])
        }
    }
}
