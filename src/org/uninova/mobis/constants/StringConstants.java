package org.uninova.mobis.constants;

/**
 * Interface StringConstants
 * @author PAF@UNINOVA
 */
public interface StringConstants {
	
	// TRANSPORT TYPES
	public static final String AIR_DISPLAY_TYPE = "A" ;
	public static final String BUS_DISPLAY_TYPE = "B" ;
	public static final String CAR_DISPLAY_TYPE = "C" ;
	public static final String AIRPORT_BUS_DISPLAY_TYPE = "D" ;
	public static final String AIRPORT_TAXI_DISPLAY_TYPE = "E" ;
	public static final String FERRY_DISPLAY_TYPE = "F" ;
	public static final String WALKING_DISPLAY_TYPE = "G" ;
	public static final String TRAIN_DISPLAY_TYPE = "J" ;
	public static final String DOCK_DISPLAY_TYPE = "K" ;
	public static final String STREETCAR_DISPLAY_TYPE = "S" ;
	public static final String TAXI_DISPLAY_TYPE = "T" ;
	public static final String UNDERGROUND_DISPLAY_TYPE = "U" ;
	public static final String WALK_LONG_DISTANCE_DISPLAY_TYPE = "GL" ;
	public static final String BYCICLE_DISPLAY_TYPE = "Y" ;
	public static final String MOTORBYKE_DISPLAY_TYPE = "M" ;
	
	// CONNECTIONS
	public static final String JDBC_POSTGRES = "jdbc:postgresql" ;
	public static final String JDBC_MYSQL = "jdbc:mysql" ;
	
	// DATABASES
	public static final String SWEDEN_GRAPH = "sweden_graph" ;
	public static final String SLOVENIA_GRAPH = "slovenia_graph" ;
	public static final String GREECE_GRAPH = "greece_graph" ;
	public static final String MOBIS_TRAFFIC = "mobis_traffic" ;
	public static final String MOBIS_MAIN = "mobis_main" ;
	public static final String MOBIS_GTFS = "mobis_gtfs" ;
	
	// API URL's
	public static final String OSM_API_URL = "http://api.openstreetmap.org/api/0.6/" ; 
	public static final String NOMINATIM_URL = "http://nominatim.openstreetmap.org/" ;
	public static final String RESROBOT_URL = "https://api.trafiklab.se/samtrafiken/resrobot/" ;
	public static final String RESROBOT_GET_DIRECTIONS_URL = "https://api.trafiklab.se/samtrafiken/resrobotstops/GetDepartures.json?" ;
	public static final String FOURSQUARE_URL = "https://api.foursquare.com/v2/venues/search?" ;
	public static final String WORLD_WEATHER_URL = "http://api.worldweatheronline.com/free/v1/weather.ashx?" ;
	public static final String GOOGLE_STATIC_MAPS_URL = "http://maps.googleapis.com/maps/api/staticmap?" ;
	
	// RESROBOT PARAMETERS
	public static final String RESROBOT_API_VERSION = "2.1" ;
	public static final String RESROBOT_KEY = "a112ea56d55f3cd838afb60226b1c1ad" ;
	public static final String COORD_SYSTEM = "WGS84" ;
	public static final String RESROBOT_GET_DEPARTURES_KEY = "ce75244c468522ec32a890019b30abaf" ;
	public static final String RESROBOT_GET_DEPARTURES_API_VERSION = "2.2" ;
	
	
	// ROUTE CRITERIA
	public static final String SHORTEST_CRITERIA = "shortest" ;
	public static final String QUICKEST_CRITERIA = "quickest" ;
	
	// ROUTE ROAD RESTRICTIONS
	public static final String CAR_RESTRICTIONS = " WHERE class_id NOT IN (112,113,114,115,116,117,118,119,120,121,122,501,502)" ;
	public static final String BIKE_RESTRICTIONS = " WHERE class_id NOT IN (101,102,103,106,107,116,122,114,119,501,502)" ;
	public static final String WALK_RESTRICTIONS = " WHERE class_id NOT IN (101,102,103,501,502)" ;
	public static final String TRAIN_RESTRICTIONS = " WHERE class_id IN (501)" ;
	public static final String METRO_RESTRICTIONS = " WHERE class_id IN (502)" ;
	public static final String BUS_RESTRICTIONS = " WHERE class_id NOT IN (112,113,114,115,117,118,119,120,121,122,501,502)" ;
	
