##
## plot case/control odds ratio for each FR
##

plot(frs$support, log10(frs$OR),
     xlab="Total Support",
     ylab="case/control log10(odds ratio)",
     main=graphPrefix
     )

source("params.R")

