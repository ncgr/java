##
## read the segregation data from a txt or txt.gz file
##
## contig start id REF ALT caseString controlString caseNoCalls controlNoCalls statistic p
read.seg = function(file="seg.txt.gz") {
    seg = read.table(file=file, header=F, sep="\t")
    colnames(seg) = c("chr","pos","id","ref","alts","caseString","controlString","caseNoCalls","controlNoCalls","statistic","p")
    ## chromosomes aren't all numbers
    chrs = unique(seg$chr)
    ## store these to save time later
    seg$mlog10p = -log10(seg$p)
    return(seg)
}


