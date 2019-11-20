import java.io.*;

/**
 * Clean out redundant qseq or sseq lines from a BLAST outfmt 6 file.
 */
public class BlastCleaner {
    public static void main(String[] args) throws IOException {
        if (args.length!=2) {
            System.err.println("Usage: BlastCleaner blast.out qseq|sseq");
            System.exit(0);
        }
        String filename = args[0];
        boolean qseqUnique = args[1].equals("qseq");
        boolean sseqUnique = args[1].equals("sseq");
        if (!qseqUnique && !sseqUnique) {
            System.err.println("Usage: BlastCleaner blast.out qseq|sseq");
            System.exit(0);
        }
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line = null;
        String qseq = "";
        String sseq = "";
        while ((line=reader.readLine())!=null) {
            String[] parts = line.split("\t");
            String qseqNew = parts[0];
            String sseqNew = parts[1];
            if ((qseqUnique && !qseqNew.equals(qseq)) || (sseqUnique && !sseqNew.equals(sseq))) {
                System.out.println(line);
                qseq = qseqNew;
                sseq = sseqNew;
            }
        }
    }
}
