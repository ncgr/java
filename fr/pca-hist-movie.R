##
## spin through PCA contribution histograms
##
## presumes that paths.pca and paths are already populated

for (pc in 1:50) {
    plot.pca.hist(pc)
    invisible(readline(prompt="Press [enter] to continue"))
}
