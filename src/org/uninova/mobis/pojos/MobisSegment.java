package org.uninova.mobis.pojos;

import java.util.ArrayList;

/**
 * POJO MobisSegment
 * @author PAF@UNINOVA
 */
public class MobisSegment {

	private long segmentId = -1 ;
	private String transport = "" ;
	private String startTime = "" ;
	private String endTime = "" ;
	private Double distance = 0.0 ;
	private String startStation = "" ;
	private int startStationId = 0 ;
	private String endStation = "" ;
	private int endStationId = 0 ;
	private String carrierName = "" ;
	private String carrierId = "" ;
	private String carrierNumber = "" ;
	private String direction = "" ;
	private int segmentNumber = -1 ;
	private String staticMapURL = "" ;
	private ArrayList<MobisNode> nodes = null ;
	
	
	public MobisSegment() {}

	public long getSegmentId() {
		return segmentId;
	}

	public void setSegmentId(long segmentId) {
		this.segmentId = segmentId;
	}

	public String getTransport() {
		return transport;
	}

	public void setTransport(String transport) {
		this.transport = transport;
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

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public String getStartStation() {
		return startStation;
	}

	public void setStartStation(String startStation) {
		this.startStation = startStation;
	}

	public String getEndStation() {
		return endStation;
	}

	public void setEndStation(String endStation) {
		this.endStation = endStation;
	}

	public String getCarrierName() {
		return carrierName;
	}

	public void setCarrierName(String carrierName) {
		this.carrierName = carrierName;
	}

	public String getCarrierNumber() {
		return carrierNumber;
	}

	public void setCarrierNumber(String carrierNumber) {
		this.carrierNumber = carrierNumber;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public ArrayList<MobisNode> getNodes() {
		return nodes;
	}

	public void setNodes(ArrayList<MobisNode> nodes) {
		this.nodes = nodes;
	}

	public int getSegmentNumber() {
		return segmentNumber;
	}

	public void setSegmentNumber(int segmentNumber) {
		this.segmentNumber = segmentNumber;
	}

	public String getStaticMapURL() {
		return staticMapURL;
	}

	public void setStaticMapURL(String staticMapURL) {
		this.staticMapURL = staticMapURL;
	}

	public int getStartStationId() {
		return startStationId;
	}

	public void setStartStationId(int startStationId) {
		this.startStationId = startStationId;
	}

	public int getEndStationId() {
		return endStationId;
	}

	public void setEndStationId(int endStationId) {
		this.endStationId = endStationId;
	}

	public String getCarrierId() {
		return carrierId;
	}

	public void setCarrierId(String carrierId) {
		this.carrierId = carrierId;
	}
	
	
}
