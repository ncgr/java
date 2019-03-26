##
## plot PCA loadings given a prcomp output object
##

require(ggplot2)
require(RColorBrewer)

## params location
xfrac = 0.7
yfrac = 0.9
dyfrac = 0.04

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

if (labelsExist) {
    ## case/control labels and colors
    labels = array(dim=length(rownames(pca$rotation)))
    colors = array(dim=length(rownames(pca$rotation)))
    for (i in 1:length(rownames(pca$rotation))) {
        labels[i] = rownames(pca$rotation)[i]
        if (grepl("case", labels[i])) {
            colors[i] = "darkred"
        } else if (grepl("ctrl", labels[i])) {
            colors[i] = "darkgreen"
        } else {
            colors[i] = "gray";
        }
    }
} else {
    colors = brewer.pal(12,"Set3")
}

for (i in (num-1):1) {
    xlabel = paste("PC",i,  " ",round(summary(pca)$importance["Proportion of Variance",i]*100,1),"% of variance", sep="")
    ylabel = paste("PC",i+1," ",round(summary(pca)$importance["Proportion of Variance",i+1]*100,1),"% of variance", sep="")
    plot(pca$rotation[,i], pca$rotation[,i+1], xlab=xlabel, ylab=ylabel, pch=20, cex=1.2, col=colors)
    title(main=paste(outputprefix,": alpha=",alpha," kappa=",kappa," case/ctrl=",casectrl, sep=""), cex.main=0.9)
    ## colors!
    points(pca$rotation[,i], pca$rotation[,i+1], xlab=xlabel, ylab=ylabel, pch=20, col=colors)
    text(pca$rotation[,i], pca$rotation[,i+1], labels, pos=1, col=colors, cex=0.5)
    source("params.R")
}
