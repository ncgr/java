/*
 * XML Type:  AuthorType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.AuthorType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML AuthorType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public interface AuthorType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.AuthorType> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "authortype1888type");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets the "LastName" element
     */
    java.lang.String getLastName();

    /**
     * Gets (as xml) the "LastName" element
     */
    org.apache.xmlbeans.XmlString xgetLastName();

    /**
     * True if has "LastName" element
     */
    boolean isSetLastName();

    /**
     * Sets the "LastName" element
     */
    void setLastName(java.lang.String lastName);

    /**
     * Sets (as xml) the "LastName" element
     */
    void xsetLastName(org.apache.xmlbeans.XmlString lastName);

    /**
     * Unsets the "LastName" element
     */
    void unsetLastName();

    /**
     * Gets the "ForeName" element
     */
    java.lang.String getForeName();

    /**
     * Gets (as xml) the "ForeName" element
     */
    org.apache.xmlbeans.XmlString xgetForeName();

    /**
     * True if has "ForeName" element
     */
    boolean isSetForeName();

    /**
     * Sets the "ForeName" element
     */
    void setForeName(java.lang.String foreName);

    /**
     * Sets (as xml) the "ForeName" element
     */
    void xsetForeName(org.apache.xmlbeans.XmlString foreName);

    /**
     * Unsets the "ForeName" element
     */
    void unsetForeName();

    /**
     * Gets the "Initials" element
     */
    java.lang.String getInitials();

    /**
     * Gets (as xml) the "Initials" element
     */
    org.apache.xmlbeans.XmlString xgetInitials();

    /**
     * True if has "Initials" element
     */
    boolean isSetInitials();

    /**
     * Sets the "Initials" element
     */
    void setInitials(java.lang.String initials);

    /**
     * Sets (as xml) the "Initials" element
     */
    void xsetInitials(org.apache.xmlbeans.XmlString initials);

    /**
     * Unsets the "Initials" element
     */
    void unsetInitials();

    /**
     * Gets the "Suffix" element
     */
    java.lang.String getSuffix();

    /**
     * Gets (as xml) the "Suffix" element
     */
    org.apache.xmlbeans.XmlString xgetSuffix();

    /**
     * True if has "Suffix" element
     */
    boolean isSetSuffix();

    /**
     * Sets the "Suffix" element
     */
    void setSuffix(java.lang.String suffix);

    /**
     * Sets (as xml) the "Suffix" element
     */
    void xsetSuffix(org.apache.xmlbeans.XmlString suffix);

    /**
     * Unsets the "Suffix" element
     */
    void unsetSuffix();

    /**
     * Gets a List of "NameID" elements
     */
    java.util.List<gov.nih.nlm.ncbi.eutils.NameIDType> getNameIDList();

    /**
     * Gets array of all "NameID" elements
     */
    gov.nih.nlm.ncbi.eutils.NameIDType[] getNameIDArray();

    /**
     * Gets ith "NameID" element
     */
    gov.nih.nlm.ncbi.eutils.NameIDType getNameIDArray(int i);

    /**
     * Returns number of "NameID" element
     */
    int sizeOfNameIDArray();

    /**
     * Sets array of all "NameID" element
     */
    void setNameIDArray(gov.nih.nlm.ncbi.eutils.NameIDType[] nameIDArray);

    /**
     * Sets ith "NameID" element
     */
    void setNameIDArray(int i, gov.nih.nlm.ncbi.eutils.NameIDType nameID);

    /**
     * Inserts and returns a new empty value (as xml) as the ith "NameID" element
     */
    gov.nih.nlm.ncbi.eutils.NameIDType insertNewNameID(int i);

    /**
     * Appends and returns a new empty value (as xml) as the last "NameID" element
     */
    gov.nih.nlm.ncbi.eutils.NameIDType addNewNameID();

    /**
     * Removes the ith "NameID" element
     */
    void removeNameID(int i);

    /**
     * Gets the "CollectiveName" element
     */
    java.lang.String getCollectiveName();

    /**
     * Gets (as xml) the "CollectiveName" element
     */
    org.apache.xmlbeans.XmlString xgetCollectiveName();

    /**
     * True if has "CollectiveName" element
     */
    boolean isSetCollectiveName();

    /**
     * Sets the "CollectiveName" element
     */
    void setCollectiveName(java.lang.String collectiveName);

    /**
     * Sets (as xml) the "CollectiveName" element
     */
    void xsetCollectiveName(org.apache.xmlbeans.XmlString collectiveName);

    /**
     * Unsets the "CollectiveName" element
     */
    void unsetCollectiveName();

    /**
     * Gets the "ValidYN" attribute
     */
    gov.nih.nlm.ncbi.eutils.AuthorType.ValidYN.Enum getValidYN();

    /**
     * Gets (as xml) the "ValidYN" attribute
     */
    gov.nih.nlm.ncbi.eutils.AuthorType.ValidYN xgetValidYN();

    /**
     * True if has "ValidYN" attribute
     */
    boolean isSetValidYN();

    /**
     * Sets the "ValidYN" attribute
     */
    void setValidYN(gov.nih.nlm.ncbi.eutils.AuthorType.ValidYN.Enum validYN);

    /**
     * Sets (as xml) the "ValidYN" attribute
     */
    void xsetValidYN(gov.nih.nlm.ncbi.eutils.AuthorType.ValidYN validYN);

    /**
     * Unsets the "ValidYN" attribute
     */
    void unsetValidYN();

    /**
     * An XML ValidYN(@).
     *
     * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.AuthorType$ValidYN.
     */
    public interface ValidYN extends org.apache.xmlbeans.XmlNMTOKEN {
        ElementFactory<gov.nih.nlm.ncbi.eutils.AuthorType.ValidYN> Factory = new ElementFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "validyna6e1attrtype");
        org.apache.xmlbeans.SchemaType type = Factory.getType();


        org.apache.xmlbeans.StringEnumAbstractBase getEnumValue();
        void setEnumValue(org.apache.xmlbeans.StringEnumAbstractBase e);

        Enum Y = Enum.forString("Y");
        Enum N = Enum.forString("N");

        int INT_Y = Enum.INT_Y;
        int INT_N = Enum.INT_N;

        /**
         * Enumeration value class for gov.nih.nlm.ncbi.eutils.AuthorType$ValidYN.
         * These enum values can be used as follows:
         * <pre>
         * enum.toString(); // returns the string value of the enum
         * enum.intValue(); // returns an int value, useful for switches
         * // e.g., case Enum.INT_Y
         * Enum.forString(s); // returns the enum value for a string
         * Enum.forInt(i); // returns the enum value for an int
         * </pre>
         * Enumeration objects are immutable singleton objects that
         * can be compared using == object equality. They have no
         * public constructor. See the constants defined within this
         * class for all the valid values.
         */
        final class Enum extends org.apache.xmlbeans.StringEnumAbstractBase {
            /**
             * Returns the enum value for a string, or null if none.
             */
            public static Enum forString(java.lang.String s) {
                return (Enum)table.forString(s);
            }

            /**
             * Returns the enum value corresponding to an int, or null if none.
             */
            public static Enum forInt(int i) {
                return (Enum)table.forInt(i);
            }

            private Enum(java.lang.String s, int i) {
                super(s, i);
            }

            static final int INT_Y = 1;
            static final int INT_N = 2;

            public static final org.apache.xmlbeans.StringEnumAbstractBase.Table table =
                new org.apache.xmlbeans.StringEnumAbstractBase.Table(new Enum[] {
                new Enum("Y", INT_Y),
                new Enum("N", INT_N),
            });
            private static final long serialVersionUID = 1L;
            private java.lang.Object readResolve() {
                return forInt(intValue());
            }
        }
    }
}
