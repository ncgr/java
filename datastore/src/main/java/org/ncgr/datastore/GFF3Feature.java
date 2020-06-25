package org.ncgr.datastore;

import java.util.Map;

import org.biojava.nbio.genome.parsers.gff.Feature;
import org.biojava.nbio.genome.parsers.gff.Location;

/**
 * Extends Feature to support strand in GFF3 output.
 */
public class GFF3Feature extends Feature {

    /**
     * Construct from a Feature
     */
    public GFF3Feature(Feature feature) {
	super(feature);
    }
    
    /**
     * Construct from the items in a Feature constructor
     */
    public GFF3Feature(String seqname, String source, String type, Location location, Double score, int frame, String attributes) {
	super(seqname, source, type, location, score, frame, attributes);
    }
    
    /**
     * Construct from a GFF3 record string
     * Fields must be tab-separated. Also, all but the final field in each feature line must contain a value; "empty" columns should be denoted with a '.'
     *
     * 0 seqid - name of the chromosome or scaffold
     * 1 source - name of the program that generated this feature, or the data source (database or project name)
     * 2 type - type of feature. Must be a term or accession from the SOFA sequence ontology
     * 3 start - Start position of the feature, with sequence numbering starting at 1.
     * 4 end - End position of the feature, with sequence numbering starting at 1.
     * 5 score - A floating point value or '.'.
     * 6 strand - defined as + (forward) or - (reverse).
     * 7 phase - One of '0', '1', '2' or '.'.
     * 8 attributes
     */
    public GFF3Feature(String gff3Record) {
	super(getFeature(gff3Record));
    }

    /**
     * Return a Feature given a GFF3 record string. Location contains strand.
     */
    private static Feature getFeature(String gff3Record) {
    	String[] fields = gff3Record.split("\\t");
	String seqname = fields[0];
	String source = fields[1];
	String type = fields[2];
	int start = Integer.parseInt(fields[3]);
	int end = Integer.parseInt(fields[4]);
	char strand = fields[6].charAt(0);
	Location location = Location.fromBio(start, end, strand);
	double score = 0.0;
	if (!fields[5].equals(".")) score = Double.parseDouble(fields[5]);
	int frame = 0;
	if (!fields[7].equals(".")) frame = Integer.parseInt(fields[7]);
	String attributes = fields[8];
	return new Feature(seqname, source, type, location, score, frame, attributes);
    }

    /**
     * Return a GFF3 string representation including a strand column.
     *
     * 0 seqid - name of the chromosome or scaffold
     * 1 source - name of the program that generated this feature, or the data source (database or project name)
     * 2 type - type of feature. Must be a term or accession from the SOFA sequence ontology
     * 3 start - Start position of the feature, with sequence numbering starting at 1.
     * 4 end - End position of the feature, with sequence numbering starting at 1.
     * 5 score - A floating point value or '.'.
     * 6 strand - defined as + (forward) or - (reverse).
     * 7 phase - One of '0', '1', '2' or '.'.
     * 8 attributes
     *
     * @override
     */
    public String toString() {
	String rec = "";
	rec += seqname() + "\t";
	rec += source() + "\t";
	rec += type() + "\t";
	rec += location().bioStart() + "\t";
	rec += location().bioEnd() + "\t";
	rec += score() + "\t";
	rec += location().bioStrand() + "\t";
	rec += frame() + "\t";
	rec += attributes();
	return rec;
    }
}
