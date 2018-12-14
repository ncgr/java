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
    int c_step = 2;
    int g_begin = 3;
    int g_end = -15;
    int g_step = -2;  

    // command-line operation
    public static void main(String[] args) {
        
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        if (args.length==0) {
            System.out.println("Usage:");
            System.out.println("Grid [options]");
            System.exit(1);
        }

        // Usage: grid.py [grid_options] [svm_options] dataset
        // svm_options : additional options for svm-train""")

        Option log2cOption = new Option("log2c", true, "set range/step of C [-5,15,2]");
        log2cOption.setRequired(false);
        options.addOption(log2cOption);

        Option log2gOption = new Option("log2g", true, "set range/step of gamma [3,-15,-2]");
        log2gOption.setRequired(false);
        options.addOption(log2gOption);

        Option vOption = new Option("v", true, "n-fold cross validation [5]");
        vOption.setRequired(false);
        options.addOption(vOption);

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("Grid", options);
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

        if (cmd.hasOption("v")) {
            gs.fold = Integer.parseInt(cmd.getOptionValue("v"));
            System.out.println(gs.fold+"-fold cross validation");
        }

        // data file is last argument
        String datafile = args[args.length-1];
        System.out.println("data:"+datafile);
        
    }
    
}
