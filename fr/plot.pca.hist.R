##
## plot a histogram of the PCA coordinates of the individuals
##
col.cases = rgb(1,0,0,1/2)
col.controls = rgb(0,0,1,1/2)
nCases = length(paths$Label[paths$Label=="case"])
nControls = length(paths$Label[paths$Label=="ctrl"])
                
plot.pca.hist = function(pc) {
    avg.cases = sum(pca.ind$coord[cases,pc])/length(pca.ind$coord[cases,pc])
    avg.controls = sum(pca.ind$coord[controls,pc])/length(pca.ind$coord[controls,pc])
    diffCasesControls = avg.cases - avg.controls
    ## use same breaks for both plots
    xmin = min(pca.ind$coord[,pc]) - 1
    xmax = max(pca.ind$coord[,pc]) + 1
    dx = (xmax - xmin)/20
    breaks = xmin + dx*(0:20)
    ## DEBUG
    print(breaks)
    ##
    h.cases = hist(pca.ind$coord[cases,pc], breaks=breaks, plot=FALSE)
    h.controls = hist(pca.ind$coord[controls,pc], breaks=breaks, plot=FALSE)
    ## plot density to accomodate different numbers of cases and controls
    ymax = max(h.cases$density, h.controls$density)
    plot(h.cases, freq=FALSE, col=col.cases, xlab="PCA Coords", ylim=c(0,ymax), 
         main=paste("PC", pc, ": cases-controls=", signif(diffCasesControls,3), sep="")
         )
    plot(h.controls, freq=FALSE, col=col.controls, ylim=c(0,ymax), add=TRUE)
    legend(x="topleft", c(paste(nCases,"cases"),paste(nControls,"controls"),paste(nCases+nControls,"both")),
           fill=c(col.cases,col.controls,rgb(.5,0,.5)), bty="n")
}

