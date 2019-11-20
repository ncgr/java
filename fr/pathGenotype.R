## return the sample genotypes(s) associated with the given full path string(s)
## 128686.1.case
pathGenotype = function(pathStrings) {
    genotypes = c()
    for (pathString in pathStrings) {
        pieces = strsplit(pathString, ".", fixed=T)
        genotypes = c(genotypes, as.numeric(pieces[[1]][2]))
    }
    return(genotypes)
}
    
