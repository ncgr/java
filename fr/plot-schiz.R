## make informative plots of FRFinder schiz results

plot(schiz$ctrl.n,
     schiz$case.n,
     xlab="ctrl sample support",
     ylab="case sample support",
     col="darkgreen"
     )

title(main=paste("Schiz:"," alpha=",alpha," kappa=",kappa," minsup=",minsup," minlen=",minlen," genotype=",genotype, sep=""), cex.main=0.9)

text(schiz$ctrl.n,
     schiz$case.n,
     schiz$nodes,
     cex=0.4, pos=1, offset=0.4
     )

lines(c(0,100), c(0,100), col="lightgray")
for (i in 1:100) {
    lines(c(0,i),  c(i,0), col="lightgray", lty=2)
}

