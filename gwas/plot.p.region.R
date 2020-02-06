##
## plot log Fisher p value of each seg call on a single chromosome in the given start,end range
## we focus on loci with positive odds ratio since we're interested in ALTs that lead to the condition
##
plot.p.region = function(chr="1", start=1, end=0, gene=NULL, label=FALSE, minCalls=0, showGenes=FALSE, ymin=0, ymax=0) {
   
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
        
    if (end==0) end = max(seg$pos[seg$chr==chr])
    
    pts = seg$chr==chr & is.finite(seg$OR) & seg$pos>=start & seg$pos<=end & (seg$caseVars+seg$controlVars)>=minCalls & (seg$caseRefs+seg$controlRefs)>=minCalls
    ptsCase = pts & seg$p<pSig & seg$log10OR>0
    ptsControl = pts & seg$p<pSig & seg$log10OR<0
    hasCase = nrow(seg[ptsCase,])>0
    hasControl = nrow(seg[ptsControl,])>0

    if (ymax==0) ymax = max(seg$mlog10p[pts])
    ylim = c(ymin, ymax)

    plot(seg$pos[pts], seg$mlog10p[pts],
         xlab=paste("Chr",chr,"position"),
         ylab="-log10(p)",
         xlim=c(start,end),
         ylim=ylim,
         main=segFile,
         pch=1, cex=0.5, col="black")

    ## significance line at 1e-2
    lines(c(1,1e9), rep(2,2), col="gray", lty=2)
    
    ## highlight highly significant p values
    if (hasCase) { points(seg$pos[ptsCase], seg$mlog10p[ptsCase], pch=19, cex=0.8, col="darkred") }
    if (hasControl) { points(seg$pos[ptsControl], seg$mlog10p[ptsControl], pch=19, cex=0.8, col="darkgreen") }

    ## label them with position if requested
    if (label && hasCase) {
        text(seg$pos[ptsCase], seg$mlog10p[ptsCase], col="darkred", pos=4, cex=0.8, offset=0.2,
             paste(seg$id[ptsCase]," ",seg$ref[ptsCase],seg$alts[ptsCase],
                   " (",seg$caseVars[ptsCase],"/",seg$caseRefs[ptsCase],"|",seg$controlVars[ptsCase],"/",seg$controlRefs[ptsCase],";OR=",signif(seg$OR[ptsCase],3),")",sep="")
             )
    }
    if (label && hasControl) {
        text(seg$pos[ptsControl], seg$mlog10p[ptsControl], col="darkgreen", pos=4, cex=0.8, offset=0.2,
             paste(seg$id[ptsControl]," ",seg$ref[ptsControl],seg$alts[ptsControl],
                   " (",seg$caseVars[ptsControl],"/",seg$caseRefs[ptsControl],"|",seg$controlVars[ptsControl],"/",seg$controlRefs[ptsControl],";OR=",signif(seg$OR[ptsControl],3),")",sep="")
             )
    }

    ## show genes if requested
    ## REQUIRES load-genes!!
    if (showGenes) {
        ypos = par("yaxp")[1]
        bar = c(ypos-0.2, ypos+0.2)
        within = genes$seqid==chr & genes$end>=start & genes$start<=end
        genesWithin = genes[within,]
        for (i in 1:nrow(genesWithin)) {
            lines(c(genesWithin$start[i],genesWithin$end[i]), c(ypos,ypos))
            x = (genesWithin$start[i]+genesWithin$end[i])/2
            text(x, ypos, genesWithin$name[i], pos=3)
            lines(rep(genesWithin$start[i],2), bar)
            lines(rep(genesWithin$end[i],2), bar)
            if (genesWithin$strand[i]=="-") {
                text((genesWithin$start[i]+genesWithin$end[i])/2, ypos, "<")
            } else {
                text((genesWithin$start[i]+genesWithin$end[i])/2, ypos, ">")
            }
        }
    }
}

