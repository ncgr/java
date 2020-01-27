## plot a sort of heatmap of FRs versus nodes
## frs and nodes must already be loaded

source("params.R")

plot.heatmap = function(ymin=0, ymax=0, requiredNode=0) {

    sigPri = 200

    xlim=c(1, nrow(nodes))
    if (ymax==0) {
        ymax = max(frs$pri)
    }
    if (ymin==0) {
        ymin = minPriority
    }
    ylim = c(ymin, ymax)

    xlab = "node"
    if (requiredNode>0) xlab = paste(xlab, " (", requiredNode," required)", sep="")
    
    plot(xlim, rep(sigPri,2), type="l", lty=2, col="lightgray",
         xlim=xlim, ylim=ylim,
         xlab=xlab, ylab="priority",
         main=paste(prefix,"  ","alpha=",alpha," ","kappa=",kappa, " ","minPriority=",minPriority,sep=""))

    for (i in 1:nrow(frs)) {
        nodeString = frs$nodes[i]
        frNodes = as.numeric(strsplit(sub("\\[","",sub("\\]","",nodeString)), ",")[[1]])
        if ((frs$pri[i]>=ymin && frs$pri[i]<=ymax) && (requiredNode==0 || requiredNode %in% frNodes)) {
            if (frs$pri[i]>sigPri && frs$OR[i]>1.0) {
                color = "darkred"
            } else if (frs$pri[i]>sigPri && frs$OR[i]<1.0) {
                color = "darkgreen"
            } else {
                color = "darkgray"
            }
            if (length(frNodes)==1) {
                pchar = 19
                if (frs$pri[i]>sigPri) {
                    lines(rep(frNodes,2), ylim, lty=3, col=color)
                    text(frNodes, ylim[1]-(ylim[2]-ylim[1])*0.02, frNodes, col=color, cex=0.7)
                }
            } else {
                pchar = 0
            }
            points(frNodes, rep(frs$pri[i],length(frNodes)), col=color, pch=pchar)
            lastFR = frs[i,]
            lastFRNodes = frNodes
        }
    }
    
    points(lastFRNodes, rep(lastFR$pri,length(lastFRNodes)), col=color, pch=15)
    text(lastFRNodes, ylim[2]+(ylim[2]-ylim[1])*0.02, lastFRNodes, col=color, cex=0.7)

    params(x="bottomleft")
}
