## case+pos vs control+neg
plot(hdstudy$ctrl,
     hdstudy$case,
     xlab="ctrl sample support", ylab="case sample support",
     col="darkgray"
     )
title(main=paste("HDStudy  alpha=",alpha," kappa=",kappa," minsize=",minsize," minlen=",minlen," min(case/ctrl)=",mincasectrlratio, sep=""), cex.main=0.9)
lines(c(0,100), c(0,100), col="lightgray")
for (i in 1:100) {
    lines(c(0,i),  c(i,0), col="lightgray", lty=2)
}
text(hdstudy$ctrl,
     hdstudy$case,
     hdstudy$nodes,
     cex=0.4, pos=1, offset=0.4, col="gray"
     )
text(0, 0, paste(length(hdstudy$nodes),"FRs"), pos=4)


## ## case-control vs pos-neg
## plot(hdstudy$case-hdstudy$ctrl,
##      hdstudy$pos.n-hdstudy$neg.n,
##      xlab="case support - ctrl support", ylab="pos support - neg support",
##      xlim=c(-8,8), ylim=c(-2,2),
##      col="darkgray"
##      )
## title(main=paste("alpha=",alpha," kappa=",kappa," minsup=",minsup," minlen=",minlen," genotype=",genotype," min(case/ctrl)=",mincasectrlratio, sep=""), cex.main=0.9)
## text(hdstudy$case-hdstudy$ctrl,
##      hdstudy$pos.n-hdstudy$neg.n,
##      hdstudy$nodes,
##      cex=0.4, pos=1, offset=0.4, col="gray"
##      )
## text(-8, 2, paste(length(hdstudy$nodes),"FRs"), pos=4)


## ## pos vs case, neg vs control
## split.screen(c(2,1))
## screen(1)
## plot(hdstudy$case, hdstudy$pos.n-hdstudy$neg.n,
##      xlim=c(0,8), ylim=c(-2,2),
##      xlab="case support", ylab="pos-neg support",
##      col="darkgreen"
##      )
## title(main=paste("alpha=",alpha," kappa=",kappa," minsup=",minsup," minlen=",minlen," genotype=",genotype," min(case/ctrl)=",mincasectrlratio, sep=""), cex.main=0.9)
## screen(2)
## plot(hdstudy$ctrl, hdstudy$neg.n-hdstudy$pos.n,
##      xlim=c(0,8), ylim=c(-2,2),
##      xlab="ctrl support", ylab="neg-pos support",
##      col="darkred"
##      )
## title(main=paste(length(hdstudy$nodes),"FRs"))
## close.screen(all = TRUE)    # exit split-screen mode

