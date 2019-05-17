##
## plot the results of an SVM run on an ROC graph
##

library(scales)
library(RColorBrewer)

colors = brewer.pal(4, "Dark2")

plotROC = TRUE

if (requireHomozygous) {
    mergeMode = "HOM mode: CASE call correct IFF both case paths called CASE"
} else {
    mergeMode = "HET mode: CASE call correct IF either case path called CASE"
}

if (plotROC) {
    x = results$FPR
    y = results$TPR
    xlab = "False Positive Rate"
    ylab = "True Positive Rate"
} else {
    x = results$recall
    y = results$precision
    xlab = "Recall TP/(TP+FN)"
    ylab = "Precision TP/(TP+FP)"
}

plot(x[results$alpha==0.2], y[results$alpha==0.2],
     xlim=c(0,1), ylim=c(0,1),
     xlab=xlab, ylab=ylab,
     cex=0.4,
     col=alpha(colors[1], 0.5)
     )
points(x[results$alpha==0.5], y[results$alpha==0.5],
       cex=0.6,
       col=alpha(colors[2], 0.5)
       )
points(x[results$alpha==0.8], y[results$alpha==0.8],
       cex=0.8,
       col=alpha(colors[3], 0.5)
       )
points(x[results$alpha==1.0], y[results$alpha==1.0],
       cex=1.0,
       col=alpha(colors[4], 0.5)
       )

title(main=paste("study:",study," ",
                 "graph:",results$graph[1],
                 "\n",
                 cases," case paths; ",controls," control paths; ",
                 length(results$FPR)," SVM cross-comparisons",
                 "\n",
                 mergeMode,
                 sep=""),
      cex.main=1.0
      )

if (plotROC) {
    lines(c(0,1), c(0,1), lty=2, col="gray")
}

legend(x="bottomright",
       legend=c(expression(paste(alpha,"=0.2",sep="")),
                expression(paste(alpha,"=0.5",sep="")),
                expression(paste(alpha,"=0.8",sep="")),
                expression(paste(alpha,"=1.0",sep=""))),
       pch=1, col=colors, pt.cex=c(0.4, 0.6, 0.8, 1.0)
       )

alphaValueString = 

legend(x=0.35, y=0.2,
       legend=c(
           paste("alpha:\t", capture.output(cat(alphaValues,sep=",")), sep=""),
           paste("kappa:\t", capture.output(cat(kappaValues,sep=",")), sep=""),
           paste("minsup:\t", capture.output(cat(minsupValues,sep=",")), sep=""),
           paste("minsize:\t", capture.output(cat(minsizeValues,sep=",")), sep=""),
           paste("minlen:\t", capture.output(cat(minlenValues,sep=",")), sep="")
       ),
       bty="n",
       pt.cex=0
       )
## "\n",
## "kappa=",capture.output(cat(kappaValues)),
## "\n",


## legend(x, y = NULL, legend, fill = NULL, col = par("col"),
##        border = "black", lty, lwd, pch,
##        angle = 45, density = NULL, bty = "o", bg = par("bg"),
##        box.lwd = par("lwd"), box.lty = par("lty"), box.col = par("fg"),
##        pt.bg = NA, cex = 1, pt.cex = cex, pt.lwd = lwd,
##        xjust = 0, yjust = 1, x.intersp = 1, y.intersp = 1,
##        adj = c(0, 0.5), text.width = NULL, text.col = par("col"),
##        text.font = NULL, merge = do.lines && has.pch, trace = FALSE,
##        plot = TRUE, ncol = 1, horiz = FALSE, title = NULL,
##        inset = 0, xpd, title.col = text.col, title.adj = 0.5,
##        seg.len = 2)
