package org.uninova.mobis.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.commons.lang.StringEscapeUtils;
import org.uninova.mobis.pojos.Coordinate;
import org.uninova.mobis.pojos.MobisTrafficSituation;
import org.uninova.mobis.pojos.MobisTrafficSituationRecord;
import org.uninova.mobis.utils.DBUtils;
import org.uninova.mobis.utils.GISUtils;
import org.uninova.mobis.utils.GISUtilsImpl;

/**
 * CLASS MobisTrafficDBConnectorImpl
 * @author PAF@UNINOVA
 */
public class MobisTrafficDBConnectorImpl implements MobisTrafficDBConnector {

	/**
	 * MobisTrafficDBConnectorImpl Class Constructor
	 */
	public MobisTrafficDBConnectorImpl() {}
	
	/**
	 * Fetches all situations from the traffic event database
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con		The {@link java.sql.Connection} to the traffic event database
	 * @return			The {@link java.util.ArrayList} object containing all traffic situations
	 */
	public ArrayList<MobisTrafficSituation> getAllSituations(DBUtils dbUtils, Connection con, String country) {
		String sqlQuery = "SELECT * FROM public.trafficsituation WHERE country='" + country + "'" ;
		ResultSet rs = dbUtils.queryDB(con, sqlQuery), recordSet ;
		ArrayList<MobisTrafficSituation> situations = new ArrayList<>() ;
		MobisTrafficSituation sit ;
		ArrayList<MobisTrafficSituationRecord> records ;
		MobisTrafficSituationRecord sitRec ;
		
		try {
			while (rs.next()) {
				sit = this.createSituation(rs) ;
				records = new ArrayList<>() ;
				
				sqlQuery = "SELECT * FROM public.trafficsituationrecord WHERE situationId='" + sit.getSituationId() + "'" ;
				recordSet = dbUtils.queryDB(con, sqlQuery) ;
				while (recordSet.next()) {
					sitRec = this.createRecord(recordSet, dbUtils) ;
					records.add(sitRec) ;
				}
				sit.setRecords(records) ;
				situations.add(sit) ;
			}
			return situations ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null ;
	}
	
	/**
	 * Fetches all situations of a given category from the traffic event database
	 * @param dbUtils		The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con			The {@link java.sql.Connection} to the traffic event database
	 * @param types			A {@link java.lang.String} object containing the traffic events' category types, separated by a comma (,)
	 * @param getRelated	If true, also fetches the related situations to the ones of the categories given in <code>types</code>
	 * @return				The {@link java.util.ArrayList} object containing all traffic situations 
	 */
	public ArrayList<MobisTrafficSituation> getSituationsByCategory(DBUtils dbUtils, Connection con, String types, boolean getRelated, String country) {
		String sqlQuery = "SELECT * FROM public.trafficsituation WHERE country='" + country + "'" ;
		ResultSet rs = dbUtils.queryDB(con, sqlQuery), recordSet ;
		ArrayList<MobisTrafficSituation> situations = new ArrayList<>() ;
		MobisTrafficSituation sit ;
		ArrayList<MobisTrafficSituationRecord> records ;
		MobisTrafficSituationRecord sitRec ;
		String[] typesArray = types.split(",") ;
		
		try {
			while (rs.next()) {
				sit = this.createSituation(rs) ;
				records = new ArrayList<>() ;
				
				sqlQuery = "SELECT * FROM public.trafficsituationrecord WHERE situationId='" + sit.getSituationId() + "' AND (type='" + typesArray[0] + "'" ;
				if (typesArray.length > 1) {
					for (int i = 1; i < typesArray.length; i++) {
						sqlQuery += " OR type='" + typesArray[i] + "'" ;
					}
				}
				sqlQuery += ")" ;
				recordSet = dbUtils.queryDB(con, sqlQuery) ;
				while (recordSet.next()) {
					sitRec = this.createRecord(recordSet, dbUtils) ;
					records.add(sitRec) ;
				}
				if (getRelated) {
					for (MobisTrafficSituationRecord record : records) {
						sqlQuery = "SELECT * FROM public.trafficsituationrecord WHERE situationId='" + sit.getSituationId() + "' AND NOT id='" + record.getRecordId() + "'" ;
						recordSet = dbUtils.queryDB(con, sqlQuery) ;
						while (recordSet.next()) {
							sitRec = this.createRecord(recordSet, dbUtils) ;
							records.add(sitRec) ;
						}
					}
				}
				sit.setRecords(records) ;
				situations.add(sit) ;
			}
			return situations ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null ;
	}
	
	/**
	 * Fetches all situations of a given category from the traffic event database, which are located on the given coordinates and comprise the given trip time
	 * @param dbUtils		The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @param con			The {@link java.sql.Connection} to the traffic event database
	 * @param types			A {@link java.lang.String} object containing the traffic events' category types, separated by a comma (,)
	 * @param getRelated	If true, also fetches the related situations to the ones of the categories given in <code>types</code>
	 * @param coords		The {@link java.util.ArrayList} object with the coordinates to locate the situations
	 * @param startDateTime	The time for the trip
	 * @return				The {@link java.util.ArrayList} object containing all traffic situations 
	 */
	public ArrayList<MobisTrafficSituation> getLocalizedSituationsByCategory(DBUtils dbUtils, Connection con, String types, boolean getRelated, ArrayList<Coordinate> coords, String startDateTime) {
		GISUtils gisUtils = new GISUtilsImpl() ;
		ArrayList<MobisTrafficSituation> situations = new ArrayList<>() ;
		MobisTrafficSituation sit ;
		ArrayList<MobisTrafficSituationRecord> records ;
		ArrayList<String> situationIds = new ArrayList<>() ;
		MobisTrafficSituationRecord sitRec ;
		String sqlQuery, situationId ;
		double lat, lng ;
		ResultSet rs, sitSet, recordSet ;
		String[] typesArray = {} ;
		if (types != null && !types.equals(""))
			typesArray = types.split(",") ;
		Date startDate, endDate, date ;
		Time startTime, endTime, time ;
		Object[] dateTime ;
		
		dateTime = dbUtils.readStringToDateTime(startDateTime) ;
		date = (Date) dateTime[0] ;
		time = (Time) dateTime[1] ;
		
		try {
			for (int i = 0; i < coords.size(); i++) {
				lat = coords.get(i).getLat() ;
				lng = coords.get(i).getLng() ;
				if (typesArray.length <= 0 || typesArray[0].equals("")) {
					/*sqlQuery = "SELECT * FROM mobis_datex_db.datexsituationrecord WHERE lat=" + lat + " AND lng=" + lng + " AND (situationStartTime <= '" + startTime + "' AND situationStartDate <= '" + startDate + "' AND situationEndTime > '" + startTime + "' AND situationEndDate > '" + startDate + "');" ;*/
					sqlQuery = "SELECT * FROM public.trafficsituationrecord WHERE lat=" + lat + " AND lng=" + lng + ";" ;
					
				}
				else {
					/*sqlQuery = "SELECT * FROM mobis_datex_db.datexsituationrecord WHERE lat=" + lat + " AND lng=" + lng + " AND (situationStartTime <= '" + startTime + "' AND situationStartDate <= '" + startDate + "' AND situationEndTime > '" + startTime + "' AND situationEndDate > '" + startDate + "') AND (type='" + typesArray[0] + "'" ;*/
					sqlQuery = "SELECT * FROM public.trafficsituationrecord WHERE lat=" + lat + " AND lng=" + lng + " AND (type='" + typesArray[0] + "'" ;
					
					if (typesArray.length > 1) {
						for (int j = 1; j < typesArray.length; j++) {
							sqlQuery += " OR type='" + typesArray[j] + "'" ;
						}
					}
					sqlQuery += ");" ;
				}
				rs = dbUtils.queryDB(con, sqlQuery) ;
				if (rs != null && rs.next()) {
					startDate = rs.getDate("situationStartDate") ;
					startTime = rs.getTime("situationStartTime") ;
					endDate = rs.getDate("situationEndDate") ;
					endTime = rs.getTime("situationEndTime") ;
					
					if ((date.after(startDate) && date.before(endDate)) || (date.compareTo(startDate) == 0 && time.after(startTime)) || (date.compareTo(endDate) == 0 && time.before(endTime))) {
					situationId = rs.getString("situationId") ;
					if (!situationIds.contains(situationId)) {
						sqlQuery = "SELECT * FROM public.trafficsituation WHERE id='" + situationId + "';" ;
						sitSet = dbUtils.queryDB(con, sqlQuery) ;
						sitSet.next() ;
						sit = this.createSituation(sitSet) ;
						records = new ArrayList<>() ;
						sitRec = this.createRecord(rs, dbUtils) ;
						records.add(sitRec) ;
						if (getRelated) {
							sqlQuery = "SELECT * FROM public.trafficsituationrecord WHERE situationId='" + situationId + "' AND (situationStartTime <= '" + startTime + "' AND situationStartDate <= '" + startDate + "' AND situationEndTime > '" + startTime + "' AND situationEndDate > '" + startDate + "') AND NOT id='" + sitRec.getRecordId() + "'" ;
							recordSet = dbUtils.queryDB(con, sqlQuery) ;
							while (recordSet.next()) {
								sitRec = this.createRecord(recordSet, dbUtils) ;
								records.add(sitRec) ;
							}
						}
						sit.setRecords(records) ;
						situations.add(sit) ;
						situationIds.add(situationId) ;
					}
					}
				}
				else {
					ArrayList<Coordinate> bounds = gisUtils.getBoundingBoxFromCoordinate(new Coordinate (lat, lng), 0.05) ;
					if (typesArray.length <= 0 || typesArray[0].equals("")) {
						/*sqlQuery = "SELECT * FROM mobis_datex_db.datexsituationrecord WHERE lat<" + bounds.get(0).lat + " AND lat>" + lat + " AND lng>" + bounds.get(0).lng + " AND lng<" + bounds.get(1).lng + " AND (situationStartTime <= '" + startTime + "' AND situationStartDate <= '" + startDate + "' AND situationEndTime > '" + startTime + "' AND situationEndDate > '" + startDate + "');" ;*/
						sqlQuery = "SELECT * FROM public.trafficsituationrecord WHERE lat<" + bounds.get(0).getLat() + " AND lat>" + bounds.get(0).getLat() + " AND lng>" + bounds.get(0).getLng() + " AND lng<" + bounds.get(1).getLng() + ";" ;
					}
					else {
						/*sqlQuery = "SELECT * FROM mobis_datex_db.datexsituationrecord WHERE lat<" + bounds.get(0).lat + " AND lat>" + bounds.get(1).lat + " AND lng>" + bounds.get(0).lng + " AND lng<" + bounds.get(1).lng + " AND (situationStartTime <= '" + startTime + "' AND situationStartDate <= '" + startDate + "' AND situationEndTime > '" + startTime + "' AND situationEndDate > '" + startDate + "') AND (type='" + typesArray[0] + "'" ;*/
						sqlQuery = "SELECT * FROM public.trafficsituationrecord WHERE lat<" + bounds.get(0).getLat() + " AND lat>" + bounds.get(1).getLat() + " AND lng>" + bounds.get(0).getLng() + " AND lng<" + bounds.get(1).getLng() + " AND (type='" + typesArray[0] + "'" ;
						
						if (typesArray.length > 1) {
							for (int j = 1; j < typesArray.length; j++) {
								sqlQuery += " OR type='" + typesArray[j] + "'" ;
							}
						}
						sqlQuery += ");" ;
					}
					rs = dbUtils.queryDB(con, sqlQuery) ;
					if (rs != null && rs.next()) {
						startDate = rs.getDate("situationStartDate") ;
						startTime = rs.getTime("situationStartTime") ;
						endDate = rs.getDate("situationEndDate") ;
						endTime = rs.getTime("situationEndTime") ;
						
						if ((date.after(startDate) && date.before(endDate)) || (date.compareTo(startDate) == 0 && time.after(startTime)) || (date.compareTo(endDate) == 0 && time.before(endTime))) {
						situationId = rs.getString("situationId") ;
						if (!situationIds.contains(situationId)) {
							sqlQuery = "SELECT * FROM public.trafficsituation WHERE id='" + situationId + "';" ;
							sitSet = dbUtils.queryDB(con, sqlQuery) ;
							sitSet.next() ;
							sit = this.createSituation(sitSet) ;
							records = new ArrayList<>() ;
							sitRec = this.createRecord(rs, dbUtils) ;
							records.add(sitRec) ;
							if (getRelated) {
								sqlQuery = "SELECT * FROM public.trafficsituationrecord WHERE situationId='" + situationId + "' AND (situationStartTime <= '" + startTime + "' AND situationStartDate <= '" + startDate + "' AND situationEndTime > '" + startTime + "' AND situationEndDate > '" + startDate + "') AND NOT id='" + sitRec.getRecordId() + "'" ;
								recordSet = dbUtils.queryDB(con, sqlQuery) ;
								while (recordSet.next()) {
									sitRec = this.createRecord(recordSet, dbUtils) ;
									records.add(sitRec) ;
								}
							}
							sit.setRecords(records) ;
							situations.add(sit) ;
							situationIds.add(situationId) ;
						}
						}
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return situations ;
	}
	
	/**
	 * Creates a {@link org.uninova.mobis.pojos.MobisTrafficSituation} object from a {@link java.sql.ResultSet} object 
	 * @param rs	The {@link java.sql.ResultSet} object 
	 * @return		The {@link org.uninova.mobis.pojos.MobisTrafficSituation} object
	 */
	private MobisTrafficSituation createSituation(ResultSet rs) {
		MobisTrafficSituation sit = new MobisTrafficSituation() ;
		try {
			sit.setProviderId(rs.getString("id")) ;
			sit.setProvider(rs.getString("provider")) ;
			sit.setVersion(rs.getInt("version")) ;
			sit.setPublicationTime(rs.getString("publicationTime")) ;
			sit.setVersionTime(rs.getString("versionTime")) ;
			sit.setOverallSeverity(rs.getString("overallSeverity")) ;
			sit.setCountry(rs.getString("country"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sit ;
	}
	
	/**
	 * Creates a {@link org.uninova.mobis.pojos.MobisTrafficSituationRecord} object from a {@link java.sql.ResultSet} object 
	 * @param rs		The {@link java.sql.ResultSet} object 
	 * @param dbUtils	The {@link org.uninova.mobis.utils.DBUtils} object that provides database interaction functions
	 * @return			The {@link org.uninova.mobis.pojos.MobisTrafficSituationRecord} object
	 */
	private MobisTrafficSituationRecord createRecord(ResultSet rs, DBUtils dbUtils) {
		MobisTrafficSituationRecord sitRec = new MobisTrafficSituationRecord() ;
		try {
			sitRec.setProviderId(rs.getString("id")) ;
			sitRec.setProvider(rs.getString("provider")) ;
			sitRec.setSituationRecordVersion(rs.getInt("version")) ;
			sitRec.setSituationRecordType(rs.getString("type")) ;
			sitRec.setSeverity(rs.getString("severity")) ;
			sitRec.setAffectedWays(rs.getString("affectedWays")) ;
			sitRec.setDescription(StringEscapeUtils.escapeHtml(rs.getString("description"))) ;
			sitRec.setStartTime(dbUtils.readDateTimeToString(rs.getDate("situationStartDate"), rs.getTime("situationStartTime"))) ;
			if (rs.getDate("situationEndDate") != null) {
				sitRec.setEndTime(dbUtils.readDateTimeToString(rs.getDate("situationEndDate"), rs.getTime("situationEndDate"))) ;
			}
			else {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss") ;
				Calendar cal = Calendar.getInstance() ;
				cal.setTime(rs.getDate("situationStartDate")) ;
				Calendar timeCal = Calendar.getInstance() ;
				timeCal.setTime(rs.getTime("situationStartTime")) ;
				timeCal.add(Calendar.MINUTE, 60) ;
				cal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY)) ;
				cal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE)) ;
				cal.set(Calendar.SECOND, timeCal.get(Calendar.SECOND)) ;
				sitRec.setEndTime(format.format(cal.getTime())) ;
			}
			sitRec.setLat(rs.getDouble("lat")) ;
			sitRec.setLng(rs.getDouble("lng")) ;
			sitRec.setLengthAffected(rs.getInt("lengthAffected")) ;
			sitRec.setLocationDescriptor(StringEscapeUtils.escapeHtml(rs.getString("locationDescriptor"))) ;
			sitRec.setRoadNumber(rs.getString("roadNumber")) ;
			sitRec.setText(StringEscapeUtils.escapeHtml(rs.getString("text"))) ;
			sitRec.setVersionTime(rs.getString("versionTime")) ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sitRec ;
	}
}
