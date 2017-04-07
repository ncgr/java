/**
 * FlagsType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package edu.sdsc.nbcr.opal.types;

public class FlagsType  implements java.io.Serializable {
    private org.apache.axis.types.Id id;
    private java.lang.String tag;
    private java.lang.Boolean _default;
    private java.lang.String textDesc;

    public FlagsType() {
    }

    public FlagsType(
           org.apache.axis.types.Id id,
           java.lang.String tag,
           java.lang.Boolean _default,
           java.lang.String textDesc) {
           this.id = id;
           this.tag = tag;
           this._default = _default;
           this.textDesc = textDesc;
    }


    /**
     * Gets the id value for this FlagsType.
     * 
     * @return id
     */
    public org.apache.axis.types.Id getId() {
        return id;
    }


    /**
     * Sets the id value for this FlagsType.
     * 
     * @param id
     */
    public void setId(org.apache.axis.types.Id id) {
        this.id = id;
    }


    /**
     * Gets the tag value for this FlagsType.
     * 
     * @return tag
     */
    public java.lang.String getTag() {
        return tag;
    }


    /**
     * Sets the tag value for this FlagsType.
     * 
     * @param tag
     */
    public void setTag(java.lang.String tag) {
        this.tag = tag;
    }


    /**
     * Gets the _default value for this FlagsType.
     * 
     * @return _default
     */
    public java.lang.Boolean get_default() {
        return _default;
    }


    /**
     * Sets the _default value for this FlagsType.
     * 
     * @param _default
     */
    public void set_default(java.lang.Boolean _default) {
        this._default = _default;
    }


    /**
     * Gets the textDesc value for this FlagsType.
     * 
     * @return textDesc
     */
    public java.lang.String getTextDesc() {
        return textDesc;
    }


    /**
     * Sets the textDesc value for this FlagsType.
     * 
     * @param textDesc
     */
    public void setTextDesc(java.lang.String textDesc) {
        this.textDesc = textDesc;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FlagsType)) return false;
        FlagsType other = (FlagsType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.id==null && other.getId()==null) || 
             (this.id!=null &&
              this.id.equals(other.getId()))) &&
            ((this.tag==null && other.getTag()==null) || 
             (this.tag!=null &&
              this.tag.equals(other.getTag()))) &&
            ((this._default==null && other.get_default()==null) || 
             (this._default!=null &&
              this._default.equals(other.get_default()))) &&
            ((this.textDesc==null && other.getTextDesc()==null) || 
             (this.textDesc!=null &&
              this.textDesc.equals(other.getTextDesc())));
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
        if (getId() != null) {
            _hashCode += getId().hashCode();
        }
        if (getTag() != null) {
            _hashCode += getTag().hashCode();
        }
        if (get_default() != null) {
            _hashCode += get_default().hashCode();
        }
        if (getTextDesc() != null) {
            _hashCode += getTextDesc().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FlagsType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://nbcr.sdsc.edu/opal/types", "FlagsType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("id");
        elemField.setXmlName(new javax.xml.namespace.QName("", "id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "ID"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("tag");
        elemField.setXmlName(new javax.xml.namespace.QName("", "tag"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("_default");
        elemField.setXmlName(new javax.xml.namespace.QName("", "default"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("textDesc");
        elemField.setXmlName(new javax.xml.namespace.QName("", "textDesc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
