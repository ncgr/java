##
## plot log Fisher p value of each seg call on each chromosome, stacked plots
##
    
opar = par(mfrow=c(25,1))
par(mar=c(0.4,4,0.4,0.4))

pMax = 1e-1
pRed = 1e-3

## full chromosomes
xmin = 1
xmax = max(seg$start[seg$chr==1])

## ## HTT
## xmin = 3076408
## xmax = 3245687

ymax = max(seg$mlog10p)

xlim = c(xmin,xmax)
ylim = c(-log10(pMax),ymax)

yline1 = rep(2, 2)
yline2 = rep(-log10(pRed), 2)

## one plot per chromosome
## "1"  "2"  "3"  "4"  "5"  "6"  "7"  "8"  "9"  "10" "11" "12" "13" "14" "15" "16" "17" "18" "19" "20" "21" "22" "X" "Y" "MT"
for (chr in chrs) {

    pts = (seg$chr==chr & seg$p<pMax & seg$start>=xmin & seg$start<=xmax)
    highpts = (pts & seg$p<1e-3)
    
    plot(seg$start[pts], seg$mlog10p[pts],
         pch=1, cex=0.5, col="black", ylab=paste(chr), xlim=xlim, ylim=ylim,  xaxt='n', xaxs='i')
    lines(xlim,  yline1, col="gray", lty=2)
    
    ## highlight highly significant p values
    lines(xlim,  yline2, col="darkred", lty=2)
    points(seg$start[highpts], seg$mlog10p[highpts], pch=19, cex=0.5, col="darkred")
}

par(opar)

