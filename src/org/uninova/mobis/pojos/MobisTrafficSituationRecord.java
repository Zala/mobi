package org.uninova.mobis.pojos;

/**
 * POJO MobisTrafficSituationRecord
 * @author PAF@UNINOVA
 */
public class MobisTrafficSituationRecord {

	private String situationRecordType = "" ;
	private String severity = "" ;
	private String provider = "" ;
	private String providerId = "" ;
	private long recordId = -1 ;
	private String versionTime = "" ;
	private int situationRecordVersion = 0;
	private String startTime = "" ;
	private String endTime = "" ;
	private String description = "" ;
	private String locationDescriptor = "" ;
	private String affectedWays = "" ;
	private int lengthAffected = 0 ;
	private double lat = 0.0 ;
	private double lng = 0.0 ;
	private String roadNumber = "" ;
	private String text = "" ;
	
	public MobisTrafficSituationRecord() {}

	public String getSituationRecordType() {
		return situationRecordType;
	}

	public void setSituationRecordType(String situationRecordType) {
		this.situationRecordType = situationRecordType;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getProviderId() {
		return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	public long getRecordId() {
		return recordId;
	}

	public void setRecordId(long id) {
		this.recordId = id;
	}

	public String getVersionTime() {
		return versionTime;
	}

	public void setVersionTime(String versionTime) {
		this.versionTime = versionTime;
	}

	public int getSituationRecordVersion() {
		return situationRecordVersion;
	}

	public void setSituationRecordVersion(int situationRecordVersion) {
		this.situationRecordVersion = situationRecordVersion;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLocationDescriptor() {
		return locationDescriptor;
	}

	public void setLocationDescriptor(String locationDescriptor) {
		this.locationDescriptor = locationDescriptor;
	}

	public String getAffectedWays() {
		return affectedWays;
	}

	public void setAffectedWays(String affectedWays) {
		this.affectedWays = affectedWays;
	}

	public int getLengthAffected() {
		return lengthAffected;
	}

	public void setLengthAffected(int lengthAffected) {
		this.lengthAffected = lengthAffected;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public String getRoadNumber() {
		return roadNumber;
	}

	public void setRoadNumber(String roadNumber) {
		this.roadNumber = roadNumber;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
}
