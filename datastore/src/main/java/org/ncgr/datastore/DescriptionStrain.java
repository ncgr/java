package org.ncgr.datastore;

import java.util.List;

/**
 * Encapsulates the data associated with a single strain in a Datastore description_Genus_species file.
 *
 *   - identifier: Wm82
 *     accession: PI 518671
 *     name: Williams 82
 *     accession_group: Reference - Williams 82
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
 *
 */
public class DescriptionStrain {
    public String identifier;
    public String accession;
    public String name;
    public String origin;
    public String description;
    public String accession_group;
    public List<DescriptionResource> resources;
}
