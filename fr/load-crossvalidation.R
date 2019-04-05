##
## load cross validation results from an SVM run output
##
## HBB-0.2-0-80.2.8-2.crossvalidation.txt
## alpha=0.2 kappa=0 minsup=80 minsize=2 minlen=8 run=2
##

## these will be used by plot-crossvalidation.R as well
study = readline(prompt="Study (ex. SCD): ")
graph = readline(prompt="Graph (ex. HBB): ")
requireHomozygous = as.logical(readline(prompt="Require homozygous calls (TRUE/FALSE): "))

## standard
alphaValues = c("0.2","0.5","0.8","1.0")
kappaValues = c(0,1,2,3,12,48,192,768)
minsupValues = c(1,2,4,10,20,50,100)
minsizeValues = c(1,2,4,10,20,50,100)
minlenValues = c(1,2,4,10,20,50,100,500)

## our resulting data frame
results = data.frame(graph=character(), alpha=numeric(), kappa=numeric(),
                     minsup=numeric(), minsize=numeric(), minlen=numeric(), 
                     maxIndex=numeric(), C=numeric(), gamma=numeric(), nrFold=numeric(),
                     cases=numeric(), controls=numeric(), caseFails=numeric(), controlFails=numeric(),
                     TPR=numeric(), FPR=numeric())

## loop through all the files, adding to results
for (alpha in alphaValues) {
    for (kappa in kappaValues) {
        prefix = paste(graph,"-",alpha,"-",kappa, sep="")
        for (minsup in minsupValues) {
            for (minsize in minsizeValues) {
                for (minlen in minlenValues) {
                    for (run in 1:3) {
                        filename = paste(prefix,"-",minsup,".",minsize,".",minlen,"-",run,".crossvalidation.txt", sep="")
                        filesize = file.size(filename)
                        if (!is.na(filesize) && filesize>0) {
                            cat(filename, "\n")
                            ## read header
                            ## HBB-0.2-0-10.4.1.svm.scale.txt 28 0.03125 0.125 10 40 40 2 0
                            con = file(filename,"r")
                            header = readLines(con, n=1)
                            close(con)
                            parts = strsplit(header, "\t", fixed=TRUE)[[1]]
                            svmfile = parts[1]
                            maxIndex = as.numeric(parts[2])
                            C = as.numeric(parts[3])
                            gamma = as.numeric(parts[4])
                            nrFold = as.numeric(parts[5])
                            ## cases and controls are PATH counts, not sample counts!
                            cases = as.numeric(parts[6])
                            controls = as.numeric(parts[7])
                            ## read data to merge the path calls per sample
                            ## HG00122.1 ctrl true
                            df = read.table(filename, skip=1, colClasses=c("character", "character", "logical"))
                            colnames(df) = c("Path", "Label", "Correct")
                            caseFails = 0
                            controlFails = 0
                            sample0 = ""
                            correct0 = FALSE
                            for (i in 1:length(df$Path)) {
                                path = df$Path[i]
                                label = df$Label[i]
                                correct = df$Correct[i]
                                sample = strsplit(path, ".", fixed=TRUE)[[1]][1]
                                if (sample==sample0) {
                                    ## second path for this sample, so make the combined call
                                    if (requireHomozygous) {
                                        if (label=="case") {
                                            ## HOM case: both paths must be called "case"
                                            correct = correct && correct0
                                        } else {
                                            ## HOM control: either path may be called "ctrl"
                                            correct = correct || correct0
                                        }
                                    } else {
                                        if (label=="case") {
                                            ## HET case: either path may be called "case"
                                            correct = correct || correct0
                                        } else {
                                            ## HET control: both paths must be called "ctrl"
                                            correct = correct && correct0
                                        }
                                    }
                                    ## increment the case/control failure counts per SAMPLE
                                    if (!correct) {
                                        if (label=="case") {
                                            caseFails = caseFails + 1
                                        } else if (label=="ctrl") {
                                            controlFails = controlFails + 1
                                        }
                                    }
                                } else {
                                    ## first path for this sample
                                    sample0 = sample
                                    correct0 = correct
                                }
                            }
                            ## standard classifier rates
                            TPR = (cases/2-caseFails)/(cases/2)
                            FPR = (controlFails)/(controls/2)
                            ## append to the results dataframe
                            df = data.frame(graph=graph, alpha=as.numeric(alpha), kappa=kappa,
                                            minsup=minsup, minsize=minsize, minlen=minlen,
                                            maxIndex=maxIndex, C=C, gamma=gamma, nrFold=nrFold,
                                            cases=cases, controls=controls, caseFails=caseFails, controlFails=controlFails,
                                            TPR=TPR, FPR=FPR)
                            results = rbind(results, df)
                        }
                    }
                }
            }
        }
    }
}
