##
## plot log Fisher p value of each seg call on each chromosome, stacked plots
##
    
opar = par(mfrow=c(23,1)) # save!
par(mar=c(0.4,4,0.4,0.4))

pRed = 1e-4
minCalls = 100

## full chromosomes
xmin = 1
xmax = max(seg$start[seg$chr==1])
xlim = c(xmin,xmax)

## use same scale for all chrs
ymin = 0
ymax = max(seg$mlog10p)
ylim = c(0,ymax)

## one plot per chromosome, skipping Y and MT
## "1"  "2"  "3"  "4"  "5"  "6"  "7"  "8"  "9"  "10" "11" "12" "13" "14" "15" "16" "17" "18" "19" "20" "21" "22" "X"
for (chr in chrs) {
    if (chr!="Y" && chr!="MT") {
        ## filter
        pts = seg$chr==chr & (seg$caseVars+seg$ctrlVars)>=minCalls & (seg$caseRefs+seg$ctrlRefs)>=minCalls
        hpts = (pts & seg$p<pRed)
        lowpts = (pts & seg$p>=pRed)
        ## plot low points in black
        plot(seg$start[lowpts], seg$mlog10p[lowpts],
             xlim=xlim, ylim=ylim,
             xaxt='n', xaxs='i', pch=1, cex=0.5, col="black",
             ylab=paste(chr))

        ## gray line at p=0.01
        lines(xlim,  rep(2,2), col="gray", lty=2)

        ## thin vertical lines at 10MB intervals
        for (x in (1:24)*10e6) {
            lines(c(x,x), c(ymin,ymax), col="gray", lwd=1)
        }
        ## plot high points in red last so they overlay lines
        points(seg$start[hpts], seg$mlog10p[hpts], pch=19, cex=0.5, col="darkred")
    }
}

par(opar)

