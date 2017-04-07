package org.ncgr.irods;

import java.io.File;
import java.net.URI;

/**
 * Simple File wrapper providing methods that discern what sort of LIS file it is.
 */
public class LISFile extends File {

    public LISFile(File file) {
        super(file.getAbsolutePath());
    }
    
    public LISFile(File parent, String child) {
        super(parent, child);
    }
    
    public LISFile(String pathname) {
        super(pathname);
    }

    public LISFile(String parent, String child) {
        super(parent, child);
    }

    public LISFile(URI uri) {
        super(uri);
    }

    public boolean isFasta() {
        return getName().endsWith("fa.gz");
    }

    public boolean isHardMaskedFasta() {
        return getName().endsWith("hardmasked.fa.gz");
    }

    public boolean isSoftMaskedFasta() {
        return getName().endsWith("softmasked.fa.gz");
    }

    public boolean isUnmaskedFasta() {
        return isFasta() && !isHardMaskedFasta() && !isSoftMaskedFasta();
    }
    
    public boolean isCDSFasta() {
        return getName().endsWith("cds.fa.gz");
    }

    public boolean isCDSPrimaryTranscriptOnlyFasta() {
        return getName().endsWith("cds_primaryTranscriptOnly.fa.gz");
    }

    public boolean isProteinFasta() {
        return getName().endsWith("protein.fa.gz") || getName().endsWith("pep.fa.gz");
    }

    public boolean isProteinPrimaryTranscriptOnlyFasta() {
        return getName().endsWith("protein_primaryTranscriptOnly.fa.gz");
    }
    
    public boolean isTranscriptFasta() {
        return getName().endsWith("transcript.fa.gz");
    }
    
    public boolean isTranscriptPrimaryTranscriptOnlyFasta() {
        return getName().endsWith("transcript_primaryTranscriptOnly.fa.gz");
    }

    public boolean isGFF() {
        return getName().endsWith("gff3.gz") || getName().endsWith("gff.gz");
    }
    
    public boolean isGeneGFF() {
        return getName().endsWith("gene.gff3.gz") || getName().endsWith("gene.gff.gz");
    }

    public boolean isGeneExonsGFF() {
        return getName().endsWith("gene_exons.gff3.gz") || getName().endsWith("gene_exons.gff.gz");
    }

    public boolean isGeneModelsGFF() {
        return getName().endsWith("gene_models.gff3.gz") || getName().endsWith("gene_models.gff.gz");
    }

    public boolean isReadme() {
        return getName().startsWith("README");
    }

    public boolean isGenomeDir() {
        String suffix = getSuffix(getName());
        if (suffix==null) {
            return false;
        } else if (suffix.startsWith("gnm")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isAnnotationDir() {
        String suffix = getSuffix(getName());
        if (suffix==null) {
            return false;
        } else if (suffix.startsWith("ann")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isDiversityDir() {
        String suffix = getSuffix(getName());
        if (suffix==null) {
            return false;
        } else if (suffix.startsWith("div")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isVariantDir() {
        String suffix = getSuffix(getName());
        if (suffix==null) {
            return false;
        } else if (suffix.startsWith("var")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isBACDir() {
        String suffix = getSuffix(getName());
        if (suffix==null) {
            return false;
        } else if (suffix.startsWith("bac")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isMarkerDir() {
        String suffix = getSuffix(getName());
        if (suffix==null) {
            return false;
        } else if (suffix.startsWith("mrk")) {
            return true;
        } else {
            return false;
        }
    }


    public boolean isSyntenyDir() {
        String suffix = getSuffix(getName());
        if (suffix==null) {
            return false;
        } else if (suffix.startsWith("syn")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isTranscriptomeDir() {
        String suffix = getSuffix(getName());
        if (suffix==null) {
            return false;
        } else if (suffix.startsWith("tcp")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Pull the suffix off the given string.
     */
    String getSuffix(String str) {
        String[] parts = str.split("\\.");
        if (parts.length>1) {
            return parts[parts.length-1];
        } else {
            return null;
        }
    }

}
