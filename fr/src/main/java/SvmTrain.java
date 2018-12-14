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

public class SvmTrain {
    
    svm_parameter param;
    String input_file_name;
    String model_file_name;

    boolean cross_validation;
    int nr_fold;

    svm_problem prob;
    svm_model model;

    String error_msg;

    static svm_print_interface svm_print_null = new svm_print_interface() { public void print(String s) {} };
    
    void do_cross_validation() {
        int i;
        int total_correct = 0;
        double total_error = 0;
        double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;
        double[] target = new double[prob.l];

        svm.svm_cross_validation(prob,param,nr_fold,target);
        if (param.svm_type == svm_parameter.EPSILON_SVR || param.svm_type == svm_parameter.NU_SVR) {
            for (i=0;i<prob.l;i++) {
                double y = prob.y[i];
                double v = target[i];
                total_error += (v-y)*(v-y);
                sumv += v;
                sumy += y;
                sumvv += v*v;
                sumyy += y*y;
                sumvy += v*y;
            }
            System.out.print("Cross Validation Mean squared error = "+total_error/prob.l+"\n");
            System.out.print("Cross Validation Squared correlation coefficient = "+
                             ((prob.l*sumvy-sumv*sumy)*(prob.l*sumvy-sumv*sumy))/
                             ((prob.l*sumvv-sumv*sumv)*(prob.l*sumyy-sumy*sumy))+"\n"
                             );
        } else {
            for (i=0;i<prob.l;i++) {
                if (target[i] == prob.y[i]) {
                        ++total_correct;
                }
                System.out.print("Cross Validation Accuracy = "+100.0*total_correct/prob.l+"%\n");
            }
        }
    }

    void run() throws IOException {
        readProblem();
        error_msg = svm.svm_check_parameter(prob,param);

        if (error_msg!=null) {
            System.err.print("ERROR: "+error_msg+"\n");
            System.exit(1);
        }

        if (cross_validation) {
            do_cross_validation();
        } else {
            model = svm.svm_train(prob, param);
            svm.svm_save_model(model_file_name, model);
        }
    }

    /**
     * Command line version.
     */
    public static void main(String[] args) throws IOException {

        if (args.length==0) exitWithHelp();

        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        Option svmTypeOption = new Option("s", "svm-type", true, "set type of SVM [0]");
        svmTypeOption.setRequired(false);
        options.addOption(svmTypeOption);

        Option kernelTypeOption = new Option("t", "kernel-type", true, "set type of kernel function [2]");
        kernelTypeOption.setRequired(false);
        options.addOption(kernelTypeOption);

        Option kernelDegreeOption = new Option("d", "kernel-degree", true, "set degree in kernel function [3]");
        kernelDegreeOption.setRequired(false);
        options.addOption(kernelDegreeOption);

        Option kernelGammaOption = new Option("g", "kernel-gamma", true, "set gamme in kernel function [1/#features]");
        kernelGammaOption.setRequired(false);
        options.addOption(kernelGammaOption);
        
        Option kernelCoef0Option = new Option("r", "kernel-coef0", true, "set coef0 in kernel function [0]");
        kernelCoef0Option.setRequired(false);
        options.addOption(kernelCoef0Option);

        Option costOption = new Option("c", "cost", true, "set the parameter C in C-SVC, epsilon-SVR and nu-SVR [1]");
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

        Option nfoldOption = new Option("v", true, "n value for n-fold cross-validation mode");
        nfoldOption.setRequired(false);
        options.addOption(nfoldOption);

        Option quietOption = new Option("q", "quiet", false, "quiet mode (no output)");
        quietOption.setRequired(false);
        options.addOption(quietOption);

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("SvmScale", options);
            System.exit(1);
            return;
        }

        SvmTrain st = new SvmTrain();

        // default values
        st.param = new svm_parameter();
        st.param.svm_type = svm_parameter.C_SVC;
        st.param.kernel_type = svm_parameter.RBF;
        st.param.degree = 3;
        st.param.gamma = 0;	// 1/num_features
        st.param.coef0 = 0;
        st.param.nu = 0.5;
        st.param.cache_size = 100;
        st.param.C = 1;
        st.param.eps = 1e-3;
        st.param.p = 0.1;
        st.param.shrinking = 1;
        st.param.probability = 0;
        st.param.nr_weight = 0;
        st.param.weight_label = new int[0];
        st.param.weight = new double[0];
        st.cross_validation = false;

        svm_print_interface print_func = null;	// default printing to stdout

