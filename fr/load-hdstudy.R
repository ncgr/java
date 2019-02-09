## load a pair of FRFinder hdstudy files

hdstudy = read.table("hdstudy.out", header=TRUE)
rownames(hdstudy) = hdstudy$FR
hdstudy$FR = NULL

params = read.delim(file="hdstudy.out.params", header=F)
for (i in 1:length(rownames(params))) {
    if (params$V1[i]=="json") json = params$V2[i]
    if (params$V1[i]=="pathlabels") pathlabels = params$V2[i]
    if (params$V1[i]=="genotype") genotype = params$V2[i]
    if (params$V1[i]=="alpha") alpha = params$V2[i]
    if (params$V1[i]=="kappa") kappa = params$V2[i]
    if (params$V1[i]=="minsup") minsup = params$V2[i]
    if (params$V1[i]=="maxsup") maxsup = params$V2[i]
    if (params$V1[i]=="minsize") minsize = params$V2[i]
    if (params$V1[i]=="minlen") minlen = params$V2[i]
    if (params$V1[i]=="nrounds") nrounds = params$V2[i]
    if (params$V1[i]=="mincasectrlratio") mincasectrlratio = params$V2[i]
}
rm(params)
