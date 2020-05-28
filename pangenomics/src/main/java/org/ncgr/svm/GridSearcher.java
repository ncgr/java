package org.ncgr.svm;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.text.DecimalFormat;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;

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

    static DecimalFormat df = new DecimalFormat("0.00E0");
    static DecimalFormat pf = new DecimalFormat("00.0%");

    // grid defaults
    static double C_BEGIN = -2.0;
    static double C_END = 5.0;
    static double C_STEP = 1.0;
    static double G_BEGIN = -4.0;
    static double G_END = 0.0;
    static double G_STEP = 1.0;

    // search grid ranges
    double c_begin = C_BEGIN;
    double c_end = C_END;
    double c_step = C_STEP;
    double g_begin = G_BEGIN;
    double g_end = G_END;
    double g_step = G_STEP;

    // quantities that only depend on samples
    Vector<Double> vy = new Vector<>();
    Vector<svm_node[]> vx = new Vector<>();

    // cross-validation
    int nrFold = SvmUtil.NRFOLD;
    
    boolean verbose = false;

    public int maxIndex = 0;

    // input data
    String dataFile;
    public List<Sample> samples;
    public Vector<String> sampleNames;

    /**
     * Construct with a data file and default parameters.
     */
    public GridSearcher(String dataFile) throws FileNotFoundException, IOException {
	this.dataFile = dataFile;
        samples = SvmUtil.readSamples(dataFile);
	sampleNames = new Vector<>();
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
	    System.err.println("GridSearcher ERROR: maxIndex==0, exiting.");
	    System.exit(1);
	}
	// bizarre setting of static function
	SvmUtil.setQuiet();
    }

    /**
     * Run the search in a parallel stream for each C and gamma value in a parallel stream
     */
    public void run() {
	if (verbose) {
	    System.err.println("GridSearcher: "+dataFile);
	    System.err.println("GridSearcher: "+samples.size()+" samples");
            System.err.println("GridSearcher: "+nrFold+"-fold cross-validation");
	    System.err.println("GridSearcher: log10(C) from "+c_begin+" to "+c_end+" in steps of "+c_step);
	    System.err.println("GridSearcher: log10(gamma) from "+g_begin+" to "+g_end+" in steps of "+g_step);
	}
        // cycle through C and gamma in parallel streams
	ConcurrentSkipListSet<Double> log10CSet = new ConcurrentSkipListSet<>();
        for (double c=c_begin; c<=c_end; c+=c_step) {
	    log10CSet.add(c);
	}
	ConcurrentSkipListSet<Double> log10gammaSet = new ConcurrentSkipListSet<>();
	for (double g=g_begin; g<=g_end; g+=g_step) {
	    log10gammaSet.add(g);
	}
	// these maps are keyed by a string representing C and gamma
	ConcurrentSkipListMap<String,svm_parameter> paramMap = new ConcurrentSkipListMap<>();
	ConcurrentSkipListMap<String,Integer> totalCorrectMap = new ConcurrentSkipListMap<>();
	ConcurrentSkipListMap<String,Double> accuracyMap = new ConcurrentSkipListMap<>();
	////////////////////////////////////////////////////////////////////////////////////////////////
	log10CSet.parallelStream().forEach(log10C -> {
		log10gammaSet.parallelStream().forEach(log10gamma -> {
			svm_parameter param = SvmUtil.getDefaultParam();
			param.C = Math.pow(10.0,log10C);
			param.gamma = Math.pow(10.0,log10gamma);
			String key = param.C+":"+param.gamma;
			svm_problem prob = createProblem(vx, vy, param, maxIndex);
			SvmCrossValidator svc = new SvmCrossValidator(param, nrFold, prob);
			svc.run();
			if (verbose) {
			    System.err.println("GridSearcher: C="+df.format(param.C)+" gamma="+df.format(param.gamma)+" totalCorrect="+svc.totalCorrect+" accuracy="+pf.format(svc.accuracy));
			}
			// store the results
			paramMap.put(key, param);
			totalCorrectMap.put(key, svc.totalCorrect);
			accuracyMap.put(key, svc.accuracy);
		    });
	    });
	////////////////////////////////////////////////////////////////////////////////////////////////
	// scan the results for best results
	svm_parameter bestParam = null;
	int bestTotalCorrect = 0;
	double bestAccuracy = 0.0;
	for (String key : paramMap.keySet()) {
	    svm_parameter param = paramMap.get(key);
	    int totalCorrect = totalCorrectMap.get(key);
	    double accuracy = accuracyMap.get(key);
	    if (totalCorrect>bestTotalCorrect) {
		bestParam = param;
		bestTotalCorrect = totalCorrect;
		bestAccuracy = accuracy;
	    }
	}
	// output
        System.out.println("BEST VALUES:");
        System.out.println("correct/samples="+bestTotalCorrect+"/"+samples.size());
        System.out.println("C\tgamma\t\taccuracy");
        System.out.println(bestParam.C+"\t"+bestParam.gamma+"\t"+bestAccuracy);
    }

    /**
     * Create an svm_problem from samples. Sets instance vars prob and param.
     */
    static svm_problem createProblem(Vector<svm_node[]> vx, Vector<Double> vy, svm_parameter param, int maxIndex) {
        // create and populate the svm_problem
        svm_problem prob = new svm_problem();
        prob.l = vy.size();
        prob.x = new svm_node[prob.l][];
        for (int i=0;i<prob.l;i++) {
            prob.x[i] = vx.elementAt(i);
        }
        prob.y = new double[prob.l];
        for (int i=0;i<prob.l;i++) {
            prob.y[i] = vy.elementAt(i);
        }
        // validation
        if (param.kernel_type == svm_parameter.PRECOMPUTED) {
            for (int i=0;i<prob.l;i++) {
                if (prob.x[i][0].index != 0) {
                    System.err.println("GridSearcher ERROR: Wrong kernel matrix: first column must be 0:sample_serial_number.");
                    System.exit(1);
                }
                if ((int)prob.x[i][0].value <= 0 || (int)prob.x[i][0].value > maxIndex) {
                    System.err.println("GridSearcher ERROR: Wrong input format: sample_serial_number out of range.");
                    System.exit(1);
                }
            }
        }
        // String errorMsg = svm.svm_check_parameter(prob,param);
        // if (errorMsg!=null) {
        //     System.err.println("GridSearcher ERROR: "+errorMsg);
        //     System.exit(1);
        // }
	return prob;
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
        CommandLine cmd = null;

	Option log10COption = new Option("log10C", true, "set range/step of log10(C) ["+C_BEGIN+","+C_END+","+C_STEP+"]");
        log10COption.setRequired(false);
        options.addOption(log10COption);

        Option log10gammaOption = new Option("log10gamma", true, "set range/step of log10(gamma) ["+G_BEGIN+","+G_END+","+G_STEP+"]");
        log10gammaOption.setRequired(false);
        options.addOption(log10gammaOption);

        Option nFoldOption = new Option("k", true, "k-fold for cross validation ["+SvmUtil.NRFOLD+"]");
        nFoldOption.setRequired(false);
        options.addOption(nFoldOption);

        Option vOption = new Option("v", false, "toggle verbose output");
        vOption.setRequired(false);
        options.addOption(vOption);

        if (args.length==0) {
            formatter.printHelp("GridSearcher [options]", options);
            System.exit(1);
        }
	
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp("GridSearcher", options);
            System.exit(1);
        }

        // data file is last argument
        String datafile = args[args.length-1];

        // initialize with default parameters
        GridSearcher gs = new GridSearcher(datafile);

        if (cmd.hasOption("v")) {
            gs.setVerbose();
        }

        if (cmd.hasOption("log10C")) {
            String log10CValues = cmd.getOptionValue("log10C");
            String[] parts = log10CValues.split(",");
            gs.c_begin = Double.parseDouble(parts[0]);
            gs.c_end = Double.parseDouble(parts[1]);
            gs.c_step = Double.parseDouble(parts[2]);
        }

        if (cmd.hasOption("log10gamma")) {
            String log10gammaValues = cmd.getOptionValue("log10gamma");
            String[] parts = log10gammaValues.split(",");
            gs.g_begin = Double.parseDouble(parts[0]);
            gs.g_end = Double.parseDouble(parts[1]);
            gs.g_step = Double.parseDouble(parts[2]);
        }

        if (cmd.hasOption("k")) {
            gs.nrFold = Integer.parseInt(cmd.getOptionValue("k"));
        }

        // run the search
        gs.run();
    }

    /**
     * Python-to-Java function.
     */
    static double atof(String s) {
        double d = Double.valueOf(s).doubleValue();
        if (Double.isNaN(d) || Double.isInfinite(d)) {
            System.err.println("GridSearcher ERROR: NaN or Infinity in input.");
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
