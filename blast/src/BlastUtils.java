package org.ncgr.blast;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;
import org.biojava.nbio.core.sequence.io.FastaWriterHelper;

/**
 * A set of static utility methods for running Blast and returning BlastOutput.
 *
 * @author Sam Hokin
 */
public class BlastUtils {

    /**
     * Run blastn with some fixed parameters, taking two sequences and word size as input.
     *
     * @param subjectFilename the name of the FASTA file containing the subject sequence(s)
     * @param queryFilename the name of the FASTA file containing the query sequence(s)
     * @param parameters a Map of parameter names (without the dash) and values, both represented as Strings, e.g. "word_size":"8"; outfmt, out, subject and query will be ignored.
     */
    public static BlastOutput runBlastn(String subjectFilename, String queryFilename, Map<String,String> parameters) throws IOException, InterruptedException, JAXBException {
        Runtime rt = Runtime.getRuntime();
        String filename = "/tmp/blastutils_"+System.currentTimeMillis();
        String command = "blastn -outfmt 5 -subject "+subjectFilename+" -query "+queryFilename;
        for (String parameter : parameters.keySet()) {
            parameter = parameter.replace("-",""); // remove dash as a courtesy
            String value = parameters.get(parameter);
            if (!parameter.equals("outfmt") &&
                !parameter.equals("out") &&
                !parameter.equals("subject") &&
                !parameter.equals("query")) {
                command += " -"+parameter+" "+value;
            }
            // indicate the query range that we're searching in the file name
            if (parameter.equals("query_loc")) filename += "_"+value;
        }
        filename += ".xml";
        command += " -out "+filename;
        Process pr = rt.exec(command);
        pr.waitFor();
        if (pr.exitValue()!=0) {
            System.err.println("Aborting: blastn returned exit value "+pr.exitValue());
            System.exit(pr.exitValue());
        }
        return getBlastOutput(filename);
    }

    /**
     * Return a BlastOutput from a given XML filename
     *
     * @param filename the name of the XML file containing blast output
     * @return a BlastOutput instance
     */
    public static BlastOutput getBlastOutput(String filename) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(BlastOutput.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        BlastOutput blastOutput = (BlastOutput) jaxbUnmarshaller.unmarshal(new File(filename));
        return blastOutput;
    }

    /**
     * Return a BlastOutput from an XML file given by a URL
     *
     * @param url the URL of the XML file
     * @return a BlastOutput instance
     */
    public static BlastOutput getBlastOutput(URL url) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(BlastOutput.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        BlastOutput blastOutput = (BlastOutput) jaxbUnmarshaller.unmarshal(url);
        return blastOutput;
    }
    
    /**
     * Return a double score for an input DNA sequence equal to the log of the inverse of the probability of each letter being produced randomly.
     * The probabilities for each are set at the top. Longer sequences naturally get much larger scores.
     *
     * @param  sequence a string sequence of DNA letters
     * @return an integer score
     */
    public static double scoreDNASequence(String sequence) {
        char[] letters = { 'A',  'T',  'C',  'G',  'W',  'K',  'R',  'M',  'Y',  'S',  'N'  };
        double[] probs = { 0.35, 0.35, 0.15, 0.15, 1.00, 0.50, 0.50, 0.50, 0.50, 0.30, 1.00 };
        char[] chars = sequence.toCharArray();
        double totalProb = 1.00;
        for (int i=0; i<chars.length; i++) {
            for (int j=0; j<letters.length; j++) {
                if (chars[i]==letters[j]) {
                    totalProb *= probs[j];
                    j = chars.length;
                }
            }
        }
        return -Math.log10(totalProb);
    }

