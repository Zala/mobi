package org.uninova.mobis.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

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

public interface MobisMainDBConnector {

	// Table ROUTE
	
	/**
	 * Gets the ID for the last saved route
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @return			The ID for the last saved route
	 */
	public long getLastRouteId(DBUtils dbUtils, Connection con)  ;
	
	/**
	 * Gets a {@link org.uninova.mobis.pojos.MobisRoute} object with the given input <code>id</code>
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @param id		The unique ID for the route instance
	 * @return			A {@link org.uninova.mobis.pojos.MobisRoute} object containing the route
	 */
	public MobisRoute getRouteByRouteId(DBUtils dbUtils, Connection con, long id) ;
	
	/**
	 * Gets the number of total routes in the database
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @return			The total number of route instance in the database
	 */
	public int getRoutesCount(DBUtils dbUtils, Connection con) ;
	
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
	 * @return				-1 or 0 if the route creation went wrong, >0 if the transaction was successful
	 */
	public int createRoute(DBUtils dbUtils, Connection con, long userId, String transport, String starttime, String endtime, String criteria, String creationtime, String frequency, int freqNumber, Coordinate startCoord, Coordinate endCoord) ;
	
	/**
	 * Creates a new route instance in the database
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @param route		The {@link org.uninova.mobis.pojos.MobisRoute} object containing information fir the route
	 * @param userId	The unique ID for the user instance
	 * @return			-1 or 0 if the route creation went wrong, >0 if the transaction was successful
	 */
	public int createRoute(DBUtils dbUtils, Connection con, MobisRoute route, long userId) ;
	
	/**
	 * Gets all the routes of a particular user
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @param userId	The unique ID for the user instance
	 * @return			An {@link java.util.ArrayList} object containing all the routes for the user
	 */
	public ArrayList<MobisRoute> getAllRoutesFromUser(DBUtils dbUtils, Connection con, long userId) ;
	
	/**
	 * Gets the routes from a particular user that are planned to a specific date
	 * @param dbUtils			The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con				The {@link java.sql.Connection} to the main database
	 * @param userId			The unique ID for the user instance
	 * @param dateTimeString	The date and time for departure or arrival
	 * @param isStartTime		true if dateTimeString is the start time of the route, false if dateTimeString is the arrival time of the route
	 * @return					An {@link java.util.ArrayList} object containing all the routes for the user starting or finishing at the time given by dateTimeString
	 */
	public ArrayList<MobisRoute> getRouteFromUserByDate(DBUtils dbUtils, Connection con, long userId, String dateTimeString, boolean isStartTime) ;
	
	// Table USER
	
	/**
	 * Get the number of user instances in the database
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @return			The number of user instances in the database
	 */
	public int getUsersCount(DBUtils dbUtils, Connection con) ;
	
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
	public int createUser(DBUtils dbUtils, Connection con, String firstName, String lastName, String username, String password, String email, String token, String country, String gId, String gToken, String gRefreshToken) ;
	
	/**
	 * Fetches an {@link java.util.ArrayList} object containing the usernames for all user instances in the database
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @return			An {@link java.util.ArrayList} object containing all usernames on the database
	 */
	public ArrayList<String> getUsernames(DBUtils dbUtils, Connection con) ;
	
	/**
	 * Fetches an user instance from database with the given username
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @param username	The username for the user
	 * @return			An {@link org.uninova.mobis.pojos.MobisUser} object containing all the information about the user
	 */
	public MobisUser getUserByUsername(DBUtils dbUtils, Connection con, String username) ;
	
	/**
	 * Gets the ID of the user instance with the given token
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @param token		The unique token created by Prophet server to identify a user
	 * @return			The unique ID for the user instance
	 */
	public long getUserIdFromToken(DBUtils dbUtils, Connection con, String token) ;
	
	/**
	 * Gets the user instance identified by the input userId
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @param userId	The unique ID for the user instance
	 * @return			An {@link org.uninova.mobis.pojos.MobisUser} object containing all the information about the user
	 */
	public MobisUser getUserByUserId(DBUtils dbUtils, Connection con, long userId) ;
	
	/**
	 * Gets the user instance identified by the Google's userId
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @param gUserId	The unique Google ID for the user instance
	 * @return			An {@link org.uninova.mobis.pojos.MobisUser} object containing all the information about the user
	 */
	public MobisUser getUserByGoogleUserId(DBUtils dbUtils, Connection con, String gUserId) ;
	
	
	/**
	 * Checks the existence of the input token
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @param token		The unique token created by Prophet server to identify a user
	 * @return			true if the token exists, false otherwise
	 */
	public boolean checkToken(DBUtils dbUtils, Connection con, String token) ;
	
	/**
	 * Checks the existence of the input username
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @param username	The username for the user
	 * @return			true if the username exists, false otherwise
	 */
	public boolean checkUsername(DBUtils dbUtils, Connection con, String username) ;
	
	/**
	 * Checks the validity of the login credentials
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @param username	The username for the user
	 * @param password	The password of the user
	 * @return			-1 if the user doesn't exist, the unique ID for the user instance, if it exists
	 */
	public long checkLoginCredentials(DBUtils dbUtils, Connection con, String username, String password ) ;
	
	//Table SEGMENT
	
	/**
	 * Gets the ID for the last segment instance in the database
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @return			The unique ID for the segment instance
	 */
	public long getLastSegmentId(DBUtils dbUtils, Connection con) ;
	
	/**
	 * Gets the total number of segment instances in the database
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @return 			The total number of segment instances in the databasel
	 */
	public int getSegmentsCount(DBUtils dbUtils, Connection con) ;
	
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
	public int createSegment(DBUtils dbUtils, Connection con, String transport, String startDateTime, String endDateTime, double distance, String startStation, String endStation, String carrierName, String number, String direction, long routeId, int segmentNumber, int endStationId, int startStationId, String carrierId) ;
	
