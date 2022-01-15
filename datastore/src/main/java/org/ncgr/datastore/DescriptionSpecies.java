package org.ncgr.datastore;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import java.util.List;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

/**
 * Encapsulate an LIS Datastore description_Genus_species file, which is in YAML format:
 *
 * ---
 * taxid: 3847
 * genus: Glycine
 * species: max
 * abbrev: glyma
 * commonName: soybean
 * description: "Soybean (Glycine max), the predominant oil-seed legume worldwide, was likely domesticated in East Asia..."
 *
 * resources:
 *   - name: SoyMine
 *     URL: "https://mines.legumeinfo.org/soymine/begin.do"
 *     description: "InterMine interface for accessing genetic and genomic data for several species in Glycine."
 *   - name: GCViT
 *     URL: "https://soybase.org/gcvit"
 *     description: "Genotype comparison visualization tool "
 *
 * strains:
 *   - identifier: Wm82
 *     accession: PI 518671
 *     name: Williams 82
 *     origin: Northern United States
 *     description: "Williams 82, the soybean cultivar used to produce the reference genome sequence..."
 *     resources:
 *       - name: Soybase Genome Browser (GBrowse)
 *         URL: "https://soybase.org/gb2/gbrowse/gmax2.0"
 *         description: "GBrowse for Wm82 assemblies 2.0"
 *       - name: Genome Browser (GBrowse), assembly 1
 *         URL: "https://soybase.org/gb2/gbrowse/gmax1.01"
 *         description: "GBrowse for Wm82 assemblies 1.01"
 *       - name: LIS SequenceServer
 *         URL: "https://legumeinfo.org/sequenceserver/"
 *         description: "SequenceServer BLAST against the Wm82 v2 assembly"
 *   - identifier: Zh13
 *     accession: GWHAAEV00000000.1 
 *     name: Zhonghuang 13
 *     origin: China
 *     description: "Zhonghuang 13 is a Chinese cultivar derived from accessions Yudou 18 and Zhongzuo 90052-76..."
 *     resources:
 *       - name: Soybase Genome Browser (GBrowse)
 *         URL: "https:*soybase.org/gb2/gbrowse/glyma.Zh13.gnm1"
 *         description: "GBrowse for glycine max Zh13 genome assembly verson 1"
 *       - name: LIS SequenceServer
 *         URL: "https://legumeinfo.org/sequenceserver/"
 *         description: "SequenceServer BLAST against the glycine max Zh13 assembly"
 *
 * @author Sam Hokin
 */
public class DescriptionSpecies {

    // species attributes
    public String taxid;
    public String genus;
    public String species;
    public String abbrev;
    public String commonName;
    public String description;

    // species resources
    public List<DescriptionResource> resources;

    // strains including their resources
    public List<DescriptionStrain> strains;

    /**
     * Parse a DescriptionSpecies from a File given by a filename.
     */
    public static DescriptionSpecies parse(String filename) throws IOException {
        YAMLMapper mapper = new YAMLMapper();
        return mapper.readValue(new File(filename), DescriptionSpecies.class);
    }

    /**
     * Parse a DescriptionSpecies from a File.
     */
    public static DescriptionSpecies parse(File file) throws IOException {
        YAMLMapper mapper = new YAMLMapper();
        return mapper.readValue(file, DescriptionSpecies.class);
    }

    /**
     * Parse a DescriptionSpecies from a Reader.
     */
    public static DescriptionSpecies parse(Reader reader) throws IOException {
        YAMLMapper mapper = new YAMLMapper();
        return mapper.readValue(reader, DescriptionSpecies.class);
    }

    /**
     * Main method for testing.
     */
    public static void main(String[] args) throws IOException {
        String filename = args[0];
        DescriptionSpecies species = DescriptionSpecies.parse(filename);
    }
}
