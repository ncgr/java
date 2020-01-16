## plot a sort of heatmap of FRs versus nodes
## frs and nodes must already be loaded

xlim=c(1, nrow(nodes))
ylim=c(0, max(-log10(frs$p)))

plot(xlim, rep(2.0,2), type="l", lty=2, col="gray",
     xlim=xlim, ylim=ylim,
     xlab="node", ylab="-log10(p) of FR",
     main=prefix)

for (i in 1:nrow(frs)) {
    nodeString = frs$nodes[i]
    frNodes = as.numeric(strsplit(sub("\\[","",sub("\\]","",nodeString)), ",")[[1]])
    if (frs$p[i]>1e-2) {
        color = "darkgray"
    } else if (frs$OR[i]>1.0) {
        color = "darkred"
    } else if (frs$OR[i]<1.0)  {
        color = "darkgreen"
    } else {
        color = "darkgray"
    }
    points(frNodes, rep(-log10(frs$p[i]),length(frNodes)), col=color, pch=15)
}
