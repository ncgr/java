##
## plot FR case vs control p-values from two-sample chi-squared test, after combining path support into sample support with get-sample-support.R
##

## mergeMode should be set in get-sample-support.R

caseSamples = labelCounts["case",1]/2
ctrlSamples = labelCounts["ctrl",1]/2

pcase = caseSamples/(caseSamples+ctrlSamples) # null hypothesis for case fraction
pctrl = ctrlSamples/(caseSamples+ctrlSamples) # null hypothesis for ctrl fraction

chisquared = data.frame(row.names=colnames(sampleSupport))
chisquared = chisquared[rownames(chisquared)!="label",]

for (fr in colnames(sampleSupport)) {
    if (fr!="label") {
        scase = sum(sampleSupport[sampleSupport$label=="case",fr])
        sctrl = sum(sampleSupport[sampleSupport$label=="ctrl",fr])
        chisq = chisq.test(x=c(scase,sctrl), p=c(pcase,pctrl))
        chisquared[fr,"scase"] = scase
        chisquared[fr,"sctrl"] = sctrl
        chisquared[fr,"statistic"] = chisq$statistic
        chisquared[fr,"p.value"] = chisq$p.value
    }
}

plot(-log10(chisquared$p.value),
	xlab="FR #", ylab="chi-squared test: -log10(p)",
	pch=19
	)

title(main=paste("chi-squared test",graphPrefix,"alpha =",alpha,"kappa =",kappa), cex.main=1.0)
lines(c(0,length(chisquared$p.value)), rep(-log10(0.01),2), lty=2)
lines(c(0,length(chisquared$p.value)), rep(-log10(0.05),2), lty=2)

text(-log10(chisquared$p.value), paste(chisquared$scase,"/",chisquared$sctrl,sep=""), pos=4, cex=0.7, offset=0.2)
    
source("params.R")

