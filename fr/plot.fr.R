##
## plots one FR versus the other
##

colors = c("darkred","darkblue","darkgreen")

plot.fr = function(m, n) {
    frNum = length(colnames(fr))
    subjectNum = length(rownames(fr))
    col = array()
    for (i in 1:subjectNum) {
        subject = rownames(fr)[i]
        col[i] = colors[subjects[subject,1]]
    }
    plot(fr[,m], fr[,n], xlab=colnames(fr)[m], ylab=colnames(fr)[n], col=col, cex=0.1*(1:50))
    ##     text(fr[,n], fr[,m], rownames(fr))
}

    
