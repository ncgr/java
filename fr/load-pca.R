##
## load PCA data and run prcomp
##

prefix = readline(prompt="graph prefix (e.g. 3q29): ")

## load/prune nodes
nodes = read.table(paste(prefix,".pathpca.txt", sep=""))
nodes = nodes[rowSums(nodes)!=0 & rowSums(nodes)!=400,]

## paths data frame
paths = as.data.frame(t(nodes))
paths.pca = prcomp(paths, center=TRUE)

## add labels
paths$Label = substr(rownames(paths),10,14)

## plot PC2 vs PC1
library(ggfortify)
autoplot(paths.pca, data=paths, colour='Label', x=1, y=2)

