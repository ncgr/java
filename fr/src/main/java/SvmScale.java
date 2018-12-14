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

public class SvmScale {
    
    // defaults
    double lower = -1.0;
    double upper = 1.0;
    double y_lower;
    double y_upper;
    boolean y_scaling = false;

    // files
    String save_filename;
    String restore_filename;
    String data_filename;

    // I/O
    BufferedReader fp;
    BufferedReader fp_restore;

    double[] feature_max;
    double[] feature_min;
    double y_max = -Double.MAX_VALUE;
    double y_min = Double.MAX_VALUE;
    long num_nonzeros = 0;
    long new_num_nonzeros = 0;

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
        if (y_scaling) {
            if (value == y_min) {
                value = y_lower;
            } else if (value == y_max) {
                value = y_upper;
            } else {
                value = y_lower + (y_upper-y_lower) *  (value-y_min) / (y_max-y_min);
            }
        }
        System.out.print((int)value + " ");
    }

    void output(int index, double value) {
        /* skip single-valued attribute */
        if (feature_max[index] == feature_min[index]) return;
        
        if (value == feature_min[index]) {
            value = lower;
        } else if (value == feature_max[index]) {
            value = upper;
        } else {
            value = lower + (upper-lower) *  (value-feature_min[index])/(feature_max[index]-feature_min[index]);
        }
        if (value != 0) {
            System.out.print(index + ":" + value + " ");
            new_num_nonzeros++;
        }
    }

    String readline(BufferedReader fp) throws IOException {
        line = fp.readLine();
        return line;
    }

    void run() throws IOException {

        try {
            fp = new BufferedReader(new FileReader(data_filename));
        } catch (Exception e) {
            System.err.println("can't open file " + data_filename);
            System.exit(1);
        }

        /* assumption: min index of attributes is 1 */
        /* pass 1: find out max index of attributes */
        max_index = 0;

        if (restore_filename != null) {
            int idx, c;

            try {
                fp_restore = new BufferedReader(new FileReader(restore_filename));
            }
            catch (Exception e) {
                System.err.println("can't open file " + restore_filename);
                System.exit(1);
            }
            if ((c = fp_restore.read()) == 'y') {
                fp_restore.readLine();
                fp_restore.readLine();
                fp_restore.readLine();
            }
            fp_restore.readLine();
            fp_restore.readLine();

            String restore_line = null;
            while ((restore_line = fp_restore.readLine())!=null) {
                StringTokenizer st2 = new StringTokenizer(restore_line);
                idx = Integer.parseInt(st2.nextToken());
                max_index = Math.max(max_index, idx);
            }
            fp_restore = rewind(fp_restore, restore_filename);
        }

        while (readline(fp) != null) {
            StringTokenizer st = new StringTokenizer(line," \t\n\r\f:");
            st.nextToken();
            while (st.hasMoreTokens()) {
                index = Integer.parseInt(st.nextToken());
                max_index = Math.max(max_index, index);
                st.nextToken();
                num_nonzeros++;
            }
        }

        try {
            feature_max = new double[(max_index+1)];
            feature_min = new double[(max_index+1)];
        } catch(OutOfMemoryError e) {
            System.err.println("can't allocate enough memory");
            System.exit(1);
        }

        for (i=0;i<=max_index;i++) {
            feature_max[i] = -Double.MAX_VALUE;
            feature_min[i] = Double.MAX_VALUE;
        }

        fp = rewind(fp, data_filename);

        /* pass 2: find out min/max value */
        while (readline(fp) != null) {
            int next_index = 1;
            double target;
            double value;
            
            StringTokenizer st = new StringTokenizer(line," \t\n\r\f:");
            target = Double.parseDouble(st.nextToken());
            y_max = Math.max(y_max, target);
            y_min = Math.min(y_min, target);
            
            while (st.hasMoreTokens()) {
                index = Integer.parseInt(st.nextToken());
                value = Double.parseDouble(st.nextToken());
                
                for (i = next_index; i<index; i++) {
                    feature_max[i] = Math.max(feature_max[i], 0);
                    feature_min[i] = Math.min(feature_min[i], 0);
                }
                
                feature_max[index] = Math.max(feature_max[index], value);
                feature_min[index] = Math.min(feature_min[index], value);
                next_index = index + 1;
            }

            for (i=next_index;i<=max_index;i++) {
                feature_max[i] = Math.max(feature_max[i], 0);
                feature_min[i] = Math.min(feature_min[i], 0);
            }
        }

        fp = rewind(fp, data_filename);

        /* pass 2.5: save/restore feature_min/feature_max */
        if (restore_filename != null) {
            // fp_restore rewinded in finding max_index
            int idx, c;
            double fmin, fmax;
            
            fp_restore.mark(2);				// for reset
            if ((c = fp_restore.read()) == 'y') {
                fp_restore.readLine();		// pass the '\n' after 'y'
                StringTokenizer st = new StringTokenizer(fp_restore.readLine());
                y_lower = Double.parseDouble(st.nextToken());
                y_upper = Double.parseDouble(st.nextToken());
                st = new StringTokenizer(fp_restore.readLine());
                y_min = Double.parseDouble(st.nextToken());
                y_max = Double.parseDouble(st.nextToken());
                y_scaling = true;
            } else {
                fp_restore.reset();
            }

            if (fp_restore.read() == 'x') {
                fp_restore.readLine();		// pass the '\n' after 'x'
                StringTokenizer st = new StringTokenizer(fp_restore.readLine());
                lower = Double.parseDouble(st.nextToken());
                upper = Double.parseDouble(st.nextToken());
                String restore_line = null;
                while ((restore_line = fp_restore.readLine())!=null) {
                    StringTokenizer st2 = new StringTokenizer(restore_line);
                    idx = Integer.parseInt(st2.nextToken());
                    fmin = Double.parseDouble(st2.nextToken());
                    fmax = Double.parseDouble(st2.nextToken());
                    if (idx <= max_index) {
                        feature_min[idx] = fmin;
                        feature_max[idx] = fmax;
                    }
                }
            }
            fp_restore.close();
        }

        if (save_filename!=null) {
            Formatter formatter = new Formatter(new StringBuilder());
            BufferedWriter fp_save = null;

            try {
                fp_save = new BufferedWriter(new FileWriter(save_filename));
            } catch(IOException e) {
                System.err.println("can't open file " + save_filename);
                System.exit(1);
            }

            if (y_scaling) {
                formatter.format("y\n");
                formatter.format("%.16g %.16g\n", y_lower, y_upper);
                formatter.format("%.16g %.16g\n", y_min, y_max);
            }
            formatter.format("x\n");
            formatter.format("%.16g %.16g\n", lower, upper);
            for (i=1;i<=max_index;i++) {
                if (feature_min[i]!=feature_max[i]) formatter.format("%d %.16g %.16g\n", i, feature_min[i], feature_max[i]);
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
        if (new_num_nonzeros > num_nonzeros) {
            System.err.print(
                             "WARNING: original #nonzeros " + num_nonzeros+"\n"
                             +"         new      #nonzeros " + new_num_nonzeros+"\n"
                             +"Use -l 0 if many original feature values are zeros\n");
        }

        fp.close();
    }

    /**
     * Command-line version.
     */
    public static void main(String[] args) throws IOException {

        if (args.length==0) exitWithHelp();

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

        Option yScalingOption = new Option("y", true, "y scaling limits y_lower,y_upper [no y scaling]");
        yScalingOption.setRequired(false);
        options.addOption(yScalingOption);

        Option saveFileOption = new Option("s", true, "filename to save scaling parameters to");
        saveFileOption.setRequired(false);
        options.addOption(saveFileOption);

        Option restoreFileOption = new Option("r", true, "filename to read scaling parameters from");
        restoreFileOption.setRequired(false);
        options.addOption(restoreFileOption);

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("SvmScale", options);
            System.exit(1);
            return;
        }
        
        SvmScale s = new SvmScale();

        if (cmd.hasOption("l")) {
            s.lower = Double.parseDouble(cmd.getOptionValue("l"));
        }

        if (cmd.hasOption("u")) {
            s.upper = Double.parseDouble(cmd.getOptionValue("u"));
        }

        if (cmd.hasOption("y")) {
            String[] parts = cmd.getOptionValue("y").split(",");
            s.y_lower = Double.parseDouble(parts[0]);
            s.y_upper = Double.parseDouble(parts[1]);
            s.y_scaling = true;
        }

        if (cmd.hasOption("s")) {
            s.save_filename = cmd.getOptionValue("s");
        }

        if (cmd.hasOption("r")) {
            s.restore_filename = cmd.getOptionValue("r");
        }

        // data file is required and last argument
        s.data_filename = args[args.length-1];

        // validate
        s.validate();
    
        // and go!
        s.run();
        
    }

    /**
     * Validate current settings.
     */
    void validate() {
        if (save_filename!=null && restore_filename!=null) {
            System.err.println("The -s and -r options are exclusive: you may save scaling parameters XOR restore parameters for application to data.");
            System.exit(1);
        }
        if (!(upper>lower)) {
            System.err.println("You have provided inconsistent lower/upper values.");
            System.exit(1);
        }
        if (y_scaling && !(y_upper>y_lower)) {
            System.err.println("You have provided inconsistent y_scaling values.");
            System.exit(1);
        }
        if (data_filename==null) {
            System.err.println("You have not provided a data file to scale.");
            System.exit(1);
        }
    }

    static void exitWithHelp() {
        System.out.print(
                         "Usage: SvmScale [options] data_filename\n"
                         +"options:\n"
                         +"-l lower : x scaling lower limit (default -1)\n"
                         +"-u upper : x scaling upper limit (default +1)\n"
                         +"-y y_lower,y_upper : y scaling limits (default: no y scaling)\n"
                         +"-s save_filename : file to save scaling parameters to save_filename\n"
                         +"-r restore_filename : file to restore scaling parameters from\n"
                         );
        System.exit(1);
    }
    
}
