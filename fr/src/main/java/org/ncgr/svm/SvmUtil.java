package org.ncgr.svm;

import libsvm.*;

/**
 * Some utility static methods.
 */
public class SvmUtil {

    // cross-validation k-fold default value
    public static int NRFOLD = 10;

    // null output
    static svm_print_interface svm_print_null = new svm_print_interface() { public void print(String s) {} };

    // stdout
    static svm_print_interface svm_print_stdout = new svm_print_interface() { public void print(String s) { System.out.print(s); } };
    
    public static svm_parameter getDefaultParam() {
        svm_parameter param = new svm_parameter();
        param.svm_type = svm_parameter.C_SVC;
        param.kernel_type = svm_parameter.RBF;
        param.degree = 3;
        param.gamma = 0;	// set to 1/num_features in readProblem
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
        return param;
    }

    public static void setQuiet() {
        svm.svm_set_print_string_function(svm_print_null);
    }
    public static void setVerbose() {
        svm.svm_set_print_string_function(svm_print_stdout);
    }

}
