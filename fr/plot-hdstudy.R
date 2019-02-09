## make informative plots of FRFinder hdstudy results

plot(hdstudy$ctrl.n,
     hdstudy$case.n,
     xlab="HDStudy ctrl sample support", ylab="HDStudy case sample support",
     xlim=c(0,10), ylim=c(0,10),
     col="darkgray"
     )

title(main=paste("alpha=",alpha," kappa=",kappa," minsup=",minsup," minlen=",minlen," genotype=",genotype," min(case/ctrl)=",mincasectrlratio, sep=""), cex.main=0.9)

lines(c(0,10), c(0,10), col="lightgray")
for (i in 1:20) {
    lines(c(0,i),  c(i,0), col="lightgray", lty=2)
}

text(hdstudy$ctrl.n,
     hdstudy$case.n,
     hdstudy$nodes,
     cex=0.4, pos=1, offset=0.4, col="gray"
     )

text(0, 0, paste(length(hdstudy$nodes),"FRs"), pos=4)

## lines(c(0,10), c(15,5),  col="gray", lty=2)
## lines(c(0,10), c(20,10), col="gray", lty=2)
## lines(c(0,10), c(25,15), col="gray", lty=2)
## lines(c(0,10), c(30,20), col="gray", lty=2)
## lines(c(0,10), c(37,27), col="gray", lty=2)

## legend(c("roots", "children"),
##        pch=c(19,1),
##        col=c("darkred","darkgreen"),
##        x="topleft")
       

