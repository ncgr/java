/*
 * An XML document type.
 * Localname: URL
 * Namespace: http://www.ncbi.nlm.nih.gov/eutils
 * Java type: gov.nih.nlm.ncbi.eutils.URLDocument
 *
 * Automatically generated - do not modify.
 */
package gov.nih.nlm.ncbi.eutils.impl;

import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.XmlObject;

/**
 * A document containing one URL(@http://www.ncbi.nlm.nih.gov/eutils) element.
 *
 * This is a complex type.
 */
public class URLDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements gov.nih.nlm.ncbi.eutils.URLDocument {
    private static final long serialVersionUID = 1L;

    public URLDocumentImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final QName[] PROPERTY_QNAME = {
        new QName("http://www.ncbi.nlm.nih.gov/eutils", "URL"),
    };


    /**
     * Gets the "URL" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.URLDocument.URL getURL() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.URLDocument.URL target = null;
            target = (gov.nih.nlm.ncbi.eutils.URLDocument.URL)get_store().find_element_user(PROPERTY_QNAME[0], 0);
            return (target == null) ? null : target;
        }
    }

    /**
     * Sets the "URL" element
     */
    @Override
    public void setURL(gov.nih.nlm.ncbi.eutils.URLDocument.URL url) {
        generatedSetterHelperImpl(url, PROPERTY_QNAME[0], 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
    }

    /**
     * Appends and returns a new empty "URL" element
     */
    @Override
    public gov.nih.nlm.ncbi.eutils.URLDocument.URL addNewURL() {
        synchronized (monitor()) {
            check_orphaned();
            gov.nih.nlm.ncbi.eutils.URLDocument.URL target = null;
            target = (gov.nih.nlm.ncbi.eutils.URLDocument.URL)get_store().add_element_user(PROPERTY_QNAME[0]);
            return target;
        }
    }
    /**
     * An XML URL(@http://www.ncbi.nlm.nih.gov/eutils).
     *
     * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.URLDocument$URL.
     */
    public static class URLImpl extends org.apache.xmlbeans.impl.values.JavaStringHolderEx implements gov.nih.nlm.ncbi.eutils.URLDocument.URL {
        private static final long serialVersionUID = 1L;

        public URLImpl(org.apache.xmlbeans.SchemaType sType) {
            super(sType, true);
        }

        protected URLImpl(org.apache.xmlbeans.SchemaType sType, boolean b) {
            super(sType, b);
        }

        private static final QName[] PROPERTY_QNAME = {
            new QName("", "lang"),
            new QName("", "Type"),
        };


        /**
         * Gets the "lang" attribute
         */
        @Override
        public gov.nih.nlm.ncbi.eutils.URLDocument.URL.Lang.Enum getLang() {
            synchronized (monitor()) {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[0]);
                return (target == null) ? null : (gov.nih.nlm.ncbi.eutils.URLDocument.URL.Lang.Enum)target.getEnumValue();
            }
        }

        /**
         * Gets (as xml) the "lang" attribute
         */
        @Override
        public gov.nih.nlm.ncbi.eutils.URLDocument.URL.Lang xgetLang() {
            synchronized (monitor()) {
                check_orphaned();
                gov.nih.nlm.ncbi.eutils.URLDocument.URL.Lang target = null;
                target = (gov.nih.nlm.ncbi.eutils.URLDocument.URL.Lang)get_store().find_attribute_user(PROPERTY_QNAME[0]);
                return target;
            }
        }

        /**
         * True if has "lang" attribute
         */
        @Override
        public boolean isSetLang() {
            synchronized (monitor()) {
                check_orphaned();
                return get_store().find_attribute_user(PROPERTY_QNAME[0]) != null;
            }
        }

        /**
         * Sets the "lang" attribute
         */
        @Override
        public void setLang(gov.nih.nlm.ncbi.eutils.URLDocument.URL.Lang.Enum lang) {
            synchronized (monitor()) {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[0]);
                if (target == null) {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[0]);
                }
                target.setEnumValue(lang);
            }
        }

        /**
         * Sets (as xml) the "lang" attribute
         */
        @Override
        public void xsetLang(gov.nih.nlm.ncbi.eutils.URLDocument.URL.Lang lang) {
            synchronized (monitor()) {
                check_orphaned();
                gov.nih.nlm.ncbi.eutils.URLDocument.URL.Lang target = null;
                target = (gov.nih.nlm.ncbi.eutils.URLDocument.URL.Lang)get_store().find_attribute_user(PROPERTY_QNAME[0]);
                if (target == null) {
                    target = (gov.nih.nlm.ncbi.eutils.URLDocument.URL.Lang)get_store().add_attribute_user(PROPERTY_QNAME[0]);
                }
                target.set(lang);
            }
        }

        /**
         * Unsets the "lang" attribute
         */
        @Override
        public void unsetLang() {
            synchronized (monitor()) {
                check_orphaned();
                get_store().remove_attribute(PROPERTY_QNAME[0]);
            }
        }

        /**
         * Gets the "Type" attribute
         */
        @Override
        public gov.nih.nlm.ncbi.eutils.URLDocument.URL.Type.Enum getType() {
            synchronized (monitor()) {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[1]);
                return (target == null) ? null : (gov.nih.nlm.ncbi.eutils.URLDocument.URL.Type.Enum)target.getEnumValue();
            }
        }

        /**
         * Gets (as xml) the "Type" attribute
         */
        @Override
        public gov.nih.nlm.ncbi.eutils.URLDocument.URL.Type xgetType() {
            synchronized (monitor()) {
                check_orphaned();
                gov.nih.nlm.ncbi.eutils.URLDocument.URL.Type target = null;
                target = (gov.nih.nlm.ncbi.eutils.URLDocument.URL.Type)get_store().find_attribute_user(PROPERTY_QNAME[1]);
                return target;
            }
        }

        /**
         * True if has "Type" attribute
         */
        @Override
        public boolean isSetType() {
            synchronized (monitor()) {
                check_orphaned();
                return get_store().find_attribute_user(PROPERTY_QNAME[1]) != null;
            }
        }

        /**
         * Sets the "Type" attribute
         */
        @Override
        public void setType(gov.nih.nlm.ncbi.eutils.URLDocument.URL.Type.Enum type) {
            synchronized (monitor()) {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(PROPERTY_QNAME[1]);
                if (target == null) {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(PROPERTY_QNAME[1]);
                }
                target.setEnumValue(type);
            }
        }

        /**
         * Sets (as xml) the "Type" attribute
         */
        @Override
        public void xsetType(gov.nih.nlm.ncbi.eutils.URLDocument.URL.Type type) {
            synchronized (monitor()) {
                check_orphaned();
                gov.nih.nlm.ncbi.eutils.URLDocument.URL.Type target = null;
                target = (gov.nih.nlm.ncbi.eutils.URLDocument.URL.Type)get_store().find_attribute_user(PROPERTY_QNAME[1]);
                if (target == null) {
                    target = (gov.nih.nlm.ncbi.eutils.URLDocument.URL.Type)get_store().add_attribute_user(PROPERTY_QNAME[1]);
                }
                target.set(type);
            }
        }

        /**
         * Unsets the "Type" attribute
         */
        @Override
        public void unsetType() {
            synchronized (monitor()) {
                check_orphaned();
                get_store().remove_attribute(PROPERTY_QNAME[1]);
            }
        }
        /**
         * An XML lang(@).
         *
         * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.URLDocument$URL$Lang.
         */
        public static class LangImpl extends org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx implements gov.nih.nlm.ncbi.eutils.URLDocument.URL.Lang {
            private static final long serialVersionUID = 1L;

            public LangImpl(org.apache.xmlbeans.SchemaType sType) {
                super(sType, false);
            }

            protected LangImpl(org.apache.xmlbeans.SchemaType sType, boolean b) {
                super(sType, b);
            }
        }
        /**
         * An XML Type(@).
         *
         * This is an atomic type that is a restriction of gov.nih.nlm.ncbi.eutils.URLDocument$URL$Type.
         */
        public static class TypeImpl extends org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx implements gov.nih.nlm.ncbi.eutils.URLDocument.URL.Type {
            private static final long serialVersionUID = 1L;

            public TypeImpl(org.apache.xmlbeans.SchemaType sType) {
                super(sType, false);
            }

            protected TypeImpl(org.apache.xmlbeans.SchemaType sType, boolean b) {
                super(sType, b);
            }
        }
    }
}
