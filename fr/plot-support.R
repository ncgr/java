##
## plot support vs. node size (no labels)
##

## params location
xfrac = 0.0
yfrac = 0.2
dyfrac = 0.04

plot(frs$size, frs$support,
     xlim=c(0,max(frs$size)), ylim=c(0,max(frs$support)),
     xlab="FR node cluster size",  ylab="FR support",
     col=alpha("darkgray",0.5)
     )
title(main=paste(outputprefix,": alpha=",alpha," kappa=",kappa," priority=",priority, sep=""), cex.main=0.9)

source("params.R")

