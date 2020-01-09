##
## plot log Fisher p value of each seg call on a single chromosome in the given start,end range
## we focus on loci with positive odds ratio since we're interested in ALTs that lead to the condition
##
plot.p.region = function(chr="1", start=1, end=0, label=FALSE, minCalls=0, showGenes=FALSE, pMax=1) {
   
    pRed = 1e-4
    
    if (end==0) end = max(seg$start[seg$chr==chr])
    
    pts = seg$p<pMax & seg$chr==chr & is.finite(seg$OR) & seg$start>=start & seg$start<=end & (seg$caseVars+seg$ctrlVars)>=minCalls & (seg$caseRefs+seg$ctrlRefs)>=minCalls
    hpts = pts & seg$p<pRed

    plot(seg$start[pts], seg$mlog10p[pts],
         xlab=paste("Chr",chr,"position"),
         ylab="-log10(p)",
         xlim=c(start,end),
         ## main=paste("GRCh37 ",chr,":",start,"-",end," ",(end-start+1),"bp", sep=""),
         pch=1, cex=0.5, col="black")

    ## significance line at 1e-2
    lines(c(1,1e9), rep(2,2), col="gray", lty=2)
    
    ## highlight highly significant p values
    points(seg$start[hpts], seg$mlog10p[hpts], pch=19, cex=0.8, col="darkred")

    ## label them with position if requested
    if (label) {
        text(seg$start[hpts], seg$mlog10p[hpts], col="darkred", pos=4, cex=0.8, offset=0.2,
             paste(seg$start[hpts]," (",seg$caseVars[hpts],"/",seg$caseRefs[hpts],"|",seg$ctrlVars[hpts],"/",seg$ctrlRefs[hpts],";OR=",signif(seg$OR[hpts],3),")",sep="")
             )
    }

    ## show genes if requested
    ## REQUIRES load-genes!!
    if (showGenes) {
        height = 3.0
        bar = c(height-0.5, height+0.5)
        within = genes$seqid==chr & genes$end>=start & genes$start<=end
        genesWithin = genes[within,]
        for (i in 1:nrow(genesWithin)) {
            lines(c(genesWithin$start[i],genesWithin$end[i]), c(height,height))
            x = (genesWithin$start[i]+genesWithin$end[i])/2
            text(x, height, genesWithin$name[i], pos=3)
            lines(rep(genesWithin$start[i],2), bar)
            lines(rep(genesWithin$end[i],2), bar)
            if (genesWithin$strand[i]=="-") {
                text((genesWithin$start[i]+genesWithin$end[i])/2, height, "<")
            } else {
                text((genesWithin$start[i]+genesWithin$end[i])/2, height, ">")
            }
        }
    }
}

