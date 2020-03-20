##
## return a data frame containing the case/control stats per node with optional HET and HOM delineation
## paths:
##                N1 N2 N3 N4 N5 N6 N7 N8 N9 N10 N11 N12 N13 N14 N15 N16 N17 N18 Label
## X339636.1.case  1  0  1  1  0  1  1  0  1   1   0   1   1   0   1   1   1   0 case
## X508072.1.ctrl  1  0  1  1  0  1  1  0  1   1   0   1   1   0   1   1   1   0 ctrl

source("pathName.R")
source("pathLabel.R")
source("pathGenotype.R")

getNodeStats = function(doHetHom=FALSE) {
    nodeStats = data.frame()
    
    ## casePaths, ctrlPaths, caseRef, caseHet, caseHom, ctrlRef, ctrlHet, ctrlHom
    nCasePaths = nrow(paths[paths$Label=="case",])
    nCtrlPaths = nrow(paths[paths$Label=="ctrl",])
    for (n in 1:length(colnames(paths))) {
        if (colnames(paths)[n]!="Label") {
            casePaths = sum(paths[paths$Label=="case",n])
            ctrlPaths = sum(paths[paths$Label=="ctrl",n])
            nodeStats[n,"casePaths"] = casePaths
            nodeStats[n,"ctrlPaths"] = ctrlPaths
            if (nCasePaths>0 && nCtrlPaths>0 && ctrlPaths>0) {
                pathOR = (casePaths/nCasePaths) / (ctrlPaths/nCtrlPaths)
                nodeStats[n,"pathOR"] = pathOR
                nodeStats[n, "log10PathOR"] = log10(pathOR)
            }
            nodeStats[n, "pathP"] = as.numeric(fisher.test(matrix(c(casePaths,nCasePaths,ctrlPaths,nCtrlPaths), nrow=2))["p.value"])
            if (doHetHom) {
                ## accumulate stats per sample; takes some time!
                ## TODO: SPEED THIS UP!
                caseHet = 0
                ctrlHet = 0
                caseHom = 0
                ctrlHom = 0
                ## coalesce the paths into sample names
                sampleNames = unique(pathName(rownames(paths)))
                for (s in 1:length(sampleNames)) {
                    sampleName = sampleNames[s]
                    case = is.na(paths[paste(sampleName,".0.ctrl",sep=""),1])
                    if (case) {
                        path.0 = paste(sampleName,".0.case",sep="")
                        path.1 = paste(sampleName,".1.case",sep="")
                        if ((paths[path.0,n]+paths[path.1,n])==2) {
                            caseHom = caseHom + 1
                        } else if ((paths[path.0,n]+paths[path.1,n])==1) {
                            caseHet = caseHet + 1
                        }
                    } else {
                        path.0 = paste(sampleName,".0.ctrl",sep="")
                        path.1 = paste(sampleName,".1.ctrl",sep="")
                        if ((paths[path.0,n]+paths[path.1,n])==2) {
                            ctrlHom = ctrlHom + 1
                        } else if ((paths[path.0,n]+paths[path.1,n])==1) {
                            ctrlHet = ctrlHet + 1
                        }
                    }
                }
                nodeStats[n,"caseHet"] = caseHet
                nodeStats[n,"caseHom"] = caseHom
                nodeStats[n,"ctrlHet"] = ctrlHet
                nodeStats[n,"ctrlHom"] = ctrlHom
                nodeStats[n, "sampleP"] = as.numeric(fisher.test(matrix(c(caseHet,caseHom,ctrlHet,ctrlHom), nrow=2))["p.value"])
            }
        }
    }
    return(nodeStats)
}
