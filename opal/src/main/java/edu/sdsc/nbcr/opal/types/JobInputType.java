/**
 * JobInputType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package edu.sdsc.nbcr.opal.types;

public class JobInputType  implements java.io.Serializable {
    private java.lang.String argList;
    private java.lang.Integer numProcs;
    private java.lang.String userEmail;
    private java.lang.String password;
    private org.apache.axis.types.NonNegativeInteger wallClockTime;
    private edu.sdsc.nbcr.opal.types.InputFileType[] inputFile;
    private java.lang.Boolean extractInputs;
    private java.lang.Boolean sendNotification;

    public JobInputType() {
    }

    public JobInputType(
           java.lang.String argList,
           java.lang.Integer numProcs,
           java.lang.String userEmail,
           java.lang.String password,
           org.apache.axis.types.NonNegativeInteger wallClockTime,
           edu.sdsc.nbcr.opal.types.InputFileType[] inputFile,
           java.lang.Boolean extractInputs,
           java.lang.Boolean sendNotification) {
           this.argList = argList;
           this.numProcs = numProcs;
           this.userEmail = userEmail;
           this.password = password;
           this.wallClockTime = wallClockTime;
           this.inputFile = inputFile;
           this.extractInputs = extractInputs;
           this.sendNotification = sendNotification;
    }


    /**
     * Gets the argList value for this JobInputType.
     * 
     * @return argList
     */
    public java.lang.String getArgList() {
        return argList;
    }


    /**
     * Sets the argList value for this JobInputType.
     * 
     * @param argList
     */
    public void setArgList(java.lang.String argList) {
        this.argList = argList;
    }


    /**
     * Gets the numProcs value for this JobInputType.
     * 
     * @return numProcs
     */
    public java.lang.Integer getNumProcs() {
        return numProcs;
    }


    /**
     * Sets the numProcs value for this JobInputType.
     * 
     * @param numProcs
     */
    public void setNumProcs(java.lang.Integer numProcs) {
        this.numProcs = numProcs;
    }


    /**
     * Gets the userEmail value for this JobInputType.
     * 
     * @return userEmail
     */
    public java.lang.String getUserEmail() {
        return userEmail;
    }


    /**
     * Sets the userEmail value for this JobInputType.
     * 
     * @param userEmail
     */
    public void setUserEmail(java.lang.String userEmail) {
        this.userEmail = userEmail;
    }


    /**
     * Gets the password value for this JobInputType.
     * 
     * @return password
     */
    public java.lang.String getPassword() {
        return password;
    }


    /**
     * Sets the password value for this JobInputType.
     * 
     * @param password
     */
    public void setPassword(java.lang.String password) {
        this.password = password;
    }


    /**
     * Gets the wallClockTime value for this JobInputType.
     * 
     * @return wallClockTime
     */
    public org.apache.axis.types.NonNegativeInteger getWallClockTime() {
        return wallClockTime;
    }


    /**
     * Sets the wallClockTime value for this JobInputType.
     * 
     * @param wallClockTime
     */
    public void setWallClockTime(org.apache.axis.types.NonNegativeInteger wallClockTime) {
        this.wallClockTime = wallClockTime;
    }


    /**
     * Gets the inputFile value for this JobInputType.
     * 
     * @return inputFile
     */
    public edu.sdsc.nbcr.opal.types.InputFileType[] getInputFile() {
        return inputFile;
    }


    /**
     * Sets the inputFile value for this JobInputType.
     * 
     * @param inputFile
     */
    public void setInputFile(edu.sdsc.nbcr.opal.types.InputFileType[] inputFile) {
        this.inputFile = inputFile;
    }

    public edu.sdsc.nbcr.opal.types.InputFileType getInputFile(int i) {
        return this.inputFile[i];
    }

    public void setInputFile(int i, edu.sdsc.nbcr.opal.types.InputFileType _value) {
        this.inputFile[i] = _value;
    }


    /**
     * Gets the extractInputs value for this JobInputType.
     * 
     * @return extractInputs
     */
    public java.lang.Boolean getExtractInputs() {
        return extractInputs;
    }


    /**
     * Sets the extractInputs value for this JobInputType.
     * 
     * @param extractInputs
     */
    public void setExtractInputs(java.lang.Boolean extractInputs) {
        this.extractInputs = extractInputs;
    }


    /**
     * Gets the sendNotification value for this JobInputType.
     * 
     * @return sendNotification
     */
    public java.lang.Boolean getSendNotification() {
        return sendNotification;
    }


    /**
     * Sets the sendNotification value for this JobInputType.
     * 
     * @param sendNotification
     */
    public void setSendNotification(java.lang.Boolean sendNotification) {
        this.sendNotification = sendNotification;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof JobInputType)) return false;
        JobInputType other = (JobInputType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.argList==null && other.getArgList()==null) || 
             (this.argList!=null &&
              this.argList.equals(other.getArgList()))) &&
            ((this.numProcs==null && other.getNumProcs()==null) || 
             (this.numProcs!=null &&
              this.numProcs.equals(other.getNumProcs()))) &&
            ((this.userEmail==null && other.getUserEmail()==null) || 
             (this.userEmail!=null &&
              this.userEmail.equals(other.getUserEmail()))) &&
            ((this.password==null && other.getPassword()==null) || 
             (this.password!=null &&
              this.password.equals(other.getPassword()))) &&
            ((this.wallClockTime==null && other.getWallClockTime()==null) || 
             (this.wallClockTime!=null &&
              this.wallClockTime.equals(other.getWallClockTime()))) &&
            ((this.inputFile==null && other.getInputFile()==null) || 
             (this.inputFile!=null &&
              java.util.Arrays.equals(this.inputFile, other.getInputFile()))) &&
            ((this.extractInputs==null && other.getExtractInputs()==null) || 
             (this.extractInputs!=null &&
              this.extractInputs.equals(other.getExtractInputs()))) &&
            ((this.sendNotification==null && other.getSendNotification()==null) || 
             (this.sendNotification!=null &&
              this.sendNotification.equals(other.getSendNotification())));
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
        if (getArgList() != null) {
            _hashCode += getArgList().hashCode();
        }
        if (getNumProcs() != null) {
            _hashCode += getNumProcs().hashCode();
        }
        if (getUserEmail() != null) {
            _hashCode += getUserEmail().hashCode();
        }
        if (getPassword() != null) {
            _hashCode += getPassword().hashCode();
        }
        if (getWallClockTime() != null) {
            _hashCode += getWallClockTime().hashCode();
        }
        if (getInputFile() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getInputFile());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getInputFile(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getExtractInputs() != null) {
            _hashCode += getExtractInputs().hashCode();
        }
        if (getSendNotification() != null) {
            _hashCode += getSendNotification().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(JobInputType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://nbcr.sdsc.edu/opal/types", "JobInputType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("argList");
        elemField.setXmlName(new javax.xml.namespace.QName("", "argList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numProcs");
        elemField.setXmlName(new javax.xml.namespace.QName("", "numProcs"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userEmail");
        elemField.setXmlName(new javax.xml.namespace.QName("", "userEmail"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("password");
        elemField.setXmlName(new javax.xml.namespace.QName("", "password"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("wallClockTime");
        elemField.setXmlName(new javax.xml.namespace.QName("", "wallClockTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "nonNegativeInteger"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("inputFile");
        elemField.setXmlName(new javax.xml.namespace.QName("", "inputFile"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://nbcr.sdsc.edu/opal/types", "InputFileType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extractInputs");
        elemField.setXmlName(new javax.xml.namespace.QName("", "extractInputs"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sendNotification");
        elemField.setXmlName(new javax.xml.namespace.QName("", "sendNotification"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
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
