package org.uninova.mobis.pojos;

import java.util.ArrayList;

/**
 * POJO MobisTrafficSituation
 * @author PAF@UNINOVA
 */
public class MobisTrafficSituation {

	private ArrayList<MobisTrafficSituationRecord> records = null ;
	private long situationId = -1;
	private String provider = "" ;
	private String providerId = "" ;
	private String publicationTime = "" ;
	private String versionTime = "" ;
	private int version = -1 ;
	private String overallSeverity = "" ;
	private String country = "" ;
	
	public MobisTrafficSituation() {}

	public ArrayList<MobisTrafficSituationRecord> getRecords() {
		return records;
	}

	public void setRecords(ArrayList<MobisTrafficSituationRecord> records) {
		this.records = records;
	}

	public long getSituationId() {
		return situationId;
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

	public void setSituationId(long situationId) {
		this.situationId = situationId;
	}

	public String getPublicationTime() {
		return publicationTime;
	}

	public void setPublicationTime(String publicationTime) {
		this.publicationTime = publicationTime;
	}

	public String getVersionTime() {
		return versionTime;
	}

	public void setVersionTime(String versionTime) {
		this.versionTime = versionTime;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getOverallSeverity() {
		return overallSeverity;
	}

	public void setOverallSeverity(String overallSeverity) {
		this.overallSeverity = overallSeverity;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
	
}
