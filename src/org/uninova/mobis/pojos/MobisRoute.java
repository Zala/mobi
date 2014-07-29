package org.uninova.mobis.pojos;

import java.util.ArrayList;

/**
 * POJO MobisRoute
 * @author PAF@UNINOVA
 */
public class MobisRoute {

	private long routeId = -1 ;
	private String transport = "" ;
	private String starttime = "" ;
	private String endtime = "" ;
	private int criteria = -1 ;
	private String creationtime = "" ;
	private String frequency = "" ; // ONCE, DAILY, WEEKLY, MONTHLY
	private int freqNumber = -1 ;	// 2+DAILY -> Twice a day 
	private Coordinate startCoord = null ;
	private Coordinate endCoord = null ;
	private ArrayList<MobisSegment> segments = null ;
	
	public MobisRoute() {}

	public long getRouteId() {
		return routeId;
	}

	public void setRouteId(long routeId) {
		this.routeId = routeId;
	}

	public String getTransport() {
		return transport;
	}

	public void setTransport(String transport) {
		this.transport = transport;
	}

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public int getCriteria() {
		return criteria;
	}

	public void setCriteria(int criteria) {
		this.criteria = criteria;
	}

	public String getCreationtime() {
		return creationtime;
	}

	public void setCreationtime(String creationtime) {
		this.creationtime = creationtime;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public int getFreqNumber() {
		return freqNumber;
	}

	public void setFreqNumber(int freqNumber) {
		this.freqNumber = freqNumber;
	}

	public Coordinate getStartCoord() {
		return startCoord;
	}

	public void setStartCoord(Coordinate startCoord) {
		this.startCoord = startCoord;
	}

	public Coordinate getEndCoord() {
		return endCoord;
	}

	public void setEndCoord(Coordinate endCoord) {
		this.endCoord = endCoord;
	}

	public ArrayList<MobisSegment> getSegments() {
		return segments;
	}

	public void setSegments(ArrayList<MobisSegment> segments) {
		this.segments = segments;
	}
	
	
}
