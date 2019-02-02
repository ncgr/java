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

## case/control labels and colors
labels = array(dim=length(rownames(pca$rotation)))
colors = array(dim=length(rownames(pca$rotation)))
for (i in 1:length(rownames(pca$rotation))) {
    parts = unlist(strsplit(rownames(pca$rotation)[i], ".", fixed = TRUE))
    labels[i] = substring(parts[1], 2);
    if (parts[2]=="case") {
        colors[i] = "darkred"
    } else if (parts[2]=="ctrl") {
        colors[i] = "darkgreen"
    } else {
        colors[i] = "gray";
    }
}

for (i in (num-1):1) {
    xlabel = paste("PC",i,  " ",round(summary(pca)$importance["Proportion of Variance",i]*100,1),"% of variance", sep="")
    ylabel = paste("PC",i+1," ",round(summary(pca)$importance["Proportion of Variance",i+1]*100,1),"% of variance", sep="")
    plot(pca$rotation[,i], pca$rotation[,i+1], xlab=xlabel, ylab=ylabel, pch=20, cex=1.2, col=colors)
    ## ## colors!
    ## points(pca$rotation[,i], pca$rotation[,i+1], xlab=xlabel, ylab=ylabel, pch=20, col=colors)
    text(pca$rotation[,i], pca$rotation[,i+1], labels, pos=1, col=colors)
}
