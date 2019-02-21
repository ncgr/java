##
## plot FR case vs control support
##

plot(frs$ctrl, frs$case,
     xlim=c(0,max(frs$ctrl)), ylim=c(0,max(frs$case)),
     xlab="ctrl sample support",  ylab="case sample support",
     col="darkgray"
     )
title(main=paste(outputprefix,": alpha=",alpha," kappa=",kappa," case/ctrl=",casectrl, sep=""), cex.main=0.9)

source("params.R")

text(frs$ctrl,
     frs$case,
     paste(frs$size,frs$avgLen),
     cex=0.4, pos=1, offset=0.4, col="gray"
     )

lines(c(0,labelcounts["ctrl",1]),c(0,labelcounts["case",1]), col="lightgray")

for (i in seq(5,100,by=5)) {
    lines(c(0,i),  c(i,0), col="lightgray", lty=2)
}

