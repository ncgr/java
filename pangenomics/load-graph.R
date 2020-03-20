## load the graph data

graph = readline(prompt="Graph (ex. HTT): ")
nodesFilename = paste(graph, ".nodes.txt", sep="")
pathsFilename = paste(graph, ".paths.txt", sep="")

nodes.in = read.table(nodesFilename, header=FALSE, stringsAsFactors=FALSE)
nodes = nodes.in$V2
rm(nodes.in)

paths.in = read.table(pathsFilename, header=FALSE, stringsAsFactors=FALSE, row.names=1, dec=",", fill=TRUE)
paths = subset(paths.in, select=c(1,2))
paths.in$V2 = NULL
paths.in$V3 = NULL

for (i in 1:nrow(paths)) {
    path = as.numeric(paths.in[i,])
    path = path[!is.na(path)]
    paths$nodes[i] = list(path)
}

colnames(paths) = c("label","length","nodes")




