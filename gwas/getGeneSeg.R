## pull a gene from the seg dataframe
##
## load-genes.R must be run first!

getGeneSeg = function(gene="ARSD", significant=FALSE) {
    gene = genes[genes$name=="ARSD",]
    if (significant) {
        return(seg[seg$chr==gene$seqid & seg$pos>=gene$start & seg$pos<=gene$end & seg$p<1e-2,])
    } else {
        return(seg[seg$chr==gene$seqid & seg$pos>=gene$start & seg$pos<=gene$end,])
    }
}
