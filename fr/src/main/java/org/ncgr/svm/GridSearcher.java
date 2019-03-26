package org.ncgr.svm;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.List;
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
    public int maxIndex = 0;
    public int totalSamples = 0;
    public int bestTotalCorrect = 0;
    public double bestC = 0.0;
    public double bestGamma = 0.0;
    public double bestAccuracy = 0.0;

    // input data
    public List<Sample> samples;
    public Vector<String> sampleNames;

    /**
     * Construct with a data file and default parameters.
     */
    public GridSearcher(String dataFile) throws FileNotFoundException, IOException {
        this.samples = SvmUtil.readSamples(dataFile);
        // initialize svm_param with defaults
        param = SvmUtil.getDefaultParam();
        // build the problem
        createProblem();
    }

    /**
     * Run the search
     */
    public void run() {
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
     * Create an svm_problem from samples. Sets instance vars prob and param.
     */
    public void createProblem() {
        sampleNames = new Vector<>();
        Vector<Double> vy = new Vector<>();
        Vector<svm_node[]> vx = new Vector<>();
        maxIndex = 0;
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

        // bail if we've got nothing
        if (maxIndex==0) {
            System.exit(1);
        }

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
        if (param.gamma==0 && maxIndex>0) param.gamma = 1.0/maxIndex;

        // validation
        if (param.kernel_type == svm_parameter.PRECOMPUTED) {
            for (int i=0;i<prob.l;i++) {
                if (prob.x[i][0].index != 0) {
                    System.err.println("Wrong kernel matrix: first column must be 0:sample_serial_number");
                    System.exit(1);
                }
                if ((int)prob.x[i][0].value <= 0 || (int)prob.x[i][0].value > maxIndex) {
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
     * Set verbose flag to true.
     */
    public void setVerbose() {
        verbose = true;
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

        Option nFoldOption = new Option("k", true, "k-fold for cross validation ["+SvmUtil.NRFOLD+"]");
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

        // data file is last argument
        String datafile = args[args.length-1];
        
        // initialize with default parameters
        GridSearcher gs = new GridSearcher(datafile);

        boolean verbose = false;
        if (cmd.hasOption("v")) {
            verbose = true;
            gs.setVerbose();
        }

        if (verbose) System.out.println("data:"+datafile);

        if (cmd.hasOption("log2c")) {
            String log2cValues = cmd.getOptionValue("log2c");
            if (verbose) System.out.println("C:["+log2cValues+"]");
            String[] parts = log2cValues.split(",");
            gs.c_begin = Integer.parseInt(parts[0]);
            gs.c_end = Integer.parseInt(parts[1]);
            gs.c_step = Integer.parseInt(parts[2]);
        }

        if (cmd.hasOption("log2g")) {
            String log2gValues = cmd.getOptionValue("log2g");
            if (verbose) System.out.println("gamma:["+log2gValues+"]");
            String[] parts = log2gValues.split(",");
            gs.g_begin = Integer.parseInt(parts[0]);
            gs.g_end = Integer.parseInt(parts[1]);
            gs.g_step = Integer.parseInt(parts[2]);
        }

        if (cmd.hasOption("k")) {
            gs.nrFold = Integer.parseInt(cmd.getOptionValue("k"));
            if (verbose) System.out.println("cross-validation:"+gs.nrFold+"-fold");
        }

        // run the search
        gs.run();

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