	// ROUTE SQL STATEMENTS
	public static final String DIJKSTRA_KSP_SQL_SELECT = "SELECT gid AS id," +
							                         "source," +
							                         "target," ;
	
	public static final String ASTAR_SQL_SELECT = "SELECT gid AS id," +
									            "source," +
									            "target," +
									            "x1, y1, x2, y2," ;
	
	public static final String FROM_EDGE_TABLE = " FROM ways" ;
	
	public static final String EDGE_TABLE = "ways" ;
	
	public static final String EPSG = "4326" ;
	
	// ROUTE COST FUNCTIONS
	public static final String SHORTEST_COST = "length AS cost" ;
	public static final String QUICKEST_CAR_COST = "(length/maxspeed_forward) AS cost" ;
	public static final String QUICKEST_BIKE_COST = "(length/24) AS cost" ; 	// 24 Km/h is the average bike speed								           
	public static final String QUICKEST_WALK_COST = "(length/5) AS cost" ; 		// 5 Km/h is the average human walking speed
	
	// FOURSQUARE CREDENTIALS
	public static final String FOURSQUARE_CLIENT_ID = "QWVTQKJJRGWKWT3RNYHQWSNQ3B5OIBZCOQBMIHUV34MOVM5V" ;
	public static final String FOURSQUARE_CLIENT_SECRET = "HNOTE5L5OM1H3NNVRZ5LI5MGQYOX13I3ZKPXMORNP3CCTCV4" ;
	
	// FACTUAL CREDENTIALS
	public static final String FACTUAL_KEY = "Opo9Mi26QHydGhAJxEQV8ojfcICdodK3Qu4YKeBY" ;
	public static final String FACTUAL_SECRET = "tleR5qY4vBmwFOMh0PlTxJCRdsvmr9Z3qUc0GGKN" ;
	
	// WORLD WEATHER ONLINE KEY
	public static final String WORLD_WEATHER_KEY = "vrq5sex5ugevbmphz5m9y722" ;
	
	// GOOGLE CREDENTIALS
	public static final String GOOGLE_API_KEY = "AIzaSyA55PtfVDr5Py8L5NBGPYxowaulbI5sAvY" ;
	
	// ERROR MESSAGES
	public static final String ERROR_LOGIN_VALIDATION = "ERROR: Login validation" ;
	public static final String ERROR_USER_EXISTS = "ERROR: User already exists" ;
	public static final String ERROR_DUPLICATE_USERNAME = "ERROR: Username already exists" ;
	public static final String ERROR_NO_ROUTE_IN_SESSION = "ERROR: No routes in session" ;
	public static final String ERROR_NO_LOGIN = "ERROR: User is not logged in" ;
	public static final String ERROR_BAD_REQUEST_PARAMETER = "ERROR: Bad request parameter - " ;
	
	public static final String ERROR_ORIGIN_COORDINATE_NOT_FOUND = "ERROR: Origin coordinate not found" ;
	public static final String ERROR_DESTINATION_COORDINATE_NOT_FOUND = "ERROR: Destination coordinate not found" ;
	public static final String ERROR_ORIGIN_ADDRESS_NOT_FOUND = "ERROR: Origin address not found" ;
	public static final String ERROR_DESTINATION_ADDRESS_NOT_FOUND = "Destination address not found" ;
	public static final String ERROR_NULL_ROUTE = "ERROR: Null route" ;
	public static final String ERROR_NONE_TRAFFIC_EVENT_FOUND = "ERROR: No traffic events found" ;
	public static final String ERROR_NONE_USER_ROUTE = "ERROR: None route stored for this user" ;
	public static final String ERROR_NONE_VENUE_NEARBY = "ERROR: No nearby venues" ;
	public static final String ERROR_NODE_INSERTION = "ERROR: Route node was not inserted into database" ;
	public static final String ERROR_SEGMENT_INSERTION = "ERROR: Route segment was not inserted into database" ;
	public static final String ERROR_ROUTE_INSERTION = "ERROR: Route was not inserted into database" ;
	public static final String ERROR_PROFILE_INSERTION = "ERROR: Profile was not inserted into database" ;
	public static final String ERROR_CC_REASONING_ENGINE_NOT_FOUND = "ERROR: CC Component cannot access its reasoning engine";
}
