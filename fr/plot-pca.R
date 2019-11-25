##
## plot PCA loadings given a prcomp output object
##

require(stringr)
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
        casestart = str_locate_all(pattern='case', rownames(pca$rotation))[[i]][1]
        ctrlstart = str_locate_all(pattern='ctrl', rownames(pca$rotation))[[i]][1] 
        if (!is.na(casestart)) {
            colors[i] = "darkred"
            labels[i] = substring(rownames(pca$rotation)[i], 1, casestart-2)
        } else if (!is.na(ctrlstart)) {
            colors[i] = "darkgreen"
            labels[i] = substring(rownames(pca$rotation)[i], 1, ctrlstart-2)
        }
    }
} else {
    colors = brewer.pal(12,"Set3")
}

for (i in (num-1):1) {
    xlabel = paste("PC",i,  " ",round(summary(pca)$importance["Proportion of Variance",i]*100,1),"% of variance", sep="")
    ylabel = paste("PC",i+1," ",round(summary(pca)$importance["Proportion of Variance",i+1]*100,1),"% of variance", sep="")
    plot(pca$rotation[,i], pca$rotation[,i+1], xlab=xlabel, ylab=ylabel, pch=20, cex=1.2, col=colors)
    title(main=paste(graphPrefix,": alpha=",alpha," kappa=",kappa," case/ctrl=",caseCtrl, sep=""), cex.main=0.9)
    ## colors!
    points(pca$rotation[,i], pca$rotation[,i+1], xlab=xlabel, ylab=ylabel, pch=20, col=colors)
    text(pca$rotation[,i], pca$rotation[,i+1], labels, pos=1, col=colors, cex=0.5)
    source("params.R")
}
