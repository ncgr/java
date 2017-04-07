/**
 * AppService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package edu.sdsc.nbcr.opal;

public interface AppService extends javax.xml.rpc.Service {
    public java.lang.String getAppServicePortAddress();

    public edu.sdsc.nbcr.opal.AppServicePortType getAppServicePort() throws javax.xml.rpc.ServiceException;

    public edu.sdsc.nbcr.opal.AppServicePortType getAppServicePort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
