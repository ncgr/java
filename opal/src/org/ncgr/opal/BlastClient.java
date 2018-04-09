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
 * Client for Opal-based BLAST Web service
 * 
 * @author Sam Hokin
 */
public class BlastClient extends OpalClient {
    
    public static int NUM_PROCS = 1;

    public static String QUERY_FILENAME = "query.fasta";
    public static String SUBJECT_FILENAME = "subject.fasta";
    public static String OUTPUT_FILENAME = "output.xml";

    /**
     * Construct by initializing the connection to the web service.
     */
    public BlastClient(String serviceURL) throws MalformedURLException, ServiceException, FileNotFoundException, IOException, GSSException, RemoteException {
        super(serviceURL);
    }

    /**
     * Launch a Blast job, passing in FASTA data in byte[] arrays.
     *
     * @param   subjectData byte array of FASTA subject data for blast run
     * @param   queryData   byte array of FASTA query data for blast run
     * @return  a JobSubOutputType from launchJob
     */
    public JobSubOutputType launchJob(byte[] subjectData, byte[] queryData) throws RemoteException {

        checkAppServicePort();

        JobInputType in = new JobInputType();
        
        // parallel processing
        if (NUM_PROCS>1) {
            in.setNumProcs(new Integer(NUM_PROCS));
        }

        // have to include the file names on the command line!
        String cmdArgs = "-subject "+SUBJECT_FILENAME+" -query "+QUERY_FILENAME+" -out "+OUTPUT_FILENAME;
        in.setArgList(cmdArgs);
        
        // add subject and query "files" to the input
        InputFileType[] infileArray = new InputFileType[2];
        
        infileArray[0] = new InputFileType();
        infileArray[0].setName(QUERY_FILENAME);
        infileArray[0].setContents(queryData);

        infileArray[1] = new InputFileType();
        infileArray[1].setName(SUBJECT_FILENAME);
        infileArray[1].setContents(subjectData);

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
            System.out.println("\tBlastClient <serviceURL> getAppMetadata");
            System.out.println("\tBlastClient <serviceURL> getSystemInfo");
            System.out.println("\tBlastClient <serviceURL> launchJob <subject-file> <query-file>");
            System.out.println("\tBlastClient <serviceURL> queryStatus <jobID>");
            System.out.println("\tBlastClient <serviceURL> getStatistics <jobID>");
            System.out.println("\tBlastClient <serviceURL> getOutputs <jobID>");
            System.out.println("\tBlastClient <serviceURL> destroy <jobID>");
            System.exit(1);
        }

        String serviceURL = args[0];
        String operation = args[1];

        if (operation.equals("getAppMetadata")) {
            
            BlastClient bc = new BlastClient(serviceURL);
            System.out.println(bc.getAppMetadata());
            
        } else if (operation.equals("getSystemInfo")) {
            
            BlastClient bc = new BlastClient(serviceURL);
            System.out.println(bc.getSystemInfo());
            
        } else if (operation.equals("launchJob")) {

            if (args.length<4) {
                System.out.println("Usage: BlastClient <serviceURL> launchJob <subject-file> <query-file>");
                System.exit(1);
            }

            // read the subject file into a byte array
            File subjectFile = new File(args[2]);
            byte[] subjectData = new byte[(int) subjectFile.length()];
            FileInputStream subjectInputStream = new FileInputStream(subjectFile);
            subjectInputStream.read(subjectData);
            subjectInputStream.close();
            System.out.println("Read in "+subjectData.length+" bytes from file "+subjectFile.getName());

            // read the query file into a byte array
            File queryFile = new File(args[3]);
            byte[] queryData = new byte[(int) queryFile.length()];
            FileInputStream queryInputStream = new FileInputStream(queryFile);
            queryInputStream.read(queryData);
            queryInputStream.close();
            System.out.println("Read in "+queryData.length+" bytes from file "+queryFile.getName());

            BlastClient bc = new BlastClient(serviceURL);

            JobSubOutputType subOut = bc.launchJob(subjectData, queryData);
            StatusOutputType status = subOut.getStatus();
        
            System.out.println("Status for job: "+subOut.getJobID());
            System.out.println("\tCode: " + status.getCode());
            System.out.println("\tMessage: " + status.getMessage());
            System.out.println("\tOutput Base URL: " + status.getBaseURL());

        } else if (operation.equals("queryStatus")) {

            if (args.length<3) {
                System.out.println("Usage: BlastClient <serviceURL> queryStatus <jobID>");
                System.exit(1);
            }

            String jobID = args[2];

            BlastClient bc = new BlastClient(serviceURL);
            StatusOutputType status = bc.queryStatus(jobID);

            System.out.println("Status for job: "+jobID);
            System.out.println("\tCode: "+status.getCode());
            System.out.println("\tMessage: "+status.getMessage());
            System.out.println("\tOutput Base URL: "+status.getBaseURL());

        } else if (operation.equals("getStatistics")) {

            if (args.length<3) {
                System.out.println("Usage: BlastClient <serviceURL> getStatistics <jobID>");
                System.exit(1);
            }

            String jobID = args[2];

            BlastClient bc = new BlastClient(serviceURL);
            JobStatisticsType stats = bc.getStatistics(jobID);

            String output = "Statistics for job: "+jobID+"\n";
            output += "\tSubmission time: "+stats.getStartTime().getTime()+"\n";
            if (stats.getActivationTime() != null) output += "\tActivation time: "+stats.getActivationTime().getTime()+"\n";
            if (stats.getCompletionTime() != null) output += "\tCompletion time: "+stats.getCompletionTime().getTime()+"\n";
            System.out.println(output);

        } else if (operation.equals("getOutputs")) {

            if (args.length<3) {
                System.out.println("Usage: BlastClient <serviceURL> getOutputs <jobID>");
                System.exit(1);
            }

            BlastClient bc = new BlastClient(serviceURL);
            System.out.println(bc.getOutputs(args[2]));

        } else if (operation.equals("destroy")) {

            if (args.length<3) {
                System.out.println("Usage: BlastClient <serviceURL> destroy <jobID>");
                System.exit(1);
            }

            BlastClient bc = new BlastClient(serviceURL);
            System.out.println(bc.destroy(args[2]));

        }

    }
    
}    
