## plot a sort of heatmap of FRs versus nodes
## frs and nodes must already be loaded

source("plot.params.R")

plot.frs.pri = function(xmin=0, xmax=0, ymin=0, ymax=0, xlegend="bottomleft", connect=TRUE) {

    sigPri = 200

    if (xmin==0) {
        xmin = min(as.numeric(rownames(nodes)))
    }
    if (xmax==0) {
        xmax = max(as.numeric(rownames(nodes)))
    }
    xlim = c(xmin,xmax)
    
    if (ymin==0) {
        ymin = min(frs$pri)
    }
    if (ymax==0) {
        ymax = max(frs$pri)
    }
    ylim = c(ymin, ymax)

    xlab = "node"

    ylab = "priority"
    if (priorityOption==4) {
        ylab = paste(ylab, "=", "-log10(p)×100")
    }

    plot(xlim, rep(sigPri,2), type="l", lty=2, col="lightgray",
         xlim=xlim, ylim=ylim,
         xlab=xlab, ylab=ylab)

    oldNodes = c()

    reqNodes = as.numeric(strsplit(sub("\\[","",sub("\\]","",requiredNodes)), ",")[[1]])

    for (i in 1:nrow(frs)) {
        nodeString = frs$nodes[i]
        frNodes = as.numeric(strsplit(sub("\\[","",sub("\\]","",nodeString)), ",")[[1]])
        if ((frs$pri[i]>=ymin && frs$pri[i]<=ymax)) {
            if (frs$pri[i]>sigPri && frs$OR[i]>1.0) {
                color = "darkred"
            } else if (frs$pri[i]>sigPri && frs$OR[i]<1.0) {
                color = "darkgreen"
            } else {
                color = "darkgray"
            }
            points(frNodes, rep(frs$pri[i],length(frNodes)), col=color, pch=0)
            if (connect) {
                lines(frNodes, rep(frs$pri[i],length(frNodes)), col=color, lwd=1)
            }
            text(max(frNodes), frs$pri[i], paste(frs$case[i],"/",frs$ctrl[i]), col="black", pos=4, cex=0.7)
            for (node in frNodes) {
                if (!node%in%oldNodes) {
                    text(node, frs$pri[i], node, pos=1, col=color, cex=0.7)
                    oldNodes = c(oldNodes, node)
                    if (node%in%reqNodes) {
                        lines(rep(frNodes,2), ylim, lty=3, col=color, lwd=2)
                    }
                }
            }
            lastFR = frs[i,]
            lastFRNodes = frNodes
        }
    }
    
    points(lastFRNodes, rep(lastFR$pri,length(lastFRNodes)), col=color, pch=15)
    text(lastFRNodes, ylim[2]+(ylim[2]-ylim[1])*0.02, lastFRNodes, col=color, cex=0.7)

    plot.params(x=xlegend)
}
