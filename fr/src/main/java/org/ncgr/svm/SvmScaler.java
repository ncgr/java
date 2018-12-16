package org.ncgr.svm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.text.DecimalFormat;

import java.util.Formatter;
import java.util.StringTokenizer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Utility to scale data to a given interval like [-1,1].
 */
public class SvmScaler {
    
    // defaults
    double lower = -1.0;
    double upper = 1.0;
    double yLower;
    double yUpper;
    boolean yScaling = false;

    // files
    String saveFilename;
    String restoreFilename;
    String dataFilename;

    // I/O
    BufferedReader fp;
    BufferedReader fpRestore;

    double[] featureMax;
    double[] featureMin;
    double yMax = -Double.MAX_VALUE;
    double yMin = Double.MAX_VALUE;
    long numNonzeroes = 0;
    long newNumNonzeroes = 0;

    // probably bad form that these are global vars
    String line;
    int max_index;
    int index;
    int i;

    BufferedReader rewind(BufferedReader fp, String filename) throws IOException {
        fp.close();
        return new BufferedReader(new FileReader(filename));
    }

    void outputTarget(double value) {
        if (yScaling) {
            if (value == yMin) {
                value = yLower;
            } else if (value == yMax) {
                value = yUpper;
            } else {
                value = yLower + (yUpper-yLower) *  (value-yMin) / (yMax-yMin);
            }
        }
        System.out.print((int)value + " ");
    }

    void output(int index, double value) {
        /* skip single-valued attribute */
        if (featureMax[index] == featureMin[index]) return;
        
        if (value == featureMin[index]) {
            value = lower;
        } else if (value == featureMax[index]) {
            value = upper;
        } else {
            value = lower + (upper-lower) *  (value-featureMin[index])/(featureMax[index]-featureMin[index]);
        }
        if (value != 0) {
            System.out.print(index + ":" + value + " ");
            newNumNonzeroes++;
        }
    }

    String readline(BufferedReader fp) throws IOException {
        line = fp.readLine();
        return line;
    }

    void run() throws IOException {

        try {
            fp = new BufferedReader(new FileReader(dataFilename));
        } catch (Exception e) {
            System.err.println("can't open file " + dataFilename);
            System.exit(1);
        }

        /* assumption: min index of attributes is 1 */
        /* pass 1: find out max index of attributes */
        max_index = 0;

        if (restoreFilename != null) {
            int idx, c;

            try {
                fpRestore = new BufferedReader(new FileReader(restoreFilename));
            }
            catch (Exception e) {
                System.err.println("can't open file " + restoreFilename);
                System.exit(1);
            }
            if ((c = fpRestore.read()) == 'y') {
                fpRestore.readLine();
                fpRestore.readLine();
                fpRestore.readLine();
            }
            fpRestore.readLine();
            fpRestore.readLine();

            String restore_line = null;
            while ((restore_line = fpRestore.readLine())!=null) {
                StringTokenizer st2 = new StringTokenizer(restore_line);
                idx = Integer.parseInt(st2.nextToken());
                max_index = Math.max(max_index, idx);
            }
            fpRestore = rewind(fpRestore, restoreFilename);
        }

        while (readline(fp) != null) {
            StringTokenizer st = new StringTokenizer(line," \t\n\r\f:");
            st.nextToken();
            while (st.hasMoreTokens()) {
                index = Integer.parseInt(st.nextToken());
                max_index = Math.max(max_index, index);
                st.nextToken();
                numNonzeroes++;
            }
        }

        try {
            featureMax = new double[(max_index+1)];
            featureMin = new double[(max_index+1)];
        } catch(OutOfMemoryError e) {
            System.err.println("can't allocate enough memory");
            System.exit(1);
        }

        for (i=0;i<=max_index;i++) {
            featureMax[i] = -Double.MAX_VALUE;
            featureMin[i] = Double.MAX_VALUE;
        }

        fp = rewind(fp, dataFilename);

        /* pass 2: find out min/max value */
        while (readline(fp) != null) {
            int next_index = 1;
            double target;
            double value;
            
            StringTokenizer st = new StringTokenizer(line," \t\n\r\f:");
            target = Double.parseDouble(st.nextToken());
            yMax = Math.max(yMax, target);
            yMin = Math.min(yMin, target);
            
            while (st.hasMoreTokens()) {
                index = Integer.parseInt(st.nextToken());
                value = Double.parseDouble(st.nextToken());
                
                for (i = next_index; i<index; i++) {
                    featureMax[i] = Math.max(featureMax[i], 0);
                    featureMin[i] = Math.min(featureMin[i], 0);
                }
                
                featureMax[index] = Math.max(featureMax[index], value);
                featureMin[index] = Math.min(featureMin[index], value);
                next_index = index + 1;
            }

            for (i=next_index;i<=max_index;i++) {
                featureMax[i] = Math.max(featureMax[i], 0);
                featureMin[i] = Math.min(featureMin[i], 0);
            }
        }

        fp = rewind(fp, dataFilename);

        /* pass 2.5: save/restore featureMin/featureMax */
        if (restoreFilename != null) {
            // fpRestore rewinded in finding max_index
            int idx, c;
            double fmin, fmax;
            
            fpRestore.mark(2);				// for reset
            if ((c = fpRestore.read()) == 'y') {
                fpRestore.readLine();		// pass the '\n' after 'y'
                StringTokenizer st = new StringTokenizer(fpRestore.readLine());
                yLower = Double.parseDouble(st.nextToken());
                yUpper = Double.parseDouble(st.nextToken());
                st = new StringTokenizer(fpRestore.readLine());
                yMin = Double.parseDouble(st.nextToken());
                yMax = Double.parseDouble(st.nextToken());
                yScaling = true;
            } else {
                fpRestore.reset();
            }

            if (fpRestore.read() == 'x') {
                fpRestore.readLine();		// pass the '\n' after 'x'
                StringTokenizer st = new StringTokenizer(fpRestore.readLine());
                lower = Double.parseDouble(st.nextToken());
                upper = Double.parseDouble(st.nextToken());
                String restore_line = null;
                while ((restore_line = fpRestore.readLine())!=null) {
                    StringTokenizer st2 = new StringTokenizer(restore_line);
                    idx = Integer.parseInt(st2.nextToken());
                    fmin = Double.parseDouble(st2.nextToken());
                    fmax = Double.parseDouble(st2.nextToken());
                    if (idx <= max_index) {
                        featureMin[idx] = fmin;
                        featureMax[idx] = fmax;
                    }
                }
            }
            fpRestore.close();
        }

        if (saveFilename!=null) {
            Formatter formatter = new Formatter(new StringBuilder());
            BufferedWriter fp_save = null;

            try {
                fp_save = new BufferedWriter(new FileWriter(saveFilename));
            } catch(IOException e) {
                System.err.println("can't open file " + saveFilename);
                System.exit(1);
            }

            if (yScaling) {
                formatter.format("y\n");
                formatter.format("%.16g %.16g\n", yLower, yUpper);
                formatter.format("%.16g %.16g\n", yMin, yMax);
            }
            formatter.format("x\n");
            formatter.format("%.16g %.16g\n", lower, upper);
            for (i=1;i<=max_index;i++) {
                if (featureMin[i]!=featureMax[i]) formatter.format("%d %.16g %.16g\n", i, featureMin[i], featureMax[i]);
            }
            fp_save.write(formatter.toString());
            fp_save.close();
        }

        /* pass 3: scale */
        while (readline(fp)!=null) {
            int next_index = 1;
            double target;
            double value;
            
            StringTokenizer st = new StringTokenizer(line," \t\n\r\f:");
            target = Double.parseDouble(st.nextToken());
            outputTarget(target);
            while (st.hasMoreElements()) {
                index = Integer.parseInt(st.nextToken());
                value = Double.parseDouble(st.nextToken());
                for (i = next_index; i<index; i++) output(i, 0);
                output(index, value);
                next_index = index + 1;
            }

            for (i=next_index;i<= max_index;i++) output(i, 0);
            System.out.print("\n");
        }
        if (newNumNonzeroes > numNonzeroes) {
            System.err.print(
                             "WARNING: original #nonzeros " + numNonzeroes+"\n"
                             +"         new      #nonzeros " + newNumNonzeroes+"\n"
                             +"Use -l 0 if many original feature values are zeros\n");
        }

        fp.close();
    }

