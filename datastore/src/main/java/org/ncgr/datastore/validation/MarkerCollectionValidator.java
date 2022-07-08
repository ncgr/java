package org.ncgr.datastore.validation;

import org.ncgr.zip.GZIPBufferedReader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.biojava.nbio.genome.parsers.gff.FeatureI;
import org.biojava.nbio.genome.parsers.gff.FeatureList;
import org.biojava.nbio.genome.parsers.gff.GFF3Reader;

/**
 * Methods to validate an LIS Datastore /markers/ collection.
 * IDs are a bit odd:

 * collection:      Wm82.gnm1.mrk.BARCSOYSSR
 * ID:        glyma.Wm82.gnm1.BARCSOYSSR_20_1323
 *
 * So ID validation only uses gensp.strain.gnm.
 */
public class MarkerCollectionValidator extends CollectionValidator {

    private static final String TEMPFILE = "/tmp/marker.gff3";

    /**
     * Construct from an genome directory
     */
    public MarkerCollectionValidator(String dirString) {
        requiredFileTypes = Arrays.asList("gff3.gz");
        setVars(dirString);
    }

    public static void main(String[] args) {
        if (args.length!=1) {
            System.err.println("Usage: MarkerCollectionValidator [genome directory]");
            System.exit(1);
        }

        // construct our validator and check required files
        MarkerCollectionValidator validator = new MarkerCollectionValidator(args[0]);
        validator.printHeader();
        validator.checkRequiredFiles();

        // gff3.gz
        if (validator.dataFileExists("gff3.gz")) {
            try {
                File file = validator.getDataFile("gff3.gz");
                System.out.println(" - "+file.getName());
                // uncompress the gff3.gz file
                File tempfile = new File(TEMPFILE);
                tempfile.delete();
                BufferedWriter writer = new BufferedWriter(new FileWriter(tempfile));
                BufferedReader reader = GZIPBufferedReader.getReader(file);
                String line = null;
                while ( (line=reader.readLine())!=null ) {
                    writer.write(line);
                    writer.newLine();
                }
                writer.close();
                // validate the uncompressed GFF3 file
                FeatureList featureList = GFF3Reader.read(TEMPFILE);
                for (FeatureI featureI : featureList) {
                    if (!validator.hasValidSeqname(featureI)) {
                        validator.printError(file.getName()+" has an invalid seqname:");
                        validator.printError(featureI.toString());
                    }
                    if (!validator.hasValidGenomicID(featureI)) {
                        validator.printError(file.getName()+" has a missing or invalid ID attribute:");
                        validator.printError(featureI.toString());
                    }
                    if (!validator.valid) System.exit(1);
                }
            } catch (Exception ex) {
                validator.printError(ex.getMessage());
            }
        }        

        // valid!
        if (validator.valid) printIsValidMessage();
    }

}
    
