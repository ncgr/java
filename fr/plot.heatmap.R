## plot a sort of heatmap of FRs versus nodes
## frs and nodes must already be loaded

plot.heatmap = function(ymin=0, ymax=0) {

    xlim=c(1, nrow(nodes))
    if (ymax==0) {
        ymax = max(-log10(frs$p))
    }
    ylim = c(ymin, ymax)
    
    plot(xlim, rep(2.0,2), type="l", lty=2, col="lightgray",
         xlim=xlim, ylim=ylim,
         xlab="node", ylab="-log10(p)",
         main=paste(prefix,"  ","alpha=",alpha," ","kappa=",kappa, " ","minPriority=",minPriority,sep=""))

    for (i in 1:nrow(frs)) {
        nodeString = frs$nodes[i]
        frNodes = as.numeric(strsplit(sub("\\[","",sub("\\]","",nodeString)), ",")[[1]])
        if (frs$p[i]<1e-2 && frs$OR[i]>1.0) {
            color = "darkred"
        } else if (frs$p[i]<1e-2 && frs$OR[i]<1.0) {
            color = "darkgreen"
        } else {
            color = "darkgray"
        }
        if (length(frNodes)==1) {
            pchar = 19
            if (frs$p[i]<1e-2) {
                lines(rep(frNodes,2), ylim, lty=3, col=color)
                text(frNodes, ylim[1]-(ylim[2]-ylim[1])*0.02, frNodes, col=color, cex=0.7)
            }
        } else {
            pchar = 0
        }
        points(frNodes, rep(-log10(frs$p[i]),length(frNodes)), col=color, pch=pchar)
    }
    
    points(frNodes, rep(-log10(frs$p[i]),length(frNodes)), col=color, pch=15)
    text(frNodes, ylim[2]+(ylim[2]-ylim[1])*0.02, frNodes, col=color, cex=0.7)
}
