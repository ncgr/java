##
## spin through two-component PCA dotplots
##
## presumes that paths.pca and paths are already populated

for (x in 1:min(9,dim(paths.pca$rotation)[2])) {
    for (y in (x+1):min(10,dim(paths.pca$rotation)[2]+1)) {
        show(autoplot(paths.pca, data=paths, colour="Label", x=x, y=y,
	              main=paste(prefix,":",dim(paths[cases,])[1],"cases",dim(paths[controls,])[1],"controls")
		      ))
        invisible(readline(prompt="Press [enter] to continue"))
    }
}
