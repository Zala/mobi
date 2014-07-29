package org.uninova.mobis.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.uninova.mobis.pojos.Coordinate;
import org.uninova.mobis.pojos.MobisSegment;
import org.uninova.mobis.utils.DBUtils;

/**
 * GTFSDBConnectorImpl
 * @author PAF
 */
public class GTFSDBConnectorImpl implements GTFSDBConnector {

	/**
	 * GTFSDBConnectorImpl Class Constructor
	 */
	public GTFSDBConnectorImpl() {}
	
	/**
	 * Gets the coordinates for the input {@link org.uninova.mobis.pojos.ResRobotSegment} object
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtilsImpl} object that manages the connection to the GTFS database
	 * @param con		The {@link java.sql.Connection} object connecting to the GTFS database
	 * @param seg		The {@link org.uninova.mobis.pojos.ResRobotSegment} object to get the coordinates to
	 * @return			The list of coordinates for the input segment, as a {@link java.util.ArrayList}
	 */
	public ArrayList<Coordinate> getSegmentCoordinates(DBUtils dbUtils, Connection con, MobisSegment seg) {
		// RETRIEVES THE STOPS AND TRIPS FROM THE GTFS DB FOR THE INPUT SEGMENT	
		String sqlSelectRoute = "SELECT route_gtfs_id FROM public.route WHERE agency_gtfs_id='" + seg.getCarrierId() + "'" ;
		String sqlSelectTrip = "SELECT trip_gtfs_id FROM public.trip WHERE route_gtfs_id IN (" + sqlSelectRoute + ")" ;
		String selectStopSequences = "SELECT trip_gtfs_id,stop_gtfs_id, stop_sequence FROM public.stop_time WHERE (stop_gtfs_id='" + seg.getStartStationId() + "' OR stop_gtfs_id='" + seg.getEndStationId() + "') AND trip_gtfs_id IN (" + sqlSelectTrip + ");" ;
		
		ResultSet set = dbUtils.queryDB(con, selectStopSequences) ;
		
		TreeMap<Integer, Integer> stopSequence = new TreeMap<Integer, Integer>() ;
		
		ArrayList<Coordinate> stopCoords = new ArrayList<Coordinate>() ;
		ArrayList<Integer> departureTrips = new ArrayList<Integer>() ;
		ArrayList<Integer> arrivalTrips = new ArrayList<Integer>() ;
		
		int trip = 0, arrivalStopNumber = 0, departureStopNumber = 0 ;
		
		// CHECKS WHICH TRIPS HAVE BOTH DEPARTURE AND ARRIVAL STATIONS
		try {
			while (set.next()) {
				if (seg.getEndStationId() == set.getInt("stop_gtfs_id")) {
					arrivalStopNumber = set.getInt("stop_sequence") ;
					if (departureTrips.contains(set.getInt("trip_gtfs_id"))) {
						trip = set.getInt("trip_gtfs_id") ;
						break ;
					}
					else {
						arrivalTrips.add(set.getInt("trip_gtfs_id")) ;
					}
				}
				else if (seg.getStartStationId() == set.getInt("stop_gtfs_id")) {
					if (arrivalTrips.contains(set.getInt("trip_gtfs_id"))) {
						
						trip = set.getInt("trip_gtfs_id") ;
						break ;
					}
					else {
						departureTrips.add(set.getInt("trip_gtfs_id")) ;
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		arrivalStopNumber = this.getStopNumber(seg.getEndStationId(), trip, con, dbUtils) ;
		departureStopNumber = this.getStopNumber(seg.getStartStationId(), trip, con, dbUtils) ;
		
		// RETRIEVES THE STOPS FROM THE GTFS DB FOR A PARTICULAR TRIP ID
		String sqlSelectStopTimes = "SELECT stop_gtfs_id, stop_sequence FROM public.stop_time WHERE trip_gtfs_id='" + trip + "'" ;
		set = dbUtils.queryDB(con, sqlSelectStopTimes) ;
		try {
			while (set.next()) {
				stopSequence.put(set.getInt("stop_sequence"), set.getInt("stop_gtfs_id")) ;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// ARRANGES THE STOPS BY STOP SEQUENCE
		if (arrivalStopNumber > departureStopNumber) {		// IF THE DEPARTURE STATION IS FIRST THAN THE ARRIVAL STATION IN THE STOP SEQUENCE
			stopCoords.add(this.getStopCoordinate(seg.getStartStationId(), con, dbUtils)) ;
			
			boolean pathStarted = false ;
			SortedSet<Integer> keys = new TreeSet<Integer>(stopSequence.keySet()) ;
			for (Integer key : keys) { 
			   Integer value = stopSequence.get(key);
			   if (value == seg.getStartStationId() && !pathStarted) {
				   pathStarted = true ;
			   }
			   if (pathStarted) {
				   Coordinate coord = this.getStopCoordinate(value, con, dbUtils) ;
				   stopCoords.add(coord) ;
			   }
			   if (value == seg.getEndStationId() && pathStarted) {
				   pathStarted = false ;
			   }
			}
		}
		else {												// IF THE DEPARTURE STATION IS NOT FIRST THAN THE ARRIVAL STATION IN THE STOP SEQUENCE
			stopCoords.add(this.getStopCoordinate(seg.getStartStationId(), con, dbUtils)) ;
			
			SortedSet<Integer> keys = new TreeSet<Integer>(stopSequence.keySet()).descendingSet() ;
			
			boolean pathStarted = false ;
			for (Integer key : keys) { 
			   Integer value = stopSequence.get(key);
			   if (value == seg.getStartStationId() && !pathStarted) {
				   pathStarted = true ;
			   }
			   if (pathStarted) {
				   Coordinate coord = this.getStopCoordinate(value, con, dbUtils) ;
				   stopCoords.add(coord) ;
			   }
			   if (value == seg.getEndStationId() && pathStarted) {
				   pathStarted = false ;
			   }
			}
		}
		
		return stopCoords ;
	}
	
	/**
	 * Gets the GPS coordinates from the GTFS database for the stop identified by the input <code>stopId</code>
	 * @param stopId	The GTFS unique ID for the stop
	 * @param con		The {@link java.sql.Connection} object connecting to the GTFS database
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtilsImpl} object that manages the connection to the GTFS database
	 * @return			The Coordinate for the stop
	 */
	public Coordinate getStopCoordinate(long stopId, Connection con, DBUtils dbUtils) {
		String sqlSelectStopCoords = "SELECT stop_lat,stop_lon FROM public.stop WHERE stop_gtfs_id='" + stopId + "'" ;
		ResultSet set = dbUtils.queryDB(con, sqlSelectStopCoords) ;
		Coordinate coord ;
		try {
			if(set.next()) {
				coord = new Coordinate(set.getDouble("stop_lat"), set.getDouble("stop_lon")) ;
				return coord ;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null ;
	}
	
	/**
	 * Gets the stop sequence number for the stop identified by the input <code>stopId</code> on the trip identified by the input <code>tripId</code>
	 * @param stopId	The GTFS unique ID for the stop
	 * @param tripId	The GTFS unique ID for the trip
	 * @param con		The {@link java.sql.Connection} object connecting to the GTFS database
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtilsImpl} object that manages the connection to the GTFS database
	 * @return			The sequence number for the stop on the specified trip
	 */
	public int getStopNumber(long stopId, int tripId, Connection con, DBUtils dbUtils) {
		String sqlSelectStopSequence = "SELECT stop_sequence FROM public.stop_time WHERE stop_gtfs_id='" + stopId + "' AND trip_gtfs_id='" + tripId + "'" ;
		ResultSet set = dbUtils.queryDB(con, sqlSelectStopSequence) ;
		try {
			if (set.next()) {
				return set.getInt("stop_sequence") ;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1 ;
	}
}
