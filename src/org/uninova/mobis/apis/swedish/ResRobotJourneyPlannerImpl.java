package org.uninova.mobis.apis.swedish;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringEscapeUtils;
import org.uninova.mobis.constants.StringConstants;
import org.uninova.mobis.database.GTFSDBConnector;
import org.uninova.mobis.database.GTFSDBConnectorImpl;
import org.uninova.mobis.database.MobisGraphDBConnector;
import org.uninova.mobis.database.MobisGraphDBConnectorImpl;
import org.uninova.mobis.pojos.Coordinate;
import org.uninova.mobis.pojos.MobisNode;
import org.uninova.mobis.pojos.MobisSegment;
import org.uninova.mobis.utils.DBUtils;
import org.uninova.mobis.utils.DBUtilsImpl;
import org.uninova.mobis.utils.GISUtils;
import org.uninova.mobis.utils.GISUtilsImpl;
import org.uninova.mobis.utils.HTTPUtils;
import org.uninova.mobis.utils.HTTPUtilsImpl;

/**
 * ResRobotJourneyPlannerImpl
 * @author PAF
 */
public class ResRobotJourneyPlannerImpl implements ResRobotJourneyPlanner {
	
	/**
	 * ResRobotJourneyPlannerImpl Class Constructor
	 */
	public ResRobotJourneyPlannerImpl() {}
		
