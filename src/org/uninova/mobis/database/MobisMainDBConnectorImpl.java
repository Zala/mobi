package org.uninova.mobis.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.uninova.mobis.constants.StringConstants;
import org.uninova.mobis.pojos.Coordinate;
import org.uninova.mobis.pojos.MobisInstruction;
import org.uninova.mobis.pojos.MobisNode;
import org.uninova.mobis.pojos.MobisPlace;
import org.uninova.mobis.pojos.MobisProfile;
import org.uninova.mobis.pojos.MobisRoute;
import org.uninova.mobis.pojos.MobisSegment;
import org.uninova.mobis.pojos.MobisSensorLog;
import org.uninova.mobis.pojos.MobisUser;
import org.uninova.mobis.pojos.MobisVehicle;
import org.uninova.mobis.utils.DBUtils;

/**
 * CLASS MobisMainDBConnectorImpl
 * @author PAF@UNINOVA
 */
public class MobisMainDBConnectorImpl implements MobisMainDBConnector {

	/**
	 * MobisMainDBConnectorImpl Class Constructor
	 */
	public MobisMainDBConnectorImpl() {}
	
	/**
	 * Gets the ID for the last saved route
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @return			The ID for the last saved route
	 */
	public long getLastRouteId(DBUtils dbUtils, Connection con) {
		String sqlQuery = "SELECT MAX(routeid) AS routeid FROM public.route;" ;
		ResultSet rs = dbUtils.queryDB(con, sqlQuery) ;
		try {
			if (rs != null && rs.next()) {
				return rs.getLong("routeid") ;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1 ;
	}
	
	/**
	 * Gets a {@link org.uninova.mobis.pojos.MobisRoute} object with the given input <code>id</code>
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @param id		The unique ID for the route instance
	 * @return			A {@link org.uninova.mobis.pojos.MobisRoute} object containing the route
	 */
	public MobisRoute getRouteByRouteId(DBUtils dbUtils, Connection con, long id) {
		String sqlQuery = "SELECT * FROM public.route WHERE routeid=" + id ;
		ResultSet rs = dbUtils.queryDB(con, sqlQuery) ;
		try {
			if (rs != null && rs.next()) {
				MobisRoute route = new MobisRoute() ;
				route.setCreationtime(dbUtils.readDateTimeToString(rs.getDate("creationdate"), rs.getTime("creationtime"))) ;
				route.setCriteria(rs.getInt("criteria")) ;
				route.setEndtime(dbUtils.readDateTimeToString(rs.getDate("enddate"), rs.getTime("endtime"))) ;
				route.setRouteId(id) ;
				route.setStarttime(dbUtils.readDateTimeToString(rs.getDate("startdate"), rs.getTime("starttime"))) ;
				route.setTransport(rs.getString("transport"));
				route.setSegments(this.getRouteSegments(dbUtils, con, id)) ;
				route.setFrequency(rs.getString("frequency")) ;
				route.setFreqNumber(rs.getInt("frequencynumber")) ;
				route.setStartCoord(new Coordinate(rs.getDouble("startlat"), rs.getDouble("startlon"))) ;
				route.setEndCoord(new Coordinate(rs.getDouble("endlat"), rs.getDouble("endlon"))) ;
				return route ;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null ;
	}
	
	/**
	 * Gets the number of total routes in the database
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @return			The total number of route instance in the database
	 */
	public int getRoutesCount(DBUtils dbUtils, Connection con) {
		String sqlQuery = "SELECT count(*) AS count FROM public.route;" ;
		ResultSet rs = dbUtils.queryDB(con, sqlQuery) ;
		try {
			if (rs != null && rs.next()) {
				return rs.getInt("count") ;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1 ;
	}
	
	/**
	 * Creates a new route instance in the database
	 * @param dbUtils		The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con			The {@link java.sql.Connection} to the main database
	 * @param userId		The unique ID for the user instance
	 * @param transport		The type of transport used in the route
	 * @param starttime		The start time for this route, in the format "yyyy-MM-dd HH:mm:ss"
	 * @param endtime		The end time for this route, in the format "yyyy-MM-dd HH:mm:ss"
	 * @param criteria		The criteria for the route (0: Shortest, 1: Quickest,...)
	 * @param creationtime	The creation time of this route, in the format "yyyy-MM-dd HH:mm:ss"
	 * @param frequency		The frequency of the route - DAILY, ONCE, WEEKLY, MONTHLY, YEARLY
	 * @param freqNumber	The number of times the route is made within the frequency. e.g.: frequency = "DAILY" & freqNumber = 2 -> Twice per day
	 * @return				-1 or 0 if the route creation went wrong, >0 if the transaction was successful
	 */
	public int createRoute(DBUtils dbUtils, Connection con, long userId, String transport, String starttime, String endtime, String criteria, String creationtime, String frequency, int freqNumber, Coordinate startCoord, Coordinate endCoord) {
		Object[] dateTime = dbUtils.readStringToDateTime(starttime) ;
		Date startDate = (Date) dateTime[0] ;
		Time startTime = (Time) dateTime[1] ;
		dateTime = dbUtils.readStringToDateTime(endtime) ;
		Date endDate = (Date) dateTime[0] ;
		Time endTime = (Time) dateTime[1] ;
		dateTime = dbUtils.readStringToDateTime(creationtime) ;
		Date creationDate = (Date) dateTime[0] ;
		Time creationTime = (Time) dateTime[1] ;
		String sqlInsert = "INSERT INTO public.route (userid, transport, starttime, endtime, criteria, creationtime, startdate, enddate, creationdate, frequency, frequencynumber, startlat, startlon, endlat, endlon) VALUES (" + userId + ",'" + transport + "','" + startTime + "','" + endTime + "','" + criteria + "','" + creationTime + "','" + startDate + "','" + endDate + "','" + creationDate + "','" + frequency + "'," + freqNumber + "," + startCoord.getLat() + "," + startCoord.getLng() + "," + endCoord.getLat() + "," + endCoord.getLng() + ");" ;
		return dbUtils.modifyDB(con, sqlInsert) ;
	}
	
	/**
	 * Creates a new route instance in the database
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @param route		The {@link org.uninova.mobis.pojos.MobisRoute} object containing information fir the route
	 * @param userId	The unique ID for the user instance
	 * @return			-1 or 0 if the route creation went wrong, >0 if the transaction was successful
	 */
	public int createRoute(DBUtils dbUtils, Connection con, MobisRoute route, long userId) {
		Object[] dateTime = dbUtils.readStringToDateTime(route.getStarttime()) ;
		Date startDate = (Date) dateTime[0] ;
		Time startTime = (Time) dateTime[1] ;
		dateTime = dbUtils.readStringToDateTime(route.getEndtime()) ;
		Date endDate = (Date) dateTime[0] ;
		Time endTime = (Time) dateTime[1] ;
		dateTime = dbUtils.readStringToDateTime(route.getCreationtime()) ;
		Date creationDate = (Date) dateTime[0] ;
		Time creationTime = (Time) dateTime[1] ;
		String sqlInsert = "INSERT INTO public.route (userid, transport, starttime, endtime, criteria, creationtime, startdate, enddate, creationdate, frequency, frequencynumber, startlat, startlon, endlat, endlon) VALUES (" + userId + ",'" + route.getTransport() + "','" + startTime + "','" + endTime + "','" + route.getCriteria() + "','" + creationTime + "','" + startDate + "','" + endDate + "','" + creationDate + "','" + route.getFrequency() +"'," + route.getFreqNumber() + "," + route.getStartCoord().getLat() + "," + route.getStartCoord().getLng() + "," + route.getEndCoord().getLat() + "," + route.getEndCoord().getLng() + ");" ;
		return dbUtils.modifyDB(con, sqlInsert) ;
	}
	
	/**
	 * Gets all the routes of a particular user
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @param userId	The unique ID for the user instance
	 * @return			An {@link java.util.ArrayList} object containing all the routes for the user
	 */
	public ArrayList<MobisRoute> getAllRoutesFromUser(DBUtils dbUtils, Connection con, long userId) {
		String sqlQuery = "SELECT * FROM public.route WHERE userid=" + userId ;
		ResultSet rs = dbUtils.queryDB(con, sqlQuery) ;
		ArrayList<MobisRoute> routes ;
		MobisRoute route ;
		if (rs != null) {
			try {
				routes = new ArrayList<>() ;
				while (rs.next()) {
					route = new MobisRoute() ;
					route.setRouteId(rs.getLong("routeid")) ;
					route.setTransport(rs.getString("transport")) ;
					route.setStarttime(dbUtils.readDateTimeToString(rs.getDate("startdate"), rs.getTime("starttime"))) ;
					route.setEndtime(dbUtils.readDateTimeToString(rs.getDate("enddate"), rs.getTime("endtime"))) ;
					route.setCreationtime(dbUtils.readDateTimeToString(rs.getDate("creationdate"), rs.getTime("creationtime"))) ;
					route.setCriteria(rs.getInt("criteria")) ;
					route.setFrequency(rs.getString("frequency")) ;
					route.setFreqNumber(rs.getInt("frequencynumber")) ;
					route.setStartCoord(new Coordinate(rs.getDouble("startlat"),rs.getDouble("startlon"))) ;
					route.setEndCoord(new Coordinate(rs.getDouble("endlat"), rs.getDouble("endlon"))) ;
					route.setSegments(this.getRouteSegments(dbUtils, con, route.getRouteId())) ;
					routes.add(route) ;
				}
				return routes ;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null ;
	}
	
	/**
	 * Gets the routes from a particular user that are planned to a specific date
	 * @param dbUtils			The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con				The {@link java.sql.Connection} to the main database
	 * @param userId			The unique ID for the user instance
	 * @param dateTimeString	The date and time for departure or arrival
	 * @param isStartTime		true if dateTimeString is the start time of the route, false if dateTimeString is the arrival time of the route
	 * @return					An {@link java.util.ArrayList} object containing all the routes for the user starting or finishing at the time given by dateTimeString
	 */
	public ArrayList<MobisRoute> getRouteFromUserByDate(DBUtils dbUtils, Connection con, long userId, String dateTimeString, boolean isStartTime) {
		String sqlQuery ;
		Object[] dateTime = dbUtils.readStringToDateTime(dateTimeString) ;
		ArrayList<MobisRoute> routes ;
		MobisRoute route ;
		ResultSet rs ;
		
		if (isStartTime) {
			sqlQuery = "SELECT * FROM public.route WHERE userid=" + userId + " AND startdate='" + (Date) dateTime[0] + "' AND starttime='" + (Time) dateTime[1] + "';" ;
		}
		else {
			sqlQuery = "SELECT * FROM public.route WHERE userid=" + userId + " AND enddate='" + (Date) dateTime[0] + "' AND endtime='" + (Time) dateTime[1] + "';" ;
		}
		rs = dbUtils.queryDB(con, sqlQuery) ;
		if (rs != null) {
			try {
				routes = new ArrayList<>() ;
				while (rs.next()) {
					route = new MobisRoute() ;
					route.setRouteId(rs.getLong("routeid")) ;
					route.setTransport(rs.getString("transport")) ;
					route.setStarttime(dbUtils.readDateTimeToString(rs.getDate("startdate"), rs.getTime("starttime"))) ;
					route.setEndtime(dbUtils.readDateTimeToString(rs.getDate("enddate"), rs.getTime("endtime"))) ;
					route.setCreationtime(dbUtils.readDateTimeToString(rs.getDate("creationdate"), rs.getTime("creationtime"))) ;
					route.setCriteria(rs.getInt("criteria")) ;
					route.setFrequency(rs.getString("frequency")) ;
					route.setFreqNumber(rs.getInt("frequencynumber")) ;
					route.setStartCoord(new Coordinate(rs.getDouble("startlat"),rs.getDouble("startlon"))) ;
					route.setEndCoord(new Coordinate(rs.getDouble("endlat"), rs.getDouble("endlon"))) ;
					route.setSegments(this.getRouteSegments(dbUtils, con, route.getRouteId())) ;
					routes.add(route) ;
				}
				return routes ;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null ;
	}
	
	// Table USER
	
	/**
	 * Get the number of user instances in the database
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @return			The number of user instances in the database
	 */
	public int getUsersCount(DBUtils dbUtils, Connection con) {
		String sqlQuery = "SELECT count(*) AS count FROM public.user;" ;
		ResultSet rs = dbUtils.queryDB(con, sqlQuery) ;
		try {
			if (rs != null && rs.next()) {
				return rs.getInt("count") ;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1 ;
	}
	
	/**
	 * Creates a new user instance in the database
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @param firstName	The first name of the user
	 * @param lastName	The last name of the user
	 * @param username	The username for the user
	 * @param password	The password for the user
	 * @param email		The email of the user
	 * @param token		The unique token created by Prophet server to identify a user
	 * @param country	The country of the user
	 * @return			-1 or 0 if the user creation went wrong, >0 if the transaction was successful
	 */
	public int createUser(DBUtils dbUtils, Connection con, String firstName, String lastName, String username, String password, String email, String gId, String token, String country, String gToken, String gRefreshToken) {
		if (!this.checkUsername(dbUtils, con, username)) {
			String sqlInsert = "INSERT INTO public.user (firstname,lastname,username,password,email,token,country,google_id, google_access_token,google_refresh_token) VALUES ('" + firstName + "','" + lastName + "','" + username + "','" + password + "','" + email + "','" + token + "','" + country + "','" + gId +"','" + gToken + "','" + gRefreshToken + "');" ;
			return dbUtils.modifyDB(con, sqlInsert) ;
		}
		else return -1 ;
	}
	
	/**
	 * Fetches an {@link java.util.ArrayList} object containing the usernames for all user instances in the database
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @return			An {@link java.util.ArrayList} object containing all usernames on the database
	 */
	public ArrayList<String> getUsernames(DBUtils dbUtils, Connection con) {
		String sqlQuery = "SELECT username FROM public.user;" ;
		ResultSet rs = dbUtils.queryDB(con, sqlQuery) ;
		ArrayList<String> usernames = new ArrayList<>() ;
		try {
			if (rs != null && rs.next()) {
				usernames.add(rs.getString("username")) ;
				while (rs.next()) {
					usernames.add(rs.getString("username")) ;
				}
			}
			rs.close() ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return usernames ;
	}

	/**
	 * Fetches an user instance from database with the given username
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @param username	The username for the user
	 * @return			An {@link org.uninova.mobis.pojos.MobisUser} object containing all the information about the user
	 */
	public MobisUser getUserByUsername(DBUtils dbUtils, Connection con, String username) {
		String sqlQuery = "SELECT * FROM public.user WHERE username='" + username + "';" ;
		ResultSet rs = dbUtils.queryDB(con, sqlQuery) ;
		MobisUser user ;
		try {
			if (rs != null && rs.next()) {
				user = new MobisUser() ;
				user.setUserId(rs.getLong("userid")) ;
				user.setFirstName(rs.getString("firstname")) ;
				user.setLastName(rs.getString("lastname")) ;
				user.setUsername(rs.getString("username")) ;
				user.setPassword(rs.getString("password")) ;
				user.setEmail(rs.getString("email")) ;
				user.setToken(rs.getString("token")) ;
				user.setCountry(rs.getString("country")) ;
				rs.close() ; 
				return user ;
			} 
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null ;
	}
	
	/**
	 * Gets the ID of the user instance with the given token
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @param token		The unique token created by Prophet server to identify a user
	 * @return			The unique ID for the user instance
	 */
	public long getUserIdFromToken(DBUtils dbUtils, Connection con, String token) {
		String sqlQuery = "SELECT userid FROM public.user WHERE token='" + token + "';" ;
		ResultSet rs = dbUtils.queryDB(con, sqlQuery) ;
		try {
			if (rs != null && rs.next()) {
				return rs.getLong("userid") ;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1 ;
	}
	
	/**
	 * Gets the user instance identified by the input userId
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @param userId	The unique ID for the user instance
	 * @return			An {@link org.uninova.mobis.pojos.MobisUser} object containing all the information about the user
	 */
	public MobisUser getUserByUserId(DBUtils dbUtils, Connection con, long userId) {
		String sqlQuery = "SELECT * FROM public.user WHERE userid=" + userId + ";" ;
		ResultSet rs = dbUtils.queryDB(con, sqlQuery) ;
		MobisUser user ;
		try {
			if (rs != null && rs.next()) {
				user = new MobisUser() ;
				user.setUserId(rs.getLong("userid")) ;
				user.setFirstName(rs.getString("firstname")) ;
				user.setLastName(rs.getString("lastname")) ;
				user.setUsername(rs.getString("username")) ;
				user.setPassword(rs.getString("password")) ;
				user.setEmail(rs.getString("email")) ;
				user.setToken(rs.getString("token")) ;
				user.setCountry(rs.getString("country")) ;
				rs.close() ; 
				return user ;
			} 
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null ;
	}
	
	public MobisUser getUserByGoogleUserId(DBUtils dbUtils, Connection con, String gUserId) {
		String sqlQuery = "SELECT * FROM public.user WHERE google_id='" + gUserId + "';" ;
		ResultSet rs = dbUtils.queryDB(con, sqlQuery) ;
		MobisUser user ;
		try {
			if (rs != null && rs.next()) {
				user = new MobisUser() ;
				user.setUserId(rs.getLong("userid")) ;
				user.setFirstName(rs.getString("firstname")) ;
				user.setLastName(rs.getString("lastname")) ;
				user.setUsername(rs.getString("username")) ;
				user.setPassword(rs.getString("password")) ;
				user.setEmail(rs.getString("email")) ;
				user.setToken(rs.getString("token")) ;
				user.setCountry(rs.getString("country")) ;
				
				String refreshToken = rs.getString("google_refresh_token").trim();
				if (refreshToken != null && !refreshToken.isEmpty())
					user.setHasOfflineGoogleAccess(true);
				rs.close() ; 
				return user ;
			} 
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null ;
	}
	
	/**
	 * Checks the existence of the input token
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @param token		The unique token created by Prophet server to identify a user
	 * @return			true if the token exists, false otherwise
	 */
	public boolean checkToken(DBUtils dbUtils, Connection con, String token) {
		String sqlQuery = "SELECT * FROM public.user WHERE token='" + token + "';" ;
		ResultSet rs = dbUtils.queryDB(con, sqlQuery) ;
		try {
			if (rs != null && rs.next()) {
				return true ;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false ;
	}
	
	/**
	 * Checks the existence of the input username
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @param username	The username for the user
	 * @return			true if the username exists, false otherwise
	 */
	public boolean checkUsername(DBUtils dbUtils, Connection con, String username) {
		if(this.getUsernames(dbUtils, con).contains(username)) {
			return true ;
		}
		else return false ;
	}
	
	/**
	 * Checks the validity of the login credentials
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @param username	The username for the user
	 * @param password	The password of the user
	 * @return			-1 if the user doesn't exist, the unique ID for the user instance, if it exists
	 */
	public long checkLoginCredentials(DBUtils dbUtils, Connection con, String username, String password) {
		String sqlQuery = "" ;
		//if (password != null && !password.equals(""))
			sqlQuery = "SELECT userid FROM public.user WHERE username='" + username + "' AND password='" + password + "';" ;
		//else if (gToken != null && !gToken.equals("")) {
		//	sqlQuery = "SELECT userid FROM public.user WHERE username='" + username + "' AND gtoken='" + gToken + "';" ;
		//}
		ResultSet rs = dbUtils.queryDB(con, sqlQuery) ;
		try {
			if (rs != null && rs.next()) {
				return rs.getLong("userid") ;
			}
			else return -1 ;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1 ;
		}
	}
	
	//Table SEGMENT
	
	/**
	 * Gets the ID for the last segment instance in the database
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @return			The unique ID for the segment instance
	 */
	public long getLastSegmentId(DBUtils dbUtils, Connection con) {
		String sqlQuery = "SELECT MAX(segmentid) AS segmentid FROM public.segment;" ;
		ResultSet rs = dbUtils.queryDB(con, sqlQuery) ;
		try {
			if (rs != null && rs.next()) {
				return rs.getLong("segmentid") ;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1 ;
	}
	
	/**
	 * Gets the total number of segment instances in the database
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @return 			The total number of segment instances in the databasel
	 */
	public int getSegmentsCount(DBUtils dbUtils, Connection con) {
		String sqlQuery = "SELECT count(*) AS count FROM public.segment;" ;
		ResultSet rs = dbUtils.queryDB(con, sqlQuery) ;
		try {
			if (rs != null && rs.next()) {
				return rs.getInt("count") ;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1 ;
	}
	
	/**
	 * Creates a new segment instance in the database
	 * @param dbUtils			The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con				The {@link java.sql.Connection} to the main database
	 * @param transport			The type of transport used in the segment
	 * @param startDateTime		The start date and time for the segment
	 * @param endDateTime		The end date and time for the segment
	 * @param distance			The distance of the segment (in Kms)
	 * @param startStation		The name of the start station for the segment, in case of public transportation
	 * @param endStation		The name of the end station for the segment, in case of public transportation
	 * @param carrierName		The name of the transport provider, in case of public transportation
	 * @param number			The number of the transport, in case of public transportation
	 * @param direction			The direction of the segment
	 * @param routeId			The unique ID for the route
	 * @param segmentNumber		The sequence number of the segment in the overall route
	 * @return					-1 or 0 if the segment creation went wrong, >0 if the transaction was successful
	 */
	public int createSegment(DBUtils dbUtils, Connection con, String transport, String startDateTime, String endDateTime, double distance, String startStation, String endStation, String carrierName, String number, String direction, long routeId, int segmentNumber, int endStationId, int startStationId, String carrierId) {
		Object[] dateTime = dbUtils.readStringToDateTime(startDateTime) ;
		Date startDate = (Date) dateTime[0] ;
		Time startTime = (Time) dateTime[1] ;
		dateTime = dbUtils.readStringToDateTime(endDateTime) ;
		Date endDate = (Date) dateTime[0] ;
		Time endTime = (Time) dateTime[1] ;
		String sqlInsert = "INSERT INTO public.segment (transport, starttime, endtime, distance, startstation, startstationid, endstation,endstationid, carriername, carriernumber, carrierid, direction, routeid, segmentnumber,startdate,enddate) VALUES ('" + transport + "','" + startTime + "','" + endTime + "'," + distance + ",'" + startStation + "'," + startStationId + ",'" + endStation + "'," + endStationId + ",'" + carrierName + "','" + number + "','" + carrierId + "','" + direction + "'," + routeId + "," + segmentNumber + ",'" + startDate + "','" + endDate + "');" ;
		return dbUtils.modifyDB(con, sqlInsert) ;
	}
	
	/**
	 * Creates a new segment instance in the database
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @param segment	The {@link org.uninova.mobis.pojos.MobisSegment} object containing all the information about the segment
	 * @param routeId	The unique ID for the route
	 * @return			-1 or 0 if the segment creation went wrong, >0 if the transaction was successful
	 */
	public int createSegment(DBUtils dbUtils, Connection con, MobisSegment segment, long routeId) {
		Object[] dateTime = dbUtils.readStringToDateTime(segment.getStartTime()) ;
		Date startDate = (Date) dateTime[0] ;
		Time startTime = (Time) dateTime[1] ;
		dateTime = dbUtils.readStringToDateTime(segment.getEndStation()) ;
		Date endDate = (Date) dateTime[0] ;
		Time endTime = (Time) dateTime[1] ;
		String sqlInsert = "INSERT INTO public.segment (transport, starttime, endtime, distance, startstation, startstationid, endstation, endstationid, carriername, carriernumber, carrierid, direction, routeid, segmentnumber,startdate,enddate) VALUES ('" + segment.getTransport() + "','" + startTime + "','" + endTime + "'," + segment.getDistance() + ",'" + segment.getStartStation() + "'," + segment.getStartStationId() + ",'" + segment.getEndStation() + "'," + segment.getEndStationId() + ",'" + segment.getCarrierName() + "','" + segment.getCarrierNumber() + "','" + segment.getCarrierId() + "','" + segment.getDirection() + "'," + routeId + "," + segment.getSegmentNumber() + ",'" + startDate + "','" + endDate + "');" ;
		return dbUtils.modifyDB(con, sqlInsert) ;
	}
	
	/**
	 * Gets all segments belonging to the route with the input routeId
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @param routeId	The unique ID for the route
	 * @return			An {@link java.util.ArrayList} object containing all segment instances in the database, belonging to the route
	 */
	public ArrayList<MobisSegment> getRouteSegments(DBUtils dbUtils, Connection con, long routeId) {
		String sqlQuery = "SELECT * FROM public.segment WHERE routeid=" + routeId ;
		ResultSet rs = dbUtils.queryDB(con, sqlQuery) ;
		ArrayList<MobisSegment> segments ;
		MobisSegment segment ;
		if (rs != null) {
			try {
				segments = new ArrayList<>() ;
				while (rs.next()) {
					segment = new MobisSegment() ;
					segment.setSegmentId(rs.getLong("segmentid")) ;
					segment.setTransport(rs.getString("transport")) ; 
					segment.setStartTime(dbUtils.readDateTimeToString(rs.getDate("startdate"), rs.getTime("starttime"))) ;
					segment.setEndTime(dbUtils.readDateTimeToString(rs.getDate("enddate"), rs.getTime("endtime"))) ;
					segment.setDistance(rs.getDouble("distance")) ;
					segment.setStartStation(rs.getString("startstation")) ;
					segment.setStartStationId(rs.getInt("startstationid")) ;
					segment.setEndStation(rs.getString("endstation")) ;
					segment.setEndStationId(rs.getInt("endstationid")) ;
					segment.setCarrierName(rs.getString("carriername")) ;
					segment.setCarrierNumber(rs.getString("carriernumber")) ;
					segment.setCarrierId(rs.getString("carrierid"));
					segment.setDirection(rs.getString("direction")) ;
					segment.setSegmentNumber(rs.getInt("segmentnumber")) ;
					segment.setNodes(this.getSegmentNodes(dbUtils, con, segment.getSegmentId())) ;
					segments.add(segment) ;
				}
				return segments ;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null ;
	}
	
	//Table NODE
	
	/**
	 * Gets the ID for the last node inserted in the database
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @return			The ID of the last node instance inserted in the database
	 */
	public long getLastNodeId(DBUtils dbUtils, Connection con) {
		String sqlQuery = "SELECT MAX(nodeid) AS nodeid FROM public.node;" ;
		ResultSet rs = dbUtils.queryDB(con, sqlQuery) ;
		try {
			if (rs != null && rs.next()) {
				return rs.getLong("nodeid") ;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1 ;
	}
	
	/**
	 * Gets the total number of node instances in the database
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @return			The total number of node instances in the database
	 */
	public int getNodesCount(DBUtils dbUtils, Connection con) {
		String sqlQuery = "SELECT count(*) AS count FROM public.node;" ;
		ResultSet rs = dbUtils.queryDB(con, sqlQuery) ;
		try {
			if (rs != null && rs.next()) {
				return rs.getInt("count") ;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1 ;
	}
	
	/**
	 * Creates a new node instance in the database
	 * @param dbUtils		The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con			The {@link java.sql.Connection} to the main database
	 * @param isStart		true if the node is the start node for the route, false otherwise
	 * @param isFinish		true if the node is the finish node for the route, false otherwise
	 * @param isWaypoint	true if the node is a waypoint node for the route, false otherwise
	 * @param lat			The latitude value for the node
	 * @param lng			The longitude value for the node
	 * @param nodeNumber	The sequence number for the node in the overall segment
	 * @param osmId			The unique OpenStreetMaps ID for the node
	 * @param segmentId		The unique Id for the segment
	 * @return				-1 or 0 if the node creation went wrong, >0 if the transaction was successful
	 */
	public int createNode(DBUtils dbUtils, Connection con, int isStart, int isFinish, int isWaypoint, double lat, double lng, int nodeNumber, String osmId, long segmentId) {
		String sqlInsert = "INSERT INTO public.node (isstart, isfinish, iswaypoint, lat, lng, nodenumber, osmnodeid, segmentid) VALUES (" + isStart + "," + isFinish + "," + isWaypoint + "," + lat + "," + lng + "," + nodeNumber + ",'" + osmId + "'," + segmentId + ");" ;
		return dbUtils.modifyDB(con, sqlInsert) ;
	}
	
	/**
	 * Creates a new node instance in the database
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @param node		The {@link org.uninova.mobis.pojos.MobisNode} object containing all the information about the node
	 * @param segmentId	The unique ID for the segment
	 * @return			-1 or 0 if the node creation went wrong, >0 if the transaction was successful
	 */
	public int createNode(DBUtils dbUtils, Connection con, MobisNode node, long segmentId) {
		String sqlInsert = "INSERT INTO public.node (isstart, isfinish, iswaypoint, lat, lng, nodenumber, osmnodeid, segmentid) VALUES (" + (node.isStart() ? 1 : 0) + "," + (node.isFinish() ? 1 : 0) + "," + (node.isWaypoint() ? 1 : 0) + "," + node.getLat() + "," + node.getLng() + "," + node.getNodeNumber() + ",'" + node.getOsmNodeId() + "'," + segmentId + ");" ;
		return dbUtils.modifyDB(con, sqlInsert) ;
	}
	
	/**
	 * Gets all nodes for the segment with the input segmentId
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @param segmentId	The unique ID for the segment
	 * @return			An {@link java.util.ArrayList} object containing all node instances for the segment
	 */
	public ArrayList<MobisNode> getSegmentNodes(DBUtils dbUtils, Connection con, long segmentId) {
		String sqlQuery = "SELECT * FROM public.node WHERE segmentid=" + segmentId ;
		ArrayList<MobisNode> nodes ;
		MobisNode node ;
		ResultSet rs = dbUtils.queryDB(con, sqlQuery) ;
		if (rs != null) {
			nodes = new ArrayList<>() ;
			try {
				while (rs.next()) {
					node = new MobisNode() ;
					node.setStart(rs.getBoolean("isstart")) ;
					node.setFinish(rs.getBoolean("isfinish")) ;
					node.setWaypoint(rs.getBoolean("iswaypoint")) ;
					node.setLat(rs.getDouble("lat")) ;
					node.setLng(rs.getDouble("lng")) ;
					node.setNodeNumber(rs.getInt("nodenumber")) ;
					node.setOsmNodeId(rs.getString("osmnodeid")) ;
					node.setNodeId(rs.getLong("nodeid")) ;
					node.setInstruction(this.getNodeInstruction(dbUtils, con, node.getNodeId())) ;
					node.setPlaces(this.getNodePlaces(dbUtils, con, node.getNodeId())) ;
					nodes.add(node) ;
				}
				return nodes ;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null ;
	}
	
	//Table PLACE
	
	/**
	 * Gets the ID for the last place instance inserted in the database
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @return			The unique ID for the last place instance inserted in the database
	 */
	public long getLastPlaceId(DBUtils dbUtils, Connection con) {
		String sqlQuery = "SELECT MAX(idplace) AS placeid FROM public.place;" ;
		ResultSet rs = dbUtils.queryDB(con, sqlQuery) ;
		try {
			if (rs != null && rs.next()) {
				return rs.getLong("placeid") ;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1 ;
	}
	
	/**
	 * Gets the total number of place instances in the database
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @return			The total number of place instances in the database
	 */
	public int getPlacesCount(DBUtils dbUtils, Connection con) {
		String sqlQuery = "SELECT count(*) AS count FROM public.place;" ;
		ResultSet rs = dbUtils.queryDB(con, sqlQuery) ;
		try {
			if (rs != null && rs.next()) {
				return rs.getInt("count") ;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1 ;
	}
	
	/**
	 * Creates a new place instance in the database
	 * @param dbUtils		The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con			The {@link java.sql.Connection} to the main database
	 * @param provider		The name of the venue provider (e.g. Google Places, Foursquare)
	 * @param providerId	The unique ID given by the provider for the venue
	 * @param nodeId		The unique ID for the node
	 * @return				-1 or 0 if the place creation went wrong, >0 if the transaction was successful
	 */
	public int createPlace(DBUtils dbUtils, Connection con, String provider, String providerId, long nodeId) {
		String sqlInsert = "INSERT INTO public.place (provider,providerid,idnode) VALUES ('" + provider + "','" + providerId + "'," + nodeId + ");" ;
		return dbUtils.modifyDB(con, sqlInsert) ;
	}
	
	/**
	 * Fetches all place instances from the node with the input nodeId
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @param nodeId	The unique ID for the node
	 * @return			-1 or 0 if the place creation went wrong, >0 if the transaction was successful
	 */
	public ArrayList<MobisPlace> getNodePlaces(DBUtils dbUtils, Connection con, long nodeId) {
		String sqlQuery = "SELECT * FROM public.place WHERE idnode=" + nodeId ;
		ResultSet rs = dbUtils.queryDB(con, sqlQuery) ;
		ArrayList<MobisPlace> places ;
		MobisPlace place ;
		if (rs != null) {
			places = new ArrayList<>() ;
			try {
				while (rs.next()) {
					place = new MobisPlace() ;
					place.setProvider(rs.getString("provider")) ;
					place.setProviderId(rs.getString("providerid")) ;
					place.setPlaceId(rs.getLong("idplace")) ;
					places.add(place) ;
				}
				return places ;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null ;
	}
	
	
	// Table INSTRUCTION
	
	/**
	 * Gets the ID for the last instruction instance inserted in the database
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @return			The unique ID for the last instruction instance inserted in the database
	 */
	public long getLastInstructionId(DBUtils dbUtils, Connection con) {
		String sqlQuery = "SELECT MAX(idinstruction) AS instructionid FROM public.instruction;" ;
		ResultSet rs = dbUtils.queryDB(con, sqlQuery) ;
		try {
			if (rs != null && rs.next()) {
				return rs.getLong("instructionid") ;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1 ;
	}
	
	/**
	 * Gets the total number of instruction instances in the database
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @return			The total number of instruction instances in the database
	 */
	public int getInstructionsCount(DBUtils dbUtils, Connection con) {
		String sqlQuery = "SELECT count(*) AS count FROM public.instruction;" ;
		ResultSet rs = dbUtils.queryDB(con, sqlQuery) ;
		try {
			if (rs != null && rs.next()) {
				return rs.getInt("count") ;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1 ;
	}
	
	/**
	 * Creates a new instruction instance in the database
	 * @param dbUtils		The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con			The {@link java.sql.Connection} to the main database
	 * @param distance		The distance to travel
	 * @param duration		The travel duration
	 * @param totalDistance	The total distance travelled
	 * @param totalDuration	The total duration travelled
	 * @param pointType		The topological type of the point (e.g. junction, roundabout)
	 * @param turn			The degree value to turn (e.g. 0 to go straight ahead)
	 * @param bearing		The bearing value
	 * @param highway		The name of the road
	 * @param nodeId		The unique ID for the node
	 * @return				-1 or 0 if the instruction creation went wrong, >0 if the transaction was successful
	 */
	public int createInstruction(DBUtils dbUtils, Connection con, double distance, double duration, double totalDistance, double totalDuration, String pointType, String turn, String bearing, String highway, long nodeId) {
		String sqlInsert = "INSERT INTO public.instruction (distance, duration, totaldistance, totalduration, pointtype, turn, bearing, highway, idnode) VALUES (" + distance + "," + duration + "," + totalDistance + "," + totalDuration + ",'" + pointType + "','" + turn + "','" + bearing + "','" + highway + "'," + nodeId + ");" ;
		return dbUtils.modifyDB(con, sqlInsert) ;
	}
	
	/**
	 * Gets the instruction instance for the node with the input nodeId
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @param nodeId	The unique ID for the node
	 * @return 			A {@link org.uninova.mobis.pojos.MobisInstruction} object containing all the information about the instruction
	 */
	public MobisInstruction getNodeInstruction(DBUtils dbUtils, Connection con, long nodeId) {
		String sqlQuery = "SELECT * FROM public.instruction WHERE idnode=" + nodeId ;
		ResultSet rs = dbUtils.queryDB(con, sqlQuery) ;
		MobisInstruction instruction ;
		
		if (rs != null) {
			try {
				if (rs.next()) {
					instruction = new MobisInstruction() ;
					instruction.setDistance(rs.getDouble("distance")) ;
					instruction.setDuration(rs.getDouble("duration")) ;
					instruction.setTotalDistance(rs.getDouble("totaldistance")) ;
					instruction.setTotalDuration(rs.getDouble("totalduration")) ;
					instruction.setPointType(rs.getString("pointtype")) ;
					instruction.setTurn(rs.getString("turn")) ;
					instruction.setBearing(rs.getString("bearing")) ;
					instruction.setHighway(rs.getString("highway")) ;
					instruction.setInstructionId(rs.getLong("idinstruction")) ;
					return instruction ;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null ;
	}
	
	public long createProfile(DBUtils dbUtils, Connection con, long userId, MobisProfile profile) {
		String sqlInsert = "INSERT INTO public.profile "
				+ "("
				+ "gps_device,"
				+ "gps_service,"
				+ "trip_planner,"
				+ "paper_map,"
				+ "other_trip_assistant,"
				+ "owns_car,"
				+ "owns_motorbike,"
				+ "owns_bike,"
				+ "handicapped,"
				+ "disable_car,"
				+ "disable_motorbike,"
				+ "disable_bike,"
				+ "disable_train,"
				+ "disable_bus,"
				+ "disable_taxi,"
				+ "disable_subway,"
				+ "disable_walk,"
				+ "car_frequency,"
				+ "motorbike_frequency,"
				+ "bike_frequency,"
				+ "train_frequency,"
				+ "bus_frequency,"
				+ "taxi_frequency,"
				+ "subway_frequency,"
				+ "walk_frequency,"
				+ "stress_level,"
				+ "traffic_disturbance_level,"
				+ "userid"
				+ ") VALUES ("
				+ (profile.isGpsDevice() ? "1," : "0,")
				+ (profile.isGpsService() ? "1," : "0,")
				+ (profile.isTripPlanner() ? "1," : "0,")
				+ (profile.isPaperMap() ? "1," : "0,")
				+ ((profile.getOtherAssistant() != null && !profile.getOtherAssistant().equals("")) ? "'" + profile.getOtherAssistant() + "'," : "'',")
				+ (profile.isOwnsCar() ? "1," : "0,")
				+ (profile.isOwnsMotorbike() ? "1," : "0,")
				+ (profile.isOwnsBike() ? "1," : "0,")
				+ (profile.isHandicapped() ? "1," : "0,")
				+ (profile.isDisableCar() ? "1," : "0,")
				+ (profile.isDisableMotorbike() ? "1," : "0,")
				+ (profile.isDisableBike() ? "1," : "0,")
				+ (profile.isDisableTrain() ? "1," : "0,")
				+ (profile.isDisableBus() ? "1," : "0,")
				+ (profile.isDisableTaxi() ? "1," : "0,")
				+ (profile.isDisableSubway() ? "1," : "0,")
				+ (profile.isDisableWalk() ? "1," : "0,")
				+ (profile.getCarFrequency() + ",")
				+ (profile.getMotorbikeFrequency() + ",")
				+ (profile.getBikeFrequency() + ",")
				+ (profile.getTrainFrequency() + ",")
				+ (profile.getBusFrequency() + ",")
				+ (profile.getTaxiFrequency() + ",")
				+ (profile.getSubwayFrequency() + ",")
				+ (profile.getWalkFrequency() + ",")
				+ (profile.getStressLevel() + ",")
				+ (profile.getTrafficDisturbanceLevel() + ",")
				+ (userId)
				+ ") RETURNING profileid;" ;
		
		ResultSet rs = dbUtils.queryDB(con, sqlInsert) ;
		try {
			if (rs != null && rs.next()) {
				return rs.getLong("profileid") ;
			}
			else return -1 ;
		} catch (SQLException e) {
			return -1 ;
		}
	}
	
	public int createVehicle(DBUtils dbUtils, Connection con, long profileId, MobisVehicle vehicle) {
		String sqlInsert = "INSERT INTO public.user_vehicle "
				+ "("
				+ "vehicle_type,"
				+ "car_type,"
				+ "vehicle_engine,"
				+ "fuel_type,"
				+ "profileid"
				+ ") VALUES ("
				+ ((vehicle.getVehicleType() != null && !vehicle.getVehicleType().equals("")) ? "'" + vehicle.getVehicleType() + "'," : "'',")
				+ (vehicle.getCarType() + ",")
				+ (vehicle.getVehicleEngine() + ",")
				+ (vehicle.getFuelType() + ",")
				+ (profileId)
				+ ");" ;
		
		return dbUtils.modifyDB(con, sqlInsert) ;
	}
	
	public MobisProfile insertProfileQuestion(DBUtils dbUtils, Connection con, long userId, int questionId, MobisProfile profile) {
		String sqlQuery = "" ;
		MobisVehicle vehicle = null ;
		ArrayList<MobisVehicle> vehicles ;
		boolean profileExists ;
		try {
			profileExists = this.userProfileExists(dbUtils, con, userId) ; 
			switch (questionId) {
				case 1:
					if (!profileExists) {
						sqlQuery = "INSERT INTO public.profile "
								+ "(" 
							+ "gps_device,"
							+ "gps_service,"
							+ "trip_planner,"
							+ "paper_map,"
							+ "other_trip_assistant,"
							+ "userid"
							+ ") VALUES ("
							+ profile.isGpsDevice() + ","
							+ profile.isGpsService() + "," 
							+ profile.isTripPlanner() + ","
							+ profile.isPaperMap() + ","
							+ ((profile.getOtherAssistant() != null && !profile.getOtherAssistant().isEmpty()) ? ("'" + profile.getOtherAssistant() + "'") : ("''")) 
							+ ");";
						profile.setProfileId(this.getUserProfileId(dbUtils, con, userId)) ;
					}
					else {
						sqlQuery = "UPDATE public.profile SET ("
								+ "gps_device,"
								+ "gps_service,"
								+ "trip_planner,"
								+ "paper_map,"
								+ "other_trip_assistant"
								+ ") = ("
								+ profile.isGpsDevice() + ","
								+ profile.isGpsService() + ","
								+ profile.isTripPlanner() + ","
								+ profile.isPaperMap() + ","
								+ ((profile.getOtherAssistant() != null && !profile.getOtherAssistant().isEmpty()) ? ("'" + profile.getOtherAssistant() + "'") : ("''"))
								+ ") WHERE userid=" + userId + ";" ;
					}
					
					break ;
				case 2: 
					sqlQuery = "UPDATE public.profile SET ("
							+ "owns_car,"
							+ "owns_motorbike,"
							+ "owns_bike"
							+ ") = ("
							+ profile.isOwnsCar() + ","
							+ profile.isOwnsMotorbike() + ","
							+ profile.isOwnsBike()
							+ ") WHERE userid=" + userId + ";" ;
					break ;
				case 3: 
					sqlQuery = "UPDATE public.profile SET ("
							+ "handicapped,"
							+ "disable_car,"
							+ "disable_motorbike,"
							+ "disable_bike,"
							+ "disable_train,"
							+ "disable_bus,"
							+ "disable_taxi,"
							+ "disable_subway,"
							+ "disable_walk"
							+ ") = ("
							+ profile.isHandicapped() + ","
							+ profile.isDisableCar() + ","
							+ profile.isDisableMotorbike() + ","
							+ profile.isDisableBike() + ","
							+ profile.isDisableTrain() + ","
							+ profile.isDisableBus() + ","
							+ profile.isDisableTaxi() + ","
							+ profile.isDisableSubway() + ","
							+ profile.isDisableWalk()
							+ ") WHERE userid=" + userId + ";" ;
					break ;
				case 4: 
					sqlQuery = "UPDATE public.profile SET ("
							+ "car_frequency,"
							+ "motorbike_frequency,"
							+ "bike_frequency,"
							+ "train_frequency,"
							+ "bus_frequency,"
							+ "taxi_frequency,"
							+ "subway_frequency,"
							+ "walk_frequency"
							+ ") = ("
							+ profile.getCarFrequency() + ","
							+ profile.getMotorbikeFrequency() + ","
							+ profile.getBikeFrequency() + ","
							+ profile.getTrainFrequency() + ","
							+ profile.getBusFrequency() + ","
							+ profile.getTaxiFrequency() + ","
							+ profile.getSubwayFrequency() + ","
							+ profile.getWalkFrequency()
							+ ") WHERE userid=" + userId + ";" ;
					break ;
				case 5: 
					sqlQuery = "UPDATE public.profile SET ("
							+ "stress_level"
							+ "="
							+ profile.getStressLevel()
							+ ") WHERE userid=" + userId + ";" ;
					break ;
				case 6: 
					sqlQuery = "UPDATE public.profile SET ("
							+ "traffic_disturbance_level"
							+ "="
							+ profile.getTrafficDisturbanceLevel()
							+ ") WHERE userid=" + userId + ";" ;
					break ;
				case 7: 
					vehicles = profile.getVehicles() ;
					for (MobisVehicle _vehicle : vehicles) {
						if (_vehicle.getVehicleType().equals(StringConstants.CAR_DISPLAY_TYPE)) {
							vehicle = _vehicle ;
						}
					}
					sqlQuery = "INSERT INTO public.user_vehicle (vehicle_type,car_type,profileid) VALUES (" + vehicle.getVehicleType() + "," + vehicle.getCarType() + "," + this.getUserProfileId(dbUtils, con, userId) + ");" ;
					break ;
				case 8: 
					vehicles = profile.getVehicles() ;
					for (MobisVehicle _vehicle : vehicles) {
						if (_vehicle.getVehicleType().equals(StringConstants.CAR_DISPLAY_TYPE)) {
							vehicle = _vehicle ;
						}
					}
					sqlQuery = "UPDATE public.user_vehicle SET (vehicle_engine=" + vehicle.getVehicleEngine() + ") WHERE profileid=" + this.getUserProfileId(dbUtils, con, userId) + ";" ;
					break ;
				case 9: 
					vehicles = profile.getVehicles() ;
					for (MobisVehicle _vehicle : vehicles) {
						if (_vehicle.getVehicleType().equals(StringConstants.CAR_DISPLAY_TYPE)) {
							vehicle = _vehicle ;
						}
					}
					sqlQuery = "UPDATE public.user_vehicle SET (fuel_type=" + vehicle.getFuelType() + ") WHERE profileid=" + this.getUserProfileId(dbUtils, con, userId) + ";" ;
					break ;
				case 10: 
					vehicles = profile.getVehicles() ;
					for (MobisVehicle _vehicle : vehicles) {
						if (_vehicle.getVehicleType().equals(StringConstants.MOTORBYKE_DISPLAY_TYPE)) {
							vehicle = _vehicle ;
						}
					}
					sqlQuery = "INSERT INTO public.user_vehicle (vehicle_type,vehicle_engine,profileid) VALUES (" + vehicle.getVehicleType() + "," + vehicle.getVehicleEngine() + "," + this.getUserProfileId(dbUtils, con, userId) + ");" ;
 					break ;
				case 11: 
					vehicles = profile.getVehicles() ;
					for (MobisVehicle _vehicle : vehicles) {
						if (_vehicle.getVehicleType().equals(StringConstants.MOTORBYKE_DISPLAY_TYPE)) {
							vehicle = _vehicle ;
						}
					}
					sqlQuery = "UPDATE public.user_vehicle SET (fuel_type=" + vehicle.getFuelType() + ") WHERE profileid=" + this.getUserProfileId(dbUtils, con, userId) + ";" ;
					break ;
			}
			if (sqlQuery != null && ! sqlQuery.equals("")) {
				dbUtils.modifyDB(con, sqlQuery) ;
				return profile ;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null ;	
	}
	
	public boolean userProfileExists(DBUtils dbUtils, Connection con, long userId) throws SQLException {
		String sqlQuery = "SELECT * FROM public.profile WHERE userid=" + userId + ";" ;
		ResultSet rs = dbUtils.queryDB(con, sqlQuery) ;
		if (rs != null && rs.next()) {
			return true ;
		}
		else return false ;
	}
	
	public long getLastInsertedProfileId(DBUtils dbUtils, Connection con) throws SQLException {
		String sqlQuery = "SELECT MAX(profileid) AS profileid FROM public.profile;" ;
		ResultSet rs = dbUtils.queryDB(con, sqlQuery) ;
		if (rs != null && rs.next()) {
			return rs.getLong("profileid") ;
		}
		else return -1 ;
	}
	
	public long getUserProfileId(DBUtils dbUtils, Connection con, long userId) throws SQLException {
		String sqlQuery = "SELECT profileid FROM public.profile WHERE userid=" + userId + ";" ;
		ResultSet rs = dbUtils.queryDB(con, sqlQuery) ;
		if (rs != null && rs.next()) {
			return rs.getLong("profileid") ;
		}
		else return -1 ;
	}
	
	
	public boolean createSensorLog(DBUtils dbUtils, Connection con, long routeLogId, MobisSensorLog log) {
		String sqlInsert = "INSERT INTO public.sensor_log (timestamp, route_logid, log_type, record) VALUES (?,?,?,?);" ;
		try {
			PreparedStatement stmt = con.prepareStatement(sqlInsert) ;
			
			Timestamp stamp = new Timestamp(Long.parseLong(log.getTimestamp())) ;
			
			stmt.setTimestamp(1, stamp) ;
			stmt.setLong(2, routeLogId) ;
			stmt.setString(3, log.getLogType()) ;
			stmt.setString(4, log.getRecord()) ;
			
			return stmt.execute() ;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false ;
	}
	
	public boolean createRouteLog(DBUtils dbUtils, Connection con, long userId, String startTimestamp) {
		String sqlInsert = "INSERT INTO public.route_log (start_timestamp, userid) VALUES (?,?);" ;
		try {
			PreparedStatement stmt = con.prepareStatement(sqlInsert) ;
			
			Timestamp stamp = new Timestamp(Long.parseLong(startTimestamp)) ;
			
			stmt.setTimestamp(1, stamp) ;
			stmt.setLong(2, userId) ;
			
			return stmt.execute() ;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false ;
	}
	
	public int updateRouteLog(DBUtils dbUtils, Connection con, String endTimestamp, long routeLogId) {
		String sqlInsert = "UPDATE public.route_log SET end_timestamp=? WHERE route_logid=?" ;
		try {
			PreparedStatement stmt = con.prepareStatement(sqlInsert) ;
			
			Timestamp stamp = new Timestamp(Long.parseLong(endTimestamp)) ;
			
			stmt.setTimestamp(1, stamp) ;
			stmt.setLong(2, routeLogId) ;
			
			return stmt.executeUpdate() ;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1 ;
	}
	
	public long getLastRouteLogId(DBUtils dbUtils, Connection con) {
		String sqlQuery = "SELECT MAX(route_logid) AS route_logid FROM public.route_log;" ;
		ResultSet rs = dbUtils.queryDB(con, sqlQuery) ;
		try {
			if (rs != null && rs.next()) {
				return rs.getLong("route_logid") ;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1 ;
	}
}
