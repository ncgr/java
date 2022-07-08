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
 * Methods to validate an LIS Datastore /synteny/ collection.
 * We have to iterate over the files and validate each one.
 *
 * collection: Wm82.gnm2.syn.HXNY
 *             0     1    2    3 4     5      6    7    8    9
 * files:      glyma.Wm82.gnm2.x.aradu.V14167.gnm1.HXNY.gff3.gz
 *             glyma.Wm82.gnm2.x.araip.K30076.gnm1.HXNY.gff3.gz
 *             ...
 *
 * glyma.Wm82.gnm2.Gm01 DAGchainer syntenic_region 524166 873763 415.0 + . Name=aradu.V14167.gnm1.A03;matches=aradu.V14167.gnm1.A03:30052111..30725164;median_Ks=0.7867
 *
 */
public class SyntenyCollectionValidator extends CollectionValidator {

    private static final String TEMPFILE = "/tmp/synteny.gff3";

    /**
     * Construct from an genome directory
     */
    public SyntenyCollectionValidator(String dirString) {
        setVars(dirString);
    }

    public static void main(String[] args) {
        if (args.length!=1) {
            System.err.println("Usage: SyntenyCollectionValidator [genome directory]");
            System.exit(1);
        }

        // construct our validator
        SyntenyCollectionValidator validator = new SyntenyCollectionValidator(args[0]);
        validator.printHeader();
        
        // iterate over data files in dir
        for (File file : validator.dir.listFiles()) {
            if (file.getName().endsWith("gff3.gz")) {
                // 0      1       2    3 4      5       6    7    8    9
                // gensp1.Strain1.gnm1.x.gensp2.Strain2.gnm2.KEY4.gff3.gz
                boolean filenameOK = true;
                String[] parts = file.getName().split("\\.");
                String genspStrainGnm1 = parts[0] + "." + parts[1] + "." + parts[2];
                filenameOK = filenameOK && genspStrainGnm1.equals(validator.genspStrainGnm);    // match collection
                filenameOK = filenameOK && parts[3].equals("x");
                if (!filenameOK) {
                    validator.printErrorAndExit(file.getName()+" is incorrectly named. Format: gensp.Strain.gnm.x.gensp.Strain.gnm.KEY4.gff3.gz");
                }
                System.out.println(" - "+file.getName());
                // will check genspStrainGnm2 against Name and matches attributes
                String genspStrainGnm2 = parts[4] + "." + parts[5] + "." + parts[6];
                File tempfile = new File(TEMPFILE);
                tempfile.delete();
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(tempfile));
                    BufferedReader reader = GZIPBufferedReader.getReader(file);
                    String line = null;
                    while ( (line=reader.readLine())!=null ) {
                        writer.write(line);
                        writer.newLine();
                    }
                    writer.close();
                    // validate
                    FeatureList featureList = GFF3Reader.read(TEMPFILE);
                    for (FeatureI featureI : featureList) {
                        if (!validator.hasValidSeqname(featureI)) {
                            validator.printError(file.getName()+" has an invalid seqname:");
                            validator.printError(featureI.toString());
                        }
                        String name = validator.getAttribute(featureI, "Name");
                        if (!name.startsWith(genspStrainGnm2)) {
                            validator.printError(file.getName()+" record has incorrect format Name attribute:");
                            validator.printError(featureI.toString());
                        }
                        String matches = validator.getAttribute(featureI, "matches");
                        if (!matches.startsWith(genspStrainGnm2)) {
                            validator.printError(file.getName()+" record has incorrect format matches attribute:");
                            validator.printError(featureI.toString());
                        }
                        if (!validator.valid) break;
                    }
                } catch (Exception ex) {
                    validator.printError(ex.getMessage());
                }
            }
        }

        // valid!
        if (validator.valid) printIsValidMessage();
    }

}
    
