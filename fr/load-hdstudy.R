## load a FRFinder output csfr file

study="HDStudy"
dataset="HTT"

mem=60

alpha=0.2
kappa=30
minsup=2
minsize=1

subjects = read.table(file=paste(study,"/subjects.txt",sep=""))

prefix=paste("FR-a",alpha,"-kp",kappa,"-sup",minsup,"-sz",minsize,sep="")
dot=paste("HDStudy+1kG.",dataset,".k",mem,sep="")
fasta=paste("HDStudy+1kG.",dataset,sep="")
frdir=paste("FR-",dot,".dot-",fasta,".fasta",sep="")
csfr=paste(frdir,"/",prefix,".csfr.txt",sep="")

fr = read.table(file=csfr, sep=",", row.names=1, stringsAsFactors=FALSE)

frNum = length(colnames(fr))
sampleNum = length(rownames(fr))

colnames(fr) = paste("FR", 1:frNum, sep="")

for (i in 1:sampleNum) {
    rownames(fr)[i] = strsplit(rownames(fr)[i],".",fixed=TRUE)[[1]][1]
    for (j in 1:frNum) {
        fr[i,j] = strsplit(fr[i,j],":",fixed=TRUE)[[1]][2]
    }
}
for (j in 1:frNum) {
    fr[,j] = as.numeric(fr[,j])
}

## apply PCA: scale. = TRUE is highly advisable, but default is FALSE. 
pca = prcomp(fr, center=TRUE, scale.=FALSE)



