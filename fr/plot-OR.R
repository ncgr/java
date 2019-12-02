##
## plot case/control odds ratio for each FR
##

plot(frs$support, log10(frs$OR),
     xlab="total support",
     ylab="case/control log10(OR)",
     main=graphPrefix
     )

text(frs$support, log10(frs$OR),
     rownames(frs),
     pos=1
     )

## zero line
lines(c(min(frs$support),max(frs$support)), c(0,0), lty=3, col="gray")

source("params.R")

## highlight low-p values on top
lowp = frs$p<1e-2
points(frs$support[lowp], log10(frs$OR[lowp]), pch=19, col="darkred")
