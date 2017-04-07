/**
 * StatusOutputType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package edu.sdsc.nbcr.opal.types;

public class StatusOutputType  implements java.io.Serializable {
    private int code;
    private java.lang.String message;
    private org.apache.axis.types.URI baseURL;

    public StatusOutputType() {
    }

    public StatusOutputType(
           int code,
           java.lang.String message,
           org.apache.axis.types.URI baseURL) {
           this.code = code;
           this.message = message;
           this.baseURL = baseURL;
    }


    /**
     * Gets the code value for this StatusOutputType.
     * 
     * @return code
     */
    public int getCode() {
        return code;
    }


    /**
     * Sets the code value for this StatusOutputType.
     * 
     * @param code
     */
    public void setCode(int code) {
        this.code = code;
    }


    /**
     * Gets the message value for this StatusOutputType.
     * 
     * @return message
     */
    public java.lang.String getMessage() {
        return message;
    }


    /**
     * Sets the message value for this StatusOutputType.
     * 
     * @param message
     */
    public void setMessage(java.lang.String message) {
        this.message = message;
    }


    /**
     * Gets the baseURL value for this StatusOutputType.
     * 
     * @return baseURL
     */
    public org.apache.axis.types.URI getBaseURL() {
        return baseURL;
    }


    /**
     * Sets the baseURL value for this StatusOutputType.
     * 
     * @param baseURL
     */
    public void setBaseURL(org.apache.axis.types.URI baseURL) {
        this.baseURL = baseURL;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof StatusOutputType)) return false;
        StatusOutputType other = (StatusOutputType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.code == other.getCode() &&
            ((this.message==null && other.getMessage()==null) || 
             (this.message!=null &&
              this.message.equals(other.getMessage()))) &&
            ((this.baseURL==null && other.getBaseURL()==null) || 
             (this.baseURL!=null &&
              this.baseURL.equals(other.getBaseURL())));
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
        _hashCode += getCode();
        if (getMessage() != null) {
            _hashCode += getMessage().hashCode();
        }
        if (getBaseURL() != null) {
            _hashCode += getBaseURL().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(StatusOutputType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://nbcr.sdsc.edu/opal/types", "StatusOutputType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("code");
        elemField.setXmlName(new javax.xml.namespace.QName("", "code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("message");
        elemField.setXmlName(new javax.xml.namespace.QName("", "message"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("baseURL");
        elemField.setXmlName(new javax.xml.namespace.QName("", "baseURL"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyURI"));
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
