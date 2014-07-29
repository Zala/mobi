package org.uninova.mobis.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

//import org.uninova.mobis.apis.geolocation.NominatimGeolocation;
//import org.uninova.mobis.apis.geolocation.NominatimGeolocationImpl;
import org.uninova.mobis.constants.StringConstants;
import org.uninova.mobis.pojos.Coordinate;
import org.uninova.mobis.pojos.MobisNode;
import org.uninova.mobis.pojos.MobisSegment;
import org.uninova.mobis.utils.DBUtils;
import org.uninova.mobis.utils.GISUtils;
import org.uninova.mobis.utils.GISUtilsImpl;

public class MobisGraphDBConnectorImpl implements MobisGraphDBConnector {
	
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss") ;
	
	/**
	 * Dijkstra’s algorithm, conceived by Dutch computer scientist Edsger Dijkstra in 1956. It is a graph search algorithm that solves the single-source shortest path problem for a graph with non-negative edge path costs, producing a shortest path tree.
	 * @param dbUtils		The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con			The {@link java.sql.Connection} to the graph database
	 * @param source		Coordinate for the source point
	 * @param segNum		Sequence number of the segment
	 * @param transport		The type of transport used in the route
	 * @param startDateTime	The start time for this route, in the format "yyyy-MM-dd HH:mm:ss"
	 * @param target		Coordinate for the target point
	 * @param criteria		The criteria for the route (0: Shortest, 1: Quickest,...)
	 * @param tolerance		
	 * @param edgeTable		The name of the database table for edges (arcs)
	 * @param directed		true if the algorithm is to be applied considering a directed graph, false otherwise
	 * @param reverseCost	true if the algorithm should contemplate reverse cost
	 * @param epsgRefSys	The EPSG code for the reference system used (e.g.: WGS84 -> EPSG 4326)
	 * @param isFirst		true if the segment is the first segment on the route, false otherwise
	 * @param customCost	A {@link java.lang.String} object containing a SQL sentence defining a custom cost
	 * @return				A {@link org.uninova.mobis.pojos.MobisSegment} object containing the routing algorithm's result
	 */
	public MobisSegment dijkstraPath(DBUtils dbUtils, Connection con, Coordinate source, int segNum, String transport, String startDateTime, Coordinate target, int criteria, double tolerance, String edgeTable, boolean directed, boolean reverseCost, String epsgRefSys, boolean isFirst, String customCost) {
		//NominatimGeolocation geo = new NominatimGeolocationImpl() ;
		long sourceId, targetId ;
		ResultSet rs, subSet ;
		String query, criteriaStr = "" ;
		MobisSegment segment ;
		ArrayList<MobisNode> nodes ;
		MobisNode node ;
		int maxspeed = 0 ;
		double totalDistance = 0.0, totalTime = 0.0 ;
		Calendar cal ;
		Coordinate finalCoord = new Coordinate() ;
		
		try {
			if (transport.equals(StringConstants.BYCICLE_DISPLAY_TYPE)) {
				maxspeed = 20 ;
			}
			else if (transport.equals(StringConstants.WALKING_DISPLAY_TYPE) || transport.equals(StringConstants.WALK_LONG_DISTANCE_DISPLAY_TYPE)) {
				maxspeed = 5 ;
			}
			
			criteriaStr = this.buildCriteriaString(criteria, transport, StringConstants.DIJKSTRA_KSP_SQL_SELECT, customCost) ;
			
			sourceId = this.getNearestVertex(source, edgeTable, epsgRefSys, con, dbUtils, transport) ;
			targetId = this.getNearestVertex(target, edgeTable, epsgRefSys, con, dbUtils, transport) ;
			
			query = "SELECT seq, id1 AS node, id2 AS edge FROM public.pgr_dijkstra('" + criteriaStr + "', " + sourceId + ", " + targetId + ", " + directed + ", " + reverseCost + ");" ;
			rs = dbUtils.queryDB(con, query) ;
			
			if (rs != null) {
				segment = new MobisSegment() ;
				nodes = new ArrayList<>() ;
				while (rs.next()) {
					if (rs.isLast()) {
						node = new MobisNode() ;
						node.setLat(finalCoord.getLat()) ;
						node.setLng(finalCoord.getLng()) ;
						node.setNodeNumber(nodes.size()) ;
						node.setFinish(true) ;
						nodes.add(node) ;
						break ;
					}
					node = new MobisNode() ;
					query = "SELECT * FROM public.ways WHERE gid=" + rs.getLong("edge") + ";" ;
					subSet = dbUtils.queryDB(con, query) ;
					if (subSet != null && subSet.next()) {
						if (transport.equals(StringConstants.CAR_DISPLAY_TYPE)) {
							maxspeed = subSet.getInt("maxspeed_forward") ;
						}
						if (subSet.getDouble("length") > 0.0) {
							totalDistance += subSet.getDouble("length") ;
							totalTime += (subSet.getDouble("length")*60/maxspeed) ;
						}
						node.setLat(subSet.getDouble("y1")) ;
						node.setLng(subSet.getDouble("x1")) ;
						node.setNodeNumber(rs.getInt("seq")) ;
						finalCoord = new Coordinate(subSet.getDouble("y2"),subSet.getDouble("x2")) ;
						//node.setOsmNodeId(String.valueOf(geo.getNearestNodeId(new Coordinate(node.getLat(), node.getLng())))) ;
						
					}
					
					if (rs.isFirst()) {
						if (isFirst) {
							node.setStart(true) ;
						}
						else {
							node.setWaypoint(true) ;
						}
					}
					nodes.add(node) ;
				}
				segment.setStartTime(startDateTime) ;
				segment.setDistance(totalDistance) ;
				segment.setTransport(transport) ;
				segment.setNodes(nodes) ;
				segment.setSegmentNumber(segNum) ;
				
				cal = Calendar.getInstance() ;
				if (startDateTime.indexOf(":") == startDateTime.lastIndexOf(":")) {
					startDateTime += ":00" ;
				}
				cal.setTime(dateFormat.parse(startDateTime)) ;
				cal.add(Calendar.MINUTE, (int) Math.round(totalTime)) ;
				segment.setEndTime(dateFormat.format(cal.getTime())) ;
				return segment ;
			}
			
			
			
		} catch (SQLException | ParseException e) {
			return null ;
		}
		return null ;
	}
	 
