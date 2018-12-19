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
 * Java port of the libsvm grid.py utility for searching a (C,gamma) grid to find optimal values.
 *
 * @author Sam Hokin
 */
public class GridSearcher {

    // grid defaults
    int c_begin = -5;
    int c_end = 15;
    int c_step = 1;
    int g_begin = 3;
    int g_end = -15;
    int g_step = -1;

    // cross-validation
    int nrFold = SvmUtil.NRFOLD;
    svm_parameter param;
    svm_problem prob;
    
    boolean verbose = false;

    // results
    public int totalSamples = 0;
    public int bestTotalCorrect = 0;
    public double bestC = 0.0;
    public double bestGamma = 0.0;
    public double bestAccuracy = 0.0;

    // run the search
    public void run(String datafile) throws IOException {
        // initialize svm_param with defaults
        param = SvmUtil.getDefaultParam();
        // load the problem once
        readProblem(datafile);
        // cycle through C and gamma
        for (int c=c_begin; c<=c_end; c+=c_step) {
            for (int g=g_begin; g>=g_end; g+=g_step) {
                param.C = Math.pow(2.0,c);
                param.gamma = Math.pow(2.0,g);
                SvmCrossValidator svc = new SvmCrossValidator(param, nrFold, prob);
                SvmUtil.setQuiet();
                svc.run();
                if (verbose) System.out.print("TotalCorrect,Accuracy="+svc.totalCorrect+","+svc.accuracy);
                if (verbose) System.out.print("\tC,gamma:"+param.C+","+param.gamma);
                if (svc.totalCorrect>bestTotalCorrect) {
                    bestC = param.C;
                    bestGamma = param.gamma;
                    totalSamples = svc.totalSamples;
                    bestTotalCorrect = svc.totalCorrect;
                    bestAccuracy = svc.accuracy;
                    if (verbose) System.out.print("***");
                }
                if (verbose) System.out.println("");
            }
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
     * Command-line operation.
     */
    public static void main(String[] args) throws IOException {
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        if (args.length==0) {
            System.out.println("Usage:");
            System.out.println("GridSearcher [options]");
            System.exit(1);
        }

        Option log2cOption = new Option("log2c", true, "set range/step of C [-5,15,2]");
        log2cOption.setRequired(false);
        options.addOption(log2cOption);

        Option log2gOption = new Option("log2g", true, "set range/step of gamma [3,-15,-2]");
        log2gOption.setRequired(false);
        options.addOption(log2gOption);

        Option nFoldOption = new Option("k", true, "k-fold for cross validation [5]");
        nFoldOption.setRequired(false);
        options.addOption(nFoldOption);

        Option vOption = new Option("v", false, "toggle verbose output");
        vOption.setRequired(false);
        options.addOption(vOption);

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("GridSearcher", options);
            System.exit(1);
            return;
        }
        
        GridSearcher gs = new GridSearcher();

        if (cmd.hasOption("log2c")) {
            String log2cValues = cmd.getOptionValue("log2c");
            System.out.println("C:["+log2cValues+"]");
            String[] parts = log2cValues.split(",");
            gs.c_begin = Integer.parseInt(parts[0]);
            gs.c_end = Integer.parseInt(parts[1]);
            gs.c_step = Integer.parseInt(parts[2]);
        }

        if (cmd.hasOption("log2g")) {
            String log2gValues = cmd.getOptionValue("log2g");
            System.out.println("gamma:["+log2gValues+"]");
            String[] parts = log2gValues.split(",");
            gs.g_begin = Integer.parseInt(parts[0]);
            gs.g_end = Integer.parseInt(parts[1]);
            gs.g_step = Integer.parseInt(parts[2]);
        }

        if (cmd.hasOption("k")) {
            gs.nrFold = Integer.parseInt(cmd.getOptionValue("k"));
            System.out.println("cross-validation:"+gs.nrFold+"-fold");
        }

        gs.verbose = cmd.hasOption("v");

        // data file is last argument
        String datafile = args[args.length-1];
        System.out.println("data:"+datafile);

        // run the search
        gs.run(datafile);

        System.out.println("BEST VALUES:");
        System.out.println("correct/samples="+gs.bestTotalCorrect+"/"+gs.totalSamples);
        System.out.println("C\tgamma\t\taccuracy");
        System.out.println(gs.bestC+"\t"+gs.bestGamma+"\t"+gs.bestAccuracy*100.0);
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
