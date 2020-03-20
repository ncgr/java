##
## spin through PCA rotations for neighboring nodes
##
## presumes that paths.pca and paths are already populated

for (n in 1:(dim(paths.pca$rotation)[1]-1)) {
    for (m in (n+1):dim(paths.pca$rotation)[1]) {
        plot(paths.pca$rotation[n,], xlab="PC", col="red",
	     main=paste(rownames(paths.pca$rotation)[n],rownames(paths.pca$rotation)[m])
	     )
        points(paths.pca$rotation[m,], col="blue")
    	invisible(readline(prompt="Press [enter] to continue"))
    }
}