	/**
	 * Creates a new segment instance in the database
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @param segment	The {@link org.uninova.mobis.pojos.MobisSegment} object containing all the information about the segment
	 * @param routeId	The unique ID for the route
	 * @return			-1 or 0 if the segment creation went wrong, >0 if the transaction was successful
	 */
	public int createSegment(DBUtils dbUtils, Connection con, MobisSegment segment, long routeId) ;
	
	/**
	 * Gets all segments belonging to the route with the input routeId
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @param routeId	The unique ID for the route
	 * @return			An {@link java.util.ArrayList} object containing all segment instances in the database, belonging to the route
	 */
	public ArrayList<MobisSegment> getRouteSegments(DBUtils dbUtils, Connection con, long routeId) ;
	
	//Table NODE
	
	/**
	 * Gets the ID for the last node inserted in the database
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @return			The ID of the last node instance inserted in the database
	 */
	public long getLastNodeId(DBUtils dbUtils, Connection con) ;
	
	/**
	 * Gets the total number of node instances in the database
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @return			The total number of node instances in the database
	 */
	public int getNodesCount(DBUtils dbUtils, Connection con) ;
	
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
	public int createNode(DBUtils dbUtils, Connection con, int isStart, int isFinish, int isWaypoint, double lat, double lng, int nodeNumber, String osmId, long segmentId) ;
	
	/**
	 * Creates a new node instance in the database
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @param node		The {@link org.uninova.mobis.pojos.MobisNode} object containing all the information about the node
	 * @param segmentId	The unique ID for the segment
	 * @return			-1 or 0 if the node creation went wrong, >0 if the transaction was successful
	 */
	public int createNode(DBUtils dbUtils, Connection con, MobisNode node, long segmentId) ;
	
	/**
	 * Gets all nodes for the segment with the input segmentId
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @param segmentId	The unique ID for the segment
	 * @return			An {@link java.util.ArrayList} object containing all node instances for the segment
	 */
	public ArrayList<MobisNode> getSegmentNodes(DBUtils dbUtils, Connection con, long segmentId) ;
	
	//Table PLACE

	/**
	 * Gets the ID for the last place instance inserted in the database
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @return			The unique ID for the last place instance inserted in the database
	 */
	public long getLastPlaceId(DBUtils dbUtils, Connection con) ;
	
	/**
	 * Gets the total number of place instances in the database
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @return			The total number of place instances in the database
	 */
	public int getPlacesCount(DBUtils dbUtils, Connection con) ;
	
	/**
	 * Creates a new place instance in the database
	 * @param dbUtils		The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con			The {@link java.sql.Connection} to the main database
	 * @param provider		The name of the venue provider (e.g. Google Places, Foursquare)
	 * @param providerId	The unique ID given by the provider for the venue
	 * @param nodeId		The unique ID for the node
	 * @return				-1 or 0 if the place creation went wrong, >0 if the transaction was successful
	 */
	public int createPlace(DBUtils dbUtils, Connection con, String provider, String providerId, long nodeId) ;
	
	/**
	 * Fetches all place instances from the node with the input nodeId
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @param nodeId	The unique ID for the node
	 * @return			-1 or 0 if the place creation went wrong, >0 if the transaction was successful
	 */
	public ArrayList<MobisPlace> getNodePlaces(DBUtils dbUtils, Connection con, long nodeId) ;
	
	// Table INSTRUCTION

	/**
	 * Gets the ID for the last instruction instance inserted in the database
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @return			The unique ID for the last instruction instance inserted in the database
	 */
	public long getLastInstructionId(DBUtils dbUtils, Connection con) ;
	
	/**
	 * Gets the total number of instruction instances in the database
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @return			The total number of instruction instances in the database
	 */
	public int getInstructionsCount(DBUtils dbUtils, Connection con) ;
	
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
	public int createInstruction(DBUtils dbUtils, Connection con, double distance, double duration, double totalDistance, double totalDuration, String pointType, String turn, String bearing, String highway, long nodeId) ;
	
	/**
	 * Gets the instruction instance for the node with the input nodeId
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the main database
	 * @param nodeId	The unique ID for the node
	 * @return 			A {@link org.uninova.mobis.pojos.MobisInstruction} object containing all the information about the instruction
	 */
	public MobisInstruction getNodeInstruction(DBUtils dbUtils, Connection con, long nodeId) ;
	
	public long createProfile(DBUtils dbUtils, Connection con, long userId, MobisProfile profile) ;
	
	public int createVehicle(DBUtils dbUtils, Connection con, long profileId, MobisVehicle vehicle)  ;
	
	public MobisProfile insertProfileQuestion(DBUtils dbUtils, Connection con, long userId, int questionId, MobisProfile profile) ;
	
	public boolean userProfileExists(DBUtils dbUtils, Connection con, long userId) throws SQLException ;
	
	public long getLastInsertedProfileId(DBUtils dbUtils, Connection con) throws SQLException ;
	
	public long getUserProfileId(DBUtils dbUtils, Connection con, long userId) throws SQLException ;
	
	public boolean createSensorLog(DBUtils dbUtils, Connection con, long routeLogId, MobisSensorLog log) ;
	
	public boolean createRouteLog(DBUtils dbUtils, Connection con, long userId, String startTimestamp) ;
	
	public int updateRouteLog(DBUtils dbUtils, Connection con, String endTimestamp, long routeLogId) ;
	
	public long getLastRouteLogId(DBUtils dbUtils, Connection con) ;
}
