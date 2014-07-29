package org.uninova.mobis.pojos;

public class GoogleMapMarker {
	
	private String size = "" ;
	private String color = "" ;
	private String label = "" ;
	private Coordinate coord = null ;
	private String address = "" ;
	
	public GoogleMapMarker() {}
	
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public Coordinate getCoord() {
		return coord;
	}
	public void setCoord(Coordinate coord) {
		this.coord = coord;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
}
