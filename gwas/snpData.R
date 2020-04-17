library(jsonlite)
##
## query SNP data from myvariant.info service
##
## colnames(json$hits):
## _id  _score cadd chrom cosmic dbnsfp dbsnp exac exac_nontcga geno2mp gnomad_exome gnomad_genome hg19 mutdb observed snpeff vcf  
##
## colnames(json$hits$dbsnp):
## alleles alt chrom dbsnp_build gene hg19 ref rsid vartype
##
snpData = function(rsId) {
    url = paste("http://myvariant.info/v1/query?q=", rsId, sep="")
    return(fromJSON(url))
}
