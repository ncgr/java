package org.ncgr.gwas;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.math3.distribution.NormalDistribution;

/**
 * Class that calculates the Cochran-Armitage test for trend
 * on a 2xnumCols contingency table.  Used to estimate association
 * in genetic models of genotype data.
 *
 * Input data should be sorted by decreasing genotype frequency, REF/REF, REF/ALT, ALT/ALT
 * Additive association would then use weights = 0,1,2.
 * Simple allelic association would use weights = 0,1,1.
 *
 * From Data Algorithms by Mahmoud Parsian
 * Publisher: O'Reilly Media, Inc.
 * Release Date: July 2015
 * ISBN: 9781491906187A
 */
public class CochranArmitage {

    // weights, e.g. corresponding to additive/codominant model = 0,1,2,3,...
    int[] weights;

    int numRows = 2;
    int numCols = 0; // = weights.length

    // variables to hold variance, raw statistic, standardized statistic, and p-value
    double stat = 0.0;
    double standardStatistics = 0.0;
    double variance = 0.0;
    double pValue = -1.0; // range is 0.0 to 1.0 (-1.0 means undefined)

    // NormalDistribution class from Apache used to calculate p-values
    static NormalDistribution normDist = new NormalDistribution();

    /**
     * Initialize with weights, which will also set numCols.
     */
    public CochranArmitage(int[] weights) {
        this.weights = weights;
        this.numCols = weights.length;
    }

    /**
     * Computes the Cochran-Armitage test for trend for the passed contingency table which matches numRows and numCols
     * @param countTable = 2xnumCols contingency table.
     * @return the p-value of the Cochran-Armitage statistic of the passed table
     */
    public double callCochranArmitageTest(int[][] countTable) {
        if (countTable == null) {
            throw new IllegalArgumentException("Contingency table cannot be null/empty.");
        }
        
        if ((countTable.length!=numRows) || (countTable[0].length!=numCols)) {
            throw new IllegalArgumentException("Contingency table must be "+numRows+" rows by "+numCols+" columns");
        }
        
        int totalSum=0;
        int[] rowSum = new int[numRows];
        int[] colSum = new int[numCols];
        
        // calculate marginal and overall sums for the contingency table
        for (int i=0; i<numRows; i++) {
            for (int j=0; j<numCols; j++) {
                rowSum[i] += countTable[i][j];
                colSum[j] += countTable[i][j];
                totalSum += countTable[i][j];
            }
        }

        // calculate the test statistic and variance based on the formulae at
        // http://en.wikipedia.org/wiki/Cochran-Armitage_test_for_trend
        stat = 0.0;
        variance = 0.0;
        for (int j=0; j<numCols; j++) {
            stat += weights[j] * (countTable[0][j]*rowSum[1] - 
                                  countTable[1][j]*rowSum[0]);
            variance += weights[j]*weights[j]*colSum[j]*(totalSum-colSum[j]);

            if (j!=numCols-1) {
                for (int k=j+1;k<numCols;k++) {
                    variance -= 2*weights[j]*weights[k]*colSum[j]*colSum[k];
                }
            }
        }
        variance *= rowSum[0]*rowSum[1]/totalSum;

        // standardized statistic is stat divided by SD
        standardStatistics = stat/Math.sqrt(variance);

        // use Apache Commons normal distribution to calculate two-tailed p-value
        pValue = 2*normDist.cumulativeProbability(-Math.abs(standardStatistics));

        // return the p-value
        return pValue;
    }

    /**
     * @param args input/output files for testing/debugging a series of 6-entry lines each representing a 2x3 matrix
     * args[0] as input file
     */
    public static void main(String[] args) throws IOException {
        if (args.length!=1) {
            System.out.println("usage: java CochranArmitage <input-filename> <output-filename>");
            System.exit(1);
        }

        long startTime = System.currentTimeMillis();
        String inputFileName = args[0];

        int[] weights = { 0, 1, 2 };
        CochranArmitage catest = new CochranArmitage(weights);    
        
        System.out.println("score\tp-value");
        BufferedReader infile = new BufferedReader(new FileReader(inputFileName));
        int[][] countTable = new int[catest.numRows][catest.numCols];
        String line = null;
        while ((line=infile.readLine())!=null) {
            String[] tokens = line.split("\t");
            int index=0;
            // populate numRowsxnumCols contingency table
            for(int i=0; i<catest.numRows; i++) {
                for(int j=0; j<catest.numCols; j++) {
                    countTable[i][j] = Integer.parseInt(tokens[index++]);
                }
            }
            double pValue = catest.callCochranArmitageTest(countTable);
            System.out.println(String.format("%f\t%f", catest.standardStatistics, pValue));
        }
        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("run time (in milliseconds): " + elapsedTime);
        infile.close();
    }
}
