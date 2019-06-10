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

text(xtext, ytext-dytext*0, paste("graph:\t\t",gfafile), pos=4, cex=cexText)
if (labelsExist) {
    text(xtext, ytext-dytext*1, paste("labels:\t\t",pathlabels), pos=4, cex=cexText)
    text(xtext, ytext-dytext*2, paste("case paths:\t",labelcounts["case",1]), pos=4, cex=cexText)
    text(xtext, ytext-dytext*3, paste("control paths:\t",labelcounts["ctrl",1]), pos=4, cex=cexText)
}
text(xtext, ytext-dytext*4, paste("FRs:\t\t",length(frs$nodes)), pos=4, cex=cexText)
text(xtext, ytext-dytext*5, paste("clock time:\t",clocktime), pos=4, cex=cexText)

