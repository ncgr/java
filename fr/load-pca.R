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
paths.split = strsplit(rownames(paths), ".", fixed=TRUE)
for (i in 1:length(rownames(paths))) {
    paths$Label[i] = paths.split[[i]][3]
}

## plot PC2 vs PC1
library(ggfortify)
autoplot(paths.pca, data=paths, colour='Label', x=1, y=2)

