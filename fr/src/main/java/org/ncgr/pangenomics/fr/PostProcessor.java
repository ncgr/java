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

    // parameter defaults
    static int MINSUP = 1;
    static int MAXSUP = Integer.MAX_VALUE;
    static int MINSIZE = 1;
    static int MINLEN = 1;
 
    // optional parameters, set with setters
    int minSup = MINSUP;   // minimum support: minimum number of genome paths (fr.support) for an FR to be considered interesting
    int maxSup = MAXSUP;   // maximum support: maximum number of genome paths (fr.support) for an FR to be considered interesting
    int minSize = MINSIZE; // minimum size: minimum number of de Bruijn nodes (fr.nodes.size()) that an FR must contain to be considered interesting
    int minLen = MINLEN;   // minimum average length of a frequented region's subpath sequences (fr.avgLength) to be considered interesting
    String outputPrefix = null; // output file for FRs (stdout if null)

    /**
     * Command-line utility
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {

        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        Option outputprefixOption = new Option("o", "outputprefix", true, "output file prefix from FRFinder run");
        outputprefixOption.setRequired(false);
        options.addOption(outputprefixOption);
        //
        Option minLenOption = new Option("l", "minlen", true, "minlen=minimum allowed average length (bp) of an FR's subpaths ("+MINLEN+")");
        minLenOption.setRequired(false);
        options.addOption(minLenOption);
        //
        Option minSupOption = new Option("m", "minsup", true, "minsup=minimum number of supporting paths for a region to be considered interesting ("+MINSUP+")");
        minSupOption.setRequired(false);
        options.addOption(minSupOption);
        //
        Option maxSupOption = new Option("n", "maxsup", true, "maxsup=maximum number of supporting paths for a region to be considered interesting ("+MAXSUP+")");
        maxSupOption.setRequired(false);
        options.addOption(maxSupOption);
        //
        Option minSizeOption = new Option("s", "minsize", true, "minsize=minimum number of nodes that a FR must contain to be considered interesting ("+MINSIZE+")");
        minSizeOption.setRequired(false);
        options.addOption(minSizeOption);

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp("PostProcessor", options);
            System.exit(1);
            return;
        }

        // // instantiate an FRFinder with the FRs read from frfile
        // String outputPrefix = null;
        // if (cmd.hasOption("outputprefix")) {
        //     outputPrefix = cmd.getOptionValue("outputprefix");
        // } else {
        //     System.err.println("-o/--outprefix is required");
        //     System.exit(1);
        // }
        
        // // load the previous FRFinder state from the output files
        // FRFinder frf = new FRFinder(outputPrefix);

        // //
        // // grab the maps and stuff and run a post-process and then print out the results
        // //
    }

}
