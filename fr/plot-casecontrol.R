##
## plot FR case vs control support
## requires labelCounts
##

library(scales)

## ## PNG OUTPUT
## xfrac = 0.0
## yfrac = 0.9
## dyfrac = 0.03
## cex.axis = 1.8
## cex.lab = 1.5
## cex.main = 1.8
## cexDots = 1.0
## cexText = 1.5

## ON-SCREEN OUTPUT
xfrac = 0.65
yfrac = 0.65
dyfrac = 0.04
cex.axis = 1.0
cex.lab = 1.0
cex.main = 1.0
cexDots = 0.5
cexText = 0.9

if (labelsExist) {

    xmin = 0
    xmax = max(frs$ctrl)
    ymin = 0
    ymax = max(frs$case)

    plot(frs$ctrl, frs$case,
         xlim=c(0,xmax), ylim=c(0,ymax),
         xlab="ctrl sample support",  ylab="case sample support",
         col=alpha("black",0.5), cex.axis=cex.axis, cex.lab=cex.lab
         )
    title(main=paste(outputPrefix," alpha=",alpha," kappa=",kappa, sep=""), cex.main=cex.main)
    
    ## text(frs$ctrl, frs$case,
    ##      paste(rownames(frs),":",frs$size,",",frs$avgLen,sep=""),
    ##      cex=cexText, pos=1, offset=0.4, col=alpha("black",0.5)
    ##      )

    text(frs$ctrl, frs$case, rownames(frs),
         cex=cexText, pos=1, offset=0.4, col=alpha("black",0.5)
         )

    lines(c(0,labelCounts["ctrl",1]*100),c(0,labelCounts["case",1]*100), col="gray")

    source("params.R")
    
} else {

    print("This experiment has no labels.")

}

