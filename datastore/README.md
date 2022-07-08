# org.ncgr.datastore

## org.ncgr.datastore.Readme
Java class representation of a README file, along with a method to load it from YAML file.

## org.ncgr.datastore.CollectionValidator

The abstract class extended by collection-specific validators:
- AnnotationCollectionValidator
- ExpressionCollectionValidator
- GeneticCollectionValidator
- GenomeCollectionValidator
- MapCollectionValidator
- MarkerCollectionValidator

The main method of each provides output in markdown syntax (and color coded on the terminal), such as

--------------------------------------------------------------------------------
### Validating cajca collection ICPL87119.gnm1.ann1.expr.4H6Z
### INVALID: Required file type obo.tsv.gz is not present in ICPL87119.gnm1.ann1.expr.4H6Z
--------------------------------------------------------------------------------
### Validating cicar collection ICC4958.gnm2.ann1.expr.4Y7B
### INVALID: Required file type obo.tsv.gz is not present in ICC4958.gnm2.ann1.expr.4Y7B
--------------------------------------------------------------------------------
### Validating glyma collection Wm82.gnm2.ann1.expr.G7ZY
 - glyma.Wm82.gnm2.ann1.expr.G7ZY.samples.tsv.gz (no validation)
 - glyma.Wm82.gnm2.ann1.expr.G7ZY.obo.tsv.gz (no validation)
 - glyma.Wm82.gnm2.ann1.expr.G7ZY.values.tsv.gz
### VALID
--------------------------------------------------------------------------------
### Validating medtr collection A17_HM341.gnm4.ann2.expr.RLZY
 - medtr.A17_HM341.gnm4.ann2.expr.RLZY.samples.tsv.gz (no validation)
 - medtr.A17_HM341.gnm4.ann2.expr.RLZY.obo.tsv.gz (no validation)
 - medtr.A17_HM341.gnm4.ann2.expr.RLZY.values.tsv.gz
### VALID
--------------------------------------------------------------------------------
### Validating phavu collection G19833.gnm1.ann1.expr.4ZDQ
 - phavu.G19833.gnm1.ann1.expr.4ZDQ.samples.tsv.gz (no validation)
 - phavu.G19833.gnm1.ann1.expr.4ZDQ.obo.tsv.gz (no validation)
 - phavu.G19833.gnm1.ann1.expr.4ZDQ.values.tsv.gz
### VALID
--------------------------------------------------------------------------------
### Validating vigun collection IT97K-499-35.gnm1.ann1.expr.BWH5
 - vigun.IT97K-499-35.gnm1.ann1.expr.BWH5.samples.tsv.gz (no validation)
 - vigun.IT97K-499-35.gnm1.ann1.expr.BWH5.obo.tsv.gz (no validation)
 - vigun.IT97K-499-35.gnm1.ann1.expr.BWH5.values.tsv.gz
### VALID
