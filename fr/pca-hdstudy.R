source("load-hdstudy.R")

paths = read.table("hdstudy.paths.txt", header=TRUE, row.names=1, check.names=FALSE)
pca = prcomp(paths, center=TRUE, scale.=FALSE)

source("plot-pca.R")
