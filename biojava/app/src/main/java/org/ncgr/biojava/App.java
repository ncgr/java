package org.ncgr.biojava;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;

/**
 * Test biojava parsing of FASTA files.
 */
public class App {

    public static void main(String[] args) throws IOException {
        File file = new File(args[0]);
        Map<String,DNASequence> sequences = FastaReaderHelper.readFastaDNASequence(file);
        for (String key : sequences.keySet()) {
            DNASequence seq = sequences.get(key);
            System.out.println("key:\t"+key);
            System.out.println("getAccession():\t"+seq.getAccession());
            System.out.println("getDescription():\t"+seq.getDescription());
            System.out.println("getComments():\t"+seq.getComments());
        }
    }

}
