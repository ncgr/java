##
## load the segregation data from a txt or txt.gz file
##
## contig start id REF ALT caseVars controlVars caseRefs controlRefs p OR caseNoCallCount controlNoCallCount

read.seg = function(file="seg.txt.gz") {
    seg = read.table(file=file, header=F, sep="\t")
    colnames(seg) = c("chr","pos","id","ref","alts","caseVars","controlVars","caseRefs","controlRefs","p","OR","caseNoCalls","controlNoCalls")
    ## recalculate the odds ratio so we get infinite values, handy in R
    seg$OR = (seg$caseVars*seg$controlRefs) / (seg$controlVars*seg$caseRefs)
    ## chromosomes aren't all numbers
    chrs = unique(seg$chr)
    ## store these to save time later
    seg$mlog10p = -log10(seg$p)
    seg$log10OR = log10(seg$OR)
    return(seg)
}