	/**
	 * This method is used to find one or Several trips
	 * 		
	 * @param inputType			"coords" - the input is given by the inputs <code>fromCoord</code> and <code>toCoord</code>;
	 * 							"ids" - the input is given by the inputs <code>fromId</code> and <code>toId</code>;
	 * @param from				Name to display on the starting location
	 * @param to				Name to display on end location
	 * @param fromCoord			fromX  - X-coordinate of the starting location.
	 *							fromY  - Y coordinate of the start location.
	 * @param toCoord			toX  - X-coordinate of end location.
	 *							toY  - Y coordinate of end location. 
	 * @param toId				Location id of end location. If this is not Unspecified then it replaces the parameters: "to", "tox" and "Toy".
	 * @param fromId			Location id of the starting location. If this is not Unspecified, then it replaces the parameters: "from", "fromX" and "fromY".
	 * @param journeyDateTime	date  - Date of trip. Eg: 2006-12-31. Default is today's date.
	 *							time  - Time of trip. Eg: 23:39. Default is the next even 10 minutes.
	 * @param isArrivalTime		Set to "true" if <code>time</code> is the wanted arrival time. By default, <code>time</code> is the departure time.
	 * @param searchType		Desired type of search. Not a mandatory parameter. Default is "F" (default search). 
	 * 							1 - "F": Standard search. ResRobot will find the fastest trip using all possible transport modes.
	 * 							2 - "T": Train and local public transport. Express buses are not included in the search.
	 * 							3 - "B": Express bus and local public transport. Regional trains and speed trains are not included in the search.
	 * @param avoidModes		Turn on or off the transport modes. Eg: mode4 = false & mode5 = false
	 * 							Mode1: Speed ​​train. X2000 and the Arlanda Express
	 * 							Mode2: Train (except speed trains)
	 * 							Mode3: Bus (except express buses)
	 * 							Mode4: Boat
	 * 							Mode5: Express bus
	 * 
	 * @return					A plan for a journey in json format
	 */
	public String search(String inputType, Coordinate fromCoord, Coordinate toCoord, String toId, String fromId, String journeyDateTime, boolean isArrivalTime, int searchType, int[] avoidModes) {
		
		HTTPUtils httpUtils = new HTTPUtilsImpl() ;
		String url = StringConstants.RESROBOT_URL + "Search.json?" ;
		String params = "key=" + StringConstants.RESROBOT_KEY + "&apiVersion=" + StringConstants.RESROBOT_API_VERSION + "&coordSys=" + StringConstants.COORD_SYSTEM ;
		
		params += "&from=origin&to=destination" ;
		
		if (inputType.equals("coords")) {
			if (fromCoord != null && toCoord != null && fromCoord.getLat() != 0.0 && fromCoord.getLng() != 0.0 && toCoord.getLat() != 0.0 && toCoord.getLng() != 0.0) {
				params += "&fromX=" + fromCoord.getLng() + "&fromY=" + fromCoord.getLat() + "&toX=" + toCoord.getLng() + "&toY=" + toCoord.getLat() ;
			}
			else {
				System.err.println("ERROR: Invalid origin or destination coordinates. Coordinates must be valid, double WGS84 coordinates.") ;
				return "error_coords" ;
			}
		}
		else if (inputType.equals("ids")) {
			if (fromId != null && toId != null && !fromId.equals("") && !toId.equals("")) {
				params += "&fromId=" + fromId + "&toId=" + toId ;
			}
			else {
				System.err.println("ERROR: Invalid origin or destination station IDs. IDs must be valid strings that match ResRobot's station IDs.") ;
				return "error_ids" ;
			}
		}
		else {
			System.err.println("ERROR: Wrong input type. Value may be 'address', 'coords' and 'ids'.") ;
			return "error_inputType" ;
		}
		
		if (journeyDateTime != null) {
			
			String formattedDate = journeyDateTime.substring(0, journeyDateTime.indexOf(" ")) ;
			String formattedTime = journeyDateTime.substring(journeyDateTime.indexOf(" ") + 1, journeyDateTime.lastIndexOf(":")) ;
			params += "&date=" + formattedDate + "&time=" + formattedTime ;
		}
		
		if (isArrivalTime)
			params += "&arrival=" + isArrivalTime ;
		switch (searchType) {
			case 1: params += "&searchType=F" ; break ;
			case 2: params += "&searchType=T" ; break ;
			case 3: params += "&searchType=B" ; break ;
			default: System.err.println("ERROR: Invalid search type. Valid search type must be an Integer between 1 and 3.") ; return "error_searchType" ;
		}
		
		if (avoidModes != null && avoidModes.length > 0) {
			for (int i = 0; i < avoidModes.length; i++) {
				params += "&mode" + avoidModes[i] + "=false" ;
			}
		}
		
		try {
			String response = httpUtils.requestURLConnection(url + params) ;
			
			return response ;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null ;
	}
	
	/**
	 * This method is used to search for stations in the vicinity of a geographic location.
	 * 
	 * @param center	centerX - X-coordinate of center point.
	 *					centerY - Y coordinate of center point.
	 * @param radius	Radius (unit meters) from the center point stations should be found in
	 * 
	 * @return			A json array with stations nearby
	 */
	public String stationsInZone(Coordinate center, int radius) {
		HTTPUtilsImpl httpUtils = new HTTPUtilsImpl() ;
		String url = StringConstants.RESROBOT_URL + "StationsInZone.json?" ;
		String params = "key=" + StringConstants.RESROBOT_KEY + "&apiVersion=" + StringConstants.RESROBOT_API_VERSION + "&coordSys=" + StringConstants.COORD_SYSTEM ;
		
		if (center != null && radius > 0) {
			params += "&centerX=" + center.getLng() + "&centerY=" + center.getLat() + "&radius=" + radius ;  
			try {
				String response = httpUtils.requestURLConnection(url + params) ;
				return response ;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null ;
		}
		else{
			System.err.println("ERROR: Invalid center or radius.") ;
			return "error_stationsInZone" ;
		}
	}
	
	/**
	 * This request is used to retrieve the start and end date of the current timetable period. It is not possible to search for trips beyond the timetable period,
	 * so the calling system Should check the search parameters before sending requests to search .
	 * 
	 * @return	The result contains the first date of the timetable period, and the last date of the timetable period in json format.
	 */
	public String timeTablePeriod() {
		HTTPUtilsImpl httpUtils = new HTTPUtilsImpl() ;
		String url = StringConstants.RESROBOT_URL + "TimeTablePeriod.json?" ;
		String params = "key=" + StringConstants.RESROBOT_KEY + "&apiVersion=" + StringConstants.RESROBOT_API_VERSION ;
		
		try {
			String response = httpUtils.requestURLConnection(url + params) ;
			return response ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null ;
	}
	
	/**
	 * This request is used to get a list of all the Producers That are used in the system. The producer id is present in the reply from Search in the
	 * <carrier> <id> </ id> </ carrier> elements. It is overpriced gift in the reply from StationsInZone in the <producerlist> element structure.
	 * The list of producers May change, but it is normally not updated more than once thwart per week, so the calling system does not have to call this 
	 * method more than once a day.
	 * 
	 * @return The result contains a list of all the Producers used in the system in json format.
	 */
	public String producerList() {
		HTTPUtilsImpl httpUtils = new HTTPUtilsImpl() ;
		String url = StringConstants.RESROBOT_URL + "ProducerList.json?" ;
		String params = "key=" + StringConstants.RESROBOT_KEY + "&apiVersion=" + StringConstants.RESROBOT_API_VERSION ;
		
		try {
			String response = httpUtils.requestURLConnection(url + params) ;
			return response ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null ;
	}
	
	/**
	 * This request is used to get a list of all the possible transport modes That are being used in the system.
	 * The list of transport modes May change, but it is normally not updated more than once thwart per week, so the calling system does not have to call
	 * this method more than once a day.
	 * @return	The list of all possible transport modes in JSON format
	 */
	public String transportModeList() {
		HTTPUtilsImpl httpUtils = new HTTPUtilsImpl() ;
		String url = StringConstants.RESROBOT_URL + "TransportModeList.json?" ;
		String params = "key=" + StringConstants.RESROBOT_KEY + "&apiVersion=" + StringConstants.RESROBOT_API_VERSION ;
		
		try {
			String response = httpUtils.requestURLConnection(url + params) ;
			return response ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null ;
	}
	
	/**
	 * This request is used to find the station and/or addresses that match a search string. The results are suitable for presentation to an end user 
	 * so that they can confirm their choice.
	 * 
	 * @param from	The search string that should be matched. Minimum 2 characters.
	 * @param to	Optional search string that should be matched. Minimum 2 characters.
	 * 
	 * @return		The result contains a list of where each <location> Such structure describes a station or an address. Coordinates in the result are given
	 *              using the coordinate system that was Unspecified with the parameter coordSys.
	 *				The location That is Considered the best match will contain an element <bestmatch> true </ best match>. Only one element in the
	 *              <from> structure and one element in the <to> structure will contain the <bestmatch> elements.
	 *				The elements <type> specifies what type of location it is. The value "S" means That it is a station and the value "A" means That it is an
	 *				address or a place. If it is a station then the location element will also containers a <locationid> elements.
	 *				The elements <displayname> is not the exact name of the station. In many cases there is a suffix Specifying in what municipality the station
	 *   			is located. This has been added Because sometimes there are many stations with the very same name (for example "CA") and the end-user then
	 *    			needs more information to be able to pick the right one.
	 */
	public String findLocation(String from, String to) {
		HTTPUtilsImpl httpUtils = new HTTPUtilsImpl() ;
		String url = StringConstants.RESROBOT_URL + "FindLocation.json?" ;
		String params = "key=" + StringConstants.RESROBOT_KEY + "&apiVersion=" + StringConstants.RESROBOT_API_VERSION + "&coordSys=" + StringConstants.COORD_SYSTEM ;
		
		if (from != null && to != null && !from.equals("") && !to.equals("")) {
			params += "&from=" + from + "&to=" + to ;
		}
		else {
			System.err.println("ERROR: Invalid from or to address. 'from' and 'to' must be valid strings.") ;
			return "error_findLocation" ;
		}
		
		try {
			String response = httpUtils.requestURLConnection(url + params) ;
			return response ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null ;
	}
	
	/**
	 * This request is used to get the upcoming departures for a specified location.
	 * 
	 * @param locationId	Location id of the location to get departures for. Get the location id from the  ResRobot Find Location Method, 
	 * 						ResRobot StationsInZone method or  ResRobot Search method  (note that These methods are from a different API and 
	 * 						thus a different API key is needed).
	 * @param timespan		Get departures for the next timespan minutes. Valid values ​​are 30 and 120, other values ​​results in an error. 
	 * 						Default if not specified is 30 minutes. NB: There is no guarantee That the departures will actually cover the whole 
	 * 						requested timespan period. That might happen when ResRobot cycle the information directly from another system that 
	 * 						does not support this functionality, or the other system may have a limit on the number of departures returned. 
	 * 						Also note, that you might not get any real time information for trips far into the future since the trips perhaps 
	 * 						have not even departed yet, etc.
	 * @return				The result contains a list of where each <departuresegment> Such structure describes a departure. Coordinates in the
	 *  					result are given using the coordinate system that was Unspecified with the parameter coordSys.
	 *						The <datetime> element shows the departure time as it should be According To the timetable. If there is any real time 
	 *						information for the departure then there will exist a <realtime> elements as well. The <realtime> element may contain 
	 *						a deviation Unspecified time in minutes, or informationthat the departure has been canceled. It can overpriced containers 
	 *						a <departureseverity> element thatgives an indication of how severe the deviation is. It can have the Following values:
	 *							40 - Critical - A major disturbance
	 *							30 - Normal - A disturbance
	 *							20 - None - No disturbance
	 *							10 - Passed - A disturbance That occurred in the past
	 *						The elements <mot> signifies "means of transportation". It has an attribute "type" that uses a text code to denote what type 
	 *						of transportation is used for the trip, eg "JX2". The description of the transport mode is shown within the element, eg "X 2000 '. 
	 *						The attribute "display type" shows what basic type of transport this corresponds to. It can be one of the values ​​in the table below:
	 *							A - Air
	 *							B - Bus
	 *							C - Car
	 *							D - Airport bus
	 *							E - Airport taxi
	 *							F - Ferry
	 *							J - Train
	 *							K - However
	 *							S - Streetcar
	 *							T - Taxi
	 *							U - Underground
	 *							N - Unspecified
	 */
	public String getDepartures(String locationId, int timespan) {
		HTTPUtilsImpl httpUtils = new HTTPUtilsImpl() ;
		
		String params = "key=" + StringConstants.RESROBOT_GET_DEPARTURES_KEY + "&apiVersion=" + StringConstants.RESROBOT_GET_DEPARTURES_API_VERSION + "&coordSys=" + StringConstants.COORD_SYSTEM ;
		
		if (locationId != null && !locationId.equals("")) {
			params += "&locationId=" + locationId ;
		}
		
		if (timespan == 30 || timespan == 120) {
			params += "&timespan=" + timespan ;
		}
		
		try {
			String response = httpUtils.requestURLConnection(StringConstants.RESROBOT_GET_DIRECTIONS_URL + params) ;
			return response ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null ;
	}
	
	/**
	 * Parses all segments contained in a {@link net.sf.json.JSONObject} object to an {@link java.util.HashMap} containing all segments for all routes given by ResRobot
	 * @param resultObj	The {@link net.sf.json.JSONObject} object
	 * @return			The {@link java.util.HashMap} containing all segments for all routes
	 */
	public HashMap<String, ArrayList<MobisSegment>> parseResRobotSegments(JSONObject resultObj) {
		JSONObject aux, carrier, segmentId, departure, arrival, mot, location, timeTableResult = resultObj.getJSONObject("timetableresult") ;
		JSONArray segment, segmentArray = timeTableResult.getJSONArray("ttitem") ;
		String segmentStr, str ;
		GISUtils gisUtils = new GISUtilsImpl() ;
		MobisSegment seg ;
		ArrayList<MobisSegment> segments ;
		MobisNode node ;
		ArrayList<MobisNode> nodes ;
		Date date ;
		DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm") ;
		HashMap<String, ArrayList<MobisSegment>> segmentMap = new HashMap<>() ;
		try {
			for (int i = 0; i < segmentArray.size(); i++) {
				segmentStr = segmentArray.getString(i) ;
				segments = new ArrayList<>() ;
				if ((segment = JSONObject.fromObject(segmentStr).getJSONArray("segment")) != null) {	// IF THERE IS MORE THAN ONE SEGMENT, THEN WE ARE HANDLING A JSONArray
					for (int j = 0 ; j < segment.size(); j++) {
						seg = new MobisSegment() ;
						str = segment.getString(j) ;
						aux = JSONObject.fromObject(str) ;
						segmentId = aux.getJSONObject("segmentid") ;
						arrival = aux.getJSONObject("arrival") ;
						departure = aux.getJSONObject("departure") ;
						mot = segmentId.getJSONObject("mot") ;
						seg.setTransport(mot.getString("@displaytype")) ;
						if (segmentId.has("carrier")) {
							carrier = segmentId.getJSONObject("carrier") ;
							seg.setCarrierName(StringEscapeUtils.unescapeXml(carrier.getString("name")));
							seg.setCarrierNumber(carrier.getString("number"));
							seg.setCarrierId(carrier.getString("id"));
						}
						if (aux.has("direction")) {
							seg.setDirection(StringEscapeUtils.unescapeXml(aux.getString("direction"))) ;
						}
						if (segmentId.has("distance"))
							seg.setDistance(segmentId.getDouble("distance"));
						location = departure.getJSONObject("location") ;
						nodes = new ArrayList<>() ;
						node = new MobisNode() ;
						node.setLat(location.getDouble("@y")) ;
						node.setLng(location.getDouble("@x")) ;
						node.setNodeNumber(0) ;
						nodes.add(node) ;
						if (location.has("@id")) 
							seg.setStartStationId(location.getInt("@id")) ;
						seg.setStartStation(StringEscapeUtils.unescapeXml(location.getString("name"))) ;
						date = fmt.parse(departure.getString("datetime")) ;
						seg.setStartTime(fmt.format(date)) ;
						location = arrival.getJSONObject("location") ;
						node = new MobisNode() ;
						node.setLat(location.getDouble("@y")) ;
						node.setLng(location.getDouble("@x")) ;
						node.setNodeNumber(1) ;
						nodes.add(node) ;
						seg.setEndStation(StringEscapeUtils.unescapeXml(location.getString("name"))) ;
						if (location.has("@id")) 
							seg.setEndStationId(location.getInt("@id")) ;
						date = fmt.parse(arrival.getString("datetime")) ;
						seg.setEndTime(fmt.format(date)) ;
						seg.setNodes(nodes) ;
						seg.setStaticMapURL(gisUtils.getStaticMapForSegment(seg, "blue", 2, "300x400", 1, "jpg", "hybrid", false));
						
						seg = this.getSegmentNodes(seg, i) ;
						
 						segments.add(seg) ;
					}
				}
				else {		// IF THERE IS ONLY ONE SEGMENT, THEN WE ARE HANDLING A JSONObject
					seg = new MobisSegment() ;
					aux = JSONObject.fromObject(segmentStr).getJSONObject("segment") ;
					segmentId = aux.getJSONObject("segmentid") ;
					arrival = aux.getJSONObject("arrival") ;
					departure = aux.getJSONObject("departure") ;
					mot = segmentId.getJSONObject("mot") ;
					seg.setTransport(mot.getString("@displaytype")) ;
					if (segmentId.has("carrier")) {
						carrier = segmentId.getJSONObject("carrier") ;
						seg.setCarrierName(StringEscapeUtils.unescapeXml(carrier.getString("name")));
						seg.setCarrierNumber(carrier.getString("number"));
						seg.setCarrierId(carrier.getString("id"));
					}
					if (aux.has("direction")) {
						seg.setDirection(StringEscapeUtils.unescapeXml(aux.getString("direction"))) ;
					}
					if (segmentId.has("distance"))
						seg.setDistance(segmentId.getDouble("distance"));
					location = departure.getJSONObject("location") ;
					
					nodes = new ArrayList<>() ;
					node = new MobisNode() ;
					node.setLat(location.getDouble("@y")) ;
					node.setLng(location.getDouble("@x")) ;
					node.setNodeNumber(0) ;
					nodes.add(node) ;
					if (location.has("@id")) 
						seg.setStartStationId(location.getInt("@id")) ;
					seg.setStartStation(StringEscapeUtils.unescapeXml(location.getString("name"))) ;
					date = fmt.parse(departure.getString("datetime")) ;
					seg.setStartTime(fmt.format(date)) ;
					location = arrival.getJSONObject("location") ;
					node = new MobisNode() ;
					node.setLat(location.getDouble("@y")) ;
					node.setLng(location.getDouble("@x")) ;
					node.setNodeNumber(1) ;
					nodes.add(node) ;
					if (location.has("@id")) 
						seg.setEndStationId(location.getInt("@id")) ;
					seg.setEndStation(StringEscapeUtils.unescapeXml(location.getString("name"))) ;
					date = fmt.parse(arrival.getString("datetime")) ;
					seg.setEndTime(fmt.format(date)) ;
					//seg.setStaticMapURL(gisUtils.getStaticMapForSegment(seg, "blue", 2, "300x400", 1, "jpg", "hybrid", false));
					seg.setNodes(nodes) ;
					
					seg = this.getSegmentNodes(seg, i) ;
					
					segments.add(seg) ;
				}
				segmentMap.put(String.valueOf(i), segments) ;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return segmentMap ;
	}
	
	public MobisSegment getSegmentNodes(MobisSegment segment, int segNum) {
		Coordinate origin ;
		Coordinate destination ;
		ArrayList<MobisNode> nodes = new ArrayList<>() ;
		DBUtils dbUtils = new DBUtilsImpl() ;
		GTFSDBConnector gtfs = new GTFSDBConnectorImpl() ;
		MobisGraphDBConnector graph = new MobisGraphDBConnectorImpl() ;
		Connection con ;
		String transport = segment.getTransport() ; 
		ArrayList<Coordinate> coords ;
		boolean isFirst = true ;
		int criteria = 0 ;
		MobisSegment dummy ;
		MobisNode node ;
		
		if (segNum > 0)
			isFirst = false ;
		try {
			if (transport.equals(StringConstants.WALKING_DISPLAY_TYPE) || transport.equals(StringConstants.WALK_LONG_DISTANCE_DISPLAY_TYPE)) {
				con = dbUtils.startConnection("/MobisPGSQLConfig.xml", StringConstants.SWEDEN_GRAPH, StringConstants.JDBC_POSTGRES) ;
				origin = new Coordinate(segment.getNodes().get(0).getLat(), segment.getNodes().get(0).getLng()) ;
				destination = new Coordinate(segment.getNodes().get(1).getLat(), segment.getNodes().get(1).getLng()) ;
				
				dummy = graph.dijkstraPath(dbUtils, con, origin, segNum, transport, segment.getStartTime(), destination, criteria, 0.00001, StringConstants.EDGE_TABLE, false, false, StringConstants.EPSG, isFirst, "") ;
				if (dummy.getNodes().size() > 1) {
					segment.setNodes(dummy.getNodes()) ;
				}
				else {
					node = new MobisNode() ;
					node.setLat(origin.getLat()) ;
					node.setLng(origin.getLng()) ;
					node.setNodeNumber(0);
					nodes.add(node) ;
					
					node = new MobisNode() ;
					node.setLat(destination.getLat()) ;
					node.setLng(destination.getLng()) ;
					node.setNodeNumber(1) ;
					nodes.add(node) ;
				}
			}
			else {
				con = dbUtils.startConnection("/MobisPGSQLConfig.xml", StringConstants.MOBIS_GTFS, StringConstants.JDBC_POSTGRES) ;
				coords = gtfs.getSegmentCoordinates(dbUtils, con, segment) ;
				con.close() ;
				con = dbUtils.startConnection("/MobisPGSQLConfig.xml", StringConstants.SWEDEN_GRAPH, StringConstants.JDBC_POSTGRES) ;
				for (int i = 1; i < coords.size(); i++) {
					origin = coords.get(i - 1) ;
					destination = coords.get(i) ;
					dummy = graph.dijkstraPath(dbUtils, con, origin, segNum, transport, segment.getStartTime(), destination, criteria, 0.00001, StringConstants.EDGE_TABLE, true, false, StringConstants.EPSG, isFirst, "") ;
					if (dummy != null) {
						if (dummy.getNodes().size() > 1) {
							for (int j = 0; j < dummy.getNodes().size(); j++) {
								dummy.getNodes().get(j).setNodeNumber(nodes.size() + j) ;
							}
							nodes.addAll(dummy.getNodes()) ;
						}
						else {
							if (i == 1) {
								node = new MobisNode() ;
								node.setLat(origin.getLat()) ;
								node.setLng(origin.getLng()) ;
								node.setNodeNumber(nodes.size());
								nodes.add(node) ;
							}
							node = new MobisNode() ;
							node.setLat(destination.getLat()) ;
							node.setLng(destination.getLng()) ;
							node.setNodeNumber(nodes.size()) ;
							nodes.add(node) ;
						}
					}
					else {
						if (i == 1) {
							node = new MobisNode() ;
							node.setLat(origin.getLat()) ;
							node.setLng(origin.getLng()) ;
							node.setNodeNumber(nodes.size());
							nodes.add(node) ;
						}
						node = new MobisNode() ;
						node.setLat(destination.getLat()) ;
						node.setLng(destination.getLng()) ;
						node.setNodeNumber(nodes.size()) ;
						nodes.add(node) ;
					}
				}
				segment.setNodes(nodes) ;
			}
			
			con.close() ; 
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return segment ;
	}
}	