package org.ncgr.svm;

import libsvm.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Perform stratified k-fold cross validation on a libsvm "problem".
 *
 * k-fold cross-validation - from Wikipedia article: Cross-validation (statistics)
 *
 * In k-fold cross-validation, the original sample is randomly partitioned into k equal sized subsamples. Of the k subsamples, a single subsample is retained
 * as the validation data for testing the model, and the remaining k−1 subsamples are used as training data. The cross-validation process is then repeated k times,
 * with each of the k subsamples used exactly once as the validation data. The k results can then be averaged to produce a single estimation. The advantage of this
 * method over repeated random sub-sampling is that all observations are used for both training and validation, and each observation is used for validation exactly
 * once. 10-fold cross-validation is commonly used, but in general k remains an unfixed parameter.
 *
 * For example, setting k=2 results in 2-fold cross-validation. In 2-fold cross-validation, we randomly shuffle the dataset into two sets d0 and d1, so that both sets
 * are equal size (this is usually implemented by shuffling the data array and then splitting it in two). We then train on d0 and validate on d1, followed by training
 * on d1 and validating on d0.
 *
 * When k=n (the number of observations), the k-fold cross-validation is exactly the leave-one-out cross-validation.
 *
 * In stratified k-fold cross-validation, the folds are selected so that the mean response value is approximately equal in all the folds. In the case of binary
 * classification, this means that each fold contains roughly the same proportions of the two types of class labels. 
 *
 * @author Sam Hokin
 */
public class KFoldCrossValidator {

    public static int DEFAULT_K = 10;

    int k;
    svm_parameter param;
    svm_problem prob;

    // run the search
    public void run(String modelFilename, String dataFilename) throws IOException {
        readProblem(dataFilename);
        System.out.println("Read problem from "+dataFilename);
    }

    /**
     * Command-line operation.
     */
    public static void main(String[] args) throws IOException {
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        if (args.length==0) {
            System.out.println("Usage:");
            System.out.println("KFoldCrossValidator [options]");
            System.exit(1);
        }

        Option kOption = new Option("k", true, "set the k-value [10, or n if n<10]");
        kOption.setRequired(false);
        options.addOption(kOption);

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("KFoldCrossValidator", options);
            System.exit(1);
            return;
        }
        
        KFoldCrossValidator kfcv = new KFoldCrossValidator();

        kfcv.k = DEFAULT_K;
        if (cmd.hasOption("k")) {
            kfcv.k = Integer.parseInt(cmd.getOptionValue("k"));
        }
        
        kfcv.param = SvmUtil.getDefaultParam();

        // model file is second to last argument
        String modelFilename = args[args.length-2];
        
        // data file is last argument
        String dataFilename = args[args.length-1];

        // run the process
        kfcv.run(modelFilename, dataFilename);

        // System.out.println("BEST VALUES:");
        // System.out.println("correct/samples="+gs.bestTotalCorrect+"/"+gs.totalSamples);
        // System.out.println("C\tgamma\t\taccuracy");
        // System.out.println(gs.bestC+"\t"+gs.bestGamma+"\t"+gs.bestAccuracy*100.0);
    }

    /**
     * Read an svm_problem in from a file in svmlight format.
     * Sets instance vars prob and param.
     */
    public void readProblem(String inputFilename) throws IOException {
        Vector<Double> vy = new Vector<Double>();
        Vector<svm_node[]> vx = new Vector<svm_node[]>();
        int max_index = 0;

        BufferedReader fp = new BufferedReader(new FileReader(inputFilename));
        String line;
        while ((line=fp.readLine())!=null) {
            StringTokenizer st = new StringTokenizer(line," \t\n\r\f:");
            vy.addElement(atof(st.nextToken()));
            int m = st.countTokens()/2;
            svm_node[] x = new svm_node[m];
            for (int j=0;j<m;j++) {
                x[j] = new svm_node();
                x[j].index = atoi(st.nextToken());
                x[j].value = atof(st.nextToken());
            }
            if (m>0) max_index = Math.max(max_index, x[m-1].index);
            vx.addElement(x);
        }
        fp.close();

        // create and populate the svm_problem
        prob = new svm_problem();
        prob.l = vy.size();
        prob.x = new svm_node[prob.l][];
        for (int i=0;i<prob.l;i++) {
            prob.x[i] = vx.elementAt(i);
        }
        prob.y = new double[prob.l];
        for (int i=0;i<prob.l;i++) {
            prob.y[i] = vy.elementAt(i);
        }

        // set param.gamma = 1/N if zero
        if (param.gamma==0 && max_index>0) param.gamma = 1.0/max_index;

        // validation
        if (param.kernel_type == svm_parameter.PRECOMPUTED) {
            for (int i=0;i<prob.l;i++) {
                if (prob.x[i][0].index != 0) {
                    System.err.println("Wrong kernel matrix: first column must be 0:sample_serial_number");
                    System.exit(1);
                }
                if ((int)prob.x[i][0].value <= 0 || (int)prob.x[i][0].value > max_index) {
                    System.err.println("Wrong input format: sample_serial_number out of range");
                    System.exit(1);
                }
            }
        }
        String errorMsg = svm.svm_check_parameter(prob,param);
        if (errorMsg!=null) {
            System.err.println("ERROR: "+errorMsg);
            System.exit(1);
        }
    }

    /**
     * Python-to-Java function.
     */
    static double atof(String s) {
        double d = Double.valueOf(s).doubleValue();
        if (Double.isNaN(d) || Double.isInfinite(d)) {
            System.err.print("NaN or Infinity in input\n");
            System.exit(1);
        }
        return(d);
    }

    /**
     * Python-to-Java function.
     */
    static int atoi(String s) {
        return Integer.parseInt(s);
    }


}
