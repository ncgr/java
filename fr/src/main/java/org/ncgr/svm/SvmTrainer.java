package org.ncgr.svm;

import libsvm.*;

import java.io.BufferedReader;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
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

public class SvmTrainer {
    
    svm_parameter param;
    svm_problem prob;
    svm_model model;

    /**
     * Instantiate with given param values.
     */
    public SvmTrainer(svm_parameter param) {
        this.param = param;
    }
    
    /**
     * Train the SVM.
     */
    void run() throws IOException {
        model = svm.svm_train(prob, param);
    }

    /**
     * Save the model to a file.
     */
    void save(String filename) throws IOException {
        svm.svm_save_model(filename, model);
    }

    /**
     * Read a problem in from a file (in svmlight format)
     */
    void readProblem(String filename) throws IOException {
        BufferedReader fp = new BufferedReader(new FileReader(filename));
        Vector<Double> vy = new Vector<Double>();
        Vector<svm_node[]> vx = new Vector<svm_node[]>();
        int max_index = 0;

        while (true) {
            String line = fp.readLine();
            if (line == null) break;
            
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

        prob = new svm_problem();
        prob.l = vy.size();
        prob.x = new svm_node[prob.l][];
        for (int i=0;i<prob.l;i++)
            prob.x[i] = vx.elementAt(i);
        prob.y = new double[prob.l];
        for (int i=0;i<prob.l;i++)
            prob.y[i] = vy.elementAt(i);

        if (param.gamma == 0 && max_index > 0)
            param.gamma = 1.0/max_index;

        if (param.kernel_type == svm_parameter.PRECOMPUTED) {
            for (int i=0;i<prob.l;i++) {
                if (prob.x[i][0].index != 0) {
                    System.err.print("Wrong kernel matrix: first column must be 0:sample_serial_number\n");
                    System.exit(1);
                }
                if ((int)prob.x[i][0].value <= 0 || (int)prob.x[i][0].value > max_index) {
                    System.err.print("Wrong input format: sample_serial_number out of range\n");
                    System.exit(1);
                }
            }
        }

        fp.close();

        // check the parameters, bail if a problem
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

        Option verboseOption = new Option("v", "verbose", false, "verbose output");
        verboseOption.setRequired(false);
        options.addOption(verboseOption);

        if (args.length==0) {
            formatter.printHelp("SvmTrainer [options] input-file model-file", options);
            System.exit(1);
        }
        
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("SvmTrainer [options] input-file model-file", options);
            System.exit(1);
            return;
        }

        // default values
        svm_parameter param = new svm_parameter();
        param.svm_type = svm_parameter.C_SVC;
        param.kernel_type = svm_parameter.RBF;
        param.degree = 3;
        param.gamma = 0;	// 1/num_features
        param.coef0 = 0;
        param.nu = 0.5;
        param.cache_size = 100;
        param.C = 1;
        param.eps = 1e-3;
        param.p = 0.1;
        param.shrinking = 1;
        param.probability = 0;
        param.nr_weight = 0;
        param.weight_label = new int[0];
        param.weight = new double[0];

        // set values based on options
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

        // this is weird, setting a static function in svm
        if (cmd.hasOption("v")) {
            SvmUtil.setVerbose();
        } else {
            SvmUtil.setQuiet();
        }

        // get input training data filename from second-to-last parameter
        String inputFilename = args[args.length-2];

        // get output model filename from the last parameter
        String modelFilename = args[args.length-1];

        // instantiate with this param object
        SvmTrainer st = new SvmTrainer(param);

        // load the problem
        st.readProblem(inputFilename);

        // run it
        st.run();

        // save the model file
        st.save(modelFilename);

    }

    /**
     * Python-to-Java noise.
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
     * Python-to-Java noise.
     */
    static int atoi(String s) {
        return Integer.parseInt(s);
    }


}
