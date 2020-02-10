source("params.R")

## plot the FR p-value vs. odds ratio
plot.p.or = function(xmin=0, xmax=0, ymin=0, ymax=0) {

    ## represents infinity
    INFINITY = log10(max(frs$OR[is.finite(frs$OR)])*2)

    if (xmin==0 && min(frs$OR)>0) {
        xmin = log10(min(frs$OR))
    } else if (xmin==0) {
        xmin = -INFINITY
    }
    if (xmax==0 && is.finite(max(frs$OR))) {
        xmax = log10(max(frs$OR))
    } else if (xmax==0) {
        xmax = INFINITY
    }
    xlim = c(xmin,xmax)

    print(xlim)

    if (ymin==0) {
        ymin = min(-log10(frs$p))
    }
    if (ymax==0) {
        ymax = max(-log10(frs$p))
    }
    ylim = c(ymin,ymax)

    singles = frs$size==1
    multiples = frs$size>1
    
    sigcase = frs$OR>1.0 & frs$p<1e-2
    sigctrl = frs$OR<1.0 & frs$p<1e-2

    plot(log10(frs$OR[singles]), -log10(frs$p[singles]), pch=0, col="black",
         xlim=xlim, ylim=ylim, 
         xlab="<---control     log10(odds ratio)     case--->",
         ylab="-log10(p)"
         )

    points(log10(frs$OR[multiples]), -log10(frs$p[multiples]), pch=1, col="black")
    points(log10(frs$OR[sigcase]), -log10(frs$p[sigcase]), pch=19, col="darkred")
    points(log10(frs$OR[sigctrl]), -log10(frs$p[sigctrl]), pch=19, col="darkgreen")

    text(log10(frs$OR), -log10(frs$p), paste(frs$case,"/",frs$ctrl), col="black", pos=4, cex=0.7)
    params()
}


