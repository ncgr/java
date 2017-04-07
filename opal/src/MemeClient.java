package org.ncgr.opal;

import edu.sdsc.nbcr.opal.AppServiceLocator;
import edu.sdsc.nbcr.opal.AppServicePortType;
import edu.sdsc.nbcr.opal.types.AppMetadataType;
import edu.sdsc.nbcr.opal.types.AppMetadataInputType;
import edu.sdsc.nbcr.opal.types.InputFileType;
import edu.sdsc.nbcr.opal.types.OutputFileType;
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
 * Client for Opal-based meme Web service
 * 
 * @author Sam Hokin
 */
public class MemeClient extends OpalClient {
    
    public static String FASTA_FILENAME = "input.fasta";
    public static int NUM_PROCS = 1;

    /**
     * Construct by initializing the connection to the web service.
     */
    public MemeClient(String serviceURL) throws MalformedURLException, ServiceException, FileNotFoundException, IOException, GSSException, RemoteException {
        super(serviceURL);
    }

    /**
     * Launch a Meme job, passing in FASTA data in a byte[] array.
     *
     * @param   data byte array of FASTA data
     * @return  a JobSubOutputType from launchJob
     */
    public JobSubOutputType launchJob(byte[] data) throws RemoteException {

        checkAppServicePort();

        JobInputType in = new JobInputType();
        
        // have to include the filename on the command line!
        String cmdArgs = FASTA_FILENAME;
        in.setArgList(cmdArgs);
        
        // parallel processing
        if (NUM_PROCS>1) {
            in.setNumProcs(new Integer(NUM_PROCS));
        }
        
        // add the single input "file"
        InputFileType[] infileArray = new InputFileType[1];
        infileArray[0] = new InputFileType();
        infileArray[0].setName(FASTA_FILENAME);
        infileArray[0].setContents(data);
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
            System.out.println("\tMemeClient <serviceURL> getAppMetadata");
            System.out.println("\tMemeClient <serviceURL> getSystemInfo");
            System.out.println("\tMemeClient <serviceURL> launchJob <fasta-file>");
            System.out.println("\tMemeClient <serviceURL> queryStatus <jobID>");
            System.out.println("\tMemeClient <serviceURL> getStatistics <jobID>");
            System.out.println("\tMemeClient <serviceURL> getOutputs <jobID>");
            System.out.println("\tMemeClient <serviceURL> destroy <jobID>");
            System.exit(1);
        }

        String serviceURL = args[0];
        String operation = args[1];

        if (operation.equals("getAppMetadata")) {
            
            MemeClient mc = new MemeClient(serviceURL);
            System.out.println(mc.getAppMetadata());
            
        } else if (operation.equals("getSystemInfo")) {
            
            MemeClient mc = new MemeClient(serviceURL);
            System.out.println(mc.getSystemInfo());
            
        } else if (operation.equals("launchJob")) {

            if (args.length<3) {
                System.out.println("Usage: MemeClient <serviceURL> launchJob <fasta file>");
                System.exit(1);
            }

            // read the file into a byte arrray
            File file = new File(args[2]);
            byte[] data = new byte[(int) file.length()];
            FileInputStream fis = new FileInputStream(file);
            fis.read(data);
            fis.close();

            System.out.println("Read in "+data.length+" bytes from file "+file.getName());
            
            MemeClient mc = new MemeClient(serviceURL);

            JobSubOutputType subOut = mc.launchJob(data);
            StatusOutputType status = subOut.getStatus();
        
            System.out.println("Status for job: "+subOut.getJobID());
            System.out.println("\tCode: " + status.getCode());
            System.out.println("\tMessage: " + status.getMessage());
            System.out.println("\tOutput Base URL: " + status.getBaseURL());

        } else if (operation.equals("queryStatus")) {

            if (args.length<3) {
                System.out.println("Usage: MemeClient <serviceURL> queryStatus <jobID>");
                System.exit(1);
            }

            String jobID = args[2];

            MemeClient mc = new MemeClient(serviceURL);
            StatusOutputType status = mc.queryStatus(jobID);

            System.out.println("Status for job: "+jobID);
            System.out.println("\tCode: "+status.getCode());
            System.out.println("\tMessage: "+status.getMessage());
            System.out.println("\tOutput Base URL: "+status.getBaseURL());

        } else if (operation.equals("getStatistics")) {

            if (args.length<3) {
                System.out.println("Usage: MemeClient <serviceURL> getStatistics <jobID>");
                System.exit(1);
            }

            String jobID = args[2];

            MemeClient mc = new MemeClient(serviceURL);
            JobStatisticsType stats = mc.getStatistics(jobID);

            String output = "Statistics for job: "+jobID+"\n";
            output += "\tSubmission time: "+stats.getStartTime().getTime()+"\n";
            if (stats.getActivationTime() != null) output += "\tActivation time: "+stats.getActivationTime().getTime()+"\n";
            if (stats.getCompletionTime() != null) output += "\tCompletion time: "+stats.getCompletionTime().getTime()+"\n";
            System.out.println(output);

        } else if (operation.equals("getOutputs")) {

            if (args.length<3) {
                System.out.println("Usage: MemeClient <serviceURL> getOutputs <jobID>");
                System.exit(1);
            }

            MemeClient mc = new MemeClient(serviceURL);
            System.out.println(mc.getOutputs(args[2]));

        } else if (operation.equals("destroy")) {

            if (args.length<3) {
                System.out.println("Usage: MemeClient <serviceURL> destroy <jobID>");
                System.exit(1);
            }

            MemeClient mc = new MemeClient(serviceURL);
            System.out.println(mc.destroy(args[2]));

        }

    }
    
}    
