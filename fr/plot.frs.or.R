## plot a sort of heatmap of FRs versus nodes
## frs and nodes must already be loaded

source("params.R")

plot.frs.or = function(xmin=0, xmax=0, ymin=0, ymax=0, requiredNode=0, xlegend="bottomleft", connect=TRUE) {

    if (xmin==0) {
        xmin = min(as.numeric(rownames(nodes)))
    }
    if (xmax==0) {
        xmax = max(as.numeric(rownames(nodes)))
    }
    xlim = c(xmin,xmax)
    
    if (ymin==0) {
        ymin = min(frs$OR)
    }
    if (ymax==0) {
        ymax = max(frs$OR)
    }
    ylim = c(ymin, ymax)

    xlab = "node"
    if (requiredNode>0) xlab = paste(xlab, " (", requiredNode," required)", sep="")

    ylab = "odds ratio"

    plot(xlim, rep(1.0,2), type="l", lty=2, col="lightgray",
         xlim=xlim, ylim=ylim,
         xlab=xlab, ylab=ylab)

    for (i in 1:nrow(frs)) {
        nodeString = frs$nodes[i]
        frNodes = as.numeric(strsplit(sub("\\[","",sub("\\]","",nodeString)), ",")[[1]])
        if ((frs$OR[i]>=ymin && frs$OR[i]<=ymax) && (requiredNode==0 || requiredNode %in% frNodes)) {
            if (frs$p[i]<1e-2 && frs$OR[i]>1.0) {
                color = "darkred"
            } else if (frs$p[i]<1e-2 && frs$OR[i]<1.0) {
                color = "darkgreen"
            } else {
                color = "darkgray"
            }
            if (length(frNodes)==1) {
                pchar = 19
                if (frNodes==requiredNode) {
                    lines(rep(frNodes,2), ylim, lty=3, col=color, lwd=2)
                    text(frNodes, ylim[1]-(ylim[2]-ylim[1])*0.02, frNodes, col=color, cex=1.0)
                } else if (frs$p[i]<1e-2) {
                    lines(rep(frNodes,2), ylim, lty=3, col=color)
                    text(frNodes, ylim[1]-(ylim[2]-ylim[1])*0.02, frNodes, col=color, cex=0.7)
                }
            } else {
                pchar = 0
            }
            points(frNodes, rep(frs$OR[i],length(frNodes)), col=color, pch=pchar)
            if (connect) {
                lines(frNodes, rep(frs$OR[i],length(frNodes)), col=color, lwd=1)
            }
            text(max(frNodes), frs$OR[i], paste(frs$case[i],"/",frs$ctrl[i]), col="black", pos=4, cex=0.7)
            lastFR = frs[i,]
            lastFRNodes = frNodes
        }
    }
    
    points(lastFRNodes, rep(lastFR$OR,length(lastFRNodes)), col=color, pch=15)
    text(lastFRNodes, rep(lastFR$OR,length(lastFRNodes))+0.1, lastFRNodes, col=color, cex=0.7)

    params(x=xlegend)
}
