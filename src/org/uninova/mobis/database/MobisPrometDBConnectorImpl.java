package org.uninova.mobis.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;

import org.uninova.mobis.pojos.Coordinate;
import org.uninova.mobis.pojos.MobisPrometEvent;
import org.uninova.mobis.utils.DBUtils;
import org.uninova.mobis.utils.GISUtils;
import org.uninova.mobis.utils.GISUtilsImpl;

public class MobisPrometDBConnectorImpl implements MobisPrometDBConnector {

	public MobisPrometDBConnectorImpl() {}
	
	public ArrayList<MobisPrometEvent> getAllEvents(DBUtils dbUtils, Connection con) {
		String sqlQuery = "SELECT * FROM public.promet_event ;" ;
		ResultSet rs = dbUtils.queryDB(con, sqlQuery) ;
		ArrayList<MobisPrometEvent> events = new ArrayList<>() ;
		MobisPrometEvent event ;
		Date validFromDate, validToDate ;
		Time validFromTime, validToTime ;
		Object[] dateTime ;
		
		try {
			while (rs.next()) {
				dateTime = dbUtils.readStringToDateTime(rs.getString("validfrom").replace("T", " ").substring(0, rs.getString("validfrom").lastIndexOf("."))) ;
				validFromDate = (Date) dateTime[0] ;
				validFromTime = (Time) dateTime[1] ;
				
				dateTime = dbUtils.readStringToDateTime(rs.getString("validto").replace("T", " ").substring(0, rs.getString("validto").lastIndexOf("."))) ;
				validToDate = (Date) dateTime[0] ;
				validToTime = (Time) dateTime[1] ;
				
				event = new MobisPrometEvent() ;
				event.setId(rs.getLong("id")) ;
				event.setCategory(rs.getString("category")) ;
				event.setRoad(rs.getString("road")) ;
				event.setType(rs.getString("type")) ;
				event.setDescription(rs.getString("description")) ;
				event.setValidFromDate(validFromDate) ;
				event.setValidFromTime(validFromTime) ;
				event.setValidToDate(validToDate) ;
				event.setValidToTime(validToTime) ;
				event.setSection(rs.getString("section")) ;
				event.setDistance(rs.getString("distance")) ;
				event.setPriority(rs.getInt("priority")) ;
				event.setRoadPriority(rs.getInt("roadpriority")) ;
				event.setIcon(rs.getString("icon")) ;
				event.setPoint(new Coordinate(rs.getDouble("lat"), rs.getDouble("lng"))) ;
				
				dateTime = dbUtils.readStringToDateTime(rs.getString("versiontime").replace("T", " ").substring(0, rs.getString("versiontime").lastIndexOf("."))) ;
				event.setVersionDate((Date) dateTime[0]) ;
				event.setVersionTime((Time) dateTime[1]) ;
				event.setBorder(rs.getBoolean("isborder")) ;
				events.add(event) ;
			}
			return events ;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null ;
	}
	
	public ArrayList<MobisPrometEvent> getEventsByCategory(DBUtils dbUtils, Connection con, String types) {
		ArrayList<MobisPrometEvent> events = new ArrayList<>() ;
		MobisPrometEvent event ;
		String sqlQuery ;
		String[] typesArray = {} ;
		Date validFromDate, validToDate ;
		Time validFromTime, validToTime ;
		Object[] dateTime ;
		ResultSet rs ;
		
		if (types != null && !types.equals(""))
			typesArray = types.split(",") ;
		
		if (typesArray != null && typesArray.length > 0) {
			sqlQuery = "SELECT * FROM public.promet_event WHERE (type='" + typesArray[0] + "'" ;
			
			if (typesArray.length > 1) {
				for (int j = 1; j < typesArray.length; j++) {
					sqlQuery += " OR type='" + typesArray[j] + "'" ;
				}
			}
			sqlQuery += ");" ;
			
			rs = dbUtils.queryDB(con, sqlQuery) ;
			
			try {
				while (rs != null && rs.next()) {
					dateTime = dbUtils.readStringToDateTime(rs.getString("validfrom").replace("T", " ").substring(0, rs.getString("validfrom").lastIndexOf("."))) ;
					validFromDate = (Date) dateTime[0] ;
					validFromTime = (Time) dateTime[1] ;
					
					dateTime = dbUtils.readStringToDateTime(rs.getString("validto").replace("T", " ").substring(0, rs.getString("validto").lastIndexOf("."))) ;
					validToDate = (Date) dateTime[0] ;
					validToTime = (Time) dateTime[1] ;
					
					event = new MobisPrometEvent() ;
					event.setId(rs.getLong("id")) ;
					event.setCategory(rs.getString("category")) ;
					event.setRoad(rs.getString("road")) ;
					event.setType(rs.getString("type")) ;
					event.setDescription(rs.getString("description")) ;
					event.setValidFromDate(validFromDate) ;
					event.setValidFromTime(validFromTime) ;
					event.setValidToDate(validToDate) ;
					event.setValidToTime(validToTime) ;
					event.setSection(rs.getString("section")) ;
					event.setDistance(rs.getString("distance")) ;
					event.setPriority(rs.getInt("priority")) ;
					event.setRoadPriority(rs.getInt("roadpriority")) ;
					event.setIcon(rs.getString("icon")) ;
					event.setPoint(new Coordinate(rs.getDouble("lat"), rs.getDouble("lng"))) ;
					
					dateTime = dbUtils.readStringToDateTime(rs.getString("versiontime").replace("T", " ").substring(0, rs.getString("versiontime").lastIndexOf("."))) ;
					event.setVersionDate((Date) dateTime[0]) ;
					event.setVersionTime((Time) dateTime[1]) ;
					event.setBorder(rs.getBoolean("isborder")) ;
					events.add(event) ;
				}
				return events ;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null ;
	}
	
	public ArrayList<MobisPrometEvent> getLocalizedEventsByCategory(DBUtils dbUtils, Connection con, String types, ArrayList<Coordinate> coords, String startDateTime) {
		GISUtils gisUtils = new GISUtilsImpl() ;
		ArrayList<MobisPrometEvent> events = new ArrayList<>() ;
		MobisPrometEvent event ;
		String sqlQuery ;
		double lat, lng ;
		String[] typesArray = {} ;
		Date validFromDate, validToDate, date ;
		Time validFromTime, validToTime, time ;
		Object[] dateTime ;
		ResultSet rs ;
		ArrayList<Coordinate> bounds ;
		
		dateTime = dbUtils.readStringToDateTime(startDateTime) ;
		date = (Date) dateTime[0] ;
		time = (Time) dateTime[1] ;
		
		
		if (types != null && !types.equals(""))
			typesArray = types.split(",") ;
		
		try {
			for (int i = 0; i < coords.size(); i++) {
				lat = coords.get(i).getLat() ;
				lng = coords.get(i).getLng() ;
				if (typesArray.length <= 0 || typesArray[0].equals("")) {
					/*sqlQuery = "SELECT * FROM mobis_datex_db.datexsituationrecord WHERE lat=" + lat + " AND lng=" + lng + " AND (situationStartTime <= '" + startTime + "' AND situationStartDate <= '" + startDate + "' AND situationEndTime > '" + startTime + "' AND situationEndDate > '" + startDate + "');" ;*/
					sqlQuery = "SELECT * FROM public.promet_event WHERE lat=" + lat + " AND lng=" + lng + ";" ;
					
				}
				else {
					/*sqlQuery = "SELECT * FROM mobis_datex_db.datexsituationrecord WHERE lat=" + lat + " AND lng=" + lng + " AND (situationStartTime <= '" + startTime + "' AND situationStartDate <= '" + startDate + "' AND situationEndTime > '" + startTime + "' AND situationEndDate > '" + startDate + "') AND (type='" + typesArray[0] + "'" ;*/
					sqlQuery = "SELECT * FROM public.promet_event WHERE lat=" + lat + " AND lng=" + lng + " AND (type='" + typesArray[0] + "'" ;
					
					if (typesArray.length > 1) {
						for (int j = 1; j < typesArray.length; j++) {
							sqlQuery += " OR type='" + typesArray[j] + "'" ;
						}
					}
					sqlQuery += ");" ;
				}
				
				rs = dbUtils.queryDB(con, sqlQuery) ;
			
				if (rs != null && rs.next()) {
					dateTime = dbUtils.readStringToDateTime(rs.getString("validfrom").replace("T", " ").substring(0, rs.getString("validfrom").lastIndexOf("."))) ;
					validFromDate = (Date) dateTime[0] ;
					validFromTime = (Time) dateTime[1] ;
					
					dateTime = dbUtils.readStringToDateTime(rs.getString("validto").replace("T", " ").substring(0, rs.getString("validto").lastIndexOf("."))) ;
					validToDate = (Date) dateTime[0] ;
					validToTime = (Time) dateTime[1] ;
					
					if ((date.after(validFromDate) && date.before(validToDate)) || (date.compareTo(validFromDate) == 0 && time.after(validFromTime)) || (date.compareTo(validToDate) == 0 && time.before(validToTime))) {
						event = new MobisPrometEvent() ;
						event.setId(rs.getLong("id")) ;
						event.setCategory(rs.getString("category")) ;
						event.setRoad(rs.getString("road")) ;
						event.setType(rs.getString("type")) ;
						event.setDescription(rs.getString("description")) ;
						event.setValidFromDate(validFromDate) ;
						event.setValidFromTime(validFromTime) ;
						event.setValidToDate(validToDate) ;
						event.setValidToTime(validToTime) ;
						event.setSection(rs.getString("section")) ;
						event.setDistance(rs.getString("distance")) ;
						event.setPriority(rs.getInt("priority")) ;
						event.setRoadPriority(rs.getInt("roadpriority")) ;
						event.setIcon(rs.getString("icon")) ;
						event.setPoint(new Coordinate(rs.getDouble("lat"), rs.getDouble("lng"))) ;
						
						dateTime = dbUtils.readStringToDateTime(rs.getString("versiontime").replace("T", " ").substring(0, rs.getString("versiontime").lastIndexOf("."))) ;
						event.setVersionDate((Date) dateTime[0]) ;
						event.setVersionTime((Time) dateTime[1]) ;
						event.setBorder(rs.getBoolean("isborder")) ;
						events.add(event) ;
					}
				}
				else {
					bounds = gisUtils.getBoundingBoxFromCoordinate(new Coordinate (lat, lng), 0.05) ;
					if (typesArray.length <= 0 || typesArray[0].equals("")) {
						sqlQuery = "SELECT * FROM public.promet_event WHERE lat<" + bounds.get(0).getLat() + " AND lat>" + bounds.get(0).getLat() + " AND lng>" + bounds.get(0).getLng() + " AND lng<" + bounds.get(1).getLng() + ";" ;
					}
					else {
						sqlQuery = "SELECT * FROM public.promet_event WHERE lat<" + bounds.get(0).getLat() + " AND lat>" + bounds.get(1).getLat() + " AND lng>" + bounds.get(0).getLng() + " AND lng<" + bounds.get(1).getLng() + " AND (type='" + typesArray[0] + "'" ;
						
						if (typesArray.length > 1) {
							for (int j = 1; j < typesArray.length; j++) {
								sqlQuery += " OR type='" + typesArray[j] + "'" ;
							}
						}
						sqlQuery += ");" ;
					}
					
					rs = dbUtils.queryDB(con, sqlQuery) ;
					if (rs != null && rs.next()) {
						dateTime = dbUtils.readStringToDateTime(rs.getString("validfrom").replace("T", " ").substring(0, rs.getString("validfrom").lastIndexOf("."))) ;
						validFromDate = (Date) dateTime[0] ;
						validFromTime = (Time) dateTime[1] ;
						
						dateTime = dbUtils.readStringToDateTime(rs.getString("validto").replace("T", " ").substring(0, rs.getString("validto").lastIndexOf("."))) ;
						validToDate = (Date) dateTime[0] ;
						validToTime = (Time) dateTime[1] ;
						
						if ((date.after(validFromDate) && date.before(validToDate)) || (date.compareTo(validFromDate) == 0 && time.after(validFromTime)) || (date.compareTo(validToDate) == 0 && time.before(validToTime))) {
							event = new MobisPrometEvent() ;
							event.setId(rs.getLong("id")) ;
							event.setCategory(rs.getString("category")) ;
							event.setRoad(rs.getString("road")) ;
							event.setType(rs.getString("type")) ;
							event.setDescription(rs.getString("description")) ;
							event.setValidFromDate(validFromDate) ;
							event.setValidFromTime(validFromTime) ;
							event.setValidToDate(validToDate) ;
							event.setValidToTime(validToTime) ;
							event.setSection(rs.getString("section")) ;
							event.setDistance(rs.getString("distance")) ;
							event.setPriority(rs.getInt("priority")) ;
							event.setRoadPriority(rs.getInt("roadpriority")) ;
							event.setIcon(rs.getString("icon")) ;
							event.setPoint(new Coordinate(rs.getDouble("lat"), rs.getDouble("lng"))) ;
							
							dateTime = dbUtils.readStringToDateTime(rs.getString("versiontime").replace("T", " ").substring(0, rs.getString("versiontime").lastIndexOf("."))) ;
							event.setVersionDate((Date) dateTime[0]) ;
							event.setVersionTime((Time) dateTime[1]) ;
							event.setBorder(rs.getBoolean("isborder")) ;
							events.add(event) ;
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return events ;
	}
	
}
