package org.ncgr.datastore;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import java.util.List;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

/**
 * Encapsulate an LIS Datastore description_Genus file, which is in YAML format:
 * ---                                                                                                                                                                                                         
 * taxid: 3883                                                                                                                                                                                                 
 * genus: Phaseolus                                                                                                                                                                                            
 * commonName: Bean                                                                                                                                                                                            
 * description: "Phaseolus (bean, wild bean) is a genus of herbaceous to woody annual ...."                                                                                                                    
 *
 * species:                                                                                                                                                                                                    
 *   - lunatus                                                                                                                                                                                                 
 *   - vulgaris                                                                                                                                                                                                
 *   - acutifolius                                                                                                                                                                                             
 *
 * resources:                                                                                                                                                                                                  
 *   - name: PhaseolusMine                                                                                                                                                                                     
 *     URL: "https://mines.legumeinfo.org/phaseolusmine/begin.do"                                                                                                                                              
 *     description: "InterMine interface for accessing genetic and genomic data for several Phaseolus species."                                                                                                
 *   - name: ZZBrowse                                                                                                                                                                                          
 *     URL: "https://zzbrowse.legumeinfo.org/?tab=WhGen&datasets=Common%20Bean%20GWAS&chr=Chr01&selected=100000&..."                                                                                           
 *     description: "Association viewers (QTL, GWAS)"
 *
 * @author Sam Hokin
 */
public class DescriptionGenus {

    // genus attributes
    public int taxid;
    public String genus;
    public String commonName;
    public String description;

    // species
    public List<String> species;
    
    // genus resources
    public List<DescriptionResource> resources;

    /**
     * Parse a DescriptionGenus from a File given by a filename.
     */
    public static DescriptionGenus parse(String filename) throws IOException {
        YAMLMapper mapper = new YAMLMapper();
        return mapper.readValue(new File(filename), DescriptionGenus.class);
    }

    /**
     * Parse a DescriptionGenus from a File.
     */
    public static DescriptionGenus parse(File file) throws IOException {
        YAMLMapper mapper = new YAMLMapper();
        return mapper.readValue(file, DescriptionGenus.class);
    }

    /**
     * Parse a DescriptionGenus from a Reader.
     */
    public static DescriptionGenus parse(Reader reader) throws IOException {
        YAMLMapper mapper = new YAMLMapper();
        return mapper.readValue(reader, DescriptionGenus.class);
    }

    /**
     * Main method for testing.
     */
    public static void main(String[] args) throws IOException {
        String filename = args[0];
        DescriptionGenus genus = DescriptionGenus.parse(filename);
        System.out.println(genus.taxid);
        System.out.println(genus.genus);
        System.out.println(genus.commonName);
        System.out.println(genus.description);
        System.out.println(genus.species);
        System.out.println(genus.resources);
    }
}