    /**
     * Command-line version.
     */
    public static void main(String[] args) throws IOException {

        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        Option lowerOption = new Option("l", true, "x scaling lower limit [-1]");
        lowerOption.setRequired(false);
        options.addOption(lowerOption);

        Option upperOption = new Option("u", true, "x scaling upper limit [+1]");
        upperOption.setRequired(false);
        options.addOption(upperOption);

        Option yScalingOption = new Option("y", true, "y scaling limits yLower,yUpper [no y scaling]");
        yScalingOption.setRequired(false);
        options.addOption(yScalingOption);

        Option saveFileOption = new Option("s", true, "filename to save scaling parameters to");
        saveFileOption.setRequired(false);
        options.addOption(saveFileOption);

        Option restoreFileOption = new Option("r", true, "filename to read scaling parameters from");
        restoreFileOption.setRequired(false);
        options.addOption(restoreFileOption);

        if (args.length==0) {
            formatter.printHelp("SvmScaler", options);
            System.exit(1);
        }
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("SvmScaler", options);
            System.exit(1);
            return;
        }

        // instantiate with defaults
        SvmScaler s = new SvmScaler();
        
        if (cmd.hasOption("l")) {
            s.lower = Double.parseDouble(cmd.getOptionValue("l"));
        }

        if (cmd.hasOption("u")) {
            s.upper = Double.parseDouble(cmd.getOptionValue("u"));
        }

        if (cmd.hasOption("y")) {
            String[] parts = cmd.getOptionValue("y").split(",");
            s.yLower = Double.parseDouble(parts[0]);
            s.yUpper = Double.parseDouble(parts[1]);
            s.yScaling = true;
        }

        if (cmd.hasOption("s")) {
            s.saveFilename = cmd.getOptionValue("s");
        }

        if (cmd.hasOption("r")) {
            s.restoreFilename = cmd.getOptionValue("r");
        }


        // data file is required and last argument
        s.dataFilename = args[args.length-1];

        // validate
        s.validate();
    
        // and go!
        s.run();
    }

    /**
     * Validate current settings.
     */
    void validate() {
        if (saveFilename!=null && restoreFilename!=null) {
            System.err.println("The -s and -r options are mutually exclusive: you may save scaling parameters XOR restore parameters for application to data.");
            System.exit(1);
        }
        if (!(upper>lower)) {
            System.err.println("You have provided inconsistent lower/upper values.");
            System.exit(1);
        }
        if (yScaling && !(yUpper>yLower)) {
            System.err.println("You have provided inconsistent y-scaling values.");
            System.exit(1);
        }
        if (dataFilename==null) {
            System.err.println("You have not provided a data file to scale.");
            System.exit(1);
        }
    }

}
