## load paths for PCA

prefix = readline(prompt="FR file prefix (ex. HTT.400-0.5-1 or HTT.save): ")
pathfrs = read.table(file=paste(prefix,".pathfrs.txt",sep=""), stringsAsFactors=FALSE, check.names=FALSE)

## PCA on path FR vectors
paths = as.data.frame(t(pathfrs))
pca = prcomp(paths, center=TRUE)
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

## determine which are cases and which are controls
cases = endsWith(rownames(paths), "case")
cases.0 = endsWith(rownames(paths), "0.case")
cases.1 = endsWith(rownames(paths), "1.case")
controls = endsWith(rownames(paths), "ctrl")
controls.0 = endsWith(rownames(paths), "0.ctrl")
controls.1 = endsWith(rownames(paths), "1.ctrl")

## append case/control label to paths
paths$Label[cases] = "case"
paths$Label[controls] = "ctrl"
