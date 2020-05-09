package org.ncgr.datastore;

import java.io.File;
import java.net.URI;

import org.irods.jargon.core.pub.io.IRODSFile;

/**
 * Simple File wrapper providing methods that discern what sort of LIS file it is.
 */
public class LISFile extends File {

    private boolean isDirectory = false;
    private boolean isFile = false;
    private long length = 0;

    public LISFile(File file) {
        super(file.getAbsolutePath());
        isDirectory = file.isDirectory();
        isFile = file.isFile();
        length = file.length();
    }

    public LISFile(IRODSFile file) {
        super(file.getAbsolutePath());
        isDirectory = file.isDirectory();
        isFile = file.isFile();
        length = file.length();
    }

    // isDirectory(), isFile() are both false with this
    public LISFile(File parent, String child) {
        super(parent, child);
    }
    
    // isDirectory(), isFile() are both false with this
    public LISFile(String pathname) {
        super(pathname);
    }

    // isDirectory(), isFile() are both false with this
    public LISFile(String parent, String child) {
        super(parent, child);
    }

    // isDirectory(), isFile() are both false with this
    public LISFile(URI uri) {
        super(uri);
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public boolean isFile() {
        return isFile;
    }

    public long length() {
        return length;
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

    /**
     * Pull the second-to-last suffix off the given string.
     */
    String getDirTypeSuffix(String str) {
        String[] parts = str.split("\\.");
        if (parts.length>2) {
            return parts[parts.length-2];
        } else {
            return null;
        }
    }        

    /**
     * Return a simple sting denoting the type of file that this is.
     */
    public String getFileType() {
        String type = "";
        if (isFasta()) {
            type = "FASTA";
            if (isHardMaskedFasta()) type += " (hardmasked)";
            if (isSoftMaskedFasta()) type += " (softmasked)";
            if (isUnmaskedFasta()) type += " (unmasked)";
            if (isCDSFasta()) type += " (CDS)";
            if (isCDSPrimaryTranscriptOnlyFasta()) type += " (CDS - primary transcript only)";
            if (isProteinFasta()) type += " (protein)";
            if (isProteinPrimaryTranscriptOnlyFasta()) type += " (protein - primary transcript only)";
            if (isTranscriptFasta()) type += " (transcript)";
            if (isTranscriptPrimaryTranscriptOnlyFasta()) type += " (transcript - primary transcript only)";
        } else if (isGFF()) {
            type = "GFF";
            if (isGeneGFF()) type += " (genes)";
            if (isGeneExonsGFF()) type += " (genes+exons)";
            if (isGeneModelsGFF()) type += " (gene models)";
        } else if (isReadme()) {
            type = "README";
        } else if (isExcel()) {
            type = "EXCEL";
        } else if (isHMP()) {
            type = "HMP";
        }
        return type;
    }

    /**
     * Return a simple string denoting the type of directory that this is.
     */
    public String getDirType() {
        String type = "";
        if (isGenomeDir()) {
            type = "GENOME";
        } else if (isAnnotationDir()) {
            type = "ANNOTATION";
        } else if (isDiversityDir()) {
            type = "DIVERSITY";
        } else if (isVariantDir()) {
            type = "VARIANT";
        } else if (isBACDir()) {
            type = "BAC";
        } else if (isMarkerDir()) {
            type = "MARKER";
        } else if (isSyntenyDir()) {
            type = "SYNTENY";
        } else if (isTranscriptomeDir()) {
            type = "TRANSCRIPTOME";
        }
        return type;
    }

    // file type getters

    public boolean isFasta() {
        return getName().endsWith("fa.gz") || getName().endsWith("fna.gz") || getName().endsWith("fa") || getName().endsWith("fna");
    }

    public boolean isHardMaskedFasta() {
        return isFasta() && getName().contains("hardmasked");
    }
    
    public boolean isSoftMaskedFasta() {
        return isFasta() && getName().contains("softmasked");
    }

    public boolean isUnmaskedFasta() {
        return isFasta() && !isHardMaskedFasta() && !isSoftMaskedFasta();
    }
    
    public boolean isCDSFasta() {
        return isFasta() && getName().contains("cds");
    }

    public boolean isCDSPrimaryTranscriptOnlyFasta() {
        return isFasta() && getName().contains("cds_primaryTranscriptOnly");
    }

    public boolean isProteinFasta() {
        return isFasta() && (getName().contains("protein") || getName().contains("pep"));
    }

    public boolean isProteinPrimaryTranscriptOnlyFasta() {
        return isFasta() && getName().contains("protein_primaryTranscriptOnly");
    }
    
    public boolean isTranscriptFasta() {
        return isFasta() && getName().contains("transcript");
    }
    
    public boolean isTranscriptPrimaryTranscriptOnlyFasta() {
        return isFasta() && getName().contains("transcript_primaryTranscriptOnly");
    }

    public boolean isGFF() {
        return getName().endsWith("gff3.gz") || getName().endsWith("gff.gz") || getName().endsWith("gff3") || getName().endsWith("gff");
    }
    
    public boolean isGeneGFF() {
        return getName().endsWith("gene.gff3.gz") || getName().endsWith("gene.gff.gz") || getName().endsWith("gene.gff3") || getName().endsWith("gene.gff");
    }

    public boolean isGeneExonsGFF() {
        return getName().endsWith("gene_exons.gff3.gz") || getName().endsWith("gene_exons.gff.gz") || getName().endsWith("gene_exons.gff3") || getName().endsWith("gene_exons.gff");
    }

    public boolean isGeneModelsGFF() {
        return getName().endsWith("gene_models.gff3.gz") || getName().endsWith("gene_models.gff.gz") || getName().endsWith("gene_models.gff3") || getName().endsWith("gene_models.gff");
    }

    public boolean isReadme() {
        return getName().startsWith("README");
    }

    public boolean isExcel() {
        return getName().endsWith("xlsx.gz") || getName().endsWith("xlsx") || getName().endsWith("xls.gz") || getName().endsWith("xls");
    }

    public boolean isHMP() {
        return getName().endsWith("hmp.gz") || getName().endsWith("hmp");
    }

    // dir type getters

    public boolean isGenomeDir() {
        String suffix = getDirTypeSuffix(getName());
        if (suffix==null) {
            return false;
        } else if (suffix.contains("gnm")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isAnnotationDir() {
        String suffix = getDirTypeSuffix(getName());
        if (suffix==null) {
            return false;
        } else if (suffix.contains("ann")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isDiversityDir() {
        String suffix = getDirTypeSuffix(getName());
        if (suffix==null) {
            return false;
        } else if (suffix.contains("div")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isVariantDir() {
        String suffix = getDirTypeSuffix(getName());
        if (suffix==null) {
            return false;
        } else if (suffix.contains("var")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isBACDir() {
        String suffix = getDirTypeSuffix(getName());
        if (suffix==null) {
            return false;
        } else if (suffix.contains("bac")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isMarkerDir() {
        String suffix = getDirTypeSuffix(getName());
        if (suffix==null) {
            return false;
        } else if (suffix.contains("mrk")) {
            return true;
        } else {
            return false;
        }
    }


    public boolean isSyntenyDir() {
        String suffix = getDirTypeSuffix(getName());
        if (suffix==null) {
            return false;
        } else if (suffix.contains("syn")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isTranscriptomeDir() {
        String suffix = getDirTypeSuffix(getName());
        if (suffix==null) {
            return false;
        } else if (suffix.contains("tcp")) {
            return true;
        } else {
            return false;
        }
    }

}
