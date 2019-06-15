##
## plot the F1 score in some informative way
##


## PNG OUTPUT
cex.axis = 1.5
cex.lab = 1.5
cex.main = 1.5
cexText = 1.4

plot(hist(results$F1, breaks=(0:20)/20), main="", xlim=c(0,1),
     xlab="F1 Score",
     cex.axis=cex.axis, cex.lab=cex.lab)

title(main=paste("Study:",study," ",mergeMode,sep=""), cex.main=cex.main)

xmin = par()$usr[1]
xmax = par()$usr[2]
ymin = par()$usr[3]
ymax = par()$usr[4]

xtext = 0.0
ytext = ymax*0.9
dytext = ymax*0.03

text(xtext, ytext-dytext*0, paste("graph:\t\t",graph), pos=4, cex=cexText)
text(xtext, ytext-dytext*1, paste("case paths:\t",cases), pos=4, cex=cexText)
text(xtext, ytext-dytext*2, paste("control paths:\t",controls), pos=4, cex=cexText)
text(xtext, ytext-dytext*3, paste("SVM x-comps:\t", length(results$FPR)), pos=4, cex=cexText)

text(xtext, ytext-dytext*5, paste("alpha:\t", capture.output(cat(alphaValues,sep=","))), pos=4, cex=cexText)
text(xtext, ytext-dytext*6, paste("kappa:\t", capture.output(cat(kappaValues,sep=","))), pos=4, cex=cexText)
text(xtext, ytext-dytext*7, paste("minsup:\t", capture.output(cat(minsupValues,sep=","))), pos=4, cex=cexText)
text(xtext, ytext-dytext*8, paste("minsize:\t", capture.output(cat(minsizeValues,sep=","))), pos=4, cex=cexText)
text(xtext, ytext-dytext*9, paste("minlen:\t", capture.output(cat(minlenValues,sep=","))), pos=4, cex=cexText)

text(xtext, ytext-dytext*11, paste("max(F1):\t", round(max(results$F1),2)), pos=4, cex=cexText)
