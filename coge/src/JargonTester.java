import org.ncgr.irods.IRODSParameters;
import org.ncgr.irods.LISFile;

import java.io.File;
import java.io.FileInputStream;
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
 * Test the Jargon API. Loads iRODS connection properties from properties file "irods.properties".
 */
public class JargonTester {

    static String PROPERTIES_FILE = "irods.properties";

    public static void main(String[] args) {

        if (args.length!=2) {
            System.out.println("Usage: JargonTester Genus species");
            System.out.println("Example: JargonTester Cajanus cajan");
            System.exit(1);
        }

        String genus = args[0];
	String species = args[1];
	String directory = "/iplant/home/shared/Legume_Federation/"+genus+"_"+species;

        IRODSFileSystem iRODSFileSystem = null;

        try {
            
            IRODSParameters params = new IRODSParameters(PROPERTIES_FILE);
            
            iRODSFileSystem = IRODSFileSystem.instance();
            System.out.println("IRODSFileSystem instantiated.");

            IRODSAccount irodsAccount = new IRODSAccount(params.getHost(), params.getPort(), params.getUser(), params.getPassword(),
                                                         params.getHomeDirectory(), params.getUserZone(), params.getDefaultStorageResource());
            System.out.println(irodsAccount.toURI(false));
            System.out.println("\tisAnonymousAccount\t"+irodsAccount.isAnonymousAccount());
            System.out.println("\tisDefaultObfuscate\t"+irodsAccount.isDefaultObfuscate());

            DataTransferOperations dataTransferOperations = iRODSFileSystem.getIRODSAccessObjectFactory().getDataTransferOperations(irodsAccount);

            IRODSFileFactory irodsFileFactory = iRODSFileSystem.getIRODSFileFactory(irodsAccount);

            System.out.println(directory);
            IRODSFile irodsFile = irodsFileFactory.instanceIRODSFile(directory);
            File[] files = irodsFile.listFiles();
            for (int i=0; i<files.length; i++) {
                String fileType = null;
                if (files[i].isFile()) fileType = "file";
                if (files[i].isDirectory()) fileType = "directory";
                LISFile lisFile = new LISFile(files[i]);
                // output
                System.out.print("\t"+fileType+"\t"+lisFile.getName()); 
                if (lisFile.isGenomeDir()) System.out.print("\tGENOME DIRECTORY");
                if (lisFile.isAnnotationDir()) System.out.print("\tANNOTATION DIRECTORY");
                if (lisFile.isDiversityDir()) System.out.print("\tDIVERSITY DIRECTORY");
                if (lisFile.isSyntenyDir()) System.out.print("\tSYNTENY DIRECTORY");
                if (lisFile.isTranscriptomeDir()) System.out.print("\tTRANSCRIPTOME DIRECTORY");
                if (lisFile.isBACDir()) System.out.print("\tBAC DIRECTORY");
                if (lisFile.isMarkerDir()) System.out.print("\tMARKER DIRECTORY");
                if (lisFile.isVariantDir()) System.out.print("\tVARIANT DIRECTORY");
                System.out.println("");
                // drill deeper
                if (files[i].isDirectory()) {
                    IRODSFile subFile = irodsFileFactory.instanceIRODSFile(directory+"/"+lisFile.getName());
                    File[] subFiles = subFile.listFiles();
                    for (int j=0; j<subFiles.length; j++) {
                        String subFileType = null;
                        if (subFiles[j].isFile()) subFileType = "file";
                        if (subFiles[j].isDirectory()) subFileType = "directory";
                        LISFile subLISFile = new LISFile(subFiles[j]);
                        // output
                        System.out.print("\t\t"+subFileType+"\t\t"+subLISFile.getName());
                        
                        if (subLISFile.isFasta()) System.out.print("\tFASTA");
                        if (subLISFile.isHardMaskedFasta()) System.out.print("\tHARDMASKED");
                        if (subLISFile.isSoftMaskedFasta()) System.out.print("\tSOFTMASKED");
                        
                        if (subLISFile.isCDSFasta()) System.out.print("\tCDS");
                        if (subLISFile.isCDSPrimaryTranscriptOnlyFasta()) System.out.print("\tCDS Primary Transcripts Only");
                        
                        if (subLISFile.isProteinFasta()) System.out.print("\tPROTEIN");
                        if (subLISFile.isProteinPrimaryTranscriptOnlyFasta()) System.out.print("\tPROTEIN Primary Transcripts Only");

                        if (subLISFile.isTranscriptFasta()) System.out.print("\tTRANSCRIPTS");
                        if (subLISFile.isTranscriptPrimaryTranscriptOnlyFasta()) System.out.print("\tTRANSCRIPTS Primary Transcripts Only");

                        if (subLISFile.isGFF()) System.out.print("\tGFF");
                        if (subLISFile.isGeneGFF()) System.out.print("\tGENES");
                        if (subLISFile.isGeneExonsGFF()) System.out.print("\tGENES+EXONS");

                        if (subLISFile.isReadme()) {
                            System.out.print("\tREADME");
                            IRODSFile sourceFile = irodsFileFactory.instanceIRODSFile(directory+"/"+files[i].getName()+"/"+subLISFile.getName());
                            File localFile = new File(subLISFile.getName());
                            try {
                                dataTransferOperations.getOperation(sourceFile, localFile, null, null);
                                System.out.print("\tCOPIED TO LOCAL DIRECTORY.");
                            } catch (OverwriteException oe) {
                                System.out.print("\tFILE ALREADY IN LOCAL DIRECTORY.");
                            }
                        }
                        
                        System.out.println("");
                    }
                }
            }
            
        } catch (AuthenticationException e) {
            System.out.println("Your username/password combination is invalid.");
        } catch (Exception e) {
            System.err.println(e);
        }

        if (iRODSFileSystem!=null) {
            try {
                iRODSFileSystem.close();
                System.out.println("IRODSFileSystem closed.");
            } catch (JargonException e) {
                System.err.println(e);
            }
        }

    }

}
