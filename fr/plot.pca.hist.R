##
## plot a histogram of the contributions of the individuals to a given PCA component,
## controls on left, cases on right

plot.pca.hist = function(pc) {
    sum.cases = sum(paths.pca.ind$contrib[cases,pc])/length(paths.pca.ind$contrib[cases,pc])
    sum.controls = sum(paths.pca.ind$contrib[controls,pc])/length(paths.pca.ind$contrib[controls,pc])
    ratio = (sum.cases - sum.controls)/(sum.cases + sum.controls)
    xmax = max(c(-paths.pca.ind$contrib[cases,pc],paths.pca.ind$contrib[controls,pc]))
    hist(c(-paths.pca.ind$contrib[cases,pc],paths.pca.ind$contrib[controls,pc]), breaks=20,
         xlab="Cases | Controls", main=paste("PC",pc," Individual Contribution ","case excess=",round(ratio*100,1),'%',sep=""),
	 xlim=c(-xmax,xmax)
	 )
}