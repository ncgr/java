/**
 * SystemInfoType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package edu.sdsc.nbcr.opal.types;

public class SystemInfoType  implements java.io.Serializable {
    private java.lang.String jobManagerType;
    private java.lang.String dataLifetime;
    private int hardLimit;
    private int numCpuTotal;
    private int numCpuFree;
    private int numJobsRunning;
    private int numJobsQueued;

    public SystemInfoType() {
    }

    public SystemInfoType(
           java.lang.String jobManagerType,
           java.lang.String dataLifetime,
           int hardLimit,
           int numCpuTotal,
           int numCpuFree,
           int numJobsRunning,
           int numJobsQueued) {
           this.jobManagerType = jobManagerType;
           this.dataLifetime = dataLifetime;
           this.hardLimit = hardLimit;
           this.numCpuTotal = numCpuTotal;
           this.numCpuFree = numCpuFree;
           this.numJobsRunning = numJobsRunning;
           this.numJobsQueued = numJobsQueued;
    }


    /**
     * Gets the jobManagerType value for this SystemInfoType.
     * 
     * @return jobManagerType
     */
    public java.lang.String getJobManagerType() {
        return jobManagerType;
    }


    /**
     * Sets the jobManagerType value for this SystemInfoType.
     * 
     * @param jobManagerType
     */
    public void setJobManagerType(java.lang.String jobManagerType) {
        this.jobManagerType = jobManagerType;
    }


    /**
     * Gets the dataLifetime value for this SystemInfoType.
     * 
     * @return dataLifetime
     */
    public java.lang.String getDataLifetime() {
        return dataLifetime;
    }


    /**
     * Sets the dataLifetime value for this SystemInfoType.
     * 
     * @param dataLifetime
     */
    public void setDataLifetime(java.lang.String dataLifetime) {
        this.dataLifetime = dataLifetime;
    }


    /**
     * Gets the hardLimit value for this SystemInfoType.
     * 
     * @return hardLimit
     */
    public int getHardLimit() {
        return hardLimit;
    }


    /**
     * Sets the hardLimit value for this SystemInfoType.
     * 
     * @param hardLimit
     */
    public void setHardLimit(int hardLimit) {
        this.hardLimit = hardLimit;
    }


    /**
     * Gets the numCpuTotal value for this SystemInfoType.
     * 
     * @return numCpuTotal
     */
    public int getNumCpuTotal() {
        return numCpuTotal;
    }


    /**
     * Sets the numCpuTotal value for this SystemInfoType.
     * 
     * @param numCpuTotal
     */
    public void setNumCpuTotal(int numCpuTotal) {
        this.numCpuTotal = numCpuTotal;
    }


    /**
     * Gets the numCpuFree value for this SystemInfoType.
     * 
     * @return numCpuFree
     */
    public int getNumCpuFree() {
        return numCpuFree;
    }


    /**
     * Sets the numCpuFree value for this SystemInfoType.
     * 
     * @param numCpuFree
     */
    public void setNumCpuFree(int numCpuFree) {
        this.numCpuFree = numCpuFree;
    }


    /**
     * Gets the numJobsRunning value for this SystemInfoType.
     * 
     * @return numJobsRunning
     */
    public int getNumJobsRunning() {
        return numJobsRunning;
    }


    /**
     * Sets the numJobsRunning value for this SystemInfoType.
     * 
     * @param numJobsRunning
     */
    public void setNumJobsRunning(int numJobsRunning) {
        this.numJobsRunning = numJobsRunning;
    }


    /**
     * Gets the numJobsQueued value for this SystemInfoType.
     * 
     * @return numJobsQueued
     */
    public int getNumJobsQueued() {
        return numJobsQueued;
    }


    /**
     * Sets the numJobsQueued value for this SystemInfoType.
     * 
     * @param numJobsQueued
     */
    public void setNumJobsQueued(int numJobsQueued) {
        this.numJobsQueued = numJobsQueued;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SystemInfoType)) return false;
        SystemInfoType other = (SystemInfoType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.jobManagerType==null && other.getJobManagerType()==null) || 
             (this.jobManagerType!=null &&
              this.jobManagerType.equals(other.getJobManagerType()))) &&
            ((this.dataLifetime==null && other.getDataLifetime()==null) || 
             (this.dataLifetime!=null &&
              this.dataLifetime.equals(other.getDataLifetime()))) &&
            this.hardLimit == other.getHardLimit() &&
            this.numCpuTotal == other.getNumCpuTotal() &&
            this.numCpuFree == other.getNumCpuFree() &&
            this.numJobsRunning == other.getNumJobsRunning() &&
            this.numJobsQueued == other.getNumJobsQueued();
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
        if (getJobManagerType() != null) {
            _hashCode += getJobManagerType().hashCode();
        }
        if (getDataLifetime() != null) {
            _hashCode += getDataLifetime().hashCode();
        }
        _hashCode += getHardLimit();
        _hashCode += getNumCpuTotal();
        _hashCode += getNumCpuFree();
        _hashCode += getNumJobsRunning();
        _hashCode += getNumJobsQueued();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SystemInfoType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://nbcr.sdsc.edu/opal/types", "SystemInfoType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("jobManagerType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "jobManagerType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dataLifetime");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dataLifetime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hardLimit");
        elemField.setXmlName(new javax.xml.namespace.QName("", "hardLimit"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numCpuTotal");
        elemField.setXmlName(new javax.xml.namespace.QName("", "numCpuTotal"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numCpuFree");
        elemField.setXmlName(new javax.xml.namespace.QName("", "numCpuFree"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numJobsRunning");
        elemField.setXmlName(new javax.xml.namespace.QName("", "numJobsRunning"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numJobsQueued");
        elemField.setXmlName(new javax.xml.namespace.QName("", "numJobsQueued"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
