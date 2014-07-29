package org.uninova.mobis.pojos;

/**
 * POJO OSMNode
 * @author PAF@UNINOVA
 */
public class OSMNode {

	private long osmId ;
	private Coordinate coord ;

	public OSMNode() {}
	
	public OSMNode(long osmId, Coordinate coord) {
		this.osmId = osmId ;
		this.coord = coord ;
	}
	
	public long getOsmId() {
		return osmId;
	}
	public void setOsmId(long osmId) {
		this.osmId = osmId;
	}
	public Coordinate getCoord() {
		return coord;
	}
	public void setCoord(Coordinate coord) {
		this.coord = coord;
	} 
}
