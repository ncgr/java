package org.ncgr.pangenomics.fr;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Contains methods to post-process FRFinder output.
 */
public class PostProcessor {

    /**
     * Command-line utility
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {

        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        //
        Option inputfileOption = new Option("i", "inputfile", true, "input file (output from FRFinder)");
        inputfileOption.setRequired(false);
        options.addOption(inputfileOption);
        //
        Option outputfileOption = new Option("o", "outputfile", true, "output file (stdout)");
        outputfileOption.setRequired(false);
        options.addOption(outputfileOption);
        //
        Option removeChildrenOption = new Option("rc", "removechildren", false, "remove child FRs");
        removeChildrenOption.setRequired(false);
        options.addOption(removeChildrenOption);

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp("PostProcessor", options);
            System.exit(1);
            return;
        }

        // instantiate an FRFinder with the FRs read from inputfile
        String inputFile = cmd.getOptionValue("inputfile");
        FRFinder frf = new FRFinder(inputFile);
        frf.readParameters();
        if (cmd.hasOption("outputfile")) {
            frf.setOutputFile(cmd.getOptionValue("outputfile"));
        }

        // // do the deed
        // if (cmd.hasOption("removechildren")) {
        //     frf.setRemoveChildren();
        //     frf.removeChildren();
        // }

        // print out the parameters to stdout or outputFile+".params" if exists
        frf.printParameters();

        // output the result
        frf.printFrequentedRegions();
    }

}
