/**
 * InputFileType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package edu.sdsc.nbcr.opal.types;

public class InputFileType  implements java.io.Serializable {
    private java.lang.String name;
    private byte[] contents;
    private org.apache.axis.types.URI location;
    private javax.activation.DataHandler attachment;

    public InputFileType() {
    }

    public InputFileType(
           java.lang.String name,
           byte[] contents,
           org.apache.axis.types.URI location,
           javax.activation.DataHandler attachment) {
           this.name = name;
           this.contents = contents;
           this.location = location;
           this.attachment = attachment;
    }


    /**
     * Gets the name value for this InputFileType.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this InputFileType.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the contents value for this InputFileType.
     * 
     * @return contents
     */
    public byte[] getContents() {
        return contents;
    }


    /**
     * Sets the contents value for this InputFileType.
     * 
     * @param contents
     */
    public void setContents(byte[] contents) {
        this.contents = contents;
    }


    /**
     * Gets the location value for this InputFileType.
     * 
     * @return location
     */
    public org.apache.axis.types.URI getLocation() {
        return location;
    }


    /**
     * Sets the location value for this InputFileType.
     * 
     * @param location
     */
    public void setLocation(org.apache.axis.types.URI location) {
        this.location = location;
    }


    /**
     * Gets the attachment value for this InputFileType.
     * 
     * @return attachment
     */
    public javax.activation.DataHandler getAttachment() {
        return attachment;
    }


    /**
     * Sets the attachment value for this InputFileType.
     * 
     * @param attachment
     */
    public void setAttachment(javax.activation.DataHandler attachment) {
        this.attachment = attachment;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof InputFileType)) return false;
        InputFileType other = (InputFileType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
            ((this.contents==null && other.getContents()==null) || 
             (this.contents!=null &&
              java.util.Arrays.equals(this.contents, other.getContents()))) &&
            ((this.location==null && other.getLocation()==null) || 
             (this.location!=null &&
              this.location.equals(other.getLocation()))) &&
            ((this.attachment==null && other.getAttachment()==null) || 
             (this.attachment!=null &&
              this.attachment.equals(other.getAttachment())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getContents() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getContents());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getContents(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getLocation() != null) {
            _hashCode += getLocation().hashCode();
        }
        if (getAttachment() != null) {
            _hashCode += getAttachment().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(InputFileType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://nbcr.sdsc.edu/opal/types", "InputFileType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("", "name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("contents");
        elemField.setXmlName(new javax.xml.namespace.QName("", "contents"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("location");
        elemField.setXmlName(new javax.xml.namespace.QName("", "location"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyURI"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("attachment");
        elemField.setXmlName(new javax.xml.namespace.QName("", "attachment"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://xml.apache.org/xml-soap", "DataHandler"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
