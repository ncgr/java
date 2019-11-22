##
## load the segregation data
##

segFile = readline(prompt="Segregation file: ")
seg = read.table(file=segFile, header=F)

colnames(seg) = c("chr","start","caseVars","ctrlVars","caseRefs","ctrlRefs","p")

## chromosomes aren't all numbers
chrs = unique(seg$chr)

## do this once
seg$mlog10p = -log10(seg$p)


