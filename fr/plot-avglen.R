##
## plot avg length vs. support (no labels)
##

## params location
xfrac = 0.55
yfrac = 0.2
dyfrac = 0.04

plot(frs$support, frs$avgLen,
     xlim=c(0,max(frs$support)), ylim=c(0,max(frs$avgLen)),
     xlab="FR subpath support",  ylab="FR subpath average length (bp)",
     col="darkgray"
     )
title(main=paste(outputprefix,": alpha=",alpha," kappa=",kappa," case/ctrl=",casectrl, sep=""), cex.main=0.9)

source("params.R")

