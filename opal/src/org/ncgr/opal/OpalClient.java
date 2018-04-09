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
 * Client for Opal-based Web service; extend to implement specific functionality.
 * 
 * @author Sam Hokin
 */
public abstract class OpalClient {
    
    public static String SERVER_DN = "/C=US/O=nbcr/OU=sdsc/CN=apbs_service";

    AppServicePortType appServicePort;

    /**
     * Construct by initializing the connection to the web service, resulting in an initialized appServicePort.
     */
    public OpalClient(String serviceURL) throws MalformedURLException, ServiceException, FileNotFoundException, IOException, GSSException, RemoteException {
        
    	// connect to the App Web service
	AppServiceLocator asl = new AppServiceLocator();
        
	// register a protocol handler for https, if need be
	int index = serviceURL.indexOf(":");
	boolean httpsInUse = false;
	if (index > 0) {
	    String proto = serviceURL.substring(0, index);
	    if (proto.equals("https")) {
		httpsInUse = true;
	    }
	}
	if (httpsInUse) {
	    SimpleProvider provider = new SimpleProvider();	
	    SimpleTargetedChain c = new SimpleTargetedChain(new HTTPSSender());
	    provider.deployTransport("https", c);
	    asl.setEngine(new AxisClient(provider));
	    Util.registerTransport();
	}

	appServicePort = asl.getAppServicePort(new URL(serviceURL));
        
	// read credentials for the client
	GSSCredential proxy = null;
	if (httpsInUse) {
	    String proxyPath = System.getProperty("X509_USER_PROXY");
	    if (proxyPath == null) {
		System.err.println("Required property X509_USER_PROXY not set");
		System.exit(1);
	    }
	    File f = new File(proxyPath);
	    byte[] proxyData = new byte[(int) f.length()];
	    FileInputStream in = new FileInputStream(f);
	    in.read(proxyData);
	    in.close();
	    ExtendedGSSManager manager = 
		(ExtendedGSSManager) ExtendedGSSManager.getInstance();
	    proxy = manager.createCredential(proxyData,
					     ExtendedGSSCredential.IMPEXP_OPAQUE,
					     GSSCredential.DEFAULT_LIFETIME,
					     null, // use default mechanism - GSI
					     GSSCredential.INITIATE_AND_ACCEPT);
	}

	// set the GSI specific properties
	IdentityAuthorization auth = new IdentityAuthorization(SERVER_DN);
	if (httpsInUse) {
	    ((Stub) appServicePort)._setProperty(GSIConstants.GSI_AUTHORIZATION, auth);
	    ((Stub) appServicePort)._setProperty(GSIConstants.GSI_CREDENTIALS, proxy);
	}

    }

    /**
     * Return the appServicePort
     */
    public AppServicePortType getAppServicePort() {
        return appServicePort;
    }

    /**
     * Check whether appServicePort is initialized; throw RuntimeException if not.
     */
    void checkAppServicePort() throws RuntimeException {
        if (appServicePort==null) {
            throw new RuntimeException("AppServicePort is null - you must run the constructor first.");
        }
    }

    /**
     * Return the app metadata; will throw exception if appServicePort isn't initialized.
     */
    public String getAppMetadata() throws RemoteException, IOException {

        checkAppServicePort();

        AppMetadataType amt = appServicePort.getAppMetadata(new AppMetadataInputType());
        
        TypeDesc typeDesc = amt.getTypeDesc();
        StringWriter sw = new StringWriter();
        MessageContext mc = new MessageContext(new AxisClient());
        SerializationContext sc = new SerializationContext(sw, mc);
        sc.setDoMultiRefs(false);
        sc.setPretty(true);
        sc.serialize(typeDesc.getXmlType(), 
                     null, 
                     amt,
                     typeDesc.getXmlType(), 
                     new Boolean(true), 
                     new Boolean(true));
        sw.close();
        
        return sw.toString();
        
    }

    /**
     * Return the system info; will throw exception if appServicePort isn't initialized.
     */
    public String getSystemInfo() throws RemoteException, IOException {

        checkAppServicePort();

        SystemInfoType sit = appServicePort.getSystemInfo(new SystemInfoInputType());
        
        TypeDesc typeDesc = sit.getTypeDesc();
        StringWriter sw = new StringWriter();
        MessageContext mc = new MessageContext(new AxisClient());
        SerializationContext sc = new SerializationContext(sw, mc);
        sc.setDoMultiRefs(false);
        sc.setPretty(true);
        sc.serialize(typeDesc.getXmlType(), 
                     null, 
                     sit,
                     typeDesc.getXmlType(), 
                     new Boolean(true), 
                     new Boolean(true));
        sw.close();
        
        return sw.toString();

    }

    /**
     * Query job status.
     * 
     * @param jobID of the job of interest
     * @return the StatusOutputType returned from the query
     */
    public StatusOutputType queryStatus(String jobID) throws RemoteException {

        checkAppServicePort();
        return appServicePort.queryStatus(jobID);
    
    }

    /**
     * Get job statistics.
     *
     * @param jobID of the job of interest
     * @return the JobStatisticsType returned from the query
     */
    public JobStatisticsType getStatistics(String jobID) throws RemoteException {

        checkAppServicePort();
        return appServicePort.getJobStatistics(jobID);
        
    }
            
    /**
     * Get job outputs.
     */
    public String getOutputs(String jobID) throws RemoteException {

        checkAppServicePort();

        JobOutputType out = appServicePort.getOutputs(jobID);

        String output = "Standard output:\t"+out.getStdOut().toString()+"\n";
        output += "Standard error:\t\t"+out.getStdErr().toString()+"\n";
        
        OutputFileType[] outfile = out.getOutputFile();
        if (outfile!=null) {
            for (int i=0; i<outfile.length; i++) {
                output += outfile[i].getName()+":\t"+outfile[i].getUrl()+"\n";
            }
        }

        return output;

    }
            
    /**
     * Destroy a running job.
     */
    public String destroy(String jobID) throws RemoteException {

        checkAppServicePort();

        StatusOutputType status = appServicePort.destroy(jobID);

        return "Final status for job: "+jobID+"\n" +
            "\tCode: "+status.getCode()+"\n" +
            "\tMessage: "+status.getMessage()+"\n" +
            "\tOutput Base URL: "+status.getBaseURL();
    }

}    
