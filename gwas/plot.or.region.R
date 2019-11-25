##
## plot the odds ratio of each seg call on a single chromosome in the given start,end range
##
plot.or.region = function(chr="1", start=1, end=0, label=FALSE, minCalls=0, gene="") {
    
    pRed = 1e-4
    
    if (end==0) end = max(seg$start[seg$chr==chr])

    nonzeros = seg$caseVars>0 & seg$ctrlVars>0 & seg$caseRefs>0 & seg$ctrlRefs>0
    pts = nonzeros & seg$chr==chr & !is.na(seg$OR) & seg$start>=start & seg$start<=end & (seg$caseVars+seg$ctrlVars)>=minCalls & (seg$caseRefs+seg$ctrlRefs)>=minCalls
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
         main=paste(gene," ",(end-start+1),"bp ",chr,":",start,"-",end," (GRCh37)", sep=""),
         pch=1, cex=0.5, col="black")

    ## highlight highly significant p values
    points(seg$start[hpts], seg$log10OR[hpts], pch=19, cex=0.5, col="darkred")

    ## label them with position if requested
    if (label) {
        text(seg$start[hpts], seg$log10OR[hpts], col="darkred", pos=4, cex=0.6, offset=0.2,
             paste(seg$start[hpts],"(",seg$caseVars[hpts],"/",seg$ctrlVars[hpts],"|",seg$caseRefs[hpts],"/",seg$ctrlRefs[hpts],";p=",signif(seg$p[hpts],3),")",sep="")
             )
    }
}

