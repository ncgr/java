##
## plot the results of an SVM run on an ROC graph
##

library(scales)
library(RColorBrewer)

## PNG OUTPUT
xtext = 0.5
ytext = 0.3
dytext = 0.03
xleg = 0.4
yleg = 0.15
cex.axis = 1.8
cex.lab = 1.5
cex.main = 1.3
cexStep = 0.6
cexText = 1.4

## ## ON-SCREEN OUTPUT
## xtext = 0.5
## ytext = 0.5
## dytext = 0.1
## xleg = 0.35
## yleg = 0.2
## cex.main = 1.0
## cex.axis = 1.0
## cex.lab = 1.0
## cexStep = 0.5
## cexText = 0.9

colors = brewer.pal(4, "Dark2")

plotROC = TRUE

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
     cex=cexStep*1,
     col=alpha(colors[1], 0.5),
     cex.axis=cex.axis, cex.lab=cex.lab
     )
points(x[results$alpha==0.5], y[results$alpha==0.5],
       cex=cexStep*2,
       col=alpha(colors[2], 0.5)
       )
points(x[results$alpha==0.8], y[results$alpha==0.8],
       cex=cexStep*3,
       col=alpha(colors[3], 0.5)
       )
points(x[results$alpha==1.0], y[results$alpha==1.0],
       cex=cexStep*4,
       col=alpha(colors[4], 0.5)
       )

title(main=paste("Study:",study," ",mergeMode,sep=""), cex.main=cex.main)

if (plotROC) {
    lines(c(0,1), c(0,1), lty=2, col="gray")
}

legend(x="bottomleft",
       legend=c(expression(paste(alpha,"=0.2",sep="")),
                expression(paste(alpha,"=0.5",sep="")),
                expression(paste(alpha,"=0.8",sep="")),
                expression(paste(alpha,"=1.0",sep=""))),
       pch=1, col=colors, pt.cex=cexStep*c(1,2,3,4), cex=cexText
       )

if (prunedGraph) {
    text(xtext, ytext-dytext*0, paste("graph:  ",graph," PRUNED"), pos=4, cex=cexText)
} else {
    text(xtext, ytext-dytext*0, paste("graph:  ",graph), pos=4, cex=cexText)
}
text(xtext, ytext-dytext*1, paste("case paths: ",cases), pos=4, cex=cexText)
text(xtext, ytext-dytext*2, paste("control paths: ",controls), pos=4, cex=cexText)
text(xtext, ytext-dytext*4, paste("SVM x-comps: ", length(results$FPR)), pos=4, cex=cexText)
text(xtext, ytext-dytext*5, paste("alpha: ", capture.output(cat(alphaValues,sep=","))), pos=4, cex=cexText)
text(xtext, ytext-dytext*6, paste("kappa: ", capture.output(cat(kappaValues,sep=","))), pos=4, cex=cexText)
text(xtext, ytext-dytext*7, paste("minsup: ", capture.output(cat(minsupValues,sep=","))), pos=4, cex=cexText)
text(xtext, ytext-dytext*8, paste("minsize: ", capture.output(cat(minsizeValues,sep=","))), pos=4, cex=cexText)
text(xtext, ytext-dytext*9, paste("minlen: ", capture.output(cat(minlenValues,sep=","))), pos=4, cex=cexText)
