package org.ncgr.datastore;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import java.util.List;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

/**
 * Encapsulate an LIS Datastore description_Genus file, which is in YAML format:
 *
 * ---
 * taxid: 3847
 * genus: Glycine 
 * commonName: soybean 
 * description: "The best-known species in Glycine is the cultivated soybean...."
 *
 * species:
 *   - max
 *   - soja
 *   - cyrtoloba
 *   - dolichocarpa
 *   - falcata
 *   - stenophita
 *   - syndetika
 *   - D3-tomentella  
 *
 * resources:
 *   - name: SoyMine
 *     URL: "https://mines.legumeinfo.org/soymine/begin.do"
 *     description: "InterMine interface for accessing genetic and genomic data for several species in Glycine."
 *   - name: ZZBrowse
 *     URL: "https://zzbrowse.legumeinfo.org/?tab=WhGen&datasets=Peanut..."
 *     description: "Association viewers (QTL, GWAS)"
 *
 * @author Sam Hokin
 */
public class DescriptionGenus {

    // species attributes
    public String taxid;
    public String genus;
    public String commonName;
    public String description;

    // species resources
    public List<String> species;

    // strains including their resources
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
    }
}
