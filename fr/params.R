##
## display the parameters on the current plot
## xfrac, yfrac and dyfrac must be set, on [0,1].
## cexText must also be set.
##

## xmin = par()$usr[1]
## xmax = par()$usr[2]
## ymin = par()$usr[3]
## ymax = par()$usr[4]

## xtext = xmin + (xmax-xmin)*xfrac
## ytext = ymin + (ymax-ymin)*yfrac
## dytext = (ymax-ymin)*dyfrac

## text(xtext, ytext-dytext*0, date, pos=4, cex=cexText)
## text(xtext, ytext-dytext*1, gfaFile, pos=4, cex=cexText)
## text(xtext, ytext-dytext*2, paste("clock time:\t",clocktime), pos=4, cex=cexText)
## text(xtext, ytext-dytext*3, paste("FRs:\t\t",length(frs$nodes)), pos=4, cex=cexText)
## if (labelsExist) {
##     text(xtext, ytext-dytext*4, paste("case paths:\t",labelCounts["case",1]), pos=4, cex=cexText)
##     text(xtext, ytext-dytext*5, paste("control paths:\t",labelCounts["ctrl",1]), pos=4, cex=cexText)
## }
## if (prunedGraph) {
##     text(xtext, ytext-dytext*6, paste("graph has been pruned"), pos=4, cex=cexText)
## }


if (isSaveSet) {
    legend(x="topleft", bty="n",
           c(paste("FRs:",length(frs$nodes)),
             paste("case paths:",labelCounts["case",1]),
             paste("control paths:",labelCounts["ctrl",1])
             )
           )
} else {
    legend(x="topleft", bty="n",
           c(date,
             gfaFile,
             paste("clock time:",clocktime),
             paste("FRs:",length(frs$nodes)),
             paste("case paths:",labelCounts["case",1]),
             paste("control paths:",labelCounts["ctrl",1])
             )
           )
}

