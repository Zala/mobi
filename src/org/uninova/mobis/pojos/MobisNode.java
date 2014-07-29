package org.uninova.mobis.pojos;

import java.util.ArrayList;

/**
 * POJO MobisNode
 * @author PAF@UNINOVA
 */
public class MobisNode {

	private long nodeId = -1 ;
	private boolean isStart = false;
	private boolean isFinish = false ;
	private boolean isWaypoint = false ;
	private double lat = 0.0 ;
	private double lng = 0.0 ;
	private int nodeNumber = -1 ;
	private String osmNodeId = "" ;
	private ArrayList<MobisPlace> places = null ;
	private MobisInstruction instruction = null ;
	
	public MobisNode() {}

	public long getNodeId() {
		return nodeId;
	}

	public void setNodeId(long nodeId) {
		this.nodeId = nodeId;
	}

	public boolean isStart() {
		return isStart;
	}

	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}

	public boolean isFinish() {
		return isFinish;
	}

	public void setFinish(boolean isFinish) {
		this.isFinish = isFinish;
	}

	public boolean isWaypoint() {
		return isWaypoint;
	}

	public void setWaypoint(boolean isWaypoint) {
		this.isWaypoint = isWaypoint;
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

	public int getNodeNumber() {
		return nodeNumber;
	}

	public void setNodeNumber(int nodeNumber) {
		this.nodeNumber = nodeNumber;
	}

	public String getOsmNodeId() {
		return osmNodeId;
	}

	public void setOsmNodeId(String osmNodeId) {
		this.osmNodeId = osmNodeId;
	}

	public ArrayList<MobisPlace> getPlaces() {
		return places;
	}

	public void setPlaces(ArrayList<MobisPlace> places) {
		this.places = places;
	}

	public MobisInstruction getInstruction() {
		return instruction;
	}

	public void setInstruction(MobisInstruction instruction) {
		this.instruction = instruction;
	}

	
}
