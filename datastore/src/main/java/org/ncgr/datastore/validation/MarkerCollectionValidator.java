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
     * Construct from a /markers/ directory
     */
    public MarkerCollectionValidator(String dirString) {
        super(dirString);
        requiredFileTypes = Arrays.asList("gff3.gz");
    }

    public static void main(String[] args) {
        if (args.length!=1) {
            System.err.println("Usage: MarkerCollectionValidator [genome directory]");
            System.exit(1);
        }

        // construct our validator and check required files
        MarkerCollectionValidator validator = new MarkerCollectionValidator(args[0]);
        validator.validate();
        if (validator.isValid()) printIsValidMessage();
    }

    /**
     * Validate the current instance.
     */
    public void validate() {
        printHeader();
        try {
            checkRequiredFiles();
        } catch (ValidationException ex) {
            printErrorAndExit(ex.getMessage());
        }

        // genotyping_platform is required
        if (readme.genotyping_platform==null) {                                                                                                                                           
                throw new RuntimeException("README does not have required genotyping_platform key:value.");
        }

        // gff3.gz
        try {
            File file = getDataFile("gff3.gz");
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
                if (!hasValidSeqname(featureI)) {
                    printError(file.getName()+" record seqname is invalid:");
                    printError(featureI.toString());
                }
                if (!hasValidGenomicID(featureI)) {
                    printError(file.getName()+" record ID attribute is missing or invalid:");
                    printError(featureI.toString());
                }
                if (!valid) System.exit(1);
            }
        } catch (Exception ex) {
            printError(ex.getMessage());
        }
    }

}
