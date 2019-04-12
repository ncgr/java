/**
 * ParamsType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package edu.sdsc.nbcr.opal.types;

public class ParamsType  implements java.io.Serializable {
    private org.apache.axis.types.Id id;
    private java.lang.String tag;
    private java.lang.String _default;
    private edu.sdsc.nbcr.opal.types.ParamType paramType;
    private edu.sdsc.nbcr.opal.types.IOType ioType;
    private java.lang.Boolean required;
    private java.lang.String[] value;
    private java.lang.String semanticType;
    private java.lang.String textDesc;

    public ParamsType() {
    }

    public ParamsType(
           org.apache.axis.types.Id id,
           java.lang.String tag,
           java.lang.String _default,
           edu.sdsc.nbcr.opal.types.ParamType paramType,
           edu.sdsc.nbcr.opal.types.IOType ioType,
           java.lang.Boolean required,
           java.lang.String[] value,
           java.lang.String semanticType,
           java.lang.String textDesc) {
           this.id = id;
           this.tag = tag;
           this._default = _default;
           this.paramType = paramType;
           this.ioType = ioType;
           this.required = required;
           this.value = value;
           this.semanticType = semanticType;
           this.textDesc = textDesc;
    }


    /**
     * Gets the id value for this ParamsType.
     * 
     * @return id
     */
    public org.apache.axis.types.Id getId() {
        return id;
    }


    /**
     * Sets the id value for this ParamsType.
     * 
     * @param id
     */
    public void setId(org.apache.axis.types.Id id) {
        this.id = id;
    }


    /**
     * Gets the tag value for this ParamsType.
     * 
     * @return tag
     */
    public java.lang.String getTag() {
        return tag;
    }


    /**
     * Sets the tag value for this ParamsType.
     * 
     * @param tag
     */
    public void setTag(java.lang.String tag) {
        this.tag = tag;
    }


    /**
     * Gets the _default value for this ParamsType.
     * 
     * @return _default
     */
    public java.lang.String get_default() {
        return _default;
    }


    /**
     * Sets the _default value for this ParamsType.
     * 
     * @param _default
     */
    public void set_default(java.lang.String _default) {
        this._default = _default;
    }


    /**
     * Gets the paramType value for this ParamsType.
     * 
     * @return paramType
     */
    public edu.sdsc.nbcr.opal.types.ParamType getParamType() {
        return paramType;
    }


    /**
     * Sets the paramType value for this ParamsType.
     * 
     * @param paramType
     */
    public void setParamType(edu.sdsc.nbcr.opal.types.ParamType paramType) {
        this.paramType = paramType;
    }


    /**
     * Gets the ioType value for this ParamsType.
     * 
     * @return ioType
     */
    public edu.sdsc.nbcr.opal.types.IOType getIoType() {
        return ioType;
    }


    /**
     * Sets the ioType value for this ParamsType.
     * 
     * @param ioType
     */
    public void setIoType(edu.sdsc.nbcr.opal.types.IOType ioType) {
        this.ioType = ioType;
    }


    /**
     * Gets the required value for this ParamsType.
     * 
     * @return required
     */
    public java.lang.Boolean getRequired() {
        return required;
    }


    /**
     * Sets the required value for this ParamsType.
     * 
     * @param required
     */
    public void setRequired(java.lang.Boolean required) {
        this.required = required;
    }


    /**
     * Gets the value value for this ParamsType.
     * 
     * @return value
     */
    public java.lang.String[] getValue() {
        return value;
    }


    /**
     * Sets the value value for this ParamsType.
     * 
     * @param value
     */
    public void setValue(java.lang.String[] value) {
        this.value = value;
    }

    public java.lang.String getValue(int i) {
        return this.value[i];
    }

    public void setValue(int i, java.lang.String _value) {
        this.value[i] = _value;
    }


    /**
     * Gets the semanticType value for this ParamsType.
     * 
     * @return semanticType
     */
    public java.lang.String getSemanticType() {
        return semanticType;
    }


    /**
     * Sets the semanticType value for this ParamsType.
     * 
     * @param semanticType
     */
    public void setSemanticType(java.lang.String semanticType) {
        this.semanticType = semanticType;
    }


    /**
     * Gets the textDesc value for this ParamsType.
     * 
     * @return textDesc
     */
    public java.lang.String getTextDesc() {
        return textDesc;
    }


    /**
     * Sets the textDesc value for this ParamsType.
     * 
     * @param textDesc
     */
    public void setTextDesc(java.lang.String textDesc) {
        this.textDesc = textDesc;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ParamsType)) return false;
        ParamsType other = (ParamsType) obj;
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
            ((this.paramType==null && other.getParamType()==null) || 
             (this.paramType!=null &&
              this.paramType.equals(other.getParamType()))) &&
            ((this.ioType==null && other.getIoType()==null) || 
             (this.ioType!=null &&
              this.ioType.equals(other.getIoType()))) &&
            ((this.required==null && other.getRequired()==null) || 
             (this.required!=null &&
              this.required.equals(other.getRequired()))) &&
            ((this.value==null && other.getValue()==null) || 
             (this.value!=null &&
              java.util.Arrays.equals(this.value, other.getValue()))) &&
            ((this.semanticType==null && other.getSemanticType()==null) || 
             (this.semanticType!=null &&
              this.semanticType.equals(other.getSemanticType()))) &&
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
        if (getParamType() != null) {
            _hashCode += getParamType().hashCode();
        }
        if (getIoType() != null) {
            _hashCode += getIoType().hashCode();
        }
        if (getRequired() != null) {
            _hashCode += getRequired().hashCode();
        }
        if (getValue() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getValue());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getValue(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getSemanticType() != null) {
            _hashCode += getSemanticType().hashCode();
        }
        if (getTextDesc() != null) {
            _hashCode += getTextDesc().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ParamsType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://nbcr.sdsc.edu/opal/types", "ParamsType"));
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
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("_default");
        elemField.setXmlName(new javax.xml.namespace.QName("", "default"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paramType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "paramType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://nbcr.sdsc.edu/opal/types", "ParamType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ioType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ioType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://nbcr.sdsc.edu/opal/types", "IOType"));
        elemField.setMinOccurs(0);
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
        elemField.setFieldName("value");
        elemField.setXmlName(new javax.xml.namespace.QName("", "value"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
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
