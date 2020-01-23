## #alpha=1.0
## #kappa=100
## #clocktime=00:47:11
## #Thu Jan 16 09:04:56 MST 2020
## minSup=1
## debug=false
## minSize=1
## minLen=1.0
## txtFile=HLAB.601.nodes.txt
## resume=false
## verbose=true
## graphName=HLAB.601
## requiredNodes=[]
## maxRound=100
## priorityOption=4
## minPriority=1
## forbiddenNodes=[]
## keepOption=subset

params = read.delim(file=paste(prefix,".params.txt",sep=""), header=FALSE, stringsAsFactors=FALSE, sep="=")
for (i in 1:length(rownames(params))) {
    ## comments section
    if (params$V1[i]=="#alpha") {
        alpha = as.numeric(params$V2[i])
    } else if (params$V1[i]=="#kappa") {
        kappa = as.numeric(params$V2[i])
        if (kappa==2147483647) {
            kappa = Inf
        }
    } else if (params$V1[i]=="#clocktime") {
        clocktime = params$V2[i]
    } else if (substr(params$V1[i], 1, 1)=="#") {
        date = substr(params$V1[i], 2, 1000)
    } else {
        ## defaults
        gfaFile = NULL
        txtFile = NULL
        ## main parameters section
        if (params$V1[i]=="minSup") minSup = as.numeric(params$V2[i])
        if (params$V1[i]=="debug") debug = as.logical(params$V2[i])
        if (params$V1[i]=="minSize") minSize = as.numeric(params$V2[i])
        if (params$V1[i]=="gfaFile") gfaFile = params$V2[i]
        if (params$V1[i]=="txtFile") txtFile = params$V2[i]
        if (params$V1[i]=="minLen") minLen = as.numeric(params$V2[i])
        if (params$V1[i]=="resume") resume = as.logical(params$V2[i])
        if (params$V1[i]=="verbose") verbose = as.logical(params$V2[i])
        if (params$V1[i]=="graphName") graphName = params$V2[i]
        if (params$V1[i]=="requiredNodes") requiredNodes = params$V2[i]
        if (params$V1[i]=="maxRound") maxRound = as.numeric(params$V2[i])
        if (params$V1[i]=="priorityOption") priorityOption = as.numeric(params$V2[i])
        if (params$V1[i]=="minPriority") minPriority = as.numeric(params$V2[i])
        if (params$V1[i]=="forbiddenNodes") forbiddenNodes = params$V2[i]
        if (params$V1[i]=="keepOption") keepOption = sub("\\\\","",params$V2[i])
    }
}
