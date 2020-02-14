##
## load the segregation data
##

segFile = readline(prompt="Segregation file: ")
seg = read.table(file=segFile, header=F, sep="\t")

## System.out.println(contig+"\t"+start+"\t"+id+"\t"+
##                    vc.getReference()+"\t"+vc.getAlternateAlleles()+"\t"+
##                    caseVars+"\t"+controlVars+"\t"+caseRefs+"\t"+controlRefs+"\t"+p+"\t"+or);
## }

colnames(seg) = c("chr","pos","id","ref","alts","caseVars","controlVars","caseRefs","controlRefs","p","OR")

## recalculate the odds ratio so we get infinite values, handy in R
seg$OR = (seg$caseVars*seg$controlRefs) / (seg$controlVars*seg$caseRefs)

## chromosomes aren't all numbers
chrs = unique(seg$chr)

## do this once
seg$mlog10p = -log10(seg$p)
seg$log10OR = log10(seg$OR)


