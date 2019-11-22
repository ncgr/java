##
## plot log Fisher p value of each seg call on a single chromosome in the given start,finish range
##

plot.seg.region = function(chr="1", start=1, finish=10000000, gene="") {
    
    pMax = 1	
    pRed = 1e-4
    
    pts = (seg$chr==chr & seg$p<pMax & seg$start>=start & seg$start<=finish)
    highpts = (pts & seg$p<pRed)

    xmin = start
    xmax = finish

    ymin = 0
    ymax = max(seg$mlog10p[pts])

    xlim = c(xmin,xmax)
    ylim = c(ymin,ymax)

    yline1 = rep(2, 2)
    yline2 = rep(-log10(pRed), 2)

    
    plot(seg$start[pts], seg$mlog10p[pts],
         xlim=xlim, ylim=ylim,
         xlab="POS", ylab="-log10(p)",
         main=paste(gene," ",(finish-start+1),"bp ",chr,":",start,"-",finish," (GRCh37)", sep=""),
         pch=1, cex=0.5, col="black")
    lines(xlim,  yline1, col="gray", lty=2)
    lines(rep(xmin,2), ylim, col="red", lty=2)
    lines(rep(xmax,2), ylim, col="red", lty=2)
    
    ## highlight highly significant p values
    points(seg$start[highpts], seg$mlog10p[highpts], pch=19, cex=0.5, col="darkred")
}

