## return the sample label(s) associated with the given full path string(s)
## 128686.1.case
pathLabel = function(pathStrings) {
    labels = c()
    for (pathString in pathStrings) {
        pieces = strsplit(pathString, ".", fixed=T)
        labels = c(labels, pieces[[1]][3])
    }
    return(labels)
}
    
