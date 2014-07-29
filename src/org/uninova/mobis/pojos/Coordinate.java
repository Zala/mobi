package org.uninova.mobis.pojos;

/**
 * POJO Coordinate
 * @author PAF@UNINOVA
 */
public class Coordinate {
	private double lat ;		// Latitude value for Coordinate
	private double lng ;		// Longitude value for Coordinate
	
	/**
	 * Coordinate Class Constructors
	 */
	public Coordinate() {
		this.lat = 0.0 ;
		this.lng = 0.0 ;
	}
	
	public Coordinate (double argLat, double argLng) {
		this.lat = argLat ;
		this.lng = argLng ;
	}
	
	/**
	 * Coordinate Getters & Setters
	 */
	public double getLat() {
		return this.lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return this.lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}
}
