##
## display the parameters on the current plot
## xfrac, yfrac and dyfrac must be set, on [0,1]
##

xmin = par()$usr[1]
xmax = par()$usr[2]
ymin = par()$usr[3]
ymax = par()$usr[4]

xtext = xmin + (xmax-xmin)*xfrac
ytext = ymin + (ymax-ymin)*yfrac
dytext = (ymax-ymin)*dyfrac

text(xtext, ytext-dytext*0, gfafile, pos=4)
if (labelsExist) {
    text(xtext, ytext-dytext*1, pathlabels, pos=4)
    text(xtext, ytext-dytext*2, paste("case paths:",labelcounts["case",1]), pos=4)
    text(xtext, ytext-dytext*3, paste("ctrl paths:",labelcounts["ctrl",1]), pos=4)
}
text(xtext, ytext-dytext*4, paste("FRs:",length(frs$nodes)), pos=4)
text(xtext, ytext-dytext*5, paste("clock time:",clocktime), pos=4)

