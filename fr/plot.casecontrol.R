##
## plot FR case vs control support with priority coloring
## requires labelCounts
##

plot.casecontrol = function() {

    xlim = c(0, max(frs$ctrl))
    ylim = c(0, max(frs$case))

    plot(frs$ctrl, frs$case,
         xlim=xlim, ylim=ylim,
         xlab="control sample support",  ylab="case sample support",
         col=alpha("black",0.5), cex.axis=cex.axis, cex.lab=cex.lab
         )
    title(main=paste(graphPrefix," alpha=",alpha," kappa=",kappa, sep=""), cex.main=cex.main)
    
    ## fr:pri
    text(frs$ctrl, frs$case,
         paste(rownames(frs),":",frs$pri,sep=""),
         cex=cexText, pos=1, offset=0.4, col=alpha("black",0.5)
         )

    lines(c(0,labelCounts["ctrl",1]*100),c(0,labelCounts["case",1]*100), col="gray")

    source("params.R")

    ## highlight low-p values on top
    lowp = frs$p<1e-2
    points(frs$ctrl[lowp], frs$case[lowp], pch=19, col="darkred")
}

