package org.ncgr.opal;

import edu.sdsc.nbcr.opal.AppServiceLocator;
import edu.sdsc.nbcr.opal.AppServicePortType;
import edu.sdsc.nbcr.opal.types.AppMetadataType;
import edu.sdsc.nbcr.opal.types.AppMetadataInputType;
import edu.sdsc.nbcr.opal.types.InputFileType;
import edu.sdsc.nbcr.opal.types.JobInputType;
import edu.sdsc.nbcr.opal.types.JobOutputType;
import edu.sdsc.nbcr.opal.types.JobStatisticsType;
import edu.sdsc.nbcr.opal.types.JobSubOutputType;
import edu.sdsc.nbcr.opal.types.StatusOutputType;
import edu.sdsc.nbcr.opal.types.SystemInfoType;
import edu.sdsc.nbcr.opal.types.SystemInfoInputType;

import java.net.URL;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.Stub;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import org.apache.axis.client.Call;
import org.apache.axis.client.AxisClient;
import org.apache.axis.SimpleTargetedChain;
import org.apache.axis.configuration.SimpleProvider;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.MessageContext;
import org.apache.axis.types.URI;

import org.globus.gram.GramJob;
import org.globus.axis.util.Util;
import org.globus.axis.gsi.GSIConstants;
import org.globus.axis.transport.GSIHTTPSender;
import org.globus.axis.transport.HTTPSSender;
import org.globus.axis.gsi.GSIConstants;
import org.globus.gsi.gssapi.auth.IdentityAuthorization;

import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.gridforum.jgss.ExtendedGSSManager;
import org.gridforum.jgss.ExtendedGSSCredential;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.net.MalformedURLException;

import java.util.Vector;

import java.rmi.RemoteException;

/**
 * Client for Opal-based Seqlogo Web service
 * 
 * @author Sam Hokin
 */
public class SeqlogoClient extends OpalClient {
    
    public static int NUM_PROCS = 1;

    public static String FASTA_FILENAME = "alignment.fasta";
    public static String OUTPUT_ROOT = "alignment";

    /**
     * Construct by initializing the connection to the web service.
     */
    public SeqlogoClient(String serviceURL) throws MalformedURLException, ServiceException, FileNotFoundException, IOException, GSSException, RemoteException {
        super(serviceURL);
    }

    /**
     * Launch a seqlogo job, passing in FASTA data in byte[] arrays.
     *
     * @param   fastaData byte array of FASTA alignments
     * @param   title the title placed on the image; null to skip
     * @return  a JobSubOutputType from launchJob
     */
    public JobSubOutputType launchJob(byte[] fastaData, String title) throws RemoteException {

        checkAppServicePort();

        JobInputType in = new JobInputType();
        
        // parallel processing
        if (NUM_PROCS>1) {
            in.setNumProcs(new Integer(NUM_PROCS));
        }

        // have to include the file names on the command line!
        String cmdArgs = "-f "+FASTA_FILENAME+" -o "+OUTPUT_ROOT;
        if (title!=null) cmdArgs += " -t "+title;
        in.setArgList(cmdArgs);

        // add subject and query "files" to the input
        InputFileType[] infileArray = new InputFileType[1];
        
        infileArray[0] = new InputFileType();
        infileArray[0].setName(FASTA_FILENAME);
        infileArray[0].setContents(fastaData);

        in.setInputFile(infileArray);

        // set up a non-blocking call
        JobSubOutputType subOut = appServicePort.launchJob(in);

        return subOut;
	
    }
        
