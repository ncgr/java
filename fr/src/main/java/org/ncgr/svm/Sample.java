package org.ncgr.svm;

import java.util.TreeMap;

/**
 * Representation of a sample consisting of a name, label and a map of indices to double values.
 * Comparable is implemented for alpha sorting on sample.name.
 */
public class Sample implements Comparable<Sample> {

    public String name;
    public String label;
    public TreeMap<Integer,Double> values;

    /**
     * Instantiate from a tab-separated line of the form:
     * name1    case    1:1.0    2:3.0    3:2.0    ...    99:34.5 
     * name2    ctrl    1:2.0    2:1.0    3:0.0    ...    99:22.5 
     */
    public Sample(String line) {
        String[] parts = line.split("\t");
        name = parts[0];
        label = parts[1];
        values = new TreeMap<>();
        for (int i=2; i<parts.length; i++) {
            String[] vparts = parts[i].split(":");
            int index = Integer.parseInt(vparts[0]);
            double value = Double.parseDouble(vparts[1]);
            values.put(index, value);
        }
    }

    /**
     * Return a string representation of the form: name label 1:1.0 2:3.0 3:2.0 ... 99:34.5
     */
    public String toString() {
        String s = "";
        s += name;
        s += " "+label;
        for (int index : values.keySet()) {
            s += " "+index+":"+values.get(index);
        }
        return s;
    }

    /**
     * Add an index:value pair to this sample.
     */
    public void addValue(int index, double value) {
        values.put(index, value);
    }

    /**
     * Two samples are equal if they have the same name.
     */
    public boolean equals(Sample that) {
        return this.name.equals(that.name);
    }

    /**
     * Compare based on name, alphabetically.
     */
    public int compareTo(Sample that) {
        return this.name.compareTo(that.name);
    }
}
    