	/**
	 * The A* (pronounced “A Star”) algorithm is based on Dijkstra’s algorithm with a heuristic that allow it to solve most shortest path problems by evaluation only a sub-set of the overall graph.
	 * @param dbUtils		The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con			The {@link java.sql.Connection} to the graph database
	 * @param source		Coordinate for the source point
	 * @param target		Coordinate for the target point
	 * @param segNum		Sequence number of the segment
	 * @param transport		The type of transport used in the route
	 * @param startDateTime	The start time for this route, in the format "yyyy-MM-dd HH:mm:ss"
	 * @param criteria		The criteria for the route (0: Shortest, 1: Quickest,...)
	 * @param tolerance		
	 * @param edgeTable		The name of the database table for edges (arcs)
	 * @param directed		true if the algorithm is to be applied considering a directed graph, false otherwise
	 * @param reverseCost	true if the algorithm should contemplate reverse cost
	 * @param epsgRefSys	The EPSG code for the reference system used (e.g.: WGS84 -> EPSG 4326)
	 * @param isFirst		true if the segment is the first segment on the route, false otherwise
	 * @param customCost	A {@link java.lang.String} object containing a SQL sentence defining a custom cost
	 * @return				A {@link org.uninova.mobis.pojos.MobisSegment} object containing the routing algorithm's result
	 */
	public MobisSegment aStarPath(DBUtils dbUtils, Connection con, Coordinate source, Coordinate target, int segNum, String startDateTime, String transport, double tolerance, int criteria, String edgeTable, boolean directed, boolean reverseCost, String epsgRefSys, boolean isFirst, String customCost) {
		//NominatimGeolocation geo = new NominatimGeolocationImpl() ;
		ResultSet rs, subSet ;
		String query, criteriaStr = "" ;
		MobisSegment segment ;
		ArrayList<MobisNode> nodes ;
		MobisNode node ;
		int maxspeed = 0 ;
		double totalDistance = 0.0, totalTime = 0.0 ;
		Calendar cal ;
		long sourceId, targetId ;
		Coordinate finalCoord = new Coordinate() ;
		
		if (transport.equals(StringConstants.BYCICLE_DISPLAY_TYPE)) {
			maxspeed = 20 ;
		}
		else if (transport.equals(StringConstants.WALKING_DISPLAY_TYPE) || transport.equals(StringConstants.WALK_LONG_DISTANCE_DISPLAY_TYPE)) {
			maxspeed = 5 ;
		}
		
		criteriaStr = this.buildCriteriaString(criteria, transport, StringConstants.ASTAR_SQL_SELECT, customCost) ;
		
		sourceId = this.getNearestVertex(source, edgeTable, epsgRefSys, con, dbUtils, transport) ;
		targetId = this.getNearestVertex(target, edgeTable, epsgRefSys, con, dbUtils, transport) ;
		
		query = "SELECT seq, id1 AS node, id2 AS edge FROM public.pgr_astar('" + criteriaStr + "', " + sourceId + ", " + targetId + ", " + directed + ", " + reverseCost + ");" ;
		rs = dbUtils.queryDB(con, query) ;
		
		try {
			if (rs != null) {
				segment = new MobisSegment() ;
				nodes = new ArrayList<>() ;
				while (rs.next()) {
					if (rs.isLast()) {
						node = new MobisNode() ;
						node.setLat(finalCoord.getLat()) ;
						node.setLng(finalCoord.getLng()) ;
						node.setNodeNumber(nodes.size()) ;
						node.setFinish(true) ;
						nodes.add(node) ;
						break ;
					}
					node = new MobisNode() ;
					query = "SELECT * FROM public.ways WHERE gid=" + rs.getLong("edge") + ";" ;
					subSet = dbUtils.queryDB(con, query) ;
					if (subSet != null && subSet.next()) {
						if (transport.equals(StringConstants.CAR_DISPLAY_TYPE)) {
							maxspeed = subSet.getInt("maxspeed_forward") ;
						}
						if (subSet.getDouble("length") > 0.0) {
							totalDistance += subSet.getDouble("length") ;
							totalTime += (subSet.getDouble("length")*60/maxspeed) ;
						}
						node.setLat(subSet.getDouble("y1")) ;
						node.setLng(subSet.getDouble("x1")) ;
						node.setNodeNumber(rs.getInt("seq")) ;
						finalCoord = new Coordinate(subSet.getDouble("y2"),subSet.getDouble("x2")) ;
						//node.setOsmNodeId(String.valueOf(geo.getNearestNodeId(new Coordinate(node.getLat(), node.getLng())))) ;
						
					}
					
					if (rs.isFirst()) {
						if (isFirst) {
							node.setStart(true) ;
						}
						else {
							node.setWaypoint(true) ;
						}
					}
					nodes.add(node) ;
				}
				segment.setStartTime(startDateTime) ;
				segment.setDistance(totalDistance) ;
				segment.setTransport(transport) ;
				segment.setNodes(nodes) ;
				segment.setSegmentNumber(segNum) ;
				
				cal = Calendar.getInstance() ;
				cal.setTime(dateFormat.parse(startDateTime)) ;
				cal.add(Calendar.MINUTE, (int) Math.round(totalTime)) ;
				segment.setEndTime(dateFormat.format(cal.getTime())) ;
				return segment ;
			}
		} catch (SQLException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null ;
	}
	
	/**
	 * The K shortest path routing algorithm based on Yen’s algorithm. “K” is the number of shortest paths desired.
	 * @param dbUtils		The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con			The {@link java.sql.Connection} to the graph database
	 * @param source		Coordinate for the source point
	 * @param target		Coordinate for the target point
	 * @param segNum		Sequence number of the segment
	 * @param optionsNum	The number of shortest paths in the result (K)
	 * @param transport		The type of transport used in the route
	 * @param startDateTime	The start time for this route, in the format "yyyy-MM-dd HH:mm:ss"
	 * @param criteria		The criteria for the route (0: Shortest, 1: Quickest,...)
	 * @param tolerance		
	 * @param edgeTable		The name of the database table for edges (arcs)
	 * @param directed		true if the algorithm is to be applied considering a directed graph, false otherwise
	 * @param reverseCost	true if the algorithm should contemplate reverse cost
	 * @param epsgRefSys	The EPSG code for the reference system used (e.g.: WGS84 -> EPSG 4326)
	 * @param isFirst		true if the segment is the first segment on the route, false otherwise
	 * @param customCost	A {@link java.lang.String} object containing a SQL sentence defining a custom cost
	 * @return				A {@link java.util.HashMap} object containing {@link org.uninova.mobis.pojos.MobisSegment} objects for each route option
	 */
	public HashMap<String, ArrayList<MobisSegment>> kShortestPaths(DBUtils dbUtils, Connection con, Coordinate source, Coordinate target, String transport, int criteria, int segNum, int optionsNum, String startDateTime, String edgeTable, boolean reverseCost, String epsgRefSys, boolean isStart, String customCost) {
		//GISUtils gisUtils = new GISUtilsImpl() ;
		//NominatimGeolocation geo = new NominatimGeolocationImpl() ;
		String criteriaStr = "", query ;
		int maxspeed = 0, counter = -1, currentRoute ;
		long sourceId, targetId ;
		double totalDistance = 0.0, totalTime = 0.0 ;
		ResultSet rs, subSet ;
		MobisSegment segment ;
		HashMap<String, ArrayList<MobisSegment>> segmentMap = new HashMap<>() ;
		ArrayList<MobisSegment> segments ;
		MobisNode node ;
		ArrayList<MobisNode> nodes ;
		Calendar cal ;
		Coordinate finalCoord = new Coordinate();
		
		if (transport.equals(StringConstants.BYCICLE_DISPLAY_TYPE)) {
			maxspeed = 20 ;
		}
		else if (transport.equals(StringConstants.WALKING_DISPLAY_TYPE) || transport.equals(StringConstants.WALK_LONG_DISTANCE_DISPLAY_TYPE)) {
			maxspeed = 5 ;
		}
		
		criteriaStr = this.buildCriteriaString(criteria, transport, StringConstants.DIJKSTRA_KSP_SQL_SELECT, customCost) ;
		
		sourceId = this.getNearestVertex(source, edgeTable, epsgRefSys, con, dbUtils, transport) ;
		targetId = this.getNearestVertex(target, edgeTable, epsgRefSys, con, dbUtils, transport) ;
		
		query = "SELECT seq, id1 AS route, id2 AS node, id3 AS edge, cost FROM public.pgr_ksp('" + criteriaStr + "', " + sourceId + ", " + targetId + ", " + optionsNum + ", " + reverseCost + ");" ;
		rs = dbUtils.queryDB(con, query) ;
		try {
			if (rs != null) {
				segments = new ArrayList<>() ;
				segment = new MobisSegment() ;
				nodes = new ArrayList<>() ;
				while (rs.next()) {
					currentRoute = rs.getInt("route") ;
					node = new MobisNode() ;
					if (currentRoute != counter) {
						if (currentRoute > 0) {
							if (isStart) {
								nodes.get(0).setStart(true) ;
							}
							else {
								nodes.get(0).setWaypoint(true) ;
							}
							nodes.get(nodes.size() - 1).setFinish(true) ;
							segment.setStartTime(startDateTime) ;
							segment.setDistance(totalDistance) ;
							segment.setTransport(transport) ;
							segment.setNodes(nodes) ;
							segment.setSegmentNumber(segNum) ;
							cal = Calendar.getInstance() ;
							cal.setTime(dateFormat.parse(startDateTime)) ;
							cal.add(Calendar.MINUTE, (int) Math.round(totalTime)) ;
							segment.setEndTime(dateFormat.format(cal.getTime())) ;
							//segment.setStaticMapURL(gisUtils.getStaticMapForSegment(segment, "blue", 2, "300x400", 1, "jpg", "hybrid", false)) ;
							
							segments.add(segment) ;
							segmentMap.put(String.valueOf(counter), segments) ;
							segments = new ArrayList<>() ;
							segment = new MobisSegment() ;
							nodes = new ArrayList<>() ;
							totalDistance = 0.0 ;
							totalTime = 0.0 ;
						}
						counter++ ;
					}
					if (rs.isLast()) {
						node = new MobisNode() ;
						node.setLat(finalCoord.getLat()) ;
						node.setLng(finalCoord.getLng()) ;
						node.setNodeNumber(rs.getInt("seq") + 1) ;
						node.setFinish(true) ;
						//node.setOsmNodeId(String.valueOf(geo.getNearestNodeId(new Coordinate(node.getLat(), node.getLng())))) ;
						nodes.add(node) ;
						
						if (isStart) {
							nodes.get(0).setStart(true) ;
						}
						else {
							nodes.get(0).setWaypoint(true) ;
						}
						segment.setStartTime(startDateTime) ;
						segment.setDistance(totalDistance) ;
						segment.setTransport(transport) ;
						segment.setNodes(nodes) ;
						segment.setSegmentNumber(segNum) ;
						cal = Calendar.getInstance() ;
						cal.setTime(dateFormat.parse(startDateTime)) ;
						cal.add(Calendar.MINUTE, (int) Math.round(totalTime)) ;
						segment.setEndTime(dateFormat.format(cal.getTime())) ;
						//segment.setStaticMapURL(gisUtils.getStaticMapForSegment(segment, "blue", 2, "300x400", 1, "jpg", "hybrid", false)) ;
						
						segments.add(segment) ;
						segmentMap.put(String.valueOf(counter), segments) ;
						continue ;
					}
					query = "SELECT * FROM public.ways WHERE gid=" + rs.getLong("edge") + ";" ;
					subSet = dbUtils.queryDB(con, query) ;
					if (subSet != null && subSet.next()) {
						if (transport.equals(StringConstants.CAR_DISPLAY_TYPE)) {
							maxspeed = subSet.getInt("maxspeed_forward") ;
						}
						if (subSet.getDouble("length") > 0.0) {
							totalDistance += subSet.getDouble("length") ;
							totalTime += (subSet.getDouble("length")*60/maxspeed) ;
						}
						node.setLat(subSet.getDouble("y1")) ;
						node.setLng(subSet.getDouble("x1")) ;
						node.setNodeNumber(rs.getInt("seq")) ;
						finalCoord = new Coordinate(subSet.getDouble("y2"), subSet.getDouble("x2")) ;
						//node.setOsmNodeId(String.valueOf(geo.getNearestNodeId(new Coordinate(node.getLat(), node.getLng())))) ;
					}
					
					nodes.add(node) ;
				}
				return segmentMap ;
			}
		} catch (SQLException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null ;
	}
	
	
	/**
	 * Gets the nearest graph vertex to the input coordinate
	 * @param coord			The coordinate to analyze
	 * @param edgeTable		The name of the database table for edges (arcs)
	 * @param epsgRefSys	The EPSG code for the reference system used (e.g.: WGS84 -> EPSG 4326)
	 * @param con			The {@link java.sql.Connection} to the graph database
	 * @param dbUtils		The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @return				The unique ID of the nearest vertex node in the database
	 */
	public long getNearestVertex(Coordinate coord, String edgeTable, String epsgRefSys, Connection con, DBUtils dbUtils, String transport) {
		
		String selectQuery = "SELECT x1,x2,y1,y2,source,target FROM public." + edgeTable ;
		if (transport.equals(StringConstants.CAR_DISPLAY_TYPE)) {
			selectQuery += " " + StringConstants.CAR_RESTRICTIONS ;
		}
		else if (transport.equals(StringConstants.WALK_LONG_DISTANCE_DISPLAY_TYPE) || transport.equals(StringConstants.WALKING_DISPLAY_TYPE)) {
			selectQuery += " " + StringConstants.WALK_RESTRICTIONS ;
		}
		else if (transport.equals(StringConstants.BYCICLE_DISPLAY_TYPE)) {
			selectQuery += " " + StringConstants.BIKE_RESTRICTIONS ;
		}
		else if (transport.equals(StringConstants.AIRPORT_BUS_DISPLAY_TYPE) || transport.equals(StringConstants.AIRPORT_TAXI_DISPLAY_TYPE) || transport.equals(StringConstants.BUS_DISPLAY_TYPE) || transport.equals(StringConstants.TAXI_DISPLAY_TYPE)) {
			selectQuery += " " + StringConstants.BUS_RESTRICTIONS ;
		}
		else if (transport.equals(StringConstants.TRAIN_DISPLAY_TYPE)) {
			selectQuery += " " + StringConstants.TRAIN_RESTRICTIONS ;
		}
		else if (transport.equals(StringConstants.UNDERGROUND_DISPLAY_TYPE)) {
			selectQuery += " " + StringConstants.METRO_RESTRICTIONS ;
		}
		selectQuery	+= " ORDER BY the_geom <-> ST_GeometryFromText('POINT(" + coord.getLng() + " " + coord.getLat() + ")'," + epsgRefSys + ") LIMIT 1;" ;
		
		GISUtils gisUtils = new GISUtilsImpl() ;
		double distToSourceNode, distToTargetNode ;
		
		ResultSet rs = dbUtils.queryDB(con, selectQuery) ;
		if (rs != null) {
			try {
				while (rs.next()) {
					distToSourceNode = gisUtils.distFrom(coord, new Coordinate(rs.getDouble("y1"), rs.getDouble("x1"))) ;
					distToTargetNode = gisUtils.distFrom(coord, new Coordinate(rs.getDouble("y2"), rs.getDouble("x2"))) ;
					
					if (distToSourceNode <= distToTargetNode) {
						return rs.getLong("source") ;
					}
					else {
						return  rs.getLong("target") ;
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return -1 ;
	}
	
	/**
	 * Gets the nearest graph edge to the coordinate
	 * @param coord			The coordinate to analyze
	 * @param edgeTable		The name of the database table for edges (arcs)
	 * @param epsgRefSys	The EPSG code for the reference system used (e.g.: WGS84 -> EPSG 4326)
	 * @param con			The {@link java.sql.Connection} to the graph database
	 * @param dbUtils		The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @return				The unique ID of the nearest edge in the database
	 */
	public long getNearestEdge(Coordinate coord, String edgeTable, String epsgRefSys, Connection con, DBUtils dbUtils) {
		
		String selectQuery = "SELECT gid,x1,x2,y1,y2,source,target FROM public." + edgeTable + " ORDER BY the_geom <-> ST_GeometryFromText('POINT(" + coord.getLng() + " " + coord.getLat() + ")'," + epsgRefSys + ") LIMIT 1;" ;
		
		ResultSet rs = dbUtils.queryDB(con, selectQuery) ;
		if (rs != null) {
			try {
				while (rs.next()) {
					return rs.getLong("gid") ;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		return -1 ;
	}
	
	/**
	 * Creates a new cost function from the input string to use in any of the routing algorithms
	 * @param cost	The input string defining a SQL cost statement 
	 * @return		The cost function string
	 */
	private String createCustomCostFunction(String cost) {
		return cost + " AS cost" ;
	}
	
	/**
	 * Builds the criteria SQL string (e.g. criteria=0 -> length AS cost -> shortest route) 
	 * @param criteria			The criteria for the route (0: Shortest, 1: Quickest,...)
	 * @param transport			The type of transport used in the route
	 * @param sqlSelectFunction	The SQL select string for the specific algorithm being used
	 * @param customCost		A {@link java.lang.String} object containing a SQL sentence defining a custom cost
	 * @return					The full criteria SQL statement
	 */
	private String buildCriteriaString(int criteria, String transport, String sqlSelectFunction, String customCost) {
		switch (criteria) {
		case 0: 
			if (transport.equals(StringConstants.CAR_DISPLAY_TYPE)) {
				return (sqlSelectFunction + StringConstants.SHORTEST_COST + StringConstants.FROM_EDGE_TABLE + StringConstants.CAR_RESTRICTIONS) ;
			}
			else if (transport.equals(StringConstants.BYCICLE_DISPLAY_TYPE)) {
				return (sqlSelectFunction + StringConstants.SHORTEST_COST + StringConstants.FROM_EDGE_TABLE + StringConstants.BIKE_RESTRICTIONS) ;
			}
			else if (transport.equals(StringConstants.WALKING_DISPLAY_TYPE) || transport.equals(StringConstants.WALK_LONG_DISTANCE_DISPLAY_TYPE)) {
				return (sqlSelectFunction + StringConstants.SHORTEST_COST + StringConstants.FROM_EDGE_TABLE + StringConstants.WALK_RESTRICTIONS) ;
			}
			else if (transport.equals(StringConstants.AIRPORT_BUS_DISPLAY_TYPE) || transport.equals(StringConstants.AIRPORT_TAXI_DISPLAY_TYPE) || transport.equals(StringConstants.BUS_DISPLAY_TYPE) || transport.equals(StringConstants.TAXI_DISPLAY_TYPE)) {
				return (sqlSelectFunction + StringConstants.SHORTEST_COST + StringConstants.FROM_EDGE_TABLE + StringConstants.BUS_RESTRICTIONS) ;
			}
			else if (transport.equals(StringConstants.TRAIN_DISPLAY_TYPE)) {
				return (sqlSelectFunction + StringConstants.SHORTEST_COST + StringConstants.FROM_EDGE_TABLE + StringConstants.TRAIN_RESTRICTIONS) ;
			}
			else if (transport.equals(StringConstants.UNDERGROUND_DISPLAY_TYPE)) {
				return (sqlSelectFunction + StringConstants.SHORTEST_COST + StringConstants.FROM_EDGE_TABLE + StringConstants.METRO_RESTRICTIONS) ;
			}
			break ;
		case 1:
			if (transport.equals(StringConstants.CAR_DISPLAY_TYPE)) {
				return (sqlSelectFunction + StringConstants.QUICKEST_CAR_COST + StringConstants.FROM_EDGE_TABLE + StringConstants.CAR_RESTRICTIONS) ;
			}
			else if (transport.equals(StringConstants.BYCICLE_DISPLAY_TYPE)) {
				return (sqlSelectFunction + StringConstants.QUICKEST_BIKE_COST + StringConstants.FROM_EDGE_TABLE + StringConstants.BIKE_RESTRICTIONS) ;
			}
			else if (transport.equals(StringConstants.WALKING_DISPLAY_TYPE) || transport.equals(StringConstants.WALK_LONG_DISTANCE_DISPLAY_TYPE)) {
				return (sqlSelectFunction + StringConstants.QUICKEST_WALK_COST + StringConstants.FROM_EDGE_TABLE + StringConstants.WALK_RESTRICTIONS) ;
			}
			else if (transport.equals(StringConstants.AIRPORT_BUS_DISPLAY_TYPE) || transport.equals(StringConstants.AIRPORT_TAXI_DISPLAY_TYPE) || transport.equals(StringConstants.BUS_DISPLAY_TYPE) || transport.equals(StringConstants.TAXI_DISPLAY_TYPE)) {
				return (sqlSelectFunction + StringConstants.QUICKEST_CAR_COST + StringConstants.FROM_EDGE_TABLE + StringConstants.BUS_RESTRICTIONS) ;
			}
			else if (transport.equals(StringConstants.TRAIN_DISPLAY_TYPE)) {
				return (sqlSelectFunction + StringConstants.QUICKEST_CAR_COST + StringConstants.FROM_EDGE_TABLE + StringConstants.TRAIN_RESTRICTIONS) ;
			}
			else if (transport.equals(StringConstants.UNDERGROUND_DISPLAY_TYPE)) {
				return (sqlSelectFunction + StringConstants.QUICKEST_CAR_COST + StringConstants.FROM_EDGE_TABLE + StringConstants.METRO_RESTRICTIONS) ;
			}
			break ;
		default: 
			if (transport.equals(StringConstants.CAR_DISPLAY_TYPE)) {
				return (sqlSelectFunction + this.createCustomCostFunction(customCost) + StringConstants.FROM_EDGE_TABLE + StringConstants.CAR_RESTRICTIONS) ;
			}
			else if (transport.equals(StringConstants.BYCICLE_DISPLAY_TYPE)) {
				return (sqlSelectFunction + this.createCustomCostFunction(customCost) + StringConstants.FROM_EDGE_TABLE + StringConstants.BIKE_RESTRICTIONS) ;
			}
			else if (transport.equals(StringConstants.WALKING_DISPLAY_TYPE) || transport.equals(StringConstants.WALK_LONG_DISTANCE_DISPLAY_TYPE)) {
				return (sqlSelectFunction + this.createCustomCostFunction(customCost) + StringConstants.FROM_EDGE_TABLE + StringConstants.WALK_RESTRICTIONS) ;
			}
			break ;
		}	
		return "" ;
	}
}
