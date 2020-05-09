package org.ncgr.datastore;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Encapsulate the auth and other iRODS parameters in this object, which can be loaded from a properties file.
 */
public class IRODSParameters {

    String host;
    int port;
    String user;
    String password;
    String homeDirectory;
    String userZone;
    String defaultStorageResource;

    /**
     * Construct from a populated Properties object.
     */
    public IRODSParameters(Properties props) {
        init(props);
    }

    /**
     * Construct from a properties filename.
     */
    public IRODSParameters(String filename) throws IOException, FileNotFoundException {
        Properties props = new Properties();
        props.load(new FileInputStream(filename));
        init(props);
    }

    /**
     * Set fields from a properties object.
     */
    protected void init(Properties props) {
        host = props.getProperty("irods.host");
        port = Integer.parseInt(props.getProperty("irods.port"));
        user = props.getProperty("irods.user");
        password = props.getProperty("irods.password");
        homeDirectory = props.getProperty("irods.home.directory");
        userZone = props.getProperty("irods.user.zone");
        defaultStorageResource = props.getProperty("irods.default.storage.resource");
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getHomeDirectory() {
        return homeDirectory;
    }

    public String getUserZone() {
        return userZone;
    }

    public String getDefaultStorageResource() {
        return defaultStorageResource;
    }

}
