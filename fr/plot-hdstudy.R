## case+pos vs control+neg
plot(frs$ctrl, frs$case,
     xlim=c(0,max(frs$ctrl)), ylim=c(0,max(frs$case)),
     xlab="ctrl sample support",  ylab="case sample support",
     col="darkgray"
     )
title(main=paste("HDStudy  alpha=",alpha," kappa=",kappa," case/ctrl=",casectrl, sep=""), cex.main=0.9)
lines(c(0,47), c(0,27), col="lightgray")
for (i in seq(5,100,by=5)) {
    lines(c(0,i),  c(i,0), col="lightgray", lty=2)
}
text(0, 0, paste(length(frs$nodes),"FRs"), pos=4)
## text(frs$ctrl,
##      frs$case,
##      frs$nodes,
##      cex=0.4, pos=1, offset=0.4, col="gray"
##      )

## ## case-control vs pos-neg
## plot(frs$case-frs$ctrl,
##      frs$pos.n-frs$neg.n,
##      xlab="case support - ctrl support", ylab="pos support - neg support",
##      xlim=c(-8,8), ylim=c(-2,2),
##      col="darkgray"
##      )
## title(main=paste("alpha=",alpha," kappa=",kappa," minsup=",minsup," minlen=",minlen," genotype=",genotype," min(case/ctrl)=",mincasectrlratio, sep=""), cex.main=0.9)
## text(frs$case-frs$ctrl,
##      frs$pos.n-frs$neg.n,
##      frs$nodes,
##      cex=0.4, pos=1, offset=0.4, col="gray"
##      )
## text(-8, 2, paste(length(frs$nodes),"FRs"), pos=4)


## ## pos vs case, neg vs control
## split.screen(c(2,1))
## screen(1)
## plot(frs$case, frs$pos.n-frs$neg.n,
##      xlim=c(0,8), ylim=c(-2,2),
##      xlab="case support", ylab="pos-neg support",
##      col="darkgreen"
##      )
## title(main=paste("alpha=",alpha," kappa=",kappa," minsup=",minsup," minlen=",minlen," genotype=",genotype," min(case/ctrl)=",mincasectrlratio, sep=""), cex.main=0.9)
## screen(2)
## plot(frs$ctrl, frs$neg.n-frs$pos.n,
##      xlim=c(0,8), ylim=c(-2,2),
##      xlab="ctrl support", ylab="neg-pos support",
##      col="darkred"
##      )
## title(main=paste(length(frs$nodes),"FRs"))
## close.screen(all = TRUE)    # exit split-screen mode

