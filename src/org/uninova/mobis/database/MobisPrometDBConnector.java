package org.uninova.mobis.database;

import java.sql.Connection;
import java.util.ArrayList;

import org.uninova.mobis.pojos.Coordinate;
import org.uninova.mobis.pojos.MobisPrometEvent;
import org.uninova.mobis.utils.DBUtils;

public interface MobisPrometDBConnector {

	public ArrayList<MobisPrometEvent> getAllEvents(DBUtils dbUtils, Connection con) ;
	
	public ArrayList<MobisPrometEvent> getEventsByCategory(DBUtils dbUtils, Connection con, String types)  ;
	
	public ArrayList<MobisPrometEvent> getLocalizedEventsByCategory(DBUtils dbUtils, Connection con, String types, ArrayList<Coordinate> coords, String startDateTime) ;
	
}
