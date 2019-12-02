##
## plot case/control p-values each FR
##

plot(frs$support, -log10(frs$p),
     xlab="total support",
     ylab="-log10(p) Fisher's exact test case/ctrl support vs paths",
     main=graphPrefix
     )

text(frs$support, -log10(frs$p), rownames(frs), pos=1)

source("params.R")

## highlight low-p values on top
lowp = frs$p<1e-2
points(frs$support[lowp], -log10(frs$p[lowp]), pch=19, col="darkred")
