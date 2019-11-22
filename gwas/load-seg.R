##
## load the segregation data
##

segFile = readline(prompt="Segregation file: ")
seg = read.table(file=segFile, header=F)

colnames(seg) = c("chr","start","caseVars","ctrlVars","caseRefs","ctrlRefs","p")

## TEMP calculate the odds ratio
seg$OR = (seg$caseVars/seg$ctrlVars) / (seg$caseRefs/seg$ctrlRefs)

## chromosomes aren't all numbers
chrs = unique(seg$chr)

## do this once
seg$mlog10p = -log10(seg$p)
seg$log10OR = log10(seg$OR)


