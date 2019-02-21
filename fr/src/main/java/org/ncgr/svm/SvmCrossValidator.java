package org.ncgr.svm;

import libsvm.*;

import java.io.BufferedReader;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
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
 * Use svm to run a stratified k-fold cross-validation of data.
 */
public class SvmCrossValidator {

    static DecimalFormat pf = new DecimalFormat("0.0%");
    
    svm_parameter param;
    svm_problem prob;
    int nrFold = SvmUtil.NRFOLD;

    // regression results
    public double totalError = 0.0;
    public double meanSquaredError = 0.0;
    public double squaredCorrCoeff = 0.0;

    // classification results
    public int totalSamples = 0;
    public int totalCorrect = 0;
    public double accuracy = 0.0;
    
    /**
     * Construct with default svm_parameter object, default-fold cross-validation and the given input file name.
     */
    public SvmCrossValidator(String inputFilename) throws IOException {
        this.param = SvmUtil.getDefaultParam();
        readProblem(inputFilename);
    }

    /**
     * Construct given a populated svm_parameter object, n-fold number and an input file name.
     */
    public SvmCrossValidator(svm_parameter param, int nrFold, String inputFilename) throws IOException {
        this.param = param;
        // validate nrFold
        if (nrFold<2) {
            System.err.println("Error: n-fold cross validation requires n>=2.");
            System.exit(1);
        }
        this.nrFold = nrFold;
        // load the problem from the input file
        readProblem(inputFilename);
    }

    /**
     * Construct given a populated svm_problem, nr-fold number and populated svm_parameter.
     */
    public SvmCrossValidator(svm_parameter param, int nrFold, svm_problem prob) throws IOException {
        // validate nrFold
        if (nrFold<2) {
            System.err.println("Error: n-fold cross validation requires n>=2.");
            System.exit(1);
        }
        this.nrFold = nrFold;
        this.param = param;
        this.prob = prob;
    }

