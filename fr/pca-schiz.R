source("load-schiz.R")
pathfrs = read.table("schiz.pathfrs.txt", stringsAsFactors=FALSE, check.names=FALSE)
pca = prcomp(pathfrs, center=TRUE, scale.=FALSE)
source("plot-pca.R")
