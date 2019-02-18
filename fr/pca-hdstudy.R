source("load-hdstudy.R")
pathfrs = read.table("hdstudy.pathfrs.txt", stringsAsFactors=FALSE, check.names=FALSE)
pca = prcomp(pathfrs, center=TRUE, scale.=FALSE)
source("plot-pca.R")