    /**
     * Perform the cross validation.
     */
    public void run() {
        double[] target = new double[prob.l];
        double sumv = 0.0, sumy = 0.0, sumvv = 0.0, sumyy = 0.0, sumvy = 0.0;
        // run it
	try {
	    svm.svm_cross_validation(prob, param, nrFold, target);
	} catch (Exception e) {
	    // soft-ish crash
	    System.err.println(e.getMessage());
	    System.exit(1);
	}
        if (param.svm_type == svm_parameter.EPSILON_SVR || param.svm_type == svm_parameter.NU_SVR) {
            // regression results
            for (int i=0; i<prob.l; i++) {
                double y = prob.y[i];
                double v = target[i];
                totalError += (v-y)*(v-y);
                sumv += v;
                sumy += y;
                sumvv += v*v;
                sumyy += y*y;
                sumvy += v*y;
            }
            meanSquaredError = totalError/prob.l;
            squaredCorrCoeff = ((prob.l*sumvy-sumv*sumy)*(prob.l*sumvy-sumv*sumy))/((prob.l*sumvv-sumv*sumv)*(prob.l*sumyy-sumy*sumy));
        } else {
            // classification results
            for (int i=0; i<prob.l; i++) {
                if (target[i] == prob.y[i]) {
                    ++totalCorrect;
                }
            }
            accuracy = (double)totalCorrect/(double)prob.l;
            totalSamples = prob.l;
        }
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
     * Command line version.
     */
    public static void main(String[] args) throws IOException {
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;
        
        Option svmTypeOption = new Option("s", "svm-type", true, "set type of SVM [0]: 0=C-SVC; 1=nu-SVC; 2=one-class SVM; 3=epsilon-SVR; 4=nu-SVR");
        svmTypeOption.setRequired(false);
        options.addOption(svmTypeOption);

        Option kernelTypeOption = new Option("t", "kernel-type", true, "set type of kernel function [2]: 0=linear; 1=polynomial; 2=radial basis function; 3=sigmoid; 4=precomputed kernel");
        kernelTypeOption.setRequired(false);
        options.addOption(kernelTypeOption);

        Option kernelDegreeOption = new Option("d", "kernel-degree", true, "set degree in kernel function [3]");
        kernelDegreeOption.setRequired(false);
        options.addOption(kernelDegreeOption);

        Option kernelGammaOption = new Option("g", "kernel-gamma", true, "set gamma in kernel function [1/#features]");
        kernelGammaOption.setRequired(false);
        options.addOption(kernelGammaOption);
        
        Option kernelCoef0Option = new Option("r", "kernel-coef0", true, "set coef0 in kernel function [0]");
        kernelCoef0Option.setRequired(false);
        options.addOption(kernelCoef0Option);

        Option costOption = new Option("c", "cost", true, "set the cost parameter C in C-SVC, epsilon-SVR and nu-SVR [1]");
        costOption.setRequired(false);
        options.addOption(costOption);

        Option nuOption = new Option("n", "nu", true, "set the parameter nu of nu-SVC, one-class SVM, and nu-SVR [0.5]");
        nuOption.setRequired(false);
        options.addOption(nuOption);
        
        Option epsilonLossOption = new Option("p", "epsilon-loss", true, "set the epsilon value in loss function of epsilon-SVR [0.1]");
        epsilonLossOption.setRequired(false);
        options.addOption(epsilonLossOption);
        
        Option cacheSizeOption = new Option("m", "cachesize", true, "set cache memory size in MB [100]");
        cacheSizeOption.setRequired(false);
        options.addOption(cacheSizeOption);
        
        Option epsilonOption = new Option("e", "epsilon", true, "set tolerance of termination criterion [0.001]");
        epsilonOption.setRequired(false);
        options.addOption(epsilonOption);

        Option shrinkingOption = new Option("h", "shrinking", true, "0/1 toggle whether to use the shrinking heuristics [1]");
        shrinkingOption.setRequired(false);
        options.addOption(shrinkingOption);

        Option probabilityEstimatesOption = new Option("b", "probability-estimates", true, "0/1 toggle whether to train a SVC or SVR model for probability estimates [0]");
        probabilityEstimatesOption.setRequired(false);
        options.addOption(probabilityEstimatesOption);

        Option weightOption = new Option("w", "weight", true, "set the parameter C of class i to weight*C, for C-SVC [1]");
        weightOption.setRequired(false);
        options.addOption(weightOption);

        Option nrFoldOption = new Option("k", "nrfold", true, "k value for k-fold cross-validation");
        nrFoldOption.setRequired(false);
        options.addOption(nrFoldOption);

        Option verboseOption = new Option("v", "verbose", false, "verbose output");
        verboseOption.setRequired(false);
        options.addOption(verboseOption);

        if (args.length==0) {
            formatter.printHelp("SvmCrossValidator [options]", options);
            System.exit(1);
        }
        
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("SvmCrossValidator [options]", options);
            System.exit(1);
            return;
        }

        // start with default param values
        svm_parameter param = SvmUtil.getDefaultParam();

        // update values based on options
        if (cmd.hasOption("s")) {
            param.svm_type = Integer.parseInt(cmd.getOptionValue("s"));
        }
        if (cmd.hasOption("t")) {
            param.kernel_type = Integer.parseInt(cmd.getOptionValue("t"));
        }
        if (cmd.hasOption("d")) {
            param.degree = Integer.parseInt(cmd.getOptionValue("d"));
        }
        if (cmd.hasOption("g")) {
            param.gamma = Double.parseDouble(cmd.getOptionValue("g"));
        }
        if (cmd.hasOption("n")) {
            param.nu = Double.parseDouble(cmd.getOptionValue("n"));
        }
        if (cmd.hasOption("m")) {
            param.cache_size = Double.parseDouble(cmd.getOptionValue("m"));
        }
        if (cmd.hasOption("c")) {
            param.C = Double.parseDouble(cmd.getOptionValue("c"));
        }
        if (cmd.hasOption("e")) {
            param.eps = Double.parseDouble(cmd.getOptionValue("e"));
        }
        if (cmd.hasOption("p")) {
            param.p = Double.parseDouble(cmd.getOptionValue("p"));
        }
        if (cmd.hasOption("h")) {
            param.shrinking = Integer.parseInt(cmd.getOptionValue("h"));
        }
        if (cmd.hasOption("b")) {
            param.probability = Integer.parseInt(cmd.getOptionValue("b"));
        }

        int nrFold = SvmUtil.NRFOLD; // default
        if (cmd.hasOption("k")) {
            nrFold = Integer.parseInt(cmd.getOptionValue("k"));
        }

        // this is weird, setting a static function in svm
        if (cmd.hasOption("v")) {
            SvmUtil.setVerbose();
        } else {
            SvmUtil.setQuiet();
        }

        // get input file from last parameter
        String inputFilename = args[args.length-1];

        // instantiate
        SvmCrossValidator svc = new SvmCrossValidator(param, nrFold, inputFilename);

        // run
	svc.run();

        // some final output
        if (svc.param.svm_type==svm_parameter.EPSILON_SVR || svc.param.svm_type== svm_parameter.NU_SVR) {
            System.out.println("Cross Validation Mean squared error = "+svc.meanSquaredError);
            System.out.println("Cross Validation Squared correlation coefficient = "+svc.squaredCorrCoeff);
        } else {
            System.out.println("correct/total="+svc.totalCorrect+"/"+svc.totalSamples);
            System.out.println("Cross Validation Accuracy = "+pf.format(svc.accuracy));
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
