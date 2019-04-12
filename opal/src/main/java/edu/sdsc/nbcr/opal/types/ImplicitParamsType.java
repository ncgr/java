/**
 * ImplicitParamsType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package edu.sdsc.nbcr.opal.types;

public class ImplicitParamsType  implements java.io.Serializable {
    private org.apache.axis.types.Id id;
    private java.lang.String name;
    private java.lang.String extension;
    private edu.sdsc.nbcr.opal.types.IOType ioType;
    private java.lang.Boolean required;
    private java.lang.String semanticType;
    private java.lang.String textDesc;
    private java.lang.Integer min;
    private java.lang.Integer max;

    public ImplicitParamsType() {
    }

    public ImplicitParamsType(
           org.apache.axis.types.Id id,
           java.lang.String name,
           java.lang.String extension,
           edu.sdsc.nbcr.opal.types.IOType ioType,
           java.lang.Boolean required,
           java.lang.String semanticType,
           java.lang.String textDesc,
           java.lang.Integer min,
           java.lang.Integer max) {
           this.id = id;
           this.name = name;
           this.extension = extension;
           this.ioType = ioType;
           this.required = required;
           this.semanticType = semanticType;
           this.textDesc = textDesc;
           this.min = min;
           this.max = max;
    }


    /**
     * Gets the id value for this ImplicitParamsType.
     * 
     * @return id
     */
    public org.apache.axis.types.Id getId() {
        return id;
    }


    /**
     * Sets the id value for this ImplicitParamsType.
     * 
     * @param id
     */
    public void setId(org.apache.axis.types.Id id) {
        this.id = id;
    }


    /**
     * Gets the name value for this ImplicitParamsType.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this ImplicitParamsType.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the extension value for this ImplicitParamsType.
     * 
     * @return extension
     */
    public java.lang.String getExtension() {
        return extension;
    }


    /**
     * Sets the extension value for this ImplicitParamsType.
     * 
     * @param extension
     */
    public void setExtension(java.lang.String extension) {
        this.extension = extension;
    }


    /**
     * Gets the ioType value for this ImplicitParamsType.
     * 
     * @return ioType
     */
    public edu.sdsc.nbcr.opal.types.IOType getIoType() {
        return ioType;
    }


    /**
     * Sets the ioType value for this ImplicitParamsType.
     * 
     * @param ioType
     */
    public void setIoType(edu.sdsc.nbcr.opal.types.IOType ioType) {
        this.ioType = ioType;
    }


    /**
     * Gets the required value for this ImplicitParamsType.
     * 
     * @return required
     */
    public java.lang.Boolean getRequired() {
        return required;
    }


    /**
     * Sets the required value for this ImplicitParamsType.
     * 
     * @param required
     */
    public void setRequired(java.lang.Boolean required) {
        this.required = required;
    }


    /**
     * Gets the semanticType value for this ImplicitParamsType.
     * 
     * @return semanticType
     */
    public java.lang.String getSemanticType() {
        return semanticType;
    }


    /**
     * Sets the semanticType value for this ImplicitParamsType.
     * 
     * @param semanticType
     */
    public void setSemanticType(java.lang.String semanticType) {
        this.semanticType = semanticType;
    }


    /**
     * Gets the textDesc value for this ImplicitParamsType.
     * 
     * @return textDesc
     */
    public java.lang.String getTextDesc() {
        return textDesc;
    }


    /**
     * Sets the textDesc value for this ImplicitParamsType.
     * 
     * @param textDesc
     */
    public void setTextDesc(java.lang.String textDesc) {
        this.textDesc = textDesc;
    }


    /**
     * Gets the min value for this ImplicitParamsType.
     * 
     * @return min
     */
    public java.lang.Integer getMin() {
        return min;
    }


    /**
     * Sets the min value for this ImplicitParamsType.
     * 
     * @param min
     */
    public void setMin(java.lang.Integer min) {
        this.min = min;
    }


    /**
     * Gets the max value for this ImplicitParamsType.
     * 
     * @return max
     */
    public java.lang.Integer getMax() {
        return max;
    }


    /**
     * Sets the max value for this ImplicitParamsType.
     * 
     * @param max
     */
    public void setMax(java.lang.Integer max) {
        this.max = max;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ImplicitParamsType)) return false;
        ImplicitParamsType other = (ImplicitParamsType) obj;
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
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
            ((this.extension==null && other.getExtension()==null) || 
             (this.extension!=null &&
              this.extension.equals(other.getExtension()))) &&
            ((this.ioType==null && other.getIoType()==null) || 
             (this.ioType!=null &&
              this.ioType.equals(other.getIoType()))) &&
            ((this.required==null && other.getRequired()==null) || 
             (this.required!=null &&
              this.required.equals(other.getRequired()))) &&
            ((this.semanticType==null && other.getSemanticType()==null) || 
             (this.semanticType!=null &&
              this.semanticType.equals(other.getSemanticType()))) &&
            ((this.textDesc==null && other.getTextDesc()==null) || 
             (this.textDesc!=null &&
              this.textDesc.equals(other.getTextDesc()))) &&
            ((this.min==null && other.getMin()==null) || 
             (this.min!=null &&
              this.min.equals(other.getMin()))) &&
            ((this.max==null && other.getMax()==null) || 
             (this.max!=null &&
              this.max.equals(other.getMax())));
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
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getExtension() != null) {
            _hashCode += getExtension().hashCode();
        }
        if (getIoType() != null) {
            _hashCode += getIoType().hashCode();
        }
        if (getRequired() != null) {
            _hashCode += getRequired().hashCode();
        }
        if (getSemanticType() != null) {
            _hashCode += getSemanticType().hashCode();
        }
        if (getTextDesc() != null) {
            _hashCode += getTextDesc().hashCode();
        }
        if (getMin() != null) {
            _hashCode += getMin().hashCode();
        }
        if (getMax() != null) {
            _hashCode += getMax().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ImplicitParamsType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://nbcr.sdsc.edu/opal/types", "ImplicitParamsType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("id");
        elemField.setXmlName(new javax.xml.namespace.QName("", "id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "ID"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("", "name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extension");
        elemField.setXmlName(new javax.xml.namespace.QName("", "extension"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ioType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ioType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://nbcr.sdsc.edu/opal/types", "IOType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("required");
        elemField.setXmlName(new javax.xml.namespace.QName("", "required"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("semanticType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "semanticType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("textDesc");
        elemField.setXmlName(new javax.xml.namespace.QName("", "textDesc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("min");
        elemField.setXmlName(new javax.xml.namespace.QName("", "min"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("max");
        elemField.setXmlName(new javax.xml.namespace.QName("", "max"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
