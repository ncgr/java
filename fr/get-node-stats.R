##
## get the case/control stats per node including HET and HOM delineation
##

source("pathName.R")
source("pathLabel.R")
source("pathGenotype.R")

##                N1 N2 N3 N4 N5 N6 N7 N8 N9 N10 N11 N12 N13 N14 N15 N16 N17 N18 Label
## X339636.1.case  1  0  1  1  0  1  1  0  1   1   0   1   1   0   1   1   1   0 case
## X508072.1.ctrl  1  0  1  1  0  1  1  0  1   1   0   1   1   0   1   1   1   0 ctrl

## coalesce the paths into sample names
sampleNames = unique(pathName(rownames(paths)))

## casePaths, ctrlPaths, caseRef, caseHet, caseHom, ctrlRef, ctrlHet, ctrlHom
nCasePaths = nrow(paths[paths$Label=="case",])
nCtrlPaths = nrow(paths[paths$Label=="ctrl",])
nodeStats = data.frame()
for (node in 1:length(colnames(paths))) {
    if (colnames(paths)[node]!="Label") {
        casePaths = sum(paths[paths$Label=="case",node])
        ctrlPaths = sum(paths[paths$Label=="ctrl",node])
        nodeStats[i,"casePaths"] = casePaths
        nodeStats[i,"ctrlPaths"] = ctrlPaths
        if (nCasePaths>0 && nCtrlPaths>0 && ctrlPaths>0) {
            pathOR = (casePaths/nCasePaths) / (ctrlPaths/nCtrlPaths)
            nodeStats[i,"pathOR"] = pathOR
            nodeStats[i, "log10PathOR"] = log10(pathOR)
        }
        nodeStats[node, "pathP"] = as.numeric(fisher.test(matrix(c(casePaths,nCasePaths,ctrlPaths,nCtrlPaths), nrow=2))["p.value"])
        ## accumulate stats per sample; takes some time!
        caseHet = 0
        ctrlHet = 0
        caseHom = 0
        ctrlHom = 0
        for (j in 1:length(sampleNames)) {
            sampleName = sampleNames[j]
            case = is.na(paths[paste(sampleName,".0.ctrl",sep=""),1])
            if (case) {
                path.0 = paste(sampleName,".0.case",sep="")
                path.1 = paste(sampleName,".1.case",sep="")
                if ((paths[path.0,node]+paths[path.1,node])==2) {
                    caseHom = caseHom + 1
                } else if ((paths[path.0,node]+paths[path.1,node])==1) {
                    caseHet = caseHet + 1
                }
            } else {
                path.0 = paste(sampleName,".0.ctrl",sep="")
                path.1 = paste(sampleName,".1.ctrl",sep="")
                if ((paths[path.0,node]+paths[path.1,node])==2) {
                    ctrlHom = ctrlHom + 1
                } else if ((paths[path.0,node]+paths[path.1,node])==1) {
                    ctrlHet = ctrlHet + 1
                }
            }
        }
        nodeStats[node,"caseHet"] = caseHet
        nodeStats[node,"caseHom"] = caseHom
        nodeStats[node,"ctrlHet"] = ctrlHet
        nodeStats[node,"ctrlHom"] = ctrlHom
        nodeStats[node, "sampleP"] = as.numeric(fisher.test(matrix(c(caseHet,caseHom,ctrlHet,ctrlHom), nrow=2))["p.value"])
    }
}

                                              
