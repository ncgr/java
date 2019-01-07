##
## plot PCA loadings given a prcomp output object
##

require(ggplot2)
require(RColorBrewer)

## FANCY
## theta = seq(0,2*pi,length.out = 100)
## circle = data.frame(x = cos(theta), y = sin(theta))
## p = ggplot(circle,aes(x,y)) + geom_path()
## loadings = data.frame(pca$rotation, .names=row.names(pca$rotation))
## p + geom_text(data=loadings, mapping=aes(x=PC1, y=PC2, label=.names, colour=.names)) +
##     coord_fixed(ratio=1) +
##     labs(x="PC1", y="PC2")

## NOT FANCY
num = length(colnames(pca$rotation))
## colors = brewer.pal(12,"Set3")
colors = c("darkred", "darkgreen", "blue", "purple")

for (i in (num-1):1) {
    xlabel = paste("PC",i,  " ",round(summary(pca)$importance["Proportion of Variance",i]*100,1),"% of variance", sep="")
    ylabel = paste("PC",i+1," ",round(summary(pca)$importance["Proportion of Variance",i+1]*100,1),"% of variance", sep="")
    plot(pca$rotation[,i], pca$rotation[,i+1], xlab=xlabel, ylab=ylabel, pch=20, cex=1.2)
    ## colors!
    points(pca$rotation[,i], pca$rotation[,i+1], xlab=xlabel, ylab=ylabel, pch=20, col=colors[1])
    text(pca$rotation[,i], pca$rotation[,i+1], rownames(pca$rotation), pos=4, col=colors[1])
}
