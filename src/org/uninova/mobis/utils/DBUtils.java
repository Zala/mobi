package org.uninova.mobis.utils;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Time;

/**
 * Interface DBUtils
 * @author PAF@UNINOVA
 */
public interface DBUtils {

	/**
	 * Starts a {@link java.sql.Connection} to the database configured on the input file <code>configFile</code>
	 * @param configFile	The name for the XML configuration file
	 * @return				The {@link java.sql.Connection} object
	 */
	public Connection startConnection(String configFile, String database, String connection) ;
	
	/**
	 * Starts a {@link java.sql.Connection} to the database referred on the input parameter <code>url</code>
	 * @param url		The URL for the database connection to start
	 * @param username	The username for the database connection to start
	 * @param password 	The password for the database connection to start
	 * @return			The {@link java.sql.Connection} object
	 */
	public Connection startConnection(String url, String username, String password, String driver) ;
	
	/**
	 * Executes a query to the database given by the input parameter <code>conn</code>
	 * @param conn		The {@link java.sql.Connection} object that references the database to query
	 * @param sqlQuery	The query statement
	 * @return			A {@link java.sql.ResultSet} object with the response for the input query
	 */
	public ResultSet queryDB(Connection conn, String sqlQuery) ;
	
	/**
	 * Executes an update on the database given by the input parameter <code>conn</code>
	 * @param conn			The {@link java.sql.Connection} object that references the database to update
	 * @param sqlStatement	The update statement
	 * @return				The exit value for the database update
	 */
	public int modifyDB(Connection conn, String sqlStatement) ;
	
	/**
	 * Formats a {@link java.sql.Date} object and a {@link java.sql.Time} object into a {@link java.lang.String} object in the format "yyyy-MM-dd HH:mm:ss"  
	 * @param date	The {@link java.sql.Date} object to format
	 * @param time	The {@link java.sql.Time} object to format
	 * @return 		The formatted {@link java.lang.String} object
	 */
	public String readDateTimeToString(Date date, Time time) ;
	
	/**
	 * Parses a {@link java.lang.String} object into a {@link java.sql.Date} object and a {@link java.sql.Time} object
	 * @param string	The {@link java.lang.String} object
	 * @return			An array with two positions: the first with the {@link java.sql.Date} object and the second with the {@link java.sql.Time} object
	 */
	public Object[] readStringToDateTime(String string) ;
}
