##
## plot the Mathews correlation coefficition (MCC) in some informative way
##

plot(hist(results$MCC, breaks=20),
     xlim=c(-1,1), main="", xlab="Mathews Correlation Coefficient (MCC)")

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

legend(x="topleft",
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
