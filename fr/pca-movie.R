##
## spin through two-component PCA dotplots
##
## presumes that pca and paths are already populated

for (x in 1:min(9,dim(pca$rotation)[2])) {
    for (y in (x+1):min(10,dim(pca$rotation)[2]+1)) {
        show(autoplot(pca, data=paths, colour="Label", x=x, y=y,
	              main=paste(prefix,":",dim(paths[cases,])[1],"case paths",dim(paths[controls,])[1],"control paths")
		      ))
        invisible(readline(prompt="Press [enter] to continue"))
    }
}
