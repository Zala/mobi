package org.uninova.mobis.utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Class DBUtilsImpl
 * @author PAF@UNINOVA
 */
public class DBUtilsImpl implements DBUtils {

	/**
	 * DBUtilsImpl Class Constructor
	 */
	public DBUtilsImpl() {}
	
	/**
	 * Starts a {@link java.sql.Connection} to the database configured on the input file <code>configFile</code>
	 * @param configFile	The name for the XML configuration file
	 * @return				The {@link java.sql.Connection} object
	 */
	public Connection startConnection(String configFile, String database, String connection) {
		try {
			Node nNode ;
			Element eElement ;
			String host = "", port = "", url = "", username = "", password = "", driver = "" ;
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(this.getClass().getResourceAsStream(configFile)) ;
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("dbconfig") ;
			
			for (int temp = 0; temp < nList.getLength(); temp++) {
				nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					eElement = (Element) nNode;
					driver = eElement.getElementsByTagName("driver").item(0).getTextContent() ;
					host = eElement.getElementsByTagName("host").item(0).getTextContent() ;
					port = eElement.getElementsByTagName("port").item(0).getTextContent() ;
					username = eElement.getElementsByTagName("user").item(0).getTextContent() ;
					password = eElement.getElementsByTagName("pwd").item(0).getTextContent() ;
					url = connection + "://" + host + ":" + port + "/" + database;
				}
			}
			
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, username, password);
			
			return conn ;
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null ;
	}
	
	/**
	 * Starts a {@link java.sql.Connection} to the database referred on the input parameter <code>url</code>
	 * @param url		The URL for the database connection to start
	 * @param username	The username for the database connection to start
	 * @param password 	The password for the database connection to start
	 * @return			The {@link java.sql.Connection} object
	 */
	public Connection startConnection(String url, String username, String password, String driver) {
		try {
			//Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, username, password);
			
			return conn ;
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null ;
	}
	
	/**
	 * Executes a query to the database given by the input parameter <code>conn</code>
	 * @param conn		The {@link java.sql.Connection} object that references the database to query
	 * @param sqlQuery	The query statement
	 * @return			A {@link java.sql.ResultSet} object with the response for the input query
	 */
	public ResultSet queryDB(Connection conn, String sqlQuery) {
		try {	
			ResultSet set = conn.createStatement().executeQuery(sqlQuery) ;
			
			return set ;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null ;
	}
	
	/**
	 * Executes an update on the database given by the input parameter <code>conn</code>
	 * @param conn			The {@link java.sql.Connection} object that references the database to update
	 * @param sqlStatement	The update statement
	 * @return				The exit value for the database update
	 */
	public int modifyDB(Connection conn, String sqlStatement) {
		try {
			return conn.createStatement().executeUpdate(sqlStatement) ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1 ;
	}
	
	/**
	 * Formats a {@link java.sql.Date} object and a {@link java.sql.Time} object into a {@link java.lang.String} object in the format "yyyy-MM-dd HH:mm:ss"  
	 * @param date	The {@link java.sql.Date} object to format
	 * @param time	The {@link java.sql.Time} object to format
	 * @return 		The formatted {@link java.lang.String} object
	 */
	public String readDateTimeToString(Date date, Time time) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal, timeCal ;
		timeCal = Calendar.getInstance() ;
		timeCal.setTime(time) ;
		cal = Calendar.getInstance() ;
		cal.setTime(date) ;
		cal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE)) ;
		cal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY)) ;
		cal.set(Calendar.SECOND, timeCal.get(Calendar.SECOND)) ;
		
		return dateFormat.format(cal.getTime()) ;
	}
	
	/**
	 * Parses a {@link java.lang.String} object into a {@link java.sql.Date} object and a {@link java.sql.Time} object
	 * @param string	The {@link java.lang.String} object
	 * @return			An array with two positions: the first with the {@link java.sql.Date} object and the second with the {@link java.sql.Time} object
	 */
	public Object[] readStringToDateTime(String string) {
		if (string != null && !string.equals("")) {
			Date date = Date.valueOf(string.substring(0, string.indexOf(" "))) ;
			Time time = Time.valueOf(string.substring(string.indexOf(" ") + 1)) ;
			return new Object[]{date, time} ;
		}
		return null ;
	}
}
