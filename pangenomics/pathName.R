## return the sample name(s) associated with the given full path string(s)
## 128686.1.case
pathName = function(pathStrings) {
    names = c()
    for (pathString in pathStrings) {
        pieces = strsplit(pathString, ".", fixed=T)
        names = c(names, pieces[[1]][1])
    }
    return(names)
}
    
