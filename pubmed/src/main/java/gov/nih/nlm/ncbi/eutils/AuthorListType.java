/*
 * XML Type:  AuthorListType
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.AuthorListType
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils;

import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;


/**
 * An XML AuthorListType(@http://www.ncbi.nlm.nih.gov/eutils).
 *
 * This is a complex type.
 */
public interface AuthorListType extends org.apache.xmlbeans.XmlObject {
    DocumentFactory<gov.nih.nlm.ncbi.eutils.AuthorListType> Factory = new DocumentFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "authorlisttype440atype");
    org.apache.xmlbeans.SchemaType type = Factory.getType();


    /**
     * Gets a List of "Author" elements
     */
    java.util.List<gov.nih.nlm.ncbi.eutils.AuthorType> getAuthorList();

    /**
     * Gets array of all "Author" elements
     */
    gov.nih.nlm.ncbi.eutils.AuthorType[] getAuthorArray();

    /**
     * Gets ith "Author" element
     */
    gov.nih.nlm.ncbi.eutils.AuthorType getAuthorArray(int i);

    /**
     * Returns number of "Author" element
     */
    int sizeOfAuthorArray();

    /**
     * Sets array of all "Author" element
     */
    void setAuthorArray(gov.nih.nlm.ncbi.eutils.AuthorType[] authorArray);

    /**
     * Sets ith "Author" element
     */
    void setAuthorArray(int i, gov.nih.nlm.ncbi.eutils.AuthorType author);

    /**
     * Inserts and returns a new empty value (as xml) as the ith "Author" element
     */
    gov.nih.nlm.ncbi.eutils.AuthorType insertNewAuthor(int i);

    /**
     * Appends and returns a new empty value (as xml) as the last "Author" element
     */
    gov.nih.nlm.ncbi.eutils.AuthorType addNewAuthor();

    /**
     * Removes the ith "Author" element
     */
    void removeAuthor(int i);

    /**
     * Gets the "CompleteYN" attribute
     */
    gov.nih.nlm.ncbi.eutils.AuthorListType.CompleteYN.Enum getCompleteYN();

    /**
     * Gets (as xml) the "CompleteYN" attribute
     */
    gov.nih.nlm.ncbi.eutils.AuthorListType.CompleteYN xgetCompleteYN();

    /**
     * True if has "CompleteYN" attribute
     */
    boolean isSetCompleteYN();

    /**
     * Sets the "CompleteYN" attribute
     */
    void setCompleteYN(gov.nih.nlm.ncbi.eutils.AuthorListType.CompleteYN.Enum completeYN);

    /**
     * Sets (as xml) the "CompleteYN" attribute
     */
    void xsetCompleteYN(gov.nih.nlm.ncbi.eutils.AuthorListType.CompleteYN completeYN);

    /**
     * Unsets the "CompleteYN" attribute
     */
    void unsetCompleteYN();

    /**
     * An XML CompleteYN(@).
     *
     * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.AuthorListType$CompleteYN.
     */
    public interface CompleteYN extends org.apache.xmlbeans.XmlNMTOKEN {
        ElementFactory<gov.nih.nlm.ncbi.eutils.AuthorListType.CompleteYN> Factory = new ElementFactory<>(org.apache.xmlbeans.metadata.system.eutils.TypeSystemHolder.typeSystem, "completeyn1c58attrtype");
        org.apache.xmlbeans.SchemaType type = Factory.getType();


        org.apache.xmlbeans.StringEnumAbstractBase getEnumValue();
        void setEnumValue(org.apache.xmlbeans.StringEnumAbstractBase e);

        Enum Y = Enum.forString("Y");
        Enum N = Enum.forString("N");

        int INT_Y = Enum.INT_Y;
        int INT_N = Enum.INT_N;

        /**
         * Enumeration value class for gov.nih.nlm.ncbi.eutils.AuthorListType$CompleteYN.
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
