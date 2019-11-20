##
## plot a histogram of the PCA coordinates of the individuals
##

plot.pca.hist = function(pc) {
    col.cases = rgb(1,0,0,1/2)
    col.controls = rgb(0,0,1,1/2)
    sum.cases = sum(pca.ind$coord[cases,pc])/length(pca.ind$coord[cases,pc])
    sum.controls = sum(pca.ind$coord[controls,pc])/length(pca.ind$coord[controls,pc])
    cases.controls = sum.cases - sum.controls
    xmin = min(pca.ind$coord[,pc]) - 1
    xmax = max(pca.ind$coord[,pc]) + 1
    dx = (xmax - xmin)/20
    breaks = xmin + dx*(0:20)
    h.cases = hist(pca.ind$coord[cases,pc], breaks=breaks)
    h.controls = hist(pca.ind$coord[controls,pc], breaks=breaks)
    ymax = max(c(h.cases$counts,h.controls$counts))
    plot(h.cases, col=col.cases, xlab="PCA Coords", 
        main=paste("PC", pc, "Individual Coordinates", "cases - controls=", cases.controls),
	ylim=c(0,ymax)
	)
    plot(h.controls, col=col.controls, add=TRUE)
    legend(x="topleft", c("cases","controls","both"),
    			fill=c(col.cases,col.controls,rgb(.5,0,.5)), bty="n")
}

