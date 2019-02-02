df = read.table("HDStudy/HTT.paths.json.pca.txt")
pca = prcomp(df, center=TRUE, scale.=TRUE)
source("plot-pca.R")
