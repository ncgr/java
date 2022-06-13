package org.ncgr.zip;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.util.LinkedHashMap;

import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompound;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet;
import org.biojava.nbio.core.sequence.compound.NucleotideCompound;
import org.biojava.nbio.core.sequence.compound.AmbiguityDNACompoundSet;
import org.biojava.nbio.core.sequence.io.FastaReader;
import org.biojava.nbio.core.sequence.io.GenericFastaHeaderParser;
import org.biojava.nbio.core.sequence.io.DNASequenceCreator;
import org.biojava.nbio.core.sequence.io.ProteinSequenceCreator;
import org.biojava.nbio.core.util.InputStreamProvider;

/**
 * Static methods to read gzipped FASTA files which may include ambiguous DNA compound symbols.
 */
public class GZIPFastaReader {

    /**
     * Use FastaReader to read DNA sequences from a file, using AmbiguityDNACompoundSet to handle non-ATCG symbols.
     *
     * @param the FASTA file, which may be gzipped
     * @return a map of String identifiers to DNASequence objects
     */
    public static LinkedHashMap<String,DNASequence> readFastaDNASequence(File file) throws IOException {
        // automatically uncompress files using InputStreamProvider
        InputStreamProvider isp = new InputStreamProvider();
        InputStream inStream = isp.getInputStream(file);
        FastaReader<DNASequence, NucleotideCompound> fastaReader =
            new FastaReader<DNASequence, NucleotideCompound>(inStream,
                                                             new GenericFastaHeaderParser<DNASequence, NucleotideCompound>(),
                                                             new DNASequenceCreator(AmbiguityDNACompoundSet.getDNACompoundSet()));
        return fastaReader.process();
    }

    /**
     * Use FastaReader to read Protein sequences from a file, using AmbiguityDNACompoundSet to handle non-ATCG symbols.
     *
     * @param the FASTA file, which may be gzipped
     * @return a map of String identifiers to DNASequence objects
     */
    public static LinkedHashMap<String,ProteinSequence> readFastaProteinSequence(File file) throws IOException {
        // automatically uncompress files using InputStreamProvider
        InputStreamProvider isp = new InputStreamProvider();
        InputStream inStream = isp.getInputStream(file);
        FastaReader<ProteinSequence, AminoAcidCompound> fastaReader =
            new FastaReader<ProteinSequence, AminoAcidCompound>(inStream,
                                                                new GenericFastaHeaderParser<ProteinSequence, AminoAcidCompound>(),
                                                                new ProteinSequenceCreator(AminoAcidCompoundSet.getAminoAcidCompoundSet()));
        return fastaReader.process();
    }

}