    /**
     * Return a combined sequence from two input DNA sequences, where mismatches are represented by their standard IUB/IUPAC codes if useIUB=true,
     * or simply 'N' if useIUB=false. Returns null if sequences are not same length.
     * 
     * @param seq1 string sequence of DNA letters
     * @param seq2 string sequence of DNA letters
     * @param useIUB boolean indicating whether to use IUB/IUPAC codes for mismatches; only N is used if false
     * @return combined a string sequence representing seq1 and seq2 with mismatches represented by N; null if not same length
     */
    public static String combineDNASequences(String seq1, String seq2, boolean useIUB) {
        if (seq1.length()!=seq2.length()) {
            return null;
        }
        char[] seq1Chars = seq1.toCharArray();
        char[] seq2Chars = seq2.toCharArray();
        String combined = "";
        for (int i=0; i<seq1Chars.length; i++) {
            if ( seq1Chars[i]==seq2Chars[i] ) {
                combined += seq1Chars[i];  // identical
            } else if ( useIUB && (seq1Chars[i]=='A'||seq1Chars[i]=='G') && (seq2Chars[i]=='A'||seq2Chars[i]=='G') ) {
                combined += 'R';           // puRine
            } else if ( useIUB && (seq1Chars[i]=='C'||seq1Chars[i]=='T') && (seq2Chars[i]=='C'||seq2Chars[i]=='T') ) {
                combined += 'Y';           // pYrimidines
            } else if ( useIUB && (seq1Chars[i]=='G'||seq1Chars[i]=='T') && (seq2Chars[i]=='G'||seq2Chars[i]=='T') ) {
                combined += 'K';           // Ketones
            } else if ( useIUB && (seq1Chars[i]=='A'||seq1Chars[i]=='C') && (seq2Chars[i]=='A'||seq2Chars[i]=='C') ) {
                combined += 'M';           // aMino groups
            } else if ( useIUB && (seq1Chars[i]=='C'||seq1Chars[i]=='G') && (seq2Chars[i]=='C'||seq2Chars[i]=='G') ) {
                combined += 'S';           // Strong interaction
            } else if ( useIUB && (seq1Chars[i]=='A'||seq1Chars[i]=='T') && (seq2Chars[i]=='A'||seq2Chars[i]=='T') ) {
                combined += 'W';           // Weak interaction
            } else if ( useIUB && (seq1Chars[i]!='A') && (seq2Chars[i]!='A') ) {
                combined += 'B';
            } else if ( useIUB && (seq1Chars[i]!='C') && (seq2Chars[i]!='C') ) {
                combined += 'D';
            } else if ( useIUB && (seq1Chars[i]!='G') && (seq2Chars[i]!='G') ) {
                combined += 'H';
            } else if ( useIUB && (seq1Chars[i]!='T') && (seq2Chars[i]!='T') ) {
                combined += 'V';
            } else {
                combined += 'N';
            }
        }
        return combined;
    }

