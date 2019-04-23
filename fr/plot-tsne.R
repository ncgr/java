##
## plot the final t-SNE results
##

plot(tsne_frs, type="n", main=prefix, xlab="t-SNE dim 1", ylab="t-SNE dim 2");
text(tsne_frs, labels=pathnames, col=colors[labels], cex=0.75)
