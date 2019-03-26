##
## plot node size vs. avg length (no labels)
##

## params location
xfrac = 0.6
yfrac = 0.2
dyfrac = 0.04

plot(frs$avgLen, frs$size,
     xlim=c(0,max(frs$avgLen)), ylim=c(0,max(frs$size)),
     xlab="FR subpath average length (bp)", ylab="FR node cluster size",
     col="darkgray"
     )
title(main=paste(outputprefix,": alpha=",alpha," kappa=",kappa," case/ctrl=",casectrl, sep=""), cex.main=0.9)

source("params.R")

