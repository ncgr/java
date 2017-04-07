import org.ncgr.irods.IRODSParameters;
import org.ncgr.irods.LISFile;
import org.ncgr.irods.Readme;

import org.coge.api.CoGe;
import org.coge.api.CoGeObject;
import org.coge.api.CoGeParameters;
import org.coge.api.CoGeResponse;
import org.coge.api.Genome;
import org.coge.api.Organism;

import java.io.File;

import java.util.List;
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
 * Submit genome annotation to CoGe for genomes that are already there and match up.
 */
public class IRODSCoGeAnnotation {

    static String IRODS_PROPERTIES_FILE = "irods.properties";
    static String COGE_PROPERTIES_FILE = "coge.properties";

    public static void main(String[] args) {

        if (args.length!=2) {
            System.out.println("Usage: IRODSAnnotateCoGe <iplant directory> <CoGe organism name>");
            System.out.println("Example: IRODSAnnotateCoGe /iplant/home/shared/Legume_Federation/Cajanus_cajan \"Cajanus cajan\"");
            System.exit(1);
        }

        String iRODSDirectory = args[0];
        String cogeOrganismName = args[1];
        
        IRODSFileSystem iRODSFileSystem = null;

        try {

            // iRODS annotation README data
            String identifier = null;
            String genotype = null;
            String source = null;
            String provenance = null;
            String subject = null;

            // iRODS annotation files
            LISFile geneGFF = null;

            // get the CoGe auth params and initialize token
            CoGeParameters cogeParams = new CoGeParameters(COGE_PROPERTIES_FILE);

            // initialize Agave/CoGe token
            try {
                cogeParams.initializeToken();
            } catch (Exception e) {
                System.err.println("Error initializing token:");
                System.err.println(e.toString());
                System.exit(1);
            }
            
            if (cogeParams.hasToken()) {
                System.out.println("");
                System.out.println("CoGe baseURL:\t"+cogeParams.getBaseURL());
                System.out.println("CoGe Token:\t"+cogeParams.getToken());
            } else {
                System.err.println("Error: couldn't get CoGe token.");
                System.exit(1);
            }
            
            // instantiate our CoGe workhorse
            CoGe coge = new CoGe(cogeParams.getBaseURL(), cogeParams.getUser(), cogeParams.getToken());

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
                if (files[i].isDirectory()) {
                    // we only want genomes and annotation
                    LISFile g = new LISFile(files[i]);
                    if (g.isAnnotationDir()) {
                        printLine(files[i]);
                        IRODSFile subFile = irodsFileFactory.instanceIRODSFile(iRODSDirectory+"/"+files[i].getName());
                        File[] subFiles = subFile.listFiles();
                        for (int j=0; j<subFiles.length; j++) {
                            LISFile f = new LISFile(subFiles[j]);
                            // gather the GFF files
                            if (f.isGeneGFF()) geneGFF = f;
                            // download the README.md
                            if (f.isReadme()) {
                                IRODSFile sourceFile = irodsFileFactory.instanceIRODSFile(iRODSDirectory+"/"+files[i].getName()+"/"+subFiles[j].getName());
                                File localFile = new File(subFiles[j].getName());
                                try {
                                    dataTransferOperations.getOperation(sourceFile, localFile, null, null);
                                } catch (OverwriteException oe) {
                                    localFile = new File(localFile.getName());
                                }
                                Readme readme = new Readme(localFile);
                                identifier = readme.getContent("Identifier");
                                genotype = readme.getContent("Genotype");
                                source = readme.getContent("Source");
                                provenance = readme.getContent("Provenance");
                                subject = readme.getContent("Subject");
                            }

                        }
                    }
                }
            }

            // Hopefully we found everything
            if (identifier!=null && genotype!=null && source!=null && provenance!=null) {

                // CoGe Feature Add terms
                // form the version from the GFF filename
                String name = genotype;
                String description = provenance;
                String sourceName = source;
                String[] parts = geneGFF.getName().split("."+identifier+".");
                String version = parts[0];
                System.out.println("");
                System.out.println("CoGe Feature Add (annotation) data:");
                System.out.println("");
                System.out.println("name:\t"+name);
                System.out.println("description:\t"+description);
                System.out.println("version:\t"+version);
                System.out.println("source_name:\t"+sourceName);
                System.out.println("gene_exons:\t"+geneGFF.getAbsolutePath());

                // now for the CoGe Genome Add
                List<Organism> organisms = coge.searchOrganism(cogeOrganismName);
                if (organisms.size()==0) {
                    System.out.println("");
                    System.out.println("CoGe organism NOT FOUND.");
                } else if (organisms.size()>1) {
                    System.out.println("");
                    System.out.println("Multiple CoGe organisms found. Refine your CoGe organism string.");
                } else {
                    
                    Organism organism = organisms.get(0);
                    System.out.println("");
                    System.out.println("Single CoGe organism FOUND.");
                    System.out.println("Organism Name:\t"+organism.getName());
                    System.out.println("Organism ID:\t"+organism.getId());
                    System.out.println("Organism Description:\t"+organism.getDescription());

                    // spin through the genomes with this name and version; don't include deleted genomes
                    List<Genome> genomes = coge.searchGenome(name, false);
                    if (genomes.size()>0) {
                        for (Genome g : genomes) {
                            if (g.getName().equals(name) && g.getVersion().equals(version)) {
                                System.out.println("");
                                System.out.println("Adding gene features to genome name:"+name+"\tversion:"+version);
                                CoGeResponse response = coge.addFeatures(g, name, description, version, sourceName, geneGFF.getAbsolutePath());
                                System.out.println(response.toString());
                            }
                        }
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
        if (file.isDirectory()) {
            System.out.println("");
            System.out.println("Dir:\t"+file.getAbsolutePath());
        } else if (file.isFile()) {
            System.out.println("File:\t\t"+file.getAbsolutePath());
        }
    }

    /**
     * Informatativational output
     */
    static void printLine(IRODSFile file) {
        if (file.isDirectory()) {
            System.out.println("");
            System.out.println("Dir:\t"+file.getAbsolutePath());
        } else if (file.isFile()) {
            System.out.println("File:\t\t"+file.getAbsolutePath());
        }
    }

}
