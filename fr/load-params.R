## #alpha=0.1
## #kappa=0
## #clocktime=03:44:35
## #Mon Dec 16 03:46:50 MST 2019
## minSup=2
## debug=false
## minSize=1
## minLen=1.0
## txtFile=BBS4+ADPGK.21151.nodes.txt
## resume=false
## verbose=false
## graphName=BBS4+ADPGK.21151
## prunedGraph=false
## maxRound=0
## priorityOption=4
## minPriority=0
params = read.delim(file=paste(prefix,".params.txt",sep=""), header=FALSE, stringsAsFactors=FALSE, sep="=")
for (i in 1:length(rownames(params))) {
    ## comments section
    if (params$V1[i]=="#alpha") {
        alpha = as.numeric(params$V2[i])
    } else if (params$V1[i]=="#kappa") {
        kappa = as.numeric(params$V2[i])
    } else if (params$V1[i]=="#clocktime") {
        clocktime = params$V2[i]
    } else if (substr(params$V1[i], 1, 1)=="#") {
        date = substr(params$V1[i], 2, 1000)
    } else {
        ## main parameters section
        if (params$V1[i]=="minSup") minSup = as.numeric(params$V2[i])
        if (params$V1[i]=="debug") debug = as.logical(params$V2[i])
        if (params$V1[i]=="minSize") minSize = as.numeric(params$V2[i])
        if (params$V1[i]=="gfaFile") gfaFile = params$V2[i]
        if (params$V1[i]=="txtFile") txtFile = 
        if (params$V1[i]=="minLen") minLen = as.numeric(params$V2[i])
        if (params$V1[i]=="resume") resume = as.logical(params$V2[i])
        if (params$V1[i]=="verbose") verbose = as.logical(params$V2[i])
        if (params$V1[i]=="graphName") graphName = params$V2[i]
        if (params$V1[i]=="prunedGraph") prunedGraph = as.logical(params$V2[i])
        if (params$V1[i]=="maxRound") maxRound = as.numeric(params$V2[i])
        if (params$V1[i]=="priorityOption") priorityOption = as.numeric(params$V2[i])
        if (params$V1[i]=="minPriority") minPriority = as.numeric(params$V2[i])
        if (params$V1[i]=="keepOption") keepOption = sub("\\\\","",params$V2[i])
    }
}
