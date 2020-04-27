## #alpha=1.0
## #kappa=2147483647
## #clocktime=02:05:32
## #Sun Apr 26 19:19:13 MDT 2020
## requiredNodeString=[710]
## minSize=0
## minLength=0.0
## resume=false
## graphName=SchizophreniaSwedish_Sklar/HLAA
## minMAF=0.01
## maxRound=50
## priorityOption=4
## writeSaveFiles=false
## minSupport=2500
## minPriority=700
## excludedNodeString=[]
## keepOption=subset
## requireBestNodeSet=true

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
        ## string parameters
        if (params$V1[i]=="graphName") graphName = params$V2[i]
        if (params$V1[i]=="gfaFile") gfaFile = params$V2[i]
        if (params$V1[i]=="txtFile") txtFile = params$V2[i]
        if (params$V1[i]=="requiredNodeString") requiredNodeString = params$V2[i]
        if (params$V1[i]=="priorityOption") priorityOption = params$V2[i]
        if (params$V1[i]=="excludedNodeString") excludedNodeString = params$V2[i]
        if (params$V1[i]=="keepOption") keepOption = sub("\\\\","",params$V2[i])
        ## boolean parameters
        if (params$V1[i]=="requireBestNodeSet") requireBestNodeSet = (params$V2[i]=="true")
        ## numeric parameters
        if (params$V1[i]=="minSupport") minSupport = as.numeric(params$V2[i])
        if (params$V1[i]=="minPriority") minPriority = as.numeric(params$V2[i])
        if (params$V1[i]=="minSize") minSize = as.numeric(params$V2[i])
        if (params$V1[i]=="minMAF") minMAF = as.numeric(params$V2[i])
        if (params$V1[i]=="maxRound") maxRound = as.numeric(params$V2[i])
        
        if (params$V1[i]=="minLength") minLength = as.numeric(params$V2[i])
    }
}
