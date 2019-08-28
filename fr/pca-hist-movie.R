##
## spin through PCA contribution histograms
##
## presumes that paths.pca and paths are already populated

source("plot.pca.hist.R")

for (pc in 1:length(colnames(paths.pca$x))) {
    plot.pca.hist(pc)
    invisible(readline(prompt="Press [enter] to continue"))
}
