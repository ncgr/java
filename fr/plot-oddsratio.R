##
## plot odds ratio vs FR for a run, after combining path support into sample support with get-sample-support.R
##
## sampleSupport
##
##        label FR1 FR2 FR3 FR4 FR5 FR6 FR7 FR8 FR9 FR10 FR11 FR12 FR13 FR14
## 339537  case   1   1   0   1   0   0   0   0   0    0    0    0    0    0
## 483736  ctrl   1   1   0   1   1   1   1   1   1    1    1    1    1    1
## 373826  ctrl   1   1   0   1   0   0   0   0   0    0    0    0    0    0

## mergeMode should be set in get-sample-support.R

caseSamples = labelCounts["case",1]/2
ctrlSamples = labelCounts["ctrl",1]/2

oddsratio = data.frame(row.names=colnames(sampleSupport))
oddsratio = oddsratio[rownames(oddsratio)!="label",]

for (fr in colnames(sampleSupport)) {
    if (fr!="label") {
        cases = sum(sampleSupport[sampleSupport$label=="case",fr])
        ctrls = sum(sampleSupport[sampleSupport$label=="ctrl",fr])
        oddsratio[fr,"cases"] = cases
        oddsratio[fr,"ctrls"] = ctrls
        oddsratio[fr,"OR"] = (cases/ctrls) / (caseSamples/ctrlSamples)
    }
}

plot(oddsratio$OR,
     xlab="FR number",
     ylab="<--more controls     Odds ratio     more cases-->",
     xlim=c(0,nrow(oddsratio)+1),
     pch=19,
     main=paste("Odds Ratio",graphPrefix,"alpha =",alpha,"kappa =",kappa), cex.main=1.0
     )

lines(c(1,nrow(oddsratio)), c(1,1), lty=2)

text(oddsratio$OR,
     paste(oddsratio[,"cases"],"/",oddsratio[,"ctrls"], sep=""),
     pos=4, offset=0.3
     )

source("params.R")


