package org.uninova.mobis.database;

import java.sql.Connection;
import java.util.ArrayList;

import org.uninova.mobis.pojos.Coordinate;
import org.uninova.mobis.pojos.MobisSegment;
import org.uninova.mobis.utils.DBUtils;

public interface GTFSDBConnector {

	public ArrayList<Coordinate> getSegmentCoordinates(DBUtils dbUtils, Connection con, MobisSegment seg) ;
	
	public Coordinate getStopCoordinate(long stopId, Connection con, DBUtils dbUtils) ;
	
	public int getStopNumber(long stopId, int tripId, Connection con, DBUtils dbUtils) ;
}