    /**
     * Command-line version, mainly for testing.
     */
    public static void main(String[] args) throws Exception {

        if (args.length<2) {
            System.out.print("Usage:");
            System.out.println("\tSeqlogoClient <serviceURL> getAppMetadata");
            System.out.println("\tSeqlogoClient <serviceURL> getSystemInfo");
            System.out.println("\tSeqlogoClient <serviceURL> launchJob <fasta-file>");
            System.out.println("\tSeqlogoClient <serviceURL> queryStatus <jobID>");
            System.out.println("\tSeqlogoClient <serviceURL> getStatistics <jobID>");
            System.out.println("\tSeqlogoClient <serviceURL> getOutputs <jobID>");
            System.out.println("\tSeqlogoClient <serviceURL> destroy <jobID>");
            System.exit(0);
        }

        String serviceURL = args[0];
        String operation = args[1];

        if (operation.equals("getAppMetadata")) {
            
            SeqlogoClient client = new SeqlogoClient(serviceURL);
            System.out.println(client.getAppMetadata());
            
        } else if (operation.equals("getSystemInfo")) {
            
            SeqlogoClient client = new SeqlogoClient(serviceURL);
            System.out.println(client.getSystemInfo());
            
        } else if (operation.equals("launchJob")) {

            if (args.length<3) {
                System.out.println("Usage: SeqlogoClient <serviceURL> launchJob <fasta-file> [title]");
                System.exit(0);
            }

            // leave title null to skip that option
            String title = null;
            if (args.length>3) title = args[3];

            // read the input file into a byte array
            File fastaFile = new File(args[2]);
            byte[] fastaData = new byte[(int) fastaFile.length()];
            FileInputStream subjectInputStream = new FileInputStream(fastaFile);
            subjectInputStream.read(fastaData);
            subjectInputStream.close();
            System.out.println("Read in "+fastaData.length+" bytes from file "+fastaFile.getName());

            SeqlogoClient client = new SeqlogoClient(serviceURL);

            JobSubOutputType subOut = client.launchJob(fastaData, title);
            StatusOutputType status = subOut.getStatus();
        
            System.out.println("Status for job: "+subOut.getJobID());
            System.out.println("\tCode: " + status.getCode());
            System.out.println("\tMessage: " + status.getMessage());
            System.out.println("\tOutput Base URL: " + status.getBaseURL());

        } else if (operation.equals("queryStatus")) {

            if (args.length<3) {
                System.out.println("Usage: SeqlogoClient <serviceURL> queryStatus <jobID>");
                System.exit(0);
            }

            String jobID = args[2];

            SeqlogoClient client = new SeqlogoClient(serviceURL);
            StatusOutputType status = client.queryStatus(jobID);

            System.out.println("Status for job: "+jobID);
            System.out.println("\tCode: "+status.getCode());
            System.out.println("\tMessage: "+status.getMessage());
            System.out.println("\tOutput Base URL: "+status.getBaseURL());

        } else if (operation.equals("getStatistics")) {

            if (args.length<3) {
                System.out.println("Usage: SeqlogoClient <serviceURL> getStatistics <jobID>");
                System.exit(0);
            }

            String jobID = args[2];

            SeqlogoClient client = new SeqlogoClient(serviceURL);
            JobStatisticsType stats = client.getStatistics(jobID);

            String output = "Statistics for job: "+jobID+"\n";
            output += "\tSubmission time: "+stats.getStartTime().getTime()+"\n";
            if (stats.getActivationTime() != null) output += "\tActivation time: "+stats.getActivationTime().getTime()+"\n";
            if (stats.getCompletionTime() != null) output += "\tCompletion time: "+stats.getCompletionTime().getTime()+"\n";
            System.out.println(output);

        } else if (operation.equals("getOutputs")) {

            if (args.length<3) {
                System.out.println("Usage: SeqlogoClient <serviceURL> getOutputs <jobID>");
                System.exit(0);
            }

            SeqlogoClient client = new SeqlogoClient(serviceURL);
            System.out.println(client.getOutputs(args[2]));

        } else if (operation.equals("destroy")) {

            if (args.length<3) {
                System.out.println("Usage: SeqlogoClient <serviceURL> destroy <jobID>");
                System.exit(0);
            }

            SeqlogoClient client = new SeqlogoClient(serviceURL);
            System.out.println(client.destroy(args[2]));

        }

    }
    
}    
