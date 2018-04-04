package org.ncgr.motifs;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Properties;

/**
 * Database connection utility.
 *
 * @author Sam Hokin
 */
public class DB {

  /** static definitions */
  static final String PROPERTIESFILE = "db.properties";
  static final String DSKEY = "db.datasource";
  static final String DRIVERKEY = "db.driver";
  static final String URLKEY = "db.url";
  static final String USERKEY = "db.user";
  static final String PASSWORDKEY = "db.password";
  static final String TYPEKEY = "db.type";

  /** settings are stored in a Properties object */
  Properties properties;

  /** DB Connection, Statement, ResultSet */
  public ResultSet rs;
  public PreparedStatement ps;
  Connection conn;
  Statement stmt;

  /** database type, for database-specific methods */
  public String dbtype = "pgsql";  // default

  /**
   * application constructor with db.properties file containing:
   *   db.driver
   *   db.url
   *   db.user
   *   db.password
   *   db.type [pgsql, mysql, MSSQL]
   */
  public DB() throws FileNotFoundException, IOException, ClassNotFoundException, SQLException {
    getPropertiesFromFile();
    makeConnection(properties.getProperty(USERKEY),properties.getProperty(PASSWORDKEY));
  }

  /** load the properties from a properties file */
  void getPropertiesFromFile() throws FileNotFoundException, IOException {
    properties = new Properties();
    FileInputStream in = new FileInputStream(PROPERTIESFILE);
    properties.load(in);
    in.close();
  }

  /** make the db connection using the supplied user and password */
  void makeConnection(String user, String password) throws ClassNotFoundException, SQLException {
    String driverName = properties.getProperty(DRIVERKEY);
    String url = properties.getProperty(URLKEY);
    Class.forName(driverName);
    conn = DriverManager.getConnection(url, user, password);
    stmt = conn.createStatement();
  }

  /** execute a query which returns a ResultSet (SELECT) */
  public void executeQuery(String query) throws SQLException {
    rs = stmt.executeQuery(query);
  }

  /** execute a query which does not return a ResultSet (INSERT/UPDATE/DELETE) */
  public void executeUpdate(String query) throws SQLException {
    stmt.executeUpdate(query);
  }

  /** get a prepared statement */
  public void prepareStatement(String s) throws SQLException {
    ps = conn.prepareStatement(s);
  }

  /** execute a prepared statement update query */
  public void executeUpdate() throws SQLException {
    ps.executeUpdate();
  }

  /** Close the connections, and set = null so they aren't closed again */
  public void close() throws SQLException {
    if (rs!=null) rs.close();
    if (ps!=null) ps.close();
    if (stmt!=null) stmt.close();
    if (conn!=null) conn.close();
  }

  /** convenience routine to return an empty string if null, otherwise the string */
  public String getStringOrBlank(String fieldname) throws SQLException {
    if (rs.getString(fieldname)==null) {
      return "";
    } else {
      return rs.getString(fieldname);
    }
  }

  /** convenience routine to return an empty string if 0, otherwise the int */
  public String getIntOrBlank(String fieldname) throws SQLException {
    if (rs.getInt(fieldname)==0) {
      return "";
    } else {
      return ""+rs.getInt(fieldname);
    }
  }

  /** returns the timezone from the database */
  public String getTimeZone() throws SQLException {
    String timezone = "US/Central"; // default, could be wrong
    if (dbtype.equals("pgsql")) {
      // time zones in timestamps only supported for postgresql so far
      executeQuery("SHOW TimeZone");
      if (rs.next()) timezone = rs.getString("TimeZone");
    } else if (dbtype.equals("mysql")) {
      // get system time zone, mysql doesn't store time zone with timestamps; this is in CDT format
      executeQuery("SELECT @@system_time_zone");
      if (rs.next()) timezone = rs.getString("@@system_time_zone");
    }
    return timezone;
  }

  /** returns the current time from the database */
  public Timestamp getCurrentTimestamp() throws SQLException {
    executeQuery("SELECT "+now()+" AS now");
    rs.next();
    return rs.getTimestamp("now");
  }

  /** returns the database-specific current-time function */
  public String now() {
    if (dbtype.equals("pgsql") || dbtype.equals("mysql")) {
      return "CURRENT_TIMESTAMP";
    } else if (dbtype.equals("MSSQL")) {
      return "getdate()";
    } else {
      return null;
    }
  }

  /** returns the database-specific case-independent LIKE operator */
  public String iLike() {
    if (dbtype.equals("pgsql")) {
      return "ILIKE";
    } else if (dbtype.equals("MSSQL") || dbtype.equals("mysql")) {
      return "LIKE";
    } else {
      return "LIKE";
    }
  }

  /** returns the database-specific boolean value; assume mysql uses tinyint(1)=bool=boolean */
  public String bool(boolean val) {
    if (dbtype.equals("pgsql")) {
      if (val) return "true"; else return "false";
    } else if (dbtype.equals("MSSQL") || dbtype.equals("mysql")) {
      if (val) return "1"; else return "0";
    } else {
      return ""+val;
    }
  }

  /** returns the name of the database */
  public String getDBName() throws SQLException {
    String dbName = null;
    if (dbtype.equals("pgsql")) {
      executeQuery("SELECT current_database() AS db_name");
      if (rs.next()) dbName = rs.getString("db_name");
    } else if (dbtype.equals("mysql")) {
      executeQuery("SELECT DATABASE() AS db_name");
      if (rs.next()) dbName = rs.getString("db_name");
    } else if (dbtype.equals("MSSQL")) {
      executeQuery("SELECT DB_NAME() AS db_name");
      if (rs.next()) dbName = rs.getString("db_name");
    }
    return dbName;
  }

  /** returns the version of the database server */
  public String getDBVersion() throws SQLException {
    String dbVersion = null;
    if (dbtype.equals("pgsql") || dbtype.equals("mysql")) {
      executeQuery("SELECT version() AS version");
      if (rs.next()) dbVersion = rs.getString("version");
    } else if (dbtype.equals("MSSQL")) {
      executeQuery("SELECT @@VERSION AS version");
      if (rs.next()) dbVersion = rs.getString("version");
    }
    return dbVersion;
  }

  /** Sets the search path to be the given schema and NOT public */
  public void setSearchPath(String schema) throws SQLException {
    executeUpdate("SET search_path TO "+schema);
  }

}
