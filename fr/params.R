##
## display the parameters on the current plot
## xfrac, yfrac and dyfrac must be set, on [0,1].
## cexText must also be set.
##

xmin = par()$usr[1]
xmax = par()$usr[2]
ymin = par()$usr[3]
ymax = par()$usr[4]

xtext = xmin + (xmax-xmin)*xfrac
ytext = ymin + (ymax-ymin)*yfrac
dytext = (ymax-ymin)*dyfrac

text(xtext, ytext-dytext*0, gfafile, pos=4, cex=cexText)
if (labelsExist) {
    text(xtext, ytext-dytext*1, pathlabels, pos=4, cex=cexText)
    text(xtext, ytext-dytext*2, paste("case paths:\t",labelcounts["case",1]), pos=4, cex=cexText)
    text(xtext, ytext-dytext*3, paste("control paths:\t",labelcounts["ctrl",1]), pos=4, cex=cexText)
}
if (prunedGraph) {
    text(xtext, ytext-dytext*4, paste("graph has been pruned"), pos=4, cex=cexText)
}
text(xtext, ytext-dytext*6, paste("FRs:\t\t",length(frs$nodes)), pos=4, cex=cexText)
text(xtext, ytext-dytext*7, paste("clock time:\t",clocktime), pos=4, cex=cexText)

