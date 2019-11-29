##
## plot case/control odds ratio for each FR
##

plot(frs$support, log10(frs$OR),
     xlab="total support",
     ylab="case/control log10(odds ratio)",
     main=graphPrefix
     )

text(frs$support, log10(frs$OR),
     rownames(frs),
     pos=1
     )

source("params.R")

