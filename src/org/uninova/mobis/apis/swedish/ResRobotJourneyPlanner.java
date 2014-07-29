package org.uninova.mobis.apis.swedish;

import java.util.ArrayList;
import java.util.HashMap;

import net.sf.json.JSONObject;

import org.uninova.mobis.pojos.Coordinate;
import org.uninova.mobis.pojos.MobisSegment;

public interface ResRobotJourneyPlanner {
	
	public String search(String inputType, Coordinate fromCoord, Coordinate toCoord, String toId, String fromId, String journeyDateTime, boolean isArrivalTime, int searchType, int[] avoidModes) ;
	
	public String stationsInZone(Coordinate center, int radius) ;
	
	public String timeTablePeriod() ;
	
	public String producerList() ;
	
	public String transportModeList() ;
	
	public String findLocation(String from, String to) ;
	
	public String getDepartures(String locationId, int timespan) ;
	
	public HashMap<String, ArrayList<MobisSegment>> parseResRobotSegments(JSONObject resultObj) ;
}
