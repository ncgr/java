import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Update a VCF file to have a new ALT allele for a given variant ID.
 *
 * Specifically hardcoded to replace the HTT allele with n repeats. The ref has 19 repeats so we append n-19 repeats to the REF allele.
 * Also, the genotype is made homozygous to force use of the new ALT allele.
 * 
 * 4	3076603	rs374076986	CCAGCAG	C,CCAGCAGCAGCAGCAGCAG,CCAG	1023.32	PASS	.	GT:AD:DP:GQ:PL:PP	0/0:8,0,0,0:8:15:0,15,225,15,225,225,15,225,225,225:0,15,225,15,225,225,15,225,225,225
 *                              19      17,23,18
 */
public class VCFChanger {

    public static void main(String[] args) throws FileNotFoundException, IOException {

	if (args.length!=2) {
	    System.err.println("Usage: VCFChanger <vcf-file> <n-CAG-repeats>");
	    System.exit(1);
	}

	String filename = args[0];
	int repeats = Integer.parseInt(args[1]);

	// output the reduced header
	System.out.println("##fileformat=VCFv4.1");
	System.out.println("##ALT=<ID=NON_REF,Description=\"Represents any possible alternative allele at this location\">");
	System.out.println("##FORMAT=<ID=GT,Number=1,Type=String,Description=\"Genotype\">");
	System.out.println("##contig=<ID=4,length=191154276,assembly=b37>");
	System.out.println("##reference=file:///isilon/sequencing/GATK_resource_bundle/bwa_mem_0.7.5a_ref/human_g1k_v37_decoy.fasta");
	System.out.println("##source=CalculateGenotypePosteriors");
	System.out.println("##source=SelectVariants");
	
	BufferedReader reader = new BufferedReader(new FileReader(filename));
	String line = null;
	while ((line=reader.readLine())!=null) {
	    if (line.startsWith("#CHROM")) {
		// #CHROM line has this sample's ID
		System.out.println(line);
		continue;
	    } else if (line.startsWith("#")) {
		// skip other comments
		continue;
	    }
	    // #CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO	FORMAT	116386
	    String[] fields = line.split("\t");
	    String chrom = fields[0];
	    String pos = fields[1];
	    String id = fields[2];
	    String ref = fields[3];
	    String alt = fields[4];
	    String qual = fields[5];
	    String filter = fields[6];
	    String info = fields[7];
	    String format = fields[8];
	    String values = fields[9];
	    // munge to simpler format
	    chrom = chrom;
	    pos = pos;
	    id = id;
	    ref = ref;
	    alt = alt;
	    qual = "100";
	    filter = filter;
	    info = ".";
	    format = "GT";
	    String[] parts = values.split(":");
	    values = parts[0];
	    if (id.equals("rs374076986")) {
		ref = "CCAGCAG";
		alt = "C,CCAGCAGCAGCAGCAGCAG,CCAG";
		if (repeats==19) {
		    // ref
		    values = "0/0";
		} else if (repeats==17) {
		    // standard allele 1
		    values = "1/1";
		} else if (repeats==23) {
		    // standard allele 2
		    values = "2/2";
		} else if (repeats==18) {
		    // standard allele 3
		    values = "3/3";
		} else if (repeats>19) {
		    // add a fourth alt which will be the homozygous allele
		    alt += ",CCAGCAG"; // start with REF (19)
		    for (int i=19; i<repeats; i++) alt += "CAG";
		    values = "4/4";
		}
	    }
	    String newline = chrom+"\t"+pos+"\t"+id+"\t"+ref+"\t"+alt+"\t"+qual+"\t"+filter+"\t"+info+"\t"+format+"\t"+values;
	    System.out.println(newline);
	}
    }

}
