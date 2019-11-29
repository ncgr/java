##
## plot the odds ratio of each seg call on a single chromosome in the given start,end range
##
plot.or.region = function(chr="1", start=1, end=0, label=FALSE, minCalls=0, showGenes=FALSE) {
    
    pRed = 1e-4
    
    if (end==0) end = max(seg$start[seg$chr==chr])

    pts = is.finite(seg$log10OR) & seg$chr==chr & seg$start>=start & seg$start<=end & (seg$caseVars+seg$ctrlVars)>=minCalls & (seg$caseRefs+seg$ctrlRefs)>=minCalls
    hpts = pts & seg$p<pRed
    
    xmin = start
    xmax = end
    
    ymin = min(seg$log10OR[pts])
    ymax = max(seg$log10OR[pts])

    xlim = c(xmin,xmax)
    ylim = c(ymin,ymax)
    
    plot(seg$start[pts], seg$log10OR[pts],
         xlim=xlim, ylim=ylim,
         xlab="POS", ylab="log10(OR)",
         main=paste("GRCh37 ",chr,":",start,"-",end," ",(end-start+1),"bp", sep=""),
         pch=1, cex=0.5, col="black")

    ## highlight highly significant p values
    points(seg$start[hpts], seg$log10OR[hpts], pch=19, cex=0.8, col="darkred")

    ## label them with position if requested
    if (label) {
        text(seg$start[hpts], seg$log10OR[hpts], col="darkred", pos=4, cex=0.8, offset=0.2,
             paste(seg$start[hpts]," (",seg$caseVars[hpts],"/",seg$caseRefs[hpts],"|",seg$ctrlVars[hpts],"/",seg$ctrlRefs[hpts],";p=",signif(seg$p[hpts],3),")",sep="")
             )
    }

    ## show genes if requested
    ## REQUIRES load-genes!!
    if (showGenes) {
        within = genes$seqid==chr & genes$end>=start & genes$start<=end
        genesWithin = genes[within,]
        for (i in 1:nrow(genesWithin)) {
            lines(c(genesWithin$start[i],genesWithin$end[i]), c(0,0))
            x = (genesWithin$start[i]+genesWithin$end[i])/2
            text(x, 0, genesWithin$name[i], pos=3)
            lines(rep(genesWithin$start[i],2), c(-0.05,+0.05))
            lines(rep(genesWithin$end[i],2), c(-0.05,+0.05))
            if (genesWithin$strand[i]=="-") {
                text((genesWithin$start[i]+genesWithin$end[i])/2, 0, "<")
            } else {
                text((genesWithin$start[i]+genesWithin$end[i])/2, 0, ">")
            }
        }
    }
}

