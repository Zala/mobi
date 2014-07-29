package org.uninova.mobis.database;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import org.uninova.mobis.pojos.Coordinate;
import org.uninova.mobis.pojos.MobisSegment;
import org.uninova.mobis.utils.DBUtils;

public interface MobisGraphDBConnector {

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
	 * @return		
	 */
	public MobisSegment dijkstraPath(DBUtils dbUtils, Connection con, Coordinate source, int segNum, String transport, String startDateTime, Coordinate target, int criteria, double tolerance, String edgeTable, boolean directed, boolean reverseCost, String epsgRefSys, boolean isFirst, String customCost) ;
	
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
	public MobisSegment aStarPath(DBUtils dbUtils, Connection con, Coordinate source, Coordinate target, int segNum, String startDateTime, String transport, double tolerance, int criteria, String edgeTable, boolean directed, boolean reverseCost, String epsgRefSys, boolean isFirst, String customCost) ;
	
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
	public HashMap<String, ArrayList<MobisSegment>> kShortestPaths(DBUtils dbUtils, Connection con, Coordinate source, Coordinate target, String transport, int criteria, int segNum, int optionsNum, String startDateTime, String edgeTable, boolean reverseCost, String epsgRefSys, boolean isStart, String customCost) ;
	
	/**
	 * Gets the nearest graph vertex to the input coordinate
	 * @param coord			The coordinate to analyze
	 * @param edgeTable		The name of the database table for edges (arcs)
	 * @param epsgRefSys	The EPSG code for the reference system used (e.g.: WGS84 -> EPSG 4326)
	 * @param con			The {@link java.sql.Connection} to the graph database
	 * @param dbUtils		The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @return				The unique ID of the nearest vertex node in the database
	 */
	public long getNearestVertex(Coordinate coord, String edgeTable, String epsgRefSys, Connection con, DBUtils dbUtils, String transport) ;
	
	/**
	 * Gets the nearest graph edge to the coordinate
	 * @param coord			The coordinate to analyze
	 * @param edgeTable		The name of the database table for edges (arcs)
	 * @param epsgRefSys	The EPSG code for the reference system used (e.g.: WGS84 -> EPSG 4326)
	 * @param con			The {@link java.sql.Connection} to the graph database
	 * @param dbUtils		The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @return				The unique ID of the nearest edge in the database
	 */
	public long getNearestEdge(Coordinate coord, String edgeTable, String epsgRefSys, Connection con, DBUtils dbUtils) ;
}
