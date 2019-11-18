##
## plot FR case vs control p-values from two-sample test for proportions Z-test
##

plot(-log10(frs$p.value),
	xlab="FR #", ylab="-log10(p)",
	pch=19
	)

title(main=paste("chi-squared test",outputPrefix,"alpha =",alpha,"kappa =",kappa), cex.main=1.0)
lines(c(0,length(frs$p.value)), rep(-log10(0.01),2), lty=2)
lines(c(0,length(frs$p.value)), rep(-log10(0.05),2), lty=2)

text(-log10(frs$p.value), paste(frs$case,"/",frs$ctrl,sep=""), pos=4, cex=0.7, offset=0.2)
    
source("params.R")

