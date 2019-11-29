##
## plot case/control p-values each FR
##

plot(frs$support, -log10(frs$p),
     xlab="total support",
     ylab="case/control p-value (Fisher's exact test)",
     main=graphPrefix
     )

text(frs$support, -log10(frs$p),
     rownames(frs),
     pos=1
     )

source("params.R")

