##
## plot FR case vs control support
## requires labelCounts
##

library(scales)

## params location
xfrac = 0
yfrac = 0.95
dyfrac = 0.04

if (labelsExist) {

    xmin = 0
    xmax = max(frs$ctrl)
    ymin = 0
    ymax = max(frs$case)

    plot(frs$ctrl, frs$case,
         xlim=c(0,xmax), ylim=c(0,ymax),
         xlab="ctrl sample support",  ylab="case sample support",
         col=alpha("black",0.5)
         )
    title(main=paste(outputprefix,": alpha=",alpha," kappa=",kappa," case/ctrl=",casectrl, sep=""), cex.main=0.9)
    
    text(frs$ctrl, frs$case, paste(frs$size,frs$avgLen),
         cex=0.4, pos=1, offset=0.4, col=alpha("black",0.5)
         )
    

    lines(c(0,labelcounts["ctrl",1]*10),c(0,labelcounts["case",1]*10), col="gray")

    source("params.R")
    
} else {

    print("This experiment has no labels.")

}

