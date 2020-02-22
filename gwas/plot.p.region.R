source("snpData.R")

##
## plot log Fisher p value of each seg call on a single chromosome in the given start,end range
## we focus on loci with positive odds ratio since we're interested in ALTs that lead to the condition
##
## http://myvariant.info/v1/query?q=rs727503873
##

plot.p.region = function(chr="1", start=0, end=0, gene=NULL, label=FALSE, labelSNP=FALSE, minCalls=0, minAlts=0, showGenes=FALSE, ymin=0, ymax=0, caseOnly=FALSE, ctrlOnly=FALSE) {
   
    pSig = 1e-2

    if (!is.null(gene)) {
        geneRecord = genes[genes$name==gene,]
        chr = geneRecord$seqid
        start = geneRecord$start
        end = geneRecord$end
    }
        
    if (start==0) start = min(seg$pos[seg$chr==chr])
    if (end==0) end = max(seg$pos[seg$chr==chr])
    
    pts = seg$chr==chr & seg$pos>=start & seg$pos<=end &
        (seg$caseVars+seg$controlVars+seg$caseRefs+seg$controlRefs)>=minCalls &
        (seg$caseVars+seg$controlVars)>=minAlts
    if (caseOnly) {
        pts = pts & is.finite(seg$log10OR) & seg$log10OR>0
    } else if (ctrlOnly) {
        pts = pts & is.finite(seg$log10OR) & seg$log10OR<0
    }
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
         pch=1, cex=0.3, col="black")

    ## significance line at 1e-2
    lines(c(1,1e9), rep(2,2), col="gray", lty=2)
    
    ## highlight highly significant p values
    if (hasControl) { points(seg$pos[ptsControl], seg$mlog10p[ptsControl], pch=19, cex=0.6, col="darkgreen") }
    if (hasCase) { points(seg$pos[ptsCase], seg$mlog10p[ptsCase], pch=19, cex=0.6, col="darkred") }

    ## label them with position if requested
    if (label) {
        if (hasControl) {
            if (labelSNP) {
                snpInfo = c()
                for (rsId in seg$id[ptsControl]) {
                    if (startsWith(rsId, "rs")) {
                        ## query the SNP API
                        json = snpData(rsId)
                        if (is.null(json$hits$clinvar)) {
                            clinVarSig = "Not in ClinVar"
                        } else {
                            clinVar = json$hits$clinvar
                            clinVarSig = clinVar$rcv$clinical_significance
                        }
                        snpInfo = c(snpInfo, clinVarSig)
                    } else {
                        snpInfo = c(snpInfo, "")
                    }
                }
            } else {
                snpInfo = rep("", nrow(seg[ptsControl,]))
            }
            text(seg$pos[ptsControl], seg$mlog10p[ptsControl], paste(seg$id[ptsControl]," ",seg$ref[ptsControl],seg$alts[ptsControl]," (",
                                                                     seg$caseVars[ptsControl],"/",seg$caseRefs[ptsControl],"|",
                                                                     seg$controlVars[ptsControl],"/",seg$controlRefs[ptsControl],
                                                                     ";OR=",signif(seg$OR[ptsControl],3),") ",snpInfo,sep=""),
                 col="darkgreen", pos=4, cex=0.6, offset=0.2)
        }
        if (hasCase) {
            if (labelSNP) {
                snpInfo = c()
                for (rsId in seg$id[ptsCase]) {
                    if (startsWith(rsId, "rs")) {
                        ## query the SNP API
                        json = snpData(rsId)
                        if (is.null(json$hits$clinvar)) {
                            clinVarSig = "Not in ClinVar"
                        } else {
                            clinVar = json$hits$clinvar
                            clinVarSig = clinVar$rcv$clinical_significance
                        }
                        snpInfo = c(snpInfo, clinVarSig)
                    } else {
                        snpInfo = c(snpInfo, "")
                    }
                }
            } else {
                snpInfo = rep("", nrow(seg[ptsCase,]))
            }
            text(seg$pos[ptsCase], seg$mlog10p[ptsCase], paste(seg$id[ptsCase]," ",
                                                               seg$ref[ptsCase],seg$alts[ptsCase]," (",
                                                               seg$caseVars[ptsCase],"/",seg$caseRefs[ptsCase],"|",
                                                               seg$controlVars[ptsCase],"/",seg$controlRefs[ptsCase],";OR=",
                                                               signif(seg$OR[ptsCase],3),") ",snpInfo,sep=""),
                 col="darkred", pos=4, cex=0.6, offset=0.2)
        }
    }

    ## show gene or genes if requested
    ## REQUIRES load-genes!!
    if (!is.null(gene)) {
        ypos = par("yaxp")[1]
        bar = c(ypos-0.2, ypos+0.2)
        lines(c(start,end), c(ypos,ypos))
        x = (start+end)/2
        text(x, ypos, gene, pos=1, cex=0.6)
        lines(rep(start,2), bar)
        lines(rep(end,2), bar)
        if (geneRecord$strand=="-") {
            text((start+end)/2, ypos, "<")
        } else {
            text((start+end)/2, ypos, ">")
        }
    }
    if (showGenes) {
        ypos = par("yaxp")[1]
        bar = c(ypos-0.2, ypos+0.2)
        within = genes$seqid==chr & genes$end>=start & genes$start<=end
        genesWithin = genes[within,]
        for (i in 1:nrow(genesWithin)) {
            lines(c(genesWithin$start[i],genesWithin$end[i]), c(ypos,ypos))
            x = (genesWithin$start[i]+genesWithin$end[i])/2
            text(x, ypos, genesWithin$name[i], pos=1, cex=0.6)
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

