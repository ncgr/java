##
## load PCA data and run prcomp
##

## for plotting and analysis
library(ggfortify)
library(factoextra)

## get the file prefix from the user
prefix = readline(prompt="graph prefix (e.g. 3q29): ")

## ## load paths
## ## V1        V2   V3 V4 V5 V6 V7 V8 V9 V10 V11 V12 V13 V14 V15 V16 V17 V18 V19
## ## HG00123.0 ctrl 1606  1  3  4  3  7  3  10   2  13  11  16   5  19  11  22   5
## paths = read.table(paste(prefix,"paths.txt", sep="."))
## rownames(paths) = paste(paths$V1,paths$V2, sep=".")
## paths$V1 = NULL
## paths$V2 = NULL
## paths$V3 = NULL

## load/prune pathpca records
pathpca = read.table(paste(prefix,"pathpca.txt", sep="."))

## paths data frame from pathpca data frame (rather than paths file)
paths = as.data.frame(t(pathpca))
pca = prcomp(pathpca, center=TRUE)

## get the results for variables (nodes)
## res.var$coord          # Coordinates
## res.var$contrib        # Contributions to the PCs
## res.var$cos2           # Quality of representation 
pca.var = get_pca_var(pca)

## get the results for individuals
## res.ind$coord          # Coordinates
## res.ind$contrib        # Contributions to the PCs
## res.ind$cos2           # Quality of representation
pca.ind = get_pca_ind(pca)

## which are cases and which are controls
cases = endsWith(rownames(paths), "case")
controls = endsWith(rownames(paths), "ctrl")

## append case/control label to paths
paths$Label[cases] = "case"
paths$Label[controls] = "ctrl"

