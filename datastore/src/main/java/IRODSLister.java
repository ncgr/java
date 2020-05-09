import org.ncgr.datastore.IRODSParameters;
import org.ncgr.datastore.LISFile;
import org.ncgr.datastore.Readme;

import java.io.File;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.packinstr.TransferOptions;
import org.irods.jargon.core.pub.DataTransferOperations;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.pub.io.IRODSFileFactory;
import org.irods.jargon.core.transfer.DefaultTransferControlBlock;

import org.irods.jargon.core.exception.AuthenticationException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.exception.OverwriteException;

/**
 * No-package command-line utility to list files in the iRODS datastore.
 */
public class IRODSLister {

    static String IRODS_PROPERTIES_FILE = "irods.properties";

    public static void main(String[] args) {

        if (args.length!=1) {
            System.out.println("Usage: IRODSLister <iplant directory>");
            System.out.println("Example: IRODSLister /iplant/home/shared/Legume_Federation/Cajanus_cajan");
            System.exit(1);
        }
        
        String iRODSDirectory = args[0];
        IRODSFileSystem iRODSFileSystem = null;

        try {

            // iRODS genome README data
            String identifier = null;
            String genotype = null;
            String source = null;
            String provenance = null;

            // iRODS FASTA files
            LISFile unmaskedFasta = null;
            LISFile softmaskedFasta = null;
            LISFile hardmaskedFasta = null;

            // get the iRODS parameters
            IRODSParameters iRODSParams = new IRODSParameters(IRODS_PROPERTIES_FILE);
            
            // instantiate the IRODSFileSystem object
            iRODSFileSystem = IRODSFileSystem.instance();
            System.out.println("");
            System.out.println("IRODSFileSystem instantiated.");

            // instantiate our iRODS account
            IRODSAccount irodsAccount = new IRODSAccount(iRODSParams.getHost(), iRODSParams.getPort(), iRODSParams.getUser(), iRODSParams.getPassword(),
                                                         iRODSParams.getHomeDirectory(), iRODSParams.getUserZone(), iRODSParams.getDefaultStorageResource());
            System.out.println("iRODS Account:\t"+irodsAccount.toURI(false));

            // data transfer ops and iRODS file factory
            DataTransferOperations dataTransferOperations = iRODSFileSystem.getIRODSAccessObjectFactory().getDataTransferOperations(irodsAccount);
            IRODSFileFactory irodsFileFactory = iRODSFileSystem.getIRODSFileFactory(irodsAccount);

            // now get the data from the requested iRODS directory
            IRODSFile irodsFile = irodsFileFactory.instanceIRODSFile(iRODSDirectory);
            printLine(irodsFile);

            File[] files = irodsFile.listFiles();
            for (int i=0; i<files.length; i++) {
                printLine(files[i]);
                if (files[i].isDirectory()) {
                    IRODSFile subFile = irodsFileFactory.instanceIRODSFile(iRODSDirectory+"/"+files[i].getName());
                    File[] subFiles = subFile.listFiles();
                    for (int j=0; j<subFiles.length; j++) {
                        printLine(subFiles[j]);
                    }
                }
            }

        } catch (AuthenticationException e) {
            System.out.println("Your username/password combination is invalid.");
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            if (iRODSFileSystem!=null) {
                try {
                    iRODSFileSystem.close();
                    System.out.println("");
                    System.out.println("IRODSFileSystem closed.");
                } catch (JargonException e) {
                    System.err.println(e);
                }
            }
        }

    }

    /**
     * Informatativational output
     */
    static void printLine(File file) {
        printLine(new LISFile(file));
    }

    /**
     * Informatativational output
     */
    static void printLine(IRODSFile file) {
        printLine(new LISFile(file));
    }

    /**
     * Informatativational output
     */
    static void printLine(LISFile lisFile) {
        if (lisFile.isDirectory()) {
            System.out.println("");
            System.out.println(lisFile.getDirType()+"\t"+lisFile.getAbsolutePath());
        } else if (lisFile.isFile()) {
            System.out.println(lisFile.length()+"\t"+lisFile.getAbsolutePath());
        } else {
            System.out.println("??????\t"+lisFile.getAbsolutePath());
        }
    }

}