    /**
     * Read a Blast-generated XML file (-outfmt 5) and spit out the contents.
     *
     * @param filepath the full path of the blast XML file
     */
    public static void readBlastXML(String filepath) throws JAXBException {

            File file = new File(filepath);
            JAXBContext jaxbContext = JAXBContext.newInstance(BlastOutput.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            
            BlastOutput blastOutput = (BlastOutput) jaxbUnmarshaller.unmarshal(file);
            System.out.println("======== BlastOutput ========-");
            System.out.println("db="+blastOutput.getBlastOutputDb());
            System.out.println("program="+blastOutput.getBlastOutputProgram());
            System.out.println("queryDef="+blastOutput.getBlastOutputQueryDef());
            System.out.println("queryID="+blastOutput.getBlastOutputQueryID());
            System.out.println("queryLen="+blastOutput.getBlastOutputQueryLen());
            System.out.println("querySeq="+blastOutput.getBlastOutputQuerySeq());
            System.out.println("outputReference="+blastOutput.getBlastOutputReference());
            System.out.println("outputVersion="+blastOutput.getBlastOutputVersion());

            BlastOutputMbstat mbstat = blastOutput.getBlastOutputMbstat();
            System.out.println("======== Statistics ========");
            if (mbstat==null) {
                System.out.println("getBlastOutputMbstat() returned null.");
            } else {
                Statistics stats = mbstat.getStatistics();
                if (stats==null) {
                    System.out.println("getStatistics() returned null.");
                } else {
                    System.out.println("dblen="+stats.getStatisticsDbLen());
                    System.out.println("dbnum="+stats.getStatisticsDbNum());
                    System.out.println("effspace="+stats.getStatisticsEffSpace());
                    System.out.println("entropy="+stats.getStatisticsEntropy());
                    System.out.println("hsplen="+stats.getStatisticsHspLen());
                    System.out.println("kappa="+stats.getStatisticsKappa());
                    System.out.println("lambda="+stats.getStatisticsLambda());
                }
            }

            BlastOutputParam param = blastOutput.getBlastOutputParam();
            System.out.println("======== Parameters ========");
            if (param==null) {
                System.out.println("getBlastOutputParam() returned null.");
            } else {
                Parameters params = param.getParameters();
                if (params==null) {
                    System.out.println("getParameters() returned null.");
                } else {
                    System.out.println("entrezQuery="+params.getParametersEntrezQuery());
                    System.out.println("expect="+params.getParametersExpect());
                    System.out.println("filter="+params.getParametersFilter());
                    System.out.println("gapExtend="+params.getParametersGapExtend());
                    System.out.println("gapOpen="+params.getParametersGapOpen());
                    System.out.println("include="+params.getParametersInclude());
                    System.out.println("matrix="+params.getParametersMatrix());
                    System.out.println("pattern="+params.getParametersPattern());
                    System.out.println("scMatch="+params.getParametersScMatch());
                    System.out.println("scMismatch="+params.getParametersScMismatch());
                }
            }

            BlastOutputIterations iterations = blastOutput.getBlastOutputIterations();
            System.out.println("======== Iterations ========");
            if (iterations==null) {
                System.out.println("getBlastOutputIterations() returned null.");
            } else {
                List<Iteration> iterationList = iterations.getIteration();
                if (iterationList==null) {
                    System.out.println("getIteration() returned null.");
                } else {
                    for (Iteration iteration : iterationList) {
                        
                        System.out.println("num="+iteration.getIterationIterNum());
                        System.out.println("message="+iteration.getIterationMessage());
                        System.out.println("queryDef="+iteration.getIterationQueryDef());
                        System.out.println("queryID="+iteration.getIterationQueryID());
                        System.out.println("queryLen="+iteration.getIterationQueryLen());
                        
                        IterationStat stat = iteration.getIterationStat();
                        Statistics stats = stat.getStatistics();
                        System.out.println("======== Stats ========");
                        if (stats==null) {
                            System.out.println("getStatistics() returned null.");
                        } else {
                            System.out.println("dblen="+stats.getStatisticsDbLen());
                            System.out.println("dbnum="+stats.getStatisticsDbNum());
                            System.out.println("effspace="+stats.getStatisticsEffSpace());
                            System.out.println("entropy="+stats.getStatisticsEntropy());
                            System.out.println("hsplen="+stats.getStatisticsHspLen());
                            System.out.println("kappa="+stats.getStatisticsKappa());
                            System.out.println("lambda="+stats.getStatisticsLambda());
                        }
                        
                        IterationHits hits = iteration.getIterationHits();
                        if (hits==null) {
                            System.out.println("getIterationHits() returned null.");
                        } else {
                            List<Hit> hitList = hits.getHit();
                            if (hitList==null) {
                                System.out.println("getHit() returned null.");
                            } else {
                                for (Hit hit : hitList) {
                                    System.out.println("======== HIT "+hit.getHitNum()+" ========");
                                    System.out.println("accession="+hit.getHitAccession());
                                    System.out.println("def="+hit.getHitDef());
                                    System.out.println("id="+hit.getHitId());
                                    System.out.println("len="+hit.getHitLen());
                                    HitHsps hsps = hit.getHitHsps();
                                    if (hsps==null) {
                                        System.out.println("getHitHsps() returned null.");
                                    } else {
                                        List<Hsp> hspList = hsps.getHsp();
                                        if (hspList==null) {
                                            System.out.println("getHsp() returned null.");
                                        } else {
                                            for (Hsp hsp : hspList) {
                                                System.out.println("-------- HSP "+hsp.getHspNum()+" --------");
                                                System.out.println("alignLen="+hsp.getHspAlignLen());
                                                System.out.println("bitScore="+hsp.getHspBitScore());
                                                System.out.println("density="+hsp.getHspDensity());
                                                System.out.println("evalue="+hsp.getHspEvalue());
                                                System.out.println("gaps="+hsp.getHspGaps());
                                                System.out.println("hitFrame="+hsp.getHspHitFrame());
                                                System.out.println("hseq\t"+hsp.getHspHseq()+"\t"+hsp.getHspHitFrom()+"-"+hsp.getHspHitTo());
                                                System.out.println("midline\t"+hsp.getHspMidline());
                                                System.out.println("qseq\t"+hsp.getHspQseq()+"\t"+hsp.getHspQueryFrom()+"-"+hsp.getHspQueryTo());
                                                System.out.println("identity="+hsp.getHspIdentity());
                                                System.out.println("patternFrom="+hsp.getHspPatternFrom());
                                                System.out.println("patternTo="+hsp.getHspPatternTo());
                                                System.out.println("positive="+hsp.getHspPositive());
                                                System.out.println("queryFrame="+hsp.getHspQueryFrame());
                                                System.out.println("score="+hsp.getHspScore());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

    }

    
}
