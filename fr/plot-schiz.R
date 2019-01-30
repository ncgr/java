## make informative plots of FRFinder schiz results

plot(schiz.all$ctrl.n,
     schiz.all$case.n,
     xlab="ctrl sample support",
     ylab="case sample support",
     xlim=c(0,10),
     ylim=c(0,27),
     col="darkgreen"
     )

title(main=paste("Schiz:"," alpha=",alpha," kappa=",kappa," minsup=",minsup," minlen=",minlen," genotype=",genotype, sep=""), cex.main=0.9)

points(schiz.rc$ctrl.n,
       schiz.rc$case.n,
       col="darkred",
       )

text(schiz.all$ctrl.n,
     schiz.all$case.n,
     schiz.all$nodes,
     cex=0.4, pos=1, offset=0.4
     )

lines(c(0,10), c(0,27), col="gray")

lines(c(0,5),  c(5,0),   col="gray", lty=2)
lines(c(0,10), c(10,0),  col="gray", lty=2)
lines(c(0,10), c(15,5),  col="gray", lty=2)
lines(c(0,10), c(20,10), col="gray", lty=2)
lines(c(0,10), c(25,15), col="gray", lty=2)
lines(c(0,10), c(30,20), col="gray", lty=2)
lines(c(0,10), c(37,27), col="gray", lty=2)

legend(c("roots", "children"),
       pch=1,
       col=c("darkred","darkgreen"),
       x="topleft")
       

