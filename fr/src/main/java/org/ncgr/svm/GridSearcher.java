package org.ncgr.svm;

import libsvm.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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

    // defaults
    int fold = 5;
    int c_begin = -5;
    int c_end = 15;
    int c_step = 1;
    int g_begin = 3;
    int g_end = -15;
    int g_step = -1;

    int nrFold = 5;
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
        svm_parameter param = SvmUtil.getDefaultParam();
        // cycle through C and gamma
        for (int c=c_begin; c<=c_end; c+=c_step) {
            for (int g=g_begin; g>=g_end; g+=g_step) {
                param.C = Math.pow(2.0,c);
                param.gamma = Math.pow(2.0,g);
                SvmCrossValidator svc = new SvmCrossValidator(param, nrFold, datafile);
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

        Option nOption = new Option("n", true, "n-fold for cross validation [5]");
        nOption.setRequired(false);
        options.addOption(nOption);

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

        if (cmd.hasOption("n")) {
            gs.fold = Integer.parseInt(cmd.getOptionValue("n"));
            System.out.println("cross-validation:"+gs.fold+"-fold");
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
    
}
