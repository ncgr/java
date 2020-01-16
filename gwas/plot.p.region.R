##
## plot log Fisher p value of each seg call on a single chromosome in the given start,end range
## we focus on loci with positive odds ratio since we're interested in ALTs that lead to the condition
##
plot.p.region = function(chr="1", start=1, end=0, gene=NULL, label=FALSE, minCalls=0, showGenes=FALSE, ymax=0) {
   
    pSig = 1e-4

    if (!is.null(gene)) {
        ## genes$start[genes$name=="HLA-A"]
        ## [1] 29909037
        ## genes$end[genes$name=="HLA-A"]
        ## [1] 29913661
        ## genes$seqid[genes$name=="HLA-A"]
        ## [1] 6
        chr = genes$seqid[genes$name==gene]
        start = genes$start[genes$name==gene]
        end = genes$end[genes$name==gene]
        showGenes = TRUE
    }
        
    if (end==0) end = max(seg$start[seg$chr==chr])
    
    pts = seg$chr==chr & is.finite(seg$OR) & seg$start>=start & seg$start<=end & (seg$caseVars+seg$ctrlVars)>=minCalls & (seg$caseRefs+seg$ctrlRefs)>=minCalls
    ptsCase = pts & seg$p<pSig & seg$log10OR>0
    ptsCtrl = pts & seg$p<pSig & seg$log10OR<0

    if (ymax==0) ymax = max(seg$mlog10p[pts])
    ylim = c(0, ymax)

    plot(seg$start[pts], seg$mlog10p[pts],
         xlab=paste("Chr",chr,"position"),
         ylab="-log10(p)",
         xlim=c(start,end),
         ylim=ylim,
         ## main=paste("GRCh37 ",chr,":",start,"-",end," ",(end-start+1),"bp", sep=""),
         pch=1, cex=0.5, col="black")

    ## significance line at 1e-2
    lines(c(1,1e9), rep(2,2), col="gray", lty=2)
    
    ## highlight highly significant p values
    points(seg$start[ptsCase], seg$mlog10p[ptsCase], pch=19, cex=0.8, col="darkred")
    points(seg$start[ptsCtrl], seg$mlog10p[ptsCtrl], pch=19, cex=0.8, col="darkgreen")

    ## label them with position if requested
    if (label) {
        text(seg$start[ptsCase], seg$mlog10p[ptsCase], col="darkred", pos=4, cex=0.8, offset=0.2,
             paste(seg$start[ptsCase],
                   " (",seg$caseVars[ptsCase],"/",seg$caseRefs[ptsCase],"|",seg$ctrlVars[ptsCase],"/",seg$ctrlRefs[ptsCase],";OR=",signif(seg$OR[ptsCase],3),")",sep="")
             )
        text(seg$start[ptsCtrl], seg$mlog10p[ptsCtrl], col="darkgreen", pos=4, cex=0.8, offset=0.2,
             paste(seg$start[ptsCtrl],
                   " (",seg$caseVars[ptsCtrl],"/",seg$caseRefs[ptsCtrl],"|",seg$ctrlVars[ptsCtrl],"/",seg$ctrlRefs[ptsCtrl],";OR=",signif(seg$OR[ptsCtrl],3),")",sep="")
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

