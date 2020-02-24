##
## plot the odds ratio of each seg call on a single chromosome in the given start,end range
##
plot.or.region = function(seg=seg, chr="1", start=1, end=0, label=FALSE, minCalls=0, minAlts=0, showGenes=FALSE, ymin=0, ymax=0) {
    
    pSig = 1e-2
    
    if (end==0) end = max(seg$pos[seg$chr==chr])

    pts = is.finite(seg$log10OR) & seg$chr==chr & seg$pos>=start & seg$pos<=end &
        (seg$caseVars+seg$controlVars+seg$caseRefs+seg$controlRefs)>=minCalls &
        (seg$caseVars+seg$controlVars)>=minAlts
    hpts = pts & seg$p<pSig
    
    xmin = start
    xmax = end

    if (ymin==0) {
        ymin = min(c(0.0,seg$log10OR[pts]))
    }
    if (ymax==0) {
        ymax = max(seg$log10OR[pts])
    }

    xlim = c(xmin,xmax)
    ylim = c(ymin,ymax)
    
    plot(seg$pos[pts], seg$log10OR[pts],
         xlim=xlim, ylim=ylim,
         xlab=paste("Chr",chr,"position"),
         ylab="log10(OR)",
         ## main=paste("GRCh37 ",chr,":",start,"-",end," ",(end-start+1),"bp", sep=""),
         pch=1, cex=0.3, col="black")

    lines(xlim, c(0,0), col="gray")

    ## highlight highly significant p values
    points(seg$pos[hpts], seg$log10OR[hpts], pch=19, cex=0.6, col="darkblue")

    ## label them with position if requested
    if (label) {
        text(seg$pos[hpts], seg$log10OR[hpts], col="darkblue", pos=4, cex=0.6, offset=0.2,
             paste(seg$id[hpts]," ",seg$ref[hpts],seg$alts[hpts]," (",signif(seg$p[hpts],3),")",sep="")
             )
    }

    ## show genes if requested
    ## REQUIRES load-genes!!
    if (showGenes) {
        within = genes$seqid==chr & genes$end>=start & genes$start<=end
        genesWithin = genes[within,]
        if (nrow(genesWithin)==0) {
            print("No genes within requested region.", quote=FALSE)
        } else {
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
}

