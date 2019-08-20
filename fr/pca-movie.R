##
## spin through two-component PCA dotplots
##
## presumes that paths.pca and paths are already populated

for (x in 1:5) {
    for (y in (x+1):10) {
        show(autoplot(paths.pca, data=paths, colour="Label", x=x, y=y))
    }
}
