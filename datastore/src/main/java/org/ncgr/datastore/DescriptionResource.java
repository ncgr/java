package org.ncgr.datastore;

/**
 * Encapsulates a single resource given in an LIS description file.
 *
 *     resources:
 *       - name: Soybase Genome Browser (GBrowse)
 *         URL: "https://soybase.org/gb2/gbrowse/gmax2.0"
 *         description: "GBrowse for Wm82 assemblies 2.0"
 *
 */
public class DescriptionResource {

    public String name;
    public String URL;
    public String description;

}
