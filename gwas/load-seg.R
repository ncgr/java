##
## load the segregation data
##

seg = read.table(file="CIDR_HD_Modifiers.seg.txt", header=F)

colnames(seg) = c("chr","start","caseVars","ctrlVars","caseRefs","ctrlRefs","p")

## takes a long time and not really needed
## rownames(seg) = paste(seg$chr,"_",seg$pos,sep="")

## chromosomes aren't all numbers
chrs = unique(seg$chr)

## do this once
seg$mlog10p = -log10(seg$p)


