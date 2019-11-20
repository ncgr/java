package org.ncgr.gwas;

import java.io.IOException;

import org.biojava.nbio.genome.parsers.gff.FeatureList;
import org.biojava.nbio.genome.parsers.gff.GFF3Reader;
import org.biojava.nbio.genome.parsers.gff.Location;

/**
 * Loads a GFF file and provides handy methods.
 *
 * @author Sam Hokin
 */
public class GFFLoader {

    FeatureList featureList;

    /**
     * Construct from a GFF file.
     */
    public GFFLoader(String gffFilename) throws IOException {
        featureList = GFF3Reader.read(gffFilename);
    }

    /**
     * Search for a given location and return the FeatureList containing overlapping features.
     */
    public FeatureList search(String seqname, Location location) throws Exception {
        return featureList.selectOverlapping(seqname, location, true);
    }

    /**
     * Search for a given ID and return the FeatureList containing overlapping features.
     */
    public FeatureList searchID(String id) {
        return featureList.selectByAttribute("ID", id);
    }

}
