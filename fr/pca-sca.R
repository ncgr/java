source("load-sca.R")
pathfrs = read.table("sca.pathfrs.txt", stringsAsFactors=FALSE, check.names=FALSE)
pca = prcomp(pathfrs, center=TRUE, scale.=FALSE)
source("plot-pca.R")
