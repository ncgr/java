import org.ncgr.irods.IRODSParameters;
import org.ncgr.irods.Readme;
import org.ncgr.irods.LISFile;

import org.coge.api.CoGe;
import org.coge.api.CoGeObject;
import org.coge.api.CoGeParameters;
import org.coge.api.CoGeResponse;
import org.coge.api.Genome;
import org.coge.api.Notebook;
import org.coge.api.Organism;

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
 * Copy genome data from iRODS to CoGe.
 */
public class IRODSCoGeGenomes {

    static String IRODS_PROPERTIES_FILE = "irods.properties";
    static String COGE_PROPERTIES_FILE = "coge.properties";

    public static void main(String[] args) {

        if (args.length<2) {
            System.out.println("Usage: IRODSToCoGe <iplant directory> <CoGe organism name> [CoGe notebook name]");
            System.out.println("Example: IRODSToCoGe /iplant/home/shared/Legume_Federation/Cajanus_cajan \"Cajanus cajan\"");
            System.exit(1);
        }

        String iRODSDirectory = args[0];
        String cogeOrganismName = args[1];
        String notebookName = null;
        if (args.length==3) notebookName = args[2];
        
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


            // find our CoGe notebook, if desired; if more than one, show error
            Notebook notebook = null;
            if (notebookName!=null) {
                List<Notebook> list = coge.searchNotebook(notebookName);
                System.out.println("");
                if (list.size()>1) {
                    System.out.println("More than one CoGe notebook found matching the term: "+notebookName+". Genomes will not be added to a notebook.");
                } else if (list.size()==0) {
                    System.out.println("No CoGe notebook found matching the term: "+notebookName+". Genomes will not be added to a notebook.");
                } else {
                    notebook = list.get(0);
                    System.out.println("CoGe notebook "+notebook.getName()+" found. Genomes will be added to this notebook.");
                }
            }

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
                    printLine(files[i]);
                    LISFile g = new LISFile(files[i]);
                    // we only want genomes here
                    if (g.isGenomeDir()) {
                        IRODSFile subFile = irodsFileFactory.instanceIRODSFile(iRODSDirectory+"/"+files[i].getName());
                        File[] subFiles = subFile.listFiles();
                        for (int j=0; j<subFiles.length; j++) {
                            printLine(subFiles[j]);
                            LISFile f = new LISFile(subFiles[j]);
                            // gather the FASTA files
                            if (f.isUnmaskedFasta()) unmaskedFasta = f;
                            if (f.isSoftMaskedFasta()) softmaskedFasta = f;
                            if (f.isHardMaskedFasta()) hardmaskedFasta = f;
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
                                System.out.println("Identifier:"+identifier+"\tGenotype:"+genotype+"\tSource:"+source+"\tProvenance:"+provenance);
                            }
                        }
                    }
                }
            }

            // Hopefully we found everything
            if (identifier!=null && genotype!=null && source!=null && provenance!=null && unmaskedFasta!=null) {

                // CoGe Genome Add terms
                String name = genotype;
                String description = provenance;
                // form the version from the unmasked FASTA filename
                // e.g. phavu.G19833.gnm1.zBnF.fa.gz
                String[] parts = unmaskedFasta.getName().split("."+identifier+".");
                String version = parts[0];
                String sourceName = source;
                boolean restricted = false;
                System.out.println("");
                System.out.println("CoGe Genome Add data:");
                System.out.println("");
                System.out.println("name:\t"+name);
                System.out.println("description:\t"+description);
                System.out.println("version:\t"+version);
                System.out.println("source_name:\t"+sourceName);
                System.out.println("restricted:\t"+restricted);
                System.out.println("");
                if (unmaskedFasta!=null) System.out.println("type=unmasked:\t\t"+unmaskedFasta.getAbsolutePath());
                if (softmaskedFasta!=null) System.out.println("type=soft-masked:\t"+softmaskedFasta.getAbsolutePath());
                if (hardmaskedFasta!=null) System.out.println("type=hard-masked:\t"+hardmaskedFasta.getAbsolutePath());

                // flags to NOT add a genome becaus it's already in CoGe
                boolean skipUnmaskedFasta = false;
                boolean skipHardmaskedFasta = false;
                boolean skipSoftmaskedFasta = false;
                
                // now for the CoGe Genome Add
                List<Organism> organisms = coge.searchOrganism(cogeOrganismName);
                if (organisms.size()==0) {
                    System.out.println("");
                    System.out.println("CoGe organism NOT FOUND.");
                } else if (organisms.size()>1) {
                    System.out.println("");
                    System.out.println("Multiple CoGe organisms found. Refine your CoGe organism string.");
                } else {
                    
                    // let's first see if we've already got genomes with this name and version; don't include deleted genomes
                    List<Genome> genomes = coge.searchGenome(name, false);
                    if (genomes.size()>0) {
                        for (Genome g : genomes) {
                            if (g.getName().equals(name) && g.getVersion().equals(version)) {
                                if (g.getSequenceType().getName().equals("unmasked")) skipUnmaskedFasta = true;
                                if (g.getSequenceType().getName().equals("soft-masked")) skipSoftmaskedFasta = true;
                                if (g.getSequenceType().getName().equals("hard-masked")) skipHardmaskedFasta = true;
                            }
                        }
                    }
                        
                    Organism organism = organisms.get(0);
                    System.out.println("");
                    System.out.println("Single CoGe organism FOUND.");
                    System.out.println("Organism Name:\t"+organism.getName());
                    System.out.println("Organism ID:\t"+organism.getId());
                    System.out.println("Organism Description:\t"+organism.getDescription());

                    // unmasked CoGe Genome
                    if (skipUnmaskedFasta) {
                        System.out.println("");
                        System.out.println("Skipping unmasked FASTA: unmasked genome already in CoGe.");
                    } else if (unmaskedFasta!=null) {
                        Genome unmaskedGenome = new Genome(name, description);
                        unmaskedGenome.setOrganism(organism);
                        unmaskedGenome.setVersion(version);
                        unmaskedGenome.setSourceName(sourceName);
                        unmaskedGenome.setRestricted(restricted);
                        unmaskedGenome.setSequenceType(new CoGeObject(0,"unmasked"));
                        System.out.println("");
                        System.out.println("Adding unmasked genome to CoGe...");
                        CoGeResponse response1 = coge.addGenome(unmaskedGenome, unmaskedFasta.getAbsolutePath());
                        System.out.println(response1.toString());
                        // add this genome to the desired notebook
                        if (notebook!=null && response1.getSuccess()) {
                            Map<Integer,String> items = new HashMap<Integer,String>();
                            items.put(response1.getId(), "genome");
                            boolean success = coge.addItemsToNotebook(notebook, items);
                            if (success) {
                                System.out.println("Success adding unmasked genome to notebook: "+notebook.getName());
                            } else {
                                System.out.println("Failure adding unmasked genome to notebook: "+notebook.getName());
                            }
                        }                            
                    }

                    // soft-masked CoGe genome
                    if (skipSoftmaskedFasta) {
                        System.out.println("");
                        System.out.println("Skipping soft-masked FASTA: soft-masked genome already in CoGe.");
                    } else if (softmaskedFasta!=null) {
                        Genome softmaskedGenome = new Genome(name, description);
                        softmaskedGenome.setOrganism(organism);
                        softmaskedGenome.setVersion(version);
                        softmaskedGenome.setSourceName(sourceName);
                        softmaskedGenome.setRestricted(restricted);
                        softmaskedGenome.setSequenceType(new CoGeObject(0,"soft-masked"));
                        System.out.println("");
                        System.out.println("Adding soft-masked genome to CoGe...");
                        CoGeResponse response2 = coge.addGenome(softmaskedGenome, softmaskedFasta.getAbsolutePath());
                        System.out.println(response2.toString());
                        // add this genome to the desired notebook
                        if (notebook!=null && response2.getSuccess()) {
                            Map<Integer,String> items = new HashMap<Integer,String>();
                            items.put(response2.getId(), "genome");
                            boolean success = coge.addItemsToNotebook(notebook, items);
                            if (success) {
                                System.out.println("Success adding soft-masked genome to notebook: "+notebook.getName());
                            } else {
                                System.out.println("Failure adding soft-masked genome to notebook: "+notebook.getName());
                            }
                        }
                    }
                        
                    // hard-masked CoGe genome
                    if (skipHardmaskedFasta) {
                        System.out.println("");
                        System.out.println("Skipping hard-masked FASTA: hard-masked genome already in CoGe.");
                    } else if (hardmaskedFasta!=null) {
                        Genome hardmaskedGenome = new Genome(name, description);
                        hardmaskedGenome.setOrganism(organism);
                        hardmaskedGenome.setVersion(version);
                        hardmaskedGenome.setSourceName(sourceName);
                        hardmaskedGenome.setRestricted(restricted);
                        hardmaskedGenome.setSequenceType(new CoGeObject(0,"hard-masked"));
                        System.out.println("");
                        System.out.println("Adding hard-masked genome to CoGe...");
                        CoGeResponse response3 = coge.addGenome(hardmaskedGenome, hardmaskedFasta.getAbsolutePath());
                        System.out.println(response3.toString());
                        // add this genome to the desired notebook
                        if (notebook!=null && response3.getSuccess()) {
                            Map<Integer,String> items = new HashMap<Integer,String>();
                            items.put(response3.getId(), "genome");
                            boolean success = coge.addItemsToNotebook(notebook, items);
                            if (success) {
                                System.out.println("Success adding hard-masked genome to notebook: "+notebook.getName());
                            } else {
                                System.out.println("Failure adding hard-masked genome to notebook: "+notebook.getName());
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
        } else {
            System.out.println("???:\t\t"+file.getAbsolutePath());
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
