##
## read the segregation data from a txt or txt.gz file
##
## contig start id genotypeString caseString controlString noCallCount statistic p
read.seg = function(file="seg.txt.gz") {
    seg = read.table(file=file, header=F, sep="\t")
    colnames(seg) = c("chr","pos","id","genotypes","caseString","controlString","noCalls","statistic","p")
    ## store the number of genotypes, 2=SNP without ALT HOM so we can compute odds ratio
    ## NOTE: there should be a way of doing this without a slow loop!!!
    seg$ngenotypes = 0
    foo = strsplit(seg$genotypes, ":", TRUE)
    for (i in 1:nrow(seg)) {
        seg$ngenotypes[i] = length(foo[[i]])
    }
    ## store -log10(p) to save time later
    seg$mlog10p = -log10(seg$p)
    ## get the chromosomes into a simple list
    chrs = unique(seg$chr)
    return(seg)
}


