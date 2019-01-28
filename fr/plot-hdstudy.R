## make informative plots of FRFinder hdstudy results

plot(hdstudy.all$ctrl.n,
     hdstudy.all$case.n,
     xlab="ctrl sample support",
     ylab="case sample support",
     xlim=c(0,10),
     ylim=c(0,27),
     col="darkgreen"
     )

title(main=paste("HDStudy:"," alpha=",alpha," kappa=",kappa," minsup=",minsup," minlen=",minlen," genotype=",genotype, sep=""), cex.main=0.9)


points(hdstudy.rc$ctrl.n,
       hdstudy.rc$case.n,
       col="darkred",
       )

text(hdstudy.all$ctrl.n,
     hdstudy.all$case.n,
     hdstudy.all$nodes,
     cex=0.4, pos=1, offset=0.4
     )

lines(c(0,10), c(0,27), col="gray")

legend(c("roots", "children"),
       pch=1,
       col=c("darkred","darkgreen"),
       x="topleft")
       

