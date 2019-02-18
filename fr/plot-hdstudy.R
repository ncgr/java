## case+pos vs control+neg
plot(frs$ctrl, frs$case,
     xlim=c(0,max(frs$ctrl)), ylim=c(0,max(frs$case)),
     xlab="ctrl sample support",  ylab="case sample support",
     col="darkgray"
     )
title(main=paste("alpha=",alpha," kappa=",kappa," case/ctrl=",casectrl, sep=""), cex.main=0.9)

text(frs$ctrl,
     frs$case,
     paste(frs$size,frs$avgLen),
     cex=0.4, pos=1, offset=0.4, col="gray"
     )

xmin = par()$usr[1]
xmax = par()$usr[2]
ymin = par()$usr[3]
ymax = par()$usr[4]
xtext = xmin+(xmax-xmin)*0.05
dytext = (ymax-ymin)*0.05
text(xtext, ymax-dytext, gfafile, pos=4)
text(xtext, ymax-dytext*2, pathlabels, pos=4)
text(xtext, ymax-dytext*3, paste(labelcounts["case",1]," case paths"), pos=4)
text(xtext, ymax-dytext*4, paste(labelcounts["ctrl",1]," ctrl paths"), pos=4)
text(xtext, ymax-dytext*5, paste(length(frs$nodes),"FRs"), pos=4)

lines(c(0,labelcounts["ctrl",1]),c(0,labelcounts["case",1]), col="lightgray")

for (i in seq(5,100,by=5)) {
    lines(c(0,i),  c(i,0), col="lightgray", lty=2)
}

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

