import org.mskcc.cbio.portal.stats.FisherExact;

/**
 * Test the FisherExact algorithm.
 */

public class FisherExactTest {
    public static void main(String[] args) {
        if (args.length!=4) {
            System.err.println("Usage: FisherExactTest a b c d");
            System.exit(0);
        }

        int a = Integer.parseInt(args[0]);
        int b = Integer.parseInt(args[1]);
        int c = Integer.parseInt(args[2]);
        int d = Integer.parseInt(args[3]);

        // initialize FisherExact with max a+b+c+d
        FisherExact fisherExact = new FisherExact(a+b+c+d);

        // do it
        System.out.println("cumulative P="+fisherExact.getCumlativeP​(a, b, c, d));
        System.out.println("left-tailed P="+fisherExact.getLeftTailedP​(a, b, c, d));
        System.out.println("P="+fisherExact.getP​(a, b, c, d));
        System.out.println("right-tailed P="+fisherExact.getRightTailedP​(a, b, c, d));
        System.out.println("two-tailed P="+fisherExact.getTwoTailedP​(a, b, c, d));
    }
}
