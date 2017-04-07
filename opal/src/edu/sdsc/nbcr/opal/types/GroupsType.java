/**
 * GroupsType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package edu.sdsc.nbcr.opal.types;

public class GroupsType  implements java.io.Serializable {
    private org.apache.axis.types.Id id;
    private java.lang.String name;
    private org.apache.axis.types.IDRefs elements;
    private java.lang.Boolean required;
    private java.lang.Boolean exclusive;
    private java.lang.String semanticType;
    private java.lang.String textDesc;

    public GroupsType() {
    }

    public GroupsType(
           org.apache.axis.types.Id id,
           java.lang.String name,
           org.apache.axis.types.IDRefs elements,
           java.lang.Boolean required,
           java.lang.Boolean exclusive,
           java.lang.String semanticType,
           java.lang.String textDesc) {
           this.id = id;
           this.name = name;
           this.elements = elements;
           this.required = required;
           this.exclusive = exclusive;
           this.semanticType = semanticType;
           this.textDesc = textDesc;
    }


    /**
     * Gets the id value for this GroupsType.
     * 
     * @return id
     */
    public org.apache.axis.types.Id getId() {
        return id;
    }


    /**
     * Sets the id value for this GroupsType.
     * 
     * @param id
     */
    public void setId(org.apache.axis.types.Id id) {
        this.id = id;
    }


    /**
     * Gets the name value for this GroupsType.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this GroupsType.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the elements value for this GroupsType.
     * 
     * @return elements
     */
    public org.apache.axis.types.IDRefs getElements() {
        return elements;
    }


    /**
     * Sets the elements value for this GroupsType.
     * 
     * @param elements
     */
    public void setElements(org.apache.axis.types.IDRefs elements) {
        this.elements = elements;
    }


    /**
     * Gets the required value for this GroupsType.
     * 
     * @return required
     */
    public java.lang.Boolean getRequired() {
        return required;
    }


    /**
     * Sets the required value for this GroupsType.
     * 
     * @param required
     */
    public void setRequired(java.lang.Boolean required) {
        this.required = required;
    }


    /**
     * Gets the exclusive value for this GroupsType.
     * 
     * @return exclusive
     */
    public java.lang.Boolean getExclusive() {
        return exclusive;
    }


    /**
     * Sets the exclusive value for this GroupsType.
     * 
     * @param exclusive
     */
    public void setExclusive(java.lang.Boolean exclusive) {
        this.exclusive = exclusive;
    }


    /**
     * Gets the semanticType value for this GroupsType.
     * 
     * @return semanticType
     */
    public java.lang.String getSemanticType() {
        return semanticType;
    }


    /**
     * Sets the semanticType value for this GroupsType.
     * 
     * @param semanticType
     */
    public void setSemanticType(java.lang.String semanticType) {
        this.semanticType = semanticType;
    }


    /**
     * Gets the textDesc value for this GroupsType.
     * 
     * @return textDesc
     */
    public java.lang.String getTextDesc() {
        return textDesc;
    }


    /**
     * Sets the textDesc value for this GroupsType.
     * 
     * @param textDesc
     */
    public void setTextDesc(java.lang.String textDesc) {
        this.textDesc = textDesc;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GroupsType)) return false;
        GroupsType other = (GroupsType) obj;
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
            ((this.elements==null && other.getElements()==null) || 
             (this.elements!=null &&
              this.elements.equals(other.getElements()))) &&
            ((this.required==null && other.getRequired()==null) || 
             (this.required!=null &&
              this.required.equals(other.getRequired()))) &&
            ((this.exclusive==null && other.getExclusive()==null) || 
             (this.exclusive!=null &&
              this.exclusive.equals(other.getExclusive()))) &&
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
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getElements() != null) {
            _hashCode += getElements().hashCode();
        }
        if (getRequired() != null) {
            _hashCode += getRequired().hashCode();
        }
        if (getExclusive() != null) {
            _hashCode += getExclusive().hashCode();
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
        new org.apache.axis.description.TypeDesc(GroupsType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://nbcr.sdsc.edu/opal/types", "GroupsType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("id");
        elemField.setXmlName(new javax.xml.namespace.QName("", "id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "ID"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("", "name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("elements");
        elemField.setXmlName(new javax.xml.namespace.QName("", "elements"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "IDREFS"));
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
        elemField.setFieldName("exclusive");
        elemField.setXmlName(new javax.xml.namespace.QName("", "exclusive"));
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
