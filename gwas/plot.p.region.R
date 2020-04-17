source("snpData.R")
##
## plot log Fisher p value of each seg call on a single chromosome in the given start,end range
## we focus on loci with positive odds ratio since we're interested in ALTs that lead to the condition
##
## http://myvariant.info/v1/query?q=rs727503873
##
## colnames(seg) = c("chr","pos","id","ref","alts","caseString","controlString","caseNoCalls","controlNoCalls","statistic","p")
##
plot.p.region = function(seg=seg, chr="0", start=0, end=0, gene=NULL, label=FALSE, labelSNP=FALSE, showGenes=FALSE, ymin=0, ymax=0, pSig=5e-8) {

    if (!is.null(gene)) {
        geneRecord = genes[genes$name==gene,]
        chr = geneRecord$seqid
        start = geneRecord$start
        end = geneRecord$end
    }
        
    if (chr!="0" && start==0) start = min(seg$pos[seg$chr==chr])
    if (chr!="0" && end==0) end = max(seg$pos[seg$chr==chr])

    ## collect the points of interest
    pts = seg$pos>0
    if (chr!="0") {
        pts = pts & seg$chr==chr & seg$pos>=start & seg$pos<=end
    }

    ## significant points
    ptsSig = pts & seg$p<pSig
    hasSig = length(seg$p[ptsSig]) > 0

    ## limits
    if (ymax==0) ymax = max(seg$mlog10p[pts])
    ylim = c(ymin, ymax)

    ## plot
    if (chr=="0") {
        plot(seg$mlog10p[pts],
             xlab=paste("All Chromosomes"),
             ylab="-log10(p)",
             ylim=ylim,
             main=deparse(substitute(seg)),
             pch=1, cex=0.3, col="black")
    } else {
        plot(seg$pos[pts], seg$mlog10p[pts],
             xlab=paste("Chr",chr,"position"),
             ylab="-log10(p)",
             xlim=c(start,end),
             ylim=ylim,
             main=paste(deparse(substitute(seg)),chr,":",start,"-",end),
             pch=1, cex=0.3, col="black")
    }

    ## vertical chromosome lines if plotting full genome
    if (chr=="0") {
        segpts = seg[pts,]
        currentChr = "0"
        for (i in 1:nrow(segpts)) {
            if (segpts$chr[i]!=currentChr) {
                currentChr = segpts$chr[i]
                lines(c(i,i), ylim, lwd=1, col="gray")
                text(i, ylim[2], currentChr, cex=0.5)
            }
        }
    }
    
    ## highlight highly significant p values
    if (hasSig && chr=="0") {
        points(as.numeric(rownames(seg)[ptsSig]), seg$mlog10p[ptsSig], pch=19, cex=0.6, col="darkgreen")
    } else if (hasSig) {
        points(seg$pos[ptsSig], seg$mlog10p[ptsSig], pch=19, cex=0.6, col="darkgreen")
    }

    ## label significant points with position if requested
    if (hasSig && chr!="0" && label) {
        snpInfo = c()
        for (rsId in seg$id[ptsSig]) {
            clinvar.clinsig = ""
            if (labelSNP && startsWith(rsId,"rs")) {
                ## query the SNP API
                json = snpData(rsId)
                for (hit in json$hits) {
                    if ("clinvar" %in% colnames(hit)) {
                        clinvar = hit$clinvar
                        for (clinsig in clinvar$clinsig) {
                            if (!is.na(clinsig)) clinvar.clinsig = paste(clinvar.clinsig, clinsig)
                        }
                    }
                }
            }
            snpInfo = c(snpInfo, clinvar.clinsig)
        }
        text(seg$pos[ptsSig], seg$mlog10p[ptsSig],
             paste(seg$id[ptsSig],seg$genotypes[ptsSig],seg$caseString[ptsSig],seg$controlString[ptsSig],snpInfo),
             col="darkgreen", pos=4, cex=0.6, offset=0.2)
    }

    ## show gene or genes if requested
    ## REQUIRES load-genes!!
    if (!is.null(gene)) {
        ypos = par("yaxp")[1]
        bar = c(ypos*0.99, ypos*1.01)
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
        bar = c(ypos*0.99, ypos*1.01)
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

