##
## plot FR case vs control support with priority coloring
## requires labelCounts
##

source("params.R")

plot.casecontrol = function(xmin=0, xmax=0, ymin=0, ymax=0, connect=FALSE) {

    if (xmax==0) xmax = max(frs$ctrl)
    if (ymax==0) ymax = max(frs$case)

    xlim = c(xmin, xmax)
    ylim = c(ymin, ymax)

    plot(frs$ctrl, frs$case,
         xlim=xlim, ylim=ylim,
         xlab="control sample support",  ylab="case sample support",
         col=alpha("black",0.5)  # , cex.axis=cex.axis, cex.lab=cex.lab
         )
    title(main=paste(graphPrefix," alpha=",alpha," kappa=",kappa, sep="")) # , cex.main=cex.main)

    if (connect) {
        lines(frs$ctrl, frs$case)
    }
    
    ## fr:pri
    text(frs$ctrl, frs$case,
         paste(rownames(frs),":",frs$pri,sep=""),
         pos=1, offset=0.4, col=alpha("black",0.5) # cex=cexText, 
         )

    lines(c(0,labelCounts["ctrl",1]*100),c(0,labelCounts["case",1]*100), col="gray")

    params()

    ## highlight low-p values on top
    lowp = frs$p<1e-2
    points(frs$ctrl[lowp], frs$case[lowp], pch=19, col="darkred")
}

