package org.ncgr.svm;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.text.DecimalFormat;

import java.util.List;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import libsvm.svm;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

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
    public int maxIndex = 0;
    public Vector<Integer> errorIndices;

    // the input samples
    public List<Sample> samples;
    public Vector<String> sampleNames; // for alignment with libsvm vectors
    
    /**
     * Construct with default svm_parameter object, default-fold cross-validation and the given input file name.
     */
    public SvmCrossValidator(String inputFilename) throws FileNotFoundException, IOException {
        this.param = SvmUtil.getDefaultParam();
        this.samples = SvmUtil.readSamples(inputFilename);
        createProblem();
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
        this.samples = SvmUtil.readSamples(inputFilename);
        createProblem();
    }

    /**
     * Construct given a populated svm_parameter object, n-fold number and populated labels and svm_nodes.
     */
    public SvmCrossValidator(svm_parameter param, int nrFold, Vector<Double> vy, Vector<svm_node[]> vx) {
        this.param = param;
        // validate nrFold
        if (nrFold<2) {
            System.err.println("Error: n-fold cross validation requires n>=2.");
            System.exit(1);
        }
        this.nrFold = nrFold;
        createProblem(vy, vx);
    }

    /**
     * Construct given a populated svm_problem, nr-fold number and populated svm_parameter.
     */
    public SvmCrossValidator(svm_parameter param, int nrFold, svm_problem prob) {
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
        errorIndices = new Vector<>();
        // run it
	try {
	    svm.svm_cross_validation(prob, param, nrFold, target);
	} catch (Exception e) {
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
                } else {
                    errorIndices.add(i);
                }
            }
            accuracy = (double)totalCorrect/(double)prob.l;
            totalSamples = prob.l;
        }
    }

    /**
     * Build label vector vy and nodes vector vx from a list of samples and then create the svm_problem.
     */
    public void createProblem() {
        maxIndex = 0;
        sampleNames = new Vector<>();
        Vector<Double> vy = new Vector<>();
        Vector<svm_node[]> vx = new Vector<>();
        for (Sample sample : samples) {
            sampleNames.addElement(sample.name);
            double dlabel = 0;
            if (sample.label.equals("case") || sample.label.equals("1") || sample.label.equals("+1")) {
                dlabel = 1.0;
            } else if (sample.label.equals("ctrl") || sample.label.equals("-1")) {
                dlabel = -1.0;
            }
            vy.addElement(dlabel);
            svm_node[] x = new svm_node[sample.values.size()];
            int j = 0;
            for (int index : sample.values.keySet()) {
                double value = sample.values.get(index);
                x[j] = new svm_node();
                x[j].index = index;
                x[j].value = value;
                maxIndex = Math.max(maxIndex, x[j].index);
                j++;
            }
            vx.addElement(x);
        }
        createProblem(vy, vx);
    }

    /**
     * Create and populate the svm_problem from the label vector vy and nodes vector vx.
     * Sets instance vars prob and param.
     */
    void createProblem(Vector<Double> vy, Vector<svm_node[]> vx) {
        // find the maximum index value
        int max_index = 0;
        for (svm_node[] x : vx) {
            for (svm_node node : x) {
                max_index = Math.max(max_index, node.index);
            }
        }
        // create the problem round town
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
        //
        Option svmTypeOption = new Option("s", "svm-type", true,
                                          "set type of SVM [0]: 0=C-SVC; 1=nu-SVC; 2=one-class SVM; 3=epsilon-SVR; 4=nu-SVR");
        svmTypeOption.setRequired(false);
        options.addOption(svmTypeOption);
        //
        Option kernelTypeOption = new Option("t", "kernel-type", true,
                                             "set type of kernel function [2]: 0=linear; 1=polynomial; 2=radial basis function; 3=sigmoid; 4=precomputed kernel");
        kernelTypeOption.setRequired(false);
        options.addOption(kernelTypeOption);
        //
        Option kernelDegreeOption = new Option("d", "kernel-degree", true, "set degree in kernel function [3]");
        kernelDegreeOption.setRequired(false);
        options.addOption(kernelDegreeOption);
        //
        Option kernelGammaOption = new Option("g", "kernel-gamma", true, "set gamma in kernel function [1/#features]");
        kernelGammaOption.setRequired(false);
        options.addOption(kernelGammaOption);
        //
        Option kernelCoef0Option = new Option("r", "kernel-coef0", true, "set coef0 in kernel function [0]");
        kernelCoef0Option.setRequired(false);
        options.addOption(kernelCoef0Option);
        //
        Option costOption = new Option("c", "cost", true, "set the cost parameter C in C-SVC, epsilon-SVR and nu-SVR [1]");
        costOption.setRequired(false);
        options.addOption(costOption);
        //
        Option nuOption = new Option("n", "nu", true, "set the parameter nu of nu-SVC, one-class SVM, and nu-SVR [0.5]");
        nuOption.setRequired(false);
        options.addOption(nuOption);
        //
        Option epsilonLossOption = new Option("p", "epsilon-loss", true, "set the epsilon value in loss function of epsilon-SVR [0.1]");
        epsilonLossOption.setRequired(false);
        options.addOption(epsilonLossOption);
        //
        Option cacheSizeOption = new Option("m", "cachesize", true, "set cache memory size in MB [100]");
        cacheSizeOption.setRequired(false);
        options.addOption(cacheSizeOption);
        //
        Option epsilonOption = new Option("e", "epsilon", true, "set tolerance of termination criterion [0.001]");
        epsilonOption.setRequired(false);
        options.addOption(epsilonOption);
        //
        Option shrinkingOption = new Option("h", "shrinking", true, "0/1 toggle whether to use the shrinking heuristics [1]");
        shrinkingOption.setRequired(false);
        options.addOption(shrinkingOption);
        //
        Option probabilityEstimatesOption = new Option("b", "probability-estimates", true, "0/1 toggle whether to train a SVC or SVR model for probability estimates [0]");
        probabilityEstimatesOption.setRequired(false);
        options.addOption(probabilityEstimatesOption);
        //
        Option weightOption = new Option("w", "weight", true, "set the parameter C of class i to weight*C, for C-SVC [1]");
        weightOption.setRequired(false);
        options.addOption(weightOption);
        //
        Option nrFoldOption = new Option("k", "nrfold", true, "k value for k-fold cross-validation");
        nrFoldOption.setRequired(false);
        options.addOption(nrFoldOption);
        //
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

        // instantiate from a samples file
        SvmCrossValidator scv = new SvmCrossValidator(param, nrFold, inputFilename);

        // run
	scv.run();

        // output
        if (scv.param.svm_type==svm_parameter.EPSILON_SVR || scv.param.svm_type== svm_parameter.NU_SVR) {
            System.out.println("Cross Validation Mean squared error = "+scv.meanSquaredError);
            System.out.println("Cross Validation Squared correlation coefficient = "+scv.squaredCorrCoeff);
        } else {
            // total up fails
            int plusFails = 0;
            int minusFails = 0;
            // System.out.print("Fails (0-based):");
	    for (int i : scv.errorIndices) {
                if (scv.prob.y[i]==+1) {
                    plusFails++;
		    // System.out.print(" "+i+"(+1)");
                } else if (scv.prob.y[i]==-1) {
		    minusFails++;
		    // System.out.print(" "+i+"(-1)");
                }
	    }
	    // System.out.println("");
            // System.out.println("+1/-1 fails: "+plusFails+"/"+minusFails);
            // CVA, TPR, FPR
            int plusTotal = 0;
            int minusTotal = 0;
            for (double y : scv.prob.y) {
                if (y==+1) plusTotal++;
                if (y==-1) minusTotal++;
            }
            // System.out.println("CVA="+pf.format(scv.accuracy)+" ("+scv.totalCorrect+"/"+scv.totalSamples+")");
            // System.out.println("TPR="+pf.format(1.0 - (double)plusFails/(double)plusTotal)+" ("+(plusTotal-plusFails)+"/"+plusTotal+")");
            // System.out.println("FPR="+pf.format((double)minusFails/(double)minusTotal)+" ("+minusFails+"/"+minusTotal+")");
            //
            // summary line
            System.out.println(inputFilename+"\t"+scv.maxIndex+"\t"+param.C+"\t"+param.gamma+"\t"+nrFold+
                               "\t"+plusTotal+"\t"+minusTotal+"\t"+plusFails+"\t"+minusFails);
            // predictions by sample
            for (int i=0; i<scv.prob.l; i++) {
                String label = "";
                System.out.print(scv.samples.get(i).name+"\t"+scv.samples.get(i).label);
                if (scv.errorIndices.contains(i)) {
                    System.out.println("\tfalse");
                } else {
                    System.out.println("\ttrue");
                }
            }
        }
    }
}
