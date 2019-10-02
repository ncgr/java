##
## plot a histogram of the PCA coordinates of the individuals
##

plot.pca.hist = function(pc) {
    sum.cases = sum(paths.pca.ind$coord[cases,pc])/length(paths.pca.ind$coord[cases,pc])
    sum.controls = sum(paths.pca.ind$coord[controls,pc])/length(paths.pca.ind$coord[controls,pc])
    cases.controls = sum.cases - sum.controls
    xmin = min(paths.pca.ind$coord[,pc]) - 1
    xmax = max(paths.pca.ind$coord[,pc]) + 1
    dx = (xmax - xmin)/20
    breaks = xmin + dx*(0:20)
    h.cases = hist(paths.pca.ind$coord[cases,pc], breaks=breaks)
    h.controls = hist(paths.pca.ind$coord[controls,pc], breaks=breaks)
    ymax = max(c(h.cases$counts,h.controls$counts))
    plot(h.cases, col=rgb(1,0,0,1/2), xlab="PCA Coords", 
        main=paste("PC", pc, "Individual Coordinates", "cases - controls=", cases.controls),
	ylim=c(0,ymax)
	)
    plot(h.controls, col=rgb(0,1,0,1/2), add=TRUE)
}

