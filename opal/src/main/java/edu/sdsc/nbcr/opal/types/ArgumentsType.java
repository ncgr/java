/**
 * ArgumentsType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package edu.sdsc.nbcr.opal.types;

public class ArgumentsType  implements java.io.Serializable {
    private edu.sdsc.nbcr.opal.types.FlagsType[] flags;
    private edu.sdsc.nbcr.opal.types.ParamsArrayType taggedParams;
    private edu.sdsc.nbcr.opal.types.ParamsArrayType untaggedParams;
    private edu.sdsc.nbcr.opal.types.ImplicitParamsType[] implicitParams;
    private edu.sdsc.nbcr.opal.types.GroupsType[] groups;

    public ArgumentsType() {
    }

    public ArgumentsType(
           edu.sdsc.nbcr.opal.types.FlagsType[] flags,
           edu.sdsc.nbcr.opal.types.ParamsArrayType taggedParams,
           edu.sdsc.nbcr.opal.types.ParamsArrayType untaggedParams,
           edu.sdsc.nbcr.opal.types.ImplicitParamsType[] implicitParams,
           edu.sdsc.nbcr.opal.types.GroupsType[] groups) {
           this.flags = flags;
           this.taggedParams = taggedParams;
           this.untaggedParams = untaggedParams;
           this.implicitParams = implicitParams;
           this.groups = groups;
    }


    /**
     * Gets the flags value for this ArgumentsType.
     * 
     * @return flags
     */
    public edu.sdsc.nbcr.opal.types.FlagsType[] getFlags() {
        return flags;
    }


    /**
     * Sets the flags value for this ArgumentsType.
     * 
     * @param flags
     */
    public void setFlags(edu.sdsc.nbcr.opal.types.FlagsType[] flags) {
        this.flags = flags;
    }


    /**
     * Gets the taggedParams value for this ArgumentsType.
     * 
     * @return taggedParams
     */
    public edu.sdsc.nbcr.opal.types.ParamsArrayType getTaggedParams() {
        return taggedParams;
    }


    /**
     * Sets the taggedParams value for this ArgumentsType.
     * 
     * @param taggedParams
     */
    public void setTaggedParams(edu.sdsc.nbcr.opal.types.ParamsArrayType taggedParams) {
        this.taggedParams = taggedParams;
    }


    /**
     * Gets the untaggedParams value for this ArgumentsType.
     * 
     * @return untaggedParams
     */
    public edu.sdsc.nbcr.opal.types.ParamsArrayType getUntaggedParams() {
        return untaggedParams;
    }


    /**
     * Sets the untaggedParams value for this ArgumentsType.
     * 
     * @param untaggedParams
     */
    public void setUntaggedParams(edu.sdsc.nbcr.opal.types.ParamsArrayType untaggedParams) {
        this.untaggedParams = untaggedParams;
    }


    /**
     * Gets the implicitParams value for this ArgumentsType.
     * 
     * @return implicitParams
     */
    public edu.sdsc.nbcr.opal.types.ImplicitParamsType[] getImplicitParams() {
        return implicitParams;
    }


    /**
     * Sets the implicitParams value for this ArgumentsType.
     * 
     * @param implicitParams
     */
    public void setImplicitParams(edu.sdsc.nbcr.opal.types.ImplicitParamsType[] implicitParams) {
        this.implicitParams = implicitParams;
    }


    /**
     * Gets the groups value for this ArgumentsType.
     * 
     * @return groups
     */
    public edu.sdsc.nbcr.opal.types.GroupsType[] getGroups() {
        return groups;
    }


    /**
     * Sets the groups value for this ArgumentsType.
     * 
     * @param groups
     */
    public void setGroups(edu.sdsc.nbcr.opal.types.GroupsType[] groups) {
        this.groups = groups;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArgumentsType)) return false;
        ArgumentsType other = (ArgumentsType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.flags==null && other.getFlags()==null) || 
             (this.flags!=null &&
              java.util.Arrays.equals(this.flags, other.getFlags()))) &&
            ((this.taggedParams==null && other.getTaggedParams()==null) || 
             (this.taggedParams!=null &&
              this.taggedParams.equals(other.getTaggedParams()))) &&
            ((this.untaggedParams==null && other.getUntaggedParams()==null) || 
             (this.untaggedParams!=null &&
              this.untaggedParams.equals(other.getUntaggedParams()))) &&
            ((this.implicitParams==null && other.getImplicitParams()==null) || 
             (this.implicitParams!=null &&
              java.util.Arrays.equals(this.implicitParams, other.getImplicitParams()))) &&
            ((this.groups==null && other.getGroups()==null) || 
             (this.groups!=null &&
              java.util.Arrays.equals(this.groups, other.getGroups())));
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
        if (getFlags() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getFlags());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getFlags(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getTaggedParams() != null) {
            _hashCode += getTaggedParams().hashCode();
        }
        if (getUntaggedParams() != null) {
            _hashCode += getUntaggedParams().hashCode();
        }
        if (getImplicitParams() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getImplicitParams());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getImplicitParams(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getGroups() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getGroups());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getGroups(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ArgumentsType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://nbcr.sdsc.edu/opal/types", "ArgumentsType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("flags");
        elemField.setXmlName(new javax.xml.namespace.QName("", "flags"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://nbcr.sdsc.edu/opal/types", "FlagsType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "flag"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("taggedParams");
        elemField.setXmlName(new javax.xml.namespace.QName("", "taggedParams"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://nbcr.sdsc.edu/opal/types", "ParamsArrayType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("untaggedParams");
        elemField.setXmlName(new javax.xml.namespace.QName("", "untaggedParams"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://nbcr.sdsc.edu/opal/types", "ParamsArrayType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("implicitParams");
        elemField.setXmlName(new javax.xml.namespace.QName("", "implicitParams"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://nbcr.sdsc.edu/opal/types", "ImplicitParamsType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "param"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("groups");
        elemField.setXmlName(new javax.xml.namespace.QName("", "groups"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://nbcr.sdsc.edu/opal/types", "GroupsType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "group"));
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
