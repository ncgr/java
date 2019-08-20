##
## load PCA data and run prcomp
##

## for plotting and analysis
library(ggfortify)
library(factoextra)

## get the file prefix from the user
prefix = readline(prompt="graph prefix (e.g. 3q29): ")

## load/prune nodes
nodes = read.table(paste(prefix,".pathpca.txt", sep=""))

nodes = nodes[rowSums(nodes)!=0 & rowSums(nodes)!=dim(nodes)[2],]

## paths data frame
paths = as.data.frame(t(nodes))

## which are cases and which are controls
cases = endsWith(rownames(paths.pca.ind$contrib), "case")
controls = endsWith(rownames(paths.pca.ind$contrib), "ctrl")

## compute PCA using prcomp
paths.pca = prcomp(paths, center=TRUE)

## get the results for variables (nodes)
## res.var$coord          # Coordinates
## res.var$contrib        # Contributions to the PCs
## res.var$cos2           # Quality of representation 
paths.pca.var = get_pca_var(paths.pca)

## get the results for individuals
## res.ind$coord          # Coordinates
## res.ind$contrib        # Contributions to the PCs
## res.ind$cos2           # Quality of representation
paths.pca.ind = get_pca_ind(paths.pca)

## add labels to data frames
paths$Label[cases] = "case"
paths$Label[controls] = "ctrl"

