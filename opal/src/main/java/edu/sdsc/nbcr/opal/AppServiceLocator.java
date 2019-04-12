/**
 * AppServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package edu.sdsc.nbcr.opal;

public class AppServiceLocator extends org.apache.axis.client.Service implements edu.sdsc.nbcr.opal.AppService {

    public AppServiceLocator() {
    }


    public AppServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public AppServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for AppServicePort
    private java.lang.String AppServicePort_address = "http://localhost:8080/opal2/services/meme";

    public java.lang.String getAppServicePortAddress() {
        return AppServicePort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String AppServicePortWSDDServiceName = "AppServicePort";

    public java.lang.String getAppServicePortWSDDServiceName() {
        return AppServicePortWSDDServiceName;
    }

    public void setAppServicePortWSDDServiceName(java.lang.String name) {
        AppServicePortWSDDServiceName = name;
    }

    public edu.sdsc.nbcr.opal.AppServicePortType getAppServicePort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(AppServicePort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getAppServicePort(endpoint);
    }

    public edu.sdsc.nbcr.opal.AppServicePortType getAppServicePort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            edu.sdsc.nbcr.opal.AppServicePortSoapBindingStub _stub = new edu.sdsc.nbcr.opal.AppServicePortSoapBindingStub(portAddress, this);
            _stub.setPortName(getAppServicePortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setAppServicePortEndpointAddress(java.lang.String address) {
        AppServicePort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (edu.sdsc.nbcr.opal.AppServicePortType.class.isAssignableFrom(serviceEndpointInterface)) {
                edu.sdsc.nbcr.opal.AppServicePortSoapBindingStub _stub = new edu.sdsc.nbcr.opal.AppServicePortSoapBindingStub(new java.net.URL(AppServicePort_address), this);
                _stub.setPortName(getAppServicePortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("AppServicePort".equals(inputPortName)) {
            return getAppServicePort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://nbcr.sdsc.edu/opal", "AppService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://nbcr.sdsc.edu/opal", "AppServicePort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("AppServicePort".equals(portName)) {
            setAppServicePortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