        // set values based on options
        if (cmd.hasOption("s")) {
            st.param.svm_type = Integer.parseInt(cmd.getOptionValue("s"));
        }
        if (cmd.hasOption("t")) {
            st.param.kernel_type = Integer.parseInt(cmd.getOptionValue("t"));
        }
        if (cmd.hasOption("d")) {
            st.param.degree = Integer.parseInt(cmd.getOptionValue("d"));
        }
        if (cmd.hasOption("g")) {
            st.param.gamma = Double.parseDouble(cmd.getOptionValue("g"));
        }
        if (cmd.hasOption("n")) {
            st.param.nu = Double.parseDouble(cmd.getOptionValue("n"));
        }
        if (cmd.hasOption("m")) {
            st.param.cache_size = Double.parseDouble(cmd.getOptionValue("m"));
        }
        if (cmd.hasOption("c")) {
            st.param.C = Double.parseDouble(cmd.getOptionValue("c"));
        }
        if (cmd.hasOption("e")) {
            st.param.eps = Double.parseDouble(cmd.getOptionValue("e"));
        }
        if (cmd.hasOption("p")) {
            st.param.p = Double.parseDouble(cmd.getOptionValue("p"));
        }
        if (cmd.hasOption("h")) {
            st.param.shrinking = Integer.parseInt(cmd.getOptionValue("h"));
        }
        if (cmd.hasOption("b")) {
            st.param.probability = Integer.parseInt(cmd.getOptionValue("b"));
        }
        if (cmd.hasOption("v")) {
            // INSTANCE VARS HERE!
            st.cross_validation = true;
            st.nr_fold = Integer.parseInt(cmd.getOptionValue("v"));
            if (st.nr_fold<2) {
                System.err.println("Error: n-fold cross validation requires n>=2.");
                exitWithHelp();
            }
        }
        if (cmd.hasOption("q")) {
            print_func = svm_print_null;
        }

        svm.svm_set_print_string_function(print_func);

        // get data file from last parameter
        st.input_file_name = args[args.length-1];

        // form the output model file name from the input file name
        String[] parts = st.input_file_name.split("\\.");
        String modelFileName = "";
        if (parts.length==1) {
            modelFileName = st.input_file_name;
        } else {
            modelFileName = parts[0];
            for (int k=1; k<parts.length-1; k++) {
                modelFileName += "."+parts[k];
            }
        }
        st.model_file_name = modelFileName+".model";
        System.out.println("input_file_name:"+st.input_file_name);
        System.out.println("model_file_name:"+st.model_file_name);

        // validate instance values
        st.validate();
        
        // run this puppy!
        st.run();
    }

    static double atof(String s) {
        double d = Double.valueOf(s).doubleValue();
        if (Double.isNaN(d) || Double.isInfinite(d)) {
            System.err.print("NaN or Infinity in input\n");
            System.exit(1);
        }
        return(d);
    }

    static int atoi(String s) {
        return Integer.parseInt(s);
    }

    // read in a problem (in svmlight format)
    void readProblem() throws IOException {
        BufferedReader fp = new BufferedReader(new FileReader(input_file_name));
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
    }

    static void exitWithHelp() {
        System.out.print(
                         "Usage: SvmTrain [options] training_set_file\n"
                         +"options:\n"
                         +"-s svm_type : set type of SVM (default 0)\n"
                         +"	0 -- C-SVC		(multi-class classification)\n"
                         +"	1 -- nu-SVC		(multi-class classification)\n"
                         +"	2 -- one-class SVM\n"
                         +"	3 -- epsilon-SVR	(regression)\n"
                         +"	4 -- nu-SVR		(regression)\n"
                         +"-t kernel_type : set type of kernel function (default 2)\n"
                         +"	0 -- linear: u'*v\n"
                         +"	1 -- polynomial: (gamma*u'*v + coef0)^degree\n"
                         +"	2 -- radial basis function: exp(-gamma*|u-v|^2)\n"
                         +"	3 -- sigmoid: tanh(gamma*u'*v + coef0)\n"
                         +"	4 -- precomputed kernel (kernel values in training_set_file)\n"
                         +"-d degree : set degree in kernel function (default 3)\n"
                         +"-g gamma : set gamma in kernel function (default 1/num_features)\n"
                         +"-r coef0 : set coef0 in kernel function (default 0)\n"
                         +"-c cost : set the parameter C of C-SVC, epsilon-SVR, and nu-SVR (default 1)\n"
                         +"-n nu : set the parameter nu of nu-SVC, one-class SVM, and nu-SVR (default 0.5)\n"
                         +"-p epsilon : set the epsilon in loss function of epsilon-SVR (default 0.1)\n"
                         +"-m cachesize : set cache memory size in MB (default 100)\n"
                         +"-e epsilon : set tolerance of termination criterion (default 0.001)\n"
                         +"-h shrinking : whether to use the shrinking heuristics, 0 or 1 (default 1)\n"
                         +"-b probability_estimates : whether to train a SVC or SVR model for probability estimates, 0 or 1 (default 0)\n"
                         +"-wi weight : set the parameter C of class i to weight*C, for C-SVC (default 1)\n"
                         +"-v n : n-fold cross validation mode\n"
                         +"-q : quiet mode (no outputs)\n"
                         );
        System.exit(1);
    }

    /**
     * Validate instance values.
     */
    void validate() {
        if (!cross_validation && model_file_name==null) {
            System.err.println("Either cross-validation mode must be toggled on with -v N XOR output model file name must be given with --model.");
            exitWithHelp();
        }
    }

}
