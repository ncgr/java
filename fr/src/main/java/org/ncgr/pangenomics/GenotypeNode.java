package org.ncgr.pangenomics;


/**
 * Container for a node with genotype information as well as the position on the genome.
 */
public class GenotypeNode implements Comparable {
    public String contig;
    public int position;
    public String genotype;

    public GenotypeNode(String contig, int position, String genotype) {
	this.contig = contig;
	this.position = position;
	this.genotype = genotype;
    }

    public String toString() {
	return contig+":"+position+"["+genotype+"]";
    }

    public boolean equals(Object o) {
	GenotypeNode that = (GenotypeNode) o;
	if (this.position==that.position) {
	    if (this.contig.equals(that.contig)) {
		return this.genotype.equals(that.genotype);
	    } else {
		return false;
	    }
	} else {
	    return false;
	}
    }

    public int compareTo(Object o) {
	GenotypeNode that = (GenotypeNode) o;
	if (this.contig.equals(that.contig)) {
	    if (this.position==that.position) {
		return this.genotype.compareTo(that.genotype);
	    } else {
		return Integer.compare(this.position, that.position);
	    }
	} else {
	    return this.contig.compareTo(that.contig);
	}
    }
}
